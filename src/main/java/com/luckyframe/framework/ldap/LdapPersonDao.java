package com.luckyframe.framework.ldap;

import com.luckyframe.common.exception.BusinessException;
import com.luckyframe.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Component;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import java.util.List;

@Component
public class LdapPersonDao implements ILdapPersonDao {

    private final LdapConfig config;

    private final LdapTemplate template;
    public LdapPersonDao(LdapConfig config){
        this.config = config;
        LdapContextSource cs = new LdapContextSource();
        cs.setCacheEnvironmentProperties(false);
        cs.setUrl(String.format("ldap://%s:%d", config.getHost(), config.getPort()));
        cs.setAuthenticationSource(new AuthenticationSource() {
            @Override
            public String getCredentials() {
                return config.getBindPassword();
            }

            @Override
            public String getPrincipal() {
                return  config.getBindDn();
            }
        });
        template = new LdapTemplate(cs);

    }

    public Person checkUserPassword(String uid, String password){

        if(!config.isEnable()) return null;

        String filter = "(&"+config.getFilter()+"("+config.getIdAttr()+"=" + uid + "))";
        List result =  template.search(config.getBaseDn(), filter, SearchScope.SUBTREE.getId(), new AbstractContextMapper(){

            @Override
            protected Object doMapFromContext(DirContextOperations ctx) {
                return ctx.getNameInNamespace();
            }
        });

        if(result.size() != 1) {
            throw new BusinessException("找不到用户 "+ uid);
        }

        String dn = (String)result.get(0);

        DirContext rst = template.getContextSource().getContext(dn, password);
        if(null == rst){
            throw new BusinessException("密码错误");
        }
        try {
            return getUserByDn(dn);
        } catch (NamingException e) {
            throw new BusinessException(e.getMessage());
        }
    }
    public Person getUserByDn(String dn) throws NamingException {
        Attributes attributes = template.getContextSource().getReadOnlyContext().getAttributes( dn);

        Person user = new Person();

        Attribute a = attributes.get(config.getNameAttr());
        if (a != null) user.setRealname((String)a.get());

        a = attributes.get(config.getEmailAttr());
        if (a != null) user.setEmail(StringUtils.defaultString((String)a.get(), ""));
        else user.setEmail("");

        a = attributes.get(config.getIdAttr());
        if (a != null) user.setUsername(StringUtils.defaultString((String)a.get(), ""));
        else user.setUsername("");

        a = attributes.get(config.getPhoneAttr());
        if (a != null) user.setTelephone(StringUtils.defaultString((String)a.get(), ""));
        else user.setTelephone("");


        return user;
    }
}


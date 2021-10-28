package com.luckyframe.framework.ldap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ldap")
public class LdapConfig {
    private boolean enable;
    private String host;
    private int port;
    private String bindDn ;
    private String bindPassword;
    private String baseDn;
    private String filter;
    private String idAttr;
    private String nameAttr;
    private String emailAttr;
    private String phoneAttr;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getBindDn() {
        return bindDn;
    }

    public void setBindDn(String bindDn) {
        this.bindDn = bindDn;
    }

    public String getBindPassword() {
        return bindPassword;
    }

    public void setBindPassword(String bindPassword) {
        this.bindPassword = bindPassword;
    }

    public String getBaseDn() {
        return baseDn;
    }

    public void setBaseDn(String baseDn) {
        this.baseDn = baseDn;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getIdAttr() {
        return idAttr;
    }

    public void setIdAttr(String idAttr) {
        this.idAttr = idAttr;
    }

    public String getNameAttr() {
        return nameAttr;
    }

    public void setNameAttr(String nameAttr) {
        this.nameAttr = nameAttr;
    }

    public String getEmailAttr() {
        return emailAttr;
    }

    public void setEmailAttr(String emailAttr) {
        this.emailAttr = emailAttr;
    }

    public String getPhoneAttr() {
        return phoneAttr;
    }

    public void setPhoneAttr(String phoneAttr) {
        this.phoneAttr = phoneAttr;
    }

}

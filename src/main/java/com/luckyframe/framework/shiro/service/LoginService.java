package com.luckyframe.framework.shiro.service;

import com.luckyframe.framework.ldap.ILdapPersonDao;
import com.luckyframe.framework.ldap.Person;
import com.luckyframe.project.system.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.luckyframe.common.constant.Constants;
import com.luckyframe.common.constant.ShiroConstants;
import com.luckyframe.common.constant.UserConstants;
import com.luckyframe.common.exception.user.CaptchaException;
import com.luckyframe.common.exception.user.UserBlockedException;
import com.luckyframe.common.exception.user.UserDeleteException;
import com.luckyframe.common.exception.user.UserNotExistsException;
import com.luckyframe.common.exception.user.UserPasswordNotMatchException;
import com.luckyframe.common.utils.DateUtils;
import com.luckyframe.common.utils.MessageUtils;
import com.luckyframe.common.utils.ServletUtils;
import com.luckyframe.common.utils.security.ShiroUtils;
import com.luckyframe.framework.manager.AsyncManager;
import com.luckyframe.framework.manager.factory.AsyncFactory;
import com.luckyframe.project.system.role.service.IRoleProjectService;
import com.luckyframe.project.system.user.domain.User;
import com.luckyframe.project.system.user.domain.UserStatus;
import com.luckyframe.project.system.user.service.IUserService;

/**
 * 登录校验方法
 * 
 * @author ruoyi
 */
@Component
public class LoginService
{
    @Autowired
    private PasswordService passwordService;

    @Autowired
    private IUserService userService;   
    
	@Autowired
	private IRoleProjectService roleProjectService;

	@Autowired
    private ILdapPersonDao ldapPersonDao;

    /**
     * 登录
     */
    public User login(String username, String password)
    {
        // 验证码校验
        if (!StringUtils.isEmpty(ServletUtils.getRequest().getAttribute(ShiroConstants.CURRENT_CAPTCHA)))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
            throw new CaptchaException();
        }
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null")));
            throw new UserNotExistsException();
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }

        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }

        // 查询用户信息
        User user = userService.selectUserByLoginName(username);

        if (user == null && maybeMobilePhoneNumber(username))
        {
            user = userService.selectUserByPhoneNumber(username);
        }

        if (user == null && maybeEmail(username))
        {
            user = userService.selectUserByEmail(username);
        }

        if(user == null){
            Person person = ldapPersonDao.checkUserPassword(username,password);
            if(person!=null){
                user = new User();
                user.setLoginName(person.getUsername());
                user.setUserType("02");
                user.setEmail(person.getEmail());
                user.setUserName(person.getRealname());
                user.setPhonenumber(person.getTelephone());

                user.setSalt("");
                user.setPassword("");
                user.setCreateBy("ldap");

                userService.insertUser(user);
                user = userService.selectUserByLoginName(username);
            }
        }
        else{
            if(user.getUserType().equals("02")){
                Person person = ldapPersonDao.checkUserPassword(username,password);
                if(person==null){
                    throw new UserPasswordNotMatchException();
                }

                if(!person.getEmail().equals(user.getEmail()) ||
                        !person.getRealname().equals(user.getUserName()) ||
                        !person.getTelephone().equals(user.getPhonenumber())) {
                    user.setEmail(person.getEmail());
                    user.setUserName(person.getRealname());
                    user.setPhonenumber(person.getTelephone());
                    userService.updateUserInfo(user);
                }

            }
        }

        if (user == null)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.not.exists")));
            throw new UserNotExistsException();
        }
        
        if (UserStatus.DELETED.getCode().equals(user.getDelFlag()))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.delete")));
            throw new UserDeleteException();
        }
        
        if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.blocked", user.getRemark())));
            throw new UserBlockedException();
        }

        if(!user.getUserType().equals("02"))
            passwordService.validate(user, password);
        
        /*加入项目权限ID列表*/
        user.setProjectIdForRoles(roleProjectService.selectProjectPermsByUserId(user.getUserId()));

        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        recordLoginInfo(user);
        return user;
    }

    private boolean maybeEmail(String username)
    {
        return username.matches(UserConstants.EMAIL_PATTERN);
    }

    private boolean maybeMobilePhoneNumber(String username)
    {
        return username.matches(UserConstants.MOBILE_PHONE_NUMBER_PATTERN);
    }

    /**
     * 记录登录信息
     */
    public void recordLoginInfo(User user)
    {
        user.setLoginIp(ShiroUtils.getIp());
        user.setLoginDate(DateUtils.getNowDate());
        userService.updateUserInfo(user);
    }
}

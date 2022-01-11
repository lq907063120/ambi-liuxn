package com.liuxn.demo.shiro.realm;

import com.liuxn.demo.entity.UserVo;
import com.liuxn.demo.service.LoginService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 登陆验证
 *
 * @author liuxn
 * @date 2022/1/11
 */
public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private LoginService loginService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        /*主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。*/
        String username = (String) token.getPrincipal();
        UserVo user = loginService.getUserInfo(username);
        if (user == null) {
            throw new UnknownAccountException("账户不存在!");
        }
        //SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
        //        userInfo, //用户名
        //        userInfo.getPassword(), //密码
        //        ByteSource.Util.bytes(userInfo.getCredentialsSalt()),//salt=username+salt
        //        getName()  //realm name
        //);
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(username, user.getPassword().toCharArray(), getName());
        return info;
    }
}

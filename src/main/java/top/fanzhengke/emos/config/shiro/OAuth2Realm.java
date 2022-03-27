package top.fanzhengke.emos.config.shiro;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.fanzhengke.emos.db.pojo.TbUser;
import top.fanzhengke.emos.service.TbUserService;

import java.util.Set;

@Component
public class OAuth2Realm extends AuthorizingRealm {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TbUserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * 授权:验证权限时调用
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        TbUser user = (TbUser) principalCollection.getPrimaryPrincipal();

        // 查询用户的权限列表
        Integer userId = user.getId();
        Set<String> permsSet = userService.searchUserPermissions(userId);
        // 把权限列表添加到info对象中
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        return info;
    }

    /**
     * 认证:验证登录时调用
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getPrincipal();

        // 从令牌中获取userId,检测该账户是否被冻结
        Integer userId = jwtUtil.getUserId(token);
        TbUser user = userService.searchById(userId);

        if (user == null) {
            throw new LockedAccountException("账号已被锁定,请联系管理员");
        }

        // 往info对象中添加用户信息、token字符串
        return new SimpleAuthenticationInfo(user, token, getName());
    }
}

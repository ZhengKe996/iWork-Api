package top.fanzhengke.emos.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 把 token 封装成认证对象
 */
public class OAuth2Token implements AuthenticationToken {
    private String token;

    public OAuth2Token(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}

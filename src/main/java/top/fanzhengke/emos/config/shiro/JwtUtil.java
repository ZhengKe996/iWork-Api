package top.fanzhengke.emos.config.shiro;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    @Value("${emos.jwt.secret}")
    private String secret;

    @Value("${emos.jwt.expire}")
    private Integer expire;

    /**
     * 根据用户ID生成token
     *
     * @param userId
     * @return
     */
    public String createToken(Integer userId) {
        DateTime dateTime = DateUtil.offset(new Date(), DateField.DAY_OF_YEAR, 5);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTCreator.Builder builder = JWT.create();

        return builder.withClaim("userId", userId)
                .withExpiresAt(dateTime)
                .sign(algorithm);
    }

    /**
     * 根据token 反向获取用户ID
     *
     * @param token
     * @return
     */
    public Integer getUserId(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("userId").asInt();
    }

    /**
     * token 有效性验证方法
     *
     * @param token
     */
    public void verifierToken(String token) {
        // 创建算法对象
        Algorithm algorithm = Algorithm.HMAC256(secret);

        // 构建 token 验证对象
        JWTVerifier verifier = JWT.require(algorithm).build();

        // 验证 token
        verifier.verify(token);

    }

}

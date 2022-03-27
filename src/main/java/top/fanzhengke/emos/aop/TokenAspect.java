package top.fanzhengke.emos.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.fanzhengke.emos.common.util.Result;
import top.fanzhengke.emos.config.shiro.ThreadLocalToken;

@Aspect
@Component
public class TokenAspect {
    @Autowired
    private ThreadLocalToken threadLocalToken;

    @Pointcut("execution(public * top.fanzhengke.emos.controller.*.*(..))")
    public void aspect() {

    }

    @Around("aspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Result result = (Result) point.proceed();
        String token = threadLocalToken.getToken();
        if (token != null) {
            result.put("token", token);
            threadLocalToken.clear();
        }
        return result;
    }
}

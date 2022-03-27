package top.fanzhengke.emos;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import top.fanzhengke.emos.config.SystemConstants;
import top.fanzhengke.emos.db.mapper.SysConfigMapper;
import top.fanzhengke.emos.db.pojo.SysConfig;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

@Slf4j
@EnableAsync
@ServletComponentScan
@SpringBootApplication
public class EmosWxApiApplication {
    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private SystemConstants systemConstants;

    public static void main(String[] args) {
        SpringApplication.run(EmosWxApiApplication.class, args);
    }

    @PostConstruct
    public void init() {
        List<SysConfig> list = sysConfigMapper.selectAllParam();
        list.forEach(one -> {
            String key = one.getParamKey();
            key = StrUtil.toCamelCase(key);
            String value = one.getParamValue();
            try {
                Field field = systemConstants.getClass().getDeclaredField(key);
                field.set(systemConstants, value);
            } catch (Exception e) {
                log.error("执行异常", e);
            }
        });
    }
}

package top.fanzhengke.emos.common.util;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class Result extends HashMap<String, Object> {
    public Result() {
        put("code", HttpStatus.SC_OK);
        put("msg", "success");
    }

    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public static Result ok() {
        return new Result();
    }

    public static Result ok(String msg) {
        Result result = new Result();
        result.put("msg", msg);
        return result;
    }

    public static Result ok(Map<String, Object> map) {
        Result result = new Result();
        result.putAll(map);
        return result;
    }

    public static Result error(Integer code, String msg) {
        Result result = new Result();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    public static Result error(String msg) {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }

    public static Result error() {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知错误,请联系管理员");
    }

}

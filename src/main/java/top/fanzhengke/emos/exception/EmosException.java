package top.fanzhengke.emos.exception;

import lombok.Data;

/**
 * 自定义异常类
 */
@Data
public class EmosException extends RuntimeException {
    private String msg;
    private Integer code = 500;

    public EmosException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public EmosException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public EmosException(String msg, Integer code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public EmosException(String msg, Integer code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }
}

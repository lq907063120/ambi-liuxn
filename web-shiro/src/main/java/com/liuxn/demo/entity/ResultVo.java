package com.liuxn.demo.entity;

import lombok.Data;

/**
 * @author liuxn
 * @date 2022/1/11
 */
@Data
public class ResultVo<T> {


    public ResultVo(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultVo(int code, String msg, T t) {
        this.code = code;
        this.msg = msg;
        this.data = t;
    }


    private int code;
    private String msg;
    private T data;
}

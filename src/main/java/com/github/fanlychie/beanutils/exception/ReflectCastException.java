package com.github.fanlychie.beanutils.exception;

/**
 * 运行时异常, 它内部包装了真实的非运行时异常对象, 通过getCause来取出真实异常
 * Created by fanlychie on 2017/3/3.
 */
public class ReflectCastException extends RuntimeException {

    private Throwable cause;

    public ReflectCastException(Throwable throwable) {
        this.cause = throwable;
    }

    @Override
    public synchronized Throwable getCause() {
        return cause;
    }

}
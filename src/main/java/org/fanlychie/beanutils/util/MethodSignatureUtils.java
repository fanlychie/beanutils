package org.fanlychie.beanutils.util;

import org.fanlychie.beanutils.exception.MethodOperateException;

/**
 * 方法签名工具类
 * Created by fanlychie on 2017/3/21.
 */
public final class MethodSignatureUtils {

    /**
     * 换算哈希码字符
     *
     * @param methodName 方法名称
     * @param argValues  方法参数的值列表
     * @return 返回换算的哈希码字符
     */
    public static String hashCodeString(String methodName, Object[] argValues) {
        return hashCodeString(methodName, getValueTypes(argValues));
    }

    /**
     * 换算哈希码字符
     *
     * @param methodName 方法名称
     * @param argTypes   方法参数的类型列表
     * @return 返回换算的哈希码字符
     */
    public static String hashCodeString(String methodName, Class<?>[] argTypes) {
        if (methodName == null && (argTypes == null || argTypes.length == 0)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        if (methodName != null) {
            builder.append(methodName.hashCode());
        }
        if (argTypes != null && argTypes.length > 0) {
            for (Class<?> argType : argTypes) {
                Class<?> handlerType = PrimitiveWrapperTypeUtils.getWrapperType(argType);
                if (handlerType != null) {
                    argType = handlerType;
                }
                builder.append(argType.hashCode());
            }
        }
        return builder.toString();
    }

    /**
     * 方法操作异常
     *
     * @param methodName 方法名称
     * @param argValues  方法参数的值列表
     * @return 返回方法操作异常对象
     */
    public static MethodOperateException methodOperateException(String methodName, Object[] argValues) {
        return methodOperateException(methodName, getValueTypes(argValues));
    }

    /**
     * 方法操作异常
     *
     * @param methodName 方法名称
     * @param argTypes   方法参数的类型列表
     * @return 返回方法操作异常对象
     */
    public static MethodOperateException methodOperateException(String methodName, Class<?>[] argTypes) {
        String methodSignatureInfo = null;
        StringBuilder methodSignatureInfoBuilder = new StringBuilder("can not found method ");
        methodSignatureInfoBuilder.append(methodName).append("(");
        if (argTypes != null && argTypes.length > 0) {
            for (Class<?> argType : argTypes) {
                methodSignatureInfoBuilder.append(argType.getSimpleName()).append(", ");
            }
            int length = methodSignatureInfoBuilder.length();
            methodSignatureInfo = methodSignatureInfoBuilder.replace(length - 2, length, ")").toString();
        } else {
            methodSignatureInfo = methodSignatureInfoBuilder.append(")").toString();
        }
        return new MethodOperateException(methodSignatureInfo);
    }

    /**
     * 获取参数值对应的类型
     *
     * @param argValues 方法参数的值列表
     * @return 返回方法参数的值的类型列表
     */
    private static Class<?>[] getValueTypes(Object[] argValues) {
        Class<?>[] argTypes = null;
        if (argValues != null && argValues.length > 0) {
            argTypes = new Class<?>[argValues.length];
            for (int i = 0; i < argValues.length; i++) {
                argTypes[i] = argValues[i].getClass();
            }
        }
        return argTypes;
    }

}
package org.fanlychie.beanutils.operator;

import org.fanlychie.beanutils.exception.ReflectCastException;
import org.fanlychie.beanutils.util.MethodSignatureUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于操纵方法操作, 提供操作类或对象的函数的方法
 * Created by fanlychie on 2019/7/25.
 */
public class MethodOperator {

    /**
     * 任意的Class类型
     */
    private Class<?> pojoClass;

    /**
     * 是否允许访问Class的静态属性
     */
    private boolean accessibleStatic;

    /**
     * 是否递归查找Class父类的函数
     */
    private boolean accessibleSuperclass;

    /**
     * 在递归查找时, 遇到此类则终止
     */
    private Class<?> stopClass;

    /**
     * 方法签名对照表
     */
    private Map<String, Method> methodSignature;

    /**
     * 构建实例
     *
     * @param pojoClass            任意的Class类型
     * @param accessibleStatic     是否允许访问Class的静态属性
     * @param accessibleSuperclass 是否递归查找Class的父类属性
     * @param stopClass            在递归查找时, 遇到此类则终止搜索
     */
    public MethodOperator(Class<?> pojoClass, boolean accessibleStatic, boolean accessibleSuperclass, Class<?> stopClass) {
        this.pojoClass = pojoClass;
        this.stopClass = stopClass;
        this.accessibleStatic = accessibleStatic;
        this.accessibleSuperclass = accessibleSuperclass;
    }

    /**
     * 调用方法
     *
     * @param obj        目标对象
     * @param methodName 方法名称
     * @param argValues  方法参数的值列表
     * @param <T>        期望返回的数据类型
     * @return 返回方法调用的结果
     */
    public <T> T invokeMethod(Object obj, String methodName, Object... argValues) {
        String signature = MethodSignatureUtils.hashCodeString(fullPathMethodName(methodName), argValues);
        Method method = methodSignature.get(signature);
        if (method == null) {
            throw MethodSignatureUtils.methodOperateException(fullPathMethodName(methodName), argValues);
        }
        try {
            return (T) method.invoke(obj, argValues);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectCastException(e);
        }
    }

    /**
     * 调用静态方法
     *
     * @param methodName 方法名称
     * @param argValues  方法参数的值列表
     * @param <T>        期望返回的数据类型
     * @return 返回方法调用的结果
     */
    public <T> T invokeStaticMethod(String methodName, Object... argValues) {
        return invokeMethod(null, methodName, argValues);
    }

    /**
     * 判断方法是否为静态的
     *
     * @param method 方法
     * @return 若为静态属性则返回 true, 否则返回 false
     */
    public boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    /**
     * 初始化
     *
     * @return 返回当前对象
     */
    public MethodOperator init() {
        methodSignature = lookupClassMethodSignature(pojoClass);
        return this;
    }

    /**
     * 获取方法全路径名称
     *
     * @param method 方法名称
     * @return 返回方法的全路径名称
     */
    private String fullPathMethodName(String method) {
        return pojoClass.getName() + "." + method;
    }

    /**
     * 获取类声明的方法列表
     *
     * @param pojoClass 任意的Class类型
     * @return 返回类声明的方法列表
     */
    private List<Method> getDeclaredMethods(Class<?> pojoClass) {
        List<Method> list = new ArrayList<>();
        Method[] methods = pojoClass.getDeclaredMethods();
        if (methods.length > 0) {
            for (Method method : methods) {
                if (!accessibleStatic && isStatic(method)) {
                    continue;
                }
                method.setAccessible(true);
                list.add(method);
            }
        }
        return list;
    }

    /**
     * 查找类声明的方法签名表
     *
     * @param pojoClass 任意的Class类型
     * @return 返回类声明的方法签名表
     */
    private Map<String, Method> lookupClassMethodSignature(Class<?> pojoClass) {
        Map<String, Method> methodSignature = new HashMap<>();
        do {
            List<Method> methods = getDeclaredMethods(pojoClass);
            for (Method method : methods) {
                String signature = MethodSignatureUtils.hashCodeString(fullPathMethodName(method.getName()), method.getParameterTypes());
                if (!methodSignature.containsKey(signature)) {
                    method.setAccessible(true);
                    methodSignature.put(signature, method);
                }
            }
        } while (accessibleSuperclass && (pojoClass = pojoClass.getSuperclass()) != stopClass);
        return methodSignature;
    }

}
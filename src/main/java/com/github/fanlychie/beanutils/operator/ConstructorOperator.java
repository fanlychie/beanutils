package com.github.fanlychie.beanutils.operator;

import com.github.fanlychie.beanutils.exception.ReflectCastException;
import com.github.fanlychie.beanutils.util.MethodSignatureUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于操作构造器, 提供操作类构造器的方法
 * Created by fanlychie on 2019/7/25.
 */
public class ConstructorOperator {

    /**
     * 任意的Class类型
     */
    private Class<?> pojoClass;

    /**
     * 构造器签名对照表
     */
    private Map<String, Constructor<?>> constructorSignature;

    /**
     * 构建实例
     *
     * @param pojoClass 任意的Class类型
     */
    public ConstructorOperator(Class<?> pojoClass) {
        this.pojoClass = pojoClass;
    }

    /**
     * 调用构造器
     *
     * @param argValues 构造器参数的值列表
     * @return 返回创建的实例对象
     */
    public <T> T invokeConstructor(Object... argValues) {
        String signature = MethodSignatureUtils.hashCodeString(pojoClass.getName(), argValues);
        Constructor<?> constructor = constructorSignature.get(signature);
        if (constructor == null) {
            throw MethodSignatureUtils.methodOperateException(pojoClass.getName(), argValues);
        }
        try {
            return (T) constructor.newInstance(argValues);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ReflectCastException(e);
        }
    }

    /**
     * 初始化
     *
     * @return 返回当前对象
     */
    public ConstructorOperator init() {
        constructorSignature = getDeclaredConstructors();
        return this;
    }

    /**
     * 获取类声明的构造器
     *
     * @return 返回类声明的构造器参数签名对照表
     */
    private Map<String, Constructor<?>> getDeclaredConstructors() {
        Map<String, Constructor<?>> constructorSignature = new HashMap<>();
        Constructor<?>[] constructors = pojoClass.getDeclaredConstructors();
        if (constructors != null) {
            for (Constructor<?> constructor : constructors) {
                String signature = MethodSignatureUtils.hashCodeString(pojoClass.getName(), constructor.getParameterTypes());
                if (!constructorSignature.containsKey(signature)) {
                    constructor.setAccessible(true);
                    constructorSignature.put(signature, constructor);
                }
            }
        }
        return constructorSignature;
    }

}
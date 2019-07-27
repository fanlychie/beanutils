package org.fanlychie.beanutils;

import org.fanlychie.beanutils.operator.ConstructorOperator;
import org.fanlychie.beanutils.operator.FieldOperator;
import org.fanlychie.beanutils.operator.MethodOperator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 操纵工具类
 * Created by fanlychie on 2019/7/25.
 */
public class BeanUtils {

    // 当第一次被调用时才装载进内存, 延迟加载
    private static class FieldOperatorCacheHolder {
        // 字段常驻内存
        private static Map<Class<?>, FieldOperator> cache = new ConcurrentHashMap<>();
    }

    // 当第一次被调用时才装载进内存, 延迟加载
    private static class MethodOperatorCacheHolder {
        // 方法常驻内存
        private static Map<Class<?>, MethodOperator> cache = new ConcurrentHashMap<>();
    }

    // 当第一次被调用时才装载进内存, 延迟加载
    private static class ConstructorOperatorCacheHolder {
        // 构造器常驻内存
        private static Map<Class<?>, ConstructorOperator> cache = new ConcurrentHashMap<>();
    }

    /**
     * 获得操作字段能力的实例
     *
     * @param pojoClass 任意的Class类型
     */
    public static FieldOperator fieldOperate(Class<?> pojoClass) {
        return fieldOperate(pojoClass, true, true, Object.class);
    }

    /**
     * 获得操作字段能力的实例
     *
     * @param pojoClass            任意的Class类型
     * @param accessibleStatic     是否允许访问Class的静态属性
     * @param accessibleSuperclass 是否递归查找Class的父类属性
     * @param stopClass            在递归查找时, 遇到此类则终止搜索字段
     */
    public static FieldOperator fieldOperate(Class<?> pojoClass, boolean accessibleStatic, boolean accessibleSuperclass, Class<?> stopClass) {
        if (!FieldOperatorCacheHolder.cache.containsKey(pojoClass)) {
            synchronized (pojoClass) {
                if (!FieldOperatorCacheHolder.cache.containsKey(pojoClass)) {
                    FieldOperatorCacheHolder.cache.put(pojoClass,
                            new FieldOperator(pojoClass, accessibleStatic, accessibleSuperclass, stopClass).init());
                }
            }
        }
        return FieldOperatorCacheHolder.cache.get(pojoClass);
    }

    /**
     * 获得操作方法能力的实例
     *
     * @param pojoClass 任意的Class类型
     */
    public static MethodOperator methodOperate(Class<?> pojoClass) {
        return methodOperate(pojoClass, true, true, Object.class);
    }

    /**
     * 获得操作方法能力的实例
     *
     * @param pojoClass            任意的Class类型
     * @param accessibleStatic     是否允许访问Class的静态属性
     * @param accessibleSuperclass 是否递归查找Class的父类属性
     * @param stopClass            在递归查找时, 遇到此类则终止搜索字段
     */
    public static MethodOperator methodOperate(Class<?> pojoClass, boolean accessibleStatic, boolean accessibleSuperclass, Class<?> stopClass) {
        if (!MethodOperatorCacheHolder.cache.containsKey(pojoClass)) {
            synchronized (pojoClass) {
                if (!MethodOperatorCacheHolder.cache.containsKey(pojoClass)) {
                    MethodOperatorCacheHolder.cache.put(pojoClass,
                            new MethodOperator(pojoClass, accessibleStatic, accessibleSuperclass, stopClass).init());
                }
            }
        }
        return MethodOperatorCacheHolder.cache.get(pojoClass);
    }

    /**
     * 获得操作构造器能力的实例
     *
     * @param pojoClass 任意的Class类型
     */
    public static ConstructorOperator constructorOperate(Class<?> pojoClass) {
        if (!ConstructorOperatorCacheHolder.cache.containsKey(pojoClass)) {
            synchronized (pojoClass) {
                if (!ConstructorOperatorCacheHolder.cache.containsKey(pojoClass)) {
                    ConstructorOperatorCacheHolder.cache.put(pojoClass,
                            new ConstructorOperator(pojoClass).init());
                }
            }
        }
        return ConstructorOperatorCacheHolder.cache.get(pojoClass);
    }

}
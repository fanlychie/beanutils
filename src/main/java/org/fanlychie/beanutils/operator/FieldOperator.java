package org.fanlychie.beanutils.operator;

import org.fanlychie.beanutils.exception.FieldOperateException;
import org.fanlychie.beanutils.exception.ReflectCastException;
import org.fanlychie.beanutils.util.PrimitiveWrapperTypeUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于操作字段属性, 提供操作对象属性或类属性的方法
 * Created by fanlychie on 2019/7/25.
 */
public class FieldOperator {

    /**
     * 任意的Class类型
     */
    private Class<?> pojoClass;

    /**
     * 是否允许访问Class的静态属性
     */
    private boolean accessibleStatic;

    /**
     * 是否递归查找Class的父类属性
     */
    private boolean accessibleSuperclass;

    /**
     * 在递归查找时, 遇到此类则终止搜索
     */
    private Class<?> stopClass;

    /**
     * 用于存储查找到的<属性名称, 属性字段>散列表
     */
    private Map<String, Field> nameFieldMap;

    /**
     * 构建实例
     *
     * @param pojoClass            任意的Class类型
     * @param accessibleStatic     是否允许访问Class的静态属性
     * @param accessibleSuperclass 是否递归查找Class的父类属性
     * @param stopClass            在递归查找时, 遇到此类则终止搜索
     */
    public FieldOperator(Class<?> pojoClass, boolean accessibleStatic, boolean accessibleSuperclass, Class<?> stopClass) {
        this.pojoClass = pojoClass;
        this.stopClass = stopClass;
        this.accessibleStatic = accessibleStatic;
        this.accessibleSuperclass = accessibleSuperclass;
    }

    /**
     * 获取实例对象的属性值
     *
     * @param obj  具体对象
     * @param name 属性名称
     * @param <T>  期望的类型
     * @return 返回对象属性的值
     */
    public <T> T getValueByFieldName(Object obj, String name) {
        try {
            return (T) getFieldByFieldName(name).get(obj);
        } catch (IllegalAccessException e) {
            throw new ReflectCastException(e);
        }
    }

    /**
     * 获取类的静态属性值
     *
     * @param name 类静态属性名称
     * @param <T>  期望的类型
     * @return 返回对象属性的值
     */
    public <T> T getValueByStaticFieldName(String name) {
        return getValueByFieldName(null, name);
    }

    /**
     * 根据属性类型获取对象的属性的值, 若没有找到参数给定类型的属性或找到多于1个以上将抛出异常
     *
     * @param obj  具体对象
     * @param type 属性类型
     * @param <T>  期望的类型
     * @return 返回对象属性的值
     */
    public <T> T getValueByFieldType(Object obj, Class<?> type) {
        try {
            return (T) getFieldByFieldType(type).get(obj);
        } catch (IllegalAccessException e) {
            throw new ReflectCastException(e);
        }
    }

    /**
     * 根据类静态属性的类型获取静态属性值, 若没有找到参数给定类型的属性或找到多于1个以上将抛出异常
     *
     * @param type 属性类型
     * @param <T>  期望的类型
     * @return 返回对象属性的值
     */
    public <T> T getValueByStaticFieldType(Class<?> type) {
        return getValueByFieldType(null, type);
    }

    /**
     * 根据属性名称设置对象属性的值
     *
     * @param obj   具体对象
     * @param name  属性名称
     * @param value 值
     */
    public void setValueByFieldName(Object obj, String name, Object value) {
        try {
            getFieldByFieldName(name).set(obj, value);
        } catch (IllegalAccessException e) {
            throw new ReflectCastException(e);
        }
    }

    /**
     * 根据类静态属性名称设置类属性的值
     *
     * @param name  属性名称
     * @param value 值
     */
    public void setValueByStaticFieldName(String name, Object value) {
        setValueByFieldName(null, name, value);
    }

    /**
     * 根据值的类型设置对象属性的值, 按此类型若没有找到或找到多于1个以上的字段属性则抛出异常
     *
     * @param obj   具体对象
     * @param value 值
     */
    public void setValueByFieldType(Object obj, Object value) {
        Field field = null;
        FieldOperateException foe = null;
        Class<?> valueType = value.getClass();
        try {
            field = getFieldByFieldType(valueType);
        } catch (FieldOperateException e) {
            foe = e;
            try {
                Class<?> primitiveType = PrimitiveWrapperTypeUtils.getPrimitiveType(valueType);
                if (primitiveType != null) {
                    field = getFieldByFieldType(primitiveType);
                }
            } catch (Exception ex) {}
        }
        if (field == null) {
            throw foe;
        }
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new ReflectCastException(e);
        }
    }

    /**
     * 根据值的类型设置类静态属性的值, 按此类型若没有找到或找到多于1个以上的静态字段属性则抛出异常
     *
     * @param value 值
     */
    public void setValueByStaticFieldType(Object value) {
        setValueByFieldType(null, value);
    }

    /**
     * 获取属性声明的注解表
     *
     * @param annotationClass 注解类型
     * @param <T>             期望的返回值类型
     * @return 返回参数给定的类型的注解表
     */
    public <T extends Annotation> Map<Field, T> getAnnotationFieldMap(Class<T> annotationClass) {
        Map<Field, T> map = new HashMap<>();
        List<Field> fields = getFields();
        for (Field field : fields) {
            T annotation = field.getAnnotation(annotationClass);
            if (annotation != null) {
                map.put(field, annotation);
            }
        }
        return map;
    }

    /**
     * 根据名称获取字段属性
     *
     * @param name 属性名称
     * @return 返回得到的字段属性
     */
    public Field getFieldByFieldName(String name) {
        if (name == null) {
            throw new NullPointerException();
        }
        Field field = getNameFieldMap().get(name);
        if (field == null) {
            throw new FieldOperateException(name + " property can not be found in " + pojoClass);
        }
        return field;
    }

    /**
     * 根据类型获取属性
     *
     * @param type 属性类型
     * @return 返回得到的字段属性
     */
    public Field getFieldByFieldType(Class<?> type) {
        if (type == null) {
            throw new NullPointerException();
        }
        List<Field> fields = getFields();
        List<Field> matches = new ArrayList<>();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            if (fieldType != Object.class && (
                    PrimitiveWrapperTypeUtils.matche(fieldType, type) ||
                    fieldType.isAssignableFrom(type))) {
                matches.add(field);
            }
        }
        if (matches.isEmpty()) {
            throw new FieldOperateException(type.getName() + " type property can not be found in " + pojoClass);
        }
        if (matches.size() > 1) {
            throw new FieldOperateException("find more than one " + type.getName() + " type property in " + pojoClass);
        }
        return matches.get(0);
    }

    /**
     * 获取查找到的<属性名称, 属性字段>散列表
     *
     * @return 返回查找到的 <属性名称, 字段属性> Map
     */
    public Map<String, Field> getNameFieldMap() {
        return nameFieldMap;
    }

    /**
     * 获取查找到的字段属性集合
     *
     * @return 返回查找到的字段属性集合
     */
    public List<Field> getFields() {
        return new ArrayList<>(getNameFieldMap().values());
    }

    /**
     * 获取查找到的属性名称集合
     *
     * @return 返回查找到的属性名称集合
     */
    public List<String> getFieldNames() {
        return new ArrayList<>(getNameFieldMap().keySet());
    }

    /**
     * 判断属性是否为静态的
     *
     * @param field 字段属性
     * @return 若为静态属性则返回 true, 否则返回 false
     */
    public boolean isStatic(Field field) {
        return (field.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
    }

    /**
     * 判断属性是否为静态的
     *
     * @param field 字段属性
     * @return 若为静态属性则返回 true, 否则返回 false
     */
    public boolean isStatic(String field) {
        return isStatic(nameFieldMap.get(field));
    }

    /**
     * 初始化
     *
     * @return 返回当前对象
     */
    public FieldOperator init() {
        this.nameFieldMap = lookupClassNameFieldMap(pojoClass);
        return this;
    }

    /**
     * 获取参数给定的类声明的属性属性集合
     *
     * @param pojoClass POJO 类
     * @return 返回参数给定的类声明的属性属性集合
     */
    private List<Field> getClassDeclaredFields(Class<?> pojoClass) {
        List<Field> list = new ArrayList<>();
        Field[] fields = pojoClass.getDeclaredFields();
        if (fields.length > 0) {
            for (Field field : fields) {
                if (!accessibleStatic && isStatic(field)) {
                    continue;
                }
                field.setAccessible(true);
                list.add(field);
            }
        }
        return list;
    }

    /**
     * 查找参数给定的类的 <属性名称, 字段属性> Map
     *
     * @param pojoClass POJO 类
     * @return 返回参数给定的类的 <属性名称, 字段属性> Map
     */
    private Map<String, Field> lookupClassNameFieldMap(Class<?> pojoClass) {
        Map<String, Field> nameFieldMap = new HashMap<>();
        do {
            List<Field> fields = getClassDeclaredFields(pojoClass);
            for (Field field : fields) {
                String name = field.getName();
                if (!nameFieldMap.containsKey(name)) {
                    nameFieldMap.put(name, field);
                }
            }
        } while (accessibleSuperclass && (pojoClass = pojoClass.getSuperclass()) != stopClass);
        return nameFieldMap;
    }

}
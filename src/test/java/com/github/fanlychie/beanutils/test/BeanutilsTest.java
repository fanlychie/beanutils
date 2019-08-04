package com.github.fanlychie.beanutils.test;

import com.github.fanlychie.beanutils.BeanUtils;
import com.github.fanlychie.beanutils.operator.FieldOperator;
import com.github.fanlychie.beanutils.operator.MethodOperator;
import com.github.fanlychie.beanutils.test.model.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class BeanutilsTest {

    static User user;

    @Test
    public void testFieldOperator() {
        FieldOperator fieldOperator = BeanUtils.fieldOperate(User.class);
        // 设置对象字段的值
        fieldOperator.setValueByFieldName(user, "name", "fanlychie");
        // 获取对象字段的值
        String name = fieldOperator.getValueByFieldName(user, "name");
        // 获取静态属性的值
        String version = fieldOperator.getValueByStaticFieldName("version");
        assertEquals("fanlychie", name);
        assertEquals("1.0.0-SNAPSHOT", version);
    }

    @Test
    public void testMethodOperator() {
        MethodOperator methodOperator = BeanUtils.methodOperate(User.class);
        // 调用实例的方法
        methodOperator.invokeMethod(user, "setName", "fanlychie");
        // 调用实例的方法
        String name = methodOperator.invokeMethod(user, "getName");
        // 调用静态方法
        String version = methodOperator.invokeStaticMethod("getVersion");
        assertEquals("fanlychie", name);
        assertEquals("1.0.0-SNAPSHOT", version);
    }

    @BeforeClass
    public static void before() {
        System.out.println(">>>>>>>>>>>>> 单元测试开始");
        user = new User();
    }

    @AfterClass
    public static void after() {
        System.out.println("<<<<<<<<<<<<< 单元测试结束");
    }

}
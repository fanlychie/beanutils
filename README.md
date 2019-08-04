<p align="center">
    <a href="#">
        <img src="https://raw.githubusercontent.com/fanlychie/mdimg/master/beanutils_logo_img.png">
    </a>
</p>
<p align="center">
    基于反射实现的用于操纵 Bean 的工具
</p>
<p align="center">
    <a href="https://circleci.com/gh/fanlychie/beanutils" target="_blank" title="Circle CI">
        <img src="https://circleci.com/gh/fanlychie/beanutils.svg?style=svg&circle-token=1173052afd21856384d886a4aac200286199cc15">
    </a>
    <a href="https://codecov.io/gh/fanlychie/beanutils" target="_blank" title="Codecov">
        <img src="https://codecov.io/gh/fanlychie/beanutils/branch/master/graph/badge.svg">
    </a>
    <a href="https://www.codacy.com/app/fanlychie/beanutils?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=fanlychie/beanutils&amp;utm_campaign=Badge_Grade" target="_blank" title="Codacy">
        <img src="https://api.codacy.com/project/badge/Grade/5ff9303ddee34d3c96c56f8309c34960">
    </a>
    <a href="http://www.apache.org/licenses/LICENSE-2.0" target="_blank" title="License">
        <img src="https://img.shields.io/github/license/fanlychie/beanutils.svg">
    </a>
    <a href="https://jitpack.io/#fanlychie/beanutils" target="_blank" title="Jitpack">
        <img src="https://jitpack.io/v/fanlychie/beanutils.svg">
    </a>
</p>

---

## 依赖声明

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    ... ...
    <dependency>
        <groupId>com.github.fanlychie</groupId>
        <artifactId>beanutils</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

---

## 使用样例

设现有一 POJO 类：

```java
public class User {

    private int age;

    private String name;
    
    private static String version = "1.0.0";

    // 忽略 setter 和 getter
}
```

```java
User user = new User();
```

### 操纵字段

```java
FieldOperator fieldOperator = BeanUtils.fieldOperate(User.class);
// 设置对象字段的值
fieldOperator.setValueByFieldName(user, "name", "fanlychie");
// 获取对象字段的值
String name = fieldOperator.getValueByFieldName(user, "name");
// 获取静态属性的值
String version = fieldOperator.getValueByStaticFieldName("version");
```

---

### 操纵方法

```java
MethodOperator methodOperator = BeanUtils.methodOperate(User.class);
// 调用实例的方法
methodOperator.invokeMethod(user, "setName", "fanlychie");
// 调用实例的方法
String name = methodOperator.invokeMethod(user, "getName");
// 调用静态方法
String version = methodOperator.invokeStaticMethod("getVersion");
```

---

### 操纵构造器

```java
User user = BeanUtils.constructorOperate(User.class).invokeConstructor();
```
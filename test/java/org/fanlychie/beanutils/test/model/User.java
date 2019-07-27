package org.fanlychie.beanutils.test.model;

public class User {

    private int age;

    private String name;

    private static String version = "1.0.0-SNAPSHOT";

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getVersion() {
        return version;
    }

    public static void setVersion(String version) {
        User.version = version;
    }

}
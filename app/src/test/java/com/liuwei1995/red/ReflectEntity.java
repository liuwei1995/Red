package com.liuwei1995.red;

import java.io.Serializable;

/**
 * Created by linxins on 17-4-16.
 */

public class ReflectEntity implements Serializable{
    private int age;
    private String name;
    public void set(int age){
        this.age = age;
        System.out.println("set age:"+age);
    }

    private void set(String name){
        this.name = name;
        System.out.println("set name:"+name);
    }

    public ReflectEntity(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public ReflectEntity() {
    }

    @Override
    public String toString() {
        return "ReflectEntity{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}

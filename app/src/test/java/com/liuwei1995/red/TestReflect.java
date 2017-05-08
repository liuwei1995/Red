package com.liuwei1995.red;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by linxins on 17-4-16.
 */

public class TestReflect {


    @Test
    public void test() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> aClass = Class.forName("com.liuwei1995.red.ReflectEntity");
        Class<?> superclass = aClass.getSuperclass();
        System.out.println("superclass:"+(superclass == null?superclass : superclass.toString()));
        Class<?>[] interfaces = aClass.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            System.out.println("interfaces:"+interfaces[i].toString());
        }

        Constructor<?>[] constructors = aClass.getConstructors();
        for (int i = 0; i < constructors.length; i++) {
            Constructor<?> constructor = constructors[i];
            Class<?>[] parameterTypes = constructor.getParameterTypes();

            System.out.println("parameterTypes   length:"+parameterTypes.length);
            for (int i1 = 0; i1 < parameterTypes.length; i1++) {
                System.out.println("parameterTypes:"+i1+"\t"+parameterTypes[i1].toString());
            }
//            这个是无参构造函数
//            Object newInstance = aClass.newInstance();
            Object args [] = new Object[parameterTypes.length];
            for (int i1 = 0; i1 < args.length; i1++) {
                if(parameterTypes[i1].getComponentType() == int.class){
                    args[i1] = i1;
                }else if (parameterTypes[i1].getComponentType() == String.class){
                    args[i1] = "name"+i1;
                }
                Constructor<?> aClassConstructor = aClass.getConstructor(int.class,String.class);
                aClassConstructor.setAccessible(true);
                Object newInstance = aClassConstructor.newInstance(args);

                Method method_toString = aClass.getMethod("toString");
                Object invoke_toString = method_toString.invoke(newInstance);
                System.out.println("invoke_toString:"+(invoke_toString == null?invoke_toString : invoke_toString.toString()));

            }

        }
    }
}

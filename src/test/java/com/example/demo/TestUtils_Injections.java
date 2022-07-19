package com.example.demo;

import java.lang.reflect.Field;

//used for user mock only
public class TestUtils_Injections {

    public static void injectObjects(Object target, String fieldName, Object toInject) {
        boolean wasPrivate = false;

        try {
            Field targetFeild = target.getClass().getDeclaredField(fieldName);

            //
            if (!targetFeild.isAccessible()) {
                targetFeild.setAccessible(true);
                wasPrivate = true;
            }
            targetFeild.set(target, toInject);

            if (wasPrivate) {
                targetFeild.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

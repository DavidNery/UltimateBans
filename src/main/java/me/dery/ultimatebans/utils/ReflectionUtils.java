package me.dery.ultimatebans.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {

    public static <R> R getMethodInvocation(Method method, Object object, Object... params) {
        try {
            R r = (R) method.invoke(object, params);
            return r;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... params) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, params);
            if (!method.isAccessible()) method.setAccessible(true);

            return method;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            if (clazz.getSuperclass() != null)
                return getMethod(clazz.getSuperclass(), methodName, params);
            return null;
        }
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (!field.isAccessible()) field.setAccessible(true);

            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            if (clazz.getSuperclass() != null)
                return getField(clazz.getSuperclass(), fieldName);
            return null;
        }
    }

}

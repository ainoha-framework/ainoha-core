package com.ainoha.internal.annotation.processors;

import java.lang.reflect.Field;

public final class FieldMother {

    private Object fakeField;

    private FieldMother() { }

    public static Field getObjectField() {
        return getDeclaredField(FieldMother.class, "fakeField");
    }

    public static Field getDeclaredField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
        }

        return null;
    }
}

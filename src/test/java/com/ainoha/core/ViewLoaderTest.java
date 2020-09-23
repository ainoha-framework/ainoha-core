package com.ainoha.core;

import org.junit.jupiter.api.Test;
import test.fxapp.ViewTestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class ViewLoaderTest {

    @Test
    public void viewThrowNullPointerExceptionIfControllerClassIsNull() {
        assertThatThrownBy(() -> new ViewLoader(){}.view(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("'controllerClass' cannot be null");
    }

    @Test
    public void view() {
        var controllerClass = ViewTestController.class;

        assertThat(new ViewLoader(){}.view(controllerClass))
                .isInstanceOf(ViewLoaderBuilder.class)
                .extracting(builder -> getFieldValue(ViewLoaderBuilder.class, "controllerClass", builder))
                .isNotNull()
                .isSameAs(ViewTestController.class);

    }

    private Object getFieldValue(Class clazz, String fieldName, Object instance) {
        try {
            var field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}

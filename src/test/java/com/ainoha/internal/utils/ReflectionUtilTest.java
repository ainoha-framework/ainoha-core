package com.ainoha.internal.utils;

import com.ainoha.core.annotation.FxApplication;
import com.ainoha.core.annotation.FxmlController;
import com.ainoha.core.exception.FxmlControllerDependenciesException;
import javafx.fxml.FXML;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ReflectionUtilTest {

    private static boolean wasDummyClass_staticMethod_invoked = false;

    static class DummyClass {

    }

    @FxmlController(fxmlPath = "/dummy/path/file.fxml")
    public static class DummyController {
        @FXML
        private String privateStr = "";

        public static void staticMethod() {
            wasDummyClass_staticMethod_invoked = true;
        }
    }

    // TESTS FOR METHOD: invokeStaticMethod()
    @Test
    public void invokeStaticMethod()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        ReflectionUtil.invokeStaticMethod(DummyController.class, "staticMethod", null, null);
        assertThat(wasDummyClass_staticMethod_invoked)
                .isTrue();
    }


    // TESTS FOR METHOD: getDeclaredAnnotation()
    @Test
    public void getDeclaredAnnotationReturnsEmptyOptionalIfNotFound() {
        assertThat(ReflectionUtil.getDeclaredAnnotation(DummyController.class, FxApplication.class))
                .isEmpty();
    }

    @Test
    public void getDeclaredAnnotation() {
        assertThat(ReflectionUtil.getDeclaredAnnotation(DummyController.class, FxmlController.class))
                .isNotEmpty();
    }


    // TESTS FOR METHOD: isAnnotatedWith()
    @Test
    public void isAnnotatedWithReturnFalse() {
        assertThat(ReflectionUtil.isAnnotatedWith(DummyController.class, FxApplication.class))
                .isFalse();
    }

    @Test
    public void isAnnotatedWith() {
        assertThat(ReflectionUtil.isAnnotatedWith(DummyController.class, FxmlController.class))
                .isTrue();
    }


    // TESTS FOR METHOD: setValueInAnnotatedFields()
    @Test
    public void setValueInAnnotatedFieldsFailIncompatibleTypes() {
        var dummyController = new DummyController();
        var integer5 = Integer.valueOf(5);

        assertThatThrownBy(() -> ReflectionUtil.setValueInAnnotatedFields(dummyController, FXML.class, integer5))
                .isExactlyInstanceOf(FxmlControllerDependenciesException.class)
                .hasMessage("Cannot inject the value. Required type: " + integer5.getClass().getName()
                                                + ". Found type: " + dummyController.privateStr.getClass().getName());
    }

    @Test
    public void setValueInAnnotatedFields() {
        var dummyController = new DummyController();
        var value = "hello";

        ReflectionUtil.setValueInAnnotatedFields(dummyController, FXML.class, value);

        assertThat(dummyController.privateStr)
                .isEqualTo(value);
    }


    // TESTS FOR METHOD: newInstanceOf()
    @Test
    public void newInstanceOf()
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        Class<DummyController> newObjectClass = DummyController.class;

        assertThat(ReflectionUtil.newInstanceOf(newObjectClass))
                .isNotNull()
                .isExactlyInstanceOf(newObjectClass);
    }

    // TESTS FOR METHOD: getFirstAnnotatedFieldValueFromController()
    @Test
    public void getFirstAnnotatedFieldValueFromControllerReturnsEmptyOptionalIfNotAnnotatedWithFxmlController() throws IllegalAccessException {
        var dummyClass = new DummyClass();

        assertThat(ReflectionUtil.getFirstAnnotatedFieldValueFromController(dummyClass, null))
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void getFirstAnnotatedFieldValueFromControllerReturnsEmptyOptionalIfThereAreNoAnnotatedFields() throws IllegalAccessException {
        var dummyClass = new DummyClass();

        assertThat(ReflectionUtil.getFirstAnnotatedFieldValueFromController(dummyClass, FXML.class))
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void getFirstAnnotatedFieldValueFromController() throws IllegalAccessException {
        var dummyController = new DummyController();
        dummyController.privateStr = "test";

        assertThat(ReflectionUtil.getFirstAnnotatedFieldValueFromController(dummyController, FXML.class))
                .isNotNull()
                .isNotEmpty()
                .get()
                .isEqualTo(dummyController.privateStr);
    }
}

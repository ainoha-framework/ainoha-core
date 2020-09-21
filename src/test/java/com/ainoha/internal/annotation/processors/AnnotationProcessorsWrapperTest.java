package com.ainoha.internal.annotation.processors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import test.fxapp.Main;
import test.fxapp.ViewControllersHub;

public final class AnnotationProcessorsWrapperTest {

    @BeforeAll
    public static void startFxClass() {
        new Thread(() -> Main.run(new String[0])).start();

        while (ViewControllersHub.getInstance().getViewTestController() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) { }
        }
    }

    @AfterAll
    public static void exitApp() {
        ViewControllersHub.getInstance().getViewTestController().exit();
    }


    @Nested
    class NestedCssPressedStyleOnTouchAnnotationProcessorTest extends CssPressedStyleOnTouchAnnotationProcessorTest {

    }

    @Nested
    class NestedInputValidatorAnnotationProcessorTest extends InputValidatorAnnotationProcessorTest {

    }

    @Nested
    class NestedPostInitializeAnnotationProcessorTest extends PostInitializeAnnotationProcessorTest {

    }

    @Nested
    class NestedTableViewBindingAnnotationProcessorTest extends TableViewBindingAnnotationProcessorTest {

    }
}

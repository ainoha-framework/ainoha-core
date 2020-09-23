package com.ainoha.internal.annotation.processors;

import org.junit.jupiter.api.Test;
import test.fxapp.ViewControllersHub;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class PostInitializeAnnotationProcessorTest {

    /*
        IMPORTANT!
        ----------------
        ALL tests of this class are running as part of class com.ainoha.gui.GuiDependentWrapperTest
    */

    @Test
    public void process() {
        // The ViewTestController is injected in the ViewControllersHub
        // from a method annotated with @PostInitialize
        assertThat(ViewControllersHub.getInstance().getViewTestController())
                .isNotNull();
    }
}

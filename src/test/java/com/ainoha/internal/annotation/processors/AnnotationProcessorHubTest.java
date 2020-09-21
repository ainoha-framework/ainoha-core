package com.ainoha.internal.annotation.processors;

import com.ainoha.core.annotation.CssPressedStyleOnTouch;
import com.ainoha.core.annotation.PostInitialize;
import com.ainoha.core.annotation.TableViewBinding;
import com.ainoha.core.validators.InputValidator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotationProcessorHubTest {

    @Test
    public void registeredProcessorForPostInitializeAnnotation() {
        var processorFound = AnnotationProcessorHub.forAnnotationClass(PostInitialize.class);

        assertThat(processorFound)
                .isNotNull()
                .isExactlyInstanceOf(PostInitializeAnnotationProcessor.class);
    }

    @Test
    public void registeredProcessorForInputValidatorAnnotation() {
        var processorFound = AnnotationProcessorHub.forAnnotationClass(InputValidator.class);

        assertThat(processorFound)
                .isNotNull()
                .isExactlyInstanceOf(InputValidatorAnnotationProcessor.class);
    }

    @Test
    public void registeredProcessorForTableViewBindingAnnotation() {
        var processorFound = AnnotationProcessorHub.forAnnotationClass(TableViewBinding.class);

        assertThat(processorFound)
                .isNotNull()
                .isExactlyInstanceOf(TableViewBindingAnnotationProcessor.class);
    }

    @Test
    public void registeredProcessorForCssPressedStyleOnTouchAnnotation() {
        var processorFound = AnnotationProcessorHub.forAnnotationClass(CssPressedStyleOnTouch.class);

        assertThat(processorFound)
                .isNotNull()
                .isExactlyInstanceOf(CssPressedStyleOnTouchAnnotationProcessor.class);
    }

    @Test
    public void allRegisteredProcessors() {
        var processorClasses = AnnotationProcessorHub.registeredProcessorClasses();

        assertThat(processorClasses)
                .isNotNull()
                .hasSize(4)
                .containsExactlyInAnyOrder(
                        PostInitialize.class,
                        InputValidator.class,
                        TableViewBinding.class,
                        CssPressedStyleOnTouch.class
                );
    }
}

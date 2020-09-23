package com.ainoha.internal.annotation.processors;

import com.ainoha.core.exception.AnnotationProcessorException;
import com.ainoha.core.validators.InputValidator;
import javafx.application.Platform;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.jupiter.api.Test;
import test.fxapp.ViewControllersHub;
import test.fxapp.ViewTestController;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class InputValidatorAnnotationProcessorTest {

    /*
        IMPORTANT!
        ----------------
        ALL tests of this class are running as part of class com.ainoha.gui.GuiDependentWrapperTest
    */

    @Test
    public void processFailInvalidTargetFieldType() {
        Field nonTextInputControlField = FieldMother.getObjectField();
        var processor = new InputValidatorAnnotationProcessor();

        assertThatThrownBy(() -> processor.process(nonTextInputControlField, null))
                .isExactlyInstanceOf(AnnotationProcessorException.class)
                .hasMessage("Annotation @" + InputValidator.class.getName()
                                    + " can be used only in fields of type " + TextInputControl.class.getName()
                                    + ", or any of it subclasses"
                                    + ". Found field type: " + nonTextInputControlField.getType().getName());
    }

    @Test
    public void process() {
        ViewTestController controller = ViewControllersHub.getInstance().getViewTestController();
        var textField = controller.getTextField();
        Field textFieldField = FieldMother.getDeclaredField(controller.getClass(), "textField");
        var processor = new InputValidatorAnnotationProcessor();

        processor.process(textFieldField, controller);

        // Trigger key typed events
        var aKeyTyped = new KeyEvent(KeyEvent.KEY_TYPED, "a", "", KeyCode.UNDEFINED, false, false, false,false);
        var zeroKeyTyped = new KeyEvent(KeyEvent.KEY_TYPED, "0", "", KeyCode.UNDEFINED, false, false, false,false);

        // Fire the events in the JavaFX Thread
        // The text field is configured, with @InputValidator, to allow a max length
        // of 2 characters and only letters
        Platform.runLater(() -> {
            textField.fireEvent(aKeyTyped);   // Character `a` must be appended
            textField.fireEvent(zeroKeyTyped);// Character `0` must NOT be appended: Digits not allowed
            textField.fireEvent(aKeyTyped);   // Character `a` must be appended
            textField.fireEvent(aKeyTyped);   // Character `a` must NOT be appended: Max length reached
         });

        // Wait up to 3 seconds for events to dispatched
        int timeout = 3000;
        int consumedTime = 0;
        while (textField.getText().length() < 2 && consumedTime < timeout) {
            try {
                consumedTime += 50;
                Thread.sleep(50);
            } catch (InterruptedException e) { }
        }

        assertThat(textField.getText())
                .hasSize(2)
                .isEqualTo("aa");
    }
}

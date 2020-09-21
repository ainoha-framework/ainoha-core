package com.ainoha.internal.annotation.processors;

import com.ainoha.core.annotation.CssPressedStyleOnTouch;
import com.ainoha.core.exception.AnnotationProcessorException;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.input.PickResult;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import org.junit.jupiter.api.Test;
import test.fxapp.ViewControllersHub;
import test.fxapp.ViewTestController;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class CssPressedStyleOnTouchAnnotationProcessorTest {

    @Test
    public void processFailInvalidTargetFieldType() {
        Field nonNodeField = FieldMother.getObjectField();
        var processor = new CssPressedStyleOnTouchAnnotationProcessor();

        assertThatThrownBy(() -> processor.process(nonNodeField, null))
                .isExactlyInstanceOf(AnnotationProcessorException.class)
                .hasMessage("Annotation @" + CssPressedStyleOnTouch.class.getName()
                                    + " can be used only in fields of type " + Node.class.getName()
                                    + ", or any of it subclasses"
                                    + ". Found field type: " + nonNodeField.getType().getName());
    }

    @Test
    public void process() {
        ViewTestController controller = ViewControllersHub.getInstance().getViewTestController();
        var pane = controller.getPane();
        Field paneField = FieldMother.getDeclaredField(controller.getClass(), "pane");
        var processor = new CssPressedStyleOnTouchAnnotationProcessor();

        processor.process(paneField, controller);

        // Trigger a touch pressed event
        var touchPressedPoint = new TouchPoint(1, TouchPoint.State.PRESSED, 0, 0, 0, 0, pane, new PickResult(pane, 0, 0));
        var touchPressedEvent = new TouchEvent(pane, pane, TouchEvent.TOUCH_PRESSED, touchPressedPoint, List.of(), 2, false, false, false, false);
        pane.fireEvent(touchPressedEvent);

        // Assert :pressed pseudo-class active
        assertThat(pane.getPseudoClassStates())
                .hasSize(1)
                .containsExactly(PseudoClass.getPseudoClass("pressed"));

        // Trigger a touch released event
        var touchReleasedPoint = new TouchPoint(1, TouchPoint.State.RELEASED, 0, 0, 0, 0, pane, new PickResult(pane, 0, 0));
        var touchReleasedEvent = new TouchEvent(pane, pane, TouchEvent.TOUCH_RELEASED, touchReleasedPoint, List.of(), 2, false, false, false, false);
        pane.fireEvent(touchReleasedEvent);

        // Assert :pressed pseudo-class inactive
        assertThat(pane.getPseudoClassStates())
                .hasSize(0);
    }
}

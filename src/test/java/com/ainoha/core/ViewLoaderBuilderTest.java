package com.ainoha.core;

import com.ainoha.core.exception.ShowingViewException;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Test;
import test.fxapp.ViewTestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public final class ViewLoaderBuilderTest {

    @Test
    public void constructor() {
        var controllerClass = ViewTestController.class;

        assertThat(new ViewLoaderBuilder(controllerClass))
                .extracting("controllerClass")
                .isNotNull()
                .isSameAs(ViewTestController.class);

    }

    @Test
    public void builderOverrideDefaults() {
        var builder = new ViewLoaderBuilder(ViewTestController.class);

        assertAll(
                // stage
                () -> {
                    assertNull(getFieldValue(ViewLoaderBuilder.class, "viewStage", builder));
                    var stage = mock(Stage.class);
                    builder.stage(stage);
                    assertSame(stage, getFieldValue(ViewLoaderBuilder.class, "viewStage", builder));
                },

                // owner
                () -> {
                    assertNull(getFieldValue(ViewLoaderBuilder.class, "owner", builder));
                    var owner = mock(Stage.class);
                    builder.owner(owner);
                    assertSame(owner, getFieldValue(ViewLoaderBuilder.class, "owner", builder));
                },

                // userData
                () -> {
                    assertNull(getFieldValue(ViewLoaderBuilder.class, "params", builder));
                    var params = "param1";
                    builder.viewParams(params);
                    assertEquals(params, getFieldValue(ViewLoaderBuilder.class, "params", builder));
                },

                // modality
                () -> {
                    assertNull(getFieldValue(ViewLoaderBuilder.class, "modality", builder));
                    var modality = Modality.WINDOW_MODAL;
                    builder.modality(modality);
                    assertEquals(modality, getFieldValue(ViewLoaderBuilder.class, "modality", builder));
                },

                // stageStyle
                () -> {
                    assertNull(getFieldValue(ViewLoaderBuilder.class, "stageStyle", builder));
                    var stageStyle = StageStyle.UNDECORATED;
                    builder.stageStyle(stageStyle);
                    assertEquals(stageStyle, getFieldValue(ViewLoaderBuilder.class, "stageStyle", builder));
                },

                // notResizable
                () -> {
                    assertTrue((boolean) getFieldValue(ViewLoaderBuilder.class, "resizable", builder));
                    builder.notResizable();
                    assertFalse((boolean) getFieldValue(ViewLoaderBuilder.class, "resizable", builder));
                },

                // maximized
                () -> {
                    assertFalse((boolean) getFieldValue(ViewLoaderBuilder.class, "maximized", builder));
                    builder.maximized();
                    assertTrue((boolean) getFieldValue(ViewLoaderBuilder.class, "maximized", builder));
                },

                // fullScreenExitHint
                () -> {
                    assertNull(getFieldValue(ViewLoaderBuilder.class, "fullScreenExitHint", builder));
                    var fullScreenExitHint = "My Full Screen Exit Hint";
                    builder.fullScreenExitHint(fullScreenExitHint);
                    assertEquals(fullScreenExitHint, getFieldValue(ViewLoaderBuilder.class, "fullScreenExitHint", builder));
                },

                // fullScreenExitKeyCombination
                () -> {
                    assertNull(getFieldValue(ViewLoaderBuilder.class, "fullScreenExitKeyCombination", builder));
                    var fullScreenExitKeyCombination = new KeyCodeCombination(KeyCode.F12, KeyCombination.SHIFT_DOWN);
                    builder.fullScreenExitKeyCombination(fullScreenExitKeyCombination);
                    assertEquals(fullScreenExitKeyCombination, getFieldValue(ViewLoaderBuilder.class, "fullScreenExitKeyCombination", builder));
                }
        );
    }

    private Object getFieldValue(Class clazz, String fieldName, Object instance) {
        try {
            var field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error getting field '" + fieldName + "' value", e);
        }
    }

    @Test
    public void show() {
        var builder = new ViewLoaderBuilder(ViewTestController.class)
                .stage(mock(Stage.class));

        // show() throws this exception if FxmlViewHelper.showFxmlView() is called
        // because there are not JavaFX app running
        assertThatThrownBy(() -> builder.show())
                .isInstanceOf(ShowingViewException.class)
                .hasMessageStartingWith("An error occurred while showing the view");
    }

    @Test
    public void showUndecorated() {
        var builder = new ViewLoaderBuilder(ViewTestController.class)
                .stage(mock(Stage.class));

        // showUndecorated() throws this exception if FxmlViewHelper.showFxmlView() is called
        // because there are not JavaFX app running
        assertThatThrownBy(() -> builder.showUndecorated())
                .isExactlyInstanceOf(ShowingViewException.class)
                .hasMessageStartingWith("An error occurred while showing the view");

        assertThat(builder)
                .extracting("stageStyle")
                .isEqualTo(StageStyle.UNDECORATED);
    }

    @Test
    public void showFullScreen() {
        var builder = new ViewLoaderBuilder(ViewTestController.class)
                .stage(mock(Stage.class));

        assertThat(builder).extracting("fullScreen").isEqualTo(false);

        // showFullScreen() throws this exception if FxmlViewHelper.showFxmlView() is called
        // because there are not JavaFX app running
        assertThatThrownBy(() -> builder.showFullScreen())
                .isExactlyInstanceOf(ShowingViewException.class)
                .hasMessageStartingWith("An error occurred while showing the view");

        assertThat(builder).extracting("fullScreen").isEqualTo(true);
    }

    @Test
    public void reset() {
        var builder = new ViewLoaderBuilder(ViewTestController.class)
                .stage(mock(Stage.class))
                .owner(mock(Stage.class))
                .viewParams("param1")
                .modality(Modality.WINDOW_MODAL)
                .stageStyle(StageStyle.UNDECORATED)
                .notResizable()
                .maximized()
                .fullScreenExitHint("My Full Screen Exit Hint")
                .fullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.F12, KeyCombination.SHIFT_DOWN));

        assertAll("before reset",
                // stage
                () -> assertNotNull(getFieldValue(ViewLoaderBuilder.class, "viewStage", builder)),
                // owner
                () -> assertNotNull(getFieldValue(ViewLoaderBuilder.class, "owner", builder)),
                // userData
                () -> assertNotNull(getFieldValue(ViewLoaderBuilder.class, "params", builder)),
                // modality
                () -> assertNotNull(getFieldValue(ViewLoaderBuilder.class, "modality", builder)),
                // stageStyle
                () -> assertNotNull(getFieldValue(ViewLoaderBuilder.class, "stageStyle", builder)),
                // notResizable
                () -> assertFalse((boolean) getFieldValue(ViewLoaderBuilder.class, "resizable", builder)),
                // maximized
                () -> assertTrue((boolean) getFieldValue(ViewLoaderBuilder.class, "maximized", builder)),
                // fullScreenExitHint
                () -> assertNotNull(getFieldValue(ViewLoaderBuilder.class, "fullScreenExitHint", builder)),
                // fullScreenExitKeyCombination
                () -> assertNotNull(getFieldValue(ViewLoaderBuilder.class, "fullScreenExitKeyCombination", builder))
        );

        // Reset default values
        builder.reset();

        assertAll("after reset",
                  // stage
                  () -> assertNull(getFieldValue(ViewLoaderBuilder.class, "viewStage", builder)),
                  // owner
                  () -> assertNull(getFieldValue(ViewLoaderBuilder.class, "owner", builder)),
                  // userData
                  () -> assertNull(getFieldValue(ViewLoaderBuilder.class, "params", builder)),
                  // modality
                  () -> assertNull(getFieldValue(ViewLoaderBuilder.class, "modality", builder)),
                  // stageStyle
                  () -> assertNull(getFieldValue(ViewLoaderBuilder.class, "stageStyle", builder)),
                  // notResizable
                  () -> assertTrue((boolean) getFieldValue(ViewLoaderBuilder.class, "resizable", builder)),
                  // maximized
                  () -> assertFalse((boolean) getFieldValue(ViewLoaderBuilder.class, "maximized", builder)),
                  // fullScreenExitHint
                  () -> assertNull(getFieldValue(ViewLoaderBuilder.class, "fullScreenExitHint", builder)),
                  // fullScreenExitKeyCombination
                  () -> assertNull(getFieldValue(ViewLoaderBuilder.class, "fullScreenExitKeyCombination", builder))
        );
    }
}
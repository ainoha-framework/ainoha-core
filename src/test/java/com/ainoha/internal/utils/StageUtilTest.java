package com.ainoha.internal.utils;

import com.ainoha.core.ApplicationContext;
import org.junit.jupiter.api.Test;
import test.fxapp.ViewControllersHub;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class StageUtilTest {

    /*
        IMPORTANT!
        ----------------
        ALL tests of this class are running as part of class com.ainoha.gui.GuiDependentWrapperTest
    */

    @Test
    public void setStageTitleKeepCurrentTitleIfStageTitleAndStageTitleKeyAreBlankOrNull() {
        var context = ApplicationContext.instance();
        var stage = ViewControllersHub.getInstance().getViewTestController().getStage();
        String winTitle = "fake title";
        stage.setTitle(winTitle);

        StageUtil.setStageTitle(context, stage, null, null);
        assertThat(stage.getTitle())
                .isEqualTo(winTitle);

        StageUtil.setStageTitle(context, stage, "", "");
        assertThat(stage.getTitle())
                .isEqualTo(winTitle);
    }

    @Test
    public void setStageTitleAlwaysUseStageTitleParamIfNotBlank() {
        var context = ApplicationContext.instance();
        var stage = ViewControllersHub.getInstance().getViewTestController().getStage();
        stage.setTitle("fake title");

        String title1 = "Title 1";
        String title2 = "Title 2";

        StageUtil.setStageTitle(context, stage, null, title1);
        assertThat(stage.getTitle())
                .isEqualTo(title1);

        StageUtil.setStageTitle(context, stage, "titleKey", title2);
        assertThat(stage.getTitle())
                .isEqualTo(title2);
    }

    @Test
    public void setStageTitleSetBlankWhenThrowMissingResourceException() {
        setStageTitleBlankWhenThrowException(MissingResourceException.class);
    }

    @Test
    public void setStageTitleSetBlankWhenThrowRuntimeException() {
        setStageTitleBlankWhenThrowException(RuntimeException.class);
    }

    private void setStageTitleBlankWhenThrowException(Class<? extends Throwable> exceptionClass) {
        var stage = ViewControllersHub.getInstance().getViewTestController().getStage();
        stage.setTitle("fake title");

        var mockAppContext = mock(ApplicationContext.class);
        when(mockAppContext.getResourceBundle()).thenThrow(exceptionClass);

        // `stageTitle` param (the last one) must be null or blank
        // in order to not be picked as window title
        StageUtil.setStageTitle(mockAppContext, stage, "titleKey", null);

        assertThat(stage.getTitle())
                .isBlank();
    }

    @Test
    public void setStageTitleBlankIfLangResourcesAreNotFound() {
        var stage = ViewControllersHub.getInstance().getViewTestController().getStage();
        stage.setTitle("fake title");

        var mockAppContext = mock(ApplicationContext.class);
        when(mockAppContext.getResourceBundle()).thenReturn(null);
        when(mockAppContext.getDefaultResourceBundle()).thenReturn(null);

        // `stageTitle` param (the last one) must be null or blank
        // in order to not be picked as window title
        StageUtil.setStageTitle(mockAppContext, stage, "titleKey", null);

        assertThat(stage.getTitle())
                .isBlank();
    }

    @Test
    public void setStageTitleFromDefaultLangResources() {
        var stage = ViewControllersHub.getInstance().getViewTestController().getStage();
        stage.setTitle("fake title");

        // Build default language ResourceBundle
        String defaultTitleKey = "default.title";
        byte[] propFileAsBytes = (defaultTitleKey + "=Default title").getBytes();
        ResourceBundle rb = null;
        try {
            rb = new PropertyResourceBundle(new ByteArrayInputStream(propFileAsBytes));
        } catch (IOException e) { /* Never will be thrown */ }

        var mockAppContext = mock(ApplicationContext.class);
        when(mockAppContext.getResourceBundle()).thenReturn(null);
        when(mockAppContext.getDefaultResourceBundle()).thenReturn(rb);

        // `stageTitle` param (the last one) must be null or blank
        // in order to not be picked as window title
        StageUtil.setStageTitle(mockAppContext, stage, defaultTitleKey, null);

        assertThat(stage.getTitle())
                .isEqualTo(rb.getString(defaultTitleKey));
    }

    @Test
    public void setStageTitleFromCurrentLangResources() {
        var stage = ViewControllersHub.getInstance().getViewTestController().getStage();
        stage.setTitle("fake title");

        String titleKey = "win.title";

        // Build default language ResourceBundle
        byte[] defaultPropFileAsBytes = (titleKey + "=Default title").getBytes();
        ResourceBundle defaultRb = null;
        try {
            defaultRb = new PropertyResourceBundle(new ByteArrayInputStream(defaultPropFileAsBytes));
        } catch (IOException e) { /* Never will be thrown */ }

        // Build CURRENT language ResourceBundle
        byte[] propFileAsBytes = (titleKey + "=Current title").getBytes();
        ResourceBundle rb = null;
        try {
            rb = new PropertyResourceBundle(new ByteArrayInputStream(propFileAsBytes));
        } catch (IOException e) { /* Never will be thrown */ }

        var mockAppContext = mock(ApplicationContext.class);
        when(mockAppContext.getResourceBundle()).thenReturn(rb);
        when(mockAppContext.getDefaultResourceBundle()).thenReturn(defaultRb);

        // `stageTitle` param (the last one) must be null or blank
        // in order to not be picked as window title
        StageUtil.setStageTitle(mockAppContext, stage, titleKey, null);

        assertThat(stage.getTitle())
                .isEqualTo(rb.getString(titleKey));
    }
}

package com.ainoha.core;

import com.ainoha.core.annotation.FxApplication;
import com.ainoha.core.exception.ApplicationContextNotFoundException;
import javafx.application.Application;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.utils.ApplicationContextUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class ApplicationContextTest {

    private ApplicationContext currentApplicationContext;

    @BeforeEach
    public void backupCurrentApplicationContext() throws Exception {
        currentApplicationContext = ApplicationContextUtil.getCurrentApplicationContext();
    }

    @AfterEach
    public void resetCurrentApplicationContext() throws Exception {
        ApplicationContextUtil.setApplicationContext(currentApplicationContext);
    }


    // TESTS FOR METHOD: startApplication()
    @Test
    public void startApplicationFailsIfContextIsAlreadyInitialized() throws Exception {
        ApplicationContextUtil.mockApplicationContext();

        assertThatThrownBy(() -> ApplicationContext.startApplication(null))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("ApplicationContext.startApplication() cannot be called more than once");
    }

    @Test
    public void startApplicationFailsIfAppClassIsNull() throws Exception {
        ApplicationContextUtil.setApplicationContext(null);

        assertThatThrownBy(() -> ApplicationContext.startApplication(null))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("'appClass' cannot be null");
    }

    abstract class FakeFxApplication extends Application { }

    @Test
    public void startApplicationFailsIfAppClassIsNotAnnotatedWithFxApplication() throws Exception {
        var appClass = FakeFxApplication.class;
        ApplicationContextUtil.setApplicationContext(null);

        assertThatThrownBy(() -> ApplicationContext.startApplication(appClass))
                  .isExactlyInstanceOf(IllegalArgumentException.class)
                  .hasMessage("Application could not be started. Class " + appClass.getName()
                                      + " must be annotated with @" + FxApplication.class.getName());
    }


    // TESTS FOR METHOD: instance()
    @Test
    public void instanceFailIfContextIsNotInitialized() throws Exception {
        ApplicationContextUtil.setApplicationContext(null);

        assertThatThrownBy(() -> ApplicationContext.instance())
                .isExactlyInstanceOf(ApplicationContextNotFoundException.class)
                .hasMessage("Application context not exist");
    }

    @Test
    public void instance() throws Exception {
        ApplicationContextUtil.mockApplicationContext();
        var mockApplicationContext = ApplicationContextUtil.getCurrentApplicationContext();

        assertThat(ApplicationContext.instance())
                .isSameAs(mockApplicationContext);
    }
}

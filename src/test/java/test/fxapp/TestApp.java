package test.fxapp;

import com.ainoha.core.ApplicationContext;
import com.ainoha.core.ViewLoader;
import com.ainoha.core.annotation.FxApplication;

import java.util.Locale;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

@FxApplication
public class TestApp extends Application implements ViewLoader {

    public static void runApp(String[] args) {
        Locale.setDefault(new Locale("en"));
        try {
            ApplicationContext.startApplication(TestApp.class, args);
        } catch (Exception e) {
            e.printStackTrace();

            terminate();
        }
    }

    @Override
    public void start(Stage stage) {
        view(ViewTestController.class)
                .stage(stage)
                .show();
    }

    public static void terminate() {
        ViewControllersHub.getInstance().terminate();
        Platform.exit();
    }
}

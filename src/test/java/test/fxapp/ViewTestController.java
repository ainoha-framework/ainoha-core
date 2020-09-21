package test.fxapp;

import com.ainoha.core.annotation.FxmlController;
import com.ainoha.core.annotation.PostInitialize;
import com.ainoha.core.annotation.ViewStage;
import com.ainoha.core.validators.InputValidator;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

@FxmlController(fxmlPath = "/test/fxapp/ViewTest.fxml")
public final class ViewTestController {

    @ViewStage
    Stage stage;

    @FXML private Pane pane;
    @InputValidator(maxLength = 2, pattern = "[a-z]*")
    @FXML private TextField textField;
    @FXML private TableView tableView;


    @PostInitialize
    public void postInit() {
        ViewControllersHub.getInstance().setViewTestController(this);

        stage.setOnCloseRequest(event -> exit());
    }

    public void exit() {
        // This method is used, instead Platform.exit(), because
        // it also allows to invalidate the ViewControllersHub
        TestApp.terminate();
    }

    public Pane getPane() {
        return pane;
    }

    public TextField getTextField() {
        return textField;
    }

    public TableView getTableView() {
        return tableView;
    }
}

package com.ainoha.internal.annotation.processors;

import com.ainoha.core.annotation.TableViewBinding;
import com.ainoha.core.exception.AnnotationProcessorException;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import test.fxapp.ViewControllersHub;
import test.fxapp.ViewTestController;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public abstract class TableViewBindingAnnotationProcessorTest {

    /*
        IMPORTANT!
        ----------------
        ALL tests of this class are running as part of class com.ainoha.gui.GuiDependentWrapperTest
    */

    @Test
    public void processFailInvalidTargetFieldType() {
        Field nonTableViewField = FieldMother.getObjectField();
        var processor = new TableViewBindingAnnotationProcessor();

        assertThatThrownBy(() -> processor.process(nonTableViewField, null))
                .isExactlyInstanceOf(AnnotationProcessorException.class)
                .hasMessage("Annotation @" + TableViewBinding.class.getName()
                                    + " can be used only in fields of type " + TableView.class.getName()
                                    + ". Found field type: " + nonTableViewField.getType().getName());
    }

    @Test
    public void process() {
        ViewTestController controller = ViewControllersHub.getInstance().getViewTestController();
        var tableView = controller.getTableView();
        Field tableViewField = FieldMother.getDeclaredField(controller.getClass(), "tableView");
        var processor = new TableViewBindingAnnotationProcessor();

        processor.process(tableViewField, controller);

        assertThat(tableView.getColumns())
                .hasSize(2);

        TableColumn col1 = (TableColumn) tableView.getColumns().get(0);
        TableColumn col2 = (TableColumn) tableView.getColumns().get(1);

        // Always check both columns
        assertAll(
                // Check first column
                () -> assertThat(col1.getCellValueFactory())
                        .isNotNull()
                        .isExactlyInstanceOf(PropertyValueFactory.class)
                        .asInstanceOf(InstanceOfAssertFactories.type(PropertyValueFactory.class))
                        .extracting("property")
                        .isNotNull()
                        .isEqualTo(col1.getId()),

                // Check second column
                () -> assertThat(col2.getCellValueFactory())
                        .isNotNull()
                        .isExactlyInstanceOf(PropertyValueFactory.class)
                        .asInstanceOf(InstanceOfAssertFactories.type(PropertyValueFactory.class))
                        .extracting("property")
                        .isNotNull()
                        .isEqualTo(col2.getId())
        );
    }
}

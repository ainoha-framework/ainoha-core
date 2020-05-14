/**
 * Copyright 2019 Eduardo E. Betanzos Morales
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ainoha.internal.annotation.processors;

import com.ainoha.core.annotation.TableViewBinding;
import com.ainoha.core.exception.AnnotationProcessorException;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.lang.reflect.Field;

/**
 * Processor for {@code @}{@link TableViewBinding} annotation.<br>
 * <br>
 * Method {@link TableViewBindingAnnotationProcessor#process(Object, Object)} has to receive as first parameter a
 * reference to the annotated field (an instance of {@link Field}) and as second the controller instance containing
 * this field. The annotated field must by an instance of {@link javafx.scene.control.TableView}
 *
 * @author Eduardo Betanzos
 * @since 1.0
 */
class TableViewBindingAnnotationProcessor implements AnnotationProcessor {
    @Override
    public void process(Object target, Object source) {
        try {
            Field field = (Field) target;

            if (!field.getType().equals(TableView.class)) {
                throw new AnnotationProcessorException(
                        "Annotation @" + TableViewBinding.class.getName()
                                + " can be used only in fields of type " + TableView.class.getName()
                                + ". Found field type: " + field.getType().getName()
                );
            }

            field.setAccessible(true);
            TableView tableView = (TableView) field.get(source);
            addTableColumsDataBinding(tableView.getColumns());
        } catch (AnnotationProcessorException e) {
            throw e;
        } catch (Exception e) {
            throw new AnnotationProcessorException(e);
        }
    }

    /**
     * @param columns List of table columns (see {@link TableColumn})
     *
     * @see TableViewBinding
     */
    public static void addTableColumsDataBinding(ObservableList columns) {
        columns.stream().forEach(column -> {
            TableColumn tableColumn = (TableColumn) column;

            ObservableList chlidColumns = tableColumn.getColumns();
            if (chlidColumns.isEmpty()) {
                tableColumn.setCellValueFactory(new PropertyValueFactory(tableColumn.getId()));
            } else {
                addTableColumsDataBinding(chlidColumns);
            }
        });
    }
}

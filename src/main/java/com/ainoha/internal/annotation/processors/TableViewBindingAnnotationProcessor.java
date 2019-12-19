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
 * Procesador para la anotación {@code @}{@link TableViewBinding}.<br>
 * <br>
 * El método {@link TableViewBindingAnnotationProcessor#process(Object, Object)} tiene que recibir como primer parámetro
 * una referencia al campo anotado (instancia de {@link Field}), el que tiene que ser de tipo {@link javafx.scene.control.TableView}
 * y como segundo parámetro la instancia del controlador que contiene este campo.<br>
 * <br>
 * Si ocurre algún error durante la ejecución del método {@link TableViewBindingAnnotationProcessor#process(Object, Object)}
 * se lanzará una excepción de tipo {@link AnnotationProcessorException}.
 *
 * @author Eduardo Betanzos
 */
class TableViewBindingAnnotationProcessor implements AnnotationProcessor {
    @Override
    public void process(Object target, Object source) {
        try {
            Field field = (Field) target;

            if (!field.getType().equals(TableView.class)) {
                throw new AnnotationProcessorException("La anotación @" + TableViewBinding.class.getName()
                        + " solo puede aplicarse a campos de tipo " + TableView.class.getName() + ". Tipo de dato encontrado: "
                        + field.getType().getName());
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
     * Crea, para cada una de las columnas de la lista {@code columns}, un {@code CellValueFactory} para hacer binding
     * con una propiedad X dentro de un Bean. Para lograr que el binding funcione, el valor del atributo {@link TableColumn#id}
     * de cada columna debe coincidir con el nombre de la propiedad con que se desea hacer binding dentro del Bean. <br>
     * <br>
     * En otras palabras, este método permite definir automáticamente el binding de las columnas de una tabla con las
     * propiedades correspondientes dentro de los objetos con que se llena esta.
     *
     * @param columns Listado de columnas de una tabla (ver {@link TableColumn})
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

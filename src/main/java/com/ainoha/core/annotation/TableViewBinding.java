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
package com.ainoha.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows to add a {@code CellValueFactory} to each column of the annotated {@link javafx.scene.control.TableView} in
 * order to bind its with the model class properties. This binding work only if the value of
 * {@link javafx.scene.control.TableColumn#id} match with the name of the property which you want to display in the
 * column.<br>
 * <br>
 * In other words, this annotation allows you to automatically define the binding of the columns of a table with the
 * corresponding properties within the objects with which it is filled.
 *
 * @author Eduardo Betanzos
 * @since 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableViewBinding {

}

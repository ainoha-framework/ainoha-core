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
 * Permite agregar a cada una de las columnas del {@link javafx.scene.control.TableView} anotado un {@code CellValueFactory}
 * para hacer un binding con una propiedad dentro de una clase usada como modelo. Para lograr que el binding funcione,
 * el valor del campo {@link javafx.scene.control.TableColumn#id} de cada columna debe coincidir con el nombre de la
 * propiedad con la que se desea hacer el binding dentro del modelo.<br>
 * <br>
 * En otras palabras, esta anotación permite definir automáticamente el binding de las columnas de una tabla con las
 * propiedades correspondientes dentro de los objetos con que esta se llena.
 *
 * @author Eduardo Betanzos
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableViewBinding {

}

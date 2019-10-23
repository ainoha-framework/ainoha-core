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
 * Marca una clase como controlador de una vista FXML. Las clases anotadas con esta anotación son las que se podrán
 * utiliar para mostrar vistas de la aplicación. Esto invierte el modo propuesto por JavaFX para  mostrar vistas del tipo
 * FXML, ya que en vez de definir en el archivo {@code .fxml} de la vista el controlador asociado ahora en el controlador
 * definidmos la vista que este maneja. Esto desacopla el proceso de diseño de la programación, ya que no es necesario
 * ligar un archivo {@code .fxml} a una clase de la aplicación.
 *
 * @author Eduardo Betanzos
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FxmlController {

    /**
     * Ruta, relativa al CLASS_PATH, del archivo FXML de la vista (ej. /com/my/app/view.fxml). Si la ruta no se incluye
     * la extensión {@code .fxml} el framework la incuirá.
     */
    String fxmlPath();

    /**
     * Ruta, relativa al CLASS_PATH, del icono de la ventana (ej. /com/my/app/icon.png)<br>
     * <br>
     * Valor por defecto: "app.png" (No se tendrá en cuenta)
     */
    String iconPath() default "/app.png";

    /**
     * Clave del título de la ventana dentro de los recursos de idioma<br>
     * <br>
     * Valor por defecto: "" (No se tendrá en cuenta)
     */
    String titleKey() default "";

    /**
     * Título de la ventana. Si se le define un valor provocará que no tenga en cuenta la propiedad
     * {@link FxmlController#titleKey()}<br>
     * <br>
     * Valor por defecto: "" (No se tendrá en cuenta)
     */
    String title() default "";
}

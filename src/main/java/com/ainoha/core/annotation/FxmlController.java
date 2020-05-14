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
 * Defines a FXML view controller.<br>
 * <br>
 * Classes annotated with this annotation will be those that can be used to display application views. This invert
 * the JavaFX's way to display the views from {@code .fxml} files, since each controller defines it FXML view
 * associated instead of define which controller handle the view in the {@code .fxml} file. This approach allow
 * to decouple the design and programming processes, since it is not necessary to bind a {@code .fxml} file to an
 * application class.
 *
 * @author Eduardo Betanzos
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FxmlController {

    /**
     * Path, in the CLASSPATH, to the view FXML file (i.e. /com/my/app/view.fxml). File extension
     * ({@code .fxml}) is optional.
     */
    String fxmlPath();

    /**
     * Path, in the CLASSPATH, to the view icon (ej. /com/my/app/icon.png)<br>
     * <br>
     * Default: "/app.png"
     */
    String iconPath() default "/app.png";

    /**
     * Window title key within language resources<br>
     * <br>
     * Default: "" (Will be ignored)
     */
    String titleKey() default "";

    /**
     * Window title. This override the value taken from {@link FxmlController#titleKey()}<br>
     * <br>
     * Default: "" (Will be ignored)
     */
    String title() default "";
}

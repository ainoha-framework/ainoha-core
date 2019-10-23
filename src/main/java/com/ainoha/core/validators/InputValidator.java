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
package com.ainoha.core.validators;

import javafx.scene.control.TextInputControl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Permite agregar las validaciones definidas por {@link #pattern()} y {@link #maxLength()} a un {@link TextInputControl}
 * (solo debe utilizarse con controles de este tipo).<br>
 * <br>
 * Estas validaciones permiten evitar la entrada de texto inválido al control. Por tal motivo la validación se lleva a
 * cabo para cada intento de entrada de dato. Si el texto del control una vez hecha la entrada es inválido, dicha
 * entrada será rechazada.
 *
 * @author Eduardo Betanzos
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InputValidator {
    /**
     * Expresión regular que será utilizada para validar el contenido del campo de texto.
     */
    String pattern() default ".*";

    /**
     * Cantidad máxima de caracteres que se podrán introducir en el campo de texto.<br>
     * <br>
     * Un valor menor o igual que 0 indica que no se realice la validación.
     */
    int maxLength() default 0;
}

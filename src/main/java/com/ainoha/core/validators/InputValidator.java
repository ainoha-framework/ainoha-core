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
 * Allows to add validations to the annotated {@link TextInputControl} (should be used only with subtypes of
 * this class).<br>
 * <br>
 * This validations avoid entering invalid text. Because of this, validation is carried out for each data entry
 * attempt. If the text control once the entry is made is invalid, the entry will be rejected.<br>
 * <br>
 *
 * @author Eduardo Betanzos
 * @since 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InputValidator {
    /**
     * Regex defining the text pattern
     */
    String pattern() default ".*";

    /**
     * Max capacity of the text field.<br>
     * <br>
     * Values less than or equals to 0 will disable the validation
     */
    int maxLength() default 0;
}

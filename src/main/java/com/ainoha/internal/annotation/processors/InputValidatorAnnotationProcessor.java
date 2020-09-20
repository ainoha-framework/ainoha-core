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

import com.ainoha.core.validators.InputValidator;
import com.ainoha.core.exception.AnnotationProcessorException;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * Processor for {@code @}{@link InputValidator} annotation.<br>
 * <br>
 * {@link InputValidatorAnnotationProcessor#process(Object, Object)} method must receive as first parameter a
 * reference to the annotated field (an instance of {@link Field}) and as second the controller instance
 * containing this field. The annotated field must by an instance of {@link InputValidator}.
 *
 * @author Eduardo Betanzos
 * @since 1.0
 */
class InputValidatorAnnotationProcessor implements AnnotationProcessor {

    @Override
    public void process(Object target, Object source) {
        try {
            Field field = (Field) target;

            if (!TextInputControl.class.isAssignableFrom(field.getType())) {
                throw new AnnotationProcessorException(
                        "Annotation @" + InputValidator.class.getName()
                                + " can be used only in fields of type " + TextInputControl.class.getName()
                                + ", or any of it subclasses"
                                + ". Found field type: " + field.getType().getName()
                );
            }

            InputValidator annotation = field.getDeclaredAnnotation(InputValidator.class);

            field.setAccessible(true);
            TextInputControl inputControl = (TextInputControl) field.get(source);
            addValidator(inputControl, annotation.pattern(), annotation.maxLength());
        } catch (AnnotationProcessorException e) {
            throw e;
        } catch (Exception e) {
            throw new AnnotationProcessorException(e);
        }
    }

    private void addValidator(final TextInputControl textField, final String validationPattern, final int maxLength) {
        textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.V) {// for catch Ctrl + V
                String textToPaste = Clipboard.getSystemClipboard().getString();

                if (textToPaste == null
                        || textToPaste.isEmpty()
                        || !maxLengthValidation(maxLength, textField, textToPaste)
                        || !patternValidation(validationPattern, textField, textToPaste)) {

                    event.consume();
                }
            }
        });

        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String textToInsert = event.getCharacter();

            if (!maxLengthValidation(maxLength, textField, textToInsert)
                    || !patternValidation(validationPattern, textField, textToInsert)) {

                event.consume();
            }
        });
    }

    private boolean maxLengthValidation(int maxLength, TextInputControl textField, String textToInsert) {
        int countCharactersToReplace = textField.getSelection().getEnd() - textField.getSelection().getStart();
        return !(maxLength > 0 && (textField.getText().length() + textToInsert.length() - countCharactersToReplace) > maxLength);
    }

    private boolean patternValidation(String validationPattern, TextInputControl textField, String textToInsert) {
        String currentText = textField.getText();
        String eventResultText = currentText.substring(0, textField.getSelection().getStart())
                + textToInsert
                + currentText.substring(textField.getSelection().getEnd());

        return Pattern.matches(validationPattern, eventResultText);
    }
}

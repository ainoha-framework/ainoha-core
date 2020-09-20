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

import com.ainoha.core.annotation.CssPressedStyleOnTouch;
import com.ainoha.core.exception.AnnotationProcessorException;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.input.TouchEvent;

import java.lang.reflect.Field;

/**
 * Processor for {@code @}{@link CssPressedStyleOnTouch} annotation.<br>
 * <br>
 * {@link CssPressedStyleOnTouchAnnotationProcessor#process(Object, Object)} method must receive as first parameter a
 * reference to the annotated field (an instance of {@link Field}) and as second the controller instance containing
 * this field.
 *
 * @author Eduardo Betanzos
 * @since 1.0
 */
class CssPressedStyleOnTouchAnnotationProcessor implements AnnotationProcessor {

    @Override
    public void process(Object target, Object source) {
        try {
            Field field = (Field) target;

            if (!Node.class.isAssignableFrom(field.getType())) {
                throw new AnnotationProcessorException(
                        "Annotation @" + CssPressedStyleOnTouch.class.getName()
                                + " can be used only in fields of type " + Node.class.getName()
                                + ", or any of it subclasses"
                                + ". Found field type: " + field.getType().getName()
                );
            }

            field.setAccessible(true);
            Node node = (Node) field.get(source);
            addTouchPressedFilter(node);
        } catch (AnnotationProcessorException e) {
            throw e;
        } catch (Exception e) {
            throw new AnnotationProcessorException(e);
        }
    }

    private void addTouchPressedFilter(final Node node) {
        node.addEventFilter(TouchEvent.TOUCH_PRESSED, event -> {
            node.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });
        node.addEventFilter(TouchEvent.TOUCH_RELEASED, event -> {
            node.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });
    }
}

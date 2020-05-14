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

import com.ainoha.core.annotation.FxmlController;
import com.ainoha.core.exception.AnnotationProcessorException;

/**
 * @author Eduardo Betanzos
 * @since 1.0
 */
public interface AnnotationProcessor {
    /**
     * Executes the process associated with the annotation
     *
     * @param target Annotated element (usually an instance of either {@link java.lang.reflect.Field}
     *               or {@link java.lang.reflect.Method})
     * @param source Object containing the {@code target} (an instance of a class annotated with {@link FxmlController})
     *
     * @throws AnnotationProcessorException If an error occurs during method execution
     */
    void process(Object target, Object source);
}

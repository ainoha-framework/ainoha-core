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

import com.ainoha.core.annotation.PostInitialize;
import com.ainoha.core.exception.AnnotationProcessorException;

import java.lang.reflect.Method;

/**
 * Procesador para la anotación {@code @}{@link PostInitialize}.<br>
 * <br>
 * El método {@link PostInitializeAnnotationProcessor#process(Object, Object)} tiene que recibir como primer parámetro
 * una referencia al método anotado (instancia de {@link Method}) y como segundo parámetro la instancia del controlador
 * que contiene este método.<br>
 * <br>
 * Si ocurre algún error durante la ejecución del método {@link PostInitializeAnnotationProcessor#process(Object, Object)}
 * se lanzará una excepción de tipo {@link AnnotationProcessorException}.
 *
 * @author Eduardo Betanzos
 */
public class PostInitializeAnnotationProcessor implements AnnotationProcessor {
    @Override
    public void process(Object target, Object source) {
        try {
            Method method = (Method) target;
            method.setAccessible(true);
            method.invoke(source);
        } catch (Exception e) {
            throw new AnnotationProcessorException(e);
        }
    }
}

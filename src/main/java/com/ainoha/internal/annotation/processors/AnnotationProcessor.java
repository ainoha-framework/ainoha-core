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

/**
 * @author Eduardo Betanzos
 */
public interface AnnotationProcessor {
    /**
     * Ejecuta el proceso asociado a la anotación.
     *
     * @param target Elemento anotado (generalmente una instancia de: {@link java.lang.reflect.Field} o {@link java.lang.reflect.Method})
     * @param source Objeto que contiene al {@code target} (instancia de una clase anotada con {@link FxmlController})
     */
    void process(Object target, Object source);
}

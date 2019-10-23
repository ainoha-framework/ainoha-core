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
import com.ainoha.core.annotation.TableViewBinding;
import com.ainoha.core.validators.InputValidator;

import java.util.Map;
import java.util.Set;

/**
 * @author Eduardo Betanzos
 */
public class AnnotationProcessorHub {
    private final static Map<Class, AnnotationProcessor> processors;

    static {
        processors = Map.of(
                PostInitialize.class, new PostInitializeAnnotationProcessor(),
                InputValidator.class, new InputValidatorAnnotationProcessor(),
                TableViewBinding.class, new TableViewBindingAnnotationProcessor()
        );
    }

    public static AnnotationProcessor forAnnotationClass(Class clazz) {
        return processors.get(clazz);
    }
    public static Set<Class> registeredProcessorClasses() {
        return processors.keySet();
    }
}

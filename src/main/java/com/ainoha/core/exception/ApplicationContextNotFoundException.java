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
package com.ainoha.core.exception;

/**
 * Se lanza para indicar que no existe el contexto de la aplicación. Esto puede deberse principalmente a que no se ha
 * iniciado la aplicación a través de una llamada al método {@link com.ainoha.core.ApplicationContext#startApplication}.
 *
 * @author Eduardo Betanzos
 */
public class ApplicationContextNotFoundException extends RuntimeException {

    public ApplicationContextNotFoundException() {
    }

    public ApplicationContextNotFoundException(String message) {
        super(message);
    }
}

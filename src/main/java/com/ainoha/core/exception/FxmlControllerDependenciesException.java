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
 * Se lanza para indicar que ocurrió un error relacionado con las dependencias de un controlador de archivos FXML.
 * Generalmente está relaciona con errores al intentar inyectar las dependencias en el controllador o al intentar
 * obtener referencias a una de estas dependencias.
 *
 * @author Eduardo Betanzos
 */
public class FxmlControllerDependenciesException extends RuntimeException {

    public FxmlControllerDependenciesException() {
    }

    public FxmlControllerDependenciesException(String message) {
        super(message);
    }

    public FxmlControllerDependenciesException(String message, Throwable cause) {
        super(message, cause);
    }
    
}

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
package com.ainoha.core;

/**
 * Agrega a la clase anotada una forma sencilla de obtener un builder que simplifica el proceso de mostrar
 * vistas, ya que este puede requerir diferentes combinaciones de parámetros.
 *
 * @author Eduardo Betanzos
 */
public interface ViewLoader {

    default ViewLoaderBuilder view(Class controllerClass) {
        return new ViewLoaderBuilder(controllerClass);
    }
}

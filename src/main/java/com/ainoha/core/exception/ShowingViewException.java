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
 * An error occurred while showing an application view. Must contain another exception with the error details.
 *
 * @author Eduardo Betanzos
 * @since 1.0
 */
public class ShowingViewException extends RuntimeException {

    public ShowingViewException() {
    }

    public ShowingViewException(String message, Throwable cause) {
        super(message, cause);
    }
}

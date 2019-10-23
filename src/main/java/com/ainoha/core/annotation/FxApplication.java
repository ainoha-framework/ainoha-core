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
package com.ainoha.core.annotation;

import com.ainoha.core.ApplicationContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación principal del framework que debe ser utilizada en la clase principal de la aplicación de JavaFX la cual a
 * su vez debe heradar de {@link javafx.application.Application}. La clase que haya sido anotada con esta anotación es la
 * que se debe utilizar para arrancar la aplicación por medio de {@link ApplicationContext#startApplication(Class, String...)}.
 *
 * @author Eduardo Betanzos
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FxApplication {

    /**
     * Paquete que contiene los recursos de idioma + el nombre común de los archivos {@code .properties}
     * (ej. paquete.de.recursos.language, donde 'language' es el nombre de los archivos de idioma como: language_es.properties,
     * language_en.properties). <br>
     * <br>
     * Valor por defecto: language (se refiere a los archivos language_xx.properties que se encuentren en el paquete por defecto)
     */
    String langResourcesPackage() default "language";

    /**
     * Idioma por defecto (ej. 'es' para español, 'en' para inglés, etc)<br>
     * <br>
     * Valor por defecto: es (Idioma español)
     */
    String defaultLang() default "es";

    /**
     * Ruta, relativa al CLASS_PATH, del icono de la aplicación (ej. /com/my/app/icon.png)<br>
     * <br>
     * Valor por defecto: /app.png (Archivo PNG depositado en la raíz del CLASS_PATH)
     */
    String appImagePath() default "/app.png";
}

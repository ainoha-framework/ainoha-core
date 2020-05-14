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
 * This is the Ainoha Framework most important annotation which must be used in the main JavaFX application class (
 * which inherits from {@link javafx.application.Application}).<br>
 * <br>
 * The annotated class must be used to start the application through
 * {@link ApplicationContext#startApplication(Class, String...)}.
 *
 * @author Eduardo Betanzos
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FxApplication {

    /**
     * Package of language resources + common filename of {@code .properties} (i.e. resources.lang.language,
     * being 'language' the common filename: language_en.properties, language_es.properties)<br>
     * <br>
     * Default: language (refers to the language_xx.properties files in the default package)
     */
    String langResourcesPackage() default "language";

    /**
     * Default language (i.e. 'en' for english, 'es' for spanish, etc.)<br>
     * <br>
     * Default: en
     */
    String defaultLang() default "en";

    /**
     * Application image path relative to the CLASSPATH (ej. /com/my/app/icon.png)<br>
     * <br>
     * Default: /app.png
     */
    String appImagePath() default "/app.png";
}

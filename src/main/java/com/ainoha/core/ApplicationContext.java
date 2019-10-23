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

import com.ainoha.core.annotation.FxApplication;
import com.ainoha.core.exception.ApplicationContextNotFoundException;
import com.ainoha.core.exception.ApplicationStartupException;
import com.ainoha.internal.utils.ReflectionUtil;
import javafx.application.Application;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contexto de la aplicación.
 *
 * @author Eduardo Betanzos
 */
public final class ApplicationContext {

    private static final Logger LOGGER = Logger.getLogger(ApplicationContext.class.getName());

    /**
     * Referencia al contexto de la aplicación
     */
    private static ApplicationContext context;

    /**
     * Paquete que contiene los recursos de idioma + el nombre común de los archivos {@code .properties}
     * (ej. paquete.de.recursos.languaje, donde 'languaje' es el nombre de los archivos de idioma como: languaje_es.properties,
     * languaje_en.properties)
     */
    private final String LANG_RESOURCES;
    /**
     * Recursos de idioma para el idioma definido en {@link Locale#getDefault()}
     */
    private ResourceBundle resourceBundle;

    /**
     * {@link Locale} actual de la aplicación. Se actualiza con la llamada al método {@link ApplicationContext#getResourceBundle()}
     */
    private Locale currentResourcesLocale;

    /**
     * Idioma por defecto (ej. 'es' para español, 'en' para inglés, etc)
     */
    private final String DEFAULT_LOCALE_LANG;
    /**
     * Recursos de idioma para el idioma por defecto (definido por {@link ApplicationContext#DEFAULT_LOCALE_LANG})
     */
    private ResourceBundle defaultResourceBundle;

    /**
     * Ruta, relativa al CLASS_PATH, del icono de la aplicación (ej. /com/my/app/icon.png)
     */
    private final String appImageResourcePath;
    /**
     * Imagen de la aplicación
     */
    private Image appImage;
    /**
     * Parámetros de línea de comandos pasados a la aplicación JavaFX
     */
    private final String[] appParams;

    /**
     * Crea una instancia de la clase.
     *
     * @param appClass Clase principal de la aplicación anotada con {@code @}{@link FxApplication}. Esta clase tiene que
     *      *          heradar de la clase {@link Application}
     * @param langResources Paquete que contiene los recursos de idioma + el nombre común de los archivos {@code .properties}
     *                      (ej. paquete.de.recursos.languaje, donde 'languaje' es el nombre de los archivos de idioma
     *                      como: languaje_es.properties, languaje_en.properties)
     * @param defaultLang Idioma por defecto (ej. 'es' para español, 'en' para inglés, etc)
     * @param appImageResourcePath Ruta, relativa al CLASS_PATH, del icono de la aplicación (ej. /com/my/app/icon.png)
     * @param appParams Parámetros de línea de comandos pasados a la aplicación JavaFX
     */
    private ApplicationContext(final Class appClass, final String langResources, final String defaultLang, final String appImageResourcePath, String... appParams) {
        LANG_RESOURCES = langResources;
        DEFAULT_LOCALE_LANG = defaultLang;
        this.appParams = appParams;

        if (this.LANG_RESOURCES != null && !this.LANG_RESOURCES.isBlank()) {
            currentResourcesLocale = Locale.getDefault();
            try {
                resourceBundle = ResourceBundle.getBundle(LANG_RESOURCES, currentResourcesLocale);
            } catch (MissingResourceException e) {
                LOGGER.severe("Error cargando el recurso de idioma para el locale actual: " + LANG_RESOURCES + "_"
                        + currentResourcesLocale.getLanguage());
            }

            if (this.DEFAULT_LOCALE_LANG != null && !this.DEFAULT_LOCALE_LANG.isBlank()) {
                try {
                    defaultResourceBundle = ResourceBundle.getBundle(LANG_RESOURCES, new Locale(DEFAULT_LOCALE_LANG));
                } catch (MissingResourceException e) {
                    LOGGER.severe("Error cargando el recurso de idioma por defecto: " + LANG_RESOURCES + "_"
                            + DEFAULT_LOCALE_LANG);
                }
            }
        }

        this.appImageResourcePath = appImageResourcePath;
        if (this.appImageResourcePath != null && !this.appImageResourcePath.isBlank()) {
            InputStream appImageStream = appClass.getResourceAsStream(this.appImageResourcePath);
            if (appImageStream != null) {
                appImage = new Image(appImageStream);
            }
        }
    }

    /**
     * Arranca la aplicación de JavaFX e inicializa su contexto. Típicamente este método se llama desde el método
     * {@code main(String[])}. No debe ser llamado más de una vez (excepto si la llamada anterior lanzó una excepción)
     * ya que una excepción del tipo {@link IllegalStateException} será lanzada.
     *
     * @param appClass Clase principal de la aplicación anotada con {@code @}{@link FxApplication}. Esta clase tiene que
     *                 heradar de la clase {@link Application}
     * @param args Parámetros por línea de comandos pasados a la aplicación
     *
     * @throws IllegalStateException Si el método es llamado más de una vez con éxito
     * @throws IllegalArgumentException Si el parámetro {@code appClass} es {@code null} o si la clase que representa
     *                                  dicho parámetro no está anotada con {@code @}{@link FxApplication}
     * @throws ApplicationStartupException Si ocurrió un error durante la invocación al método {@link Application#launch(String...)}
     */
    public static void startApplication(Class<? extends Application> appClass, String... args) {
        if (context != null) {
            throw new IllegalStateException("El método ApplicationContext.startApplication() no puede ser llamado más de una vez");
        }

        if (appClass == null) {
            throw new IllegalArgumentException("No se pudo iniciar la aplicación. 'appClass' no puede ser null");
        }

        FxApplication fxApplicationAnnotation = appClass.getDeclaredAnnotation(FxApplication.class);

        if (fxApplicationAnnotation == null) {
            throw new IllegalArgumentException("No se pudo iniciar la aplicación. La clase " + appClass.getName()
                    + " debe estar anotada con @" + FxApplication.class.getName());
        }

        // Crear el contexto de la aplicación
        context = new ApplicationContext(appClass,
                                         fxApplicationAnnotation.langResourcesPackage(),
                                         fxApplicationAnnotation.defaultLang(),
                                         fxApplicationAnnotation.appImagePath(),
                                         args);

        boolean startupError = false;
        Throwable startupErrorCause = null;
        try {
            ReflectionUtil.invokeStaticMethod(Application.class, "launch", new Class[] {Class.class, String[].class}, appClass, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // NoSuchMethodException no puede ocurrir porque appClass representa una clase que hereda de javafx.application.Application
            // IllegalAccessException no puede ocurrir, porque el método 'launch' de la clase javafx.application.Application es 'public static'
            // InvocationTargetException se lanza si ocurre un error en la ejecución del método
            LOGGER.log(Level.FINE, "Error invocando el método 'launch()' en la clase principal de la aplicación", e);

            startupError = true;
            startupErrorCause = e;
        } finally {
            if (startupError) {
                // Invalidar el contexto
                context = null;

                throw new ApplicationStartupException("No se puedo arrancar la aplicación producto a un error", startupErrorCause);
            }
        }
    }

    /**
     * Permite obtener una referencia al contexto de la aplicación. Si este método se llamada antes de arrancar la aplicación
     * (esto es, no se ha realizado una llamada exitosa al método {@link ApplicationContext#startApplication}) se lanzará
     * una excepción del tipo {@link ApplicationContextNotFoundException}
     *
     * @return Referencia al contexto de la aplicación
     *
     * @throws ApplicationContextNotFoundException Si no existe el contexto de la aplicación
     */
    public static ApplicationContext instance() {
        if (context == null) {
            throw new ApplicationContextNotFoundException("No existe el contexto de la aplicación");
        }

        return context;
    }

    /**
     * Permite obtener los recursos de idioma de la aplicación para el idioma definido en {@link ApplicationContext#currentResourcesLocale}.
     * Si los recursos de idioma actuales, no se corresponden con el idioma actual de {@link Locale#getDefault()}, estos
     * serán actualizados.
     *
     * @return Recursos de idioma o {@code null} si no se configuraron
     */
    public ResourceBundle getResourceBundle() {
        if (this.LANG_RESOURCES == null || this.LANG_RESOURCES.isBlank()) {
            return null;
        }

        if (!currentResourcesLocale.equals(Locale.getDefault())) {
            currentResourcesLocale = Locale.getDefault();
            resourceBundle = ResourceBundle.getBundle(LANG_RESOURCES, currentResourcesLocale);
        }

        return resourceBundle;
    }

    /**
     * Permite obtener los recursos de idioma para el idioma por defecto de la aplicación.
     *
     * @return Recursos de idioma por defecto o {@code null} si no se configuró
     */
    public ResourceBundle getDefaultResourceBundle() {
        return defaultResourceBundle;
    }

    /**
     * Permite obtener la imágen de la aplicación.
     *
     * @return Imagen de la aplicación o {@code null} si no se configuró
     */
    public Image getAppImage() {
        return appImage;
    }

    /**
     * Permite  obtener los parátros de línea de comandos pasados a la aplicación JavaFX.
     *
     * @return Parámetros de línea de comandos
     */
    public String[] getAppParams() {
        return appParams;
    }
}

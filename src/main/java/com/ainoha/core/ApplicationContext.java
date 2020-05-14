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
 * This class handle the global context of the application.
 *
 * @author Eduardo Betanzos
 * @since 1.0
 */
public final class ApplicationContext {

    private static final Logger LOGGER = Logger.getLogger(ApplicationContext.class.getName());

    private static ApplicationContext context;

    /**
     * Package of language resources + common filename of {@code .properties} (i.e. resources.lang.language,
     * being 'language' the common filename: language_en.properties, language_es.properties)
     */
    private final String LANG_RESOURCES;
    /**
     * Language resources for the language (defined by {@link Locale#getDefault()})
     */
    private ResourceBundle resourceBundle;

    /**
     * Current application {@link Locale}. Could be updated when the method {@link ApplicationContext#getResourceBundle()}
     * is called.
     */
    private Locale currentResourcesLocale;

    /**
     * Default language (i.e. 'en' for english, 'es' for spanish, etc.)
     */
    private final String DEFAULT_LOCALE_LANG;
    /**
     * Language resources for the default language (defined by {@link ApplicationContext#DEFAULT_LOCALE_LANG})
     */
    private ResourceBundle defaultResourceBundle;

    /**
     * Application image path relative to the CLASSPATH (ej. /com/my/app/icon.png)
     */
    private final String appImageResourcePath;
    /**
     * Application image
     */
    private Image appImage;
    /**
     * Applications command-line arguments
     */
    private final String[] appParams;

    /**
     * Create an instance.
     *
     * @param appClass             Application main class which should be annotated with {@code @}{@link FxApplication}
     *                             and inherit from {@code @}{@link FxApplication}
     * @param langResources        Package for language resources + common filename of {@code .properties} (i.e.
     *                             resources.lang.language, being 'language' the common filename: language_en.properties,
     *                             language_es.properties)
     * @param defaultLang          Default language (i.e. 'en' for english, 'es' for spanish, etc.)
     * @param appImageResourcePath Application image path relative to the CLASSPATH (ej. /com/my/app/icon.png)
     * @param appParams            Applications command-line arguments
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
                LOGGER.severe("Error loading the language resource for the current locale: " + LANG_RESOURCES + "_"
                        + currentResourcesLocale.getLanguage());
            }

            if (this.DEFAULT_LOCALE_LANG != null && !this.DEFAULT_LOCALE_LANG.isBlank()) {
                try {
                    defaultResourceBundle = ResourceBundle.getBundle(LANG_RESOURCES, new Locale(DEFAULT_LOCALE_LANG));
                } catch (MissingResourceException e) {
                    LOGGER.severe("Error loading default language resource: " + LANG_RESOURCES + "_"
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
     * Launch the JavaFX application and initialize its context. Typically this method will be called from
     * {@code main(String[])} method. Should not be called more than once (except if the previous call threw
     * an exception) because an {@link IllegalStateException} will be throw.
     *
     * @param appClass Application main class which should be annotated with {@code @}{@link FxApplication}
     *                 and inherit from {@code @}{@link FxApplication}
     * @param args     Applications command-line arguments
     *
     * @throws IllegalStateException       If this method is called more than once successfully
     * @throws IllegalArgumentException    If {@code appClass} is {@code null} or the class is not annotated
     *                                     with {@code @}{@link FxApplication}
     * @throws ApplicationStartupException If an exception is thew when the {@link Application#launch(String...)}
     *                                     method is invoked
     */
    public static void startApplication(Class<? extends Application> appClass, String... args) {
        if (context != null) {
            throw new IllegalStateException("ApplicationContext.startApplication() cannot be called more than once");
        }

        if (appClass == null) {
            throw new IllegalArgumentException("'appClass' cannot be null");
        }

        FxApplication fxApplicationAnnotation = appClass.getDeclaredAnnotation(FxApplication.class);

        if (fxApplicationAnnotation == null) {
            throw new IllegalArgumentException("Application could not be started. Class " + appClass.getName()
                    + " must be annotated with @" + FxApplication.class.getName());
        }

        // Crear el contexto de la aplicación
        context = new ApplicationContext(appClass,
                                         fxApplicationAnnotation.langResourcesPackage(),
                                         fxApplicationAnnotation.defaultLang(),
                                         fxApplicationAnnotation.appImagePath(),
                                         args);

        try {
            ReflectionUtil.invokeStaticMethod(
                    Application.class,
                    "launch",
                    new Class[] {Class.class, String[].class},
                    appClass,
                    args
            );
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // NoSuchMethodException cannot occur because appClass represents a class that inherits
            //                       from javafx.application.Application
            // IllegalAccessException cannot occur because javafx.application.Application#launch()
            //                        method is 'public static'
            // InvocationTargetException thrown if an error occurs in method execution
            LOGGER.log(Level.FINE, "Error invoking 'launch()' method from tha application main class", e);

            // Invalidar el contexto
            context = null;

            throw new ApplicationStartupException("Application could not start due to an error", e);
        }
    }

    /**
     * Allows to get a reference to the application context. If this method is called before launch the application
     * (this is, a successful call to method {@link ApplicationContext#startApplication} was not made) an
     * {@link ApplicationContextNotFoundException} will be throw.
     *
     * @return A reference to the application context
     *
     * @throws ApplicationContextNotFoundException If the application context does not exist
     */
    public static ApplicationContext instance() {
        if (context == null) {
            throw new ApplicationContextNotFoundException("Application context not exist");
        }

        return context;
    }

    /**
     * Allows to get the language resources for the current language (defined by {@link ApplicationContext#currentResourcesLocale}).
     * If current cached language resources do not match the current language (defined by {@link Locale#getDefault()}) these will
     * be updated.
     *
     * @return Language resources or {@code null} if there are not defined
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
     * Allows to get the language resources for the default application language.
     *
     * @return Default language resources or {@code null} if there are not defined
     */
    public ResourceBundle getDefaultResourceBundle() {
        return defaultResourceBundle;
    }

    /**
     * Allows to get the application image.
     *
     * @return Application image or {@code null} if there are not defined
     */
    public Image getAppImage() {
        return appImage;
    }

    /**
     * Allows to get the applications command-line parameters
     *
     * @return Command-line parameters
     */
    public String[] getAppParams() {
        return appParams;
    }
}

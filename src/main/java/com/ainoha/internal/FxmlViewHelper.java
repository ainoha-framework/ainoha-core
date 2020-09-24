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
package com.ainoha.internal;

import com.ainoha.core.ApplicationContext;
import com.ainoha.core.annotation.*;
import com.ainoha.core.exception.ControllerConstructorNotFoundException;
import com.ainoha.core.exception.ShowingViewException;
import com.ainoha.core.exception.ViewNotFoundException;
import com.ainoha.internal.annotation.processors.AnnotationProcessorHub;
import com.ainoha.internal.utils.ReflectionUtil;
import com.ainoha.internal.utils.StageUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class handle the process of loading and displaying FXML files as application views.<br>
 * <br>
 * In order to use this class the view controllers must be annotated with {@code @}{@link FxmlController}.
 *
 * @author Eduardo Betanzos
 * @since 1.0
 */
public final class FxmlViewHelper {

    private FxmlViewHelper() {}

    /**
     * Show an FXML application view.
     *
     * @param controllerClass    View controller class. Must be annotated with {@code @}{@link FxmlController}
     * @param viewStage          Stage where the view will be displayed. If is {@code null} one will be created
     * @param owner              Owner of the Stage where the view will be displayed. If is {@code null} will not
     *                           be defined
     * @param params             Is used to pass data to the view
     * @param modality           Window {@link Modality}. If is {@code null} default JavaFX value will
     *                           be used
     * @param stageStyle         The {@link StageStyle}. If is {@code null} default JavaFX value will be used
     * @param resizable          Defines if the window can be resized
     * @param fullScreen         Defines whether the window should be displayed in full screen mode
     * @param fullScreenExitHint Specifies the text to show when the window enters full screen mode. If is
     *                           {@code null} or {@code fullScreen == false} will be ignored so default JavaFX
     *                           text will be used
     * @param fullScreenExitKeyCombination Specifies the {@link KeyCombination} to exit full screen mode. If is
     *                                     {@code null} or {@code fullScreen == false} will be ignored so default
     *                                     JavaFX value will be used
     *
     * @throws ShowingViewException If an error occurs during method execution. Cause must contain more details
     */
    public static void showFxmlView(Class controllerClass, Stage viewStage, Stage owner, Object params, Modality modality,
                                    StageStyle stageStyle, boolean resizable, boolean maximized, boolean fullScreen,
                                    String fullScreenExitHint, KeyCombination fullScreenExitKeyCombination) {

        String viewFilePath = null;

        try {
            var controllerMetadata = getControllerMetadata(controllerClass);

            Stage stage = viewStage == null ? new Stage() : viewStage;

            // Add the window icon
            // By default, image defined in the application context must be used
            Image viewImage = ApplicationContext.instance().getAppImage();

            String viewIconPath = controllerMetadata.viewIconPath;
            if (viewIconPath != null) {
                InputStream appImageStream = controllerClass.getResourceAsStream(viewIconPath);
                if (appImageStream != null) {
                    viewImage = new Image(appImageStream);
                }
            }

            if (viewImage != null) {
                stage.getIcons().clear();
                stage.getIcons().add(viewImage);
            }

            // Pass data to the view
            stage.setUserData(params);

            if (modality != null) {
                stage.initModality(modality);
            }

            if (stageStyle != null) {
                stage.initStyle(stageStyle);
            }

            stage.setResizable(resizable);
            stage.setMaximized(maximized);

            // Full screen mode
            if (fullScreen) {
                stage.setFullScreen(fullScreen);

                if (fullScreenExitHint != null) {
                    stage.setFullScreenExitHint(fullScreenExitHint);
                }

                if (fullScreenExitKeyCombination != null) {
                    stage.setFullScreenExitKeyCombination(fullScreenExitKeyCombination);
                }
            }

            // Create view controller instance
            Object controller = getControllerInstance(controllerClass);

            // Load the FXML view file into the Stage
            viewFilePath = controllerMetadata.viewFilePath;
            loadViewFromResources(stage, owner, controller, viewFilePath, controllerMetadata.titleKey, controllerMetadata.title);

            // Display the view
            stage.show();
        } catch (Exception e) {
            throw new ShowingViewException("An error occurred while showing the view '" + viewFilePath + "'", e);
        }
    }

    /**
     * Allows to load the view specified by the annotation {@code @}{@link FxmlController} defined in the
     * {@code controller} class. {@code controller} will be defined as the view controller.
     *
     * @param controller Controller of the view to be displayed. This object class must be annotated with
     *                   {@code @}{@link FxmlController}
     * @param fully      If {@code true} all controller class members (related with Ainoha Framework) will
     *                   be processed, otherwise only the fields will be
     *
     * @return View root
     */
    public static Parent loadFxmlViewAsParent(Object controller, boolean fully) {
        String viewFilePath = null;

        try {
            Class controllerClass = controller.getClass();

            var controllerMetadata = getControllerMetadata(controllerClass);
            viewFilePath = controllerMetadata.viewFilePath;
            FXMLLoader loader = new FXMLLoader(controllerClass.getResource(viewFilePath));

            // Override the FXML file controller definition
            loader.setController(controller);

            // Set the language resource for render view texts in de current locale
            ApplicationContext context = ApplicationContext.instance();
            ResourceBundle rb = context.getResourceBundle();
            loader.setResources(rb);

            // Execute the JavaFX loading process
            Parent root = loader.load();

            // Inject dependencies to the view controller
            injectControllerDependencies(controller, null, null, rb, loader.getLocation());

            if (fully) {
                // Processes all controller class members (fields and methods) related with Ainoha Framework
                processControllerAnnotations(controller);
            } else {
                // Processes controller class fields related with Ainoha Framework and annotated with
                // {@code @}{@link FXML} too
                // This is needed because when the view is loaded by JavaFX all controller fields annotated with
                // {@code @}{@link FXML} are reprocessed and any previous processing doing by Ainoha Framework will
                // be lost
                processControllerAnnotationsForFxmlAnnotatedFields(controller);
            }

            return root;
        } catch (Exception e) {
            throw new ShowingViewException("An error occurred while showing the view  '" + viewFilePath
                    + "' associated with the controller " + controller.getClass().getName(), e);
        }
    }

    static class ViewControllerMetadata {
        private String viewFilePath;
        private String viewIconPath;
        private String titleKey;
        private String title;

        public ViewControllerMetadata(String viewFilePath, String viewIconPath, String titleKey, String title) {
            this.viewFilePath = viewFilePath;
            this.viewIconPath = viewIconPath;
            this.titleKey = titleKey;
            this.title = title;
        }
    }

    /**
     * Allows to get the {@code controllerClass} metadata defined by {@code @}{@link FxmlController} annotation.
     *
     * @param controllerClass Clase del controlador
     *
     * @return Instance of {@link ViewControllerMetadata}
     *
     * @throws IllegalArgumentException If {@code controllerClass} is not annotated with {@code @}{@link FxmlController}
     *                                  or {@code fxmlPath} is empty (using {@link String#isBlank()})
     * @throws ViewNotFoundException    If the FXML file defined by {@code @}{@link FxmlController#fxmlPath()} is not
     *                                  found
     */
    private static ViewControllerMetadata getControllerMetadata(Class controllerClass) {
        FxmlController controllerAnnotation = (FxmlController) controllerClass.getDeclaredAnnotation(FxmlController.class);

        if (controllerAnnotation == null) {
            throw new IllegalArgumentException("Class " + controllerClass.getName() + " must be annotated with @"
                    + FxmlController.class.getName());
        }

        String viewFilePath = controllerAnnotation.fxmlPath();

        if (viewFilePath.isBlank()) {
            throw new IllegalArgumentException("The value of 'fxmlPath' parameter is empty in the annotation @"
                    + FxmlController.class.getName() + " defined in " + controllerClass.getName());
        }

        if (!viewFilePath.endsWith(".fxml")) {
            viewFilePath += ".fxml";
        }

        if (controllerClass.getResource(viewFilePath) == null) {
            throw new ViewNotFoundException("The FXML view file '" + viewFilePath + "' defined in "
                    + controllerClass.getName() + "was not found");
        }

        return new ViewControllerMetadata(viewFilePath,
                                          controllerAnnotation.iconPath(),
                                          controllerAnnotation.titleKey(),
                                          controllerAnnotation.title());
    }

    /**
     * Creates an instance of {@code controllerClass}. A non-argument or default constructor is required.
     *
     * @param controllerClass Controller class to instantiate
     *
     * @return Instance of {@code controllerClass}
     *
     * @throws ControllerConstructorNotFoundException If the class no have a non-argument or default constructor
     * @throws IllegalAccessException                 If the constructor is inaccessible
     * @throws InvocationTargetException              If constructor call throws an exception because of it
     *                                                implementation
     * @throws InstantiationException                 If {@code controllerClass} is an abstract class
     *
     */
    private static Object getControllerInstance(Class controllerClass)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {

        try {
            return ReflectionUtil.newInstanceOf(controllerClass);
        } catch (NoSuchMethodException e) {
            throw new ControllerConstructorNotFoundException("Non-argument constructor, or default, was not found in "
                    + "class " + controllerClass.getName());
        }
    }

    /**
     * Load the view {@code viewFileName} in the {@code viewStage}.<br>
     * <br>
     * This process include:<br>
     * - inject dependencies,<br>
     * - set window title, and<br>
     * - process all other annotations not related to dependency injection
     *
     * @param viewStage      Stage where the view will be displayed. If is {@code null} one will be created
     * @param owner          Owner of the Stage where the view will be displayed. If is {@code null} will not
     *                       be defined
     * @param viewController View controller class. Must be annotated with {@code @}{@link FxmlController}
     * @param viewFilePath   Path, in the CLASSPATH, to the view FXML file (i.e. /com/my/app/view.fxml)
     * @param stageTitleKey  Window title key within language resources
     * @param stageTitle     Window title. This override the value taken from {@code stageTitleKey}
     */
    private static void loadViewFromResources(Stage viewStage,
                                              Stage owner,
                                              Object viewController,
                                              String viewFilePath,
                                              String stageTitleKey,
                                              String stageTitle) throws IOException {

        FXMLLoader loader = new FXMLLoader(viewController.getClass().getResource(viewFilePath));

        // Override the FXML file controller definition
        loader.setController(viewController);

        // Set the language resource for render view texts in de current locale
        ApplicationContext context = ApplicationContext.instance();
        ResourceBundle rb = context.getResourceBundle();
        loader.setResources(rb);

        // Execute the JavaFX loading process
        Parent root = loader.load();

        // If the stage have a scene it is reused
        if (viewStage.getScene() != null) {
            viewStage.getScene().setRoot(root);
        } else {
            Scene scene = new Scene(root);
            viewStage.setScene(scene);
        }

        if (!viewStage.isShowing()) {
            if (owner != null) {
                viewStage.initOwner(owner);
            }
            viewStage.centerOnScreen();
        }

        // Inject dependencies to the view controller
        injectControllerDependencies(viewController, viewStage, viewStage.getScene(), rb, loader.getLocation());

        // Set window title
        StageUtil.setStageTitle(context, viewStage, stageTitleKey, stageTitle);

        // Process all controller class members (fields and methods) related with Ainoha Framework
        processControllerAnnotations(viewController);
    }

    /**
     * Inject dependencies in the {@code viewController} fields.
     *
     * @param viewController View controller instance
     * @param stage          View Stage
     * @param scene          View Scene
     * @param rb             Language resources
     * @param viewURL        FXML view file URL
     */
    private static void injectControllerDependencies(Object viewController, Stage stage, Scene scene, ResourceBundle rb, URL viewURL) {
        if (ReflectionUtil.isAnnotatedWith(viewController.getClass(), FxmlController.class)) {
            if (stage != null) {
                ReflectionUtil.setValueInAnnotatedFields(viewController, ViewStage.class, stage);
            }

            if (scene != null) {
                ReflectionUtil.setValueInAnnotatedFields(viewController, ViewScene.class, scene);
            }

            if (rb != null) {
                ReflectionUtil.setValueInAnnotatedFields(viewController, ViewResourceBundle.class, rb);
            }

            if (viewURL != null) {
                ReflectionUtil.setValueInAnnotatedFields(viewController, ViewFxmlUrl.class, viewURL);
            }
        }
    }

    /**
     * Processes all class members (fields and methods) of {@code viewController} related with Ainoha Framework.
     *
     * @param viewController View controller instance
     */
    private static void processControllerAnnotations(Object viewController) {
        List<AccessibleObject> members = new ArrayList<>();

        Collections.addAll(members, viewController.getClass().getDeclaredFields());
        Collections.addAll(members, viewController.getClass().getDeclaredMethods());

        for (var accessibleObject : members) {
            AnnotationProcessorHub.registeredProcessorClasses()
                    .stream()
                    .filter(accessibleObject::isAnnotationPresent)
                    .map(AnnotationProcessorHub::forAnnotationClass)
                    .forEach(annotationProcessor -> annotationProcessor.process(accessibleObject, viewController));
        }
    }

    /**
     * Processes the class fields of {@code viewController} related with Ainoha Framework annotated with @FXML.
     *
     * @param viewController View controller instance
     */
    private static void processControllerAnnotationsForFxmlAnnotatedFields(Object viewController) {
        Field[] fields = viewController.getClass().getDeclaredFields();

        for (var field : fields) {
            if (field.getAnnotation(FXML.class) == null) {
                continue;
            }

            AnnotationProcessorHub.registeredProcessorClasses()
                    .stream()
                    .filter(field::isAnnotationPresent)
                    .map(AnnotationProcessorHub::forAnnotationClass)
                    .forEach(annotationProcessor -> annotationProcessor.process(field, viewController));
        }
    }
}

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
 * Esta clase permite gestionar el proceso de carga y visualización de archivos FXML.<br>
 * <br>
 * Para utilizar esta clase es necesario que los controladores de las vistas estén anotados con {@code @}{@link FxmlController}.
 * 
 * @author Eduardo Betanzos
 */
public final class FxmlViewHelper {
    
    /**
     * Muestra una vista de la aplicación.
     *
     * @param controllerClass Clase del controllador de la vista que se desea mostrar. Esta clase debe estar anotada con
     *                        {@code @}{@link FxmlController} y dicha anotación debe definir obligatoriamente el valor
     *                        del parámetro {@code fxmlPath}.
     * @param viewStage Stage (escenario) donde se debe mostrar la vista. Si es {@code null} se creará uno.
     * @param owner Stage (escenario) propietario de la vista que se desea mostrar. Si es {@code null} no se definirá.
     * @param params Se usa para pasar parámetros a la nueva vista.
     * @param modality Modo en que se mostrará la ventana (ver {@link Modality}). Si es {@code null} se utilizará la
     *                 modalidad por defecto de JavaFX.
     * @param stageStyle Estilo del Stage. Si es {@code null} se utilizará el estilo por defecto de JavaFX.
     * @param resizable Indica si la ventana podrá redimensionarse.
     * @param fullScreen Indica si la ventana se debe mostrar en modo pantalla-completa.
     * @param fullScreenExitHint Define el mensaje de salida que se mostrará en modo pantalla-completa. Solo se usará su
     *                           valor si {@code fullScreen == true}. Si su valor es {@code null} se omitirá, por lo que
     *                           se utilizará el valor por defecto que asigne JavaFX.
     * @param fullScreenExitKeyCombination Combinación de teclas para salir del modo pantalla-completa. Solo se usará si
     *                                     {@code fullscreen == true}. Si su valor es {@code null} se omitirá, por lo que
     *                                     se utilizará el valor por defecto que asigne JavaFX.
     *
     * @throws ShowingViewException Si ocurre algún error durante el proceso. La causa contendrá más detalles del error.
     */
    public static void showFxmlView(Class controllerClass, Stage viewStage, Stage owner, Object params, Modality modality,
                                    StageStyle stageStyle, boolean resizable, boolean fullScreen, String fullScreenExitHint,
                                    KeyCombination fullScreenExitKeyCombination) {

        String viewFilePath = null;

        try {
            // Cargar los metadatos del controlador
            var controllerMetadata = getControllerMetadata(controllerClass);

            // Stage (escenario) donde se mostrará la vista
            Stage stage = viewStage == null ? new Stage() : viewStage;

            // Agregar el icono de la vantana. Utilizar por defecto el la imagen de la aplicación definida en el ApplicationContext
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

            // Pasar los parámetros
            stage.setUserData(params);

            // Definir la modalidad
            if (modality != null) {
                stage.initModality(modality);
            }

            // Definir estilo del stage
            if (stageStyle != null) {
                stage.initStyle(stageStyle);
            }

            // Definir si es redimensionable
            stage.setResizable(resizable);

            // Activar modo pantalla-completa
            if (fullScreen) {
                stage.setFullScreen(fullScreen);

                if (fullScreenExitHint != null) {
                    stage.setFullScreenExitHint(fullScreenExitHint);
                }

                if (fullScreenExitKeyCombination != null) {
                    stage.setFullScreenExitKeyCombination(fullScreenExitKeyCombination);
                }
            }

            // Crear la instancia del controllador
            Object controller = getControllerInstance(controllerClass);

            // Cargar y mostrar la vista dentro del stage (escenario)
            viewFilePath = controllerMetadata.viewFilePath;
            loadViewFromResources(stage, owner, controller, viewFilePath, controllerMetadata.titleKey, controllerMetadata.title);

            // Mostrar la ventana
            stage.show();
        } catch (Exception e) {
            throw new ShowingViewException("Ocurrió un error mientras se mostraba la vista '" + viewFilePath + "'", e);
        }
    }

    /**
     * Permite cargar la vista definida en la anotación {@code @}{@link FxmlController} definida en la declaración de la
     * clase correspondiente a {@code controller}. A la vista cargada se le asociará {@code controller} como controlador.
     *
     * @param controller Controllador de la vista que se desea mostrar. La calse de este objeto debe estar anotada con
     *                   {@code @}{@link FxmlController} y dicha anotación debe definir obligatoriamente el valor
     *                   del parámetro {@code fxmlPath}.
     *
     * @param fully Si es {@code true} se procesarán todos los miembros anotados, en caso contrario solo se procesarán los
     *              atributos.
     *
     * @return Raíz de la vista
     */
    public static Parent loadFxmlViewAsParent(Object controller, boolean fully) {
        String viewFilePath = null;

        try {
            Class controllerClass = controller.getClass();

            var controllerMetadata = getControllerMetadata(controllerClass);
            viewFilePath = controllerMetadata.viewFilePath;

            // Creo el loader para cargar el XML de la vista
            FXMLLoader loader = new FXMLLoader(controllerClass.getResource(controllerMetadata.viewFilePath));

            // Sobrescribir la definición del controlador hecha en el archivo FXML
            loader.setController(controller);

            // Referencia al contexto de la aplicación
            ApplicationContext context = ApplicationContext.instance();

            // Le paso al Loader el fichero de idioma para cargar la vista según el Locale actual del sistema
            ResourceBundle rb = context.getResourceBundle();
            loader.setResources(rb);

            // Cargo la vista y obtengo la raíz
            Parent root = loader.load();

            // Injectar las dependencias al controllador
            injectControllerDependencies(controller, null, null, rb, loader.getLocation());

            if (fully) {
                // Procesar todas las anotaciones presentes en el controllador (ej. @PostInitialize)
                processControllerAnnotations(controller);
            } else {
                // Procesar las anotaciones presentes en los campos del controllador anotados también con @FXML (ej. @TableViewBinding)
                // Esto es necesario porque cuando se carga la vista, los campos anotados con @FXML son procesados por JavaFX
                // y se pierde cualquier procesamiento previo hecho por Ainoha Framework
                processControllerAnnotationsForFxmlAnnotatedFields(controller);
            }

            return root;
        } catch (Exception e) {
            throw new ShowingViewException("Ocurrió un error mientras se mostraba la vista '" + viewFilePath
                    + "' correspondiente al controlador " + controller.getClass().getName(), e);
        }
    }

    /**
     * Esta clase se utiliza para evitar tener que procesar varias veces un controlador anotado con {@code @}{@link FxmlController}
     * con el fin de obtener los valores de los parámetros de esta anotación.
     */
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
     * Permite obtener los metadatos de controlador que representa {@code controllerClass}. Este controlador debe estar
     * anotado con {@code @}{@link FxmlController}.
     *
     * @param controllerClass Clase del controlador
     *
     * @return Instancia de {@link ViewControllerMetadata}
     *
     * @throws IllegalArgumentException Si la clase {@code controllerClass} no está anotada con {@code @}{@link FxmlController}
     *                                  o si al llamar {@link String#isBlank()} sobre el valor del parámetro {@code fxmlPath}
     *                                  devuelve {@code true}
     * @throws ViewNotFoundException Si no se encuentra el archivo FXML definido en {@code @}{@link FxmlController#fxmlPath()}
     *                               como recurso de la aplicación
     */
    private static ViewControllerMetadata getControllerMetadata(Class controllerClass) {
        FxmlController controllerAnnotation = (FxmlController) controllerClass.getDeclaredAnnotation(FxmlController.class);

        if (controllerAnnotation == null) {
            throw new IllegalArgumentException("La clase " + controllerClass.getName() + " debe estar anotada con @"
                    + FxmlController.class.getName());
        }

        String viewFilePath = controllerAnnotation.fxmlPath();

        if (viewFilePath.isBlank()) {
            throw new IllegalArgumentException("No se definió el valor del parámetro 'fxmlPath' en la anotación @"
                    + FxmlController.class.getName() + " de la clase " + controllerClass.getName());
        }

        if (!viewFilePath.endsWith(".fxml")) {
            viewFilePath += ".fxml";
        }

        if (controllerClass.getResource(viewFilePath) == null) {
            throw new ViewNotFoundException("No se encontró la vista FXML '" + viewFilePath + "' definida en "
                    + controllerClass.getName());
        }

        return new ViewControllerMetadata(viewFilePath,
                                          controllerAnnotation.iconPath(),
                                          controllerAnnotation.titleKey(),
                                          controllerAnnotation.title());
    }

    /**
     * Crea una instancia del controlador que representa la clase {@code controllerClass}. Para esto es necesario que la
     * clase cuente con un constructor sin argumentos, o uno por defecto.
     *
     * @param controllerClass Clase del controllador que se desea instanciar
     *
     * @return Instancia de {@code controllerClass}
     *
     * @throws ControllerConstructorNotFoundException Si la clase no cuenta con un constructor sin arguentos o uno por defecto
     * @throws IllegalAccessException Si el constructor es inaccesible
     * @throws InvocationTargetException Si la llamada al constructor de la clase lanza una excepción producto de su implementación
     * @throws InstantiationException Si la clase que {@code controllerClass} es abstracta
     */
    private static Object getControllerInstance(Class controllerClass) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        try {
            return ReflectionUtil.newInstanceOf(controllerClass);
        } catch (NoSuchMethodException e) {
            throw new ControllerConstructorNotFoundException("No se encontró el constructor por defecto o uno sin " +
                    "argumetos en el controllador " + controllerClass.getName());
        }
    }

    /**
     * Carga la vista que representa {@code viewFileName} y en el stage (escenario) {@code viewStage}. El proceso de cargar
     * la vista incluye: inyectar las dependencias anotadas con anotaciones de Ainoha Framework, poner el título a la ventana
     * de acuerdo al idioma actual y procesar el resto de anotaciones de Ainoha Framework.
     * 
     * @param viewStage Stage (escenario) donde se cargará la vista.
     * @param owner Stage (escenario) dueño de la vista a mostrar. Si en {@code null} se omitirá.
     * @param viewController Controllador de la vista.
     * @param viewFilePath Ruta del archivo FXML de la vista relativo al CLASS_PATH, ej: /com/my/app/view.fxml.
     * @param stageTitleKey Clave del título de la ventana dentro de los recursos de idioma.
     * @param stageTitle Título de la ventana. Sobrescribe a {@code stageTitleKey} si su valor es diferente de {@code null}
     *                   y {@link String#isBlank()} retorna {@code false}.
     */
    private static void loadViewFromResources(Stage viewStage, Stage owner, Object viewController, String viewFilePath, String stageTitleKey, String stageTitle)
            throws IOException {

        // Creo el loader para cargar el XML de la vista
        FXMLLoader loader = new FXMLLoader(viewController.getClass().getResource(viewFilePath));

        // Sobrescribir la definición del controlador hecha en el archivo FXML
        loader.setController(viewController);

        // Referencia al contexto de la aplicación
        ApplicationContext context = ApplicationContext.instance();

        // Le paso al Loader el fichero de idioma para cargar la vista según el Locale actual del sistema
        ResourceBundle rb = context.getResourceBundle();
        loader.setResources(rb);

        // Cargo la vista y obtengo la raíz
        Parent root = loader.load();

        // Si el stage ya tiene un scene utilizo ese mismo
        if (viewStage.getScene() != null) {
            viewStage.getScene().setRoot(root);
        } else {
            // Sino, creo la escena
            Scene scene = new Scene(root);
            viewStage.setScene(scene);
        }

        if (!viewStage.isShowing()) {
            if (owner != null) {
                viewStage.initOwner(owner);
            }
            viewStage.centerOnScreen();
        }

        // Injectar las dependencias al controllador
        injectControllerDependencies(viewController, viewStage, viewStage.getScene(), rb, loader.getLocation());

        // Poner el título de la ventana
        StageUtil.setStageTitle(viewStage, stageTitleKey, stageTitle);

        // Procesar las anotaciones presentes en el controllador (ej. @PostInitialize)
        processControllerAnnotations(viewController);
    }

    /**
     * Inyecta las dependencias anotadas con anotaciones de Ainoha Framework.
     *
     * @param viewController Instancia del controlador de la vista
     * @param stage Stage sobre el que se cargó la vista
     * @param scene Scene de la vista
     * @param rb Recursos de idioma actuales
     * @param viewURL URL del archivo FXML de la vista
     */
    private static void injectControllerDependencies(Object viewController, Stage stage, Scene scene, ResourceBundle rb, URL viewURL) {
        // Inyectar campos anotados
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
     * Procesa las anotaciones de Ainoha Framework que no están relacionadas con la inyeción de dependencias.
     *
     * @param viewController Instancia del controlador de la vista
     */
    private static void processControllerAnnotations(Object viewController) {
        List<AccessibleObject> members = new ArrayList<>();

        Collections.addAll(members, viewController.getClass().getDeclaredFields());
        Collections.addAll(members, viewController.getClass().getDeclaredMethods());

        for (var accessibleObject : members) {
            AnnotationProcessorHub.registeredProcessorClasses()
                    .stream()
                    .filter(accessibleObject::isAnnotationPresent)
                    .forEach(annotationClass -> AnnotationProcessorHub
                            .forAnnotationClass(annotationClass)
                            .process(accessibleObject, viewController)
                    );
        }
    }

    /**
     * Procesa las anotaciones de Ainoha Framework que no están relacionadas con la inyeción de dependencias para los
     * campos anotados con {@code @}{@link FXML}.
     *
     * @param viewController Instancia del controlador de la vista
     */
    private static void processControllerAnnotationsForFxmlAnnotatedFields(Object viewController) {
        Field[] fields = viewController.getClass().getDeclaredFields();

        for (var field : fields) {
            AnnotationProcessorHub.registeredProcessorClasses()
                    .stream()
                    .filter(registeredProcessorClass -> field.getAnnotation(FXML.class) != null && field.isAnnotationPresent(registeredProcessorClass))
                    .forEach(annotationClass -> AnnotationProcessorHub
                            .forAnnotationClass(annotationClass)
                            .process(field, viewController)
                    );
        }
    }
}

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

import com.ainoha.core.annotation.FxmlController;
import com.ainoha.core.annotation.ViewStage;
import com.ainoha.core.exception.FxmlControllerDependenciesException;
import com.ainoha.internal.FxmlViewHelper;
import com.ainoha.internal.utils.ReflectionUtil;
import com.ainoha.internal.utils.StageUtil;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.util.logging.Logger;

/**
 * Adds methods to the class that allow to reload the current view within it same {@link Stage}.<br>
 * <br>
 * In order to the methods of this interface work, target class must be annotated with {@code @}{@link FxmlController}
 * and must have a field annotated with {@code @}{@link ViewStage}. If any of these requirements are not met the methods
 * will not execute its process.<br>
 * <br>
 * Methods of this interface are useful if we need to update the texts in the screen after change the application
 * language, for example.
 *
 * @author Eduardo Betanzos
 * @since 1.0
 */
public interface Reloadable {

    Logger LOGGER = Logger.getLogger(Reloadable.class.getName());

    /**
     * Reloads the user interface handle by the controller class which implements this interface. This implies to
     * process all annotated controller fields.
     */
    default void reloadUI() {
        reload(false);
    }

    /**
     * Reloads the fully user interface handle by the controller class which implements this interface. "Fully" means
     * that all controller members (fields and methods) will be processed.
     */
    default void reloadFullyUI() {
        reload(true);
    }

    private void reload(boolean fully) {
        ReflectionUtil.getDeclaredAnnotation(this.getClass(), FxmlController.class)
                .ifPresentOrElse(
                        a -> reloadViewInStage(a, fully),
                        () -> getLogger().fine("Class " + this.getClass().getName() + " is not annotated with @"
                                + FxmlController.class.getName())
                );
    }

    private void reloadViewInStage(FxmlController fxmlControllerAnnotation, boolean fully) {
        try {
            // Obtener la referencia al Stage de la vista
            ReflectionUtil.<Stage>getFieldValueFromController(this, ViewStage.class)
                    .ifPresentOrElse(s -> {
                                // this es el controlador de la vista
                                Parent root = FxmlViewHelper.loadFxmlViewAsParent(this, fully);

                                // Si el título actual lo obtuvo del parámetro @FxmlController.title, no es
                                // necesario cambiarlo, por eso el último null
                                StageUtil.setStageTitle(
                                        ApplicationContext.instance(),
                                        s,
                                        fxmlControllerAnnotation.titleKey(),
                                        null
                                );

                                s.getScene().setRoot(root);
                            }, () -> getLogger().fine("Controller class " + this.getClass().getName()
                                    + " no have a field annotated with @" + ViewStage.class.getName())
                    );
        } catch (IllegalAccessException | ClassCastException e) {
            throw new FxmlControllerDependenciesException("Stage reference could not be obtained due to an error.", e);
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(Reloadable.class.getName());
    }
}

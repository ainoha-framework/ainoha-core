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
 * Agrega a la clase anotada el método {@link Reloadable#reloadUI()} que permite recargar la vista dentro del mismo
 * {@link Stage}.<br>
 * <br>
 * Se requiere que la clase esté anotada con {@code @}{@link FxmlController} y que tenga un campo anotado
 * con {@code @}{@link ViewStage} el cual debe tener una referencia al {@link Stage} de la vista.
 *
 * @author Eduardo Betanzos
 */
public interface Reloadable {

    Logger LOGGER = Logger.getLogger(Reloadable.class.getName());

    /**
     * Recarga la interfaz de usuario vinculada al controlador que implemente esta interfaz. El controllador tiene que
     * estar anotado con {@code @}{@link FxmlController} y tener un campo anotado con {@code @}{@link ViewStage} el cual
     * debe tener una referencia al {@link Stage} de la vista. Si alguna de las condiciones anteriore no se cumple, el
     * método no ejecutará el proceso de recarga así como ninguno de los procesos asociados.<br>
     * <br>
     * Además de recargar la vista, la llamada a este método procesará nuevamente todos los campos anotados del controlador.<br>
     * <br>
     * Este método resulta útil cuando cambia el idioma de la aplicación y se necesita actualizar el idioma de los textos
     * en la pantalla.
     */
    default void reloadUI() {
        reload(false);
    }

    /**
     * Recarga completamente la interfaz de usuario vinculada al controlador que implemente esta interfaz. Con "completamente"
     * se quiere decir que se procesarán todos los miembros anotaciones, tanto campos como métodos.<br>
     * <br>
     * El controllador tiene que estar anotado con {@code @}{@link FxmlController} y tener un campo anotado con
     * {@code @}{@link ViewStage} el cual debe tener una referencia al {@link Stage} de la vista. Si alguna de las
     * condiciones anteriore no se cumple, el método no ejecutará el proceso de recarga así como ninguno de los procesos
     * asociados.<br>
     * <br>
     * Este método resulta útil cuando cambia el idioma de la aplicación y se necesita actualizar el idioma de los textos
     * en la pantalla.
     */
    default void reloadFullyUI() {
        reload(true);
    }

    private void reload(boolean fully) {
        ReflectionUtil.getDeclaredAnnotation(this.getClass(), FxmlController.class)
                .ifPresentOrElse(
                        a -> reloadViewInStage(a, fully),
                        () -> getLogger().fine("La clase " + this.getClass().getName() + " no está anotada con @"
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

                                // Si el título actual lo obtuvo del parámetro @FxmlController.title, no es necesario cambiarlo, por eso el último null
                                StageUtil.setStageTitle(s, fxmlControllerAnnotation.titleKey(), null);

                                s.getScene().setRoot(root);
                            }, () -> getLogger().fine("El controlador " + this.getClass().getName()
                                    + " no tiene un campo anotado con @" + ViewStage.class.getName())
                    );
        } catch (IllegalAccessException | ClassCastException e) {
            throw new FxmlControllerDependenciesException("No se pudo obtener la referencia al Stage producto a un error.", e);
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(Reloadable.class.getName());
    }
}

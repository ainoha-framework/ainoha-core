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

import com.ainoha.internal.FxmlViewHelper;
import javafx.scene.input.KeyCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

/**
 * Esta clase permite simplificar el proceso de visualización de vistas FXML; ya que este requiere que se especifiquen
 * una serie de parámetros que definen el modo en que se debe mostrar la vista.
 * 
 * @author Eduardo Betanzos
 */
public final class ViewLoaderBuilder {

    private Class controllerClass;
    private Stage viewStage;
    private Stage owner;
    private Object params;
    private Modality modality;
    private StageStyle stageStyle;
    private boolean resizable = true;
    private boolean maximized = false;
    private boolean fullScreen;
    private String fullScreenExitHint;
    private KeyCombination fullScreenExitKeyCombination;

    public ViewLoaderBuilder(Class controllerClass) {
        Objects.requireNonNull(controllerClass, "La clase de controlador no puede ser 'null'");
        this.controllerClass = controllerClass;
    }

    /**
     * Define el {@link Stage} donde se mostrará la vista. Si este es {@code null} se creará uno.<br>
     * <br>
     * Valor por defecto: {@code null}
     *
     * @param viewStage {@link Stage}
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder stage(Stage viewStage) {
        this.viewStage = viewStage;
        return this;
    }

    /**
     * Define el {@link Stage} propietario de la vista. Si este es {@code null} la vista no tendrá propietario.<br>
     * <br>
     * Valor por defecto: {@code null}
     *
     * @param owner {@link Stage} propietario
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder owner(Stage owner) {
        this.owner = owner;
        return this;
    }

    /**
     * Permite pasar datos al controlador de la vista que se mostrará.<br>
     * <br>
     * Valor por defecto: {@code null}
     *
     * @param userData Datos a pasar
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder userData(Object userData) {
        this.params = userData;
        return this;
    }

    /**
     * Define la modalidad ({@link Modality}) del {@link Stage} donde se mostrará la vista. Si es {@code null} no tendrá
     * efecto.<br>
     * <br>
     * Valor por defecto: {@code null}
     *
     * @param modality {@link Modality}
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder modality(Modality modality) {
        this.modality = modality;
        return this;
    }

    /**
     * Define el estilo ({@link StageStyle}) del {@link Stage} donde se mostrará la vista. Si es {@code null} no tendrá
     * efecto.<br>
     * <br>
     * Valor por defecto: {@code null}
     *
     * @param stageStyle {@link StageStyle}
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder stageStyle(StageStyle stageStyle) {
        this.stageStyle = stageStyle;
        return this;
    }

    /**
     * Define que el {@link Stage} donde se mostrará la vista no será redimensionable.<br>
     * <br>
     * Valor por defecto: Si no se indica lo contrario el {@link Stage} siempre será redimensionable
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder notResizable() {
        this.resizable = false;
        return this;
    }

    /**
     * Define que el {@link Stage} donde se mostrará la vista se debe mostrar maximizado.<br>
     * <br>
     * Valor por defecto: Si no se indica lo contrario el {@link Stage} no se mostrará maximizado
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder maximized() {
        this.maximized = true;
        return this;
    }

    /**
     * Especifica el texto que se muestra cuando el {@link Stage} se muestra en modo de pantalla completa, generalmente
     * se usa para indicar la forma en que un usuario debe salir del modo de pantalla completa. El valor {code null}
     * dará como resultado que se muestre el mensaje predeterminado sergún el {@link java.util.Locale} de la aplicación.
     * Si se pasa una cadena vacía, no se mostrará ningún mensaje.<br>
     * <br>
     * Valor por defecto: {@code null}
     *
     * @param fullScreenExitHint {@link String}
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder fullScreenExitHint(String fullScreenExitHint) {
        this.fullScreenExitHint = fullScreenExitHint;
        return this;
    }

    /**
     * Especifica la combinación de teclas que permitirá al usuario salir del modo de pantalla completa. Un valor de
     * {@link KeyCombination#NO_MATCH} no coincidirá con ningún {@link javafx.scene.input.KeyEvent} y lo hará para que
     * el usuario no pueda salir del modo de pantalla completa. El valor {@code null} indica que se debe utilizar la
     * combinación de teclas por defecto específica de la plataforma.<br>
     * <br>
     * Valor por defecto: {@code null}
     *
     * @param fullScreenExitKeyCombination {@link KeyCombination}
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder fullScreenExitKeyCombination(KeyCombination fullScreenExitKeyCombination) {
        this.fullScreenExitKeyCombination = fullScreenExitKeyCombination;
        return this;
    }

    /**
     * Muestra la vista utilizando los parámetros definidos antes de llamar este método. Los parámetros para los cuales
     * no se haya definido un valor explícitamente utilizarán su valor por defecto.
     */
    public void show() {
        FxmlViewHelper.showFxmlView(
                controllerClass,
                viewStage,
                owner,
                params,
                modality,
                stageStyle,
                resizable,
                maximized,
                fullScreen,
                fullScreenExitHint,
                fullScreenExitKeyCombination
        );
    }

    /**
     * Muestra la vista sin decoración utilizando los parámetros definidos antes de llamar este método y sobrescribiendo
     * el valor del parámetro {@code stageStyle}. Los parámetros para los cuales no se haya definido un valor explícitamente
     * utilizarán su valor por defecto, excepto el parámetro {@code stageStyle} cuyo valor será {@link StageStyle#UNDECORATED}.
     */
    public void showUndecorated() {
        stageStyle = StageStyle.UNDECORATED;
        show();
    }

    /**
     * Muestra la vista en modo pantalla completa utilizando los parámetros definidos antes de llamar este método. Los
     * parámetros para los cuales no se haya definido un valor explícitamente utilizarán su valor por defecto.
     */
    public void showFullScreen() {
        fullScreen = true;
        show();
    }

    /**
     * Regresa al builder a su estado inicial definiendo el valor por defecto de todos los parámetros.
     */
    public void reset() {
        viewStage = null;
        owner = null;
        params = null;
        modality = null;
        stageStyle = null;
        resizable = true;
        fullScreen = false;
        fullScreenExitHint = null;
        fullScreenExitKeyCombination = null;
    }
}

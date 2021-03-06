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
 * This builder simplify the process for showing applications views, since this process may require combining many
 * parameters.
 *
 * @author Eduardo Betanzos
 * @since 1.0
 */
public final class ViewLoaderBuilder<T> {

    private final Class<T> controllerClass;
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

    public ViewLoaderBuilder(Class<T> controllerClass) {
        Objects.requireNonNull(controllerClass, "'controllerClass' cannot be null");
        this.controllerClass = controllerClass;
    }

    /**
     * Defines the view {@link Stage}. If this {@link Stage} is {@code null} one will be created.<br>
     * <br>
     * Default: {@code null}
     *
     * @param viewStage {@link Stage}
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder<T> stage(Stage viewStage) {
        this.viewStage = viewStage;
        return this;
    }

    /**
     * Defines the view owner {@link Stage}. If this owner {@link Stage} is {@code null} the view will not have owner.<br>
     * <br>
     * Default: {@code null}
     *
     * @param owner {@link Stage} owner
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder<T> owner(Stage owner) {
        this.owner = owner;
        return this;
    }

    /**
     * Allows to pass data to the view.<br>
     * <br>
     * Default: {@code null}
     *
     * @param params Data to pass
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder<T> viewParams(Object params) {
        this.params = params;
        return this;
    }

    /**
     * Defines the {@link Stage} {@link Modality}. If {@code null} will be ignored.<br>
     * <br>
     * Default: {@code null}
     *
     * @param modality {@link Modality}
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder<T> modality(Modality modality) {
        this.modality = modality;
        return this;
    }

    /**
     * Defines the {@link StageStyle}. If {@code null} will be ignored.<br>
     * <br>
     * Default: {@code null}
     *
     * @param stageStyle {@link StageStyle}
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder<T> stageStyle(StageStyle stageStyle) {
        this.stageStyle = stageStyle;
        return this;
    }

    /**
     * Defines that the {@link Stage} where the view will be displayed will not allow resizing.<br>
     * <br>
     * Default: {@link Stage} allow resizing
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder<T> notResizable() {
        this.resizable = false;
        return this;
    }

    /**
     * Defines that the {@link Stage} where the view will be displayed should be maximized.<br>
     * <br>
     * Default: {@link Stage} will not be maximized
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder<T> maximized() {
        this.maximized = true;
        return this;
    }

    /**
     * Defines the exit hint text for fullscreen mode.<br>
     * <br>
     * If an empty {@link String} is defined the exit hint text will be empty too. If {@code null} the default value
     * (defined by JavaFX) will be used, according the current {@link java.util.Locale}.<br>
     * <br>
     * Default: {@code null}
     *
     * @param fullScreenExitHint {@link String}
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder<T> fullScreenExitHint(String fullScreenExitHint) {
        this.fullScreenExitHint = fullScreenExitHint;
        return this;
    }

    /**
     * Defines the exit {@link KeyCombination} for fullscreen mode.<br>
     * <br>
     * The value {@link KeyCombination#NO_MATCH} will prevent the fullscreen mode from being exited. If {@code null}
     * the default value (defined by JavaFX) will be used.<br>
     * <br>
     * Default: {@code null}
     *
     * @param fullScreenExitKeyCombination {@link KeyCombination} for exit fullscreen mode
     *
     * @return {@code this}
     */
    public ViewLoaderBuilder<T> fullScreenExitKeyCombination(KeyCombination fullScreenExitKeyCombination) {
        this.fullScreenExitKeyCombination = fullScreenExitKeyCombination;
        return this;
    }

    /**
     * Same behavior that call {@code show(false)}.
     *
     * @return Controller instance of the displayed view
     *
     * @see #show(boolean)
     */
    public T show() {
        return show(false);
    }

    /**
     * Shows the view using parameters defined before call this method. If no explicit values are defined for the view
     * parameters, default values will be used.
     *
     * @param waitFor If {@code true} shows this stage and waits for it to be hidden (closed) before returning
     *                to the caller (see {@link Stage#showAndWait()} for details).
     *
     * @return Controller instance of the displayed view
     */
    public T show(boolean waitFor) {
        return FxmlViewHelper.showFxmlView(
                controllerClass,
                waitFor,
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
     * Same behavior that call {@code showUndecorated(false)}.
     *
     * @return Controller instance of the displayed view
     *
     * @see #showUndecorated(boolean)
     *
     */
    public T showUndecorated() {
        return showUndecorated(false);
    }

    /**
     * Shows the view undecorated by defining {@link StageStyle#UNDECORATED} as value for the {@code stageStyle}
     * parameter.
     *
     * @param waitFor If {@code true} shows this stage and waits for it to be hidden (closed) before returning
     *                to the caller (see {@link Stage#showAndWait()} for details).
     *
     * @return Controller instance of the displayed view
     *
     * @see ViewLoaderBuilder#show()
     */
    public T showUndecorated(boolean waitFor) {
        stageStyle = StageStyle.UNDECORATED;
        return show(waitFor);
    }

    /**
     * Same behavior that call {@code showFullScreen(false)}.
     *
     * @return Controller instance of the displayed view
     *
     * @see #showFullScreen(boolean)
     *
     */
    public T showFullScreen() {
        return showFullScreen(false);
    }

    /**
     * Shows the view in fullscreen mode.
     *
     * @param waitFor If {@code true} shows this stage and waits for it to be hidden (closed) before returning
     *                to the caller (see {@link Stage#showAndWait()} for details).
     *
     * @return Controller instance of the displayed view
     *
     * @see ViewLoaderBuilder#show()
     */
    public T showFullScreen(boolean waitFor) {
        fullScreen = true;
        return show(waitFor);
    }

    /**
     * Reset the builder internal state by setting up the default values for all parameters.
     */
    public void reset() {
        viewStage = null;
        owner = null;
        params = null;
        modality = null;
        stageStyle = null;
        resizable = true;
        maximized = false;
        fullScreen = false;
        fullScreenExitHint = null;
        fullScreenExitKeyCombination = null;
    }
}

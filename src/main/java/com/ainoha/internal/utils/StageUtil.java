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
package com.ainoha.internal.utils;

import com.ainoha.core.ApplicationContext;
import javafx.stage.Stage;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Common utilities for working with {@link Stage}s
 *
 * @author Eduardo Betanzos
 * @since 1.0
 */
public final class StageUtil {

    private static final Logger LOGGER = Logger.getLogger(StageUtil.class.getName());

    private StageUtil() {}

    /**
     * Allows to set the title of the window associated to {@code viewStage}
     *
     * @param context       The application context
     * @param viewStage     Target window
     * @param stageTitleKey Title key from language resources. If {@code null} or {@link String#isBlank()} returns
     *                      {@code true} will be ignored
     * @param stageTitle    Window title. Override {@code stageTitleKey} if is not {@code null} and
     *                      {@link String#isBlank()} returns {@code false}
     */
    public static void setStageTitle(ApplicationContext context, Stage viewStage, String stageTitleKey, String stageTitle) {
        if (stageTitle != null && !stageTitle.isBlank()) {
            viewStage.setTitle(stageTitle);
        } else if (stageTitleKey != null && !stageTitleKey.isBlank()) {
            String stageTitleValue = "";
            try {
                ResourceBundle rb = context.getResourceBundle();

                String defaultStageTitleValue = context.getDefaultResourceBundle() != null
                        ? context.getDefaultResourceBundle().getString(stageTitleKey)
                        : "";
                stageTitleValue = rb != null ? rb.getString(stageTitleKey) : defaultStageTitleValue;
            } catch (MissingResourceException e) {
                LOGGER.severe("Window title language key '" + stageTitleKey + "' was not found.");
            } catch (RuntimeException e) {
                LOGGER.severe("An error occurs trying to get the value of the window title language key '"
                                      + stageTitleKey + "'");
                LOGGER.log(Level.FINE, "", e);
            }
            viewStage.setTitle(stageTitleValue);
        }
    }
}

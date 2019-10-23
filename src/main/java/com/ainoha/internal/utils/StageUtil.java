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
 * Clase para agrupar utilidades comunes al trabajo con {@link Stage}s.
 *
 * @author Eduardo Betanzos
 */
public final class StageUtil {

    private static final Logger LOGGER = Logger.getLogger(StageUtil.class.getName());

    private StageUtil() {}

    /**
     * Permite ponerle título a la ventana asociada al Stage {@code viewStage}.
     *
     * @param viewStage Ventana a la que se le pondrá el título
     * @param stageTitleKey Clave del título de la ventana dentro de los recursos de idioma. Si es {@code null} o
     *                      {@link String#isBlank()} devuelve {@code true}, será desechado y portanto no procesado
     * @param stageTitle Título de la ventana. Sobrescribe a {@code stageTitleKey} si su valor es diferente de {@code null}
     *                   y {@link String#isBlank()} retorna {@code false}
     */
    public static void setStageTitle(Stage viewStage, String stageTitleKey, String stageTitle) {
        if (stageTitle != null && !stageTitle.isBlank()) {
            viewStage.setTitle(stageTitle);
        } else if (stageTitleKey != null && !stageTitleKey.isBlank()) {
            String stageTitleValue = "";
            try {
                ApplicationContext context = ApplicationContext.instance();
                ResourceBundle rb = context.getResourceBundle();

                stageTitleValue = context.getDefaultResourceBundle() != null ? context.getDefaultResourceBundle().getString(stageTitleKey) : "";
                stageTitleValue = rb != null ? rb.getString(stageTitleKey) : stageTitleValue;
            } catch (MissingResourceException e) {
                LOGGER.severe("No se encontró la clave '" + stageTitleKey + "', correspondiente al " +
                        "título de la vista, en el archivo de idioma.");
            } catch (RuntimeException e) {
                LOGGER.severe("Ocurrió un error intentando obtener el valor de la clave '" + stageTitleKey
                        + "', correspondiente al título de la vista, en el archivo de idioma.");
                LOGGER.log(Level.FINE, "", e);
            }
            viewStage.setTitle(stageTitleValue);
        }
    }
}

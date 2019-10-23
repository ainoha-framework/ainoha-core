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

import com.ainoha.core.annotation.FxmlController;
import com.ainoha.core.exception.FxmlControllerDependenciesException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Optional;

/**
 * @author Eduardo Betanzos
 */
public final class ReflectionUtil {

    private ReflectionUtil() {}

    public static void invokeStaticMethod(final Class clazz, final String methodName, final Class[] parameterTypes, final Object... args)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method lunchMethod = clazz.getMethod(methodName, parameterTypes);
        lunchMethod.invoke(null, args);
    }

    public static <T extends Annotation> Optional<T> getDeclaredAnnotation(final AnnotatedElement element, final Class<T> annotation) {
        return Optional.ofNullable(element.getDeclaredAnnotation(annotation));
    }

    public static boolean isAnnotatedWith(final AnnotatedElement element, final Class<? extends Annotation> annotation) {
        return getDeclaredAnnotation(element, annotation).isPresent();
    }

    public static void setValueInAnnotatedFields(Object object, Class<? extends Annotation> annotationClass, Object value) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (isAnnotatedWith(field, annotationClass)) {
                if (!field.getType().isAssignableFrom(value.getClass())) {
                    throw new FxmlControllerDependenciesException("No se puede inyectar el valor. Tipo requerido: "
                            + value.getClass().getName() + ". Tipo encontrado: " + field.getType().getName());
                }

                field.setAccessible(true);
                try {
                    field.set(object, value);
                } catch (IllegalAccessException e) {
                    throw new FxmlControllerDependenciesException("No se pudo inyectar la referencia.", e);
                }
            }
        }
    }

    public static Object newInstanceOf(Class clazz)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Constructor constructor = clazz.getConstructor();
        return constructor.newInstance();
    }

    public static <T> Optional<T> getFieldValueFromController(Object controller, Class<? extends Annotation> annotation) throws IllegalAccessException {
        Class controllerClass = controller.getClass();

        if (isAnnotatedWith(controllerClass, FxmlController.class)) {
            Field[] fields = controllerClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.getDeclaredAnnotation(annotation) != null) {
                    field.setAccessible(true);
                    return Optional.ofNullable((T) field.get(controller));
                }
            }
        }

        return Optional.empty();
    }
}

package test.utils;

import com.ainoha.core.ApplicationContext;

import static org.mockito.Mockito.mock;

public final class ApplicationContextUtil {
    private ApplicationContextUtil() { }

    public static ApplicationContext getCurrentApplicationContext() throws Exception {
        var field = ApplicationContext.class.getDeclaredField("context");
        field.setAccessible(true);
        return (ApplicationContext) field.get(null);
    }

    public static void mockApplicationContext() throws Exception {
        var mockAppContext = mock(ApplicationContext.class);
        var field = ApplicationContext.class.getDeclaredField("context");
        field.setAccessible(true);
        field.set(null, mockAppContext);
    }

    public static void setApplicationContext(ApplicationContext context) throws Exception {
        var field = ApplicationContext.class.getDeclaredField("context");
        field.setAccessible(true);
        field.set(null, context);
    }
}

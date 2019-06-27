package com.company.demo.web.dataloadcoordinator;

import com.haulmont.cuba.gui.model.DataLoader;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.sys.UiControllerReflectionInspector;

import java.lang.invoke.MethodHandle;
import java.util.function.Consumer;

public class OnScreenEventLoadTrigger implements LoadTrigger {

    private final DataLoader loader;

    public OnScreenEventLoadTrigger(Screen screen, UiControllerReflectionInspector reflectionInspector, DataLoader loader, Class eventClass) {
        this.loader = loader;
        MethodHandle addListenerMethod = reflectionInspector.getAddListenerMethod(screen.getClass(), eventClass);
        if (addListenerMethod == null) {
            throw new IllegalStateException("Cannot find addListener method for " + eventClass);
        }
        try {
            addListenerMethod.invoke(screen, (Consumer) event -> load());
        } catch (Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException("Unable to add listener for " + eventClass, e);
        }
    }

    private void load() {
        loader.load();
    }

    @Override
    public DataLoader getLoader() {
        return loader;
    }
}

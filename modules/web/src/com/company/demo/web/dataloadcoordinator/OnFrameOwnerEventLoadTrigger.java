package com.company.demo.web.dataloadcoordinator;

import com.haulmont.cuba.gui.model.DataLoader;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.ScreenFragment;
import com.haulmont.cuba.gui.sys.UiControllerReflectionInspector;

import java.lang.invoke.MethodHandle;
import java.util.function.Consumer;

public class OnFrameOwnerEventLoadTrigger implements LoadTrigger {

    private final DataLoader loader;

    public OnFrameOwnerEventLoadTrigger(FrameOwner frameOwner, UiControllerReflectionInspector reflectionInspector,
                                        DataLoader loader, Class eventClass) {
        this.loader = loader;
        MethodHandle addListenerMethod = reflectionInspector.getAddListenerMethod(frameOwner.getClass(), eventClass);
        if (addListenerMethod == null) {
            throw new IllegalStateException("Cannot find addListener method for " + eventClass);
        }
        try {
            addListenerMethod.invoke(frameOwner, (Consumer) event -> load());
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

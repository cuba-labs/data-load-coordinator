package com.company.demo.web.dataloadcoordinator;

import com.haulmont.cuba.core.global.DevelopmentException;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.model.DataLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;

public class OnComponentValueChangedLoadTrigger implements LoadTrigger {

    private final DataLoader loader;
    private HasValue component;
    private final String param;

    public OnComponentValueChangedLoadTrigger(DataLoader loader, Component component, String param) {
        if (!(component instanceof HasValue)) {
            throw new DevelopmentException(String.format(
                    "Invalid component type in onComponentValueChanged load trigger: %s. Expected HasValue", component.getClass().getName()));
        }
        this.loader = loader;
        this.component = (HasValue) component;
        this.param = param;
        this.component.addValueChangeListener(event -> load());
    }

    private void load() {
        loader.setParameter(param, component.getValue());
        loader.load();
    }

    @Override
    public DataLoader getLoader() {
        return loader;
    }
}

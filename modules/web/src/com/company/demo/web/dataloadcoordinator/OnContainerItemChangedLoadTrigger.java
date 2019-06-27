package com.company.demo.web.dataloadcoordinator;

import com.haulmont.cuba.gui.model.DataLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;

public class OnContainerItemChangedLoadTrigger implements LoadTrigger{

    private final DataLoader loader;
    private final InstanceContainer container;
    private final String param;

    public OnContainerItemChangedLoadTrigger(DataLoader loader, InstanceContainer container, String param) {
        this.loader = loader;
        this.container = container;
        this.param = param;
        container.addItemChangeListener(event -> load());
    }

    private void load() {
        loader.setParameter(param, container.getItemOrNull());
        loader.load();
    }

    @Override
    public DataLoader getLoader() {
        return loader;
    }
}

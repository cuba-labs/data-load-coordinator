package com.company.demo.web.dataloadcoordinator;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Facet;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.model.DataLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;

import javax.annotation.Nullable;

public interface DataLoadCoordinator extends Facet {

    void addOnScreenEventLoadTrigger(DataLoader loaderId, Class eventClass);

    void addOnContainerItemChangedLoadTrigger(DataLoader loader, InstanceContainer container, @Nullable String param);

    void addOnComponentValueChangedLoadTrigger(DataLoader loader, Component component, @Nullable String param);

    void configureAutomatically();
}

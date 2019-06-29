package com.company.demo.web.dataloadcoordinator;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Facet;
import com.haulmont.cuba.gui.model.DataLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;

import javax.annotation.Nullable;
import java.util.List;

public interface DataLoadCoordinator extends Facet {

    String CONTAINER_PREFIX = "container_";
    String COMPONENT_PREFIX = "component_";

    void setContainerPrefix(String value);

    void setComponentPrefix(String value);

    void addOnFrameOwnerEventLoadTrigger(DataLoader loaderId, Class eventClass);

    void addOnContainerItemChangedLoadTrigger(DataLoader loader, InstanceContainer container, @Nullable String param);

    void addOnComponentValueChangedLoadTrigger(DataLoader loader, Component component, @Nullable String param, LikeClause likeClause);

    void configureAutomatically();

    List<LoadTrigger> getLoadTriggers();
}

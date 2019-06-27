package com.company.demo.web.dataloadcoordinator;

import com.haulmont.cuba.gui.GuiDevelopmentException;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.model.DataLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.sys.UiControllerReflectionInspector;
import com.haulmont.cuba.gui.xml.FacetProvider;
import com.haulmont.cuba.gui.xml.layout.ComponentLoader;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;

@Component("cuba_DataLoadCoordinatorFacetProvider")
public class DataLoadCoordinatorFacetProvider implements FacetProvider<DataLoadCoordinator> {

    private UiControllerReflectionInspector reflectionInspector;

    @Inject
    public void setReflectionInspector(UiControllerReflectionInspector reflectionInspector) {
        this.reflectionInspector = reflectionInspector;
    }

    @Override
    public Class<DataLoadCoordinator> getFacetClass() {
        return DataLoadCoordinator.class;
    }

    @Override
    public DataLoadCoordinator create() {
        return new WebDataLoadCoordinator(reflectionInspector);
    }

    @Override
    public String getFacetTag() {
        return "dataLoadCoordinator";
    }

    @Override
    public void loadFromXml(DataLoadCoordinator facet, Element element, ComponentLoader.ComponentContext context) {
        facet.setOwner(context.getFrame());
        for (Element loaderEl : element.elements("loader")) {
            String loaderId = loaderEl.attributeValue("ref");
            if (loaderId == null) {
                throw new GuiDevelopmentException("'dataLoadCoordinator.loader' element has no 'ref' attribute", context);
            }

            String onScreenEvent = loaderEl.attributeValue("onScreenEvent");
            if (onScreenEvent != null) {
                Class eventClass;
                switch (onScreenEvent) {
                    case "Init":
                        eventClass = Screen.InitEvent.class;
                        break;
                    case "AfterInit":
                        eventClass = Screen.AfterInitEvent.class;
                        break;
                    case "BeforeShow":
                        eventClass = Screen.BeforeShowEvent.class;
                        break;
                    case "AfterShow":
                        eventClass = Screen.AfterShowEvent.class;
                        break;
                    default:
                        throw new GuiDevelopmentException("Unsupported 'dataLoadCoordinator.loader.onScreenEvent' value: " + onScreenEvent, context);
                }
                context.addInjectTask(new OnScreenEventLoadTriggerInitTask(facet, loaderId, eventClass));
                continue;
            }

            String onContainerItemChanged = loaderEl.attributeValue("onContainerItemChanged");
            if (onContainerItemChanged != null) {
                String param = loaderEl.attributeValue("param");
                context.addInjectTask(new OnContainerItemChangedLoadTriggerInitTask(facet, loaderId, onContainerItemChanged, param));
                continue;
            }

            String onComponentValueChanged = loaderEl.attributeValue("onComponentValueChanged");
            if (onComponentValueChanged != null) {
                String param = loaderEl.attributeValue("param");
                context.addInjectTask(new OnComponentValueChangedLoadTriggerInitTask(facet, loaderId, onComponentValueChanged, param));
                continue;
            }
        }

        if (Boolean.parseBoolean(element.attributeValue("auto"))) {
            context.addPostInitTask(new AutoConfigurationInitTask(facet));
        }
    }

    public static class OnScreenEventLoadTriggerInitTask implements ComponentLoader.InjectTask {

        private final DataLoadCoordinator facet;
        private final String loaderId;
        private final Class eventClass;

        public OnScreenEventLoadTriggerInitTask(DataLoadCoordinator facet, String loaderId, Class eventClass) {
            this.facet = facet;
            this.loaderId = loaderId;
            this.eventClass = eventClass;
        }

        @Override
        public void execute(ComponentLoader.ComponentContext context, Frame window) {
            DataLoader loader = context.getScreenData().getLoader(loaderId);
            facet.addOnScreenEventLoadTrigger(loader, eventClass);
        }
    }

    public static class OnContainerItemChangedLoadTriggerInitTask implements ComponentLoader.InjectTask {

        private final DataLoadCoordinator facet;
        private final String loaderId;
        private final String containerId;
        @Nullable
        private final String param;

        public OnContainerItemChangedLoadTriggerInitTask(
                DataLoadCoordinator facet, String loaderId, String containerId, @Nullable String param) {
            this.facet = facet;
            this.loaderId = loaderId;
            this.containerId = containerId;
            this.param = param;
        }

        @Override
        public void execute(ComponentLoader.ComponentContext context, Frame window) {
            DataLoader loader = context.getScreenData().getLoader(loaderId);
            InstanceContainer container = context.getScreenData().getContainer(containerId);
            facet.addOnContainerItemChangedLoadTrigger(loader, container, param);
        }
    }

    public static class OnComponentValueChangedLoadTriggerInitTask implements ComponentLoader.InjectTask {

        private final DataLoadCoordinator facet;
        private final String loaderId;
        private final String componentId;
        private final String param;

        public OnComponentValueChangedLoadTriggerInitTask(
                DataLoadCoordinator facet, String loaderId, String componentId, @Nullable String param) {
            this.facet = facet;
            this.loaderId = loaderId;
            this.componentId = componentId;
            this.param = param;
        }

        @Override
        public void execute(ComponentLoader.ComponentContext context, Frame window) {
            DataLoader loader = context.getScreenData().getLoader(loaderId);

            com.haulmont.cuba.gui.components.Component component = context.getFrame().getComponentNN(componentId);
            facet.addOnComponentValueChangedLoadTrigger(loader, component, param);
        }
    }

    public static class AutoConfigurationInitTask implements ComponentLoader.PostInitTask {

        private final DataLoadCoordinator facet;

        public AutoConfigurationInitTask(DataLoadCoordinator facet) {
            this.facet = facet;
        }

        @Override
        public void execute(ComponentLoader.ComponentContext context, Frame window) {
            facet.configureAutomatically();
        }
    }
}

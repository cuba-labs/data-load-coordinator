package com.company.demo.web.dataloadcoordinator;

import com.google.common.base.Strings;
import com.haulmont.cuba.core.global.DevelopmentException;
import com.haulmont.cuba.core.global.queryconditions.Condition;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.model.DataLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.ScreenData;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.UiControllerUtils;
import com.haulmont.cuba.gui.sys.UiControllerReflectionInspector;
import com.haulmont.cuba.web.gui.WebAbstractFacet;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class WebDataLoadCoordinator extends WebAbstractFacet implements DataLoadCoordinator {

    public static final String CONTAINER_PREFIX = "container$";
    public static final String COMPONENT_PREFIX = "component$";
    private List<LoadTrigger> loadTriggers = new ArrayList<>();

    private UiControllerReflectionInspector reflectionInspector;

    private static final Pattern PARAM_PATTERN = Pattern.compile(":([\\w$]+)");

    public WebDataLoadCoordinator(UiControllerReflectionInspector reflectionInspector) {
        this.reflectionInspector = reflectionInspector;
    }

    @Override
    public void addOnScreenEventLoadTrigger(DataLoader loader, Class eventClass) {
        OnScreenEventLoadTrigger loadTrigger = new OnScreenEventLoadTrigger(getScreen(), reflectionInspector, loader, eventClass);
        loadTriggers.add(loadTrigger);
    }

    @Override
    public void addOnContainerItemChangedLoadTrigger(DataLoader loader, InstanceContainer container, @Nullable String param) {
        String nonNullParam = param != null ? param : findSingleParam(loader);
        OnContainerItemChangedLoadTrigger loadTrigger = new OnContainerItemChangedLoadTrigger(loader, container, nonNullParam);
        loadTriggers.add(loadTrigger);
    }

    @Override
    public void addOnComponentValueChangedLoadTrigger(DataLoader loader, Component component, @Nullable String param) {
        String nonNullParam = param != null ? param : findSingleParam(loader);
        OnComponentValueChangedLoadTrigger loadTrigger = new OnComponentValueChangedLoadTrigger(loader, component, nonNullParam);
        loadTriggers.add(loadTrigger);
    }

    @Override
    public void configureAutomatically() {
        Screen screen = getScreen();
        ScreenData screenData = UiControllerUtils.getScreenData(screen);

        getUnconfiguredLoaders(screenData).forEach(loader -> configureAutomatically(loader, screen));
    }

    private Stream<DataLoader> getUnconfiguredLoaders(ScreenData screenData) {
        return screenData.getLoaderIds().stream()
                .map(screenData::<DataLoader>getLoader)
                .distinct()
                .filter(loader -> loadTriggers.stream()
                        .map(LoadTrigger::getLoader).noneMatch(configuredLoader -> configuredLoader == loader));
    }

    private void configureAutomatically(DataLoader loader, Screen screen) {
        List<String> queryParameters = getQueryParameters(loader);
        List<String> allParameters = new ArrayList<>(queryParameters);
        allParameters.addAll(getConditionParameters(loader));

        // add triggers on container/component events
        for (String parameter : allParameters) {
            if (parameter.startsWith(CONTAINER_PREFIX)) {
                InstanceContainer container = UiControllerUtils.getScreenData(screen).getContainer(
                        parameter.substring(CONTAINER_PREFIX.length()));
                OnContainerItemChangedLoadTrigger loadTrigger = new OnContainerItemChangedLoadTrigger(
                        loader, container, parameter);
                loadTriggers.add(loadTrigger);

            } else if (parameter.startsWith(COMPONENT_PREFIX)) {
                String componentId = parameter.substring(COMPONENT_PREFIX.length());
                Component component = screen.getWindow().getComponentNN(componentId);
                OnComponentValueChangedLoadTrigger loadTrigger = new OnComponentValueChangedLoadTrigger(
                        loader, component, parameter);
                loadTriggers.add(loadTrigger);
            }
        }
        // if the loader has no parameters in query, add trigger on BeforeShowEvent
        if (queryParameters.isEmpty()) {
            OnScreenEventLoadTrigger loadTrigger = new OnScreenEventLoadTrigger(
                    screen, reflectionInspector, loader, Screen.BeforeShowEvent.class);
            loadTriggers.add(loadTrigger);
        }
    }

    private List<String> getQueryParameters(DataLoader loader) {
        List<String> parameters = new ArrayList<>();
        if (!Strings.isNullOrEmpty(loader.getQuery())) {
            Matcher matcher = PARAM_PATTERN.matcher(loader.getQuery());
            while (matcher.find()) {
                parameters.add(matcher.group(1));
            }
        }
        return parameters;
    }

    private List<String> getConditionParameters(DataLoader loader) {
        List<String> parameters = new ArrayList<>();
        Condition condition = loader.getCondition();
        if (condition != null) {
            parameters.addAll(condition.getParameters());
        }
        return parameters;
    }

    private String findSingleParam(DataLoader loader) {
        List<String> parameters = getQueryParameters(loader);
        parameters.addAll(getConditionParameters(loader));
        if (parameters.isEmpty()) {
            throw new DevelopmentException("Cannot find a query parameter for onContainerItemChanged load trigger." +
                    "\nQuery: " + loader.getQuery());
        }
        if (parameters.size() > 1) {
            throw new DevelopmentException("There is more than one query parameter for onContainerItemChanged load trigger. " +
                    "Specify the parameter name in the 'param' attribute.\nQuery: " + loader.getQuery());
        }
        return parameters.get(0);
    }

    private Screen getScreen() {
        Frame frame = getOwner();
        if (frame == null) {
            throw new IllegalStateException("Owner window is null");
        }
        FrameOwner frameOwner = frame.getFrameOwner();
        if (!(frameOwner instanceof Screen)) {
            throw new UnsupportedOperationException("WebDataLoadCoordinator can work only in Screen");
        }
        return (Screen) frameOwner;
    }
}

package com.company.demo.web.dataloadcoordinator;

import com.google.common.base.Strings;
import com.haulmont.cuba.core.global.DevelopmentException;
import com.haulmont.cuba.core.global.queryconditions.Condition;
import com.haulmont.cuba.core.global.queryconditions.JpqlCondition;
import com.haulmont.cuba.core.global.queryconditions.LogicalCondition;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.model.DataLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.ScreenData;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.ScreenFragment;
import com.haulmont.cuba.gui.screen.UiControllerUtils;
import com.haulmont.cuba.gui.sys.UiControllerReflectionInspector;
import com.haulmont.cuba.web.gui.WebAbstractFacet;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class WebDataLoadCoordinator extends WebAbstractFacet implements DataLoadCoordinator {

    private String containerPrefix = CONTAINER_PREFIX;
    private String componentPrefix = COMPONENT_PREFIX;

    private List<LoadTrigger> loadTriggers = new ArrayList<>();

    private UiControllerReflectionInspector reflectionInspector;

    private static final Pattern PARAM_PATTERN = Pattern.compile(":([\\w$]+)");
    private static final Pattern LIKE_PATTERN = Pattern.compile("\\s+like\\s+:([\\w$]+)");

    public WebDataLoadCoordinator(UiControllerReflectionInspector reflectionInspector) {
        this.reflectionInspector = reflectionInspector;
    }

    @Override
    public void setContainerPrefix(String value) {
        containerPrefix = value;
    }

    @Override
    public void setComponentPrefix(String value) {
        componentPrefix = value;
    }

    @Override
    public List<LoadTrigger> getLoadTriggers() {
        return Collections.unmodifiableList(loadTriggers);
    }

    @Override
    public void addOnFrameOwnerEventLoadTrigger(DataLoader loader, Class eventClass) {
        OnFrameOwnerEventLoadTrigger loadTrigger = new OnFrameOwnerEventLoadTrigger(getFrameOwner(), reflectionInspector, loader, eventClass);
        loadTriggers.add(loadTrigger);
    }

    @Override
    public void addOnContainerItemChangedLoadTrigger(DataLoader loader, InstanceContainer container, @Nullable String param) {
        String nonNullParam = param != null ? param : findSingleParam(loader);
        OnContainerItemChangedLoadTrigger loadTrigger = new OnContainerItemChangedLoadTrigger(loader, container, nonNullParam);
        loadTriggers.add(loadTrigger);
    }

    @Override
    public void addOnComponentValueChangedLoadTrigger(DataLoader loader, Component component, @Nullable String param,
                                                      LikeClause likeClause) {
        String nonNullParam = param != null ? param : findSingleParam(loader);
        OnComponentValueChangedLoadTrigger loadTrigger = new OnComponentValueChangedLoadTrigger(
                loader, component, nonNullParam, likeClause);
        loadTriggers.add(loadTrigger);
    }

    @Override
    public void configureAutomatically() {
        FrameOwner frameOwner = getFrameOwner();
        ScreenData screenData = UiControllerUtils.getScreenData(frameOwner);

        getUnconfiguredLoaders(screenData).forEach(loader -> configureAutomatically(loader, frameOwner));
    }

    private Stream<DataLoader> getUnconfiguredLoaders(ScreenData screenData) {
        return screenData.getLoaderIds().stream()
                .map(screenData::<DataLoader>getLoader)
                .distinct()
                .filter(loader -> loadTriggers.stream()
                        .map(LoadTrigger::getLoader).noneMatch(configuredLoader -> configuredLoader == loader));
    }

    private void configureAutomatically(DataLoader loader, FrameOwner frameOwner) {
        List<String> queryParameters = getQueryParameters(loader);
        List<String> allParameters = new ArrayList<>(queryParameters);
        allParameters.addAll(getConditionParameters(loader));

        // add triggers on container/component events
        for (String parameter : allParameters) {
            if (parameter.startsWith(containerPrefix)) {
                InstanceContainer container = UiControllerUtils.getScreenData(frameOwner).getContainer(
                        parameter.substring(containerPrefix.length()));
                OnContainerItemChangedLoadTrigger loadTrigger = new OnContainerItemChangedLoadTrigger(
                        loader, container, parameter);
                loadTriggers.add(loadTrigger);

            } else if (parameter.startsWith(componentPrefix)) {
                String componentId = parameter.substring(componentPrefix.length());
                Component component = frameOwner instanceof Screen ?
                        ((Screen) frameOwner).getWindow().getComponentNN(componentId) :
                        ((ScreenFragment) frameOwner).getFragment().getComponentNN(componentId);
                OnComponentValueChangedLoadTrigger loadTrigger = new OnComponentValueChangedLoadTrigger(
                        loader, component, parameter, findLikeClause(loader, parameter));
                loadTriggers.add(loadTrigger);
            }
        }
        // if the loader has no parameters in query, add trigger on BeforeShowEvent/AttachEvent
        if (queryParameters.isEmpty()) {
            Class eventClass = frameOwner instanceof Screen ? Screen.BeforeShowEvent.class : ScreenFragment.AttachEvent.class;
            OnFrameOwnerEventLoadTrigger loadTrigger = new OnFrameOwnerEventLoadTrigger(
                    frameOwner, reflectionInspector, loader, eventClass);
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

    private LikeClause findLikeClause(DataLoader loader, String parameter) {
        if (!Strings.isNullOrEmpty(loader.getQuery())) {
            if (containsLikeClause(loader.getQuery(), parameter)) {
                return LikeClause.CASE_INSENSITIVE;
            }
        }
        if (loader.getCondition() != null) {
            if (containsLikeClause(loader.getCondition(), parameter)) {
                return LikeClause.CASE_INSENSITIVE;
            }
        }
        return LikeClause.NONE;
    }

    private boolean containsLikeClause(Condition condition, String parameter) {
        if (condition instanceof JpqlCondition) {
            String where = ((JpqlCondition) condition).getValue("where");
            if (where != null) {
                Matcher matcher = LIKE_PATTERN.matcher(where);
                while (matcher.find()) {
                    if (matcher.group(1).equals(parameter)) {
                        return true;
                    }
                }
            }
        } else if (condition instanceof LogicalCondition) {
            for (Condition nestedCondition : ((LogicalCondition) condition).getConditions()) {
                if (containsLikeClause(nestedCondition, parameter))
                    return true;
            }
        }
        return false;
    }

    private boolean containsLikeClause(String query, String parameter) {
        Matcher matcher = LIKE_PATTERN.matcher(query);
        while (matcher.find()) {
            if (matcher.group(1).equals(parameter)) {
                return true;
            }
        }
        return false;
    }

    private FrameOwner getFrameOwner() {
        Frame frame = getOwner();
        if (frame == null) {
            throw new IllegalStateException("Owner frame is null");
        }
        return frame.getFrameOwner();
    }
}

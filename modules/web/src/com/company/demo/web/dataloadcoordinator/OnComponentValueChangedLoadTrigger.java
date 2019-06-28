package com.company.demo.web.dataloadcoordinator;

import com.haulmont.cuba.core.global.DevelopmentException;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.model.DataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnComponentValueChangedLoadTrigger implements LoadTrigger {

    private final DataLoader loader;
    private final HasValue component;
    private final String param;
    private final LikeClause likeClause;

    private static final Logger log = LoggerFactory.getLogger(OnComponentValueChangedLoadTrigger.class);

    public OnComponentValueChangedLoadTrigger(DataLoader loader, Component component, String param, LikeClause likeClause) {
        this.likeClause = likeClause;
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
        Object value = component.getValue();
        if (value != null && likeClause != LikeClause.NONE) {
            if (!(value instanceof String)) {
                log.warn("Like clause with non-string parameter. The value is passed as is without wrapping in %.");
            } else {
                value = "%" + value + "%";
                if (likeClause == LikeClause.CASE_INSENSITIVE) {
                    value = "(?i)" + value;
                }
            }
        }
        loader.setParameter(param, value);
        loader.load();
    }

    @Override
    public DataLoader getLoader() {
        return loader;
    }
}

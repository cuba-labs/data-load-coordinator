package com.company.demo.web.screens.owner;

import com.company.demo.entity.Owner;
import com.google.common.base.Strings;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

@UiController("demo_Owner.browse.semiauto")
@UiDescriptor("owner-browse-semi-auto.xml")
@LookupComponent("ownersTable")
//@LoadDataBeforeShow
public class OwnerBrowseSemiAuto extends StandardLookup<Owner> {

    @Inject
    private CollectionLoader<Owner> ownersDl;

    @Subscribe("nameFilterField")
    private void onNameFilterFieldValueChange(HasValue.ValueChangeEvent<String> event) {
        String value = event.getValue();
        if (!Strings.isNullOrEmpty(value)) {
            ownersDl.setParameter("name", "(?i)%" + value + "%");
        } else {
            ownersDl.removeParameter("name");
        }
        ownersDl.load();
    }}
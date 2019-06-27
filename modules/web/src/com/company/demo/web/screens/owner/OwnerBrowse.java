package com.company.demo.web.screens.owner;

import com.google.common.base.Strings;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.company.demo.entity.Owner;

import javax.inject.Inject;

@UiController("demo_Owner.browse")
@UiDescriptor("owner-browse.xml")
@LookupComponent("ownersTable")
//@LoadDataBeforeShow
public class OwnerBrowse extends StandardLookup<Owner> {

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
    }
}
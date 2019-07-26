package com.company.demo.web.screens.owner;

import com.company.demo.entity.Owner;
import com.haulmont.cuba.gui.screen.LookupComponent;
import com.haulmont.cuba.gui.screen.StandardLookup;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

@UiController("demo_Owner.browse")
@UiDescriptor("owner-browse.xml")
@LookupComponent("ownersTable")
//@LoadDataBeforeShow
public class OwnerBrowse extends StandardLookup<Owner> {
}
package com.company.demo.web.screens.owner;

import com.haulmont.cuba.gui.screen.*;
import com.company.demo.entity.Owner;

@UiController("demo_Owner.edit")
@UiDescriptor("owner-edit.xml")
@EditedEntityContainer("ownerDc")
//@LoadDataBeforeShow
public class OwnerEdit extends StandardEditor<Owner> {
}
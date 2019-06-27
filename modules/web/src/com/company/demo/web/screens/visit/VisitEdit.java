package com.company.demo.web.screens.visit;

import com.haulmont.cuba.gui.screen.*;
import com.company.demo.entity.Visit;

@UiController("demo_Visit.edit")
@UiDescriptor("visit-edit.xml")
@EditedEntityContainer("visitDc")
@LoadDataBeforeShow
public class VisitEdit extends StandardEditor<Visit> {
}
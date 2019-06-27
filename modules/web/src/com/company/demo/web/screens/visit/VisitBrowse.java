package com.company.demo.web.screens.visit;

import com.haulmont.cuba.gui.screen.*;
import com.company.demo.entity.Visit;

@UiController("demo_Visit.browse")
@UiDescriptor("visit-browse.xml")
@LookupComponent("visitsTable")
//@LoadDataBeforeShow
public class VisitBrowse extends StandardLookup<Visit> {
}
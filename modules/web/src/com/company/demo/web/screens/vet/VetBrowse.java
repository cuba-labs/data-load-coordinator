package com.company.demo.web.screens.vet;

import com.haulmont.cuba.gui.screen.*;
import com.company.demo.entity.Vet;

@UiController("demo_Vet.browse")
@UiDescriptor("vet-browse.xml")
@LookupComponent("vetsTable")
@LoadDataBeforeShow
public class VetBrowse extends StandardLookup<Vet> {
}
package com.company.demo.web.screens.vet;

import com.haulmont.cuba.gui.screen.*;
import com.company.demo.entity.Vet;

@UiController("demo_Vet.edit")
@UiDescriptor("vet-edit.xml")
@EditedEntityContainer("vetDc")
@LoadDataBeforeShow
public class VetEdit extends StandardEditor<Vet> {
}
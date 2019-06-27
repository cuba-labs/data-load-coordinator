package com.company.demo.web.screens.pet;

import com.haulmont.cuba.gui.screen.*;
import com.company.demo.entity.Pet;

@UiController("demo_Pet.edit")
@UiDescriptor("pet-edit.xml")
@EditedEntityContainer("petDc")
@LoadDataBeforeShow
public class PetEdit extends StandardEditor<Pet> {
}
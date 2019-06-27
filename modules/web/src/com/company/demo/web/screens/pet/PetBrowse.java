package com.company.demo.web.screens.pet;

import com.haulmont.cuba.gui.screen.*;
import com.company.demo.entity.Pet;

@UiController("demo_Pet.browse")
@UiDescriptor("pet-browse.xml")
@LookupComponent("petsTable")
@LoadDataBeforeShow
public class PetBrowse extends StandardLookup<Pet> {
}
package com.company.demo.web.screens.country;

import com.haulmont.cuba.gui.screen.*;
import com.company.demo.entity.Country;

@UiController("demo_Country.browse")
@UiDescriptor("country-browse.xml")
@LookupComponent("table")
@LoadDataBeforeShow
public class CountryBrowse extends MasterDetailScreen<Country> {
}
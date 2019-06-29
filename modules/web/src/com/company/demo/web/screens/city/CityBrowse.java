package com.company.demo.web.screens.city;

import com.haulmont.cuba.gui.screen.*;
import com.company.demo.entity.City;

@UiController("demo_City.browse")
@UiDescriptor("city-browse.xml")
@LookupComponent("table")
@LoadDataBeforeShow
public class CityBrowse extends MasterDetailScreen<City> {
}
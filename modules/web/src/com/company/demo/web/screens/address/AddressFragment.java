package com.company.demo.web.screens.address;

import com.company.demo.entity.City;
import com.company.demo.entity.Country;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

@UiController("demo_AddressFragment")
@UiDescriptor("address-fragment.xml")
public class AddressFragment extends ScreenFragment {

    @Inject
    private LookupField<City> cityField;

    @Subscribe(id = "countriesDc", target = Target.DATA_CONTAINER)
    private void onCountriesDcItemChange(InstanceContainer.ItemChangeEvent<Country> event) {
        cityField.setValue(null);
    }
}
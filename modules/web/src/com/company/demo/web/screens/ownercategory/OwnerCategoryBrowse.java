package com.company.demo.web.screens.ownercategory;

import com.haulmont.cuba.gui.screen.*;
import com.company.demo.entity.OwnerCategory;

@UiController("demo_OwnerCategory.browse")
@UiDescriptor("owner-category-browse.xml")
@LookupComponent("table")
@LoadDataBeforeShow
public class OwnerCategoryBrowse extends MasterDetailScreen<OwnerCategory> {
}
# DataLoadCoordinator

## Overview

`DataLoadCoordinator` facet is designed for declarative linking data loaders to data containers, visual components and screen events. It can work in two modes: 

* automatic mode relies on parameter names with special prefixes denoting components which produce parameter values and change events
* in manual mode, the links are specified in the facet markup or via its API (the latter hardly makes sense).

In automatic mode, if the loader has no parameters, it is refreshed on `BeforeShowEvent` in `Screen` and on `AttachEvent` in `ScreenFragment`.

Semi-automatic mode is also possible, when some links are specified explicitly and the rest is configured automatically.

When using `DataLoadCoordinator`, the `@LoadDataBeforeShow` annotation must be removed, otherwise it will lead to multiple reloads. We should implement an inspection in Studio for this later.

## Use Cases

### Automatic configuration

* Loaders having prefixed parameters are triggered on corresponding Container/Component events.
* Loaders not having parameters in query (though may have parameters in conditions) triggered on BeforeShow event.

```xml
<data readOnly="true">
    <collection id="ownersDc" class="com.company.demo.entity.Owner" view="owner-with-category-view">
        <loader id="ownersDl">
            <query>
                <!-- no params in query - will be triggered BeforeShow -->
                <![CDATA[select e from demo_Owner e]]>
                <condition>
                    <c:jpql>
                        <!-- triggers on 'categoryFilterField' value change -->
                        <c:where>e.category = :component_categoryFilterField</c:where> 
                    </c:jpql>
                </condition>
            </query>
        </loader>
    </collection>
    <collection id="petsDc" class="com.company.demo.entity.Pet">
        <loader id="petsDl">
            <!-- triggers on 'ownersDc' item change -->
            <query><![CDATA[select e from demo_Pet e where e.owner = :container_ownersDc]]></query> 
        </loader>
    </collection>
</data>
<facets>
    <dataLoadCoordinator auto="true"/>
</facets>
<layout>
    <pickerField id="categoryFilterField" metaClass="demo_OwnerCategory"/>
```

### Manual configuration

```xml
<data readOnly="true">
    <collection id="ownersDc" class="com.company.demo.entity.Owner" view="owner-with-category-view">
        <loader id="ownersDl">
            <query>
                <![CDATA[select e from demo_Owner e]]>
                <condition>
                    <and>
                        <c:jpql>
                            <c:where>e.category = :category</c:where>
                        </c:jpql>
                        <c:jpql>
                            <c:where>e.name like :name</c:where>
                        </c:jpql>                    
                    </and>
                </condition>
            </query>
        </loader>
    </collection>
    <collection id="petsDc" class="com.company.demo.entity.Pet">
        <loader id="petsDl">
            <query><![CDATA[select e from demo_Pet e where e.owner = :owner]]></query>
        </loader>
    </collection>
</data>
<facets>
    <dataLoadCoordinator>
        <refresh loader="ownersDl" onScreenEvent="Init"/>
        <refresh loader="petsDl" param="owner" onContainerItemChanged="ownersDc"/>
        <refresh loader="ownersDl" param="category" onComponentValueChanged="categoryFilterField"/>

        <refresh loader="ownersDl" param="name" onComponentValueChanged="nameFilterField" 
            likeClause="CASE_INSENSITIVE"/> <!-- wraps value in '(?i)%value%' --> 
    </dataLoadCoordinator>
</facets>
<layout>
    <pickerField id="categoryFilterField" metaClass="demo_OwnerCategory"/>
    <textField id="nameFilterField"/>    
```

### Manual configuration in Fragment

```xml
<data>
    <instance id="addressDc"
              class="com.company.demo.entity.Address"
              view="_local" provided="true">
    </instance>
    <collection id="countriesDc" class="com.company.demo.entity.Country" view="_minimal">
        <loader id="countriesDl">
            <query><![CDATA[select e from demo_Country e ]]></query>
        </loader>
    </collection>
    <collection id="citiesDc" class="com.company.demo.entity.City" view="_minimal">
        <loader id="citiesDl">
            <query><![CDATA[select e from demo_City e where e.country = :container_countriesDc]]></query>
        </loader>
    </collection>
</data>
<facets>
    <dataLoadCoordinator>
        <refresh loader="countriesDl" onFragmentEvent="Attach"/>
        <refresh loader="citiesDl" param="country" onContainerItemChanged="countriesDc"/>
    </dataLoadCoordinator>
</facets>
```


### Manual configuration with parameter name omitted

Works if there is a single parameter in query or conditions
   
```xml
<facets>
    <dataLoadCoordinator>
        <refresh loader="ownersDl" onScreenEvent="Init"/>
        <refresh loader="petsDl" onContainerItemChanged="ownersDc"/>
        <refresh loader="ownersDl" onComponentValueChanged="categoryFilterField"/>
    </dataLoadCoordinator>
</facets>
```

### Semi-automatic configuration

Affects only loaders not having manual configurations.
   
```xml
<data readOnly="true">
    <collection id="ownersDc" class="com.company.demo.entity.Owner" view="owner-with-category-view">
        <loader id="ownersDl">
            <query>
                <![CDATA[select e from demo_Owner e]]>
                <condition>
                    <c:jpql>
                        <c:where>e.category = :component_categoryFilterField</c:where>
                    </c:jpql>
                </condition>
            </query>
        </loader>
    </collection>
    <collection id="petsDc" class="com.company.demo.entity.Pet">
        <loader id="petsDl">
            <query><![CDATA[select e from demo_Pet e where e.owner = :owner]]></query>
        </loader>
    </collection>
</data>
<facets>
    <dataLoadCoordinator auto="true">
        <refresh loader="petsDl" onContainerItemChanged="ownersDc" param="owner"/>
    </dataLoadCoordinator>
</facets>
<layout>
    <pickerField id="categoryFilterField" metaClass="demo_OwnerCategory"/>
```
    
### Auto-configuration with handling case-insensitive `like` parameters
   
```xml
<data readOnly="true">
    <collection id="ownersDc" class="com.company.demo.entity.Owner" view="owner-with-category-view">
        <loader id="ownersDl">
            <query>
                <![CDATA[select e from demo_Owner e]]>
                <condition>
                    <and>
                        <c:jpql>
                            <!-- triggers on 'categoryFilterField' value change -->
                            <c:where>e.category = :component_categoryFilterField</c:where>
                        </c:jpql>
                        <c:jpql>
                            <!-- triggers on 'nameFilterField' value change, automatically wraps value in '(?i)%value%' -->
                            <c:where>e.name like :component_nameFilterField</c:where>
                        </c:jpql>
                    </and>
                </condition>
            </query>
        </loader>
    </collection>
    <collection id="petsDc" class="com.company.demo.entity.Pet">
        <loader id="petsDl">
            <!-- triggers on 'ownersDc' item change -->
            <query><![CDATA[select e from demo_Pet e where e.owner = :container_ownersDc]]></query> 
        </loader>
    </collection>
</data>
<facets>
    <dataLoadCoordinator auto="true"/>
</facets>
<layout>
    <pickerField id="categoryFilterField" metaClass="demo_OwnerCategory"/>
    <textField id="nameFilterField"/>    
```

# DataLoadCoordinator

## Overview

`DataLoadCoordinator` facet is designed for declarative linking data loaders to data containers, visual components and screen events. It can work in two modes: 

* automatic mode relies on parameter names with special prefixes denoting components which produce parameter values and change events
* in manual mode, the links are specified in the facet markup or via its API (the latter hardly makes sense).

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
                        <c:where>e.category = :component$categoryFilterField</c:where> 
                    </c:jpql>
                </condition>
            </query>
        </loader>
    </collection>
    <collection id="petsDc" class="com.company.demo.entity.Pet">
        <loader id="petsDl">
            <!-- triggers on 'ownersDc' item change -->
            <query><![CDATA[select e from demo_Pet e where e.owner = :container$ownersDc]]></query> 
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
                    <c:jpql>
                        <c:where>e.category = :category</c:where>
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
    <dataLoadCoordinator>
        <loader ref="ownersDl" onScreenEvent="Init"/>
        <loader ref="petsDl" onContainerItemChanged="ownersDc" param="owner"/>
        <loader ref="ownersDl" onComponentValueChanged="categoryFilterField" param="category"/>
    </dataLoadCoordinator>
</facets>
<layout>
    <pickerField id="categoryFilterField" metaClass="demo_OwnerCategory"/>
```

### Manual configuration with parameter name omitted. Works if there is a single parameter in query or conditions
   
```xml
<facets>
    <dataLoadCoordinator>
        <loader ref="ownersDl" onScreenEvent="Init"/>
        <loader ref="petsDl" onContainerItemChanged="ownersDc"/>
        <loader ref="ownersDl" onComponentValueChanged="categoryFilterField"/>
    </dataLoadCoordinator>
</facets>
```

### Semi-automatic configuration: affects only loaders not having manual configurations
   
```xml
<data readOnly="true">
    <collection id="ownersDc" class="com.company.demo.entity.Owner" view="owner-with-category-view">
        <loader id="ownersDl">
            <query>
                <![CDATA[select e from demo_Owner e]]>
                <condition>
                    <c:jpql>
                        <c:where>e.category = :component$categoryFilterField</c:where>
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
        <loader ref="petsDl" onContainerItemChanged="ownersDc" param="owner"/>
    </dataLoadCoordinator>
</facets>
<layout>
    <pickerField id="categoryFilterField" metaClass="demo_OwnerCategory"/>
```
    
### Mixed auto-configuration and reloading in controller

Needed for pre-processing parameter values, for example for handling case-insensitive `like` parameters.
   
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
                            <c:where>e.category = :component$categoryFilterField</c:where>
                        </c:jpql>
                        <c:jpql>
                            <!-- this parameters is set in controller -->
                            <c:where>e.name like :name</c:where>
                        </c:jpql>
                    </and>
                </condition>
            </query>
        </loader>
    </collection>
    <collection id="petsDc" class="com.company.demo.entity.Pet">
        <loader id="petsDl">
            <!-- triggers on 'ownersDc' item change -->
            <query><![CDATA[select e from demo_Pet e where e.owner = :container$ownersDc]]></query> 
        </loader>
    </collection>
</data>
<facets>
    <dataLoadCoordinator auto="true"/>
</facets>
<layout>
    <pickerField id="categoryFilterField" metaClass="demo_OwnerCategory"/>
```

```java
@Inject
private CollectionLoader<Owner> ownersDl;

@Subscribe("nameFilterField")
private void onNameFilterFieldValueChange(HasValue.ValueChangeEvent<String> event) {
    String value = event.getValue();
    if (!Strings.isNullOrEmpty(value)) {
        ownersDl.setParameter("name", "(?i)%" + value + "%");
    } else {
        ownersDl.removeParameter("name");
    }
    ownersDl.load();
}    
```

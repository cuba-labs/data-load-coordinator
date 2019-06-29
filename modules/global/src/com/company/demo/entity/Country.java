package com.company.demo.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|name")
@Table(name = "DEMO_COUNTRY")
@Entity(name = "demo_Country")
public class Country extends StandardEntity {
    private static final long serialVersionUID = 1094553473097083761L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "CODE", length = 2)
    protected String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
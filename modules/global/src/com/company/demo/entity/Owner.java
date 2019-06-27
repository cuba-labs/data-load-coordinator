package com.company.demo.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.Email;

@NamePattern("%s|name")
@Table(name = "DEMO_OWNER")
@Entity(name = "demo_Owner")
public class Owner extends StandardEntity {
    private static final long serialVersionUID = 3567970938271564819L;

    @Column(name = "NAME")
    protected String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    protected OwnerCategory category;

    @Email
    @Column(name = "EMAIL")
    protected String email;

    public OwnerCategory getCategory() {
        return category;
    }

    public void setCategory(OwnerCategory category) {
        this.category = category;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
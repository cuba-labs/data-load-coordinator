package com.company.demo.core;

import com.company.demo.entity.Owner;
import com.company.demo.entity.Pet;
import com.haulmont.cuba.core.app.events.EntityPersistingEvent;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.UUID;

@Component(PetPersistingListener.NAME)
public class PetPersistingListener {

    public static final String NAME = "demo_PetPersistingListener";

    private static final UUID defaultOwnerId = UUID.fromString("678bec07-c8b0-fb7c-97b8-dba5fbcb2586");

    @Inject
    private DataManager dataManager;

    @EventListener
    public void onPetPersisting(EntityPersistingEvent<Pet> event) {
        Pet pet = event.getEntity();

        if (pet.getOwner() == null) {
            pet.setOwner(dataManager.getReference(Owner.class, defaultOwnerId));
        }
    }
}
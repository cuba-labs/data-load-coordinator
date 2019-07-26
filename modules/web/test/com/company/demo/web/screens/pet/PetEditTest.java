package com.company.demo.web.screens.pet;

import com.company.demo.entity.Pet;
import com.company.demo.web.DemoWebTestContainer;
import com.haulmont.cuba.core.app.DataService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.OpenMode;
import com.haulmont.cuba.gui.screen.UiControllerUtils;
import com.haulmont.cuba.web.app.main.MainScreen;
import com.haulmont.cuba.web.testsupport.TestEntityFactory;
import com.haulmont.cuba.web.testsupport.TestEntityState;
import com.haulmont.cuba.web.testsupport.TestUiEnvironment;
import com.haulmont.cuba.web.testsupport.proxy.TestServiceProxy;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PetEditTest {

    @Rule
    public TestUiEnvironment environment =
            new TestUiEnvironment(DemoWebTestContainer.Common.INSTANCE).withUserLogin("admin");

    private Pet pet;

    @Mocked
    private DataService dataService;

    @Before
    public void setUp() throws Exception {
        TestEntityFactory<Pet> petsFactory =
                environment.getContainer().getEntityFactory(Pet.class, TestEntityState.DETACHED);

        pet = petsFactory.create("name", "Test");

        new Expectations() {{
            dataService.load((LoadContext<? extends Entity>) any);
            result = new Delegate() {
                Entity load(LoadContext lc) {
                    if ("demo_Pet".equals(lc.getEntityMetaClass())) {
                        return pet;
                    } else
                        return null;
                }
            };
        }};

        TestServiceProxy.mock(DataService.class, dataService);
    }

    @After
    public void tearDown() throws Exception {
        TestServiceProxy.clear();
    }

    @Test
    public void testLoadData() {
        Screens screens = environment.getScreens();

        screens.create(MainScreen.class, OpenMode.ROOT).show();

        // open editor with existing instance
        PetEdit petEdit = screens.create(PetEdit.class);
        petEdit.setEntityToEdit(pet);
        petEdit.show();

        // check that data container has the instance
        InstanceContainer<Pet> petDc = UiControllerUtils.getScreenData(petEdit).getContainer("petDc");
        assertEquals(pet, petDc.getItem());

        // modify the instance
        petDc.getItem().setName("changed");

        // commit editor
        Action commitAndCloseAction = petEdit.getWindow().getActionNN("windowCommitAndClose");
        commitAndCloseAction.actionPerform(null);

        // check that the committed instance has modified attribute
        new Verifications() {{
            CommitContext cc;
            dataService.commit(cc = withCapture());

            Pet committedPet = (Pet) cc.getCommitInstances().iterator().next();
            assertEquals(pet, committedPet);
            assertEquals("changed", committedPet.getName());
        }};
    }
}
package com.company.demo.web.screens.timer;

import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;

@UiController("demo_TimerDemoScreen")
@UiDescriptor("timer-demo-screen.xml")
public class TimerDemoScreen extends Screen {

    @Inject
    private Notifications notifications;

    @Subscribe("myTimer")
    private void onTimer(Timer.TimerActionEvent event) {
        notifications.create().withCaption("on timer").show();
    }
}
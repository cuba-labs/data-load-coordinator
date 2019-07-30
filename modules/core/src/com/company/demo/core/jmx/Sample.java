package com.company.demo.core.jmx;

import org.springframework.stereotype.Component;

@Component
public class Sample implements SampleMBean {

    @Override
    public String echo(String input) {
        return input;
    }
}

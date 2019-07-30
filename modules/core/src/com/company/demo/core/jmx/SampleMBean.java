package com.company.demo.core.jmx;

import com.haulmont.cuba.core.sys.jmx.JmxBean;

@JmxBean(module = "demo", alias = "Sample")
public interface SampleMBean {

    String echo(String input);
}

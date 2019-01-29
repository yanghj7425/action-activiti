package com.yhj.activiti.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MDCErrorServiceDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(MDCErrorServiceDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        LOGGER.info("{}", execution.getEventName());
//        throw new RuntimeException();
    }
}

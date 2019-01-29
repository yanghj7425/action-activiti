package com.yhj.hello.activiti;


import com.yhj.hello.activiti.step1.HelloWordDemoMain;

import java.text.ParseException;

public class DemoMain {
    public static void main(String[] args) throws ParseException {
        // step.1 demo action
        HelloWordDemoMain helloWordDemoMain = new HelloWordDemoMain();
        helloWordDemoMain.launch();

    }


}

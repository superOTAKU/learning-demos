package com.example.module2;

import com.example.module1.HelloService;

public class HelloCaller {

    public void callSayHello() {
        new HelloService().sayHello();
    }

}

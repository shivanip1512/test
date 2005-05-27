package com.cannontech.notif.test;

import java.util.ArrayList;
import java.util.List;

public class TestObject {
    public String one = "One";
    public int two = 2;
    public short three = 3;
    public Object blank = new Object();
    public List someList = new ArrayList();
    
    public TestObject() {
        someList.add("aba");
        someList.add("bab");
        someList.add("caca");
        someList.add("dada");
        List otherList = new ArrayList();
        otherList.add("rock");
        otherList.add("cool");
        someList.add(otherList);
    }

}

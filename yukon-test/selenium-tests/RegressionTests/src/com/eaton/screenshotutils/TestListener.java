package com.eaton.screenshotutils;

import org.testng.ITestListener;
import org.testng.ITestResult;

import com.eaton.framework.SeleniumTestSetup;

public class TestListener extends SeleniumTestSetup implements ITestListener {
    
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("***** Error " + result.getName() + " test has failed *****");
        
        String methodName=result.getName().toString().trim();
        
        TestUtil.captureScreenshot(methodName);
    }   
}
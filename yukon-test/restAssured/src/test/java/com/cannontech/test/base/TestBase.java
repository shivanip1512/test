package com.cannontech.test.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;


public class TestBase implements ITestListener {
    private static final Logger log = LogManager.getLogger(TestBase.class);
    long testStartTime, testEndTime, testExecutionTime;

    @Override
    public void onStart(ITestContext context) {
        log.info("Starting Test :" + context.getName());
    }

    @Override
    public void onTestStart(ITestResult context) {
        log.info("Starting Test :" + context.getName());
    }

    @Override
    public void onTestSuccess(ITestResult context) {
        log.error("Test Passed :" + context.getName());
    }

    @Override
    public void onTestFailure(ITestResult context) {
        log.info("Test Failed :" + context.getName());
      
    }

    @Override
    public void onTestSkipped(ITestResult context) {
        log.info("Test Skipped :" + context.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult context) {

    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("Test Finished:" + context.getName());
    }

}

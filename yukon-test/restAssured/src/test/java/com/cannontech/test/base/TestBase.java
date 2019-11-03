package com.cannontech.test.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class TestBase implements ITestListener {
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

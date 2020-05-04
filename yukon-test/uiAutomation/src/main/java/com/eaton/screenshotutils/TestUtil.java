package com.eaton.screenshotutils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.eaton.framework.SeleniumTestSetup;

public class TestUtil extends SeleniumTestSetup {
    static String filePath = "C:\\Screenshots\\";
    private static final String DATE_FORMAT = "ddMMyyyyHHmmss";

    public static void captureScreenshot(String methodName) {
        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());
        File scrFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);

        try {
            FileUtils.copyFile(scrFile, new File(filePath + methodName + "_" + timeStamp + ".png"));
            System.out.println("***Placed screen shot in " + filePath + " ***");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package com.eaton.framework.test.annotation;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import com.eaton.framework.SeleniumTestSetup;

public class RefreshPageTestNgAnnotationListener implements IInvokedMethodListener, ITestListener  {
    
    boolean refreshPage;
    boolean testSuccess = true;
    
    String url = null;
    
    /* (non-Javadoc)
     * @see org.testng.IInvokedMethodListener#beforeInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
     */
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
     // TODO Auto-generated method stub
    }
    
    private boolean annotationPresent(IInvokedMethod method, Class clazz) {
        boolean retVal = method.getTestMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(clazz) ? true : false;
        return retVal;
    }
    
    /* (non-Javadoc)
     * @see org.testng.IInvokedMethodListener#afterInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
     */
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {        
        if(method.isTestMethod() && annotationPresent(method, CustomTestNgAnnotations.class) ) {            
            if(refreshPage) {
                SeleniumTestSetup.navigate(url);                
            }            
        }
    }
    
    public void onTestStart(ITestResult result) {
        // TODO Auto-generated method stub
        
    }

    public void onTestSuccess(ITestResult result) {
        // TODO Auto-generated method stub
        
    }

    public void onTestFailure(ITestResult result) {
        // TODO Auto-generated method stub
        
    }

    public void onTestSkipped(ITestResult result) {
        // TODO Auto-generated method stub
        
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // TODO Auto-generated method stub
        
    }

    public void onStart(ITestContext context) {
        for(ITestNGMethod m1 : context.getAllTestMethods()) {
            if(m1.getConstructorOrMethod().getMethod().isAnnotationPresent(CustomTestNgAnnotations.class)) {
                //capture metadata information.
                url = m1.getConstructorOrMethod().getMethod().getAnnotation(CustomTestNgAnnotations.class).urlToRefresh();
                refreshPage = m1.getConstructorOrMethod().getMethod().getAnnotation(CustomTestNgAnnotations.class).refreshPage();
            }
        }
        
    }

    public void onFinish(ITestContext context) {
        // TODO Auto-generated method stub
        
    }
}

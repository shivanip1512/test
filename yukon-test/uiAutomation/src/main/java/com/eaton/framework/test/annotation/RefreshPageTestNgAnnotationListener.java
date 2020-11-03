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
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
     // Auto-generated method stub
    }
    
    private boolean annotationPresent(IInvokedMethod method, Class clazz) {
        boolean retVal = method.getTestMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(clazz) ? true : false;
        return retVal;
    }
    
    /* (non-Javadoc)
     * @see org.testng.IInvokedMethodListener#afterInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
     */
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {        
        if(method.isTestMethod() && annotationPresent(method, CustomTestNgAnnotations.class) ) {            
            if(refreshPage) {
                SeleniumTestSetup.navigate(url);                
            }            
        }
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        // Auto-generated method stub
        
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // Auto-generated method stub
        
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Auto-generated method stub
        
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // Auto-generated method stub
        
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Auto-generated method stub
        
    }
    
    @Override
    public void onStart(ITestContext context) {
        for(ITestNGMethod m1 : context.getAllTestMethods()) {
            if(m1.getConstructorOrMethod().getMethod().isAnnotationPresent(CustomTestNgAnnotations.class)) {
                //capture metadata information.
                url = m1.getConstructorOrMethod().getMethod().getAnnotation(CustomTestNgAnnotations.class).urlToRefresh();
                refreshPage = m1.getConstructorOrMethod().getMethod().getAnnotation(CustomTestNgAnnotations.class).refreshPage();
            }
        }
        
    }

    @Override
    public void onFinish(ITestContext context) {
        // Auto-generated method stub
        
    }
}

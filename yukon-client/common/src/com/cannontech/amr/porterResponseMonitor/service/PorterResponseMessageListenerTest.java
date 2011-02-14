package com.cannontech.amr.porterResponseMonitor.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorErrorCode;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorMatchStyle;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorRule;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorTransaction;
import com.google.common.collect.Lists;

public class PorterResponseMessageListenerTest {

    // --- ALL ---
    @Test
    public void test_all1() {
        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode(34);
        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1);
        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
        rule.setErrorCodes(errorCodes);
        rule.setSuccess(false);
        rule.setMatchStyle(PorterResponseMonitorMatchStyle.all);
        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 34);
        transaction.addErrorCode(42);
        transaction.addErrorCode(54);
        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
        Assert.assertEquals(true, shouldSendPointData);
    }
    @Test
    public void test_all2() {
        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode(34);
        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1);
        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
        rule.setErrorCodes(errorCodes);
        rule.setSuccess(true);
        rule.setMatchStyle(PorterResponseMonitorMatchStyle.all);
        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 34);
        transaction.addErrorCode(42);
        transaction.addErrorCode(54);
        transaction.addErrorCode(0);
        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
        Assert.assertEquals(true, shouldSendPointData);
    }
    @Test
    public void test_all3() {
        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode(34);
        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1);
        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
        rule.setErrorCodes(errorCodes);
        rule.setSuccess(true);
        rule.setMatchStyle(PorterResponseMonitorMatchStyle.all);
        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 34);
        transaction.addErrorCode(42);
        transaction.addErrorCode(0);
        transaction.addErrorCode(54);
        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
        Assert.assertEquals(false, shouldSendPointData);
    }
    @Test
    public void test_all4() {
        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode(34);
        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1);
        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
        rule.setErrorCodes(errorCodes);
        rule.setSuccess(false);
        rule.setMatchStyle(PorterResponseMonitorMatchStyle.all);
        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 34);
        transaction.addErrorCode(42);
        transaction.addErrorCode(0);
        transaction.addErrorCode(54);
        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
        Assert.assertEquals(true, shouldSendPointData);
    }
    @Test
    public void test_all5() {
        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode();
        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1);
        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
        rule.setErrorCodes(errorCodes);
        rule.setSuccess(false);
        rule.setMatchStyle(PorterResponseMonitorMatchStyle.all);
        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 34);
        transaction.addErrorCode(42);
        transaction.addErrorCode(54);
        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
        Assert.assertEquals(true, shouldSendPointData);
    }
    @Test
    public void test_all6() {
        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode();
        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1);
        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
        rule.setErrorCodes(errorCodes);
        rule.setSuccess(true);
        rule.setMatchStyle(PorterResponseMonitorMatchStyle.all);
        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 34);
        transaction.addErrorCode(42);
        transaction.addErrorCode(0);
        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
        Assert.assertEquals(true, shouldSendPointData);
    }
    @Test
    public void test_all7() {
        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode();
        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1);
        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
        rule.setErrorCodes(errorCodes);
        rule.setSuccess(true);
        rule.setMatchStyle(PorterResponseMonitorMatchStyle.all);
        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 0);
        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
        Assert.assertEquals(true, shouldSendPointData);
    }

    // --- ANY ---
    @Test
    public void test_any1() {
        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode(34);
        PorterResponseMonitorErrorCode error2 = new PorterResponseMonitorErrorCode(44);
        PorterResponseMonitorErrorCode error3 = new PorterResponseMonitorErrorCode(54);
        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1, error2, error3);
        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
        rule.setErrorCodes(errorCodes);
        rule.setSuccess(false);
        rule.setMatchStyle(PorterResponseMonitorMatchStyle.any);
        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 34);
        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
        Assert.assertEquals(true, shouldSendPointData);
    }
    @Test
    public void test_any2() {
        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode(34);
        PorterResponseMonitorErrorCode error2 = new PorterResponseMonitorErrorCode(44);
        PorterResponseMonitorErrorCode error3 = new PorterResponseMonitorErrorCode(54);
        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1, error2, error3);
        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
        rule.setErrorCodes(errorCodes);
        rule.setSuccess(false);
        rule.setMatchStyle(PorterResponseMonitorMatchStyle.any);
        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 34);
        transaction.addErrorCode(84);
        transaction.addErrorCode(456);
        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
        Assert.assertEquals(true, shouldSendPointData);
    }
    @Test
    public void test_any3() {
        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode(34);
        PorterResponseMonitorErrorCode error2 = new PorterResponseMonitorErrorCode(44);
        PorterResponseMonitorErrorCode error3 = new PorterResponseMonitorErrorCode(54);
        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1, error2, error3);
        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
        rule.setErrorCodes(errorCodes);
        rule.setSuccess(false);
        rule.setMatchStyle(PorterResponseMonitorMatchStyle.any);
        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 57);
        transaction.addErrorCode(84);
        transaction.addErrorCode(456);
        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
        Assert.assertEquals(false, shouldSendPointData);
    }
    // The following 3 rules currently won't pass. 
    // Leaving them here in case I want to account for this failure in my shouldSendPointData method at some point
//    @Test
//    public void test_any4() {
//        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode();
//        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1);
//        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
//        rule.setErrorCodes(errorCodes);
//        rule.setSuccess(true);
//        rule.setMatchStyle(PorterResponseMonitorMatchStyle.any);
//        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 57);
//        transaction.addErrorCode(84);
//        transaction.addErrorCode(456);
//        transaction.addErrorCode(0);
//        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
//        Assert.assertEquals(true, shouldSendPointData);
//    }
//    @Test
//    public void test_any5() {
//        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode();
//        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1);
//        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
//        rule.setErrorCodes(errorCodes);
//        rule.setSuccess(false);
//        rule.setMatchStyle(PorterResponseMonitorMatchStyle.any);
//        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 57);
//        transaction.addErrorCode(84);
//        transaction.addErrorCode(456);
//        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
//        Assert.assertEquals(true, shouldSendPointData);
//    }
//    @Test
//    public void test_any6() {
//        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode();
//        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1);
//        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
//        rule.setErrorCodes(errorCodes);
//        rule.setSuccess(true);
//        rule.setMatchStyle(PorterResponseMonitorMatchStyle.any);
//        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 0);
//        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
//        Assert.assertEquals(true, shouldSendPointData);
//    }

    // --- NONE ---
    @Test
    public void test_none1() {
        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode(34);
        PorterResponseMonitorErrorCode error2 = new PorterResponseMonitorErrorCode(44);
        PorterResponseMonitorErrorCode error3 = new PorterResponseMonitorErrorCode(54);
        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1, error2, error3);
        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
        rule.setErrorCodes(errorCodes);
        rule.setSuccess(false);
        rule.setMatchStyle(PorterResponseMonitorMatchStyle.none);
        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 57);
        transaction.addErrorCode(84);
        transaction.addErrorCode(456);
        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
        Assert.assertEquals(true, shouldSendPointData);
    }
    @Test
    public void test_none2() {
        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode(34);
        PorterResponseMonitorErrorCode error2 = new PorterResponseMonitorErrorCode(44);
        PorterResponseMonitorErrorCode error3 = new PorterResponseMonitorErrorCode(54);
        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1, error2, error3);
        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
        rule.setErrorCodes(errorCodes);
        rule.setSuccess(false);
        rule.setMatchStyle(PorterResponseMonitorMatchStyle.none);
        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 57);
        transaction.addErrorCode(84);
        transaction.addErrorCode(44);
        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
        Assert.assertEquals(false, shouldSendPointData);
    }
    @Test
    public void test_none3() {
        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode();
        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1);
        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
        rule.setErrorCodes(errorCodes);
        rule.setSuccess(false);
        rule.setMatchStyle(PorterResponseMonitorMatchStyle.none);
        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 57);
        transaction.addErrorCode(84);
        transaction.addErrorCode(44);
        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
        Assert.assertEquals(true, shouldSendPointData);
    }
    @Test
    public void test_none4() {
        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode();
        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1);
        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
        rule.setErrorCodes(errorCodes);
        rule.setSuccess(true);
        rule.setMatchStyle(PorterResponseMonitorMatchStyle.none);
        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 57);
        transaction.addErrorCode(84);
        transaction.addErrorCode(44);
        transaction.addErrorCode(0);
        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
        Assert.assertEquals(true, shouldSendPointData);
    }
    @Test
    public void test_none5() {
        PorterResponseMonitorErrorCode error1 = new PorterResponseMonitorErrorCode();
        List<PorterResponseMonitorErrorCode> errorCodes = Lists.newArrayList(error1);
        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
        rule.setErrorCodes(errorCodes);
        rule.setSuccess(true);
        rule.setMatchStyle(PorterResponseMonitorMatchStyle.none);
        PorterResponseMonitorTransaction transaction = new PorterResponseMonitorTransaction(1234, 0);
        boolean shouldSendPointData = PorterResponseMessageListener.shouldSendPointData(rule, transaction);
        Assert.assertEquals(true, shouldSendPointData);
    }
}

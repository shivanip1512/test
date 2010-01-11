package com.cannontech.web.taglib;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.web.taglib.MessageScopeHelper.MessageScope;
import com.google.common.collect.ImmutableList;

public class MessageScopeHelperTest {

    @Test
    public void test_basic_module_and_page() {
        MessageScope messageScope = new MessageScopeHelper.MessageScope();

        messageScope.pushScope("prefix.someModule", false);
        messageScope.pushScope("somePage", false);

        List<String> actual = messageScope.getFullKeys("suffix", "default.");

        ImmutableList<String> expected = ImmutableList.of("prefix.someModule.somePage.suffix",
                                                          "prefix.someModule.suffix",
                                                          "default.suffix");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_three_keys() {
        MessageScope messageScope = new MessageScopeHelper.MessageScope();

        messageScope.pushScope("prefix.someModule", false);
        messageScope.pushScope("somePage", false);
        messageScope.pushScope("someContext", false);

        List<String> actual = messageScope.getFullKeys("suffix", "default.");

        ImmutableList<String> expected = ImmutableList.of("prefix.someModule.somePage.someContext.suffix",
                                                          "prefix.someModule.somePage.suffix",
                                                          "prefix.someModule.suffix",
                                                          "default.suffix");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_simple_sticky() {
        MessageScope messageScope = new MessageScopeHelper.MessageScope();

        messageScope.pushScope("prefix.someModule", false);
        messageScope.pushScope("somePage", false);
        messageScope.pushScope("someComponent", true);

        List<String> actual = messageScope.getFullKeys("suffix", "default.");

        ImmutableList<String> expected = ImmutableList.of("prefix.someModule.somePage.someComponent.suffix",
                                                          "prefix.someModule.someComponent.suffix",
                                                          "default.someComponent.suffix",
                                                          "default.suffix");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_middle_sticky() {
        MessageScope messageScope = new MessageScopeHelper.MessageScope();

        messageScope.pushScope("modules.someModule", false);
        messageScope.pushScope("somePage", false);
        messageScope.pushScope("someComponent", true);
        messageScope.pushScope("someContext", false);

        List<String> actual = messageScope.getFullKeys("suffix", "default.");

        ImmutableList<String> expected = ImmutableList.of("modules.someModule.somePage.someComponent.someContext.suffix",
                                                          "modules.someModule.someComponent.someContext.suffix",
                                                          "default.someComponent.someContext.suffix",
                                                          "default.someComponent.suffix",
                                                          "default.suffix");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_two_sticky() {
        MessageScope messageScope = new MessageScopeHelper.MessageScope();

        messageScope.pushScope("modules.someModule", false);
        messageScope.pushScope("somePage", false);
        messageScope.pushScope("componentName", false);
        messageScope.pushScope("someComponent", true);
        messageScope.pushScope("someComponent2", true);
        messageScope.pushScope("someContext2", false);

        List<String> actual = messageScope.getFullKeys("suffix", "default.");

        ImmutableList<String> expected = ImmutableList.of("modules.someModule.somePage.componentName.someComponent.someComponent2.someContext2.suffix",
                                                          "modules.someModule.somePage.someComponent.someComponent2.someContext2.suffix",
                                                          "modules.someModule.someComponent.someComponent2.someContext2.suffix",
                                                          "default.someComponent.someComponent2.someContext2.suffix",
                                                          "default.someComponent2.someContext2.suffix",
                                                          "default.someComponent2.suffix",
                                                          "default.suffix");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_push_pop() {
        MessageScope messageScope = new MessageScopeHelper.MessageScope();

        messageScope.pushScope("prefix.someModule", false);
        messageScope.pushScope("somePage", false);
        messageScope.pushScope("someContext1", false);
        messageScope.popScope();
        messageScope.pushScope("someContext2", false);

        List<String> actual = messageScope.getFullKeys("suffix", "default.");

        ImmutableList<String> expected = ImmutableList.of("prefix.someModule.somePage.someContext2.suffix",
                                                          "prefix.someModule.somePage.suffix",
                                                          "prefix.someModule.suffix",
                                                          "default.suffix");

        Assert.assertEquals(expected, actual);
    }
}

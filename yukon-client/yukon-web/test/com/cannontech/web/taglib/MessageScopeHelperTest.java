package com.cannontech.web.taglib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.cannontech.web.taglib.MessageScopeHelper.MessageScope;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

public class MessageScopeHelperTest {

    private void assertContainsInOrder(List<String> expected,
            List<String> actual) {
        if (expected.equals(actual)) return;
        if (expected.size() > actual.size()) fail("actual smaller than expected");

        Iterator<String> expectedIter = expected.iterator();
        Iterator<String> actualIter = actual.iterator();

        while (expectedIter.hasNext()) {
            String thisExpected = expectedIter.next();
            if (!Iterators.contains(actualIter, thisExpected)) {
                fail("doesn't contain: " + thisExpected);
            }
        }
    }

    @Test
    public void test_basic_module_and_page() {
        MessageScope messageScope = new MessageScopeHelper.MessageScope();

        messageScope.pushScope("modules.someModule.somePage");

        List<String> actual = messageScope.getFullKeys(".suffix", "yukon.web.");

        ImmutableList<String> expected = ImmutableList.of("yukon.web.modules.someModule.somePage.suffix");

        assertContainsInOrder(expected, actual);
    }

    @Test
    public void test_basic_widget() {
        MessageScope messageScope = new MessageScopeHelper.MessageScope();

        messageScope.pushScope("modules.someModule.somePage");
        messageScope.pushScope(".someWidget", "widgets.someWidget");

        List<String> actual = messageScope.getFullKeys(".suffix", "yukon.web.");

        ImmutableList<String> expected = ImmutableList.of("yukon.web.modules.someModule.somePage.someWidget.suffix",
                                                          "yukon.web.widgets.someWidget.suffix");

        assertContainsInOrder(expected, actual);
    }

    @Test
    public void test_basic_container() {
        MessageScope messageScope = new MessageScopeHelper.MessageScope();

        messageScope.pushScope("modules.someModule.somePage");
        messageScope.pushScope(".someContainer", "");

        List<String> actual = messageScope.getFullKeys(".suffix", "yukon.web.");

        ImmutableList<String> expected = ImmutableList.of("yukon.web.modules.someModule.somePage.someContainer.suffix",
                                                          "yukon.web.modules.someModule.somePage.suffix");

        assertContainsInOrder(expected, actual);
    }

    @Test
    public void test_basic_forced_scope() {
        MessageScope messageScope = new MessageScopeHelper.MessageScope();
        
        messageScope.pushScope("modules.someModule.somePage");
        messageScope.pushScope(".someScope");
        
        List<String> actual = messageScope.getFullKeys(".suffix", "yukon.web.");
        
        ImmutableList<String> expected = ImmutableList.of("yukon.web.modules.someModule.somePage.someScope.suffix");
        
        assertContainsInOrder(expected, actual);
    }
    
    @Test
    public void test_basic_default() {
        MessageScope messageScope = new MessageScopeHelper.MessageScope();

        messageScope.pushScope("modules.someModule.somePage");

        List<String> actual = messageScope.getFullKeys("suffix", "yukon.common.");

        ImmutableList<String> expected = ImmutableList.of("yukon.common.suffix");

        assertEquals(expected, actual);
    }

    @Test
    public void test_basic_component() {
        MessageScope messageScope = new MessageScopeHelper.MessageScope();

        messageScope.pushScope("modules.someModule.somePage");
        messageScope.pushScope(".someTag.instance", "components.someTag.instance", "components.someTag");

        List<String> actual = messageScope.getFullKeys(".suffix", "yukon.web.");

        ImmutableList<String> expected = ImmutableList.of("yukon.web.modules.someModule.somePage.someTag.instance.suffix",
                                                          "yukon.web.components.someTag.instance.suffix",
                                                          "yukon.web.components.someTag.suffix");

        assertEquals(expected, actual);
    }

    @Test
    public void test_push_pop() {
        MessageScope messageScope = new MessageScopeHelper.MessageScope();

        messageScope.pushScope("modules.someModule.somePage");
        messageScope.pushScope(".someTag.instance", "components.someTag.instance", "components.someTag");
        messageScope.popScope();
        messageScope.pushScope(".someTag2.instance", "components.someTag2.instance", "components.someTag2");

        List<String> actual = messageScope.getFullKeys(".suffix", "yukon.web.");

        ImmutableList<String> expected = ImmutableList.of("yukon.web.modules.someModule.somePage.someTag2.instance.suffix",
                                                          "yukon.web.components.someTag2.instance.suffix",
        "yukon.web.components.someTag2.suffix");

        assertEquals(expected, actual);
    }

    @Test
    public void test_basic_component_in_widget() {
        MessageScope messageScope = new MessageScopeHelper.MessageScope();

        messageScope.pushScope("modules.someModule.somePage");
        messageScope.pushScope(".someWidget", "widgets.someWidget");
        messageScope.pushScope(".someTag.instance", "components.someTag.instance", "components.someTag");

        List<String> actual = messageScope.getFullKeys(".suffix", "yukon.web.");

        ImmutableList<String> expected = ImmutableList.of("yukon.web.modules.someModule.somePage.someWidget.someTag.instance.suffix",
                                                          "yukon.web.widgets.someWidget.someTag.instance.suffix",
                                                          "yukon.web.components.someTag.instance.suffix",
                                                          "yukon.web.components.someTag.suffix");

        assertEquals(expected, actual);
    }

}

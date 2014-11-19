package util;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.web.amr.util.cronExpressionTag.handler.OneTimeCronTagStyleHandler;

public class OneTimeCronTagStyleHandlerTest {
    private OneTimeCronTagStyleHandler handler = new OneTimeCronTagStyleHandler();

    @Test
    public void test_canParse() {
        assertCanParse("0 15 10 1 1 ? 2005", true);
        assertCanParse("0 15 10 ? 1 5 2005", true);
        assertCanParse("0 15 10 ? JAN 2 2005", true);
        
        assertCanParse("0 15 10 * 1 ? 2005", false);
        assertCanParse("0 15 10 1 * ? 2005", false);
        assertCanParse("0 0/5 14,18 * * ?", false);
        assertCanParse("0 15 10 ? * MON-FRI", false);
        assertCanParse("0 15 10 ? * 6L 2002-2005", false);
        assertCanParse("0 11 11 11 11 ?", false);
        assertCanParse("0 15 10 ? * 6#3", false);
        assertCanParse("0 10,44 14 ? 3 WED", false);
        assertCanParse("0 15 10 ? * 6L", false);
    }

    public void assertCanParse(String cron, boolean canParse) {
        Assert.assertEquals("OneTimeCronTagStyleHandler.canParse(\"" + cron + "\") failed.", canParse,
            handler.canParse(cron.split(" ")));
    }
}
package com.cannontech.web.deviceConfiguration.enumeration;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.Assert;
import org.junit.BeforeClass;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MockConfigurationSource;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.impl.DurationFormattingServiceImpl;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.deviceConfiguration.enumeration.DeviceConfigurationInputEnumeration.SelectionType;
import com.cannontech.web.input.type.InputOption;

public class IntervalTest {

    private static YukonUserContext userContext;
    private static ConfigurationSource falseConfigurationSource; 
    private static ConfigurationSource trueConfigurationSource; 
    private static DurationFormattingServiceImpl durationService;

    @Test
    public void testProfileInterval() {
        ProfileInterval interval = new ProfileInterval();
        setDurationService(interval);
        
        List<InputOption> expected = new InputOptionListBuilder()
                .add( "5",  "5 mins")
                .add("15", "15 mins")
                .add("30", "30 mins")
                .add("60",  "1 hr")
                .build();
        
        assertEquals(expected, interval.getDisplayableValues(userContext));
        
        assertEquals("ProfileInterval", interval.getEnumOptionName());
        assertEquals(SelectionType.SWITCH, interval.getSelectionType());
    }

    @Test
    public void testMctDemandInterval() {
        MctDemandInterval interval = (new MctDemandInterval());
        setDurationService(interval);

        List<InputOption> expected = new InputOptionListBuilder()
                .add( "5",  "5 mins")
                .add("15", "15 mins")
                .add("30", "30 mins")
                .add("60",  "1 hr")
                .build();
        
        assertEquals(expected, interval.getDisplayableValues(userContext));
        
        assertEquals("MctDemandInterval", interval.getEnumOptionName());
        assertEquals(SelectionType.SWITCH, interval.getSelectionType());
    }

    @Test
    public void testRfnDemandInterval() {
        RfnDemandInterval interval = (new RfnDemandInterval());
        setDurationService(interval);

        List<InputOption> expected = new InputOptionListBuilder()
                .add( "1",  "1 min")
                .add( "5",  "5 mins")
                .add("15", "15 mins")
                .add("30", "30 mins")
                .add("60",  "1 hr")
                .build();
        
        assertEquals(expected, interval.getDisplayableValues(userContext));
        
        assertEquals("RfnDemandInterval", interval.getEnumOptionName());
        assertEquals(SelectionType.SWITCH, interval.getSelectionType());
    }

    @Test
    public void testDisconnectDemandInterval() {
        DisconnectDemandInterval interval = (new DisconnectDemandInterval());
        setDurationService(interval);

        List<InputOption> expected = new InputOptionListBuilder()
                .add( "5",  "5 mins")
                .add("10", "10 mins")
                .add("15", "15 mins")
                .build();
        
        assertEquals(expected, interval.getDisplayableValues(userContext));
        
        assertEquals("DisconnectDemandInterval", interval.getEnumOptionName());
        assertEquals(SelectionType.SWITCH, interval.getSelectionType());
    }

    @Test
    public void testRfnRecordingInterval() {
        RfnRecordingInterval interval = (new RfnRecordingInterval());
        setDurationService(interval);
        {
            setConfigurationSource(interval, falseConfigurationSource);
    
            List<InputOption> expected = new InputOptionListBuilder()
                    .add(  "5",  "5 minutes", false)
                    .add( "15", "15 minutes")
                    .add( "30", "30 minutes")
                    .add( "60",  "1 hour")
                    .add("120",  "2 hours")
                    .add("240",  "4 hours")
                    .build();
            
            assertEquals(expected, interval.getDisplayableValues(userContext));
        }
        {
            setConfigurationSource(interval, trueConfigurationSource);
    
            List<InputOption> expected = new InputOptionListBuilder()
                    .add(  "5",  "5 minutes")
                    .add( "15", "15 minutes")
                    .add( "30", "30 minutes")
                    .add( "60",  "1 hour")
                    .add("120",  "2 hours")
                    .add("240",  "4 hours")
                    .build();
            
            assertEquals(expected, interval.getDisplayableValues(userContext));
        }
        assertEquals("RecordingInterval", interval.getEnumOptionName());
        assertEquals(SelectionType.STANDARD, interval.getSelectionType());
    }

    @Test
    public void testRfnReportingInterval() {
        RfnReportingInterval interval = (new RfnReportingInterval());
        setDurationService(interval);
        setConfigurationSource(interval, falseConfigurationSource);
        {
        List<InputOption> expected = new InputOptionListBuilder()
                .add(   "5",  "5 minutes", false)
                .add(  "15", "15 minutes", false)
                .add(  "30", "30 minutes", false)
                .add(  "60",  "1 hour",    false)
                .add( "120",  "2 hours")
                .add( "240",  "4 hours")
                .add( "360",  "6 hours")
                .add( "720", "12 hours")
                .add("1440",  "1 day")
                .add("2880",  "2 days")
                .build();
            
            assertEquals(expected, interval.getDisplayableValues(userContext));
        }
        setConfigurationSource(interval, trueConfigurationSource);
        {
            List<InputOption> expected = new InputOptionListBuilder()
                    .add(   "5",  "5 minutes")
                    .add(  "15", "15 minutes")
                    .add(  "30", "30 minutes")
                    .add(  "60",  "1 hour")
                    .add( "120",  "2 hours")
                    .add( "240",  "4 hours")
                    .add( "360",  "6 hours")
                    .add( "720", "12 hours")
                    .add("1440",  "1 day")
                    .add("2880",  "2 days")
                    .build();
            
            assertEquals(expected, interval.getDisplayableValues(userContext));
        }
        
        assertEquals("ReportingInterval", interval.getEnumOptionName());
        assertEquals(SelectionType.STANDARD, interval.getSelectionType());
    }

    @Test
    public void testWaterRecordingInterval() {
        WaterRecordingInterval interval = (new WaterRecordingInterval());
        setDurationService(interval);

        List<InputOption> expected = new InputOptionListBuilder()
                .add(  "900", "15 minutes")
                .add( "1800", "30 minutes")
                .add( "3600",  "1 hour")
                .add( "7200",  "2 hours")
                .add("14400",  "4 hours")
                .build();
        
        assertEquals(expected, interval.getDisplayableValues(userContext));
        
        assertEquals("WaterRecordingInterval", interval.getEnumOptionName());
        assertEquals(SelectionType.STANDARD, interval.getSelectionType());
    }

    @Test
    public void testWaterReportingInterval() {
        WaterReportingInterval interval = (new WaterReportingInterval());
        setDurationService(interval);

        List<InputOption> expected = new InputOptionListBuilder()
                .add(  "7200",  "2 hours")
                .add( "14400",  "4 hours")
                .add( "21600",  "6 hours")
                .add( "43200", "12 hours")
                .add( "86400",  "1 day")
                .add("172800",  "2 days")
                .build();
        
        assertEquals(expected, interval.getDisplayableValues(userContext));
        
        assertEquals("WaterReportingInterval", interval.getEnumOptionName());
        assertEquals(SelectionType.STANDARD, interval.getSelectionType());
    }

    @Test
    public void testGasRecordingInterval() {
        GasRecordingInterval interval = (new GasRecordingInterval());
        setDurationService(interval);

        List<InputOption> expected = new InputOptionListBuilder()
                .add(  "900", "15 minutes")
                .add( "1800", "30 minutes")
                .add( "3600",  "1 hour")
                .add( "7200",  "2 hours")
                .add("14400",  "4 hours")
                .build();
        
        assertEquals(expected, interval.getDisplayableValues(userContext));
        
        assertEquals("GasRecordingInterval", interval.getEnumOptionName());
        assertEquals(SelectionType.STANDARD, interval.getSelectionType());
    }

    @Test
    public void testGasReportingInterval() {
        GasReportingInterval interval = (new GasReportingInterval());
        setDurationService(interval);

        List<InputOption> expected = new InputOptionListBuilder()
                .add(  "7200",  "2 hours")
                .add( "14400",  "4 hours")
                .add( "21600",  "6 hours")
                .add( "43200", "12 hours")
                .add( "86400",  "1 day")
                .add("172800",  "2 days")
                .build();
        
        assertEquals(expected, interval.getDisplayableValues(userContext));
        
        assertEquals("GasReportingInterval", interval.getEnumOptionName());
        assertEquals(SelectionType.STANDARD, interval.getSelectionType());
    }

    private <E extends DeviceConfigurationInputEnumeration> void setDurationService(E inputEnumeration) {
        ReflectionTestUtils.setField(inputEnumeration, "durationService", durationService);
    }

    private <E extends RfnElectricMeterChannelDataInterval> void setConfigurationSource(E rfnChannelDataInterval, ConfigurationSource configurationSource) {
        ReflectionTestUtils.setField(rfnChannelDataInterval, "configurationSource", configurationSource);
    }

    static class InputOptionListBuilder {
        List<InputOption> inputOptions = new ArrayList<>();
        
        public InputOptionListBuilder add(String value, String text) {
            inputOptions.add(new InputOption(value, text));
            return this;
        }
        
        public InputOptionListBuilder add(String value, String text, boolean enabled) {
            inputOptions.add(new InputOption(value, text, enabled));
            return this;
        }
        
        public List<InputOption> build() {
            return inputOptions;
        }
    }

    @SuppressWarnings("serial")
    @BeforeClass
    public static void setup() {
        userContext = new YukonUserContext() {

            @Override
            public Locale getLocale() {
                return Locale.US;
            }

            @Override
            public LiteYukonUser getYukonUser() {
                Assert.fail();
                return null;
            }

            @Override
            public TimeZone getTimeZone() {
                Assert.fail();
                return null;
            }

            @Override
            public DateTimeZone getJodaTimeZone() {
                return DateTimeZone.forID("Africa/Djibouti");
            }

            @Override
            public String getThemeName() {
                Assert.fail();
                return null;
            }
        };
        
        StaticMessageSource messageSource = new StaticMessageSource();
        {
            messageSource.addMessage("yukon.common.durationFormatting.pattern.DHMS_REDUCED", Locale.US, "%D_FULL% %H_FULL% %M_FULL% %S_FULL%");
            messageSource.addMessage("yukon.common.durationFormatting.pattern.DHMS_SHORT_REDUCED", Locale.US, "%D_SHORT% %H_SHORT% %M_SHORT% %S_SHORT%");
            messageSource.addMessage("yukon.common.durationFormatting.pattern.DH", Locale.US, "%D_FULL% %H_FULL%");
            messageSource.addMessage("yukon.common.durationFormatting.pattern.HMS", Locale.US, "%H_FULL% %M_FULL% %S_FULL%");
            messageSource.addMessage("yukon.common.durationFormatting.pattern.HM", Locale.US, "%H_FULL% %M_FULL%");
            messageSource.addMessage("yukon.common.durationFormatting.pattern.H", Locale.US, "%H_FULL%");
            messageSource.addMessage("yukon.common.durationFormatting.pattern.M", Locale.US, "%M_FULL%");
            messageSource.addMessage("yukon.common.durationFormatting.pattern.S", Locale.US, "%S_FULL%");
            messageSource.addMessage("yukon.common.durationFormatting.pattern.S_SHORT", Locale.US, "%S_SHORT%");
            messageSource.addMessage("yukon.common.durationFormatting.pattern.HM_SHORT", Locale.US, "%H%:%M%");

            messageSource.addMessage("yukon.common.durationFormatting.symbol.S.suffix.singular", Locale.US, "");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.S.suffix.plural", Locale.US, "");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.S_FULL.suffix.singular", Locale.US, "second");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.S_FULL.suffix.plural", Locale.US, "seconds");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.S_SHORT.suffix.singular", Locale.US, "sec");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.S_SHORT.suffix.plural", Locale.US, "secs");

            messageSource.addMessage("yukon.common.durationFormatting.symbol.M.suffix.singular", Locale.US, "");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.M.suffix.plural", Locale.US, "");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.M_FULL.suffix.singular", Locale.US, "minute");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.M_FULL.suffix.plural", Locale.US, "minutes");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.M_SHORT.suffix.singular", Locale.US, "min");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.M_SHORT.suffix.plural", Locale.US, "mins");
            
            messageSource.addMessage("yukon.common.durationFormatting.symbol.H.suffix.singular", Locale.US, "");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.H.suffix.plural", Locale.US, "");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.H_FULL.suffix.singular", Locale.US, "hour");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.H_FULL.suffix.plural", Locale.US, "hours");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.H_SHORT.suffix.singular", Locale.US, "hr");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.H_SHORT.suffix.plural", Locale.US, "hrs");
            
            messageSource.addMessage("yukon.common.durationFormatting.symbol.D.suffix.singular", Locale.US, "");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.D.suffix.plural", Locale.US, "");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.D_FULL.suffix.singular", Locale.US, "day");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.D_FULL.suffix.plural", Locale.US, "days");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.D_SHORT.suffix.singular", Locale.US, "day");
            messageSource.addMessage("yukon.common.durationFormatting.symbol.D_SHORT.suffix.plural", Locale.US, "days");
}
        class YukonUserContextMessageSourceResolverMock implements YukonUserContextMessageSourceResolver, MessageSourceAware {
        
            private MessageSource messageSource;

            @Override
            public MessageSource getMessageSource(YukonUserContext userContext) {
                return messageSource;
            }

            @Override
            public MessageSourceAccessor getMessageSourceAccessor(YukonUserContext userContext) {
                return new MessageSourceAccessor(messageSource, Locale.US);
            }

            @Override
            public void setMessageSource(MessageSource messageSource) {
                this.messageSource = messageSource;
            }
        };
        
        YukonUserContextMessageSourceResolverMock messageSourceResolver = new YukonUserContextMessageSourceResolverMock();
        {
            messageSourceResolver.setMessageSource(messageSource);
        }
        
        durationService = new DurationFormattingServiceImpl();

        durationService.init();
        durationService.setMessageSourceResolver(messageSourceResolver);
        
        falseConfigurationSource = new MockConfigurationSource() {
            @Override
            public boolean getBoolean(MasterConfigBoolean key, boolean defaultValue) {
                return false;
            }
        };
        trueConfigurationSource = new MockConfigurationSource() {
            @Override
            public boolean getBoolean(MasterConfigBoolean key, boolean defaultValue) {
                return true;
            }
        };
    }
}

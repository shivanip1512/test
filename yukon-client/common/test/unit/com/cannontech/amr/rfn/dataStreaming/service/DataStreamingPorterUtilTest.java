package com.cannontech.amr.rfn.dataStreaming.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.cannontech.common.rfn.dataStreaming.DataStreamingMetricStatus;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingAttribute;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;

public class DataStreamingPorterUtilTest {

    @Test
    public void test_empty() throws IOException {
        String empty = "";
        ReportedDataStreamingConfig config = DataStreamingPorterUtil.extractReportedDataStreamingConfig(empty);
        assertNull(config);
    }

    @Test
    public void test_invalid() throws IOException {
        String invalid = "jimmy";
        ReportedDataStreamingConfig config = DataStreamingPorterUtil.extractReportedDataStreamingConfig(invalid);
        assertNull(config);
    }
        
    @Test
    public void test_valid() throws IOException {
        String valid = String.join("\r\n", 
                "Data Streaming Set Metrics Request:", 
                "DATA_STREAMING_JSON{", 
                "\"streamingEnabled\" : false,", 
                "\"configuredMetrics\" : [", 
                "  {", 
                "    \"attribute\" : \"DELIVERED_DEMAND\",", 
                "    \"interval\" : 5,", 
                "    \"enabled\" : true,", 
                "    \"status\" : \"OK\"", 
                "  },", 
                "  {",
                "    \"attribute\" : \"VOLTAGE\",", 
                "    \"interval\" : 15,", 
                "    \"enabled\" : false,", 
                "    \"status\" : \"CHANNEL_NOT_ENABLED\"", 
                "  },", 
                "  {",
                "    \"attribute\" : \"POWER_FACTOR\",", 
                "    \"interval\" : 30,", 
                "    \"enabled\" : true,", 
                "    \"status\" : \"OK\"", 
                "  }],", 
                "\"sequence\" : 3735928559", 
                "}");
            
        ReportedDataStreamingConfig config = DataStreamingPorterUtil.extractReportedDataStreamingConfig(valid);
        
        assertNotNull(config);
        assertFalse(config.isStreamingEnabled());
        assertEquals(config.getSequence(), 3735928559L);
        
        List<ReportedDataStreamingAttribute> metrics = config.getConfiguredMetrics();
        
        assertNotNull(metrics);
        assertEquals(metrics.size(), 3);
        
        Iterator<ReportedDataStreamingAttribute> itr = metrics.iterator();
        {
            ReportedDataStreamingAttribute attr = itr.next();
            
            assertEquals(attr.getAttribute(), "DELIVERED_DEMAND");
            assertEquals(attr.getInterval(), 5);
            assertEquals(attr.getStatus(), DataStreamingMetricStatus.OK);
            assertTrue(attr.isEnabled());
        }
        {
            ReportedDataStreamingAttribute attr = itr.next();
            
            assertEquals(attr.getAttribute(), "VOLTAGE");
            assertEquals(attr.getInterval(), 15);
            assertEquals(attr.getStatus(), DataStreamingMetricStatus.CHANNEL_NOT_ENABLED);
            assertFalse(attr.isEnabled());
        }
        {
            ReportedDataStreamingAttribute attr = itr.next();
            
            assertEquals(attr.getAttribute(), "POWER_FACTOR");
            assertEquals(attr.getInterval(), 30);
            assertEquals(attr.getStatus(), DataStreamingMetricStatus.OK);
            assertTrue(attr.isEnabled());
        }
    }

    @Test
    public void test_trailing() throws IOException {
        String valid = String.join("\r\n",
                "Data Streaming Set Metrics Request:", 
                "DATA_STREAMING_JSON{", 
                "\"streamingEnabled\" : false,", 
                "\"configuredMetrics\" : [", 
                "  {", 
                "    \"attribute\" : \"DELIVERED_DEMAND\",", 
                "    \"interval\" : 5,", 
                "    \"enabled\" : true,", 
                "    \"status\" : \"OK\"", 
                "  },", 
                "  {", 
                "    \"attribute\" : \"VOLTAGE\",", 
                "    \"interval\" : 15,", 
                "    \"enabled\" : false,", 
                "    \"status\" : \"CHANNEL_NOT_ENABLED\"", 
                "  },", 
                "  {", 
                "    \"attribute\" : \"POWER_FACTOR\",", 
                "    \"interval\" : 30,", 
                "    \"enabled\" : true,", 
                "    \"status\" : \"OK\"", 
                "  }],", 
                "\"sequence\" : 3735928559", 
                "}trailing nonsense{} this[] could{ break } /*things*/");
            
        ReportedDataStreamingConfig config = DataStreamingPorterUtil.extractReportedDataStreamingConfig(valid);
        
        assertNotNull(config);
        assertFalse(config.isStreamingEnabled());
        assertEquals(config.getSequence(), 3735928559L);
        
        List<ReportedDataStreamingAttribute> metrics = config.getConfiguredMetrics();
        
        assertNotNull(metrics);
        assertEquals(metrics.size(), 3);
        
        Iterator<ReportedDataStreamingAttribute> itr = metrics.iterator();
        {
            ReportedDataStreamingAttribute attr = itr.next();
            
            assertEquals(attr.getAttribute(), "DELIVERED_DEMAND");
            assertEquals(attr.getInterval(), 5);
            assertEquals(attr.getStatus(), DataStreamingMetricStatus.OK);
            assertTrue(attr.isEnabled());
        }
        {
            ReportedDataStreamingAttribute attr = itr.next();
            
            assertEquals(attr.getAttribute(), "VOLTAGE");
            assertEquals(attr.getInterval(), 15);
            assertEquals(attr.getStatus(), DataStreamingMetricStatus.CHANNEL_NOT_ENABLED);
            assertFalse(attr.isEnabled());
        }
        {
            ReportedDataStreamingAttribute attr = itr.next();
            
            assertEquals(attr.getAttribute(), "POWER_FACTOR");
            assertEquals(attr.getInterval(), 30);
            assertEquals(attr.getStatus(), DataStreamingMetricStatus.OK);
            assertTrue(attr.isEnabled());
        }
    }
}

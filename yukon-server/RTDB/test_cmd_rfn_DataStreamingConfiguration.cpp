#include "cmd_rfn_DataStreamingConfiguration.h"

#include "ctidate.h"
#include "std_helper.h"
#include "boost_test_helpers.h"

#include <boost/test/unit_test.hpp>

using namespace Cti::Devices::Commands;

BOOST_AUTO_TEST_SUITE(test_cmd_rfn_ChannelConfiguration)

static const CtiTime execute_time{ CtiDate{ 24, 6, 2016 }, 12, 34, 56 };

BOOST_AUTO_TEST_CASE(test_RfnDataStreamingGetMetricsListCommand)
{
    RfnDataStreamingGetMetricsListCommand cmd;

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand(execute_time);
        RfnCommand::RfnRequestPayload exp{ 
            0x84,  //  command code
        };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        const std::vector<unsigned char> response{
        0x85,       //  command code
            0x03,  //  number of metrics
            0x01,  //  data streaming on/off
            0x00, 0x05,  //  metric ID 1
            0x01,          //  metric ID 1 enable/disable
            0x05,          //  metric ID 1 interval
            0x00,          //  metric ID 1 status
            0x00, 0x73,  //  metric ID 2
            0x00,          //  metric ID 2 enable/disable
            0x0f,          //  metric ID 2 interval
            0x01,          //  metric ID 2 status
            0x00, 0x53,  //  metric ID 3
            0x01,          //  metric ID 3 enable/disable
            0x1e,          //  metric ID 3 interval
            0x02,          //  metric ID 3 status
            0xde, 0xad, 0xbe, 0xef };  //  DS metrics sequence number

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
R"json({
"streamingEnabled" : true,
"configuredMetrics" : [
  {
    "attribute" : "DEMAND",
    "interval" : 5,
    "enabled" : true,
    "status" : "OK"
  },
  {
    "attribute" : "VOLTAGE",
    "interval" : 15,
    "enabled" : false,
    "status" : "METER_ACCESS_ERROR"
  },
  {
    "attribute" : "POWER_FACTOR",
    "interval" : 30,
    "enabled" : true,
    "status" : "METER_OR_NODE_BUSY"
  }],
"sequence" : 3735928559
})json";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);
    }
}


BOOST_AUTO_TEST_CASE(test_RfnDataStreamingGetMetricsListCommand_globally_disabled)
{
    RfnDataStreamingGetMetricsListCommand cmd;

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand(execute_time);
        RfnCommand::RfnRequestPayload exp{
            0x84,  //  command code
        };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        const std::vector<unsigned char> response{
            0x85,       //  command code
            0x03,  //  number of metrics
            0x00,  //  data streaming on/off
            0x00, 0x05,  //  metric ID 1
            0x01,        //  metric ID 1 enable/disable
            0x05,        //  metric ID 1 interval
            0x03,        //  metric ID 1 status
            0x00, 0x73,  //  metric ID 2
            0x00,        //  metric ID 2 enable/disable
            0x0f,        //  metric ID 2 interval
            0x04,        //  metric ID 2 status
            0x00, 0x53,  //  metric ID 3
            0x01,        //  metric ID 3 enable/disable
            0x1e,        //  metric ID 3 interval
            0x05,        //  metric ID 3 status
            0xde, 0xad, 0xbe, 0xef };  //  DS metrics sequence number

        try
        {
            RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);
            BOOST_FAIL("Did not throw");
        }
        catch( const Cti::YukonErrorException& e )
        {
            const std::string desc_exp =
R"json({
"streamingEnabled" : false,
"configuredMetrics" : [
  {
    "attribute" : "DEMAND",
    "interval" : 5,
    "enabled" : true,
    "status" : "METER_READ_TIMEOUT"
  },
  {
    "attribute" : "VOLTAGE",
    "interval" : 15,
    "enabled" : false,
    "status" : "METER_PROTOCOL_ERROR"
  },
  {
    "attribute" : "POWER_FACTOR",
    "interval" : 30,
    "enabled" : true,
    "status" : "CHANNEL_NOT_SUPPORTED"
  }],
"sequence" : 3735928559
})json";

            BOOST_CHECK_EQUAL(e.error_description, desc_exp);
        }
    }
}


BOOST_AUTO_TEST_CASE(test_RfnDataStreamingGetMetricsListCommand_invalid_metric_id)
{
    RfnDataStreamingGetMetricsListCommand cmd;

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand(execute_time);
        RfnCommand::RfnRequestPayload exp{ 
            0x84,  //  command code
        };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        const std::vector<unsigned char> response{
            0x85,       //  command code
            0x03,       //  number of metrics
            0x01,       //  data streaming on/off
            0xff, 0xff,  //  metric ID 1  //  Invalid metric ID (65535), so this metric will be excluded entirely
            0x01,        //  metric ID 1 enable/disable
            0x05,        //  metric ID 1 interval
            0x06,        //  metric ID 1 status
            0x00, 0x73,  //  metric ID 2
            0x00,        //  metric ID 2 enable/disable
            0x0f,        //  metric ID 2 interval
            0x07,        //  metric ID 2 status
            0x00, 0x53,  //  metric ID 3
            0x01,        //  metric ID 3 enable/disable
            0x1e,        //  metric ID 3 interval
            0x08,        //  metric ID 3 status
            0xde, 0xad, 0xbe, 0xef };  //  DS metrics sequence number

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp = 
R"json({
"streamingEnabled" : true,
"configuredMetrics" : [
  {
    "attribute" : "VOLTAGE",
    "interval" : 15,
    "enabled" : false,
    "status" : "CHANNEL_NOT_ENABLED"
  },
  {
    "attribute" : "POWER_FACTOR",
    "interval" : 30,
    "enabled" : true,
    "status" : "INVALID_ERROR_8"
  }],
"sequence" : 3735928559
})json";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);
    }
}


BOOST_AUTO_TEST_CASE(test_RfnDataStreamingSetMetricsCommand_global_enable)
{
    RfnDataStreamingSetMetricsCommand cmd{ RfnDataStreamingConfigurationCommand::StreamingEnabled };

    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand(execute_time);
        RfnCommand::RfnRequestPayload exp{
            0x86,  //  command code
            0x00,  //  number of metrics
            0x01 };//  data streaming ON

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        const std::vector<unsigned char> response{
            0x87,       //  command code
            0x03,  //  number of metrics
            0x01,  //  data streaming on/off
            0x00, 0x05,  //  metric ID 1
            0x01,        //  metric ID 1 enable/disable
            0x05,        //  metric ID 1 interval
            0x00,        //  metric ID 1 status
            0x00, 0x73,  //  metric ID 2
            0x00,        //  metric ID 2 enable/disable
            0x0f,        //  metric ID 2 interval
            0x00,        //  metric ID 2 status
            0x00, 0x53,  //  metric ID 3
            0x01,        //  metric ID 3 enable/disable
            0x1e,        //  metric ID 3 interval
            0x00,        //  metric ID 3 status
            0xde, 0xad, 0xbe, 0xef };  //  DS metrics sequence number

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
R"json({
"streamingEnabled" : true,
"configuredMetrics" : [
  {
    "attribute" : "DEMAND",
    "interval" : 5,
    "enabled" : true,
    "status" : "OK"
  },
  {
    "attribute" : "VOLTAGE",
    "interval" : 15,
    "enabled" : false,
    "status" : "OK"
  },
  {
    "attribute" : "POWER_FACTOR",
    "interval" : 30,
    "enabled" : true,
    "status" : "OK"
  }],
"sequence" : 3735928559
})json";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);
    }
}

BOOST_AUTO_TEST_CASE(test_RfnDataStreamingSetMetricsCommand_global_disable)
{
    RfnDataStreamingSetMetricsCommand cmd{ RfnDataStreamingConfigurationCommand::StreamingDisabled };

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand(execute_time);
        RfnCommand::RfnRequestPayload exp{
            0x86,  //  command code
            0x00,  //  number of metrics
            0x00 };//  data streaming OFF

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        const std::vector<unsigned char> response{
            0x87,       //  command code
            0x03,  //  number of metrics
            0x00,  //  data streaming on/off
            0x00, 0x05,  //  metric ID 1
            0x01,        //  metric ID 1 enable/disable
            0x05,        //  metric ID 1 interval
            0x00,        //  metric ID 1 status
            0x00, 0x73,  //  metric ID 2
            0x00,        //  metric ID 2 enable/disable
            0x0f,        //  metric ID 2 interval
            0x00,        //  metric ID 2 status
            0x00, 0x53,  //  metric ID 3
            0x01,        //  metric ID 3 enable/disable
            0x1e,        //  metric ID 3 interval
            0x00,        //  metric ID 3 status
            0xde, 0xad, 0xbe, 0xef };  //  DS metrics sequence number

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
R"json({
"streamingEnabled" : false,
"configuredMetrics" : [
  {
    "attribute" : "DEMAND",
    "interval" : 5,
    "enabled" : true,
    "status" : "OK"
  },
  {
    "attribute" : "VOLTAGE",
    "interval" : 15,
    "enabled" : false,
    "status" : "OK"
  },
  {
    "attribute" : "POWER_FACTOR",
    "interval" : 30,
    "enabled" : true,
    "status" : "OK"
  }],
"sequence" : 3735928559
})json";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);
    }
}


BOOST_AUTO_TEST_CASE(test_RfnDataStreamingSetMetricsCommand_enable_one)
{
    using MetricState = RfnDataStreamingSetMetricsCommand::MetricState;

    RfnDataStreamingSetMetricsCommand cmd{ { MetricState{ 5, 5 } } };

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand(execute_time);
        RfnCommand::RfnRequestPayload exp{
            0x86,  //  command code
            0x01,  //  number of metrics
            0x01,  //  data streaming ON
            0x00, 0x05,  //  metric ID 1
            0x01,        //  metric ID 1 enable/disable
            0x05         //  metric ID 1 interval
        };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        const std::vector<unsigned char> response{
            0x87,  //  command code
            0x01,  //  number of metrics
            0x01,  //  data streaming on/off
            0x00, 0x05,  //  metric ID 1
            0x01,        //  metric ID 1 enable/disable
            0x05,        //  metric ID 1 interval
            0x00,        //  metric ID 1 status
            0xde, 0xad, 0xbe, 0xef };  //  DS metrics sequence number

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
R"json({
"streamingEnabled" : true,
"configuredMetrics" : [
  {
    "attribute" : "DEMAND",
    "interval" : 5,
    "enabled" : true,
    "status" : "OK"
  }],
"sequence" : 3735928559
})json";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);
    }
}


BOOST_AUTO_TEST_CASE(test_RfnDataStreamingSetMetricsCommand_disable_one)
{
    using MetricState = RfnDataStreamingSetMetricsCommand::MetricState;

    RfnDataStreamingSetMetricsCommand cmd{ { MetricState{ 83, 0 } } };

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand(execute_time);
        RfnCommand::RfnRequestPayload exp{
            0x86,  //  command code
            0x01,  //  number of metrics
            0x01,  //  data streaming ON
            0x00, 0x53,  //  metric ID 1
            0x00,        //  metric ID 1 enable/disable
            0x1e         //  metric ID 1 interval
        };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        const std::vector<unsigned char> response{
            0x87,  //  command code
            0x01,  //  number of metrics
            0x01,  //  data streaming on/off
            0x00, 0x53,  //  metric ID 1
            0x00,        //  metric ID 1 enable/disable
            0x1e,        //  metric ID 1 interval
            0x00,        //  metric ID 1 status
            0xde, 0xad, 0xbe, 0xef };  //  DS metrics sequence number

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
R"json({
"streamingEnabled" : true,
"configuredMetrics" : [
  {
    "attribute" : "POWER_FACTOR",
    "interval" : 30,
    "enabled" : false,
    "status" : "OK"
  }],
"sequence" : 3735928559
})json";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);
    }
}


BOOST_AUTO_TEST_CASE(test_RfnDataStreamingSetMetricsCommand_enable_two)
{
    using MetricState = RfnDataStreamingSetMetricsCommand::MetricState;

    RfnDataStreamingSetMetricsCommand cmd{ { 
            MetricState{ 5, 5 }, 
            MetricState{ 83, 15 } } };

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand(execute_time);
        RfnCommand::RfnRequestPayload exp{
            0x86,  //  command code
            0x02,  //  number of metrics
            0x01,  //  data streaming ON
            0x00, 0x05,  //  metric ID 1
            0x01,        //  metric ID 1 enable/disable
            0x05,        //  metric ID 1 interval
            0x00, 0x53,  //  metric ID 2
            0x01,        //  metric ID 2 enable/disable
            0x0f         //  metric ID 2 interval
        };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }
}


BOOST_AUTO_TEST_CASE(test_RfnDataStreamingSetMetricsCommand_enable_one_disable_one)
{
    using MetricState = RfnDataStreamingSetMetricsCommand::MetricState;

    RfnDataStreamingSetMetricsCommand cmd{ { 
            MetricState{ 5, 5 }, 
            MetricState{ 83, 0 } } };

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand(execute_time);
        RfnCommand::RfnRequestPayload exp{
            0x86,  //  command code
            0x02,  //  number of metrics
            0x01,  //  data streaming ON
            0x00, 0x05,  //  metric ID 1
            0x01,        //  metric ID 1 enable/disable
            0x05,        //  metric ID 1 interval
            0x00, 0x53,  //  metric ID 2
            0x00,        //  metric ID 2 enable/disable
            0x1e         //  metric ID 2 interval
        };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }
}


BOOST_AUTO_TEST_SUITE_END()

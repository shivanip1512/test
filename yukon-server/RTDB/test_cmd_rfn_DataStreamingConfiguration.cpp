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
		0x85,	   //  command code
			0x03,  //  number of metrics
			0x01,  //  data streaming on/off
			0x00, 0x05,  //  metric ID 1
			0x01,	     //  metric ID 1 enable/disable
			0x05,        //  metric ID 1 interval
			0x00, 0x73,  //  metric ID 2
			0x00,	     //  metric ID 2 enable/disable
			0x0f,        //  metric ID 2 interval
			0x00, 0x53,  //  metric ID 3
			0x01,	     //  metric ID 3 enable/disable
			0x1e,        //  metric ID 3 interval
			0xde, 0xad, 0xbe, 0xef };  //  DS metrics sequence number

		RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

		const std::string desc_exp =
			"Data Streaming metrics:"
			"\n3 metrics configured"
			"\nMetric 1: DEMAND, 5 min"
			"\nMetric 2: POWER_FACTOR, 15 min, disabled individually"
			"\nMetric 3: VOLTAGE, 30 min"
			"\nSequence: 3735928559";

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
            0x85,	   //  command code
            0x03,  //  number of metrics
            0x00,  //  data streaming on/off
            0x00, 0x05,  //  metric ID 1
            0x01,	     //  metric ID 1 enable/disable
            0x05,        //  metric ID 1 interval
            0x00, 0x73,  //  metric ID 2
            0x00,	     //  metric ID 2 enable/disable
            0x0f,        //  metric ID 2 interval
            0x00, 0x53,  //  metric ID 3
            0x01,	     //  metric ID 3 enable/disable
            0x1e,        //  metric ID 3 interval
            0xde, 0xad, 0xbe, 0xef };  //  DS metrics sequence number

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
            "Data Streaming metrics:"
            "\nData streaming DISABLED"
            "\n3 metrics configured"
            "\nMetric 1: DEMAND, 5 min, disabled globally"
            "\nMetric 2: POWER_FACTOR, 15 min, disabled individually"
            "\nMetric 3: VOLTAGE, 30 min, disabled globally"
            "\nSequence: 3735928559";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);
    }
}


BOOST_AUTO_TEST_CASE(test_RfnDataStreamingSetMetricsCommand_global_enable)
{
    RfnDataStreamingSetMetricsCommand cmd{ RfnDataStreamingSetMetricsCommand::StreamingEnabled };

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
            0x87,	   //  command code
            0x03,  //  number of metrics
            0x01,  //  data streaming on/off
            0x00, 0x05,  //  metric ID 1
            0x01,	     //  metric ID 1 enable/disable
            0x05,        //  metric ID 1 interval
            0x00, 0x73,  //  metric ID 2
            0x00,	     //  metric ID 2 enable/disable
            0x0f,        //  metric ID 2 interval
            0x00, 0x53,  //  metric ID 3
            0x01,	     //  metric ID 3 enable/disable
            0x1e,        //  metric ID 3 interval
            0xde, 0xad, 0xbe, 0xef };  //  DS metrics sequence number

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
            "Data Streaming metrics:"
            "\n2 metrics configured"
            "\nMetric 1: DEMAND, 5 min"
            "\nMetric 2: POWER_FACTOR, 15 min, disabled individually"
            "\nSequence: 3735928559";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);
    }
}

BOOST_AUTO_TEST_CASE(test_RfnDataStreamingSetMetricsCommand_global_disable)
{
    RfnDataStreamingSetMetricsCommand cmd{ RfnDataStreamingSetMetricsCommand::StreamingDisabled };

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
            0x87,	   //  command code
            0x03,  //  number of metrics
            0x00,  //  data streaming on/off
            0x00, 0x05,  //  metric ID 1
            0x01,	     //  metric ID 1 enable/disable
            0x05,        //  metric ID 1 interval
            0x00, 0x73,  //  metric ID 2
            0x00,	     //  metric ID 2 enable/disable
            0x0f,        //  metric ID 2 interval
            0x00, 0x53,  //  metric ID 3
            0x01,	     //  metric ID 3 enable/disable
            0x1e,        //  metric ID 3 interval
            0xde, 0xad, 0xbe, 0xef };  //  DS metrics sequence number

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
            "Data Streaming metrics:"
            "\nData streaming DISABLED"
            "\n2 metrics configured"
            "\nMetric 1: DEMAND, 5 min, disabled globally"
            "\nMetric 2: POWER_FACTOR, 15 min, disabled individually"
            "\nSequence: 3735928559";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);
    }
}


BOOST_AUTO_TEST_CASE(test_RfnDataStreamingSetMetricsCommand_enable_one)
{
    using MetricState = RfnDataStreamingSetMetricsCommand::MetricState;

    RfnDataStreamingSetMetricsCommand cmd{ { MetricState{ 99, 17, MetricState::Enabled } } };

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand(execute_time);
        RfnCommand::RfnRequestPayload exp{
            0x86,  //  command code
            0x01,  //  number of metrics
            0x01,  //  data streaming ON
            0x00, 0x05,  //  metric ID 1
            0x01,	     //  metric ID 1 enable/disable
            0x05         //  metric ID 1 interval
        };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        const std::vector<unsigned char> response{
            0x87,	   //  command code
            0x01,  //  number of metrics
            0x01,  //  data streaming on/off
            0x00, 0x05,  //  metric ID 1
            0x01,	     //  metric ID 1 enable/disable
            0x05,        //  metric ID 1 interval
            0xde, 0xad, 0xbe, 0xef };  //  DS metrics sequence number

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
            "Data Streaming metrics:"
            "\n1 metric configured"
            "\nMetric 1: DEMAND, 5 min"
            "\nSequence: 3735928559";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);
    }
}


BOOST_AUTO_TEST_CASE(test_RfnDataStreamingSetMetricsCommand_disable_one)
{
    using MetricState = RfnDataStreamingSetMetricsCommand::MetricState;

    RfnDataStreamingSetMetricsCommand cmd{ { MetricState{ 99, 17, MetricState::Disabled } } };

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand(execute_time);
        RfnCommand::RfnRequestPayload exp{
            0x86,  //  command code
            0x01,  //  number of metrics
            0x01,  //  data streaming ON
            0x00, 0x53,  //  metric ID 1
            0x00,	     //  metric ID 1 enable/disable
            0x0f         //  metric ID 1 interval
        };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        const std::vector<unsigned char> response{
            0x87,	   //  command code
            0x01,  //  number of metrics
            0x01,  //  data streaming on/off
            0x00, 0x05,  //  metric ID 1
            0x00,	     //  metric ID 1 enable/disable
            0x05,        //  metric ID 1 interval
            0xde, 0xad, 0xbe, 0xef };  //  DS metrics sequence number

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        const std::string desc_exp =
            "Data Streaming metrics:"
            "\n1 metric configured"
            "\nMetric 1: DEMAND, 5 min, disabled individually"
            "\nSequence: 3735928559";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);
    }
}


BOOST_AUTO_TEST_CASE(test_RfnDataStreamingSetMetricsCommand_enable_two)
{
    using MetricState = RfnDataStreamingSetMetricsCommand::MetricState;

    RfnDataStreamingSetMetricsCommand cmd{ { MetricState{ 99, 17, MetricState::Enabled }, MetricState{ 99, 17, MetricState::Enabled } } };

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand(execute_time);
        RfnCommand::RfnRequestPayload exp{
            0x86,  //  command code
            0x02,  //  number of metrics
            0x01,  //  data streaming ON
            0x00, 0x05,  //  metric ID 1
            0x01,	     //  metric ID 1 enable/disable
            0x05,        //  metric ID 1 interval
            0x00, 0x53,  //  metric ID 2
            0x01,	     //  metric ID 2 enable/disable
            0x0f         //  metric ID 2 interval
        };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }
}


BOOST_AUTO_TEST_CASE(test_RfnDataStreamingSetMetricsCommand_enable_one_disable_one)
{
    using MetricState = RfnDataStreamingSetMetricsCommand::MetricState;

    RfnDataStreamingSetMetricsCommand cmd{ { MetricState{ 99, 17, MetricState::Enabled }, MetricState{ 99, 17, MetricState::Disabled } } };

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand(execute_time);
        RfnCommand::RfnRequestPayload exp{
            0x86,  //  command code
            0x02,  //  number of metrics
            0x01,  //  data streaming ON
            0x00, 0x05,  //  metric ID 1
            0x01,	     //  metric ID 1 enable/disable
            0x05,        //  metric ID 1 interval
            0x00, 0x53,  //  metric ID 2
            0x00,	     //  metric ID 2 enable/disable
            0x0f         //  metric ID 2 interval
        };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }
}


BOOST_AUTO_TEST_SUITE_END()

#include <dsm2err.h>

#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

BOOST_AUTO_TEST_SUITE( test_error )

//  Used by test_GetError, test_GetErrorString, and test_FormatError
const std::vector<std::string> ErrorStrings = boost::assign::list_of
    //  0
    ("Normal (Success) Return")
    ("Not Normal (Unsuccessful) Return")
    ("No D word")
    ("Bad Message Type")
    ("D Word Wrong length")
    ("Bad Load Specification")
    ("Bad Time Specification")
    ("Bad Level Specification")
    ("Bad ID Specification")
    ("Parameter out of Range")
    //  10
    ("Missing Parameter")
    ("Syntax Error")
    ("Bad Latch Control Specification")
    ("Feature Not Implemented")
    ("Bad State Specification")
    ("Parity Error")
    ("Bad CCU Specification")
    ("Word 1 NACK")
    ("Word 2 NACK")
    ("Word 3 NACK")
    //  20
    ("Word 1 NACK Padded")
    ("Word 2 NACK Padded")
    ("Word 3 NACK Padded")
    ("Bad CCU Type")
    ("Bad Repeat Count Specification")
    ("Bad Pause Interval Specification")
    ("Bad Parameter")
    ("Bad Route Specification")
    ("Bad Bus Specification")
    ("Bad Amp Specification")
    //  30
    ("Read Error")
    ("Timeout Reading from Port")
    ("Sequence Reject Frame Received... Sequencing Adjusted")
    ("Framing Error")
    ("Bad CRC on Message")
    ("Bad Length Specification")
    ("Bad HDLC UA Frame")
    ("Unknown Error")
    ("Bad Unique Repeater Address")
    ("Bad Repeater Role Number")
    //  40
    ("Invalid Repeater Fixed Number")
    ("Invalid Repeater Out Value")
    ("Invalid Repeater In Value")
    ("Invalid Repeater Stages")
    ("Error table entry not defined")
    ("Bad or Missing File")
    ("REQACK Flag set-- Frame Unexecutable")
    ("Route File Error")
    ("No Time Routes Found")
    ("Route Not Found")
    //  50
    ("File Not Open")
    ("Role Not Found")
    ("Role File Error")
    ("DataBase File Error")
    ("ID Not Found")
    ("Type File Error")
    ("Function and/or Type Not Found")
    ("E-Word Received in Returned Message")
    ("Error Filling Fill Area of Command")
    ("OS or System Error")
    //  60
    ("Bad Port Specification")
    ("Error Reading Queue")
    ("Error Writing Queue")
    ("Error Allocating or Manipulating Memory")
    ("Error Handling Semaphore")
    ("No DCD on Return Message")
    ("Timeout Writing to Port")
    ("Error Reading from Port")
    ("Error Writing to Port")
    ("Error Writing to Named Pipe")
    //  70
    ("Error Reading from Named Pipe")
    ("Error Executing CCU Queue Entry")
    ("DLC Read Timeout on CCU Queue Entry")
    ("No Attempt Made on CCU Queue Entry")
    ("Route Failed on CCU Queue Entry")
    ("Transponder Communication Failed on CCU Queue Entry")
    ("J-Word Received in Returned Message")
    ("Remote Porter Can Not be Reached")
    ("Communications Attempted With Inhibited Remote")
    ("CCU Queue was Flushed... Entries Lost in Drain")
    //  80
    ("Pipe Connect is Broken")
    ("Pipe Connect was Broken")
    ("Pipe Not Opened")
    ("Communications Attempted Over Inhibited Port")
    ("Device Does Not Support Accumulators")
    ("Operation Attempted on Inhibited Device")
    ("Operation Attempted on Inhibited Point")
    ("Error Dialing Up Remote")
    ("Wrong Unique Address Received")
    ("Error Connecting to TCP socket")
    //  90
    ("Error Writing to TCP socket")
    ("Error Reading from TCP socket")
    ("Address Does Not Match Expected Value")
    ("Bad Data Buffer for IED")
    ("Missing Required Configuration Entry")
    ("Error table entry not defined")
    ("Error table entry not defined")
    ("Error table entry not defined")
    ("Bad Nexus Specification")
    ("Error Writing to Nexus")
    //  100
    ("Bad BCH")
    .repeat(99, "Unknown Error")
    //  200
    ("Yukon Base Error")
    ("Memory Error")
    ("No Method")
    ("No Refresh Method")
    ("No General Scan Method")
    ("No Integrity Scan Method")
    ("No Accum Scan Method")
    ("Unknown Error")
    ("No Process Result Method")
    ("No Exec. Req. Method")
    //  210
    ("No Result Decode Method")
    ("No ErrorDecode Method")
    ("No Handshake Method")
    ("No Generate Command Method")
    ("No DecodeResponse Method")
    ("No Data Copy Method")
    ("Unknown Error")
    ("Not Numeric")
    ("No Config Data Found")
    ("Unknown Error")
    //  220
    ("Unknown Error")
    ("No Route for Group Dev.")
    ("No Routes for Macro Rte")
    ("Macro Offset does not exist in Macro Rte")
    ("Macro Offset refers to a macro sub-rte")
    ("Device is control disabled")
    ("Point is control disabled")
    ("Control Completed")
    ("Requested operation expired due to time")
    ("Error Reading Nexus")
    //  230
    ("CtiConnection: InThread Terminated")
    ("CtiConnection: OutThread Terminated")
    ("CtiConnection: Inbound Socket Bad")
    ("CtiConnection: Outbound Socket Bad")
    ("Retry Resubmitted")
    ("Unknown Error")
    ("Unknown Error")
    ("Scanned device is inhibited")
    ("Illegal scan of global device")
    ("Device window is closed")
    //  240
    ("Port Init Failed")
    ("Dialup connection failed. Port in error")
    ("Dialup connection failed. Device in error")
    ("Port is simulated, no inbound data available")
    ("Port echoed request bytes")
    ("Invalid transaction, typ. bad pager id or password")
    ("TAP Repeat Requested, but retries exhausted")
    ("No response from TAP terminal")
    ("Invalid/Incomplete Request")
    ("Unknown Error")
    //  250
    ("Invalid or unsuccessful HTTP response")
    ("XML parser initialization failed")
    ("Invalid WCTP response format")
    ("Time out when receiving WCTP response")
    ("Protocol Error 300 Series")
    ("Protocol Error 400 Series")
    ("Protocol Error 500 Series")
    ("Protocol Error 600 Series")
    ("Queue purged to limit memory usage")
    ("Device has not reported in, outbound IP unknown")
    //  260
    ("MACS timed out on this message")
    ("The freeze check bit in the frozen reading does not match the last recorded freeze sent to the device.")
    ("The frozen peak timestamp is outside of the expected range.")
    ("The freeze counter is less than the expected value.")
    ("Invalid data was was received for one or more data points.")
    ("There is no record of the last freeze sent to this device.")
    ("Request was cancelled")
    ("Invalid time returned OR time outside of requested range.")
    ("Invalid channel returned by daily read.")
    ("Insufficient SSPEC/Firmware Revision")
    //  270
    ("Verify SSPEC/Firmware Revision")
    ("Transmitter is busy")
    ("Device Not Supported")
    ("Port not initialized")
    ("Command already in progress")
    ("Device is not connected")
    ("No disconnect configured on this device")
    ("Transmitter is overheating")
    ("Command needs channel config to continue.")
    ("Command requires a valid date.")
    //  280
    ("Failed to resolve an IP for the given DNS name.")
    ("Failed to find a point for the given device.")
    .repeat(68, "Unknown Error");

BOOST_AUTO_TEST_CASE(test_GetError)
{
    std::vector<std::string> results;

    for( int i = 0; i < 350; i++ )
    {
        results.push_back(GetError(i));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        ErrorStrings.begin(), ErrorStrings.end(),
        results.begin(), results.end());
}

BOOST_AUTO_TEST_CASE(test_GetErrorString)
{
    std::vector<std::string> results;

    for( int i = 0; i < 350; i++ )
    {
        char e[200];
        char sentinel = 1;

        GetErrorString(i, e);

        BOOST_REQUIRE_EQUAL(sentinel, 1);  //  make sure we didn't overflow our bounds

        results.push_back(e);
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        ErrorStrings.begin(), ErrorStrings.end(),
        results.begin(), results.end());
}

BOOST_AUTO_TEST_CASE(test_FormatError)
{
    std::vector<std::string> results;

    for( int i = 0; i < 350; i++ )
    {
        results.push_back(FormatError(i));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        ErrorStrings.begin(), ErrorStrings.end(),
        results.begin(), results.end());
}

BOOST_AUTO_TEST_CASE(test_GetErrorType)
{
    const int _ = 1;

    const int expected[] =
    {
        0, _, 2, 2, 2, _, _, _, _, _,
        _, _, _, _, _, 3, 3, 2, 2, 2,
        2, 2, 2, _, _, _, _, _, _, _,
        _, 3, 3, 3, 3, _, 3, _, _, _,
        _, _, _, _, 0, _, 3, _, _, _,
        _, _, _, _, _, _, _, 2, _, _,
        _, _, _, _, _, 3, 3, 3, 3, _,
        _, 2, 2, _, 2, 2, 2, _, _, _,
        _, _, _, _, _, _, _, 3, 3, 3,
        3, 3, _, _, _, 0, 0, 0, _, _,
        //  100
        2, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        //  200
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, 3, _, 2, 3, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, 2, 2, 2, 2, 2, _, _, _, _,
        _, 2, _, 3, _, _, _, 2, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        //  300
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _
    };
    const int *expected_end = expected + sizeof(expected) / sizeof(*expected);

    std::vector<int> results;

    for( int i = 0; i < 350; i++ )
    {
        results.push_back(GetErrorType(i));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected, expected_end,
        results.begin(), results.end());
}

BOOST_AUTO_TEST_SUITE_END()


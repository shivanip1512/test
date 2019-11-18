#include <error.h>

#include "yukon.h"
#include "resolvers.h"

#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>
#include <boost/lexical_cast.hpp>

BOOST_AUTO_TEST_SUITE( test_error )

struct UnknownErrorHelper
{
    unsigned num;

    UnknownErrorHelper(unsigned initial) : num(initial) {}

    operator std::string()
    {
        return "Unknown Error Code (" + boost::lexical_cast<std::string>(num++) + ")";
    }
};

//  Used by test_GetError, test_GetErrorString, and test_FormatError
const std::vector<std::string> ErrorStrings = boost::assign::list_of<std::string>
    //  0
    ("Normal (Success) Return")
    ("Not Normal (Unsuccessful) Return")
    ("Route has no associated transmitter")
    ("Bad Message Type")
    ("D Word Wrong length")
    ("Unknown Error Code (5)")
    ("Unknown Error Code (6)")
    ("Unknown Error Code (7)")
    ("Bad ID Specification")
    ("Parameter Out of Range")
    //  10
    ("Missing Parameter")
    ("Syntax Error")
    ("Unknown Error Code (12)")
    ("Bad read")
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
    ("Unknown Error Code (23)")
    ("No Requests for CCU Queue")
    ("Unknown Error Code (25)")
    ("Bad Parameter")
    ("Bad Route Specification")
    ("Bad Bus Specification")
    ("Unknown Error Code (29)")
    //  30
    ("Read Error")
    ("Timeout Reading from Port")
    ("Sequence Reject Frame Received... Sequencing Adjusted")
    ("Framing Error")
    ("Bad CRC on Message")
    ("Bad Length Specification")
    ("Bad HDLC UA Frame")
    ("Unknown Error Code (37)")
    ("Unknown Command Received")
    ("Unknown Error Code (39)")
    //  40
    ("Unknown Error Code (40)")
    ("Unknown Error Code (41)")
    ("Unknown Error Code (42)")
    ("Unknown Error Code (43)")
    ("Unknown Error Code (44)")
    ("Unknown Error Code (45)")
    ("REQACK Flag set-- Frame Unexecutable")
    ("Unknown Error Code (47)")
    ("Unknown Error Code (48)")
    ("Route Not Found")
    //  50
    ("Unknown Error Code (50)")
    ("Unknown Error Code (51)")
    ("Unknown Error Code (52)")
    ("Outstation address not found on port")
    ("Device ID Not Found")
    ("Child Device Unknown")
    ("Function and/or Type Not Found")
    ("E-Word Received in Returned Message")
    ("B-Word Received in Returned Message")
    ("OS or System Error")
    //  60
    ("Bad Port Specification")
    ("Error Reading Queue")
    ("Error Writing Queue")
    ("Error Allocating or Manipulating Memory")
    ("Unknown Error Code (64)")
    ("No DCD on Return Message")
    ("Unknown Error Code (66)")
    ("Error Reading from Port")
    ("Error Writing to Port")
    ("Unknown Error Code (69)")
    //  70
    ("Unknown Error Code (70)")
    ("Error Executing CCU Queue Entry")
    ("DLC Read Timeout on CCU Queue Entry")
    ("No Attempt Made on CCU Queue Entry")
    ("Route Failed on CCU Queue Entry")
    ("Transponder Communication Failed on CCU Queue Entry")
    ("Unknown Error Code (76)")
    ("Unknown Error Code (77)")
    ("Communications Attempted With Inhibited Remote")
    ("CCU Queue was Flushed... Entries Lost in Drain")
    //  80
    ("Unknown Error Code (80)")
    ("Unknown Error Code (81)")
    ("Unknown Error Code (82)")
    ("Operation Attempted on Inhibited Port")
    ("Device Does Not Support Accumulators")
    ("Operation Attempted on Inhibited Device")
    ("Unknown Error Code (86)")
    ("Error Dialing Up Remote")
    ("Wrong Unique Address Received")
    ("Error Connecting to TCP socket")
    //  90
    ("Error Writing to TCP socket")
    ("Error Reading from TCP socket")
    ("Address Does Not Match Expected Value")
    ("Bad Data Buffer for IED")
    ("Missing Required Configuration Entry")
    ("Unknown Error Code (95)")
    ("Unknown Error Code (96)")
    ("Unknown Error Code (97)")
    ("Bad Nexus Specification")
    ("Error Writing to Port")
    //  100
    ("Bad BCH")
    .repeat(99, UnknownErrorHelper(101))
    //  200
    ("Unknown Error Code (200)")
    ("Memory Error")
    ("No Method or Invalid Command.")
    ("Unknown Error Code (203)")
    ("No General Scan Method")
    ("No Integrity Scan Method")
    ("No Accum Scan Method")
    ("Unknown Error Code (207)")
    ("No Process Result Method")
    ("No Exec. Req. Method")
    //  210
    ("No Result Decode Method")
    ("No ErrorDecode Method")
    ("No Handshake Method")
    ("No Generate Command Method")
    ("No DecodeResponse Method")
    ("No Data Copy Method")
    ("Unknown Error Code (216)")
    ("Unknown Error Code (217)")
    ("No Config Data Found")
    ("Config is not current")
    //  220
    ("Config is current")
    ("No Route for Group Dev.")
    ("No Routes for Macro Rte")
    ("Macro Offset does not exist in Macro Rte")
    ("Macro Offset refers to a macro sub-rte")
    ("Device is control disabled")
    ("Control is currently inhibited for the point.")
    ("Request timed out.")
    ("Requested operation expired due to time")
    ("Point has no control configuration")
    //  230
    ("Unknown Error Code (230)")
    ("Unknown Error Code (231)")
    ("Unknown Error Code (232)")
    ("Unknown Error Code (233)")
    ("Retry Resubmitted")
    ("Unknown Error Code (235)")
    ("Unknown Error Code (236)")
    ("Scanned device is inhibited")
    ("Illegal scan of global device")
    ("Device window is closed")
    //  240
    ("Unknown Error Code (240)")
    ("Dialup connection failed. Port in error")
    ("Dialup connection failed. Device in error")
    ("Port is simulated, no inbound data available")
    ("Port echoed request bytes")
    ("Invalid transaction, typ. bad pager id or password")
    ("TAP Repeat Requested, but retries exhausted")
    ("No response from TAP terminal")
    ("Invalid/Incomplete Request")
    ("Unknown Error Code (249)")
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
    ("Unknown Error Code (259)")
    //  260
    ("MACS timed out on this message")
    ("The freeze check bit in the frozen reading does not match the last recorded freeze sent to the device.")
    ("The frozen peak timestamp is outside of the expected range.")
    ("The freeze counter is less than the expected value.")
    ("Invalid data was received for one or more data points.")
    ("There is no record of the last freeze sent to this device.")
    ("Request was cancelled")
    ("Invalid time returned OR time outside of requested range.")
    ("Invalid channel returned by daily read.")
    ("Invalid or insufficient device SSPEC revision.")
    //  270
    ("Verification of device SSPEC revision is required.")
    ("Invalid future data rejected")
    ("Device not supported by Porter")
    ("Port not initialized")
    ("Command already in progress")
    ("No connection to device")
    ("No disconnect configured on this device")
    ("Transmitter is overheating")
    ("Command requires channel configuration to continue.")
    ("Command requires a valid date.")
    //  280
    ("Failed to resolve an IP for the given DNS name.")
    ("Failed to find a point for the given device.")
    ("The device's date range must be reset to continue.")
    ("Not enough data received from the device.")
    ("Configuration data is invalid.")
    ("Device address is unknown.")
    ("Network is unavailable.")
    ("Request packet is too large.")
    ("Protocol is unsupported.")
    ("Network server ID is invalid.")
    //  290
    ("Application service ID is invalid.")
    ("Network traffic limiting.")
    ("Did not receive a response from the device.")
    ("Did not receive a response from Network Manager.")
    ("E2E request payload too large.")
    ("Endpoint indicated request not acceptable.")
    ("The device has no points attached.")
    ("Network service failure.")
    ("Channel not enabled in RF node.")
    ("Channel not supported by meter.")
    //  300
    ("Invalid battery node device.")
    ("Unknown battery node.")
    ("Unknown gateway.")
    ("Unspecified battery node failure.")
    ("Function code not supported.")
    ("Unknown object.")
    ("Parameter error, not all points in the index range or object index prefixes exist.")
    ("Requested operation is already executing.")
    ("Aggregate response did not include an entry for the command.")
    ("Endpoint indicated a bad request.")
    ("Endpoint indicated command failure.")
    //  310
    ("Endpoint returned an unmapped error.")
    ("Meter is bricked, it is not responding to the node.")
    ("The meter rejected a message from the node, no specific reason was provided.")
    ("The meter rejected a command message because request could not be honored.")
    ("The security code used to log into the meter was insufficient for configuration.")
    ("The node requested some action that is not possible.")
    ("The node requested an inappropriate action.")
    ("The node's request was not acted upon because the meter was busy.")
    ("The node’s request was unsuccessful because the requested data is not ready to be accessed.")
    ("The node’s request was unsuccessful because the data cannot be accessed.")
    //  320
    ("The meter wishes to return to Base State and renegotiate communication parameters.")
    ("The node’s request is not accepted at the current service sequence state.")
    ("The file download process was aborted.")
    ("The configuration file size is greater than the space available.")
    ("A configuration is in progress in the node.")
    ("The node is unable to get the file.")
    ("The meter’s hardware or firmware version is insufficient to allow remote configuration.")
    ("The Expiration Date/Time in the file is past the current date/time in the node.")
    ("The meter does not meet the requirements in the Meter Requirements field of the Configuration file.")
    ("The configuration file has an error in format.")
    //  330
    ("A test in a Verification field failed.")
    ("The configuration file contains a Write Key TLV, but the node is unable to write the key.")
    ("If there is an outage or node resets while writing to the meter, the meter may be left in a state where the node is unable to configure it.")
    .repeat(16, UnknownErrorHelper(334));

BOOST_AUTO_TEST_CASE(test_ClientErrors_None_must_be_zero)
{
    BOOST_REQUIRE_EQUAL(ClientErrors::None, 0);
}

BOOST_AUTO_TEST_CASE(test_unknownErrors)
{
    const auto unknownErrors = CtiError::GetUnknownErrors();

    for (const auto & unknownError : unknownErrors)
    {
        BOOST_ERROR("Unknown error with error type " << unknownError.type << " and description " << unknownError.description << " found.");
    }
}

BOOST_AUTO_TEST_CASE(test_GetErrorString)
{
    std::vector<std::string> results;

    for( int i = 0; i < 350; i++ )
    {
        results.push_back(CtiError::GetErrorString(static_cast<YukonError_t>(i)));
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
        0, _, _, 2, 2, _, _, _, _, _,
        _, _, _, 2, _, 3, 3, 2, 2, 2,
        2, 2, 2, _, _, _, _, _, _, _,
        _, 3, 3, 3, 3, _, 3, _, 2, _,
        _, _, _, _, _, _, 3, _, _, _,
        _, _, _, _, _, _, _, 2, 2, _,
        _, _, _, _, _, 3, _, 3, 3, _,
        _, 2, 2, _, 2, 2, _, _, _, _,
        _, _, _, _, _, _, _, 3, 3, 3,
        3, 3, _, _, _, _, _, _, _, _,
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
        _, _, _, 3, _, _, _, 2, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        //  300
        _, _, _, _, 2, 2, 2, 2, 2, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
    };
    const int *expected_end = expected + sizeof(expected) / sizeof(*expected);

    std::vector<int> results;

    for( int i = 0; i < 350; i++ )
    {
        results.push_back(CtiError::GetErrorType(static_cast<YukonError_t>(i)));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected, expected_end,
        results.begin(), results.end());
}

BOOST_AUTO_TEST_SUITE_END()


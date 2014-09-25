#include <dsm2err.h>

#include "yukon.h"

#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

BOOST_AUTO_TEST_SUITE( test_error )

//  Used by test_GetError, test_GetErrorString, and test_FormatError
const std::vector<std::string> ErrorStrings = boost::assign::list_of
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
    ("Parameter out of Range")
    //  10
    ("Missing Parameter")
    ("Syntax Error")
    ("Unknown Error Code (12)")
    ("Unknown Error Code (13)")
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
    ("Unknown Error Code (24)")
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
    ("Unknown Error")
    ("Unknown Error Code (38)")
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
    ("Unknown Error Code (53)")
    ("ID Not Found")
    ("Unknown Error Code (55)")
    ("Function and/or Type Not Found")
    ("E-Word Received in Returned Message")
    ("Unknown Error Code (58)")
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
    ("Pipe Connect was Broken")
    ("Pipe Not Opened")
    ("Communications Attempted Over Inhibited Port")
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
    ("Error Writing to Nexus")
    //  100
    ("Bad BCH")                 ("Unknown Error Code (101)")("Unknown Error Code (102)")("Unknown Error Code (103)")("Unknown Error Code (104)")("Unknown Error Code (105)")("Unknown Error Code (106)")("Unknown Error Code (107)")("Unknown Error Code (108)")("Unknown Error Code (109)")
    ("Unknown Error Code (110)")("Unknown Error Code (111)")("Unknown Error Code (112)")("Unknown Error Code (113)")("Unknown Error Code (114)")("Unknown Error Code (115)")("Unknown Error Code (116)")("Unknown Error Code (117)")("Unknown Error Code (118)")("Unknown Error Code (119)")
    ("Unknown Error Code (120)")("Unknown Error Code (121)")("Unknown Error Code (122)")("Unknown Error Code (123)")("Unknown Error Code (124)")("Unknown Error Code (125)")("Unknown Error Code (126)")("Unknown Error Code (127)")("Unknown Error Code (128)")("Unknown Error Code (129)")
    ("Unknown Error Code (130)")("Unknown Error Code (131)")("Unknown Error Code (132)")("Unknown Error Code (133)")("Unknown Error Code (134)")("Unknown Error Code (135)")("Unknown Error Code (136)")("Unknown Error Code (137)")("Unknown Error Code (138)")("Unknown Error Code (139)")
    ("Unknown Error Code (140)")("Unknown Error Code (141)")("Unknown Error Code (142)")("Unknown Error Code (143)")("Unknown Error Code (144)")("Unknown Error Code (145)")("Unknown Error Code (146)")("Unknown Error Code (147)")("Unknown Error Code (148)")("Unknown Error Code (149)")
    ("Unknown Error Code (150)")("Unknown Error Code (151)")("Unknown Error Code (152)")("Unknown Error Code (153)")("Unknown Error Code (154)")("Unknown Error Code (155)")("Unknown Error Code (156)")("Unknown Error Code (157)")("Unknown Error Code (158)")("Unknown Error Code (159)")
    ("Unknown Error Code (160)")("Unknown Error Code (161)")("Unknown Error Code (162)")("Unknown Error Code (163)")("Unknown Error Code (164)")("Unknown Error Code (165)")("Unknown Error Code (166)")("Unknown Error Code (167)")("Unknown Error Code (168)")("Unknown Error Code (169)")
    ("Unknown Error Code (170)")("Unknown Error Code (171)")("Unknown Error Code (172)")("Unknown Error Code (173)")("Unknown Error Code (174)")("Unknown Error Code (175)")("Unknown Error Code (176)")("Unknown Error Code (177)")("Unknown Error Code (178)")("Unknown Error Code (179)")
    ("Unknown Error Code (180)")("Unknown Error Code (181)")("Unknown Error Code (182)")("Unknown Error Code (183)")("Unknown Error Code (184)")("Unknown Error Code (185)")("Unknown Error Code (186)")("Unknown Error Code (187)")("Unknown Error Code (188)")("Unknown Error Code (189)")
    ("Unknown Error Code (190)")("Unknown Error Code (191)")("Unknown Error Code (192)")("Unknown Error Code (193)")("Unknown Error Code (194)")("Unknown Error Code (195)")("Unknown Error Code (196)")("Unknown Error Code (197)")("Unknown Error Code (198)")("Unknown Error Code (199)")
    //  200
    ("Unknown Error Code (200)")
    ("Memory Error")
    ("No Method")
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
    ("Unknown Error Code (219)")
    //  220
    ("Unknown Error Code (220)")
    ("No Route for Group Dev.")
    ("No Routes for Macro Rte")
    ("Macro Offset does not exist in Macro Rte")
    ("Macro Offset refers to a macro sub-rte")
    ("Device is control disabled")
    ("Point is control disabled")
    ("Unknown Error Code (227)")
    ("Requested operation expired due to time")
    ("Unknown Error Code (229)")
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
    ("Invalid data was was received for one or more data points.")
    ("There is no record of the last freeze sent to this device.")
    ("Request was cancelled")
    ("Invalid time returned OR time outside of requested range.")
    ("Invalid channel returned by daily read.")
    ("Insufficient SSPEC/Firmware Revision")
    //  270
    ("Verify SSPEC/Firmware Revision")
    ("Unknown Error Code (271)")
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
    ("Unknown Error Code (294)")
    ("Unknown Error Code (295)")
    ("The device has no points attached.")
    ("Unknown Error Code (297)")
    ("Unknown Error Code (298)")
    ("Unknown Error Code (299)")
    //  300
    ("Unknown Error Code (300)")("Unknown Error Code (301)")("Unknown Error Code (302)")("Unknown Error Code (303)")("Unknown Error Code (304)")("Unknown Error Code (305)")("Unknown Error Code (306)")("Unknown Error Code (307)")("Unknown Error Code (308)")("Unknown Error Code (309)")
    ("Unknown Error Code (310)")("Unknown Error Code (311)")("Unknown Error Code (312)")("Unknown Error Code (313)")("Unknown Error Code (314)")("Unknown Error Code (315)")("Unknown Error Code (316)")("Unknown Error Code (317)")("Unknown Error Code (318)")("Unknown Error Code (319)")
    ("Unknown Error Code (320)")("Unknown Error Code (321)")("Unknown Error Code (322)")("Unknown Error Code (323)")("Unknown Error Code (324)")("Unknown Error Code (325)")("Unknown Error Code (326)")("Unknown Error Code (327)")("Unknown Error Code (328)")("Unknown Error Code (329)")
    ("Unknown Error Code (330)")("Unknown Error Code (331)")("Unknown Error Code (332)")("Unknown Error Code (333)")("Unknown Error Code (334)")("Unknown Error Code (335)")("Unknown Error Code (336)")("Unknown Error Code (337)")("Unknown Error Code (338)")("Unknown Error Code (339)")
    ("Unknown Error Code (340)")("Unknown Error Code (341)")("Unknown Error Code (342)")("Unknown Error Code (343)")("Unknown Error Code (344)")("Unknown Error Code (345)")("Unknown Error Code (346)")("Unknown Error Code (347)")("Unknown Error Code (348)")("Unknown Error Code (349)");

BOOST_AUTO_TEST_CASE(test_ClientErrors_None_must_be_zero)
{
    BOOST_REQUIRE_EQUAL(ClientErrors::None, 0);
}

BOOST_AUTO_TEST_CASE(test_GetErrorString)
{
    std::vector<std::string> results;

    for( int i = 0; i < 350; i++ )
    {
        results.push_back(GetErrorString(static_cast<YukonError_t>(i)));
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
        _, _, _, _, _, 3, 3, 2, 2, 2,
        2, 2, 2, _, _, _, _, _, _, _,
        _, 3, 3, 3, 3, _, 3, _, _, _,
        _, _, _, _, _, _, 3, _, _, _,
        _, _, _, _, _, _, _, 2, _, _,
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
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
        _, _, _, _, _, _, _, _, _, _,
    };
    const int *expected_end = expected + sizeof(expected) / sizeof(*expected);

    std::vector<int> results;

    for( int i = 0; i < 350; i++ )
    {
        results.push_back(GetErrorType(static_cast<YukonError_t>(i)));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected, expected_end,
        results.begin(), results.end());
}

BOOST_AUTO_TEST_SUITE_END()


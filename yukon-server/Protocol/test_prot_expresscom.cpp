#include <boost/test/auto_unit_test.hpp>

#include "expresscom.h"

BOOST_AUTO_TEST_SUITE( test_prot_expresscom )

BOOST_AUTO_TEST_CASE(test_prot_xcom_extended_tier_timeout)
{
    CtiCommandParser        parse( "putconfig xcom extended tier 2 timeout 14400 serial 1234" );
    CtiProtocolExpresscom   xcom;

    BYTE expected_result[] = { 0, 0, 0, 0, 0,       // addressing not assigned: defaults to zero
                               0x17,                // message type: extended tier
                               0x10,                // flags: send a timeout value
                               0x02,                // tier number
                               0x38, 0x40 };        // timeout value: 14400 == 0x3840

    BOOST_CHECK_EQUAL( xcom.parseRequest(parse), ClientErrors::None );
    BOOST_CHECK_EQUAL( xcom.entries(), 1 );
    BOOST_CHECK_EQUAL( xcom.messageSize(1), sizeof (expected_result) / sizeof (expected_result[0]) );

    for (int i = 0; i < xcom.messageSize(1); ++i)
    {
        BOOST_CHECK_EQUAL( xcom.getByte(i, 1), expected_result[i] );
    }
}

BOOST_AUTO_TEST_CASE(test_prot_xcom_xcdata_ascii)
{
    // Test using centssign special field
    {
        CtiCommandParser        parse( "putconfig xcom data 'Offpeak rate:6.5*CENTSSIGN*/kWh' msgpriority 7 timeout 150 min" );
        CtiProtocolExpresscom   xcom;

        BYTE expected_result[] = { 0, 0, 0, 0, 0,       // addressing not assigned: defaults to zero
                                   0x1D,                // message type: Data
                                   0x20,                // config: use ascii
                                   0x18,                // Data length
                                   0x07,                // Display Priority 7=highest
                                   0x00,                // Flags (Timeout in Minutes)
                                   0x96,                // Timeout 150m
                                   0x4F, 0x66, 0x66, 0x70, 0x65, 0x61, 0x6B, 0x20, 0x72, 0x61, 0x74, 0x65, 0x3A, 0x36, 0x2E, 0x35, 0xA2, 0x2F, 0x6B, 0x57, 0x68};
                                   //O    f     f     p     e      a     k    ' '    r     a     t    e     :     6      .    5    cent   /     k     W      h

        BOOST_CHECK_EQUAL( xcom.parseRequest(parse), ClientErrors::None );
        BOOST_CHECK_EQUAL( xcom.entries(), 1 );
        BOOST_CHECK_EQUAL( xcom.messageSize(1), sizeof (expected_result) / sizeof (expected_result[0]) );

        for (int i = 0; i < xcom.messageSize(1); ++i)
        {
            BOOST_CHECK_EQUAL( xcom.getByte(i, 1), expected_result[i] );
        }
    }

    // Lower Priority, 150 hours
    {
        CtiCommandParser        parse( "putconfig xcom data 'Offpeak rate:6.5*CENTSSIGN*/kWh' msgpriority 3 timeout 150 hour" );
        CtiProtocolExpresscom   xcom;

        BYTE expected_result[] = { 0, 0, 0, 0, 0,       // addressing not assigned: defaults to zero
                                   0x1D,                // message type: Data
                                   0x20,                // config: use ascii
                                   0x18,                // Data length
                                   0x03,                // Display Priority 7=highest
                                   0x01,                // Flags (Timeout in hours)
                                   0x96,                // Timeout 150h
                                   0x4F, 0x66, 0x66, 0x70, 0x65, 0x61, 0x6B, 0x20, 0x72, 0x61, 0x74, 0x65, 0x3A, 0x36, 0x2E, 0x35, 0xA2, 0x2F, 0x6B, 0x57, 0x68};
                                   //O    f     f     p     e      a     k    ' '    r     a     t    e     :     6      .    5    cent   /     k     W      h

        BOOST_CHECK_EQUAL( xcom.parseRequest(parse), ClientErrors::None );
        BOOST_CHECK_EQUAL( xcom.entries(), 1 );
        BOOST_CHECK_EQUAL( xcom.messageSize(1), sizeof (expected_result) / sizeof (expected_result[0]) );

        for (int i = 0; i < xcom.messageSize(1); ++i)
        {
            BOOST_CHECK_EQUAL( xcom.getByte(i, 1), expected_result[i] );
        }
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_extended_tier_delay)
{
    CtiCommandParser        parse( "putconfig xcom extended tier 2 delay 3600 serial 1234" );
    CtiProtocolExpresscom   xcom;

    BYTE expected_result[] = { 0, 0, 0, 0, 0,       // addressing not assigned: defaults to zero
                               0x17,                // message type: extended tier
                               0x08,                // flags: send a delay value
                               0x02,                // tier number
                               0x0E, 0x10 };        // delay value: 3600 == 0x0E10

    BOOST_CHECK_EQUAL( xcom.parseRequest(parse), ClientErrors::None );
    BOOST_CHECK_EQUAL( xcom.entries(), 1 );
    BOOST_CHECK_EQUAL( xcom.messageSize(1), sizeof (expected_result) / sizeof (expected_result[0]) );

    for (int i = 0; i < xcom.messageSize(1); ++i)
    {
        BOOST_CHECK_EQUAL( xcom.getByte(i, 1), expected_result[i] );
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_extended_tier_timeout_and_delay)
{
    CtiCommandParser        parse( "putconfig xcom extended tier 2 timeout 14400 delay 3600 serial 1234" );
    CtiProtocolExpresscom   xcom;

    BYTE expected_result[] = { 0, 0, 0, 0, 0,       // addressing not assigned: defaults to zero
                               0x17,                // message type: extended tier
                               0x18,                // flags: send a timeout and a delay value
                               0x02,                // tier number
                               0x38, 0x40,          // timeout value: 14400 == 0x3840
                               0x0E, 0x10 };        // delay value: 3600 == 0x0E10

    BOOST_CHECK_EQUAL( xcom.parseRequest(parse), ClientErrors::None );
    BOOST_CHECK_EQUAL( xcom.entries(), 1 );
    BOOST_CHECK_EQUAL( xcom.messageSize(1), sizeof (expected_result) / sizeof (expected_result[0]) );

    for (int i = 0; i < xcom.messageSize(1); ++i)
    {
        BOOST_CHECK_EQUAL( xcom.getByte(i, 1), expected_result[i] );
    }
}


/*
    Testing: CtiProtocolExpresscom::addAddressing()

    addAddressing() passes in the parameters in the exact size they are required to hold, so the
    only invalid address available is all bits set.

    Exceptions to the above:

        FEEDER is a 16 bit bitfield, hence ALL values are allowed: 0 to 0xFFFF

        ZIP is only 24 bits wide, but is held in a 32 bit quantity.  So values >= 0x01000000
            also return BADPARAM.
*/
BOOST_AUTO_TEST_CASE(test_prot_xcom_addAddressing_default)
{
    CtiProtocolExpresscom   xcom;

    // all addresses are 0
    BOOST_CHECK_EQUAL( xcom.addAddressing() , ClientErrors::None);
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_addAddressing_serial)
{
    // test the serial# -- nothing to do in this case

    BOOST_CHECK_EQUAL(true , true);
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_addAddressing_spid)
{
    CtiProtocolExpresscom   xcom;

    // test the SPID
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, CtiProtocolExpresscom::SpidMin) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0x1234) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, CtiProtocolExpresscom::SpidMax) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, CtiProtocolExpresscom::SpidMax + 1) , ClientErrors::BadParameter);
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_addAddressing_geo)
{
    CtiProtocolExpresscom   xcom;

    // test the GEO
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, CtiProtocolExpresscom::GeoMin) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0x1234) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, CtiProtocolExpresscom::GeoMax) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, CtiProtocolExpresscom::GeoMax + 1) , ClientErrors::BadParameter);
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_addAddressing_substation)
{
    CtiProtocolExpresscom   xcom;

    // test the SUBSTATION
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, CtiProtocolExpresscom::SubstationMin) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0x1234) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, CtiProtocolExpresscom::SubstationMax) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, CtiProtocolExpresscom::SubstationMax + 1) , ClientErrors::BadParameter);
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_addAddressing_feeder)
{
    CtiProtocolExpresscom   xcom;

    // test the FEEDER -- all numbers are possible
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, CtiProtocolExpresscom::FeederMin) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0x1234) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, CtiProtocolExpresscom::FeederMax) , ClientErrors::None);
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_addAddressing_zip)
{
    CtiProtocolExpresscom   xcom;

    // test the ZIP
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, CtiProtocolExpresscom::ZipMin) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0x1234) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, CtiProtocolExpresscom::ZipMax) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, CtiProtocolExpresscom::ZipMax + 1) , ClientErrors::BadParameter);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, CtiProtocolExpresscom::ZipMax + 10000) , ClientErrors::BadParameter);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, CtiProtocolExpresscom::ZipMax + 10000000) , ClientErrors::BadParameter);
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_addAddressing_uda)
{
    CtiProtocolExpresscom   xcom;

    // test the UDA
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::UserMin) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0x1234) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::UserMax) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::UserMax + 1) , ClientErrors::BadParameter);
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_addAddressing_program)
{
    CtiProtocolExpresscom   xcom;

    // test the PROGRAM
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::ProgramMin) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0, 0x12) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::ProgramMax) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::ProgramMax + 1) , ClientErrors::BadParameter);
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_addAddressing_splinter)
{
    CtiProtocolExpresscom   xcom;

    // test the SPLINTER
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::SplinterMin) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0, 0, 0x12) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::SplinterMax) , ClientErrors::None);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::SplinterMax + 1) , ClientErrors::BadParameter);
}


/*
    Testing: CtiProtocolExpresscom::parseAddressing()

    Here the input comes in the form of a CtiCommandParser parse object.  The values specified
    aren't passed into the function in exact-width registers.  This enables testing with more values
    outside the valid ranges on the high end.
*/
BOOST_AUTO_TEST_CASE(test_prot_xcom_parseAddressing_serial)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign serial 0" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign serial 1" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign serial 123456" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign serial 4294967295" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign serial 4294967296" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign serial 5000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseAddressing_spid)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign spid 0" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign spid 1" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign spid 12345" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign spid 65534" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign spid 65535" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign spid 1234567" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign spid 2000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign spid 4000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseAddressing_geo)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign geo 0" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign geo 1" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign geo 12345" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign geo 65534" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign geo 65535" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign geo 1234567" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign geo 2000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign geo 4000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseAddressing_substation)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign sub 0" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign sub 1" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign sub 12345" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign sub 65534" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign sub 65535" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign sub 1234567" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign sub 2000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign sub 4000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseAddressing_feeder)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign feeder 0" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign feeder 1" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign feeder 12345" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign feeder 65535" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign feeder 65536" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign feeder 1234567" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign feeder 2000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign feeder 4000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseAddressing_zip)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 0" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 1" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 12345" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 1234567" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 16777214" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 16777215" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 123456789" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 2000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 4000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseAddressing_uda)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign uda 0" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign uda 1" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign uda 12345" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign uda 65534" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign uda 65535" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign uda 1234567" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign uda 2000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign uda 4000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseAddressing_program)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign program 0" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign program 1" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign program 34" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign program 254" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign program 255" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign program 450" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign program 1234567" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign program 2000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign program 4000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseAddressing_splinter)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 0" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 1" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 34" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 254" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 255" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 450" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 1234567" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 2000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 4000000000" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::BadParameter);
    }
}


// Example command given from YUK-12764
BOOST_AUTO_TEST_CASE(test_prot_xcom_parseAddressing_manual_config_zero_address)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign p 3,0,0 load 1,2,3 update serial 404850" );
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , ClientErrors::None);
    }
}


/*
    Wrapper to expose the CtiProtocolExpresscom::parseTargetAddressing() function.
*/
class Test_CtiProtocolExpresscom : public CtiProtocolExpresscom
{
public:
    INT test_parseTargetAddressing(CtiCommandParser &parse)
    {
        return parseTargetAddressing(parse);
    }
};


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseTargetAddressing_serial)
{
    // test the serial# -- fixed and assigned in the factory.....

    BOOST_CHECK_EQUAL(true , true);
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseTargetAddressing_spid)
{
    Test_CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom target spid 0 assign spid 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 0 assign spid 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 1 assign spid 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 1 assign spid 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 1 assign spid 10000000" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 2345 assign spid 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 65534 assign spid 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 65535 assign spid 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 1234567 assign spid 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseTargetAddressing_geo)
{
    Test_CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom target geo 0 assign geo 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target geo 0 assign geo 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target geo 1 assign geo 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target geo 1 assign geo 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target geo 1 assign geo 10000000" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target geo 2345 assign geo 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target geo 65534 assign geo 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target geo 65535 assign geo 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target geo 1234567 assign geo 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseTargetAddressing_substation)
{
    Test_CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom target sub 0 assign sub 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target sub 0 assign sub 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target sub 1 assign sub 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target sub 1 assign sub 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target sub 1 assign sub 10000000" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target sub 2345 assign sub 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target sub 65534 assign sub 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target sub 65535 assign sub 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target sub 1234567 assign sub 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseTargetAddressing_feeder)
{
    Test_CtiProtocolExpresscom   xcom;

    // For the "target feeder 0" we need some more target information, because the addressing
    //  level is not properly set.  I tacked on a SPID for these two tests.
    {
        CtiCommandParser    parse( "putconfig xcom target spid 1 feeder 0 assign feeder 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 1 feeder 0 assign feeder 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target feeder 1 assign feeder 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target feeder 1 assign feeder 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target feeder 1 assign feeder 10000000" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target feeder 65534 assign feeder 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target feeder 65535 assign feeder 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target feeder 65536 assign feeder 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target feeder 1234567 assign feeder 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseTargetAddressing_zip)
{
    Test_CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom target zip 0 assign zip 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target zip 0 assign zip 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target zip 1 assign zip 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target zip 1 assign zip 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target zip 1 assign zip 20000000" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target zip 777214 assign zip 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target zip 16777214 assign zip 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target zip 16777215 assign zip 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target zip 1234567890 assign zip 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseTargetAddressing_uda)
{
    Test_CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom target uda 0 assign uda 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target uda 0 assign uda 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target uda 1 assign uda 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target uda 1 assign uda 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target uda 1 assign uda 10000000" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target uda 2345 assign uda 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target uda 65534 assign uda 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target uda 65535 assign uda 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target uda 1234567 assign uda 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseTargetAddressing_program)
{
    Test_CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom target program 0 assign program 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 0 assign program 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 1 assign program 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 1 assign program 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 1 assign program 1000" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 34 assign program 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 254 assign program 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 255 assign program 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 500 assign program 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 1234567 assign program 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }
}


BOOST_AUTO_TEST_CASE(test_prot_xcom_parseTargetAddressing_splinter)
{
    Test_CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 0 assign splinter 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 0 assign splinter 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 1 assign splinter 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 1 assign splinter 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 1 assign splinter 1000" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 34 assign splinter 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 254 assign splinter 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::None);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 255 assign splinter 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 500 assign splinter 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 1234567 assign splinter 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , ClientErrors::BadParameter);
    }
}

BOOST_AUTO_TEST_CASE(test_get_message_as_string)
{
    CtiCommandParser        parse( "putconfig xcom extended tier 2 timeout 14400 delay 3600" );
    CtiProtocolExpresscom   xcom;

    /*Shamefully copied from below
      BYTE expected_result[] = { 0, 0, 0, 0, 0,       // Address defaults to Serial 0
                               0x17,                // message type: extended tier
                               0x18,                // flags: send a timeout and a delay value
                               0x02,                // tier number
                               0x38, 0x40,          // timeout value: 14400 == 0x3840
                               0x0E, 0x10,          // delay value: 3600 == 0x0E10
                               0xED, 0x0F };        // CRC generated by CDT <>MSG CNS08 U000000000017180238400E10ED0FV*/

    BOOST_CHECK_EQUAL( xcom.parseRequest(parse), ClientErrors::None );

    BOOST_CHECK_EQUAL("u000000000017180238400E10ED0Fv", xcom.getMessageAsString());
}

BOOST_AUTO_TEST_CASE(test_prot_xcom_crc)
{
    // First 3 blocks are identical commands, different ways of setting the CRC
    {
        CtiCommandParser        parse( "putconfig xcom extended tier 2 timeout 14400 delay 3600" );
        CtiProtocolExpresscom   xcom;

        BYTE expected_result[] = { 0, 0, 0, 0, 0,       // Address defaults to Serial 0
                                   0x17,                // message type: extended tier
                                   0x18,                // flags: send a timeout and a delay value
                                   0x02,                // tier number
                                   0x38, 0x40,          // timeout value: 14400 == 0x3840
                                   0x0E, 0x10,          // delay value: 3600 == 0x0E10
                                   0xED, 0x0F };        // CRC generated by CDT <>MSG CNS08 U000000000017180238400E10ED0FV

        BOOST_CHECK_EQUAL( xcom.parseRequest(parse), ClientErrors::None );
        BOOST_CHECK_EQUAL( xcom.entries(), 1 );
        BOOST_CHECK_EQUAL( xcom.getStartByte(), 'u' ); // With CRC, these are U and V.
        BOOST_CHECK_EQUAL( xcom.getStopByte(), 'v' );
        BOOST_CHECK_EQUAL( xcom.messageSize(), sizeof (expected_result) / sizeof (expected_result[0]) );

        // only doing this test once to verify getFullMessage is the same as getBytes.
        std::vector<unsigned char> message;
        xcom.getFullMessage(message);
        BOOST_CHECK_EQUAL( message.front(), 'u' ); // With CRC, these are U and V.
        BOOST_CHECK_EQUAL( message.back(), 'v' );

        BOOST_CHECK_EQUAL_COLLECTIONS(message.begin()+1, message.end()-1,
                                      expected_result,   expected_result+xcom.messageSize());

        for (int i = 0; i < xcom.messageSize(); ++i)
        {
            // Do this check by hand so we can have a nice printout on failure.
            if (xcom.getByte(i) != expected_result[i])
            {
                BOOST_CHECK_EQUAL( xcom.getByte(i), expected_result[i] );
                std::cout << "Failed when i was " << i << std::endl;
            }
        }
    }

    {
        CtiCommandParser        parse( "putconfig xcom extended tier 2 timeout 14400 delay 3600" );
        CtiProtocolExpresscom   xcom;
        xcom.setUseCRC(true);

        BYTE expected_result[] = { 0, 0, 0, 0, 0,       // Address defaults to Serial 0
                                   0x17,                // message type: extended tier
                                   0x18,                // flags: send a timeout and a delay value
                                   0x02,                // tier number
                                   0x38, 0x40,          // timeout value: 14400 == 0x3840
                                   0x0E, 0x10,          // delay value: 3600 == 0x0E10
                                   0xED, 0x0F };        // CRC generated by CDT <>MSG CNS08 U000000000017180238400E10ED0FV

        BOOST_CHECK_EQUAL( xcom.parseRequest(parse), ClientErrors::None );
        BOOST_CHECK_EQUAL( xcom.entries(), 1 );
        BOOST_CHECK_EQUAL( xcom.getStartByte(), 'u' ); // With CRC, these are U and V.
        BOOST_CHECK_EQUAL( xcom.getStopByte(), 'v' );
        BOOST_CHECK_EQUAL( xcom.messageSize(), sizeof (expected_result) / sizeof (expected_result[0]) );

        for (int i = 0; i < xcom.messageSize(); ++i)
        {
            // Do this check by hand so we can have a nice printout on failure.
            if (xcom.getByte(i) != expected_result[i])
            {
                BOOST_CHECK_EQUAL( xcom.getByte(i), expected_result[i] );
                std::cout << "Failed when i was " << i << std::endl;
            }
        }
    }

    {
        CtiCommandParser        parse( "putconfig xcom extended tier 2 timeout 14400 delay 3600" );
        CtiProtocolExpresscom   xcom;

        //**************************************
        xcom.setUseCRC(false);
        //**************************************

        BYTE expected_result[] = { 0, 0, 0, 0, 0,       // Address defaults to Serial 0
                                   0x17,                // message type: extended tier
                                   0x18,                // flags: send a timeout and a delay value
                                   0x02,                // tier number
                                   0x38, 0x40,          // timeout value: 14400 == 0x3840
                                   0x0E, 0x10 };        // delay value: 3600 == 0x0E10, NO CRC

        BOOST_CHECK_EQUAL( xcom.parseRequest(parse), ClientErrors::None );
        BOOST_CHECK_EQUAL( xcom.entries(), 1 );
        BOOST_CHECK_EQUAL( xcom.getStartByte(), 's' ); // With CRC, these are U and V.
        BOOST_CHECK_EQUAL( xcom.getStopByte(), 't' );
        BOOST_CHECK_EQUAL( xcom.messageSize(), sizeof (expected_result) / sizeof (expected_result[0]) );

        for (int i = 0; i < xcom.messageSize(); ++i)
        {
            // Do this check by hand so we can have a nice printout on failure.
            if (xcom.getByte(i) != expected_result[i])
            {
                BOOST_CHECK_EQUAL( xcom.getByte(i), expected_result[i] );
                std::cout << "Failed when i was " << i << std::endl;
            }
        }
    }

    // Changed a single bit in the message. Ran through CDT also.
    {
        CtiCommandParser        parse( "putconfig xcom extended tier 2 timeout 14401 delay 3600" );
        CtiProtocolExpresscom   xcom;

        BYTE expected_result[] = { 0, 0, 0, 0, 0,       // Address defaults to Serial 0
                                   0x17,                // message type: extended tier
                                   0x18,                // flags: send a timeout and a delay value
                                   0x02,                // tier number
                                   0x38, 0x41,          // timeout value: 14401 == 0x3841
                                   0x0E, 0x10,          // delay value: 3600 == 0x0E10
                                   0x95, 0xA8 };        // CRC generated by CDT <>MSG CNS08 U000000000017180238410E1095A8V

        BOOST_CHECK_EQUAL( xcom.parseRequest(parse), ClientErrors::None );
        BOOST_CHECK_EQUAL( xcom.entries(), 1 );
        BOOST_CHECK_EQUAL( xcom.getStartByte(), 'u' ); // With CRC, these are U and V.
        BOOST_CHECK_EQUAL( xcom.getStopByte(), 'v' );
        BOOST_CHECK_EQUAL( xcom.messageSize(), sizeof (expected_result) / sizeof (expected_result[0]) );

        for (int i = 0; i < xcom.messageSize(); ++i)
        {
            // Do this check by hand so we can have a nice printout on failure.
            if (xcom.getByte(i) != expected_result[i])
            {
                BOOST_CHECK_EQUAL( xcom.getByte(i), expected_result[i] );
                std::cout << "Failed when i was " << i << std::endl;
            }
        }
    }
}


BOOST_AUTO_TEST_SUITE_END();

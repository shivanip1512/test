/*
    Test Expresscom address validation
*/

#define BOOST_TEST_MAIN "Test Expresscom Stuff"

#include <boost/test/unit_test.hpp>
#include "boostutil.h"

#define BOOST_AUTO_TEST_MAIN "Test Expresscom Address Validation"

using boost::unit_test_framework::test_suite;

#include "expresscom.h"

/* 
    Testing: CtiProtocolExpresscom::addAddressing()
 
    addAddressing() passes in the parameters in the exact size they are required to hold, so the
    only invalid address available is all bits set.
 
    Exceptions to the above:
 
        FEEDER is a 16 bit bitfield, hence ALL values are allowed: 0 to 0xFFFF
 
        ZIP is only 24 bits wide, but is held in a 32 bit quantity.  So values >= 0x01000000
            also return BADPARAM.
*/
BOOST_AUTO_TEST_CASE(test_xcom_addAddressing_default)
{
    CtiProtocolExpresscom   xcom;

    // all addresses are 0
    BOOST_CHECK_EQUAL( xcom.addAddressing() , NORMAL);
}

BOOST_AUTO_TEST_CASE(test_xcom_addAddressing_serial)
{
    // test the serial# -- nothing to do in this case

    BOOST_CHECK_EQUAL(true , true);
}

BOOST_AUTO_TEST_CASE(test_xcom_addAddressing_spid)
{
    CtiProtocolExpresscom   xcom;

    // test the SPID 
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, CtiProtocolExpresscom::SpidMin) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0x1234) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, CtiProtocolExpresscom::SpidMax) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, CtiProtocolExpresscom::SpidMax + 1) , BADPARAM);
}

BOOST_AUTO_TEST_CASE(test_xcom_addAddressing_geo)
{
    CtiProtocolExpresscom   xcom;

    // test the GEO
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, CtiProtocolExpresscom::GeoMin) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0x1234) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, CtiProtocolExpresscom::GeoMax) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, CtiProtocolExpresscom::GeoMax + 1) , BADPARAM);
}

BOOST_AUTO_TEST_CASE(test_xcom_addAddressing_substation)
{
    CtiProtocolExpresscom   xcom;

    // test the SUBSTATION
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, CtiProtocolExpresscom::SubstationMin) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0x1234) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, CtiProtocolExpresscom::SubstationMax) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, CtiProtocolExpresscom::SubstationMax + 1) , BADPARAM);
}

BOOST_AUTO_TEST_CASE(test_xcom_addAddressing_feeder)
{
    CtiProtocolExpresscom   xcom;

    // test the FEEDER -- all numbers are possible
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, CtiProtocolExpresscom::FeederMin) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0x1234) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, CtiProtocolExpresscom::FeederMax) , NORMAL);
}

BOOST_AUTO_TEST_CASE(test_xcom_addAddressing_zip)
{
    CtiProtocolExpresscom   xcom;

    // test the ZIP
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, CtiProtocolExpresscom::ZipMin) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0x1234) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, CtiProtocolExpresscom::ZipMax) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, CtiProtocolExpresscom::ZipMax + 1) , BADPARAM);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, CtiProtocolExpresscom::ZipMax + 10000) , BADPARAM);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, CtiProtocolExpresscom::ZipMax + 10000000) , BADPARAM);
}

BOOST_AUTO_TEST_CASE(test_xcom_addAddressing_uda)
{
    CtiProtocolExpresscom   xcom;

    // test the UDA
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::UserMin) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0x1234) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::UserMax) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::UserMax + 1) , BADPARAM);
}

BOOST_AUTO_TEST_CASE(test_xcom_addAddressing_program)
{
    CtiProtocolExpresscom   xcom;

    // test the PROGRAM
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::ProgramMin) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0, 0x12) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::ProgramMax) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::ProgramMax + 1) , BADPARAM);
}

BOOST_AUTO_TEST_CASE(test_xcom_addAddressing_splinter)
{
    CtiProtocolExpresscom   xcom;

    // test the SPLINTER
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::SplinterMin) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0, 0, 0x12) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::SplinterMax) , NORMAL);
    BOOST_CHECK_EQUAL( xcom.addAddressing(0, 0, 0, 0, 0, 0, 0, 0, CtiProtocolExpresscom::SplinterMax + 1) , BADPARAM);
}

/*
    Testing: CtiProtocolExpresscom::parseAddressing()
 
    Here the input comes in the form of a CtiCommandParser parse object.  The values specified
    aren't passed into the function in exact-width registers.  This enables testing with more values
    outside the valid ranges on the high end.
*/
BOOST_AUTO_TEST_CASE(test_xcom_parseAddressing_serial)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign serial 0" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign serial 1" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign serial 123456" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign serial 4294967295" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign serial 4294967296" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign serial 5000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }
}

BOOST_AUTO_TEST_CASE(test_xcom_parseAddressing_spid)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign spid 0" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign spid 1" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign spid 12345" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign spid 65534" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign spid 65535" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign spid 1234567" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign spid 2000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign spid 4000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }
}

BOOST_AUTO_TEST_CASE(test_xcom_parseAddressing_geo)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign geo 0" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign geo 1" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign geo 12345" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign geo 65534" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign geo 65535" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign geo 1234567" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign geo 2000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign geo 4000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }
}

BOOST_AUTO_TEST_CASE(test_xcom_parseAddressing_substation)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign sub 0" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign sub 1" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign sub 12345" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign sub 65534" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign sub 65535" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign sub 1234567" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign sub 2000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign sub 4000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }
}

BOOST_AUTO_TEST_CASE(test_xcom_parseAddressing_feeder)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign feeder 0" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign feeder 1" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign feeder 12345" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign feeder 65535" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign feeder 65536" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign feeder 1234567" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign feeder 2000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign feeder 4000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }
}

BOOST_AUTO_TEST_CASE(test_xcom_parseAddressing_zip)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 0" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 1" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 12345" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 1234567" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 16777214" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 16777215" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 123456789" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 2000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign zip 4000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }
}

BOOST_AUTO_TEST_CASE(test_xcom_parseAddressing_uda)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign uda 0" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign uda 1" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign uda 12345" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign uda 65534" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign uda 65535" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign uda 1234567" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign uda 2000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign uda 4000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }
}

BOOST_AUTO_TEST_CASE(test_xcom_parseAddressing_program)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign program 0" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign program 1" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign program 34" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign program 254" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign program 255" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign program 450" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign program 1234567" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign program 2000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign program 4000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }
}

BOOST_AUTO_TEST_CASE(test_xcom_parseAddressing_splinter)
{
    CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 0" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 1" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 34" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 254" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 255" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 450" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 1234567" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 2000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom assign splinter 4000000000" );    
        BOOST_CHECK_EQUAL( xcom.parseAddressing(parse) , BADPARAM);
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


BOOST_AUTO_TEST_CASE(test_xcom_parseTargetAddressing_serial)
{
    // test the serial# -- fixed and assigned in the factory.....

    BOOST_CHECK_EQUAL(true , true);
}

BOOST_AUTO_TEST_CASE(test_xcom_parseTargetAddressing_spid)
{
    Test_CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom target spid 0 assign spid 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 0 assign spid 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 1 assign spid 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 1 assign spid 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 1 assign spid 10000000" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 2345 assign spid 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 65534 assign spid 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 65535 assign spid 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 1234567 assign spid 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }
}

BOOST_AUTO_TEST_CASE(test_xcom_parseTargetAddressing_geo)
{
    Test_CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom target geo 0 assign geo 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target geo 0 assign geo 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target geo 1 assign geo 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target geo 1 assign geo 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target geo 1 assign geo 10000000" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target geo 2345 assign geo 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target geo 65534 assign geo 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target geo 65535 assign geo 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target geo 1234567 assign geo 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }
}

BOOST_AUTO_TEST_CASE(test_xcom_parseTargetAddressing_substation)
{
    Test_CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom target sub 0 assign sub 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target sub 0 assign sub 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target sub 1 assign sub 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target sub 1 assign sub 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target sub 1 assign sub 10000000" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target sub 2345 assign sub 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target sub 65534 assign sub 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target sub 65535 assign sub 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target sub 1234567 assign sub 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }
}

BOOST_AUTO_TEST_CASE(test_xcom_parseTargetAddressing_feeder)
{
    Test_CtiProtocolExpresscom   xcom;

    // For the "target feeder 0" we need some more target information, because the addressing
    //  level is not properly set.  I tacked on a SPID for these two tests.
    {
        CtiCommandParser    parse( "putconfig xcom target spid 1 feeder 0 assign feeder 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target spid 1 feeder 0 assign feeder 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target feeder 1 assign feeder 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target feeder 1 assign feeder 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target feeder 1 assign feeder 10000000" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target feeder 65534 assign feeder 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target feeder 65535 assign feeder 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target feeder 65536 assign feeder 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target feeder 1234567 assign feeder 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }
}

BOOST_AUTO_TEST_CASE(test_xcom_parseTargetAddressing_zip)
{
    Test_CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom target zip 0 assign zip 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target zip 0 assign zip 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target zip 1 assign zip 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target zip 1 assign zip 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target zip 1 assign zip 20000000" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target zip 777214 assign zip 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target zip 16777214 assign zip 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target zip 16777215 assign zip 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target zip 1234567890 assign zip 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }
}

BOOST_AUTO_TEST_CASE(test_xcom_parseTargetAddressing_uda)
{
    Test_CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom target uda 0 assign uda 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target uda 0 assign uda 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target uda 1 assign uda 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target uda 1 assign uda 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target uda 1 assign uda 10000000" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target uda 2345 assign uda 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target uda 65534 assign uda 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target uda 65535 assign uda 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target uda 1234567 assign uda 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }
}

BOOST_AUTO_TEST_CASE(test_xcom_parseTargetAddressing_program)
{
    Test_CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom target program 0 assign program 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 0 assign program 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 1 assign program 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 1 assign program 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 1 assign program 1000" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 34 assign program 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 254 assign program 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 255 assign program 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 500 assign program 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target program 1234567 assign program 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }
}

BOOST_AUTO_TEST_CASE(test_xcom_parseTargetAddressing_splinter)
{
    Test_CtiProtocolExpresscom   xcom;

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 0 assign splinter 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 0 assign splinter 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 1 assign splinter 0" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 1 assign splinter 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 1 assign splinter 1000" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 34 assign splinter 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 254 assign splinter 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , NORMAL);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 255 assign splinter 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 500 assign splinter 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }

    {
        CtiCommandParser    parse( "putconfig xcom target splinter 1234567 assign splinter 1" );
        BOOST_CHECK_EQUAL( xcom.test_parseTargetAddressing(parse) , BADPARAM);
    }
}


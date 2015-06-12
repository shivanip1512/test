#include <boost/test/unit_test.hpp>

#include "dev_mct4xx.h"

#include <boost/assign/list_of.hpp>

using namespace Cti::Protocols;

BOOST_AUTO_TEST_SUITE( test_dev_mct4xx )

struct test_Mct4xxDevice : Cti::Devices::Mct4xxDevice
{
    //  these are just placeholder pure-virtuals so we can instantiate this abstract base class
    const ValueMapping *getMemoryMap() const
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return 0;  }
    const FunctionReadValueMappings *getFunctionReadValueMaps() const
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return 0;  }
    frozen_point_info getDemandData( const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter ) const
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return frozen_point_info();  }
    frozen_point_info getAccumulatorData( const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter ) const
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return frozen_point_info();  };
    point_info getLoadProfileData(unsigned channel, long interval_len, const unsigned char *buf, unsigned len)
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return point_info();  }
    long getLoadProfileInterval(unsigned channel)
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return 0;  }
    YukonError_t decodeGetStatusFreeze( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return ClientErrors::None;  }
    ConfigPartsList getPartsList()
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return ConfigPartsList();  }
    bool isSupported(const Features feature) const
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return false;  }
    bool sspecValid(const unsigned sspec, const unsigned rev) const
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return false;  }
    unsigned getUsageReportDelay(const unsigned int,const unsigned int) const
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return 0;  }

    typedef Mct4xxDevice Inherited;

    typedef frozen_point_info test_point_info;  //  expose it publicly for our testing

    //  I don't know of a better way to do this - "using Inherited::ValueType4xx;" doesn't work
    enum test_ValueType4xx
    {
        ValueType_Accumulator       = Inherited::ValueType_Accumulator,
        ValueType_FrozenAccumulator = Inherited::ValueType_FrozenAccumulator,
        ValueType_Raw               = Inherited::ValueType_Raw,
    };

    test_point_info getData_Accumulator(unsigned char *buf, int len)
    {
        return Inherited::getData(buf, len, Mct4xxDevice::ValueType_Accumulator);
    }

    test_point_info getData_FrozenAccumulator(unsigned char *buf, int len)
    {
        return Inherited::getData(buf, len, Mct4xxDevice::ValueType_FrozenAccumulator);
    }

    using Mct4xxDevice::executePutConfig;
    using Mct4xxDevice::isProfileTablePointerCurrent;
    using MctDevice::getOperation;
};

BOOST_AUTO_TEST_CASE(test_dev_mct4xx_getdata)
{
    unsigned char kwh_read[3] = { 0x00, 0x01, 0x00 };

    test_Mct4xxDevice dev;
    test_Mct4xxDevice::test_point_info pi;

    pi = dev.getData_FrozenAccumulator(kwh_read, 3);

    BOOST_CHECK_EQUAL( pi.value,      256 );
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );

    kwh_read[2] = 0x01;

    pi = dev.getData_FrozenAccumulator(kwh_read, 3);

    BOOST_CHECK_EQUAL( pi.value,      257 );  //  we don't round or set the freeze bit by default
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );
}

BOOST_AUTO_TEST_CASE(test_dev_mct4xx_getdata_kwh_rounding_accumulator)
{
    unsigned char kwh_read[3] = { 0x00, 0x02, 0x00 };

    test_Mct4xxDevice dev;
    test_Mct4xxDevice::test_point_info pi;

    pi = dev.getData_Accumulator(kwh_read, 3);

    BOOST_CHECK_EQUAL( pi.value,      512 );
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );

    kwh_read[2] = 0x01;

    pi = dev.getData_Accumulator(kwh_read, 3);

    BOOST_CHECK_EQUAL( pi.value,      513 );  //  we don't round or set the freeze bit by default
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );
}

BOOST_AUTO_TEST_CASE(test_dev_mct4xx_getdata_kwh_rounding_frozen_accumulator)
{
    unsigned char kwh_read[3] = { 0x00, 0x09, 0x10 };

    test_Mct4xxDevice dev;
    test_Mct4xxDevice::test_point_info pi;

    pi = dev.getData_FrozenAccumulator(kwh_read, 3);

    BOOST_CHECK_EQUAL( pi.value,      2320 );
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );

    kwh_read[2] = 0x11;

    pi = dev.getData_FrozenAccumulator(kwh_read, 3);

    BOOST_CHECK_EQUAL( pi.value,      2321 );  //  we don't round or set the freeze bit by default
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );
}

BOOST_AUTO_TEST_CASE(test_dev_mct4xx_getdata_error_codes)
{
    unsigned char kwh_read[3] = { 0xff, 0xff, 0xfa };

    test_Mct4xxDevice dev;
    test_Mct4xxDevice::test_point_info pi;

    pi = dev.getData_Accumulator(kwh_read, 3);

    BOOST_CHECK_EQUAL( pi.value,       0 );
    BOOST_CHECK_EQUAL( pi.freeze_bit,  false );
    BOOST_CHECK_EQUAL( pi.quality,     InvalidQuality );
    BOOST_CHECK_EQUAL( pi.description, "Requested interval outside of valid range");
}

BOOST_AUTO_TEST_CASE(test_dev_mct4xx_isProfileTablePointerCurrent)
{
    test_Mct4xxDevice dev;

    // The math for the Mct4xx devices is (seconds_from_utc_midnight / interval) % 96 + 1.

    {
        CtiTime t(CtiDate(1, 1, 2011), 19, 16, 0);  //  76 minutes past UTC midnight

        long tz;
        _get_timezone(&tz);
        t.addSeconds(21600 - tz);

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(96, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent( 1, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent( 6, t, 3600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent( 7, t, 3600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(96, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent( 1, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent( 6, t, 1800));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent( 7, t, 1800));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(96, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent( 1, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent( 6, t,  900));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent( 7, t,  900));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent( 6, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent( 7, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(12, t,  600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(13, t,  600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(12, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(13, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(18, t,  300));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(19, t,  300));
    }

    {
        CtiTime t(CtiDate(1, 1, 2011), 1, 16, 0);  //  436 minutes past UTC midnight

        long tz;
        _get_timezone(&tz);
        t.addSeconds(21600 - tz);

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(78, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(79, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(84, t, 3600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(85, t, 3600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(60, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(61, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(66, t, 1800));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(67, t, 1800));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(24, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(25, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(30, t,  900));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(31, t,  900));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(90, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(91, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(96, t,  600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(97, t,  600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(84, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(85, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(90, t,  300));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(91, t,  300));
    }

    {
        CtiTime t(CtiDate(1, 1, 2011), 9, 16, 0);  //  916 minutes past UTC midnight

        long tz;
        _get_timezone(&tz);
        t.addSeconds(21600 - tz);

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(84, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(85, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(90, t, 3600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(91, t, 3600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(78, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(79, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(84, t, 1800));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(85, t, 1800));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(60, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(61, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(66, t,  900));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(67, t,  900));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(42, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(43, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(48, t,  600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(49, t,  600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(84, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(85, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(90, t,  300));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(91, t,  300));
    }

    {
        CtiTime t(CtiDate(1, 1, 2011), 12, 26, 0);  //  1106 minutes past UTC midnight

        long tz;
        _get_timezone(&tz);
        t.addSeconds(21600 - tz);

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(90, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(91, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(96, t, 3600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(97, t, 3600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(84, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(85, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(90, t, 1800));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(91, t, 1800));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(72, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(73, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(78, t,  900));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(79, t,  900));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(60, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(61, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(66, t,  600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(67, t,  600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(24, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(25, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(30, t,  300));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(31, t,  300));
    }

    {
        CtiTime t(CtiDate(2, 1, 2011), 12, 26, 0);  //  1106 minutes past UTC midnight

        long tz;
        _get_timezone(&tz);
        t.addSeconds(21600 - tz);

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(18, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(19, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(24, t, 3600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(25, t, 3600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(36, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(37, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(42, t, 1800));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(43, t, 1800));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(72, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(73, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(78, t,  900));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(79, t,  900));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(12, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(13, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(18, t,  600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(19, t,  600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(24, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(25, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(30, t,  300));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(31, t,  300));
    }
}

struct putConfig_helper
{
    test_Mct4xxDevice mct;

    test_Mct4xxDevice::CtiMessageList vgList, retList;
    test_Mct4xxDevice::OutMessageList outList;

    CtiRequestMsg request;

    OUTMESS *om;

    putConfig_helper()
    {
        om = new OUTMESS;
    }

    ~putConfig_helper()
    {
        delete om;
        delete_container( vgList );
        delete_container( retList );
        delete_container( outList );
    }
};

BOOST_FIXTURE_TEST_SUITE( test_dev_mct4xx_tou, putConfig_helper )
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
    BOOST_AUTO_TEST_CASE( test_midnight_only_schedules )
    {
        const std::vector<unsigned char> expectedMsg1 = boost::assign::list_of
                (0xff)(0xff)(0xff)(0xff)(0xff)  // Schedule 3 Durations
                (0x00)(0x00)                    // Schedule 3 Rates
                (0xff)(0xff)(0xff)(0xff)(0xff)  // Schedule 4 Durations
                (0x00)(0x00)                    // Schedule 4 Rates
                (0x00);                         // Default Rate

        const std::vector<unsigned char> expectedMsg2 = boost::assign::list_of
                (0x40)(0x00)                    // Day Table
                (0xff)(0xff)(0xff)(0xff)(0xff)  // Schedule 1 Durations
                (0x00)(0x00)                    // Schedule 1 & 2 Rates
                (0xff)(0xff)(0xff)(0xff)(0xff)  // Schedule 2 Durations
                (0x0f);                         // Schedule 2 Rates

        CtiCommandParser parse( "putconfig tou 11111112 schedule 1 a/0:00 schedule 2 d/0:00 default a" );

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( 2, outList.size() );

        test_Mct4xxDevice::OutMessageList::const_iterator om_itr = outList.begin();

        // First out message (rates and durations for schedules 3 and 4).
        {
            const OUTMESS *msg = *om_itr++;

            BOOST_REQUIRE( msg );
            BOOST_CHECK_EQUAL( msg->Buffer.BSt.Length, 15 );

            const unsigned char *results_begin = msg->Buffer.BSt.Message;
            const unsigned char *results_end   = msg->Buffer.BSt.Message + msg->Buffer.BSt.Length;

            BOOST_CHECK_EQUAL_COLLECTIONS( expectedMsg1.begin(), expectedMsg1.end(),
                                           results_begin,        results_end );
        }

        // Second out message (rates and durations for schedules 1 and 2).
        {
            const OUTMESS *msg = *om_itr++;

            BOOST_REQUIRE( msg );
            BOOST_CHECK_EQUAL( msg->Buffer.BSt.Length, 15 );

            const unsigned char *results_begin = msg->Buffer.BSt.Message;
            const unsigned char *results_end   = msg->Buffer.BSt.Message + msg->Buffer.BSt.Length;

            BOOST_CHECK_EQUAL_COLLECTIONS( expectedMsg2.begin(), expectedMsg2.end(),
                                           results_begin,        results_end );
        }
    }

    BOOST_AUTO_TEST_CASE( test_varied_tou_schedule_conditions )
    {
        const std::vector<unsigned char> expectedMsg1 = boost::assign::list_of
                (0x48)(0x48)(0x48)(0xff)(0xff)  // Schedule 3 Durations
                (0x01)(0x4e)                    // Schedule 3 Rates
                (0x60)(0x60)(0xff)(0xff)(0xff)  // Schedule 4 Durations
                (0x00)(0x53)                    // Schedule 4 Rates
                (0x00);                         // Default Rate

        const std::vector<unsigned char> expectedMsg2 = boost::assign::list_of
                (0x40)(0x00)                    // Day Table
                (0x01)(0xff)(0xff)(0xff)(0xff)  // Schedule 1 Durations
                (0x00)(0x14)                    // Schedule 1 & 2 Rates
                (0x0c)(0xff)(0xff)(0xff)(0xff)  // Schedule 2 Durations
                (0x29);                         // Schedule 2 Rates

        CtiCommandParser parse( "putconfig tou 11111112 "
                                "schedule 1 a/0:00 b/0:05 "
                                "schedule 2 b/0:00 c/1:00 "
                                "schedule 3 c/0:00 d/6:00 a/12:00 b/18:00 "
                                "schedule 4 d/0:00 a/8:00 b/16:00 "
                                "default a" );

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( 2, outList.size() );

        test_Mct4xxDevice::OutMessageList::const_iterator om_itr = outList.begin();

        // First out message (rates and durations for schedules 3 and 4).
        {
            const OUTMESS *msg = *om_itr++;

            BOOST_REQUIRE( msg );
            BOOST_CHECK_EQUAL( msg->Buffer.BSt.Length, 15 );

            const unsigned char *results_begin = msg->Buffer.BSt.Message;
            const unsigned char *results_end   = msg->Buffer.BSt.Message + msg->Buffer.BSt.Length;

            BOOST_CHECK_EQUAL_COLLECTIONS( expectedMsg1.begin(), expectedMsg1.end(),
                                           results_begin,        results_end );
        }

        // Second out message (rates and durations for schedules 1 and 2).
        {
            const OUTMESS *msg = *om_itr++;

            BOOST_REQUIRE( msg );
            BOOST_CHECK_EQUAL( msg->Buffer.BSt.Length, 15 );

            const unsigned char *results_begin = msg->Buffer.BSt.Message;
            const unsigned char *results_end   = msg->Buffer.BSt.Message + msg->Buffer.BSt.Length;

            BOOST_CHECK_EQUAL_COLLECTIONS( expectedMsg2.begin(), expectedMsg2.end(),
                                           results_begin,        results_end );
        }
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()

struct getOperation_helper
{
    test_Mct4xxDevice mct;
    BSTRUCT BSt;
};

BOOST_FIXTURE_TEST_SUITE(test_getOperation, getOperation_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
    BOOST_AUTO_TEST_CASE(test_getOperation_01)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Command_Loop, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_02)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Model, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_03)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Install, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_04)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_GroupAddressEnable, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x54);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_05)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_GroupAddressInhibit, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x53);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_06)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Raw, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_07)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Shed, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_08)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Restore, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_09)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Connect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write | 0x20);  //  Q_ARML
        BOOST_CHECK_EQUAL(BSt.Function, 0x42);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_10)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write | 0x20);  //  Q_ARML
        BOOST_CHECK_EQUAL(BSt.Function, 0x41);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_11)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_ARMC, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x62);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_12)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_ARML, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x60);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_13)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x49);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    //  MCT-4xx commands
    BOOST_AUTO_TEST_CASE(test_getOperation_14)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_TOUPeak, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xb0);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_15)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutValue_TOUReset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x5f);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_16)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutValue_ResetPFCount, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x89);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_17)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf0);
        BOOST_CHECK_EQUAL(BSt.Length,   6);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_18)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TOUEnable, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x56);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_19)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TOUDisable, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x55);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_20)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_LoadProfileInterest, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x05);
        BOOST_CHECK_EQUAL(BSt.Length,   6);
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_SUITE_END()

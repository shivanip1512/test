#include "dev_mct4xx.h"

#define BOOST_TEST_MAIN
#include <boost/test/unit_test.hpp>

using Cti::Devices::Mct4xxDevice;
using Cti::Protocols::EmetconProtocol;

using std::list;

struct test_Mct4xxDevice : Mct4xxDevice
{
    //  these are just placeholder pure-virtuals so we can instantiate this abstract base class
    const ValueMapping *getMemoryMap() const
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return 0;  };
    const FunctionReadValueMappings *getFunctionReadValueMaps() const
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return 0;  };
    point_info getDemandData(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return point_info();  };
    point_info getAccumulatorData(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return point_info();  };
    point_info getLoadProfileData(unsigned channel, const unsigned char *buf, unsigned len)
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return point_info();  };
    long getLoadProfileInterval(unsigned channel)
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return 0;  };
    INT decodeGetStatusFreeze( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return 0;  };
    ConfigPartsList getPartsList()
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return ConfigPartsList();  };
    bool isSupported(const Features feature) const
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return false;  };
    bool sspecValid(const unsigned sspec, const unsigned rev) const
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return false;  };
    unsigned getUsageReportDelay(const unsigned int,const unsigned int) const
        {  BOOST_FAIL("this virtual should never be called during this unit test");  return 0;  }

    typedef Mct4xxDevice Inherited;

    typedef point_info test_point_info;  //  expose it publicly for our testing

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

BOOST_AUTO_TEST_CASE(test_dev_mct4xx_isProfileTablePointerCurrent)
{
    test_Mct4xxDevice dev;

    // The math for the Mct4xx devices is (seconds_from_utc_midnight / interval) % 96.

    {
        const CtiTime t(CtiDate(1, 1, 2011), 19, 16, 0);  //  76 minutes past UTC midnight

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(95, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent( 0, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent( 5, t, 3600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent( 6, t, 3600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(95, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent( 0, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent( 5, t, 1800));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent( 6, t, 1800));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(95, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent( 0, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent( 5, t,  900));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent( 6, t,  900));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent( 5, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent( 6, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(11, t,  600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(12, t,  600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(11, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(12, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(17, t,  300));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(18, t,  300));
    }

    {
        const CtiTime t(CtiDate(1, 1, 2011), 1, 16, 0);  //  436 minutes past UTC midnight

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(77, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(78, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(83, t, 3600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(84, t, 3600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(59, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(60, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(65, t, 1800));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(66, t, 1800));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(23, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(24, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(29, t,  900));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(30, t,  900));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(89, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(90, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(95, t,  600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(96, t,  600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(83, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(84, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(89, t,  300));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(90, t,  300));
    }

    {
        const CtiTime t(CtiDate(1, 1, 2011), 9, 16, 0);  //  916 minutes past UTC midnight

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(83, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(84, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(89, t, 3600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(90, t, 3600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(77, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(78, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(83, t, 1800));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(84, t, 1800));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(59, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(60, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(65, t,  900));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(66, t,  900));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(41, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(42, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(47, t,  600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(48, t,  600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(83, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(84, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(89, t,  300));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(90, t,  300));
    }

    {
        const CtiTime t(CtiDate(1, 1, 2011), 12, 26, 0);  //  1106 minutes past UTC midnight

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(89, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(90, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(95, t, 3600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(96, t, 3600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(83, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(84, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(89, t, 1800));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(90, t, 1800));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(71, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(72, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(77, t,  900));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(78, t,  900));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(59, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(60, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(65, t,  600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(66, t,  600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(23, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(24, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(29, t,  300));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(30, t,  300));
    }

    {
        const CtiTime t(CtiDate(2, 1, 2011), 12, 26, 0);  //  1106 minutes past UTC midnight

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(17, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(18, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(23, t, 3600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(24, t, 3600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(35, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(36, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(41, t, 1800));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(42, t, 1800));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(71, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(72, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(77, t,  900));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(78, t,  900));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(11, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(12, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(17, t,  600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(18, t,  600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(23, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(24, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(29, t,  300));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(30, t,  300));
    }
}

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

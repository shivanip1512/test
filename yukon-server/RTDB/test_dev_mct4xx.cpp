#define BOOST_TEST_MAIN "Test dev_mct4xx"

#include "dev_mct4xx.h"

#include <boost/test/unit_test.hpp>
#include <boost/test/floating_point_comparison.hpp>

using Cti::Devices::Mct4xxDevice;

struct test_Mct4xxDevice : Mct4xxDevice
{
    //  these placeholder pure-virtual overrides should never be called in our testing, so the BOOST_CHECK(0) call is there to alert us if they are
    const read_key_store_t & getReadKeyStore(void) const                               {  BOOST_CHECK(0);  return fake_key_store;  };
    point_info getDemandData(unsigned char *buf, int len, bool is_frozen_data) const   {  BOOST_CHECK(0);  return point_info();  };
    point_info getLoadProfileData(unsigned channel, const unsigned char *buf, unsigned len)  {  BOOST_CHECK(0);  return point_info();  };
    long getLoadProfileInterval(unsigned channel)                                      {  BOOST_CHECK(0);  return 0;  };
    INT decodeGetStatusFreeze( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList ) {  BOOST_CHECK(0);  return 0;};
    ConfigPartsList getPartsList()  {  BOOST_CHECK(0);  return ConfigPartsList();  };
    bool isSupported(const Features feature) const                  {  BOOST_CHECK(0);  return false;  };
    bool sspecValid(const unsigned sspec, const unsigned rev) const {  BOOST_CHECK(0);  return false;  };

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

    read_key_store_t fake_key_store;
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

    BOOST_CHECK_EQUAL( pi.value,      256 ); // Still should be 256, we round down!
    BOOST_CHECK_EQUAL( pi.freeze_bit, true );
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

    BOOST_CHECK_EQUAL( pi.value,      512 ); // Still should be 512, we round down!
    BOOST_CHECK_EQUAL( pi.freeze_bit, true );
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

    BOOST_CHECK_EQUAL( pi.value,      2320 ); // Still should be 2320, we round down!
    BOOST_CHECK_EQUAL( pi.freeze_bit, true );
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

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent( 5, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent( 6, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(11, t, 3600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(12, t, 3600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(11, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(12, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(17, t, 1800));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(18, t, 1800));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(23, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(24, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(29, t,  900));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(30, t,  900));

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
        const CtiTime t(CtiDate(1, 1, 2011), 9, 16, 0);  //  916 minutes past UTC midnight

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(11, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(12, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(17, t, 3600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(18, t, 3600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(29, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(30, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(35, t, 1800));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(36, t, 1800));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(59, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(60, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(65, t,  900));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(66, t,  900));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(89, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(90, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(95, t,  600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent( 0, t,  600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(83, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(84, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(89, t,  300));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(90, t,  300));
    }

    {
        const CtiTime t(CtiDate(1, 1, 2011), 12, 26, 0);  //  1106 minutes past UTC midnight

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

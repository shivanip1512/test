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



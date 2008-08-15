/*
 * test CtiDeviceMCT4xx
 *
 */

#include <boost/test/unit_test.hpp>

#include "dev_mct4xx.h"

#define BOOST_AUTO_TEST_MAIN "Test MCT_4xx Device"
#include <boost/test/auto_unit_test.hpp>
using boost::unit_test_framework::test_suite;

class test_CtiDeviceMCT4xx : public CtiDeviceMCT4xx
{
    //  these virtuals should never be called in our testing, so the BOOST_CHECK(0) call is there to alert us if they are
    virtual const read_key_store_t & getReadKeyStore(void) const                               {  BOOST_CHECK(0);  return read_key_store_t();  };
    virtual point_info getDemandData(unsigned char *buf, int len) const                        {  BOOST_CHECK(0);  return point_info();  };
    virtual point_info getLoadProfileData(unsigned channel, unsigned char *buf, unsigned len)  {  BOOST_CHECK(0);  return point_info();  };
    virtual long getLoadProfileInterval(unsigned channel)                                      {  BOOST_CHECK(0);  return 0;  };

public:
    typedef CtiDeviceMCT4xx Inherited;

    typedef point_info test_point_info;  //  expose it publicly for our testing

    //  I don't know of a better way to do this - "using Inherited::ValueType4xx;" doesn't work
    enum test_ValueType4xx
    {
        ValueType_Accumulator       = Inherited::ValueType_Accumulator,
        ValueType_FrozenAccumulator = Inherited::ValueType_FrozenAccumulator,
        ValueType_Raw               = Inherited::ValueType_Raw,
    };

    test_point_info test_getData(unsigned char *buf, int len, test_ValueType4xx vt)
    {
        return Inherited::getData(buf, len, static_cast<Inherited::ValueType4xx>(vt));
    }
};

BOOST_AUTO_UNIT_TEST(test_dev_mct4xx_getdata)
{
    unsigned char kwh_read[3] = { 0x00, 0x01, 0x00 };

    test_CtiDeviceMCT4xx dev;
    test_CtiDeviceMCT4xx::test_point_info pi;

    pi = dev.test_getData(kwh_read, 3, test_CtiDeviceMCT4xx::ValueType_FrozenAccumulator);

    BOOST_CHECK_EQUAL( pi.value,      256 );
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );

    kwh_read[2] = 0x01;

    pi = dev.test_getData(kwh_read, 3, test_CtiDeviceMCT4xx::ValueType_FrozenAccumulator);

    BOOST_CHECK_EQUAL( pi.value,      256 );
    BOOST_CHECK_EQUAL( pi.freeze_bit, true );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );
}


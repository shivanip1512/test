/*
 * test CtiDeviceMCT4xx
 *
 */

#include "dev_mct4xx.h"

#include <boost/test/floating_point_comparison.hpp>

#define BOOST_TEST_MAIN "Test dev_mct4xx"
#include <boost/test/unit_test.hpp>

class test_CtiDeviceMCT4xx : public CtiDeviceMCT4xx
{
    //  these virtuals should never be called in our testing, so the BOOST_CHECK(0) call is there to alert us if they are
    virtual const read_key_store_t & getReadKeyStore(void) const                               {  BOOST_CHECK(0);  return fake_key_store;  };
    virtual point_info getDemandData(unsigned char *buf, int len, bool is_frozen_data) const   {  BOOST_CHECK(0);  return point_info();  };
    virtual point_info getLoadProfileData(unsigned channel, unsigned char *buf, unsigned len)  {  BOOST_CHECK(0);  return point_info();  };
    virtual long getLoadProfileInterval(unsigned channel)                                      {  BOOST_CHECK(0);  return 0;  };
    virtual INT decodeGetStatusFreeze( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList ) {BOOST_CHECK(0);  return 0;};
    //NOTE on above function:  This is a "BOGUS" function, that just is a place-holder, since it is declared pure-virtual in the base class.

    virtual ConfigPartsList getPartsList(){return ConfigPartsList();};

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

private:
    read_key_store_t fake_key_store;
};

BOOST_AUTO_TEST_CASE(test_dev_mct4xx_getdata)
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


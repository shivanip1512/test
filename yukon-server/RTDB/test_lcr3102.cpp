
/*
 * test Cti::Devices::LCR3102
 *
 */

#include "dev_lcr3102.h"
#include <map>

#define BOOST_TEST_MAIN "Test dev_lcr3102"
#include <boost/test/unit_test.hpp>

using boost::unit_test_framework::test_suite;

namespace Cti {
namespace Devices {

class test_LCR3102 : public LCR3102
{
public:

    typedef CtiDeviceSingle::point_info point_info;

    point_info test_getSixBitValue(unsigned char buffer[], unsigned int valuePosition, unsigned int bufferSize)
    {
        return getSixBitValueFromBuffer(buffer, valuePosition, bufferSize);
    }

    INT test_decodeGetValueIntervalLast ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
    {
        return decodeGetValueIntervalLast ( InMessage, TimeNow, vgList, retList, outList );
    }

    INT test_decodeGetValuePropCount ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
    {
        return decodeGetValuePropCount(InMessage, TimeNow, vgList, retList, outList);
    }

    // Currently untested. Some testing is done through getSixBitValueFromBuffer
    INT test_decodeGetValueHistoricalTime( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
    {
        return decodeGetValueHistoricalTime(InMessage, TimeNow, vgList, retList, outList);
    }

    INT test_decodeGetValueControlTime( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
    {
        return decodeGetValueControlTime(InMessage, TimeNow, vgList, retList, outList);
    }


    std::vector<int> test_decodeMessageAddress( BYTE Message[] )
    {
        return decodeMessageAddress(Message);
    }

    std::vector<int> test_decodeMessageSoftspec( BYTE Message[] )
    {
        return decodeMessageSoftspec(Message);
    }

    std::vector<int> test_decodeMessageSubstation( BYTE Message[] )
    {
        return decodeMessageSubstation(Message);
    }

    int test_decodeMessageTime( BYTE Message[] )
    {
        return decodeMessageTime(Message);
    }

    std::vector<int> test_decodeMessageTemperature( BYTE Message[] )
    {
        return decodeMessageTemperature(Message);
    }

    int test_decodeMessageTransmitPower( BYTE Message[] )
    {
        return decodeMessageTransmitPower(Message);
    }

    std::vector<int> test_decodeXfmrHistoricalRuntimeMessage( BYTE message[] )
    {
        return decodeXfmrHistoricalRuntimeMessage( message );
    }

    std::vector<int> test_decodeMessageDutyCycle( BYTE message[] )
    {
        return decodeMessageDutyCycle(message);
    }

private:
    typedef std::map< int, point_info > point_results_map;
    typedef std::map< int, point_info >::iterator point_results_map_iter;
    point_results_map point_results;

public:

    // Note that this always overwrites
    virtual void insertPointDataReport(CtiPointType_t type, int offset, CtiReturnMsg *rm, point_info pi, const string &default_pointname="", const CtiTime &timestamp=CtiTime(), double default_multiplier=1.0, int tags=0)
    {
        point_results.erase(offset);
        point_results.insert(std::make_pair(offset, pi));
    }

    point_info test_getPointResults(int offset)
    {
        point_info pi;
        pi.value = 0xFFFFFFFF;
        pi.quality = InvalidQuality;

        point_results_map_iter iter;

        if( (iter = point_results.find(offset)) != point_results.end() )
        {
            pi = iter->second;
        }

        return pi;
    }
};

BOOST_AUTO_TEST_CASE(test_dev_lcr3102_get6BitData)
{
    test_LCR3102::point_info result;
    test_LCR3102 device;
    static const int BUFFER_SIZE = 7;
    unsigned char buffer[BUFFER_SIZE] = {0x10, 0x03, 0xCF, 0x3C, 0xF3, 0xCF, 0x01};

    result = device.test_getSixBitValue(buffer, 0, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(4, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.test_getSixBitValue(buffer, 1, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(0, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.test_getSixBitValue(buffer, 2, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(15, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.test_getSixBitValue(buffer, 3, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(15, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.test_getSixBitValue(buffer, 4, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(15, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.test_getSixBitValue(buffer, 5, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(15, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.test_getSixBitValue(buffer, 6, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(15, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.test_getSixBitValue(buffer, 7, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(15, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.test_getSixBitValue(buffer, 8, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(0, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.test_getSixBitValue(buffer, 9, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(0x3F, result.value);
    BOOST_CHECK_EQUAL(InvalidQuality, result.quality);
    result = device.test_getSixBitValue(buffer, 10, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(0x3F, result.value);
    BOOST_CHECK_EQUAL(InvalidQuality, result.quality);
}

BOOST_AUTO_TEST_CASE(test_decode_get_interval_last)
{
    /*
    0xCC KW Used
    This is the amount of KW used in the last interval.  It includes the relay numbers, multipliers,
    and the amounts in watts.  Relay number will be the same value as sent in the request. The lowest
    relay number is always the first Watts value returned. If only one relay is specified the Watts for
    the 2nd relay should be ignored.

    Fields:
    Relay Numbers and Multiplier
    Watts for first relay (16 bit value)
    Watts for second relay (16 bit value) (optional)

    Relay Numbers And Multiplier:
    "   M2 M2 M1 M1R4R3R2R1
    "   M2M2 = is the multiplier for CT2
    "   M1M1= is the multiplier for CT1
    "   The Multipliers are set as:
    o   00 = KW
    o   01 = 1/10 KW
    o   10 = 1/100 kw
    o   11 = 1/1000 kw
    "   R4R3R2R1 is a bit field showing which Relays the CT is attached to. The highest bit is Relay 4, least bit is Relay 1. No more than 2 relays exist on a device so no more than 2 of these bits can ever be set.
    Watts:  0xFFFF means invalid data.
    */

    //PointOffset_LastIntervalBase = 1,   //      PointOffset = Base# + (load/relay# - 1)
    static const int PointOffest_intervals_relay_1 = 1;
    static const int PointOffest_intervals_relay_2 = 2;
    static const int PointOffest_intervals_relay_3 = 3;
    static const int PointOffest_intervals_relay_4 = 4;
    INMESS InMessage;
    CtiTime now;
    list< CtiMessage* > vgList;
    list< CtiMessage* > retList;
    list< OUTMESS* > outList;

    test_LCR3102 test_device;

    unsigned char flags = 0x33; // Relay 1, multiplier 1/1000 kw. Relay 2, multiplier 1 kw
    unsigned short relay_1_watts = 11; // 11/1000 kw
    unsigned short relay_2_watts = 11; // 11 kw

    InMessage.Buffer.DSt.Address = 10;
    InMessage.Buffer.DSt.Alarm   = 0;
    InMessage.Buffer.DSt.DSTFlag = 0;
    InMessage.Buffer.DSt.Power   = 0;
    InMessage.Buffer.DSt.RepVar  = 0;
    InMessage.Buffer.DSt.Time    = now.seconds();
    InMessage.Buffer.DSt.TSync   = 0;

    InMessage.Buffer.DSt.Message[0] = flags;
    InMessage.Buffer.DSt.Message[1] = relay_1_watts >> 8;
    InMessage.Buffer.DSt.Message[2] = relay_1_watts;
    InMessage.Buffer.DSt.Message[3] = relay_2_watts >> 8;
    InMessage.Buffer.DSt.Message[4] = relay_2_watts;

    test_device.test_decodeGetValueIntervalLast(&InMessage, now, vgList, retList, outList);

    test_LCR3102::point_info pi;

    pi = test_device.test_getPointResults(9876448);
    BOOST_CHECK_EQUAL(pi.quality, InvalidQuality);

    pi = test_device.test_getPointResults(PointOffest_intervals_relay_1);
    BOOST_CHECK_EQUAL(pi.quality, NormalQuality);
    BOOST_CHECK_CLOSE(pi.value, (double)11*1/1000, 0.00000001);

    pi = test_device.test_getPointResults(PointOffest_intervals_relay_2);
    BOOST_CHECK_EQUAL(pi.quality, NormalQuality);
    BOOST_CHECK_EQUAL(pi.value, 11);

    flags = 0x6C; // Relay 3, multiplier 1/100 Relay 4, multiplier 1/10
    unsigned short relay_3_watts = 0x1234;
    unsigned short relay_4_watts = 0x9873;

    InMessage.Buffer.DSt.Message[0] = flags;
    InMessage.Buffer.DSt.Message[1] = relay_3_watts >> 8;
    InMessage.Buffer.DSt.Message[2] = relay_3_watts;
    InMessage.Buffer.DSt.Message[3] = relay_4_watts >> 8;
    InMessage.Buffer.DSt.Message[4] = relay_4_watts;

    pi = test_device.test_getPointResults(PointOffest_intervals_relay_3);
    BOOST_CHECK_EQUAL(pi.quality, InvalidQuality);

    test_device.test_decodeGetValueIntervalLast(&InMessage, now, vgList, retList, outList);

    pi = test_device.test_getPointResults(PointOffest_intervals_relay_3);
    BOOST_CHECK_EQUAL(pi.quality, NormalQuality);
    BOOST_CHECK_CLOSE(pi.value, (double)relay_3_watts*1/100, 0.00000001);

    pi = test_device.test_getPointResults(PointOffest_intervals_relay_4);
    BOOST_CHECK_EQUAL(pi.quality, NormalQuality);
    BOOST_CHECK_CLOSE(pi.value, (double)relay_4_watts*1/10, 0.00000001);

    // Instead of making test_LCR3102 more fancy to the point where it needs its own unit test
    // I create a new test device so I dont have to clear out data, ect...

    // Test with only 1 relay!
    test_LCR3102 test_device_2;
    flags = 0x61; // 1, multiplier 1/100
    relay_1_watts = 0x124;

    InMessage.Buffer.DSt.Message[0] = flags;
    InMessage.Buffer.DSt.Message[1] = relay_1_watts >> 8;
    InMessage.Buffer.DSt.Message[2] = relay_1_watts;
    InMessage.Buffer.DSt.Message[3] = relay_4_watts >> 8;
    InMessage.Buffer.DSt.Message[4] = relay_4_watts;

    pi = test_device_2.test_getPointResults(PointOffest_intervals_relay_1);
    BOOST_CHECK_EQUAL(pi.quality, InvalidQuality);
    pi = test_device_2.test_getPointResults(PointOffest_intervals_relay_2);
    BOOST_CHECK_EQUAL(pi.quality, InvalidQuality);
    pi = test_device_2.test_getPointResults(PointOffest_intervals_relay_3);
    BOOST_CHECK_EQUAL(pi.quality, InvalidQuality);
    pi = test_device_2.test_getPointResults(PointOffest_intervals_relay_4);
    BOOST_CHECK_EQUAL(pi.quality, InvalidQuality);

    test_device_2.test_decodeGetValueIntervalLast(&InMessage, now, vgList, retList, outList);

    pi = test_device_2.test_getPointResults(PointOffest_intervals_relay_2);
    BOOST_CHECK_EQUAL(pi.quality, InvalidQuality);
    pi = test_device_2.test_getPointResults(PointOffest_intervals_relay_3);
    BOOST_CHECK_EQUAL(pi.quality, InvalidQuality);
    pi = test_device_2.test_getPointResults(PointOffest_intervals_relay_4);
    BOOST_CHECK_EQUAL(pi.quality, InvalidQuality);

    pi = test_device_2.test_getPointResults(PointOffest_intervals_relay_1);
    BOOST_CHECK_EQUAL(pi.quality, NormalQuality);
    BOOST_CHECK_CLOSE(pi.value, (double)relay_1_watts*1/100, 0.00000001);
}

BOOST_AUTO_TEST_CASE(test_decode_get_propcount)
{
    /*
    Propcount is a single value return
    */

    //PointOffset_PropCount        = 13,
    static const int PointOffest_Propcount = 13;
    INMESS InMessage;
    CtiTime now;
    list< CtiMessage* > vgList;
    list< CtiMessage* > retList;
    list< OUTMESS* > outList;

    test_LCR3102 test_device;

    unsigned char propcount = 0x33; // Relay 1, multiplier 1/1000 kw. Relay 2, multiplier 1 kw

    InMessage.Buffer.DSt.Address = 10;
    InMessage.Buffer.DSt.Alarm   = 0;
    InMessage.Buffer.DSt.DSTFlag = 0;
    InMessage.Buffer.DSt.Power   = 0;
    InMessage.Buffer.DSt.RepVar  = 0;
    InMessage.Buffer.DSt.Time    = now.seconds();
    InMessage.Buffer.DSt.TSync   = 0;

    InMessage.Buffer.DSt.Message[0] = propcount;

    test_device.test_decodeGetValuePropCount(&InMessage, now, vgList, retList, outList);

    test_LCR3102::point_info pi;

    pi = test_device.test_getPointResults(9876448);
    BOOST_CHECK_EQUAL(pi.quality, InvalidQuality);

    pi = test_device.test_getPointResults(PointOffest_Propcount);
    BOOST_CHECK_EQUAL(pi.quality, NormalQuality);
    BOOST_CHECK_CLOSE(pi.value, (double)propcount, 0.0000001);

    propcount = 0xFF; // This is valid

    InMessage.Buffer.DSt.Message[0] = propcount;

    test_device.test_decodeGetValuePropCount(&InMessage, now, vgList, retList, outList);

    pi = test_device.test_getPointResults(PointOffest_Propcount);
    BOOST_CHECK_EQUAL(pi.quality, NormalQuality);
    BOOST_CHECK_CLOSE(pi.value, (double)propcount, 0.0000001);

}

BOOST_AUTO_TEST_CASE(test_decode_control_time)
{
    static const int PointOffest_controltime_relay_1 = 15;
    INMESS InMessage;
    CtiTime now;
    list< CtiMessage* > vgList;
    list< CtiMessage* > retList;
    list< OUTMESS* > outList;

    // Set the InMessage's control time for relay 1 (half-seconds).
    const int relay1_controlTime = 590;
    const int expected_controlTime = 295;

    test_LCR3102 test_device;

    InMessage.Buffer.DSt.Address = 10;
    InMessage.Buffer.DSt.Alarm   = 0;
    InMessage.Buffer.DSt.DSTFlag = 0;
    InMessage.Buffer.DSt.Power   = 0;
    InMessage.Buffer.DSt.RepVar  = 0;
    InMessage.Buffer.DSt.Time    = now.seconds();
    InMessage.Buffer.DSt.TSync   = 0;

    // Set the relay to 1 and give it its control time.
    InMessage.Buffer.DSt.Message[0] = 1;
    InMessage.Buffer.DSt.Message[1] = relay1_controlTime >> 24;
    InMessage.Buffer.DSt.Message[2] = relay1_controlTime >> 16;
    InMessage.Buffer.DSt.Message[3] = relay1_controlTime >> 8;
    InMessage.Buffer.DSt.Message[4] = relay1_controlTime;

    test_device.test_decodeGetValueControlTime(&InMessage, now, vgList, retList, outList);

    test_LCR3102::point_info pi = test_device.test_getPointResults(PointOffest_controltime_relay_1);

    BOOST_CHECK_EQUAL(pi.value, expected_controlTime); // We are expecting the half seconds to be converted to seconds here!
    BOOST_CHECK_EQUAL(pi.quality, NormalQuality);
}

BOOST_AUTO_TEST_CASE(test_data_read_address)
{
    INMESS InMessage;
    test_LCR3102 test_device;

    InMessage.Buffer.DSt.Message[0] = 0x04;
    InMessage.Buffer.DSt.Message[1] = 0x08;
    InMessage.Buffer.DSt.Message[2] = 0x0f;
    InMessage.Buffer.DSt.Message[3] = 0x10;
    InMessage.Buffer.DSt.Message[4] = 0x17;
    InMessage.Buffer.DSt.Message[5] = 0x2A;
    InMessage.Buffer.DSt.Message[6] = 0x39;
    InMessage.Buffer.DSt.Message[7] = 0x44;

    std::vector<int> test_vec = test_device.test_decodeMessageAddress(InMessage.Buffer.DSt.Message);

    BOOST_CHECK_EQUAL(test_vec.at(7),  4);
    BOOST_CHECK_EQUAL(test_vec.at(6),  8);
    BOOST_CHECK_EQUAL(test_vec.at(5), 15);
    BOOST_CHECK_EQUAL(test_vec.at(4), 16);
    BOOST_CHECK_EQUAL(test_vec.at(3), 23);
    BOOST_CHECK_EQUAL(test_vec.at(2), 42);
    BOOST_CHECK_EQUAL(test_vec.at(1), 57);
    BOOST_CHECK_EQUAL(test_vec.at(0), 68);
}

BOOST_AUTO_TEST_CASE(test_data_read_sspec)
{
    INMESS InMessage;
    test_LCR3102 test_device;

    InMessage.Buffer.DSt.Message[0]  = 0xb0;
    InMessage.Buffer.DSt.Message[1]  = 0x10;
    InMessage.Buffer.DSt.Message[2]  = 0x00;
    InMessage.Buffer.DSt.Message[3]  = 0x00;
    InMessage.Buffer.DSt.Message[4]  = 0x09;
    InMessage.Buffer.DSt.Message[5]  = 0x00;
    InMessage.Buffer.DSt.Message[6]  = 0x99;
    InMessage.Buffer.DSt.Message[7]  = 0x88;
    InMessage.Buffer.DSt.Message[8]  = 0xa4;
    InMessage.Buffer.DSt.Message[9]  = 0x02;
    InMessage.Buffer.DSt.Message[10] = 0x62;
    InMessage.Buffer.DSt.Message[11] = 0xdc;
    InMessage.Buffer.DSt.Message[12] = 0x19;

    std::vector<int> test_vec = test_device.test_decodeMessageSoftspec(InMessage.Buffer.DSt.Message);

    BOOST_CHECK_EQUAL(test_vec.at(4),     2480);
    BOOST_CHECK_EQUAL(test_vec.at(3),       16);
    BOOST_CHECK_EQUAL(test_vec.at(2), 10061988);
    BOOST_CHECK_EQUAL(test_vec.at(1),      610);
    BOOST_CHECK_EQUAL(test_vec.at(0),    56345);
    BOOST_CHECK_EQUAL(InMessage.Buffer.DSt.Message[2], 0);
    BOOST_CHECK_EQUAL(InMessage.Buffer.DSt.Message[3], 0);
}

BOOST_AUTO_TEST_CASE(test_data_read_substation)
{
    INMESS InMessage;
    test_LCR3102 test_device;

    InMessage.Buffer.DSt.Message[0]  = 0x03;
    InMessage.Buffer.DSt.Message[1]  = 0x09;
    InMessage.Buffer.DSt.Message[2]  = 0x27;
    InMessage.Buffer.DSt.Message[3]  = 0x10;
    InMessage.Buffer.DSt.Message[4]  = 0xdb;
    InMessage.Buffer.DSt.Message[5]  = 0xf0;
    InMessage.Buffer.DSt.Message[6]  = 0x00;
    InMessage.Buffer.DSt.Message[7]  = 0x03;
    InMessage.Buffer.DSt.Message[8]  = 0x40;

    std::vector<int> test_vec = test_device.test_decodeMessageSubstation(InMessage.Buffer.DSt.Message);

    BOOST_CHECK_EQUAL(test_vec.at(3),      777);
    BOOST_CHECK_EQUAL(test_vec.at(2),    10000);
    BOOST_CHECK_EQUAL(test_vec.at(1), 14413824);
    BOOST_CHECK_EQUAL(test_vec.at(0),      832);
}

BOOST_AUTO_TEST_CASE(test_data_read_time)
{
    INMESS InMessage;
    test_LCR3102 test_device;

    InMessage.Buffer.DSt.Message[0]  = 0x49;
    InMessage.Buffer.DSt.Message[1]  = 0x96;
    InMessage.Buffer.DSt.Message[2]  = 0x02;
    InMessage.Buffer.DSt.Message[3]  = 0xd2;

    int utcSeconds = test_device.test_decodeMessageTime(InMessage.Buffer.DSt.Message);

    BOOST_CHECK_EQUAL(utcSeconds, 1234567890);
}

BOOST_AUTO_TEST_CASE(test_data_read_power)
{
    INMESS InMessage;
    test_LCR3102 test_device;

    InMessage.Buffer.DSt.Message[0]  = 0x0a;

    int transmitPower = test_device.test_decodeMessageTransmitPower(InMessage.Buffer.DSt.Message);

    BOOST_CHECK_EQUAL(transmitPower, 10);
}

BOOST_AUTO_TEST_CASE(test_data_read_temperature)
{
    INMESS InMessage;
    test_LCR3102 test_device;

    InMessage.Buffer.DSt.Message[0]  = 0x0f;
    InMessage.Buffer.DSt.Message[1]  = 0xa0;
    InMessage.Buffer.DSt.Message[2]  = 0x07;
    InMessage.Buffer.DSt.Message[3]  = 0xd0;

    std::vector<int> test_vec = test_device.test_decodeMessageTemperature(InMessage.Buffer.DSt.Message);

    BOOST_CHECK_EQUAL(test_vec.at(1), 4000);
    BOOST_CHECK_EQUAL(test_vec.at(0), 2000);
}

BOOST_AUTO_TEST_CASE(test_decode_xfmr_historical)
{
    INMESS InMessage;
    test_LCR3102 test_device;

    InMessage.Buffer.DSt.Message[0] = 0x04;
    InMessage.Buffer.DSt.Message[1] = 0x08;
    InMessage.Buffer.DSt.Message[2] = 0x0f;
    InMessage.Buffer.DSt.Message[3] = 0x10;
    InMessage.Buffer.DSt.Message[4] = 0x17;
    InMessage.Buffer.DSt.Message[5] = 0x2a;

    std::vector<int> runtimeHours = test_device.test_decodeXfmrHistoricalRuntimeMessage(InMessage.Buffer.DSt.Message);

    BOOST_CHECK_EQUAL(runtimeHours.at(0),  1);
    BOOST_CHECK_EQUAL(runtimeHours.at(1),  0);
    BOOST_CHECK_EQUAL(runtimeHours.at(2), 32);
    BOOST_CHECK_EQUAL(runtimeHours.at(3), 15);
    BOOST_CHECK_EQUAL(runtimeHours.at(4),  4);
    BOOST_CHECK_EQUAL(runtimeHours.at(5),  1);
    BOOST_CHECK_EQUAL(runtimeHours.at(6), 28);
    BOOST_CHECK_EQUAL(runtimeHours.at(7), 42);
}

BOOST_AUTO_TEST_CASE(test_duty_cycle)
{
    INMESS InMessage;
    test_LCR3102 test_device;

    InMessage.Buffer.DSt.Message[0]  = 0x02;
    InMessage.Buffer.DSt.Message[1]  = 0x2a;

    std::vector<int> test_vec = test_device.test_decodeMessageDutyCycle(InMessage.Buffer.DSt.Message);

    BOOST_CHECK_EQUAL(test_vec.at(1),  2);
    BOOST_CHECK_EQUAL(test_vec.at(0), 42);
}

};
};

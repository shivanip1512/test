#include <boost/test/unit_test.hpp>

#include "dev_lcr3102.h"

#include "rtdb_test_helpers.h"

using std::list;
using std::string;

struct test_Lcr3102Device : Cti::Devices::Lcr3102Device
{
    typedef CtiDeviceSingle::point_info point_info;

    using Lcr3102Device::getSixBitValueFromBuffer;
    using Lcr3102Device::decodeMessageAddress;
    using Lcr3102Device::decodeMessageSubstation;
    using Lcr3102Device::decodeMessageTime;
    using Lcr3102Device::decodeMessageTransmitPower;
    using Lcr3102Device::decodeMessageSoftspec;
    using Lcr3102Device::decodeMessageTemperature;
    using Lcr3102Device::decodeMessageXfmrHistoricalRuntime;
    using Lcr3102Device::decodeMessageDutyCycle;
    using Lcr3102Device::decodeGetValueIntervalLast;
    using Lcr3102Device::decodeGetValuePropCount;
    using Lcr3102Device::decodeGetValueHistoricalTime;
    using Lcr3102Device::decodeGetValueControlTime;
    using Lcr3102Device::executeGetValueHistorical;

    typedef std::map< int, point_info > point_results_map;
    typedef std::map< int, point_info >::iterator point_results_map_iter;
    point_results_map point_results;

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

struct resetGlobals_helper
{
    Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;
};

BOOST_FIXTURE_TEST_SUITE(test_dev_lcr3102, resetGlobals_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE

BOOST_AUTO_TEST_CASE(test_dev_lcr3102_get6BitData)
{
    test_Lcr3102Device::point_info result;
    test_Lcr3102Device device;
    static const int BUFFER_SIZE = 7;
    unsigned char buffer[BUFFER_SIZE] = {0x10, 0x03, 0xCF, 0x3C, 0xF3, 0xCF, 0x01};

    result = device.getSixBitValueFromBuffer(buffer, 0, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(4, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.getSixBitValueFromBuffer(buffer, 1, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(0, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.getSixBitValueFromBuffer(buffer, 2, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(15, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.getSixBitValueFromBuffer(buffer, 3, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(15, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.getSixBitValueFromBuffer(buffer, 4, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(15, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.getSixBitValueFromBuffer(buffer, 5, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(15, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.getSixBitValueFromBuffer(buffer, 6, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(15, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.getSixBitValueFromBuffer(buffer, 7, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(15, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.getSixBitValueFromBuffer(buffer, 8, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(0, result.value);
    BOOST_CHECK_EQUAL(NormalQuality, result.quality);
    result = device.getSixBitValueFromBuffer(buffer, 9, BUFFER_SIZE);
    BOOST_CHECK_EQUAL(0x3F, result.value);
    BOOST_CHECK_EQUAL(InvalidQuality, result.quality);
    result = device.getSixBitValueFromBuffer(buffer, 10, BUFFER_SIZE);
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

    test_Lcr3102Device test_device;

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

    test_device.decodeGetValueIntervalLast(InMessage, now, vgList, retList, outList);

    test_Lcr3102Device::point_info pi;

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

    test_device.decodeGetValueIntervalLast(InMessage, now, vgList, retList, outList);

    pi = test_device.test_getPointResults(PointOffest_intervals_relay_3);
    BOOST_CHECK_EQUAL(pi.quality, NormalQuality);
    BOOST_CHECK_CLOSE(pi.value, (double)relay_3_watts*1/100, 0.00000001);

    pi = test_device.test_getPointResults(PointOffest_intervals_relay_4);
    BOOST_CHECK_EQUAL(pi.quality, NormalQuality);
    BOOST_CHECK_CLOSE(pi.value, (double)relay_4_watts*1/10, 0.00000001);

    // Instead of making test_LCR3102 more fancy to the point where it needs its own unit test
    // I create a new test device so I dont have to clear out data, ect...

    // Test with only 1 relay!
    test_Lcr3102Device test_device_2;
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

    test_device_2.decodeGetValueIntervalLast(InMessage, now, vgList, retList, outList);

    pi = test_device_2.test_getPointResults(PointOffest_intervals_relay_2);
    BOOST_CHECK_EQUAL(pi.quality, InvalidQuality);
    pi = test_device_2.test_getPointResults(PointOffest_intervals_relay_3);
    BOOST_CHECK_EQUAL(pi.quality, InvalidQuality);
    pi = test_device_2.test_getPointResults(PointOffest_intervals_relay_4);
    BOOST_CHECK_EQUAL(pi.quality, InvalidQuality);

    pi = test_device_2.test_getPointResults(PointOffest_intervals_relay_1);
    BOOST_CHECK_EQUAL(pi.quality, NormalQuality);
    BOOST_CHECK_CLOSE(pi.value, (double)relay_1_watts*1/100, 0.00000001);

	delete_container(vgList);
	delete_container(retList);
	delete_container(outList);
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

    test_Lcr3102Device test_device;

    unsigned char propcount = 0x33; // Relay 1, multiplier 1/1000 kw. Relay 2, multiplier 1 kw

    InMessage.Buffer.DSt.Address = 10;
    InMessage.Buffer.DSt.Alarm   = 0;
    InMessage.Buffer.DSt.DSTFlag = 0;
    InMessage.Buffer.DSt.Power   = 0;
    InMessage.Buffer.DSt.RepVar  = 0;
    InMessage.Buffer.DSt.Time    = now.seconds();
    InMessage.Buffer.DSt.TSync   = 0;

    InMessage.Buffer.DSt.Message[0] = propcount;

    test_device.decodeGetValuePropCount(InMessage, now, vgList, retList, outList);

    test_Lcr3102Device::point_info pi;

    pi = test_device.test_getPointResults(9876448);
    BOOST_CHECK_EQUAL(pi.quality, InvalidQuality);

    pi = test_device.test_getPointResults(PointOffest_Propcount);
    BOOST_CHECK_EQUAL(pi.quality, NormalQuality);
    BOOST_CHECK_CLOSE(pi.value, (double)propcount, 0.0000001);

    propcount = 0xFF; // This is valid

    InMessage.Buffer.DSt.Message[0] = propcount;

    test_device.decodeGetValuePropCount(InMessage, now, vgList, retList, outList);

    pi = test_device.test_getPointResults(PointOffest_Propcount);
    BOOST_CHECK_EQUAL(pi.quality, NormalQuality);
    BOOST_CHECK_CLOSE(pi.value, (double)propcount, 0.0000001);

    delete_container(vgList);
    delete_container(retList);
    delete_container(outList);
}

BOOST_AUTO_TEST_CASE(test_decode_control_time)
{
    static const int PointOffset_controltime_relay_1 = 15,
                     PointOffset_controltime_relay_2 = 16;

    INMESS InMessage;
    CtiTime now;
    list< CtiMessage* > vgList;
    list< CtiMessage* > retList;
    list< OUTMESS* > outList;

    test_Lcr3102Device test_device;

    // Set the InMessage's control time for relay 1 (half-seconds).
    const int relay1_controlTime = 590;
    const int expected_controlTime = 295;

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

    // Revision 1.0
    {
        test_device.setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SSpecRevision, 10);

        test_device.decodeGetValueControlTime(InMessage, now, vgList, retList, outList);

        test_Lcr3102Device::point_info pi = test_device.test_getPointResults(PointOffset_controltime_relay_1);

        BOOST_CHECK_EQUAL(pi.value, expected_controlTime); // We are expecting the half seconds to be converted to seconds here!
        BOOST_CHECK_EQUAL(pi.quality, NormalQuality);
        BOOST_CHECK_EQUAL(pi.description, "Control Time Remaining Relay 1");
    }

    // Revision 1.1+ (relays are 0-based in revisions 1.1+)
    {
        test_device.setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SSpecRevision, 11);

        test_device.decodeGetValueControlTime(InMessage, now, vgList, retList, outList);

        test_Lcr3102Device::point_info pi = test_device.test_getPointResults(PointOffset_controltime_relay_2);

        BOOST_CHECK_EQUAL(pi.value, expected_controlTime); // We are expecting the half seconds to be converted to seconds here!
        BOOST_CHECK_EQUAL(pi.quality, NormalQuality);
        BOOST_CHECK_EQUAL(pi.description, "Control Time Remaining Relay 2"); // Relay + 1 should be reported here!
    }

    delete_container(vgList);
    delete_container(retList);
    delete_container(outList);
}

BOOST_AUTO_TEST_CASE(test_data_read_address)
{
    INMESS InMessage;
    test_Lcr3102Device test_device;
    int prgAddr1, prgAddr2, prgAddr3, prgAddr4, splAddr1, splAddr2, splAddr3, splAddr4;

    InMessage.Buffer.DSt.Message[0] = 0x04;
    InMessage.Buffer.DSt.Message[1] = 0x08;
    InMessage.Buffer.DSt.Message[2] = 0x0f;
    InMessage.Buffer.DSt.Message[3] = 0x10;
    InMessage.Buffer.DSt.Message[4] = 0x17;
    InMessage.Buffer.DSt.Message[5] = 0x2A;
    InMessage.Buffer.DSt.Message[6] = 0x39;
    InMessage.Buffer.DSt.Message[7] = 0x44;

    test_device.decodeMessageAddress(InMessage.Buffer.DSt.Message,
                                     prgAddr1, prgAddr2, prgAddr3, prgAddr4,
                                     splAddr1, splAddr2, splAddr3, splAddr4);

    BOOST_CHECK_EQUAL(prgAddr1,  4);
    BOOST_CHECK_EQUAL(prgAddr2,  8);
    BOOST_CHECK_EQUAL(prgAddr3, 15);
    BOOST_CHECK_EQUAL(prgAddr4, 16);
    BOOST_CHECK_EQUAL(splAddr1, 23);
    BOOST_CHECK_EQUAL(splAddr2, 42);
    BOOST_CHECK_EQUAL(splAddr3, 57);
    BOOST_CHECK_EQUAL(splAddr4, 68);
}

BOOST_AUTO_TEST_CASE(test_data_read_sspec)
{
    INMESS InMessage;
    test_Lcr3102Device test_device;
    int sspec, rev, serial, spid, geo;

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

    test_device.decodeMessageSoftspec(InMessage.Buffer.DSt.Message, sspec, rev, serial, spid, geo);

    BOOST_CHECK_EQUAL(sspec, 2480);
    BOOST_CHECK_EQUAL(rev, 16);
    BOOST_CHECK_EQUAL(serial, 10061988);
    BOOST_CHECK_EQUAL(spid, 610);
    BOOST_CHECK_EQUAL(geo, 56345);
}

BOOST_AUTO_TEST_CASE(test_data_read_substation)
{
    INMESS InMessage;
    test_Lcr3102Device test_device;
    int substation, feeder, zip, uda;

    InMessage.Buffer.DSt.Message[0]  = 0x03;
    InMessage.Buffer.DSt.Message[1]  = 0x09;
    InMessage.Buffer.DSt.Message[2]  = 0x27;
    InMessage.Buffer.DSt.Message[3]  = 0x10;
    InMessage.Buffer.DSt.Message[4]  = 0xdb;
    InMessage.Buffer.DSt.Message[5]  = 0xf0;
    InMessage.Buffer.DSt.Message[6]  = 0x00;
    InMessage.Buffer.DSt.Message[7]  = 0x03;
    InMessage.Buffer.DSt.Message[8]  = 0x40;

    test_device.decodeMessageSubstation(InMessage.Buffer.DSt.Message, substation, feeder, zip, uda);

    BOOST_CHECK_EQUAL(substation, 777);
    BOOST_CHECK_EQUAL(feeder, 10000);
    BOOST_CHECK_EQUAL(zip, 14413824);
    BOOST_CHECK_EQUAL(uda, 832);
}

BOOST_AUTO_TEST_CASE(test_data_read_time)
{
    INMESS InMessage;
    test_Lcr3102Device test_device;
    CtiTime time;

    InMessage.Buffer.DSt.Message[0]  = 0x49;
    InMessage.Buffer.DSt.Message[1]  = 0x96;
    InMessage.Buffer.DSt.Message[2]  = 0x02;
    InMessage.Buffer.DSt.Message[3]  = 0xd2;

    test_device.decodeMessageTime(InMessage.Buffer.DSt.Message, time);

    BOOST_CHECK_EQUAL(time, CtiTime::fromLocalSeconds(1234567890));
}

BOOST_AUTO_TEST_CASE(test_data_read_power)
{
    INMESS InMessage;
    test_Lcr3102Device test_device;
    int transmitPower;

    InMessage.Buffer.DSt.Message[0]  = 0x0a;

    test_device.decodeMessageTransmitPower(InMessage.Buffer.DSt.Message, transmitPower);

    BOOST_CHECK_EQUAL(transmitPower, 10);
}

BOOST_AUTO_TEST_CASE(test_data_read_temperature)
{
    INMESS InMessage;
    test_Lcr3102Device test_device;
    int txTemp, boxTemp;

    InMessage.Buffer.DSt.Message[0]  = 0x0f;
    InMessage.Buffer.DSt.Message[1]  = 0xa0;
    InMessage.Buffer.DSt.Message[2]  = 0x07;
    InMessage.Buffer.DSt.Message[3]  = 0xd0;

    test_device.decodeMessageTemperature(InMessage.Buffer.DSt.Message, txTemp, boxTemp);

    BOOST_CHECK_EQUAL(txTemp, 4000);
    BOOST_CHECK_EQUAL(boxTemp, 2000);
}

BOOST_AUTO_TEST_CASE(test_decode_xfmr_historical)
{
    INMESS InMessage;
    test_Lcr3102Device test_device;

    InMessage.Buffer.DSt.Message[0] = 0x04;
    InMessage.Buffer.DSt.Message[1] = 0x08;
    InMessage.Buffer.DSt.Message[2] = 0x0f;
    InMessage.Buffer.DSt.Message[3] = 0x10;
    InMessage.Buffer.DSt.Message[4] = 0x17;
    InMessage.Buffer.DSt.Message[5] = 0x2a;

    std::vector<test_Lcr3102Device::point_info> runtimeHours;

    test_device.decodeMessageXfmrHistoricalRuntime(InMessage.Buffer.DSt, runtimeHours);

    BOOST_CHECK_EQUAL((runtimeHours.at(0)).value,  1);
    BOOST_CHECK_EQUAL((runtimeHours.at(1)).value,  0);
    BOOST_CHECK_EQUAL((runtimeHours.at(2)).value, 32);
    BOOST_CHECK_EQUAL((runtimeHours.at(3)).value, 15);
    BOOST_CHECK_EQUAL((runtimeHours.at(4)).value,  4);
    BOOST_CHECK_EQUAL((runtimeHours.at(5)).value,  1);
    BOOST_CHECK_EQUAL((runtimeHours.at(6)).value, 28);
    BOOST_CHECK_EQUAL((runtimeHours.at(7)).value, 42);
}

BOOST_AUTO_TEST_CASE(test_duty_cycle)
{
    INMESS InMessage;
    test_Lcr3102Device test_device;
    int dutyCycle, currentTransformer;

    InMessage.Buffer.DSt.Message[0]  = 0x02;
    InMessage.Buffer.DSt.Message[1]  = 0x2a;

    test_device.decodeMessageDutyCycle(InMessage.Buffer.DSt.Message, dutyCycle, currentTransformer);

    BOOST_CHECK_EQUAL(currentTransformer, 2);
    BOOST_CHECK_EQUAL(dutyCycle, 42);
}

struct beginExecuteRequest_helper
{
    CtiRequestMsg           request;
    std::list<CtiMessage*>  vgList, retList;
    std::list<OUTMESS*>     outList;

    ~beginExecuteRequest_helper()
    {
        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);
    }
};

BOOST_FIXTURE_TEST_SUITE(test_execute_getvalue, beginExecuteRequest_helper)
//{ Brace matching for BOOST_FIXTURE_TEST_SUITE
    BOOST_AUTO_TEST_CASE(test_execute_getvalue_historical_three_read)
    {
        OUTMESS *OutMessage = new OUTMESS;
        test_Lcr3102Device test_device;
        const long userMessageId = 42, connectionHandle = 0;
        const std::string command = "getvalue runtime load 1 previous 36 update noqueue";
        CtiCommandParser parse(command);

        request.setUserMessageId(userMessageId);
        request.setConnectionHandle(connectionHandle);

        test_device.executeGetValueHistorical(&request, parse, OutMessage, outList);

        BOOST_CHECK_EQUAL(test_device.getGroupMessageCount(userMessageId, connectionHandle), 3);

        BOOST_CHECK_EQUAL(outList.size(), 3);

        CtiDeviceSingle::OutMessageList::const_iterator itr = outList.begin();

        {
            const OUTMESS *om = *itr++;

            BOOST_REQUIRE( om );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,        Cti::Protocols::EmetconProtocol::IO_Function_Read );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function,  0xb0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,    13 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr,   command);
            BOOST_CHECK_EQUAL( om->Request.OptionsField, 1);
        }

        {
            const OUTMESS *om = *itr++;

            BOOST_REQUIRE( om );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,        Cti::Protocols::EmetconProtocol::IO_Function_Read );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function,  0xb4 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,    13 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr,   command);
            BOOST_CHECK_EQUAL( om->Request.OptionsField, 2);
        }

        {
            const OUTMESS *om = *itr++;

            BOOST_REQUIRE( om );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,        Cti::Protocols::EmetconProtocol::IO_Function_Read );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function,  0xb8 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,    4 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr,   command);
            BOOST_CHECK_EQUAL( om->Request.OptionsField, 3);
        }

        delete OutMessage;
        OutMessage = NULL;
    }

    BOOST_AUTO_TEST_CASE(test_execute_getvalue_historical_12_read)
    {
        OUTMESS *OutMessage = new OUTMESS;
        test_Lcr3102Device test_device;
        const long userMessageId = 42, connectionHandle = 0;
        const std::string command = "getvalue runtime load 1 previous 12 update noqueue";
        CtiCommandParser parse(command);

        request.setUserMessageId(userMessageId);
        request.setConnectionHandle(connectionHandle);

        test_device.executeGetValueHistorical(&request, parse, OutMessage, outList);

        BOOST_CHECK_EQUAL(test_device.getGroupMessageCount(userMessageId, connectionHandle), 0);

        BOOST_CHECK_EQUAL(outList.size(), 1);

        CtiDeviceSingle::OutMessageList::const_iterator itr = outList.begin();

        {
            const OUTMESS *om = *itr++;

            BOOST_REQUIRE( om );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,        Cti::Protocols::EmetconProtocol::IO_Function_Read );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function,  0xb0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,    13 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr,   command);
            BOOST_CHECK_EQUAL( om->Request.OptionsField, 0);
        }

        delete OutMessage;
        OutMessage = NULL;
    }

    BOOST_AUTO_TEST_CASE(test_execute_getvalue_historical_24_read)
    {
        OUTMESS *OutMessage = new OUTMESS;
        test_Lcr3102Device test_device;
        const long userMessageId = 42, connectionHandle = 0;
        const std::string command = "getvalue runtime load 1 previous 24 update noqueue";
        CtiCommandParser parse(command);

        request.setUserMessageId(userMessageId);
        request.setConnectionHandle(connectionHandle);

        test_device.executeGetValueHistorical(&request, parse, OutMessage, outList);

        BOOST_CHECK_EQUAL(test_device.getGroupMessageCount(userMessageId, connectionHandle), 2);

        BOOST_CHECK_EQUAL(outList.size(), 2);

        CtiDeviceSingle::OutMessageList::const_iterator itr = outList.begin();

        {
            const OUTMESS *om = *itr++;

            BOOST_REQUIRE( om );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,        Cti::Protocols::EmetconProtocol::IO_Function_Read );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function,  0xb0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,    13 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr,   command);
            BOOST_CHECK_EQUAL( om->Request.OptionsField, 1);
        }

        {
            const OUTMESS *om = *itr++;

            BOOST_REQUIRE( om );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,        Cti::Protocols::EmetconProtocol::IO_Function_Read );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function,  0xb4 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,    13 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr,   command);
            BOOST_CHECK_EQUAL( om->Request.OptionsField, 2);
        }

        delete OutMessage;
        OutMessage = NULL;
    }

    BOOST_AUTO_TEST_CASE(test_execute_getvalue_historical_retry_32_read)
    {
        OUTMESS *OutMessage = new OUTMESS;
        test_Lcr3102Device test_device;
        const long userMessageId = 42, connectionHandle = 0;
        const std::string command = "getvalue runtime load 1 previous 32 update noqueue";
        CtiCommandParser parse(command);

        request.setUserMessageId(userMessageId);
        request.setConnectionHandle(connectionHandle);

         // This is a retry of the second read. Setup the RequestMessage as such.
        request.setOptionsField(2);

        test_device.executeGetValueHistorical(&request, parse, OutMessage, outList);

        BOOST_CHECK_EQUAL(test_device.getGroupMessageCount(userMessageId, connectionHandle), 0);

        BOOST_CHECK_EQUAL(outList.size(), 1);

        CtiDeviceSingle::OutMessageList::const_iterator itr = outList.begin();

        {
            const OUTMESS *om = *itr++;

            BOOST_REQUIRE( om );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,        Cti::Protocols::EmetconProtocol::IO_Function_Read );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function,  0xb4 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,    13 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr,   command);
            BOOST_CHECK_EQUAL( om->Request.OptionsField, 2);
        }

        delete OutMessage;
        OutMessage = NULL;
    }

    BOOST_AUTO_TEST_CASE(test_execute_getvalue_historical_retry_36_read)
    {
        OUTMESS *OutMessage = new OUTMESS;
        test_Lcr3102Device test_device;
        const long userMessageId = 42, connectionHandle = 0;
        const std::string command = "getvalue runtime load 1 previous 36 update noqueue";
        CtiCommandParser parse(command);

        request.setUserMessageId(userMessageId);
        request.setConnectionHandle(connectionHandle);

         // This is a retry of the third read. Set up the RequestMessage as such.
        request.setOptionsField(3);

        test_device.executeGetValueHistorical(&request, parse, OutMessage, outList);

        BOOST_CHECK_EQUAL(test_device.getGroupMessageCount(userMessageId, connectionHandle), 0);

        BOOST_CHECK_EQUAL(outList.size(), 1);

        CtiDeviceSingle::OutMessageList::const_iterator itr = outList.begin();

        {
            const OUTMESS *om = *itr++;

            BOOST_REQUIRE( om );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,        Cti::Protocols::EmetconProtocol::IO_Function_Read );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function,  0xb8 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,    4 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr,   command);
            BOOST_CHECK_EQUAL( om->Request.OptionsField, 3);
        }

        delete OutMessage;
        OutMessage = NULL;
    }

    BOOST_AUTO_TEST_CASE(test_execute_getvalue_historical_retry_17)
    {
        OUTMESS *OutMessage = new OUTMESS;
        test_Lcr3102Device test_device;
        const long userMessageId = 42, connectionHandle = 0;
        const std::string command = "getvalue runtime load 1 previous 17 update noqueue";
        CtiCommandParser parse(command);

        request.setUserMessageId(userMessageId);
        request.setConnectionHandle(connectionHandle);

        test_device.executeGetValueHistorical(&request, parse, OutMessage, outList);

        BOOST_CHECK_EQUAL(test_device.getGroupMessageCount(userMessageId, connectionHandle), 2);

        BOOST_CHECK_EQUAL(outList.size(), 2);

        CtiDeviceSingle::OutMessageList::const_iterator itr = outList.begin();

        {
            const OUTMESS *om = *itr++;

            BOOST_REQUIRE( om );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,        Cti::Protocols::EmetconProtocol::IO_Function_Read );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function,  0xb0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,    13 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr,   command);
            BOOST_CHECK_EQUAL( om->Request.OptionsField, 1);
        }

        {
            const OUTMESS *om = *itr++;

            BOOST_REQUIRE( om );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,        Cti::Protocols::EmetconProtocol::IO_Function_Read );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function,  0xb4 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,    13 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr,   command);
            BOOST_CHECK_EQUAL( om->Request.OptionsField, 2);
        }

        delete OutMessage;
        OutMessage = NULL;
    }
//} Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_SUITE_END()

#include <boost/test/unit_test.hpp>

#include "dev_mct410.h"
#include "dev_ccu.h"
#include "rte_ccu.h"
#include "pt_analog.h"
#include "pt_accum.h"
#include "pt_status.h"
#include "devicetypes.h"
#include "mgr_config.h"

#include "boost_test_helpers.h"

#include <boost/assign/list_of.hpp>

using namespace Cti::Protocols;
using std::string;
using std::list;
using std::vector;


class test_ConfigManager : public CtiConfigManager
{
    Cti::Config::DeviceConfigSPtr   _config;

public:

    test_ConfigManager( Cti::Config::DeviceConfigSPtr config )
        :   _config( config )
    {
        // empty
    }

    virtual Cti::Config::DeviceConfigSPtr   fetchConfig( const long deviceID, const DeviceTypes deviceType )
    {
        return _config;
    }
};

struct test_CtiDeviceCCU : CtiDeviceCCU
{
    test_CtiDeviceCCU()
    {
        _paObjectID = 12345;
    }
};

struct test_CtiRouteCCU : CtiRouteCCU
{
    CtiDeviceSPtr ccu;

    test_CtiRouteCCU() : ccu(new test_CtiDeviceCCU)
    {
        _tblPAO.setID(1234);
        setDevicePointer(ccu);
    }
};

struct test_Mct410Device : Cti::Devices::Mct410Device
{
    long demand_interval;

protected:
    CtiRouteSPtr rte;

    test_Mct410Device(int type, const string &name) :
        rte(new test_CtiRouteCCU)
    {
        setType(type);
        _name = name;
        _paObjectID = 123456;
        demand_interval = 3600;
    }

public:
    typedef Mct410Device::point_info point_info;

    using MctDevice::getOperation;
    using MctDevice::ReadDescriptor;
    using MctDevice::value_locator;
    using MctDevice::getDescriptorForRead;
    using MctDevice::ResultDecode;

    using Mct4xxDevice::getUsageReportDelay;

    using Mct410Device::getDemandData;
    using Mct410Device::extractDynamicPaoInfo;
    using Mct410Device::executeGetConfig;
    using Mct410Device::executeGetValue;
    using Mct410Device::executePutConfig;
    using Mct410Device::executePutStatus;

    using Mct410Device::decodeDisconnectStatus;
    using Mct410Device::decodeDisconnectConfig;
    using Mct410Device::decodeDisconnectDemandLimitConfig;
    using Mct410Device::decodeDisconnectCyclingConfig;
    using Mct410Device::decodeGetValueDailyRead;
    using Mct410Device::decodeGetValueOutage;

    using Mct410Device::isDailyReadVulnerableToAliasing;

    virtual LONG getDemandInterval()
    {
        return demand_interval;
    }

    typedef std::map<int, CtiPointSPtr>              PointOffsetMap;
    typedef std::map<CtiPointType_t, PointOffsetMap> PointTypeOffsetMap;

    PointTypeOffsetMap points;

    bool test_isSupported_Mct410Feature_HourlyKwh() const
            {  return isSupported(Feature_HourlyKwh);  }

    bool test_isSupported_Mct410Feature_DisconnectCollar() const
            {  return isSupported(Feature_DisconnectCollar);  }

    bool test_isSupported_Mct4xxFeature_LoadProfilePeakReport() const
            {  return isSupported(Feature_LoadProfilePeakReport);  }

    bool test_isSupported_Mct4xxFeature_TouPeaks() const
            {  return isSupported(Feature_TouPeaks);  }

    virtual CtiPointSPtr getDevicePointOffsetTypeEqual(int offset, CtiPointType_t type)
    {
        CtiPointSPtr point = points[type][offset];

        if( point )
        {
            return point;
        }

        unsigned point_count = 0;

        for each( const PointTypeOffsetMap::value_type &p in points )
        {
            point_count += p.second.size();
        }

        switch( type )
        {
            case AnalogPointType:
            {
                Test_CtiPointAnalog *analog = new Test_CtiPointAnalog();
                analog->setName(desolvePointType(type) + CtiNumStr(offset));
                analog->setPointOffset(offset);
                analog->setDeviceID(reinterpret_cast<long>(&points));
                analog->setID(point_count);
                point.reset(analog);
            }
            break;

            case PulseAccumulatorPointType:
            case DemandAccumulatorPointType:
            {
                Test_CtiPointAccumulator *accumulator = new Test_CtiPointAccumulator();
                accumulator->setName(desolvePointType(type) + CtiNumStr(offset));
                accumulator->setPointOffset(offset);
                accumulator->setDeviceID(reinterpret_cast<long>(&points));
                accumulator->setID(point_count);
                point.reset(accumulator);
            }
            break;

            case StatusPointType:
            {
                Test_CtiPointStatus *status = new Test_CtiPointStatus();
                status->setName(desolvePointType(type) + CtiNumStr(offset));
                status->setPointOffset(offset);
                status->setDeviceID(reinterpret_cast<long>(&points));
                status->setID(point_count);
                point.reset(status);
            }
            break;
        }

        return point;
    }

    virtual CtiRouteSPtr getRoute() const
    {
        return rte;
    }
};

struct test_DeviceConfig : public Cti::Config::DeviceConfig
{
    test_DeviceConfig() : DeviceConfig(-1, string()) {}

    using DeviceConfig::insertValue;
};

struct test_Mct410IconDevice : test_Mct410Device
{
    test_Mct410IconDevice() :
        test_Mct410Device(TYPEMCT410IL, "Test MCT-410iL")
    {
    }
};

struct test_Mct410CentronDevice : test_Mct410Device
{
    test_Mct410CentronDevice() :
        test_Mct410Device(TYPEMCT410CL, "Test MCT-410cL")
    {
    }
};

struct test_Mct410FocusDevice : test_Mct410Device
{
    test_Mct410FocusDevice() :
        test_Mct410Device(TYPEMCT410FL, "Test MCT-410fL")
    {
    }
};

namespace std {
    //  defined in rtdb/test_main.cpp
    ostream& operator<<(ostream& out, const unsigned char &uc);
    ostream& operator<<(ostream& out, const test_Mct410Device::ReadDescriptor &rd);
    ostream& operator<<(ostream& out, const vector<boost::tuples::tuple<unsigned, unsigned, int>> &rd);
    bool operator==(const test_Mct410Device::value_locator &lhs, const boost::tuples::tuple<unsigned, unsigned, int> &rhs);
}

namespace boost {
namespace test_tools {
    //  defined in rtdb/test_main.cpp
    bool operator!=(const test_Mct410Device::ReadDescriptor &lhs, const std::vector<boost::tuples::tuple<unsigned, unsigned, int>> &rhs);
}
}

BOOST_AUTO_TEST_SUITE( test_dev_mct410 )

BOOST_AUTO_TEST_CASE(test_isSupported_Mct410Feature_DisconnectCollar)
{
    BOOST_CHECK_EQUAL(true, test_Mct410CentronDevice()
                                .test_isSupported_Mct410Feature_DisconnectCollar());

    BOOST_CHECK_EQUAL(true, test_Mct410FocusDevice()
                                .test_isSupported_Mct410Feature_DisconnectCollar());

    BOOST_CHECK_EQUAL(true, test_Mct410IconDevice()
                                .test_isSupported_Mct410Feature_DisconnectCollar());
}

BOOST_AUTO_TEST_CASE(test_isSupported_Mct410Feature_HourlyKwh)
{
    test_Mct410CentronDevice mct;

    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 10291);
    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 40);

    BOOST_CHECK_EQUAL(false, mct.test_isSupported_Mct410Feature_HourlyKwh());

    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 1029);
    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 20);

    BOOST_CHECK_EQUAL(false, mct.test_isSupported_Mct410Feature_HourlyKwh());

    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 1029);
    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 40);

    BOOST_CHECK_EQUAL(true, mct.test_isSupported_Mct410Feature_HourlyKwh());
}

BOOST_AUTO_TEST_CASE(test_isSupported_Mct4xxFeature_LoadProfilePeakReport)
{
    test_Mct410CentronDevice mct;

    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 10291);
    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 9);

    BOOST_CHECK_EQUAL(false, mct.test_isSupported_Mct4xxFeature_LoadProfilePeakReport());

    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 1029);
    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 1);

    BOOST_CHECK_EQUAL(false, mct.test_isSupported_Mct4xxFeature_LoadProfilePeakReport());

    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 1029);
    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 9);

    BOOST_CHECK_EQUAL(true, mct.test_isSupported_Mct4xxFeature_LoadProfilePeakReport());
}

BOOST_AUTO_TEST_CASE(test_isSupported_Mct4xxFeature_TouPeaks)
{
    test_Mct410CentronDevice mct;

    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 10291);
    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 13);

    BOOST_CHECK_EQUAL(false, mct.test_isSupported_Mct4xxFeature_TouPeaks());

    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 1029);
    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 12);

    BOOST_CHECK_EQUAL(false, mct.test_isSupported_Mct4xxFeature_TouPeaks());

    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 1029);
    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 13);

    BOOST_CHECK_EQUAL(true, mct.test_isSupported_Mct4xxFeature_TouPeaks());
}

BOOST_AUTO_TEST_CASE(test_dev_mct410_getDemandData)
{
    test_Mct410IconDevice dev;

    const unsigned char *none = 0;

    const unsigned char even = 12;
    const unsigned char odd  = 13;

    struct demand_checks
    {
        const unsigned char raw_value[2];
        const unsigned char *freeze_counter;
        const double value;
        const bool freeze_bit;
    };

    demand_checks dc[10] = {
        {{0x30, 0x05},  none, 0.005, 1},
        {{0x30, 0x05}, &odd,  0.004, 1},
        {{0x30, 0x04},  none, 0.004, 0},
        {{0x30, 0x04}, &even, 0.004, 0},
        {{0x2f, 0x0f},  none, 38.55, 1},
        {{0x2f, 0x0f}, &odd,  38.54, 1},
        {{0x2f, 0x0e},  none, 38.54, 0},
        {{0x2f, 0x0e}, &even, 38.54, 0},
        {{0x01, 0x11},  none, 273,   1},
        {{0x01, 0x11}, &odd,  272,   1}};

    test_Mct410Device::point_info pi;

    pi = dev.getDemandData(dc[0].raw_value, 2, dc[0].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[0].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[0].freeze_bit);

    pi = dev.getDemandData(dc[1].raw_value, 2, dc[1].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[1].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[1].freeze_bit);

    pi = dev.getDemandData(dc[2].raw_value, 2, dc[2].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[2].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[2].freeze_bit);

    pi = dev.getDemandData(dc[3].raw_value, 2, dc[3].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[3].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[3].freeze_bit);

    pi = dev.getDemandData(dc[4].raw_value, 2, dc[4].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[4].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[4].freeze_bit);

    pi = dev.getDemandData(dc[5].raw_value, 2, dc[5].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[5].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[5].freeze_bit);

    pi = dev.getDemandData(dc[6].raw_value, 2, dc[6].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[6].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[6].freeze_bit);

    pi = dev.getDemandData(dc[7].raw_value, 2, dc[7].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[7].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[7].freeze_bit);

    pi = dev.getDemandData(dc[8].raw_value, 2, dc[8].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[8].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[8].freeze_bit);

    pi = dev.getDemandData(dc[9].raw_value, 2, dc[9].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[9].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[9].freeze_bit);
}


BOOST_AUTO_TEST_CASE(test_decodePulseAccumulator)
{
    unsigned char kwh_read[3] = { 0x00, 0x02, 0x00 };

    CtiDeviceSingle::point_info pi;

    pi = Cti::Devices::Mct410Device::decodePulseAccumulator(kwh_read, 3, 0);

    BOOST_CHECK_EQUAL( pi.value,      512 );
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );

    kwh_read[2] = 0x01;

    pi = Cti::Devices::Mct410Device::decodePulseAccumulator(kwh_read, 3, 0);

    BOOST_CHECK_EQUAL( pi.value,      512 );
    BOOST_CHECK_EQUAL( pi.freeze_bit, true );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_extractDynamicPaoInfo)
{
    test_Mct410IconDevice dev;

    INMESS im;

    im.Buffer.DSt.Message[ 0] = 0xff;
    im.Buffer.DSt.Message[ 1] = 0xfe;
    im.Buffer.DSt.Message[ 2] = 0xfd;
    im.Buffer.DSt.Message[ 3] = 0xfc;
    im.Buffer.DSt.Message[ 4] = 0xfb;
    im.Buffer.DSt.Message[ 5] = 0xfa;
    im.Buffer.DSt.Message[ 5] = 0xf9;
    im.Buffer.DSt.Message[ 6] = 0xf8;
    im.Buffer.DSt.Message[ 7] = 0xf7;
    im.Buffer.DSt.Message[ 8] = 0xf6;
    im.Buffer.DSt.Message[ 9] = 0xf5;
    im.Buffer.DSt.Message[10] = 0xf4;
    im.Buffer.DSt.Message[11] = 0xf3;
    im.Buffer.DSt.Message[12] = 0xf2;

    im.Buffer.DSt.Length = 13;
    im.Return.ProtocolInfo.Emetcon.Function = 0;
    im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Read;

    dev.extractDynamicPaoInfo(im);

    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Options));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2));
    //  unavailable - only the first byte available in the range 0x00 - 0x0c
    BOOST_CHECK(!dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask));

    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec),           0xfbff);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision),   0xfe);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Options),         0xfd);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration),   0xfc);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1), 0xf4);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2), 0xf3);

    im.Buffer.DSt.Length = 2;
    im.Return.ProtocolInfo.Emetcon.Function = 18;
    im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Read;

    dev.extractDynamicPaoInfo(im);

    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressBronze));
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressBronze), 0xfe);

    im.Buffer.DSt.Length = 3;
    im.Return.ProtocolInfo.Emetcon.Function = 0xad; //  FuncRead_TOUDaySchedulePos;  it's protected, so instead of inheriting it, it's a magic number
    im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Function_Read;

    dev.extractDynamicPaoInfo(im);

    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DayTable));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate));
    //  outside the collected range
    BOOST_CHECK(!dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset));

    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DayTable),       0xfffe);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate), 0xfd);
}


struct executeRequest_helper
{
    test_Mct410IconDevice mct410;

    test_Mct410Device::CtiMessageList vgList, retList;
    test_Mct410Device::OutMessageList outList;

    ~executeRequest_helper()
    {
        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);
    }
};

BOOST_FIXTURE_TEST_SUITE(requests, executeRequest_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE

    BOOST_AUTO_TEST_CASE(test_dev_mct_control_connect_execute)
    {
        CtiRequestMsg    req( -1, "control connect" );
        CtiCommandParser parse( req.CommandString() );

        //mct410.setDisconnectAddress(123);  //  unnecessary for "control connect"...  bug?

        BOOST_CHECK_EQUAL( NoError , mct410.beginExecuteRequest(&req, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  1, outList.size() );

        OUTMESS *om = outList.front();

        BOOST_REQUIRE( om );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 76 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       0 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   0 );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct_control_connect_decode)
    {
        CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

        INMESS im;

        im.Sequence = EmetconProtocol::Control_Connect;
        im.Buffer.DSt.Length = 0;
        im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

        strcpy(im.Return.CommandStr, "control connect");

        BOOST_CHECK_EQUAL( NoError, mct410.ResultDecode(&im, timeNow, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( 2, retList.size() );
        BOOST_CHECK( outList.empty() );

        const std::vector<const CtiMessage *> retMsgs(retList.begin(), retList.end());

        const CtiRequestMsg *req = dynamic_cast<const CtiRequestMsg *>(retMsgs[0]);

        BOOST_REQUIRE( req );
        BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( req->CommandString(), "getstatus disconnect" );

        const CtiReturnMsg *ret = dynamic_cast<const CtiReturnMsg *>(retMsgs[1]);

        BOOST_REQUIRE( ret );
        BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( ret->Status(),   0 );
        BOOST_CHECK_EQUAL( ret->CommandString(), "control connect" );
        BOOST_CHECK_EQUAL( ret->ResultString(),  "Test MCT-410iL / control sent" );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct_control_disconnect)
    {
        CtiRequestMsg    req( -1, "control disconnect" );
        CtiCommandParser parse( req.CommandString() );

        mct410.setDisconnectAddress(123);

        BOOST_CHECK_EQUAL( NoError, mct410.beginExecuteRequest(&req, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );
        BOOST_REQUIRE_EQUAL( 1, outList.size() );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE( om );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 77 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       0 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   0 );
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct_control_disconnect_decode)
    {
        CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

        INMESS im;

        im.Sequence = EmetconProtocol::Control_Disconnect;
        im.Buffer.DSt.Length = 0;
        im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

        strcpy(im.Return.CommandStr, "control disconnect");

        BOOST_CHECK_EQUAL( NoError, mct410.ResultDecode(&im, timeNow, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( 2, retList.size() );
        BOOST_CHECK( outList.empty() );

        const std::vector<const CtiMessage *> retMsgs(retList.begin(), retList.end());

        const CtiRequestMsg *req = dynamic_cast<const CtiRequestMsg *>(retMsgs[0]);

        BOOST_REQUIRE( req );
        BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( req->CommandString(), "getstatus disconnect" );

        const CtiReturnMsg *ret = dynamic_cast<const CtiReturnMsg *>(retMsgs[1]);

        BOOST_REQUIRE( ret );
        BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( ret->Status(),   0 );
        BOOST_CHECK_EQUAL( ret->CommandString(), "control disconnect" );
        BOOST_CHECK_EQUAL( ret->ResultString(),  "Test MCT-410iL / control sent" );
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


struct mctExecute_helper : executeRequest_helper
{
    CtiRequestMsg request;
    OUTMESS *om;

    mctExecute_helper()
    {
        om = new OUTMESS;
    }
    ~mctExecute_helper()
    {
        delete om;
    }
};

BOOST_FIXTURE_TEST_SUITE(command_executions, mctExecute_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
    /*
    *** TESTING: "putconfig emetcon centron...." commands and parameters
    */
    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 5x1 test 0s errors disable" );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  2 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x00 , om->Buffer.BSt.Message[1] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 40 display 5x1 test 0s errors disable" );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  3 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x00 , om->Buffer.BSt.Message[1] );
        BOOST_CHECK_EQUAL(   40 , om->Buffer.BSt.Message[2] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_4x1_1s_enable)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 4x1 test 1s errors enable" );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  2 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x15 , om->Buffer.BSt.Message[1] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_4x1_1s_enable)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 60 display 4x1 test 1s errors enable" );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  3 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x15 , om->Buffer.BSt.Message[1] );
        BOOST_CHECK_EQUAL(   60 , om->Buffer.BSt.Message[2] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_4x10_7s_enable)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 4x10 test 7s errors enable" );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  2 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x1E , om->Buffer.BSt.Message[1] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_4x10_7s_enable)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 200 display 4x10 test 7s errors enable" );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  3 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x1E , om->Buffer.BSt.Message[1] );
        BOOST_CHECK_EQUAL(  200 , om->Buffer.BSt.Message[2] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_invalid_display)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 5x3 test 0s errors disable" );
                                                               //  5x3 is not a valid option
        BOOST_CHECK_EQUAL(    0 , retList.size() );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  1 , retList.size() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( errorMsg );

        BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
        BOOST_CHECK_EQUAL( "Test MCT-410iL / Invalid display configuration \"5x3\"" , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_invalid_display)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 40 display 5x3 test 0s errors disable" );
                                                                        //  5x3 is not a valid option
        BOOST_CHECK_EQUAL(    0 , retList.size() );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  1 , retList.size() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( errorMsg );

        BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
        BOOST_CHECK_EQUAL( "Test MCT-410iL / Invalid display configuration \"5x3\"" , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_invalid_test)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 5x1 test 3s errors disable" );
                                                                         // 3 is not a valid option
        BOOST_CHECK_EQUAL(    0 , retList.size() );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  1 , retList.size() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( errorMsg );

        BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
        BOOST_CHECK_EQUAL( "Test MCT-410iL / Invalid test duration \"3\"" , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_invalid_test)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 40 display 5x1 test 3s errors disable" );
                                                                                  // 3 is not a valid option
        BOOST_CHECK_EQUAL(    0 , retList.size() );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  1 , retList.size() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( errorMsg );

        BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
        BOOST_CHECK_EQUAL( "Test MCT-410iL / Invalid test duration \"3\"" , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_out_of_bounds)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 400 display 5x1 test 0s errors disable" );
                                                              // 400 is not a valid option ( 0 <= ratio <= 255 )
        BOOST_CHECK_EQUAL(    0 , retList.size() );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  1 , retList.size() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( errorMsg );

        BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
        BOOST_CHECK_EQUAL( "Test MCT-410iL / Invalid transformer ratio (400)" , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_errors)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 5x1 test 0s" );

        BOOST_CHECK_EQUAL( NoMethod, mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );
        BOOST_CHECK_EQUAL(        0, om->Buffer.BSt.Length );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_test)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 5x1 errors enable" );

        BOOST_CHECK_EQUAL( NoMethod, mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );
        BOOST_CHECK_EQUAL(        0, om->Buffer.BSt.Length );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_display)
    {
        CtiCommandParser parse( "putconfig emetcon centron test 0 errors enable" );

        BOOST_CHECK_EQUAL( NoMethod, mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );
        BOOST_CHECK_EQUAL(        0, om->Buffer.BSt.Length );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_all_with_ratio)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 40" );

        BOOST_CHECK_EQUAL( NoMethod, mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );
        BOOST_CHECK_EQUAL(        0, om->Buffer.BSt.Length );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getconfig_address_unique)
    {
        CtiCommandParser parse( "getconfig address unique" );

        BOOST_CHECK_EQUAL( NoError , mct410.executeGetConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x10);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   3);
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getconfig_thresholds)
    {
        CtiCommandParser parse( "getconfig thresholds" );

        BOOST_CHECK_EQUAL( NoError , mct410.executeGetConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x1e);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   5);
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getconfig_meter_ratio)
    {
        CtiCommandParser parse( "getconfig meter ratio" );

        BOOST_CHECK_EQUAL( NoError , mct410.executeGetConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x19);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1);
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getconfig_freeze)
    {
        CtiCommandParser parse( "getconfig freeze" );

        BOOST_CHECK_EQUAL( NoError , mct410.executeGetConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x4f);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1);
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_putstatus_reset)
    {
        CtiCommandParser parse( "putstatus reset alarms" );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutStatus(&request, parse, om, vgList, retList, outList) );

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x08);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   2);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Message[0], 0x00);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Message[1], 0x00);
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_outage_old)
    {
        Cti::Test::set_to_central_timezone();

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec,         1029);
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 7);     //  set the device to SSPEC revision 0.7

        {
            CtiCommandParser parse( "getvalue outage 1" );

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x10);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

            INMESS im;

            //  4 bytes of time, 2 bytes duration in cycles.
            //    If duration's high bit is set, the duration is in seconds, not cycles.
            char input[13] = {0x50, 1, 2, 3, 4, 5, 0x50, 7, 8, 9, 0x8a, 11, 12};

            std::copy(input, input + 13, im.Buffer.DSt.Message);
            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            strcpy(im.Return.CommandStr, "getvalue outage 1");

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueOutage(&im, timeNow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_REQUIRE_EQUAL( points.size(), 2 );

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_CLOSE( pdata->getValue(), 17.15, 0.001 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), CtiTime(CtiDate(14,  7, 2012), 0, 22, 11) );
                    BOOST_CHECK_EQUAL( pdata->getString(), "Test MCT-410iL / Outage 1 : 07/14/2012 00:22:11 for 00:00:17.150");
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_CLOSE( pdata->getValue(), 2571, 0.001 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), CtiTime(CtiDate(18,  7, 2012), 14, 01, 29) );
                    BOOST_CHECK_EQUAL( pdata->getString(), "Test MCT-410iL / Outage 2 : 07/18/2012 14:01:29 for 00:42:51");
                }
            }
        }
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_outage)
    {
        Cti::Test::set_to_central_timezone();

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec,         1029);
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 17);  //  set the device to SSPEC revision 1.7

        {
            CtiCommandParser parse( "getvalue outage 1" );

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x10);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

            INMESS im;

            //  4 bytes of time, 2 bytes duration in cycles.  Last byte is duration types.
            char input[13] = {0x50, 1, 2, 3, 4, 5, 0x50, 7, 8, 9, 10, 11, 0x01};

            std::copy(input, input + 13, im.Buffer.DSt.Message);
            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            strcpy(im.Return.CommandStr, "getvalue outage 1");

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueOutage(&im, timeNow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_REQUIRE_EQUAL( points.size(), 2 );

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_CLOSE( pdata->getValue(), 17.15, 0.001 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), CtiTime(CtiDate(14,  7, 2012), 0, 22, 11) );
                    BOOST_CHECK_EQUAL( pdata->getString(), "Test MCT-410iL / Outage 1 : 07/14/2012 00:22:11 for 00:00:17.150");
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_CLOSE( pdata->getValue(), 2571, 0.001 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), CtiTime(CtiDate(18,  7, 2012), 14, 01, 29) );
                    BOOST_CHECK_EQUAL( pdata->getString(), "Test MCT-410iL / Outage 2 : 07/18/2012 14:01:29 for 00:42:51");
                }
            }
        }
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_0kwh)
    {
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            //  The rest are initialized to 0
            im.Buffer.DSt.Message[2] = 0x01;
            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 6 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 4 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 3 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[3]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[4]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[5]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight );
                }
            }
        }
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_underflow)
    {
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x00, 0x00, 0x03, 0x00, 0x02, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00 };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 6 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), OverflowQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), OverflowQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 4 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), OverflowQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 3 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[3]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[4]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[5]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 2.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight );
                }
            }
        }
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_normal_deltas)
    {
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x00, 0x01, 0x02, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04, 0x00, 0x05 };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 6 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 242 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 248 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 4 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 252 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 3 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[3]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 254 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[4]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 256 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[5]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 258 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight );
                }
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_0xX1faxx_kwh)
    {
        //  test for YUK-12652
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x01, 0xfa, 0x00, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04, 0x00, 0x05 };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 6 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 129520 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 129526 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 4 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 129530 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 3 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[3]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 129532 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[4]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 129534 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[5]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 129536 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight );
                }
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_bad_first_kwh)
    {
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x3f, 0xfa, 0x00, 0x01, 0x02, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04 };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 5 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 248 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 252 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 4 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 254 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 3 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[3]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 256 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[4]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 258 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_bad_first_delta)
    {
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x3f, 0xfa, 0x00, 0x01, 0x02, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04 };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 5 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 248 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 252 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 4 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 254 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 3 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[3]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 256 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[4]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 258 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_large_deltas)
    {
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x01, 0x00, 0x00, 0x3f, 0x9f, 0x3f, 0xa0, 0x3f, 0xa1, 0x3f, 0xfa, 0x3f, 0xff };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 3 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 32960 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 49248 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 65536 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight );
                }
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_all_deltas_bad)
    {
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x00, 0x01, 0x02, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 1 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 258 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight );
                }
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_all_bad)
    {
        const CtiDate datenow(4, 7, 2013);
        CtiTime timenow(datenow, 12, 34, 56);

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0xff, 0xff, 0xfa };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( ErrorInvalidTimestamp , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            BOOST_CHECK_EQUAL( retMsg->Status(), ErrorInvalidTimestamp );
            /*
            //  Doesn't work due to const CtiDate Today; in dev_mct410.cpp
            BOOST_CHECK_EQUAL(
                    retMsg->ResultString(),
                    "Test MCT-410iL / PulseAccumulator1 = (invalid data) @ 12/14/2013 00:00:00 [Requested interval outside of valid range]\n"
                    "Test MCT-410iL / PulseAccumulator1 = (invalid data) @ 12/15/2013 00:00:00 [Requested interval outside of valid range]\n"
                    "Test MCT-410iL / PulseAccumulator1 = (invalid data) @ 12/16/2013 00:00:00 [Requested interval outside of valid range]\n"
                    "Test MCT-410iL / PulseAccumulator1 = (invalid data) @ 12/17/2013 00:00:00 [Requested interval outside of valid range]\n"
                    "Test MCT-410iL / PulseAccumulator1 = (invalid data) @ 12/18/2013 00:00:00 [Requested interval outside of valid range]\n"
                    "Test MCT-410iL / PulseAccumulator1 = (invalid data) @ 12/19/2013 00:00:00 [Requested interval outside of valid range]\n"
                    "Multi-day daily read request complete\n" );
            */
            CtiMultiMsg_vec points = retMsg->PointData();

            BOOST_CHECK_EQUAL( points.size(), 0 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_all_bad_but_last)
    {
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x12, 0x34, 0x56 };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 1 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0x123456 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
            }
        }
    }

    BOOST_AUTO_TEST_CASE(test_executeGetConfigDisconnect)
    {
        test_Mct410IconDevice test_dev;

        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, 0x08);

        unsigned commandSequence;

        {
            CtiCommandParser parse("getconfig disconnect");

            BOOST_CHECK_EQUAL(NoError, test_dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

            BOOST_CHECK( retList.empty() );
            BOOST_CHECK( vgList.empty() );
            BOOST_REQUIRE_EQUAL( outList.size(), 1 );

            CtiOutMessage *outmsg = outList.front();

            BOOST_REQUIRE( outmsg );

            commandSequence = outmsg->Sequence;  //  for command tracking - must be copied to inmessage

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0xfe);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x03);  //  function read
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 11);
        }

        delete_container(outList);
        outList.clear();

        {
            INMESS  InMessage;
            CtiTime t(CtiDate(14, 7, 2011), 19, 16, 11);

            unsigned char test_data[] = {0x1,0x1,0x1,0x2,0x1,0x3,0x1,0x4,0x1,0x5,0x1,0x6};  //  leave as 12 bytes, as if we're reading queued

            memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));
            InMessage.Buffer.DSt.Length = sizeof(test_data);

            InMessage.Return.UserID                        = 0;
            InMessage.Sequence                             = commandSequence;
            InMessage.Return.ProtocolInfo.Emetcon.Function = 0xfe;
            InMessage.Return.ProtocolInfo.Emetcon.IO       = 3;

            BOOST_CHECK_EQUAL(NoError, test_dev.ResultDecode(&InMessage, t, vgList, retList, outList));

            BOOST_REQUIRE_EQUAL(retList.size(), 1);
            BOOST_CHECK(outList.empty());
            BOOST_REQUIRE_EQUAL(vgList.size(), 2);

            {
                const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

                BOOST_REQUIRE(retMsg);

                const std::string expected =
                    "Test MCT-410iL / Disconnect Info:\n"
                    "Disconnect load limit count: 1\n"
                    "Test MCT-410iL / Disconnect Config:\n"
                    "Disconnect receiver address: 66049\n"
                    "Disconnect load limit connect delay: 4 minutes\n"
                    "Autoreconnect enabled\n"
                    "Disconnect demand threshold disabled\n";

                BOOST_CHECK_EQUAL( retMsg->Status(), NoError );
                BOOST_CHECK_EQUAL( retMsg->ResultString(), expected );

                const CtiMultiMsg_vec &points = retMsg->getData();

                BOOST_REQUIRE_EQUAL( points.size(), 1 );

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_CLOSE( pdata->getValue(), 3, 0.001 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), CtiTime(CtiDate(14, 7, 2011), 19, 16, 11) );
                    //BOOST_CHECK_EQUAL( pdata->getString(), "Test MCT-410iL / Status1 =  @ 07/14/2011 19:16:11");
                }
            }

            test_Mct410Device::CtiMessageList::const_iterator vgList_itr = vgList.begin();

            {
                const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(*vgList_itr++);

                BOOST_REQUIRE( retMsg );

                const CtiMultiMsg_vec &points = retMsg->getData();

                BOOST_CHECK_EQUAL( points.size(), 1 );

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_CLOSE( pdata->getValue(), 3, 0.001 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), CtiTime(CtiDate(14, 7, 2011), 19, 16, 11) );
                    //BOOST_CHECK_EQUAL( pdata->getString(), "Test MCT-410iL / Status1 =  @ 07/14/2011 19:16:11");
                }
            }
            {
                const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(*vgList_itr++);

                BOOST_REQUIRE( retMsg );

                const CtiMultiMsg_vec &points = retMsg->getData();

                BOOST_CHECK_EQUAL( points.size(), 2 );

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_CLOSE( pdata->getValue(), 0, 0.001 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), CtiTime(CtiDate(14, 7, 2011), 19, 16, 11) );
                    //BOOST_CHECK_EQUAL( pdata->getString(), "Test MCT-410iL / Status10: Normal");
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_CLOSE( pdata->getValue(), 0, 0.001 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), CtiTime(CtiDate(14, 7, 2011), 19, 16, 11) );
                    //BOOST_CHECK_EQUAL( pdata->getString(), "Test MCT-410iL / Status9: Normal");
                }
            }
        }
    }

    BOOST_AUTO_TEST_CASE(test_executeGetConfigDisconnect_no_Mct_Configuration)
    {
        test_Mct410IconDevice test_dev;

        //test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, 0x08);

        CtiCommandParser parse("getconfig disconnect");

        BOOST_CHECK_EQUAL(NoError, test_dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK( retList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 2 );

        test_Mct410Device::OutMessageList::const_iterator outList_itr = outList.begin();

        {
            CtiOutMessage *outmsg = *outList_itr++;

            BOOST_REQUIRE( outmsg );

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x00);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x01);  //  read
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 8);
        }

        {
            CtiOutMessage *outmsg = *outList_itr++;

            BOOST_REQUIRE( outmsg );

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0xfe);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x03);  //  read
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 11);
        }
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigDisconnect_no_parameters)
    {
        test_Mct410IconDevice mct410;

        mct410.setDisconnectAddress(314159);
        mct410.demand_interval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect");

        BOOST_CHECK_EQUAL(NoError, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK( retList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        CtiOutMessage *outmsg = outList.front();

        BOOST_REQUIRE( outmsg );

        std::vector<unsigned char> expected = boost::assign::list_of
                (0x04)(0xcb)(0x2f)(0x00)(0x00)(0x05);

        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0fe);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x02);  //  function write
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size());

        const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
        const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                             outmsg->Buffer.BSt.Length;

        BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(), expected.end(),
                results_begin, results_end);
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigDisconnect_load_limit)
    {
        test_Mct410IconDevice mct410;

        mct410.setDisconnectAddress(314159);
        mct410.demand_interval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect load limit 1.234 4");

        BOOST_CHECK_EQUAL(NoError, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK( retList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        CtiOutMessage *outmsg = outList.front();

        BOOST_REQUIRE( outmsg );

        std::vector<unsigned char> expected = boost::assign::list_of
                (0x04)(0xcb)(0x2f)(0x34)(0x04)(0x04);

        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0fe);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x02);  //  function write
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size());

        const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
        const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                             outmsg->Buffer.BSt.Length;

        BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(), expected.end(),
                results_begin, results_end);
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigDisconnect_load_limit_bad_threshold)
    {
        test_Mct410IconDevice mct410;

        mct410.setDisconnectAddress(314159);
        mct410.demand_interval = 300;

        //  load limit of 12345.678 kW is 1028.81 kWh/5 min interval - much higher than the 409.5 kWh allowed
        CtiCommandParser parse("putconfig emetcon disconnect load limit 12345.678 4");

        BOOST_CHECK_EQUAL(NoError, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg*>(retList.front());

        BOOST_REQUIRE( errorMsg );

        const std::string expected = "Test MCT-410iL / Invalid disconnect parameters (1028806.500, 4)";

        BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
        BOOST_CHECK_EQUAL( expected , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigDisconnect_load_limit_bad_connect_delay)
    {
        test_Mct410IconDevice mct410;

        mct410.setDisconnectAddress(314159);
        mct410.demand_interval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect load limit 1.234 11");  //  max is 10 mins

        BOOST_CHECK_EQUAL(NoError, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg*>(retList.front());

        BOOST_REQUIRE( errorMsg );

        const std::string expected = "Test MCT-410iL / Invalid disconnect parameters (102.833, 11)";

        BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
        BOOST_CHECK_EQUAL( expected , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigDisconnect_cycle)
    {
        test_Mct410IconDevice mct410;

        mct410.setDisconnectAddress(314159);
        mct410.demand_interval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect cycle 7 17");

        BOOST_CHECK_EQUAL(NoError, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK( retList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        CtiOutMessage *outmsg = outList.front();

        BOOST_REQUIRE( outmsg );

        std::vector<unsigned char> expected = boost::assign::list_of
                (0x04)(0xcb)(0x2f)(0x00)(0x00)(0x05)(0x07)(0x11);

        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0fe);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x02);  //  function write
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size());

        const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
        const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                             outmsg->Buffer.BSt.Length;

        BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(), expected.end(),
                results_begin, results_end);
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigDisconnect_cycle_bad_connect_low)
    {
        test_Mct410IconDevice mct410;

        mct410.setDisconnectAddress(314159);
        mct410.demand_interval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect cycle 4 17");  //  min is 5

        BOOST_CHECK_EQUAL(NoError, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg*>(retList.front());

        BOOST_REQUIRE( errorMsg );

        const std::string expected = "Test MCT-410iL / Invalid disconnect cycle parameters (4, 17)";

        BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
        BOOST_CHECK_EQUAL( expected , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigDisconnect_cycle_bad_connect_high)
    {
        test_Mct410IconDevice mct410;

        mct410.setDisconnectAddress(314159);
        mct410.demand_interval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect cycle 64 17");  //  max is 60

        BOOST_CHECK_EQUAL(NoError, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg*>(retList.front());

        BOOST_REQUIRE( errorMsg );

        const std::string expected = "Test MCT-410iL / Invalid disconnect cycle parameters (64, 17)";

        BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
        BOOST_CHECK_EQUAL( expected , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigDisconnect_cycle_bad_disconnect_low)
    {
        test_Mct410IconDevice mct410;

        mct410.setDisconnectAddress(314159);
        mct410.demand_interval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect cycle 7 4");  //  min is 5

        BOOST_CHECK_EQUAL(NoError, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg*>(retList.front());

        BOOST_REQUIRE( errorMsg );

        const std::string expected = "Test MCT-410iL / Invalid disconnect cycle parameters (7, 4)";

        BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
        BOOST_CHECK_EQUAL( expected , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigDisconnect_cycle_bad_disconnect_high)
    {
        test_Mct410IconDevice mct410;

        mct410.setDisconnectAddress(314159);
        mct410.demand_interval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect cycle 7 77");  //  max is 60

        BOOST_CHECK_EQUAL(NoError, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg*>(retList.front());

        BOOST_REQUIRE( errorMsg );

        const std::string expected = "Test MCT-410iL / Invalid disconnect cycle parameters (7, 77)";

        BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
        BOOST_CHECK_EQUAL( expected , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigDisconnect_load_limit_and_cycle)
    {
        test_Mct410IconDevice mct410;

        mct410.setDisconnectAddress(314159);
        mct410.demand_interval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect load limit 1.234 4 cycle 7 17");

        BOOST_CHECK_EQUAL(NoError, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK( retList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        CtiOutMessage *outmsg = outList.front();

        BOOST_REQUIRE( outmsg );

        std::vector<unsigned char> expected = boost::assign::list_of
                (0x04)(0xcb)(0x2f)(0x34)(0x04)(0x04)(0x07)(0x11);

        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0fe);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x02);  //  function write
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size());

        const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
        const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                             outmsg->Buffer.BSt.Length;

        BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(), expected.end(),
                results_begin, results_end);
    }

    BOOST_AUTO_TEST_CASE(test_putconfig_install_all_no_config)
    {
        test_Mct410IconDevice mct410;

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( NoError, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( outList.empty() );

        CtiDeviceBase::CtiMessageList::const_iterator retList_itr = retList.begin();

        {
            const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg*>(*retList_itr++);

            BOOST_REQUIRE( errorMsg );

            const std::string expected = "ERROR: Device not assigned to a config.";

            BOOST_CHECK_EQUAL( 218,      errorMsg->Status() );
            BOOST_CHECK_EQUAL( expected, errorMsg->ResultString() );
        }
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_all)
    {
        test_Mct410IconDevice mct410;
        mct410.setDisconnectAddress(1234567);

        test_DeviceConfig config;

        config.insertValue("disconnectDemandThreshold", "2.71");
        config.insertValue("disconnectLoadLimitConnectDelay", "4");
        config.insertValue("disconnectMinutes", "7");
        config.insertValue("connectMinutes", "17");
        config.insertValue("reconnectButton", "true");
        config.insertValue("demandFreezeDay", "12");

        test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&config, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        mct410.setConfigManager(&cfgMgr);   // attach config manager to the deice so it can find the config

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( NoError, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );

        BOOST_REQUIRE_EQUAL( outList.size(), 4 );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        int writeMsgPriority,
            readMsgPriority;        // Capture message priorities to validate ordering

        // Disconnect messages - read-after-write.
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xfe );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      9 );

            const std::vector<unsigned> expected = boost::assign::list_of
                (0x12)(0xd6)(0x87)(0x2a)(0x96)(0x04)(0x07)(0x11)(0x40);

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );

            writeMsgPriority = om->Priority;
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xfe );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     13 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority

        BOOST_CHECK( writeMsgPriority > readMsgPriority );

        // Freeze Day messages - read-after-write.
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x4f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            const std::vector<unsigned> expected = boost::assign::list_of
                (0x0c);

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );

            writeMsgPriority = om->Priority;
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x4f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority

        BOOST_CHECK( writeMsgPriority > readMsgPriority );
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_all_alternate)
    {
        test_Mct410IconDevice mct410;
        mct410.setDisconnectAddress(123456);

        test_DeviceConfig config;

        config.insertValue("disconnectDemandThreshold", "4.71");
        config.insertValue("disconnectLoadLimitConnectDelay", "3");
        config.insertValue("disconnectMinutes", "6");
        config.insertValue("connectMinutes", "18");
        config.insertValue("reconnectButton", "false");
        config.insertValue("demandFreezeDay", "21");

        test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&config, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        mct410.setConfigManager(&cfgMgr);   // attach config manager to the deice so it can find the config

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( NoError, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );

        BOOST_REQUIRE_EQUAL( outList.size(), 4 );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        int writeMsgPriority,
            readMsgPriority;        // Capture message priorities to validate ordering

        // Disconnect messages - read-after-write.
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xfe );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      9 );

            const std::vector<unsigned> expected = boost::assign::list_of
                (0x01)(0xe2)(0x40)(0x11)(0xd7)(0x03)(0x06)(0x12)(0x44);

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );

            writeMsgPriority = om->Priority;
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xfe );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     13 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority

        BOOST_CHECK( writeMsgPriority > readMsgPriority );

        // Freeze Day messages - read-after-write.
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x4f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            const std::vector<unsigned> expected = boost::assign::list_of
                (0x15);

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );

            writeMsgPriority = om->Priority;
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x4f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority

        BOOST_CHECK( writeMsgPriority > readMsgPriority );
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


struct single_error_validator : mctExecute_helper
{
    ~single_error_validator()
    {
        BOOST_CHECK(outList.empty());
        BOOST_CHECK_EQUAL(retList.size(), 1);
    }
};


// These commands are errors and should only return a single error. See YUK-5059
BOOST_FIXTURE_TEST_SUITE(test_single_error_executor, single_error_validator)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor01)
    {
        CtiCommandParser parse("getvalue lp channel 1 02/02/2010 01/01/2010");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor02)
    {
        CtiCommandParser parse("control disconnect");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor03)
    {
        CtiCommandParser parse("putconfig emetcon address uniq 2455535535553555");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor04)
    {
        CtiCommandParser parse("putconfig emetcon centron ratio 10 display 4x5 test 5 errors disable");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor05)
    {
        CtiCommandParser parse("putconfig emetcon centron ratio 290 display 5x1 test 7 errors disable");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor06)
    {
        CtiCommandParser parse("putconfig emetcon centron ratio 25 display 5x1 test 10 errors disable");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor07)
    {
        CtiCommandParser parse("putconfig emetcon disconnect load limit 100 100");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor08)
    {
        CtiCommandParser parse("putconfig emetcon disconnect cycle 1 1");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor09)
    {
        CtiCommandParser parse("putconfig emetcon outage threshold 200");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor10)
    {
        CtiCommandParser parse("putconfig emetcon freeze day 266");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor11)
    {
        CtiCommandParser parse("getvalue daily read detail channel 2 02/02/2000");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor12)
    {
        CtiCommandParser parse("getvalue daily read detail channel 8");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


BOOST_AUTO_TEST_CASE(test_dev_mct410_decodeDisconnectStatus)
{
    struct test_case
    {
        const unsigned char dst_message_0;
        const unsigned char dst_message_1;
        const unsigned char dst_message_8;
        const string expected;
    }
    test_cases[] =
    {
        {0x3c, 0x02, 123, "Load limiting mode active\n"
                          "Cycling mode active, currently connected\n"
                          "Disconnect state uncertain (powerfail during disconnect)\n"
                          "Disconnect error - demand detected after disconnect command sent to collar\n"
                          "Disconnect load limit count: 123\n"},
        {0x3c, 0x02, 123, "Load limiting mode active\n"
                          "Cycling mode active, currently connected\n"
                          "Disconnect state uncertain (powerfail during disconnect)\n"
                          "Disconnect error - demand detected after disconnect command sent to collar\n"
                          "Disconnect load limit count: 123\n"},
        {0x1c, 0x00, 199, "Load limiting mode active\n"
                          "Cycling mode active, currently connected\n"
                          "Disconnect load limit count: 199\n"},
        {0x10, 0x00,  17, "Disconnect load limit count: 17\n"},
    };

    std::vector<std::string> expected, results;

    for each(test_case tc in test_cases)
    {
        expected.push_back(tc.expected);

        test_Mct410IconDevice mct410;

        DSTRUCT DSt;

        DSt.Message[0] = tc.dst_message_0;
        DSt.Message[1] = tc.dst_message_1;
        DSt.Message[8] = tc.dst_message_8;

        results.push_back(mct410.decodeDisconnectStatus(DSt));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(expected.begin(), expected.end(),
                                  results.begin(),  results.end());
}

BOOST_AUTO_TEST_CASE(test_dev_mct410_decodeDisconnectDemandLimitConfig)
{
    //  Possibilities:  Has config.  Has demand limit bit.  Has demand threshold.
    struct
    {
        const boost::optional<int> config;
        const double demand_threshold;
        const string expected;
    }
    tc[] =
    {
        {boost::none, 0.00, "Disconnect demand threshold disabled\n"},
        {boost::none, 0.18, "Disconnect demand threshold: 0.180 kW\n"},
        {0x00,        0.00, "Disconnect demand threshold disabled\n"},
        {0x00,        0.19, "Disconnect demand threshold disabled\n"},
        {0x08,        0.00, "Disconnect demand threshold disabled\n"},
        {0x08,        0.20, "Demand limit mode enabled\n"
                            "Disconnect demand threshold: 0.200 kW\n"}
    };

    BOOST_CHECK_EQUAL(tc[0].expected, test_Mct410Device::decodeDisconnectDemandLimitConfig(tc[0].config, tc[0].demand_threshold));
    BOOST_CHECK_EQUAL(tc[1].expected, test_Mct410Device::decodeDisconnectDemandLimitConfig(tc[1].config, tc[1].demand_threshold));
    BOOST_CHECK_EQUAL(tc[2].expected, test_Mct410Device::decodeDisconnectDemandLimitConfig(tc[2].config, tc[2].demand_threshold));
    BOOST_CHECK_EQUAL(tc[3].expected, test_Mct410Device::decodeDisconnectDemandLimitConfig(tc[3].config, tc[3].demand_threshold));
    BOOST_CHECK_EQUAL(tc[4].expected, test_Mct410Device::decodeDisconnectDemandLimitConfig(tc[4].config, tc[4].demand_threshold));
    BOOST_CHECK_EQUAL(tc[5].expected, test_Mct410Device::decodeDisconnectDemandLimitConfig(tc[5].config, tc[5].demand_threshold));
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_decodeDisconnectCyclingConfig)
{
    struct
    {
        const boost::optional<int> config;
        const unsigned disconnect_minutes;
        const unsigned connect_minutes;
        const string expected;
    }
    tc[] =
    {
        {boost::none, 0, 0, "Disconnect cycling mode disabled\n"},
        {boost::none, 0, 1, "Disconnect cycling mode disabled\n"},
        {boost::none, 2, 0, "Disconnect cycling mode disabled\n"},
        {boost::none, 3, 4, "Cycling mode - disconnect minutes: 3\n"
                            "Cycling mode - connect minutes   : 4\n"},
        {0x00,        0, 0, "Disconnect cycling mode disabled\n"},
        {0x00,        0, 1, "Disconnect cycling mode disabled\n"},
        {0x00,        2, 0, "Disconnect cycling mode disabled\n"},
        {0x00,        3, 4, "Disconnect cycling mode disabled\n"},
        {0x08,        0, 0, "Disconnect cycling mode disabled\n"},
        {0x08,        0, 1, "Disconnect cycling mode disabled\n"},
        {0x08,        2, 0, "Disconnect cycling mode disabled\n"},
        {0x08,        3, 4, "Disconnect cycling mode disabled\n"},
        {0x10,        0, 0, "Disconnect cycling mode enabled\n"
                            "Cycling mode - disconnect minutes: 0\n"
                            "Cycling mode - connect minutes   : 0\n"},
        {0x10,        0, 1, "Disconnect cycling mode enabled\n"
                            "Cycling mode - disconnect minutes: 0\n"
                            "Cycling mode - connect minutes   : 1\n"},
        {0x10,        2, 0, "Disconnect cycling mode enabled\n"
                            "Cycling mode - disconnect minutes: 2\n"
                            "Cycling mode - connect minutes   : 0\n"},
        {0x10,        3, 4, "Disconnect cycling mode enabled\n"
                            "Cycling mode - disconnect minutes: 3\n"
                            "Cycling mode - connect minutes   : 4\n"},
        {0x18,        0, 0, "Disconnect cycling mode disabled\n"},
        {0x18,        0, 1, "Disconnect cycling mode disabled\n"},
        {0x18,        2, 0, "Disconnect cycling mode disabled\n"},
        {0x18,        3, 4, "Disconnect cycling mode disabled\n"}
    };

    BOOST_CHECK_EQUAL(tc[ 0].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 0].config, tc[ 0].disconnect_minutes, tc[ 0].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 1].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 1].config, tc[ 1].disconnect_minutes, tc[ 1].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 2].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 2].config, tc[ 2].disconnect_minutes, tc[ 2].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 3].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 3].config, tc[ 3].disconnect_minutes, tc[ 3].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 4].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 4].config, tc[ 4].disconnect_minutes, tc[ 4].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 5].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 5].config, tc[ 5].disconnect_minutes, tc[ 5].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 6].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 6].config, tc[ 6].disconnect_minutes, tc[ 6].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 7].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 7].config, tc[ 7].disconnect_minutes, tc[ 7].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 8].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 8].config, tc[ 8].disconnect_minutes, tc[ 8].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 9].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 9].config, tc[ 9].disconnect_minutes, tc[ 9].connect_minutes));
    BOOST_CHECK_EQUAL(tc[10].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[10].config, tc[10].disconnect_minutes, tc[10].connect_minutes));
    BOOST_CHECK_EQUAL(tc[11].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[11].config, tc[11].disconnect_minutes, tc[11].connect_minutes));
    BOOST_CHECK_EQUAL(tc[12].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[12].config, tc[12].disconnect_minutes, tc[12].connect_minutes));
    BOOST_CHECK_EQUAL(tc[13].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[13].config, tc[13].disconnect_minutes, tc[13].connect_minutes));
    BOOST_CHECK_EQUAL(tc[14].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[14].config, tc[14].disconnect_minutes, tc[14].connect_minutes));
    BOOST_CHECK_EQUAL(tc[15].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[15].config, tc[15].disconnect_minutes, tc[15].connect_minutes));
    BOOST_CHECK_EQUAL(tc[16].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[16].config, tc[16].disconnect_minutes, tc[16].connect_minutes));
    BOOST_CHECK_EQUAL(tc[17].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[17].config, tc[17].disconnect_minutes, tc[17].connect_minutes));
    BOOST_CHECK_EQUAL(tc[18].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[18].config, tc[18].disconnect_minutes, tc[18].connect_minutes));
    BOOST_CHECK_EQUAL(tc[19].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[19].config, tc[19].disconnect_minutes, tc[19].connect_minutes));
}

BOOST_AUTO_TEST_CASE(test_dev_mct410_decodeDisconnectConfig)
{
    //  Test case permutations:
    //    SSPEC: < Cycling, < ConfigReadEnhanced, == ConfigReadEnhanced
    //    DSTRUCT: length < 13, length = 13
    //    Config byte: not retrieved, autoreconnect disabled, autoreconnect enabled
    //  Config byte cannot be missing when the SSPEC is ConfigReadEnhanced, since it returns the config byte

    struct test_case
    {
        const int sspec_revision;
        const int dst_length;
        const boost::optional<int> config_byte;
        const string expected;
    }
    test_cases[] =
    {
        //  sspec_revision < Mct410Device::SspecRev_Disconnect_Cycling
        {11, 12, boost::none, "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect demand threshold: 0.129 kW\n"},
        {11, 12, 0x00,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect demand threshold disabled\n"},
        {11, 12, 0x04,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Autoreconnect enabled\n"
                              "Disconnect demand threshold disabled\n"},
        //  if the message length is >= 13, the config byte is in the message
        {11, 13, 0x00,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect demand threshold disabled\n"},
        {11, 13, 0x04,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Autoreconnect enabled\n"
                              "Disconnect demand threshold disabled\n"},

        //  sspec_revision < Mct410Device::SspecRev_Disconnect_ConfigReadEnhanced
        {19, 12, boost::none, "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect demand threshold: 0.129 kW\n"
                              "Cycling mode - disconnect minutes: 10\n"
                              "Cycling mode - connect minutes   : 11\n"},
        {19, 12, 0x00,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        {19, 12, 0x04,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Autoreconnect enabled\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        //  if the message length is >= 13, the config byte is in the message
        {19, 13, 0x00,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        {19, 13, 0x04,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Autoreconnect enabled\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},

        //  sspec_revision == Mct410Device::SspecRev_Disconnect_ConfigReadEnhanced
        {20, 12, boost::none, "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect demand threshold: 0.129 kW\n"
                              "Cycling mode - disconnect minutes: 10\n"
                              "Cycling mode - connect minutes   : 11\n"},
        {20, 12, 0x00,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        {20, 12, 0x04,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Autoreconnect enabled\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        //  if the message length is >= 13, the config byte is in the message
        {20, 13, 0x00,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect verification threshold: 12.300 kW (205 Wh/minute)\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        {20, 13, 0x04,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect verification threshold: 12.300 kW (205 Wh/minute)\n"
                              "Autoreconnect enabled\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
    };

    std::vector<std::string> expected, results;

    for each( const test_case &tc in test_cases )
    {
        expected.push_back(tc.expected);

        {
            DSTRUCT DSt;

            DSt.Message[2] = 2;     //  Disconnect address
            DSt.Message[3] = 3;     //     :
            DSt.Message[4] = 4;     //     :
            DSt.Message[5] = 0x35;  //  Disconnect demand threshold
            DSt.Message[6] = 0x08;  //     :
            DSt.Message[7] = 34;    //  Disconnect load limit connect delay

            DSt.Message[9]  = 10;
            DSt.Message[10] = 11;

            DSt.Message[11] = tc.config_byte ? *tc.config_byte : 0xff;
            DSt.Message[12] = 205;

            DSt.Length      = tc.dst_length;

            test_Mct410IconDevice mct410;

            mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, tc.sspec_revision);

            if( tc.config_byte )
            {
                mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, *tc.config_byte);
            }

            results.push_back(mct410.decodeDisconnectConfig(DSt));
        }
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(expected.begin(), expected.end(),
                                  results.begin(),  results.end());
}

BOOST_AUTO_TEST_CASE(test_dev_mct410_canDailyReadDateAlias)
{
    //  test case 1/31 (aliases to 5/31 from 6/01 to 7/30)
    //    we don't need to check dates prior to 6/01, since it's not possible to request 5/31 before then
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  5, 2001), CtiDate( 1,  6, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  5, 2001), CtiDate( 1,  7, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  5, 2001), CtiDate(30,  7, 2001)));

    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  5, 2001), CtiDate(31,  7, 2001)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  5, 2001), CtiDate( 1,  8, 2001)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  5, 2001), CtiDate( 2,  8, 2001)));

    //  test case 11/29 (aliases to 3/29 from 3/30 to 5/28)
    //    we don't need to check dates prior to 3/30, since it's not possible to request 3/29 before then
    //  non-leap-year case
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2001), CtiDate(30,  3, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2001), CtiDate( 1,  4, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2001), CtiDate( 1,  5, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2001), CtiDate(28,  5, 2001)));

    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2001), CtiDate(29,  5, 2001)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2001), CtiDate( 1,  6, 2001)));

    //  leap-year case
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2004), CtiDate(30,  3, 2004)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2004), CtiDate( 1,  4, 2004)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2004), CtiDate( 1,  5, 2004)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2004), CtiDate(28,  5, 2004)));

    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2004), CtiDate(29,  5, 2004)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2004), CtiDate( 1,  6, 2004)));

    //  test case 11/30 (aliases to 3/30 from 3/31 to 5/29)
    //    we don't need to check dates prior to 3/31, since it's not possible to request 3/30 before then
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate(31,  3, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate( 1,  4, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate( 2,  4, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate(30,  4, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate( 1,  5, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate( 2,  5, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate(27,  5, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate(28,  5, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate(29,  5, 2001)));

    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate(30,  5, 2001)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate(31,  5, 2001)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate( 1,  6, 2001)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate( 2,  6, 2001)));

    //  test case 8/31 (aliases to 12/31 from 1/1 to 3/31)
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31, 12, 2001), CtiDate( 1,  1, 2002)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31, 12, 2001), CtiDate( 1,  2, 2002)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31, 12, 2001), CtiDate( 1,  3, 2002)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31, 12, 2001), CtiDate(31,  3, 2002)));

    //  test case 3/31 (aliases to 7/31 from 8/1 to 10/31)
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  7, 2001), CtiDate( 1,  8, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  7, 2001), CtiDate( 1,  9, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  7, 2001), CtiDate( 1, 10, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  7, 2001), CtiDate(31, 10, 2001)));
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_getUsageReportDelay)
{
    {
        const test_Mct410IconDevice mct;

        //  Calculation is 10s + intervals/day * days * 1ms

        //  24 * 12 *  1 * 0.001 = 0.288s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(300,   1), 10);

        //  24 * 12 * 30 * 0.001 = 8.640s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(300,  30), 18);

        //  24 * 12 * 60 * 0.001 = 17.280s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(300,  60), 27);

        //  24 *  6 *  1 * 0.001 = 0.144s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(600,   1), 10);

        //  24 *  6 * 30 * 0.001 = 4.320s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(600,  30), 14);

        //  24 *  6 * 60 * 0.001 = 8.640s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(600,  60), 18);

        //  24 *  1 *  1 * 0.001 = 0.024s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(3600,  1), 10);

        //  24 *  1 * 30 * 0.001 = 0.720s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(3600, 30), 10);

        //  24 *  1 * 60 * 0.001 = 1.440s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(3600, 60), 11);
    }

    {
        const test_Mct410FocusDevice mct;

        //  Calculation is 10s + intervals/day * days * 2ms

        //  24 * 12 *  1 * 0.002 = 0.576s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(300,   1), 10);

        //  24 * 12 * 30 * 0.002 = 17.280s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(300,  30), 27);

        //  24 * 12 * 60 * 0.002 = 34.560s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(300,  60), 44);

        //  24 *  6 *  1 * 0.002 = 0.288s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(600,   1), 10);

        //  24 *  6 * 30 * 0.002 = 8.640s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(600,  30), 18);

        //  24 *  6 * 60 * 0.002 = 17.280s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(600,  60), 27);

        //  24 *  1 *  1 * 0.002 = 0.048s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(3600,  1), 10);

        //  24 *  1 * 30 * 0.002 = 1.440s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(3600, 30), 11);

        //  24 *  1 * 60 * 0.002 = 2.880s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(3600, 60), 12);
    }
}


struct getOperation_helper
{
    test_Mct410IconDevice mct;
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
    //  Overridden by MCT-410
    /*
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
    */
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
    //  Overridden by MCT-410
    /*
    BOOST_AUTO_TEST_CASE(test_getOperation_13)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x49);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    */
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
    //  MCT-410 commands
    BOOST_AUTO_TEST_CASE(test_getOperation_21)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_Accum, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x90);
        BOOST_CHECK_EQUAL(BSt.Length,   9);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_22)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_KWH, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x90);
        BOOST_CHECK_EQUAL(BSt.Length,   9);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_23)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_FrozenKWH, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x91);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_24)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_TOUkWh, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xe0);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_25)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_FrozenTOUkWh, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xe1);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_26)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_Integrity, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x92);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_27)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_28)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_29)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_LoadProfilePeakReport, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_30)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_Demand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x92);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_31)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_PeakDemand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x93);
        BOOST_CHECK_EQUAL(BSt.Length,   9);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_32)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_FrozenPeakDemand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x94);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_33)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_Voltage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x95);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_34)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_FrozenVoltage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x96);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_35)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_Outage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x10);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_36)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_Internal, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x05);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_37)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x97);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_38)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_Freeze, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x26);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_39)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Connect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x4c);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_40)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x4d);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_41)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xfe);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_42)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xfe);
        BOOST_CHECK_EQUAL(BSt.Length,   11);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_43)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xfe);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_44)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Raw, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_45)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x44);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_46)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Time, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x3f);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_47)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_FreezeDay, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x4f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_48)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TimeZoneOffset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x3f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_49)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Intervals, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x03);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_50)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Intervals, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1a);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_51)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_OutageThreshold, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x22);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_52)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Thresholds, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1e);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_53)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_DailyReadInterest, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf3);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_54)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_PFCount, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x23);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_55)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_Reset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x8a);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_56)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_ResetAlarms, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x08);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_57)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeOne, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x51);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_58)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeTwo, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x52);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_59)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeVoltageOne, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x59);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_60)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeVoltageTwo, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x5a);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_61)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_UniqueAddress, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf1);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_62)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_UniqueAddress, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x10);
        BOOST_CHECK_EQUAL(BSt.Length,   3);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_63)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Multiplier, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x19);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_64)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_MeterParameters, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x0f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_65)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Freeze, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x4f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_66)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_LongLoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x04);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_67)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_LongLoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x9d);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_68)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_VThreshold, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1e);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_69)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Holiday, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xd0);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_70)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Holiday, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xd0);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_71)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Options, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x01);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_72)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_AutoReconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x01);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_73)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Outage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x22);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_74)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TimeAdjustTolerance, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x36);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_75)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_PhaseDetect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x0f);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_76)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_PhaseDetect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x10);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_77)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_PhaseDetectClear, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Read_1Dword)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct410Device::ReadDescriptor> results;
    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  memory read 0
        (tuple_list_of(0,5,100)(1,1,101)(2,1,131))
        (tuple_list_of(0,1,101)(1,1,131)(2,1,127))
        (tuple_list_of(0,1,131)(1,1,127))
        (tuple_list_of(0,1,127))
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(2,1,128))
        (tuple_list_of(1,1,128)(2,1,129))
        // memory read 10
        (tuple_list_of(0,1,128)(1,1,129)(2,2,130))
        (tuple_list_of(0,1,129)(1,2,130))
        (tuple_list_of(0,2,130))
        (tuple_list_of(2,1,141))
        (tuple_list_of(1,1,141))
        (tuple_list_of(0,1,141))
        (empty)
        (tuple_list_of(2,1,123))
        (tuple_list_of(1,1,123)(2,2,126))
        (tuple_list_of(0,1,123)(1,2,126))
        // memory read 20
        (tuple_list_of(0,2,126)(2,2,124))
        (tuple_list_of(1,2,124))
        (tuple_list_of(0,2,124)(2,1,125))
        (tuple_list_of(1,1,125)(2,1,142))
        (tuple_list_of(0,1,125)(1,1,142)(2,1,112))
        (tuple_list_of(0,1,142)(1,1,112)(2,1,103))
        (tuple_list_of(0,1,112)(1,1,103)(2,1,114))
        (tuple_list_of(0,1,103)(1,1,114)(2,1,113))
        (tuple_list_of(0,1,114)(1,1,113)(2,2,111))
        (tuple_list_of(0,1,113)(1,2,111))
        // memory read 30
        (tuple_list_of(0,2,111)(2,2,110))
        (tuple_list_of(1,2,110))
        (tuple_list_of(0,2,110)(2,1,132))
        (tuple_list_of(1,1,132))
        (tuple_list_of(0,1,132))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 40
        .repeat(10, empty)
        // memory read 50
        (empty)
        (tuple_list_of(2,1,202))
        (tuple_list_of(1,1,202)(2,1,106))
        (tuple_list_of(0,1,202)(1,1,106)(2,4,107))
        (tuple_list_of(0,1,106)(1,4,107))
        (tuple_list_of(0,4,107))
        (empty)
        (tuple_list_of(2,4,108))
        (tuple_list_of(1,4,108))
        (tuple_list_of(0,4,108))
        // memory read 60
        (empty)
        (tuple_list_of(2,1,109))
        (tuple_list_of(1,1,109))
        (tuple_list_of(0,1,109))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 70
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(2,1,115))
        (tuple_list_of(1,1,115)(2,2,117))
        (tuple_list_of(0,1,115)(1,2,117))
        // memory read 80
        (tuple_list_of(0,2,117))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 90
        .repeat(110, empty)
        // memory read 200
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(2,4,143))
        (tuple_list_of(1,4,143))
        (tuple_list_of(0,4,143))
        (empty)
        // memory read 210
        (tuple_list_of(2,4,144))
        (tuple_list_of(1,4,144))
        (tuple_list_of(0,4,144))
        (empty)
        (tuple_list_of(2,4,145))
        (tuple_list_of(1,4,145))
        (tuple_list_of(0,4,145))
        (empty)
        (empty)
        (empty)
        // memory read 220
        .repeat(36, empty);

    const test_Mct410IconDevice dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Read, function, 3));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Read_2Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct410Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  memory read 0
        (tuple_list_of(0,5,100)(1,1,101)(2,1,131)(3,1,127))
        (tuple_list_of(0,1,101)(1,1,131)(2,1,127))
        (tuple_list_of(0,1,131)(1,1,127))
        (tuple_list_of(0,1,127)(7,1,128))
        (tuple_list_of(6,1,128)(7,1,129))
        (tuple_list_of(5,1,128)(6,1,129)(7,2,130))
        (tuple_list_of(4,1,128)(5,1,129)(6,2,130))
        (tuple_list_of(3,1,128)(4,1,129)(5,2,130))
        (tuple_list_of(2,1,128)(3,1,129)(4,2,130)(7,1,141))
        (tuple_list_of(1,1,128)(2,1,129)(3,2,130)(6,1,141))
        // memory read
        (tuple_list_of(0,1,128)(1,1,129)(2,2,130)(5,1,141))
        (tuple_list_of(0,1,129)(1,2,130)(4,1,141))
        (tuple_list_of(0,2,130)(3,1,141)(7,1,123))
        (tuple_list_of(2,1,141)(6,1,123)(7,2,126))
        (tuple_list_of(1,1,141)(5,1,123)(6,2,126))
        (tuple_list_of(0,1,141)(4,1,123)(5,2,126)(7,2,124))
        (tuple_list_of(3,1,123)(4,2,126)(6,2,124))
        (tuple_list_of(2,1,123)(3,2,126)(5,2,124)(7,1,125))
        (tuple_list_of(1,1,123)(2,2,126)(4,2,124)(6,1,125)(7,1,142))
        (tuple_list_of(0,1,123)(1,2,126)(3,2,124)(5,1,125)(6,1,142)(7,1,112))
        // memory read
        (tuple_list_of(0,2,126)(2,2,124)(4,1,125)(5,1,142)(6,1,112)(7,1,103))
        (tuple_list_of(1,2,124)(3,1,125)(4,1,142)(5,1,112)(6,1,103)(7,1,114))
        (tuple_list_of(0,2,124)(2,1,125)(3,1,142)(4,1,112)(5,1,103)(6,1,114)(7,1,113))
        (tuple_list_of(1,1,125)(2,1,142)(3,1,112)(4,1,103)(5,1,114)(6,1,113)(7,2,111))
        (tuple_list_of(0,1,125)(1,1,142)(2,1,112)(3,1,103)(4,1,114)(5,1,113)(6,2,111))
        (tuple_list_of(0,1,142)(1,1,112)(2,1,103)(3,1,114)(4,1,113)(5,2,111)(7,2,110))
        (tuple_list_of(0,1,112)(1,1,103)(2,1,114)(3,1,113)(4,2,111)(6,2,110))
        (tuple_list_of(0,1,103)(1,1,114)(2,1,113)(3,2,111)(5,2,110)(7,1,132))
        (tuple_list_of(0,1,114)(1,1,113)(2,2,111)(4,2,110)(6,1,132))
        (tuple_list_of(0,1,113)(1,2,111)(3,2,110)(5,1,132))
        // memory read
        (tuple_list_of(0,2,111)(2,2,110)(4,1,132))
        (tuple_list_of(1,2,110)(3,1,132))
        (tuple_list_of(0,2,110)(2,1,132))
        (tuple_list_of(1,1,132))
        (tuple_list_of(0,1,132))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(7,1,202))
        (tuple_list_of(6,1,202)(7,1,106))
        (tuple_list_of(5,1,202)(6,1,106)(7,4,107))
        (tuple_list_of(4,1,202)(5,1,106)(6,4,107))
        // memory read
        (tuple_list_of(3,1,202)(4,1,106)(5,4,107))
        (tuple_list_of(2,1,202)(3,1,106)(4,4,107))
        (tuple_list_of(1,1,202)(2,1,106)(3,4,107)(7,4,108))
        (tuple_list_of(0,1,202)(1,1,106)(2,4,107)(6,4,108))
        (tuple_list_of(0,1,106)(1,4,107)(5,4,108))
        (tuple_list_of(0,4,107)(4,4,108))
        (tuple_list_of(3,4,108)(7,1,109))
        (tuple_list_of(2,4,108)(6,1,109))
        (tuple_list_of(1,4,108)(5,1,109))
        (tuple_list_of(0,4,108)(4,1,109))
        // memory read
        (tuple_list_of(3,1,109))
        (tuple_list_of(2,1,109))
        (tuple_list_of(1,1,109))
        (tuple_list_of(0,1,109))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read
        (empty)
        (empty)
        (tuple_list_of(7,1,115))
        (tuple_list_of(6,1,115)(7,2,117))
        (tuple_list_of(5,1,115)(6,2,117))
        (tuple_list_of(4,1,115)(5,2,117))
        (tuple_list_of(3,1,115)(4,2,117))
        (tuple_list_of(2,1,115)(3,2,117))
        (tuple_list_of(1,1,115)(2,2,117))
        (tuple_list_of(0,1,115)(1,2,117))
        // memory read
        (tuple_list_of(0,2,117))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read
        .repeat(110,empty)
        // memory read
        (empty)
        (tuple_list_of(7,4,143))
        (tuple_list_of(6,4,143))
        (tuple_list_of(5,4,143))
        (tuple_list_of(4,4,143))
        (tuple_list_of(3,4,143)(7,4,144))
        (tuple_list_of(2,4,143)(6,4,144))
        (tuple_list_of(1,4,143)(5,4,144))
        (tuple_list_of(0,4,143)(4,4,144))
        (tuple_list_of(3,4,144)(7,4,145))
        // memory read
        (tuple_list_of(2,4,144)(6,4,145))
        (tuple_list_of(1,4,144)(5,4,145))
        (tuple_list_of(0,4,144)(4,4,145))
        (tuple_list_of(3,4,145))
        (tuple_list_of(2,4,145))
        (tuple_list_of(1,4,145))
        (tuple_list_of(0,4,145))
        (empty)
        (empty)
        (empty)
        // memory read
        .repeat(36, empty);

    const test_Mct410IconDevice dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Read, function, 8));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Read_3Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct410Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  memory read 0
        (tuple_list_of(0,5,100)(1,1,101)(2,1,131)(3,1,127)(10,1,128)(11,1,129)(12,2,130))
        (tuple_list_of(0,1,101)(1,1,131)(2,1,127)(9,1,128)(10,1,129)(11,2,130))
        (tuple_list_of(0,1,131)(1,1,127)(8,1,128)(9,1,129)(10,2,130))
        (tuple_list_of(0,1,127)(7,1,128)(8,1,129)(9,2,130)(12,1,141))
        (tuple_list_of(6,1,128)(7,1,129)(8,2,130)(11,1,141))
        (tuple_list_of(5,1,128)(6,1,129)(7,2,130)(10,1,141))
        (tuple_list_of(4,1,128)(5,1,129)(6,2,130)(9,1,141))
        (tuple_list_of(3,1,128)(4,1,129)(5,2,130)(8,1,141)(12,1,123))
        (tuple_list_of(2,1,128)(3,1,129)(4,2,130)(7,1,141)(11,1,123)(12,2,126))
        (tuple_list_of(1,1,128)(2,1,129)(3,2,130)(6,1,141)(10,1,123)(11,2,126))
        // memory read
        (tuple_list_of(0,1,128)(1,1,129)(2,2,130)(5,1,141)(9,1,123)(10,2,126)(12,2,124))
        (tuple_list_of(0,1,129)(1,2,130)(4,1,141)(8,1,123)(9,2,126)(11,2,124))
        (tuple_list_of(0,2,130)(3,1,141)(7,1,123)(8,2,126)(10,2,124)(12,1,125))
        (tuple_list_of(2,1,141)(6,1,123)(7,2,126)(9,2,124)(11,1,125)(12,1,142))
        (tuple_list_of(1,1,141)(5,1,123)(6,2,126)(8,2,124)(10,1,125)(11,1,142)(12,1,112))
        (tuple_list_of(0,1,141)(4,1,123)(5,2,126)(7,2,124)(9,1,125)(10,1,142)(11,1,112)(12,1,103))
        (tuple_list_of(3,1,123)(4,2,126)(6,2,124)(8,1,125)(9,1,142)(10,1,112)(11,1,103)(12,1,114))
        (tuple_list_of(2,1,123)(3,2,126)(5,2,124)(7,1,125)(8,1,142)(9,1,112)(10,1,103)(11,1,114)(12,1,113))
        (tuple_list_of(1,1,123)(2,2,126)(4,2,124)(6,1,125)(7,1,142)(8,1,112)(9,1,103)(10,1,114)(11,1,113)(12,2,111))
        (tuple_list_of(0,1,123)(1,2,126)(3,2,124)(5,1,125)(6,1,142)(7,1,112)(8,1,103)(9,1,114)(10,1,113)(11,2,111))
        // memory read
        (tuple_list_of(0,2,126)(2,2,124)(4,1,125)(5,1,142)(6,1,112)(7,1,103)(8,1,114)(9,1,113)(10,2,111)(12,2,110))
        (tuple_list_of(1,2,124)(3,1,125)(4,1,142)(5,1,112)(6,1,103)(7,1,114)(8,1,113)(9,2,111)(11,2,110))
        (tuple_list_of(0,2,124)(2,1,125)(3,1,142)(4,1,112)(5,1,103)(6,1,114)(7,1,113)(8,2,111)(10,2,110)(12,1,132))
        (tuple_list_of(1,1,125)(2,1,142)(3,1,112)(4,1,103)(5,1,114)(6,1,113)(7,2,111)(9,2,110)(11,1,132))
        (tuple_list_of(0,1,125)(1,1,142)(2,1,112)(3,1,103)(4,1,114)(5,1,113)(6,2,111)(8,2,110)(10,1,132))
        (tuple_list_of(0,1,142)(1,1,112)(2,1,103)(3,1,114)(4,1,113)(5,2,111)(7,2,110)(9,1,132))
        (tuple_list_of(0,1,112)(1,1,103)(2,1,114)(3,1,113)(4,2,111)(6,2,110)(8,1,132))
        (tuple_list_of(0,1,103)(1,1,114)(2,1,113)(3,2,111)(5,2,110)(7,1,132))
        (tuple_list_of(0,1,114)(1,1,113)(2,2,111)(4,2,110)(6,1,132))
        (tuple_list_of(0,1,113)(1,2,111)(3,2,110)(5,1,132))
        // memory read
        (tuple_list_of(0,2,111)(2,2,110)(4,1,132))
        (tuple_list_of(1,2,110)(3,1,132))
        (tuple_list_of(0,2,110)(2,1,132))
        (tuple_list_of(1,1,132))
        (tuple_list_of(0,1,132))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read
        (empty)
        (tuple_list_of(12,1,202))
        (tuple_list_of(11,1,202)(12,1,106))
        (tuple_list_of(10,1,202)(11,1,106)(12,4,107))
        (tuple_list_of(9,1,202)(10,1,106)(11,4,107))
        (tuple_list_of(8,1,202)(9,1,106)(10,4,107))
        (tuple_list_of(7,1,202)(8,1,106)(9,4,107))
        (tuple_list_of(6,1,202)(7,1,106)(8,4,107)(12,4,108))
        (tuple_list_of(5,1,202)(6,1,106)(7,4,107)(11,4,108))
        (tuple_list_of(4,1,202)(5,1,106)(6,4,107)(10,4,108))
        // memory read
        (tuple_list_of(3,1,202)(4,1,106)(5,4,107)(9,4,108))
        (tuple_list_of(2,1,202)(3,1,106)(4,4,107)(8,4,108)(12,1,109))
        (tuple_list_of(1,1,202)(2,1,106)(3,4,107)(7,4,108)(11,1,109))
        (tuple_list_of(0,1,202)(1,1,106)(2,4,107)(6,4,108)(10,1,109))
        (tuple_list_of(0,1,106)(1,4,107)(5,4,108)(9,1,109))
        (tuple_list_of(0,4,107)(4,4,108)(8,1,109))
        (tuple_list_of(3,4,108)(7,1,109))
        (tuple_list_of(2,4,108)(6,1,109))
        (tuple_list_of(1,4,108)(5,1,109))
        (tuple_list_of(0,4,108)(4,1,109))
        // memory read
        (tuple_list_of(3,1,109))
        (tuple_list_of(2,1,109))
        (tuple_list_of(1,1,109))
        (tuple_list_of(0,1,109))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(12,1,115))
        (tuple_list_of(11,1,115)(12,2,117))
        (tuple_list_of(10,1,115)(11,2,117))
        // memory read
        (tuple_list_of(9,1,115)(10,2,117))
        (tuple_list_of(8,1,115)(9,2,117))
        (tuple_list_of(7,1,115)(8,2,117))
        (tuple_list_of(6,1,115)(7,2,117))
        (tuple_list_of(5,1,115)(6,2,117))
        (tuple_list_of(4,1,115)(5,2,117))
        (tuple_list_of(3,1,115)(4,2,117))
        (tuple_list_of(2,1,115)(3,2,117))
        (tuple_list_of(1,1,115)(2,2,117))
        (tuple_list_of(0,1,115)(1,2,117))
        // memory read
        (tuple_list_of(0,2,117))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read
        .repeat(100, empty)
        // memory read
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(12,4,143))
        (tuple_list_of(11,4,143))
        (tuple_list_of(10,4,143))
        (tuple_list_of(9,4,143))
        // memory read
        (tuple_list_of(8,4,143)(12,4,144))
        (tuple_list_of(7,4,143)(11,4,144))
        (tuple_list_of(6,4,143)(10,4,144))
        (tuple_list_of(5,4,143)(9,4,144))
        (tuple_list_of(4,4,143)(8,4,144)(12,4,145))
        (tuple_list_of(3,4,143)(7,4,144)(11,4,145))
        (tuple_list_of(2,4,143)(6,4,144)(10,4,145))
        (tuple_list_of(1,4,143)(5,4,144)(9,4,145))
        (tuple_list_of(0,4,143)(4,4,144)(8,4,145))
        (tuple_list_of(3,4,144)(7,4,145))
        // memory read
        (tuple_list_of(2,4,144)(6,4,145))
        (tuple_list_of(1,4,144)(5,4,145))
        (tuple_list_of(0,4,144)(4,4,145))
        (tuple_list_of(3,4,145))
        (tuple_list_of(2,4,145))
        (tuple_list_of(1,4,145))
        (tuple_list_of(0,4,145))
        (empty)
        (empty)
        (empty)
        // memory read
        .repeat(36, empty);

    const test_Mct410IconDevice dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Read, function, 13));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Function_Read_1Dword)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct410Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  function read 0
        (empty).repeat(170-1, empty)
        //  function read 170
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,2,117)(2,1,122))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  function read 180
        .repeat(76, empty);

    const test_Mct410IconDevice dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Function_Read, function, 3));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Function_Read_2Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct410Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  function read 0
        (empty).repeat(150-1, empty)
        //  function read 150
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(4,1,137)(5,1,138)(6,1,139)(7,1,140))
        (empty)
        (empty)
        //  function read 160
        .repeat(10, empty)
        //  function read 170
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,2,117)(2,1,122))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  function read 180
        .repeat(70, empty)
        //  function read 250
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(7,1,134))
        (empty);

    const test_Mct410IconDevice dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Function_Read, function, 8));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Function_Read_3Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct410Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  function read 0
        (empty).repeat(150-1, empty)
        //  function read 150
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(4,1,137)(5,1,138)(6,1,139)(7,1,140))
        (empty)
        (empty)
        //  function read 160
        .repeat(10, empty)
        //  function read 170
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,2,117)(2,1,122)(10,1,109))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  function read 180
        .repeat(70, empty)
        //  function read 250
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(7,1,134)(9,1,135)(10,1,136)(11,1,127))
        (empty);

    const test_Mct410IconDevice dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Function_Read, function, 13));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE(test_makeDynamicDemand)
{
    // Range: Under 0.0 Wh
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand(    -1.0), -1);

    // Range: 0.0 Wh - 400.0 Wh
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand(     0.0),  0x0000 |    0);
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand(     0.1),  0x3000 |    1);
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand(   400.0),  0x3000 | 4000);

    // Between endpoints, check rounding
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand(   400.4),  0x2000 |  400);
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand(   400.5),  0x2000 |  401);
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand(   400.6),  0x2000 |  401);

    // Range: 401.0 Wh - 4,000.0 Wh
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand(   401.0),  0x2000 |  401);
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand(  4000.0),  0x2000 | 4000);

    // Between endpoints, check rounding
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand(  4004.0),  0x1000 |  400);
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand(  4005.0),  0x1000 |  401);
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand(  4006.0),  0x1000 |  401);

    // Range: 4,010.0 Wh - 40,000.0 Wh
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand(  4010.0),  0x1000 |  401);
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand( 40000.0),  0x1000 | 4000);

    // Between endpoints, check rounding
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand( 40040.0),  0x0000 |  400);
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand( 40050.0),  0x0000 |  401);
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand( 40060.0),  0x0000 |  401);

    // Range: 40,100.0 Wh - 409,500.0 Wh
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand( 40100.0),  0x0000 |  401);
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand(409500.0),  0x0000 | 4095);

    // Range: Over 409,500.0 Wh
    BOOST_CHECK_EQUAL(Cti::Devices::Mct410Device::Utility::makeDynamicDemand(409501.0), -1);
}

BOOST_AUTO_TEST_SUITE_END()

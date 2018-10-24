#include <boost/test/unit_test.hpp>

#include "dev_mct410.h"
#include "dev_ccu.h"
#include "rte_ccu.h"
#include "pt_analog.h"
#include "pt_accum.h"
#include "pt_status.h"
#include "devicetypes.h"
#include "desolvers.h"
#include "rtdb_test_helpers.h"

#include "boost_test_helpers.h"

#include <boost/assign/list_of.hpp>

using namespace Cti::Protocols;
using std::string;
using std::list;
using std::vector;
typedef CtiTableDynamicPaoInfo Dpi;


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
        _tblPAO.setID(1234, test_tag);
        setDevicePointer(ccu);
    }
};

struct test_Mct410Device : Cti::Devices::Mct410Device
{
protected:
    CtiRouteSPtr rte;

    test_Mct410Device(DeviceTypes type, const string &name) :
        rte(new test_CtiRouteCCU)
    {
        setDeviceType(type);
        _name = name;
        _paObjectID = 123456;
    }

public:
    typedef Mct410Device::point_info point_info;

    using MctDevice::getOperation;
    using MctDevice::ReadDescriptor;
    using MctDevice::value_locator;
    using MctDevice::getDescriptorForRead;
    using MctDevice::ResultDecode;
    using MctDevice::SubmitRetry;
    using MctDevice::ErrorDecode;

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
    using Mct410Device::decodeGetValuePeakDemand;

    using Mct410Device::isDailyReadVulnerableToAliasing;

    long test_demandInterval = 3600;
    virtual LONG getDemandInterval() override
        {  return test_demandInterval;  }

    long test_loadProfileInterval = 300;
    virtual LONG getLoadProfileInterval(unsigned channel) override
        {  return test_loadProfileInterval;  }

    bool test_needsChannelConfig = false;
    virtual bool needsChannelConfig(const unsigned channel) const override
        {  return test_needsChannelConfig;  }

    bool test_isSupported_Mct410Feature_HourlyKwh() const
            {  return isSupported(Feature_HourlyKwh);  }

    bool test_isSupported_Mct410Feature_DisconnectCollar() const
            {  return isSupported(Feature_DisconnectCollar);  }

    bool test_isSupported_Mct4xxFeature_LoadProfilePeakReport() const
            {  return isSupported(Feature_LoadProfilePeakReport);  }

    bool test_isSupported_Mct4xxFeature_TouPeaks() const
            {  return isSupported(Feature_TouPeaks);  }

    Cti::Test::DevicePointHelper pointHelper;

    CtiPointSPtr getDevicePointOffsetTypeEqual(int offset, CtiPointType_t type) override
    {
        return pointHelper.getCachedPoint(offset, type);
    }

    CtiRouteSPtr getRoute(long routeId) const override
    {
        return rte;
    }

    std::string resolveStateName(long groupId, long rawValue) const override
    {
        static const std::array<const char *, 10> stateNames {
            "False", "True", "State Two", "State Three", "State Four", "State Five", "State Six", "State Seven", "State Eight", "State Nine"
        };

        if( rawValue >= 0 && rawValue < stateNames.size() )
        {
            return stateNames[rawValue];
        }

        return "State " + std::to_string(rawValue);
    }
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

struct resetGlobals_helper
{
    Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;
};

BOOST_FIXTURE_TEST_SUITE( test_dev_mct410, resetGlobals_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE

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

    test_Mct410Device::frozen_point_info pi;

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

    Cti::Devices::Mct4xxDevice::frozen_point_info pi;

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


struct executeRequest_helper : resetGlobals_helper
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

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.beginExecuteRequest(&req, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(1, vgList.size());
        BOOST_CHECK(retList.empty());
        BOOST_REQUIRE_EQUAL(1, outList.size());

        OUTMESS *om = outList.front();

        BOOST_REQUIRE( om );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 76 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       0 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   0 );

        auto ret = dynamic_cast<const CtiSignalMsg *>(vgList.front());

        BOOST_REQUIRE(ret);
        BOOST_CHECK_EQUAL(ret->getId(), 0);
        BOOST_CHECK_EQUAL(ret->getSOE(), 0);
        BOOST_CHECK_EQUAL(ret->getText(), "Test MCT-410iL");
        BOOST_CHECK_EQUAL(ret->getAdditionalInfo(), "CONNECT");
        BOOST_CHECK_EQUAL(ret->getLogType(), 3);
        BOOST_CHECK_EQUAL(ret->getSignalCategory(), 1);
        BOOST_CHECK_EQUAL(ret->getUser(), "(yukon system)");
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct_control_connect_decode)
    {
        CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

        INMESS im;

        im.Sequence = EmetconProtocol::Control_Connect;
        im.Buffer.DSt.Length = 0;
        im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

        strcpy(im.Return.CommandStr, "control connect");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.ResultDecode(im, timeNow, vgList, retList, outList) );

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

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&req, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( 1, vgList.size() );
        BOOST_CHECK( retList.empty() );
        BOOST_REQUIRE_EQUAL( 1, outList.size() );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE( om );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 77 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       0 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   0 );

        auto ret = dynamic_cast<const CtiSignalMsg *>(vgList.front());

        BOOST_REQUIRE(ret);
        BOOST_CHECK_EQUAL(ret->getId(), 0);
        BOOST_CHECK_EQUAL(ret->getSOE(), 0);
        BOOST_CHECK_EQUAL(ret->getText(), "Test MCT-410iL");
        BOOST_CHECK_EQUAL(ret->getAdditionalInfo(), "DISCONNECT");
        BOOST_CHECK_EQUAL(ret->getLogType(), 3);
        BOOST_CHECK_EQUAL(ret->getSignalCategory(), 1);
        BOOST_CHECK_EQUAL(ret->getUser(), "(yukon system)");
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct_control_disconnect_decode)
    {
        CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

        INMESS im;

        im.Sequence = EmetconProtocol::Control_Disconnect;
        im.Buffer.DSt.Length = 0;
        im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

        strcpy(im.Return.CommandStr, "control disconnect");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.ResultDecode(im, timeNow, vgList, retList, outList) );

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
    const Cti::ConnectionHandle testConnHandle{ 999 };
    CtiRequestMsg request;
    OUTMESS *om;

    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    mctExecute_helper() :
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
    {
        om = new OUTMESS;
        request.setConnectionHandle(testConnHandle);
    }
    ~mctExecute_helper()
    {
        delete om;
    }
};

struct mctExecute_noConfig_helper : executeRequest_helper
{
    const Cti::ConnectionHandle testConnHandle{ 999 };
    CtiRequestMsg request;
    OUTMESS *om;

    Cti::Test::Override_ConfigManager overrideConfigManager;

    mctExecute_noConfig_helper() :
        overrideConfigManager(Cti::Config::DeviceConfigSPtr())
    {
        om = new OUTMESS;
        request.setConnectionHandle(testConnHandle);
    }
    ~mctExecute_noConfig_helper()
    {
        delete om;
    }
};

bool isSentOnRouteMsg(const CtiMessage *msg)
{
    if( auto ret = dynamic_cast<const CtiReturnMsg *>(msg) )
    {
        return ret->ResultString() == "Emetcon DLC command sent on route ";
    }

    return false;
}

BOOST_FIXTURE_TEST_SUITE(command_executions, mctExecute_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
    /*
    *** TESTING: "putconfig emetcon centron...." commands and parameters
    */
    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 5x1 test 0s errors disable" );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  2 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x00 , om->Buffer.BSt.Message[1] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 40 display 5x1 test 0s errors disable" );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  3 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x00 , om->Buffer.BSt.Message[1] );
        BOOST_CHECK_EQUAL(   40 , om->Buffer.BSt.Message[2] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_4x1_1s_enable)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 4x1 test 1s errors enable" );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  2 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x15 , om->Buffer.BSt.Message[1] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_4x1_1s_enable)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 60 display 4x1 test 1s errors enable" );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  3 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x15 , om->Buffer.BSt.Message[1] );
        BOOST_CHECK_EQUAL(   60 , om->Buffer.BSt.Message[2] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_4x10_7s_enable)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 4x10 test 7s errors enable" );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  2 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x1E , om->Buffer.BSt.Message[1] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_4x10_7s_enable)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 200 display 4x10 test 7s errors enable" );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

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

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  1 , retList.size() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( errorMsg );

        BOOST_CHECK_EQUAL( ClientErrors::BadParameter , errorMsg->Status() );
        BOOST_CHECK_EQUAL( "Test MCT-410iL / Invalid display configuration \"5x3\"" , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_invalid_display)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 40 display 5x3 test 0s errors disable" );
                                                                        //  5x3 is not a valid option
        BOOST_CHECK_EQUAL(    0 , retList.size() );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  1 , retList.size() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( errorMsg );

        BOOST_CHECK_EQUAL( ClientErrors::BadParameter , errorMsg->Status() );
        BOOST_CHECK_EQUAL( "Test MCT-410iL / Invalid display configuration \"5x3\"" , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_invalid_test)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 5x1 test 3s errors disable" );
                                                                         // 3 is not a valid option
        BOOST_CHECK_EQUAL(    0 , retList.size() );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  1 , retList.size() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( errorMsg );

        BOOST_CHECK_EQUAL( ClientErrors::BadParameter , errorMsg->Status() );
        BOOST_CHECK_EQUAL( "Test MCT-410iL / Invalid test duration \"3\"" , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_invalid_test)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 40 display 5x1 test 3s errors disable" );
                                                                                  // 3 is not a valid option
        BOOST_CHECK_EQUAL(    0 , retList.size() );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  1 , retList.size() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( errorMsg );

        BOOST_CHECK_EQUAL( ClientErrors::BadParameter , errorMsg->Status() );
        BOOST_CHECK_EQUAL( "Test MCT-410iL / Invalid test duration \"3\"" , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_out_of_bounds)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 400 display 5x1 test 0s errors disable" );
                                                              // 400 is not a valid option ( 0 <= ratio <= 255 )
        BOOST_CHECK_EQUAL(    0 , retList.size() );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  1 , retList.size() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( errorMsg );

        BOOST_CHECK_EQUAL( ClientErrors::BadParameter , errorMsg->Status() );
        BOOST_CHECK_EQUAL( "Test MCT-410iL / Invalid transformer ratio (400)" , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_errors)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 5x1 test 0s" );

        BOOST_CHECK_EQUAL( ClientErrors::NoMethod, mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );
        BOOST_CHECK_EQUAL(        0, om->Buffer.BSt.Length );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_test)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 5x1 errors enable" );

        BOOST_CHECK_EQUAL( ClientErrors::NoMethod, mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );
        BOOST_CHECK_EQUAL(        0, om->Buffer.BSt.Length );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_display)
    {
        CtiCommandParser parse( "putconfig emetcon centron test 0 errors enable" );

        BOOST_CHECK_EQUAL( ClientErrors::NoMethod, mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );
        BOOST_CHECK_EQUAL(        0, om->Buffer.BSt.Length );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_all_with_ratio)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 40" );

        BOOST_CHECK_EQUAL( ClientErrors::NoMethod, mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );
        BOOST_CHECK_EQUAL(        0, om->Buffer.BSt.Length );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_putconfig_timezone)
    {
        CtiCommandParser parse( "putconfig emetcon timezone ct" );

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x3f);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Message[0], 0xe8);  //  Sent in 15 minute increments, so -6 * 4 = -24 = 0xff - 23 = 0xff - 0x17 = 0xe8
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getconfig_address_unique)
    {
        CtiCommandParser parse( "getconfig address unique" );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executeGetConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x10);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   3);
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getconfig_thresholds)
    {
        CtiCommandParser parse( "getconfig thresholds" );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executeGetConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x1e);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   5);
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getconfig_meter_ratio)
    {
        CtiCommandParser parse( "getconfig meter ratio" );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executeGetConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x19);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1);
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getconfig_freeze)
    {
        CtiCommandParser parse( "getconfig freeze" );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executeGetConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x4f);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1);
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_putstatus_reset)
    {
        CtiCommandParser parse( "putstatus reset alarms" );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executePutStatus(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

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
            unsigned char input[13] {0x50, 1, 2, 3, 4, 5, 0x50, 7, 8, 9, 0x8a, 11, 12};

            std::copy(input, input + 13, im.Buffer.DSt.Message);
            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            strcpy(im.Return.CommandStr, "getvalue outage 1");

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.decodeGetValueOutage(im, timeNow, vgList, retList, outList) );
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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.decodeGetValueOutage(im, timeNow, vgList, retList, outList) );
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

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_peak_frozen_scheduled_freeze)
    {
        CtiTime configTime(CtiDate(1, 10, 2009), 1, 2, 3);
        CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeDay, 21);
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeConfigTimestamp, configTime);
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp, configTime);

        {
            CtiCommandParser parse("getvalue peak frozen");

            BOOST_CHECK_EQUAL(ClientErrors::None, mct410.executeGetValue(&request, parse, om, vgList, retList, outList));

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL(om->Buffer.BSt.IO, Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL(om->Buffer.BSt.Function, 0x94);
            BOOST_CHECK_EQUAL(om->Buffer.BSt.Length, 10);

            BOOST_CHECK(outList.empty());
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            std::vector<unsigned char> buf{ 0x31, 0x02, 0x4b, 0x00, 0x4d, 0x9c, 0x01, 0x02, 0x04, 0x63 };

            std::copy(buf.begin(), buf.end(), im.Buffer.DSt.Message);

            im.Buffer.DSt.Length = buf.size();
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            strcpy_s(im.Return.CommandStr, "getvalue peak frozen");

            BOOST_CHECK_EQUAL(ClientErrors::None, mct410.decodeGetValuePeakDemand(im, timeNow, vgList, retList, outList));
        }

        {
            BOOST_REQUIRE_EQUAL(retList.size(), 1);

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            const auto& points = retMsg->PointData();

            {
                BOOST_REQUIRE_EQUAL(points.size(), 3);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE(pdata);

                    BOOST_CHECK_CLOSE(pdata->getValue(), 0.258, 0.0001);
                    BOOST_CHECK_EQUAL(pdata->getQuality(), NormalQuality);
                    BOOST_CHECK_EQUAL(pdata->getString(), "Test MCT-410iL / DemandAccumulator11 = 0.258 @ 11/15/2009 12:51:08");
                    BOOST_CHECK_EQUAL(pdata->getId(), 1);
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE(pdata);

                    BOOST_CHECK_CLOSE(pdata->getValue(), 0.258, 0.0001);
                    BOOST_CHECK_EQUAL(pdata->getQuality(), NormalQuality);
                    BOOST_CHECK_EQUAL(pdata->getString(), "Test MCT-410iL / DemandAccumulator21 = 0.258 @ 11/15/2009 12:51:08");
                    BOOST_CHECK_EQUAL(pdata->getId(), 2);
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<const CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE(pdata);

                    BOOST_CHECK_EQUAL(pdata->getValue(), 66052);
                    BOOST_CHECK_EQUAL(pdata->getQuality(), NormalQuality);
                    BOOST_CHECK_EQUAL(pdata->getString(), "Test MCT-410iL / PulseAccumulator1 = 66052.000 @ 12/22/2009 00:00:00");
                    BOOST_CHECK_EQUAL(pdata->getId(), 3);
                }
            }
        }
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_peak_frozen_scheduled_freeze_invalid_peak_time)
    {
        CtiTime configTime(CtiDate(1, 10, 2009), 1, 2, 3);
        CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeDay, 21);
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeConfigTimestamp, configTime);
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp, configTime);
        
        {
            CtiCommandParser parse("getvalue peak frozen");

            BOOST_CHECK_EQUAL(ClientErrors::None, mct410.executeGetValue(&request, parse, om, vgList, retList, outList));

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL(om->Buffer.BSt.IO, Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL(om->Buffer.BSt.Function, 0x94);
            BOOST_CHECK_EQUAL(om->Buffer.BSt.Length, 10);

            BOOST_CHECK(outList.empty());
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            std::vector<unsigned char> buf { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x04, 0x63 };

            std::copy(buf.begin(), buf.end(), im.Buffer.DSt.Message);

            im.Buffer.DSt.Length = buf.size();
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            strcpy_s(im.Return.CommandStr, "getvalue peak frozen");

            BOOST_CHECK_EQUAL(ClientErrors::InvalidFrozenPeakTimestamp, mct410.decodeGetValuePeakDemand(im, timeNow, vgList, retList, outList));
        }

        {
            BOOST_REQUIRE_EQUAL(retList.size(), 1);

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            BOOST_CHECK(retMsg->PointData().empty());

            BOOST_CHECK_EQUAL(retMsg->ResultString(), 
                              "Test MCT-410iL / DemandAccumulator11 = (invalid data) [Invalid peak timestamp]"
                              "\nTest MCT-410iL / DemandAccumulator21 = (invalid data) [Invalid peak timestamp]");
        }
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_0kwh)
    {
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_REQUIRE_EQUAL( points.size(), 6 );

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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

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

            BOOST_CHECK_EQUAL( ClientErrors::InvalidTimestamp , mct410.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::InvalidTimestamp );
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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

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

            BOOST_CHECK_EQUAL( ClientErrors::None , mct410.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
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

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_read_detail_daily_peak_point)
    {
        const CtiDate nowDate(17, 10, 2017);
        const CtiTime nowTime(nowDate, 8, 24, 57);

        Cti::Test::Override_CtiDate_Now overrideDate(nowDate);
        Cti::Test::Override_CtiTime_Now overrideTime(nowTime);

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse("getvalue daily read detail");  //  detail, includes daily peak

            BOOST_CHECK_EQUAL(ClientErrors::None, mct410.executeGetValue(&request, parse, om, vgList, retList, outList));

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL(om->Buffer.BSt.IO, Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL(om->Buffer.BSt.Function, 0x30);
            BOOST_CHECK_EQUAL(om->Buffer.BSt.Length, 11);

            BOOST_CHECK(outList.empty());
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x01, 0xe2, 0x40, 0x16, 0xcc, 0x02, 0xf2, 0x03, 0x09, 0x10, 0x09 };

            std::copy(buf, buf + 13, im.Buffer.DSt.Message);

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL(ClientErrors::None, mct410.decodeGetValueDailyRead(im, nowTime, vgList, retList, outList));
        }

        {
            BOOST_REQUIRE_EQUAL(retList.size(), 1);

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);
            
            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_REQUIRE_EQUAL(points.size(), 3);

                const CtiDate Midnight(nowTime);
                const CtiTime DemandPeak(Midnight - 1, 12, 34);

                auto point_itr = points.begin();

                {
                    const auto pdata = dynamic_cast<const CtiPointDataMsg *>(*point_itr++);

                    BOOST_REQUIRE(pdata);

                    BOOST_CHECK_EQUAL(1, pdata->getId());
                    BOOST_CHECK_EQUAL(1, mct410.getDevicePointOffsetTypeEqual(20, PulseAccumulatorPointType)->getID());
                    BOOST_CHECK_EQUAL(pdata->getValue(), 777);
                    BOOST_CHECK_EQUAL(pdata->getQuality(), NormalQuality);
                    BOOST_CHECK_EQUAL(pdata->getTime(), Midnight);
                }
                {
                    const auto pdata = dynamic_cast<const CtiPointDataMsg *>(*point_itr++);

                    BOOST_REQUIRE(pdata);

                    BOOST_CHECK_EQUAL(2, pdata->getId());
                    BOOST_CHECK_EQUAL(2, mct410.getDevicePointOffsetTypeEqual(1, PulseAccumulatorPointType)->getID());
                    BOOST_CHECK_EQUAL(pdata->getValue(), 123456);
                    BOOST_CHECK_EQUAL(pdata->getQuality(), NormalQuality);
                    BOOST_CHECK_EQUAL(pdata->getTime(), Midnight);
                }
                {
                    const auto pdata = dynamic_cast<const CtiPointDataMsg *>(*point_itr++);

                    BOOST_REQUIRE(pdata);

                    BOOST_CHECK_EQUAL(3, pdata->getId());
                    BOOST_CHECK_EQUAL(3, mct410.getDevicePointOffsetTypeEqual(1, DemandAccumulatorPointType)->getID());
                    BOOST_CHECK_EQUAL(pdata->getValue(), 174);
                    BOOST_CHECK_EQUAL(pdata->getQuality(), NormalQuality);
                    BOOST_CHECK_EQUAL(pdata->getTime(), DemandPeak);
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

            BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

            BOOST_REQUIRE_EQUAL( retList.size(), 1 );
            BOOST_CHECK( vgList.empty() );
            BOOST_REQUIRE_EQUAL( outList.size(), 1 );

            BOOST_CHECK( isSentOnRouteMsg(retList.front()) );

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

            BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.ResultDecode(InMessage, t, vgList, retList, outList));

            BOOST_REQUIRE_EQUAL(retList.size(), 2);
            BOOST_CHECK(outList.empty());
            BOOST_REQUIRE_EQUAL(vgList.size(), 2);

            auto retList_itr = retList.begin();

            {
                BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );
            }
            {
                auto *retMsg = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                const std::string expected =
                    "Test MCT-410iL / Disconnect Info:\n"
                    "Disconnect load limit count: 1\n"
                    "Test MCT-410iL / Disconnect Config:\n"
                    "Disconnect receiver address: 66049\n"
                    "Disconnect load limit connect delay: 4 minutes\n"
                    "Autoreconnect enabled\n"
                    "Disconnect demand threshold disabled\n";

                BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
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

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 2 );

        BOOST_CHECK( isSentOnRouteMsg(retList.front()) );

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
        mct410.test_demandInterval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect");

        BOOST_CHECK_EQUAL(ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        BOOST_CHECK( isSentOnRouteMsg(retList.front()) );

        CtiOutMessage *outmsg = outList.front();

        BOOST_REQUIRE( outmsg );

        std::vector<unsigned char> expected = {
                0x04, 0xcb, 0x2f, 0x00, 0x00, 0x05 };

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
        mct410.test_demandInterval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect load limit 1.234 4");

        BOOST_CHECK_EQUAL(ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        BOOST_CHECK( isSentOnRouteMsg(retList.front()) );

        CtiOutMessage *outmsg = outList.front();

        BOOST_REQUIRE( outmsg );

        std::vector<unsigned char> expected = {
                0x04, 0xcb, 0x2f, 0x34, 0x04, 0x04 };

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
        mct410.test_demandInterval = 300;

        //  load limit of 12345.678 kW is 1028.81 kWh/5 min interval - much higher than the 409.5 kWh allowed
        CtiCommandParser parse("putconfig emetcon disconnect load limit 12345.678 4");

        BOOST_CHECK_EQUAL(ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg*>(retList.front());

        BOOST_REQUIRE( errorMsg );

        const std::string expected = "Test MCT-410iL / Invalid disconnect parameters (1028806.500, 4)";

        BOOST_CHECK_EQUAL( ClientErrors::BadParameter , errorMsg->Status() );
        BOOST_CHECK_EQUAL( expected , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigDisconnect_load_limit_bad_connect_delay)
    {
        test_Mct410IconDevice mct410;

        mct410.setDisconnectAddress(314159);
        mct410.test_demandInterval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect load limit 1.234 11");  //  max is 10 mins

        BOOST_CHECK_EQUAL(ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg*>(retList.front());

        BOOST_REQUIRE( errorMsg );

        const std::string expected = "Test MCT-410iL / Invalid disconnect parameters (102.833, 11)";

        BOOST_CHECK_EQUAL( ClientErrors::BadParameter , errorMsg->Status() );
        BOOST_CHECK_EQUAL( expected , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigDisconnect_cycle)
    {
        test_Mct410IconDevice mct410;

        mct410.setDisconnectAddress(314159);
        mct410.test_demandInterval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect cycle 7 17");

        BOOST_CHECK_EQUAL(ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        BOOST_CHECK( isSentOnRouteMsg(retList.front()) );

        CtiOutMessage *outmsg = outList.front();

        BOOST_REQUIRE( outmsg );

        std::vector<unsigned char> expected = {
                0x04, 0xcb, 0x2f, 0x00, 0x00, 0x05, 0x07, 0x11 };

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
        mct410.test_demandInterval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect cycle 4 17");  //  min is 5

        BOOST_CHECK_EQUAL(ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg*>(retList.front());

        BOOST_REQUIRE( errorMsg );

        const std::string expected = "Test MCT-410iL / Invalid disconnect cycle parameters (4, 17)";

        BOOST_CHECK_EQUAL( ClientErrors::BadParameter , errorMsg->Status() );
        BOOST_CHECK_EQUAL( expected , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigDisconnect_cycle_bad_connect_high)
    {
        test_Mct410IconDevice mct410;

        mct410.setDisconnectAddress(314159);
        mct410.test_demandInterval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect cycle 64 17");  //  max is 60

        BOOST_CHECK_EQUAL(ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg*>(retList.front());

        BOOST_REQUIRE( errorMsg );

        const std::string expected = "Test MCT-410iL / Invalid disconnect cycle parameters (64, 17)";

        BOOST_CHECK_EQUAL( ClientErrors::BadParameter , errorMsg->Status() );
        BOOST_CHECK_EQUAL( expected , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigDisconnect_cycle_bad_disconnect_low)
    {
        test_Mct410IconDevice mct410;

        mct410.setDisconnectAddress(314159);
        mct410.test_demandInterval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect cycle 7 4");  //  min is 5

        BOOST_CHECK_EQUAL(ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg*>(retList.front());

        BOOST_REQUIRE( errorMsg );

        const std::string expected = "Test MCT-410iL / Invalid disconnect cycle parameters (7, 4)";

        BOOST_CHECK_EQUAL( ClientErrors::BadParameter , errorMsg->Status() );
        BOOST_CHECK_EQUAL( expected , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigDisconnect_cycle_bad_disconnect_high)
    {
        test_Mct410IconDevice mct410;

        mct410.setDisconnectAddress(314159);
        mct410.test_demandInterval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect cycle 7 77");  //  max is 60

        BOOST_CHECK_EQUAL(ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );

        const CtiReturnMsg *errorMsg = dynamic_cast<const CtiReturnMsg*>(retList.front());

        BOOST_REQUIRE( errorMsg );

        const std::string expected = "Test MCT-410iL / Invalid disconnect cycle parameters (7, 77)";

        BOOST_CHECK_EQUAL( ClientErrors::BadParameter , errorMsg->Status() );
        BOOST_CHECK_EQUAL( expected , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigDisconnect_load_limit_and_cycle)
    {
        test_Mct410IconDevice mct410;

        mct410.setDisconnectAddress(314159);
        mct410.test_demandInterval = 300;

        CtiCommandParser parse("putconfig emetcon disconnect load limit 1.234 4 cycle 7 17");

        BOOST_CHECK_EQUAL(ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        BOOST_CHECK( isSentOnRouteMsg(retList.front()) );

        CtiOutMessage *outmsg = outList.front();

        BOOST_REQUIRE( outmsg );

        std::vector<unsigned char> expected = {
                0x04, 0xcb, 0x2f, 0x34, 0x04, 0x04, 0x07, 0x11 };

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

    BOOST_FIXTURE_TEST_CASE(test_putconfig_install_all_no_config, mctExecute_noConfig_helper)
    {
        test_Mct410IconDevice mct410;

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 2 );
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

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("disconnectMode", "DEMAND_THRESHOLD");
        config.insertValue("disconnectDemandThreshold", "2.71");
        config.insertValue("disconnectLoadLimitConnectDelay", "4");
        config.insertValue("disconnectMinutes", "7");
        config.insertValue("connectMinutes", "17");
        config.insertValue("reconnectParam", "ARM");
        config.insertValue("demandFreezeDay", "12");
        config.insertValue("timeZoneOffset", "CHICAGO");

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 6 );
        BOOST_REQUIRE_EQUAL( outList.size(), 6 );

        auto retList_itr = retList.begin();
        BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );
        BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );
        BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );
        BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );
        BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );

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

            const std::vector<unsigned> expected = {
                0x12, 0xd6, 0x87, 0x2a, 0x96, 0x04, 0x07, 0x11, 0x40 };

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

            const std::vector<unsigned> expected = {
                0x0c };

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

        // Timezone message
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x3f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            const std::vector<unsigned> expected = {
                0xe8 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL(om->Buffer.BSt.IO,          1 );
            BOOST_CHECK_EQUAL(om->Buffer.BSt.Function, 0x3f );
            BOOST_CHECK_EQUAL(om->Buffer.BSt.Length,      1 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority

        BOOST_CHECK(writeMsgPriority > readMsgPriority);
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_all_alternate)
    {
        test_Mct410IconDevice mct410;
        mct410.setDisconnectAddress(123456);

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("disconnectMode", "DEMAND_THRESHOLD");
        config.insertValue("disconnectDemandThreshold", "4.71");
        config.insertValue("disconnectLoadLimitConnectDelay", "3");
        config.insertValue("disconnectMinutes", "6");
        config.insertValue("connectMinutes", "18");
        config.insertValue("reconnectParam", "IMMEDIATE");
        config.insertValue("demandFreezeDay", "21");
        config.insertValue("timeZoneOffset", "CHICAGO");

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 6 );
        BOOST_REQUIRE_EQUAL( outList.size(), 6 );

        auto retList_itr = retList.begin();
        BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );
        BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );
        BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );
        BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );
        BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );

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

            const std::vector<unsigned> expected = {
                0x01, 0xe2, 0x40, 0x11, 0xd7, 0x03, 0x06, 0x12, 0x44 };

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

            const std::vector<unsigned> expected = {
                0x15 };

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

        // Timezone message
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x3f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            const std::vector<unsigned> expected = {
                0xe8 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x3f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority

        BOOST_CHECK(writeMsgPriority > readMsgPriority);
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_disconnect_demand_threshold)
    {
        test_Mct410IconDevice mct410;
        mct410.setDisconnectAddress(1234567);

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("disconnectMode", "DEMAND_THRESHOLD");
        config.insertValue("disconnectDemandThreshold", "2.71");
        config.insertValue("disconnectLoadLimitConnectDelay", "4");
        config.insertValue("disconnectMinutes", "7");
        config.insertValue("connectMinutes", "17");
        config.insertValue("reconnectParam", "ARM");

        CtiCommandParser parse("putconfig install disconnect");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 2 );
        BOOST_REQUIRE_EQUAL( outList.size(), 2 );

        auto retList_itr = retList.begin();
        BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );
        BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        int writeMsgPriority,
            readMsgPriority;        // Capture message priorities to validate ordering

        INMESS im;

        // Disconnect messages - read-after-write.
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xfe );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      9 );

            const std::vector<unsigned> expected = {
                0x12, 0xd6, 0x87, 0x2a, 0x96, 0x04, 0x07, 0x11, 0x40 };

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
            OutEchoToIN(*om, im);
        }

        // This validates the read-after-write behavior... write message has higher priority

        BOOST_CHECK( writeMsgPriority > readMsgPriority );

        {
            im.Return.ProtocolInfo.Emetcon.Function = 0xfe;
            im.Return.ProtocolInfo.Emetcon.IO = 3;
            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Message[5] = 0x12;
            im.Buffer.DSt.Message[6] = 0x34;

            BOOST_CHECK( ! mct410.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold) );
            BOOST_CHECK( ! mct410.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DisconnectMode) );

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.ResultDecode(im, CtiTime(), vgList, retList, outList) );

            double threshold = 0.0;

            BOOST_CHECK( mct410.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold, threshold) );

            BOOST_CHECK_CLOSE( threshold, 5.64 * 12, 0.001 );

            std::string mode;

            BOOST_CHECK( mct410.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DisconnectMode, mode) );

            BOOST_CHECK_EQUAL( mode, "DEMAND_THRESHOLD" );
        }
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_disconnect_cycling)
    {
        test_Mct410IconDevice mct410;
        mct410.setDisconnectAddress(1234567);

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("disconnectMode", "CYCLING");
        config.insertValue("disconnectDemandThreshold", "2.71");
        config.insertValue("disconnectLoadLimitConnectDelay", "4");
        config.insertValue("disconnectMinutes", "7");
        config.insertValue("connectMinutes", "17");
        config.insertValue("reconnectParam", "ARM");

        CtiCommandParser parse("putconfig install disconnect");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 2 );
        BOOST_REQUIRE_EQUAL( outList.size(), 2 );

        auto retList_itr = retList.begin();
        BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );
        BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        int writeMsgPriority,
            readMsgPriority;        // Capture message priorities to validate ordering

        INMESS im;

        // Disconnect messages - read-after-write.
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xfe );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      9 );

            const std::vector<unsigned> expected = {
                0x12, 0xd6, 0x87, 0x00, 0x00, 0x04, 0x07, 0x11, 0x40 };

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
            OutEchoToIN(*om, im);
        }

        // This validates the read-after-write behavior... write message has higher priority

        BOOST_CHECK( writeMsgPriority > readMsgPriority );

        {
            im.Return.ProtocolInfo.Emetcon.Function = 0xfe;
            im.Return.ProtocolInfo.Emetcon.IO = 3;
            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Message[9]  = 7;
            im.Buffer.DSt.Message[10] = 17;

            BOOST_CHECK( ! mct410.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold) );
            BOOST_CHECK( ! mct410.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DisconnectMode) );

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.ResultDecode(im, CtiTime(), vgList, retList, outList) );

            double threshold = 0.0;

            BOOST_CHECK( mct410.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold, threshold) );

            BOOST_CHECK_EQUAL( threshold, 0.0f );

            std::string mode;

            BOOST_CHECK( mct410.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DisconnectMode, mode) );

            BOOST_CHECK_EQUAL( mode, "CYCLING" );
        }
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_disconnect_on_demand)
    {
        test_Mct410IconDevice mct410;
        mct410.setDisconnectAddress(1234567);

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("disconnectMode", "ON_DEMAND");
        config.insertValue("disconnectDemandThreshold", "2.71");
        config.insertValue("disconnectLoadLimitConnectDelay", "4");
        config.insertValue("disconnectMinutes", "7");
        config.insertValue("connectMinutes", "17");
        config.insertValue("reconnectParam", "ARM");

        CtiCommandParser parse("putconfig install disconnect");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 2 );
        BOOST_REQUIRE_EQUAL( outList.size(), 2 );

        auto retList_itr = retList.begin();
        BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );
        BOOST_CHECK( isSentOnRouteMsg(*retList_itr++) );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        int writeMsgPriority,
            readMsgPriority;        // Capture message priorities to validate ordering

        INMESS im;

        // Disconnect messages - read-after-write.
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xfe );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      9 );

            const std::vector<unsigned> expected = {
                0x12, 0xd6, 0x87, 0x00, 0x00, 0x04, 0x00, 0x00, 0x40 };

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
            OutEchoToIN(*om, im);
        }

        // This validates the read-after-write behavior... write message has higher priority

        BOOST_CHECK( writeMsgPriority > readMsgPriority );

        {
            im.Return.ProtocolInfo.Emetcon.Function = 0xfe;
            im.Return.ProtocolInfo.Emetcon.IO = 3;
            im.Buffer.DSt.Length = 13;

            BOOST_CHECK( ! mct410.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold) );
            BOOST_CHECK( ! mct410.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DisconnectMode) );

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.ResultDecode(im, CtiTime(), vgList, retList, outList) );

            double threshold = 0.0;

            BOOST_CHECK( mct410.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold, threshold) );

            BOOST_CHECK_EQUAL( threshold, 0.0f );

            std::string mode;

            BOOST_CHECK( mct410.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DisconnectMode, mode) );

            BOOST_CHECK_EQUAL( mode, "ON_DEMAND" );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_resume)
    {
        test_Mct410IconDevice mct410;

        Cti::Test::Override_CtiTime_Now overrideNow(CtiTime(CtiDate(21, 3, 2014), 12, 34, 56));

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestBegin, CtiTime(CtiDate(14, 3, 2014), 3, 0, 0));
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestEnd,   CtiTime(CtiDate(17, 3, 2014)));
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_Channel,      2);

        CtiCommandParser parse("getvalue lp resume");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        auto retList_itr = retList.cbegin();

        {
            auto req = dynamic_cast<const CtiRequestMsg *>(*retList_itr++);

            BOOST_REQUIRE(req);

            BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( req->CommandString(), "getvalue lp channel 2 03/14/2014 03:00:00 03/17/2014 00:00:00" );
            BOOST_CHECK_EQUAL( req->getMessagePriority(), 6 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_resume_missing_dynamicpaoinfo)
    {
        test_Mct410IconDevice mct410;

        Cti::Test::Override_CtiTime_Now overrideNow(CtiTime(CtiDate(21, 3, 2014), 12, 34, 56));

        CtiCommandParser parse("getvalue lp resume");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        auto retList_itr = retList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(),
                                    123456 );
            BOOST_CHECK_EQUAL( ret->Status(),
                                    ClientErrors::MissingParameter );
            BOOST_CHECK_EQUAL( ret->ResultString(),
                                    "Test MCT-410iL / Missing one of the following:"
                                    "\nKey_MCT_LLPInterest_Channel"
                                    "\nKey_MCT_LLPInterest_RequestBegin"
                                    "\nKey_MCT_LLPInterest_RequestEnd" );
            BOOST_CHECK( ! ret->ExpectMore() );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_cancel)
    {
        test_Mct410IconDevice mct410;

        CtiCommandParser parse("getvalue lp cancel");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        auto retList_itr = retList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / No active load profile requests to cancel" );
            BOOST_CHECK( ! ret->ExpectMore() );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_invalid_channel)
    {
        test_Mct410IconDevice mct410;

        CtiCommandParser parse("getvalue lp channel 5 3/17/2011");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        auto retList_itr = retList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 202 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Bad channel specification - Acceptable values:  1-4" );
            BOOST_CHECK( ! ret->ExpectMore() );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_invalid_interval)
    {
        test_Mct410IconDevice mct410;

        mct410.test_loadProfileInterval = -1;

        CtiCommandParser parse("getvalue lp channel 5 3/17/2011");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        auto retList_itr = retList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 202 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Bad channel specification - Acceptable values:  1-4" );
            BOOST_CHECK( ! ret->ExpectMore() );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_no_channel_config)
    {
        test_Mct410IconDevice mct410;

        mct410.test_needsChannelConfig = true;

        CtiCommandParser parse("getvalue lp channel 1 3/17/2011");

        BOOST_CHECK_EQUAL( ClientErrors::NoMethod, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 2 );

        auto retList_itr = retList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 278 );  //  NeedsChannelConfig
            BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Command requires channel configuration, but it has not been stored.  Attempting to retrieve it automatically." );
            BOOST_CHECK( ret->ExpectMore() );
        }
        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 202 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "NoMethod or invalid command." );
            BOOST_CHECK( ! ret->ExpectMore() );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_invalid_date)
    {
        test_Mct410IconDevice mct410;

        CtiCommandParser parse("getvalue lp channel 1 3/17/2101");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        auto retList_itr = retList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 202 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Bad start date \"3/17/2101\"" );
            BOOST_CHECK( ! ret->ExpectMore() );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_invalid_optionsField)
    {
        test_Mct410IconDevice mct410;

        CtiCommandParser parse("getvalue lp channel 1 3/17/2011");

        request.setOptionsField(9999);  //  invalid request ID

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        auto retList_itr = retList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 1 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Long load profile request was cancelled" );
            BOOST_CHECK( ! ret->ExpectMore() );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_invalid_times)
    {
        test_Mct410IconDevice mct410;

        CtiCommandParser parse("getvalue lp channel 1 8/8/2011 3/17/2011");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        auto retList_itr = retList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 26 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Invalid date/time for LP request (8/8/2011 - 3/17/2011)" );
            BOOST_CHECK( ! ret->ExpectMore() );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_background)
    {
        test_Mct410IconDevice mct410;
        const Cti::ConnectionHandle connHandle{ 999 };

        CtiCommandParser parse("getvalue lp channel 1 3/17/2011 background");
        request.setDeviceId(123456);
        request.setCommandString(parse.getCommandStr());
        request.setConnectionHandle(connHandle);

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 3 );

        auto retList_itr = retList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Load profile request submitted for background processing - use \"getvalue lp status\" to check progress" );
            BOOST_CHECK( ret->getConnectionHandle() == connHandle );
            BOOST_CHECK( ret->ExpectMore() );  //  Bug!  Shouldn't have expectMore set
        }
        {
            auto req = dynamic_cast<const CtiRequestMsg *>(*retList_itr++);

            BOOST_REQUIRE(req);

            BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( req->CommandString(), "putconfig emetcon llp interest channel 1 3/17/2011 background" );
            BOOST_CHECK_EQUAL( req->getMessagePriority(), 0 );
        }
        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Sending load profile period of interest" );
            BOOST_CHECK( ret->getConnectionHandle() == Cti::ConnectionHandle::none );
            BOOST_CHECK( ret->ExpectMore() );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_misaligned_read)
    {
        test_Mct410IconDevice mct410;

        CtiCommandParser parse("getvalue lp channel 1 3/17/2011 read");
        request.setCommandString(parse.getCommandStr());

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        auto retList_itr = retList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 267 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Long load profile read setup error" );
            BOOST_CHECK( ! ret->ExpectMore() );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_aligned_read)
    {
        test_Mct410IconDevice mct410;

        //  set the expected period of interest
        CtiCommandParser interestParse("putconfig emetcon llp interest channel 1 3/17/2011");
        request.setCommandString(interestParse.getCommandStr());

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, interestParse, vgList, retList, outList) );

        {
            BOOST_CHECK( vgList.empty() );
            BOOST_REQUIRE_EQUAL( retList.size(), 1 );
            BOOST_REQUIRE_EQUAL( outList.size(), 1 );

            BOOST_CHECK( isSentOnRouteMsg(retList.front()) );

            const OUTMESS *msg = outList.front();

            BOOST_REQUIRE( msg );
            BOOST_CHECK_EQUAL( msg->Buffer.BSt.Length, 6 );

            const unsigned char *results_begin = msg->Buffer.BSt.Message;
            const unsigned char *results_end   = msg->Buffer.BSt.Message + msg->Buffer.BSt.Length;

            const std::vector<unsigned char> expected {
                0xff, 0x01, 0x4d, 0x81, 0x94, 0x24
            };

            BOOST_CHECK_EQUAL_COLLECTIONS( expected.begin(), expected.end(),
                                           results_begin,    results_end );

            delete_container(outList); outList.clear();
            delete_container(retList); retList.clear();
        }

        CtiCommandParser parse("getvalue lp channel 1 3/17/2011");
        request.setCommandString(parse.getCommandStr());

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 2 );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        auto retList_itr = retList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Reading 48 blocks" );
            BOOST_CHECK( ret->ExpectMore() );
        }

        BOOST_CHECK(isSentOnRouteMsg(*retList_itr++));

        {
            const OUTMESS *om = outList.front();

            BOOST_REQUIRE( om );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 64 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 13 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_single_interval)
    {
        test_Mct410IconDevice mct410;
        const CtiTime timeBegin(CtiDate(18, 3, 2011), 12, 34, 56);

        //  Initial request
        {
            CtiCommandParser parse("getvalue lp channel 1 3/17/2011 14:00");
            request.setCommandString(parse.getCommandStr());
            request.setDeviceId(123456);
            request.setMessagePriority(14);

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 2 );
        BOOST_CHECK( outList.empty() );

        {
            auto retList_itr = retList.cbegin();

            {
                auto req = dynamic_cast<const CtiRequestMsg *>(*retList_itr++);

                BOOST_REQUIRE(req);

                BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( req->CommandString(), "putconfig emetcon llp interest channel 1 3/17/2011 14:00" );
                BOOST_CHECK_EQUAL( req->getMessagePriority(), 14 );
            }
            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Sending load profile period of interest" );
                BOOST_CHECK( ret->ExpectMore() );
            }

            //  Submit period of interest request
            {
                auto continuationRequest = dynamic_cast<CtiRequestMsg *>(retList.front());

                BOOST_REQUIRE(continuationRequest);

                CtiCommandParser parse(continuationRequest->CommandString());

                BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(continuationRequest, parse, vgList, retList, outList) );
            }
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK_EQUAL( retList.size(), 3 );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        BOOST_CHECK( isSentOnRouteMsg(retList.back()) );

        {
            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE( om );
                BOOST_CHECK_EQUAL( om->Request.OptionsField, 1 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 2 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 5 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 6 );

                const unsigned char *results_begin = om->Buffer.BSt.Message;
                const unsigned char *results_end   = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

                const std::vector<unsigned char> expected {
                    0xff, 0x01, 0x4d, 0x82, 0x59, 0x04
                };

                BOOST_CHECK_EQUAL_COLLECTIONS( expected.begin(), expected.end(),
                                               results_begin,    results_end );

                OutEchoToIN(*om, im);

                im.Buffer.DSt.Length = 0;
            }

            delete_container(retList);  retList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.ResultDecode(im, timeBegin, vgList, retList, outList) );
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( outList.empty() );

        {
            auto req = dynamic_cast<CtiRequestMsg *>(retList.front());

            BOOST_REQUIRE(req);

            BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( req->CommandString(), "getvalue lp channel 1 3/17/2011 14:00 read" );
            BOOST_CHECK_EQUAL( req->getMessagePriority(), 14 );

            //  Submit actual read
            CtiCommandParser parse(req->CommandString());

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(req, parse, vgList, retList, outList) );
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 3 );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        {
            auto retList_itr = retList.cbegin();

            retList_itr++;  //  ignore the first entry, it's the request message from above

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Reading 1 block" );
                BOOST_CHECK( ret->ExpectMore() );
            }

            BOOST_CHECK(isSentOnRouteMsg(*retList_itr++));
        }

        {
            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE( om );
                BOOST_CHECK_EQUAL( om->Request.OptionsField, 2 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 3 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 64 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 13 );

                const unsigned char *results_begin = om->Buffer.BSt.Message;
                const unsigned char *results_end   = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

                OutEchoToIN(*om, im);

                unsigned char buf[13] = { 0x6c, 0x30, 0x01, 0x20, 0x01, 0x10, 0x01, 0x00, 0x01, 0x30, 0x02, 0x30, 0x03 };

                std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

                im.Buffer.DSt.Length = 13;
                im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            }

            delete_container(retList);  retList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.ResultDecode(im, timeBegin, vgList, retList, outList) );
        }

        BOOST_REQUIRE_EQUAL( vgList.size(), 2 );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( outList.empty() );

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Load profile request complete\n" );
            BOOST_CHECK( ! ret->ExpectMore() );
            BOOST_REQUIRE_EQUAL( ret->getCount(), 6 );
        }

        auto vg_itr = vgList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*vg_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Load profile request complete\n" );
            BOOST_REQUIRE_EQUAL( ret->getCount(), 6 );

            auto pd_itr = ret->PointData().cbegin();

            const auto Tolerance = 0.0001;

            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.012, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 14, 00, 00) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.012 @ 03/17/2011 14:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.12, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 14, 05, 00) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.120 @ 03/17/2011 14:05:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 1.2, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 14, 10, 00) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 1.200 @ 03/17/2011 14:10:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 12.0, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 14, 15, 00) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 12.000 @ 03/17/2011 14:15:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.024, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 14, 20, 00) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.024 @ 03/17/2011 14:20:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.036, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 14, 25, 00) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.036 @ 03/17/2011 14:25:00" );
            }
        }
        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*vg_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK( ret->ResultString().empty() );
            BOOST_REQUIRE_EQUAL( ret->getCount(), 2 );

            auto pd_itr = ret->PointData().cbegin();

            const auto Tolerance = 0.0001;

            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 2 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(18, 3, 2011), 12, 34, 56) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / Status10 = False @ 03/18/2011 12:34:56" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 3 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(18, 3, 2011), 12, 34, 56) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / Status9 = False @ 03/18/2011 12:34:56" );
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_single_day)
    {
        test_Mct410IconDevice mct410;
        mct410.test_loadProfileInterval = 3600;  //  so it only takes 4 requests to get the whole day

        const CtiTime timeBegin(CtiDate(18, 3, 2011), 12, 34, 56);

        //  Initial request
        {
            CtiCommandParser parse("getvalue lp channel 1 3/17/2011");
            request.setCommandString(parse.getCommandStr());
            request.setDeviceId(123456);
            request.setMessagePriority(14);

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 2 );
        BOOST_CHECK( outList.empty() );

        {
            auto retList_itr = retList.cbegin();

            {
                auto req = dynamic_cast<const CtiRequestMsg *>(*retList_itr++);

                BOOST_REQUIRE(req);

                BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( req->CommandString(), "putconfig emetcon llp interest channel 1 3/17/2011" );
                BOOST_CHECK_EQUAL( req->getMessagePriority(), 14 );
            }
            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Sending load profile period of interest" );
                BOOST_CHECK( ret->ExpectMore() );
            }

            //  Submit period of interest request
            {
                auto continuationRequest = dynamic_cast<CtiRequestMsg *>(retList.front());

                BOOST_REQUIRE(continuationRequest);

                CtiCommandParser parse(continuationRequest->CommandString());

                BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(continuationRequest, parse, vgList, retList, outList) );
            }
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 3 );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        BOOST_CHECK( isSentOnRouteMsg(retList.back()) );

        {
            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE( om );
                BOOST_CHECK_EQUAL( om->Request.OptionsField, 1 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 2 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 5 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 6 );

                const unsigned char *results_begin = om->Buffer.BSt.Message;
                const unsigned char *results_end   = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

                const std::vector<unsigned char> expected {
                    0xff, 0x01, 0x4d, 0x81, 0x87, 0x40
                };

                BOOST_CHECK_EQUAL_COLLECTIONS( expected.begin(), expected.end(),
                                               results_begin,    results_end );

                OutEchoToIN(*om, im);

                im.Buffer.DSt.Length = 0;
            }

            delete_container(retList);  retList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.ResultDecode(im, timeBegin, vgList, retList, outList) );
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( outList.empty() );

        {
            auto req = dynamic_cast<CtiRequestMsg *>(retList.front());

            BOOST_REQUIRE(req);

            BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( req->CommandString(), "getvalue lp channel 1 3/17/2011 read" );
            BOOST_CHECK_EQUAL( req->getMessagePriority(), 14 );

            //  Submit actual read
            CtiCommandParser parse(req->CommandString());

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(req, parse, vgList, retList, outList) );
        }

        //  Read first block

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 3 );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        {
            auto retList_itr = retList.cbegin();

            retList_itr++;  //  ignore the first entry, it's the request message from above

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Reading 4 blocks" );
                BOOST_CHECK( ret->ExpectMore() );
            }

            BOOST_CHECK(isSentOnRouteMsg(*retList_itr++));
        }

        {
            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE( om );
                BOOST_CHECK_EQUAL( om->Request.OptionsField, 2 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 3 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 64 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 13 );

                const unsigned char *results_begin = om->Buffer.BSt.Message;
                const unsigned char *results_end   = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

                OutEchoToIN(*om, im);

                unsigned char buf[13] = { 0x5a, 0x30, 0x01, 0x30, 0x02, 0x30, 0x03, 0x30, 0x04, 0x30, 0x05, 0x30, 0x06 };

                std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

                im.Buffer.DSt.Length = 13;
                im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            }

            delete_container(retList);  retList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.ResultDecode(im, timeBegin, vgList, retList, outList) );
        }

        BOOST_REQUIRE_EQUAL( vgList.size(), 2 );
        BOOST_REQUIRE_EQUAL( retList.size(), 3 );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        {
            auto retList_itr = retList.cbegin();

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Reading 3 blocks" );
                BOOST_CHECK( ret->ExpectMore() );
            }

            BOOST_CHECK(isSentOnRouteMsg(*retList_itr++));

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "" );
                BOOST_REQUIRE_EQUAL( ret->getCount(), 6 );
                BOOST_CHECK( ret->ExpectMore() );
            }
        }

        auto vg_itr = vgList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*vg_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "" );
            BOOST_REQUIRE_EQUAL( ret->getCount(), 6 );

            auto pd_itr = ret->PointData().cbegin();

            const auto Tolerance = 0.0001;

            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.001, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 0) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.001 @ 03/17/2011 00:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.002, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 1) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.002 @ 03/17/2011 01:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.003, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 2) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.003 @ 03/17/2011 02:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.004, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 3) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.004 @ 03/17/2011 03:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.005, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 4) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.005 @ 03/17/2011 04:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.006, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 5) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.006 @ 03/17/2011 05:00:00" );
            }
        }
        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*vg_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK( ret->ResultString().empty() );
            BOOST_REQUIRE_EQUAL( ret->getCount(), 2 );

            auto pd_itr = ret->PointData().cbegin();

            const auto Tolerance = 0.0001;

            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 2 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(18, 3, 2011), 12, 34, 56) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / Status10 = False @ 03/18/2011 12:34:56" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 3 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(18, 3, 2011), 12, 34, 56) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / Status9 = False @ 03/18/2011 12:34:56" );
            }
        }

        //  Read second block

        {
            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE( om );
                BOOST_CHECK_EQUAL( om->Request.OptionsField, 3 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 3 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 65 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 13 );

                const unsigned char *results_begin = om->Buffer.BSt.Message;
                const unsigned char *results_end   = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

                OutEchoToIN(*om, im);

                unsigned char buf[13] = { 0x5a, 0x30, 0x07, 0x30, 0x08, 0x30, 0x09, 0x30, 0x0a, 0x30, 0x0b, 0x30, 0x0c };

                std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

                im.Buffer.DSt.Length = 13;
                im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            }

            delete_container(retList);  retList.clear();
            delete_container(vgList);   vgList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.ResultDecode(im, timeBegin, vgList, retList, outList) );
        }

        BOOST_REQUIRE_EQUAL( vgList.size(), 2 );
        BOOST_REQUIRE_EQUAL( retList.size(), 3 );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        {
            auto retList_itr = retList.cbegin();

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Reading 2 blocks" );
                BOOST_CHECK( ret->ExpectMore() );
            }

            BOOST_CHECK(isSentOnRouteMsg(*retList_itr++));

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "" );
                BOOST_REQUIRE_EQUAL( ret->getCount(), 6 );
                BOOST_CHECK( ret->ExpectMore() );
            }
        }

        vg_itr = vgList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*vg_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "" );
            BOOST_REQUIRE_EQUAL( ret->getCount(), 6 );

            auto pd_itr = ret->PointData().cbegin();

            const auto Tolerance = 0.0001;

            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.007, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011),  6) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.007 @ 03/17/2011 06:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.008, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011),  7) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.008 @ 03/17/2011 07:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.009, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011),  8) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.009 @ 03/17/2011 08:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.010, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011),  9) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.010 @ 03/17/2011 09:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.011, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 10) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.011 @ 03/17/2011 10:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.012, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 11) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.012 @ 03/17/2011 11:00:00" );
            }
        }
        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*vg_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK( ret->ResultString().empty() );
            BOOST_REQUIRE_EQUAL( ret->getCount(), 2 );

            auto pd_itr = ret->PointData().cbegin();

            const auto Tolerance = 0.0001;

            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 2 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(18, 3, 2011), 12, 34, 56) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / Status10 = False @ 03/18/2011 12:34:56" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 3 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(18, 3, 2011), 12, 34, 56) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / Status9 = False @ 03/18/2011 12:34:56" );
            }
        }

        //  Read third block

        {
            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE( om );
                BOOST_CHECK_EQUAL( om->Request.OptionsField, 4 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 3 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 66 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 13 );

                const unsigned char *results_begin = om->Buffer.BSt.Message;
                const unsigned char *results_end   = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

                OutEchoToIN(*om, im);

                unsigned char buf[13] = { 0x5a, 0x30, 0x0d, 0x30, 0x0e, 0x30, 0x0f, 0x30, 0x10, 0x30, 0x11, 0x30, 0x12 };

                std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

                im.Buffer.DSt.Length = 13;
                im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            }

            delete_container(retList);  retList.clear();
            delete_container(vgList);   vgList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.ResultDecode(im, timeBegin, vgList, retList, outList) );
        }

        BOOST_REQUIRE_EQUAL( vgList.size(), 2 );
        BOOST_REQUIRE_EQUAL( retList.size(), 3 );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        {
            auto retList_itr = retList.cbegin();

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Reading 1 block" );
                BOOST_CHECK( ret->ExpectMore() );
            }

            BOOST_CHECK(isSentOnRouteMsg(*retList_itr++));

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "" );
                BOOST_REQUIRE_EQUAL( ret->getCount(), 6 );
                BOOST_CHECK( ret->ExpectMore() );
            }
        }

        vg_itr = vgList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*vg_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "" );
            BOOST_REQUIRE_EQUAL( ret->getCount(), 6 );

            auto pd_itr = ret->PointData().cbegin();

            const auto Tolerance = 0.0001;

            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.013, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 12) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.013 @ 03/17/2011 12:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.014, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 13) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.014 @ 03/17/2011 13:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.015, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 14) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.015 @ 03/17/2011 14:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.016, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 15) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.016 @ 03/17/2011 15:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.017, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 16) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.017 @ 03/17/2011 16:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.018, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 17) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.018 @ 03/17/2011 17:00:00" );
            }
        }
        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*vg_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK( ret->ResultString().empty() );
            BOOST_REQUIRE_EQUAL( ret->getCount(), 2 );

            auto pd_itr = ret->PointData().cbegin();

            const auto Tolerance = 0.0001;

            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 2 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(18, 3, 2011), 12, 34, 56) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / Status10 = False @ 03/18/2011 12:34:56" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 3 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(18, 3, 2011), 12, 34, 56) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / Status9 = False @ 03/18/2011 12:34:56" );
            }
        }

        //  Read fourth block (final)

        {
            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE( om );
                BOOST_CHECK_EQUAL( om->Request.OptionsField, 5 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 3 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 67 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 13 );

                const unsigned char *results_begin = om->Buffer.BSt.Message;
                const unsigned char *results_end   = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

                OutEchoToIN(*om, im);

                unsigned char buf[13] = { 0x5a, 0x30, 0x13, 0x30, 0x14, 0x30, 0x15, 0x30, 0x16, 0x30, 0x17, 0x30, 0x18 };

                std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

                im.Buffer.DSt.Length = 13;
                im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            }

            delete_container(retList);  retList.clear();
            delete_container(vgList);   vgList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.ResultDecode(im, timeBegin, vgList, retList, outList) );
        }

        BOOST_REQUIRE_EQUAL( vgList.size(), 2 );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( outList.empty() );

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Load profile request complete\n" );
            BOOST_REQUIRE_EQUAL( ret->getCount(), 6 );
            BOOST_CHECK( ! ret->ExpectMore() );
        }

        vg_itr = vgList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*vg_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Load profile request complete\n" );
            BOOST_REQUIRE_EQUAL( ret->getCount(), 6 );

            auto pd_itr = ret->PointData().cbegin();

            const auto Tolerance = 0.0001;

            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.019, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 18) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.019 @ 03/17/2011 18:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.020, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 19) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.020 @ 03/17/2011 19:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.021, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 20) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.021 @ 03/17/2011 20:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.022, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 21) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.022 @ 03/17/2011 21:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.023, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 22) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.023 @ 03/17/2011 22:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.024, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 23) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.024 @ 03/17/2011 23:00:00" );
            }
        }
        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*vg_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK( ret->ResultString().empty() );
            BOOST_REQUIRE_EQUAL( ret->getCount(), 2 );

            auto pd_itr = ret->PointData().cbegin();

            const auto Tolerance = 0.0001;

            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 2 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(18, 3, 2011), 12, 34, 56) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / Status10 = False @ 03/18/2011 12:34:56" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 3 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(18, 3, 2011), 12, 34, 56) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / Status9 = False @ 03/18/2011 12:34:56" );
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_single_retry)
    {
        test_Mct410IconDevice mct410;
        const CtiTime timeBegin(CtiDate(18, 3, 2011), 12, 34, 56);

        //  Initial request
        {
            CtiCommandParser parse("getvalue lp channel 1 3/17/2011 14:00");
            request.setCommandString(parse.getCommandStr());
            request.setDeviceId(123456);
            request.setMessagePriority(14);

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 2 );
        BOOST_CHECK( outList.empty() );

        {
            auto retList_itr = retList.cbegin();

            {
                auto req = dynamic_cast<const CtiRequestMsg *>(*retList_itr++);

                BOOST_REQUIRE(req);

                BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( req->CommandString(), "putconfig emetcon llp interest channel 1 3/17/2011 14:00" );
                BOOST_CHECK_EQUAL( req->getMessagePriority(), 14 );
            }
            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Sending load profile period of interest" );
                BOOST_CHECK( ret->ExpectMore() );
            }

            //  Submit period of interest request
            {
                auto continuationRequest = dynamic_cast<CtiRequestMsg *>(retList.front());

                BOOST_REQUIRE(continuationRequest);

                CtiCommandParser parse(continuationRequest->CommandString());

                BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(continuationRequest, parse, vgList, retList, outList) );
            }
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 3 );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        BOOST_CHECK( isSentOnRouteMsg(retList.back()) );

        {
            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE( om );
                BOOST_CHECK_EQUAL( om->Request.OptionsField, 1 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 2 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 5 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 6 );

                const unsigned char *results_begin = om->Buffer.BSt.Message;
                const unsigned char *results_end   = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

                const std::vector<unsigned char> expected {
                    0xff, 0x01, 0x4d, 0x82, 0x59, 0x04
                };

                BOOST_CHECK_EQUAL_COLLECTIONS( expected.begin(), expected.end(),
                                               results_begin,    results_end );

                OutEchoToIN(*om, im);

                im.Buffer.DSt.Length = 0;
            }

            delete_container(retList);  retList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.ResultDecode(im, timeBegin, vgList, retList, outList) );
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( outList.empty() );

        {
            auto req = dynamic_cast<CtiRequestMsg *>(retList.front());

            BOOST_REQUIRE(req);

            BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( req->CommandString(), "getvalue lp channel 1 3/17/2011 14:00 read" );
            BOOST_CHECK_EQUAL( req->getMessagePriority(), 14 );

            //  Submit actual read
            CtiCommandParser parse(req->CommandString());

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(req, parse, vgList, retList, outList) );
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK_EQUAL( retList.size(), 3 );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        {
            auto retList_itr = retList.cbegin();

            retList_itr++;  //  ignore the first entry, it's the request message from above

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Reading 1 block" );
                BOOST_CHECK( ret->ExpectMore() );
            }

            BOOST_CHECK(isSentOnRouteMsg(*retList_itr++));
        }

        {
            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE( om );
                BOOST_CHECK_EQUAL( om->Request.OptionsField, 2 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 3 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 64 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 13 );

                const unsigned char *results_begin = om->Buffer.BSt.Message;
                const unsigned char *results_end   = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

                OutEchoToIN(*om, im);

                im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            }

            delete_container(retList);  retList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.SubmitRetry(im, timeBegin, vgList, retList, outList) );
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 3 );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        {
            auto retList_itr = retList.cbegin();

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Reading 1 block" );
                BOOST_CHECK( ret->ExpectMore() );
            }

            BOOST_CHECK(isSentOnRouteMsg(*retList_itr++));

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Load profile retry submitted" );
                BOOST_CHECK( ret->ExpectMore() );
            }
        }

        {
            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE( om );
                BOOST_CHECK_EQUAL( om->Request.OptionsField, 3 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 3 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 64 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 13 );

                const unsigned char *results_begin = om->Buffer.BSt.Message;
                const unsigned char *results_end   = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

                OutEchoToIN(*om, im);

                unsigned char buf[13] = { 0x6c, 0x30, 0x01, 0x20, 0x01, 0x10, 0x01, 0x00, 0x01, 0x30, 0x02, 0x30, 0x03 };

                std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

                im.Buffer.DSt.Length = 13;
                im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            }

            delete_container(retList);  retList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.ResultDecode(im, timeBegin, vgList, retList, outList) );
        }

        BOOST_REQUIRE_EQUAL( vgList.size(), 2 );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( outList.empty() );

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Load profile request complete\n" );
            BOOST_REQUIRE_EQUAL( ret->getCount(), 6 );
            BOOST_CHECK( ! ret->ExpectMore() );
        }

        auto vg_itr = vgList.cbegin();

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*vg_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Load profile request complete\n" );
            BOOST_REQUIRE_EQUAL( ret->getCount(), 6 );

            auto pd_itr = ret->PointData().cbegin();

            const auto Tolerance = 0.0001;

            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.012, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 14, 00, 00) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.012 @ 03/17/2011 14:00:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.12, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 14, 05, 00) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.120 @ 03/17/2011 14:05:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 1.2, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 14, 10, 00) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 1.200 @ 03/17/2011 14:10:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 12.0, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 14, 15, 00) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 12.000 @ 03/17/2011 14:15:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.024, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 14, 20, 00) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.024 @ 03/17/2011 14:20:00" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 1 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0.036, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(17, 3, 2011), 14, 25, 00) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / DemandAccumulator101 = 0.036 @ 03/17/2011 14:25:00" );
            }
        }
        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(*vg_itr++);

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 0 );
            BOOST_CHECK( ret->ResultString().empty() );
            BOOST_REQUIRE_EQUAL( ret->getCount(), 2 );

            auto pd_itr = ret->PointData().cbegin();

            const auto Tolerance = 0.0001;

            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 2 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(18, 3, 2011), 12, 34, 56) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / Status10 = False @ 03/18/2011 12:34:56" );
            }
            {
                auto pd = dynamic_cast<CtiPointDataMsg *>(*pd_itr++);
                BOOST_REQUIRE(pd);
                BOOST_CHECK_EQUAL( pd->getId(), 3 );
                BOOST_CHECK_CLOSE( pd->getValue(), 0, Tolerance );
                BOOST_CHECK_EQUAL( pd->getTime(), CtiTime(CtiDate(18, 3, 2011), 12, 34, 56) );
                BOOST_CHECK_EQUAL( pd->getString(), "Test MCT-410iL / Status9 = False @ 03/18/2011 12:34:56" );
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_failure)
    {
        test_Mct410IconDevice mct410;
        const CtiTime timeBegin(CtiDate(18, 3, 2011), 12, 34, 56);

        //  Initial request
        {
            CtiCommandParser parse("getvalue lp channel 1 3/17/2011 14:00");
            request.setCommandString(parse.getCommandStr());
            request.setDeviceId(123456);
            request.setMessagePriority(14);

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 2 );
        BOOST_CHECK( outList.empty() );

        {
            auto retList_itr = retList.cbegin();

            {
                auto req = dynamic_cast<const CtiRequestMsg *>(*retList_itr++);

                BOOST_REQUIRE(req);

                BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( req->CommandString(), "putconfig emetcon llp interest channel 1 3/17/2011 14:00" );
                BOOST_CHECK_EQUAL( req->getMessagePriority(), 14 );
            }
            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Sending load profile period of interest" );
                BOOST_CHECK( ret->ExpectMore() );
            }

            //  Submit period of interest request
            {
                auto continuationRequest = dynamic_cast<CtiRequestMsg *>(retList.front());

                BOOST_REQUIRE(continuationRequest);

                CtiCommandParser parse(continuationRequest->CommandString());

                BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(continuationRequest, parse, vgList, retList, outList) );
            }
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK_EQUAL( retList.size(), 3 );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        BOOST_CHECK( isSentOnRouteMsg(retList.back()) );

        {
            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE( om );
                BOOST_CHECK_EQUAL( om->Request.OptionsField, 1 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 2 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 5 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 6 );

                const unsigned char *results_begin = om->Buffer.BSt.Message;
                const unsigned char *results_end   = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

                const std::vector<unsigned char> expected {
                    0xff, 0x01, 0x4d, 0x82, 0x59, 0x04
                };

                BOOST_CHECK_EQUAL_COLLECTIONS( expected.begin(), expected.end(),
                                               results_begin,    results_end );

                OutEchoToIN(*om, im);

                im.Buffer.DSt.Length = 0;
            }

            delete_container(retList);  retList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.ResultDecode(im, timeBegin, vgList, retList, outList) );
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );
        BOOST_CHECK( outList.empty() );

        {
            auto req = dynamic_cast<CtiRequestMsg *>(retList.front());

            BOOST_REQUIRE(req);

            BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( req->CommandString(), "getvalue lp channel 1 3/17/2011 14:00 read" );
            BOOST_CHECK_EQUAL( req->getMessagePriority(), 14 );

            //  Submit actual read
            CtiCommandParser parse(req->CommandString());

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(req, parse, vgList, retList, outList) );
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK_EQUAL( retList.size(), 3 );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        {
            auto retList_itr = retList.cbegin();

            retList_itr++;  //  ignore the first message, it's the read from above

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Reading 1 block" );
                BOOST_CHECK( ret->ExpectMore() );
            }

            BOOST_CHECK(isSentOnRouteMsg(*retList_itr++));
        }

        {
            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE( om );
                BOOST_CHECK_EQUAL( om->Request.OptionsField, 2 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 3 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 64 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 13 );

                const unsigned char *results_begin = om->Buffer.BSt.Message;
                const unsigned char *results_end   = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

                OutEchoToIN(*om, im);

                im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            }

            delete_container(retList);  retList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.SubmitRetry(im, timeBegin, vgList, retList, outList) );
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 3 );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        {
            auto retList_itr = retList.cbegin();

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL(ret->DeviceId(), 123456);
                BOOST_CHECK_EQUAL(ret->Status(), 0);
                BOOST_CHECK_EQUAL(ret->ResultString(), "Test MCT-410iL / Reading 1 block");
                BOOST_CHECK(ret->ExpectMore());
            }

            BOOST_CHECK(isSentOnRouteMsg(*retList_itr++));

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Load profile retry submitted" );
                BOOST_CHECK( ret->ExpectMore() );
            }
        }

        {
            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE( om );
                BOOST_CHECK_EQUAL( om->Request.OptionsField, 3 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 3 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 64 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 13 );

                const unsigned char *results_begin = om->Buffer.BSt.Message;
                const unsigned char *results_end   = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

                OutEchoToIN(*om, im);

                im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            }

            delete_container(retList);  retList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.SubmitRetry(im, timeBegin, vgList, retList, outList) );
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 3 );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        {
            auto retList_itr = retList.cbegin();

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Reading 1 block" );
                BOOST_CHECK( ret->ExpectMore() );
            }

            BOOST_CHECK(isSentOnRouteMsg(*retList_itr++));

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Load profile retry submitted" );
                BOOST_CHECK( ret->ExpectMore() );
            }
        }

        {
            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE( om );
                BOOST_CHECK_EQUAL( om->Request.OptionsField, 4 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 3 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 64 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 13 );

                const unsigned char *results_begin = om->Buffer.BSt.Message;
                const unsigned char *results_end   = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

                OutEchoToIN(*om, im);

                im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            }

            delete_container(retList);  retList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.SubmitRetry(im, timeBegin, vgList, retList, outList) );
        }

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 3 );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        {
            auto retList_itr = retList.cbegin();

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Reading 1 block" );
                BOOST_CHECK( ret->ExpectMore() );
            }

            BOOST_CHECK(isSentOnRouteMsg(*retList_itr++));

            {
                auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
                BOOST_CHECK_EQUAL( ret->Status(), 0 );
                BOOST_CHECK_EQUAL( ret->ResultString(), "Load profile retry submitted" );
                BOOST_CHECK( ret->ExpectMore() );
            }
        }

        {
            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE( om );
                BOOST_CHECK_EQUAL( om->Request.OptionsField, 5 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 3 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 64 );
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 13 );

                const unsigned char *results_begin = om->Buffer.BSt.Message;
                const unsigned char *results_end   = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

                OutEchoToIN(*om, im);

                im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            }

            delete_container(retList);  retList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.SubmitRetry(im, timeBegin, vgList, retList, outList) );

            BOOST_CHECK( vgList.empty() );
            BOOST_CHECK( retList.empty() );
            BOOST_CHECK( outList.empty() );

            BOOST_CHECK_EQUAL( ClientErrors::None, mct410.ErrorDecode(im, timeBegin, retList) );
        }

        BOOST_CHECK( retList.empty() );
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_peak_after_today)
    {
        Cti::Test::Override_CtiDate_Now overrideDate(CtiDate(17, 3, 2014));

        test_Mct410IconDevice mct410;

        CtiCommandParser parse("getvalue lp peak day channel 1 3/18/2014 17");
        request.setCommandString(parse.getCommandStr());

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 279 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Invalid date for peak request: cannot be after today (03/18/2014)" );
            BOOST_CHECK( ! ret->ExpectMore() );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_peak_invalid_range)
    {
        Cti::Test::Override_CtiDate_Now overrideDate(CtiDate(17, 3, 2014));

        test_Mct410IconDevice mct410;

        CtiCommandParser parse("getvalue lp peak day channel 1 3/17/2014 1000");
        request.setCommandString(parse.getCommandStr());

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 26 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Invalid range for peak request: must be 1-999 (1000)" );
            BOOST_CHECK( ! ret->ExpectMore() );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_peak_missing_sspec)
    {
        Cti::Test::Override_CtiDate_Now overrideDate(CtiDate(17, 3, 2014));

        test_Mct410IconDevice mct410;

        CtiCommandParser parse("getvalue lp peak day channel 1 3/14/2014 17");
        request.setCommandString(parse.getCommandStr());

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 270 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / SSPEC revision not retrieved yet, attempting to read it automatically; please retry command in a few minutes" );
            BOOST_CHECK( ! ret->ExpectMore() );
        }
        {
            const OUTMESS *om = outList.front();

            BOOST_REQUIRE( om );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 8 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_peak_not_supported)
    {
        Cti::Test::Override_CtiDate_Now overrideDate(CtiDate(17, 3, 2014));

        test_Mct410IconDevice mct410;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 1029);
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 8);

        CtiCommandParser parse("getvalue lp peak day channel 1 3/17/2014 17");
        request.setCommandString(parse.getCommandStr());

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 269 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Load profile reporting not supported for this device's SSPEC revision" );
            BOOST_CHECK( ! ret->ExpectMore() );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_peak_invalid_peaktype)
    {
        Cti::Test::Override_CtiDate_Now overrideDate(CtiDate(17, 3, 2014));

        test_Mct410IconDevice mct410;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 1029);
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 10);

        CtiCommandParser parse("getvalue lp peak week channel 1 3/17/2014 17");
        request.setCommandString(parse.getCommandStr());

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        BOOST_CHECK( isSentOnRouteMsg(retList.front()) );

        //  Since the "lp peak" regex wasn't matched, it's interpreted as "getvalue peak", which is confusing and somewhat terrible
        {
            const OUTMESS *om = outList.front();

            BOOST_REQUIRE( om );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, 3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 147 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length, 9 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_peak_invalid_requestId)
    {
        Cti::Test::Override_CtiDate_Now overrideDate(CtiDate(17, 3, 2014));

        test_Mct410IconDevice mct410;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 1029);
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 10);

        CtiCommandParser parse("getvalue lp peak day channel 1 3/17/2014 17");
        request.setCommandString(parse.getCommandStr());
        request.setOptionsField(9999);  //  invalid request ID

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        {
            auto ret = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(ret);

            BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
            BOOST_CHECK_EQUAL( ret->Status(), 274 );
            BOOST_CHECK_EQUAL( ret->ResultString(), "Test MCT-410iL / Load profile peak request already in progress" );
            BOOST_CHECK( ! ret->ExpectMore() );
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_peak_overlap)
    {
        Cti::Test::Override_CtiDate_Now overrideDate(CtiDate(17, 3, 2014));

        const CtiTime timeBegin(CtiDate(18, 3, 2014), 12, 34, 56);

        test_Mct410IconDevice mct410;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 1029);
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 10);

        {
            CtiCommandParser parse("getvalue lp peak day channel 1 3/17/2014 17");
            request.setCommandString(parse.getCommandStr());

            BOOST_CHECK_EQUAL(ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList));
        }

        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(outList.size(), 1);
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        BOOST_CHECK(isSentOnRouteMsg(retList.front()));

        //  reading existing peak
        {
            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE(om);
                BOOST_CHECK_EQUAL(om->Buffer.BSt.IO, 3);
                BOOST_CHECK_EQUAL(om->Buffer.BSt.Function, 160);
                BOOST_CHECK_EQUAL(om->Buffer.BSt.Length, 7);

                OutEchoToIN(*om, im);

                std::array<unsigned char, 7> buf { 0x10, 0x01, 0x00, 0x01, 0x30, 0x02, 0x30 };

                std::copy(buf.begin(), buf.end(), im.Buffer.DSt.Message);

                im.Buffer.DSt.Length = buf.size();
                im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            }

            delete_container(retList);  retList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL(ClientErrors::None, mct410.ResultDecode(im, timeBegin, vgList, retList, outList));

            BOOST_REQUIRE_EQUAL(retList.size(), 1);
            BOOST_REQUIRE_EQUAL(vgList.size(), 1);
            BOOST_REQUIRE_EQUAL(outList.size(), 1);

            auto retList_itr = retList.cbegin();

            BOOST_CHECK(isSentOnRouteMsg(*retList_itr++));

            auto outList_itr = outList.cbegin();

            const auto om = *outList_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL(om->Buffer.BSt.IO, 2);
            BOOST_CHECK_EQUAL(om->Buffer.BSt.Function, 6);
            BOOST_CHECK_EQUAL(om->Buffer.BSt.Length, 9);

            const auto results_begin = om->Buffer.BSt.Message;
            const auto results_end = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

            std::array<unsigned char, 9> expected { 0xff, 0x01, 0x53, 0x27, 0xd2, 0xd0, 0x11, 0x00, 0x00 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                    expected.begin(), expected.end(),
                    results_begin, results_end);
        }
    }
    BOOST_AUTO_TEST_CASE(test_getvalue_lp_peak_no_overlap)
    {
        const CtiDate dateBegin { 4, 4, 2016 };
        const CtiTime timeBegin { dateBegin, 17, 24, 36 };

        Cti::Test::Override_CtiDate_Now overrideDate { dateBegin };
        Cti::Test::Override_CtiTime_Now overrideTime { timeBegin };

        test_Mct410IconDevice mct410;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 1029);
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 10);

        CtiCommandParser parse("getvalue lp peak day channel 1 04/04/2016 6");
        request.setCommandString(parse.getCommandStr());

        BOOST_CHECK_EQUAL( ClientErrors::None, mct410.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        //  reading existing peak
        {
            BOOST_CHECK(isSentOnRouteMsg(retList.front()));

            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE(om);
                BOOST_CHECK_EQUAL(om->Buffer.BSt.IO, 3);
                BOOST_CHECK_EQUAL(om->Buffer.BSt.Function, 160);
                BOOST_CHECK_EQUAL(om->Buffer.BSt.Length, 7);

                OutEchoToIN(*om, im);

                std::array<unsigned char, 7> buf = { 0x00, 0x02, 0x8d, 0x56, 0xfa, 0x0b, 0xcf };

                std::copy(buf.begin(), buf.end(), im.Buffer.DSt.Message);

                im.Buffer.DSt.Length = buf.size();
                im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            }

            delete_container(retList);  retList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL(ClientErrors::None, mct410.ResultDecode(im, timeBegin, vgList, retList, outList));
        }

        BOOST_REQUIRE_EQUAL(vgList.size(), 1);
        BOOST_REQUIRE_EQUAL(retList.size(), 1);
        BOOST_REQUIRE_EQUAL(outList.size(), 1);

        {
            auto retList_itr = retList.cbegin();

            BOOST_CHECK(isSentOnRouteMsg(*retList_itr++));

            auto outList_itr = outList.cbegin();

            const auto om = *outList_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL(om->Buffer.BSt.IO, 2);
            BOOST_CHECK_EQUAL(om->Buffer.BSt.Function, 6);
            BOOST_CHECK_EQUAL(om->Buffer.BSt.Length, 9);

            const auto results_begin = om->Buffer.BSt.Message;
            const auto results_end = om->Buffer.BSt.Message + om->Buffer.BSt.Length;

            std::array<unsigned char, 9> expected { 0xff, 0x01, 0x57, 0x03, 0x46, 0x50, 0x06, 0x00, 0x00 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(), expected.end(),
                results_begin, results_end);

            INMESS im;

            OutEchoToIN(*om, im);

            im.Buffer.DSt.Length = 0;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            delete_container(retList);  retList.clear();
            delete_container(vgList);   vgList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL(ClientErrors::None, mct410.ResultDecode(im, timeBegin, vgList, retList, outList));
        }

        BOOST_CHECK(vgList.empty());
        BOOST_CHECK(outList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 2);

        {
            auto retList_itr = retList.begin();
            CtiRequestMsg *req;

            {
                auto msg = *retList_itr++;

                BOOST_REQUIRE(msg);

                req = dynamic_cast<CtiRequestMsg *>(msg);

                BOOST_REQUIRE(req);

                BOOST_CHECK_EQUAL(req->DeviceId(), 123456);
                BOOST_CHECK_EQUAL(req->CommandString(), "getvalue lp peak day channel 1 04/04/2016 6");
                BOOST_CHECK_EQUAL(req->getMessageTime(), timeBegin + 9);
            }

            {
                auto msg = *retList_itr++;

                BOOST_REQUIRE(msg);

                auto ret = dynamic_cast<const CtiReturnMsg *>(msg);

                BOOST_REQUIRE(ret);

                BOOST_CHECK_EQUAL(ret->DeviceId(), 123456);
                BOOST_CHECK_EQUAL(ret->Status(), 0);
                BOOST_CHECK_EQUAL(ret->ResultString(), "Test MCT-410iL / delaying 9 seconds for device peak report processing (until 04/04/2016 17:24:45)");
                BOOST_CHECK(ret->ExpectMore());
            }

            BOOST_CHECK_EQUAL(ClientErrors::None, mct410.beginExecuteRequest(req, CtiCommandParser{ req->CommandString() }, vgList, retList, outList));
        }

        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(outList.size(), 1);
        BOOST_REQUIRE_EQUAL(retList.size(), 3);  //  first two are the messages from before

        {
            BOOST_CHECK(isSentOnRouteMsg(retList.back()));

            INMESS im;

            {
                const OUTMESS *om = outList.front();

                BOOST_REQUIRE(om);
                BOOST_CHECK_EQUAL(om->Buffer.BSt.IO, 3);
                BOOST_CHECK_EQUAL(om->Buffer.BSt.Function, 160);
                BOOST_CHECK_EQUAL(om->Buffer.BSt.Length, 13);

                OutEchoToIN(*om, im);

                std::array<unsigned char, 13> buf { 0x00, 0x02, 0xa9, 0x57, 0x01, 0xf4, 0xcf, 0x00, 0x02, 0x97, 0x00, 0x0e, 0xcb };

                std::copy(buf.begin(), buf.end(), im.Buffer.DSt.Message);

                im.Buffer.DSt.Length = buf.size();
                im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            }

            delete_container(retList);  retList.clear();
            delete_container(outList);  outList.clear();

            BOOST_CHECK_EQUAL(ClientErrors::InvalidTimestamp, mct410.ResultDecode(im, timeBegin, vgList, retList, outList));
        }
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
    /*
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor01)
    {
        CtiCommandParser parse("getvalue lp channel 1 02/02/2010 01/01/2010");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    */
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

            Cti::Test::Override_DynamicPaoInfoManager scopedOverride;

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
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_LongLoadProfileStorage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x04);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_67)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_LongLoadProfileStorage, BSt));
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
        (tuple_list_of(0,5,(int)Dpi::Key_MCT_SSpec)(1,1,(int)Dpi::Key_MCT_SSpecRevision)(2,1,(int)Dpi::Key_MCT_Options))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_SSpecRevision)(1,1,(int)Dpi::Key_MCT_Options)(2,1,(int)Dpi::Key_MCT_Configuration))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_Options)(1,1,(int)Dpi::Key_MCT_Configuration))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_Configuration))
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_EventFlagsMask1))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_EventFlagsMask1)(2,1,(int)Dpi::Key_MCT_EventFlagsMask2))
        // memory read 10
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_EventFlagsMask1)(1,1,(int)Dpi::Key_MCT_EventFlagsMask2)(2,2,(int)Dpi::Key_MCT_MeterAlarmMask))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_EventFlagsMask2)(1,2,(int)Dpi::Key_MCT_MeterAlarmMask))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_MeterAlarmMask))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_DisplayParameters))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_DisplayParameters))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DisplayParameters))
        (empty)
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_AddressBronze))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_AddressBronze)(2,2,(int)Dpi::Key_MCT_AddressLead))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_AddressBronze)(1,2,(int)Dpi::Key_MCT_AddressLead))
        // memory read 20
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_AddressLead)(2,2,(int)Dpi::Key_MCT_AddressCollection))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_AddressCollection))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_AddressCollection)(2,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(2,1,(int)Dpi::Key_MCT_TransformerRatio))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(1,1,(int)Dpi::Key_MCT_TransformerRatio)(2,1,(int)Dpi::Key_MCT_DemandInterval))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TransformerRatio)(1,1,(int)Dpi::Key_MCT_DemandInterval)(2,1,(int)Dpi::Key_MCT_LoadProfileInterval))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DemandInterval)(1,1,(int)Dpi::Key_MCT_LoadProfileInterval)(2,1,(int)Dpi::Key_MCT_VoltageDemandInterval))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileInterval)(1,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(2,1,(int)Dpi::Key_MCT_VoltageLPInterval))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(1,1,(int)Dpi::Key_MCT_VoltageLPInterval)(2,2,(int)Dpi::Key_MCT_OverVoltageThreshold))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_VoltageLPInterval)(1,2,(int)Dpi::Key_MCT_OverVoltageThreshold))
        // memory read 30
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(2,2,(int)Dpi::Key_MCT_UnderVoltageThreshold))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_UnderVoltageThreshold))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(2,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_OutageCycles))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 40
        .repeat(10, empty)
        // memory read 50
        (empty)
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_WaterMeterReadInterval))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(2,1,(int)Dpi::Key_MCT_TimeAdjustTolerance))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(1,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(2,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(1,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_DSTStartTime))
        (empty)
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_DSTEndTime))
        // memory read 60
        (empty)
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeZoneOffset))
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
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_ScheduledFreezeDay))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(2,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(1,2,(int)Dpi::Key_MCT_DayTable))
        // memory read 80
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable))
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
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday1))
        (empty)
        // memory read 210
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday2))
        (empty)
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday3))
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
        (tuple_list_of(0,5,(int)Dpi::Key_MCT_SSpec)(1,1,(int)Dpi::Key_MCT_SSpecRevision)(2,1,(int)Dpi::Key_MCT_Options)(3,1,(int)Dpi::Key_MCT_Configuration))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_SSpecRevision)(1,1,(int)Dpi::Key_MCT_Options)(2,1,(int)Dpi::Key_MCT_Configuration))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_Options)(1,1,(int)Dpi::Key_MCT_Configuration))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_Configuration)(7,1,(int)Dpi::Key_MCT_EventFlagsMask1))
        (tuple_list_of(6,1,(int)Dpi::Key_MCT_EventFlagsMask1)(7,1,(int)Dpi::Key_MCT_EventFlagsMask2))
        (tuple_list_of(5,1,(int)Dpi::Key_MCT_EventFlagsMask1)(6,1,(int)Dpi::Key_MCT_EventFlagsMask2)(7,2,(int)Dpi::Key_MCT_MeterAlarmMask))
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_EventFlagsMask1)(5,1,(int)Dpi::Key_MCT_EventFlagsMask2)(6,2,(int)Dpi::Key_MCT_MeterAlarmMask))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_EventFlagsMask1)(4,1,(int)Dpi::Key_MCT_EventFlagsMask2)(5,2,(int)Dpi::Key_MCT_MeterAlarmMask))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_EventFlagsMask1)(3,1,(int)Dpi::Key_MCT_EventFlagsMask2)(4,2,(int)Dpi::Key_MCT_MeterAlarmMask)(7,1,(int)Dpi::Key_MCT_DisplayParameters))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_EventFlagsMask1)(2,1,(int)Dpi::Key_MCT_EventFlagsMask2)(3,2,(int)Dpi::Key_MCT_MeterAlarmMask)(6,1,(int)Dpi::Key_MCT_DisplayParameters))
        // memory read
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_EventFlagsMask1)(1,1,(int)Dpi::Key_MCT_EventFlagsMask2)(2,2,(int)Dpi::Key_MCT_MeterAlarmMask)(5,1,(int)Dpi::Key_MCT_DisplayParameters))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_EventFlagsMask2)(1,2,(int)Dpi::Key_MCT_MeterAlarmMask)(4,1,(int)Dpi::Key_MCT_DisplayParameters))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_MeterAlarmMask)(3,1,(int)Dpi::Key_MCT_DisplayParameters)(7,1,(int)Dpi::Key_MCT_AddressBronze))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_DisplayParameters)(6,1,(int)Dpi::Key_MCT_AddressBronze)(7,2,(int)Dpi::Key_MCT_AddressLead))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_DisplayParameters)(5,1,(int)Dpi::Key_MCT_AddressBronze)(6,2,(int)Dpi::Key_MCT_AddressLead))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DisplayParameters)(4,1,(int)Dpi::Key_MCT_AddressBronze)(5,2,(int)Dpi::Key_MCT_AddressLead)(7,2,(int)Dpi::Key_MCT_AddressCollection))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_AddressBronze)(4,2,(int)Dpi::Key_MCT_AddressLead)(6,2,(int)Dpi::Key_MCT_AddressCollection))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_AddressBronze)(3,2,(int)Dpi::Key_MCT_AddressLead)(5,2,(int)Dpi::Key_MCT_AddressCollection)(7,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_AddressBronze)(2,2,(int)Dpi::Key_MCT_AddressLead)(4,2,(int)Dpi::Key_MCT_AddressCollection)(6,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(7,1,(int)Dpi::Key_MCT_TransformerRatio))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_AddressBronze)(1,2,(int)Dpi::Key_MCT_AddressLead)(3,2,(int)Dpi::Key_MCT_AddressCollection)(5,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(6,1,(int)Dpi::Key_MCT_TransformerRatio)(7,1,(int)Dpi::Key_MCT_DemandInterval))
        // memory read
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_AddressLead)(2,2,(int)Dpi::Key_MCT_AddressCollection)(4,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(5,1,(int)Dpi::Key_MCT_TransformerRatio)(6,1,(int)Dpi::Key_MCT_DemandInterval)(7,1,(int)Dpi::Key_MCT_LoadProfileInterval))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_AddressCollection)(3,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(4,1,(int)Dpi::Key_MCT_TransformerRatio)(5,1,(int)Dpi::Key_MCT_DemandInterval)(6,1,(int)Dpi::Key_MCT_LoadProfileInterval)(7,1,(int)Dpi::Key_MCT_VoltageDemandInterval))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_AddressCollection)(2,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(3,1,(int)Dpi::Key_MCT_TransformerRatio)(4,1,(int)Dpi::Key_MCT_DemandInterval)(5,1,(int)Dpi::Key_MCT_LoadProfileInterval)(6,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(7,1,(int)Dpi::Key_MCT_VoltageLPInterval))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(2,1,(int)Dpi::Key_MCT_TransformerRatio)(3,1,(int)Dpi::Key_MCT_DemandInterval)(4,1,(int)Dpi::Key_MCT_LoadProfileInterval)(5,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(6,1,(int)Dpi::Key_MCT_VoltageLPInterval)(7,2,(int)Dpi::Key_MCT_OverVoltageThreshold))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(1,1,(int)Dpi::Key_MCT_TransformerRatio)(2,1,(int)Dpi::Key_MCT_DemandInterval)(3,1,(int)Dpi::Key_MCT_LoadProfileInterval)(4,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(5,1,(int)Dpi::Key_MCT_VoltageLPInterval)(6,2,(int)Dpi::Key_MCT_OverVoltageThreshold))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TransformerRatio)(1,1,(int)Dpi::Key_MCT_DemandInterval)(2,1,(int)Dpi::Key_MCT_LoadProfileInterval)(3,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(4,1,(int)Dpi::Key_MCT_VoltageLPInterval)(5,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(7,2,(int)Dpi::Key_MCT_UnderVoltageThreshold))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DemandInterval)(1,1,(int)Dpi::Key_MCT_LoadProfileInterval)(2,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(3,1,(int)Dpi::Key_MCT_VoltageLPInterval)(4,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(6,2,(int)Dpi::Key_MCT_UnderVoltageThreshold))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileInterval)(1,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(2,1,(int)Dpi::Key_MCT_VoltageLPInterval)(3,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(5,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(7,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(1,1,(int)Dpi::Key_MCT_VoltageLPInterval)(2,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(4,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(6,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_VoltageLPInterval)(1,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(3,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(5,1,(int)Dpi::Key_MCT_OutageCycles))
        // memory read
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(2,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(4,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(3,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(2,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_OutageCycles))
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
        (tuple_list_of(7,1,(int)Dpi::Key_MCT_WaterMeterReadInterval))
        (tuple_list_of(6,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(7,1,(int)Dpi::Key_MCT_TimeAdjustTolerance))
        (tuple_list_of(5,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(6,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(7,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(5,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(6,4,(int)Dpi::Key_MCT_DSTStartTime))
        // memory read
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(4,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(5,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(3,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(4,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(2,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(3,4,(int)Dpi::Key_MCT_DSTStartTime)(7,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(1,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(2,4,(int)Dpi::Key_MCT_DSTStartTime)(6,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(1,4,(int)Dpi::Key_MCT_DSTStartTime)(5,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_DSTStartTime)(4,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(3,4,(int)Dpi::Key_MCT_DSTEndTime)(7,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_DSTEndTime)(6,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_DSTEndTime)(5,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_DSTEndTime)(4,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        // memory read
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read
        (empty)
        (empty)
        (tuple_list_of(7,1,(int)Dpi::Key_MCT_ScheduledFreezeDay))
        (tuple_list_of(6,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(7,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(5,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(6,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(5,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(4,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(3,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(2,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(1,2,(int)Dpi::Key_MCT_DayTable))
        // memory read
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable))
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
        (tuple_list_of(7,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(6,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(5,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(4,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(3,4,(int)Dpi::Key_MCT_Holiday1)(7,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday1)(6,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday1)(5,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday1)(4,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(3,4,(int)Dpi::Key_MCT_Holiday2)(7,4,(int)Dpi::Key_MCT_Holiday3))
        // memory read
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday2)(6,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday2)(5,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday2)(4,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(3,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday3))
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
        (tuple_list_of(0,5,(int)Dpi::Key_MCT_SSpec)(1,1,(int)Dpi::Key_MCT_SSpecRevision)(2,1,(int)Dpi::Key_MCT_Options)(3,1,(int)Dpi::Key_MCT_Configuration)(10,1,(int)Dpi::Key_MCT_EventFlagsMask1)(11,1,(int)Dpi::Key_MCT_EventFlagsMask2)(12,2,(int)Dpi::Key_MCT_MeterAlarmMask))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_SSpecRevision)(1,1,(int)Dpi::Key_MCT_Options)(2,1,(int)Dpi::Key_MCT_Configuration)(9,1,(int)Dpi::Key_MCT_EventFlagsMask1)(10,1,(int)Dpi::Key_MCT_EventFlagsMask2)(11,2,(int)Dpi::Key_MCT_MeterAlarmMask))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_Options)(1,1,(int)Dpi::Key_MCT_Configuration)(8,1,(int)Dpi::Key_MCT_EventFlagsMask1)(9,1,(int)Dpi::Key_MCT_EventFlagsMask2)(10,2,(int)Dpi::Key_MCT_MeterAlarmMask))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_Configuration)(7,1,(int)Dpi::Key_MCT_EventFlagsMask1)(8,1,(int)Dpi::Key_MCT_EventFlagsMask2)(9,2,(int)Dpi::Key_MCT_MeterAlarmMask)(12,1,(int)Dpi::Key_MCT_DisplayParameters))
        (tuple_list_of(6,1,(int)Dpi::Key_MCT_EventFlagsMask1)(7,1,(int)Dpi::Key_MCT_EventFlagsMask2)(8,2,(int)Dpi::Key_MCT_MeterAlarmMask)(11,1,(int)Dpi::Key_MCT_DisplayParameters))
        (tuple_list_of(5,1,(int)Dpi::Key_MCT_EventFlagsMask1)(6,1,(int)Dpi::Key_MCT_EventFlagsMask2)(7,2,(int)Dpi::Key_MCT_MeterAlarmMask)(10,1,(int)Dpi::Key_MCT_DisplayParameters))
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_EventFlagsMask1)(5,1,(int)Dpi::Key_MCT_EventFlagsMask2)(6,2,(int)Dpi::Key_MCT_MeterAlarmMask)(9,1,(int)Dpi::Key_MCT_DisplayParameters))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_EventFlagsMask1)(4,1,(int)Dpi::Key_MCT_EventFlagsMask2)(5,2,(int)Dpi::Key_MCT_MeterAlarmMask)(8,1,(int)Dpi::Key_MCT_DisplayParameters)(12,1,(int)Dpi::Key_MCT_AddressBronze))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_EventFlagsMask1)(3,1,(int)Dpi::Key_MCT_EventFlagsMask2)(4,2,(int)Dpi::Key_MCT_MeterAlarmMask)(7,1,(int)Dpi::Key_MCT_DisplayParameters)(11,1,(int)Dpi::Key_MCT_AddressBronze)(12,2,(int)Dpi::Key_MCT_AddressLead))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_EventFlagsMask1)(2,1,(int)Dpi::Key_MCT_EventFlagsMask2)(3,2,(int)Dpi::Key_MCT_MeterAlarmMask)(6,1,(int)Dpi::Key_MCT_DisplayParameters)(10,1,(int)Dpi::Key_MCT_AddressBronze)(11,2,(int)Dpi::Key_MCT_AddressLead))
        // memory read
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_EventFlagsMask1)(1,1,(int)Dpi::Key_MCT_EventFlagsMask2)(2,2,(int)Dpi::Key_MCT_MeterAlarmMask)(5,1,(int)Dpi::Key_MCT_DisplayParameters)(9,1,(int)Dpi::Key_MCT_AddressBronze)(10,2,(int)Dpi::Key_MCT_AddressLead)(12,2,(int)Dpi::Key_MCT_AddressCollection))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_EventFlagsMask2)(1,2,(int)Dpi::Key_MCT_MeterAlarmMask)(4,1,(int)Dpi::Key_MCT_DisplayParameters)(8,1,(int)Dpi::Key_MCT_AddressBronze)(9,2,(int)Dpi::Key_MCT_AddressLead)(11,2,(int)Dpi::Key_MCT_AddressCollection))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_MeterAlarmMask)(3,1,(int)Dpi::Key_MCT_DisplayParameters)(7,1,(int)Dpi::Key_MCT_AddressBronze)(8,2,(int)Dpi::Key_MCT_AddressLead)(10,2,(int)Dpi::Key_MCT_AddressCollection)(12,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_DisplayParameters)(6,1,(int)Dpi::Key_MCT_AddressBronze)(7,2,(int)Dpi::Key_MCT_AddressLead)(9,2,(int)Dpi::Key_MCT_AddressCollection)(11,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(12,1,(int)Dpi::Key_MCT_TransformerRatio))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_DisplayParameters)(5,1,(int)Dpi::Key_MCT_AddressBronze)(6,2,(int)Dpi::Key_MCT_AddressLead)(8,2,(int)Dpi::Key_MCT_AddressCollection)(10,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(11,1,(int)Dpi::Key_MCT_TransformerRatio)(12,1,(int)Dpi::Key_MCT_DemandInterval))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DisplayParameters)(4,1,(int)Dpi::Key_MCT_AddressBronze)(5,2,(int)Dpi::Key_MCT_AddressLead)(7,2,(int)Dpi::Key_MCT_AddressCollection)(9,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(10,1,(int)Dpi::Key_MCT_TransformerRatio)(11,1,(int)Dpi::Key_MCT_DemandInterval)(12,1,(int)Dpi::Key_MCT_LoadProfileInterval))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_AddressBronze)(4,2,(int)Dpi::Key_MCT_AddressLead)(6,2,(int)Dpi::Key_MCT_AddressCollection)(8,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(9,1,(int)Dpi::Key_MCT_TransformerRatio)(10,1,(int)Dpi::Key_MCT_DemandInterval)(11,1,(int)Dpi::Key_MCT_LoadProfileInterval)(12,1,(int)Dpi::Key_MCT_VoltageDemandInterval))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_AddressBronze)(3,2,(int)Dpi::Key_MCT_AddressLead)(5,2,(int)Dpi::Key_MCT_AddressCollection)(7,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(8,1,(int)Dpi::Key_MCT_TransformerRatio)(9,1,(int)Dpi::Key_MCT_DemandInterval)(10,1,(int)Dpi::Key_MCT_LoadProfileInterval)(11,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(12,1,(int)Dpi::Key_MCT_VoltageLPInterval))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_AddressBronze)(2,2,(int)Dpi::Key_MCT_AddressLead)(4,2,(int)Dpi::Key_MCT_AddressCollection)(6,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(7,1,(int)Dpi::Key_MCT_TransformerRatio)(8,1,(int)Dpi::Key_MCT_DemandInterval)(9,1,(int)Dpi::Key_MCT_LoadProfileInterval)(10,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(11,1,(int)Dpi::Key_MCT_VoltageLPInterval)(12,2,(int)Dpi::Key_MCT_OverVoltageThreshold))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_AddressBronze)(1,2,(int)Dpi::Key_MCT_AddressLead)(3,2,(int)Dpi::Key_MCT_AddressCollection)(5,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(6,1,(int)Dpi::Key_MCT_TransformerRatio)(7,1,(int)Dpi::Key_MCT_DemandInterval)(8,1,(int)Dpi::Key_MCT_LoadProfileInterval)(9,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(10,1,(int)Dpi::Key_MCT_VoltageLPInterval)(11,2,(int)Dpi::Key_MCT_OverVoltageThreshold))
        // memory read
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_AddressLead)(2,2,(int)Dpi::Key_MCT_AddressCollection)(4,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(5,1,(int)Dpi::Key_MCT_TransformerRatio)(6,1,(int)Dpi::Key_MCT_DemandInterval)(7,1,(int)Dpi::Key_MCT_LoadProfileInterval)(8,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(9,1,(int)Dpi::Key_MCT_VoltageLPInterval)(10,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(12,2,(int)Dpi::Key_MCT_UnderVoltageThreshold))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_AddressCollection)(3,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(4,1,(int)Dpi::Key_MCT_TransformerRatio)(5,1,(int)Dpi::Key_MCT_DemandInterval)(6,1,(int)Dpi::Key_MCT_LoadProfileInterval)(7,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(8,1,(int)Dpi::Key_MCT_VoltageLPInterval)(9,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(11,2,(int)Dpi::Key_MCT_UnderVoltageThreshold))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_AddressCollection)(2,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(3,1,(int)Dpi::Key_MCT_TransformerRatio)(4,1,(int)Dpi::Key_MCT_DemandInterval)(5,1,(int)Dpi::Key_MCT_LoadProfileInterval)(6,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(7,1,(int)Dpi::Key_MCT_VoltageLPInterval)(8,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(10,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(12,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(2,1,(int)Dpi::Key_MCT_TransformerRatio)(3,1,(int)Dpi::Key_MCT_DemandInterval)(4,1,(int)Dpi::Key_MCT_LoadProfileInterval)(5,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(6,1,(int)Dpi::Key_MCT_VoltageLPInterval)(7,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(9,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(11,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_AddressServiceProviderID)(1,1,(int)Dpi::Key_MCT_TransformerRatio)(2,1,(int)Dpi::Key_MCT_DemandInterval)(3,1,(int)Dpi::Key_MCT_LoadProfileInterval)(4,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(5,1,(int)Dpi::Key_MCT_VoltageLPInterval)(6,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(8,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(10,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TransformerRatio)(1,1,(int)Dpi::Key_MCT_DemandInterval)(2,1,(int)Dpi::Key_MCT_LoadProfileInterval)(3,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(4,1,(int)Dpi::Key_MCT_VoltageLPInterval)(5,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(7,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(9,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DemandInterval)(1,1,(int)Dpi::Key_MCT_LoadProfileInterval)(2,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(3,1,(int)Dpi::Key_MCT_VoltageLPInterval)(4,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(6,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(8,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileInterval)(1,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(2,1,(int)Dpi::Key_MCT_VoltageLPInterval)(3,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(5,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(7,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(1,1,(int)Dpi::Key_MCT_VoltageLPInterval)(2,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(4,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(6,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_VoltageLPInterval)(1,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(3,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(5,1,(int)Dpi::Key_MCT_OutageCycles))
        // memory read
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(2,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(4,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(3,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(2,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_OutageCycles))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_OutageCycles))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read
        (empty)
        (tuple_list_of(12,1,(int)Dpi::Key_MCT_WaterMeterReadInterval))
        (tuple_list_of(11,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(12,1,(int)Dpi::Key_MCT_TimeAdjustTolerance))
        (tuple_list_of(10,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(11,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(12,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(9,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(10,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(11,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(8,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(9,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(10,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(7,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(8,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(9,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(6,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(7,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(8,4,(int)Dpi::Key_MCT_DSTStartTime)(12,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(5,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(6,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(7,4,(int)Dpi::Key_MCT_DSTStartTime)(11,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(5,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(6,4,(int)Dpi::Key_MCT_DSTStartTime)(10,4,(int)Dpi::Key_MCT_DSTEndTime))
        // memory read
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(4,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(5,4,(int)Dpi::Key_MCT_DSTStartTime)(9,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(3,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(4,4,(int)Dpi::Key_MCT_DSTStartTime)(8,4,(int)Dpi::Key_MCT_DSTEndTime)(12,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(2,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(3,4,(int)Dpi::Key_MCT_DSTStartTime)(7,4,(int)Dpi::Key_MCT_DSTEndTime)(11,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_WaterMeterReadInterval)(1,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(2,4,(int)Dpi::Key_MCT_DSTStartTime)(6,4,(int)Dpi::Key_MCT_DSTEndTime)(10,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(1,4,(int)Dpi::Key_MCT_DSTStartTime)(5,4,(int)Dpi::Key_MCT_DSTEndTime)(9,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_DSTStartTime)(4,4,(int)Dpi::Key_MCT_DSTEndTime)(8,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(3,4,(int)Dpi::Key_MCT_DSTEndTime)(7,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_DSTEndTime)(6,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_DSTEndTime)(5,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_DSTEndTime)(4,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        // memory read
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(12,1,(int)Dpi::Key_MCT_ScheduledFreezeDay))
        (tuple_list_of(11,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(12,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(10,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(11,2,(int)Dpi::Key_MCT_DayTable))
        // memory read
        (tuple_list_of(9,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(10,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(8,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(9,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(7,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(8,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(6,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(7,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(5,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(6,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(5,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(4,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(3,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(2,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_ScheduledFreezeDay)(1,2,(int)Dpi::Key_MCT_DayTable))
        // memory read
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable))
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
        (tuple_list_of(12,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(11,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(10,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(9,4,(int)Dpi::Key_MCT_Holiday1))
        // memory read
        (tuple_list_of(8,4,(int)Dpi::Key_MCT_Holiday1)(12,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(7,4,(int)Dpi::Key_MCT_Holiday1)(11,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(6,4,(int)Dpi::Key_MCT_Holiday1)(10,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(5,4,(int)Dpi::Key_MCT_Holiday1)(9,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(4,4,(int)Dpi::Key_MCT_Holiday1)(8,4,(int)Dpi::Key_MCT_Holiday2)(12,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(3,4,(int)Dpi::Key_MCT_Holiday1)(7,4,(int)Dpi::Key_MCT_Holiday2)(11,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday1)(6,4,(int)Dpi::Key_MCT_Holiday2)(10,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday1)(5,4,(int)Dpi::Key_MCT_Holiday2)(9,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday1)(4,4,(int)Dpi::Key_MCT_Holiday2)(8,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(3,4,(int)Dpi::Key_MCT_Holiday2)(7,4,(int)Dpi::Key_MCT_Holiday3))
        // memory read
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday2)(6,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday2)(5,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday2)(4,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(3,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday3))
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
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable)(2,1,(int)Dpi::Key_MCT_DefaultTOURate))
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
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_LLPChannel1Len)(5,1,(int)Dpi::Key_MCT_LLPChannel2Len)(6,1,(int)Dpi::Key_MCT_LLPChannel3Len)(7,1,(int)Dpi::Key_MCT_LLPChannel4Len))
        (empty)
        (empty)
        //  function read 160
        .repeat(10, empty)
        //  function read 170
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable)(2,1,(int)Dpi::Key_MCT_DefaultTOURate))
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
        (tuple_list_of(7,1,(int)Dpi::Key_MCT_ConnectDelay))
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
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_LLPChannel1Len)(5,1,(int)Dpi::Key_MCT_LLPChannel2Len)(6,1,(int)Dpi::Key_MCT_LLPChannel3Len)(7,1,(int)Dpi::Key_MCT_LLPChannel4Len))
        (empty)
        (empty)
        //  function read 160
        .repeat(10, empty)
        //  function read 170
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable)(2,1,(int)Dpi::Key_MCT_DefaultTOURate)(10,1,(int)Dpi::Key_MCT_TimeZoneOffset))
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
        (tuple_list_of(7,1,(int)Dpi::Key_MCT_ConnectDelay)(9,1,(int)Dpi::Key_MCT_DisconnectMinutes)(10,1,(int)Dpi::Key_MCT_ConnectMinutes)(11,1,(int)Dpi::Key_MCT_Configuration))
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

//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


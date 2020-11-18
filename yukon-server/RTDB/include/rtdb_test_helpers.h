#pragma once

#include "mgr_device.h"
#include "mgr_route.h"
#include "mgr_point.h"
#include "mgr_config.h"
#include "mgr_dyn_paoinfo.h"
//  for test_DevicePointHelper
#include "desolvers.h"
#include "pt_analog.h"
#include "pt_accum.h"
#include "pt_status.h"
#include "dev_single.h"
#include "dev_ccu.h"
#include "dev_mct410.h"
#include "dev_mct420.h"
#include "dev_rfnMeter.h"
#include "dev_rfnCommercial.h"
#include "dev_rfn_LgyrFocus_al.h"
#include "dev_rf_BatteryNode.h"
#include "rte_ccu.h"

#include "test_reader.h"
#include "std_helper.h"

#include "boost_test_helpers.h"
#include <boost/range/algorithm/count.hpp>
#include <boost/test/unit_test.hpp>
#include <boost/variant.hpp>

#include <numeric>

namespace Cti::Test {
namespace {

using std::to_string;
    
struct test_DeviceConfig : public Config::DeviceConfig
{
    test_DeviceConfig() : DeviceConfig( 271828 ) {}

    using DeviceConfig::insertValue;
    using DeviceConfig::findValue;
    using DeviceConfig::addCategory;
};

struct test_ConfigManager : ConfigManager
{
    const Config::DeviceConfigSPtr config;

    test_ConfigManager( Config::DeviceConfigSPtr config_ )
        : config( config_ )
    {
    }

    virtual Config::DeviceConfigSPtr fetchConfig( const long deviceID, const DeviceTypes deviceType )
    {
        return config;
    }
};

class Override_ConfigManager
{
    std::unique_ptr<ConfigManager> _oldConfigManager;

public:

    Override_ConfigManager(Config::DeviceConfigSPtr config)
    {
        _oldConfigManager = std::move(gConfigManager);

        gConfigManager = std::move(std::make_unique<test_ConfigManager>(config));
    }

    ~Override_ConfigManager()
    {
        gConfigManager = std::move(_oldConfigManager);
    }
};

struct test_DynamicPaoInfoManager : DynamicPaoInfoManager
{
    void loadInfo(const long id) override
    {
        loadedPaos.insert(id);
    }

    std::map<long, std::set<PaoInfoKeys>> dirtyEntries;

    void setDirty(const DynInfoSPtr &dirty) override
    {
        dirtyEntries[dirty->getPaoID()].insert(dirty->getKey());
    }
};

class Override_DynamicPaoInfoManager
{
    std::unique_ptr<DynamicPaoInfoManager> _oldDynamicPaoInfoManager;

public:

    test_DynamicPaoInfoManager *dpi;

    Override_DynamicPaoInfoManager()
    {
        dpi = new test_DynamicPaoInfoManager;

        _oldDynamicPaoInfoManager.reset(dpi);
        _oldDynamicPaoInfoManager.swap(gDynamicPaoInfoManager);
    }

    ~Override_DynamicPaoInfoManager()
    {
        _oldDynamicPaoInfoManager.swap(gDynamicPaoInfoManager);
    }
};

struct Test_CtiPointAccumulator : public CtiPointAccumulator
{
    double computeValueForUOM( double value ) const  {  return value;  }
};

struct Test_CtiPointAnalog : public CtiPointAnalog
{
    double multiplier = 1.0, offset = 0.0;

    double computeValueForUOM( double value ) const  {  return value * multiplier + offset;  }
};

Test_CtiPointAccumulator *makePulseAccumulatorPoint(long deviceid, long pointid, int offset)
{
    typedef Cti::Test::StringRow<20> AccumRow;
    typedef Cti::Test::TestReader<AccumRow> AccumReader;

    //  Sample rows
    //"107462", "kWh", "PulseAccumulator", "13555", "0", "1", "N", "N", "R", "None", "0", "1", "1", "0", "0", "0.1", "0"
    //"107480", "kW", "DemandAccumulator", "13556", "0", "1", "N", "N", "R", "None", "0", "0", "3", "0", "0", "0.1", "0"

    AccumRow keys = {
        "pointid",      "pointname",    "pointtype",    "paobjectid",       "stategroupid", "pointoffset",  "serviceflag",
        "alarminhibit", "pseudoflag",   "archivetype",  "archiveinterval",  "uomid",        "decimalplaces", "decimaldigits",
        "calctype",     "multiplier",   "dataoffset"};
    AccumRow values = {
        to_string(pointid), desolvePointType(PulseAccumulatorPointType) + to_string(offset), desolvePointType(PulseAccumulatorPointType), to_string(deviceid), "0", to_string(offset), "N",
        "N", "N", "R", "None", "0", "3", "0",
        "0", "0", "0.1", "0"};

    std::vector<AccumRow> rows;
    rows.push_back( values );

    AccumReader reader(keys, rows);

    Test_CtiPointAccumulator *accum = new Test_CtiPointAccumulator;

    if( reader() )
    {
        accum->DecodeDatabaseReader(reader);
    }

    return accum;
}

Test_CtiPointAccumulator *makeDemandAccumulatorPoint(long deviceid, long pointid, int offset)
{
    typedef Cti::Test::StringRow<20> AccumRow;
    typedef Cti::Test::TestReader<AccumRow> AccumReader;

    //  Sample rows
    //"107462", "kWh", "PulseAccumulator", "13555", "0", "1", "N", "N", "R", "None", "0", "1", "1", "0", "0", "0.1", "0"
    //"107480", "kW", "DemandAccumulator", "13556", "0", "1", "N", "N", "R", "None", "0", "0", "3", "0", "0", "0.1", "0"

    AccumRow keys = {
        "pointid",      "pointname",    "pointtype",    "paobjectid",       "stategroupid", "pointoffset",  "serviceflag",
        "alarminhibit", "pseudoflag",   "archivetype",  "archiveinterval",  "uomid",        "decimalplaces", "decimaldigits",
        "calctype",     "multiplier",   "dataoffset"};
    AccumRow values = {
        to_string(pointid), desolvePointType(DemandAccumulatorPointType) + to_string(offset), desolvePointType(DemandAccumulatorPointType), to_string(deviceid), "0", to_string(offset), "N",
        "N", "N", "R", "None", "0", "3", "0",
        "0", "0", "0.1", "0"};

    std::vector<AccumRow> rows;
    rows.push_back( values );

    AccumReader reader(keys, rows);

    Test_CtiPointAccumulator *accum = new Test_CtiPointAccumulator;

    if( reader() )
    {
        accum->DecodeDatabaseReader(reader);
    }

    return accum;
}

Test_CtiPointAnalog *makeAnalogPoint(long deviceid, long pointid, int offset)
{
    typedef Cti::Test::StringRow<20> AnalogRow;
    typedef Cti::Test::TestReader<AnalogRow> AnalogReader;

    //  Sample rows
    //"6106", "CONTROL COUNTDOWN",                "Analog", "5718", "-1", "2505", "N", "N", "R", "None",   "0", "9", "3", "0", "0", "1", "0", "-1", "NULL", "NULL"
    //"7995", "ARL Alt Config Timer (Heartbeat)", "Analog", "6469", "-1", "0",    "N", "N", "P", "None", "300", "0", "0", "0", "0", "1", "0", "-1", "1776", "N"

    AnalogRow keys = {
        "pointid",      "pointname",    "pointtype",    "paobjectid",       "stategroupid", "pointoffset",  "serviceflag",
        "alarminhibit", "pseudoflag",   "archivetype",  "archiveinterval",  "uomid",        "decimalplaces","decimaldigits",
        "calctype",     "multiplier",   "dataoffset",   "deadband",         "controloffset", "controlinhibit"};
    AnalogRow values = {
        to_string(pointid), desolvePointType(AnalogPointType) + to_string(offset), desolvePointType(AnalogPointType), to_string(deviceid), "-1", to_string(offset), "N",
        "N", "R", "None", "300", "0", "0", "0",
        "0", "1", "0", "-1", "aR4nd0MNullStR1ng", "aR4nd0MNullStR1ng"};

    std::vector<AnalogRow> rows;
    rows.push_back( values );

    AnalogReader reader(keys, rows);

    Test_CtiPointAnalog *analog = new Test_CtiPointAnalog;

    if( reader() )
    {
        analog->DecodeDatabaseReader(reader);
    }

    return analog;
}

Test_CtiPointAnalog *makeAnalogOutputPoint(long deviceid, long pointid, int offset, int controlOffset, bool controlInhibited)
{
    typedef Cti::Test::StringRow<20> AnalogRow;
    typedef Cti::Test::TestReader<AnalogRow> AnalogReader;

    //  Sample rows
    //"6106", "CONTROL COUNTDOWN",                "Analog", "5718", "-1", "2505", "N", "N", "R", "None",   "0", "9", "3", "0", "0", "1", "0", "-1", "NULL", "NULL"
    //"7995", "ARL Alt Config Timer (Heartbeat)", "Analog", "6469", "-1", "0",    "N", "N", "P", "None", "300", "0", "0", "0", "0", "1", "0", "-1", "1776", "N"

    AnalogRow keys = {
        "pointid",      "pointname",    "pointtype",    "paobjectid",       "stategroupid", "pointoffset",  "serviceflag",
        "alarminhibit", "pseudoflag",   "archivetype",  "archiveinterval",  "uomid",        "decimalplaces","decimaldigits",
        "calctype",     "multiplier",   "dataoffset",   "deadband",         "controloffset", "controlinhibit" };
    AnalogRow values = {
        to_string(pointid), desolvePointType(AnalogPointType) + to_string(offset), desolvePointType(AnalogPointType), to_string(deviceid), "-1", to_string(offset), "N",
        "N", "R", "None", "300", "0", "0", "0",
        "0", "1", "0", "-1", to_string(controlOffset), controlInhibited ? "Y" : "N" };

    std::vector<AnalogRow> rows;
    rows.push_back(values);

    AnalogReader reader(keys, rows);

    Test_CtiPointAnalog *analog = new Test_CtiPointAnalog;

    if( reader() )
    {
        analog->DecodeDatabaseReader(reader);
    }

    return analog;
}

CtiPointStatus *makeStatusPoint(long deviceid, long pointid, int offset)
{
    typedef Cti::Test::StringRow<20> StatusRow;
    typedef Cti::Test::TestReader<StatusRow> StatusReader;

    //  Sample row
    //"11656", "Bad Active Trip Relay", "Status", "7621", "4", "71", "N", "N", "R", "None", "0", "0", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"

    StatusRow keys = {
        "pointid",      "pointname",        "pointtype",        "paobjectid",       "stategroupid",     "pointoffset", "serviceflag",
        "alarminhibit", "pseudoflag",       "archivetype",      "archiveinterval",  "initialstate",     "controltype", "closetime1",
        "closetime2",   "statezerocontrol", "stateonecontrol",  "commandtimeout",   "controlinhibit",   "controloffset"};
    StatusRow values = {
        to_string(pointid), desolvePointType(StatusPointType) + to_string(offset), desolvePointType(StatusPointType), to_string(deviceid), "4", to_string(offset), "N",
        "N", "R", "None", "0", "0", "aR4nd0MNullStR1ng", "aR4nd0MNullStR1ng",
        "aR4nd0MNullStR1ng", "aR4nd0MNullStR1ng", "aR4nd0MNullStR1ng", "aR4nd0MNullStR1ng", "aR4nd0MNullStR1ng", "aR4nd0MNullStR1ng"};

    std::vector<StatusRow> rows;
    rows.push_back( values );

    StatusReader reader(keys, rows);

    CtiPointStatus *status = new CtiPointStatus;

    if( reader() )
    {
        status->DecodeDatabaseReader(reader);
    }

    return status;
}

CtiPointStatus *makeControlPoint(long deviceid, long pointid, int offset, int controlOffset, CtiControlType_t controlType)
{
    typedef Cti::Test::StringRow<20> StatusRow;
    typedef Cti::Test::TestReader<StatusRow> StatusReader;

    //  Sample row
    //"11635", "CVR Mode", "Status", "7619", "4", "4", "N", "N", "R", "None", "0", "0", "Latch", "0", "0", "control open", "control close", "0", "N", "10"

    StatusRow keys = {
        "pointid",      "pointname",        "pointtype",        "paobjectid",       "stategroupid",     "pointoffset", "serviceflag",
        "alarminhibit", "pseudoflag",       "archivetype",      "archiveinterval",  "initialstate",     "controltype", "closetime1",
        "closetime2",   "statezerocontrol", "stateonecontrol",  "commandtimeout",   "controlinhibit",   "controloffset"};
    StatusRow values = {
        to_string(pointid), desolvePointType(StatusPointType) + to_string(offset), desolvePointType(StatusPointType), to_string(deviceid), "4", to_string(offset), "N",
        "N", "R", "None", "0", "0", desolveControlType(controlType), "123",
        "456", "control open", "control close", "0", "N", to_string(controlOffset) };

    std::vector<StatusRow> rows;
    rows.push_back( values );

    StatusReader reader(keys, rows);

    CtiPointStatus *status = new CtiPointStatus;

    if( reader() )
    {
        status->DecodeDatabaseReader(reader);
    }

    return status;
}


struct DevicePointHelper
{
    typedef std::map<int, CtiPointSPtr>              PointOffsetMap;
    typedef std::map<CtiPointType_t, PointOffsetMap> PointTypeOffsetMap;

    PointTypeOffsetMap points;

    CtiPointSPtr getCachedStatusPointByControlOffset(const int controlOffset)
    {
        return getCachedStatusPointByControlOffset(controlOffset, ControlType_Normal);
    }

    CtiPointSPtr getCachedAnalogOutputPointByOffset(const int analogOutputOffset)
    {
        const CtiPointType_t type = AnalogPointType;
        int offset = analogOutputOffset + 5000;

        CtiPointSPtr &point = points[type][offset];

        if( point )
        {
            return point;
        }

        unsigned pointId = 0;

        for( const auto &p : points )
        {
            pointId += p.second.size();
        }

        const unsigned long deviceId = reinterpret_cast<unsigned long>(&points);

        point.reset(makeAnalogOutputPoint(deviceId, pointId, offset, analogOutputOffset, false));

        return point;
    }

    CtiPointSPtr getCachedStatusPointByControlOffset(const int controlOffset, const CtiControlType_t controlType)
    {
        const CtiPointType_t type = StatusPointType;
        int offset = controlOffset + 5000;

        CtiPointSPtr &point = points[type][offset];

        if( point )
        {
            return point;
        }

        unsigned pointId = 0;

        for( const auto &p : points )
        {
            pointId += p.second.size();
        }

        const unsigned long deviceId = reinterpret_cast<unsigned long>(&points);

        point.reset(makeControlPoint(deviceId, pointId, offset,  controlOffset, controlType));

        return point;
    }

    CtiPointSPtr getCachedPoint(const int offset, const CtiPointType_t type)
    {
        CtiPointSPtr &point = points[type][offset];

        if( point )
        {
            return point;
        }

        unsigned pointId = 0;

        for( const auto &p : points )
        {
            pointId += p.second.size();
        }

        const long deviceId = reinterpret_cast<unsigned long>( (&points) )/4;

        switch( type )
        {
            case AnalogPointType:
            {
                point.reset(makeAnalogPoint(deviceId, pointId, offset));
            }
            break;

            case PulseAccumulatorPointType:
            {
                point.reset(makePulseAccumulatorPoint(deviceId, pointId, offset));
            }
            break;

            case DemandAccumulatorPointType:
            {
                point.reset(makeDemandAccumulatorPoint(deviceId, pointId, offset));
            }
            break;

            case StatusPointType:
            {
                point.reset(makeStatusPoint(deviceId, pointId, offset));
            }
            break;
        }

        return point;
    }
};

struct test_CtiDeviceCCU : CtiDeviceCCU
{
    test_CtiDeviceCCU()
    {
        _paObjectID = 12345;
    }

    void setInhibited()
    {
        _disableFlag = true;
    }
};

struct test_CtiRouteCCU : CtiRouteCCU
{
    boost::shared_ptr<test_CtiDeviceCCU> ccu;

    test_CtiRouteCCU() : ccu(boost::make_shared<test_CtiDeviceCCU>())
    {
        _tblPAO.setID(1234, test_tag);
        setDevicePointer(ccu);
    }
};

struct test_Mct410flDevice : Cti::Devices::Mct410Device
{
    test_Mct410flDevice(std::string& name)
    {
        _name = name;
        setDeviceType(TYPEMCT410FL);
    }

    CtiRouteSPtr getRoute(long id) const override
    {
        return boost::make_shared<test_CtiRouteCCU>();
    }
};

struct test_Mct420flDevice : Cti::Devices::Mct420Device
{
    test_Mct420flDevice(std::string& name)
    {
        _name = name;
        setDeviceType(TYPEMCT420FL);
    }

    CtiRouteSPtr getRoute(long id) const override
    {
        return boost::make_shared<test_CtiRouteCCU>();
    }
};

struct test_Rfn410flDevice : Cti::Devices::Rfn410flDevice
{
    test_Rfn410flDevice(std::string& name)
    {
        _name = name;
        setDeviceType(TYPE_RFN410FL);
    }
};

struct test_Rfn430sl1Device : Cti::Devices::Rfn430sl1Device
{
    test_Rfn430sl1Device(std::string& name)
    {
        _name = name;
        setDeviceType(TYPE_RFN430SL1);
    }
};

struct test_Rfn510flDevice : Cti::Devices::Rfn510flDevice
{
    test_Rfn510flDevice(std::string& name)
    {
        _name = name;
        setDeviceType(TYPE_RFN510FL);
    }
};

struct test_RfBatteryNodeDevice : Cti::Devices::RfBatteryNodeDevice
{
    test_RfBatteryNodeDevice(std::string name)
    {
        _name = name;
        setDeviceType(TYPE_RFG301);
    }

    boost::optional<Messaging::Rfn::RfnGetChannelConfigReplyMessage> channelConfigReplyMsg;
    YukonError_t channelConfigResultCode;

    boost::optional<Messaging::Rfn::RfnGetChannelConfigReplyMessage> readConfigurationFromNM(const RfnIdentifier& rfnId) const override
    {
        return channelConfigReplyMsg;
    }

    YukonError_t sendConfigurationToNM(const Messaging::Rfn::RfnSetChannelConfigRequestMessage request) const override
    {
        return channelConfigResultCode;
    }
};

using namespace std::literals::string_literals;

struct test_DeviceManager : CtiDeviceManager
{
    ptr_type devSingle { boost::make_shared<CtiDeviceSingle>() };

    test_DeviceManager()
    {
        //  set the IDs for all the devices
        for( auto& device : rfnDevices )
        {
            device.second->setID(device.first, test_tag);
        }
        for( auto& device : otherDevices )
        {
            device.second->setID(device.first, test_tag);
        }
    }

    ptr_type getDeviceByID(long id) override
    {
        if( auto device = mapFind(otherDevices, id) )
        {
            return *device;
        }

        return mapFindOrDefault(rfnDevices, id, Cti::Devices::RfnDeviceSPtr());
    }

    ptr_type RemoteGetEqualbyName(const std::string &RemoteName) override
    {
        if( RemoteName == "beetlebrox" )
        {
            return devSingle;
        }
        //  TODO - return other devices by name, rather than hardcoding?
        return ptr_type();
    }

    std::map<int, Cti::Devices::RfnDeviceSPtr> rfnDevices {
        { 123, boost::make_shared<test_Rfn410flDevice>("JIMMY JOHNS GARGANTUAN (123)"s) },
        {  49, boost::make_shared<test_Rfn410flDevice>("JIMMY JOHNS VITO (49)"s) },
        { 499, boost::make_shared<test_Rfn430sl1Device>("JIMMY JOHNS TURKEY TOM (499)"s) },
        { 500, boost::make_shared<test_Rfn510flDevice>("JIMMY JOHNS ITALIAN NIGHT CLUB (500)"s) },
        { 501, boost::make_shared<test_RfBatteryNodeDevice>("JIMMY JOHNS ULTIMATE PORKER (501)"s) },
    };

    std::map<int, ptr_type> otherDevices {
        {  42, devSingle },
        { 502, boost::make_shared<test_Mct410flDevice>("MCT-410fL (502)"s) },
        { 503, boost::make_shared<test_Mct420flDevice>("MCT-420fL (503)"s) },
    };

    Cti::Devices::RfnDeviceSPtr getDeviceByRfnIdentifier(const Cti::RfnIdentifier& rfnId) override
    {
        //  TODO - lookup map instead of hardcoding?
        if( rfnId == Cti::RfnIdentifier{ "JIMMY", "JOHNS", "GARGANTUAN" } )
        {
            return rfnDevices[123];
        }
        if( rfnId == Cti::RfnIdentifier{ "JIMMY", "JOHNS", "VITO" } )
        {
            return rfnDevices[49];
        }
        if( rfnId == Cti::RfnIdentifier{ "JIMMY", "JOHNS", "TURKEY TOM" } )
        {
            return rfnDevices[499];
        }
        if( rfnId == Cti::RfnIdentifier{ "JIMMY", "JOHNS", "ITALIAN NIGHT CLUB" } )
        {
            return rfnDevices[500];
        }
        if( rfnId == Cti::RfnIdentifier{ "JIMMY", "JOHNS", "ULTIMATE PORKER" } )
        {
            return rfnDevices[501];
        }

        return nullptr;
    }
};

struct test_RouteManager : CtiRouteManager
{
    CtiRouteSPtr rte;

    struct test_route : CtiRouteBase
    {
        test_route() {
            _tblPAO.setID(84, test_tag);
        }
        YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList) override { return ClientErrors::None; }
    };

    test_RouteManager() :
        rte(new test_route())
    {
    }

    ptr_type getRouteByName(std::string RouteName) override
    {
        return (RouteName == "sixty-six")
            ? rte
            : nullptr;
    }
};

struct test_PointManager : CtiPointManager
{
    ptr_type getOffsetTypeEqual(long pao, int offset, CtiPointType_t type) override
    {
        //  We only expect analog points to come out of RFN Data Streaming
        BOOST_REQUIRE_EQUAL(type, AnalogPointType);

        if( pao > 100 )
        {
            auto pt = Cti::Test::makeAnalogPoint(pao, pao * 1000 + offset, offset);

            //  Pao 499 gets non-default multipliers
            if( pao == 499 )
            {
                pt->multiplier = 3;
                pt->offset = 100;
            }

            return ptr_type{ pt };
        }

        return nullptr;
    }
};


/**
Runs through a list of return messages, comparing them to the oracle expected messages, as well 
as verifying the return status.
*/

void msgsEqual( 
    const CtiDeviceSingle::ReturnMsgList &returnMsgs, 
    int status, 
    std::vector<std::string> oracleMsgs )
{
    for ( const auto & returnMsg : returnMsgs )
    {
        BOOST_TEST_MESSAGE(returnMsg->ResultString());
    }

    auto resultStrings = returnMsgs | boost::adaptors::transformed( [](const std::unique_ptr<CtiReturnMsg> &msg){ return msg->ResultString(); } );
    auto resultStatuses = returnMsgs | boost::adaptors::transformed( []( const std::unique_ptr<CtiReturnMsg> &msg ){ return msg->Status(); } );

    const std::vector<int> oracleStatuses( oracleMsgs.size(), status );

    BOOST_CHECK_EQUAL_RANGES( resultStrings, oracleMsgs );
    BOOST_CHECK_EQUAL_RANGES( resultStatuses, oracleStatuses );
};

auto extractExpectMore(const CtiDeviceSingle::ReturnMsgList & returnMsgs) 
{
    return boost::copy_range<std::vector<bool>>(returnMsgs | boost::adaptors::transformed([](const std::unique_ptr<CtiReturnMsg> &msg) { return msg->ExpectMore(); }));
}

bool isSentOnRouteMsg(const CtiMessage* msg)
{
    if( auto ret = dynamic_cast<const CtiReturnMsg*>(msg) )
    {
        return ret->ResultString() == "Emetcon DLC command sent on route ";
    }

    return false;
}

struct PaoInfoValidator
{
    CtiTableDynamicPaoInfo::PaoInfoKeys key;
    std::variant<int, std::string, double> value;

    template<typename T>
    bool check(CtiDeviceBase & dut, CtiTableDynamicPaoInfo::PaoInfoKeys key, T value)
    {
        T paoInfo;
        const auto hadPaoInfo = dut.getDynamicInfo( key, paoInfo );
        BOOST_CHECK( hadPaoInfo );
        BOOST_CHECK_EQUAL( paoInfo, value ); 
        return hadPaoInfo && paoInfo == value;
    }

    bool validate(CtiDeviceBase &dut)
    {
        auto visitor = Cti::lambda_overloads(
            [&](const int value)         { return check<long>       (dut, key, value); },
            [&](const std::string value) { return check<std::string>(dut, key, value); },
            [&](const double value)      { return check<double>     (dut, key, value); });

        return std::visit(visitor, value);
    }
};

}
}
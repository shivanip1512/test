#pragma once

#include "mgr_config.h"
#include "mgr_dyn_paoinfo.h"
//  for test_DevicePointHelper
#include "desolvers.h"
#include "pt_analog.h"
#include "pt_accum.h"
#include "pt_status.h"

#include "test_reader.h"

#include <numeric>

namespace Cti {
namespace Test {
namespace {

struct test_DeviceConfig : public Config::DeviceConfig
{
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
    std::auto_ptr<ConfigManager> _oldConfigManager;

public:

    Override_ConfigManager(Config::DeviceConfigSPtr config)
    {
        _oldConfigManager = gConfigManager;

        gConfigManager.reset(new test_ConfigManager(config));
    }

    ~Override_ConfigManager()
    {
        gConfigManager = _oldConfigManager;
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
    double computeValueForUOM( double value ) const  {  return value;  }
};

CtiPointAccumulator *makeAccumulatorPoint(long deviceid, long pointid, int offset)
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
        CtiNumStr(pointid), desolvePointType(PulseAccumulatorPointType) + CtiNumStr(offset), desolvePointType(PulseAccumulatorPointType), CtiNumStr(deviceid), "0", CtiNumStr(offset), "N",
        "N", "N", "R", "None", "0", "0", "3",
        "0", "0", "0.1", "0"};

    std::vector<AccumRow> rows;
    rows.push_back( values );

    AccumReader reader(keys, rows);

    CtiPointAccumulator *accum = new Test_CtiPointAccumulator;

    if( reader() )
    {
        accum->DecodeDatabaseReader(reader);
    }

    return accum;
}

CtiPointAnalog *makeAnalogPoint(long deviceid, long pointid, int offset)
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
        CtiNumStr(pointid), desolvePointType(AnalogPointType) + CtiNumStr(offset), desolvePointType(AnalogPointType), CtiNumStr(deviceid), "-1", CtiNumStr(offset), "N",
        "N", "R", "None", "300", "0", "0", "0",
        "0", "1", "0", "-1", "aR4nd0MNullStR1ng", "aR4nd0MNullStR1ng"};

    std::vector<AnalogRow> rows;
    rows.push_back( values );

    AnalogReader reader(keys, rows);

    CtiPointAnalog *analog = new Test_CtiPointAnalog;

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
        CtiNumStr(pointid), desolvePointType(StatusPointType) + CtiNumStr(offset), desolvePointType(StatusPointType), CtiNumStr(deviceid), "4", CtiNumStr(offset), "N",
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
        CtiNumStr(pointid), desolvePointType(StatusPointType) + CtiNumStr(offset), desolvePointType(StatusPointType), CtiNumStr(deviceid), "4", CtiNumStr(offset), "N",
        "N", "R", "None", "0", "0", desolveControlType(controlType), "123",
        "456", "control open", "control close", "0", "N", CtiNumStr(controlOffset) };

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

        const long deviceId = reinterpret_cast<long>(&points);

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

        const long deviceId = reinterpret_cast<long>(&points);

        switch( type )
        {
            case AnalogPointType:
            {
                point.reset(makeAnalogPoint(deviceId, pointId, offset));
            }
            break;

            case PulseAccumulatorPointType:
            case DemandAccumulatorPointType:
            {
                point.reset(makeAccumulatorPoint(deviceId, pointId, offset));
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

}
}
}

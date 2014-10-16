
#include "precompiled.h"

#include <cmath>
#include <cstdlib>
#include <utility>

#include "ccid.h"
#include "logger.h"

#include "ControlStrategy.h"
#include "StrategyLoader.h"
#include "IVVCStrategy.h"
#include "KVarStrategy.h"
#include "MultiVoltStrategy.h"
#include "MultiVoltVarStrategy.h"
#include "NoStrategy.h"
#include "PFactorKWKVarStrategy.h"
#include "PFactorKWKQStrategy.h"
#include "TimeOfDayStrategy.h"
#include "VoltStrategy.h"
#include "database_connection.h"
#include "database_reader.h"

using std::string;
using std::endl;

extern unsigned long _CC_DEBUG;


StrategyManager::StrategyMap StrategyDBLoader::load(const long ID)
{
    StrategyManager::StrategyMap    loaded;

    loadCore(ID, loaded);
    loadParameters(ID, loaded);

    return loaded;
}


void StrategyDBLoader::loadCore(const long ID, StrategyManager::StrategyMap &strategies)
{
    static const string sqlNoID =  "SELECT CCS.strategyid, CCS.strategyname, CCS.controlmethod, CCS.maxdailyoperation, "
                                       "CCS.maxoperationdisableflag, CCS.peakstarttime, CCS.peakstoptime, CCS.controlinterval, "
                                       "CCS.minresponsetime, CCS.minconfirmpercent, CCS.failurepercent, CCS.daysofweek, "
                                       "CCS.controlunits, CCS.controldelaytime, CCS.controlsendretries, CCS.integrateflag, "
                                       "CCS.integrateperiod, CCS.likedayfallback, CCS.enddaysettings "
                                   "FROM capcontrolstrategy CCS";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection);

    if( ID >= 0 )
    {
        static const string sqlID = string(sqlNoID + " WHERE CCS.strategyid = ?");
        rdr.setCommandText(sqlID);
        rdr << ID;
    }
    else
    {
        rdr.setCommandText(sqlNoID);
    }

    rdr.execute();

    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
    {
        CTILOG_INFO(dout, rdr.asString());
    }

    while ( rdr() )
    {
        std::string controlUnits;
        rdr["controlunits"]  >> controlUnits;

        if ( rdr.isValid() )
        {
            StrategyManager::SharedPtr strategy;

            if ( controlUnits == ControlStrategy::NoControlUnit )
            {
                strategy.reset( new NoStrategy );
            }
            else if ( controlUnits == ControlStrategy::KVarControlUnit )
            {
                strategy.reset( new KVarStrategy );
            }
            else if ( controlUnits == ControlStrategy::VoltsControlUnit )
            {
                strategy.reset( new VoltStrategy );
            }
            else if ( controlUnits == ControlStrategy::MultiVoltControlUnit )
            {
                strategy.reset( new MultiVoltStrategy );
            }
            else if ( controlUnits == ControlStrategy::MultiVoltVarControlUnit )
            {
                strategy.reset( new MultiVoltVarStrategy );
            }
            else if ( controlUnits == ControlStrategy::PFactorKWKVarControlUnit )
            {
                strategy.reset( new PFactorKWKVarStrategy );
            }
            else if ( controlUnits == ControlStrategy::PFactorKWKQControlUnit )
            {
                strategy.reset( new PFactorKWKQStrategy );
            }
            else if ( controlUnits == ControlStrategy::TimeOfDayControlUnit )
            {
                strategy.reset( new TimeOfDayStrategy );
            }
            else if ( controlUnits == ControlStrategy::IntegratedVoltVarControlUnit )
            {
                strategy.reset( new IVVCStrategy(PointDataRequestFactoryPtr(new PointDataRequestFactory())) );
            }
            else
            {
                CTILOG_WARN(dout, "Unsupported Strategy Type: " << controlUnits);
            }

            if (strategy != 0)
            {
                long        dBLong;
                std::string dBString;

                rdr["strategyid"] >> dBLong;
                strategy->setStrategyId(dBLong);

                rdr["strategyname"] >> dBString;
                strategy->setStrategyName(dBString);

                rdr["maxdailyoperation"] >> dBLong;
                strategy->setMaxDailyOperation(dBLong);

                rdr["maxoperationdisableflag"] >> dBString;
                CtiToLower(dBString);
                strategy->setMaxOperationDisableFlag(dBString == "y");

                rdr["peakstarttime"] >> dBLong;
                strategy->setPeakStartTime(dBLong);

                rdr["peakstoptime"] >> dBLong;
                strategy->setPeakStopTime(dBLong);

                rdr["controlinterval"] >> dBLong;
                strategy->setControlInterval(dBLong);

                rdr["minresponsetime"] >> dBLong;
                strategy->setMaxConfirmTime(dBLong);

                rdr["minconfirmpercent"] >> dBLong;
                strategy->setMinConfirmPercent(dBLong);

                rdr["failurepercent"] >> dBLong;
                strategy->setFailurePercent(dBLong);

                rdr["daysofweek"] >> dBString;
                strategy->setDaysOfWeek(dBString);

                rdr["controldelaytime"] >> dBLong;
                strategy->setControlDelayTime(dBLong);

                rdr["controlsendretries"] >> dBLong;
                strategy->setControlSendRetries(dBLong);

                rdr["integrateflag"] >> dBString;
                CtiToLower(dBString);
                strategy->setIntegrateFlag(dBString == "y");

                rdr["integrateperiod"] >> dBLong;
                strategy->setIntegratePeriod(dBLong);

                rdr["likedayfallback"] >> dBString;
                CtiToLower(dBString);
                strategy->setLikeDayFallBack(dBString == "y");

                rdr["enddaysettings"] >> dBString;
                strategy->setEndDaySettings(dBString);

                rdr["controlmethod"] >> dBString;
                strategy->setControlMethod(dBString);

                if ( rdr.isValid() )        // reader is ~still~ valid
                {
                    strategies[ strategy->getStrategyId() ] = strategy;     // insert/update...
                }
            }
        }
    }
}

void StrategyDBLoader::loadParameters(const long ID, StrategyManager::StrategyMap &strategies)
{
    static const string sqlNoID = "SELECT CST.strategyid, CST.settingname, CST.settingvalue, CST.settingtype "
                                  "FROM ccstrategytargetsettings CST";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection);

    if( ID >= 0 )
    {
        static const string sqlID = string(sqlNoID + " WHERE CST.strategyid = ?");
        rdr.setCommandText(sqlID);
        rdr << ID;
    }
    else
    {
        rdr.setCommandText(sqlNoID);
    }

    rdr.execute();

    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
    {
        CTILOG_INFO(dout, rdr.asString());
    }

    while ( rdr() )
    {
        long strategyID  = -1;
        rdr["strategyid"] >> strategyID;

        if ( rdr.isValid() )
        {
            StrategyManager::StrategyMap::const_iterator iter = strategies.find(strategyID);

            if ( iter != strategies.end() )
            {
                StrategyManager::SharedPtr strategy(iter->second);

                std::string name;
                std::string type;
                std::string value;

                rdr["settingname"] >> name;
                rdr["settingtype"] >> type;
                rdr["settingvalue"] >> value;

                if ( rdr.isValid() )
                {
                    strategy->restoreParameters(name, type, value);
                }
            }
        }
    }
}

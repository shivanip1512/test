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
#include "TimeOfDayStrategy.h"
#include "VoltStrategy.h"
#include "database_connection.h"
#include "database_reader.h"

using std::string;

extern unsigned long _CC_DEBUG;


namespace
{
    bool deserializeFlag( const std::string & input )
    {
        if ( input.size() == 1 )
        {
            if ( input[ 0 ] == 'Y' ||
                 input[ 0 ] == 'y' ||
                 input[ 0 ] == '1'  )
            {
                return true;
            }
        }

        return false;
    }
}

void StrategyLoader::parseCoreReader(Cti::RowReader & reader, StrategyManager::StrategyMap &strategies)
{
    if ( reader.isValid() )
    {
        std::string                 controlUnits;
        StrategyManager::SharedPtr  strategy;

        reader["controlunits"]  >> controlUnits;

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

            reader["strategyid"] >> dBLong;
            strategy->setStrategyId(dBLong);

            reader["strategyname"] >> dBString;
            strategy->setStrategyName(dBString);

            reader["maxdailyoperation"] >> dBLong;
            strategy->setMaxDailyOperation(dBLong);

            reader["maxoperationdisableflag"] >> dBString;
            strategy->setMaxOperationDisableFlag( deserializeFlag( dBString ) );

            reader["peakstarttime"] >> dBLong;
            strategy->setPeakStartTime(dBLong);

            reader["peakstoptime"] >> dBLong;
            strategy->setPeakStopTime(dBLong);

            reader["controlinterval"] >> dBLong;
            strategy->setControlInterval(dBLong);

            reader["minresponsetime"] >> dBLong;
            strategy->setMaxConfirmTime(dBLong);

            reader["minconfirmpercent"] >> dBLong;
            strategy->setMinConfirmPercent(dBLong);

            reader["failurepercent"] >> dBLong;
            strategy->setFailurePercent(dBLong);

            reader["daysofweek"] >> dBString;
            strategy->setDaysOfWeek(dBString);

            reader["controldelaytime"] >> dBLong;
            strategy->setControlDelayTime(dBLong);

            reader["controlsendretries"] >> dBLong;
            strategy->setControlSendRetries(dBLong);

            reader["integrateflag"] >> dBString;
            strategy->setIntegrateFlag( deserializeFlag( dBString ) );

            reader["integrateperiod"] >> dBLong;
            strategy->setIntegratePeriod(dBLong);

            reader["likedayfallback"] >> dBString;
            strategy->setLikeDayFallBack( deserializeFlag( dBString ) );

            reader["enddaysettings"] >> dBString;
            strategy->setEndDaySettings(dBString);

            reader["controlmethod"] >> dBString;
            strategy->setControlMethod(dBString);

            if ( reader.isValid() )        // reader is ~still~ valid
            {
                strategies[ strategy->getStrategyId() ] = strategy;     // insert/update...
            }
        }
    }
}


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
        parseCoreReader(rdr, strategies);
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


/* 
    COPYRIGHT: Copyright (C) 2010
                    Cooper Power Systems EAS
                    Cannon Technologies, Inc.
---------------------------------------------------------------------------*/

#include "yukon.h"

#include <cmath>
#include <cstdlib>
#include <utility>

#include "ccid.h"
#include "logger.h"
#include "rwutil.h"
#include "StrategyLoader.h"


extern ULONG _CC_DEBUG;


void StrategyDBLoader::loadCore(const long ID, StrategyMap &strategies)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _dbMutex );
    CtiLockGuard<CtiSemaphore>              cg( gDBAccessSema );

    RWDBConnection conn = getConnection();
    if ( conn.isValid() )
    {
        RWDBDatabase db                 = getDatabase();
        RWDBTable    capControlStrategy = db.table("capcontrolstrategy");
        RWDBSelector selector           = db.selector();

        selector
            << capControlStrategy["strategyid"]
            << capControlStrategy["strategyname"]
            << capControlStrategy["controlmethod"]
            << capControlStrategy["maxdailyoperation"]
            << capControlStrategy["maxoperationdisableflag"]
            << capControlStrategy["peakstarttime"]
            << capControlStrategy["peakstoptime"]
            << capControlStrategy["controlinterval"]
            << capControlStrategy["minresponsetime"]
            << capControlStrategy["minconfirmpercent"]
            << capControlStrategy["failurepercent"]
            << capControlStrategy["daysofweek"]
            << capControlStrategy["controlunits"]
            << capControlStrategy["controldelaytime"]
            << capControlStrategy["controlsendretries"]
            << capControlStrategy["integrateflag"]
            << capControlStrategy["integrateperiod"]
            << capControlStrategy["likedayfallback"]
            << capControlStrategy["enddaysettings"]
                ;

        selector.from(capControlStrategy);
        if (ID >= 0)
        {    
            selector.where( capControlStrategy["strategyid"] == ID );
        }

        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
        {
            std::string loggedSQLstring = selector.asString();
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << loggedSQLstring << endl;
            }
        }

        RWDBReader rdr = selector.reader(conn);

        while ( rdr() )
        {
            std::string controlUnits;
            StrategyMap::mapped_type strategy;

            rdr["controlunits"]  >> controlUnits;

            if ( rdr.isValid() )
            {
                if ( controlUnits == ControlStrategy::NoControlUnit )
                {
                    strategy.reset( new NoStrategy() );
                }
                else if ( controlUnits == ControlStrategy::KVarControlUnit )
                {
                    strategy.reset( new KVarStrategy() );
                }
                else if ( controlUnits == ControlStrategy::VoltsControlUnit )
                {
                    strategy.reset( new VoltStrategy() );
                }
                else if ( controlUnits == ControlStrategy::MultiVoltControlUnit )
                {
                    strategy.reset( new MultiVoltStrategy() );
                }
                else if ( controlUnits == ControlStrategy::MultiVoltVarControlUnit )
                {
                    strategy.reset( new MultiVoltVarStrategy() );
                }
                else if ( controlUnits == ControlStrategy::PFactorKWKVarControlUnit )
                {
                    strategy.reset( new PFactorKWKVarStrategy() );
                }
                else if ( controlUnits == ControlStrategy::PFactorKWKQControlUnit )
                {
                    strategy.reset( new PFactorKWKQStrategy() );
                }
                else if ( controlUnits == ControlStrategy::TimeOfDayControlUnit )
                {
                    strategy.reset( new TimeOfDayStrategy() );
                }
                else if ( controlUnits == ControlStrategy::IntegratedVoltVarControlUnit )
                {
                    strategy.reset( new IVVCStrategy() );
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Unsupported Strategy Type: " << controlUnits << endl;
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
}



void StrategyDBLoader::loadParameters(const long ID, StrategyMap &strategies)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _dbMutex );
    CtiLockGuard<CtiSemaphore>              cg( gDBAccessSema );

    RWDBConnection conn = getConnection();
    if ( conn.isValid() )
    {

        RWDBDatabase db                 = getDatabase();
        RWDBTable    ccStrategyParams   = db.table("ccstrategytargetsettings");
        RWDBSelector selector           = db.selector();

        selector
            << ccStrategyParams["strategyid"]
            << ccStrategyParams["settingname"]
            << ccStrategyParams["settingvalue"]
            << ccStrategyParams["settingtype"]
                ;

        selector.from(ccStrategyParams);
        if (ID >= 0)
        {    
            selector.where(ccStrategyParams["strategyid"] == ID);
        }

        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
        {
            std::string loggedSQLstring = selector.asString();
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << loggedSQLstring << endl;
            }
        }

        RWDBReader rdr = selector.reader(conn);

        while ( rdr() )
        {
            long strategyID  = 0;
            StrategyMap::mapped_type strategy;

            rdr["strategyid"] >> strategyID;

            if ( rdr.isValid() )
            {
                StrategyMap::iterator iter = strategies.find(strategyID);

                if ( iter != strategies.end() )
                {
                    strategy = iter->second;

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
}


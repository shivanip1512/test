
/*---------------------------------------------------------------------------
        Filename:  ccoperationstats.cpp

        Programmer:  Julie Richter

        Description:    Source file for CtiCCOperationStats.
                        CtiCCOperationStats maintains the state and handles
                        the persistence of cap banks for Cap Control.

        Initial Date:  8/30/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "ccid.h"
#include "pointdefs.h"
#include "logger.h"
#include "resolvers.h"
#include "ccoperationstats.h"

extern ULONG _CC_DEBUG;

//RWDEFINE_COLLECTABLE( CtiCCOperationStats, CtiCCOperationStats_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCOperationStats::CtiCCOperationStats()
{
    init();
    _insertDynamicDataFlag = TRUE;
    _dirty = TRUE;

    return;
}


CtiCCOperationStats::CtiCCOperationStats(const CtiCCOperationStats& twoWayPt)
{
    operator=(twoWayPt);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCOperationStats::~CtiCCOperationStats()
{

}

/*---------------------------------------------------------------------------
    Init
---------------------------------------------------------------------------*/
void CtiCCOperationStats::init()
{
    _paoid = 0;
    _userDefOpCount = 0;
    _userDefConfFail = 0;
    _dailyOpCount = 0;
    _dailyConfFail = 0;
    _weeklyOpCount = 0;	
    _weeklyConfFail = 0;
    _monthlyOpCount = 0;
    _monthlyConfFail = 0;

    return;
}

LONG CtiCCOperationStats::getPAOId() const
{
    return _paoid;
}
LONG CtiCCOperationStats::getUserDefOpCount() const
{
    return _userDefOpCount;
}
LONG CtiCCOperationStats::getUserDefConfFail() const
{
    return _userDefConfFail;
}
LONG CtiCCOperationStats::getDailyOpCount() const
{
    return _dailyOpCount;
}
LONG CtiCCOperationStats::getDailyConfFail() const
{
    return _dailyConfFail;
}
LONG CtiCCOperationStats::getWeeklyOpCount() const
{
    return _weeklyOpCount;
}
LONG CtiCCOperationStats::getWeeklyConfFail() const
{
    return _weeklyConfFail;
}                          
LONG CtiCCOperationStats::getMonthlyOpCount() const
{
    return _monthlyOpCount;
}
LONG CtiCCOperationStats::getMonthlyConfFail() const
{
    return _monthlyConfFail;
}

    
CtiCCOperationStats& CtiCCOperationStats::setPAOId(LONG paoId)
{
    _paoid = paoId;
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setUserDefOpCount(LONG value)
{
    if (_userDefOpCount != value)
    {
        _dirty = TRUE;
    }
    _userDefOpCount = value;
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setUserDefConfFail(LONG value)
{
    if (_userDefConfFail != value)
    {
        _dirty = TRUE;
    }
    _userDefConfFail = value;
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setDailyOpCount(LONG value)
{
    if (_dailyOpCount != value)
    {
        _dirty = TRUE;
    }
    _dailyOpCount = value;
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setDailyConfFail(LONG value)
{
    if (_dailyConfFail != value)
    {
        _dirty = TRUE;
    }
    _dailyConfFail = value;
    return *this;
}


CtiCCOperationStats& CtiCCOperationStats::setWeeklyOpCount(LONG value)
{
    if (_weeklyOpCount != value)
    {
        _dirty = TRUE;
    }
    _weeklyOpCount = value;
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setWeeklyConfFail(LONG value)
{
    if (_weeklyConfFail != value)
    {
        _dirty = TRUE;
    }
    _weeklyConfFail = value;
    return *this;
}


CtiCCOperationStats& CtiCCOperationStats::setMonthlyOpCount(LONG value)
{
    if (_monthlyOpCount != value)
    {
        _dirty = TRUE;
    }
    _monthlyOpCount = value;
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setMonthlyConfFail(LONG value)
{
    if (_monthlyConfFail != value)
    {
        _dirty = TRUE;
    }
    _monthlyConfFail = value;
    return *this;
}






BOOL CtiCCOperationStats::isDirty()
{
    return _dirty;
}
void CtiCCOperationStats::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,CtiTime());
}
void CtiCCOperationStats::dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime)
{

    {

        RWDBTable dynamicCCOpStats = getDatabase().table( "dynamicccoperationstatistics" );
        if( !_insertDynamicDataFlag )
        {
            RWDBUpdater updater = dynamicCCOpStats.updater();

            updater.where(dynamicCCOpStats["paobjectid"] == _paoid);

            updater << dynamicCCOpStats["userdefopcount"].assign( _userDefOpCount )
                    << dynamicCCOpStats["userdefconffail"].assign( _userDefConfFail )
                    << dynamicCCOpStats["dailyopcount"].assign(   _dailyOpCount )
                    << dynamicCCOpStats["dailyconffail"].assign( _dailyConfFail )
                    << dynamicCCOpStats["weeklyopcount"].assign( _weeklyOpCount )
                    << dynamicCCOpStats["weeklyconffail"].assign( _weeklyConfFail )
                    << dynamicCCOpStats["monthlyopcount"].assign( _monthlyOpCount )
                    << dynamicCCOpStats["monthlyconffail"].assign( _monthlyConfFail);
            ;

            updater.execute( conn );

            if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updater.asString() << endl;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Inserted CC Operation Statistics into DynamicCCOperationStatistics: " << getPAOId() << endl;
            }
            RWDBInserter inserter = dynamicCCOpStats.inserter();
            
            inserter << _paoid
                << _userDefOpCount  
                << _userDefConfFail        
                << _dailyOpCount            
                << _dailyConfFail            
                << _weeklyOpCount            
                << _weeklyConfFail          
                << _monthlyOpCount         
                << _monthlyConfFail;

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << inserter.asString().data() << endl;
            }

            inserter.execute( conn );

            if(inserter.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _insertDynamicDataFlag = FALSE;
                _dirty = FALSE;
            }
            else
            {
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << inserter.asString() << endl;
                }
            }
        }
    }

}


void CtiCCOperationStats::setDynamicData(RWDBReader& rdr)
{
    rdr["userdefopcount"] >> _userDefOpCount;
    rdr["userdefconffail"] >> _userDefConfFail;
    rdr["dailyopcount"] >> _dailyOpCount;
    rdr["dailyconffail"] >> _dailyConfFail;               
    rdr["weeklyopcount"] >> _weeklyOpCount;
    rdr["weeklyconffail"] >> _weeklyConfFail;
    rdr["monthlyopcount"] >> _monthlyOpCount;
    rdr["montlyconffail"] >> _monthlyConfFail;

    _insertDynamicDataFlag = FALSE;
    _dirty = false;


}
/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields.
---------------------------------------------------------------------------*/
CtiCCOperationStats* CtiCCOperationStats::replicate() const
{
    return(new CtiCCOperationStats(*this));
}


CtiCCOperationStats& CtiCCOperationStats::operator=(const CtiCCOperationStats& right)
{
    if( this != &right )
    {
        _paoid = right._paoid;
        _userDefOpCount   = right._userDefOpCount; 
        _userDefConfFail  = right._userDefConfFail;
        _dailyOpCount     = right._dailyOpCount;   
        _dailyConfFail    = right._dailyConfFail;  
        _weeklyOpCount    = right._weeklyOpCount;  
        _weeklyConfFail   = right._weeklyConfFail; 
        _monthlyOpCount   = right._monthlyOpCount; 
        _monthlyConfFail  = right._monthlyConfFail;

        _insertDynamicDataFlag = right._insertDynamicDataFlag;
        _dirty = right._dirty;
    }
    return *this;
}

int CtiCCOperationStats::operator==(const CtiCCOperationStats& right) const
{
    return getPAOId() == right.getPAOId();
}
int CtiCCOperationStats::operator!=(const CtiCCOperationStats& right) const
{
    return getPAOId() != right.getPAOId();
}


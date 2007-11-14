
/*---------------------------------------------------------------------------
        Filename:  ccsparea.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCSpecial.
                        CtiCCSpecial maintains the state and handles
                        the persistence of substation buses for Cap Control.

        Initial Date:  8/28/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/tpsrtvec.h>

#include "dbaccess.h"
#include "msg_signal.h"

#include "ccsparea.h"
#include "ccid.h"
#include "cccapbank.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "capcontroller.h"
#include "resolvers.h"
#include "utility.h"

extern ULONG _CC_DEBUG;

RWDEFINE_COLLECTABLE( CtiCCSpecial, CTICCSPECIALAREA_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCSpecial::CtiCCSpecial()
{
}

CtiCCSpecial::CtiCCSpecial(RWDBReader& rdr)
{
    restore(rdr);
}

CtiCCSpecial::CtiCCSpecial(const CtiCCSpecial& special)
{
    operator=(special);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCSpecial::~CtiCCSpecial()
{  
    
}


/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCCSpecial::restoreGuts(RWvistream& istrm)
{
    LONG numOfSubIds;
    LONG tempSubId;
    RWCollectable::restoreGuts( istrm );

    istrm >> _paoid
    >> _paocategory
    >> _paoclass
    >> _paoname
    >> _paotype
    >> _paodescription
    >> _disableflag;
    
    istrm >> numOfSubIds;
    _substationIds.clear();
    for(LONG i=0;i<numOfSubIds;i++)
    {
        istrm >> tempSubId;
        _substationIds.push_back(tempSubId);
    }
    istrm >> _ovUvDisabledFlag
        >> _pfactor
        >> _estPfactor;

}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCSpecial::saveGuts(RWvostream& ostrm ) const
{
    RWCollectable::saveGuts( ostrm );

    ostrm << _paoid
    << _paocategory
    << _paoclass
    << _paoname
    << _paotype
    << _paodescription
    << _disableflag;
    ostrm << _substationIds.size();
    std::list<LONG>::const_iterator iter = _substationIds.begin();

    for(LONG i=0;i<_substationIds.size();i++)
    {
        ostrm << (LONG)*iter;
        iter++;
    }
    ostrm << _ovUvDisabledFlag
        << _pfactor
        << _estPfactor;


}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::operator=(const CtiCCSpecial& right)
{
    if( this != &right )
    {
        _paoid = right.getPAOId();
        _paocategory = right._paocategory;
        _paoclass = right._paoclass;
        _paoname = right._paoname;
        _paotype = right._paotype;
        _paodescription = right._paodescription;
        _disableflag = right._disableflag;
        _ovUvDisabledFlag = right._ovUvDisabledFlag;
        _pfactor = right._pfactor;
        _estPfactor = right._estPfactor;

        _substationIds.clear();
        _substationIds.assign(right._substationIds.begin(), right._substationIds.end());
          
    }
    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiCCSpecial::operator==(const CtiCCSpecial& right) const
{
    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiCCSpecial::operator!=(const CtiCCSpecial& right) const
{
    return getPAOId() != right.getPAOId();
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCCSpecial* CtiCCSpecial::replicate() const
{
    return(new CtiCCSpecial(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiCCSpecial::restore(RWDBReader& rdr)
{
    string tempBoolString;

    rdr["paobjectid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> _paotype;
    rdr["description"] >> _paodescription;
    rdr["disableflag"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _disableflag = (tempBoolString=="y"?TRUE:FALSE);
    setOvUvDisabledFlag(FALSE);
    setPFactor(0);
    setEstPFactor(0);

}


/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc feeder.
---------------------------------------------------------------------------*/
void CtiCCSpecial::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this substation bus.
---------------------------------------------------------------------------*/
void CtiCCSpecial::dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime)
{
    {
        RWDBTable dynamicCCAreaTable = getDatabase().table( "dynamicccspecialarea" );

        if( !_insertDynamicDataFlag )
        {
            RWDBUpdater updater = dynamicCCAreaTable.updater();

            
            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};
            addFlags[0] = (_ovUvDisabledFlag?'Y':'N');
            _additionalFlags = string(char2string(*addFlags));
            _additionalFlags.append("NNNNNNNNNNNNNNNNNNN");

            updater.clear();

            updater.where(dynamicCCAreaTable["areaid"]==_paoid);

            updater.execute( conn );

                        if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
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
                dout << CtiTime() << " - Inserted area into dynamicCCArea: " << getPAOName() << endl;
            }
            string addFlags ="NNNNNNNNNNNNNNNNNNNN";

            RWDBInserter inserter = dynamicCCAreaTable.inserter();
            //TS FLAG
            inserter << _paoid
            << addFlags;

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
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
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
void CtiCCSpecial::setDynamicData(RWDBReader& rdr)
{   
    rdr["additionalflags"] >> _additionalFlags;
    _ovUvDisabledFlag = (_additionalFlags[0]=='y'?TRUE:FALSE);

    _insertDynamicDataFlag = FALSE;
    _dirty = false;



}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the unique id of the area
---------------------------------------------------------------------------*/
LONG CtiCCSpecial::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the area
---------------------------------------------------------------------------*/
const string& CtiCCSpecial::getPAOCategory() const
{
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the area
---------------------------------------------------------------------------*/
const string& CtiCCSpecial::getPAOClass() const
{
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the area
---------------------------------------------------------------------------*/
const string& CtiCCSpecial::getPAOName() const
{
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the area
---------------------------------------------------------------------------*/
const string& CtiCCSpecial::getPAOType() const
{
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the area
---------------------------------------------------------------------------*/
const string& CtiCCSpecial::getPAODescription() const
{
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the area
---------------------------------------------------------------------------*/
BOOL CtiCCSpecial::getDisableFlag() const
{
    return _disableflag;
}
/*---------------------------------------------------------------------------
    getOvUvDisabledFlag

    Returns the ovuv disable flag of the area
---------------------------------------------------------------------------*/
BOOL CtiCCSpecial::getOvUvDisabledFlag() const
{
    return _ovUvDisabledFlag;
}

/*---------------------------------------------------------------------------
    getPFactor

    Returns the getPFactor of the area
---------------------------------------------------------------------------*/
DOUBLE CtiCCSpecial::getPFactor() const
{
    return _pfactor;
}
/*---------------------------------------------------------------------------
    getEstPFactor

    Returns the getEstPFactor of the area
---------------------------------------------------------------------------*/
DOUBLE CtiCCSpecial::getEstPFactor() const
{
    return _estPfactor;
}



/*---------------------------------------------------------------------------
    getStrategyId

    Returns the strategy id of the area
---------------------------------------------------------------------------*/
LONG CtiCCSpecial::getStrategyId() const
{
    return _strategyId;
}


void CtiCCSpecial::setStrategyValues(CtiCCStrategyPtr strategy)
{
    string tempBoolString;

    _strategyName = strategy->getStrategyName();                      
    _controlmethod = strategy->getControlMethod();                    
    _maxdailyoperation = strategy->getMaxDailyOperation();            
    _maxoperationdisableflag = strategy->getMaxOperationDisableFlag();
    _peaklag = strategy->getPeakLag();                      
    _offpklag = strategy->getOffPeakLag();                
    _peaklead = strategy->getPeakLead();                      
    _offpklead = strategy->getOffPeakLead();                
    _peakVARlag = strategy->getPeakVARLag();                      
    _offpkVARlag = strategy->getOffPeakVARLag();                
    _peakVARlead = strategy->getPeakVARLead();                      
    _offpkVARlead = strategy->getOffPeakVARLead();                
    _peakstarttime = strategy->getPeakStartTime();                    
    _peakstoptime = strategy->getPeakStopTime();                      
    _controlinterval = strategy->getControlInterval();                
    _maxconfirmtime = strategy->getMaxConfirmTime();                  
    _minconfirmpercent = strategy->getMinConfirmPercent();            
    _failurepercent = strategy->getFailurePercent();                  
    _daysofweek = strategy->getDaysOfWeek();                          
    _controlunits = strategy->getControlUnits();                      
    _controldelaytime = strategy->getControlDelayTime();              
    _controlsendretries = strategy->getControlSendRetries();  
    _integrateflag = strategy->getIntegrateFlag();
    _integrateperiod = strategy->getIntegratePeriod();

}


/*---------------------------------------------------------------------------
    setPAOId

    Sets the unique id of the area - use with caution
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::setPAOId(LONG id)
{
    _paoid = id;
    //do not notify observers of this!
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOCategory

    Sets the pao category of the area
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::setPAOCategory(const string& category)
{
    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the area
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::setPAOClass(const string& pclass)
{
    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the area
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::setPAOName(const string& name)
{
    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the area
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::setPAOType(const string& _type)
{
    _paotype = _type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the area
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::setPAODescription(const string& description)
{
    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flag of the area
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::setDisableFlag(BOOL disable)
{
    _disableflag = disable;
    return *this;
}

/*---------------------------------------------------------------------------
    setStrategyId

    Sets the strategyId of the area
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::setStrategyId(LONG strategyId)
{
    _strategyId = strategyId;
    return *this;
}

/*---------------------------------------------------------------------------
    setOvUvDisabledFlag

    Sets the ovuv disable flag of the area
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::setOvUvDisabledFlag(BOOL flag)
{
    _ovUvDisabledFlag = flag;
    return *this;
}

/*---------------------------------------------------------------------------
    setPFactor
    
    Sets the PFactor of the area
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::setPFactor(DOUBLE pfactor)
{
    _pfactor = pfactor;
    return *this;
}
/*---------------------------------------------------------------------------
    setEstPFactor
    
    Sets the estPFactor of the area
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::setEstPFactor(DOUBLE estpfactor)
{
    _estPfactor = estpfactor;
    return *this;
}





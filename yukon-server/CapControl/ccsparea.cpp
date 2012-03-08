
/*---------------------------------------------------------------------------
        Filename:  ccsparea.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCSpecial.
                        CtiCCSpecial maintains the state and handles
                        the persistence of substation buses for Cap Control.

        Initial Date:  8/28/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "precompiled.h"

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
#include "ccOperationStats.h"
#include "ccConfirmationStats.h"
#include "database_writer.h"

using std::endl;

extern ULONG _CC_DEBUG;

RWDEFINE_COLLECTABLE( CtiCCSpecial, CTICCSPECIALAREA_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCSpecial::CtiCCSpecial()
    : CtiCCAreaBase(0),
      _insertDynamicDataFlag(false)
{
}

CtiCCSpecial::CtiCCSpecial(StrategyManager * strategyManager)
    : CtiCCAreaBase(strategyManager),
      _insertDynamicDataFlag(false)
{
}

CtiCCSpecial::CtiCCSpecial(Cti::RowReader& rdr, StrategyManager * strategyManager)
    : CtiCCAreaBase(rdr, strategyManager)
{
    restore(rdr);
}

CtiCCSpecial::CtiCCSpecial(const CtiCCSpecial& special)
    : CtiCCAreaBase(special)
{
    operator=(special);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCSpecial::~CtiCCSpecial()
{

}


/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCSpecial::saveGuts(RWvostream& ostrm ) const
{
    CtiCCAreaBase::saveGuts(ostrm);
    Cti::CapControl::PaoIdVector substationIds = getSubstationIds();
    ostrm << substationIds.size();
    for each (long subId in getSubstationIds())
    {
        ostrm << subId;
    }
    double pfDisplayValue = getPFactor();
    double estPfDisplayValue = getEstPFactor();

    // Modifying the display value of pFactor to represent +100% values as a negative value.
    if (pfDisplayValue > 1)
    {
        pfDisplayValue -= 2;
    }
    if (estPfDisplayValue > 1)
    {
        estPfDisplayValue -= 2;
    }

    ostrm << getOvUvDisabledFlag()
        << pfDisplayValue
        << estPfDisplayValue
        << getVoltReductionControlValue();

}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::operator=(const CtiCCSpecial& right)
{
    CtiCCAreaBase::operator=(right);

    if( this != &right )
    {
       _insertDynamicDataFlag = right._insertDynamicDataFlag;
    }
    return *this;
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

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiCCSpecial::restore(Cti::RowReader& rdr)
{
    CtiCCAreaBase::restore(rdr);
    
    setDirty(true);
    _insertDynamicDataFlag = TRUE;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this substation bus.
---------------------------------------------------------------------------*/
void CtiCCSpecial::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if ( isDirty() )
    {
        if( !_insertDynamicDataFlag )
        {


            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};
            addFlags[0] = (getOvUvDisabledFlag()?'Y':'N');
            string additionalFlags = string(char2string(*addFlags));
            additionalFlags.append("NNNNNNNNNNNNNNNNNNN");

            static const string updaterSql = "update dynamicccspecialarea set additionalflags = ?, controlvalue = ? "
                                             " where areaid = ?";
            Cti::Database::DatabaseWriter updater(conn, updaterSql);

            updater << additionalFlags << getVoltReductionControlValue() << getPaoId();

            if(updater.execute())    // No error occured!
            {
                setDirty(false);
            }
            else
            {
                
                setDirty(true);
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << updaterSql << endl;
                    }
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Inserted area into dynamicCCSpecialArea: " << getPaoName() << endl;
            }
            string addFlags ="NNNNNNNNNNNNNNNNNNNN";

            static const string inserterSql = "insert into dynamicccspecialarea values (?, ?, ?)";
            Cti::Database::DatabaseWriter inserter(conn, inserterSql);

            inserter << getPaoId() << addFlags << getVoltReductionControlValue();

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << inserterSql << endl;
                }
            }

            if(inserter.execute())    // No error occured!
            {
                _insertDynamicDataFlag = FALSE;
                setDirty(false);            
            }
            else
            {
                setDirty(true);
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << inserterSql << endl;
                    }
                }
            }
        }

        getOperationStats().dumpDynamicData(conn, currentDateTime);
    }
}
void CtiCCSpecial::setDynamicData(Cti::RowReader& rdr)
{
    CtiCCAreaBase::setDynamicData(rdr);
    _insertDynamicDataFlag = FALSE;
    setDirty(false);



}




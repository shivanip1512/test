/*-----------------------------------------------------------------------------
    Filename:  ccmessage.cpp

    Programmer:  Josh Wolberg

    Description:    Source file for message classes.

    Initial Date:  9/04/2001

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "ccmessage.h"
#include "ccid.h"
#include "VoltageRegulator.h"

#include <time.h>
#include "utility.h"

using std::endl;

extern ULONG _CC_DEBUG;


/*===========================================================================
    CtiCCShutdown
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCShutdown, CTICCSHUTDOWN_ID )

/*---------------------------------------------------------------------------
    restoreGuts

    Restores the state of self fromt he given RWvistream
---------------------------------------------------------------------------*/
void CtiCCShutdown::restoreGuts(RWvistream& strm)
{
    Inherited::restoreGuts(strm);
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCShutdown::saveGuts(RWvostream& strm) const
{
    Inherited::saveGuts(strm);
}



/*===========================================================================
    CtiCCSubstationVerificationMsg


===========================================================================*/

RWDEFINE_COLLECTABLE( CtiPAOScheduleMsg, CTIPAOSCHEDULEMSG_ID )

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiPAOScheduleMsg::~CtiPAOScheduleMsg()
{
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restores the state of self from the given RWvistream
---------------------------------------------------------------------------*/
void CtiPAOScheduleMsg::restoreGuts(RWvistream& strm)
{
    Inherited::restoreGuts(strm);
    strm >> _scheduleId;

    return;
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiPAOScheduleMsg::saveGuts(RWvostream& strm) const
{
    Inherited::saveGuts(strm);

    strm << _scheduleId;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiPAOScheduleMsg& CtiPAOScheduleMsg::operator=(const CtiPAOScheduleMsg& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);
        _scheduleId    = right._scheduleId;
    }

    return *this;
}

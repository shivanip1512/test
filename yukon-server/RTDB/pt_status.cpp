/*-----------------------------------------------------------------------------*
*
* File:   pt_status
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/pt_status.cpp-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2006/04/05 16:23:52 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "logger.h"
#include "pt_status.h"
#include "tbl_pt_alarm.h"
using namespace std;

/*----------------------------------------------------------------------------*
 * This method examines the point state, which should be in the range of
 * CtiTablePointAlarming::state0 - CtiTablePointAlarming::state9
 * if this Point has an non-event alarm grouping assigned to this state (limitOrState),
 * the method returns bool true and sets the error string.
 *----------------------------------------------------------------------------*/
bool CtiPointStatus::limitStateCheck( const int limitOrState, double val, INT &direction)
{
   bool status = false;
   int stateverify = CtiTablePointAlarming::state0 + limitOrState;

   if(CtiTablePointAlarming::state0 <= stateverify && stateverify <= CtiTablePointAlarming::state9)
   {
      if( getAlarming().getAlarmCategory(stateverify) > SignalEvent)
      {
         if( (int)val == limitOrState )
         {
            status = true;
         }
      }
   }

   return status;
}

CtiPointStatus::CtiPointStatus()
{
}

CtiPointStatus::CtiPointStatus(const CtiPointStatus& aRef)
{
   *this = aRef;
}

CtiPointStatus& CtiPointStatus::operator=(const CtiPointStatus& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
      _pointStatus   = aRef.getPointStatus();
   }

   return *this;
}

CtiTablePointStatus  CtiPointStatus::getPointStatus() const
{
    return _pointStatus;
}

CtiTablePointStatus& CtiPointStatus::getPointStatus()
{
    return _pointStatus;
}

void CtiPointStatus::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   Inherited::getSQL(db, keyTable, selector);
   CtiTablePointStatus::getSQL(db, keyTable, selector);
}

void CtiPointStatus::DecodeDatabaseReader(RWDBReader &rdr)
{
    if(isA(rdr))
    {
        Inherited::DecodeDatabaseReader(rdr);          // get the base class data out!
        //  this needs to be turned into a dout, if possible
        if(getDebugLevel() & DEBUGLEVEL_DATABASE) cout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
        _pointStatus.DecodeDatabaseReader(rdr);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getName() << " cannot decode this rdr " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void CtiPointStatus::DumpData()
{
   Inherited::DumpData();       // get the base class handled

   _pointStatus.dump();
}

UINT CtiPointStatus::adjustStaticTags(UINT &tag) const
{
    Inherited::adjustStaticTags(tag);
    return _pointStatus.adjustStaticTags(tag);
}

UINT CtiPointStatus::getStaticTags()
{
   return Inherited::getStaticTags() | _pointStatus.getStaticTags();
}


double CtiPointStatus::getDefaultValue( ) const
{
   return (double)(_pointStatus.getInitialState());
}

double CtiPointStatus::getInitialValue( ) const
{
   double v = 0.0;

   if(_pointStatus.getInitialState() >= 0)
   {
      v = _pointStatus.getInitialState();
   }

   return v;
}

int CtiPointStatus::getControlExpirationTime() const
{
    int ct = _pointStatus.getCommandTimeout();

    if(ct <= 0)
    {
        ct = Inherited::getControlExpirationTime();
    }

    return ct;
}

int CtiPointStatus::getControlOffset() const
{
    int pOff = 0;
    if(_pointStatus.getControlType() != NoneControlType && _pointStatus.getControlType() != InvalidControlType)
        pOff = _pointStatus.getControlOffset();

    return pOff;
}


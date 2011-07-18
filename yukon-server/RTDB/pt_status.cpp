/*-----------------------------------------------------------------------------*
*
* File:   pt_status
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/pt_status.cpp-arc  $
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2008/10/28 19:21:43 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"


#include "logger.h"
#include "pt_status.h"
#include "tbl_pt_alarm.h"
using namespace std;

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

string CtiPointStatus::getSQLCoreStatement()
{
    static const string sql =  "SELECT PT.pointid, PT.pointname, PT.pointtype, PT.paobjectid, PT.stategroupid, "
                                   "PT.pointoffset, PT.serviceflag, PT.alarminhibit, PT.pseudoflag, PT.archivetype, "
                                   "PT.archiveinterval, STS.initialstate, STS.controlinhibit, STS.controltype, "
                                   "STS.controloffset, STS.closetime1, STS.closetime2, STS.statezerocontrol, "
                                   "STS.stateonecontrol, STS.commandtimeout "
                               "FROM Point PT, PointStatus STS "
                               "WHERE PT.pointid = STS.pointid";

    return sql;
}

void CtiPointStatus::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    //if(isA(rdr))
    {
        Inherited::DecodeDatabaseReader(rdr);          // get the base class data out!
        //  this needs to be turned into a dout, if possible
        if(getDebugLevel() & DEBUGLEVEL_DATABASE)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        _pointStatus.DecodeDatabaseReader(rdr);
    }
    /*else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getName() << " cannot decode this rdr " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }*/
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


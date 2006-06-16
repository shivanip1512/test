/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_trigger
*
* Date:   5/16/2006
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_trigger.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2006/06/16 20:09:20 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"
#include "tbl_pt_trigger.h"
#include "logger.h"

#include "rwutil.h"


CtiTablePointTrigger::CtiTablePointTrigger() {}

CtiTablePointTrigger::CtiTablePointTrigger(const CtiTablePointTrigger& aRef)
{
    *this = aRef;
}

CtiTablePointTrigger::~CtiTablePointTrigger() {}

CtiTablePointTrigger& CtiTablePointTrigger::operator=(const CtiTablePointTrigger& aRef)
{
    if(this != &aRef)
    {
        _pointID                 = aRef.getPointID();
        _triggerID               = aRef.getTriggerID();
        _triggerDeadband         = aRef.getTriggerDeadband();
        _verificationID          = aRef.getVerificationID();
        _verificationDeadband    = aRef.getVerificationDeadband();
        _commandTimeOut          = aRef.getCommandTimeOut();
        _parameters              = aRef.getParameters();
    }
    return *this;
}

void CtiTablePointTrigger::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector, long pointID)
{
    RWDBTable tbl = db.table("PointTrigger");

    selector <<
    tbl["pointid"] <<
    tbl["triggerid"] <<
    tbl["triggerdeadband"] <<
    tbl["verificationid"] <<
    tbl["verificationdeadband"] <<
    tbl["commandtimeout"] <<
    tbl["parameters"];

    selector.from(tbl);

    if( pointID != 0)
    {
        selector.where( tbl["pointid"] == RWDBExpr( pointID ) && selector.where() );
    }
}

void CtiTablePointTrigger::DecodeDatabaseReader(RWDBReader &rdr)
{
    rdr["pointid"]              >> _pointID;
    rdr["triggerid"]            >> _triggerID;
    rdr["triggerdeadband"]      >> _triggerDeadband;
    rdr["verificationid"]       >> _verificationID;
    rdr["verificationdeadband"] >> _verificationDeadband;
    rdr["commandtimeout"]       >> _commandTimeOut;
    rdr["parameters"]           >> _parameters;
}

void CtiTablePointTrigger::dump() const
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << " Point ID                                 : " << _pointID << endl;
    dout << " Trigger ID                               : " << _triggerID << endl;
    dout << " Trigger Deadband                         : " << _triggerDeadband << endl;
    dout << " Verification ID                          : " << _verificationID << endl;
    dout << " Verification Deadband                    : " << _verificationDeadband << endl;
    dout << " Command Timeout                          : " << _commandTimeOut << endl;
    dout << " Parameters                               : " << _parameters << endl;
}

long CtiTablePointTrigger::getPointID() const
{
    return _pointID;
}

long CtiTablePointTrigger::getTriggerID() const
{
    return _triggerID;
}

DOUBLE CtiTablePointTrigger::getTriggerDeadband() const
{
    return _triggerDeadband;
}

long CtiTablePointTrigger::getVerificationID() const
{
    return _verificationID;
}

DOUBLE CtiTablePointTrigger::getVerificationDeadband() const
{
    return _verificationDeadband;
}

int CtiTablePointTrigger::getCommandTimeOut()  const
{
    return _commandTimeOut;
}

const string& CtiTablePointTrigger::getParameters() const
{
    return _parameters;
}


/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_trigger
*
* Date:   5/16/2006
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_trigger.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2007/09/28 15:43:05 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"
#include "tbl_pt_trigger.h"
#include "logger.h"

using std::string;
using std::endl;

CtiTablePointTrigger::CtiTablePointTrigger() :
    _pointID(0),
    _triggerID(0),
    _triggerDeadband(0),
    _verificationID(0),
    _verificationDeadband(0),
    _commandTimeOut(0)
{}

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
        //_parameters              = aRef.getParameters();
    }
    return *this;
}

string CtiTablePointTrigger::getSQLCoreStatement(long pointID)
{
    static const string sqlNoID =  "SELECT PTR.pointid, PTR.triggerid, PTR.triggerdeadband, PTR.verificationid, "
                                     "PTR.verificationdeadband, PTR.commandtimeout, PTR.parameters "
                                   "FROM PointTrigger PTR";

    if( pointID != 0 )
    {
        return string(sqlNoID + " WHERE PTR.pointid = ?");
    }
    else
    {
        return sqlNoID;
    }
}

void CtiTablePointTrigger::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    static const string pointid = "pointid";
    rdr[pointid] >> _pointID;
    rdr >> _triggerID;
    rdr >> _triggerDeadband;
    rdr >> _verificationID;
    rdr >> _verificationDeadband;
    rdr >> _commandTimeOut;
    //rdr >> _parameters;
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
    //dout << " Parameters                               : " << _parameters << endl;
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

/*const string& CtiTablePointTrigger::getParameters() const
{
    return _parameters;
}*/


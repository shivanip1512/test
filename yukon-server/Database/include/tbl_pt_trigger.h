/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_trigger
*
* Class:  CtiTablePointTrigger
* Date:   5/16/2006
*
* Author: Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pt_trigger.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2007/09/28 15:43:05 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __TBL_PT_TRIGGER_H__
#define __TBL_PT_TRIGGER_H__
#include "yukon.h"

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <limits.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"

class IM_EX_CTIYUKONDB CtiTablePointTrigger : public CtiMemDBObject
{
protected:

    long      _pointID;
    long      _triggerID;
    DOUBLE    _triggerDeadband;
    long      _verificationID;
    DOUBLE    _verificationDeadband;
    int       _commandTimeOut;
    //string    _parameters;

private:

    public:
    CtiTablePointTrigger();

    CtiTablePointTrigger(const CtiTablePointTrigger& aRef);
    virtual ~CtiTablePointTrigger();

    CtiTablePointTrigger& operator=(const CtiTablePointTrigger& aRef);

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector, long pointID = 0);
    void DecodeDatabaseReader(RWDBReader &rdr);
    void dump() const;

    long             getPointID()                const;
    long             getTriggerID()              const;
    DOUBLE           getTriggerDeadband()        const;
    long             getVerificationID()         const;
    DOUBLE           getVerificationDeadband()   const;
    int              getCommandTimeOut()         const;
    //const string&    getParameters()             const;
};

#endif // #ifndef __TBL_PT_TRIGGER_H__

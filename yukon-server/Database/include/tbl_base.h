

#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_base
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_base.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:40 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_BASE_H__
#define __TBL_BASE_H__

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>

#include <rw\cstring.h>

#include "resolvers.h"
#include "yukon.h"
#include "logger.h"
#include "dbmemobject.h"

/*----------------------------------------------------------------------------------------*
 * CtiTableDeviceBase is the base class for all device objects in the YUKON system.
 *
 * The CtiTableDeviceBase object is the only one which is synonymous with a table
 *  he could also be known as CtiTableDevice
 *----------------------------------------------------------------------------------------*/

class IM_EX_CTIYUKONDB CtiTableDeviceBase : public CtiMemDBObject
{

protected:

    bool      _alarmInhibit;
    bool      _controlInhibit;
    bool      _useRadioDelay;        // RadioDelay

public:

    typedef CtiMemDBObject Inherited;

    CtiTableDeviceBase();
    CtiTableDeviceBase(const CtiTableDeviceBase &aRef);
    virtual ~CtiTableDeviceBase();

    CtiTableDeviceBase& operator=(const CtiTableDeviceBase &aRef);
    CtiTableDeviceBase& setAlarmInhibit(bool b);
    CtiTableDeviceBase& setControlInhibit(bool b);
    CtiTableDeviceBase& setRadioDelay(bool b);
    bool  getAlarmInhibit() const;
    bool  getControlInhibit() const;
    bool  getRadioDelay() const;
    bool  useRadioDelays() const;
    static RWCString getTableName();
    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DumpData();
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
};

#endif

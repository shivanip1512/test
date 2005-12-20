
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_dialup
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_dialup.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __TBL_DIALUP_H__
#define __TBL_DIALUP_H__

#include <limits.h>

#include <rw/db/reader.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"


class IM_EX_CTIYUKONDB CtiTableDeviceDialup : public CtiMemDBObject
{

protected:

    LONG        _deviceID;
    string   PhoneNumber;
    INT         MinConnectTime;
    INT         MaxConnectTime;
    string   LineSettings;
    INT         BaudRate;

public:

    CtiTableDeviceDialup();

    CtiTableDeviceDialup(const CtiTableDeviceDialup &aRef);

    CtiTableDeviceDialup& operator=(const CtiTableDeviceDialup &aRef);

    INT  getMinConnectTime() const;
    void setMinConnectTime(INT  i);

    INT  getMaxConnectTime() const;
    void setMaxConnectTime(INT  i);

    INT  getBaudRate() const;
    CtiTableDeviceDialup& setBaudRate(INT i);

    string getPhoneNumber() const;
    void setPhoneNumber(const string &str);

    string getLineSettings() const;
    void setLineSettings(const string &lstr);

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    LONG getDeviceID() const;
    CtiTableDeviceDialup& setDeviceID( const LONG did);

    static string getTableName();

    virtual RWDBStatus Restore();
    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Delete();

    INT getStopBits() const;
    INT getParity() const;
    INT getBits() const;

};

#endif

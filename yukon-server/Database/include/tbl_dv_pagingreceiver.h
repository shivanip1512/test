/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_pagingreceiver
*
* Date:   5/9/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_dv_pagingreceiver.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/06/29 19:49:49 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DV_PAGINGRECEIVER_H__
#define __TBL_DV_PAGINGRECEIVER_H__

#include <rw/db/reader.h>
#include <rw\cstring.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"

class IM_EX_CTIYUKONDB CtiTableDevicePagingReceiver : public CtiMemDBObject
{
protected:

    LONG         _deviceID;
    float        _frequency;
    int          _capcode1;
    int          _capcode2;
    int          _capcode3;
    int          _capcode4;
    int          _capcode5;
    int          _capcode6;
    int          _capcode7;
    int          _capcode8;
    int          _capcode9;
    int          _capcode10;
    int          _capcode11;
    int          _capcode12;
    int          _capcode13;
    int          _capcode14;
    int          _capcode15;
    int          _capcode16;

private:

public:

    CtiTableDevicePagingReceiver();

    CtiTableDevicePagingReceiver(const CtiTableDevicePagingReceiver& aRef);

    virtual ~CtiTableDevicePagingReceiver();

    CtiTableDevicePagingReceiver& operator=(const CtiTableDevicePagingReceiver& aRef);

    float CtiTableDevicePagingReceiver::getFrequency() const;
    float CtiTableDevicePagingReceiver::getCapcode(int codeNumber) const;

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    LONG getDeviceID() const;

    static RWCString getTableName();
    virtual RWDBStatus Restore();
    virtual RWDBStatus Update();
    virtual RWDBStatus Insert();
    virtual RWDBStatus Delete();
};
#endif // #ifndef __TBL_DV_PAGINGRECEIVER_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_seriesv
*
* Date:   8/23/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __TBL_DV_SERIESV_H__
#define __TBL_DV_SERIESV_H__

#pragma warning( disable : 4786)

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

class IM_EX_CTIYUKONDB CtiTableDeviceSeriesV : public CtiMemDBObject
{
protected:

    LONG      _device_id;
    string _start_code,
              _stop_code;
    bool      _save_history;
    INT       _tick_time,
              _transmit_offset,
              _power_value_high,
              _power_value_low,
              _power_value_multiplier,
              _power_value_offset,
              _retries;

private:

public:

    CtiTableDeviceSeriesV();

    CtiTableDeviceSeriesV(const CtiTableDeviceSeriesV& aRef);

    virtual ~CtiTableDeviceSeriesV();

    CtiTableDeviceSeriesV& operator=(const CtiTableDeviceSeriesV& aRef);

    long                   getDeviceID() const;
    CtiTableDeviceSeriesV  setDeviceID(long id);

    int getTickTime()        const;
    int getTimeOffset()      const;
    int getTransmitterLow()  const;
    int getTransmitterHigh() const;

    string getStartCode() const;
    string getStopCode()  const;

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

    void DecodeDatabaseReader(RWDBReader &rdr);

    static string getTableName();

    virtual RWDBStatus Restore();
    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_DV_SERIESV_H__

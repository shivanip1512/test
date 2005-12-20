#pragma warning( disable : 4786)
#ifndef __TBL_DV_MCTIEDPORT_H__
#define __TBL_DV_MCTIEDPORT_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_mctiedport
*
* Date:   2/25/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

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

class IM_EX_CTIYUKONDB CtiTableDeviceMCTIEDPort : public CtiMemDBObject
{
protected:

    LONG      _deviceID;
    string _password;
    INT       _connectedIED,
              _iedScanRate,
              _defaultDataClass,
              _defaultDataOffset,
              _realTimeScan;

private:

public:

    CtiTableDeviceMCTIEDPort();

    CtiTableDeviceMCTIEDPort(const CtiTableDeviceMCTIEDPort& aRef);

    virtual ~CtiTableDeviceMCTIEDPort();

    enum IEDTypes
    {
        InvalidIEDType,
        AlphaPowerPlus,
        LandisGyrS4,
        GeneralElectricKV
    };

    CtiTableDeviceMCTIEDPort& operator=(const CtiTableDeviceMCTIEDPort& aRef);

    long                      getDeviceID() const;
    CtiTableDeviceMCTIEDPort  setDeviceID(long id);

    int                       getIEDType() const;
    int                      &getIEDType();
    CtiTableDeviceMCTIEDPort  setIEDType(IEDTypes type);

    string                 getPassword() const;
    string                &getPassword();
    CtiTableDeviceMCTIEDPort  setPassword(string &password);

    int                       getIEDScanRate() const;
    int                      &getIEDScanRate();
    CtiTableDeviceMCTIEDPort  setIEDScanRate(int scanrate);

    int                       getDefaultDataClass() const;
    int                      &getDefaultDataClass();
    CtiTableDeviceMCTIEDPort  setDefaultDataClass(int dataclass);

    int                       getDefaultDataOffset() const;
    int                      &getDefaultDataOffset();
    CtiTableDeviceMCTIEDPort  setDefaultDataOffset(int dataoffset);

    int                       getRealTimeScanFlag() const;
    int                      &getRealTimeScanFlag();
    CtiTableDeviceMCTIEDPort  setRealTimeScanFlag(int flag);

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

    void DecodeDatabaseReader(RWDBReader &rdr);

    static string getTableName();

    virtual RWDBStatus Restore();
    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_DV_MCTIEDPORT_H__


/*-----------------------------------------------------------------------------*
*
* File:   tbl_gateway_end_device
*
* Class:  CtiTableGatewayEndDevice
* Date:   7/16/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/07/21 22:25:44 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_GATEWAY_END_DEVICE_H__
#define __TBL_GATEWAY_END_DEVICE_H__


#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>

#include <rw\cstring.h>

#include "resolvers.h"
#include "yukon.h"
#include "logger.h"
#include "dbmemobject.h"

#define MAX_SERIAL_NUMBER_LENGTH    30
#define MAX_DATA_VALUE_LENGTH       100

class IM_EX_CTIYUKONDB CtiTableGatewayEndDevice  : public CtiMemDBObject
{
protected:

    RWCString _serialNumber;
    UINT _hardwareType;
    UINT _dataType;
    RWCString _dataValue;


private:

public:

    typedef CtiMemDBObject Inherited;

    CtiTableGatewayEndDevice();

    CtiTableGatewayEndDevice(const CtiTableGatewayEndDevice& aRef);

    virtual ~CtiTableGatewayEndDevice();

    CtiTableGatewayEndDevice& operator=(const CtiTableGatewayEndDevice& aRef);

    CtiTableGatewayEndDevice& setSerialNumber(ULONG sn);
    CtiTableGatewayEndDevice& setSerialNumber(RWCString sn);
    CtiTableGatewayEndDevice& setHardwareType(UINT hwt);
    CtiTableGatewayEndDevice& setDataType(UINT dt);
    CtiTableGatewayEndDevice& setDataValue(RWCString dv);

    RWCString getSerialNumber() const;
    UINT  getHardwareType() const;
    UINT  getDataType() const;
    RWCString getDataValue() const;


    static RWCString getTableName();
    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DumpData();
    virtual void DecodeDatabaseReader(RWDBReader &rdr);


    virtual RWDBStatus Insert();
    virtual RWDBStatus Insert(RWDBConnection &conn);
    virtual RWDBStatus Update();
    virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_GATEWAY_END_DEVICE_H__

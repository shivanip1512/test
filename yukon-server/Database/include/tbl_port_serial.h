/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_serial
*
* Date:   3/29/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_port_serial.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_PORT_SERIAL_H__
#define __TBL_PORT_SERIAL_H__

#include <rw/rwtime.h>

#include "dbaccess.h"
#include "dbmemobject.h"
#include "dlldefs.h"


class IM_EX_CTIYUKONDB CtiTablePortLocalSerial : public CtiMemDBObject
{
protected:

   RWCString     _physicalPort;              // in struct [6];

private:

public:

   CtiTablePortLocalSerial();
   CtiTablePortLocalSerial(const CtiTablePortLocalSerial& aRef);

   virtual ~CtiTablePortLocalSerial();

   CtiTablePortLocalSerial& operator=(const CtiTablePortLocalSerial& aRef);

   RWCString getPhysicalPort() const;
   RWCString& getPhysicalPort();

   CtiTablePortLocalSerial& setPhysicalPort(const RWCString& str );
   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
};
#endif // #ifndef __TBL_PORT_SERIAL_H__

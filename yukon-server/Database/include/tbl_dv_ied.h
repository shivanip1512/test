/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_ied
*
* Date:   2/25/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DV_IED_H__
#define __TBL_DV_IED_H__

#include <limits.h>

#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceIED : public CtiMemDBObject
{
protected:

   LONG           _deviceID;
   string      _password;
   INT            _slaveAddress;

private:

public:

   CtiTableDeviceIED();

   CtiTableDeviceIED(const CtiTableDeviceIED& aRef);

   virtual ~CtiTableDeviceIED();

   CtiTableDeviceIED& operator=(const CtiTableDeviceIED& aRef);

   INT                  getSlaveAddress() const;
   INT&                 getSlaveAddress();
   CtiTableDeviceIED    setSlaveAddress(INT &aInt);

   string            getPassword() const;
   string&           getPassword();
   CtiTableDeviceIED    setPassword(string &aStr);

   void DecodeDatabaseReader(const INT DeviceType, Cti::RowReader &rdr);

   static string getTableName();

   LONG getDeviceID() const;

   CtiTableDeviceIED& setDeviceID( const LONG did);

   virtual bool Insert();
   virtual bool Update();
   virtual bool Delete();

};
#endif // #ifndef __TBL_DV_IED_H__

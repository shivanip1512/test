#ifndef __TBL_DV_ADDRESS_H__
#define __TBL_DV_ADDRESS_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_address
*
* Date:   2002-aug-27
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:49 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>
#include <rw/thr/monitor.h>

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "types.h"
#include "logger.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "desolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceAddress : public CtiMemDBObject
{
protected:

   LONG     _deviceID;
   LONG     _masterAddress,
            _slaveAddress;
   INT      _postdelay;

private:

public:

   CtiTableDeviceAddress();
   CtiTableDeviceAddress(const CtiTableDeviceAddress& aRef);

   virtual ~CtiTableDeviceAddress();

   CtiTableDeviceAddress& operator=(const CtiTableDeviceAddress& aRef);

   LONG getMasterAddress() const;
   void setMasterAddress(LONG a);

   LONG getSlaveAddress() const;
   void setSlaveAddress(LONG a);

   INT  getPostDelay() const;
   void setPostDelay(int d);

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   static std::string getTableName();

   LONG getDeviceID() const;
   CtiTableDeviceAddress& setDeviceID(const LONG did);

   virtual bool Insert();
   virtual bool Update();
   virtual bool Delete();

};
#endif // #ifndef __TBL_DV_ADDRESS_H__

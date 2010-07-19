#ifndef __TBL_DIRECT_H__
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_direct
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_direct.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __TBL_DIRECT_H__

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dbmemobject.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTableDeviceDirectComm : public CtiMemDBObject
{

protected:

   LONG           _deviceID;
   RWMutexLock    DirectCommMux;
   LONG           PortID;

public:

   CtiTableDeviceDirectComm();
   virtual ~CtiTableDeviceDirectComm();
   CtiTableDeviceDirectComm(const CtiTableDeviceDirectComm &aRef);
   CtiTableDeviceDirectComm& operator=(const CtiTableDeviceDirectComm &aRef);

   LONG getPortID() const;
   void setPortID(LONG id);

   LONG getDeviceID() const;
   CtiTableDeviceDirectComm& setDeviceID( const LONG did);

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   static string getTableName();

   virtual bool Insert();
   virtual bool Update();
   virtual bool Delete();
};

#endif


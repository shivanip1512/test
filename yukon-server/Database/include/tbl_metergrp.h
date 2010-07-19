/*-----------------------------------------------------------------------------*
*
* File:   tbl_metergrp
*
* Date:   8/18/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_metergrp.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/10/14 21:25:45 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)

#ifndef __TBL_METERGRP_H__
#define __TBL_METERGRP_H__


#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "yukon.h"
#include "vcomdefs.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTableDeviceMeterGroup : public CtiMemDBObject
{
protected:

   LONG     _deviceID;
   string   _meterNumber;

   static string getTableName();
   long getDeviceID() const;

public:

   CtiTableDeviceMeterGroup();

   CtiTableDeviceMeterGroup(const CtiTableDeviceMeterGroup& aRef);

   virtual ~CtiTableDeviceMeterGroup();

   CtiTableDeviceMeterGroup& operator=(const CtiTableDeviceMeterGroup& aRef);

   string getMeterNumber() const;
   CtiTableDeviceMeterGroup& setMeterNumber( const string &mNum );

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   virtual bool Update();
   virtual bool Insert();
   virtual bool Delete();
};

#endif // #ifndef __TBL_METERGRP_H__


#pragma warning( disable : 4786)
#ifndef __TBL_PT_CONTROL_H__
#define __TBL_PT_CONTROL_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_control
*
* Class:  CtiTablePointControl
* Date:   8/14/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pt_control.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:45 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw\rwtime.h>
#include <rw\cstring.h>
#include <rw\rwtime.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/datetime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "desolvers.h"
#include "yukon.h"

class IM_EX_CTIYUKONDB CtiTablePointControl : public CtiMemDBObject
{
protected:

   INT               _controlOffset;
   INT               _closeTime1;
   INT               _closeTime2;
   RWCString         _stateZeroControl;
   RWCString         _stateOneControl;

private:

public:
   CtiTablePointControl();
   CtiTablePointControl(const CtiTablePointControl& aRef);
   virtual ~CtiTablePointControl();

   CtiTablePointControl& operator=(const CtiTablePointControl& aRef);

   INT  getControlOffset() const;
   INT  getCloseTime1() const;
   INT  getCloseTime2() const;
   const RWCString& getStateZeroControl() const;
   const RWCString& getStateOneControl() const;

   CtiTablePointControl& setControlOffset(INT i);
   CtiTablePointControl& setCloseTime1(INT i);
   CtiTablePointControl& setCloseTime2(INT i);
   CtiTablePointControl& setStateZeroControl(const RWCString& zero);
   CtiTablePointControl& setStateOneControl(const RWCString& one);

   void dump() const;
   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   UINT getStaticTags() const;
   UINT adjustStaticTags(UINT &tags) const;
};
#endif // #ifndef __TBL_PT_CONTROL_H__

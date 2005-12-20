
#pragma warning( disable : 4786)
#ifndef __TBL_PT_STATUS_H__
#define __TBL_PT_STATUS_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_status
*
* Class:  CtiTablePointStatus
* Date:   8/14/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pt_status.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
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

#include "pointtypes.h"

class IM_EX_CTIYUKONDB CtiTablePointStatus : public CtiMemDBObject
{

protected:
   /* Data Elements from Table PointStatus */

   LONG              _pointID;
   INT               _initialState;
   BOOL              _controlInhibit;
   CtiControlType_t  _controlType;
   INT               _controlOffset;
   INT               _closeTime1;
   INT               _closeTime2;
   string         _stateZeroControl;
   string         _stateOneControl;
   INT               _commandTimeout;

private:

public:

   CtiTablePointStatus();
   CtiTablePointStatus(const CtiTablePointStatus& aRef);
   virtual ~CtiTablePointStatus();

   CtiTablePointStatus& operator=(const CtiTablePointStatus& aRef);

   LONG getPointID();
   INT getInitialState() const;
   BOOL getControlInhibit() const;
   CtiControlType_t getControlType()  const;
   INT getControlOffset() const;
   INT getCloseTime1() const;
   INT getCloseTime2() const;
   const string& getStateZeroControl() const;
   const string& getStateOneControl() const;
   INT getCommandTimeout() const;

   BOOL isControlInhibited() const;

   CtiTablePointStatus& setPointID( const LONG ptid );
   CtiTablePointStatus& setInitialState(INT i);
   CtiTablePointStatus& setControlInhibit(const BOOL b = TRUE);
   CtiTablePointStatus& setControlType(CtiControlType_t t);
   CtiTablePointStatus& setControlOffset(INT i);
   CtiTablePointStatus& setCloseTime1(INT i);
   CtiTablePointStatus& setCloseTime2(INT i);
   CtiTablePointStatus& setStateZeroControl(const string& zero);
   CtiTablePointStatus& setStateOneControl(const string& one);
   CtiTablePointStatus& setCommandTimeout(INT i);

   static string getTableName();

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   void DecodeDatabaseReader(RWDBReader &rdr);
   void dump() const;

   UINT getStaticTags() const;
   UINT adjustStaticTags(UINT &tags) const;

   void Update();
};
#endif // #ifndef __TBL_PT_STATUS_H__

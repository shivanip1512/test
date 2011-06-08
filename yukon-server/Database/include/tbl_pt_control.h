
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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "row_reader.h"

#include <limits.h>
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
   std::string         _stateZeroControl;
   std::string         _stateOneControl;

private:

public:
   CtiTablePointControl();
   CtiTablePointControl(const CtiTablePointControl& aRef);
   virtual ~CtiTablePointControl();

   CtiTablePointControl& operator=(const CtiTablePointControl& aRef);

   INT  getControlOffset() const;
   INT  getCloseTime1() const;
   INT  getCloseTime2() const;
   const std::string& getStateZeroControl() const;
   const std::string& getStateOneControl() const;

   CtiTablePointControl& setControlOffset(INT i);
   CtiTablePointControl& setCloseTime1(INT i);
   CtiTablePointControl& setCloseTime2(INT i);
   CtiTablePointControl& setStateZeroControl(const std::string& zero);
   CtiTablePointControl& setStateOneControl(const std::string& one);

   void dump() const;
   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   UINT getStaticTags() const;
   UINT adjustStaticTags(UINT &tags) const;
};
#endif // #ifndef __TBL_PT_CONTROL_H__

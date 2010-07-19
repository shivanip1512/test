#pragma warning( disable : 4786)
#ifndef __TBL_PT_ACCUMHISTORY_H__
#define __TBL_PT_ACCUMHISTORY_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_accumhistory
*
* Class:  CtiTablePointAccumulatorHistory
* Date:   8/16/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pt_accumhistory.h-arc  $
* REVISION     :  $Revision: 1.5.24.2 $
* DATE         :  $Date: 2008/11/18 20:11:29 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include <string>
#include "dbmemobject.h"
#include "row_reader.h"

#include "dlldefs.h"


class IM_EX_CTIYUKONDB CtiTablePointAccumulatorHistory : public CtiMemDBObject
{
protected:

   LONG           _pointID;
   ULONG          _previousPulseCount;
   ULONG          _presentPulseCount;

private:

public:
   CtiTablePointAccumulatorHistory(LONG pid,
                                   ULONG prevpulsecount = 0,
                                   ULONG pulsecount = 0);

   CtiTablePointAccumulatorHistory(const CtiTablePointAccumulatorHistory& aRef);
   virtual ~CtiTablePointAccumulatorHistory();
   CtiTablePointAccumulatorHistory& operator=(const CtiTablePointAccumulatorHistory& aRef);

   bool operator==(const CtiTablePointAccumulatorHistory&) const;

   virtual std::string getTableName() const;

   virtual bool Insert();
   virtual bool Update();
   virtual bool Restore();
   virtual bool Delete();

   virtual void DecodeDatabaseReader(Cti::RowReader& rdr);

   LONG getPointID() const;
   CtiTablePointAccumulatorHistory& setPointID(LONG pointID);

   ULONG getPreviousPulseCount() const;
   CtiTablePointAccumulatorHistory& setPreviousPulseCount(ULONG pc);

   ULONG getPresentPulseCount() const;
   CtiTablePointAccumulatorHistory& setPresentPulseCount(ULONG pc);
};


#endif // #ifndef __TBL_PT_ACCUMHISTORY_H__

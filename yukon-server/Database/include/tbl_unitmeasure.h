#pragma warning( disable : 4786)
#ifndef __TBL_UNITMEASURE_H__
#define __TBL_UNITMEASURE_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_unitmeasure
*
* Class:  CtiTableUnitMeasure;
* Date:   04/29/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pt_analog.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2007/09/28 15:43:05 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
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


class IM_EX_CTIYUKONDB CtiTableUnitMeasure : public CtiMemDBObject
{
protected:

   //string   _uomName;
   int         _calcType;
   //string   _longName;
   //string   _formula;

private:

public:
   CtiTableUnitMeasure();

   CtiTableUnitMeasure(const CtiTableUnitMeasure& aRef);
   virtual ~CtiTableUnitMeasure();

   CtiTableUnitMeasure& operator=(const CtiTableUnitMeasure& aRef);

   void DecodeDatabaseReader(Cti::RowReader &rdr);
   void dump() const;

   //string getUOMName() const;
   int       getCalcType() const;
   //string getLongName() const;
   //string getFormula() const;
};
#endif // #ifndef __TBL_UNITMEASURE_H__


#pragma warning( disable : 4786)
#ifndef __TBL_PT_CALC_H__
#define __TBL_PT_CALC_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_calc
*
* Class:  CtiTablePointCalculated
* Date:   8/14/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pt_calc.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

class IM_EX_CTIYUKONDB CtiTablePointCalculated : public CtiMemDBObject
{

protected:

private:

public:

   CtiTablePointCalculated();

   CtiTablePointCalculated(const CtiTablePointCalculated& aRef);

   virtual ~CtiTablePointCalculated();

   CtiTablePointCalculated& operator=(const CtiTablePointCalculated& aRef);
};
#endif // #ifndef __TBL_PT_CALC_H__

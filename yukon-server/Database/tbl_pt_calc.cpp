#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_calc
*
* Date:   8/28/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_calc.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "tbl_pt_calc.h"

CtiTablePointCalculated::CtiTablePointCalculated()
{
}

CtiTablePointCalculated::CtiTablePointCalculated(const CtiTablePointCalculated& aRef)
{
   *this = aRef;
}

virtual CtiTablePointCalculated::~CtiTablePointCalculated()
{
}

CtiTablePointCalculated& CtiTablePointCalculated::operator=(const CtiTablePointCalculated& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}


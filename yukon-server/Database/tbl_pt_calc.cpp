
#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:35 $
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


/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_zero_one
*
* Date:   9/16/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/03/13 19:35:44 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include "std_ansi_tbl_zero_one.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableZeroOne::CtiAnsiTableZeroOne( BYTE *dataBlob )
{
/*
   manufacturer[4];
   ed_model[8];
   hw_version_number;
   hw_revision_number;
   fw_version_number;
   fw_revision_number;

   union
   {
      BCD       bcd_sn[8];
      char      char_sn[16];
      UINT64    ll_sn;

   }mfg_serial_number;

*/
   memcpy( manufacturer, dataBlob, 32 /*bytes for the above parts*/ );

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableZeroOne::~CtiAnsiTableZeroOne()
{

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableZeroOne& CtiAnsiTableZeroOne::operator=(const CtiAnsiTableZeroOne& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;

}


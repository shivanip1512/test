/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_zero
*
* Date:   9/16/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/03/13 19:35:43 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include "std_ansi_tbl_one_zero.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneZero::CtiAnsiTableOneZero( BYTE *dataBlob )
{
//   source_record = new SOURCE_RCD;

//   memcpy( source_record, dataBlob, sizeof( SOURCE_RCD ) );
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneZero::~CtiAnsiTableOneZero()
{
//   delete source_record;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneZero& CtiAnsiTableOneZero::operator=(const CtiAnsiTableOneZero& aRef)
{
  if(this != &aRef)
  {
  }
  return *this;
}

//=========================================================================================================================================
//this particular entry appears to exist in all the end devices... some of the entries in this table tell you if they exist in the end
//device
//=========================================================================================================================================

int CtiAnsiTableOneZero::getNumberUOMEntries( void )
{
//   return source_record->nbr_uom_entries;
   return 1;
}


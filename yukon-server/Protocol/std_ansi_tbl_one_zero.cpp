
#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/11/15 20:36:21 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

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


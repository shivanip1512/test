
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_five_five
*
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/11/15 20:36:19 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "std_ansi_tbl_five_five.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableFiveFive::CtiAnsiTableFiveFive( BYTE *dataBlob )
{
   int bytes = sizeof( TABLE_55_CLOCK_STATE );

   _tableFiveFive = new TABLE_55_CLOCK_STATE;

   if( _tableFiveFive != NULL )
      memcpy( _tableFiveFive, dataBlob, bytes );
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableFiveFive::~CtiAnsiTableFiveFive()
{
   delete _tableFiveFive;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableFiveFive& CtiAnsiTableFiveFive::operator=(const CtiAnsiTableFiveFive& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}


/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_five_two
*
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/03/13 19:35:42 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include "std_ansi_tbl_five_two.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableFiveTwo::CtiAnsiTableFiveTwo( BYTE *dataBlob )
{
   clock_table = new CLOCK_STATE_RCD;

   if( clock_table != NULL )
      memcpy( clock_table, dataBlob, sizeof( CLOCK_STATE_RCD ));
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableFiveTwo::~CtiAnsiTableFiveTwo()
{
   delete clock_table;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableFiveTwo& CtiAnsiTableFiveTwo::operator=(const CtiAnsiTableFiveTwo& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}



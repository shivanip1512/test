
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_five_two
*
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_five_two.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/04/25 15:09:53 $
*
*    History: 
      $Log: std_ansi_tbl_five_two.cpp,v $
      Revision 1.3  2003/04/25 15:09:53  dsutton
      Standard ansi tables all inherit from a base table


* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

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



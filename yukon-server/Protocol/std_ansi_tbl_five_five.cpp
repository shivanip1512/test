
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
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_five_five.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/04/25 15:09:53 $
*
*    History: 
      $Log: std_ansi_tbl_five_five.cpp,v $
      Revision 1.3  2003/04/25 15:09:53  dsutton
      Standard ansi tables all inherit from a base table


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


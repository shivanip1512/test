#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_zero
*
* Date:   9/16/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_one_zero.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:58 $
*    History: 
      $Log: std_ansi_tbl_one_zero.cpp,v $
      Revision 1.4  2005/02/10 23:23:58  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "std_ansi_tbl_one_zero.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneZero::CtiAnsiTableOneZero( BYTE *dataBlob )
{
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneZero::~CtiAnsiTableOneZero()
{
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
   return 1;
}


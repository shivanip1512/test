
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_six
*
* Date:   9/19/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_one_six.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/04/25 15:09:53 $
*    History: 
      $Log: std_ansi_tbl_one_six.cpp,v $
      Revision 1.3  2003/04/25 15:09:53  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "std_ansi_tbl_one_six.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneSix::CtiAnsiTableOneSix( BYTE *dataBlob, int nbr_constants )
{
   int   index;

   _source_link = new SOURCE_LINK_BFLD[nbr_constants];

   for( index = 0; index < nbr_constants; index++ )
   {
      memcpy(( void *)&_source_link[index], dataBlob, sizeof( SOURCE_LINK_BFLD ));
      dataBlob += sizeof( SOURCE_LINK_BFLD );
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneSix::~CtiAnsiTableOneSix()
{
   delete []_source_link;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneSix& CtiAnsiTableOneSix::operator=(const CtiAnsiTableOneSix& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}


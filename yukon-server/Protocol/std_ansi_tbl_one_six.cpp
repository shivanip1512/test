/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_six
*
* Date:   9/19/2002
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


#include "std_ansi_tbl_one_six.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneSix::CtiAnsiTableOneSix( BYTE *dataBlob, int nbr_constants )
{
   int   index;

   _source_link = new SOURCE_LINK_BFLD[nbr_constants];

   for( index = 0; index < nbr_constants; index++ )
   {
      memcpy(( void *)&_source_link[index], dataBlob, sizeof( _source_link ));
      dataBlob += sizeof( _source_link );
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


/*-----------------------------------------------------------------------------*
*
* File:   ansi_application
*
* Date:   6/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/03/13 19:35:35 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include <rw/cstring.h>

#include "guard.h"
#include "logger.h"
#include "ansi_application.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiANSIApplication::CtiANSIApplication()
{
   _storage = CTIDBG_new BYTE[1024];
   _ptr = _storage;
   _totalBytesRec = 0;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiANSIApplication::~CtiANSIApplication()
{
   delete _storage;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiANSIApplication::generate( CtiXfer &xfer )
{
   return( getDatalinkLayer().generate( xfer ) );
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIApplication::passRequest( BYTE *request, int len )
{
   getDatalinkLayer().passRequest( request, len );
}

//=========================================================================================================================================
//the tabledata buff will be filled when the layer below has actually gotten data we requested
//=========================================================================================================================================

bool CtiANSIApplication::decode( CtiXfer &xfer )
{
   BYTE  _realLiveTableData[512];
   bool  done = false;
   int   received = 0;

   memset( _realLiveTableData, NULL, sizeof( _realLiveTableData ) );

   done = getDatalinkLayer().decode( xfer, _realLiveTableData, received );

   if(( _storage != NULL ) && ( received < 1023) &&( received != 0 ))
   {
      _totalBytesRec += received;
      memcpy( _storage, _realLiveTableData, received );
      _ptr += received;
   }

   return( done );
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiANSIApplication::pullData( BYTE *table )
{
   int temp = 0;

   if( table != NULL )
      memcpy( table, _storage, _totalBytesRec );

   memset( _storage, NULL, sizeof( _storage ) );
   temp = _totalBytesRec;
   _totalBytesRec = 0;

   return( temp );
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiANSIDatalink &CtiANSIApplication::getDatalinkLayer( void )
{
   return _datalinkLayer;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiANSIApplication::getDone( void )
{
   return getDatalinkLayer().getDone();
}

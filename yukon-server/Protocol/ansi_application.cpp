
#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/11/15 14:08:03 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

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

   if( ( _storage != NULL ) && ( received != 0 ) )
   {
      _totalBytesRec += received;
      memcpy( _storage, _realLiveTableData, received );
      _ptr += received;
   }

   return( done );
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIApplication::pullData( BYTE *table )
{
   memcpy( table, _storage, _totalBytesRec );

   memset( _storage, NULL, sizeof( _storage ) );
   _totalBytesRec = 0;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiANSIDatalink &CtiANSIApplication::getDatalinkLayer( void )
{
   return _datalinkLayer;
}

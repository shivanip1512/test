
#pragma warning( disable : 4786)

#ifndef __ANSI_APPLICATION_H__
#define __ANSI_APPLICATION_H__

/*-----------------------------------------------------------------------------*
*
* File:   ansi_application
*
* Date:   6/20/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/09/03 17:27:49 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "ansi_datalink.h"

class IM_EX_PROT CtiANSIApplication
{
   public:

      CtiANSIApplication();
      ~CtiANSIApplication();

      bool generate( CtiXfer &xfer );
      void passRequest( BYTE *request, int len );

      bool decode( CtiXfer &xfer );

      void pullData( BYTE *table );
      CtiANSIDatalink &getDatalinkLayer( void );

   protected:

   private:

      int               _retries;
      bool              _ackPart;
      bool              _countPart;
      bool              _messagePart;
      BYTE              *_storage;
      CtiANSIDatalink   _datalinkLayer;
      BYTE              *_ptr;
      int               _totalBytesRec;
};


#endif // #ifndef __ANSI_APPLICATION_H__

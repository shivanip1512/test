
#pragma warning( disable : 4786)
#ifndef __TRANSDATA_DATALINK_H__
#define __TRANSDATA_DATALINK_H__

/*---------------------------------------------------------------------------------*
*
* File:   transdata_datalink
*
* Class:
* Date:   7/22/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/08/28 14:22:57 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "xfer.h"

class IM_EX_PROT CtiTransdataDatalink
{
   enum
   {
      working     = 0,
      failed
   };

   public:

      CtiTransdataDatalink();
      ~CtiTransdataDatalink();

      void buildMsg( CtiXfer &xfer );
      bool readMsg( CtiXfer &xfer, int status );
      bool isTransactionComplete( void );

      void retreiveData( BYTE *data, int *bytes );

      int getError( void );
      void setError( void );

   protected:

   private:

      int      _failCount;
      int      _error;
      int      _ioState;

      bool     _finished;

      BYTE     *_storage;

      ULONG    _totalBytesExpected;
      ULONG    _totalBytesReceived;
      ULONG    _transBytesExpected;
};

#endif // #ifndef __TRANSDATA_DATALINK_H__

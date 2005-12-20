
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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2005/12/20 17:20:02 $
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

      string buildMsg( string command, string wantToGet );
      bool readMsg( CtiXfer &xfer, int status );
      bool isTransactionComplete( void );

      void retreiveData( BYTE *data, int *bytes );

      int getError( void );
      void setError( void );

      void destroy( void );
      void reinitalize( void );

   protected:

   private:

      enum
      {
         Storage_size   = 4500
      };

      int         _failCount;
      int         _error;
      int         _offset;
      int         _index;

      bool        _finished;
      bool        _firstTime;

      BYTE        *_storage;

      ULONG       _bytesReceived;
      ULONG       _bytesExpected;

      string   _received;
      string   _lookFor;
};

#endif // #ifndef __TRANSDATA_DATALINK_H__


#pragma warning( disable : 4786)
#ifndef __PROT_TRANSDATA_H__
#define __PROT_TRANSDATA_H__

/*---------------------------------------------------------------------------------*
*
* File:   prot_transdata
*
* Class:
* Date:   7/16/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/10/06 15:19:00 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <windows.h>
#include <rw\cstring.h>
#include "transdata_application.h"
#include "transdata_data.h"
#include "xfer.h"

class IM_EX_PROT CtiProtocolTransdata
{
   public:

      CtiProtocolTransdata();
      virtual ~CtiProtocolTransdata();

      bool generate( CtiXfer &xfer );
      bool decode( CtiXfer &xfer, int status );

      int sendCommResult( INMESS *InMessage );
      int recvOutbound( OUTMESS  *OutMessage );

      bool isTransactionComplete( void );
      void injectData( RWCString str );
      void reinitalize( void );
      void destroyMe( void );

      vector<CtiTransdataData *> resultDecode( INMESS *InMessage );

   protected:

   private:

      bool                          _finished;
      bool                          _weHaveData;

      BYTE                          *_storage;

      int                           _dataSize;
      int                           _numBytes;
      CtiTransdataApplication       _application;


      vector<CtiTransdataData *>    _transVector;
      CtiTransdataData              *_converted;
};
#endif // #ifndef __PROT_TRANSDATA_H__

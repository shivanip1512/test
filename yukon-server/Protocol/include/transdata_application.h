
#pragma warning( disable : 4786)
#ifndef __TRANSDATA_APPLICATION_H__
#define __TRANSDATA_APPLICATION_H__

/*---------------------------------------------------------------------------------*
*
* File:   transdata_application
*
* Class:
* Date:   7/22/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/08/06 19:51:37 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "xfer.h"
#include "transdata_datalink.h"

class IM_EX_PROT CtiTransdataApplication
{
   enum
   {
      doLogOn = 0,
      doTalk,
      doLogOff
   };

   public:

      CtiTransdataApplication();
      ~CtiTransdataApplication();

      bool logOn( CtiXfer &xfer );
      bool logOff( CtiXfer &xfer );
      bool generate( CtiXfer &xfer );
      bool decode( CtiXfer &xfer, int status );

      bool isTransactionComplete( void );
      void injectData( RWCString str );

      void setNextState( void );

      CtiTransdataDatalink & getDataLinkLayer( void );

   protected:

   private:

      CtiTransdataDatalink _datalinkLayer;

      int _lastState;
};

#endif // #ifndef __TRANSDATA_APPLICATION_H__



#pragma warning( disable : 4786)

#ifndef __FMU_APPLICATION_H__
#define __FMU_APPLICATION_H__

/*-----------------------------------------------------------------------------*
*
* File:   fmu_application
*
* Date:   10/09/2006
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/ansi_application.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/01/26 20:20:19 $
*    History: 
      $Log: fmu_application.h,v $
      Revision 1.1  2007/01/26 20:20:19  jrichter
      FMU stuff for jess....


*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dllbase.h"
#include "fmu_datalink.h"


#define UINT32             __int32
#define UINT16             __int16
#define UINT8               __int8
#define MAXRETRIES        5

class IM_EX_PROT CtiFMUApplication
{
   public:

      CtiFMUApplication();
      ~CtiFMUApplication();


      typedef enum
      {
          ack         = 0x00,
          nak,
          commSync,
          unSolMsg,
          loCom,
          timeSyncReq,
          timeRead,
          timeRsp,
          dataReqCmd,
          dataReqRsp,
          resetMsgDataLog,
          extDevDirCmd,
          extDevDirRsp
      } FMU_COMMAND;


      void init( void );
      void destroyMe( void );
      void reinitialize( void );
      bool generate(  BYTE *packetPtr, USHORT cmd, USHORT count, INT seq, ULONG address  );
      void initializeDataLog( short aID, int aOffset, unsigned int aBytesExpected, BYTE aType, BYTE aOperation );
      BYTE* getCurrentTable( void );
      void buildAllUnreportedMsgsRequest(UCHAR *abuf, INT *buflen, INT xmitter);


    bool decode( CtiXfer &xfer, int aCommStatus  );
    void terminateSession( void );
    bool analyzePacket();
    bool checkResponse( BYTE aResponseByte);
    void identificationData(BYTE * );
    bool areThereMorePackets( void );

    bool isReadComplete( void );
    bool isReadFailed( void );

    CtiFMUDatalink &getDatalinkLayer( void );


    CtiFMUApplication &setRetries( int trysLeft );
    int getRetries( void );

    static const CHAR * FMU_DEBUGLEVEL;
    bool getFMUDebugLevel(int mask);

   protected:

   private:

       CtiFMUDatalink   _datalinkLayer;

       int              _prot_version;
       BYTE              *_currentTable;
       int               _totalBytesInTable;
       int               _initialOffset;
       bool             _tableComplete;


       short            _currentTableID;
       int              _currentTableOffset;
       unsigned int     _currentBytesExpected;
       BYTE             _currentType;
       BYTE             _currentOperation;

       //TBL_IDB_BFLD      _currentProcBfld;
       BYTE              *_parmPtr;
       BYTE              _wrSeqNbr;
       USHORT            _wrDataSize;


       bool        _readComplete;
       bool        _readFailed;
       int         _retries;

       BYTEUSHORT _maxPktSize;
       BYTE _maxNbrPkts;

};

#endif // #ifndef __FMU_APPLICATION_H__

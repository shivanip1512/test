
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
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/ansi_application.h-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2005/06/16 19:18:00 $
*    History: 
      $Log: ansi_application.h,v $
      Revision 1.11  2005/06/16 19:18:00  jrichter
      Sync ANSI code with 3.1 branch!

      Revision 1.10  2005/03/14 21:44:16  jrichter
      updated with present value regs, batterylife info, corrected quals, multipliers/offsets, corrected single precision float define, modifed for commander commands, added demand reset

      Revision 1.9  2005/02/10 23:23:58  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.8  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.7  2005/01/03 23:07:15  jrichter
      checking into 3.1, for use at columbia to test sentinel

      Revision 1.6  2004/12/10 21:58:42  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.5  2004/09/30 21:37:19  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.4  2003/04/25 15:13:45  dsutton
      Update of the base protocol pieces taking into account the manufacturer
      tables, etc.  New starting point

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dllbase.h"
#include "ansi_datalink.h"

#define MAXRETRIES        5
#define ANSI_C12_18        0x00
#define ANSI_C12_21        0x02

class IM_EX_PROT CtiANSIApplication
{
   public:

      CtiANSIApplication();
      ~CtiANSIApplication();


      typedef enum
      {
         ok    = 0x00,
         err,
         sns,
         isc,
         onp,
         iar,
         bsy,
         dnr,
         dlk,
         rno,
         isss

      } ANSI_ERR;


      typedef enum
      {
         ident         = 0x20,
         term,
         discon,
         full_read      = 0x30,
         pread_index1,
         pread_index2,
         pread_index3,
         pread_index4,
         pread_index5,
         pread_index6,
         pread_index7,
         pread_index8,
         pread_index9,
         pread_default  = 0x3e,
         pread_offset,
         full_write     = 0x40,
         pwrite_index1,
         pwrite_index2,
         pwrite_index3,
         pwrite_index4,
         pwrite_index5,
         pwrite_index6,
         pwrite_index7,
         pwrite_index8,
         pwrite_index9,
         pwrite_offset  = 0x4f,
         logon          = 0x50,
         security,
         logoff,
         authenticate,
         negotiate_no_baud = 0x60,
         negotiate1,
         negotiate2,
         negotiate3,
         negotiate4,
         negotiate5,
         negotiate6,
         negotiate7,
         negotiate8,
         negotiate9,
         negotiate10,
         negotiate11,
         wait              = 0x70,
         timing_setup

      } ANSI_SERVICE;

      typedef enum  
      {
       identified = 0,
       negotiated,
       timingSet,
       loggedOn,
       secured,
       authenticated,
       request,
       loggedOff,
       terminated,
       disconnected,
       passThrough,
       waitState
      } ANSI_STATES;

      typedef enum
      {
          kv = 0,
          kv2,
          sentinel

      } ANSI_DEVICE_TYPE;


      void init( void );
      void destroyMe( void );
      void reinitialize( void );
      bool generate( CtiXfer &xfer );
      void initializeTableRequest( short aID, int aOffset, unsigned int aBytesExpected, BYTE aType, BYTE aOperation );
      BYTE* getCurrentTable( void );


      bool decode( CtiXfer &xfer, int aCommStatus  );
    void terminateSession( void );
    bool analyzePacket();
    ANSI_STATES getNextState( ANSI_STATES current );
    bool checkResponse( BYTE aResponseByte);
    void identificationData(BYTE * );
    bool areThereMorePackets( void );

    bool isReadComplete( void );
    bool isReadFailed( void );

      CtiANSIDatalink &getDatalinkLayer( void );

        bool isTableComplete( void );
        CtiANSIApplication &setTableComplete( bool aFlag );

    CtiANSIApplication &setRetries( int trysLeft );
    int getRetries( void );
    void setLPDataMode( bool value, int sizeOfLpTable );

    void populateParmPtr(BYTE *value, int size);
    void setProcBfld( TBL_IDB_BFLD value);
    void setWriteSeqNbr( BYTE seqNbr );
    void setProcDataSize( USHORT dataSize );
    void setPassword( BYTE *password);
    void setAnsiDeviceType(BYTE devType);
    BYTE getAnsiDeviceType();
    void setFWVersionNumber(BYTE fwVersionNumber);
    BYTE getFWVersionNumber();
    RWCString getMeterTypeString();
    
    int encryptDataMethod();


    static const CHAR * ANSI_DEBUGLEVEL;
    bool getANSIDebugLevel(int mask);

   protected:

   private:

       ANSI_STATES           _currentState;
       ANSI_STATES           _requestedState;
       CtiANSIDatalink   _datalinkLayer;

       int              _prot_version;
       BYTE              *_currentTable;
       int               _totalBytesInTable;
       int               _initialOffset;

       /* JULIE TEMP */
       bool _lpMode;
       BYTE *_lpTempBigTable;
       int _sizeOfLpTable;

       /* END JULIE TEMP */

       bool             _tableComplete;


       short                  _currentTableID;
       int                  _currentTableOffset;
       unsigned int        _currentBytesExpected;
       BYTE                  _currentType;
       BYTE                  _currentOperation;

       TBL_IDB_BFLD         _currentProcBfld;
       BYTE                 *_parmPtr;
       BYTE                _wrSeqNbr;
       USHORT              _wrDataSize;

       // if authentication is supported
       bool        _authenticate;
       BYTE        _authenticationType;
       BYTE        _algorithmID;
       BYTE        _algorithmValue;
       BYTE        _authTicketLength;
       BYTE        *_authTicket;
       BYTE        *_iniAuthVector;

       bool        _readComplete;
       bool        _readFailed;
       int         _retries;


       BYTE _securityPassword[20];   
       int _negotiateRetry;
       ANSI_DEVICE_TYPE _ansiDeviceType;
       BYTE _fwVersionNumber;

       BYTEUSHORT _maxPktSize;
       BYTE _maxNbrPkts;
       BYTE _negBaudRate;

       static const RWCString KVmeter;
       static const RWCString KV2meter;
       static const RWCString SENTINELmeter;

};

#endif // #ifndef __ANSI_APPLICATION_H__


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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/04/25 15:13:45 $
*    History: 
      $Log: ansi_application.h,v $
      Revision 1.4  2003/04/25 15:13:45  dsutton
      Update of the base protocol pieces taking into account the manufacturer
      tables, etc.  New starting point

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

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
       passThrough
      } ANSI_STATES;


      void init( void );
      void destroyMe( void );
      void reinitialize( void );
      bool generate( CtiXfer &xfer );
      void initializeTableRequest( int aID, int aOffset, int aBytesExpected, int aType, int aOperation );
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

   protected:

   private:

       ANSI_STATES           _currentState;
       ANSI_STATES           _requestedState;
       CtiANSIDatalink   _datalinkLayer;

       int              _prot_version;
       BYTE              *_currentTable;
       int               _totalBytesInTable;

       bool             _tableComplete;


       int                  _currentTableID;
       int                  _currentTableOffset;
       int                  _currentBytesExpected;
       int                  _currentType;
       int                  _currentOperation;

       // if authentication is supported
       bool        _authenticate;
       BYTE        _authenticationType;
       BYTE        _algorithmID;
       BYTE        _algorithmValue;

       bool        _readComplete;
       bool        _readFailed;
       int         _retries;
};


#endif // #ifndef __ANSI_APPLICATION_H__

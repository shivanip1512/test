/*-----------------------------------------------------------------------------*
*
* File:   ansi_datalink
*
* Class:
* Date:   6/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/03/13 19:35:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __ANSI_DATALINK_H__
#define __ANSI_DATALINK_H__
#pragma warning( disable : 4786)


#include "xfer.h"

#define ANSI_EOL           0x00
#define ANSI_TERMINATE     0x21
#define ANSI_DISCONNECT    0x22
#define ANSI_STP           0xee
#define ANSI_ACK           0x06
#define ANSI_NAK           0x15
#define ANsI_RESERVED      0x00
#define ANSI_COUNT         0
#define ANSI_MSG           1
#define ANSI_C12_18        0x00
#define ANSI_C12_21        0x02
#define MAX_RETRIES        30
#define HEADER_LEN         6

class IM_EX_PROT CtiANSIDatalink
{
   public:

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

      enum States
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
         disconnected
      };

      enum DecodePos
      {
         ack = 0,
         header,
         data
      };

      //duplication of what's on the scanner side FIXME
      struct WANTS_HEADER
      {
         unsigned long  lastLoadProfileTime;
         int            numTablesRequested;
         int            command;
      };

      //this one's usable on both sides
      struct ANSI_TABLE_WANTS
      {
         int   tableID;
         int   tableOffset;
         int   bytesExpected;
      };

      CtiANSIDatalink();
      ~CtiANSIDatalink();

      bool generate( CtiXfer &xfer );
      void passRequest( BYTE *request, int len );
      bool decode( CtiXfer &xfer, BYTE *dataPtr, int &recieved );
      void assemblePacket( BYTE *packetPtr, BYTE *dataPtr, USHORT count,/* int cntl, */int seq );
      bool disassemblePacket( BYTE *packetPtr, ULONG &retrieve, ULONG &send, ULONG arrived );

      //generate side
      void identify( CtiXfer &xfer );
      void negotiate( CtiXfer &xfer );
      void timing( CtiXfer &xfer );
      void logOn( CtiXfer &xfer );
      void secure( CtiXfer &xfer );
      void authenticate( CtiXfer &xfer );
      void sendRequest( CtiXfer &xfer );
      void logOff( CtiXfer &xfer );
      void terminate( CtiXfer &xfer );
      void disconnect( CtiXfer &xfer );

      void doAck( CtiXfer &xfer );
      void read( BYTE *ptr );
      void write( BYTE *ptr );

//      void wait( BYTE *ptr );
//      void terminate( BYTE *ptr );
//      bool decipherResponse( BYTE *ptr, BYTE *msg );
      bool decipherResponse( BYTE *ptr );

      bool getPacketPart( void );
      void setPacketPart( bool isPart );

      bool getPacketFirst( void );
      void setPacketFirst( bool isFirst );

      bool getDone( void );
      void setDone( bool weDone );

      int getSequence( void );
      void setSequence( int seq );

      ULONG getExpectedBytes( void );
      void setExpectedBytes( int ULONG );

      USHORT getDataLen( void );
      void setDataLen( USHORT count );

      void setFailed( bool fail );
      bool getFailed( void );

      int getNextState( States current );

      void setRetries( int trysLeft );
      int getRetries( void );

      //decode side
      void decodeHeader( BYTE *packet );
      void decodeData( BYTE *packet, ULONG bytes );
      void identificationData( BYTE *packet );
      void negotiateData( BYTE *packet );
      void timingData( BYTE *packet );
      void logOnData( BYTE *packet );
      void authenticateData( BYTE *packet );
      void processData( BYTE *packet, ULONG bytes );
      void logOffData( BYTE *packet );
      void secureData( BYTE *packet );
      void terminateData( BYTE *packet );
      void disconnectData( BYTE *packet );

      bool checkCRC( void );

      unsigned short crc16( unsigned char octet, unsigned short crc );
      unsigned short crc( int size, unsigned char *packet );

   protected:

   private:


      ANSI_TABLE_WANTS     *_tables;
      WANTS_HEADER         *_header;


      ULONG       _bytesWeExpect;
      DecodePos   _currentPos;
      DecodePos   _previousPos;
      States      _currentState;
      States      _previousState;
      int         _sequence;
      int         _tempMsgLength;
      int         _retries;
      int         _tableIndex;
      int         _prot_version;
      USHORT      _lengthOfData;
      BYTE        *_tempMsgStorage;
      BYTE        _authenticationType;
      BYTE        _algorithmID;
      BYTE        _algorithmValue;
      BYTE        *_whatsRequested;
      bool        _multiPacketPart;
      bool        _multiPacketFirst;
      bool        _toggle;
      bool        _didFail;
      bool        _authenticate;
      bool        _sendAck;
      bool        _connected;
      bool        _ready;
      bool        _allDone;

      //tester
      BYTE        *_ptrFromAppLayer;
      int         _bytes;
      int         _byteTracker;

      int         _PRETENDER;
};

#endif // #ifndef __ANSI_DATALINK_H__


#pragma warning( disable : 4786)
#ifndef __ANSI_DATALINK_H__
#define __ANSI_DATALINK_H__

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
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/ansi_datalink.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/04/25 15:13:45 $
*    History: 
      $Log: ansi_datalink.h,v $
      Revision 1.4  2003/04/25 15:13:45  dsutton
      Update of the base protocol pieces taking into account the manufacturer
      tables, etc.  New starting point

*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

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
#define HEADER_LEN         6


class IM_EX_PROT CtiANSIDatalink
{
   public:

      enum DecodePos
      {
         ack = 0,
         header,
         data,
         sendAck
      };


      //duplication of what's on the scanner side FIXME

      CtiANSIDatalink();
      ~CtiANSIDatalink();

      // generate packets
      void buildIdentify( BYTE aServiceCode, CtiXfer &xfer );
      void buildNegotiate( BYTE aServiceCode, CtiXfer &xfer );
      void buildTiming( BYTE aServiceCode, CtiXfer &xfer );
      void buildLogOn( BYTE aServiceCode, CtiXfer &xfer );
      void buildSecure( BYTE aServiceCode, CtiXfer &xfer );
      void buildAuthenticate( BYTE aServiceCode, CtiXfer &xfer );
      void buildTableRequest( CtiXfer &xfer, int aTableID, int aOperation, int aOffset );
      void buildLogOff( BYTE aServiceCode, CtiXfer &xfer );
      void buildTerminate( BYTE aServiceCode, CtiXfer &xfer );
      void buildDisconnect( BYTE aServiceCode, CtiXfer &xfer );
        void buildAck( CtiXfer &xfer );

      bool continueBuildingPacket( CtiXfer &xfer, int aCommStatus );
    void initializeForNewPacket( void );

      // getters setters
    bool isPacketComplete( void );
    CtiANSIDatalink& setPacketComplete( bool aFlag=true );

    bool isCRCvalid( void );

    CtiANSIDatalink &setPacketFailed( bool failed );
    bool isPacketFailed( void );

    BYTE *getCurrentPacket( void );
    int getPacketBytesReceived( void );




      void init( void );
      void destroyMe( void );
      void reinitialize( void );
      void assemblePacket( BYTE *packetPtr, BYTE *dataPtr, USHORT count,/* int cntl, */int seq );
      void decodePacketHeader( void );

      void doAck( CtiXfer &xfer );
      void read( BYTE *ptr );
      void write( BYTE *ptr );

      bool getPacketPart( void );
      void setPacketPart( bool isPart );

      bool getPacketFirst( void );
      void setPacketFirst( bool isFirst );

      int getSequence( void );
      void setSequence( int seq );

      ULONG getExpectedBytes( void );
      void setExpectedBytes( int ULONG );

      unsigned short crc16( unsigned char octet, unsigned short crc );
      unsigned short crc( int size, unsigned char *packet );

   protected:

   private:

      int                   _packetBytesReceived;
      BYTE                 *_currentPacket;
      bool                  _packetComplete;

      DecodePos   _currentPos;
      DecodePos   _previousPos;
      ULONG       _bytesWeExpect;


      int         _sequence;
      bool        _multiPacketPart;
      bool        _multiPacketFirst;
      bool        _toggle;
};

#endif // #ifndef __ANSI_DATALINK_H__

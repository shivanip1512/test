

#pragma warning( disable : 4786)
#ifndef __FMU_DATALINK_H__
#define __FMU_DATALINK_H__

/*-----------------------------------------------------------------------------*
*
* File:   fmu_datalink
*
* Class:
* Date:  10/09/2006
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/fmu_datalink.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/01/26 20:20:19 $
*    History: 
      $Log: fmu_datalink.h,v $
      Revision 1.1  2007/01/26 20:20:19  jrichter
      FMU stuff for jess....


*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "dllbase.h"
#include "xfer.h"

#define FMU_STP        0xe7
#define FMU_HEADER_LEN     8

class IM_EX_PROT CtiFMUDatalink
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

      CtiFMUDatalink();
      ~CtiFMUDatalink();

      // generate packets
          void buildMessage( BYTE aServiceCode, CtiXfer &xfer );
      void buildAck( CtiXfer &xfer );

      bool continueBuildingPacket( CtiXfer &xfer, int aCommStatus );
    void initializeForNewPacket( void );

      // getters setters
    bool isPacketComplete( void );
    CtiFMUDatalink& setPacketComplete( bool aFlag=true );

    bool isCRCvalid( void );

    CtiFMUDatalink &setPacketFailed( bool failed );
    bool isPacketFailed( void );

    BYTE *getCurrentPacket( void );
    int getPacketBytesReceived( void );
    bool getToggle(void);

    void toggleToggle(void);


      void init( void );
      void destroyMe( void );
      void reinitialize( void );
      void assemblePacket( BYTE *packetPtr, BYTE *dataPtr, USHORT cmd, USHORT count, INT seq, ULONG address);
      void decodePacketHeader( void );

      void doAck( CtiXfer &xfer );
      void read( BYTE *ptr );
      void write( BYTE *ptr );

      bool getPacketPart( void );
      void setPacketPart( bool isPart );

      bool getPacketFirst( void );
      void setPacketFirst( bool isFirst );

      BYTE getSequence( void );
      void setSequence( BYTE seq );

      ULONG getExpectedBytes( void );
      void setExpectedBytes( int ULONG );

      unsigned short crc16( unsigned char octet, unsigned short crc );
      unsigned short crc( int size, unsigned char *packet );

      void setIdentityByte(BYTE identityByte);
      BYTE getIdentityByte(void);

      void setFMUAddress(ULONG address);
      ULONG getFMUAddress(void);

   protected:

   private:

      int                   _packetBytesReceived;
      BYTE                 *_currentPacket;
      bool                  _packetComplete;

      DecodePos   _currentPos;
      DecodePos   _previousPos;
      ULONG       _bytesWeExpect;

      BYTE        _startByte;
      ULONG       _fmuAddress;
      BYTE        _sequence;
      BYTE        _cmdRspType;
      BYTE        _dataLength;
      BYTE        *_data;
      

      bool        _multiPacketPart;
      bool        _multiPacketFirst;
      bool        _toggle;
      BYTE        _identityByte;

 };

#endif // #ifndef __FMU_DATALINK_H__

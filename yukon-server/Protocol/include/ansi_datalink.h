
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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2005/03/14 21:44:16 $
*    History: 
      $Log: ansi_datalink.h,v $
      Revision 1.9  2005/03/14 21:44:16  jrichter
      updated with present value regs, batterylife info, corrected quals, multipliers/offsets, corrected single precision float define, modifed for commander commands, added demand reset

      Revision 1.8  2005/02/10 23:23:58  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

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
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "dllbase.h"
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



struct TBL_IDB_BFLD
{
   unsigned short   tbl_proc_nbr:11;
   unsigned short   std_vs_mfg_flag:1;
   unsigned short   selector:4;
};

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
      void buildSecure( BYTE aServiceCode, CtiXfer &xfer, BYTE *password );
     // void buildAuthenticate( BYTE aServiceCode, CtiXfer &xfer );
      void buildAuthenticate(BYTE aServiceCode, CtiXfer &xfer, BYTE *ini_auth_vector );
      void buildTableRequest( CtiXfer &xfer, short aTableID, BYTE aOperation, int aOffset, BYTE aType, short maxPktSize, BYTE maxNbrPkts );
      void buildWriteRequest(  CtiXfer &xfer, USHORT dataSize, short aTableID, BYTE aOperation, TBL_IDB_BFLD aProc, BYTE *parmPtr, BYTE aSeqNbr );
      void buildWaitRequest(CtiXfer &xfer );
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
    bool getToggle(void);

    void toggleToggle(void);


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

      void setIdentityByte(BYTE identityByte);
      BYTE getIdentityByte(void);

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
      BYTE        _identityByte;

 };

#endif // #ifndef __ANSI_DATALINK_H__

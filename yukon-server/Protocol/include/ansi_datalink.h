#pragma once

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
         Ack = 0,
         Header,
         Data,
         SendAck
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
    BYTE getToggleByte(void);

    void toggleToggle(void);


      void init( void );
      void destroyMe( void );
      void reinitialize( void );
      void assemblePacket( BYTE *packetPtr, BYTE *dataPtr, USHORT byteCount,/* int cntl, */int seq );
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
      void setExpectedBytes( ULONG );

      unsigned short crc16( unsigned char octet, unsigned short crcIn );
      unsigned short crc( int size, unsigned char *packet );

      void setIdentityByte(BYTE identityByte);
      BYTE getIdentityByte(void);

      bool compareToggleByte();

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
      BYTE        _toggleByte;
      BYTE        _identityByte;

 };


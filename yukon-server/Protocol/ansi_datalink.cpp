#include "precompiled.h"
#include "prot_ansi.h"
#include "ansi_datalink.h"
#include "ansi_application.h"
#include "guard.h"
#include "logger.h"

using namespace std;

//=========================================================================================================================================
//=========================================================================================================================================

CtiANSIDatalink::CtiANSIDatalink() :
    _packetBytesReceived(0),
    _currentPacket(NULL),
    _packetComplete(false),
    _currentPos(Ack),
    _previousPos(Ack),
    _bytesWeExpect(0),
    _sequence(0),
    _multiPacketPart(false),
    _multiPacketFirst(false),
    _toggleMatch(true),
    _toggle(true),
    _identityByte(0)
{
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::init( void )
{
    _packetComplete = false;

   setPacketPart( false );
   setPacketFirst( false );
   setSequence( 0 );
   _currentPos = Ack;
   _previousPos = Ack;
   if (_currentPacket != NULL)
   {
       delete _currentPacket;
       _currentPacket = NULL;
   }
   _currentPacket = CTIDBG_new BYTE[512];
   _toggle = true;
   _toggleMatch = true;
   _packetBytesReceived = 0;
   _identityByte = ANsI_RESERVED;  //0x00 default

}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::reinitialize( void )
{
    _packetComplete = false;

   setPacketPart( false );
   setPacketFirst( false );
   setSequence( 0 );
   _currentPos = Ack;
   _previousPos = Ack;
   _toggle = true;
   _toggleMatch = true;
   _packetBytesReceived = 0;
   _identityByte = ANsI_RESERVED;  //0x00 default

   destroyMe();
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::destroyMe( void )
{
   if( _currentPacket != NULL )
   {
       delete []_currentPacket;
       _currentPacket = NULL;
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiANSIDatalink::~CtiANSIDatalink()
{
   destroyMe();
}

void CtiANSIDatalink::initializeForNewPacket( void )
{
    _currentPos = Ack;
    _previousPos = Ack;
    _packetComplete = false;
}
//=========================================================================================================================================
//create the packet shell and slip the data into it
//NOTE: someday, we may have to pass in large requests, so that's what the cntl int is for...
//=========================================================================================================================================

void CtiANSIDatalink::assemblePacket( BYTE *packetPtr, BYTE *dataPtr, USHORT byteCount, /*int cntl,*/ int seq )
{
   BYTEUSHORT  flip;

   //header + reserved
   packetPtr[0] = ANSI_STP;
   //packetPtr[1] = ANsI_RESERVED;
   packetPtr[1] = getIdentityByte();


   //control field
   alternateToggle();
   packetPtr[2] = getToggleByte();

   //sequence number
   packetPtr[3] = seq;

   //length
   flip.sh = byteCount;
   packetPtr[4] = flip.ch[1];
   packetPtr[5] = flip.ch[0];

   //add the data
   memcpy( &packetPtr[6], dataPtr, byteCount );
}

//=========================================================================================================================================
//anything that we need out of the packet (data from the device) gets pulled out here
//=========================================================================================================================================

bool CtiANSIDatalink::continueBuildingPacket( CtiXfer &xfer, int aCommStatus )
{
   bool  retFlag = false;
   USHORT dataBufferLength=0;
/*
   BYTE junk[2048];

   memcpy (junk, xfer.getInBuffer(),xfer.getInCountActual());
*/
   /*************************************
   * if this is anything but 0, we need to ask again
   **************************************
   */
   if (aCommStatus)
   {
       retFlag = false;
   }
   else
   {
       switch( _currentPos )
       {
       case Ack:
          {
             if( ( xfer.getInBuffer()[0] == ANSI_ACK ) && ( getExpectedBytes() == 1 ) )         //if we're only looking for the <ack>
             {
                //now we'll ask porter for the header
                xfer.setInCountExpected( 6 );
                xfer.setOutCount( 0 );
                setExpectedBytes( 6 );
                setPacketComplete (false);

                _previousPos = _currentPos;
                _currentPos = Header;
                retFlag = true;
             }
             else
             {
                 retFlag = false;

                 /***************************************
                 //now we'll ask porter for the header
                xfer.setInCountExpected( 6 );
                xfer.setOutCount( 0 );
                setExpectedBytes( 6 );
                setPacketComplete (false);

                _previousPos = _currentPos;
                _currentPos = header;
                retFlag = true;
                /*****************************************/
             }
          }
          break;

       case Header:
          {
             if( ( xfer.getInBuffer()[0] == ANSI_STP ) && ( getExpectedBytes() > 1 ) )       //if we're looking for the header
             {
                 // move the data into our current packet
                 _packetBytesReceived = xfer.getInCountActual();
                 if(( _currentPacket != NULL ) && ( _packetBytesReceived < 511 ))
                 {
                    memcpy( _currentPacket, xfer.getInBuffer(), _packetBytesReceived );
                 }

                decodePacketHeader( );

                // make sure we add the CRC
                xfer.setInCountExpected( getExpectedBytes()+2);
                xfer.setOutCount( 0 );
                setPacketComplete (false);


                _previousPos = _currentPos;
                _currentPos = Data;
                retFlag = true;

             }
             else
             {
                 retFlag = false;
                 _currentPos = Ack;
             }
          }
          break;

       case Data:
          {
              // new number of bytes received for this packet
              _packetBytesReceived += xfer.getInCountActual();

              if( getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO )
              {
                  CTILOG_DEBUG(dout, "_packetBytesReceived "<<_packetBytesReceived);
              }
              if(( _currentPacket != NULL ) && ( _packetBytesReceived < 511 ))
              {
                 memcpy( _currentPacket+_packetBytesReceived-xfer.getInCountActual(),
                          xfer.getInBuffer(),
                         xfer.getInCountActual() );
                 _previousPos = _currentPos;
                 _currentPos = SendAck;
                 buildAck (xfer);
                 retFlag = true;
              }
              else
              {
                  //we failed so let's try again if we're not out of retries
                  retFlag = false;
                  _currentPos = Ack;
              }
          }
          break;
        case SendAck:
        {
            if (getPacketPart() && getSequence() != 0)
            {
                xfer.setInCountExpected( 6 );
                xfer.setOutCount( 0 );
                setExpectedBytes( 6 );
                setPacketComplete (true);

                _previousPos = Ack;
                _currentPos = Header;
                retFlag = true;
            }
            else
            {
                xfer.setInCountExpected( 0 );
                xfer.setOutCount( 0 );
                setExpectedBytes( 0 );

                setPacketComplete (true);
                retFlag = true;

                _currentPos = Ack;
                _previousPos = Ack;
            }
            break;
        }
       }
   }

   return( retFlag ); //something
}

//=========================================================================================================================================
//here we yank the basic stuff out of the packet and store the results
//we decide if the packet is part of a multipacket transmission
//we decide if the packet is the first of a multipacket transmission
//we decide if we should toggle our flag (??)-why, we don't know
//we count how many packets are still enroute
//we count how many bytes are in the data portion of this packet
//=========================================================================================================================================

void CtiANSIDatalink::decodePacketHeader( )
{
   BYTEUSHORT  flip;

   // this is part of the class, we get the message in parts
   BYTE *packet = _currentPacket;

   //reset; our logic depends on it
   setPacketPart( false );
   setPacketFirst( false );
   setSequence( 0x00 );

   packet++; //increment past <ack> or <nak>

   //packet[0] = 0xee <ansi header>

   if( packet[1] & 0x80 )  //<cntl>
      setPacketPart( true );

   if( packet[1] & 0x40 )
      setPacketFirst( true );

   if( packet[2] != 0x00 ) //<seq_nbr>
      setSequence( packet[2] );

   //get the number of bytes in the data part
   flip.ch[0] = packet[4]; //<length>
   flip.ch[1] = packet[3];
   setExpectedBytes (flip.sh);
}

//=========================================================================================================================================
//make the data portion of the packet we send to get the device's identity
//=========================================================================================================================================

void CtiANSIDatalink::buildIdentify( BYTE aServiceCode, CtiXfer &xfer )
{
   BYTE        data;
   BYTEUSHORT  flip;

   data = aServiceCode;

   memset( xfer.getOutBuffer(), 0, 100 );
   assemblePacket( xfer.getOutBuffer(), &data, 1, 0 );

   flip.sh = crc( 1 + HEADER_LEN, xfer.getOutBuffer() );
   xfer.getOutBuffer()[7] = flip.ch[1];
   xfer.getOutBuffer()[8] = flip.ch[0];

   xfer.setOutCount( 1 + HEADER_LEN + sizeof( USHORT ) );

   //we're just going to look for one byte first (the <ack>) then we'll know what's sitting out on the port for us...
   xfer.setInCountExpected( 1 );
   setExpectedBytes( 1 );
}

//=========================================================================================================================================
//FIXME: don't really know what the parameters should be set to
//=========================================================================================================================================

void CtiANSIDatalink::buildNegotiate(BYTE aServiceCode, CtiXfer &xfer )
{
   BYTE        data[5];
   BYTEUSHORT  flip;
   int arraySize = 4;

   //this is just for TESTING
   data[0] = aServiceCode;
   data[1] = 0x00;               //176 bytes in a packet max
   data[2] = 0x80;
   data[3] = 0xff;
  // data[3] = 0x03;
   //data[4] = 0x06;
   //data[4] = 0x04;
   if (aServiceCode != 0x60)
   {
       arraySize = 5;
       data[4] = 0x06;
       //data[4] = 0x04;
   }


   memset( xfer.getOutBuffer(), 0, 100 );
   assemblePacket( xfer.getOutBuffer(), data, arraySize, 0 );
   flip.sh = crc( arraySize + HEADER_LEN, xfer.getOutBuffer() );
   xfer.getOutBuffer()[arraySize + HEADER_LEN] = flip.ch[1];
   xfer.getOutBuffer()[arraySize + HEADER_LEN + 1] = flip.ch[0];

   xfer.setOutCount( arraySize + HEADER_LEN + sizeof( USHORT ) );
   /*
   assemblePacket( xfer.getOutBuffer(), data, 4, 0 );
   flip.sh = crc( 4 + HEADER_LEN, xfer.getOutBuffer() );
   xfer.getOutBuffer()[4 + HEADER_LEN] = flip.ch[1];
   xfer.getOutBuffer()[4 + HEADER_LEN + 1] = flip.ch[0];

   xfer.setOutCount( 4 + HEADER_LEN + sizeof( USHORT ) );
   */
   //we're just going to look for one byte first (the <ack>) then we'll know what's sitting out on the port for us...
   xfer.setInCountExpected( 1 );
   setExpectedBytes( 1 );
}

//=========================================================================================================================================
//FIXME: don't really know what the parameters should be set to
//=========================================================================================================================================

void CtiANSIDatalink::buildTiming(BYTE aServiceCode, CtiXfer &xfer )
{
   BYTE        data[5];
   BYTEUSHORT  flip;

   //this is just for TESTING
   data[0] = aServiceCode;
   data[1] = 0x78;                     //120 seconds traffic timeout
   data[2] = 0x03;                     //3 seconds inter-char timeout
   data[3] = 0x0a;                     //10 seconds response timeout
   data[4] = 0x03;                     //3 retries

   memset( xfer.getOutBuffer(), 0, 100 );
   assemblePacket( xfer.getOutBuffer(), data, 5, 0 );

   flip.sh = crc( 5 + HEADER_LEN, xfer.getOutBuffer() );
   xfer.getOutBuffer()[5 + HEADER_LEN] = flip.ch[1];
   xfer.getOutBuffer()[5 + HEADER_LEN + 1] = flip.ch[0];

   xfer.setOutCount( 5 + HEADER_LEN + sizeof( USHORT ) );

   //we're just going to look for one byte first (the <ack>) then we'll know what's sitting out on the port for us...
   xfer.setInCountExpected( 1 );
   setExpectedBytes( 1 );
}

//=========================================================================================================================================
//FIXME: don't really know what the parameters should be set to
//=========================================================================================================================================

void CtiANSIDatalink::buildLogOn(BYTE aServiceCode, CtiXfer &xfer )
{
   BYTE        data[13];
   BYTEUSHORT  flip;

   memset( &data[3], 0x00, 13 );

   //this is just for TESTING
   data[0] = aServiceCode;
   data[1] = 0x00;
   data[2] = 0x02;
   data[3] = 0x6e;
   data[4] = 0x6f;
   data[5] = 0x6e;
   data[6] = 0x65;
   data[7] = 0x00;
   data[8] = 0x00;
   data[9] = 0x00;
   data[10] = 0x00;
   data[11] = 0x00;
   data[12] = 0x00;

   /*********/
   data[1] = 0x00;
   //data[2] = 0x32;   //userID?

   data[8] = 0x41;   //Admin
   data[9] = 0x64;
   data[10] = 0x6d;
   data[11] = 0x69;
   data[12] = 0x6e;
   /*********/

  /* data[3] = 0x00;
   data[4] = 0x00;
   data[5] = 0x00;
   data[6] = 0x00;
   */
   memset( xfer.getOutBuffer(), 0, 100 );
   assemblePacket( xfer.getOutBuffer(), data, 13, 0 );

   flip.sh = crc( 13 + HEADER_LEN, xfer.getOutBuffer() );
   xfer.getOutBuffer()[13 + HEADER_LEN] = flip.ch[1];
   xfer.getOutBuffer()[13 + HEADER_LEN + 1] = flip.ch[0];

   xfer.setOutCount( 13 + HEADER_LEN + sizeof( USHORT ) );

   //we're just going to look for one byte first (the <ack>) then we'll know what's sitting out on the port for us...
   xfer.setInCountExpected( 1 );
   setExpectedBytes( 1 );
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::buildSecure(BYTE aServiceCode, CtiXfer &xfer, BYTE *password )
{
   BYTE        data[21];
   BYTEUSHORT  flip;

   memset( data, NULL, 21 );
   data[0] = aServiceCode;
   memcpy( &data[1], password, 20 );

   memset( xfer.getOutBuffer(), 0, 100 );
   assemblePacket( xfer.getOutBuffer(), data, 21, 0 );

   flip.sh = crc( 21 + HEADER_LEN, xfer.getOutBuffer() );
   xfer.getOutBuffer()[21 + HEADER_LEN] = flip.ch[1];
   xfer.getOutBuffer()[21 + HEADER_LEN + 1] = flip.ch[0];

   xfer.setOutCount( 21 + HEADER_LEN + sizeof( USHORT ) );

   //we're just going to look for one byte first (the <ack>) then we'll know what's sitting out on the port for us...
   xfer.setInCountExpected( 1 );
   setExpectedBytes( 1 );
}

//=========================================================================================================================================
//we do things in the authenticate portion of logging on to the device IF the device said to do so in the identify portion
//=========================================================================================================================================

void CtiANSIDatalink::buildAuthenticate(BYTE aServiceCode, CtiXfer &xfer, BYTE *ini_auth_vector )
{
   BYTE        data[11]; //=aServiceCode;
   BYTEUSHORT  flip;

   memset (data, NULL, 11);
   data[0] = aServiceCode;
   data[1] = 0x09; //length
   data[2] = 0x00; //key id ?????
   memcpy(&data[3], ini_auth_vector, 8);
   memset( xfer.getOutBuffer(), 0, 100 );
   assemblePacket( xfer.getOutBuffer(), data, 11, 0 );

   flip.sh = crc( 11 + HEADER_LEN, xfer.getOutBuffer() ); ///FIXME: this is stolen
   xfer.getOutBuffer()[11 + HEADER_LEN] = flip.ch[1];
   xfer.getOutBuffer()[11 + HEADER_LEN] = flip.ch[0];

   xfer.setOutCount( 11 + HEADER_LEN + sizeof( USHORT ) );

   //we're just going to look for one byte first (the <ack>) then we'll know what's sitting out on the port for us...
   xfer.setInCountExpected( 1 );
   setExpectedBytes( 1 );
}

//=========================================================================================================================================
//here's where we're going to ask the device for some tables...
//note, we know how many tables we want because when we did the doAck() after we got connected, we pulled the # of tables out of the
//_header that came from the layers above. We'll build a request and increment that index and maintain the same state until all the packets
//from all the tables have come in
//=========================================================================================================================================

void CtiANSIDatalink::buildTableRequest( CtiXfer &xfer, short aTableID, BYTE aOperation, int aOffset, BYTE aType, short maxPktSize, BYTE maxNbrPkts )
{
   BYTE        data[10];
   BYTEUSHORT  flip;
   BYTEUSHORT  id;
   BYTEUSHORT  pktSize;
   BYTEULONG    offset;
   int dataSize = 0;

   // add the offset here

  //zero out data portion of the request out
  memset( data, 0x00, 10 );

      //data[0] = full_read;      //NOTE: we can't do full reads on the KV2 (may be different for other meters)
  data[0] = aOperation;
  //data[0] = 0x3f;
  //data[0] = aOperation;

//      id.sh = 75;
  id.sh = aTableID;
  offset.ul = (ULONG)aOffset;

//      data[1] = 0x08;
  data[1] = id.ch[1];
  data[2] = id.ch[0];

  if (data[0] != 0x30)
  {
      data[3] = offset.ch[2];
      data[4] = offset.ch[1];
      data[5] = offset.ch[0];
      pktSize.sh = maxPktSize;
      data[6] = pktSize.ch[1];
      data[7] = pktSize.ch[0];
      //data[6] = 0x00;
     // data[7] = 0xa4;

      dataSize = 8;
  }
  else
  {
      dataSize = 3;
  }

  memset( xfer.getOutBuffer(), 0, 100 );
  assemblePacket( xfer.getOutBuffer(), data, dataSize, 0 );

  flip.sh = crc( dataSize + HEADER_LEN, xfer.getOutBuffer() ); ///FIXME: this is stolen
  xfer.getOutBuffer()[dataSize + HEADER_LEN] = flip.ch[1];
  xfer.getOutBuffer()[dataSize + HEADER_LEN + 1] = flip.ch[0];

  xfer.setOutCount( dataSize + HEADER_LEN + sizeof( USHORT ) );

  //we're just going to look for one byte first (the <ack>) then we'll know what's sitting out on the port for us...
  xfer.setInCountExpected( 1 );
  setExpectedBytes( 1 );
}


void CtiANSIDatalink::buildWriteRequest(  CtiXfer &xfer, USHORT dataSize, short aTableID, BYTE aOperation, TBL_IDB_BFLD aProc, BYTE *parmPtr, BYTE aSeqNbr )
{
    int arraySize = 5 + dataSize + 1;
    BYTE        *data; //write 0x40 (1), tableID (2), count (2), data (dataSize),
                                        //cksum (1), crc (2)
    data = new BYTE[arraySize];
    BYTEUSHORT  flip;
    BYTEUSHORT  id;
    BYTEUSHORT  proc;
    BYTEUSHORT  dataCount;

    // add the offset here

   //zero out data portion of the request out
   memset( data, 0x00, arraySize );

   data[0] = aOperation;

   id.sh = aTableID;

   data[1] = id.ch[1];
   data[2] = id.ch[0];

   dataCount.sh = dataSize;
   data[3] = dataCount.ch[1];
   data[4] = dataCount.ch[0];

   if (aTableID == Cti::Protocols::Ansi::Focus_SetLpReadControl ||
       aTableID == Cti::Protocols::Ansi::Sentinel_BatteryLifeRequest)
   {
       //BYTEULONG lid;
       for (int i = 0; i < dataSize; i++)
       {
           //lid.ul = (ULONG *)&parmPtr;

           data[5+i] = *(parmPtr + i);
       }
   }
   else
   {
       USHORT *temp;
       temp = (USHORT *)&aProc;
       proc.sh = (USHORT )*temp;
       data[5] = proc.ch[0];
       data[6] = proc.ch[1];

       data[7] = aSeqNbr;


       TBL_IDB_BFLD tblProcBfld = (TBL_IDB_BFLD )aProc;
       switch((int)tblProcBfld.tbl_proc_nbr)
       {
           case 5:
               {
                   data[8] = *parmPtr;
                   parmPtr++;
                   BYTEUSHORT entries;
                   entries.sh = (*parmPtr * 0x100) + *(parmPtr + 1);
                   entries.ch[0] = *parmPtr;
                   entries.ch[1] = *(parmPtr + 1);
                   data[9] = entries.ch[1];
                   data[10] = entries.ch[0];
                   break;
               }
           case 8:
           {
               break;
           }
           case 9:
               {
                   data[8] = *parmPtr;
                   break;
               }
           case 16:
           case 17:
               {
                   break;
               }
               case 22:
               {
                   data[8] = *parmPtr;
                   data[9] = *(parmPtr + 1);
                   data[10] = *(parmPtr + 2);
                   data[11] = *(parmPtr + 3);
                   break;
               }
               default:
                   break;
       }
   }


   data[5 + dataSize] = 0;
   for (int xx = 5; xx < dataSize + 5; xx++)
   {
       data[5 + dataSize] += data[xx];    //2's complement cksm
   }
   BYTE cksum = data[5 + dataSize];
   data[5 + dataSize] = ~cksum + 1;

   memset( xfer.getOutBuffer(), 0, 100 );
   assemblePacket( xfer.getOutBuffer(), data, arraySize, 0 );

   flip.sh = crc( arraySize + HEADER_LEN, xfer.getOutBuffer() ); ///FIXME: this is stolen
   xfer.getOutBuffer()[arraySize+ HEADER_LEN] = flip.ch[1];
   xfer.getOutBuffer()[arraySize + HEADER_LEN + 1] = flip.ch[0];

   xfer.setOutCount( arraySize + HEADER_LEN + sizeof( USHORT ) );

   //we're just going to look for one byte first (the <ack>) then we'll know what's sitting out on the port for us...
   xfer.setInCountExpected( 1 );
   setExpectedBytes( 1 );

   if (data != NULL)
   {
       delete []data;
       data = NULL;
   }

}

//=========================================================================================================================================
// Wait Request 0x70 service code
//=========================================================================================================================================

void CtiANSIDatalink::buildWaitRequest(CtiXfer &xfer )
{
   BYTE        data[2];
   BYTEUSHORT  flip;

   data[0] = 0x70;
   data[1] = 0x05;

   assemblePacket( xfer.getOutBuffer(), data, 2, 0 );

   flip.sh = crc( 2 + HEADER_LEN, xfer.getOutBuffer() );
   xfer.getOutBuffer()[8] = flip.ch[1];
   xfer.getOutBuffer()[9] = flip.ch[0];

   xfer.setOutCount( 2 + HEADER_LEN + sizeof( USHORT ) );

   //we're just going to look for one byte first (the <ack>) then we'll know what's sitting out on the port for us...
   xfer.setInCountExpected( 1 );
   setExpectedBytes( 1 );
}


//=========================================================================================================================================
//FIXME: don't really know what the parameters should be set to
//=========================================================================================================================================

void CtiANSIDatalink::buildLogOff(BYTE aServiceCode,  CtiXfer &xfer )
{
   BYTE        data;
   BYTEUSHORT  flip;

   data = aServiceCode;

   assemblePacket( xfer.getOutBuffer(), &data, 1, 0 );

   flip.sh = crc( 1 + HEADER_LEN, xfer.getOutBuffer() );
   xfer.getOutBuffer()[7] = flip.ch[1];
   xfer.getOutBuffer()[8] = flip.ch[0];

   xfer.setOutCount( 1 + HEADER_LEN + sizeof( USHORT ) );

   //we're just going to look for one byte first (the <ack>) then we'll know what's sitting out on the port for us...
   xfer.setInCountExpected( 1 );
   setExpectedBytes( 1 );
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::buildTerminate(BYTE aServiceCode,  CtiXfer &xfer )
{
   BYTE        data;
   BYTEUSHORT  flip;

   data = aServiceCode;

   assemblePacket( xfer.getOutBuffer(), &data, 1, 0 );

   flip.sh = crc( 1 + HEADER_LEN, xfer.getOutBuffer() );
   xfer.getOutBuffer()[7] = flip.ch[1];
   xfer.getOutBuffer()[8] = flip.ch[0];

   xfer.setOutCount( 1 + HEADER_LEN + sizeof( USHORT ) );

   //we're just going to look for one byte first (the <ack>) then we'll know what's sitting out on the port for us...
   xfer.setInCountExpected( 1 );
   setExpectedBytes( 1 );

}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::buildDisconnect(BYTE aServiceCode,  CtiXfer &xfer )
{
   BYTE        data;
   BYTEUSHORT  flip;

   data = aServiceCode;

   assemblePacket( xfer.getOutBuffer(), &data, 1, 0 );

   flip.sh = crc( 1 + HEADER_LEN, xfer.getOutBuffer() );
   xfer.getOutBuffer()[7] = flip.ch[1];
   xfer.getOutBuffer()[8] = flip.ch[0];

   xfer.setOutCount( 1 + HEADER_LEN + sizeof( USHORT ) );

   //we're just going to look for one byte first (the <ack>) then we'll know what's sitting out on the port for us...
   xfer.setInCountExpected( 1 );
   setExpectedBytes( 1 );
}

//=========================================================================================================================================
//we have to send an ack after the device sends us his data
//NOTE: this ack is NOT wrapped in a packet, it's all by itself!
//we also need to send an ack after we're logged off and we keep track of if we're connected and ready to do table requests
//=========================================================================================================================================

void CtiANSIDatalink::buildAck( CtiXfer &xfer )
{
   BYTE  data;

   data = ANSI_ACK;
   memcpy( xfer.getOutBuffer(), &data, sizeof( data ) );
   xfer.setOutCount( 1 );
   xfer.setInCountExpected( 0 );
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiANSIDatalink::getPacketPart( void )
{
   return _multiPacketPart;
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::setPacketPart( bool isPart )
{
   _multiPacketPart = isPart;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiANSIDatalink::getPacketFirst( void )
{
   return _multiPacketFirst;
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::setPacketFirst( bool isFirst )
{
   _multiPacketFirst = isFirst;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiANSIDatalink::getSequence( void )
{
   return _sequence;
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::setSequence( int seq )
{
   _sequence = seq;
}

//=========================================================================================================================================
//=========================================================================================================================================

ULONG CtiANSIDatalink::getExpectedBytes( void )
{
   return _bytesWeExpect;
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::setExpectedBytes( ULONG num )
{
   _bytesWeExpect = num;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiANSIDatalink::isPacketComplete( void )
{
   return _packetComplete;
}

CtiANSIDatalink &CtiANSIDatalink::setPacketComplete( bool aFlag )
{
   _packetComplete = aFlag;
   return *this;
}


//=========================================================================================================================================
//=========================================================================================================================================

BYTE *CtiANSIDatalink::getCurrentPacket( void )
{
   return _currentPacket;
}

int CtiANSIDatalink::getPacketBytesReceived( void )
{
   return _packetBytesReceived;
}

BYTE CtiANSIDatalink::getToggleByte( void )
{
    return _toggle ? 0x20 : 0x00;
}

//=========================================================================================================================================
//this method simply lops off the end of the message where the crc is and compares it to a new calculation that is done
//=========================================================================================================================================

bool CtiANSIDatalink::isCRCvalid( void )
{
   BYTEUSHORT  CRC;
   bool        isOk = false;
   unsigned short temp;

   if( _packetBytesReceived > 3 )
   {
      CRC.ch[0] = _currentPacket[_packetBytesReceived - 1];
      CRC.ch[1] = _currentPacket[_packetBytesReceived - 2];

      if( CRC.sh == crc(_packetBytesReceived-2, _currentPacket ) )
      {
         isOk = true;
      }
   }

   return isOk;
}

//=========================================================================================================================================
//=========================================================================================================================================

unsigned short CtiANSIDatalink::crc16( unsigned char octet, unsigned short crcIn )
{
   int i;

   for( i = 8; i; i-- )
   {
      if( crcIn & 0x0001 )
      {
         crcIn >>= 1;

         if( octet & 0x01 )
            crcIn |= 0x8000;

         crcIn = crcIn^0x8408;
         octet >>= 1;
      }
      else
      {
         crcIn >>= 1;

         if( octet & 0x01 )
            crcIn |= 0x8000;

         octet >>= 1;
      }
   }
   return crcIn;
}

//=========================================================================================================================================
//=========================================================================================================================================

unsigned short CtiANSIDatalink::crc( int size, unsigned char *packet )
{
   int i;
   unsigned short theCrc;

   theCrc = ( ~packet[1] << 8 ) | ( ~packet[0] & 0xff );

   for( i = 2; i < size; i++ )
      theCrc = crc16( packet[i], theCrc );

   theCrc = crc16( 0x00, theCrc );
   theCrc = crc16( 0x00, theCrc );

   theCrc = ~theCrc;
   theCrc = theCrc >> 8 | theCrc << 8;

   return theCrc;
}


void CtiANSIDatalink::alternateToggle( void )
{
    _toggle = !_toggle;
    return;
}

void CtiANSIDatalink::setIdentityByte(BYTE identityByte)
{
    _identityByte = identityByte;
    return;
}

BYTE CtiANSIDatalink::getIdentityByte( void )
{
    return _identityByte;
}


bool CtiANSIDatalink::compareToggleByte(BYTE toggleByte)
{
    bool match = getToggleByte() == (toggleByte & 0x20);
    if (shouldToggleMatch())
    {
        return match;
    }
    else
    {
        return !match;
    }
}
bool CtiANSIDatalink::shouldToggleMatch()
{
    return _toggleMatch;
}
void CtiANSIDatalink::initToggleMatch()
{
    _toggleMatch = (_currentPacket[2] & 0x20) == getToggleByte();
}


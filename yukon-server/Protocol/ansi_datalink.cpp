
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   ansi_datalink
*
* Date:   6/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/ansi_datalink.cpp-arc  $
* REVISION     :  $Revision: 1.7 $                                                198
* DATE         :  $Date: 2004/09/30 21:37:16 $
*    History: 
      $Log: ansi_datalink.cpp,v $
      Revision 1.7  2004/09/30 21:37:16  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.6  2003/04/25 15:13:45  dsutton
      Update of the base protocol pieces taking into account the manufacturer
      tables, etc.  New starting point

*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "ansi_datalink.h"
#include "ansi_application.h"
#include "guard.h"
#include "logger.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiANSIDatalink::CtiANSIDatalink()
{
    _currentPacket = NULL;
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::init( void )
{
    _packetComplete = false;

   setPacketPart( false );
   setPacketFirst( false );
   setSequence( 0 );
   _currentPos = ack;
   _previousPos = ack;
   _currentPacket = CTIDBG_new BYTE[512];
   _toggle = false;
   _packetBytesReceived = 0;

}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::reinitialize( void )
{
    _packetComplete = false;

   setPacketPart( false );
   setPacketFirst( false );
   setSequence( 0 );
   _currentPos = ack;
   _previousPos = ack;
   _toggle = false;
   _packetBytesReceived = 0;
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::destroyMe( void )
{
   if( _currentPacket != NULL )
   {
       delete _currentPacket;
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
    _currentPos = ack;
    _previousPos = ack;
    _packetComplete = false;
}
//=========================================================================================================================================
//create the packet shell and slip the data into it
//NOTE: someday, we may have to pass in large requests, so that's what the cntl int is for...
//=========================================================================================================================================

void CtiANSIDatalink::assemblePacket( BYTE *packetPtr, BYTE *dataPtr, USHORT count, /*int cntl,*/ int seq )
{
   BYTEUSHORT  flip;

   //header + reserved
   packetPtr[0] = ANSI_STP;
   packetPtr[1] = ANsI_RESERVED;

   //control field
   if( _toggle == true )
   {
      _toggle = false;
      packetPtr[2] = 0x20;
   }
   else
   {
      _toggle = true;
      packetPtr[2] = 0x00;
   }

   //sequence number
   packetPtr[3] = seq;

   //length
   flip.sh = count;
   packetPtr[4] = flip.ch[1];
   packetPtr[5] = flip.ch[0];

   //add the data
   memcpy( &packetPtr[6], dataPtr, count );
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


   {
       CtiLockGuard< CtiLogger > doubt_guard( dout );
       dout << RWTime::now() << " xfer in buffer in dissamble packetTable 14 got here " << endl << " --";
   }

   for (int x=0;x < xfer.getInCountActual();x++)
   {
       CtiLockGuard< CtiLogger > doubt_guard( dout );
       dout << junk[x] << " ";
   }
   {
       CtiLockGuard< CtiLogger > doubt_guard( dout );
       dout << endl;
   }
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
       case ack:
          {
             if( ( xfer.getInBuffer()[0] == ANSI_ACK ) && ( getExpectedBytes() == 1 ) )         //if we're only looking for the <ack>
             {
                //now we'll ask porter for the header
                xfer.setInCountExpected( 6 );
                xfer.setOutCount( 0 );
                setExpectedBytes( 6 );
                setPacketComplete (false);

                _previousPos = _currentPos;
                _currentPos = header;
                retFlag = true;
             }
             else
             {
                 retFlag = false;

                 /****************************************
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

       case header:
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
                _currentPos = data;
                retFlag = true;

             }
             else
             {
                 retFlag = false;
                 _currentPos = ack;
             }
          }
          break;

       case data:
          {
              // new number of bytes received for this packet
              _packetBytesReceived += xfer.getInCountActual();
              {
                            CtiLockGuard< CtiLogger > doubt_guard( dout );                              
                            dout <<"  *******JULIE  getInCountActual "<<xfer.getInCountActual()<<"  _packetBytesReceived " <<_packetBytesReceived <<endl;
              }
              if(( _currentPacket != NULL ) && ( _packetBytesReceived < 511 ))
              {
                 memcpy( _currentPacket+_packetBytesReceived-xfer.getInCountActual(),
                          xfer.getInBuffer(), 
                         xfer.getInCountActual() );
                 _previousPos = _currentPos;
                 _currentPos = sendAck;
                 buildAck (xfer);
                 retFlag = true;
              }
              else
              {
                  //we failed so let's try again if we're not out of retries
                  retFlag = false;
                  _currentPos = ack;
              }
          }
          break;
        case sendAck:
        {
            if (getPacketPart() && getSequence() != 0)
            {
                xfer.setInCountExpected( 6 );
                xfer.setOutCount( 0 );
                setExpectedBytes( 6 );
                setPacketComplete (true);

                _previousPos = ack;
                _currentPos = header;
                retFlag = true;
            }
            else
            {
                setPacketComplete (true);
                retFlag = true;

                _currentPos = ack;
                _previousPos = ack;
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

   packet++;
   packet++;

   if( packet[0] & 0x80 )
      setPacketPart( true );

   if( packet[0] & 0x40 )
      setPacketFirst( true );

   /*if( packet[0] & 0x20 )
      _toggle = true;
   else
       _toggle = false;
   */
   packet++;                                          //increment past the <cntl>

   if( packet[0] != 0x00 )
      setSequence( packet[0] );
   packet++;                                          //increment past the <seq_nbr>

   //get the number of bytes in the data part
   flip.ch[0] = packet[1];
   flip.ch[1] = packet[0];
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

   memset( xfer.getOutBuffer(), NULL, 100 );
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

   //this is just for TESTING
   data[0] = aServiceCode;
   data[1] = 0x00;               //176 bytes in a packet max
   data[2] = 0xb0;
   data[3] = 0x03;
   data[4] = 0x06;

   memset( xfer.getOutBuffer(), NULL, 100 );
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

   memset( xfer.getOutBuffer(), NULL, 100 );
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
   data[7] = 0x20;
   data[8] = 0x20;
   data[9] = 0x20;
   data[10] = 0x20;
   data[11] = 0x20;
   data[12] = 0x20;

   /*
   data[1] = 0x00;
   data[2] = 0x32;   //userID?

   data[8] = 0x41;   //Admin
   data[9] = 0x64;
   data[10] = 0x6d;
   data[11] = 0x69;
   data[12] = 0x6e;
*/

   memset( xfer.getOutBuffer(), NULL, 100 );
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

void CtiANSIDatalink::buildSecure(BYTE aServiceCode, CtiXfer &xfer )
{
   BYTE        data[21];
   BYTEUSHORT  flip;
   //BYTE        password[] = { 0xab, 0xc1, 0xab, 0xc2, 0xab, 0xc3, 0xab, 0xc4, 0xab, 0xc5, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };
   BYTE        password[] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

   memset( data, NULL, 21 );
   data[0] = aServiceCode;
   memcpy( &data[1], password, 20 );

   memset( xfer.getOutBuffer(), NULL, 100 );
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

void CtiANSIDatalink::buildAuthenticate(BYTE aServiceCode, CtiXfer &xfer )
{
   BYTE        data=aServiceCode;
   BYTEUSHORT  flip;

   memset( xfer.getOutBuffer(), NULL, 100 );
   assemblePacket( xfer.getOutBuffer(), &data, 1, 0 );

   flip.sh = crc( 1 + HEADER_LEN, xfer.getOutBuffer() ); ///FIXME: this is stolen
   xfer.getOutBuffer()[7] = flip.ch[1];
   xfer.getOutBuffer()[8] = flip.ch[0];

   xfer.setOutCount( 1 + HEADER_LEN + sizeof( USHORT ) );

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

void CtiANSIDatalink::buildTableRequest( CtiXfer &xfer, int aTableID, BYTE aOperation, int aOffset, BYTE aType )
{
   BYTE        data[10];
   BYTEUSHORT  flip;
   BYTEUSHORT  id;
   BYTEULONG    offset;

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
  data[3] = offset.ch[2];
  data[4] = offset.ch[1];
  data[5] = offset.ch[0];
  data[7] = 0xaa;

  memset( xfer.getOutBuffer(), NULL, 100 );
  assemblePacket( xfer.getOutBuffer(), data, 8, 0 );

  flip.sh = crc( 8 + HEADER_LEN, xfer.getOutBuffer() ); ///FIXME: this is stolen
  xfer.getOutBuffer()[8 + HEADER_LEN] = flip.ch[1];
  xfer.getOutBuffer()[8 + HEADER_LEN + 1] = flip.ch[0];

  xfer.setOutCount( 8 + HEADER_LEN + sizeof( USHORT ) );

  //we're just going to look for one byte first (the <ack>) then we'll know what's sitting out on the port for us...
  xfer.setInCountExpected( 1 );
  setExpectedBytes( 1 );
}


void CtiANSIDatalink::buildWriteRequest(  CtiXfer &xfer, USHORT dataSize, int aTableID, BYTE aOperation, TBL_IDB_BFLD aProc, BYTE *parmPtr, BYTE aSeqNbr )
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


   data[5 + dataSize] = 0;
   {
           CtiLockGuard< CtiLogger > doubt_guard( dout );                              
           dout <<" ***** Data  ";
   }
   for (int xx = 0; xx < (5 + dataSize); xx++) 
   {
       data[5 + dataSize] += data[xx];    //2's complement cksm
       {
           CtiLockGuard< CtiLogger > doubt_guard( dout );                              
           dout <<" * "<<(int)data[xx];
       }
   }
   {
           CtiLockGuard< CtiLogger > doubt_guard( dout );                              
           dout <<endl;
   }

   memset( xfer.getOutBuffer(), NULL, 100 );
   assemblePacket( xfer.getOutBuffer(), data, arraySize, 0 );

   flip.sh = crc( arraySize + HEADER_LEN, xfer.getOutBuffer() ); ///FIXME: this is stolen
   xfer.getOutBuffer()[arraySize+ HEADER_LEN] = flip.ch[1];
   xfer.getOutBuffer()[arraySize + HEADER_LEN + 1] = flip.ch[0];

   xfer.setOutCount( arraySize + HEADER_LEN + sizeof( USHORT ) );

   //we're just going to look for one byte first (the <ack>) then we'll know what's sitting out on the port for us...
   xfer.setInCountExpected( 1 );
   setExpectedBytes( 1 );

}

//=========================================================================================================================================
// Wait Request 0x70 service code
//=========================================================================================================================================

void CtiANSIDatalink::buildWaitRequest(CtiXfer &xfer )
{
   BYTE        data[2];
   BYTEUSHORT  flip;

   data[0] = 0x70;
   data[1] = 0xff;

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

bool CtiANSIDatalink::getToggle( void )
{
    return _toggle;
}

//=========================================================================================================================================
//this method simply lops off the end of the message where the crc is and compares it to a CTIDBG_new calculation that is done
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

unsigned short CtiANSIDatalink::crc16( unsigned char octet, unsigned short crc )
{
   int i;

   for( i = 8; i; i-- )
   {
      if( crc & 0x0001 )
      {
         crc >>= 1;

         if( octet & 0x01 )
            crc |= 0x8000;

         crc = crc^0x8408;
         octet >>= 1;
      }
      else
      {
         crc >>= 1;

         if( octet & 0x01 )
            crc |= 0x8000;

         octet >>= 1;
      }
   }
   return crc;
}

//=========================================================================================================================================
//=========================================================================================================================================

unsigned short CtiANSIDatalink::crc( int size, unsigned char *packet )
{
   int i;
   unsigned short crc;

   crc = ( ~packet[1] << 8 ) | ( ~packet[0] & 0xff );

   for( i = 2; i < size; i++ )
      crc = crc16( packet[i], crc );

   crc = crc16( 0x00, crc );
   crc = crc16( 0x00, crc );

   crc = ~crc;
   crc = crc >> 8 | crc << 8;

   return crc;
}


void CtiANSIDatalink::toggleToggle( void )
{
    if (_toggle == true)
        _toggle = false;
    else
        _toggle = true;
    return;
}


/*-----------------------------------------------------------------------------*
*
* File:   ansi_datalink
*
* Date:   6/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2003/03/13 19:35:36 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include "ansi_datalink.h"
#include "guard.h"
#include "logger.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiANSIDatalink::CtiANSIDatalink()
{
   setPacketPart( false );
   setPacketFirst( false );
   setSequence( 0 );
   setDataLen( 0 );
   setRetries( MAX_RETRIES );
   _currentState = identified;
   _currentPos = ack;
   _tableIndex = 0;
   _tempMsgStorage = CTIDBG_new BYTE[512];
   _ptrFromAppLayer = CTIDBG_new BYTE[512];
   _toggle = false;
   _authenticate = false;
   _connected = false;
   _ready = false;
   _bytes = 0;
   setFailed( false );
   _allDone = false;

   _PRETENDER =0;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiANSIDatalink::~CtiANSIDatalink()
{
   delete _tempMsgStorage;
   delete _ptrFromAppLayer;
   delete _whatsRequested;
   delete _tables;
   delete _header;
}

//=========================================================================================================================================
//if the InCount of the xfer message is not 0, all we want is some bytes sitting on the port
//otherwise, we need to build a real message to send to the device
//=========================================================================================================================================

bool CtiANSIDatalink::generate( CtiXfer &xfer )
{
   if( xfer.getInCountExpected() == 0 )
   {
      if( getRetries() > 0 )
      {
         if( _sendAck != true )
         {
            //do next step to get logged in
            switch( _currentState )
            {
            case identified:
               {
                  identify( xfer );
                  {
                     dout << endl;
                     CtiLockGuard< CtiLogger > doubt_guard( dout );
                     dout << RWTime::now() << " **Identify**" << endl;
                     dout << endl;
                  }

               }
               break;

            case negotiated:
               {
                  negotiate( xfer );
                  {
                     dout << endl;
                     CtiLockGuard< CtiLogger > doubt_guard( dout );
                     dout << RWTime::now() << " **Negotiate**" << endl;
                     dout << endl;
                  }

               }
               break;

            case timingSet:
               {
                  timing( xfer );
                  {
                     dout << endl;
                     CtiLockGuard< CtiLogger > doubt_guard( dout );
                     dout << RWTime::now() << " **Timing**" << endl;
                     dout << endl;
                  }

               }
               break;

            case loggedOn:
               {
                  logOn( xfer );
                  {
                     dout << endl;
                     CtiLockGuard< CtiLogger > doubt_guard( dout );
                     dout << RWTime::now() << " **Log On**" << endl;
                     dout << endl;
                  }

               }
               break;

            case secured:
               {
                  secure( xfer );
                  {
                     dout << endl;
                     CtiLockGuard< CtiLogger > doubt_guard( dout );
                     dout << RWTime::now() << " **Secure**" << endl;
                     dout << endl;
                  }

               }
               break;

            case authenticated:
               {
                  authenticate( xfer );
                  {
                     dout << endl;
                     CtiLockGuard< CtiLogger > doubt_guard( dout );
                     dout << RWTime::now() << " **Authenticate**" << endl;
                     dout << endl;
                  }

               }
               break;

            case request:
               {
                  sendRequest( xfer );
                  {
                     dout << endl;
                     CtiLockGuard< CtiLogger > doubt_guard( dout );
                     dout << RWTime::now() << " **Request**" << endl;
                     dout << endl;
                  }

               }
               break;

            case loggedOff:
               {
                  logOff( xfer );
                  {
                     dout << endl;
                     CtiLockGuard< CtiLogger > doubt_guard( dout );
                     dout << RWTime::now() << " **Log Off**" << endl;
                     dout << endl;
                  }

               }
               break;

            case terminated:
               {
                  terminate( xfer );
                  {
                     dout << endl;
                     CtiLockGuard< CtiLogger > doubt_guard( dout );
                     dout << RWTime::now() << " **Terminate**" << endl;
                     dout << endl;
                  }

               }
               break;

            case disconnected:
               {
                  disconnect( xfer );
                  {
                     dout << endl;
                     CtiLockGuard< CtiLogger > doubt_guard( dout );
                     dout << RWTime::now() << " **Disconnect**" << endl;
                     dout << endl;
                  }

               }
               break;
            }
         }
         else
         {
            doAck( xfer );
            _previousState = _currentState;

            //if we have more tables to ask for, we want to stay where we're at
            if( ( _tableIndex == _header->numTablesRequested ) || ( _currentState != request ) )
               _currentState = ( States )getNextState( _currentState );
         }
      }
   }

   return( _ready );
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
//=========================================================================================================================================

bool CtiANSIDatalink::decode( CtiXfer &xfer, BYTE *dataPtr, int &recieved )
{
   BYTE     *packet  = xfer.getInBuffer();
   ULONG    retrieve = 0;
   ULONG    send     = 0;
   ULONG    arrived  = 0;
   bool     done     = false;

   if( _sendAck != true )
   {
      arrived = xfer.getInCountActual();

      done = disassemblePacket( packet, retrieve, send, arrived );
      xfer.setInCountExpected( retrieve );
      xfer.setOutCount( send );

      if( _bytes > 1 )
      {
         memcpy( dataPtr, _ptrFromAppLayer, _bytes );
         recieved = _bytes;
         _bytes = 0;
      }
   }
   else
   {
      _sendAck = false;

      if( getSequence() != 0 )      //we're in the middle of a table (mult. packets)
      {
         arrived = xfer.getInCountActual();

         done = disassemblePacket( packet, retrieve, send, arrived );
         xfer.setInCountExpected( retrieve );
         xfer.setOutCount( send );

         if( _bytes > 1 )
         {
            memcpy( dataPtr, _ptrFromAppLayer, _bytes );
            recieved = _bytes;
            _bytes = 0;
         }
      }
   }
   return( done );
}

//=========================================================================================================================================
//anything that we need out of the packet (data from the device) gets pulled out here
//=========================================================================================================================================

bool CtiANSIDatalink::disassemblePacket( BYTE *packet, ULONG &retrieve, ULONG &send, ULONG arrived )
{
   bool  allDone = false;

   switch( _currentPos )
   {
   case ack:
      {
         if( ( packet[0] == ANSI_ACK ) && ( getExpectedBytes() == 1 ) )         //if we're only looking for the <ack>
         {
            //now we'll ask porter for the header
            retrieve = 6;
            send = 0;
            setExpectedBytes( 6 );
            setFailed( false );

            _previousPos = _currentPos;
            _currentPos = header;
            setRetries( MAX_RETRIES );
         }
         else
         {
            //we failed so let's try again if we're not out of retries
            if( getRetries() > 0 )
            {
               setRetries( getRetries() - 1 );
            }
            setFailed( true );
         }
      }
      break;

   case header:
      {
         if( ( packet[0] == ANSI_STP ) && ( getExpectedBytes() > 1 ) )       //if we're looking for the header
         {
            decodeHeader( packet );

            retrieve = getDataLen() + 2;     //+2 is to get the crc...
            send = 0;

            setFailed( false );
            _tempMsgLength = arrived;

            if(( _tempMsgStorage != NULL ) && ( _tempMsgLength < 511 ))
            {
               memcpy( _tempMsgStorage, packet, _tempMsgLength );
            }

            _previousPos = _currentPos;
            _currentPos = data;

            setRetries( MAX_RETRIES );
         }
         else
         {
            //we failed so let's try again if we're not out of retries
            if( getRetries() > 0 )
            {
               setRetries( getRetries() - 1 );
            }
            setFailed( true );
         }
      }
      break;

   case data:
      {
         if(( _tempMsgStorage != NULL ) && ( _tempMsgLength < 511 ))
         {
            memcpy( &_tempMsgStorage[_tempMsgLength], packet, arrived );
            _tempMsgLength += arrived;
         }

         if( checkCRC() != false )
         {
            decodeData( packet, arrived );

            setFailed( false );

            _tempMsgLength = 0;

            if( _currentState == request )
            {
               if( getSequence() == 0 )
               {
                  allDone = true;

                  if( _tableIndex < _header->numTablesRequested )
                     _tableIndex++;
               }
            }

            _previousPos = _currentPos;
            _currentPos = ack;

            retrieve = 0;
            send = 1;
            setRetries( MAX_RETRIES );
            _sendAck = true;
         }
         else
         {
            //we failed so let's try again if we're not out of retries
            if( getRetries() > 0 )
            {
               setRetries( getRetries() - 1 );
            }
            setFailed( true );
         }
      }
      break;
   }

   return( allDone ); //something
}

//=========================================================================================================================================
//here we yank the basic stuff out of the packet and store the results
//we decide if the packet is part of a multipacket transmission
//we decide if the packet is the first of a multipacket transmission
//we decide if we should toggle our flag (??)-why, we don't know
//we count how many packets are still enroute
//we count how many bytes are in the data portion of this packet
//=========================================================================================================================================

void CtiANSIDatalink::decodeHeader( BYTE *packet )
{
   BYTEUSHORT  flip;

   //reset; our logic depends on it
   setPacketPart( false );
   setPacketFirst( false );
   setSequence( 0x00 );
   setDataLen( 0 );

   packet++;
   packet++;

   if( packet[0] & 0x80 )
      setPacketPart( true );

   if( packet[0] & 0x60 )
      setPacketFirst( true );

   packet++;                                          //increment past the <cntl>

   if( packet[0] != 0x00 )
      setSequence( packet[0] );
   packet++;                                          //increment past the <seq_nbr>

   //get the number of bytes in the data part
   flip.ch[0] = packet[1];
   flip.ch[1] = packet[0];

   setDataLen( flip.sh );
}

//=========================================================================================================================================
//make the data portion of the packet we send to get the device's identity
//=========================================================================================================================================

void CtiANSIDatalink::identify( CtiXfer &xfer )
{
   BYTE        data;
   BYTEUSHORT  flip;

   data = ident;

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

void CtiANSIDatalink::negotiate( CtiXfer &xfer )
{
   BYTE        data[4];
   BYTEUSHORT  flip;

   //this is just for TESTING
   data[0] = negotiate_no_baud;
   data[1] = 0x4000;               //FIXME sizewise
   data[3] = 0x04;

   memset( xfer.getOutBuffer(), NULL, 100 );
   assemblePacket( xfer.getOutBuffer(), data, 4, 0 );

   flip.sh = crc( 4 + HEADER_LEN, xfer.getOutBuffer() );
   xfer.getOutBuffer()[4 + HEADER_LEN] = flip.ch[1];
   xfer.getOutBuffer()[4 + HEADER_LEN + 1] = flip.ch[0];

   xfer.setOutCount( 4 + HEADER_LEN + sizeof( USHORT ) );

   //we're just going to look for one byte first (the <ack>) then we'll know what's sitting out on the port for us...
   xfer.setInCountExpected( 1 );
   setExpectedBytes( 1 );
}

//=========================================================================================================================================
//FIXME: don't really know what the parameters should be set to
//=========================================================================================================================================

void CtiANSIDatalink::timing( CtiXfer &xfer )
{
   BYTE        data[5];
   BYTEUSHORT  flip;

   //this is just for TESTING
   data[0] = timing_setup;
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

void CtiANSIDatalink::logOn( CtiXfer &xfer )
{
   BYTE        data[13];
   BYTEUSHORT  flip;

   memset( &data[3], 0x00, 13 );

   //this is just for TESTING
   data[0] = logon;

   data[1] = 0x00;
   data[2] = 0x32;   //userID?

   data[8] = 0x41;   //Admin
   data[9] = 0x64;
   data[10] = 0x6d;
   data[11] = 0x69;
   data[12] = 0x6e;

//   memset( &data[3], 0x00, 12 );

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

void CtiANSIDatalink::secure( CtiXfer &xfer )
{
   BYTE        data[21];
   BYTEUSHORT  flip;
   BYTE        password[] = { 0xab, 0xc1, 0xab, 0xc2, 0xab, 0xc3, 0xab, 0xc4, 0xab, 0xc5, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };

   memset( data, NULL, 21 );
   data[0] = security;
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

void CtiANSIDatalink::authenticate( CtiXfer &xfer )
{
   BYTE        data;
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

void CtiANSIDatalink::sendRequest( CtiXfer &xfer )
{
   BYTE        data[10];
   BYTEUSHORT  flip;
   BYTEUSHORT  id;

   if( _ready != false )
   {
      //zero out data portion of the request out
      memset( data, 0x00, 10 );

//      data[0] = full_read;                                      //NOTE: we can't seem to win doing full reads on the KV2 (may be different for other meters)
      data[0] = pread_offset;

      id.sh = _tables[_tableIndex].tableID;

      data[1] = id.ch[1];
      data[2] = id.ch[0];
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
}

//=========================================================================================================================================
//FIXME: don't really know what the parameters should be set to
//=========================================================================================================================================

void CtiANSIDatalink::logOff( CtiXfer &xfer )
{
   BYTE        data;
   BYTEUSHORT  flip;

   data = logoff;

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

void CtiANSIDatalink::terminate( CtiXfer &xfer )
{
   BYTE        data;
   BYTEUSHORT  flip;

   data = term;

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

void CtiANSIDatalink::disconnect( CtiXfer &xfer )
{
   BYTE        data;
   BYTEUSHORT  flip;

   data = discon;

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
//here we start to chew the meat of the message
//=========================================================================================================================================

void CtiANSIDatalink::decodeData( BYTE *packet, ULONG numBytesGot )
{
   switch( _currentState )
   {
   case identified:
      identificationData( packet );
      break;

   case negotiated:
      negotiateData( packet );
      break;

   case timingSet:
      timingData( packet );
      break;

   case loggedOn:
      logOnData( packet );
      break;

   case secured:
      secureData( packet );
      break;

   case authenticated:
      authenticateData( packet );
      break;

   case request:
      processData( packet, numBytesGot );
      break;

   case loggedOff:
      logOffData( packet );
      break;

   case terminated:
      terminateData( packet );
      break;

   case disconnected:
      disconnectData( packet );
      break;
   }
}

//=========================================================================================================================================
//FIXME: all we're doing right now is looking for the protocol version to be right
//=========================================================================================================================================

void CtiANSIDatalink::identificationData( BYTE *packet )
{
   if( decipherResponse( packet ) != false )
   {
/*
      if( packet[1] == ANSI_C12_21 )

      if( packet[1] == ANSI_C12_18 )
*/
      _prot_version = packet[1];

      {
         //get info about features
         if( packet[4] == 0x00 )                //no authentication will be used
         {
            _authenticate = false;
         }
         else if( packet[4] == 0x01 )           //we'll use authentication
         {
            _authenticate = true;
            _authenticationType = packet[5];
            _algorithmID = packet[6];
         }
         else if( packet[4] == 0x02 )           //the device will even send us the value used by the auth. algorithm
         {
            _authenticate = true;
            _authenticationType = packet[5];
            _algorithmID = packet[6];

            //FIXME: well, finish
            //we need to grab the algorithm value but I don't know
            //how it is used, so I'm not sure how to store it
         }
      }
/*      else
      {
         setFailed( true );//WHOA! big time error (wrong protocol version)
      }
*/
   }
   else
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << RWTime::now() << " Identification Request" << endl;
      dout << endl;
   }
}

//=========================================================================================================================================
//FIXME: we're sending crap data to the device, so we don't really know if or what we should do here.  At the moment, all we're
//looking for is an <ok> at the beginning of the data in the packet
//=========================================================================================================================================

void CtiANSIDatalink::negotiateData( BYTE *packet )
{
   if( decipherResponse( packet ) != false )
   {
      packet++;                                                //increment past the <ok>
   }
   else
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << RWTime::now() << " Negotiate Request" << endl;
      dout << endl;
   }
}

//=========================================================================================================================================
//FIXME: we're sending crap data to the device, so we don't really know if or what we should do here.  At the moment, all we're
//looking for is an <ok> at the beginning of the data in the packet
//=========================================================================================================================================

void CtiANSIDatalink::timingData( BYTE *packet )
{
   if( decipherResponse( packet ) != false )
   {
      packet++;                                                //increment past the <ok>
   }
   else
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << RWTime::now() << " Timing Request" << endl;
      dout << endl;
   }
}

//=========================================================================================================================================
//FIXME: we're sending crap data to the device, so we don't really know if or what we should do here.  At the moment, all we're
//looking for is an <ok> at the beginning of the data in the packet
//=========================================================================================================================================

void CtiANSIDatalink::logOnData( BYTE *packet )
{
   if( decipherResponse( packet ) != false )
   {
      if( !_authenticate )
      {
         _connected = true;
      }
   }
   else
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << RWTime::now() << " Log-On Request" << endl;
      dout << endl;
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::secureData( BYTE *packet )
{
   if( decipherResponse( packet ) != false )
   {
      if( !_authenticate )
      {
         _connected = true;
      }
   }
   else
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << RWTime::now() << " Security Request" << endl;
      dout << endl;
   }
}

//=========================================================================================================================================
//FIXME: we're sending crap data to the device, so we don't really know if or what we should do here.  At the moment, all we're
//looking for is an <ok> at the beginning of the data in the packet
//=========================================================================================================================================

void CtiANSIDatalink::authenticateData( BYTE *packet )
{
   if( decipherResponse( packet ) != false )
   {
      packet++;                                                //increment past the <ok>
      _connected = true;//this may have to change locals
   }
   else
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << RWTime::now() << " Authenticate Request" << endl;
      dout << endl;
   }
}

//=========================================================================================================================================
//here, we should look at the number of bytes that we got and see if it is the same number as we thought we were asking for and then pass
//the table data up to the applayer
//we could also verify the checksum.... maybe later.
//=========================================================================================================================================

void CtiANSIDatalink::processData( BYTE *packet, ULONG bytesWeGot )
{
   _bytes = bytesWeGot-3;

   if( decipherResponse( packet ) != false )
   {
      if( _ptrFromAppLayer != NULL )
      {
         memcpy( _ptrFromAppLayer, packet, _bytes );   //we don't want to copy in the checksum or crc
      }
   }
   else
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << RWTime::now() << " Process Data ---" << endl;
      dout << endl;
   }
}

//=========================================================================================================================================
//FIXME: we're sending crap data to the device, so we don't really know if or what we should do here.  At the moment, all we're
//looking for is an <ok> at the beginning of the data in the packet
//=========================================================================================================================================

void CtiANSIDatalink::logOffData( BYTE *packet )
{
   if( decipherResponse( packet ) != false )
   {
      packet++;                                                //increment past the <ok>
   }
   else
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << RWTime::now() << " Log-Off Request" << endl;
      dout << endl;
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::terminateData( BYTE *packet )
{
   if( decipherResponse( packet ) != false )
   {
      packet++;                                                //increment past the <ok>

      if( _prot_version == ANSI_C12_18 )
         setDone( true );
   }
   else
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << RWTime::now() << " Terminate Request" << endl;
      dout << endl;
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::disconnectData( BYTE *packet )
{
   if( decipherResponse( packet ) != false )
   {
      packet++;                                                //increment past the <ok>
      setDone( true );
   }
   else
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << RWTime::now() << " Disconnect Request" << endl;
      dout << endl;
   }
}

//=========================================================================================================================================
//FIXME:
//we should post an error to a buffer someplace where someone who cares can look and do something with it... yeah.
//=========================================================================================================================================

bool CtiANSIDatalink::decipherResponse( BYTE *dataFromDevice )
{
   bool     proceed = false;
   BYTE     response = dataFromDevice[0];
   BYTE     msg;

   switch( response )
   {
   case ok:
      {
         proceed = true;
      }
      break;

   case err:
      {
         dout << endl;
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << RWTime::now() << " The KV2 responded: Service Request Rejected" << endl;

         msg = err;
      }
      break;

   case sns:
      {
         dout << endl;
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << RWTime::now() << " The KV2 responded: Service Not Supported" << endl;

         msg = sns;
      }
      break;

   case isc:
      {
         dout << endl;
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << RWTime::now() << " The KV2 responded: Insufficent Security Clearance" << endl;

         msg = isc;
      }
      break;

   case onp:
      {
         dout << endl;
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << RWTime::now() << " The KV2 responded: Operation Not Possible" << endl;

         msg = onp;
      }
      break;

   case iar:
      {
         dout << endl;
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << RWTime::now() << " The KV2 responded: Inappropriate Action Requested" << endl;

         msg = iar;
      }
      break;

   case bsy:
      {
         dout << endl;
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << RWTime::now() << " The KV2 responded: Device Busy" << endl;

         msg = bsy;
      }
      break;

   case dnr:
      {
         dout << endl;
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << RWTime::now() << " The KV2 responded: Data Not Ready" << endl;

         msg = dnr;
      }
      break;

   case dlk:
      {
         dout << endl;
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << RWTime::now() << " The KV2 responded: Data Locked" << endl;

         msg = dlk;
      }
      break;

   case rno:
      {
         dout << endl;
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << RWTime::now() << " The KV2 responded: Renegotiate Request" << endl;

         msg = rno;
      }
      break;

   case isss:
      {
         dout << endl;
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << RWTime::now() << " The KV2 responded: Invalid Service Sequence State" << endl;

         msg = isss;
      }
      break;
   }
   return proceed;
}

//=========================================================================================================================================
//we have to send an ack after the device sends us his data
//NOTE: this ack is NOT wrapped in a packet, it's all by itself!
//we also need to send an ack after we're logged off and we keep track of if we're connected and ready to do table requests
//=========================================================================================================================================

void CtiANSIDatalink::doAck( CtiXfer &xfer )
{
   BYTE  data;

   data = ANSI_ACK;

   if( sizeof( data ) < sizeof( xfer.getOutBuffer() ))
      memcpy( xfer.getOutBuffer(), &data, sizeof( data ) );

   xfer.setOutCount( sizeof( data ) );

   //we're either done with this table and ready to move on...
   if( getSequence() == 0 )
   {
      xfer.setInCountExpected( 0 );
      setExpectedBytes( 0 );
   }
   else    //...or we're looking to get more packets from a single request
   {
      _currentPos = header;
      xfer.setInCountExpected( 6 );
      setExpectedBytes( 6 );
   }

   if( _connected )
   {
      _ready = true;
   }
}

//=========================================================================================================================================
//once we're logged in, we'll peek into the data that was sent down from above and see what those bozos wanted
//=========================================================================================================================================

void CtiANSIDatalink::passRequest( BYTE *request, int len )
{
   int index = 0;
   _whatsRequested = CTIDBG_new BYTE[256];

   //refill our structs
   _header = new WANTS_HEADER;

   if(( _whatsRequested != NULL ) && ( len < 255 ))
      memcpy( _whatsRequested, request, len );

   if( _header != NULL )
   {
      memcpy( ( void *)_header, _whatsRequested, sizeof( WANTS_HEADER ) );
      _whatsRequested += sizeof( WANTS_HEADER );

      _tables = CTIDBG_new ANSI_TABLE_WANTS[_header->numTablesRequested];

      if( _tables != NULL )
      {
         for( index = 0; index < _header->numTablesRequested; index++ )
         {
            memcpy( ( void *)&_tables[index], _whatsRequested, sizeof( ANSI_TABLE_WANTS ) );
            _whatsRequested += sizeof( ANSI_TABLE_WANTS );
         }
      }
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::setDataLen( USHORT count )
{
   _lengthOfData = count;
}

//=========================================================================================================================================
//=========================================================================================================================================

USHORT CtiANSIDatalink::getDataLen( void )
{
   return _lengthOfData;
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

void CtiANSIDatalink::setFailed( bool failed )
{
   _didFail = failed;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiANSIDatalink::getFailed( void )
{
   return _didFail;
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::setDone( bool weDone )
{
   _allDone = weDone;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiANSIDatalink::getDone( void )
{
   return _allDone;
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIDatalink::setRetries( int trysLeft )
{
   _retries = trysLeft;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiANSIDatalink::getRetries( void )
{
   return _retries;
}

//=========================================================================================================================================
//=========================================================================================================================================


int CtiANSIDatalink::getNextState( States current )
{
   States   next;

   switch( current )
   {
   case identified:
      next = negotiated;
      break;

   case negotiated:
      next = timingSet;
      break;

   case timingSet:
      next = loggedOn;
      break;

   case loggedOn:
/*      {
         if( _authenticate != false )
            next = authenticated;
         else
            next = request;
      }*/
      next = secured;
      break;

   case secured:
//      next = authenticated;
      {
         if( _authenticate != false )
            next = authenticated;
         else
            next = request;
      }
      break;

   case authenticated:
      next = request;
      break;

   case request:
      next = loggedOff;
      break;

   case loggedOff:
      next = terminated;
      break;

   case terminated:
      {
         if( _prot_version == ANSI_C12_21 )
            next = disconnected;
      }
      break;

   case disconnected:
//      next = identified;
      break;

   }
   return( next );
}

//=========================================================================================================================================
//this method simply lops off the end of the message where the crc is and compares it to a CTIDBG_new calculation that is done
//=========================================================================================================================================

bool CtiANSIDatalink::checkCRC( void )
{
   BYTEUSHORT  CRC;
   bool        isOk = false;
   unsigned short temp;

   if( _tempMsgLength > 3 )
   {
      CRC.ch[0] = _tempMsgStorage[_tempMsgLength - 1];
      CRC.ch[1] = _tempMsgStorage[_tempMsgLength - 2];

      if( CRC.sh == crc(_tempMsgLength-2, _tempMsgStorage ) )
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


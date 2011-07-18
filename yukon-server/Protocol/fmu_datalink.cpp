
#include "precompiled.h"


/*-----------------------------------------------------------------------------*

*
* File:   fmu_datalink
*
* Date:   10/09/2006
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/fmu_datalink.cpp-arc  $
* REVISION     :  $Revision: 1.2 $                                                198
* DATE         :  $Date: 2007/05/31 21:41:19 $
*    History:
      $Log: fmu_datalink.cpp,v $
      Revision 1.2  2007/05/31 21:41:19  mfisher
      Reverted text in comments from "CTIDBG_new" back to "new"

      Revision 1.1  2007/01/26 20:20:18  jrichter
      FMU stuff for jess....


*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "fmu_datalink.h"
#include "fmu_application.h"
#include "guard.h"
#include "logger.h"

using namespace std;

//=========================================================================================================================================
//=========================================================================================================================================

CtiFMUDatalink::CtiFMUDatalink()
{
    _currentPacket = NULL;
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiFMUDatalink::init( void )
{
    _packetComplete = false;

   setPacketPart( false );
   setPacketFirst( false );
   setSequence( 0 );
   _currentPos = ack;
   _previousPos = ack;
   if (_currentPacket != NULL)
   {
       delete _currentPacket;
       _currentPacket = NULL;
   }
   _currentPacket = CTIDBG_new BYTE[512];
   _toggle = false;
   _packetBytesReceived = 0;
   //_identityByte = ANsI_RESERVED;  //0x00 default

}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiFMUDatalink::reinitialize( void )
{
    _packetComplete = false;

   setPacketPart( false );
   setPacketFirst( false );
   setSequence( 0 );
   _currentPos = ack;
   _previousPos = ack;
   _toggle = false;
   _packetBytesReceived = 0;
   //_identityByte = ANsI_RESERVED;  //0x00 default

   destroyMe();
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiFMUDatalink::destroyMe( void )
{
   if( _currentPacket != NULL )
   {
       delete []_currentPacket;
       _currentPacket = NULL;
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiFMUDatalink::~CtiFMUDatalink()
{
   destroyMe();
}

void CtiFMUDatalink::initializeForNewPacket( void )
{
    _currentPos = ack;
    _previousPos = ack;
    _packetComplete = false;
}



//=========================================================================================================================================
//create the packet shell and slip the data into it
//NOTE: someday, we may have to pass in large requests, so that's what the cntl int is for...
//=========================================================================================================================================

void CtiFMUDatalink::assemblePacket( BYTE *packetPtr, BYTE *dataPtr, USHORT cmd, USHORT count, INT seq, ULONG address )
{
   BYTEULONG  addr;

   //header + reserved
   packetPtr[0] = FMU_STP;
   addr.ul = address;

   packetPtr[1] = addr.ch[0];
   packetPtr[2] = addr.ch[1];
   packetPtr[3] = addr.ch[2];
   packetPtr[4] = addr.ch[3];

   //need to set bits 7 (start of msg flag) and 6 (end of msg flag)
   packetPtr[5] = seq;

   packetPtr[6] = cmd;

   //length
   packetPtr[7] = count;

   //add the data
   memcpy( &packetPtr[8], dataPtr, count );
}

//=========================================================================================================================================
//anything that we need out of the packet (data from the device) gets pulled out here
//=========================================================================================================================================

bool CtiFMUDatalink::continueBuildingPacket( CtiXfer &xfer, int aCommStatus )
{
   bool  retFlag = false;
   USHORT dataBufferLength=0;
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
             if( ( xfer.getInBuffer()[0] == ack ) && ( getExpectedBytes() == 1 ) )         //if we're only looking for the <ack>
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

       case header:
          {
             if( ( xfer.getInBuffer()[0] == FMU_STP ) && ( getExpectedBytes() > 1 ) )       //if we're looking for the header
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

              if( getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO )
              {
                  CtiLockGuard< CtiLogger > doubt_guard( dout );
                  dout <<"  ** DEBUG **** _packetBytesReceived " <<_packetBytesReceived <<endl;
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
                xfer.setInCountExpected( 0 );
                xfer.setOutCount( 0 );
                setExpectedBytes( 0 );

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

void CtiFMUDatalink::decodePacketHeader( )
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
//we have to send an ack after the device sends us his data
//NOTE: this ack is NOT wrapped in a packet, it's all by itself!
//we also need to send an ack after we're logged off and we keep track of if we're connected and ready to do table requests
//=========================================================================================================================================

void CtiFMUDatalink::buildAck( CtiXfer &xfer )
{
   BYTE  data;

   data = ack;
   memcpy( xfer.getOutBuffer(), &data, sizeof( data ) );
   xfer.setOutCount( 1 );
   xfer.setInCountExpected( 0 );
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiFMUDatalink::getPacketPart( void )
{
   return _multiPacketPart;
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiFMUDatalink::setPacketPart( bool isPart )
{
   _multiPacketPart = isPart;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiFMUDatalink::getPacketFirst( void )
{
   return _multiPacketFirst;
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiFMUDatalink::setPacketFirst( bool isFirst )
{
   _multiPacketFirst = isFirst;
}

//=========================================================================================================================================
//=========================================================================================================================================

BYTE CtiFMUDatalink::getSequence( void )
{
   return _sequence;
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiFMUDatalink::setSequence( BYTE seq )
{
   _sequence = seq;
}

//=========================================================================================================================================
//=========================================================================================================================================

ULONG CtiFMUDatalink::getExpectedBytes( void )
{
   return _bytesWeExpect;
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiFMUDatalink::setExpectedBytes( ULONG num )
{
   _bytesWeExpect = num;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiFMUDatalink::isPacketComplete( void )
{
   return _packetComplete;
}

CtiFMUDatalink &CtiFMUDatalink::setPacketComplete( bool aFlag )
{
   _packetComplete = aFlag;
   return *this;
}


//=========================================================================================================================================
//=========================================================================================================================================

BYTE *CtiFMUDatalink::getCurrentPacket( void )
{
   return _currentPacket;
}

int CtiFMUDatalink::getPacketBytesReceived( void )
{
   return _packetBytesReceived;
}

bool CtiFMUDatalink::getToggle( void )
{
    return _toggle;
}

//=========================================================================================================================================
//this method simply lops off the end of the message where the crc is and compares it to a new calculation that is done
//=========================================================================================================================================

bool CtiFMUDatalink::isCRCvalid( void )
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

unsigned short CtiFMUDatalink::crc16( unsigned char octet, unsigned short crc )
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

unsigned short CtiFMUDatalink::crc( int size, unsigned char *packet )
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


void CtiFMUDatalink::toggleToggle( void )
{
    if (_toggle == true)
        _toggle = false;
    else
        _toggle = true;
    return;
}

void CtiFMUDatalink::setIdentityByte(BYTE identityByte)
{
    _identityByte = identityByte;
    return;
}

BYTE CtiFMUDatalink::getIdentityByte( void )
{
    return _identityByte;
}

void CtiFMUDatalink::setFMUAddress(ULONG address)
{
    _fmuAddress = address;
    return;
}

ULONG CtiFMUDatalink::getFMUAddress( void )
{
    return _fmuAddress;
}



/*-----------------------------------------------------------------------------*
*
* File:   prot_fmu
*
* Date:   9/26/2006
*
* Author: Julie Richter
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2007/01/30 18:16:26 $
*
* HISTORY      :
* $Log: prot_fmu.cpp,v $
* Revision 1.2  2007/01/30 18:16:26  jrichter
* A little bit cleaned up...
*
* Revision 1.1  2007/01/26 19:56:14  jrichter
* FMU stuff for jess....
*
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <rw\re.h>
#include <boost/regex.hpp>
#undef mask_                // Stupid RogueWave re.h

#include "cparms.h"
#include "cticalls.h"
#include "logger.h"
#include "numstr.h"
#include "prot_fmu.h"
#include "utility.h"

namespace Cti{
namespace Protocol{
namespace fmuProtocol {

CtiProtocolFMU::CtiProtocolFMU() 
{
    _currentPacket = NULL;
}

CtiProtocolFMU::~CtiProtocolFMU()
{
    if (_currentPacket != NULL) 
    {
        delete _currentPacket;
        _currentPacket = NULL;
    }
}

void CtiProtocolFMU::init(void)
{
    _currentPacket = new BYTE[512];
    _packetBytesReceived = 0;
}

CtiProtocolFMU& CtiProtocolFMU::operator=(const CtiProtocolFMU& aRef)
{
    if(this != &aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** ACH!!! Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return *this;
}




string CtiProtocolFMU::asString(const CtiSAData &sa)
{
    string rstr;

    switch(sa._commandType)
    {
    case PutConfigRequest:
        {
            rstr += "SA 205 - config. Serial " + string(sa._serial) + " - ";
            break;
        }
    case ControlRequest:
        {
            
        }
    }

    if(sa._retransmit)
    {
        rstr += " Retransmission.";
    }


    return rstr;
}


//=========================================================================================================================================
//=========================================================================================================================================

CtiFMUApplication &CtiProtocolFMU::getApplicationLayer( void )
{
   return _fmuAppLayer;
}


int CtiProtocolFMU::generate(UCHAR *abuf, INT& buflen, UINT32 address, BYTE sequence, USHORT cmd, BYTE *optionalData)
{
    int retVal = NORMAL;
    BYTE        data;
    BYTEULONG   addr;
    BYTEUSHORT  flip;
    int  dataLen = 0;
    int headerLen = 8;
    BYTE *dataPtr = NULL;

    abuf[0] = (BYTE)FMU_STP;
    addr.ul = address;                        
                                              
    abuf[1] = addr.ch[0];                
    abuf[2] = addr.ch[1];                
    abuf[3] = addr.ch[2];                
    abuf[4] = addr.ch[3];                
                                               
    abuf[5] = 0xb0 | sequence;                       
                                              
    //need to set bits 7 (start of msg flag) a
    abuf[6] = cmd;                       

    switch (cmd) 
    {
        case ack:
        case nak:
        case commSync:
        {
            abuf[7] = dataLen;
            break;
        }
        case loCom:
        {
            string loComString = "put config something something";
            dataLen = loComString.size();
  
            //use string that is passed to us...
            abuf[7] = dataLen;
            //dataPtr = loComString;
            memcpy(dataPtr, (void*)&loComString, dataLen);
            break;
        }
        case timeSyncReq:
        {
            dataLen = 4;
            CtiTime tempTime = CtiTime();
            BYTEULONG t;
            t.ul = tempTime.seconds();
            dataPtr[0] = t.ch[0];
            dataPtr[1] = t.ch[1];
            dataPtr[2] = t.ch[2];
            dataPtr[3] = t.ch[3];
  
            //memcpy(dataPtr, (void*)&tempTime.seconds(), dataLen);
            break;
        }
  
        case timeRead:
        {
            abuf[7] = dataLen;
            break;
        }
        case dataReqCmd:
        {
            if (optionalData != NULL)
            {
                if (optionalData[0] == 0x01) 
                {
                    dataPtr = optionalData;
                    dataLen = 1;
                }
                else if (optionalData[0] == 0x02) 
                {
                    ULONG utcTime = (ULONG)optionalData[1];
                    int totalHours = (USHORT)optionalData[5];
                    if (totalHours > 8784)   //max 8784 = 366 * 24 (366 accomodates leap year) 
                    {
                        totalHours = 8784;
                        //change actual optionalData HERE!
                    }
                    dataPtr = optionalData;
                    dataLen = 8;
                }
            }
            abuf[7] = dataLen;


            break;
        }
        case resetMsgDataLog:
        {
            abuf[7] = dataLen;
            break;
        }
        case extDevDirCmd:
        {
            string loComString = "put config something something";
            dataLen = loComString.size();
  
            //use string that is passed to us...
            abuf[7] = dataLen;
            //dataPtr = loComString;
            memcpy(dataPtr, (void*)&loComString, dataLen);
            break;
        }
  
        case unSolMsg:
        case timeRsp:
        case dataReqRsp:
        case extDevDirRsp:
        {
            //not supported..these commands must be initiated by the master station.
            retVal = NOTNORMAL;
            break;
        }                                                                      
  
    }
  

    if (dataLen > 0) 
    {
        //add the data                           
        memcpy( &abuf[8], dataPtr, dataLen ); 
    }
    BYTEUSHORT  crc1;

    crc1.sh = crc( dataLen, abuf );
    abuf[headerLen + dataLen+1] = crc1.ch[0];
    abuf[headerLen + dataLen+2] = crc1.ch[1];

    buflen = headerLen + dataLen + 2;  //2 bytes for crc later


    //setExpectedResponse = ??

    return retVal;
}

bool CtiProtocolFMU::getStartOfMessageFlag(BYTE sequence)
{
    return (sequence & 0x80);
}
bool CtiProtocolFMU::getEndOfMessageFlag(BYTE sequence)
{
    return (sequence & 0x40);
}

BYTE* CtiProtocolFMU::getFMUMsgs(int index)
{
    int tempIndx = 0;
    int offset = 0;

    while (tempIndx < index) 
    {
        FMU_MSG_HEADER_STRUCT* head = (FMU_MSG_HEADER_STRUCT*) _fmuMsgs[offset];
        offset += (7 + head->msgLength);
        tempIndx++;
    }
    return &_fmuMsgs[offset];

}
UINT32 CtiProtocolFMU::getAddress()
{
    return _address;
}
BYTE CtiProtocolFMU::getSequence()
{
    return _seq;
}
BYTE CtiProtocolFMU::getCmd()
{
    return _cmd;
}
BYTE CtiProtocolFMU::getDataLen()
{
    return _dataLen;
}

void CtiProtocolFMU::setAddress(UINT32 add)
{
    _address = add;
    return;
}
void CtiProtocolFMU::setSequence(BYTE seq)
{
    _seq = seq;
    return;
}
void CtiProtocolFMU::setCmd(BYTE cmd)
{
    _cmd = cmd;
    return;
}
void CtiProtocolFMU::setDataLen(BYTE len)
{
    _dataLen = len;
    return;
}

int CtiProtocolFMU::decodeHeader( CtiXfer &xfer, int status )
{
    int retVal = NOTNORMAL;

    _packetBytesReceived = xfer.getInCountActual();

    if(  xfer.getInBuffer()[0] == FMU_STP  )//&& ( getExpectedBytes() > 1 ) )
    {
        memcpy(_currentPacket, xfer.getInBuffer(), _packetBytesReceived);
        setAddress((UINT32) xfer.getInBuffer()[1]);
        setSequence(xfer.getInBuffer()[5]);
        setCmd(xfer.getInBuffer()[6]);
        setDataLen(xfer.getInBuffer()[7]);
        retVal = NORMAL;
    } 
    return retVal;
}

int CtiProtocolFMU::decodeData( CtiXfer &xfer, int status )
{
    int retVal = NOTNORMAL;

    _packetBytesReceived = xfer.getInCountActual();

    memcpy(_currentPacket + 8, xfer.getInBuffer(), _packetBytesReceived);
    if(  _currentPacket[0] == FMU_STP  )//&& ( getExpectedBytes() > 1 ) )
    {
        if (isCRCvalid())
        {
            switch (getCmd()) 
            {
                case ack:
                case nak:
                {
                    //setNextState(IDLE);
                    break;
                }
                case commSync:
                case unSolMsg:
                {
                    break;
                }
                case timeRsp:
                {
                    break;
                }
                case dataReqRsp:
                {

                    if (getStartOfMessageFlag(getSequence()))// && !getEndOfMessageFlag()) 
                    {   
                        if (_fmuMsgs != NULL) 
                        {
                            delete _fmuMsgs;
                            _fmuMsgs = NULL;
                        }
                        _currentDataOffset = 0;

                    }
                   /* else if (getStartOfMessageFlag() && getEndOfMessageFlag()) 
                    {
                        

                    }
                     */
                    /*else if (!getStartOfMessageFlag() && getEndOfMessageFlag()) 
                    {

                    } */
                    //extract msgs from data portion..
                    {
                        memcpy(_fmuMsgs + _currentDataOffset, (void*)_currentPacket[8], getDataLen());
                        _currentDataOffset += getDataLen();

                        if (getEndOfMessageFlag(getSequence())) 
                        {
                            _expectMore = false;
                        }
                        else
                            _expectMore = true;
                    }

                    break;
                }
                case extDevDirRsp:
                {
                    break;
                }
                case loCom:
                case timeSyncReq:
                case timeRead:
                case dataReqCmd:
                case resetMsgDataLog:
                case extDevDirCmd:
                {
                    //not supported..these commands must be initiated by the master station.
                    break;
                }
                default:
                    break;
                    
            }

        }



    }


    return retVal;
}

//=========================================================================================================================================
//this method simply lops off the end of the message where the crc is and compares it to a CTIDBG_new calculation that is done
//=========================================================================================================================================

bool CtiProtocolFMU::isCRCvalid( void )
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

unsigned short CtiProtocolFMU::crc16( unsigned char octet, unsigned short crc )
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

unsigned short CtiProtocolFMU::crc( int size, unsigned char *packet )
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


}
}
}

/*-----------------------------------------------------------------------------*
*
* File:   dev_tnpp
*
* Date:   6/28/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_tnpp.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/07/08 18:04:05 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>

#include "cparms.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "dev_rtm.h"
#include "logger.h"
#include "porter.h"

#include "cmdparse.h"
#include "pt_base.h"
#include "pt_accum.h"
#include "port_base.h"

#include "pointtypes.h"
#include "connection.h"
#include "mgr_route.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_trace.h"
#include "numstr.h"
#include "verification_objects.h"
#include "dev_tnpp.h"

const char *CtiDeviceTnppPagingTerminal::_SOH                 = "\x001";
const char *CtiDeviceTnppPagingTerminal::_STX                 = "\x002";
const char *CtiDeviceTnppPagingTerminal::_ETX                 = "\x003";
const char *CtiDeviceTnppPagingTerminal::_EOT                 = "\x004";
const char *CtiDeviceTnppPagingTerminal::_ENQ                 = "\x005";
const char *CtiDeviceTnppPagingTerminal::_ACK                 = "\x006";
const char *CtiDeviceTnppPagingTerminal::_NAK                 = "\x021";
const char *CtiDeviceTnppPagingTerminal::_RS                  = "\x030";
const char *CtiDeviceTnppPagingTerminal::_CAN                 = "\x024";
const char *CtiDeviceTnppPagingTerminal::_zero_origin         = "0000";
const char *CtiDeviceTnppPagingTerminal::_zero_serial         = "00";
const char *CtiDeviceTnppPagingTerminal::_originAddress       = "0002";//address of this computer according to tnpp. Random!


CtiDeviceTnppPagingTerminal::CtiDeviceTnppPagingTerminal() 
{
    resetStates();
}

/*CtiDeviceTnppPagingTerminal::CtiDeviceTnppPagingTerminal(const CtiDeviceTnppPagingTerminal& aRef)
{
    //Dont think I will need this function.
}*/

CtiDeviceTnppPagingTerminal::~CtiDeviceTnppPagingTerminal()
{
    //Nothing special.

}

// operator = is not complete!! If it is ever needed, complete it.
/*
CtiDeviceTnppPagingTerminal& CtiDeviceTnppPagingTerminal::operator=(const CtiDeviceTnppPagingTerminal& aRef)
{//why would you ever do this, I dont know???
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _snpp = aRef.getTap();

        setPreviousState(aRef.getPreviousState());
        setCurrentState(aRef.getCurrentState());
        _command = Normal;

    }
    return *this;
}*/

INT CtiDeviceTnppPagingTerminal::decode(CtiXfer &xfer,INT commReturnValue)
{
    INT status = commReturnValue;

    try
    {
        switch( getCurrentState() )
        {
            case StateDecodeHandshake:
                {
                    if(xfer.getInCountActual()>=1)
                    {
                        //this is due to the un-buffered read just in case junk is in buffer on first read...
                        if(xfer.getInBuffer()[0] == *_EOT || 
                           xfer.getInBuffer()[xfer.getInCountActual()] == *_EOT)
                        {
                            //SUCCESS!!!!
                            status = Normal;
                            setCurrentState(getPreviousState());
                            break;
                        }
                        else
                        {
                            
                            status = UnknownError;
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - invalid data recived during hanshake when using " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            // perhaps we could clear out the buffer here instead of erroring, and then try again...
                            _command = Complete;
                            break;
                        }
                    }
                    else
                    {
                        status = ErrorPageNoResponse;
                        _command = Complete;
                        break;
                    }
                }
            case StateDoNothing:
                {
                    if(getPreviousState() == StateDoNothing)
                    {
                        //this is a loop? Bad.
                        status = FinalError;
                        _command = Complete;
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - invalid state reached in " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                    else
                    {
                        setCurrentState(getPreviousState());
                        setPreviousState(StateDoNothing);
                    }
                }
            case StateDecodeResponse:
                {   
                    if(xfer.getInCountActual() == 1)
                    {
                        if(xfer.getInBuffer()[0] == *_ACK) 
                        {//YAY!
                            _retryCount = 0;
                            if(getPreviousState() == StateGenerateZeroPacket)
                            {
                                _serialNumber = 1;
                                setCurrentState(StateGeneratePacket);
                                status = Normal;
                            }
                            else if(getPreviousState() == StateGeneratePacket)
                            {
                                _command = Complete;
                                status = Normal;
                            }
                                
                        }
                        else if(xfer.getInBuffer()[0] == *_NAK)//bad crc or other error, re-send!
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - NAK received from TNPP terminal: " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            _retryCount++;
                            if(_retryCount>2)
                            {
                                _retryCount = 0;
                                status = ErrorPageRS;
                                _command = Complete; //Transaction Complete
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint - NAK received 3 times, giving up " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                            }
                            else
                            {
                                setCurrentState(getPreviousState());//retry last send!!!!
                            }
                        }
                        else if(xfer.getInBuffer()[0] == *_CAN)//fatal error!!!
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - TNPP Device had a fatal error: " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            _retryCount = 0;
                            _command = Complete;
                            status = UnknownError;
                        }
                        else if(xfer.getInBuffer()[0] == *_RS)//buffer full
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - TNPP device buffer is full: " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            _retryCount = 0;
                            _command = Complete;
                            status = UnknownError;
                        }
                        else
                        {
                            status = UnknownError;
                            _command = Complete; //Transaction Complete
                        }

                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - No response from TNPP device: " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        status = ErrorPageNoResponse;
                        _command = Complete; //Transaction Complete
                    }
                    break;
                }
            case StateEnd:
                {
                    _command = Complete;
                    break;
                }
        }
    }
    catch(...)
    {//reset and throw to regular error catcher.
        resetStates();
        throw;
    }
    return status;
}

INT CtiDeviceTnppPagingTerminal::generate(CtiXfer  &xfer)
{
    INT status = NORMAL;
    try
    {
        switch( getCurrentState() )
        {
            case StateHandshakeInitialize:            // Look for any unsolicited ID= Message first... (no outbound CR's)
                {
                    xfer.setInBuffer(_inBuffer);
                    xfer.setOutBuffer(_outBuffer);
                    xfer.setBufferSize(100);
                    xfer.setInCountActual(&_inCountActual);
                    xfer.setNonBlockingReads(true);//if we have a lot of junk, this should clear it out
                    strncpy((char*)xfer.getOutBuffer(),_ENQ,5);
                    xfer.setOutCount( 1 );              // 1 character only.

                    xfer.setInCountExpected( 1 ); // Actually expecting 2 bytes in return, 1 return message, and 1 command message (ENQ, EOT)
                    xfer.setInTimeout( 1 );

                    setPreviousState(StateGenerateZeroPacket);  
                    setCurrentState(StateDecodeHandshake);

                    break;
                }
            case StateGenerateZeroPacket:
                {
                    xfer.setNonBlockingReads(false);
                    strncpy((char*)xfer.getOutBuffer(),_SOH,5);
                    strncat((char*)xfer.getOutBuffer(),CtiNumStr(_table.getDestinationAddress()).zpad(4).hex(),10);
                    strncat((char*)xfer.getOutBuffer(),CtiNumStr(_table.getInertia()).zpad(2).hex(),10);
                    strncat((char*)xfer.getOutBuffer(),_zero_origin,10);
                    strncat((char*)xfer.getOutBuffer(),_zero_serial,10);
                    strncat((char*)xfer.getOutBuffer(),_STX,10);
                    strncat((char*)xfer.getOutBuffer(),_ETX,10);

                    unsigned int crc = crc16(xfer.getOutBuffer(),strlen((char *)xfer.getOutBuffer()));
                    strncat((char*)xfer.getOutBuffer(),reinterpret_cast<char *>(&crc),10);

                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));
                    xfer.setInCountExpected( 1 );
                    xfer.setInTimeout( 1 );
                    setPreviousState(StateGenerateZeroPacket);
                    setCurrentState(StateDecodeResponse);
                    break;
                }
            case StateGeneratePacket:
                {
                    xfer.setNonBlockingReads(false);
                    strncpy((char*)xfer.getOutBuffer(),_SOH,5);
                    strncat((char*)xfer.getOutBuffer(),CtiNumStr(_table.getDestinationAddress()).zpad(4).hex(),10);
                    strncat((char*)xfer.getOutBuffer(),CtiNumStr(_table.getInertia()).zpad(2).hex(),10);
                    strncat((char*)xfer.getOutBuffer(),CtiNumStr(_table.getOriginAddress()).zpad(4).hex(),10);
                    strncat((char*)xfer.getOutBuffer(),getSerialNumber(),10);
                    strncat((char*)xfer.getOutBuffer(),_STX,10);
                    if((char)*_table.getIdentifierFormat()=='A')
                    {   //CAP PAGE
                        strncat((char*)xfer.getOutBuffer(),_table.getIdentifierFormat(),10);
                        strncat((char*)xfer.getOutBuffer(),_table.getPagerProtocol(),10);
                        strncat((char*)xfer.getOutBuffer(),_table.getPagerDataFormat(),10);
                        strncat((char*)xfer.getOutBuffer(),_table.getChannel(),10);
                        strncat((char*)xfer.getOutBuffer(),_table.getZone(),10);
                    }
                    else if((char)*_table.getIdentifierFormat()=='B')
                    {   //ID PAGE
                        strncat((char*)xfer.getOutBuffer(),_table.getIdentifierFormat(),10);                        
                    }
                    strncat((char*)xfer.getOutBuffer(),_table.getFunctionCode(),10);
                    strncat((char*)xfer.getOutBuffer(),CtiNumStr(_table.getPagerID()).zpad(8),10);
                    strncat((char*)xfer.getOutBuffer(),(const char *)_outMessage.Buffer.OutMessage,20);
                    strncat((char*)xfer.getOutBuffer(),_ETX,10);

                    unsigned int crc = crc16((const unsigned char *)xfer.getOutBuffer(),strlen((char *)xfer.getOutBuffer()));
                    strncat((char*)xfer.getOutBuffer(),reinterpret_cast<char *>(&crc),10);

					xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));
                    xfer.setInCountExpected( 1 );
                    xfer.setInTimeout( 1 );

                    setPreviousState(StateGeneratePacket);
                    setCurrentState(StateDecodeResponse);
                }
            case StateEnd:
                {//Failsafe
                    _command = Complete;
                    break;
                }
        }
    }
    catch(...)
    {//reset state settings and throw error to regular catcher.
        resetStates();
        throw;
    }
    return status;
}

int CtiDeviceTnppPagingTerminal::recvCommRequest( OUTMESS *OutMessage )
{
    int retVal = Normal;
    if( OutMessage )
    {
        _outMessage = *OutMessage;
        resetStates();
        _command = Normal;

    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - invalid OutMessage in CtiDeviceTnppPagingTerminal::recvCommResult() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retVal = MemoryError;
    }

    return retVal;
}

bool CtiDeviceTnppPagingTerminal::isTransactionComplete()
{
    return _command == Complete;
}

INT CtiDeviceTnppPagingTerminal::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT nRet = NORMAL;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*, INT ScanPriority)
     *   (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */

    switch(parse.getCommand())
    {
        case ControlRequest:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        case GetStatusRequest:
        case LoopbackRequest:
        case GetValueRequest:
        case PutValueRequest:
        case PutStatusRequest:
        case GetConfigRequest:
        case PutConfigRequest:
        default:
            {
                nRet = NoExecuteRequestMethod;
                /* Set the error value in the base class. */
                // FIX FIX FIX 092999
                retList.insert( CTIDBG_new CtiReturnMsg(getID(),
                                                        RWCString(OutMessage->Request.CommandStr),
                                                        RWCString("SNPP Devices do not support this command (yet?)"),
                                                        nRet,
                                                        OutMessage->Request.RouteID,
                                                        OutMessage->Request.MacroOffset,
                                                        OutMessage->Request.Attempt,
                                                        OutMessage->Request.TrxID,
                                                        OutMessage->Request.UserID,
                                                        OutMessage->Request.SOE,
                                                        RWOrdered()));

                if(OutMessage)                // And get rid of our memory....
                {
                    delete OutMessage;
                    OutMessage = NULL;
                }

                break;
            }
    }


    return nRet;
}

char* CtiDeviceTnppPagingTerminal::getSerialNumber()
{
    return CtiNumStr(_serialNumber).zpad(2).hex();
}

CtiDeviceTnppPagingTerminal::StateMachine CtiDeviceTnppPagingTerminal::getCurrentState()
{
    return _currentState;
}

CtiDeviceTnppPagingTerminal::StateMachine CtiDeviceTnppPagingTerminal::getPreviousState()
{
    return _previousState;
}

void CtiDeviceTnppPagingTerminal::setCurrentState(StateMachine newCurrentState)
{
    _currentState = newCurrentState;
}

void CtiDeviceTnppPagingTerminal::setPreviousState(StateMachine newPreviousState)
{
    _previousState = newPreviousState;
}

void CtiDeviceTnppPagingTerminal::resetStates()
{
    _currentState = StateHandshakeInitialize;
    _previousState = StateHandshakeInitialize;
    _command = Normal;
}

//Database Functions
void CtiDeviceTnppPagingTerminal::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    _table.getSQL(db, keyTable, selector);
}

void CtiDeviceTnppPagingTerminal::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    _table.DecodeDatabaseReader(rdr);
}

void CtiDeviceTnppPagingTerminal::getVerificationObjects(queue< CtiVerificationBase * > &work_queue)
{
    while( !_verification_objects.empty() )
    {
        work_queue.push(_verification_objects.front());

        _verification_objects.pop();
    }
}

unsigned int CtiDeviceTnppPagingTerminal::crc16( const unsigned char *data, int length )
{
    //  CRC-16 computation
    //    from http://www.programmingparadise.com/vs/?crc/crcfast.c.html
    //    original author unknown, so i figured it was okay to use.

    unsigned short tmp, crc;

    unsigned short crc16table[256] =
    {
        0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
        0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
        0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
        0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
        0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
        0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
        0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
        0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
        0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
        0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
        0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
        0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
        0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
        0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
        0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
        0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
        0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
        0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
        0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
        0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
        0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
        0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
        0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
        0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
        0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
        0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
        0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
        0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
        0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
        0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
        0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
        0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040
    };

    crc = 0x0000;

    for( int i = 0; i < length; i++ )
    {
        tmp = crc ^ (unsigned short)data[i];
        crc = (crc >> 8) ^ crc16table[tmp & 0x00FF];
    }

    return crc;
}


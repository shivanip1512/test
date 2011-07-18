/*-----------------------------------------------------------------------------*
*
* File:   dev_tnpp
*
* Date:   6/28/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_tnpp.cpp-arc  $
* REVISION     :  $Revision: 1.17.2.1 $
* DATE         :  $Date: 2008/11/13 17:23:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

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

using std::string;
using std::endl;
using std::list;
using std::queue;
using namespace boost::posix_time;

const char *CtiDeviceTnppPagingTerminal::_SOH                 = "\x001";
const char *CtiDeviceTnppPagingTerminal::_STX                 = "\x002";
const char *CtiDeviceTnppPagingTerminal::_ETX                 = "\x003";
const char *CtiDeviceTnppPagingTerminal::_EOT                 = "\x004";
const char *CtiDeviceTnppPagingTerminal::_ENQ                 = "\x005";
const char *CtiDeviceTnppPagingTerminal::_ACK                 = "\x006";
const char *CtiDeviceTnppPagingTerminal::_NAK                 = "\x015";
const char *CtiDeviceTnppPagingTerminal::_RS                  = "\x01E";
const char *CtiDeviceTnppPagingTerminal::_CAN                 = "\x018";
const char *CtiDeviceTnppPagingTerminal::_zero_origin         = "0000";
const char *CtiDeviceTnppPagingTerminal::_zero_serial         = "00";
const char *CtiDeviceTnppPagingTerminal::_type_golay          = "G";
const char *CtiDeviceTnppPagingTerminal::_type_flex           = "F";
const char *CtiDeviceTnppPagingTerminal::_type_pocsag         = "P";
const char *CtiDeviceTnppPagingTerminal::_type_pocsag_1200    = "p";
const char *CtiDeviceTnppPagingTerminal::_type_pocsag_2400    = "Q";
const char *CtiDeviceTnppPagingTerminal::_type_numeric        = "N";
const char *CtiDeviceTnppPagingTerminal::_type_alphanumeric   = "A";
const char *CtiDeviceTnppPagingTerminal::_type_beep           = "B";
const char *CtiDeviceTnppPagingTerminal::_function_1          = "D";
const char *CtiDeviceTnppPagingTerminal::_function_2          = "C";
const char *CtiDeviceTnppPagingTerminal::_function_3          = "B";
const char *CtiDeviceTnppPagingTerminal::_function_4          = "A";

//hard coded tnpp to golay capcodes
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_0           = 371;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_2           = 51664;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_4           = 2593;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_6           = 53687;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_8           = 54778;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_10          = 5858;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_12          = 6745;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_14          = 7897;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_16          = 8765;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_18          = 59676;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_20          = 10703;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_22          = 61346;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_24          = 62817;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_26          = 63145;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_28          = 14293;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_30          = 65321;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_32          = 66684;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_34          = 17250;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_36          = 68426;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_38          = 69471;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_40          = 20910;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_42          = 71737;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_44          = 72449;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_46          = 23211;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_48          = 74466;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_50          = 75971;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_52          = 76637;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_54          = 77729;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_56          = 28847;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_58          = 79429;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_60          = 80175;
const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_62          = 31632;
const int CtiDeviceTnppPagingTerminal::_a_capcode_max                    = 62;
const int CtiDeviceTnppPagingTerminal::_a_capcode_min                    = 0;

//*****************************************************************************
/* NOTE! The tnpp device must be set to immediately send all messages
** If this is not the case, this code may cause errors (sending 0 packet
** every time causes problems if buffering is attempted) -Jess
******************************************************************************/


CtiDeviceTnppPagingTerminal::CtiDeviceTnppPagingTerminal() :
_retryCount(0),
_serialNumber(0),
_transmissionCount(0),
_previousState(StateHandshakeInitialize),
_currentState(StateHandshakeInitialize),
_command(Normal)
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
                                dout << CtiTime() << " **** Checkpoint - invalid data recived during hanshake when using " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            // perhaps we could clear out the buffer here instead of erroring, and then try again...
                            _command = Fail;
                            break;
                        }
                    }
                    else
                    {
                        status = ErrorPageNoResponse;
                        _command = Fail;
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - no response received " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        break;
                    }
                }
            case StateDoNothing:
                {
                    if(getPreviousState() == StateDoNothing)
                    {
                        //this is a loop? Bad.
                        status = FinalError;
                        _command = Fail;
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - invalid state reached in " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                                _transmissionCount --;//decrement here so we count only good packets!
                                //setup verification object

                                if( !_outMessage.VerificationSequence )
                                {
                                    _outMessage.VerificationSequence = VerificationSequenceGen();
                                }
                                CtiVerificationWork *work = CTIDBG_new CtiVerificationWork(CtiVerificationBase::Protocol_SNPP, _outMessage, _outMessage.Request.CommandStr, reinterpret_cast<char *>(_outMessage.Buffer.OutMessage), seconds(700));//11.6 minutes
                                _verification_objects.push(work);

                                if(_transmissionCount>0)//this allows multiple transmissions!!!!
                                {
                                    _serialNumber++;
                                    setCurrentState(StateGeneratePacket);//no need for another function call, I know it is StateGeneratePacket here
                                }
                                else
                                {
                                    _command = Success;
                                }
                                status = Normal;
                            }

                        }
                        else if(xfer.getInBuffer()[0] == *_NAK)//bad crc or other error, re-send!
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - NAK received from TNPP terminal: " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            _retryCount++;
                            if(_retryCount>2)
                            {
                                _retryCount = 0;
                                status = ErrorPageRS;
                                _command = Fail; //Transaction Complete
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Checkpoint - NAK received 3 times, giving up " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                                dout << CtiTime() << " **** Checkpoint - TNPP Device had a fatal error: " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            _retryCount = 0;
                            _command = Fail;
                            status = UnknownError;
                        }
                        else if(xfer.getInBuffer()[0] == *_RS)//buffer full
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - TNPP device buffer is full: " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            _retryCount = 0;
                            _command = Fail;
                            status = UnknownError;
                        }
                        else
                        {
                            status = UnknownError;
                            _command = Fail; //Transaction Complete
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - TNPP Device had a fatal unknown error: " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - No response from TNPP device: " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        status = ErrorPageNoResponse;
                        _command = Fail; //Transaction Complete
                    }
                    break;
                }
            case StateEnd:
                {
                    _command = Fail;
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
                    strncat((char*)xfer.getOutBuffer(),CtiNumStr(_table.getDestinationAddress()).zpad(4).hex().toString().c_str(),10);
                    strncat((char*)xfer.getOutBuffer(),CtiNumStr(_table.getInertia()).zpad(2).hex().toString().c_str(),10);
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
                    strncat((char*)xfer.getOutBuffer(),CtiNumStr(_table.getDestinationAddress()).zpad(4).hex().toString().c_str(),10);
                    strncat((char*)xfer.getOutBuffer(),CtiNumStr(_table.getInertia()).zpad(2).hex().toString().c_str(),10);
                    strncat((char*)xfer.getOutBuffer(),CtiNumStr(_table.getOriginAddress()).zpad(4).hex().toString().c_str(),10);
                    strncat((char*)xfer.getOutBuffer(),getSerialNumber().c_str(),10);
                    strncat((char*)xfer.getOutBuffer(),_STX,10);
                    if (_table.getIdentifierFormat().c_str()[0] == 'A')
                    {   //CAP PAGE
                        strncat((char*)xfer.getOutBuffer(),_table.getIdentifierFormat().c_str(),10);
                        strncat((char*)xfer.getOutBuffer(),getPagerProtocol().c_str(),1);
                        strncat((char*)xfer.getOutBuffer(),getPagerDataFormat().c_str(),10);
                        strncat((char*)xfer.getOutBuffer(),_table.getChannel().c_str(),10);
                        strncat((char*)xfer.getOutBuffer(),_table.getZone().c_str(),10);
                    }
                    else if (_table.getIdentifierFormat().c_str()[0] == 'B')
                    {   //ID PAGE
                        strncat((char*)xfer.getOutBuffer(),_table.getIdentifierFormat().c_str(),10);
                    }
                    strncat((char*)xfer.getOutBuffer(),getFunctionCode().c_str(),10);

                    if(_outMessage.Sequence == TnppPublicProtocolGolay)
                    {

                        if (_table.getIdentifierFormat().c_str()[0] == 'B')//This really isnt allowed..
                        {   //ID PAGE
                            strncat((char*)xfer.getOutBuffer(),_zero_serial,2);
                        }
                        strncat((char*)xfer.getOutBuffer(),_zero_serial,2);
                        strncat((char*)xfer.getOutBuffer(),getGolayCapcode().c_str(),6);
                    }
                    else
                        strncat((char*)xfer.getOutBuffer(),_table.getPagerID().c_str(),10);
                    strncat((char*)xfer.getOutBuffer(),(const char *)_outMessage.Buffer.OutMessage,300);
                    strncat((char*)xfer.getOutBuffer(),_ETX,10);

                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()) + 2);//The crc can have the null char, that causes errors.

                    int len = strlen((char *)xfer.getOutBuffer());
                    unsigned int crc = crc16((const unsigned char *)xfer.getOutBuffer(),len);
                    xfer.getOutBuffer()[len] = crc;
                    xfer.getOutBuffer()[len+1] = crc>>8;

                    xfer.setInCountExpected( 1 );
                    xfer.setInTimeout( 1 );

                    setPreviousState(StateGeneratePacket);
                    setCurrentState(StateDecodeResponse);
                    break;
                }
            case StateEnd:
                {//Failsafe
                    _command = Fail;
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
        _transmissionCount = 1;
        if(_outMessage.Sequence == TnppPublicProtocolGolay)//golay message
        {
            if(_outMessage.Buffer.SASt._function == 8 || _outMessage.Buffer.SASt._function == 14 || _outMessage.Buffer.SASt._function == 9)
            {
                _transmissionCount = 2;
            }
        }

    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - invalid OutMessage in CtiDeviceTnppPagingTerminal::recvCommResult() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retVal = MemoryError;
    }

    return retVal;
}

bool CtiDeviceTnppPagingTerminal::isTransactionComplete()
{
    return _command == Fail || _command == Success;
}

INT CtiDeviceTnppPagingTerminal::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
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
                retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                                        string(OutMessage->Request.CommandStr).c_str(),
                                                        string("TNPP Devices do not support this command (yet?)").c_str(),
                                                        nRet,
                                                        OutMessage->Request.RouteID,
                                                        OutMessage->Request.MacroOffset,
                                                        OutMessage->Request.Attempt,
                                                        OutMessage->Request.GrpMsgID,
                                                        OutMessage->Request.UserID,
                                                        OutMessage->Request.SOE,
                                                        CtiMultiMsg_vec()));

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

string CtiDeviceTnppPagingTerminal::getSerialNumber()
{
    return CtiNumStr(_serialNumber).zpad(2).hex().toString();
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

string CtiDeviceTnppPagingTerminal::getPagerProtocol()
{
    if(_outMessage.Sequence == TnppPublicProtocolGolay)
    {
        if(strstr(_table.getPagerProtocol().c_str(),_type_golay) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Golay is not supported by this device, attempting anyway: " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        return _type_golay;
    }
    else
    {
        return _table.getPagerProtocol();
    }
}

string CtiDeviceTnppPagingTerminal::getPagerDataFormat()
{
    if(_outMessage.Sequence == TnppPublicProtocolGolay)
    {
        if(strstr(_table.getPagerProtocol().c_str(),_type_golay) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Golay is not supported by this device, attempting anyway: " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        return _type_beep;
    }
    else
    {
        return _table.getPagerDataFormat();
    }
}

/******************************************************************************
    Golay capcode:

    The Golay capcode comes to us in the format BBAABB, where AA is the group
    ID and BBBB is the device ID. The output format for TNPP is much more
    complicated. Why, I dont know. The output field is eight decimal characters
    of which we only use 5. To some extent it can be thought of as AABBB, but
    this is not correct. In the TNPP format, almost everything is multiplied
    by two, which means that AA(TNPP) needs not be above 50. Anything above 50 is a
    special code which means add 100 to the final BBBB(Golay) value. The two lowest BB(TNPP)
    characters are also multiplied by two to get the tens and ones portion
    of the BBBB code. There is another offset value, where, 50 is added to the BB field
    to shift the BBBB value by 100. Additionally if this 50 is added, an extra 100
    is added (so the offset effectively adds 200). Finally the first B in the AABBB basically
    is an add 400 character. If it is 2, the real value of BBBB is increased by 800.

    Examples! in format AABBB = AA BBBB (TNPP format then modified standard format)

    15048 = 30 0096  (96 = 48*2)                (30 = 15*2)
    65048 = 30 0196  (196 = 48*2+100)           (30 = 15*2)
    15148 = 30 0496  (496 = 48*2+400)           (30 = 15*2)
    15098 = 30 0296  (296 = 98*2+100)           (30 = 15*2)
    Combined!
    65198 = 30 0796  (796 = 98*2+100+100+400)   (30 = 15*2)

    The maximum value for AA is 64 and is always even. BBBB is also always even.

    Finally, the function code in TNPP may be appropriate for standard Golay,
    but for 2312D it is inverted (function 4 in TNPP = Function 1 in 2312D)

******************************************************************************/
string CtiDeviceTnppPagingTerminal::getGolayCapcode()
{
    int returnValue;
    //BBAABB
    _outMessage.Buffer.SASt._codeSimple[6] = '\0';

    int capcode = atoi(_outMessage.Buffer.SASt._codeSimple);
    int a = (capcode%10000-capcode%100)/100;//find AA portion of capcode

    //A should never be odd.

    //Account for the extended codes that all use AAAAAA instead of BBAABB //FIX_ME JESS
    if(_outMessage.Buffer.SASt._function>4 && a<=_a_capcode_max && a>=_a_capcode_min && a%2!=1)
    {

        returnValue = getExtendedFunctionCapcode(a);//takes the "a" portion and returns the capcode
    }
    else
    {
        //parse out values
        int bHigh = capcode/10000;
        int bLow = capcode%100;
        int bTotal = bHigh*100 + bLow;

        returnValue = a/2;
        if(bHigh % 2)//odd so we need the +100 shift from AA(TNPP)
        {
            returnValue += 50;
        }
        returnValue *= 1000;//shift AA to the appropriate location! AABBB

        returnValue += 100*(bTotal/400); //Number of 400's we need * 100 to place the value in the correct location

        returnValue += bLow/2;//No shifting for this one!
        if((bTotal%400-bLow)>=200)//This can only be 100, 200, 300, 0
        {
            //if there is more than 200 remaining, the AA offset +50 is not enough, we need +200!
            returnValue += 50;//give me a 50 offset, adds 200 to final as noted above!
        }
    }

    return CtiNumStr(returnValue).zpad(6).toString();
}

int CtiDeviceTnppPagingTerminal::getExtendedFunctionCapcode(int a)
{
    switch(a)
    {
        case 0:
            return _ext_function_capcode_0;
        case 2:
            return _ext_function_capcode_2;
        case 4:
            return _ext_function_capcode_4;
        case 6:
            return _ext_function_capcode_6;
        case 8:
            return _ext_function_capcode_8;
        case 10:
            return _ext_function_capcode_10;
        case 12:
            return _ext_function_capcode_12;
        case 14:
            return _ext_function_capcode_14;
        case 16:
            return _ext_function_capcode_16;
        case 18:
            return _ext_function_capcode_18;
        case 20:
            return _ext_function_capcode_20;
        case 22:
            return _ext_function_capcode_22;
        case 24:
            return _ext_function_capcode_24;
        case 26:
            return _ext_function_capcode_26;
        case 28:
            return _ext_function_capcode_28;
        case 30:
            return _ext_function_capcode_30;
        case 32:
            return _ext_function_capcode_32;
        case 34:
            return _ext_function_capcode_34;
        case 36:
            return _ext_function_capcode_36;
        case 38:
            return _ext_function_capcode_38;
        case 40:
            return _ext_function_capcode_40;
        case 42:
            return _ext_function_capcode_42;
        case 44:
            return _ext_function_capcode_44;
        case 46:
            return _ext_function_capcode_46;
        case 48:
            return _ext_function_capcode_48;
        case 50:
            return _ext_function_capcode_50;
        case 52:
            return _ext_function_capcode_52;
        case 54:
            return _ext_function_capcode_54;
        case 56:
            return _ext_function_capcode_56;
        case 58:
            return _ext_function_capcode_58;
        case 60:
            return _ext_function_capcode_60;
        case 62:
            return _ext_function_capcode_62;
        default:
            return a;
    }
}

/******************************************************************************
*   I thought it would be worth noting the meaning behind the function codes.
*   Function 1 consists of A,B, Function 2 is A, NOT B. Function 3 is NOTA-B
*   Function 4 is NOT A,NOT B. Function 8 is NOTA, A, A, A(two commands)
*   Function 9 is A, NOTA, NOTA,NOTA. Function 14 is A,A,NOTA,A
*   Functions 1,2,3 are shed loads 1,2,3 respectively. Functions
*   4 cancels cold load pickup(same as restore). Function 8 is shed all loads.
*   Function 9 Restore all loads. Function 14 blink test LED for 8.5 minutes.
*
*   NOTA = A+1, NOTB = B+1.
*
*   Note as always the tnpp function code used here is not the code above. Here,
*   only functions 1-4 are used, and TNPP code 1 is function code 4, function code
*   3 is TNPP code 2, and so on...
******************************************************************************/
string CtiDeviceTnppPagingTerminal::getFunctionCode()
{
    if(_outMessage.Sequence == TnppPublicProtocolGolay)
    {
        switch(_outMessage.Buffer.SASt._function)
        {
            case 0:
                {
                    return _function_1;//really I dont know if this will work, so try not to send it!
                }
            case 1:
                {
                    return _function_1;
                }
            case 2:
                {
                    return _function_2;
                }
            case 3:
                {
                    return _function_3;
                }
            case 4:
                {
                    return _function_4;
                }

            case 8:
            case 9:
            case 14:
                {
                    return getExtendedFunctionCode();
                }
            default:
                return _function_1;
        }
    }
    else
    {
        return _table.getFunctionCode();
    }

}

int CtiDeviceTnppPagingTerminal::sendCommResult(INMESS *InMessage)
{
    // We are not interested in changing this return value here!
    // Must override base as we have no protocol.
    return NoError;
}


string CtiDeviceTnppPagingTerminal::getExtendedFunctionCode()
{
    _outMessage.Buffer.SASt._codeSimple[6] = '\0';

    int capcode = atoi(_outMessage.Buffer.SASt._codeSimple);
    int a = (capcode%10000-capcode%100)/100;//find AA portion of capcode

    switch(a)
    {

        //3,1 group
        case 0:
        case 6:
        case 8:
        case 10:
        case 14:
        case 18:
        case 22:
        case 24:
        case 32:
        case 36:
        case 44:
        case 52:
        case 56:
        case 60:
        case 62:
            {
                if(_outMessage.Buffer.SASt._function == 8)
                {
                    if(_transmissionCount == 2)
                        return _function_3;//Function Code 3
                    else
                        return _function_1;//Function Code 1
                }
                else if(_outMessage.Buffer.SASt._function == 9)
                {
                    if(_transmissionCount == 2)
                        return _function_2;//Function Code 3
                    else
                        return _function_4;//Function Code 1
                }
                else//function 14
                {
                    if(_transmissionCount == 2)
                        return _function_1;//Function Code 3
                    else
                        return _function_3;//Function Code 1
                }
                break;
            }

        //4,2 group
        case 2:
        case 4:
        case 12:
        case 16:
        case 20:
        case 26:
        case 28:
        case 30:
        case 34:
        case 38:
        case 40:
        case 42:
        case 46:
        case 48:
        case 50:
        case 54:
        case 58:
            {
                if(_outMessage.Buffer.SASt._function == 8)
                {
                    if(_transmissionCount == 2)
                        return _function_4;//Function Code 3
                    else
                        return _function_2;//Function Code 1
                }
                else if(_outMessage.Buffer.SASt._function == 9)
                {
                    if(_transmissionCount == 2)
                        return _function_1;//Function Code 3
                    else
                        return _function_3;//Function Code 1
                }
                else//function 14
                {
                    if(_transmissionCount == 2)
                        return _function_2;//Function Code 3
                    else
                        return _function_4;//Function Code 1
                }
                break;
            }
        default:
            return _function_1;
    }
}

//Database Functions
string CtiDeviceTnppPagingTerminal::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                     "YP.disableflag, DV.deviceid, DV.alarminhibit, DV.controlinhibit, CS.portid, "
                                     "DUS.phonenumber, DUS.minconnecttime, DUS.maxconnecttime, DUS.linesettings, "
                                     "DUS.baudrate, TNP.inertia, TNP.destinationaddress, TNP.originaddress, "
                                     "TNP.identifierformat, TNP.protocol, TNP.dataformat, TNP.channel, TNP.zone, "
                                     "TNP.functioncode, TNP.pagerid "
                                   "FROM Device DV, DeviceTNPPSettings TNP, DeviceDirectCommSettings CS, "
                                     "YukonPAObject YP LEFT OUTER JOIN DeviceDialupSettings DUS ON "
                                     "YP.paobjectid = DUS.deviceid "
                                   "WHERE YP.paobjectid = TNP.deviceid AND YP.paobjectid = DV.deviceid AND "
                                     "YP.paobjectid = CS.deviceid";

    return sqlCore;
}

void CtiDeviceTnppPagingTerminal::DecodeDatabaseReader(Cti::RowReader &rdr)
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


//===================================================================================================================
//===================================================================================================================
string CtiDeviceTnppPagingTerminal::getBaseFromEncodedGolayCapcode(string &golayString)
{
    int capcode = atoi(golayString.c_str());

    if(capcode %2)
    {
        capcode -= 1;//capcode is now even!!!
    }

    if(((capcode%10000-capcode%100)/100)%2)//if A portion (BBAABB) is odd.
    {
        capcode -= 100;
    }

    return CtiNumStr(capcode).zpad(6);

}

//===================================================================================================================
//===================================================================================================================
int CtiDeviceTnppPagingTerminal::getFunctionfromEncodedGolayCapcode(string &golayString)
{
    int capcode = atoi(golayString.c_str());
    int functionCheck = 0;

    if(capcode %2)
    {
        functionCheck +=1;
    }

    if(((capcode%10000-capcode%100)/100)%2)//if A portion (BBAABB) is odd.
    {
        functionCheck +=100;
    }

    switch(functionCheck)
    {
        case 1:
            {
                return 2;//A,NOTB
            }
        case 100:
            {
                return 3;//NOTA,B
            }
        case 101:
            {
                return 4;//NOTA, NOTB
            }
        default:
            {
                return 1;//A,B
            }
    }
}

//===================================================================================================================
//===================================================================================================================
string CtiDeviceTnppPagingTerminal::createEncodedCapcodeFromBaseAndFunction(string &golayString, int function)
{
    int capcode = atoi(golayString.c_str());

    switch(function)
    {
        case 1:
            {
                break;
            }
        case 2:
            {
                capcode+=1;//NOTA,B
                break;
            }
        case 3:
            {
                capcode+=100;//NOTA, NOTB
                break;
            }
        case 4:
            {
                capcode+=101;//NOTA, NOTB
                break;
            }
        default:
            {
                break;
            }
    }

    return CtiNumStr(capcode).zpad(6);

}


/* It is possible that a function has the wrong value. If it does, I apologize.
    They are always in sets of 3,1 or 4,2.

A word  Tnpp Capcode    Functions
0           371         3   1
2           51664       4   2
4           2593        4   2
6           53687       3   1
8           54778       3   1
10          5858        3   1
12          6745        4   2
14          7897        3   1
16          8765        4   2
18          59676       3   1
20          10703       4   2
22          61346       3   1
24          62817       3   1
26          63145       4   2
28          14293       4   2
30          65321       4   2
32          66684       3   1
34          17250       4   2
36          68426       3   1
38          69471       4   2
40          20910       4   2
42          71737       4   2
44          72449       3   1
46          23211       4   2
48          74466       4   2
50          75971       4   2
52          76637       3   1
54          77729       4   2
56          28847       3   1
58          79429       4   2
60          80175       3   1
62          31632       3   1

*/

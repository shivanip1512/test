/*-----------------------------------------------------------------------------*
*
* File:   dev_pagerreceive
*
* Date:   5/22/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_pagerreceive.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/06/29 19:51:17 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"
#pragma warning( disable : 4786)


#include <map>
#include <string>
using namespace std;

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

#include "dev_pagingreceiver.h"

const char *CtiDevicePagingReceiver::_change_mode = "J2";
const char *CtiDevicePagingReceiver::_read= "R";
const char *CtiDevicePagingReceiver::_save = "T";
const char *CtiDevicePagingReceiver::_stop = "J1";
const char *CtiDevicePagingReceiver::_char_cr_lf= "\r\n";
const char *CtiDevicePagingReceiver::_capcode_number = "B ";
const char *CtiDevicePagingReceiver::_capcode_config = "A00 44 04 00 ";
const char *CtiDevicePagingReceiver::_header = " `@\"";
const char *CtiDevicePagingReceiver::_footer = "\r\n.\r\n";

const vector<const char*> CtiDevicePagingReceiver::_commandVector = CtiDevicePagingReceiver::initCommandVector();

CtiDevicePagingReceiver::CtiDevicePagingReceiver() :
_retryCount(0),
_retryTime(second_clock::universal_time())
{
    resetStates(true);
}

CtiDevicePagingReceiver::~CtiDevicePagingReceiver()
{
}

INT CtiDevicePagingReceiver::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    //So far I am doing nothing with the result...
    resetScanPending();
    return NORMAL;
}

int CtiDevicePagingReceiver::sendCommResult(INMESS *InMessage)
{
    InMessage->EventCode = NORMAL;
    InMessage->DeviceID = getID();
    return NORMAL;
}

INT CtiDevicePagingReceiver::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan general");


    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString("scan general");
    pReq->setMessagePriority(ScanPriority);

    status = ExecuteRequest(pReq, newParse, OutMessage, vgList, retList, outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}

INT CtiDevicePagingReceiver::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT nRet = Normal;
    OUTMESS *OutMTemp = CTIDBG_new OUTMESS(*OutMessage);
    OutMTemp->Port     = getPortID();
    OutMTemp->DeviceID = getID();
    OutMTemp->TargetID = getID();
    OutMTemp->EventCode = RESULT;//send a return message!
    outList.insert( OutMTemp );
    return nRet;

}

int CtiDevicePagingReceiver::recvCommRequest(OUTMESS *OutMessage)
{
    _command = Normal;
    return Normal;//could do something with this someday.
}

void CtiDevicePagingReceiver::getVerificationObjects(queue< CtiVerificationBase * > &work_queue)
{
    while( !_verification_objects.empty() )
    {
        work_queue.push(_verification_objects.front());

        _verification_objects.pop();
    }
}

int CtiDevicePagingReceiver::generate(CtiXfer &xfer)
{
    int status = NORMAL;

    try
    {
        switch(getCurrentState())
        {
            case DoInit:
                {
                    if(_retryCount >3)
                    {
                        _retryCount = 0;
                        _retryTime = second_clock::universal_time() + minutes(20);
                        setCurrentState(Wait);
                        break;
                    }
                    else
                    if(_retryTime>second_clock::universal_time())
                    {
                        setCurrentState(Wait);
                        break;
                    }
                    _retryCount++;
                    _hadHeader = false;
                    _capcodeCount = 0;
                    xfer.setInBuffer(_inBuffer);
                    xfer.setBufferSize(DEV_PAGERRECEIVE_IN_BUFFER_SIZE);
                    xfer.setOutBuffer(_outBuffer);
                    xfer.setInCountActual(&_inCountActual);
                    xfer.setNonBlockingReads(true);//try to flush buffer.
                    xfer.setInCountExpected(0);//no delay if there are 0 bytes available.
                    
                    _cmdVectorIterator = _commandVector.begin();//This must be set to the first value to be sent
                    strncpy((char *)xfer.getOutBuffer(),_change_mode,25);//Change Mode command
                    strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));

                    setCurrentState(ReadResult);
                    setPreviousState(DoInit);
                    break;
                }
            case SendWhatStringPointerPointsTo://This is self commenting code in action!!!!
                {
                    _cmdVectorIterator++;
                    xfer.setInCountExpected(2);
                    xfer.setNonBlockingReads(false);//We want to wait for the 2 bytes if they arent there yet.

                    strncpy((char *)xfer.getOutBuffer(),*_cmdVectorIterator,25);//enable configuration.
                    strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));

                    setCurrentState(ReadResult);
                    setPreviousState(SendWhatStringPointerPointsTo);
                    if(_cmdVectorIterator == &_commandVector.back())
                    {
                        setPreviousState(SendCapcodeNumber);// ADD_CODE_HERE
                        break;
                    }                  
                    break;
                }
            case SendCapcodeNumber:
                {
                    xfer.setInCountExpected(2);
                    strncpy((char*)xfer.getOutBuffer(),_capcode_number,10);
                    strncat((char*)xfer.getOutBuffer(),getFormattedHexCapcodeNumber(_capcodeCount).c_str(),20);
                    strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));

                    setCurrentState(ReadResult);
                    setPreviousState(SendCapcodeConfig);
                    break;
                }
            case SendCapcodeConfig:
                {
                    char buffer[10];
                    xfer.setInCountExpected(2);
                    strncpy((char*)xfer.getOutBuffer(),_capcode_config,20);
                    strncat((char*)xfer.getOutBuffer(),CtiNumStr(_capcodeCount),10);
                    strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));

                    setCurrentState(ReadResult);
                    setPreviousState(SendCapcodeNumber);
                    _capcodeCount ++;
                    if(_capcodeCount>15)
                        setPreviousState(SendFrequency);
                    break;
                }
            case SendFrequency:
                {
                    xfer.setInCountExpected(2);
                    strncpy((char*)xfer.getOutBuffer(),getFormattedFrequency().c_str(),20);
                    strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));

                    setCurrentState(ReadResult);
                    setPreviousState(SendSave);
                    break;
                }
            case SendSave:
                {
                    xfer.setInCountExpected(2);
                    strncpy((char*)xfer.getOutBuffer(),_save,20);
                    strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));

                    setCurrentState(ReadResult);
                    setPreviousState(SendQuit);
                    break;
                }
            case SendQuit:
                {
                    xfer.setInCountExpected(2);
                    strncpy((char*)xfer.getOutBuffer(),_stop,20);
                    strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));

                    setCurrentState(ReadResult);
                    setPreviousState(Done);
                    break;
                }
            case ReadAndCheck:
                {
					xfer.setInBuffer(_inBuffer);
                    xfer.setOutBuffer(_outBuffer);
                    xfer.setBufferSize(DEV_PAGERRECEIVE_IN_BUFFER_SIZE);
					xfer.setOutCount(0);
                    xfer.setInCountActual(&_inCountActual);
                    xfer.setNonBlockingReads(true);//reads dont block
                    xfer.setInCountExpected(0);//no delay.

                    setPreviousState(InitializeAndRead);
                    setCurrentState(ReadAndCheck);
                    break;

                }
			default:
				{
					resetStates();
					_command = Complete;
				}
        }
    }
    catch(...)
    {
        //Do catch stuff here ADD_CODE_HERE
		resetStates();
		_command = Complete;
		status = FinalError;
        throw;
    }
    return status;
}

int CtiDevicePagingReceiver::decode(CtiXfer &xfer, int commReturnValue)
{
    int status = commReturnValue;
    int headLocation = 0;
    int footLocation = 0;
    string reportString;
    try 
    {
        switch(getCurrentState())
        {
            case ReadResult:
                {
                    //This is to read result of commands that are sent to the unit
                    //Note! The command that is sent between generate and decode does not generate the response,
                    //the previous command does!
                    if(getPreviousState() == DoInit)
                    {
                        status = NORMAL;
                        setCurrentState(SendWhatStringPointerPointsTo);
                    }
                    else
                    if(xfer.getInCountActual()>=2)
                    {
                        if(strstr((char*)xfer.getInBuffer(), "T>") != NULL)
                        {
                            status = NORMAL;
                            setCurrentState(getPreviousState());

                        }
                        else
                        {
                            status = ErrorPageNoResponse;//this isnt the best error, but it works (sort of)
                            _command = Complete;
                            resetStates(true);
                            break;
                        }
                    }
                    else
                    {
                        status = ErrorPageNoResponse;
                        _command = Complete;
                        resetStates(true);
                        break;
                    }

                    if(getPreviousState()==Done)
                    {
                        status = Normal;
                        _command = Complete;
                        resetStates();
                    }
                    break;
                }
            case ReadAndCheck:
                {
                    if(xfer.getInCountActual())//if we read more, add to the string
                        _messageString.append((char *)xfer.getInBuffer(),xfer.getInCountActual());

                    if((headLocation = _messageString.find(_header,0)) != string::npos)
                    { // <space>`@" is header string
                        if((footLocation = _messageString.find(_footer,headLocation)) != string::npos)
                        { //find footer
                            //Header and footer both found, form string if possible!
                            _hadHeader = false;
                            if(footLocation-headLocation>4)
                            {
                                CtiVerificationReport *report;
                                reportString = _messageString.substr(headLocation+4,footLocation-headLocation-4);
                                report = CTIDBG_new CtiVerificationReport(CtiVerificationBase::Protocol_SNPP, getID(), reportString.c_str(), second_clock::universal_time());
                                _verification_objects.push(report);

                                //remove current message from the string. 
                                _messageString = _messageString.substr(footLocation+5);//Dont remove more than the footer, that would cause errors.

                                if(_messageString.length()>9)
                                {
                                    //there could be another message in the buffer, check for it before we exit.
                                    
                                    
                                }
                                else
                                {
                                    _command = Complete;
                                    status = Normal;
                                    resetStates();

                                }
                            }
                            else
                            {
                                //My Parser screwed up somewhere?? It probably didnt remove a footer that should have been removed. 
                                // Im going to strip off all of the data before the footer and let the function re-try
                                _messageString = _messageString.substr(footLocation+4);
                            }
                        }
                        else
                        {
                            //header but no footer...
                            //Is message not fully received? Wait for rest to come in. But.. if theres a header then
                            //more than 300 chars, junk is in our buffer!
                            if((_messageString.length()-headLocation)>300)//I feel confident hard coding this number due to pager restrictions.
                            {
                                //Strip off the header, let function re-run. Any more junk will be cleaned off in next function run.
                                _messageString = _messageString.substr(headLocation+4);
                            }
                            else
                            if(_hadHeader = true)
                            {
                                //Strip off the header, let function re-run. Any more junk will be cleaned off in next function run.
                                _messageString = _messageString.substr(headLocation+4);//header with no footer for a full loop, no footer is coming.
								_hadHeader = false;
                            }
                            else
                            {
								//This assumes there is enough buffer space to read in an entire message in 2 reads!!! 
                                _messageString = _messageString.substr(headLocation);//take off anything before headLocation
                                _hadHeader = true;
                                _command = Complete;
                                status = Normal;
                                resetStates();
                                break;
                            }
                             
                            
                        }
                    }
                    else
                    {
                        _hadHeader = false;
                        //Nothing received yet. Check if string has junk, and clear it out (we should only need last 4 bytes to form full header..
                        if(_messageString.length()>4)
                        {
                            _messageString = _messageString.substr(_messageString.length()-4);
                        }
                        _command = Complete;
                        status = Normal;
                        resetStates();
                    }
    
                    setPreviousState(InitializeAndRead);
                    setCurrentState(ReadAndCheck);
                    break;
                }
            case Done:
                {
                    _command = Complete;
                    resetStates();
                    break;
                }
            case Wait:
                {
                    _command = Complete;
                    resetStates(true);
                    break;
                }
			default:
				{
					resetStates();
					_command = Complete;
				}
        }
    }
    catch(...)
    {
        resetStates();
		_command = Complete;
		status = FinalError;
        throw;
    }
    return status;
}

//all static control options are set from this vector.
vector<const char*> CtiDevicePagingReceiver::initCommandVector()
{
    const char *stringData;
    vector<const char*> constStringVector;

        //These are sent in order.
    stringData = "J2";//Initiate connection (tells terminal to listen)
    constStringVector.push_back(stringData);
    stringData = "T 11 22 33 44 1";//Allow editing of parameters
    constStringVector.push_back(stringData);
    stringData = "M 88 07 03";//Set parameters (07 means in terminal mode)
    constStringVector.push_back(stringData);
    stringData = "O 00 00";//Change Header Next
    constStringVector.push_back(stringData);
    stringData = "O 01 04";//four characters in header
    constStringVector.push_back(stringData);
    stringData = "O 02 20";//First header character" "
    constStringVector.push_back(stringData);
    stringData = "O 03 60";//Second header character"`" (not single quote)
    constStringVector.push_back(stringData);
    stringData = "O 04 40";//third header character"@"
    constStringVector.push_back(stringData);
    stringData = "O 05 22";//fourth header character"\""
    constStringVector.push_back(stringData);
    stringData = "O 00 01";//change footer next
    constStringVector.push_back(stringData);
    stringData = "O 01 05";//five characters in footer
    constStringVector.push_back(stringData);
    stringData = "O 02 0D";//character 1
    constStringVector.push_back(stringData);
    stringData = "O 03 0A";//character2
    constStringVector.push_back(stringData);
    stringData = "O 04 2E";//character 3
    constStringVector.push_back(stringData);
    stringData = "O 05 0D";//character 4
    constStringVector.push_back(stringData);
    stringData = "O 06 0A";//character 5
    constStringVector.push_back(stringData);
    //Set remaining options (source ID, others..)
    stringData = "O 00 00 00 00 00";//Note: this is messed up in the manual O 04 00 00 00 00 is for source ID active!
    constStringVector.push_back(stringData);

    return constStringVector;
}

string CtiDevicePagingReceiver::getFormattedFrequency()
{
    string tempString;
    float frequency = _tbl.getFrequency();

	//boundary value.
	if(frequency<130)
		return "F00 00";

    frequency -= 130;
    frequency = frequency/.0125;
	int intFreq = (int)frequency;
    tempString = "F" + CtiNumStr(intFreq).hex().zpad(4);
    if(tempString.length()==3)
        tempString.insert(1,"00");
    else
    if(tempString.length()==4)
        tempString.insert(1,"0");
    tempString.insert(3," ");
    return tempString;
}

string CtiDevicePagingReceiver::getFormattedHexCapcodeNumber(int number)
{
    //number should go from 0 to 15.
    //The capcode database uses 1-16
    string tempString;
    int capcode;
    if(number >= 0 && number <= 15)
    {
        capcode = _tbl.getCapcode(number+1);
        tempString = CtiNumStr(capcode).hex().zpad(8);
        tempString.append(CtiNumStr(number).hex().zpad(2));
        tempString.insert(8," ");
        tempString.insert(6," ");
        tempString.insert(4," ");
        tempString.insert(2," ");
        return tempString;

    }
    else
    { 
        return "0";
    }
}

void CtiDevicePagingReceiver::resetStates(bool initial)
{
    if(initial)
    {
        setPreviousState(DoInit);
        setCurrentState(DoInit);
    }
    else
    {
        setPreviousState(ReadAndCheck);
        setCurrentState(ReadAndCheck);
    }
}

void CtiDevicePagingReceiver::setCurrentState(StateMachine newCurrentState)
{
    _currentState = newCurrentState;
}

void CtiDevicePagingReceiver::setPreviousState(StateMachine newPreviousState)
{
    _previousState = newPreviousState;
}

CtiDevicePagingReceiver::StateMachine CtiDevicePagingReceiver::getCurrentState()
{
    return _currentState;
}

CtiDevicePagingReceiver::StateMachine CtiDevicePagingReceiver::getPreviousState()
{
    return _previousState;
}

bool CtiDevicePagingReceiver::isTransactionComplete()
{
    return _command == Complete;
}

//Database Functions
void CtiDevicePagingReceiver::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    _tbl.getSQL(db, keyTable, selector);
}

void CtiDevicePagingReceiver::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    _tbl.DecodeDatabaseReader(rdr);
}

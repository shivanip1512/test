
/*-----------------------------------------------------------------------------*
*
* File:   prot_sa3rdparty
*
* Date:   4/9/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/04/29 19:58:49 $
*
* HISTORY      :
* $Log: prot_sa3rdparty.cpp,v $
* Revision 1.1  2004/04/29 19:58:49  cplender
* Initial sa protocol/load group support
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include "cparms.h"
#include "logger.h"
#include "numstr.h"
#include "prot_sa3rdparty.h"
#include "protocol_sa.h"


CtiProtocolSA3rdParty::CtiProtocolSA3rdParty() :
_delayToTx(0x00),
_maxTxTime(0x3f),
_lbt(0x00),
_transmitterAddress(0),
_groupType(0),
_shed(false),
_function(0),
_code205(0),
_swTimeout(0),
_cycleTime(0),
_repeats(0),
_messageReady(false),
_bufferLen(0)
{
}

CtiProtocolSA3rdParty::CtiProtocolSA3rdParty(const CtiProtocolSA3rdParty& aRef)
{
    *this = aRef;
}

CtiProtocolSA3rdParty::~CtiProtocolSA3rdParty()
{
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::operator=(const CtiProtocolSA3rdParty& aRef)
{
    if(this != &aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** ACH!!! Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return *this;
}


INT CtiProtocolSA3rdParty::parseCommand(CtiCommandParser &parse, CtiOutMessage &OutMessage)
{
    INT status = NORMAL;


    switch(parse.getiValue("type"))
    {
    case ProtocolGolayType:
        {
            _groupType = GOLAY;
            strncpy(_codeSimple, parse.getsValue("sa_codesimple").data(), 7);
            break;
        }
    case ProtocolSADigitalType:
        {
            _groupType = SADIG;
            strncpy(_codeSimple, parse.getsValue("sa_codesimple").data(), 7);
            break;
        }
    case ProtocolSA105Type:
        {
            _groupType = SA105;
            strncpy(_codeSimple, parse.getsValue("sa_codesimple").data(), 7);
            break;
        }
    case ProtocolSA205Type:
        {
            _groupType = SA205;
            _code205 = parse.getiValue("sa_opaddress",0);                           // Comes from somewhere else please!
            break;
        }
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            return NoMethod;     // this is a fail for this protocol.
        }
    }


    switch(parse.getCommand())
    {
    case ControlRequest:
        {
            assembleControl( parse, OutMessage );
            _messageReady = true;
            break;
        }
    case PutConfigRequest:
        {
            assemblePutConfig( parse, OutMessage );
            _messageReady = true;
            break;
        }
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Unsupported command on sa205 route. Command = " << parse.getCommand() << endl;
            }

            status = CtiInvalidRequest;

            break;
        }
    }

    return status;
}

INT CtiProtocolSA3rdParty::assembleControl(CtiCommandParser &parse, CtiOutMessage &OutMessage)
{
    INT  i;
    INT  status = NORMAL;
    UINT CtlReq = CMD_FLAG_CTL_ALIASMASK & parse.getFlags();


    _function = parse.getiValue("sa_function");
    solveStrategy(parse);

    if(CtlReq == CMD_FLAG_CTL_SHED)
    {
        int shed_seconds = parse.getiValue("shed");
        if(shed_seconds >= 0)
        {
            // Add these two items to the list for control accounting!
            parse.setValue("control_interval", parse.getiValue("shed"));
            parse.setValue("control_reduction", 100 );

            loadControl();
        }
    }
    else if(CtlReq == CMD_FLAG_CTL_CYCLE)
    {
        INT period     = parse.getiValue("cycle_period", 30);
        INT repeat     = parse.getiValue("cycle_count", 8);

        // Add these two items to the list for control accounting!
        parse.setValue("control_reduction", parse.getiValue("cycle", 0) );
        parse.setValue("control_interval", 60 * period * repeat);

        loadControl();
    }
    else if(CtlReq == CMD_FLAG_CTL_RESTORE)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        loadControl();
    }
    else if(CtlReq == CMD_FLAG_CTL_TERMINATE)
    {
        INT delay = parse.getiValue("delaytime_sec", 0) / 60;
        loadControl();
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Unsupported command.  Command = " << parse.getCommand() << endl;
    }

    return status;
}

INT CtiProtocolSA3rdParty::assemblePutConfig(CtiCommandParser &parse, CtiOutMessage &OutMessage)
{
    INT status = NORMAL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}

INT CtiProtocolSA3rdParty::loadControl()
{
    INT status = NORMAL;

    SA_CODE scode;
    INT retCode;

    _errorBuf[0] = '\0';
    _errorLen = MAX_SAERR_MSG_SIZE;

    if(_groupType == SA205 || _groupType == SA105)
    {
        RWCString strcode = CtiNumStr(_code205);
        strncpy(scode.code, strcode.data(), 7);

        scode.function = _function;
        scode.type = _groupType;
        scode.repeats = _repeats;
    }
    else
    {
        strncpy(scode.code, _codeSimple, 7);
    }

    scode.swTime = _swTimeout;
    scode.cycleTime = _cycleTime;
    _buffer[0] = '/0';
    _bufferLen = MAX_SA_MSG_SIZE;


    switch(_groupType)
    {
    case SA105:
    case SA205:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                retCode = control105_205(_buffer, &_bufferLen, &scode, _transmitterAddress);
            }
            break;
        }
    case GOLAY:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                retCode = controlGolay(_buffer, &_bufferLen, _codeSimple, _function, _transmitterAddress);
            }
            break;
        }
    case SADIG:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                retCode = controlSADigital(_buffer, &_bufferLen, _codeSimple, _transmitterAddress,
                                           gConfigParms.getValueAsInt("SADIGITIAL_MARK_INDEX",3),
                                           gConfigParms.getValueAsInt("SADIGITIAL_SPARE_INDEX",10));
            }
            break;
        }
    }


    processResult(retCode);


    return status;
}

INT CtiProtocolSA3rdParty::restoreLoadControl()
{
    INT status = NORMAL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}

int CtiProtocolSA3rdParty::solveStrategy(CtiCommandParser &parse)
{
    INT status = NORMAL;

    // We only try to predict it if it has not already been fully identified for us.
    if(parse.isKeyValid("sa_restore"))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        _repeats = parse.getiValue("sa_reps", 1);
        _swTimeout = 450;
        _cycleTime = 450;
    }
    else
    {
        int shed_seconds = parse.getiValue("shed", 0);
        int cycle_percent = parse.getiValue("cycle",0);
        int cycle_period = parse.getiValue("cycle_period", 30);
        int cycle_count = parse.getiValue("cycle_count", 8);

        if(shed_seconds)
        {
            computeShedTimes(shed_seconds);
        }
        else if(cycle_percent > 0)
        {
            // It is a cycle command!

            if(cycle_period <= 8)
            {
                _cycleTime = 450;
                if(cycle_percent <= 100)
                {
                    _swTimeout = 450;
                }
                else
                {
                    status = BADPARAM;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Cycle parameters are not available on this switch for selected period." << endl;
                    }
                }
            }
            else if(cycle_period <= 15)
            {
                _cycleTime = 900;

                if(cycle_percent <= 50)
                {
                    _swTimeout = 450;
                }
                else if(cycle_percent <= 67)
                {
                    _swTimeout = 600;
                }
                else if(cycle_percent <= 73)
                {
                    _swTimeout = 660;
                }
                else if(cycle_percent <= 80)
                {
                    _swTimeout = 720;
                }
                else if(cycle_percent <= 100)
                {
                    _swTimeout = 900;
                }
                else
                {
                    status = BADPARAM;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Cycle parameters are not available on this switch for selected period." << endl;
                    }
                }
            }
#ifdef SA_PROTOCOL_COMPLETE
            else if(cycle_period <= 23)
            {
                _cycleTime = 1350;

                if(cycle_percent <= 33)
                {
                    _swTimeout = 450;
                }
                else if(cycle_percent <= 67)
                {
                    _swTimeout = 900;
                }
                else if(cycle_percent <= 100)
                {
                    _swTimeout = 1350;
                }
                else
                {
                    status = BADPARAM;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Cycle parameters are not available on this switch for selected period." << endl;
                    }
                }
            }
#endif
            else if(cycle_period <= 30)
            {
                _cycleTime = 1800;

                if(cycle_percent <= 25)
                {
                    _swTimeout = 450;
                }
                else if(cycle_percent <= 33)
                {
                    _swTimeout = 600;
                }
                else if(cycle_percent <= 50)
                {
                    _swTimeout = 900;
                }
                else if(cycle_percent <= 75)
                {
                    _swTimeout = 1350;
                }
                else if(cycle_percent <= 100)
                {
                    _swTimeout = 1800;
                }
                else
                {
                    status = BADPARAM;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Cycle parameters are not available on this switch for selected period." << endl;
                    }
                }
            }
#ifdef SA_PROTOCOL_COMPLETE
            else if(cycle_period <= 38)
            {
                _cycleTime = 2250;

                if(cycle_percent <= 20)
                {
                    _swTimeout = 450;
                }
                else if(cycle_percent <= 40)
                {
                    _swTimeout = 900;
                }
                else if(cycle_percent <= 60)
                {
                    _swTimeout = 1350;
                }
                else if(cycle_percent <= 80)
                {
                    _swTimeout = 1800;
                }
                else if(cycle_percent <= 100)
                {
                    _swTimeout = 2250;
                }
                else
                {
                    status = BADPARAM;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Cycle parameters are not available on this switch for selected period." << endl;
                    }
                }
            }
            else if(cycle_period <= 45)
            {
                _cycleTime = 2700;

                if(cycle_percent <= 17)
                {
                    _swTimeout = 450;
                }
                else if(cycle_percent <= 33)
                {
                    _swTimeout = 900;
                }
                else if(cycle_percent <= 50)
                {
                    _swTimeout = 1350;
                }
                else if(cycle_percent <= 67)
                {
                    _swTimeout = 1800;
                }
                else if(cycle_percent <= 83)
                {
                    _swTimeout = 2250;
                }
                else if(cycle_percent <= 100)
                {
                    _swTimeout = 2700;
                }
                else
                {
                    status = BADPARAM;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Cycle parameters are not available on this switch for selected period." << endl;
                    }
                }
            }
            else if(cycle_period <= 53)
            {
                _cycleTime = 3150;

                if(cycle_percent <= 14)
                {
                    _swTimeout = 450;
                }
                else if(cycle_percent <= 29)
                {
                    _swTimeout = 900;
                }
                else if(cycle_percent <= 43)
                {
                    _swTimeout = 1350;
                }
                else if(cycle_percent <= 57)
                {
                    _swTimeout = 1800;
                }
                else if(cycle_percent <= 71)
                {
                    _swTimeout = 2250;
                }
                else if(cycle_percent <= 86)
                {
                    _swTimeout = 2700;
                }
                else if(cycle_percent <= 100)
                {
                    _swTimeout = 3150;
                }
                else
                {
                    status = BADPARAM;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Cycle parameters are not available on this switch for selected period." << endl;
                    }
                }
            }
#endif
            else
            {
                _cycleTime = 3600;

                if(cycle_percent <= 13)
                {
                    _swTimeout = 450;
                }
                else if(cycle_percent <= 25)
                {
                    _swTimeout = 900;
                }
                else if(cycle_percent <= 33)
                {
                    _swTimeout = 1200;
                }
                else if(cycle_percent <= 38)
                {
                    _swTimeout = 1350;
                }
                else if(cycle_percent <= 50)
                {
                    _swTimeout = 1800;
                }
                else if(cycle_percent <= 63)
                {
                    _swTimeout = 2250;
                }
                else if(cycle_percent <= 67)
                {
                    _swTimeout = 2400;
                }
                else if(cycle_percent <= 75)
                {
                    _swTimeout = 2700;
                }
                else if(cycle_percent <= 88)
                {
                    _swTimeout = 3150;
                }
                else if(cycle_percent <= 100)
                {
                    _swTimeout = 3600;
                }
                else
                {
                    status = BADPARAM;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Cycle parameters are not available on this switch for selected period." << endl;
                    }
                }
            }

            _repeats = cycle_count;
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }


    if(0)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << " switch timeout: " << _swTimeout << endl;
        dout << " cycle time    : " << _cycleTime << endl;
        dout << " period        : " << _cycleTime << endl;
        dout << " repetitions   : " << _repeats << endl;
    }

    return status;
}


bool CtiProtocolSA3rdParty::messageReady() const
{
    return _messageReady;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setTransmitterAddress( int val )
{
    _transmitterAddress = val;
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setGroupType( int val )
{
    _groupType = val;
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setShed( bool val )
{
    _shed = val;
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setFunction( int val )
{
    _function = val;
    return *this;
}


CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setCode205( int val )
{
    _code205 = val;
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setCodeGolay( RWCString val )
{
    strncpy(_codeSimple, val.data(), 6);
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setCodeSADigital( RWCString val )
{
    strncpy(_codeSimple, val.data(), 6);
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setSwitchTimeout( int val )
{
    _swTimeout = val;
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setCycleTime( int val )
{
    _cycleTime = val;
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setRepeats( int val )
{
    _repeats = val;
    return *this;
}


void CtiProtocolSA3rdParty::computeShedTimes(int shed_time)
{
    int i;

#ifdef SA_PROTOCOL_COMPLETE
    int ctimes[] = { 450, 900, 1350, 1800, 2250, 2700, 3150, 3600 };        // These are the available cycle times in seconds
#else
    int ctimes[] = { 450, 900, 1800, 3600 };        // These are the available cycle times in seconds
#endif

    int bestoffset = 0;                             // Pick a 450 scond shed by default?
    int ctimesize = sizeof(ctimes)/sizeof(int);
    int currentbestdelta = INT_MAX;                         // We must be closer than one hour!

    for(i = 0; i < ctimesize; i++)
    {
        if(!(shed_time % ctimes[i]) && (ctimes[i] * 8 >= shed_time))  // This one divides evenly and is large enough to meet the goal! (A perfect match)
        {
            _cycleTime = ctimes[i];
            _swTimeout = ctimes[i];
            _repeats = shed_time / ctimes[i];
            break;
        }
        else
        {
            // This one is not a perfect match, so we will try to see how close we can get!
            int rep = shed_time / ctimes[i] > 8 ? 8 : shed_time / ctimes[i];
            int delta = shed_time - (rep * ctimes[i]);

            if(delta < currentbestdelta)
            {
                _cycleTime = ctimes[i];
                _swTimeout = ctimes[i];
                _repeats = rep;
            }
        }
    }

    return;
}

void CtiProtocolSA3rdParty::processResult(INT retCode)
{
    INT i;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "retcode " << retCode << " buflen: " << _bufferLen << endl;

        if((retCode != PROTSA_SUCCESS) && (retCode !=PROTSA_SUCCESS_MODIFIED_PARAM)  )
        {
            retCode = lastSAError(_errorBuf, &_errorLen);
            dout << " errorbuf: " << _errorBuf << " len: " << _errorLen << endl;
            _errorLen = MAX_SAERR_MSG_SIZE;
        }
        else
        {
            for(i = 0; i<_bufferLen; i++)
                dout << CtiNumStr(_buffer[i]).hex().zpad(2) << " ";
            dout << endl;
        }
    }

}

void CtiProtocolSA3rdParty::copyMessage(RWCString &str) const
{
    INT i;

    str = RWCString();
    if(_bufferLen>0)
    {
        for(i = 0; i<_bufferLen; i++)
            str += CtiNumStr(_buffer[i]).hex().zpad(2) + " ";
    }

    return;
}

void CtiProtocolSA3rdParty::getBuffer(BYTE *dest, ULONG &len) const
{
    int i = len;

    for(i = 0; i<_bufferLen + len; i++)
    {
        dest[i] = _buffer[i];
    }

    len = i;

    return;
}

/*
 *  Append a Variable Length Timeslot Message to the assend of this message!
 *  len is both input and output.  It is assumed to indicate the current length of the dest buffer
 *  additional bytes shall be appended.
 */
void CtiProtocolSA3rdParty::appendVariableLengthTimeSlot(BYTE *dest, ULONG &len) const
{
    int i = len + 1;
    BYTE crc = 0;

    BYTE maxTx = 0x3f;

    dest[i] = 0xa0 | (_transmitterAddress & 0x0f);
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = 0x29;                                       // Fixed identifier.
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = 0x02;                                       // Fixed identifier.
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = 0x03;                                       // Fixed identifier.
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = _delayToTx;                                 // 6 bits seconds offset before TX.
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = (_maxTxTime & 0x3f);                        // 6 bits seconds max of transmission time.
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = (0x07 & _lbt);                              // LBT Mode.
    crc ^= dest[i++];                                     // Compute CRC and advance i.

    dest[i++] = crc;

    len = i;

    return;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setLBTMode( int val )
{
    _lbt = (BYTE)val & 0x07;
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setDelayTxTime( int val )
{
    _delayToTx = (BYTE)val & 0x3f;
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setMaxTxTime( int val )
{
    _maxTxTime = (BYTE)val & 0x3f;
    return *this;
}



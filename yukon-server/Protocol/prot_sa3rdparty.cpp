/*-----------------------------------------------------------------------------*
*
* File:   prot_sa3rdparty
*
* Date:   4/9/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.20 $
* DATE         :  $Date: 2005/02/10 23:23:57 $
*
* HISTORY      :
* $Log: prot_sa3rdparty.cpp,v $
* Revision 1.20  2005/02/10 23:23:57  alauinger
* Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build
*
* Revision 1.19  2004/12/14 22:25:16  cplender
* Various to wring out config commands.  Should be pretty good.
*
* Revision 1.18  2004/12/08 21:20:47  cplender
* Worked on the address config code
* Worked on the repeat count work.. 0 - 7 = 1 - 8.
*
* Revision 1.17  2004/11/24 17:11:16  cplender
* Working on the configuration of SA receivers.
*
* Revision 1.16  2004/11/18 23:38:28  mfisher
* Debuglevel'd out some (but not all) debug printouts
*
* Revision 1.15  2004/11/08 14:40:39  cplender
* 305 Protocol should send controls on RTCs now.
*
* Revision 1.14  2004/11/05 17:25:58  cplender
*
* Getting 305s to work
*
* Revision 1.13  2004/10/29 20:00:00  mfisher
* copied comments for Slick's parameter completion info
*
* Revision 1.12  2004/10/19 20:26:46  cplender
* Extended the control shed functionality for all combos of stime/ctime.
*
* Revision 1.11  2004/10/14 20:39:09  cplender
* Added config205 and tamper205 and coldLoad205 to the party.
*
* Revision 1.10  2004/10/08 20:53:19  cplender
* Telvent altered the control105_205 to accept matrix coordinates.
*
* Revision 1.9  2004/09/20 16:11:51  mfisher
* changed RTM functions to be static - we don't need to keep state
*
* Revision 1.8  2004/07/30 21:35:07  cplender
* RTM stuff
*
* Revision 1.7  2004/06/24 13:16:12  cplender
* Some cleanup on the simulator to make RTC and LMIRTU trx sessions look the same.
* Added PORTER_SA_RTC_MAXCODES the maimum number of codes that can be sent in one block
*
* Revision 1.6  2004/06/23 18:37:27  cplender
* Altered the default constructor to memset the SAStruct.
*
* Revision 1.5  2004/06/03 21:46:17  cplender
* Simulator mods.
*
* Revision 1.4  2004/05/24 13:47:40  cplender
* Added opcode to the 105/205 protocol asString call.
*
* Revision 1.3  2004/05/20 22:44:26  cplender
* Support for repeating 205 messages after n minutes.
*
* Revision 1.2  2004/05/19 14:55:55  cplender
* Supportting new dsm2.h struct CtiSAData
*
* Revision 1.1  2004/04/29 19:58:49  cplender
* Initial sa protocol/load group support
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <rw\re.h>
#undef mask_                // Stupid RogueWave re.h

#include "cparms.h"
#include "logger.h"
#include "numstr.h"
#include "prot_sa3rdparty.h"
#include "protocol_sa.h"


CtiProtocolSA3rdParty::CtiProtocolSA3rdParty() :
_messageReady(false)
{
    memset(&_sa, 0, sizeof(_sa));
    _sa._maxTxTime = (0x3f);
}

CtiProtocolSA3rdParty::CtiProtocolSA3rdParty(const CtiSAData sa)
{
    _sa = sa;
    _messageReady = true;

    computeSnCTime();
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

    _sa._commandType = parse.getCommand();

    switch(parse.getiValue("type"))
    {
    case ProtocolGolayType:
        {
            _sa._groupType = GOLAY;
            strncpy(_sa._codeSimple, parse.getsValue("sa_codesimple").data(), 7);
            break;
        }
    case ProtocolSADigitalType:
        {
            _sa._groupType = SADIG;
            _sa._mark = parse.getiValue("sa_mark");
            _sa._space = parse.getiValue("sa_space");
            strncpy(_sa._codeSimple, parse.getsValue("sa_codesimple").data(), 7);
            break;
        }
    case ProtocolSA105Type:
        {
            _sa._groupType = SA105;
            strncpy(_sa._codeSimple, parse.getsValue("sa_codesimple").data(), 7);
            break;
        }
    case ProtocolSA205Type:
        {
            _sa._groupType = SA205;
            _sa._code205 = parse.getiValue("sa_opaddress",0);                           // Comes from somewhere else please!
            break;
        }
    default:
        {
            break;
        }
    }

    switch(_sa._commandType)
    {
    case ControlRequest:
        {
            _sa._function = parse.getiValue("sa_function");

            if( !solveStrategy(parse) && NORMAL == (status = assembleControl( parse, OutMessage )) )
            {
                loadControl();
                _messageReady = true;
            }
            break;
        }
    case PutConfigRequest:
        {
            if( NORMAL == (status = assemblePutConfig( parse, OutMessage )) )
            {
                if(  parse.isKeyValid("sa_assign") && parse.isKeyValid("serial") )
                {
                    // There has been an assignment request!
                    RWCString serialstr = CtiNumStr(parse.getiValue("serial"));
                    INT totalLen = 0;
                    // More than one config might be generated into the buffer.
                    _errorBuf[0] = '\0';
                    _errorLen = MAX_SAERR_MSG_SIZE;

                    _sa._buffer[0] = '/0';
                    _sa._bufferLen = MAX_SA_MSG_SIZE;

                    _sa._function = sac_address_config;

                    strncpy(_sa._serial, serialstr.data(), 33);

                    for(int i = 1; i <= 6; i++)     // Look for each slot assignment and add a blurb for it!
                    {
                        RWCString slotstr = RWCString("sa_slot") + CtiNumStr(i);

                        if(parse.isKeyValid(slotstr))
                        {
                            strncpy(_sa._codeSimple, parse.getsValue(slotstr).data(), 7);
                            addressAssign(totalLen, i);

                            totalLen += _sa._bufferLen;                     // What's been accumulated
                            _sa._bufferLen = MAX_SA_MSG_SIZE - totalLen;    // What's left to fill.
                        }
                    }

                    _sa._bufferLen = totalLen;                              // This is what the world wants to know and where it wants to know it.

                    _messageReady = true;
                }
                else if( parse.isKeyValid("sa_offtime") && parse.isKeyValid("serial") )
                {
                    // temporary service out command!
                    RWCString serialstr = CtiNumStr(parse.getiValue("serial"));
                    INT totalLen = 0;
                    // More than one config might be generated into the buffer.
                    _errorBuf[0] = '\0';
                    _errorLen = MAX_SAERR_MSG_SIZE;

                    _sa._buffer[0] = '/0';
                    _sa._bufferLen = MAX_SA_MSG_SIZE;

                    _sa._function = sac_toos;

                    strncpy(_sa._serial, serialstr.data(), 33);

                    tempOutOfService205(_sa._buffer, &_sa._bufferLen, _sa._serial, parse.getiValue("sa_offtime",0), _sa._transmitterAddress);

                    _messageReady = true;
                }
                else if( parse.isKeyValid("sa_coldload") && parse.isKeyValid("serial") )
                {
                    // There has been an assignment request!
                    RWCString clpstr;
                    RWCString serialstr = CtiNumStr(parse.getiValue("serial"));
                    INT totalLen = 0;
                    // More than one config might be generated into the buffer.
                    _errorBuf[0] = '\0';
                    _errorLen = MAX_SAERR_MSG_SIZE;

                    _sa._buffer[0] = '/0';
                    _sa._bufferLen = MAX_SA_MSG_SIZE;

                    strncpy(_sa._serial, serialstr.data(), 33);

                    int i, clsec, clpCount;
                    for(i = 1; i <= 4; i++)
                    {
                        clpstr = RWCString("sa_clpf") + CtiNumStr(i); // coldload_r1,,... coldload_r4

                        if( (clsec = parse.getiValue(clpstr,-1)) >= 0 )
                        {
                            clpCount = (int)( (float)clsec / 14.0616 );
                            // Input:Cold Load Pickup Count, 0-255, 1 count = 14.0616seconds
                            {
                                CtiLockGuard<CtiLogger> slog_guard(slog);
                                slog << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                slog << " Calling coldLoadPickup205(). Serial " << serialstr << " " << clpstr << " set to  " << clpCount << " on the receiver (* 14.0616 = sec) " << endl;
                            }

                            coldLoadPickup205(&_sa._buffer[totalLen], &_sa._bufferLen, _sa._serial, i, clpCount, _sa._transmitterAddress);
                            _messageReady = true;

                            totalLen += _sa._bufferLen;                     // What's been accumulated
                            _sa._bufferLen = MAX_SA_MSG_SIZE - totalLen;    // What's left to fill.
                        }
                    }

                    _sa._bufferLen = totalLen;                              // This is what the world wants to know and where it wants to know it.
                }
                else if( parse.isKeyValid("sa_tamper") && parse.isKeyValid("serial") )
                {
                    // There has been an assignment request!
                    RWCString tdstr;
                    RWCString serialstr = CtiNumStr(parse.getiValue("serial"));
                    INT totalLen = 0;
                    // More than one config might be generated into the buffer.
                    _errorBuf[0] = '\0';
                    _errorLen = MAX_SAERR_MSG_SIZE;

                    _sa._buffer[0] = '/0';
                    _sa._bufferLen = MAX_SA_MSG_SIZE;

                    strncpy(_sa._serial, serialstr.data(), 33);

                    int i, tdCount;
                    for(i = 1; i <= 4; i++)
                    {
                        tdstr = RWCString("tamperdetect_f") + CtiNumStr(i);

                        if( (tdCount = parse.getiValue(tdstr,-1)) >= 0 )
                        {
                            {
                                CtiLockGuard<CtiLogger> slog_guard(slog);
                                slog << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                slog << " Calling tamperDetect205(). Serial " << serialstr << " " << tdstr << " set to  " << tdCount << " on the receiver " << endl;
                            }

                            if(!tamperDetect205(&_sa._buffer[totalLen], &_sa._bufferLen, _sa._serial, i, tdCount, _sa._transmitterAddress))
                            {
                                _messageReady = true;

                                totalLen += _sa._bufferLen;                     // What's been accumulated
                                _sa._bufferLen = MAX_SA_MSG_SIZE - totalLen;    // What's left to fill.
                            }
                        }
                    }
                    _sa._bufferLen = totalLen;                              // This is what the world wants to know and where it wants to know it.
                }
            }
            break;
        }
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << RWTime() << " Unsupported command. Command = " << parse.getCommand() << endl;
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

    if(CtlReq == CMD_FLAG_CTL_SHED)
    {
        int shed_seconds = parse.getiValue("shed");
        if(shed_seconds >= 0)
        {
            // Add these two items to the list for control accounting!
            parse.setValue("control_interval", parse.getiValue("shed"));
            parse.setValue("control_reduction", 100 );
        }
        else
            status = BADPARAM;

    }
    else if(CtlReq == CMD_FLAG_CTL_CYCLE)
    {
        INT period     = parse.getiValue("cycle_period", 30);
        INT repeat     = parse.getiValue("cycle_count", 8) - 1;

        // Add these two items to the list for control accounting!
        parse.setValue("control_reduction", parse.getiValue("cycle", 0) );
        parse.setValue("control_interval", 60 * period * repeat);
    }
    else if(CtlReq == CMD_FLAG_CTL_RESTORE)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** ACH ??CONTROL RESTORE?? Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else if(CtlReq == CMD_FLAG_CTL_TERMINATE)
    {
        INT delay = parse.getiValue("delaytime_sec", 0) / 60;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Unsupported command.  Command = " << parse.getCommand() << endl;
        status = NoMethod;
    }

    return status;
}

INT CtiProtocolSA3rdParty::assemblePutConfig(CtiCommandParser &parse, CtiOutMessage &OutMessage)
{
    INT status = NORMAL;

    RWCString   temp, token;
    RWCString   str = parse.getCommandStr();

    if(str.contains(" assign"))
    {
        RWCString mstr;
        RWCString rwsslot;
        RWCString rwsaddr;

        int i;
        for(i = 1; i <= 6; i++)
        {
            mstr = CtiNumStr(i) + RWCString(" *= *[0-9]+");

            if(!(temp = str.match(mstr)).isNull())
            {
                RWCTokenizer slotok(temp);

                rwsslot = slotok("=");
                rwsaddr = slotok("=,\r\n ");

                int addr = atoi(rwsaddr.data());

                mstr = RWCString("sa_slot") + CtiNumStr(i); // sa_slot1, sa_slot2,... sa_slot6.

                parse.setValue(mstr, rwsaddr);              // Stored as a string because the Telvent lib wants it that way!!

                {
                    CtiLockGuard<CtiLogger> slog_guard(slog);
                    slog << RWTime() << " Address config command. Serial " << parse.getiValue("serial") << " writing address " << addr << " to slot " << i << " on the receiver" << endl;
                }
            }
        }
    }
    else if(str.contains(" override"))
    {
        INT offtime = 0;

        if(!(token = str.match(" override +[0-9]+")).isNull())
        {
            str = token.match("[0-9]+");
            offtime = atoi(str.data());
            {
                CtiLockGuard<CtiLogger> slog_guard(slog);
                slog << RWTime() << " Temporary service command. Serial " << parse.getiValue("serial") << " Offhours = " << offtime << endl;
            }
            parse.setValue("sa_offtime", offtime);              // Stored as a string because the Telvent lib wants it that way!!
        }
    }
    else if(str.contains(" service"))
    {
        if(str.contains(" temp") && !(token = str.match("service +((in)|(out)|(enable)|(disable))")).isNull())
        {
            INT offtime = 0;

            if(!(token = str.match(" offhours +[0-9]+")).isNull())
            {
                str = token.match("[0-9]+");
                offtime = atoi(str.data());
                {
                    CtiLockGuard<CtiLogger> slog_guard(slog);
                    slog << RWTime() << " Temporary service command. Serial " << parse.getiValue("serial") << " Offhours = " << offtime << endl;
                }
                parse.setValue("sa_offtime", offtime);              // Stored as a string because the Telvent lib wants it that way!!
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " Unknown service command: \"" << str << "\"" <<endl;
            }
        }
    }
    else if(parse.isKeyValid("sa_coldload"))
    {
    }
    else if(parse.isKeyValid("sa_tamper"))
    {
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

    if(_sa._groupType == SA205 || _sa._groupType == SA105)
    {
        RWCString strcode = CtiNumStr(_sa._code205);
        strncpy(scode.code, strcode.data(), 7);

        scode.function = _sa._function;
        scode.type = _sa._groupType;
        scode.repeats = _sa._repeats;
    }
    else
    {
        strncpy(scode.code, _sa._codeSimple, 7);
    }

    scode.swTime = _sa._swTimeout;
    scode.cycleTime = _sa._cycleTime;
    _sa._buffer[0] = '/0';
    _sa._bufferLen = MAX_SA_MSG_SIZE;


    switch(_sa._groupType)
    {
    case SA105:
    case SA205:
        {
            retCode = control105_205(_sa._buffer, &_sa._bufferLen, &scode, _sa._transmitterAddress, _sTime, _cTime);

            if( getDebugLevel() & DEBUGLEVEL_SA3RDPARTY )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " control105_205() complete" << endl;
            }

            break;
        }
    case GOLAY:
        {
            retCode = controlGolay(_sa._buffer, &_sa._bufferLen, _sa._codeSimple, _sa._function, _sa._transmitterAddress);

            if( getDebugLevel() & DEBUGLEVEL_SA3RDPARTY )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " controlGolay() complete" << endl;
            }

            break;
        }
    case SADIG:
        {
            retCode = controlSADigital(_sa._buffer, &_sa._bufferLen, _sa._codeSimple,
                                       _sa._transmitterAddress,
                                       _sa._mark,                                                                       // gConfigParms.getValueAsInt("SADIGITAL_MARK_INDEX",3),
                                       _sa._space);                                                                     // gConfigParms.getValueAsInt("SADIGITAL_SPACE_INDEX",10));

            if( getDebugLevel() & DEBUGLEVEL_SA3RDPARTY )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " controlSADigital() complete" << endl;
            }

            break;
        }
    }


    processResult(retCode);


    return status;
}

INT CtiProtocolSA3rdParty::addressAssign(INT &len, USHORT slot)
{
    INT status = NORMAL;

    INT retCode;

    switch(_sa._groupType)
    {
    case SA205:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                retCode = config205(&_sa._buffer[len], &_sa._bufferLen, _sa._serial, slot, _sa._codeSimple, _sa._transmitterAddress);
                dout << RWTime() << " config205() complete" << endl;
            }
            break;
        }
    case SA105:
    case GOLAY:
    case SADIG:
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Address assignment is not supported by protocol for grouptype " << _sa._groupType << endl;
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
        _sa._repeats = parse.getiValue("sa_reps", 0);
        _sa._swTimeout = 450;
        _sa._cycleTime = 450;
        _sTime = 0;
        _cTime = 0;
    }
    else
    {
        int shed_seconds = parse.getiValue("shed", 0);
        int cycle_percent = parse.getiValue("cycle",0);
        int cycle_period = parse.getiValue("cycle_period", 30);
        int cycle_count = parse.getiValue("cycle_count", 8) - 1;

        bool dlc_control = parse.getCommandStr().contains(" dlc");      // if set, we want DLC (not DI) control!

        if(shed_seconds)
        {
            computeShedTimes(shed_seconds);
        }
        else if(cycle_percent > 0)
        {
            // It is a cycle command!

            if(_sa._groupType == SA205 && cycle_period <= 8)
            {
                _sa._cycleTime = 450;
                if(cycle_percent <= 100)
                {
                    _sa._swTimeout = 450;

                    if(dlc_control)
                    {
                        _sTime = 0;
                        _cTime = 0;
                    }
                    else
                    {
                        _sTime = 3;
                        _cTime = 5;
                    }
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
                _sa._cycleTime = 900;

                if(cycle_percent <= 50)
                {
                    _sa._swTimeout = 450;
                    if(dlc_control)
                    {
                        _sTime = 0;
                        _cTime = 1;
                    }
                    else
                    {
                        _sTime = 2;
                        _cTime = 6;
                    }
                }
                else if(cycle_percent <= 67)
                {
                    _sa._swTimeout = 600;
                    _sTime = 3;
                    _cTime = 6;
                }
                else if(cycle_percent <= 73)
                {
                    _sa._swTimeout = 660;
                    _sTime = 4;
                    _cTime = 6;
                }
                else if(cycle_percent <= 80)
                {
                    _sa._swTimeout = 720;
                    _sTime = 5;
                    _cTime = 6;
                }
                else if(cycle_percent <= 100)
                {
                    _sa._swTimeout = 900;
                    if(dlc_control)
                    {
                        _sTime = 1;
                        _cTime = 1;
                    }
                    else
                    {
                        _sTime = 0;
                        _cTime = 5;
                    }
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
                _sa._cycleTime = 1350;

                if(cycle_percent <= 33)
                {
                    _sa._swTimeout = 450;
                }
                else if(cycle_percent <= 67)
                {
                    _sa._swTimeout = 900;
                }
                else if(cycle_percent <= 100)
                {
                    _sa._swTimeout = 1350;
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
                _sa._cycleTime = 1800;

                if(cycle_percent <= 25)
                {
                    _sa._swTimeout = 450;
                    if(dlc_control)
                    {
                        _sTime = 3;
                        _cTime = 0;
                    }
                    else
                    {
                        _sTime = 0;
                        _cTime = 6;
                    }
                }
                else if(cycle_percent <= 33)
                {
                    _sa._swTimeout = 600;
                    _sTime = 1;
                    _cTime = 6;
                }
                else if(cycle_percent <= 50)
                {
                    _sa._swTimeout = 900;
                    if(dlc_control)
                    {
                        _sTime = 3;
                        _cTime = 1;
                    }
                    else
                    {
                        _sTime = 0;
                        _cTime = 4;
                    }
                }
                else if(cycle_percent <= 75)
                {
                    _sa._swTimeout = 1350;
                    if(dlc_control)
                    {
                        _sTime = 3;
                        _cTime = 2;
                    }
                    else
                    {
                        _sTime = 1;
                        _cTime = 4;
                    }
                }
                else if(cycle_percent <= 100)
                {
                    _sa._swTimeout = 1800;
                    if(dlc_control)
                    {
                        _sTime = 3;
                        _cTime = 3;
                    }
                    else
                    {
                        _sTime = 1;
                        _cTime = 5;
                    }
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
                _sa._cycleTime = 2250;

                if(cycle_percent <= 20)
                {
                    _sa._swTimeout = 450;
                }
                else if(cycle_percent <= 40)
                {
                    _sa._swTimeout = 900;
                }
                else if(cycle_percent <= 60)
                {
                    _sa._swTimeout = 1350;
                }
                else if(cycle_percent <= 80)
                {
                    _sa._swTimeout = 1800;
                }
                else if(cycle_percent <= 100)
                {
                    _sa._swTimeout = 2250;
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
                _sa._cycleTime = 2700;

                if(cycle_percent <= 17)
                {
                    _sa._swTimeout = 450;
                }
                else if(cycle_percent <= 33)
                {
                    _sa._swTimeout = 900;
                }
                else if(cycle_percent <= 50)
                {
                    _sa._swTimeout = 1350;
                }
                else if(cycle_percent <= 67)
                {
                    _sa._swTimeout = 1800;
                }
                else if(cycle_percent <= 83)
                {
                    _sa._swTimeout = 2250;
                }
                else if(cycle_percent <= 100)
                {
                    _sa._swTimeout = 2700;
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
                _sa._cycleTime = 3150;

                if(cycle_percent <= 14)
                {
                    _sa._swTimeout = 450;
                }
                else if(cycle_percent <= 29)
                {
                    _sa._swTimeout = 900;
                }
                else if(cycle_percent <= 43)
                {
                    _sa._swTimeout = 1350;
                }
                else if(cycle_percent <= 57)
                {
                    _sa._swTimeout = 1800;
                }
                else if(cycle_percent <= 71)
                {
                    _sa._swTimeout = 2250;
                }
                else if(cycle_percent <= 86)
                {
                    _sa._swTimeout = 2700;
                }
                else if(cycle_percent <= 100)
                {
                    _sa._swTimeout = 3150;
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
                _sa._cycleTime = 3600;

                if(cycle_percent <= 13)
                {
                    _sa._swTimeout = 450;
                    if(dlc_control)
                    {
                        _sTime = 7;
                        _cTime = 0;
                    }
                    else
                    {
                        _sTime = 0;
                        _cTime = 7;
                    }
                }
                else if(cycle_percent <= 25)
                {
                    _sa._swTimeout = 900;
                    if(dlc_control)
                    {
                        _sTime = 7;
                        _cTime = 1;
                    }
                    else
                    {
                        _sTime = 1;
                        _cTime = 7;
                    }
                }
                else if(cycle_percent <= 33)
                {
                    _sa._swTimeout = 1200;
                    _sTime = 2;
                    _cTime = 4;
                }
                else if(cycle_percent <= 38)
                {
                    _sa._swTimeout = 1350;
                    if(dlc_control)
                    {
                        _sTime = 7;
                        _cTime = 2;
                    }
                    else
                    {
                        _sTime = 2;
                        _cTime = 7;
                    }
                }
                else if(cycle_percent <= 50)
                {
                    _sa._swTimeout = 1800;
                    if(dlc_control)
                    {
                        _sTime = 7;
                        _cTime = 3;
                    }
                    else
                    {
                        _sTime = 3;
                        _cTime = 7;
                    }
                }
                else if(cycle_percent <= 63)
                {
                    _sa._swTimeout = 2250;
                    if(dlc_control)
                    {
                        _sTime = 7;
                        _cTime = 4;
                    }
                    else
                    {
                        _sTime = 4;
                        _cTime = 7;
                    }
                }
                else if(cycle_percent <= 67)
                {
                    _sa._swTimeout = 2400;
                    _sTime = 3;
                    _cTime = 4;
                }
                else if(cycle_percent <= 75)
                {
                    _sa._swTimeout = 2700;
                    if(dlc_control)
                    {
                        _sTime = 7;
                        _cTime = 5;
                    }
                    else
                    {
                        _sTime = 5;
                        _cTime = 7;
                    }
                }
                else if(cycle_percent <= 88)
                {
                    _sa._swTimeout = 3150;
                    if(dlc_control)
                    {
                        _sTime = 7;
                        _cTime = 6;
                    }
                    else
                    {
                        _sTime = 6;
                        _cTime = 7;
                    }
                }
                else if(cycle_percent <= 100)
                {
                    _sa._swTimeout = 3600;
                    if(dlc_control)
                    {
                        _sTime = 7;
                        _cTime = 7;
                    }
                    else
                    {
                        _sTime = 2;
                        _cTime = 5;
                    }
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

            _sa._repeats = cycle_count;
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Unknown command check syntax. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }


    if(0)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << " switch timeout: " << _sa._swTimeout << endl;
        dout << " cycle time    : " << _sa._cycleTime << endl;
        dout << " period        : " << _sa._cycleTime << endl;
        dout << " repetitions   : " << _sa._repeats << endl;
    }

    return status;
}


bool CtiProtocolSA3rdParty::messageReady() const
{
    return _messageReady;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setTransmitterAddress( int val )
{
    _sa._transmitterAddress = val;
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setGroupType( int val )
{
    _sa._groupType = val;
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setShed( bool val )
{
    _sa._shed = val;
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setFunction( int val )
{
    _sa._function = val;
    return *this;
}


CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setCode205( int val )
{
    _sa._code205 = val;
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setCodeGolay( RWCString val )
{
    strncpy(_sa._codeSimple, val.data(), 6);
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setCodeSADigital( RWCString val )
{
    strncpy(_sa._codeSimple, val.data(), 6);
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setSwitchTimeout( int val )
{
    _sa._swTimeout = val;
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setCycleTime( int val )
{
    _sa._cycleTime = val;
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setRepeats( int val )
{
    _sa._repeats = val;
    return *this;
}


void CtiProtocolSA3rdParty::computeShedTimes(int shed_time)
{
    int i;

#ifdef SA_PROTOCOL_COMPLETE
    int ctimes[] = { 450, 900, 1350, 1800, 2250, 2700, 3150, 3600 };        // These are the available cycle times in seconds
#elif SA_USE_DI_ONLY
    int ctimes[] = { 450, 900, 1800, 3600 };        // These are the available cycle times in seconds
    int oactime[] = { 3, 0, 1, 2 };                 // These are the available timeout times in "matrix"
    int oastime[] = { 5, 5, 5, 5 };                 // These are the available cycle times in "matrix"
#else
    int ctimes[] = { 450, 900, 1350, 1800, 2250, 2700, 3150, 3600 };        // These are the available cycle times in seconds
    // int ctimes[] = { 450, 900, 1800, 3600 };    // These are the available cycle times in seconds
    int oactime[] = { 0, 1, 2, 3, 4, 5, 6, 7 };    // These are the available timeout times in "matrix"
    int oastime[] = { 0, 1, 2, 3, 4, 5, 6, 7  };   // These are the available cycle times in "matrix"
#endif

    int bestoffset = 0;                             // Pick a 450 scond shed by default?
    int ctimesize = sizeof(ctimes)/sizeof(int);
    int currentbestdelta = INT_MAX;                         // We must be closer than one hour!

    for(i = 0; i < ctimesize; i++)
    {
        if(!(shed_time % ctimes[i]) && (ctimes[i] * 8 >= shed_time))  // This one divides evenly and is large enough to meet the goal! (A perfect match)
        {
            _sa._cycleTime = ctimes[i];
            _sa._swTimeout = ctimes[i];
            _sa._repeats = (shed_time / ctimes[i]) - 1;
            _cTime = oactime[i];
            _sTime = oastime[i];
            break;
        }
        else
        {
            // This one is not a perfect match, so we will try to see how close we can get!
            int rep = shed_time / ctimes[i] > 8 ? 7 : (shed_time / ctimes[i]) - 1;
            int delta = shed_time - ((rep+1) * ctimes[i]);

            if(delta < currentbestdelta)
            {
                _sa._cycleTime = ctimes[i];
                _sa._swTimeout = ctimes[i];
                _sa._repeats = rep;

                _cTime = oactime[i];
                _sTime = oastime[i];
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

        if( getDebugLevel() & DEBUGLEVEL_SA3RDPARTY )
        {
            dout << "retcode " << retCode << " buflen: " << _sa._bufferLen << endl;
        }

        if((retCode != PROTSA_SUCCESS) && (retCode !=PROTSA_SUCCESS_MODIFIED_PARAM)  )
        {
            retCode = lastSAError(_errorBuf, &_errorLen);

            if( getDebugLevel() & DEBUGLEVEL_SA3RDPARTY )
            {
                dout << " errorbuf: " << _errorBuf << " len: " << _errorLen << endl;
            }

            _errorLen = MAX_SAERR_MSG_SIZE;
        }
        else if( getDebugLevel() & DEBUGLEVEL_SA3RDPARTY )
        {
            for(i = 0; i<_sa._bufferLen; i++)
                dout << CtiNumStr(_sa._buffer[i]).hex().zpad(2) << " ";
            dout << endl;
        }
    }

}

void CtiProtocolSA3rdParty::copyMessage(RWCString &str) const
{
    INT i;

    str = RWCString();
    if(_sa._bufferLen>0)
    {
        for(i = 0; i<_sa._bufferLen; i++)
            str += CtiNumStr(_sa._buffer[i]).hex().zpad(2) + " ";
    }

    return;
}

void CtiProtocolSA3rdParty::getBuffer(BYTE *dest, ULONG &len) const
{
    int i = len;

    for(i = 0; i<_sa._bufferLen + len; i++)
    {
        dest[i] = _sa._buffer[i];
    }

    len = i;

    return;
}

/*
 *  Append a Variable Length Timeslot Message to the assend of this message!
 *  len is both input and output.  It is assumed to indicate the current length of the dest buffer
 *  additional bytes shall be appended.
 */
void CtiProtocolSA3rdParty::appendVariableLengthTimeSlot(int transmitter,
                                                         BYTE *dest,
                                                         ULONG &len,
                                                         BYTE delayToTx,
                                                         BYTE maxTx,
                                                         BYTE lbtMode)
{
    int i = len;
    BYTE crc = 0;

    _sa._transmitterAddress = transmitter;
    _sa._delayToTx = delayToTx;
    _sa._maxTxTime = maxTx;
    _sa._lbt = lbtMode;


    dest[i] = 0xa0 | (_sa._transmitterAddress & 0x0f);
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = 0x29;                                       // Fixed identifier.
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = 0x02;                                       // Fixed identifier.
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = 0x03;                                       // Fixed identifier.
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = _sa._delayToTx;                             // 6 bits seconds offset before TX.
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = (_sa._maxTxTime & 0x3f);                    // 6 bits seconds max of transmission time.
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = (0x07 & _sa._lbt);                          // LBT Mode.
    crc ^= dest[i++];                                     // Compute CRC and advance i.

    dest[i++] = crc;

    len = i;

    return;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setLBTMode( int val )
{
    _sa._lbt = (BYTE)val & 0x07;
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setDelayTxTime( int val )
{
    _sa._delayToTx = (BYTE)val & 0x3f;
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setMaxTxTime( int val )
{
    _sa._maxTxTime = (BYTE)val & 0x3f;
    return *this;
}

INT CtiProtocolSA3rdParty::getSABufferLen() const
{
    return _sa._bufferLen;
}

CtiSAData CtiProtocolSA3rdParty::getSAData() const
{
    return _sa;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setSAData(const CtiSAData &sa)
{
    _sa = sa;
    computeSnCTime();
    return *this;
}


RWCString CtiProtocolSA3rdParty::asString() const
{
    RWCString rstr;

    switch(_sa._commandType)
    {
    case PutConfigRequest:
        {
            rstr += "SA 205 - config. Serial " + RWCString(_sa._serial) + " - " + strategyAsString();
            break;
        }
    case ControlRequest:
        {
            switch(_sa._groupType)
            {
            case SA105:
                {
                    rstr += "SA 105 - code " + RWCString(_sa._codeSimple) + " - " + strategyAsString();
                    break;
                }
            case SA205:
                {
                    rstr += "SA 205 - code " + CtiNumStr(_sa._code205) + " - " + strategyAsString();
                    break;
                }
            case SA305:
                {
                    rstr += "SA 305 - code ";
                    break;
                }
            case GOLAY:
                {
                    rstr += "GOLAY  - " + RWCString(_sa._codeSimple) + " Function " + CtiNumStr(_sa._function);
                    break;
                }
            case SADIG:
                {
                    rstr += "SA DIG - " + RWCString(_sa._codeSimple);
                    break;
                }
            case GRP_SA_RTM:
                {
                    rstr += "SA RTM - command " + CtiNumStr(_sa._function);
                    break;
                }
            }
            break;
        }
    }

    if(_sa._retransmit)
    {
        rstr += " Retransmission.";
    }


    return rstr;
}


RWCString CtiProtocolSA3rdParty::strategyAsString() const
{
    RWCString rstr;

    switch(_sa._function)
    {
    case sac_toos:
        {
            rstr = RWCString( " temporary out of service " + CtiNumStr(_sa._swTimeout) + " hours" );
            break;
        }
    case sac_address_config:
        {
            rstr = RWCString( " address configuration: Code " + RWCString(_sa._codeSimple) + " assigned");
            break;
        }
    default:
        {
            rstr = RWCString(functionAsString() + " " + CtiNumStr(_sa._swTimeout) + " of " + CtiNumStr(_sa._cycleTime) + " seconds (" + CtiNumStr(_sTime) + "/" + CtiNumStr(_cTime) + "). " + CtiNumStr(_sa._repeats) + " period repeats.");
            break;
        }
    }

    return rstr;
}

RWCString CtiProtocolSA3rdParty::functionAsString() const
{
    RWCString rstr;

    switch(_sa._function)
    {
    case 1:
        {
            rstr += RWCString("Shed: Load 3.");
            break;
        }
    case 2:
        {
            rstr += RWCString("Test Off.");
            break;
        }
    case 3:
        {
            rstr += RWCString("Shed: Load 4.");
            break;
        }
    case 4:
        {
            rstr += RWCString("Restore: Load 4.");
            break;
        }
    case 5:
        {
            rstr += RWCString("Shed: Load 1,2,3.");
            break;
        }
    case 6:
        {
            rstr += RWCString("Restore: Load 1,2,3.");
            break;
        }
    case 7:
        {
            rstr += RWCString("Test On.");
            break;
        }
    case 8:
        {
            rstr += RWCString("Shed: Load 1.");
            break;
        }
    case 9:
        {
            rstr += RWCString("Restore: Load 1.");
            break;
        }
    case 10:
        {
            rstr += RWCString("Shed: Load 2.");
            break;
        }
    case 11:
        {
            rstr += RWCString("Restore: Load 2.");
            break;
        }
    case 12:
        {
            rstr += RWCString("Shed: Load 1,2,3,4.");
            break;
        }
    case 13:
        {
            rstr += RWCString("Restore: Load 1,2,3,4.");
            break;
        }
    case 14:
        {
            rstr += RWCString("Shed: Load 1,2.");
            break;
        }
    case 15:
        {
            rstr += RWCString("Restore: Load 1,2.");
            break;
        }
    default:
        {
            rstr += RWCString("Unknown Function.");
            break;
        }
    }

    return rstr;
}

void CtiProtocolSA3rdParty::computeSnCTime()
{
    _cTime = -1;
    _sTime = -1;

    switch(_sa._swTimeout)
    {
    case 450:
        {
            switch(_sa._cycleTime)
            {
            case 450:
                {
                    _cTime = 3;
                    _sTime = 5;
                    break;
                }
            case 900:
                {
                    _cTime = 2;
                    _sTime = 6;
                    break;
                }
            case 1800:
                {
                    _cTime = 0;
                    _sTime = 6;
                    break;
                }
            case 3600:
                {
                    _cTime = 0;
                    _sTime = 7;
                    break;
                }
            }
            break;
        }
    case 600:
        {
            switch(_sa._cycleTime)
            {
            case 1800:
                {
                    _cTime = 1;
                    _sTime = 6;
                    break;
                }
            }
            break;
        }
    case 660:
        {
            switch(_sa._cycleTime)
            {
            case 900:
                {
                    _cTime = 4;
                    _sTime = 6;
                    break;
                }
            }
            break;
        }
    case 720:
        {
            switch(_sa._cycleTime)
            {
            case 900:
                {
                    _cTime = 5;
                    _sTime = 6;
                    break;
                }
            }
            break;
        }
    case 750:
        {
            switch(_sa._cycleTime)
            {
            case 1800:
                {
                    _cTime = 4;
                    _sTime = 5;
                    break;
                }
            }
            break;
        }
    case 900:
        {
            switch(_sa._cycleTime)
            {
            case 900:
                {
                    _cTime = 0;
                    _sTime = 5;
                    break;
                }
            case 1800:
                {
                    _cTime = 0;
                    _sTime = 4;
                    break;
                }
            case 3600:
                {
                    _cTime = 1;
                    _sTime = 7;
                    break;
                }
            }
            break;
        }
    case 1200:
        {
            switch(_sa._cycleTime)
            {
            case 3600:
                {
                    _cTime = 2;
                    _sTime = 4;
                    break;
                }
            }
            break;
        }
    case 1350:
        {
            switch(_sa._cycleTime)
            {
            case 1800:
                {
                    _cTime = 1;
                    _sTime = 4;
                    break;
                }
            case 3600:
                {
                    _cTime = 2;
                    _sTime = 7;
                    break;
                }
            }
            break;
        }
    case 1800:
        {
            switch(_sa._cycleTime)
            {
            case 1800:
                {
                    _cTime = 1;
                    _sTime = 5;
                    break;
                }
            case 3600:
                {
                    _cTime = 3;
                    _sTime = 7;
                    break;
                }
            }
            break;
        }
    case 2250:
        {
            switch(_sa._cycleTime)
            {
            case 3600:
                {
                    _cTime = 4;
                    _sTime = 7;
                    break;
                }
            }
            break;
        }
    case 2400:
        {
            switch(_sa._cycleTime)
            {
            case 3600:
                {
                    _cTime = 3;
                    _sTime = 4;
                    break;
                }
            }
            break;
        }
    case 2700:
        {
            switch(_sa._cycleTime)
            {
            case 3600:
                {
                    _cTime = 5;
                    _sTime = 7;
                    break;
                }
            }
            break;
        }
    case 3150:
        {
            switch(_sa._cycleTime)
            {
            case 3600:
                {
                    _cTime = 6;
                    _sTime = 7;
                    break;
                }
            }
            break;
        }
    case 3600:
        {
            switch(_sa._cycleTime)
            {
            case 3600:
                {
                    _cTime = 2;
                    _sTime = 5;
                    break;
                }
            }
            break;
        }
    }

}

/*
 *  command may be one of the following:

 // TMS command type
    #define TMS_ONE 0
    #define TMS_ALL 1
    #define TMS_INIT 2
    #define TMS_ACK 3
 */

/*
INT CtiProtocolSA3rdParty::formRTMRequest(USHORT command)
{
    INT retCode;

    _sa._bufferLen = MAX_SA_MSG_SIZE;
    _sa._function = command;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        retCode = formatTMScmd(_sa._buffer, &_sa._bufferLen, command, _sa._transmitterAddress);
        dout << " " << _sa._transmitterAddress << endl;

        _sa._bufferLen = _sa._bufferLen * 2;
    }

    if(retCode)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " format returned " << retCode << endl;
        }
    }

    return retCode;
}
*/

/*----------------------------------------------------------------------------*
 * Function:formatTMScmd
 *
 * abuf      - output buffer. A fully formed TMS command in ASCII is built and placed
 *              in this buffer on successful completion of the function.
 *
 * buflen   - input/output.  On entry buflen indicates the allowed size of buf.
 *              On successful exit, buflen indicates the length of the completed
 *              message stored in buf.
 *
 * TMS_cmd_type - Input: TMS command type as defined above.
 *
 * xmitter  - input: transmitter address
 *
 * Returns:
 *          - A valid return code.
 *
 * Note:  This function is a proxy for the third-party library, so we can limit
 *          its linkage to the protocol DLL alone.
 *----------------------------------------------------------------------------*/
INT CtiProtocolSA3rdParty::formatTMScmd (UCHAR *abuf, INT *buflen, USHORT TMS_cmd_type, USHORT xmitter)
{
    return ::formatTMScmd(abuf, buflen, TMS_cmd_type, xmitter);
}

/*----------------------------------------------------------------------------*
 * Function:TMSlen
 *
 * abuf  - input: first 8 bytes of TMS response in ASCII.
 *
 * len   - output:  additional bytes to receive
 *
 * Returns:
 *          - A valid return code.
 *
 * Note:  This function is a proxy for the third-party library, so we can limit
 *          its linkage to the protocol DLL alone.
 *----------------------------------------------------------------------------*/
INT CtiProtocolSA3rdParty::TMSlen (UCHAR *abuf, INT *len)
{
    return ::TMSlen(abuf, len);
}

/*----------------------------------------------------------------------------*
 * Function:procTMSmsg
 *
 * abuf     - input ASCII buffer received from TMS.
 * len      - input: reference length when convert ASCII to binary
 * scode    - output if buf contains control code.
 * x205cmd  - output if buf contains SA205 config command.
 * Returns:
 *          - TMS_EMPTY,
 *            TMS_205CMD if abuf contains SA205 config command,
 *            TMS_CODE abuf contains control code,
 *            TMS_UNKNOWN.
 *
 * Note:  This function is a proxy for the third-party library, so we can limit
 *          its linkage to the protocol DLL alone.
 *----------------------------------------------------------------------------*/
INT CtiProtocolSA3rdParty::procTMSmsg(UCHAR *abuf, INT len, SA_CODE *scode, X205CMD *x205cmd)
{
    return ::procTMSmsg(abuf, len, scode, x205cmd);
}


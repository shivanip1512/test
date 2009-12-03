/*-----------------------------------------------------------------------------*
*
* File:   prot_sa3rdparty
*
* Date:   4/9/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.43 $
* DATE         :  $Date: 2007/04/11 14:37:53 $
*
* HISTORY      :
* $Log: prot_sa3rdparty.cpp,v $
* Revision 1.43  2007/04/11 14:37:53  jotteson
* Fix for problems related to boost.
*
* Revision 1.42  2006/04/21 15:18:27  mfisher
* made the Golay parsing logic a little more readable
*
* Revision 1.41  2006/04/19 15:48:30  mfisher
* added an overloaded int version of parseGolayAddress()
*
* Revision 1.40  2006/04/19 14:57:50  mfisher
* added parseGolayAddress() to resolve base address and function from a 6-digit Golay address
*
* Revision 1.39  2006/04/17 20:12:07  cplender
* Altered the processing of golay operational addresses to fully support F1,2,3 & 4
*
* Revision 1.38  2006/01/03 20:23:38  tspar
* Moved non RW string utilities from rwutil.h to utility.h
*
* Revision 1.37  2005/12/20 17:19:56  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.36  2005/08/24 20:49:15  cplender
* Getting restores to work right on 205s
*
* Revision 1.35  2005/08/19 21:11:49  cplender
* Altered the protocol to get restores to work correctly for GRE.  May need to remove a section of code once tested
*
* Revision 1.34  2005/08/15 15:13:19  cplender
* Minor change for the verification log table writes.
*
* Revision 1.33  2005/08/01 16:20:45  cplender
* Added a method to rediscover the cycletime and switch timeout from stime/ctime.
* Used by the rtm to convert the return to plain text.
*
* Revision 1.32  2005/07/29 16:26:02  cplender
* Making slight adjustments to better serve GRE's simple protocols.  Need to verify and have a plain text decode to review.
*
* Revision 1.30.2.5  2005/08/12 19:54:04  jliu
* Date Time Replaced
*
* Revision 1.30.2.4  2005/07/27 19:28:01  alauinger
* merged from the head 20050720
*

* Revision 1.30.2.3  2005/07/18 22:30:51  jliu
* rebuild_cppunit&correct_find
*
* Revision 1.30.2.2  2005/07/14 22:27:01  jliu
* RWCStringRemoved
*
* Revision 1.30.2.1  2005/07/12 21:08:42  jliu
* rpStringWithoutCmpParser
* Revision 1.31  2005/06/16 21:25:14  cplender
* Adding the RTC scan command and decode. Must be trested with a device.
*
* Revision 1.30  2005/06/13 19:10:21  cplender
* Working to get the correct messages sent for control history to work right.
*
* Revision 1.29  2005/05/31 21:05:55  cplender
* the cycle "count" is now one based to match versacom and expresscom parse syntax.
* Control history was off by one repeat prior to this checkin.
*
* Revision 1.28  2005/05/27 02:39:21  cplender
* Only allow 60 seconds max for the rtc time slot message.
*
* Revision 1.27  2005/05/16 22:27:15  cplender
* repeats cannot be greater than 8
*
* Revision 1.26  2005/05/16 20:37:24  cplender
* Altered the terminate syntax to send a 0 count cycle if we are before the last period or nothing if beyond that point.
*
* Revision 1.25  2005/05/13 16:11:53  cplender
* Altered the computeShedTimes() function to correctly choose the "closest" time to that asked for.
*
* Revision 1.24  2005/05/04 20:49:33  cplender
* Adjusted coldload and tamper detect code for the SA stuff.
*
* Revision 1.23  2005/04/27 13:45:01  cplender
* Code change to record the most recent control command to be sent out so that a restore can use the same stime/ctime.
* Removed unused OutMessage argument from several methods.
*
* Revision 1.22  2005/03/14 01:17:00  cplender
* Grab resore and terminate in the protocol.
*
* Revision 1.21  2005/02/17 23:36:32  cplender
* Prevent failure on a 105 or 205 restore.
*
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
#include <boost/regex.hpp>
#undef mask_                // Stupid RogueWave re.h

#include "cparms.h"
#include "logger.h"
#include "numstr.h"
#include "prot_sa3rdparty.h"
#include "protocol_sa.h"
#include "utility.h"

#define SA_PROTOCOL_COMPLETE TRUE

CtiProtocolSA3rdParty::CtiProtocolSA3rdParty() :
    _onePeriodTime(YUKONEOT),
    _messageReady(false),
    _errorLen(0),
    _period(0),
    _timeout(0)
{
    ::memset(&_sa, 0, sizeof(_sa));
    _sa._maxTxTime = (0x3c);

    memset( _errorBuf,    0, sizeof(_errorBuf) );
    memset( &_sa_code,    0, sizeof(SA_CODE) );
    memset( &_sa_x205cfg, 0, sizeof(X205CMD) );
}

CtiProtocolSA3rdParty::CtiProtocolSA3rdParty(const CtiSAData sa) :
    _onePeriodTime(YUKONEOT),
    _messageReady(true),
    _errorLen(0),
    _period(0),
    _timeout(0)
{
    _sa = sa;

    pair< int, int > scTimePair = computeSnCTime(_sa._swTimeout, _sa._cycleTime);
    _sa._sTime = scTimePair.first;
    _sa._cTime = scTimePair.second;

    memset( _errorBuf,    0, sizeof(_errorBuf) );
    memset( &_sa_code,    0, sizeof(SA_CODE) );
    memset( &_sa_x205cfg, 0, sizeof(X205CMD) );
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
            dout << CtiTime() << " **** ACH!!! Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return *this;
}



INT CtiProtocolSA3rdParty::parseCommand(CtiCommandParser &parse)
{
    INT status = NORMAL;

    _sa._commandType = parse.getCommand();

    switch(parse.getiValue("type"))
    {
    case ProtocolGolayType:
        {
            _sa._groupType = GOLAY;
            strncpy(_sa._codeSimple, parse.getsValue("sa_golaybase").c_str(), 7);
            break;
        }
    case ProtocolSADigitalType:
        {
            _sa._groupType = SADIG;
            _sa._mark = parse.getiValue("sa_mark");
            _sa._space = parse.getiValue("sa_space");
            strncpy(_sa._codeSimple, parse.getsValue("sa_codesimple").c_str(), 7);
            break;
        }
    case ProtocolSA105Type:
        {
            _sa._groupType = SA105;
            strncpy(_sa._codeSimple, parse.getsValue("sa_codesimple").c_str(), 7);
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

            if( !solveStrategy(parse) && NORMAL == (status = assembleControl( parse )) )
            {
                loadControl();
                _messageReady = true;
            }
            break;
        }
    case PutConfigRequest:
        {
            if( NORMAL == (status = assemblePutConfig( parse )) )
            {
                if(  parse.isKeyValid("sa_assign") && parse.isKeyValid("serial") )
                {
                    // There has been an assignment request!
                    string serialstr = CtiNumStr(parse.getiValue("serial"));
                    INT totalLen = 0;
                    // More than one config might be generated into the buffer.
                    _errorBuf[0] = '\0';
                    _errorLen = MAX_SAERR_MSG_SIZE;

                    _sa._buffer[0] = '/0';
                    _sa._bufferLen = MAX_SA_MSG_SIZE;

                    _sa._function = sac_address_config;

                    strncpy(_sa._serial, serialstr.c_str(), 33);

                    for(int i = 1; i <= 6; i++)     // Look for each slot assignment and add a blurb for it!
                    {
                        string slotstr = string("sa_slot") + CtiNumStr(i);

                        if(parse.isKeyValid(slotstr))
                        {
                            strncpy(_sa._codeSimple, parse.getsValue(slotstr).c_str(), 7);
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
                    string serialstr = CtiNumStr(parse.getiValue("serial"));
                    INT totalLen = 0;

                    _errorBuf[0] = '\0';
                    _errorLen = MAX_SAERR_MSG_SIZE;

                    _sa._buffer[0] = '/0';
                    _sa._bufferLen = MAX_SA_MSG_SIZE;

                    _sa._function = sac_toos;

                    strncpy(_sa._serial, serialstr.c_str(), 33);

                    tempOutOfService205(_sa._buffer, &_sa._bufferLen, _sa._serial, parse.getiValue("sa_offtime",0), _sa._transmitterAddress);

                    _messageReady = true;
                }
                else if( parse.isKeyValid("sa_coldload") && parse.isKeyValid("serial") )
                {
                    // There has been an assignment request!
                    string clpstr;
                    string serialstr = CtiNumStr(parse.getiValue("serial"));
                    INT totalLen = 0;
                    // More than one config might be generated into the buffer.
                    _errorBuf[0] = '\0';
                    _errorLen = MAX_SAERR_MSG_SIZE;

                    _sa._buffer[0] = '/0';
                    _sa._bufferLen = MAX_SA_MSG_SIZE;

                    strncpy(_sa._serial, serialstr.c_str(), 33);

                    int i, clsec, clpCount;
                    for(i = 1; i <= 4; i++)
                    {
                        clpstr = string("sa_clpf") + CtiNumStr(i); // coldload_r1,,... coldload_r4

                        if( (clsec = parse.getiValue(clpstr,-1)) >= 0 )
                        {
                            clpCount = (int)( (float)clsec / 14.0616 );
                            if(clpCount >= 255) clpCount = 255;
                            // Input:Cold Load Pickup Count, 0-255, 1 count = 14.0616seconds
                            {
                                CtiLockGuard<CtiLogger> slog_guard(slog);
                                slog << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                    string tdstr;
                    string serialstr = CtiNumStr(parse.getiValue("serial"));
                    INT totalLen = 0;
                    // More than one config might be generated into the buffer.
                    _errorBuf[0] = '\0';
                    _errorLen = MAX_SAERR_MSG_SIZE;

                    _sa._buffer[0] = '/0';
                    _sa._bufferLen = MAX_SA_MSG_SIZE;

                    strncpy(_sa._serial, serialstr.c_str(), 33);

                    int i, tdCount;
                    for(i = 1; i <= 4; i++)
                    {
                        tdstr = string("tamperdetect_f") + CtiNumStr(i);

                        if( (tdCount = parse.getiValue(tdstr,-1)) >= 0 )
                        {
                            {
                                CtiLockGuard<CtiLogger> slog_guard(slog);
                                slog << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << CtiTime() << " Unsupported command. Command = " << parse.getCommand() << endl;
            }

            status = CtiInvalidRequest;

            break;
        }
    }

    return status;
}

INT CtiProtocolSA3rdParty::assembleControl(CtiCommandParser &parse)
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
        INT repeat     = parse.getiValue("cycle_count", 8) - 1;  repeat = repeat < 0 ? 0 : repeat;  repeat = repeat > 7 ? 7 : repeat;

        // Add these two items to the list for control accounting!
        parse.setValue("control_reduction", parse.getiValue("cycle", 0) );
        parse.setValue("control_interval", 60 * period * (repeat+1));
    }
    else if(CtlReq == CMD_FLAG_CTL_RESTORE)
    {
        // Add these two items to the list for control accounting!
        parse.setValue("control_reduction", 0);
        parse.setValue("control_interval", 0);

        if(_sa._groupType != SA205 && _sa._groupType != SA105)
        {
            status = NoMethod;
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** CONTROL RESTORE? **** Cannot restore this type of group." << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        _sa._sTime = parse.getiValue("sa205_last_stime", 0);
        _sa._cTime = parse.getiValue("sa205_last_ctime", 0);

        // Graceful sends the last control interval information
        if(findStringIgnoreCase(parse.getCommandStr()," graceful"))
        {
            _onePeriodTime = CtiTime( (UINT)(parse.getdValue("sa205_one_period_time", YUKONEOT)) );
        }
    }
    else if(CtlReq == CMD_FLAG_CTL_TERMINATE)
    {
        INT delay = parse.getiValue("delaytime_sec", 0) / 60;
        parse.setValue("control_reduction", 0);
        parse.setValue("control_interval", 0);

        if(findStringIgnoreCase(parse.getCommandStr()," abrupt"))
        {
            _sa._sTime = 3;     // Force it to a di 7.5/7.5 control.
            _sa._cTime = 5;
        }
        else
        {
            // Repeat the initial control intformation.  This is a terminate graceful by default.
            _sa._sTime = parse.getiValue("sa205_last_stime", 3);
            _sa._cTime = parse.getiValue("sa205_last_ctime", 5);
            _onePeriodTime = CtiTime( (UINT)(parse.getdValue("sa205_one_period_time", YUKONEOT)) );
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Unsupported command.  Command = " << parse.getCommand() << endl;
        status = NoMethod;
    }

    return status;
}

INT CtiProtocolSA3rdParty::assemblePutConfig(CtiCommandParser &parse)
{
    INT status = NORMAL;

    string   temp, token;
    string   str = parse.getCommandStr();

    if(str.find(" assign")!=string::npos)
    {
        string mstr;
        string rwsslot;
        string rwsaddr;

        int i;
        for(i = 1; i <= 6; i++)
        {
            mstr = CtiNumStr(i) + string(" *= *[0-9]+");
            boost::regex e1(mstr);
            boost::match_results<std::string::const_iterator> what;
            if(boost::regex_search(str, what, e1, boost::match_default))
            {
                temp = string(what[0]);

                boost::char_separator<char> sep("=");
                Boost_char_tokenizer slotok(temp, sep);
                Boost_char_tokenizer::iterator tok_iter = slotok.begin();
                if( tok_iter != slotok.end() )
                {
                    temp = trim_left(temp, *tok_iter);
                }

                boost::char_separator<char> sep1("=,\r\n ");
                Boost_char_tokenizer _slotok(temp, sep1);
                Boost_char_tokenizer::iterator _tok_iter = _slotok.begin();


                if( tok_iter != slotok.end() )
                {
                    rwsslot = *tok_iter;
                }
                if( _tok_iter != _slotok.end() )
                {
                    rwsaddr = *_tok_iter;
                }

                int addr = atoi(rwsaddr.c_str());

                mstr = string("sa_slot") + CtiNumStr(i); // sa_slot1, sa_slot2,... sa_slot6.

                parse.setValue(mstr, rwsaddr);              // Stored as a string because the Telvent lib wants it that way!!

                {
                    CtiLockGuard<CtiLogger> slog_guard(slog);
                    slog << CtiTime() << " Address config command. Serial " << parse.getiValue("serial") << " writing address " << addr << " to slot " << i << " on the receiver" << endl;
                }
            }
        }
    }
    else if(str.find(" override")!=string::npos)
    {
        INT offtime = 0;

        boost::regex e1(" override +[0-9]+");
        boost::match_results<std::string::const_iterator> what;
        if(boost::regex_search(str, what, e1, boost::match_default))
        {
            token = string(what[0]);
            e1.assign("[0-9]+");
            boost::regex_search(str, what, e1, boost::match_default);
            str = string(what[0]);
            offtime = atoi(str.c_str());
            {
                CtiLockGuard<CtiLogger> slog_guard(slog);
                slog << CtiTime() << " Temporary service command. Serial " << parse.getiValue("serial") << " Offhours = " << offtime << endl;
            }
            parse.setValue("sa_offtime", offtime);              // Stored as a string because the Telvent lib wants it that way!!
        }
    }
    else if(str.find(" service")!=string::npos)
    {
        boost::regex e1("service +((in)|(out)|(enable)|(disable))");
        boost::match_results<std::string::const_iterator> what;
        if(str.find(" temp") !=string::npos&& boost::regex_search(str, what, e1, boost::match_default))
        {
            INT offtime = 0;
            e1.assign(" offhours +[0-9]+");

            if(boost::regex_search(str, what, e1, boost::match_default))
            {
                token = string(what[0]);
                e1.assign("[0-9]+");
                boost::regex_search(token, what, e1, boost::match_default);
                str = string(what[0]);
                offtime = atoi(str.c_str());
                {
                    CtiLockGuard<CtiLogger> slog_guard(slog);
                    slog << CtiTime() << " Temporary service command. Serial " << parse.getiValue("serial") << " Offhours = " << offtime << endl;
                }
                parse.setValue("sa_offtime", offtime);              // Stored as a string because the Telvent lib wants it that way!!
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " Unknown service command: \"" << str << "\"" <<endl;
            }
        }
    }
    else if(parse.isKeyValid("sa_coldload"))
    {
        // Handled in parseCommand()
        // coldLoadPickup205
    }
    else if(parse.isKeyValid("sa_tamper"))
    {
        // Handled in parseCommand()
        // tamperDetect205
    }

    return status;
}

INT CtiProtocolSA3rdParty::loadControl()
{
    INT status = NORMAL;

    SA_CODE scode;
    INT retCode = 0;

    _errorBuf[0] = '\0';
    _errorLen = MAX_SAERR_MSG_SIZE;

    if(_sa._groupType == SA205 || _sa._groupType == SA105)
    {
        string strcode = CtiNumStr(_sa._code205);
        ::strncpy(scode.code, strcode.c_str(), 7);

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
            retCode = control105_205(_sa._buffer, &_sa._bufferLen, &scode, _sa._transmitterAddress, _sa._sTime, _sa._cTime);

            if( getDebugLevel() & DEBUGLEVEL_SA3RDPARTY )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " control105_205() complete" << endl;
            }

            break;
        }
    case GOLAY:
        {
            retCode = controlGolay(_sa._buffer, &_sa._bufferLen, _sa._codeSimple, _sa._function, _sa._transmitterAddress);

            if( getDebugLevel() & DEBUGLEVEL_SA3RDPARTY )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " controlGolay() complete" << endl;
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
                dout << CtiTime() << " controlSADigital() complete" << endl;
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

    INT retCode = 0;

    switch(_sa._groupType)
    {
    case SA205:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                retCode = config205(&_sa._buffer[len], &_sa._bufferLen, _sa._serial, slot, _sa._codeSimple, _sa._transmitterAddress);
                dout << CtiTime() << " config205() complete" << endl;
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
                dout << CtiTime() << " Address assignment is not supported by protocol for grouptype " << _sa._groupType << endl;
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
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}

int CtiProtocolSA3rdParty::solveStrategy(CtiCommandParser &parse)
{
    INT status = NORMAL;
    bool dlc_control = (parse.getCommandStr().find(" dlc")!=string::npos);      // if set, we want DLC (not DI) control!

    // We only try to predict it if it has not already been fully identified for us.
    if(parse.isKeyValid("sa_restore") ||
       findStringIgnoreCase(parse.getCommandStr()," restore") ||
       findStringIgnoreCase(parse.getCommandStr()," terminate") )
    {
        _sa._repeats = 0;
        _sa._swTimeout = 450;
        _sa._cycleTime = 450;

        if(dlc_control)
        {
            _sa._sTime = 0;
            _sa._cTime = 0;
        }
        else
        {
            _sa._sTime = 3;
            _sa._cTime = 5;
        }
    }
    else
    {
        int shed_seconds = parse.getiValue("shed", 0);
        int cycle_percent = parse.getiValue("cycle",0);
        int cycle_period = parse.getiValue("cycle_period", 30);
        int cycle_count = parse.getiValue("cycle_count", 8) - 1;  cycle_count = cycle_count < 0 ? 0 : cycle_count;   cycle_count = cycle_count > 7 ? 7 : cycle_count;           // Make sure we are not negative!

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
                        _sa._sTime = 0;
                        _sa._cTime = 0;
                    }
                    else
                    {
                        _sa._sTime = 3;
                        _sa._cTime = 5;
                    }
                }
                else
                {
                    status = BADPARAM;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Cycle parameters are not available on this switch for selected period." << endl;
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
                        _sa._sTime = 0;
                        _sa._cTime = 1;
                    }
                    else
                    {
                        _sa._sTime = 2;
                        _sa._cTime = 6;
                    }
                }
                else if(cycle_percent <= 67)
                {
                    _sa._swTimeout = 600;
                    _sa._sTime = 3;
                    _sa._cTime = 6;
                }
                else if(cycle_percent <= 73)
                {
                    _sa._swTimeout = 660;
                    _sa._sTime = 4;
                    _sa._cTime = 6;
                }
                else if(cycle_percent <= 80)
                {
                    _sa._swTimeout = 720;
                    _sa._sTime = 5;
                    _sa._cTime = 6;
                }
                else if(cycle_percent <= 100)
                {
                    _sa._swTimeout = 900;
                    if(dlc_control)
                    {
                        _sa._sTime = 1;
                        _sa._cTime = 1;
                    }
                    else
                    {
                        _sa._sTime = 0;
                        _sa._cTime = 5;
                    }
                }
                else
                {
                    status = BADPARAM;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Cycle parameters are not available on this switch for selected period." << endl;
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
                    _sa._sTime = 2;
                    _sa._cTime = 0;
                }
                else if(cycle_percent <= 67)
                {
                    _sa._swTimeout = 900;
                    _sa._sTime = 2;
                    _sa._cTime = 1;
                }
                else if(cycle_percent <= 100)
                {
                    _sa._swTimeout = 1350;
                    _sa._sTime = 2;
                    _sa._cTime = 2;
                }
                else
                {
                    status = BADPARAM;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Cycle parameters are not available on this switch for selected period." << endl;
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
                        _sa._sTime = 3;
                        _sa._cTime = 0;
                    }
                    else
                    {
                        _sa._sTime = 0;
                        _sa._cTime = 6;
                    }
                }
                else if(cycle_percent <= 33)
                {
                    _sa._swTimeout = 600;
                    _sa._sTime = 1;
                    _sa._cTime = 6;
                }
                else if(cycle_percent <= 50)
                {
                    _sa._swTimeout = 900;
                    if(dlc_control)
                    {
                        _sa._sTime = 3;
                        _sa._cTime = 1;
                    }
                    else
                    {
                        _sa._sTime = 0;
                        _sa._cTime = 4;
                    }
                }
                else if(cycle_percent <= 75)
                {
                    _sa._swTimeout = 1350;
                    if(dlc_control)
                    {
                        _sa._sTime = 3;
                        _sa._cTime = 2;
                    }
                    else
                    {
                        _sa._sTime = 1;
                        _sa._cTime = 4;
                    }
                }
                else if(cycle_percent <= 100)
                {
                    _sa._swTimeout = 1800;
                    if(dlc_control)
                    {
                        _sa._sTime = 3;
                        _sa._cTime = 3;
                    }
                    else
                    {
                        _sa._sTime = 1;
                        _sa._cTime = 5;
                    }
                }
                else
                {
                    status = BADPARAM;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Cycle parameters are not available on this switch for selected period." << endl;
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
                    _sa._sTime = 4;
                    _sa._cTime = 0;
                }
                else if(cycle_percent <= 40)
                {
                    _sa._swTimeout = 900;
                    _sa._sTime = 4;
                    _sa._cTime = 1;
                }
                else if(cycle_percent <= 60)
                {
                    _sa._swTimeout = 1350;
                    _sa._sTime = 4;
                    _sa._cTime = 2;
                }
                else if(cycle_percent <= 80)
                {
                    _sa._swTimeout = 1800;
                    _sa._sTime = 4;
                    _sa._cTime = 3;
                }
                else if(cycle_percent <= 100)
                {
                    _sa._swTimeout = 2250;
                    _sa._sTime = 4;
                    _sa._cTime = 4;
                }
                else
                {
                    status = BADPARAM;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Cycle parameters are not available on this switch for selected period." << endl;
                    }
                }
            }
            else if(cycle_period <= 45)
            {
                _sa._cycleTime = 2700;

                if(cycle_percent <= 17)
                {
                    _sa._swTimeout = 450;
                    _sa._sTime = 5;
                    _sa._cTime = 0;
                }
                else if(cycle_percent <= 33)
                {
                    _sa._swTimeout = 900;
                    _sa._sTime = 5;
                    _sa._cTime = 1;
                }
                else if(cycle_percent <= 50)
                {
                    _sa._swTimeout = 1350;
                    _sa._sTime = 5;
                    _sa._cTime = 2;
                }
                else if(cycle_percent <= 67)
                {
                    _sa._swTimeout = 1800;
                    _sa._sTime = 5;
                    _sa._cTime = 3;
                }
                else if(cycle_percent <= 83)
                {
                    _sa._swTimeout = 2250;
                    _sa._sTime = 5;
                    _sa._cTime = 4;
                }
                else if(cycle_percent <= 100)
                {
                    _sa._swTimeout = 2700;
                    _sa._sTime = 5;
                    _sa._cTime = 5;
                }
                else
                {
                    status = BADPARAM;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Cycle parameters are not available on this switch for selected period." << endl;
                    }
                }
            }
            else if(cycle_period <= 53)
            {
                _sa._cycleTime = 3150;

                if(cycle_percent <= 14)
                {
                    _sa._swTimeout = 450;
                    _sa._sTime = 6;
                    _sa._cTime = 0;
                }
                else if(cycle_percent <= 29)
                {
                    _sa._swTimeout = 900;
                    _sa._sTime = 6;
                    _sa._cTime = 1;
                }
                else if(cycle_percent <= 43)
                {
                    _sa._swTimeout = 1350;
                    _sa._sTime = 6;
                    _sa._cTime = 2;
                }
                else if(cycle_percent <= 57)
                {
                    _sa._swTimeout = 1800;
                    _sa._sTime = 6;
                    _sa._cTime = 3;
                }
                else if(cycle_percent <= 71)
                {
                    _sa._swTimeout = 2250;
                    _sa._sTime = 6;
                    _sa._cTime = 4;
                }
                else if(cycle_percent <= 86)
                {
                    _sa._swTimeout = 2700;
                    _sa._sTime = 6;
                    _sa._cTime = 5;
                }
                else if(cycle_percent <= 100)
                {
                    _sa._swTimeout = 3150;
                    _sa._sTime = 6;
                    _sa._cTime = 6;
                }
                else
                {
                    status = BADPARAM;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Cycle parameters are not available on this switch for selected period." << endl;
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
                        _sa._sTime = 7;
                        _sa._cTime = 0;
                    }
                    else
                    {
                        _sa._sTime = 0;
                        _sa._cTime = 7;
                    }
                }
                else if(cycle_percent <= 25)
                {
                    _sa._swTimeout = 900;
                    if(dlc_control)
                    {
                        _sa._sTime = 7;
                        _sa._cTime = 1;
                    }
                    else
                    {
                        _sa._sTime = 1;
                        _sa._cTime = 7;
                    }
                }
                else if(cycle_percent <= 33)
                {
                    _sa._swTimeout = 1200;
                    _sa._sTime = 2;
                    _sa._cTime = 4;
                }
                else if(cycle_percent <= 38)
                {
                    _sa._swTimeout = 1350;
                    if(dlc_control)
                    {
                        _sa._sTime = 7;
                        _sa._cTime = 2;
                    }
                    else
                    {
                        _sa._sTime = 2;
                        _sa._cTime = 7;
                    }
                }
                else if(cycle_percent <= 50)
                {
                    _sa._swTimeout = 1800;
                    if(dlc_control)
                    {
                        _sa._sTime = 7;
                        _sa._cTime = 3;
                    }
                    else
                    {
                        _sa._sTime = 3;
                        _sa._cTime = 7;
                    }
                }
                else if(cycle_percent <= 63)
                {
                    _sa._swTimeout = 2250;
                    if(dlc_control)
                    {
                        _sa._sTime = 7;
                        _sa._cTime = 4;
                    }
                    else
                    {
                        _sa._sTime = 4;
                        _sa._cTime = 7;
                    }
                }
                else if(cycle_percent <= 67)
                {
                    _sa._swTimeout = 2400;
                    _sa._sTime = 3;
                    _sa._cTime = 4;
                }
                else if(cycle_percent <= 75)
                {
                    _sa._swTimeout = 2700;
                    if(dlc_control)
                    {
                        _sa._sTime = 7;
                        _sa._cTime = 5;
                    }
                    else
                    {
                        _sa._sTime = 5;
                        _sa._cTime = 7;
                    }
                }
                else if(cycle_percent <= 88)
                {
                    _sa._swTimeout = 3150;
                    if(dlc_control)
                    {
                        _sa._sTime = 7;
                        _sa._cTime = 6;
                    }
                    else
                    {
                        _sa._sTime = 6;
                        _sa._cTime = 7;
                    }
                }
                else if(cycle_percent <= 100)
                {
                    _sa._swTimeout = 3600;
                    if(dlc_control)
                    {
                        _sa._sTime = 7;
                        _sa._cTime = 7;
                    }
                    else
                    {
                        _sa._sTime = 2;
                        _sa._cTime = 5;
                    }
                }
                else
                {
                    status = BADPARAM;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Cycle parameters are not available on this switch for selected period." << endl;
                    }
                }
            }

            _sa._repeats = cycle_count;

            if(cycle_count >= 0)
            {
                _onePeriodTime = CtiTime() + (_sa._cycleTime * cycle_count);
            }
            else
            {
                _onePeriodTime = CtiTime();  // This is not a cycle we want to record.  Set to EOT.
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Unknown command check syntax. **** " << __FILE__ << " (" << __LINE__ << ") " << parse.getCommandStr() << endl;
            }
        }
    }


    if(0)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setCodeGolay( string val )
{
    strncpy(_sa._codeSimple, val.c_str(), 6);
    return *this;
}

CtiProtocolSA3rdParty& CtiProtocolSA3rdParty::setCodeSADigital( string val )
{
    strncpy(_sa._codeSimple, val.c_str(), 6);
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
    int oactime[] = { 0, 1, 2, 3, 4, 5, 6, 7 };    // These are the available timeout times in "matrix"
    int oastime[] = { 0, 1, 2, 3, 4, 5, 6, 7  };   // These are the available cycle times in "matrix"
#elif SA_USE_DI_ONLY
    int ctimes[] = { 450, 900, 1800, 3600 };        // These are the available cycle times in seconds
    int oactime[] = { 3, 0, 1, 2 };                 // These are the available timeout times in "matrix"
    int oastime[] = { 5, 5, 5, 5 };                 // These are the available cycle times in "matrix"
#else
    int ctimes[] = { 450, 900, 1350, 1800, 2250, 2700, 3150, 3600 };        // These are the available cycle times in seconds
    int oactime[] = { 0, 1, 2, 3, 4, 5, 6, 7 };    // These are the available timeout times in "matrix"
    int oastime[] = { 0, 1, 2, 3, 4, 5, 6, 7  };   // These are the available cycle times in "matrix"
#endif

    int rep;
    int bestoffset = 0;                             // Pick a 450 scond shed by default?
    int ctimesize = sizeof(ctimes)/sizeof(int);
    int currentbestdelta = INT_MAX;                         // We must be closer than one hour!

    for(i = 0; i < ctimesize; i++)
    {
        if(!(shed_time % ctimes[i]) && (ctimes[i] * 8 >= shed_time))  // This one divides evenly and is large enough to meet the goal! (A perfect match)
        {
            _sa._cycleTime = ctimes[i];
            _sa._swTimeout = ctimes[i];
            rep = (shed_time / ctimes[i]) - 1;
            rep = rep < 0 ? 0 : rep;
            _sa._repeats = rep;
            _sa._cTime = oactime[i];
            _sa._sTime = oastime[i];
            break;
        }
        else
        {
            // This one is not a perfect match, so we will try to see how close we can get!
            rep = shed_time / ctimes[i] > 8 ? 7 : (shed_time / ctimes[i]) - 1;
            rep = rep < 0 ? 0 : rep;
            int delta = ::abs(shed_time - ((rep+1) * ctimes[i]));

            if(delta < currentbestdelta)
            {
                currentbestdelta = delta;

                _sa._cycleTime = ctimes[i];
                _sa._swTimeout = ctimes[i];
                _sa._repeats = rep;

                _sa._cTime = oactime[i];
                _sa._sTime = oastime[i];
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

void CtiProtocolSA3rdParty::copyMessage(string &str) const
{
    INT i;

    str = string();
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
    if(maxTx < 1 || maxTx > 60)
    {
        _sa._maxTxTime = 60;
    }
    else
    {
        _sa._maxTxTime = maxTx;
    }
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

/*
 * Perform a status scan on the RTC.
 */
void CtiProtocolSA3rdParty::statusScan(int transmitter, BYTE *dest, ULONG &len)
{
    int i = len;
    BYTE crc = 0;

    _sa._transmitterAddress = transmitter;

    dest[i] = 0xa0 | (_sa._transmitterAddress & 0x0f);
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = 0x23;                                       // Fixed identifier.
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = 0x00;                                       // Fixed identifier.
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = 0x00;                                       // Fixed identifier.
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = 0x00;                                       // Fixed identifier.
    crc ^= dest[i++];                                     // Compute CRC and advance i.
    dest[i] = 0x00;                                       // Fixed identifier.
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
    pair< int, int > scTimePair = computeSnCTime(_sa._swTimeout, _sa._cycleTime);
    _sa._sTime = scTimePair.first;
    _sa._cTime = scTimePair.second;
    return *this;
}


string CtiProtocolSA3rdParty::asString(const CtiSAData &sa)
{
    string rstr;

    switch(sa._commandType)
    {
    case PutConfigRequest:
        {
            rstr += "SA 205 - config. Serial " + string(sa._serial) + " - " + strategyAsString(sa);
            break;
        }
    case ControlRequest:
        {
            switch(sa._groupType)
            {
            case SA105:
                {
                    rstr += "SA 105 - code " + string(sa._codeSimple) + " - " + strategyAsString(sa);
                    break;
                }
            case SA205:
                {
                    rstr += "SA 205 - code " + CtiNumStr(sa._code205) + " - " + strategyAsString(sa);
                    break;
                }
            case SA305:
                {
                    rstr += "SA 305 - code ";
                    break;
                }
            case GOLAY:
                {
                    rstr += "GOLAY  - " + string(sa._codeSimple) + " Function " + CtiNumStr(sa._function);
                    break;
                }
            case SADIG:
                {
                    rstr += "SA DIG - " + string(sa._codeSimple);
                    break;
                }
            case GRP_SA_RTM:
                {
                    rstr += string("SA RTM - command ") + CtiNumStr(sa._function);
                    break;
                }
            }
            break;
        }
    }

    if(sa._retransmit)
    {
        rstr += " Retransmission.";
    }


    return rstr;
}


string CtiProtocolSA3rdParty::strategyAsString(const CtiSAData &sa)
{
    string rstr;

    switch(sa._function)
    {
    case sac_toos:
        {
            rstr = string( " Temporary out of service " + CtiNumStr(sa._swTimeout) + " hours" );
            break;
        }
    case sac_address_config:
        {
            rstr = string( " Address configuration: Code " + string(sa._codeSimple) );// + " assigned to position " + string(sa._codeSlot));
            break;
        }
    default:
        {
            rstr = string(functionAsString(sa) + " " + CtiNumStr(sa._swTimeout) + " of " + CtiNumStr(sa._cycleTime) + " seconds (" + CtiNumStr(sa._sTime) + "/" + CtiNumStr(sa._cTime) + "). " + CtiNumStr(sa._repeats) + " period repeats.");
            break;
        }
    }

    return rstr;
}

string CtiProtocolSA3rdParty::functionAsString(const CtiSAData &sa)
{
    string rstr;

    switch(sa._function)
    {
    case 0:
        {
            rstr += string("Memory Reset.");
            break;
        }
    case 1:
        {
            rstr += string("Shed: Load 3.");
            break;
        }
    case 2:
        {
            rstr += string("Test Off.");
            break;
        }
    case 3:
        {
            rstr += string("Shed: Load 4.");
            break;
        }
    case 4:
        {
            rstr += string("Restore: Load 4.");
            break;
        }
    case 5:
        {
            rstr += string("Shed: Load 1,2,3.");
            break;
        }
    case 6:
        {
            rstr += string("Restore: Load 1,2,3.");
            break;
        }
    case 7:
        {
            rstr += string("Test On.");
            break;
        }
    case 8:
        {
            rstr += string("Shed: Load 1.");
            break;
        }
    case 9:
        {
            rstr += string("Restore: Load 1.");
            break;
        }
    case 10:
        {
            rstr += string("Shed: Load 2.");
            break;
        }
    case 11:
        {
            rstr += string("Restore: Load 2.");
            break;
        }
    case 12:
        {
            rstr += string("Shed: Load 1,2,3,4.");
            break;
        }
    case 13:
        {
            rstr += string("Restore: Load 1,2,3,4.");
            break;
        }
    case 14:
        {
            rstr += string("Shed: Load 1,2.");
            break;
        }
    case 15:
        {
            rstr += string("Restore: Load 1,2.");
            break;
        }
    default:
        {
            rstr += string("Unknown Function.");
            break;
        }
    }

    return rstr;
}

pair< int, int > CtiProtocolSA3rdParty::computeSnCTime(const int swTimeout, const int cycleTime)
{
    int cTime = -1;
    int sTime = -1;

    switch(swTimeout)
    {
    case 450:
        {
            switch(cycleTime)
            {
            case 450:
                {
                    cTime = 3;
                    sTime = 5;
                    break;
                }
            case 900:
                {
                    cTime = 2;
                    sTime = 6;
                    break;
                }
            case 1800:
                {
                    cTime = 0;
                    sTime = 6;
                    break;
                }
            case 3600:
                {
                    cTime = 0;
                    sTime = 7;
                    break;
                }
            }
            break;
        }
    case 600:
        {
            switch(cycleTime)
            {
            case 1800:
                {
                    cTime = 1;
                    sTime = 6;
                    break;
                }
            }
            break;
        }
    case 660:
        {
            switch(cycleTime)
            {
            case 900:
                {
                    cTime = 4;
                    sTime = 6;
                    break;
                }
            }
            break;
        }
    case 720:
        {
            switch(cycleTime)
            {
            case 900:
                {
                    cTime = 5;
                    sTime = 6;
                    break;
                }
            }
            break;
        }
    case 750:
        {
            switch(cycleTime)
            {
            case 1800:
                {
                    cTime = 4;
                    sTime = 5;
                    break;
                }
            }
            break;
        }
    case 900:
        {
            switch(cycleTime)
            {
            case 900:
                {
                    cTime = 0;
                    sTime = 5;
                    break;
                }
            case 1800:
                {
                    cTime = 0;
                    sTime = 4;
                    break;
                }
            case 3600:
                {
                    cTime = 1;
                    sTime = 7;
                    break;
                }
            }
            break;
        }
    case 1200:
        {
            switch(cycleTime)
            {
            case 3600:
                {
                    cTime = 2;
                    sTime = 4;
                    break;
                }
            }
            break;
        }
    case 1350:
        {
            switch(cycleTime)
            {
            case 1800:
                {
                    cTime = 1;
                    sTime = 4;
                    break;
                }
            case 3600:
                {
                    cTime = 2;
                    sTime = 7;
                    break;
                }
            }
            break;
        }
    case 1800:
        {
            switch(cycleTime)
            {
            case 1800:
                {
                    cTime = 1;
                    sTime = 5;
                    break;
                }
            case 3600:
                {
                    cTime = 3;
                    sTime = 7;
                    break;
                }
            }
            break;
        }
    case 2250:
        {
            switch(cycleTime)
            {
            case 3600:
                {
                    cTime = 4;
                    sTime = 7;
                    break;
                }
            }
            break;
        }
    case 2400:
        {
            switch(cycleTime)
            {
            case 3600:
                {
                    cTime = 3;
                    sTime = 4;
                    break;
                }
            }
            break;
        }
    case 2700:
        {
            switch(cycleTime)
            {
            case 3600:
                {
                    cTime = 5;
                    sTime = 7;
                    break;
                }
            }
            break;
        }
    case 3150:
        {
            switch(cycleTime)
            {
            case 3600:
                {
                    cTime = 6;
                    sTime = 7;
                    break;
                }
            }
            break;
        }
    case 3600:
        {
            switch(cycleTime)
            {
            case 3600:
                {
                    cTime = 2;
                    sTime = 5;
                    break;
                }
            }
            break;
        }
    }

    return std::make_pair( sTime, cTime );
}


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
 * _sa_code    - output if buf contains control code.
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


pair< unsigned long, unsigned > CtiProtocolSA3rdParty::parseGolayAddress(const string &golay_code)
{
    unsigned long code = strtoul(golay_code.c_str(), 0, 10);

    return parseGolayAddress(code);
}


pair< unsigned long, unsigned > CtiProtocolSA3rdParty::parseGolayAddress(unsigned long code)
{
    unsigned function = 1;

    //  This code attempts to determine the "function" based upon the bits of the A & B words (BBAABB).
    bool a_word_odd = ((code / 100) % 2) == 1;
    bool b_word_odd = (code % 2) == 1;

    if( a_word_odd )
    {
        code -= 100;
        function += 2;
    }

    if( b_word_odd )
    {
        code -= 1;
        function += 1;
    }

    return std::make_pair(code, function);
}


int CtiProtocolSA3rdParty::getStrategySTime() const
{
    return _sa._sTime;
}

int CtiProtocolSA3rdParty::getStrategyCTime() const
{
    return _sa._cTime;
}

CtiTime CtiProtocolSA3rdParty::getStrategyOnePeriodTime() const
{
    return _onePeriodTime;
}

string CtiProtocolSA3rdParty::asString(const SA_CODE &sa)
{
    CtiSAData saData;

    try
    {
        saData._groupType = sa.type;
        saData._cTime = sa.cycleTime;
        saData._sTime = sa.swTime;
        saData._function = sa.function;
        saData._repeats = sa.repeats;

        saData._commandType = ControlRequest;           // ACH This may not always be correct!!!
        saData._retransmit = 0;                         // This is not a retransmit.

        pair< int, int > scTimePair = computeSWnCTTime(saData._sTime, saData._cTime, sa.type == SA105);
        saData._swTimeout = scTimePair.first;
        saData._cycleTime = scTimePair.second;

        switch(sa.type)
        {
        case SA205:
            {
                saData._code205 = atoi(sa.code);
                break;
            }
        case GOLAY:
        case SADIG:
            {
                memcpy(saData._codeSimple, sa.code, 7);
                saData._codeSimple[6] = '\0';
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        }

    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return CtiProtocolSA3rdParty::asString(saData);
}

string CtiProtocolSA3rdParty::asString(const X205CMD &cmd)
{
    string str;

    return str;
}

pair< int, int > CtiProtocolSA3rdParty::computeSWnCTTime(const int sTime, const int cTime, bool gt105)
{
    bool b205only = false;
    int cycleTime = -1;
    int swTimeout = -1;

    switch(cTime)
    {
    case 0:
        {
            switch(sTime)
            {
            case 0:
                {
                    swTimeout = 450;    // 100% DLC
                    cycleTime = 450;
                    break;
                }
            case 1:
                {
                    swTimeout = 450;
                    cycleTime = 900;
                    break;
                }
            case 2:
                {
                    swTimeout = 450;
                    cycleTime = 1350;       // 22.5*60;
                    break;
                }
            case 3:
                {
                    swTimeout = 450;
                    cycleTime = 1800;
                    break;
                }
            case 4:
                {
                    swTimeout = 450;
                    cycleTime = 2250;
                    break;
                }
            case 5:
                {
                    swTimeout = 450;
                    cycleTime = 2700;
                    break;
                }
            case 6:
                {
                    swTimeout = 450;
                    cycleTime = 3150;
                    break;
                }
            case 7:
                {
                    swTimeout = 450;
                    cycleTime = 3600;
                    break;
                }
            }
            break;
        }
    case 1:
        {
            switch(sTime)
            {
            case 0:
                {
                    swTimeout = 900;    // 100% DLC
                    cycleTime = 900;
                    break;
                }
            case 1:
                {
                    swTimeout = 900;
                    cycleTime = 900;
                    break;
                }
            case 2:
                {
                    swTimeout = 900;
                    cycleTime = 1350;       // 22.5*60;
                    break;
                }
            case 3:
                {
                    swTimeout = 900;
                    cycleTime = 1800;
                    break;
                }
            case 4:
                {
                    swTimeout = 900;
                    cycleTime = 2250;
                    break;
                }
            case 5:
                {
                    swTimeout = 900;
                    cycleTime = 2700;
                    break;
                }
            case 6:
                {
                    swTimeout = 900;
                    cycleTime = 3150;
                    break;
                }
            case 7:
                {
                    swTimeout = 900;
                    cycleTime = 3600;
                    break;
                }
            }
            break;
        }
    case 2:
        {
            switch(sTime)
            {
            case 0:
                {
                    swTimeout = 1350;    // 100% DLC
                    cycleTime = 1350;
                    break;
                }
            case 1:
                {
                    swTimeout = 1350;
                    cycleTime = 1350;
                    break;
                }
            case 2:
                {
                    swTimeout = 1350;
                    cycleTime = 1350;       // 22.5*60;
                    break;
                }
            case 3:
                {
                    swTimeout = 1350;
                    cycleTime = 1800;
                    break;
                }
            case 4:
                {
                    swTimeout = 1350;
                    cycleTime = 2250;
                    break;
                }
            case 5:
                {
                    swTimeout = 1350;
                    cycleTime = 2700;
                    break;
                }
            case 6:
                {
                    swTimeout = 1350;
                    cycleTime = 3150;
                    break;
                }
            case 7:
                {
                    swTimeout = 1350;
                    cycleTime = 3600;
                    break;
                }
            }
            break;
        }
    case 3:
        {
            switch(sTime)
            {
            case 0:
                {
                    swTimeout = 1800;
                    cycleTime = 1800;
                    break;
                }
            case 1:
                {
                    swTimeout = 1800;
                    cycleTime = 1800;
                    break;
                }
            case 2:
                {
                    swTimeout = 1800;
                    cycleTime = 1800;
                    break;
                }
            case 3:
                {
                    swTimeout = 1800;
                    cycleTime = 1800;
                    break;
                }
            case 4:
                {
                    swTimeout = 1800;
                    cycleTime = 2250;
                    break;
                }
            case 5:
                {
                    swTimeout = 1800;
                    cycleTime = 2700;
                    break;
                }
            case 6:
                {
                    swTimeout = 1800;
                    cycleTime = 3150;
                    break;
                }
            case 7:
                {
                    swTimeout = 1800;
                    cycleTime = 3600;
                    break;
                }
            }
            break;
        }
    case 4:
        {
            switch(sTime)
            {
            case 0:
                {
                    cycleTime = 1800;    // 50% 30 min DI
                    swTimeout = 900;
                    b205only = true;
                    break;
                }
            case 1:
                {
                    cycleTime = 1800;     // 75% 30 min DI
                    swTimeout = 1350;
                    b205only = true;
                    break;
                }
            case 2:
                {
                    cycleTime = 3600;     // 33.33% 60 min DI
                    swTimeout = 1200;
                    b205only = true;
                    break;
                }
            case 3:
                {
                    cycleTime = 3600;   // 66.67% 60 min DI
                    swTimeout = 2400;
                    b205only = true;
                    break;
                }
            case 4:
                {
                    swTimeout = 2250;   // 100% DLC
                    cycleTime = 2250;
                    break;
                }
            case 5:
                {
                    swTimeout = 2250;
                    cycleTime = 2700;
                    break;
                }
            case 6:
                {
                    swTimeout = 2250;
                    cycleTime = 3150;
                    break;
                }
            case 7:
                {
                    swTimeout = 2250;
                    cycleTime = 3600;
                    break;
                }
            }

            if(gt105 && b205only)
            {
                swTimeout = 2250;   // 100% DLC
                cycleTime = 2250;
            }

            break;
        }
    case 5:
        {
            switch(sTime)
            {
            case 0:
                {
                    cycleTime = 900;    // 100% 15 min DI
                    swTimeout = 900;
                    break;
                }
            case 1:
                {
                    cycleTime = 1800;   // 100% 30 min DI
                    swTimeout = 1800;
                    break;
                }
            case 2:
                {
                    cycleTime = 3600;   // 100% 60 min DI
                    swTimeout = 3600;
                    break;
                }
            case 3:
                {
                    cycleTime = 450;    // 100% 7.5 min DI
                    swTimeout = 450;
                    break;
                }
            case 4:
                {
                    cycleTime = 1800;   // 41.7% 30 min DI
                    swTimeout = 750;
                    b205only = true;
                    break;
                }
            case 5:
                {
                    swTimeout = 2700;   // 100% DLC Control
                    cycleTime = 2700;
                    break;
                }
            case 6:
                {
                    swTimeout = 2700;
                    cycleTime = 3150;
                    break;
                }
            case 7:
                {
                    swTimeout = 2700;
                    cycleTime = 3600;
                    break;
                }
            }

            if(gt105 && b205only)
            {
                swTimeout = 2700;   // 100% DLC Control
                cycleTime = 2700;
            }

            break;
        }
    case 6:
        {
            switch(sTime)
            {
            case 0:
                {
                    cycleTime = 1800;   // 25% of 30 min DI
                    swTimeout = 450;
                    break;
                }
            case 1:
                {
                    cycleTime = 1800;   // 33.33% of 30 min DI
                    swTimeout = 600;
                    break;
                }
            case 2:
                {
                    cycleTime = 900;    // 50% of 15 min DI
                    swTimeout = 450;
                    break;
                }
            case 3:
                {
                    cycleTime = 900;    // 66.67% of 15 min DI
                    swTimeout = 600;
                    break;
                }
            case 4:
                {
                    cycleTime = 900;    // 73.30% of 15 min DI
                    swTimeout = 660;
                    break;
                }
            case 5:
                {
                    cycleTime = 900;    // 80% of 15 min DI
                    swTimeout = 720;
                    break;
                }
            case 6:
                {
                    swTimeout = 3150;
                    cycleTime = 3150;
                    break;
                }
            case 7:
                {
                    swTimeout = 3150;
                    cycleTime = 3600;
                    break;
                }
            }
            break;
        }
    case 7:
        {
            switch(sTime)
            {
            case 0:
                {
                    cycleTime = 3600;   // 12.50% of 60 min DI
                    swTimeout = 450;
                    break;
                }
            case 1:
                {
                    cycleTime = 3600;   // 25% of 60 min DI
                    swTimeout = 900;
                    break;
                }
            case 2:
                {
                    cycleTime = 3600;   // 37.5% of 60 min DI
                    swTimeout = 1350;
                    break;
                }
            case 3:
                {
                    cycleTime = 3600;   // 50% of 60 min DI
                    swTimeout = 1800;
                    break;
                }
            case 4:
                {
                    cycleTime = 3600;   // 62.50% of 60 min DI
                    swTimeout = 2250;
                    break;
                }
            case 5:
                {
                    cycleTime = 3600;   // 75% of 60 min DI
                    swTimeout = 2700;
                    break;
                }
            case 6:
                {
                    cycleTime = 3600;   // 87.5% of 60 min DI
                    swTimeout = 3150;
                    break;
                }
            case 7:
                {
                    swTimeout = 3600;   // 100% DLC
                    cycleTime = 3600;
                    break;
                }
            }
            break;
        }
    }

    return std::make_pair( sTime, cTime );
}



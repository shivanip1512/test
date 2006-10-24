/*-----------------------------------------------------------------------------*
*
* File:   dev_mct4xx
*
* Date:   10/5/2005
*
* Author: Jess M. Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_mct4xx-arc  $
* REVISION     :  $Revision: 1.35 $
* DATE         :  $Date: 2006/10/24 18:13:48 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <rw\re.h>
#undef mask_                // Stupid RogueWave re.h

#include <boost/regex.hpp>
#include <limits>

#include "dev_mct4xx.h"
#include "dev_mct470.h"  //  for sspec information
#include "dev_mct410.h"  //  for sspec information

#include "ctistring.h"
#include "ctidate.h"
#include "numstr.h"

using Cti::Protocol::Emetcon;

const char *CtiDeviceMCT4xx::PutConfigPart_all             = "all";
const char *CtiDeviceMCT4xx::PutConfigPart_tou             = "tou";
const char *CtiDeviceMCT4xx::PutConfigPart_dst             = "dst";
const char *CtiDeviceMCT4xx::PutConfigPart_vthreshold      = "vthreshold";
const char *CtiDeviceMCT4xx::PutConfigPart_demand_lp       = "demandlp";
const char *CtiDeviceMCT4xx::PutConfigPart_options         = "options";
const char *CtiDeviceMCT4xx::PutConfigPart_addressing      = "addressing";
const char *CtiDeviceMCT4xx::PutConfigPart_disconnect      = "disconnect";
const char *CtiDeviceMCT4xx::PutConfigPart_holiday         = "holiday";
const char *CtiDeviceMCT4xx::PutConfigPart_usage           = "usage";
const char *CtiDeviceMCT4xx::PutConfigPart_llp             = "llp";
const char *CtiDeviceMCT4xx::PutConfigPart_lpchannel       = "lpchannel";
const char *CtiDeviceMCT4xx::PutConfigPart_relays          = "relays";
const char *CtiDeviceMCT4xx::PutConfigPart_precanned_table = "precannedtable";
const char *CtiDeviceMCT4xx::PutConfigPart_centron         = "centron";
const char *CtiDeviceMCT4xx::PutConfigPart_dnp             = "dnp";

const CtiDeviceMCT4xx::CommandSet      CtiDeviceMCT4xx::_commandStore = CtiDeviceMCT4xx::initCommandStore();
const CtiDeviceMCT4xx::ConfigPartsList CtiDeviceMCT4xx::_config_parts = initConfigParts();

const CtiDeviceMCT4xx::QualityMap      CtiDeviceMCT4xx::_errorQualities = initErrorQualities();

CtiDeviceMCT4xx::CtiDeviceMCT4xx()
{
    _llpInterest.time     = 0;
    _llpInterest.time_end = 0;
    _llpInterest.channel  = 0;
    _llpInterest.offset   = 0;
    _llpInterest.retry    = false;
    _llpInterest.failed   = false;

    _llpPeakInterest.channel = 0;
    _llpPeakInterest.command = 0;
    _llpPeakInterest.period  = 0;
    _llpPeakInterest.time    = 0;

    for( int i = 0; i < LPChannels; i++ )
    {
        //  initialize them to 0
        _lp_info[i].archived_reading = 0;
        _lp_info[i].current_request  = 0;
        _lp_info[i].current_schedule = 0;
    }
}

CtiDeviceMCT4xx::CtiDeviceMCT4xx(const CtiDeviceMCT4xx& aRef)
{
    *this = aRef;
}

CtiDeviceMCT4xx::~CtiDeviceMCT4xx()
{
}

CtiDeviceMCT4xx &CtiDeviceMCT4xx::operator=(const CtiDeviceMCT4xx &aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        LockGuard guard(monitor());            // Protect this device!
    }
    return *this;
}

CtiDeviceMCT4xx::QualityMap CtiDeviceMCT4xx::initErrorQualities( void )
{
    QualityMap m;

    using std::make_pair;

    //  Meter Communications Problem
    m.insert(make_pair(0xFFFFFFFF, make_pair(InvalidQuality, 0)));
    m.insert(make_pair(0xFFFFFFFE, make_pair(InvalidQuality, EC_MeterReading  |
                                                             EC_DemandReading |
                                                             EC_LoadProfile)));

    //  No Data Yet Available
    m.insert(make_pair(0xFFFFFFFD, make_pair(InvalidQuality, EC_TOUFrozenDemand)));
    m.insert(make_pair(0xFFFFFFFC, make_pair(InvalidQuality, EC_MeterReading    |
                                                             EC_DemandReading   |
                                                             EC_TOUDemand       |
                                                             EC_TOUFrozenDemand |
                                                             EC_LoadProfile)));

    //  Data not available for interval
    m.insert(make_pair(0xFFFFFFFB, make_pair(InvalidQuality, 0)));
    m.insert(make_pair(0xFFFFFFFA, make_pair(InvalidQuality, EC_LoadProfile)));

    //  Device Filler
    m.insert(make_pair(0xFFFFFFF9, make_pair(DeviceFillerQuality, 0)));
    m.insert(make_pair(0xFFFFFFF8, make_pair(DeviceFillerQuality, EC_LoadProfile)));

    //  Power failure occurred during interval
    m.insert(make_pair(0xFFFFFFF7, make_pair(PowerfailQuality, 0)));
    m.insert(make_pair(0xFFFFFFF6, make_pair(PowerfailQuality, EC_LoadProfile)));

    //  Power restored in this interval
    m.insert(make_pair(0xFFFFFFF5, make_pair(PartialIntervalQuality, 0)));
    m.insert(make_pair(0xFFFFFFF4, make_pair(PartialIntervalQuality, EC_LoadProfile)));

    //  Overflow
    m.insert(make_pair(0xFFFFFFE1, make_pair(OverflowQuality, EC_TOUFrozenDemand)));
    m.insert(make_pair(0xFFFFFFE0, make_pair(OverflowQuality, EC_DemandReading   |
                                                              EC_TOUDemand       |
                                                              EC_TOUFrozenDemand |
                                                              EC_LoadProfile)));

    return m;
}



CtiDeviceMCT4xx::ConfigPartsList CtiDeviceMCT4xx::initConfigParts()
{
    ConfigPartsList tempList;

    tempList.push_back(PutConfigPart_dst);
    tempList.push_back(PutConfigPart_vthreshold);
    tempList.push_back(PutConfigPart_demand_lp);
    tempList.push_back(PutConfigPart_options);
    tempList.push_back(PutConfigPart_addressing);
    tempList.push_back(PutConfigPart_disconnect);
    tempList.push_back(PutConfigPart_holiday);
    tempList.push_back(PutConfigPart_usage);
    tempList.push_back(PutConfigPart_llp);

    return tempList;
}

CtiDeviceMCT4xx::ConfigPartsList CtiDeviceMCT4xx::getPartsList()
{
    return _config_parts;
}


CtiDeviceMCT4xx::CommandSet CtiDeviceMCT4xx::initCommandStore()
{
    CommandSet cs;

    cs.insert(CommandStore(Emetcon::PutConfig_TSync,        Emetcon::IO_Function_Write, FuncWrite_TSyncPos,         FuncWrite_TSyncLen));

    cs.insert(CommandStore(Emetcon::PutValue_ResetPFCount,  Emetcon::IO_Write,          Command_PowerfailReset,     0));

    //  This is the default TOU reset command - the command that zeroes the rates (Command_TOUResetZero) is assigned
    //    in executePutValue() if needed
    cs.insert(CommandStore(Emetcon::PutValue_TOUReset,      Emetcon::IO_Write,          Command_TOUReset,           0));

    return cs;
}


string CtiDeviceMCT4xx::printable_time(unsigned long seconds)
{
    string retval;

    if( seconds > DawnOfTime )
    {
        retval = CtiTime(seconds).asString();
    }
    else
    {
        retval = "[invalid time (" + CtiNumStr(seconds).hex().zpad(8) + ")]";
    }

    return retval;
}


bool CtiDeviceMCT4xx::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    CommandSet::const_iterator itr = _commandStore.find( CommandStore( cmd ) );

    if( itr != _commandStore.end( ) )
    {
        function = itr->function;   //  Copy the relevant bits from the commandStore
        length   = itr->length;     //
        io       = itr->io;         //

        found = true;
    }
    else    //  Look in the parent if not found in the child
    {
        found = Inherited::getOperation( cmd, function, length, io );
    }

    return found;
}


unsigned char CtiDeviceMCT4xx::crc8( const unsigned char *buf, unsigned int len ) const
{
    const unsigned char llpcrc_poly = 0x07;

    /* Function to calculate the 8 bit crc of the channel of interest
         and the period of interest start time.  This 8 bit crc will
         be sent back in byte 1 of a long load profile read.

       Parameters: uint8_t channel - This is the channel that is going
                                       to be returned.
                   uint8_t *time - A pointer to the period of interest start time

       Return: uint8_t crc
       */

    unsigned short crc = 0;
    unsigned char  current_byte;

    // Walk through the bytes of data
    for( int i = 0; i <= len; i++ )
    {
        if( i < len )
        {
            current_byte = buf[i];
        }
        else
        {
            current_byte = 0;
        }

        // Walk through each bit of the byte
        for(int bit = 0; bit < 8; bit++ )
        {
            // Move the data bits into the crc
            crc <<= 1;
            if( current_byte & 0x80 )
            {
                crc |= 0x0001;
            }

            // Do the crc calculation
            if( crc & 0x0100 )
            {
                crc ^= llpcrc_poly;
            }

            // Move the data byte ahead 1 bit
            current_byte <<= 1;
        }
    }

    return (unsigned char)(crc & 0xff);
}


CtiDeviceMCT4xx::point_info CtiDeviceMCT4xx::getData( unsigned char *buf, int len, ValueType vt ) const
{
    PointQuality_t quality    = NormalQuality;
    __int64 value = 0;
    unsigned long error_code = 0xffffffff;  //  filled with 0xff because some data types are less than 32 bits
    unsigned char quality_flags, resolution;
    point_info  retval;

    for( int i = 0; i < len; i++ )
    {
        //  input data is in MSB order
        error_code <<= 8;
        value      <<= 8;

        //  the first byte for some value types needs to be treated specially
        if( !i && (vt == ValueType_Demand ||
                   vt == ValueType_LoadProfile_Demand) )
        {
            //  save these for use later
            quality_flags = buf[i] & 0xc0;
            resolution    = buf[i] & 0x30;

            value        |= buf[i] & 0x0f;  //  trim off the quality bits and the resolution bits
            error_code   |= buf[i] | 0xc0;  //  fill in the quality bits to get the true error code
        }
        else if( !i && (vt == ValueType_LoadProfile_Voltage) )
        {
            quality_flags = buf[i] & 0xc0;

            value        |= buf[i] & 0x3f;  //  trim off the quality bits
            error_code   |= buf[i] | 0xc0;  //  fill in the quality bits to get the true error code
        }
        else
        {
            value        |= buf[i];
            error_code   |= buf[i];
        }
    }

    retval.freeze_bit = value & 0x01;

    //  i s'pose this could be a set sometime, eh?
    QualityMap::const_iterator q_itr = _errorQualities.find(error_code);

    if( q_itr != _errorQualities.end() )
    {
        quality = q_itr->second.first;
        value   = 0;
    }
    else
    {
        //  only take the demand bits into account if everything else is cool
        switch( quality_flags )
        {
            //  time was adjusted in this interval
            case 0xc0:  quality = PartialIntervalQuality;    break;
            //  power was restored in this interval
            case 0x80:  quality = PartialIntervalQuality;    break;
            //  power failed in this interval
            case 0x40:  quality = PowerfailQuality;          break;
        }

        if( vt == ValueType_Voltage && value > 0x7fff )
        {
            value = 0;
            quality = AbnormalQuality;
        }
    }

    retval.value   = value;
    retval.quality = quality;

    if( vt == ValueType_Demand || vt == ValueType_LoadProfile_Demand )
    {
        if( getMCTDebugLevel(DebugLevel_Info) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - demand value " << (unsigned long)value << " resolution " << (int)resolution << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        //  we need to do this here because value is an unsigned long and retval.first is a double
        switch( resolution )
        {
            default:
            case 0x00:  retval.value /=    10.0;    break;  //  100 WH units -> kWH
            case 0x10:  retval.value /=   100.0;    break;  //   10 WH units -> kWH
            case 0x20:  retval.value /=  1000.0;    break;  //    1 WH units -> kWH
            case 0x30:  retval.value /= 10000.0;    break;  //  0.1 WH units -> kWH
        }

        retval.value *= 10.0;  //  REMOVE THIS for HEAD or WHATEVER build HAS the proper, pretty, nice 1.0 kWH output code
    }

    return retval;
}


CtiDeviceMCT4xx::point_info CtiDeviceMCT4xx::getLoadProfileData(unsigned channel, unsigned char *buf, unsigned len)
{
    point_info pi;

    pi = getData(buf, len, ValueType_LoadProfile_Demand);

    //  adjust for the demand interval
    pi.value *= 3600 / getLoadProfileInterval(channel);

    return pi;
}


//  eventually allow this function to construct the pointdata string as well
CtiPointDataMsg *CtiDeviceMCT4xx::makePointDataMsg(CtiPointSPtr p, const point_info &pi, const string &pointString)
{
    CtiPointDataMsg *pdm = 0;

    if( p )
    {
        pdm = CTIDBG_new CtiPointDataMsg(p->getID(), pi.value, pi.quality, p->getType(), pointString);
    }

    return pdm;
}


INT CtiDeviceMCT4xx::executeGetValue(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList)
{
    INT nRet = NoMethod;

    bool found = false;
    int function;

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                   OutMessage->Request.CommandStr,
                                                   string(),
                                                   nRet,
                                                   OutMessage->Request.RouteID,
                                                   OutMessage->Request.MacroOffset,
                                                   OutMessage->Request.Attempt,
                                                   OutMessage->Request.TrxID,
                                                   OutMessage->Request.UserID,
                                                   OutMessage->Request.SOE,
                                                   CtiMultiMsg_vec( ));

    if( parse.isKeyValid("lp_command") )  //  load profile
    {
        unsigned long request_time, relative_time;

        int request_channel;
        int year, month, day, hour, minute;
        int interval_len, block_len;

        string cmd = parse.getsValue("lp_command");

        if( !cmd.compare("status") )
        {
            CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), OutMessage->Request.CommandStr);

            ReturnMsg->setUserMessageId(OutMessage->Request.UserID);

            string lp_status_string;
            lp_status_string += getName() + " / Load profile request status:\n";

            interval_len = getLoadProfileInterval(_llpInterest.channel);

            if( _llpInterest.time_end > (_llpInterest.time + _llpInterest.offset + (interval_len * 6) + interval_len) )
            {
                lp_status_string += "Current interval: " + printable_time(_llpInterest.time + _llpInterest.offset + interval_len) + "\n";
                lp_status_string += "Ending interval:  " + printable_time(_llpInterest.time_end) + "\n";
            }
            else
            {
                lp_status_string += "No active load profile requests for this device\n";
                if( _llpInterest.failed )
                {
                    lp_status_string += "Last request failed at interval: " + printable_time(_llpInterest.time + _llpInterest.offset + interval_len) + "\n";
                }

                if( _llpInterest.time_end > (DawnOfTime + rwEpoch) )
                {
                    lp_status_string += "Last request end time: " + printable_time(_llpInterest.time_end) + "\n";
                }
            }

            ReturnMsg->setResultString(lp_status_string.c_str());

            retMsgHandler( OutMessage->Request.CommandStr, NoError, ReturnMsg, vgList, retList, true );

            delete OutMessage;
            OutMessage = 0;
            found = false;
            nRet  = NoError;
        }
        else if( !cmd.compare("cancel") )
        {
            //  reset it, that way it'll end immediately
            _llpInterest.time_end = 0;

            CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), OutMessage->Request.CommandStr);

            ReturnMsg->setUserMessageId(OutMessage->Request.UserID);

            ReturnMsg->setResultString(getName() + " / Load profile request cancelled\n");

            retMsgHandler( OutMessage->Request.CommandStr, NoError, ReturnMsg, vgList, retList, true );

            delete OutMessage;
            OutMessage = 0;
            found = false;
            nRet  = NoError;
        }
        else
        {
            request_channel = parse.getiValue("lp_channel");

            if( request_channel >  0 &&
                request_channel <= LPChannels )
            {
                request_channel--;

                interval_len = getLoadProfileInterval(request_channel);
                block_len    = interval_len * 6;

                //  grab the beginning date
                CtiTokenizer date_tok(parse.getsValue("lp_date_start"));
                month = atoi(date_tok("-/").data());
                day   = atoi(date_tok("-/").data());
                year  = atoi(date_tok("-/").data());
                //  note that this code assumes that the current century is 20xx - this will need to change in 2100
                if( year < 100 )    year += 2000;

                if( !cmd.compare("lp") )
                {
                    CtiTime time_start, time_end;

                    function = Emetcon::GetValue_LoadProfile;
                    found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

                    //  grab the beginning time, if available
                    if( parse.isKeyValid("lp_time_start") )
                    {
                        CtiTokenizer time_start_tok(parse.getsValue("lp_time_start"));
                        hour   = atoi(time_start_tok(":").data());
                        minute = atoi(time_start_tok(":").data());
                    }
                    else
                    {
                        //  otherwise, default to midnight
                        hour   = 0;
                        minute = 0;
                    }

                    time_start = CtiTime(CtiDate(day, month, year), hour, minute);

                    //  grab the end date, if available
                    if( parse.isKeyValid("lp_date_end") )
                    {
                        CtiTokenizer date_end_tok(parse.getsValue("lp_date_end"));

                        month = atoi(date_end_tok("-/").data());
                        day   = atoi(date_end_tok("-/").data());
                        year  = atoi(date_end_tok("-/").data());
                        //  note that this code assumes that the current century is 20xx - this will need to change in 2100
                        if( year < 100 )    year += 2000;

                        //  grab the end time, if available
                        if( parse.isKeyValid("lp_time_end") )
                        {
                            CtiTokenizer time_end_tok(parse.getsValue("lp_time_end"));

                            hour   = atoi(time_end_tok(":").data());
                            minute = atoi(time_end_tok(":").data());

                            time_end  = CtiTime(CtiDate(day, month, year), hour, minute);
                        }
                        else
                        {
                            //  otherwise, default to the end of the day
                            time_end  = CtiTime(CtiDate(day, month, year));
                            time_end += 86400;  //  end of the day/beginning of the next day
                        }
                    }
                    else
                    {
                        //  otherwise default to the end of the block
                        time_end  = time_start;

                        if( parse.isKeyValid("lp_time_start") )
                        {
                            //  did they want a specific time?
                            time_end += block_len;
                        }
                        else
                        {
                            //  no time specified, they must've wanted a whole day
                            time_end += 86400;
                        }
                    }

                    if( !time_start.isValid() || !time_end.isValid() || (time_start >= time_end) )
                    {
                        CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), OutMessage->Request.CommandStr);

                        ReturnMsg->setUserMessageId(OutMessage->Request.UserID);

                        found = false;
                        nRet  = BADPARAM;

                        CtiString time_error_string = getName() + " / Invalid date/time for LP request (" + parse.getsValue("lp_date_start");

                        if( parse.isKeyValid("lp_time_start") )
                        {
                            time_error_string += " @ " + parse.getsValue("lp_time_start");
                        }

                        if( parse.isKeyValid("lp_date_end") )
                        {
                            time_error_string += " - ";

                            time_error_string += parse.getsValue("lp_date_end");

                            if( parse.isKeyValid("lp_time_end") )
                            {
                                time_error_string += " @ " + parse.getsValue("lp_time_end");
                            }
                        }

                        time_error_string += ")";

                        ReturnMsg->setResultString(time_error_string);

                        retMsgHandler( OutMessage->Request.CommandStr, NoMethod, ReturnMsg, vgList, retList, true );
                    }
                    else
                    {
                        //  FIXME:  we must replicate this functionality in the decode portion - right now, _llpInterest.offset
                        //            is being overwritten, resulting in faulty decodes
                        request_time  = time_start.seconds();
                        request_time -= request_time % interval_len;
                        request_time -= interval_len;  //  we report interval-ending, yet request interval-beginning...  so back that thing up

                        _llpInterest.time_end = time_end.seconds();

                        //  this is the number of seconds from the current pointer
                        relative_time = request_time - _llpInterest.time;

                        if( (request_channel == _llpInterest.channel) &&  //  correct channel
                            (relative_time < (16 * block_len))        &&  //  within 16 blocks
                            !(relative_time % block_len) )                //  aligned
                        {
                            //  it's aligned (and close enough) to the block we're pointing at
                            function  = 0x40;
                            function += relative_time / block_len;

                            _llpInterest.offset = relative_time;
                        }
                        else
                        {
                            //  just read the first block - it'll be the one we're pointing at
                            function  = 0x40;

                            //  we need to set it to the requested interval
                            CtiOutMessage *interest_om = new CtiOutMessage(*OutMessage);

                            if( interest_om )
                            {
                                _llpInterest.time    = request_time;
                                _llpInterest.offset  = 0;
                                _llpInterest.channel = request_channel;

                                interest_om->Sequence = Emetcon::PutConfig_LoadProfileInterest;

                                interest_om->Buffer.BSt.Function = FuncWrite_LLPInterestPos;
                                interest_om->Buffer.BSt.IO       = Emetcon::IO_Function_Write;
                                interest_om->Buffer.BSt.Length   = FuncWrite_LLPInterestLen;
                                interest_om->MessageFlags |= MessageFlag_ExpectMore;

                                unsigned long utc_time = request_time - rwEpoch;

                                interest_om->Buffer.BSt.Message[0] = gMCT400SeriesSPID;

                                interest_om->Buffer.BSt.Message[1] = request_channel + 1  & 0x000000ff;

                                interest_om->Buffer.BSt.Message[2] = (utc_time >> 24) & 0x000000ff;
                                interest_om->Buffer.BSt.Message[3] = (utc_time >> 16) & 0x000000ff;
                                interest_om->Buffer.BSt.Message[4] = (utc_time >>  8) & 0x000000ff;
                                interest_om->Buffer.BSt.Message[5] = (utc_time)       & 0x000000ff;

                                outList.push_back(interest_om);
                                interest_om = 0;
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Checkpoint - unable to create outmessage, cannot set interval **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                            }
                        }

                        OutMessage->Buffer.BSt.Function = function;
                        OutMessage->Buffer.BSt.IO       = Emetcon::IO_Function_Read;
                        OutMessage->Buffer.BSt.Length   = 13;

                        function = Emetcon::GetValue_LoadProfile;

                        if( strstr(OutMessage->Request.CommandStr, " background") )
                        {
                            CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), OutMessage->Request.CommandStr);

                            ReturnMsg->setUserMessageId(OutMessage->Request.UserID);
                            ReturnMsg->setConnectionHandle(OutMessage->Request.Connection);
                            ReturnMsg->setResultString(getName() + " / Load profile request submitted for background processing - use \"getvalue lp status\" to check progress");

                            retMsgHandler( OutMessage->Request.CommandStr, NoError, ReturnMsg, vgList, retList, true );

                            OutMessage->Priority = 8;
                            //  make sure the OM doesn't report back to Commander
                            OutMessage->Request.Connection = 0;
                        }

                        nRet = NoError;
                    }
                }
                else if( !cmd.compare("peak") )
                {
                    if( getDynamicInfo(Keys::Key_MCT_SSpec) == CtiDeviceMCT410::Sspec
                        && getDynamicInfo(Keys::Key_MCT_SSpecRevision) < CtiDeviceMCT410::SspecRev_NewLLP_Min )
                    {
                        CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), OutMessage->Request.CommandStr);

                        if( ReturnMsg )
                        {
                            ReturnMsg->setUserMessageId(OutMessage->Request.UserID);
                            ReturnMsg->setResultString(getName() + " / Load profile reporting for MCT 410 only supported for SSPECs " + CtiNumStr(CtiDeviceMCT410::Sspec) + " revision " + string((char)('A' + CtiDeviceMCT410::SspecRev_NewLLP_Min - 1), 1) + " and up");

                            retMsgHandler( OutMessage->Request.CommandStr, NoMethod, ReturnMsg, vgList, retList, true );
                        }
                    }
                    else
                    {
                        function = Emetcon::GetValue_LoadProfilePeakReport;
                        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
                    }

                    if( found )
                    {
                        int lp_peak_command = -1;
                        string lp_peaktype = parse.getsValue("lp_peaktype");
                        int request_range  = parse.getiValue("lp_range");  //  add safeguards to check that we're not >30 days... ?

                        if( !lp_peaktype.compare("day") )
                        {
                            lp_peak_command = FuncRead_LLPPeakDayPos;
                        }
                        else if( !lp_peaktype.compare("hour") )
                        {
                            lp_peak_command = FuncRead_LLPPeakHourPos;
                        }
                        else if( !lp_peaktype.compare("interval") )
                        {
                            lp_peak_command = FuncRead_LLPPeakIntervalPos;
                        }

                        if( lp_peak_command > 0 )
                        {
                            //  add on a day - this is the end of the interval, not the beginning,
                            //    so we need to start at midnight of the following day
                            request_time  = CtiTime(CtiDate(day, month, year)).seconds() + 86400;

                            if( request_time    != _llpPeakInterest.time    ||
                                request_channel != _llpPeakInterest.channel ||
                                request_range   != _llpPeakInterest.period )
                            {
                                //  we need to set it to the requested interval
                                CtiOutMessage *interest_om = new CtiOutMessage(*OutMessage);

                                if( interest_om )
                                {
                                    _llpPeakInterest.time    = request_time;
                                    _llpPeakInterest.channel = request_channel;
                                    _llpPeakInterest.period  = request_range;

                                    interest_om->Priority = OutMessage->Priority + 1;  //  just make sure this goes out first
                                    interest_om->Sequence = Emetcon::PutConfig_LoadProfileReportPeriod;
                                    interest_om->Request.Connection = 0;

                                    interest_om->Buffer.BSt.Function = FuncWrite_LLPPeakInterestPos;
                                    interest_om->Buffer.BSt.IO       = Emetcon::IO_Function_Write;
                                    interest_om->Buffer.BSt.Length   = FuncWrite_LLPPeakInterestLen;
                                    interest_om->MessageFlags |= MessageFlag_ExpectMore;

                                    unsigned long utc_time = request_time - rwEpoch;

                                    interest_om->Buffer.BSt.Message[0] = gMCT400SeriesSPID;

                                    interest_om->Buffer.BSt.Message[1] = request_channel  & 0x000000ff;

                                    interest_om->Buffer.BSt.Message[2] = (utc_time >> 24) & 0x000000ff;
                                    interest_om->Buffer.BSt.Message[3] = (utc_time >> 16) & 0x000000ff;
                                    interest_om->Buffer.BSt.Message[4] = (utc_time >>  8) & 0x000000ff;
                                    interest_om->Buffer.BSt.Message[5] = (utc_time)       & 0x000000ff;

                                    interest_om->Buffer.BSt.Message[6] = request_range    & 0x000000ff;

                                    //  add a bit of a delay so the 410 can calculate...
                                    //    this delay may need to be increased by other means, depending
                                    //    on how long the larger peak report calculations take
                                    interest_om->MessageFlags |= MessageFlag_AddSilence;

                                    outList.push_back(interest_om);
                                    interest_om = 0;
                                }
                                else
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " **** Checkpoint - unable to create outmessage, cannot set interval **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }
                                }
                            }

                            _llpPeakInterest.command = lp_peak_command;

                            OutMessage->Buffer.BSt.Function = lp_peak_command;
                            OutMessage->Buffer.BSt.IO       = Emetcon::IO_Function_Read;
                            OutMessage->Buffer.BSt.Length   = 13;

                            nRet = NoError;
                        }
                    }
                }
            }
            else
            {
                if( errRet )
                {
                    CtiString temp = "Bad channel specification - Acceptable values:  1-4";
                    errRet->setResultString( temp );
                    errRet->setStatus(NoMethod);
                    retList.push_back(errRet);
                    errRet = NULL;
                }
            }
        }
    }
    else
    {
        nRet = Inherited::executeGetValue(pReq, parse, OutMessage, vgList, retList, outList);
    }

    if( found )
    {
        // Load all the other stuff that is needed
        //  FIXME:  most of this is taken care of in propagateRequest - we could probably trim a lot of this out
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().data(), COMMAND_STR_SIZE);

        nRet = NoError;
    }

    return nRet;
}

INT CtiDeviceMCT4xx::executeGetConfig( CtiRequestMsg              *pReq,
                                       CtiCommandParser           &parse,
                                       OUTMESS                   *&OutMessage,
                                       list< CtiMessage* >  &vgList,
                                       list< CtiMessage* >  &retList,
                                       list< OUTMESS* >     &outList )
{
    INT nRet = NoMethod;


    bool found = false;

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                   string(OutMessage->Request.CommandStr),
                                                   string(),
                                                   nRet,
                                                   OutMessage->Request.RouteID,
                                                   OutMessage->Request.MacroOffset,
                                                   OutMessage->Request.Attempt,
                                                   OutMessage->Request.TrxID,
                                                   OutMessage->Request.UserID,
                                                   OutMessage->Request.SOE,
                                                   CtiMultiMsg_vec( ));


    if( parse.isKeyValid("tou") )
    {
        found = true;

        if( parse.isKeyValid("tou_schedule") )
        {
            int schedulenum = parse.getiValue("tou_schedule");

            if( schedulenum == 1 || schedulenum == 2 )
            {
                OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Pos;
                OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Len;
                OutMessage->Buffer.BSt.IO       = Emetcon::IO_Function_Read;
            }
            else if( schedulenum == 3 || schedulenum == 4 )
            {
                OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Pos;
                OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Len;
                OutMessage->Buffer.BSt.IO       = Emetcon::IO_Function_Read;
            }
            else
            {
                errRet->setResultString("invalid schedule number " + CtiNumStr(schedulenum));
                retList.push_back(errRet);
                errRet = 0;

                found = false;
            }
        }
        else
        {
            OutMessage->Buffer.BSt.Function = FuncRead_TOUStatusPos;
            OutMessage->Buffer.BSt.Length   = FuncRead_TOUStatusLen;
            OutMessage->Buffer.BSt.IO       = Emetcon::IO_Function_Read;
        }

        OutMessage->Sequence = Emetcon::GetConfig_TOU;
    }
    else
    {
        nRet = Inherited::executeGetConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }


    if( found )
    {
        // Load all the other stuff that is needed
        //  FIXME:  most of this is taken care of in propagateRequest - we could probably trim a lot of this out
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = NoError;
    }

    if( errRet )
    {
        delete errRet;
        errRet = 0;
    }

    return nRet;
}


INT CtiDeviceMCT4xx::executePutConfig(CtiRequestMsg         *pReq,
                                      CtiCommandParser      &parse,
                                      OUTMESS              *&OutMessage,
                                      list< CtiMessage * >  &vgList,
                                      list< CtiMessage * >  &retList,
                                      list< OUTMESS * >     &outList)
{
    bool  found = false;
    INT   nRet = NoError, sRet;

    if( parse.isKeyValid("install") )
    {
        if( parse.getsValue("installvalue") == PutConfigPart_all )
        {
            ConfigPartsList tempList = getPartsList();
            if(!tempList.empty())
            {
                CtiRequestMsg *tempReq = CTIDBG_new CtiRequestMsg(*pReq);

                // Load all the other stuff that is needed
                OutMessage->DeviceID  = getID();
                OutMessage->TargetID  = getID();
                OutMessage->Port      = getPortID();
                OutMessage->Remote    = getAddress();
                OutMessage->Priority  = MAXPRIORITY-4;//standard seen in rest of devices.
                OutMessage->TimeOut   = 2;
                OutMessage->Retry     = 2;
                OutMessage->Sequence = Cti::Protocol::Emetcon::PutConfig_Install;  //  this will be handled by the putconfig decode - basically, a no-op
                OutMessage->Request.RouteID   = getRouteID();

                for(CtiDeviceMCT4xx::ConfigPartsList::const_iterator tempItr = tempList.begin();tempItr != tempList.end();tempItr++)
                {
                    if( tempReq != NULL && *tempItr != PutConfigPart_all)//_all == infinite loop == unhappy program == very unhappy jess
                    {
                        string tempString = pReq->CommandString();
                        string replaceString = " ";
                        replaceString += *tempItr; //FIX_ME Consider not keeping the old string but just creating a new, internal string.
                        replaceString += " ";

                        CtiToLower(tempString);

                        CtiString ts_tempString = tempString;
                        boost::regex re (" all($| )");
                        ts_tempString.replace( re,replaceString );
                        tempString = ts_tempString;

                        tempReq->setCommandString(tempString);

                        tempReq->setConnectionHandle(pReq->getConnectionHandle());

                        CtiCommandParser parseSingle(tempReq->CommandString());

                        sRet = executePutConfigSingle(tempReq, parseSingle, OutMessage, vgList, retList, outList);
                    }
                }

                if(tempReq!=NULL)
                {
                    delete tempReq;
                    tempReq = NULL;
                }

            }

        }
        else
        {
            strncpy(OutMessage->Request.CommandStr, (pReq->CommandString()).c_str(), COMMAND_STR_SIZE);
            sRet = executePutConfigSingle(pReq, parse, OutMessage, vgList, retList, outList);
        }
        recordMultiMessageRead(outList);
        incrementGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle(), outList.size());

        if( !outList.empty() && !retList.empty() )
        {
            //hackish way to fix problem of retlist automatically telling commander to not expect more anymore.
            //pil will not set expectmore on the last entry, so I do it by hand...
            //This may be useless at the moment, but is on my way to controlling expect more properly.
            ((CtiReturnMsg*)retList.back())->setExpectMore(1);
        }

        if(OutMessage!=NULL)
        {
            delete OutMessage;
            OutMessage = NULL;
        }
    }
    else if( parse.isKeyValid("timezone_offset") ||
             parse.isKeyValid("timezone_name") )
    {
        unsigned short function, length, io;

        if( getOperation(Emetcon::PutConfig_TimeZoneOffset, function, length, io) )
        {
            int timezone_blocks = 0;

            if( parse.isKeyValid("timezone_offset") )
            {
                double timezone_offset = parse.getdValue("timezone_offset", -999.0);

                timezone_blocks = (int)(timezone_offset * 4.0);
            }
            if( parse.isKeyValid("timezone_name") )
            {
                string timezone_name = parse.getsValue("timezone_name");

                if( !timezone_name.empty() )
                {
                    switch( timezone_name.at(0) )
                    {
                        case 'h':   timezone_blocks = -10 * 4;   break;  //  hawaiian time
                        case 'a':   timezone_blocks =  -9 * 4;   break;  //  alaskan time
                        case 'p':   timezone_blocks =  -8 * 4;   break;  //  pacific time
                        case 'm':   timezone_blocks =  -7 * 4;   break;  //  mountain time
                        case 'c':   timezone_blocks =  -6 * 4;   break;  //  central time
                        case 'e':   timezone_blocks =  -5 * 4;   break;  //  eastern time
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }

            OutMessage->Sequence = Emetcon::PutConfig_TimeZoneOffset;
            OutMessage->Buffer.BSt.Function   = function;
            OutMessage->Buffer.BSt.Length     = length;
            OutMessage->Buffer.BSt.IO         = io;
            OutMessage->Buffer.BSt.Message[0] = timezone_blocks;
        }
        else
        {
            nRet = NoMethod;
        }
    }
    else
    {
        nRet = Inherited::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;

}

INT CtiDeviceMCT4xx::executePutValue(CtiRequestMsg         *pReq,
                                     CtiCommandParser      &parse,
                                     OUTMESS              *&OutMessage,
                                     list< CtiMessage * >  &vgList,
                                     list< CtiMessage * >  &retList,
                                     list< OUTMESS * >     &outList)
{
    bool  found = false;
    INT   nRet = NoError, sRet;

    if( parse.isKeyValid("reset") && parse.isKeyValid("tou") )
    {
        unsigned short function, length, io;

        found = getOperation(Emetcon::PutValue_TOUReset, function, length, io);

        if( parse.isKeyValid("tou_zero") )
        {
            function = Command_TOUResetZero;
        }

        OutMessage->Sequence = Emetcon::PutValue_TOUReset;
        OutMessage->Buffer.BSt.Function   = function;
        OutMessage->Buffer.BSt.Length     = length;
        OutMessage->Buffer.BSt.IO         = io;

        if( !found )
        {
            nRet = NoMethod;
        }
    }
    else
    {
        nRet = Inherited::executePutValue(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;

}

int CtiDeviceMCT4xx::executePutConfigSingle(CtiRequestMsg         *pReq,
                                   CtiCommandParser               &parse,
                                   OUTMESS                        *&OutMessage,
                                   list< CtiMessage* >      &vgList,
                                   list< CtiMessage* >      &retList,
                                   list< OUTMESS* >         &outList)
{
    // Load all the other stuff that is needed
    OutMessage->DeviceID  = getID();
    OutMessage->TargetID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    OutMessage->Priority  = MAXPRIORITY-4;//standard seen in rest of devices.
    OutMessage->TimeOut   = 2;
    OutMessage->Retry     = 2;
    OutMessage->Sequence = Cti::Protocol::Emetcon::PutConfig_Install;  //  this will be handled by the putconfig decode - basically, a no-op
    OutMessage->Request.RouteID   = getRouteID();

    string installValue = parse.getsValue("installvalue");

    int nRet = NORMAL;
    if(      installValue == PutConfigPart_tou )              nRet = executePutConfigTOU               (pReq,parse,OutMessage,vgList,retList,outList);
    else if( installValue == PutConfigPart_dst )              nRet = executePutConfigDst               (pReq,parse,OutMessage,vgList,retList,outList);
    else if( installValue == PutConfigPart_vthreshold )       nRet = executePutConfigVThreshold        (pReq,parse,OutMessage,vgList,retList,outList);
    else if( installValue == PutConfigPart_demand_lp )        nRet = executePutConfigDemandLP          (pReq,parse,OutMessage,vgList,retList,outList);
    else if( installValue == PutConfigPart_options )          nRet = executePutConfigOptions           (pReq,parse,OutMessage,vgList,retList,outList);
    else if( installValue == PutConfigPart_addressing )       nRet = executePutConfigAddressing        (pReq,parse,OutMessage,vgList,retList,outList);
    else if( installValue == PutConfigPart_disconnect )       nRet = executePutConfigDisconnect        (pReq,parse,OutMessage,vgList,retList,outList);
    else if( installValue == PutConfigPart_holiday )          nRet = executePutConfigHoliday           (pReq,parse,OutMessage,vgList,retList,outList);
    else if( installValue == PutConfigPart_usage )            nRet = executePutConfigUsage             (pReq,parse,OutMessage,vgList,retList,outList);
    else if( installValue == PutConfigPart_llp )              nRet = executePutConfigLongLoadProfile   (pReq,parse,OutMessage,vgList,retList,outList);
    else if( installValue == PutConfigPart_lpchannel )        nRet = executePutConfigLoadProfileChannel(pReq,parse,OutMessage,vgList,retList,outList);
    else if( installValue == PutConfigPart_relays )           nRet = executePutConfigRelays            (pReq,parse,OutMessage,vgList,retList,outList);
    else if( installValue == PutConfigPart_precanned_table )  nRet = executePutConfigPrecannedTable    (pReq,parse,OutMessage,vgList,retList,outList);
    else if( installValue == PutConfigPart_centron )          nRet = executePutConfigCentron           (pReq,parse,OutMessage,vgList,retList,outList);
    else if( installValue == PutConfigPart_dnp )              nRet = executePutConfigDNP               (pReq,parse,OutMessage,vgList,retList,outList);
    else
    {   //Not sure if this is correct, this could just return NoMethod. This is here
        //just in case anyone wants to use a putconfig install  for anything but configs.
        //nRet = Inherited::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
        nRet = NoMethod;
    }

    if( nRet != NORMAL )
    {
        CtiString resultString;

        if( nRet == NoConfigData )
        {
            resultString = "ERROR: Invalid config data. Config name:" + installValue;
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Device " << getName() << " had no configuration for config: " << installValue << endl;
        }
        else if( nRet == ConfigCurrent )
        {
            resultString = "Config " + installValue + " is current.";
            nRet = NORMAL; //This is an OK return! Note that nRet is no longer propogated!
        }
        else if( nRet == ConfigNotCurrent )
        {
            resultString = "Config " + installValue + " is NOT current.";
        }
        else
        {
            resultString = "ERROR: NoMethod or invalid config. Config name:" + installValue;
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Device " << getName() << " had a configuration error using config " << installValue << endl;
        }

        retList.push_back( CTIDBG_new CtiReturnMsg(getID( ),
                                                string(OutMessage->Request.CommandStr),
                                                resultString,
                                                nRet,
                                                OutMessage->Request.RouteID,
                                                OutMessage->Request.MacroOffset,
                                                OutMessage->Request.Attempt,
                                                OutMessage->Request.TrxID,
                                                OutMessage->Request.UserID,
                                                OutMessage->Request.SOE,
                                                CtiMultiMsg_vec( )) );
    }

    return nRet;
}


INT CtiDeviceMCT4xx::decodePutConfig(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT   status = NORMAL,
          j;
    ULONG pfCount = 0;
    string resultString;

    CtiReturnMsg  *ReturnMsg = NULL;

    bool expectMore = false;

    INT ErrReturn = InMessage->EventCode & 0x3fff;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        switch( InMessage->Sequence )
        {
            case Emetcon::PutConfig_Install:
            {
                if(InMessage->Buffer.DSt.Length>0)
                {
                    resultString = "Config data received: ";
                    for(int i = 0; i<InMessage->Buffer.DSt.Length;i++)
                    {
                        resultString.append( (CtiNumStr(InMessage->Buffer.DSt.Message[i]).hex().zpad(2)).toString(),0,2);
                    }
                }
                ReturnMsg->setUserMessageId(InMessage->Return.UserID);
                ReturnMsg->setResultString( resultString );

                if( InMessage->MessageFlags & MessageFlag_ExpectMore || getGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection)!=0 )
                {
                    ReturnMsg->setExpectMore(true);
                }

                retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

                //note that at the moment only putconfig install will ever have a group message count.
                decrementGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection);

                break;
            }

            default:
            {
                status = Inherited::decodePutConfig(InMessage,TimeNow,vgList,retList,outList);
                break;
            }
        }

    }
    return status;
}


using namespace Cti;
using namespace Config;
using namespace MCT;


int CtiDeviceMCT4xx::executePutConfigVThreshold(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList)
{
    int nRet = NORMAL;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if(deviceConfig)
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTVThreshold);

        if(tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTVThreshold)
        {
            MCTVThresholdSPtr config = boost::static_pointer_cast< ConfigurationPart<MCTVThreshold> >(tempBasePtr);

            long underVThreshold, overVThreshold;
            USHORT function, length, io;

            underVThreshold = config->getLongValueFromKey(UnderVoltageThreshold);
            overVThreshold = config->getLongValueFromKey(OverVoltageThreshold);

            if(!getOperation(Emetcon::PutConfig_VThreshold, function, length, io))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Operation PutConfig_VTreshold not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            if(underVThreshold == std::numeric_limits<long>::min())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored for UnderVoltageThreshold **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if(parse.isKeyValid("force") || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_UnderVoltageThreshold) != underVThreshold
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_OverVoltageThreshold) != overVThreshold)
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        OutMessage->Buffer.BSt.Function   = function;
                        OutMessage->Buffer.BSt.Length     = length;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Write;
                        OutMessage->Buffer.BSt.Message[0] = (overVThreshold>>8);
                        OutMessage->Buffer.BSt.Message[1] = (overVThreshold);
                        OutMessage->Buffer.BSt.Message[2] = (underVThreshold>>8);
                        OutMessage->Buffer.BSt.Message[3] = (underVThreshold);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                        OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                        OutMessage->Priority             += 1;//return to normal
                    }
                    else
                    {
                        nRet = ConfigNotCurrent;
                    }
                }
                else
                {
                    nRet = ConfigCurrent;
                }
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;

    return nRet;
}

int CtiDeviceMCT4xx::executePutConfigDemandLP          (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList)    {   return NoMethod;    }
int CtiDeviceMCT4xx::executePutConfigLoadProfileChannel(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList)    {   return NoMethod;    }
int CtiDeviceMCT4xx::executePutConfigRelays            (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList)    {   return NoMethod;    }
int CtiDeviceMCT4xx::executePutConfigPrecannedTable    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList)    {   return NoMethod;    }
int CtiDeviceMCT4xx::executePutConfigOptions           (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList)    {   return NoMethod;    }
int CtiDeviceMCT4xx::executePutConfigCentron           (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList)    {   return NoMethod;    }
int CtiDeviceMCT4xx::executePutConfigDisconnect        (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList)    {   return NoMethod;    }

int CtiDeviceMCT4xx::executePutConfigTOU(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;

    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    long value, tempTime;
    if(deviceConfig)
    {
        BaseSPtr tempTOUSPtr = deviceConfig->getConfigFromType(ConfigTypeMCTTOU);
        BaseSPtr tempRateSPtr = deviceConfig->getConfigFromType(ConfigTypeMCTTOURateSchedule);

        if(tempTOUSPtr && tempTOUSPtr->getType() == ConfigTypeMCTTOU && tempRateSPtr && tempRateSPtr->getType() == ConfigTypeMCTTOURateSchedule)
        {
            long mondaySchedule, tuesdaySchedule, wednesdaySchedule, holidaySchedule,
                 thursdaySchedule, fridaySchedule, saturdaySchedule, sundaySchedule, dayTable,
                 defaultTOURate;
            long times[4][5];
            long rates[4][6];
            string
                rateStringValues[4][6], timeStringValues[4][5],
                defaultTOURateString, daySchedule1, daySchedule2, daySchedule3, daySchedule4,
                dynDaySchedule1, dynDaySchedule2, dynDaySchedule3, dynDaySchedule4;

            MCT_TOU_SPtr touConfig = boost::static_pointer_cast< ConfigurationPart<MCT_TOU> >(tempTOUSPtr);
            MCT_TOU_Rate_ScheduleSPtr rateConfig = boost::static_pointer_cast< ConfigurationPart<MCT_TOU_Rate_Schedule> >(tempRateSPtr);

            // Unfortunatelly the arrays have a 0 offset, while the schedules times/rates are referenced with a 1 offset
            // Also note that rate "0" is the midnight rate.
            mondaySchedule = touConfig->getLongValueFromKey(MondaySchedule);
            tuesdaySchedule = touConfig->getLongValueFromKey(TuesdaySchedule);
            wednesdaySchedule = touConfig->getLongValueFromKey(WednesdaySchedule);
            thursdaySchedule = touConfig->getLongValueFromKey(ThursdaySchedule);
            fridaySchedule = touConfig->getLongValueFromKey(FridaySchedule);
            saturdaySchedule = touConfig->getLongValueFromKey(SaturdaySchedule);
            sundaySchedule = touConfig->getLongValueFromKey(SundaySchedule);
            holidaySchedule = touConfig->getLongValueFromKey(HolidaySchedule);

            //These are all string values
            timeStringValues[0][0] = rateConfig->getValueFromKey(Schedule1Time1);
            timeStringValues[0][1] = rateConfig->getValueFromKey(Schedule1Time2);
            timeStringValues[0][2] = rateConfig->getValueFromKey(Schedule1Time3);
            timeStringValues[0][3] = rateConfig->getValueFromKey(Schedule1Time4);
            timeStringValues[0][4] = rateConfig->getValueFromKey(Schedule1Time5);
            timeStringValues[1][0] = rateConfig->getValueFromKey(Schedule2Time1);
            timeStringValues[1][1] = rateConfig->getValueFromKey(Schedule2Time2);
            timeStringValues[1][2] = rateConfig->getValueFromKey(Schedule2Time3);
            timeStringValues[1][3] = rateConfig->getValueFromKey(Schedule2Time4);
            timeStringValues[1][4] = rateConfig->getValueFromKey(Schedule2Time5);
            timeStringValues[2][0] = rateConfig->getValueFromKey(Schedule3Time1);
            timeStringValues[2][1] = rateConfig->getValueFromKey(Schedule3Time2);
            timeStringValues[2][2] = rateConfig->getValueFromKey(Schedule3Time3);
            timeStringValues[2][3] = rateConfig->getValueFromKey(Schedule3Time4);
            timeStringValues[2][4] = rateConfig->getValueFromKey(Schedule3Time5);
            timeStringValues[3][0] = rateConfig->getValueFromKey(Schedule4Time1);
            timeStringValues[3][1] = rateConfig->getValueFromKey(Schedule4Time2);
            timeStringValues[3][2] = rateConfig->getValueFromKey(Schedule4Time3);
            timeStringValues[3][3] = rateConfig->getValueFromKey(Schedule4Time4);
            timeStringValues[3][4] = rateConfig->getValueFromKey(Schedule4Time5);

            rateStringValues[0][0] = rateConfig->getValueFromKey(Schedule1Rate1);
            rateStringValues[0][1] = rateConfig->getValueFromKey(Schedule1Rate2);
            rateStringValues[0][2] = rateConfig->getValueFromKey(Schedule1Rate3);
            rateStringValues[0][3] = rateConfig->getValueFromKey(Schedule1Rate4);
            rateStringValues[0][4] = rateConfig->getValueFromKey(Schedule1Rate5);
            rateStringValues[0][5] = rateConfig->getValueFromKey(Schedule1Rate0);
            rateStringValues[1][0] = rateConfig->getValueFromKey(Schedule2Rate1);
            rateStringValues[1][1] = rateConfig->getValueFromKey(Schedule2Rate2);
            rateStringValues[1][2] = rateConfig->getValueFromKey(Schedule2Rate3);
            rateStringValues[1][3] = rateConfig->getValueFromKey(Schedule2Rate4);
            rateStringValues[1][4] = rateConfig->getValueFromKey(Schedule2Rate5);
            rateStringValues[1][5] = rateConfig->getValueFromKey(Schedule2Rate0);
            rateStringValues[2][0] = rateConfig->getValueFromKey(Schedule3Rate1);
            rateStringValues[2][1] = rateConfig->getValueFromKey(Schedule3Rate2);
            rateStringValues[2][2] = rateConfig->getValueFromKey(Schedule3Rate3);
            rateStringValues[2][3] = rateConfig->getValueFromKey(Schedule3Rate4);
            rateStringValues[2][4] = rateConfig->getValueFromKey(Schedule3Rate5);
            rateStringValues[2][5] = rateConfig->getValueFromKey(Schedule3Rate0);
            rateStringValues[3][0] = rateConfig->getValueFromKey(Schedule4Rate1);
            rateStringValues[3][1] = rateConfig->getValueFromKey(Schedule4Rate2);
            rateStringValues[3][2] = rateConfig->getValueFromKey(Schedule4Rate3);
            rateStringValues[3][3] = rateConfig->getValueFromKey(Schedule4Rate4);
            rateStringValues[3][4] = rateConfig->getValueFromKey(Schedule4Rate5);
            rateStringValues[3][5] = rateConfig->getValueFromKey(Schedule4Rate0);
            defaultTOURateString = touConfig->getValueFromKey(DefaultTOURate);

            for( int i = 0; i < 4; i++ )
            {
                for( int j = 0; j < 6; j++ )
                {
                    if( rateStringValues[i][j].empty() )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - bad rate string stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        nRet = NoConfigData;
                    }
                }
            }

            for( i = 0; i < 4; i++ )
            {
                for( int j = 0; j < 5; j++ )
                {
                    if( timeStringValues[i][j].length() < 4 ) //A time needs at least 4 digits X:XX
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - bad time string stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        nRet = NoConfigData;
                    }
                }
            }

            if( nRet != NoConfigData )
            {
                //Do conversions from strings to longs here.
                for( i = 0; i < 4; i++ )
                {
                    for( int j = 0; j < 6; j++ )
                    {
                        rates[i][j] = rateStringValues[i][j][0] - 'A';
                        if( rates[i][j] < 0 || rates[i][j] > 3 )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - bad rate string stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            nRet = NoConfigData;
                        }
                    }
                }
                for( i = 0; i < 4; i++ )
                {
                    for( int j = 0; j < 5; j++ )
                    {
                        // Im going to remove the :, get the remaining value, and do simple math on it. I think this
                        // results in less error checking needed.
                        timeStringValues[i][j].erase(timeStringValues[i][j].find(':'), 1);
                        tempTime = strtol(timeStringValues[i][j].data(),NULL,10);
                        times[i][j] = ((tempTime/100) * 60) + (tempTime%100);
                    }
                }
                // Time is currently the actual minutes, we need the difference. Also the MCT has 5 minute resolution.
                for( i = 0; i < 4; i++ )
                {
                    for( int j = 4; j > 0; j-- )
                    {
                        times[i][j] = times[i][j]-times[i][j-1];
                        times[i][j] = times[i][j]/5;
                        if( times[i][j] < 0 || times[i][j] > 255 )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - time sequencing **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            nRet = NoConfigData;
                        }
                    }
                }
                if( !defaultTOURateString.empty() )
                {
                    defaultTOURate = defaultTOURateString[0] - 'A';
                    if( defaultTOURate < 0 || defaultTOURate > 3 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - bad default rate **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        nRet = NoConfigData;
                    }
                }
                else
                {
                    nRet = NoConfigData;
                }
            }

            if( nRet == NoConfigData ||
                mondaySchedule == std::numeric_limits<long>::min() || tuesdaySchedule == std::numeric_limits<long>::min() ||
                fridaySchedule == std::numeric_limits<long>::min() || saturdaySchedule == std::numeric_limits<long>::min() ||
                sundaySchedule == std::numeric_limits<long>::min() || holidaySchedule == std::numeric_limits<long>::min() ||
                wednesdaySchedule == std::numeric_limits<long>::min() || thursdaySchedule == std::numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                dayTable = holidaySchedule << 14 || saturdaySchedule << 12 || fridaySchedule << 10
                     || thursdaySchedule << 8 || wednesdaySchedule << 6 || tuesdaySchedule << 4
                     || mondaySchedule << 2 || sundaySchedule;



                createTOUDayScheduleString(daySchedule1, times[0], rates[0]);
                createTOUDayScheduleString(daySchedule2, times[1], rates[1]);
                createTOUDayScheduleString(daySchedule3, times[2], rates[2]);
                createTOUDayScheduleString(daySchedule4, times[3], rates[3]);
                CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, dynDaySchedule1);
                CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, dynDaySchedule2);
                CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, dynDaySchedule3);
                CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, dynDaySchedule4);

                if(parse.isKeyValid("force") || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DayTable) != dayTable
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate) != defaultTOURate
                   || dynDaySchedule1 != daySchedule1
                   || dynDaySchedule2 != daySchedule2
                   || dynDaySchedule3 != daySchedule3
                   || dynDaySchedule4 != daySchedule4)
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        OutMessage->Buffer.BSt.Function   = FuncWrite_TOUSchedule1Pos;
                        OutMessage->Buffer.BSt.Length     = FuncWrite_TOUSchedule1Len;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0] = (dayTable>>8);
                        OutMessage->Buffer.BSt.Message[1] = dayTable;
                        OutMessage->Buffer.BSt.Message[2] = times[0][0];
                        OutMessage->Buffer.BSt.Message[3] = times[0][1];
                        OutMessage->Buffer.BSt.Message[4] = times[0][2];
                        OutMessage->Buffer.BSt.Message[5] = times[0][3];
                        OutMessage->Buffer.BSt.Message[6] = times[0][4];
                        OutMessage->Buffer.BSt.Message[7] = ( ((rates[1][4]<<6)&0xC0) | ((rates[1][3]<<4)&0x30) | ((rates[0][4]<<2)&0x0C) | (rates[0][3]&0x03) );
                        OutMessage->Buffer.BSt.Message[8] = ( ((rates[0][2]<<6)&0xC0) | ((rates[0][1]<<4)&0x30) | ((rates[0][0]<<2)&0x0C) | (rates[0][5]&0x03) );
                        OutMessage->Buffer.BSt.Message[9] =  times[1][0];
                        OutMessage->Buffer.BSt.Message[10] = times[1][1];
                        OutMessage->Buffer.BSt.Message[11] = times[1][2];
                        OutMessage->Buffer.BSt.Message[12] = times[1][3];
                        OutMessage->Buffer.BSt.Message[13] = times[1][4];
                        OutMessage->Buffer.BSt.Message[14] = ( ((rates[1][2]<<6)&0xC0) | ((rates[1][1]<<4)&0x30) | ((rates[1][0]<<2)&0x0C) | (rates[1][5]&0x03) );

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Function   = FuncWrite_TOUSchedule2Pos;
                        OutMessage->Buffer.BSt.Length     = FuncWrite_TOUSchedule2Len;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0] = times[2][0];
                        OutMessage->Buffer.BSt.Message[1] = times[2][1];
                        OutMessage->Buffer.BSt.Message[2] = times[2][2];
                        OutMessage->Buffer.BSt.Message[3] = times[2][3];
                        OutMessage->Buffer.BSt.Message[4] = times[2][4];
                        OutMessage->Buffer.BSt.Message[5] = ( ((rates[2][4]<<2)&0x0C) | (rates[2][3]&0x03) );
                        OutMessage->Buffer.BSt.Message[6] = ( ((rates[2][2]<<6)&0xC0) | ((rates[2][1]<<4)&0x30) | ((rates[2][0]<<2)&0x0C) | (rates[2][5]&0x03) );

                        OutMessage->Buffer.BSt.Message[7] = times[3][0];
                        OutMessage->Buffer.BSt.Message[8] = times[3][1];
                        OutMessage->Buffer.BSt.Message[9] = times[3][2];
                        OutMessage->Buffer.BSt.Message[10] = times[3][3];
                        OutMessage->Buffer.BSt.Message[11] = times[3][4];
                        OutMessage->Buffer.BSt.Message[12] = ( ((rates[3][4]<<2)&0x0C) | (rates[3][3]&0x03) );
                        OutMessage->Buffer.BSt.Message[13] = ( ((rates[3][2]<<6)&0xC0) | ((rates[3][1]<<4)&0x30) | ((rates[3][0]<<2)&0x0C) | (rates[3][5]&0x03) );
                        OutMessage->Buffer.BSt.Message[14] = (defaultTOURate);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        // Set up the reads here
                        OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Pos;
                        OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Len;
                        OutMessage->Buffer.BSt.IO       = Emetcon::IO_Function_Read;
                        OUTMESS *touOutMessage = CTIDBG_new OUTMESS(*OutMessage);
                        touOutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                        touOutMessage->Sequence = Emetcon::GetConfig_TOU;
                        strncpy(touOutMessage->Request.CommandStr, "getconfig tou schedule 1", COMMAND_STR_SIZE );
                        outList.push_back( CTIDBG_new OUTMESS(*touOutMessage) );

                        touOutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Pos;
                        touOutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Len;
                        strncpy(touOutMessage->Request.CommandStr, "getconfig tou schedule 3", COMMAND_STR_SIZE );
                        outList.push_back( CTIDBG_new OUTMESS(*touOutMessage) );

                        touOutMessage->Buffer.BSt.Function = FuncRead_TOUStatusPos;
                        touOutMessage->Buffer.BSt.Length   = FuncRead_TOUStatusLen;
                        strncpy(touOutMessage->Request.CommandStr, "getconfig tou", COMMAND_STR_SIZE );
                        outList.push_back( touOutMessage );
                        touOutMessage = 0;
                    }
                    else
                    {
                        nRet = ConfigNotCurrent;
                    }
                }
                else
                {
                    nRet = ConfigCurrent;
                }
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;


    return nRet;
}


int CtiDeviceMCT4xx::executePutConfigAddressing(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS* > &outList)
{
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if(deviceConfig)
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTAddressing);

        if(tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTAddressing)
        {
            long lead, bronze, collection, spid;
            USHORT function, length, io;

            MCTAddressingSPtr config = boost::static_pointer_cast< ConfigurationPart<MCTAddressing> >(tempBasePtr);

            lead = config->getLongValueFromKey(Lead);
            bronze = config->getLongValueFromKey(Bronze);
            collection = config->getLongValueFromKey(Collection);
            spid = config->getLongValueFromKey(ServiceProviderID);

            if(!getOperation(Emetcon::PutConfig_Addressing, function, length, io))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Operation PutConfig_Addressing not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            if(lead == std::numeric_limits<long>::min() || bronze == std::numeric_limits<long>::min() || collection == std::numeric_limits<long>::min() || spid == std::numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if(parse.isKeyValid("force") || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressBronze) != bronze
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressLead) != lead
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressCollection) != collection
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID) != spid )
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        OutMessage->Buffer.BSt.Function   = function;
                        OutMessage->Buffer.BSt.Length     = length;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Write;
                        OutMessage->Buffer.BSt.Message[0] = (bronze);
                        OutMessage->Buffer.BSt.Message[1] = (lead>>8);
                        OutMessage->Buffer.BSt.Message[2] = (lead);
                        OutMessage->Buffer.BSt.Message[3] = (collection>>8);
                        OutMessage->Buffer.BSt.Message[4] = (collection);
                        OutMessage->Buffer.BSt.Message[5] = spid;

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                        OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                        OutMessage->Priority             += 1;//return to normal
                    }
                    else
                    {
                        nRet = ConfigNotCurrent;
                    }
                }
                else
                {
                    nRet = ConfigCurrent;
                }
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;

    return nRet;
}

int CtiDeviceMCT4xx::executePutConfigDst(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList)
{
    int nRet = NORMAL;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if(deviceConfig)
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTDST);

        if(tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTDST)
        {
            long dstBegin, dstEnd, timezoneOffset;
            USHORT function, length, io;
            MCT_DST_SPtr dstConfig = boost::static_pointer_cast< ConfigurationPart<MCT_DST> >(tempBasePtr);
            dstBegin = dstConfig->getLongValueFromKey(DstBegin);
            dstEnd = dstConfig->getLongValueFromKey(DstEnd);
            timezoneOffset = dstConfig->getLongValueFromKey(TimeZoneOffset);

            if(!getOperation(Emetcon::PutConfig_DST, function, length, io))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Operation PutConfig_DST not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            if(dstBegin == std::numeric_limits<long>::min() || dstEnd == std::numeric_limits<long>::min() || timezoneOffset == std::numeric_limits<long>::min())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if(parse.isKeyValid("force") || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DSTStartTime) != dstBegin
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DSTEndTime) != dstEnd
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset) != timezoneOffset)
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        OutMessage->Buffer.BSt.Function   = function;
                        OutMessage->Buffer.BSt.Length     = length;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Write;
                        OutMessage->Buffer.BSt.Message[0] = (dstBegin>>24);
                        OutMessage->Buffer.BSt.Message[1] = (dstBegin>>16);
                        OutMessage->Buffer.BSt.Message[2] = (dstBegin>>8);
                        OutMessage->Buffer.BSt.Message[3] = (dstBegin);
                        OutMessage->Buffer.BSt.Message[4] = (dstEnd>>24);
                        OutMessage->Buffer.BSt.Message[5] = (dstEnd>>16);
                        OutMessage->Buffer.BSt.Message[6] = (dstEnd>>8);
                        OutMessage->Buffer.BSt.Message[7] = (dstEnd);
                        OutMessage->Buffer.BSt.Message[8] = (timezoneOffset);


                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                        OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                        OutMessage->Priority             += 1;//return to normal

                        nRet = NORMAL;
                    }
                    else
                    {
                        nRet = ConfigNotCurrent;
                    }
                }
                else
                {
                    nRet = ConfigCurrent;
                }
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;

    return nRet;
}


int CtiDeviceMCT4xx::executePutConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList)
{
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if(deviceConfig)
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTHoliday);

        if(tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTHoliday)
        {
            long holiday1, holiday2, holiday3;
            USHORT function, length, io;

            MCTHolidaySPtr config = boost::static_pointer_cast< ConfigurationPart<MCTHoliday> >(tempBasePtr);
            holiday1 = config->getLongValueFromKey(HolidayDate1);
            holiday2 = config->getLongValueFromKey(HolidayDate2);
            holiday3 = config->getLongValueFromKey(HolidayDate3);

            if(!getOperation(Emetcon::PutConfig_Holiday, function, length, io))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Operation PutConfig_Holiday not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            if(holiday1 == std::numeric_limits<long>::min() || holiday2 == std::numeric_limits<long>::min() || holiday3 == std::numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if(parse.isKeyValid("force") || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Holiday1) != holiday1
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Holiday2) != holiday2
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Holiday3) != holiday3 )
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        OutMessage->Buffer.BSt.Function   = function;
                        OutMessage->Buffer.BSt.Length     = length;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Write;
                        OutMessage->Buffer.BSt.Message[0] = (holiday1>>24);
                        OutMessage->Buffer.BSt.Message[1] = (holiday1>>16);
                        OutMessage->Buffer.BSt.Message[2] = (holiday1>>8);
                        OutMessage->Buffer.BSt.Message[3] = (holiday1);
                        OutMessage->Buffer.BSt.Message[4] = (holiday2>>24);
                        OutMessage->Buffer.BSt.Message[5] = (holiday2>>16);
                        OutMessage->Buffer.BSt.Message[6] = (holiday2>>8);
                        OutMessage->Buffer.BSt.Message[7] = (holiday2);
                        OutMessage->Buffer.BSt.Message[8] = (holiday3>>24);
                        OutMessage->Buffer.BSt.Message[9] = (holiday3>>16);
                        OutMessage->Buffer.BSt.Message[10] = (holiday3>>8);
                        OutMessage->Buffer.BSt.Message[11] = (holiday3);
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                        OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                        OutMessage->Priority             += 1;//return to normal
                    }
                    else
                    {
                        nRet = ConfigNotCurrent;
                    }
                }
                else
                {
                    nRet = ConfigCurrent;
                }
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;

    return nRet;
}

int CtiDeviceMCT4xx::executePutConfigUsage(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    return NoMethod;
}

int CtiDeviceMCT4xx::executePutConfigDNP(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    return NoMethod;
}

int CtiDeviceMCT4xx::executePutConfigLongLoadProfile(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if(deviceConfig)
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTLongLoadProfile);

        if(tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTLongLoadProfile)
        {
            long channel1, channel2, channel3, channel4, spid;
            USHORT function, length, io;


            MCTLongLoadProfileSPtr config = boost::static_pointer_cast< ConfigurationPart<MCTLongLoadProfile> >(tempBasePtr);
            channel1 = config->getLongValueFromKey(Channel1Length);
            channel2 = config->getLongValueFromKey(Channel2Length);
            channel3 = config->getLongValueFromKey(Channel3Length);
            channel4 = config->getLongValueFromKey(Channel4Length);
            spid = CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID);

            if( spid == std::numeric_limits<long>::min() )
            {
                //We dont have it in dynamic pao info yet, we will get it from the config tables
                BaseSPtr addressTempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTAddressing);

                if(addressTempBasePtr && addressTempBasePtr->getType() == ConfigTypeMCTAddressing)
                {
                    MCTAddressingSPtr addressConfig = boost::static_pointer_cast< ConfigurationPart<MCTAddressing> >(addressTempBasePtr);
                    spid = addressConfig->getLongValueFromKey(ServiceProviderID);
                }
            }

            if(!getOperation(Emetcon::PutConfig_LongloadProfile, function, length, io))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Operation PutConfig_LongloadProfile not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            if(spid == std::numeric_limits<long>::min()
               || channel1 == std::numeric_limits<long>::min()
               || channel2 == std::numeric_limits<long>::min()
               || channel3 == std::numeric_limits<long>::min()
               || channel4 == std::numeric_limits<long>::min())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if(parse.isKeyValid("force")
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPChannel1Len) != channel1
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPChannel2Len) != channel2
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPChannel3Len) != channel3
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len) != channel4 )
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        OutMessage->Buffer.BSt.Function   = function;
                        OutMessage->Buffer.BSt.Length     = length;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0] = spid;
                        OutMessage->Buffer.BSt.Message[1] = channel1;
                        OutMessage->Buffer.BSt.Message[2] = channel2;
                        OutMessage->Buffer.BSt.Message[3] = channel3;
                        OutMessage->Buffer.BSt.Message[4] = channel4;

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        getOperation(Emetcon::GetConfig_LongloadProfile, function, length, io);
                        OutMessage->Buffer.BSt.Function   = function;
                        OutMessage->Buffer.BSt.Length     = length;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Read;
                        OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                        OutMessage->Priority             += 1;//return to normal
                    }
                    else
                    {
                        nRet = ConfigNotCurrent;
                    }
                }
                else
                {
                    nRet = ConfigCurrent;
                }
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;


    return nRet;
}


INT CtiDeviceMCT4xx::decodeGetConfigTime(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiString resultString;
        unsigned long time;
        char timezone_offset;

        if( InMessage->Sequence == Emetcon::GetConfig_Time )
        {
            timezone_offset = InMessage->Buffer.DSt.Message[0];

            time = InMessage->Buffer.DSt.Message[1] << 24 |
                   InMessage->Buffer.DSt.Message[2] << 16 |
                   InMessage->Buffer.DSt.Message[3] <<  8 |
                   InMessage->Buffer.DSt.Message[4];

            resultString  = getName() + " / Current Time: " + printable_time(time) + "\n";
            resultString += getName() + " / Timezone Offset: " + CtiNumStr(((float)timezone_offset) / 4.0, 2) + " hours";
        }
        else if( InMessage->Sequence == Emetcon::GetConfig_TSync )
        {
            time = InMessage->Buffer.DSt.Message[0] << 24 |
                   InMessage->Buffer.DSt.Message[1] << 16 |
                   InMessage->Buffer.DSt.Message[2] <<  8 |
                   InMessage->Buffer.DSt.Message[3];

            resultString = getName() + " / Time Last Synced at: " + printable_time(time);
        }

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT4xx::decodeGetValueLoadProfile(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    CtiString valReport, resultString;
    int       interval_len, block_len, function, channel,
              badData;

    point_info  pi;
    unsigned long timeStamp, decode_time;


    shared_ptr<CtiPointNumeric> pPoint;
    CtiReturnMsg    *ReturnMsg = NULL;  // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData     = NULL;

    //  add error handling for automated load profile retrieval... !
    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        unsigned char interest[5];
        unsigned long tmptime = _llpInterest.time - rwEpoch;

        interest[0] = (tmptime >> 24) & 0x000000ff;
        interest[1] = (tmptime >> 16) & 0x000000ff;
        interest[2] = (tmptime >>  8) & 0x000000ff;
        interest[3] = (tmptime)       & 0x000000ff;
        interest[4] = _llpInterest.channel + 1;

        if( crc8(interest, 5) == DSt->Message[0] )
        {
            //  if we succeeded, we should be okay for successive reads...
            _llpInterest.retry = false;

            channel      = _llpInterest.channel;
            decode_time  = _llpInterest.time + _llpInterest.offset;

            interval_len = getLoadProfileInterval(channel);

            block_len    = interval_len * 6;

            pPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual( channel + 1 + PointOffset_LoadProfileOffset, DemandAccumulatorPointType ));

            for( int i = 0; i < 6; i++ )
            {
                //  this is where the block started...
                timeStamp  = decode_time + (interval_len * i);
                //  but we want interval *ending* times, so add on one more interval
                timeStamp += interval_len;

                pi = getLoadProfileData(channel + 1, DSt->Message + (i * 2) + 1, 2);

                if( pPoint )
                {
                    if( pi.quality != InvalidQuality )
                    {
                        pi.value = pPoint->computeValueForUOM(pi.value);

                        valReport = getName() + " / " + pPoint->getName() + " @ " + printable_time(timeStamp) + " = " + CtiNumStr(pi.value, pPoint->getPointUnits().getDecimalPlaces());

                        if( pData = makePointDataMsg(pPoint, pi, valReport) )
                        {
                            pData->setTime(timeStamp);
                            pData->setTags(pData->getTags() | TAG_POINT_LOAD_PROFILE_DATA);

                            ReturnMsg->insert(pData);
                        }
                    }
                    else
                    {
                        resultString += getName() + " / " + pPoint->getName() + " @ " + printable_time(timeStamp) + " = (invalid data)\n";
                    }
                }
                else
                {
                    if( pi.quality != InvalidQuality )
                    {
                        resultString += getName() + " / LP channel " + CtiNumStr(channel + 1) + " @ " + printable_time(timeStamp) + " = " + CtiNumStr(pi.value, 0) + "\n";
                    }
                    else
                    {
                        resultString += getName() + " / LP channel " + CtiNumStr(channel + 1) + " @ " + printable_time(timeStamp) + " = (invalid data)\n";
                    }
                }
            }

            if( (_llpInterest.time + _llpInterest.offset + block_len + interval_len) < _llpInterest.time_end )
            {
                CtiTime time_begin(_llpInterest.time + _llpInterest.offset + block_len + interval_len),
                        time_end  (_llpInterest.time_end);

                CtiString lp_request_str = "getvalue lp ";

                lp_request_str += "channel " + CtiNumStr(channel + 1) + " " + time_begin.asString() + " " + time_end.asString();

                //  if it's a background message, it's queued
                if(      strstr(InMessage->Return.CommandStr, " background") )   lp_request_str += " background";
                else if( strstr(InMessage->Return.CommandStr, " noqueue") )      lp_request_str += " noqueue";

                CtiRequestMsg newReq(getID(),
                                     lp_request_str,
                                     InMessage->Return.UserID,
                                     0,
                                     InMessage->Return.RouteID,
                                     InMessage->Return.MacroOffset,
                                     0,
                                     0,
                                     InMessage->Priority);

                //  this may be NULL if it's a background request, but assign it anyway
                newReq.setConnectionHandle((void *)InMessage->Return.Connection);

                CtiDeviceBase::ExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);
            }
            else
            {
                resultString += "Load profile request complete\n";

                _llpInterest.time_end = _llpInterest.time + _llpInterest.offset + block_len + interval_len;
            }
        }
        else
        {
            resultString = "Load Profile Interest check does not match";

            if( !_llpInterest.retry )
            {
                _llpInterest.time    = 0;
                _llpInterest.retry = true;

                resultString += ", retrying";

                CtiRequestMsg newReq(getID(),
                                     InMessage->Return.CommandStr,
                                     InMessage->Return.UserID,
                                     0,
                                     InMessage->Return.RouteID,
                                     InMessage->Return.MacroOffset);

                newReq.setConnectionHandle((void *)InMessage->Return.Connection);

                CtiDeviceBase::ExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);
            }
            else
            {
                resultString += " - try message again";
                _llpInterest.retry = false;
            }
        }

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }
    else
    {
        //  this code is replicated in ErrorDecode()
        if( !_llpInterest.retry )
        {
            interval_len = getLoadProfileInterval(_llpInterest.channel);

            block_len    = interval_len * 6;

            _llpInterest.retry = true;

            //  we're asking for the same block, not the next block, so don't move ahead a block
            CtiTime time_begin(_llpInterest.time + _llpInterest.offset + /* block_len + */ interval_len),
                    time_end  (_llpInterest.time_end);

            CtiString lp_request_str = "getvalue lp ";

            lp_request_str += "channel " + CtiNumStr(_llpInterest.channel + 1) + " " + time_begin.asString() + " " + time_end.asString();

            //  if it's a background message, it's queued
            if(      strstr(InMessage->Return.CommandStr, " background") )   lp_request_str += " background";
            else if( strstr(InMessage->Return.CommandStr, " noqueue") )      lp_request_str += " noqueue";

            CtiRequestMsg newReq(getID(),
                                 lp_request_str,
                                 InMessage->Return.UserID,
                                 0,
                                 InMessage->Return.RouteID,
                                 InMessage->Return.MacroOffset,
                                 0,
                                 0,
                                 InMessage->Priority);

            //  this may be NULL if it's a background request, but assign it anyway
            newReq.setConnectionHandle((void *)InMessage->Return.Connection);

            CtiDeviceBase::ExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);
        }
        else
        {
            _llpInterest.failed = true;
            _llpInterest.retry  = false;
        }
    }

    return status;
}


INT CtiDeviceMCT4xx::decodeScanLoadProfile(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    string         val_report;
    int            channel, block, interval_len;
    unsigned long  timestamp, pulses;
    point_info   pi;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    shared_ptr<CtiPointNumeric> point;
    CtiReturnMsg    *ret_msg = 0;  // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pdata   = 0;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Load Profile Scan Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ret_msg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ret_msg->setUserMessageId(InMessage->Return.UserID);

        if( (channel = parse.getiValue("scan_loadprofile_channel", 0)) &&
            (block   = parse.getiValue("scan_loadprofile_block",   0)) )
        {
            //  parse is 1-based, we need it 0-based
            channel--;

            interval_len = getLoadProfileInterval(channel);

            if( point = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(channel + PointOffset_LoadProfileOffset + 1, DemandAccumulatorPointType)) )
            {
                //  this is where the block started...
                timestamp  = TimeNow.seconds();
                timestamp -= interval_len * 6 * block;
                timestamp -= timestamp % (interval_len * 6);

                if( timestamp == _lp_info[channel].current_request )
                {
                    for( int offset = 5; offset >= 0; offset-- )
                    {
                        pi = getLoadProfileData(channel, DSt->Message + offset*2 + 1, 2);

                        //  compute for the UOM
                        pi.value = point->computeValueForUOM(pi.value);

                        if( pdata = makePointDataMsg(point, pi, "") )
                        {
                            //  the data goes from latest to earliest...  it's kind of backwards
                            pdata->setTime(timestamp + interval_len * (6 - offset));

                            pdata->setTags(TAG_POINT_LOAD_PROFILE_DATA);

                            ret_msg->insert(pdata);
                        }
                    }

                    //  unnecessary?
                    setLastLPTime (timestamp + interval_len * 6);

                    _lp_info[channel].archived_reading = timestamp + interval_len * 6;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - possible LP logic error for device \"" << getName() << "\";  calculated timestamp=" << CtiTime(timestamp) << "; current_request=" << CtiTime(_lp_info[channel].current_request) << endl;
                        dout << "commandstr = " << InMessage->Return.CommandStr << endl;
                    }
                }
            }
            else
            {
                ret_msg->setResultString("No load profile point defined for '" + getName() + "'");
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - missing scan_loadprofile token in decodeScanLoadProfile for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            ret_msg->setResultString("Malformed LP command string for '" + getName() + "'");
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ret_msg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT4xx::decodeGetConfigTOU(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        string resultString;
        unsigned long time;
        CtiTime tmpTime;

        int schedulenum = parse.getiValue("tou_schedule");

        if( schedulenum > 0 && schedulenum <= 4 )
        {
            long timeArray[2][5];
            long rateArray[2][6];
            schedulenum -= (schedulenum - 1) % 2;

            for( int offset = 0; offset < 2; offset++ )
            {
                int rates, current_rate, previous_rate, byte_offset, time_offset;

                resultString += getName() + " / TOU Schedule " + CtiNumStr(schedulenum + offset) + ":\n";

                if( offset == 0 )
                {
                    rates = ((InMessage->Buffer.DSt.Message[5] & 0x0f) << 8) | InMessage->Buffer.DSt.Message[6];
                    byte_offset = 0;
                }
                else
                {
                    rates = ((InMessage->Buffer.DSt.Message[5] & 0xf0) << 4) | InMessage->Buffer.DSt.Message[12];
                    byte_offset = 7;
                }

                rateArray[offset][0] = (rates >> 2) & 0x03;
                rateArray[offset][1] = (rates >> 4) & 0x03;
                rateArray[offset][2] = (rates >> 6) & 0x03;
                rateArray[offset][3] = (rates >> 8) & 0x03;
                rateArray[offset][4] = (rates >> 10) & 0x03;
                rateArray[offset][5] = (rates) & 0x03;

                current_rate = rates & 0x03;
                resultString += "00:00: ";
                resultString += (char)('A' + current_rate);
                resultString += "\n";
                rates >>= 2;

                time_offset = 0;
                previous_rate = current_rate;
                for( int switchtime = 0; switchtime < 5; switchtime++ )
                {
                    int hour, minute;

                    time_offset += InMessage->Buffer.DSt.Message[byte_offset + switchtime] * 300;
                    timeArray[offset][switchtime] = InMessage->Buffer.DSt.Message[byte_offset + switchtime];

                    hour   = time_offset / 3600;
                    minute = (time_offset / 60) % 60;

                    current_rate = rates & 0x03;

                    if( (hour <= 23) && (current_rate != previous_rate) )
                    {
                        resultString += CtiNumStr(hour).zpad(2) + ":" + CtiNumStr(minute).zpad(2) + ": " + (char)('A' + current_rate) + "\n";
                    }

                    previous_rate = current_rate;

                    rates >>= 2;
                }

                resultString += "- end of day - \n\n";
            }

            string dayScheduleA, dayScheduleB;
            createTOUDayScheduleString(dayScheduleA, timeArray[0], rateArray[0]);
            createTOUDayScheduleString(dayScheduleB, timeArray[1], rateArray[1]);

            if( schedulenum < 2 )
            {
                CtiDeviceBase::setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, dayScheduleA);
                CtiDeviceBase::setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, dayScheduleB);
            }
            else
            {
                CtiDeviceBase::setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, dayScheduleA);
                CtiDeviceBase::setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, dayScheduleB);
            }

        }
        else
        {
            resultString = getName() + " / TOU Status:\n\n";

            time = InMessage->Buffer.DSt.Message[6] << 24 |
                   InMessage->Buffer.DSt.Message[7] << 16 |
                   InMessage->Buffer.DSt.Message[8] <<  8 |
                   InMessage->Buffer.DSt.Message[9];

            resultString += "Current time: " + CtiTime(time).asString() + "\n";

            int tz_offset = (char)InMessage->Buffer.DSt.Message[10] * 15;

            resultString += "Time zone offset: " + CtiNumStr((float)tz_offset / 60.0, 1) + " hours ( " + CtiNumStr(tz_offset) + " minutes)\n";

            if( InMessage->Buffer.DSt.Message[3] & 0x80 )
            {
                resultString += "Critical peak active\n";
            }
            if( InMessage->Buffer.DSt.Message[4] & 0x80 )
            {
                resultString += "Holiday active\n";
            }
            if( InMessage->Buffer.DSt.Message[4] & 0x40 )
            {
                resultString += "DST active\n";
            }

            resultString += "Current rate: " + string(1, (char)('A' + (InMessage->Buffer.DSt.Message[3] & 0x7f))) + "\n";

            resultString += "Current schedule: " + CtiNumStr((int)(InMessage->Buffer.DSt.Message[4] & 0x03) + 1) + "\n";
/*
            resultString += "Current switch time: ";

            if( InMessage->Buffer.DSt.Message[5] == 0xff )
            {
                resultString += "not active\n";
            }
            else
            {
                 resultString += CtiNumStr((int)InMessage->Buffer.DSt.Message[5]) + "\n";
            }
*/
            resultString += "Default rate: ";

            if( InMessage->Buffer.DSt.Message[2] == 0xff )
            {
                resultString += "No TOU active\n";
            }
            else
            {
                resultString += string(1, (char)('A' + InMessage->Buffer.DSt.Message[2])) + "\n";
            }

            resultString += "\nDay table: \n";

            char *(daynames[8]) = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Holiday"};

            for( int i = 0; i < 8; i++ )
            {
                int dayschedule = InMessage->Buffer.DSt.Message[1 - i/4] >> ((i % 4) * 2) & 0x03;

                resultString += "Schedule " + CtiNumStr(dayschedule + 1) + " - " + daynames[i] + "\n";
            }
        }

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT4xx::ModelDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList)
{
    INT status = NORMAL;


    switch(InMessage->Sequence)
    {
        case Emetcon::GetValue_LoadProfile:
        {
            status = decodeGetValueLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::Scan_LoadProfile:
        {
            status = decodeScanLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::GetConfig_TOU):
        {
            status = decodeGetConfigTOU(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        default:
        {
            status = Inherited::ModelDecode(InMessage, TimeNow, vgList, retList, outList);

            if(status != NORMAL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " IM->Sequence = " << InMessage->Sequence << " " << getName() << endl;
            }
            break;
        }
    }


    return status;
}


INT CtiDeviceMCT4xx::ErrorDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList)
{
    int retVal = NoError;

    switch( InMessage->Sequence )
    {
        case Emetcon::GetValue_LoadProfile:
        {
            int interval_len, block_len;

            //  this code is replicated in decodeGetValueLoadProfile()
            if( !_llpInterest.retry )
            {
                interval_len = getLoadProfileInterval(_llpInterest.channel);

                block_len    = interval_len * 6;

                _llpInterest.retry = true;

                //  we're asking for the same block, not the next block, so don't move ahead a block
                CtiTime time_begin(_llpInterest.time + _llpInterest.offset + /* block_len + */ interval_len),
                        time_end  (_llpInterest.time_end);

                CtiString lp_request_str = "getvalue lp ";

                lp_request_str += "channel " + CtiNumStr(_llpInterest.channel + 1) + " " + time_begin.asString() + " " + time_end.asString();

                //  if it's a background message, it's queued
                if(      strstr(InMessage->Return.CommandStr, " background") )   lp_request_str += " background";
                else if( strstr(InMessage->Return.CommandStr, " noqueue") )      lp_request_str += " noqueue";

                CtiRequestMsg newReq(getID(),
                                     lp_request_str,
                                     InMessage->Return.UserID,
                                     0,
                                     InMessage->Return.RouteID,
                                     InMessage->Return.MacroOffset,
                                     0,
                                     0,
                                     InMessage->Priority);

                //  this may be NULL if it's a background request, but assign it anyway
                newReq.setConnectionHandle((void *)InMessage->Return.Connection);

                CtiDeviceBase::ExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);

                CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

                ReturnMsg->setUserMessageId(InMessage->Return.UserID);
                ReturnMsg->setResultString("Load profile retry submitted");

                retList.push_back(ReturnMsg);
            }
            else
            {
                _llpInterest.failed = true;
                _llpInterest.retry  = false;
            }

            break;
        }

        default:
        {
            retVal = Inherited::ErrorDecode(InMessage, TimeNow, vgList, retList, outList);

            break;
        }
    }

    return retVal;
}

void CtiDeviceMCT4xx::createTOUDayScheduleString(string &schedule, long (&times)[5], long (&rates)[6])
{
    for( int i=0; i<5; i++ )
    {
        schedule += CtiNumStr(times[i]);
        schedule += ', ';
    }
    for( i=0; i<5; i++ )
    {
        schedule += CtiNumStr(rates[i]);
        schedule += ', ';
    }
    schedule += CtiNumStr(rates[5]);
}


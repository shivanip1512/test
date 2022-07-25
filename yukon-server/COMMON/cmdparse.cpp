#include "precompiled.h"

#include "cmdparse.h"
#include "cparms.h"
#include "logger.h"
#include "numstr.h"
#include "pointdefs.h"
#include "utility.h"
#include "string_util.h"

#include "std_helper.h"

#include <boost/regex.hpp>
#include <boost/algorithm/string/replace.hpp>
#include <boost/algorithm/string/split.hpp>
#include <boost/algorithm/string/classification.hpp>
#include <boost/algorithm/string/trim.hpp>
#include <boost/algorithm/string/case_conv.hpp>

using namespace std;

static const std::string str_quoted_token("((\".*\")|('.*'))");

static const std::string str_signed_num     ("(\\+|\\-)?([0-9]+)");
static const std::string str_num     ("([0-9]+)");
static const std::string str_floatnum("([0-9]+(\\.[0-9]*)?)");
static const std::string str_floatnum_only("([0-9]+(\\.[0-9]*))");
static const std::string str_hexnum  ("(0x[0-9a-f]+)");

static const std::string str_floatnum_list(str_floatnum + "( " + str_floatnum + ")*");

// str_hexnum must come before str_num: if str_num is first it will only match the 0 of an input value in hex.
static const std::string str_anynum  ("(" + str_hexnum + "|" + str_num + ")");

static const std::string str_date("([0-9]+[/-][0-9]+[/-][0-9]+)");
static const std::string str_time("([0-9]+:[0-9]+(:[0-9]+)?)");

static const std::string str_daterange(str_date + "( " + str_date + ")?");

static const boost::regex   re_quoted_token (str_quoted_token);

const map<string, string> TokenReplacements {
    {"NUM",  str_num},
    {"DATE", str_date},
    {"TIME", str_time}};


std::string replace_tokens(std::string with_tokens)
{
    for( const auto replacement : TokenReplacements )
    {
        boost::algorithm::replace_all(with_tokens, replacement.first, replacement.second);
    }

    return with_tokens;
}

static const boost::regex   re_signed_num   (str_signed_num);
static const boost::regex   re_num      (str_num);
static const boost::regex   re_floatnum (str_floatnum);
static const boost::regex   re_hexnum   (str_hexnum);
static const boost::regex   re_anynum   (str_anynum);
static const boost::regex   re_date     (str_date);
static const boost::regex   re_time     (str_time);
static const boost::regex   re_daterange(str_daterange);

static const boost::regex   re_target   ("target.+assign");
static const boost::regex   re_assign   ("assign.+");

using Cti::matchRegex;
using Cti::locateRegex;
using Cti::containsRegex;
using Cti::containsString;
using Cti::icontainsString;
using Cti::removeRegexAllMatches;
using Cti::removeRegexFirstMatch;
using Cti::is_char;

CtiCommandParser::CtiCommandParser(const string str) :
_wasExternallyModified(false)
{
    std::string cmdStr(str);

    for( int pos = 0; pos < cmdStr.length(); )
    {
        std::string::size_type quoted_begin, quoted_length;

        //  look for any quoted tokens we need to leave untouched
        quoted_begin = locateRegex(cmdStr, re_quoted_token, &quoted_length, pos);

        //  if there's non-quoted content in the string, process it first
        if( (quoted_begin - pos) > 0 )
        {
            std::string temp_str;

            if( quoted_begin == std::string::npos )
            {
                //  grab the rest of the string
                temp_str = cmdStr.substr(pos);
            }
            else
            {
                //  grab up until the quoted token
                temp_str = cmdStr.substr(pos, quoted_begin - pos);
            }

            //  move us forward by the amount we're about to process
            pos += temp_str.length();

            //  truncate any runs of tabs and spaces
            temp_str = boost::regex_replace(temp_str, boost::regex("[ \t]+"), " ", boost::match_default | boost::format_all);

            CtiToLower(temp_str);

            _cmdString += temp_str;
        }

        //  if we found a quoted token, copy it over unmodified
        if( quoted_begin != std::string::npos )
        {
            _cmdString += cmdStr.substr(quoted_begin, quoted_length);

            pos += quoted_length;
        }
    }

    doParse(_cmdString);
}


void  CtiCommandParser::doParse(const string &_Cmd)
{
    _command = InvalidRequest;
    _flags = 0;
    parse();
}


void  CtiCommandParser::parse()
{
    std::string       CmdStr = _cmdString;
    std::string       token;
    std::string       cmdstr;
    std::string       strnum;

    _actionItems.clear();   // 20050125 CGP.  Getting duplicate actionItems when I reparse in the groups.  This should be benign.

    if( containsRegex(CmdStr, "^ *pil ") || containsRegex(CmdStr, "^ *command ") )
    {
        removeRegexAllMatches(CmdStr, "^ *pil ");
        removeRegexAllMatches(CmdStr, "^ *command ");
    }

    CtiTokenizer    tok(CmdStr);

    if(containsString(CmdStr, " serial"))
    {
        static const boost::regex regexp("serial[= ]+" + str_anynum);

        if(!(token = matchRegex(CmdStr, regexp)).empty())
        {
            UINT serial = 0;    // can't be negative - uses entire 32-bit range for values.
            CHAR *p;

            if(!(strnum = matchRegex(token, re_hexnum)).empty())
            {
                serial = strtoul(strnum.c_str(), &p, 16);
            }
            else if(!(strnum = matchRegex(token, re_num)).empty())
            {
                serial = strtoul(strnum.c_str(), &p, 10);
            }
            _cmd["serial"] = CtiParseValue( serial );
            removeRegexAllMatches(CmdStr, regexp);
        }
    }

    if(containsString(CmdStr, "system message "))
    {
        _cmd["system_message"] = CtiParseValue(true);

        if(containsString(CmdStr, "port entries"))
        {
            _cmd["port_entries"] = CtiParseValue(true);
        }
        else if( containsString(CmdStr, "request cancel") )
        {
            _cmd["request_cancel"] = CtiParseValue(true);
        }
        else if( containsString(CmdStr, "request count") )
        {
            _cmd["request_count"] = CtiParseValue(true);
        }
    }

    if(containsString(CmdStr, " select"))
    {
        static const boost::regex re_name   ("select name "        + str_quoted_token);
        static const boost::regex re_id     ("select (device)?id " + str_num);
        static const boost::regex re_grp    ("select group "       + str_quoted_token);
        static const boost::regex re_altg   ("select altgroup "    + str_quoted_token);
        static const boost::regex re_billg  ("select billgroup "   + str_quoted_token);
        static const boost::regex re_rtename("select route *name " + str_quoted_token);
        static const boost::regex re_rteid  ("select route *id "   + str_num);
        static const boost::regex re_ptname ("select point *name " + str_quoted_token);
        static const boost::regex re_ptid   ("select point *id "   + str_num);

        if(!(token = matchRegex(CmdStr, re_name)).empty())
        {
            _cmd["device"] = CtiParseValue(token.substr(13, token.length() - 14), -1 );

            removeRegexAllMatches(CmdStr, re_name);
        }
        else if(!(token = matchRegex(CmdStr, re_id)).empty())
        {
            CtiTokenizer ntok(token);
            ntok();  // pull the select keyword
            ntok();  // pull the id keyword
            if(!(token = ntok()).empty())   // get the value
            {
                _cmd["device"] = CtiParseValue( atoi(token.c_str()) );
            }
            removeRegexAllMatches(CmdStr, re_id);
        }
        else if(!(token = matchRegex(CmdStr, re_grp)).empty())
        {
            _cmd["group"] = CtiParseValue(token.substr(14, token.length() - 15), -1 );

            removeRegexAllMatches(CmdStr, re_grp);
        }
        else if(!(token = matchRegex(CmdStr, re_altg)).empty())
        {
            _cmd["altgroup"] = CtiParseValue(token.substr(17, token.length() - 18), -1 );

            removeRegexAllMatches(CmdStr, re_altg);
        }
        else if(!(token = matchRegex(CmdStr, re_billg)).empty())
        {
            _cmd["billgroup"] = CtiParseValue(token.substr(18, token.length() - 19), -1 );

            removeRegexAllMatches(CmdStr, re_altg);
        }
        else if(!(token = matchRegex(CmdStr, re_rtename)).empty())
        {
            size_t nameStart = 12;  //  /select route/
            while( token[nameStart] == ' ' )
            {
                nameStart++;
            }
            nameStart += 6;  //  add on /name "/

            _cmd["route"] = CtiParseValue(token.substr(nameStart, token.length() - nameStart - 1), -1 );

            removeRegexAllMatches(CmdStr, re_rtename);
        }
        else if(!(token = matchRegex(CmdStr, re_rteid)).empty())
        {
            removeRegexAllMatches(token,  boost::regex("select route *id "));

            if(!token.empty())   // get the value
            {
                _cmd["route"] = CtiParseValue( atoi(token.c_str()) );
            }
            removeRegexAllMatches(CmdStr, re_rteid);
        }
        else if(!(token = matchRegex(CmdStr, re_ptname)).empty())
        {
            size_t nameStart = 12;  //  /select point/
            while( token[nameStart] == ' ' )
            {
                nameStart++;
            }
            nameStart += 6;  //  add on /name "/

            _cmd["point"] = CtiParseValue(token.substr(nameStart, token.length() - nameStart - 1), -1 );

            removeRegexAllMatches(CmdStr, re_ptname);
        }
        else if( !(token = matchRegex(CmdStr, re_ptid)).empty())
        {
            size_t idStart = 12;  //  /select point/
            while( token[idStart] == ' ' )
            {
                idStart++;
            }
            idStart += 3;  //  add on /id /
            token = token.substr(idStart);

            _cmd["point"] = CtiParseValue( atoi(token.c_str()) );

            removeRegexAllMatches(CmdStr, re_ptid);
        }
        else
        {
            _cmd["device"] = CtiParseValue( -1 );
        }
    }

    resolveProtocolType(CmdStr);


    if(containsString(CmdStr, " noqueue"))
    {
        _cmd["noqueue"] = CtiParseValue("true");
    }
    if(!(token = matchRegex(CmdStr, "protocol_priority " + str_num)).empty())
    {
        if(!(strnum = matchRegex(token, re_num)).empty())
        {
            _cmd["xcpriority"] = CtiParseValue( atoi(strnum.c_str()) );            // Expresscom only supports a 0 - 3 priority 0 highest.
        }
    }


    if(!(cmdstr = tok()).empty())
    {
        CtiToLower(cmdstr);

        if(cmdstr == "getvalue")
        {
            setCommand(GetValueRequest);
            doParseGetValue(CmdStr);
        }
        else if(cmdstr == "putvalue")
        {
            setCommand(PutValueRequest);
            doParsePutValue(CmdStr);
        }
        else if(cmdstr == "getstatus")
        {
            setCommand(GetStatusRequest);
            doParseGetStatus(CmdStr);
        }
        else if(cmdstr == "putstatus")
        {
            setCommand(PutStatusRequest);
            doParsePutStatus(CmdStr);
        }
        else if(cmdstr == "getconfig")
        {
            setCommand(GetConfigRequest);
            doParseGetConfig(CmdStr);
        }
        else if(cmdstr == "putconfig")
        {
            setCommand(PutConfigRequest);
            doParsePutConfig(CmdStr);

        }
        else if(cmdstr == "loop" || cmdstr == "ping")  //  so "ping" is just an alias
        {
            setCommand(LoopbackRequest);
            _cmd["count"] = CtiParseValue( 1 );
        }
        else if(cmdstr == "control")
        {
            setCommand(ControlRequest);
            doParseControl(CmdStr);
        }
        else if(cmdstr == "scan")
        {
            setCommand(ScanRequest);
            doParseScan(CmdStr);
        }
        else
        {
            setCommand(InvalidRequest);
        }
    }
    else
    {
        setCommand(InvalidRequest);
    }

    return;
}

void  CtiCommandParser::doParseGetValue(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    UINT        flag = 0;

    std::string   temp;
    std::string   token;

    static const boost::regex   re_kxx  ("(kwh|kvah|kvarh)[abcdt]?");  //  Match on kwh, kwha,b,c,d,t
    static const boost::regex   re_hrate("h[abcdt]?");                 //  Match on h,ha,hb,hc,hd,ht
    static const boost::regex   re_rate ("rate *[abcdt]");

    static const boost::regex   re_demand(" dema|( kw| kvar| kva)( |$)");  //  match "dema"nd, but also match "kw", "kvar", or "kva"

    //  getvalue lp channel 1 12/14/04 12:00         //  reads a block of 6 readings, starting from the specified time and date
    //
    //  getvalue lp channel 2 12/13/2005             //  grabs the whole day
    //  getvalue lp channel 2 12/13/2005 12/15/2005  //  this wil do range of entire days
    //  getvalue lp channel 2 12/13/2005 12:00 12/15/2005  //  this grabs the second half of 13th, and all of the 14th and 15th
    static const boost::regex  re_lp(replace_tokens("lp channel NUM DATE( TIME)?( DATE( TIME)?)?"));

    //  getvalue lp peak daily channel 2 9/30/04 30
    //  getvalue lp peak hour channel 3 10-15-2003 15
    static const boost::regex  re_lp_peak(replace_tokens("lp peak (day|hour|interval) channel NUM DATE NUM"));

    //  getvalue voltage profile 12/13/2005 12/15/2005
    static const boost::regex  re_voltage_profile(replace_tokens("voltage profile DATE( TIME)?( DATE( TIME)?)?"));

    //  getvalue daily read
    //  getvalue daily read 12/12/2007
    //  getvalue daily read 12/12/2007 12/27/2007
    //  getvalue daily read channel n 12/12/2007 12/27/2007
    //  getvalue daily read detail 12/12/2007
    //  getvalue daily read detail channel n 12/12/2007
    //  getvalue daily read cancel
    //  getvalue daily reads
    //  getvalue daily reads 12/12/2007 12/27/2007
    static const boost::regex  re_daily_read(("daily read(s)?( cancel| detail)?( channel ") + str_num + (")?") + ("( ") + str_daterange + (")?"));

    //  getvalue hourly read
    //  getvalue hourly read 12/12/2007
    //  getvalue hourly read 12/12/2007 12/27/2007
    static const boost::regex  re_hourly_read(("hourly read( cancel)?( channel ") + str_num + (")?( ") + str_daterange + (")?"));

    static const boost::regex  re_outage(("outage ") + str_num);

    static const boost::regex  re_offset(("off(set)? *") + str_num);
    static const boost::regex  re_channel(("channel ") + str_num);


    static const boost::regex   re_dnp_collection(("dnp collection ") + str_num);
    static const boost::regex   re_dnp_analog(("dnp analog ") + str_num);
    static const boost::regex   re_dnp_status("dnp status");
    static const boost::regex   re_dnp_accumulator(("dnp accumulator ") + str_num);

    // DR 2 way reads
    static const boost::regex   re_interval_demand  (("interval last"));
    static const boost::regex   re_load_runtime     (("runtime(( load| relay) ")  + str_num + (")?( previous ") + str_num + (")?"));
    static const boost::regex   re_load_shedtime    (("shedtime(( load| relay) ") + str_num + (")?( previous ") + str_num + (")?"));
    static const boost::regex   re_propcount        (("propcount"));
    static const boost::regex   re_control_time     (("controltime remaining(( load| relay) ") + str_num + (")?"));
    static const boost::regex   re_xfmr_historical  (("historical(( transformer| table) ") + str_num + (")?"));
    static const boost::regex   re_duty_cycle       (("duty cycle( ct ") + str_num + (")?"));

    // Expresscom 3-part commands
    static const boost::regex   re_tamper_info      (("tamper info"));
    static const boost::regex   re_dr_summary       (("dr summary"));
    static const boost::regex   re_hourly_data_log  (("hourly log ") + str_date + (" ") + str_time);

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "getvalue")
    {
        if(!(token = matchRegex(CmdStr, re_kxx)).empty())      // Sourcing from CmdStr, which is the entire command string.
        {
            // I have a match on the kxxh regular expression....
            if(containsString(token, "kwh"))
            {
                flag |= CMD_FLAG_GV_KWH;
            }
            if(containsString(token, "kvah"))
            {
                flag |= CMD_FLAG_GV_KVAH;
            }
            if(containsString(token, "kvarh"))
            {
                flag |= CMD_FLAG_GV_KVARH;
            }

            if(!token.empty())
            {
                if(!(temp = matchRegex(token, re_hrate)).empty())
                {
                    if(temp == "ha")
                    {
                        flag &= ~CMD_FLAG_GV_RATEMASK;
                        flag |= CMD_FLAG_GV_RATEA;
                    }
                    if(temp == "hb")
                    {
                        flag &= ~CMD_FLAG_GV_RATEMASK;
                        flag |= CMD_FLAG_GV_RATEB;
                    }
                    if(temp == "hc")
                    {
                        flag &= ~CMD_FLAG_GV_RATEMASK;
                        flag |= CMD_FLAG_GV_RATEC;
                    }
                    if(temp == "hd")
                    {
                        flag &= ~CMD_FLAG_GV_RATEMASK;
                        flag |= CMD_FLAG_GV_RATED;
                    }
                    if(temp == "ht")
                    {
                        flag &= ~CMD_FLAG_GV_RATEMASK;
                        flag |= CMD_FLAG_GV_RATET;
                    }
                }
                else
                {
                    flag &= ~CMD_FLAG_GV_RATEMASK;
                    flag |= CMD_FLAG_GV_RATEA;
                }
            }
        }
        else if(containsString(CmdStr, " usage"))
        {
            flag |= CMD_FLAG_GV_USAGE;
        }
        else if( containsString(CmdStr, " meter_read") )
        {
            _cmd["meter_read"] = true;
        }
        else if(containsString(CmdStr, " lp "))
        {
            if(!(token = matchRegex(CmdStr, re_lp)).empty())
            {
                CtiTokenizer cmdtok(token);

                _cmd["lp_command"] = CtiParseValue("lp");

                cmdtok();  //  move past lp
                cmdtok();  //  move past channel

                _cmd["lp_channel"] = atoi(cmdtok().c_str());

                _cmd["lp_date_start"] = cmdtok();
                    temp = cmdtok();

                //  the optional "start time" parameter
                if( containsRegex(temp, re_time) )
                {
                    _cmd["lp_time_start"] = temp;
                    temp = cmdtok();
                }

                //  the optional "end date" parameter
                if( containsRegex(temp, re_date) )
                {
                    _cmd["lp_date_end"] = temp;
                    temp = cmdtok();

                    //  the optional "end time" parameter
                if( containsRegex(temp, re_time) )
                {
                    _cmd["lp_time_end"] = temp;
                    }
                }
            }
            else if(!(token = matchRegex(CmdStr, re_lp_peak)).empty())
            {
                //  getvalue lp peak daily channel 2 9/30/04 30
                //  getvalue lp peak hourly channel 3 10-15-2003 15
                CtiTokenizer cmdtok(token);

                _cmd["lp_command"]    = CtiParseValue("peak");

                cmdtok();  //  move past lp
                cmdtok();  //  move past peak

                _cmd["lp_peaktype"] = cmdtok();

                cmdtok();  //  move past channel

                _cmd["lp_channel"]    = atoi(cmdtok().c_str());
                _cmd["lp_date_start"] = cmdtok();
                _cmd["lp_range"]      = atoi(cmdtok().c_str());
            }
            else if( containsString(CmdStr, " status") )
            {
                _cmd["lp_command"] = CtiParseValue("status");
            }
            else if( containsString(CmdStr, " cancel") )
            {
                _cmd["lp_command"] = CtiParseValue("cancel");
            }
            else if( containsString(CmdStr, " resume") )
            {
                _cmd["lp_command"] = CtiParseValue("resume");
            }
        }
        else if( containsRegex(CmdStr, re_demand) )      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_GV_DEMAND;
        }
        else if( containsRegex(CmdStr, re_tamper_info) )
        {
            flag |= CMD_FLAG_GV_TAMPER_INFO;
        }
        else if( containsRegex(CmdStr, re_dr_summary) )
        {
            flag |= CMD_FLAG_GV_DR_SUMMARY;
        }
        else if(!(token = matchRegex(CmdStr, re_hourly_data_log)).empty())
        {
            flag |= CMD_FLAG_GV_HOURLY_LOG;

            //  getvalue hourly log mm/dd/yy hh:mm:ss...
            CtiTokenizer cmdtok(token);

            cmdtok(); // move past "hourly"
            cmdtok(); // move past "log"

            _cmd["hourly_log_date"] = cmdtok();
            _cmd["hourly_log_time"] = cmdtok();
        }
        else if(containsString(CmdStr, " minmax"))
        {
            flag |= CMD_FLAG_GV_MINMAX;
        }
        else if(containsString(CmdStr, " voltage"))
        {
            flag |= CMD_FLAG_GV_VOLTAGE;

            if(!(token = matchRegex(CmdStr, re_voltage_profile)).empty())
            {
                // getvalue voltage profile 12/13/2005 [03:45] [12/15/2005 [21:35]]

                _cmd["voltage_profile"] = true;

                CtiTokenizer cmdtok(token);

                cmdtok(); // voltage
                cmdtok(); // profile

                _cmd["read_points_date_begin"] = cmdtok();

                temp = cmdtok();

                //  the optional "start time" parameter
                if( containsRegex(temp, re_time) )
                {
                    _cmd["read_points_time_begin"] = temp;
                    temp = cmdtok();
                }

                //  the optional "end date" parameter
                if( containsRegex(temp, re_date) )
                {
                    _cmd["read_points_date_end"] = temp;
                    temp = cmdtok();

                    //  the optional "end time" parameter
                    if( containsRegex(temp, re_time) )
                    {
                        _cmd["read_points_time_end"] = temp;
                    }
                }
            }
        }
        else if(containsString(CmdStr, " outage"))
        {
            if(!(token = matchRegex(CmdStr, re_outage)).empty())
            {
                //  getvalue outage 1..6
                CtiTokenizer cmdtok(token);

                cmdtok();  //  move past "outage"

                _cmd["outage"] = atoi(cmdtok().c_str());
            }
            else
            {
                _cmd["outage"] = -1;
            }
        }
        else if(containsString(CmdStr, " codes"))
        {
            _cmd["codes"] = CtiParseValue(TRUE);
        }
        else if(containsString(CmdStr, " dnp"))
        {
            _cmd["ied_dnp"] = CtiParseValue(TRUE);
            if(!(token = matchRegex(CmdStr, re_dnp_collection)).empty())
            {
                //  getvalue outage 1..6
                CtiTokenizer cmdtok(token);

                cmdtok();  //  move past "dnp"
                cmdtok();  //  move past "group"

                _cmd["collectionnumber"] = atoi(cmdtok().c_str());
            }
            else if(!(token = matchRegex(CmdStr, re_dnp_analog)).empty())
            {
                //  getvalue outage 1..6
                CtiTokenizer cmdtok(token);

                cmdtok();  //  move past "dnp"
                cmdtok();  //  move past "analog"

                _cmd["analognumber"] = atoi(cmdtok().c_str());
            }
            else if(!(token = matchRegex(CmdStr, re_dnp_status)).empty())
            {
                //  getvalue outage 1..6
                CtiTokenizer cmdtok(token);

                cmdtok();  //  move past "dnp"
                cmdtok();  //  move past "binary"

                _cmd["statusnumber"] = atoi(cmdtok().c_str());
            }
            else if(!(token = matchRegex(CmdStr, re_dnp_accumulator)).empty())
            {
                //  getvalue outage 1..6
                CtiTokenizer cmdtok(token);

                cmdtok();  //  move past "dnp"
                cmdtok();  //  move past "collection"

                _cmd["accumulatornumber"] = atoi(cmdtok().c_str());
            }
            else if(containsString(CmdStr, " crc"))
            {
                _cmd["dnp_crc"] = CtiParseValue(TRUE);
            }
        }
        else if(containsString(CmdStr, " daily"))
        {
            if( !(temp = matchRegex(CmdStr, re_daily_read)).empty() )
            {
                //  getvalue daily read
                //  getvalue daily read 12/12/2007
                //  getvalue daily read 12/12/2007 12/27/2007
                //  getvalue daily read channel n 12/12/2007 12/27/2007
                //  getvalue daily read detail 12/12/2007
                //  getvalue daily read detail channel n 12/12/2007
                //  getvalue daily read cancel
                //  getvalue daily reads
                //  getvalue daily reads 12/12/2007 12/27/2007

                _cmd["daily_read"] = true;

                if( containsString(temp, " cancel") )
                {
                    _cmd["daily_read_cancel"] = true;
                }

                if( containsString(temp, " reads") )
                {
                    _cmd["daily_reads"] = true;
                }

                if( containsString(temp, " detail ") )
                {
                    _cmd["daily_read_detail"] = true;
                }

                if( !(temp = matchRegex(temp, re_daterange)).empty() )
                {
                    CtiTokenizer cmdtok(temp);

                    _cmd["daily_read_date_begin"] = cmdtok();

                    if( !(temp = cmdtok()).empty() )
                    {
                        _cmd["daily_read_date_end"] = temp;
                    }
                }
            }
        }
        else if(containsString(CmdStr, " hourly"))
        {
            if( !(temp = matchRegex(CmdStr, re_hourly_read)).empty() )
            {
                //  getvalue hourly read
                //  getvalue hourly read cancel
                //  getvalue hourly read channel n
                //  getvalue hourly read 12/12/2007
                //  getvalue hourly read 12/12/2007 12/27/2007
                //  getvalue hourly read channel n 12/12/2007 12/27/2007

                _cmd["hourly_read"] = true;

                if( containsString(temp, " cancel") )
                {
                    _cmd["hourly_read_cancel"] = true;
                }

                if( !(temp = matchRegex(temp, re_daterange)).empty() )
                {
                    CtiTokenizer cmdtok(temp);

                    _cmd["hourly_read_date_begin"] = cmdtok();

                    if( !(temp = cmdtok()).empty() )
                    {
                        _cmd["hourly_read_date_end"] = temp;
                    }
                }
            }
        }
        else if(!(token = matchRegex(CmdStr, re_propcount)).empty())
        {
            flag |= CMD_FLAG_GV_PROPCOUNT;
        }
        else if(!(token = matchRegex(CmdStr, re_interval_demand)).empty())
        {
            flag |= CMD_FLAG_GV_DEMAND;
        }
        else if(!(token = matchRegex(CmdStr, re_load_runtime)).empty() || !(token = matchRegex(CmdStr, re_load_shedtime)).empty())
        {
            if( containsString(CmdStr, "runtime") )
            {
                flag |= CMD_FLAG_GV_RUNTIME;
            }
            else
            {
                flag |= CMD_FLAG_GV_SHEDTIME;
            }

            //beyond this point, runtime and shedtime are identical
            CtiTokenizer cmdtok(token);

            cmdtok(); // Move past "runtime"

            if(!(temp = cmdtok()).empty())
            {
                if(containsString(temp, "load") || containsString(temp, "relay"))
                {
                    _cmd["load"] = atoi(cmdtok().c_str());
                }
                else if(containsString(temp, "previous"))
                {
                    _cmd["previous_hours"] = atoi(cmdtok().c_str());
                }
                else
                {
                    //This shouldnt happen, but oh well?
                    cmdtok();
                }

                if(!(temp = cmdtok()).empty())
                {
                    if(containsString(temp, "load") || containsString(temp, "relay"))
                    {
                        _cmd["load"] = atoi(cmdtok().c_str());
                    }
                    else if(containsString(temp, "previous"))
                    {
                        _cmd["previous_hours"] = atoi(cmdtok().c_str());
                    }
                }
            }
        }
        else if(!(token = matchRegex(CmdStr, re_control_time)).empty() )
        {
            flag |= CMD_FLAG_GV_CONTROLTIME;

            CtiTokenizer cmdtok(token);

            cmdtok(); // Move past "controltime"
            cmdtok(); // Move past "remaining"

            if(!(temp = cmdtok()).empty())
            {
                if(containsString(temp, "load") || containsString(temp, "relay"))
                {
                    _cmd["load"] = atoi(cmdtok().c_str());
                }
            }
        }
        else if(!(token = matchRegex(CmdStr, re_xfmr_historical)).empty() )
        {
            flag |= CMD_FLAG_GV_XFMR_HISTORICAL_RUNTIME;

            CtiTokenizer cmdtok(token);

            cmdtok(); // Move past "historical"

            if(!(temp = cmdtok()).empty())
            {
                _cmd["load"] = atoi(cmdtok().c_str());
            }
        }
        else if(!(token = matchRegex(CmdStr, re_duty_cycle)).empty())
        {
            flag |= CMD_FLAG_GV_DUTYCYCLE;

            CtiTokenizer cmdtok(token);

            cmdtok(); // Move past "duty"
            cmdtok(); // Move past "cycle"

            if(!(temp = cmdtok()).empty())
            {
                _cmd["load"] = atoi(cmdtok().c_str());
            }
        }
        else if(containsString(CmdStr, " phase current"))
        {
            _cmd["phasecurrentread"] = CtiParseValue(TRUE);
        }
        else if(containsString(CmdStr, " instant line data"))
        {
            _cmd["instantlinedata"] = CtiParseValue(TRUE);
        }
        else
        {
            // Default Get Value request has been specified....
        }

        if(!(temp = matchRegex(CmdStr, re_rate)).empty())
        {
            flag &= ~CMD_FLAG_GV_RATEMASK;   // This one overrides...

            if(temp[temp.length() - 1] == 'a')  flag |= CMD_FLAG_GV_RATEA;
            if(temp[temp.length() - 1] == 'b')  flag |= CMD_FLAG_GV_RATEB;
            if(temp[temp.length() - 1] == 'c')  flag |= CMD_FLAG_GV_RATEC;
            if(temp[temp.length() - 1] == 'd')  flag |= CMD_FLAG_GV_RATED;
            if(temp[temp.length() - 1] == 't')  flag |= CMD_FLAG_GV_RATET;
        }

        if(!(token = matchRegex(CmdStr, re_channel)).empty())
        {
            if(!(temp = matchRegex(token, re_num)).empty())
            {
                _cmd["channel"] = CtiParseValue(atoi(temp.data()));
            }
        }
        if(!(token = matchRegex(CmdStr, re_offset)).empty())
        {
            flag |= CMD_FLAG_OFFSET;

            // What offset is needed now...
            if(!(temp = matchRegex(token, re_num)).empty())
            {
                _cmd["offset"] = atoi(temp.c_str());
            }
            else
            {
                _cmd["offset"] = 0;
            }
        }
        if(containsString(CmdStr, " ied"))      // Sourcing from CmdStr, which is the entire command string.
        {
            //  Read data from the ied port, not internal counters!
            flag |= CMD_FLAG_GV_IED;
        }
        if(containsString(CmdStr, " frozen"))      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_FROZEN;
        }
        if(containsString(CmdStr, " tou"))
        {
            flag |= CMD_FLAG_GV_TOU;
        }
        if(containsString(CmdStr, " peak"))
        {
            flag |= CMD_FLAG_GV_PEAK;
        }
        if(containsString(CmdStr, " temperature"))
        {
            flag |= CMD_FLAG_GV_TEMPERATURE;
        }
        if(containsString(CmdStr, " power"))
        {
            flag |= CMD_FLAG_GV_PFCOUNT;
        }
        if(containsString(CmdStr, " update"))      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_UPDATE;
        }
        if(containsString(CmdStr, " reverse"))
        {
            flag |= CMD_FLAG_GV_REVERSE;
    }
    }
    else
    {
        // Something went WAY wrong....
        CTILOG_ERROR(dout, "This better not ever be seen by mortals...");
    }

    setFlags(flag);

    doParseGetValueExpresscom(CmdStr);
}


void  CtiCommandParser::doParseGetStatus(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    UINT        flag = 0;

    std::string   temp2;
    std::string   token;

    static const boost::regex   re_lp("lp( channel " + str_num + ")?");
    static const boost::regex   re_eventlog("eventlog(s)?");

    static const boost::regex   re_offset("off(set)? *" + str_num);

    static const boost::regex   re_sele("select");

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "getstatus")
    {
        if(!(token = matchRegex(CmdStr, re_lp)).empty())
        {
            flag |= CMD_FLAG_GS_LOADPROFILE;

            //  was an offset specified?
            if(!(temp2 = matchRegex(token, re_num)).empty())
            {
                _cmd["loadprofile_offset"] = atoi(temp2.c_str());
            }
        }
        if(!(token = matchRegex(CmdStr, re_offset)).empty())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_OFFSET;

            // What offset is needed now...
            if(!(temp2 = matchRegex(token, re_num)).empty())
            {
                _cmd["offset"] = atoi(temp2.c_str());
            }
        }
        if(containsRegex(CmdStr, re_eventlog))
        {
            _cmd["eventlog"] = CtiParseValue(true);
        }
        if(containsString(CmdStr, " disc"))
        {
            flag |= CMD_FLAG_GS_DISCONNECT;
        }
        if(containsString(CmdStr, " err"))
        {
            flag |= CMD_FLAG_GS_ERRORS;
        }
        if(containsString(CmdStr, " inter"))
        {
            flag |= CMD_FLAG_GS_INTERNAL;
        }
        if(containsString(CmdStr, " extern"))
        {
            flag |= CMD_FLAG_GS_EXTERNAL;
        }
        if(containsString(CmdStr, " ied"))
        {
            flag |= CMD_FLAG_GS_IED;

            if(containsString(CmdStr, " ied link"))
            {
                flag |= CMD_FLAG_GS_LINK;
            }
            else if(containsString(CmdStr, " ied dnp"))
            {
                _cmd["ied_dnp"] =  CtiParseValue(TRUE);
            }
        }
        if(containsString(CmdStr, " tou"))
        {
            flag |= CMD_FLAG_GS_TOU;
        }
        else
        {
            // Default GetStatus request has been specified....
        }


        if(containsString(CmdStr, " freeze"))
        {
            _cmd["freeze"] = true;
        }
        if(containsString(CmdStr, " upd"))         // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_UPDATE;
        }
    }
    else
    {
        // Something went WAY wrong....
        CTILOG_ERROR(dout, "This better not ever be seen by mortals...");
    }

    setFlags(flag);
}

void  CtiCommandParser::doParseControl(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    UINT        flag   = 0;
    UINT        iValue = 0;

    CHAR        tbuf[80];

    std::string   temp2;
    std::string   token;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "control")
    {
        if(containsString(CmdStr, " delay"))
        {
            std::string   valStr;

            if(!(temp2 = matchRegex(CmdStr, " delay ?time " + str_num)).empty())
            {
                if(!(valStr = matchRegex(temp2, re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["delaytime_sec"] = CtiParseValue( iValue * 60 );
                }
            }

            if(!(temp2 = matchRegex(CmdStr, " delay ?until [0-9]?[0-9]:[0-9][0-9]")).empty())
            {
                INT hh = 0;
                INT mm = 0;

                if(!(valStr = matchRegex(temp2, "[0-9]?[0-9]:")).empty())
                {
                    hh = atoi(valStr.c_str());
                }

                if(!(valStr = matchRegex(temp2, ":[0-9][0-9]")).empty())
                {
                    mm = atoi(valStr.c_str() + 1);
                }


                iValue = CtiTime(hh, mm).seconds() - CtiTime::now().seconds();

                if(iValue > 0)
                {
                    _cmd["delaytime_sec"] = CtiParseValue( iValue );
                }
                else
                {
                    CTILOG_ERROR(dout, "Invalid delay ("<< iValue <<")");
                }
            }
        }

        if(containsString(CmdStr, " open"))            // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_OPEN;

            _snprintf(tbuf, sizeof(tbuf), "OPEN");
        }
        else if(containsString(CmdStr, " close"))      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_CLOSE;
            _snprintf(tbuf, sizeof(tbuf), "CLOSE");
        }
        else if(containsString(CmdStr, " disc"))       // Sourcing from CmdStr, which is the entire command string.
        {
            /* MUST LOOK FOR THIS FIRST! */
            flag |= CMD_FLAG_CTL_DISCONNECT;
            _snprintf(tbuf, sizeof(tbuf), "DISCONNECT");
        }
        else if(containsString(CmdStr, " conn"))       // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_CONNECT;
            _snprintf(tbuf, sizeof(tbuf), "CONNECT");

            if( containsString(CmdStr, " arm") )
            {
                _cmd["arm"] = true;
            }
        }
        else if(containsString(CmdStr, " restore"))    // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_RESTORE;
            _snprintf(tbuf, sizeof(tbuf), "RESTORE");
        }
        else if(containsString(CmdStr, " term"))       // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_TERMINATE;
            _snprintf(tbuf, sizeof(tbuf), "TERMINATE");

            // Currently for Expresscom only. This supports new style terminate while not changing old style terminate
            if(!(token = matchRegex(CmdStr, " period " + str_num)).empty())
            {
                if(!(temp2 = matchRegex(token, re_num)).empty())
                {
                    INT _num = atoi(temp2.c_str());
                    _cmd["cycle_period"] = CtiParseValue( _num );
                }
            }
        }
        else if(containsString(CmdStr, " shed"))       // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_SHED;

            if(!(token = matchRegex(CmdStr, "shed *" + str_floatnum + " *[hms]?( |$)")).empty())      // Sourcing from CmdStr, which is the entire command string.
            {
                double shedTime = getDurationInSeconds(token);

                if(shedTime == -1.0)
                {
                    CTILOG_WARN(dout, "Command Parameter Assumed.  Shed for 1 hour.");
                    shedTime = 360;
                }

                _snprintf(tbuf, sizeof(tbuf),"SHED %dS", (INT)shedTime);

                _cmd["shed"] = CtiParseValue( shedTime );
            }
            else
            {
                _snprintf(tbuf, sizeof(tbuf), "SHED");
            }

            if(!(token = matchRegex(CmdStr,  " rand(om)? *" + str_num)).empty())
            {
                if(!(temp2 = matchRegex(token, re_num)).empty())
                {
                    INT _num = atoi(temp2.c_str());
                    _cmd["shed_rand"] = CtiParseValue( _num );
                }
            }
        }
        else if(!(token = matchRegex(CmdStr, " cycle " + str_num)).empty())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_CYCLE;

            if(!(temp2 = matchRegex(token, re_num)).empty())
            {
                iValue = atoi(temp2.c_str());
            }
            else
            {
                // Something went kinda wrong....
                CTILOG_WARN(dout, "Command Parameter Assumed.  Cycle control at 50 percent cycle.");
                iValue = 50;
            }

            if(!(token = matchRegex(CmdStr, " period " + str_num)).empty())
            {
                if(!(temp2 = matchRegex(token, re_num)).empty())
                {
                    INT _num = atoi(temp2.c_str());
                    _cmd["cycle_period"] = CtiParseValue( _num );
                }
            }

            if(!(token = matchRegex(CmdStr, " count " + str_num)).empty())
            {
                if(!(temp2 = matchRegex(token, re_num)).empty())
                {
                    INT _num = atoi(temp2.c_str());
                    _cmd["cycle_count"] = CtiParseValue( _num );
                }
            }

            _cmd["cycle"] = CtiParseValue( (iValue) );
            _snprintf(tbuf, sizeof(tbuf), "CYCLE %u%%", iValue);
        }
        else if( containsString(CmdStr, " latch") )
        {
            if(!(token = matchRegex(CmdStr, " latch relays? ([ab]+|none)")).empty())
            {
                string latch_relays;

                removeRegexAllMatches(token, " latch relays? ");

                if( containsString(token, "a") )
                {
                    //  note that it's not just a naked "a"
                    latch_relays.append("(a)");
                }
                if( containsString(token, "b") )
                {
                    //  note that it's not just a naked "b"
                    latch_relays.append("(b)");
                }
                if( containsString(token, "none") )
                {
                    latch_relays.assign("none");
                }

                _cmd["latch_relays"] = CtiParseValue(latch_relays.data());
            }
        }

        if(containsString(CmdStr, " sbo_selectonly"))          // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["sbo_selectonly"] = CtiParseValue(TRUE);
        }
        if(containsString(CmdStr, " sbo_operate"))             // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["sbo_operate"] = CtiParseValue(TRUE);
        }


        if(flag) _actionItems.push_back(tbuf);                      // If anything was set, make sure someone can be informed

        if(!(token = matchRegex(CmdStr, "off(set)? *" + str_num)).empty() )            // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_OFFSET;

            // What offset is needed now...
            if(!(temp2 = matchRegex(token, re_num)).empty())
            {
                _cmd["offset"] = atoi(temp2.c_str());
            }
            else
            {
                _cmd["offset"] = 0;
            }
        }

        /*
         *  Try to find out if a relay has been specified for the control operation!
         */
        if(containsString(CmdStr, " relay") || containsString(CmdStr, " load"))
        {
            if(!(token = matchRegex(CmdStr, "(( relay)|( load)) [0-9]+( *, *[0-9]+)*")).empty())
            {
                INT i;
                INT mask = 0;

                for(i = 0; i < 10; i++)
                {
                    _snprintf(tbuf, sizeof(tbuf), "%d", i+1);
                    if(!(temp2 = matchRegex(token, tbuf)).empty())
                    {
                        mask |= (0x01 << i);
                    }
                }

                if(mask)
                {
                    _cmd["relaymask"] = CtiParseValue( mask );
                }
            }
            if(!(token = matchRegex(CmdStr, "relay next")).empty())
            {
                _cmd["relaynext"] = CtiParseValue( TRUE );
            }
        }

        if(containsString(CmdStr, " truecycle"))
        {
            _cmd["xctruecycle"] = CtiParseValue( TRUE );
        }

        if(containsString(CmdStr, " delta"))
        {
            _cmd["xcdelta"] = CtiParseValue( TRUE );    // Temperatures are delta offsets
        }

        if(containsString(CmdStr, " noramp"))
        {
            _cmd["xcnoramp"] = CtiParseValue( TRUE );
        }

        if(containsString(CmdStr, " celsius"))
        {
            _cmd["xccelsius"] = CtiParseValue( TRUE );  // Temperatures are celsius
        }
        if(containsString(CmdStr, "froz"))         // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_FROZEN;
        }
        if(containsString(CmdStr, "upd"))          // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_UPDATE;
        }
        if(containsString(CmdStr, " gold"))
        {
            if(!(token = matchRegex(CmdStr, "gold [0-9]")).empty())
            {
                CtiTokenizer addr_tok( token );

                addr_tok();   // Get us past "gold"

                _cmd["gold"] = CtiParseValue(atoi(addr_tok().c_str()));
            }
        }
        if(containsString(CmdStr, " silver"))
        {
            if(!(token = matchRegex(CmdStr, "silver [0-9][0-9]")).empty())
            {
                CtiTokenizer addr_tok( token );

                addr_tok();   // Get us past "silver"

                _cmd["silver"] = CtiParseValue(atoi(addr_tok().c_str()));
            }
        }
    }

    else
    {
        // Something went WAY wrong....
        CTILOG_ERROR(dout, "This better not ever be seen by mortals...");
    }

    setFlags(flag);

    doParseControlExpresscom(CmdStr);
    doParseControlSA(CmdStr);
}


void  CtiCommandParser::doParsePutValue(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    UINT        flag = 0;
    UINT        offset = 0;

    std::string   temp2;
    std::string   token;

    static const boost::regex   re_reading         ("reading " + str_floatnum);
    static const boost::regex   re_kyzoffset       ("kyz *" + str_num);   //  if there's a kyz offset specified
    static const boost::regex   re_analog_offset   ("analog " + str_num + " -?" + str_floatnum);
    static const boost::regex   re_analog_no_offset("analog value -?" + str_floatnum);
    static const boost::regex   re_asciiraw        ("asciiraw " + str_quoted_token);
    static const boost::regex   re_hexraw          ("hexraw " + str_hexnum);


    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "putvalue")
    {
        if(containsString(CmdStr, " kyz"))
        {
            _cmd["kyz"] = CtiParseValue(true);

            //  if a point offset has been specified
            if(!(token = matchRegex(CmdStr, re_kyzoffset)).empty())
            {
                offset = atoi(matchRegex(token, re_num).c_str());

                _cmd["kyz_offset"] = CtiParseValue(offset);
            }

            if(!(token = matchRegex(CmdStr, re_reading)).empty())
            {
                _cmd["kyz_reading"]   = CtiParseValue(atof(matchRegex(token, re_floatnum).c_str()));
            }
        }
        if(containsString(CmdStr, " analog"))
        {
            if(!(token = matchRegex(CmdStr, re_analog_offset)).empty())
            {
                _cmd["analog"] = CtiParseValue(true);

                CtiTokenizer cmdtok(token);

                cmdtok();  //  move past "analog"

                _cmd["analogoffset"] = CtiParseValue( atoi(cmdtok().c_str()) );
                _cmd["analogvalue"]  = CtiParseValue( atof(cmdtok().c_str()) );
                if( containsRegex(token, str_floatnum_only) )
                {
                    _cmd["analogfloatvalue"] = _cmd["analogvalue"];
                }
            }
            if(!(token = matchRegex(CmdStr, re_analog_no_offset)).empty())
            {
                _cmd["analog"] = CtiParseValue(true);

                CtiTokenizer cmdtok(token);

                cmdtok();  //  move past "analog"
                cmdtok();  //  move past "value"

                _cmd["analogvalue"]  = CtiParseValue( atof(cmdtok().c_str()) );
            }
        }
        if(containsString(CmdStr, " reset"))
        {
            _cmd["reset"] = CtiParseValue(true);

            if( containsString(CmdStr, " tou") )
            {
                _cmd["tou"] = CtiParseValue(true);

                if( containsString(CmdStr, " zero") )
                {
                    _cmd["tou_zero"] = CtiParseValue(true);
                }
            }
        }
        if(containsString(CmdStr, " ied"))
        {
            _cmd["ied"] = CtiParseValue(true);
        }
        if(containsString(CmdStr, " power"))
        {
            _cmd["power"] = CtiParseValue(true);
        }
        if(containsString(CmdStr, " asciiraw"))
        {
            if(!(token = matchRegex(CmdStr, re_asciiraw)).empty())
            {
                _cmd["asciiraw"] = CtiParseValue(token.substr(10, token.length() - 11), -1 );
            }
        }
        if(containsString(CmdStr, " hexraw"))
        {
            if(!(token = matchRegex(CmdStr, re_hexraw)).empty())
            {
                //  exclude /hexraw 0x/, leave just the hex digits
                _cmd["hexraw"] = CtiParseValue(token.substr(9, token.length() - 9), -1 );
            }
        }
        if(containsString(CmdStr, " application-id"))
        {
            _cmd["application-id"] = CtiParseValue(true);
        }

        setFlags(flag);
    }
    else
    {
        // Something went WAY wrong....
        CTILOG_ERROR(dout, "This better not ever be seen by mortals...");
    }
}


void  CtiCommandParser::doParsePutStatus(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    std::string   temp2;
    std::string   token;
    static const boost::regex   re_offsetvalue("offset " + str_num + " value -?" + str_num);

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "putstatus")
    {
        INT type = getiValue("type");

        switch( type )
        {
            case ProtocolVersacomType:
            {
                doParsePutStatusVersacom(CmdStr);
                break;
            }
            case ProtocolExpresscomType:
            {
                doParsePutStatusExpresscom(CmdStr);
                break;
            }
            case ProtocolFisherPierceType:
            {
                doParsePutStatusFisherP(CmdStr);
                break;
            }
            case ProtocolEmetconType:
            {
                doParsePutStatusEmetcon(CmdStr);
                break;
            }
            case ProtocolSADigitalType:
            case ProtocolGolayType:
            case ProtocolSA105Type:
            case ProtocolSA205Type:
            case ProtocolSA305Type:
            {
                CTILOG_ERROR(dout, "Putstatus not supported for this device type");
                break;
            }
            default:
            {
                CTILOG_ERROR(dout, "Putstatus to unknown device type ");
                break;
            }
        }

        if(containsString(CmdStr, "offset "))
        {
            if(!(token = matchRegex(CmdStr, re_offsetvalue)).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();

                _cmd["offset"] = CtiParseValue( atoi(cmdtok().c_str()) );
                cmdtok();
                _cmd["value"]  = CtiParseValue( atoi(cmdtok().c_str()) );
            }
        }
        if(containsString(CmdStr, " reset"))
        {
            if(containsString(CmdStr, " alarms"))
            {
                setFlags(getFlags() | CMD_FLAG_PS_RESET_ALARMS);
            }
            else
            {
                setFlags(getFlags() | CMD_FLAG_PS_RESET);
            }
        }
        if(containsString(CmdStr, " freeze"))
        {
            if(containsString(CmdStr, " one"))
            {
                _cmd["freeze"] = CtiParseValue(1);
            }
            else if(containsString(CmdStr, " two"))
            {
                _cmd["freeze"] = CtiParseValue(2);
            }
            else
            {
                _cmd["freeze"] = CtiParseValue(0);
            }
        }
        if(containsString(CmdStr, " critical"))
        {
            static const boost::regex
                re_touCriticalPeak("tou critical peak (cancel|rate [a-d] until " + str_time + ")");

            if(!(token = matchRegex(CmdStr, re_touCriticalPeak)).empty())
            {
                _cmd["tou_critical_peak"] = true;

                if ( containsString(CmdStr, " cancel") )
                {
                    _cmd["tou_critical_peak_cancel"] = true;
                }
                else
                {
                    CtiTokenizer cmdtok(token);
                    cmdtok();   // tou
                    cmdtok();   // critical
                    cmdtok();   // peak
                    cmdtok();   // rate
                    _cmd["tou_critical_peak_rate"] = cmdtok();
                    cmdtok();   // until

                    std::string untilTime = cmdtok();

                    std::vector<std::string>    timeComponents;
                    boost::split( timeComponents, untilTime, is_char{':'}, boost::token_compress_on );

                    // regex guarantees at 2 or 3 elements in the vector -- we only care about the first 2
                    _cmd["tou_critical_peak_stop_time_hour"]   = CtiParseValue( atoi( timeComponents[0].c_str() ) );
                    _cmd["tou_critical_peak_stop_time_minute"] = CtiParseValue( atoi( timeComponents[1].c_str() ) );
                }
            }
        }
    }
    else
    {
        // Something went WAY wrong....
        CTILOG_ERROR(dout, "Unexpected command \""<< token <<"\", expected \"putstatus\"");
    }
}

void  CtiCommandParser::doParseGetConfig(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    std::string   temp2;
    std::string   token;
    UINT        flag   = 0;
    static const boost::regex    re_rolenum("role *" + str_num);
    static const boost::regex    re_rawcmd("raw (func(tion)? )?start ?= ?" + str_hexnum + "( " + str_num + ")?");
    static const boost::regex    re_interval("interval(s| lp| li)");  //  match "intervals", "interval lp", and "interval li"
    static const boost::regex    re_time_word("time( |$)");  //  only match "time" as a single word
    static const boost::regex    re_multiplier("mult(iplier)?( kyz *" + str_num + ")?");
    static const boost::regex    re_address("address (group|uniq)");
    static const boost::regex    re_lp_channel(" lp channel " + str_num);
    static const boost::regex    re_meter_parameters("(centron|meter) (ratio|parameters)");
    static const boost::regex    re_tou_schedule("tou schedule [0-9]");
    static const boost::regex    re_dnp("dnp " + str_num);
    static const boost::regex    re_dnp_address("dnp address");

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "getconfig")
    {
        if(containsString(CmdStr, " model"))
        {
            _cmd["model"] = CtiParseValue( "TRUE" );
        }
        if(containsString(CmdStr, " install"))
        {
            _cmd["install"] = CtiParseValue("TRUE");

            if(!(token = matchRegex(CmdStr, "install +[a-z0-9]* *[a-z0-9]*")).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "install"

                _cmd["installvalue"] = CtiParseValue(cmdtok());
            }
        }
        if(containsString(CmdStr, " behavior"))
        {
            if( !(token = matchRegex(CmdStr, "behavior +[a-z0-9]+")).empty() )
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "behavior"

                _cmd["behavior"] = CtiParseValue(cmdtok());
            }
        }
        if(containsString(CmdStr, " options"))
        {
            _cmd["options"] = CtiParseValue( "TRUE" );
        }
        if(containsString(CmdStr, " freeze"))
        {
            _cmd["freeze"] = CtiParseValue( "TRUE" );
        }
        if(containsString(CmdStr, " tou"))
        {
            _cmd["tou"] = CtiParseValue( "TRUE" );

            if(!(token = matchRegex(CmdStr, re_tou_schedule)).empty())
            {
                _cmd["tou_schedule"] = CtiParseValue(atoi(token.data() + 13));
            }
        }
        if(containsString(CmdStr, " holiday"))
        {
            _cmd["holiday"] = CtiParseValue(true);
        }
        if(containsString(CmdStr, " address"))
        {
            _cmd["addressing"] = CtiParseValue("TRUE");

            if(!(token = matchRegex(CmdStr, re_address)).empty())
            {
                if(containsString(token, "group"))
                {
                    _cmd["address_group"] = CtiParseValue(TRUE);
                }
                if(containsString(token, "uniq"))
                {
                    _cmd["address_unique"] = CtiParseValue(TRUE);
                }
            }
        }
        if(containsString(CmdStr, " channels"))
        {
            _cmd["channels"] = CtiParseValue(TRUE);
        }
        if(containsString(CmdStr, " codes"))
        {
            _cmd["codes"] = CtiParseValue(TRUE);
        }
        if(containsString(CmdStr, " role"))
        {
            if(!(token = matchRegex(CmdStr, re_rolenum)).empty())
            {
                _cmd["rolenum"] = CtiParseValue( atoi(matchRegex(token, re_num).c_str()) );
            }
        }
        if(containsString(CmdStr, " mult"))
        {
            _cmd["multiplier"] = CtiParseValue(TRUE);

            if(!(token = matchRegex(CmdStr, re_multiplier)).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  multiplier
                cmdtok();  //  kyz
                _cmd["multchannel"] = CtiParseValue(atoi(cmdtok().c_str()));
            }
        }
        if(containsString(CmdStr, " phasedetect"))
        {
            _cmd["phasedetect"] = CtiParseValue(TRUE);
            if(containsString(CmdStr, "read"))
            {
                _cmd["phasedetectread"] = CtiParseValue(TRUE);
            }
            if(containsString(CmdStr, "archive"))
            {
                _cmd["phasedetectarchive"] = CtiParseValue(TRUE);
            }
        }
        if(containsString(CmdStr, " raw"))
        {
            if(!(token = matchRegex(CmdStr, re_rawcmd)).empty())
            {
                CtiTokenizer cmdtok(token);
                std::string rawData;

                //  go past "raw"
                cmdtok(" =");

                //  temp2 will either contain "function" or "start"
                temp2 = cmdtok(" =");

                if(containsString(temp2, "func"))
                {
                    //  if it's "function", make a note and grab the next word
                    _cmd["rawfunc"] = CtiParseValue("TRUE");
                    temp2 = cmdtok(" =");
                }

                //  move past "start" to the first hex digit
                temp2 = cmdtok(" =");

                //  get the start address
                _cmd["rawloc"] = CtiParseValue( strtol(temp2.c_str(), NULL, 16) );

                //  get the length
                temp2 = cmdtok(" =");
                //  if there's length specified
                if( !temp2.empty() )
                {
                    _cmd["rawlen"] = CtiParseValue( atoi( temp2.c_str() ) );
                }
            }
        }
        if(containsString(CmdStr, " lp"))
        {
            if(!(token = matchRegex(CmdStr, re_lp_channel)).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  lp
                cmdtok();  //  channel
                _cmd["lp_channel"] = CtiParseValue(atoi(cmdtok().c_str()));
            }
        }
        if(containsString(CmdStr, " time"))
        {
            if(!(token = matchRegex(CmdStr, re_time_word)).empty())
            {
                _cmd["time"] = CtiParseValue("TRUE");

                if(containsString(CmdStr, "sync"))
                {
                    _cmd["sync"] = CtiParseValue("TRUE");
                }
            }
        }
        if(containsString(CmdStr, " ied"))
        {
            _cmd["ied"] = CtiParseValue("TRUE");
            if(containsString(CmdStr, " dnp"))
            {
                _cmd["dnp"] = CtiParseValue("TRUE");

                if(!(token = matchRegex(CmdStr, re_dnp)).empty())
                {
                    //  was an offset specified?
                    if(!(temp2 = matchRegex(token, re_num)).empty())
                    {
                        _cmd["start address"] = atoi(temp2.data());
                    }
                }
            }
        }
        if(containsString(CmdStr, " dnp address"))
        {
            _cmd["dnp address"] = true;
        }
        if(containsString(CmdStr, " scan"))
        {
            _cmd["scan"] = CtiParseValue("TRUE");
        }
        if(containsString(CmdStr, " thresholds"))
        {
            _cmd["thresholds"] = CtiParseValue("TRUE");
        }
        if(containsString(CmdStr, " interval"))
        {
            if(!(token = matchRegex(CmdStr, re_interval)).empty())
            {
                CtiTokenizer cmdtok(token);
                //  get "interval(s?)"
                temp2 = cmdtok();

                //  if it's just "interval," get the next token
                if( temp2 == "interval" )
                {
                    //  "li" or "lp"
                    temp2 = cmdtok();
                }

                //  so this is either "intervals," "li," or "lp"
                _cmd["interval"] = temp2;
            }
        }
        if(containsString(CmdStr, " disc"))
        {
            _cmd["disconnect"] = CtiParseValue("TRUE");
        }
        if(containsString(CmdStr, " centron") ||
           containsString(CmdStr, " meter"))
        {
            if(!(token = matchRegex(CmdStr, re_meter_parameters)).empty())
            {
                CtiTokenizer cmdtok(token);

                //  check if it's the deprecated "centron" form
                if( cmdtok() == "centron" )
                {
                    _cmd["centron_parameters"] = true;
                }

                temp2 = cmdtok();  //  grab the token we care about

                if( temp2 == "ratio" )
                {
                    _cmd["transformer_ratio"] = true;
                }
                if( temp2 == "parameters" )
                {
                    _cmd["meter_parameters"] = true;
                }
            }
        }
        if( containsString(CmdStr, "water meter read interval") )
        {
            _cmd["water_meter_read_interval"] = true;
        }
        if( containsString(CmdStr, "load profile allocation") )
        {
            _cmd["load_profile_allocation"] = true;
        }
        if(containsString(CmdStr, " update"))
        {
            flag |= CMD_FLAG_UPDATE;
        }
        if( containsString(CmdStr, " configuration") )
        {
            _cmd["configuration"] = true;
        }
        if( containsString(CmdStr, " phaseloss threshold") )
        {
            _cmd["phaseloss_threshold"] = CtiParseValue("TRUE");
        }
        if( containsString(CmdStr, " alarm_mask") )
        {
            _cmd["alarm_mask"] = CtiParseValue("TRUE");
        }

        if( containsRegex(CmdStr, " voltage profile( state)?") )
        {
            // getconfig voltage profile
            // getconfig voltage profile state

            _cmd["voltage_profile"] = true;

            if( containsString(CmdStr, " state") )
            {
                _cmd["voltage_profile_state"] = true;
            }
        }
        if( containsString(CmdStr, " availablechannels") )
        {
            _cmd["available_channels"] = CtiParseValue("TRUE");
        }

        setFlags(flag);
    }
    else
    {
        // Something went WAY wrong....
        CTILOG_ERROR(dout, "Unexpected command \""<< token <<"\", expected \"getconfig\"");
    }
}

void  CtiCommandParser::doParsePutConfig(const string &_CmdStr)
{
    static const boost::regex re_tou("tou [0-9]+( schedule [0-9]+( [a-z]/[0-9]+:[0-9]+)*)*( default [a-z])?");

    std::string CmdStr(_CmdStr);
    std::string   temp2;
    std::string   token;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "putconfig")
    {
        INT type = getiValue("type");

        if(containsString(CmdStr, " timesync"))
        {
            _cmd["timesync"] = CtiParseValue("TRUE");
        }

        if(containsString(CmdStr, " precanned"))
        {
            if(!(token = matchRegex(CmdStr, "precanned table [0-9]+( read interval [0-9]+)?")).empty())   // get the template name...
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "precanned"
                cmdtok();  //  go past "table"

                _cmd["precanned_table"] = atoi(cmdtok().c_str());

                if( !cmdtok().empty() )  //  does "read" exist?
                {
                    cmdtok();  //  go past "interval"
                    _cmd["read_interval"] = atoi(cmdtok().c_str());
                }
            }
        }

        if(containsString(CmdStr, " unsolicited"))
        {
            if(containsString(CmdStr, " enable"))
            {
                _cmd["unsolicited"] = CtiParseValue(true);
            }
            if(containsString(CmdStr, " disable"))
            {
                _cmd["unsolicited"] = CtiParseValue(false);
            }
        }

        // Template should be global.
        if(!(token = matchRegex(CmdStr, "template " + str_quoted_token)).empty())
        {
            _cmd["template"] = CtiParseValue(token.substr(10, token.length() - 11));

            std::string sistr;

            if(containsString(CmdStr, " service in"))
            {
                sistr = "service in";
            }
            _cmd["templateinservice"] = CtiParseValue( sistr );
        }

        if(containsString(CmdStr, " install"))
        {
            _cmd["install"] = CtiParseValue(TRUE);

            if(!(token = matchRegex(CmdStr, "install +[a-z0-9]* *[a-z0-9]*")).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "install"

                _cmd["installvalue"] = CtiParseValue(cmdtok());

                token = cmdtok();
                if( containsString(token, "force") )
                {
                    _cmd["force"] = CtiParseValue(true);
                }
                else if( containsString(token, "verify") )
                {
                    _cmd["verify"] = CtiParseValue(true);
                }
            }
        }
        else if(containsString(CmdStr, " behavior"))
        {
            if( !(token = matchRegex(CmdStr, "behavior +[a-z0-9]+")).empty() )
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "behavior"

                _cmd["behavior"] = CtiParseValue(cmdtok());
            }
        }
        else if(containsString(CmdStr, " tou"))
        {
            string tou_schedule;

            //  indicate that we at least tried to parse the TOU stuff
            _cmd["tou"] = CtiParseValue(true);

            if(!(tou_schedule = matchRegex(CmdStr, re_tou)).empty())
            {
                //  tou [0-9]+ (schedule [0-9]+( [a-z]/[0-9]+:[0-9]+)*)* default [a-z]
                CtiTokenizer tou_tok(tou_schedule);

                token = tou_tok();  //  tou

                _cmd["tou_days"] = CtiParseValue(tou_tok());

                token = tou_tok();

                int schedulenum = 0;
                while( token == "schedule" )
                {
                    string schedule_name;
                    schedule_name.assign("tou_schedule_");
                    schedule_name.append(CtiNumStr(schedulenum).zpad(2));

                    //  even if there are no rates supplied, we did successfully parse it
                    _cmd[schedule_name.data()] = CtiParseValue(atoi(tou_tok().data()));

                    token = tou_tok();

                    int changenum = 0;
                    while( containsRegex(token, "[a-z]/[0-9]+:[0-9]+") )
                    {
                        string change_name;
                        change_name.assign(schedule_name);
                        change_name.append("_");
                        change_name.append(CtiNumStr(changenum).zpad(2));

                        _cmd[change_name.data()] = CtiParseValue(token);

                        token = tou_tok();

                        changenum++;
                    }

                    schedulenum++;
                }

                if( token == "default" )
                {
                    _cmd["tou_default"] = CtiParseValue(tou_tok());
                }
            }
            if( containsString(CmdStr, " enable") )
            {
                _cmd["tou_enable"] = CtiParseValue(true);
            }
            if( containsString(CmdStr, " disable") )
            {
                _cmd["tou_disable"] = CtiParseValue(true);
            }
        }
        else if( !(token = matchRegex(CmdStr, "voltage profile (enable|disable|demandinterval [0-9]+ lpinterval [0-9]+)")).empty())
        {
            // putconfig voltage profile demandinterval 1234 loadinterval 123
            // putconfig voltage profile enable|disable

            _cmd["voltage_profile"] = true;

            CtiTokenizer cmdtok(token);

            cmdtok(); // voltage
            cmdtok(); // profile

            if( containsString(CmdStr, " enable") )
            {
                _cmd["voltage_profile_enable"] = true;
            }
            else if( containsString(CmdStr, " disable") )
            {
                _cmd["voltage_profile_enable"] = false;
            }
            else
            {
                cmdtok(); // demandinterval

                _cmd["demand_interval_minutes"] = atoi(cmdtok().c_str());

                cmdtok(); // lpinterval

                _cmd["load_profile_interval_minutes"] = atoi(cmdtok().c_str());
            }
        }

        if( containsRegex(CmdStr, " water meter read interval( |$)") )
        {
            _cmd["water_meter_read_interval"] = true;
            if( ! (token = matchRegex(CmdStr, "interval [0-9]+ ?[hm]")).empty())
            {
                int duration = atoi(matchRegex(token, str_num).c_str());
                if (*token.rbegin() == 'h')     // if given hours, convert to minutes (tokens last char either 'm' or 'h'
                {
                    duration *= 60;
                }
                duration *= 60;                 // convert minutes to seconds

                _cmd["read_interval_duration_seconds"] = CtiParseValue(duration);
            }
        }

        if(containsString(CmdStr, " der "))
        {
            // putconfig der {hex-string-payload}

            if( !(token = matchRegex(CmdStr, "der +[0-9a-fA-F]*")).empty() )
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "der"

                _cmd["der"] = CtiParseValue(cmdtok());
            }
        }

        switch( type )
        {
        case ProtocolVersacomType:              // For putconfig, we may not know who we are talking to.  Decode for both.
            {
                doParsePutConfigVersacom(CmdStr);
                break;
            }
        case ProtocolExpresscomType:
            {
                doParsePutConfigExpresscom(CmdStr);
                break;
            }
        case ProtocolSA105Type:
        case ProtocolSA205Type:
        case ProtocolSA305Type:
            {
                doParsePutConfigSA(CmdStr);
                break;
            }
        case ProtocolSADigitalType:
        case ProtocolGolayType:
        case ProtocolFisherPierceType:
            {
                CTILOG_ERROR(dout, "Putconfig not supported for this device type");
                break;
            }
        case ProtocolEmetconType:
            {
                doParsePutConfigEmetcon(CmdStr);
                break;
            }
        default:
            {
                CTILOG_ERROR(dout, "Putconfig to unknown device type ");
                break;
            }
        }
    }
    else
    {
        // Something went WAY wrong....
        CTILOG_ERROR(dout, "Unexpected command \""<< token <<"\", expected \"putconfig\"");
    }
}

void  CtiCommandParser::doParseScan(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    std::string   token;
    UINT        flag   = 0;
    static const boost::regex    re_loadprofile("loadprofile( +channel +[1-4])?( +block +[0-9]+)?");

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "scan")
    {
        //  this should probably just be a bunch of if statements, no else-if...
        //    the tokenizer should just tokenize, not prioritize

        if(containsString(CmdStr, " general"))             // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["scantype"] = CtiParseValue( ScanRateGeneral );
        }
        else if(containsString(CmdStr, " integrity"))      // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["scantype"] = CtiParseValue( ScanRateIntegrity );
        }
        else if(containsString(CmdStr, " accumulator"))    // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["scantype"] = CtiParseValue( ScanRateAccum );
        }
        else if(containsString(CmdStr, " status"))         // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["scantype"] = CtiParseValue( ScanRateStatus );
        }
        else if(!(token = matchRegex(CmdStr, re_loadprofile)).empty())      // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["scantype"] = CtiParseValue( ScanRateLoadProfile );

            CtiTokenizer lp_tok(token);

            lp_tok();  //  pull "loadprofile"

            token = lp_tok();

            if( token == "channel" )
            {
                token = lp_tok();

                _cmd["scan_loadprofile_channel"] = CtiParseValue(atoi(token.c_str()));

                token = lp_tok();
            }

            if( token == "block" )
            {
                token = lp_tok();

                _cmd["scan_loadprofile_block"] = CtiParseValue(atoi(token.c_str()));
            }
        }
    }

    if(containsString(CmdStr, " update"))
    {
        flag |= CMD_FLAG_UPDATE;
    }
    if(containsString(CmdStr, " frozen"))
    {
        flag |= CMD_FLAG_FROZEN;
    }
    setFlags(flag);


}

CtiClientRequest_t CtiCommandParser::getCommand() const
{
    return _command;
}

void     CtiCommandParser::setCommand(CtiClientRequest_t command)
{
    _command = command;
}

UINT     CtiCommandParser::getFlags() const
{
    return _flags;
}

void     CtiCommandParser::setFlags(UINT flags)
{
    _flags = flags;
}

UINT     CtiCommandParser::getOffset() const
{
    return getiValue("offset",-1);
}

bool  CtiCommandParser::isKeyValid(const string &key) const
{
    return _cmd.count(key);
}

INT      CtiCommandParser::getiValue(const string &key, INT valifnotfound) const
{
    INT val = valifnotfound;

    if(isKeyValid(key.c_str()))
    {
        CtiParseValue& pv = CtiParseValue(); // = _cmd["command"];
        map_itr_type itr;
        itr = _cmd.find(key.c_str());
        if( itr != _cmd.end() )
        {
            pv = (*itr).second;
        }
        val = pv.getInt();
    }

    return val;
}

DOUBLE   CtiCommandParser::getdValue(const string &key, DOUBLE valifnotfound) const
{
    DOUBLE val = valifnotfound;

    if(isKeyValid(key.c_str()))
    {
        CtiParseValue& pv = CtiParseValue(); // = _cmd["command"];
        map_itr_type itr;
        itr = _cmd.find(key.c_str() );
        if(itr != _cmd.end())
        {
            pv = (*itr).second;
        }
        val = pv.getReal();
    }

    return val;
}

string CtiCommandParser::getsValue(const string &key) const
{
    CtiParseValue &pv = CtiParseValue();
    map_itr_type itr;
    itr = _cmd.find(key.c_str() );
    if(itr != _cmd.end())
    {
        pv = (*itr).second;
    }
    return pv.getString();
}

boost::optional<std::string> CtiCommandParser::findStringForKey(const string &key) const
{
    boost::optional<CtiParseValue> pv = Cti::mapFind(_cmd, key);

    if( ! pv || ! pv->isStringValid() )
    {
        return boost::none;
    }

    return pv->getString();
}

void  CtiCommandParser::doParsePutConfigEmetcon(const string &_CmdStr)
{
    //convert to std::string for parsing.
    std::string CmdStr(_CmdStr);
    std::string temp2;
    std::string token;
    static const boost::regex  re_rawcmd("raw (func(tion)? )?start ?= ?0x[0-9a-f]+( 0x[0-9a-f]+)*");
    static const boost::regex  re_rolecmd("role [0-9]+" \
                             " [0-9]+" \
                             " [0-9]+" \
                             " [0-9]+" \
                             " [0-9]+");
    static const boost::regex  re_interval("interval(s| lp| li)");  //  match "intervals", "interval lp" and "interval li"
    static const boost::regex  re_thresholds("(outage|voltage) threshold " + str_num);
    static const boost::regex  re_thresholds_phaseloss("phaseloss threshold [0-9]+ duration [0-9]+:[0-9]+:[0-9]+");
    static const boost::regex  re_multiplier("mult(iplier)? kyz *[0-9]+ [0-9]+(\\.[0-9]+)?");  //  match "mult kyz # #(.###)
    static const boost::regex  re_ied_class("ied class [0-9]+ [0-9]+");
    static const boost::regex  re_ied_scan ("ied scan [0-9]+ [0-9]+");
    static const boost::regex  re_ied_config ("ied configure( +[a-zA-Z0-9]+)+");
    static const boost::regex  re_ied_dnp_address ("ied dnp address master " + str_anynum + " outstation " + str_anynum);
    static const boost::regex  re_ied_mask (" alarmmask " + str_anynum + " " + str_anynum);
    static const boost::regex  re_group_address("group (enable|disable)");
    static const boost::regex  re_address("address ((uniq(ue)? [0-9]+)|(gold [0-9]+ silver [0-9]+)|(bronze [0-9]+)|(lead meter [0-9]+ load [0-9]+))");
    static const boost::regex  re_mct410_meter_parameters("(centron|parameters)( ratio [0-9]+)?( display( [0-9]x[0-9]+)( test [0-9]+s?)( errors (en|dis)able))");
    static const boost::regex  re_mct420_meter_parameters("parameters( ratio [0-9]+)? lcd cycle time [0-9]+ disconnect display (en|dis)able( lcd display digits [456]x1)?");
    static const boost::regex  re_centron_reading("centron reading [0-9]+( [0-9]+)?");

    static const boost::regex  re_loadlimit("load limit " + str_floatnum + " " + str_num);
    static const boost::regex  re_llp_interest("llp interest channel " + str_num + " " + str_date + "( " + str_time + ")?");
    static const boost::regex  re_llp_peak_interest("llp peak interest channel " + str_num + " date " + str_date + " range " + str_num);

    static const boost::regex  re_cycle("cycle " + str_num + " " + str_num);

    static const boost::regex  re_freeze_day("freeze day " + str_num);

    static const boost::regex  re_holiday_index("holiday " + str_num + "( " + str_date + ")+");
    static const boost::regex  re_holiday("holiday( " + str_date + ")+");
    static const boost::regex  re_channel("channel " + str_num + " (ied|2-wire|3-wire|none)( input " + str_num + ")?( multiplier " + str_floatnum + ")?");

    static const std::string   str_phase("[ =][a-c]");
    static const boost::regex  re_phase(str_phase);

    //  matches any of AKT, HT, PT, MT, CT, ET, the standard/daylight versions of each, and whole/fractional hour offsets
    static const boost::regex  re_timezone("timezone (((ak|h|p|m|c|e)[ds]?t)|(-?" + str_floatnum + "))");

    char *p;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "putconfig")
    {
        if(containsString(CmdStr, " ied"))
        {
            _cmd["ied"] = CtiParseValue(TRUE);

            if(!(token = matchRegex(CmdStr, re_ied_class)).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "ied"
                cmdtok();  //  go past "class"

                _cmd["class"]       = CtiParseValue(atoi(cmdtok().c_str()));
                _cmd["classoffset"] = CtiParseValue(atoi(cmdtok().c_str()));
            }
            if(!(token = matchRegex(CmdStr, re_ied_scan)).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "ied"
                cmdtok();  //  go past "scan"

                _cmd["scan"]      = CtiParseValue(atoi(cmdtok().c_str()));
                _cmd["scandelay"] = CtiParseValue(atoi(cmdtok().c_str()));
            }
            if(!(token = matchRegex(CmdStr, re_ied_config)).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "ied"
                cmdtok();  //  go past "configure"

                _cmd["iedtype"]   = CtiParseValue(cmdtok().c_str());

                if(!(token = matchRegex(CmdStr, re_ied_mask)).empty())
                {
                    cmdtok(); //Past alarm mask
                    _cmd["eventmask1"] = CtiParseValue(strtol(cmdtok().c_str(), &p, 16));
                    _cmd["eventmask2"] = CtiParseValue(strtol(cmdtok().c_str(), &p, 16));

                }
                if(containsString(CmdStr, " multiplemeters"))
                {
                    _cmd["hasmultiplemeters"] = CtiParseValue(TRUE);
                }

                if(containsString(CmdStr, " dstdisable"))
                {
                    _cmd["dstenabled"] = CtiParseValue(false);
                }
            }
            if(!(token = matchRegex(CmdStr, re_ied_dnp_address)).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "ied"
                cmdtok();  //  go past "dnp"
                cmdtok();  //  go past "address"

                cmdtok();  //  go past "master"
                _cmd["ied dnp master address"]     = strtol(cmdtok().c_str(), &p, 0);  //  0 allows both decimal and hex (if prefixed with 0x)

                cmdtok();  //  go past "outstation"
                _cmd["ied dnp outstation address"] = strtol(cmdtok().c_str(), &p, 0);  //  0 allows both decimal and hex (if prefixed with 0x)
            }
        }
        if(containsString(CmdStr, " onoffpeak"))
        {
            _cmd["onoffpeak"] = CtiParseValue(TRUE);
        }
        if(containsString(CmdStr, " minmax"))
        {
            _cmd["minmax"] = CtiParseValue(TRUE);
        }
        if(containsString(CmdStr, " disconnect"))
        {
            _cmd["disconnect"] = CtiParseValue(TRUE);

            if(!(token = matchRegex(CmdStr, re_loadlimit)).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "load"
                cmdtok();  //  go past "limit"

                _cmd["disconnect demand threshold"]         = CtiParseValue(atof(cmdtok().c_str()));
                _cmd["disconnect load limit connect delay"] = CtiParseValue(atoi(cmdtok().c_str()));
            }

            if(!(token = matchRegex(CmdStr, re_cycle)).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "cycle"

                _cmd["disconnect cycle disconnect minutes"] = CtiParseValue(atoi(cmdtok().c_str()));
                _cmd["disconnect cycle connect minutes"]    = CtiParseValue(atoi(cmdtok().c_str()));
            }
        }
        if(containsString(CmdStr, " llp interest"))
        {
            if(!(token = matchRegex(CmdStr, re_llp_interest)).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "llp"
                cmdtok();  //  go past "interest"
                cmdtok();  //  go past "channel"

                _cmd["llp interest channel"] = CtiParseValue(atoi(cmdtok().c_str()));
                _cmd["llp interest date"] = cmdtok();
                _cmd["llp interest time"] = cmdtok();  //  this might just be an empty string, which is okay
            }
        }
        if(containsString(CmdStr, " llp peak interest"))
        {
            if(!(token = matchRegex(CmdStr, re_llp_peak_interest)).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "llp"
                cmdtok();  //  go past "peak"
                cmdtok();  //  go past "interest"

                cmdtok();  //  go past "channel"
                _cmd["llp peak interest channel"] = atoi(cmdtok().c_str());

                cmdtok();  //  go past "date"
                _cmd["llp peak interest date"] = cmdtok();

                cmdtok();  //  go past "range"
                _cmd["llp peak interest range"] = atoi(cmdtok().c_str());
            }
        }
        if(containsString(CmdStr, " group"))
        {
            if(!(token = matchRegex(CmdStr, re_group_address)).empty())
            {
                CtiTokenizer cmdtok(token);
                //  go past "group"
                cmdtok();

                if( std::strcmp( cmdtok().c_str(), "enable") == 0 )
                {
                    _cmd["groupaddress_enable"] = CtiParseValue( 1 );
                }
                else
                {
                    _cmd["groupaddress_enable"] = CtiParseValue( 0 );
                }
            }
        }
        if(containsString(CmdStr, " address"))
        {
            if(!(token = matchRegex(CmdStr, re_address)).empty())
            {
                _cmd["address"] = CtiParseValue(TRUE);

                CtiTokenizer cmdtok(token);

                cmdtok();  //  go past "address"

                token = cmdtok();

                if( containsString(token, "uniq") )
                {
                    _cmd["uniqueaddress"] = CtiParseValue(atoi(cmdtok().c_str()));
                }
                if( containsString(token, "gold") )
                {
                    _cmd["groupaddress_gold"] = CtiParseValue(atoi(cmdtok().c_str()));

                    cmdtok();  //  go past "silver"

                    _cmd["groupaddress_silver"] = CtiParseValue(atoi(cmdtok().c_str()));
                }
                else if( containsString(token, "bronze") )
                {
                    _cmd["groupaddress_bronze"] = CtiParseValue(atoi(cmdtok().c_str()));
                }
                else if( containsString(token, "lead") )
                {
                    cmdtok();  //  go past "meter"

                    _cmd["groupaddress_lead_meter"] = CtiParseValue(atoi(cmdtok().c_str()));

                    cmdtok();  //  go past "load"

                    _cmd["groupaddress_lead_load"] = CtiParseValue(atoi(cmdtok().c_str()));
                }
            }
        }
        if(containsString(CmdStr, " mult"))
        {
            if(!(token = matchRegex(CmdStr, re_multiplier)).empty())
            {
                CtiTokenizer cmdtok(token);
                //  go past "multiplier"
                cmdtok();
                //  go past "kyz"
                cmdtok();

                //  kyz offset (1, 2, or 3)
                _cmd["multoffset"] = CtiParseValue( atoi( cmdtok().c_str() ) );

                //  multiplier
                _cmd["multiplier"] = CtiParseValue( atof( cmdtok().c_str() ) );
            }
        }
        if(containsString(CmdStr, " interval"))
        {
            if(!(token = matchRegex(CmdStr, re_interval)).empty())
            {
                CtiTokenizer cmdtok(token);
                //  go past "interval"
                std::string temp = cmdtok();

                if( containsString(temp, "intervals") )
                {
                    _cmd["interval"] = CtiParseValue("intervals");
                }
                else
                {
                    //  "all", "li", or "lp"
                    _cmd["interval"] = CtiParseValue(cmdtok());
                }
            }
        }
        if(containsString(CmdStr, " threshold"))
        {
            if(!(token = matchRegex(CmdStr, re_thresholds)).empty())
            {
                CtiTokenizer cmdtok(token);

                std::string temp = cmdtok();

                if( containsString(temp, "outage") )
                {
                    cmdtok(); // go past "threshold"

                    _cmd["outage_threshold"] = CtiParseValue( atoi( cmdtok().c_str() ) );
                }
            }
            else if(!(token = matchRegex(CmdStr, re_thresholds_phaseloss)).empty())
            {
                CtiTokenizer cmdtok(token);

                cmdtok(); // go past "phaseloss"
                cmdtok(); // go past "threshold"

                _cmd["phaseloss_percent_threshold"]  = CtiParseValue( atoi( cmdtok().c_str() ) );

                cmdtok(); // go past "duration"

                _cmd["phaseloss_duration_threshold"] = CtiParseValue( cmdtok() );
            }
        }
        if(containsString(CmdStr, " centron") ||
           containsString(CmdStr, " parameters"))
        {
            std::string temp;

            if(!(token = matchRegex(CmdStr, re_mct420_meter_parameters)).empty())
            {
                CtiTokenizer cmdtok(token);

                cmdtok();  //  move past "parameters"

                //  it'll either be "ratio" or "lcd"
                if( cmdtok() == "ratio" )
                {
                    _cmd["transformer_ratio"] = atoi(cmdtok().c_str());
                    cmdtok();           // move to "lcd"
                }

                cmdtok();        // move past "cycle"
                cmdtok();        // move past "time"

                _cmd["lcd_cycle_time"] = atoi(cmdtok().c_str());

                cmdtok(); // move past "disconnect"
                cmdtok(); // move past "display"

                temp = cmdtok(); // grab enable/disable value.

                if( temp == "disable" )
                {
                    _cmd["disconnect_display_disabled"] = true;
                }

                if( cmdtok() == "lcd" )
                {
                    cmdtok();        // move past "display"
                    cmdtok();        // move past "digits"

                    _cmd["lcd display digits"] = cmdtok();
                }
            }
            if(!(token = matchRegex(CmdStr, re_mct410_meter_parameters)).empty())
            {
                CtiTokenizer cmdtok(token);

                cmdtok();  //  move past "centron" or "parameters"
                temp = cmdtok();

                if( temp == "ratio" )
                {
                    _cmd["transformer_ratio"] = CtiParseValue(atoi(cmdtok().c_str()));
                    cmdtok();           // move to "display"
                }

                temp = cmdtok();        // move past "display" to get the display resolution configuration
                _cmd["display_resolution"] = CtiParseValue(temp);

                cmdtok();               // move to "test"
                _cmd["display_test_duration"] = CtiParseValue(atoi(cmdtok().c_str()));

                cmdtok();               // move to "errors"
                temp = cmdtok();        // "enable" or "disable"
                if(temp[0] == 'e')
                {
                    _cmd["display_errors"] = CtiParseValue(true);
                }
            }
            if(!(token = matchRegex(CmdStr, re_centron_reading)).empty())
            {
                CtiTokenizer cmdtok(token);

                temp = cmdtok();  //  move past "centron"
                temp = cmdtok();  //  move past "reading"

                _cmd["centron_reading_forward"] = CtiParseValue(atoi(cmdtok().c_str()));
                _cmd["centron_reading_reverse"] = CtiParseValue(atoi(cmdtok().c_str()));
            }
        }
        if(containsString(CmdStr, " armc"))
        {
            _cmd["armc"] = CtiParseValue("TRUE");
        }
        if(containsString(CmdStr, " arml"))
        {
            _cmd["arml"] = CtiParseValue("TRUE");
        }
        if(containsString(CmdStr, " arms"))
        {
            _cmd["arms"] = CtiParseValue("TRUE");
        }
        //This will set broadcast to a device type to be used by the system device.
        if(!(temp2 = matchRegex(CmdStr, "broadcast[ =]*[a-z0-9_]+")).empty() )
        {
            std::string val;
            temp2.erase(0,9);//broadcast
            if(!(val = matchRegex(temp2, "[a-z0-9_]+")).empty())
            {
                _cmd["broadcast"] = val;
            }
        }
        if(containsString(CmdStr, " phasedetect"))
        {
            std::string val;
            _cmd["phasedetect"] = CtiParseValue("TRUE");

            if(!(temp2 = matchRegex(CmdStr, " clear")).empty())
            {
                _cmd["phasedetectclear"] = CtiParseValue("TRUE");
            }
            else
            {
                if(!(temp2 = matchRegex(CmdStr, " phase" + str_phase)).empty() )
                {
                    if(!(val = matchRegex(temp2, re_phase)).empty())
                    {
                        _cmd["phase"] = val.substr(1);
                    }
                }
                if(!(temp2 = matchRegex(CmdStr, " delta[ =]+" + str_signed_num)).empty())
                {
                    if(!(val = matchRegex(temp2, re_signed_num)).empty())
                    {
                        _cmd["phasedelta"] = CtiParseValue(atoi(val.c_str()));
                    }
                }
                if(!(temp2 = matchRegex(CmdStr, " interval[ =]+" + str_num)).empty())
                {
                    if(!(val = matchRegex(temp2, str_num)).empty())
                    {
                        _cmd["phaseinterval"] = CtiParseValue(atoi(val.c_str()));
                    }
                }
                if(!(temp2 = matchRegex(CmdStr, "num[ =]+" + str_num)).empty())
                {
                    if(!(val = matchRegex(temp2, str_num)).empty())
                    {
                        _cmd["phasenum"] = CtiParseValue(atoi(val.c_str()));
                    }
                }
            }

        }
        if(containsString(CmdStr, " alarm_mask"))
        {
            std::string val;
            UINT flag   = 0;
            //config byte, if specified in parse
            if(!(temp2 = matchRegex(CmdStr, "configbyte[ =]" + str_hexnum)).empty())
            {
                if(!(token = matchRegex(temp2, re_hexnum)).empty())
                {
                    _cmd["config_byte"] = CtiParseValue( strtol(token.c_str(), &p, 16) );
                }
            }
            //event flag 1/2 alarm masks
            if(containsString(CmdStr, " all"))
            {
                flag = CMD_FLAG_PC_EA_MASK;
            }
            else if(containsString(CmdStr, " alarm_mask1") || containsString(CmdStr, " alarm_mask2"))
            {
                if(!(temp2 = matchRegex(CmdStr, "alarm_mask1[ =]" + str_hexnum)).empty())
                {
                    //lower byte
                    if(!(token = matchRegex(temp2, re_hexnum)).empty())
                    {
                        flag |= strtol(token.c_str(), &p, 16);
                    }
                }
                if(!(temp2 = matchRegex(CmdStr, "alarm_mask2[ =]" + str_hexnum)).empty())
                {
                    //upper byte
                    if(!(token = matchRegex(temp2, re_hexnum)).empty())
                    {
                        flag |= (strtol(token.c_str(), &p, 16)  << 8);
                    }
                }
            }
            else
            {
                if(containsString(CmdStr, " tamper"))                  flag |= CMD_FLAG_PC_EA1_TAMPER | CMD_FLAG_PC_EA2_REVERSEPWR | CMD_FLAG_PC_EA2_ZEROUSAGE;
                if(containsString(CmdStr, " power_fail"))              flag |= CMD_FLAG_PC_EA1_PFEVENT;
                if(containsString(CmdStr, " under_voltage"))           flag |= CMD_FLAG_PC_EA1_UVEVENT;
                if(containsString(CmdStr, " over_voltage"))            flag |= CMD_FLAG_PC_EA1_OVEVENT;
                if(containsString(CmdStr, " pf_carryover"))            flag |= CMD_FLAG_PC_EA1_PFCARRYOVER;
                if(containsString(CmdStr, " rtc_adjusted"))            flag |= CMD_FLAG_PC_EA1_RTCADJUSTED;
                if(containsString(CmdStr, " holiday"))                 flag |= CMD_FLAG_PC_EA1_HOLIDAY;
                if(containsString(CmdStr, " dst_change"))              flag |= CMD_FLAG_PC_EA1_DSTCHANGE;
                if(containsString(CmdStr, " disconnect"))              flag |= CMD_FLAG_PC_EA2_DISCONNECT;
                if(containsString(CmdStr, " read_corrupted"))          flag |= CMD_FLAG_PC_EA2_READCORRUPTED;
            }
            //2 bytes - evant flag 1 alarm mask and event flag 2 alarm mask
            _cmd["alarm_mask"] = CtiParseValue( flag );

            //meter alarm mask (optional)
            if(containsString(CmdStr, " alarm_mask_meter"))
            {
                flag = 0;
                if(!(temp2 = matchRegex(CmdStr, "alarm_mask_meter1[ =]" + str_hexnum)).empty())
                {
                    //lower byte
                    if(!(token = matchRegex(temp2, re_hexnum)).empty())
                    {
                        flag |= strtol(token.c_str(), &p, 16);
                    }
                }
                if(!(temp2 = matchRegex(CmdStr, "alarm_mask_meter2[ =]" + str_hexnum)).empty())
                {
                    //upper byte
                    if(!(token = matchRegex(temp2, re_hexnum)).empty())
                    {
                        flag |= (strtol(token.c_str(), &p, 16)  << 8);
                    }

                }
                _cmd["alarm_mask_meter"] =  CtiParseValue( flag );
            }
        }
        if(containsString(CmdStr, " autoreconnect enable"))
        {
            _cmd["autoreconnect_enable"] = CtiParseValue( true );
        }
        if(containsString(CmdStr, " autoreconnect disable"))
        {
            _cmd["autoreconnect_enable"] = CtiParseValue( false );
        }

        if(containsString(CmdStr, " raw"))
        {
            if(!(token = matchRegex(CmdStr, re_rawcmd)).empty())
            {
                CtiTokenizer cmdtok(token);
                std::string rawData;

                //  go past "raw"
                cmdtok(" =");

                //  will either contain "function" or "start"
                temp2 = cmdtok(" =");

                if(containsString(temp2, "func"))
                {
                    //  if it was "function", make a note and read the next word
                    _cmd["rawfunc"] = CtiParseValue("TRUE");
                    temp2 = cmdtok(" =");
                }

                //  move past "start" to the hex digit
                temp2 = cmdtok(" =");

                _cmd["rawloc"] = CtiParseValue(strtol(temp2.c_str(), &p, 16));

                while( !(temp2 = cmdtok(" =")).empty() )
                {
                    rawData.append(1, (char)strtol(temp2.c_str(), &p, 16));
                }

                _cmd["rawdata"] = CtiParseValue( rawData );
            }
        }
        if(containsString(CmdStr, " channel"))
        {
            if(!(token = matchRegex(CmdStr, re_channel)).empty())
            {
                CtiTokenizer cmdtok(token);

                //  channel /n/ (ied|2-wire|3-wire|none) [input /n/] [multiplier n.nn]

                //  go past "channel"
                cmdtok();

                _cmd["channel_config"] = true;

                _cmd["channel_offset"] = atoi(cmdtok().data());

                //  will be one of "ied", "2-wire", "3-wire", or "none"
                _cmd["channel_type"] = cmdtok();

                temp2 = cmdtok();

                if( temp2 == "input" )
                {
                    _cmd["channel_input"] = atoi(cmdtok().data());

                    temp2 = cmdtok();
                }

                if( temp2 == "multiplier" )
                {
                    _cmd["channel_multiplier"] = atof(cmdtok().data());
                }
            }
        }
        if(containsString(CmdStr, " freeze"))
        {
            if(!(token = matchRegex(CmdStr, re_freeze_day)).empty())
            {
                CtiTokenizer cmdtok(token);

                cmdtok();  //  go past "freeze"
                cmdtok();  //  go past "day"

                _cmd["freeze_day"] = atoi(cmdtok().data());
            }
        }
        if(containsString(CmdStr, " timezone"))
        {
            if(!(token = matchRegex(CmdStr, re_timezone)).empty())
            {
                CtiTokenizer cmdtok(token);
                char *end;
                double timezone_offset;

                //  go past "timezone"
                cmdtok();

                temp2 = cmdtok();
                timezone_offset = strtod(temp2.data(), &end);

                //  Did we read a numeric value?
                if( end != temp2.data() )
                {
                    _cmd["timezone_offset"] = CtiParseValue( timezone_offset );
                }
                else
                {
                    _cmd["timezone_name"]   = CtiParseValue( temp2.c_str() );
                }
            }
        }
        if(containsString(CmdStr, " role"))
        {
            if(!(token = matchRegex(CmdStr, re_rolecmd)).empty())
            {
                CtiTokenizer cmdtok(token);
                std::string rawData;

                //  go past "role"
                cmdtok();

                temp2 = cmdtok();
                _cmd["rolenum"] = CtiParseValue( atoi(temp2.c_str()) );
                temp2 = cmdtok();
                _cmd["rolefixed"] = CtiParseValue( atoi(temp2.c_str()) );
                temp2 = cmdtok();
                _cmd["roleout"] = CtiParseValue( atoi(temp2.c_str()) );
                temp2 = cmdtok();
                _cmd["rolein"] = CtiParseValue( atoi(temp2.c_str()) );
                temp2 = cmdtok();
                _cmd["rolerpt"] = CtiParseValue( atoi(temp2.c_str()) );
            }
        }
        if(containsString(CmdStr, " mrole"))
        {
            if(!(token = matchRegex(CmdStr, "mrole( [0-9]+)+")).empty())
            {
                CtiTokenizer cmdtok(token);

                //  hop over "multi_role"
                cmdtok();

                // Command looks like this:
                // putconfig emetcon multi_role 1 f1 vo1 vi1 stf1 f2 vo2 vi2 stf2 ... fn von vin stfn
                // The roles are written in 6 role minimum blocks and are filled with default role (31 7 7 0)
                // if fewer than 6 roles are defined in the block.

                temp2 = cmdtok();
                _cmd["multi_rolenum"] = CtiParseValue( atoi(temp2.c_str()) );    // This is the first role written.  They must be consecutive!

                // Now we need to pull them off one at a time until done..
                int fixbits;
                int varbits_out;
                int varbits_in;
                int stagestf;
                int rolecount = 0;

                std::string strFixed;
                std::string strVarOut;
                std::string strVarIn;
                std::string strStages;

                while(!(temp2 = cmdtok()).empty())
                {
                    fixbits = !temp2.empty() ? atoi(temp2.c_str()) : 31;
                    temp2 = cmdtok();
                    varbits_out = !temp2.empty() ? atoi(temp2.c_str()) : 7;
                    temp2 = cmdtok();
                    varbits_in = !temp2.empty() ? atoi(temp2.c_str()) : 7;
                    temp2 = cmdtok();
                    stagestf = !temp2.empty() ? atoi(temp2.c_str()) : 15;

                    strFixed    += CtiNumStr( fixbits ) + " " ;
                    strVarOut   += CtiNumStr( varbits_out ) + " " ;
                    strVarIn    += CtiNumStr( varbits_in ) + " " ;
                    strStages   += CtiNumStr( stagestf ) + " " ;

                    rolecount++;
                }

                _cmd["multi_rolefixed"] = boost::trim_copy_if(strFixed,  is_char{' '});
                _cmd["multi_roleout"]   = boost::trim_copy_if(strVarOut, is_char{' '});
                _cmd["multi_rolein"]    = boost::trim_copy_if(strVarIn,  is_char{' '});
                _cmd["multi_rolerpt"]   = boost::trim_copy_if(strStages, is_char{' '});
                _cmd["multi_rolecount"] = CtiParseValue(rolecount);
            }
        }
        if(containsString(CmdStr, " holiday"))
        {
            if(!(token = matchRegex(CmdStr, re_holiday_index)).empty())
            {
                CtiTokenizer cmdtok(token);
                string holiday_str;

                cmdtok();  //  move past "holiday"

                //  get the count for the holiday offset
                _cmd["holiday_offset"] = atoi(cmdtok().data());

                for( int i = 0; !(holiday_str = cmdtok().data()).empty(); i++ )
                {
                    string holiday_offset = "holiday_date" + CtiNumStr(i);

                    _cmd[holiday_offset.data()] = CtiParseValue(holiday_str.data());
                }
            }
            else if(!(token = matchRegex(CmdStr, re_holiday)).empty())
            {
                CtiTokenizer cmdtok(token);
                string holiday_str;

                cmdtok();  //  move past "holiday"

                for( int i = 0; !(holiday_str = cmdtok().data()).empty(); i++ )
                {
                    string holiday_offset = "holiday_date" + CtiNumStr(i);

                    _cmd[holiday_offset.data()] = CtiParseValue(holiday_str.data());
                }
            }
            else
            {
                static const boost::regex re_holidaySetActive("holiday (active|cancel)");

                if(!(token = matchRegex(CmdStr, re_holidaySetActive)).empty())
                {
                    if( containsString(CmdStr, " cancel") )
                    {
                        _cmd["holiday_cancel_active"] = true;
                    }
                    else
                    {
                        _cmd["holiday_set_active"] = true;
                    }
                }
            }
        }
        if(containsString(CmdStr, " load profile allocation "))
        {
            if(!(token = matchRegex(CmdStr, "allocation 1:[0-9]+ 2:[0-9]+ 3:[0-9]+ 4:[0-9]+")).empty())
            {
                _cmd["load_profile_allocation"] = true;

                int channel1, channel2, channel3, channel4;

                (void)sscanf(token.c_str(), "allocation 1:%d 2:%d 3:%d 4:%d", &channel1, &channel2, &channel3, &channel4);

                _cmd["load_profile_allocation_channel_1"] = CtiParseValue(channel1);
                _cmd["load_profile_allocation_channel_2"] = CtiParseValue(channel2);
                _cmd["load_profile_allocation_channel_3"] = CtiParseValue(channel3);
                _cmd["load_profile_allocation_channel_4"] = CtiParseValue(channel4);
            }
        }
    }
    else
    {
        // Something went WAY wrong....
        CTILOG_ERROR(dout, "This better not ever be seen by mortals...");
    }
}
void  CtiCommandParser::doParsePutConfigVersacom(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    char *p;

    char        tbuf[60];

    std::string   token;
    std::string   temp, temp2;
    std::string   strnum;
    std::string   str;

    INT         _num = 0;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "putconfig")
    {
        if(!(token = matchRegex(CmdStr, "vdata( (0x)?[0-9a-f])+")).empty())
        {
            CtiTokenizer cmdtok(token);
            std::string rawData;

            //  go past "vdata"
            cmdtok();

            while( !(temp2 = cmdtok()).empty() )
            {
                rawData.append( 1,(char)strtol(temp2.c_str(), &p, 16) );
            }

            _cmd["vdata"] = CtiParseValue( rawData );
        }

        if(!(token = matchRegex(CmdStr, " util(ity)?[ =]*([ =]+0x)?[0-9a-f]+")).empty())
        {
            if(!(temp = matchRegex(token, re_hexnum)).empty())
            {
                _num = strtol(temp.c_str(), &p, 16);
            }
            else
            {
                _num = strtol(matchRegex(token, re_num).c_str(), &p, 10);
            }

            _cmd["utility"] = CtiParseValue( _num );

            _snprintf(tbuf, sizeof(tbuf), "CONFIG UID = %d", _num);
            _actionItems.push_back(tbuf);
        }

        if(!(token = matchRegex(CmdStr, " aux*[ =]*([ =]+0x)?[0-9a-f]+")).empty())
        {
            if(!(temp = matchRegex(token, re_hexnum)).empty())
            {
                _num = strtol(temp.c_str(), &p, 16);
            }
            else
            {
                _num = strtol(matchRegex(token, re_num).c_str(), &p, 10);
            }

            _cmd["aux"] = CtiParseValue( _num );

            _snprintf(tbuf, sizeof(tbuf), "CONFIG AUX = %d", _num);
            _actionItems.push_back(tbuf);
        }

        if(!(token = matchRegex(CmdStr, " sect(ion)?[ =]*[0-9]+")).empty())
        {
            {
                _num = strtol(matchRegex(token, re_num).c_str(), &p, 10);
            }

            _cmd["section"] = CtiParseValue( _num );

            _snprintf(tbuf, sizeof(tbuf), "CONFIG SECTION = %d", _num);
            _actionItems.push_back(tbuf);
        }

        if(containsString(CmdStr, " clas"))
        {
            if(!(token = matchRegex(CmdStr, " class[ =]*([ =]+)?0x[0-9a-f]+")).empty())
            {
                if(!(temp = matchRegex(token, re_hexnum)).empty())
                {
                    _num = strtol(temp.c_str(), &p, 16);
                }
                else
                {
                    _num = strtol(matchRegex(token, re_num).c_str(), &p, 10);
                    _num = (0x0001 << (16 - _num));
                }

                int itemp = 0;
                for(int q = 0; q < 16; q++)
                {
                    itemp |= (((_num >> q) & 0x0001) << (15 - q));
                }
                _num = itemp;

                _cmd["class"] = CtiParseValue( _num );

                string tempTS = convertVersacomAddressToHumanForm(_num);
                _snprintf(tbuf, sizeof(tbuf), "CONFIG CLASS = %s", tempTS.c_str());
                _actionItems.push_back(tbuf);

            }
            else if(!(token = matchRegex(CmdStr, " class [0-9]+" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                          )).empty())
            {
                CtiTokenizer   class_tok( token );
                _num = 0;
                int  mask = 0x0001;
                int  val;

                class_tok();   // Get over the class entry

                for(int i = 0; i < 16 && !(temp = class_tok(", \n\0")).empty() ; i++)
                {
                    val = atoi( temp.c_str() );

                    if(val > 0)
                    {
                        _num |= (mask << (val - 1));
                    }
                }

                _cmd["class"] = CtiParseValue( _num );
                string tempTS = convertVersacomAddressToHumanForm(_num);
                _snprintf(tbuf, sizeof(tbuf), "CONFIG CLASS = %s", tempTS.c_str());
                _actionItems.push_back(tbuf);
            }
        }

        if(containsString(CmdStr, " divi"))
        {
            if(!(token = matchRegex(CmdStr, " divi(sion)?[ =]*([ =]+)?0x[0-9a-f]+")).empty())
            {
                if(!(temp = matchRegex(token, re_hexnum)).empty())
                {
                    _num = strtol(temp.c_str(), &p, 16);
                }
                else
                {
                    _num = strtol(matchRegex(token, re_num).c_str(), &p, 10);
                    _num = (0x0001 << (16 - _num));
                }

                int itemp = 0;

                for(int q = 0; q < 16; q++)
                {
                    itemp |= (((_num >> q) & 0x0001) << (15 - q));
                }

                _num = itemp;

                _cmd["division"] = CtiParseValue( _num );
                string tempTS = convertVersacomAddressToHumanForm(_num);
                _snprintf(tbuf, sizeof(tbuf), "CONFIG DIVISION = %s", tempTS.c_str());
                _actionItems.push_back(tbuf);

            }
            else if(!(token = matchRegex(CmdStr, " divi(sion)? [0-9]+" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                           "( *, *[0-9]+)?" \
                                          )).empty())
            {
                CtiTokenizer   division_tok( token );
                _num = 0;
                int  mask = 0x0001;
                int  val;

                division_tok();   // Get over the "division" entry

                for(int i = 0; i < 16 && !(temp = division_tok(", \n\0")).empty() ; i++)
                {
                    val = atoi( temp.c_str() );

                    if(val > 0)
                    {
                        _num |= (mask << (val - 1));
                    }
                }

                _cmd["division"] = CtiParseValue( _num );
                string tempTS = convertVersacomAddressToHumanForm(_num);
                _snprintf(tbuf, sizeof(tbuf), "CONFIG DIVISION = %s", tempTS.c_str());
                _actionItems.push_back(tbuf);
            }
        }


        if(containsString(CmdStr, "from"))
        {
            if(!(token = matchRegex(CmdStr, "fromutil(ity)?[ =]*([ =]+0x)?[0-9a-f]+")).empty())
            {
                if(!(temp = matchRegex(token, re_hexnum)).empty())
                {
                    _num = strtol(temp.c_str(), &p, 16);
                }
                else
                {
                    _num = strtol(matchRegex(token, re_num).c_str(), &p, 10);
                }

                _cmd["fromutility"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG FROM UID = %d", _num);
                _actionItems.push_back(tbuf);
            }

            if(!(token = matchRegex(CmdStr, "fromsect(ion)?[ =]*([ =]+(0x))?[0-9a-f]+")).empty())
            {
                if(!(temp = matchRegex(token, re_hexnum)).empty())
                {
                    _num = strtol(temp.c_str(), &p, 16);
                }
                else
                {
                    _num = strtol(matchRegex(token, re_num).c_str(), &p, 10);
                }

                _cmd["fromsection"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG FROM SECTION = %d", _num);
                _actionItems.push_back(tbuf);
            }

            if(!(token = matchRegex(CmdStr, "fromclass[ =]*([ =]+)?0x[0-9a-f]+")).empty())
            {
                if(!(temp = matchRegex(token, re_hexnum)).empty())
                {
                    _num = strtol(temp.c_str(), &p, 16);
                }
                else
                {
                    _num = strtol(matchRegex(token, re_num).c_str(), &p, 10);
                    _num = (0x0001 << (16 - _num));
                }

                int itemp = 0;
                for(int q = 0; q < 16; q++)
                {
                    itemp |= (((_num >> q) & 0x0001) << (15 - q));
                }
                _num = itemp;

                _cmd["fromclass"] = CtiParseValue( _num );
                string tempTS = convertVersacomAddressToHumanForm(_num);
                _snprintf(tbuf, sizeof(tbuf), "CONFIG FROM CLASS = %s", tempTS.c_str());
                _actionItems.push_back(tbuf);

            }

            if(!(token = matchRegex(CmdStr, "fromdivi(sion)?[ =]*([ =]+)?0x[0-9a-f]+")).empty())
            {
                if(!(temp = matchRegex(token, re_hexnum)).empty())
                {
                    _num = strtol(temp.c_str(), &p, 16);
                }
                else
                {
                    _num = strtol(matchRegex(token, re_num).c_str(), &p, 10);
                    _num = (0x0001 << (16 - _num));
                }

                int itemp = 0;

                for(int q = 0; q < 16; q++)
                {
                    itemp |= (((_num >> q) & 0x0001) << (15 - q));
                }

                _num = itemp;

                _cmd["fromdivision"] = CtiParseValue( _num );
                string tempTS = convertVersacomAddressToHumanForm(_num);
                _snprintf(tbuf, sizeof(tbuf), "CONFIG FROM DIVISION = %s", tempTS.c_str());
                _actionItems.push_back(tbuf);

            }
        }

        if(containsString(CmdStr, "assign"))
        {
            if(!(token = matchRegex(CmdStr, "assign"\
                                      "(( [uascd][ =]*(0x)?[0-9a-f]+)*)+")).empty())
            {
                _cmd["vcassign"] = CtiParseValue( TRUE );

                if(!(strnum = matchRegex(token, " u[ =]*(0x)?[0-9a-f]+")).empty())
                {
                    _num = strtol(matchRegex(strnum, re_anynum).c_str(), &p, 0);

                    _cmd["utility"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG UTILITY = %d", _num);
                    _actionItems.push_back(tbuf);
                }
                if(!(strnum = matchRegex(token, " a[ =]*(0x)?[0-9a-f]+")).empty())
                {
                    if(!(temp = matchRegex(strnum, re_hexnum)).empty())
                    {
                        _num = strtol(temp.c_str(), &p, 16);
                    }
                    else
                    {
                        _num = strtol(matchRegex(strnum, re_num).c_str(), &p, 10);
                    }
                    _cmd["aux"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG AUX = %d", _num);
                    _actionItems.push_back(tbuf);
                }
                if(!(strnum = matchRegex(token, " s[ =]*[0-9]+")).empty())
                {
                    _num = strtol(matchRegex(strnum, re_num).c_str(), &p, 10);
                    _cmd["section"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG SECTION = %d", _num);
                    _actionItems.push_back(tbuf);
                }
                if(!(strnum = matchRegex(token, " c[ =]*(0x)?[0-9a-f]+")).empty())
                {
                    if(!(temp = matchRegex(strnum, re_hexnum)).empty())
                    {
                        _num = strtol(temp.c_str(), &p, 16);
                    }
                    else
                    {
                        _num = strtol(matchRegex(strnum, re_num).c_str(), &p, 10);
                        _num = (0x0001 << (16 - _num));
                    }

                    int itemp = 0;

                    for(int q = 0; q < 16; q++)
                    {
                        itemp |= (((_num >> q) & 0x0001) << (15 - q));
                    }

                    _num = itemp;

                    _cmd["class"] = CtiParseValue( _num );
                    string tempTS = convertVersacomAddressToHumanForm(_num);
                    _snprintf(tbuf, sizeof(tbuf), "CONFIG CLASS = %s", tempTS.c_str());
                    _actionItems.push_back(tbuf);

                }
                if(!(strnum = matchRegex(token, " d[ =]*(0x)?[0-9a-f]+")).empty())
                {
                    if(!(temp = matchRegex(strnum, re_hexnum)).empty())
                    {
                        _num = strtol(temp.c_str(), &p, 16);
                    }
                    else
                    {
                        _num = strtol(matchRegex(strnum, re_num).c_str(), &p, 10);
                        _num = (0x0001 << (16 - _num));
                    }

                    int itemp = 0;

                    for(int q = 0; q < 16; q++)
                    {
                        itemp |= (((_num >> q) & 0x0001) << (15 - q));
                    }

                    _num = itemp;

                    _cmd["division"] = CtiParseValue( _num );
                    string tempTS = convertVersacomAddressToHumanForm(_num);
                    _snprintf(tbuf, sizeof(tbuf), "CONFIG DIVISION = %s", tempTS.c_str());
                    _actionItems.push_back(tbuf);

                }

                INT serviceflag = 0;

                /*
                 *  serviceflag == VC_SERVICE_T_OUT == 1 is Temporary OUT of service
                 *  serviceflag == VC_SERVICE_T_IN  == 2 is Temporary IN service
                 *  serviceflag == VC_SERVICE_C_OUT == 4 is Contractual OUT of service
                 *  serviceflag == VC_SERVICE_C_IN  == 8 is Contractual IN service
                 *  serviceflag == VC_SERVICE_MASK  == 0x0f is a mask
                 *-------------------------------------------------------------------------*/

                if(containsString(CmdStr, " service in"))
                {
                    serviceflag |= (0x08 | 0x02); // (VC_SERVICE_C_IN | VC_SERVICE_T_IN);
                    _actionItems.push_back("SERVICE ENABLE");
                }
                else if( containsString(CmdStr, " service out"))
                {
                    serviceflag |= 0x04;
                    _actionItems.push_back("SERVICE DISABLE");
                }

                if(containsString(CmdStr, " service tin"))
                {
                    serviceflag |= 0x02;
                    _actionItems.push_back("SERVICE ENABLE TEMPORARY");
                }
                else if(containsString(CmdStr, " service tout"))
                {
                    serviceflag |= 0x01;
                    _actionItems.push_back("SERVICE DISABLE TEMPORARY");
                }

                if(serviceflag)
                {
                    _cmd["assignedservice"] = CtiParseValue( serviceflag );
                }
            }
        }

        if(containsString(CmdStr, "serv"))
        {
            if(!(token = matchRegex(CmdStr, " serv(ice)? (in|out|enable|disable)( temp)?")).empty())
            {
                INT   flag = 0;

                /*
                 *  serviceflag == VC_SERVICE_T_OUT == 1 is Temporary OUT of service
                 *  serviceflag == VC_SERVICE_T_IN  == 2 is Temporary IN service
                 *  serviceflag == VC_SERVICE_C_OUT == 4 is Contractual OUT of service
                 *  serviceflag == VC_SERVICE_C_IN  == 8 is Contractual IN service
                 *  serviceflag == VC_SERVICE_MASK  == 0x0f is a mask
                 *-------------------------------------------------------------------------*/

                // What offset is needed now...
                if(containsString(token, "in") ||
                   containsString(token, "enable"))
                {
                    flag |= 0x0a;

                    _snprintf(tbuf, sizeof(tbuf), "SERVICE ENABLE");
                }
                else if(containsString(token, "out") ||
                        containsString(token, "disable"))
                {
                    flag |= 0x04;

                    _snprintf(tbuf, sizeof(tbuf), "SERVICE DISABLE");
                }

                if(containsString(token, " temp"))
                {
                    char t2[80];
                    ::strcpy(t2, tbuf);

                    flag >>= 2;       // Make the flag match the protocol

                    ::_snprintf(tbuf, sizeof(tbuf), "%s TEMPORARY", t2);

                    if(!(token = matchRegex(CmdStr, "offhours [0-9]+")).empty())
                    {
                        bool offhourssupported = false;
                        int serialnumber = getiValue("serial", 0);

                        if( serialnumber != 0 )
                        {
                            std::string vcrangestr = gConfigParms.getValueAsString("LCR_VERSACOM_EXTENDED_TSERVICE_RANGES").data();

                            if(!vcrangestr.empty())
                            {
                                int loopcnt = 0;
                                while(!vcrangestr.empty())
                                {
                                    std::string rstr = matchRegex(vcrangestr, "[0-9]*-[0-9]*,?");

                                    if(!rstr.empty())
                                    {
                                        char *chptr;
                                        std::string startstr = matchRegex(rstr, "[0-9]*");
                                        std::string stopstr = matchRegex(rstr, " *- *[0-9]* *,? *");
                                        boost::trim_if      (stopstr, is_char{' '});
                                        boost::trim_left_if (stopstr, is_char{'-'});
                                        boost::trim_right_if(stopstr, is_char{','});
                                        boost::trim_if      (stopstr, is_char{' '});

                                        UINT startaddr = strtoul( startstr.c_str(), &chptr, 10 );
                                        UINT stopaddr = strtoul( stopstr.c_str(), &chptr, 10 );

                                        if(startaddr <= serialnumber && serialnumber <= stopaddr)
                                        {
                                            CTILOG_INFO(dout, "Range " << startaddr << " to " << stopaddr << " found and recorded for VersaCom Extended Addressing");

                                            // This is a supported versacom switch and we can continue!
                                            offhourssupported = true;
                                            break;
                                        }
                                    }

                                    removeRegexFirstMatch(vcrangestr, "[0-9]*-[0-9]*,?");

                                    if(loopcnt++ > 256)
                                    {
                                        CTILOG_ERROR(dout, "Problem found with configuration item LCR_VERSACOM_EXTENDED_TSERVICE_RANGES : \"" << gConfigParms.getValueAsString("LCR_VERSACOM_EXTENDED_TSERVICE_RANGES") << "\"");
                                        break;
                                    }
                                }
                            }
                        }

                        if(offhourssupported)
                        {
                            str = matchRegex(token, re_num);
                            int offtimeinhours = atoi(str.c_str());

                            _cmd["vctexservice"] = CtiParseValue( TRUE );
                            _cmd["vctservicetime"] = CtiParseValue( offtimeinhours > 65535 ? 65535 : offtimeinhours );  // Must be passed as half seconds for VCOM
                        }
                    }
                }

                _cmd["service"] = CtiParseValue( flag );

                _actionItems.push_back(tbuf);
            }
        }

        if(!(token = matchRegex(CmdStr, "led (y|n) *(y|n) *(y|n)")).empty())
        {
            INT   flag = 0;
            int   i;
            int   mask;

            CtiToLower(token);

            if(!(strnum = matchRegex(token, "(y|n) *(y|n) *(y|n)")).empty())
            {
                for(i = 0, mask = 0x80 ; i < strnum.length(); i++)
                {
                    if(strnum[(size_t)i] == 'y')
                    {
                        flag |= mask;
                    }

                    if(strnum[(size_t)i] != ' ')
                    {
                        mask >>= 1;
                    }
                }
            }

            _cmd["led"] = CtiParseValue( flag );

            _snprintf(tbuf, sizeof(tbuf), "CONFIG LED = Load %s, Status %s, Report %s",
                      ((flag & 0x80) ? "ON" : "OFF"),
                      ((flag & 0x40) ? "ON" : "OFF"),
                      ((flag & 0x20) ? "ON" : "OFF"));
            _actionItems.push_back(tbuf);
        }

        if(containsString(CmdStr, "reset"))
        {
            if(!(token = matchRegex(CmdStr, "reset[_a-z]*( (rtc|lf|r1|r2|r3|r4|cl|pt))+")).empty())
            {
                INT   flag = 0;

                if(containsString(token, "rtc"))
                {
                    flag |= 0x40;
                }
                if(containsString(token, "lf"))
                {
                    flag |= 0x20;
                }
                if(containsString(token, "r1"))
                {
                    flag |= 0x10;
                }
                if(containsString(token, "r2"))
                {
                    flag |= 0x08;
                }
                if(containsString(token, "r3"))
                {
                    flag |= 0x04;
                }
                if(containsString(token, "r4"))
                {
                    flag |= 0x80;
                }
                if(containsString(token, "cl"))
                {
                    flag |= 0x02;
                }
                if(containsString(token, "pt"))
                {
                    flag |= 0x01;
                }

                _cmd["reset"] = CtiParseValue( flag );

                _snprintf(tbuf, sizeof(tbuf), "CNTR RESET = 0x%02X", flag);
                _actionItems.push_back(tbuf);
            }
        }

        if(containsString(CmdStr, "prop"))
        {
            if(!(token = matchRegex(CmdStr, "prop[ a-z_]*[ =]*([ =]+)?[0-9]+")).empty())
            {
                // What offset is needed now...
                if(!(strnum = matchRegex(token, re_num)).empty())
                {
                    _num = strtol(strnum.c_str(), &p, 10);
                    _cmd["proptime"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG PROPTIME = %d", _num);
                    _actionItems.push_back(tbuf);
                }
            }
        }

        if(containsString(CmdStr, " cold"))
        {
            if(!(token = matchRegex(CmdStr, "cold[ a-z_]*" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)?" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)?")).empty())
            {
                if(!(strnum = matchRegex(token, "r1[ =]*[0-9]+[ =]*[hms]?")).empty())
                {
                    strnum = strnum.substr(2); // Blank the r1 to prevent matches on the 1
                    // _num = strtol(matchRegex(strnum, re_anynum).data(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["coldload_r1"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG COLDLOAD R1 = %d", _num);
                    _actionItems.push_back(tbuf);
                }
                if(!(strnum = matchRegex(token, "r2[ =]*[0-9]+[ =]*[hms]?")).empty())
                {
                    strnum = strnum.substr(2); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(matchRegex(strnum, re_anynum).c_str(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["coldload_r2"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG COLDLOAD R2 = %d", _num);
                    _actionItems.push_back(tbuf);
                }
                if(!(strnum = matchRegex(token, "r3[ =]*[0-9]+[ =]*[hms]?")).empty())
                {
                    strnum = strnum.substr(2); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(matchRegex(strnum, re_anynum).c_str(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["coldload_r3"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG COLDLOAD R3 = %d", _num);
                    _actionItems.push_back(tbuf);
                }
            }
        }

        if(containsString(CmdStr, " cycle"))
        {
            if(!(token = matchRegex(CmdStr, "cycle[ a-z]*" \
                                      "( *r[123][ =]*[0-9]+[ =]*)" \
                                      "( *r[123][ =]*[0-9]+[ =]*)?" \
                                      "( *r[123][ =]*[0-9]+[ =]*)?")).empty())
            {
                if(!(strnum = matchRegex(token, "r1[ =]*[0-9]+")).empty())
                {
                    strnum = strnum.substr(2); // Blank the r1 to prevent matches on the 1
                    _num = strtol(matchRegex(strnum, re_anynum).c_str(), &p, 0);
                    _cmd["cycle_r1"] = CtiParseValue( _num );
                }
                if(!(strnum = matchRegex(token, "r2[ =]*[0-9]+[ =]*")).empty())
                {
                    strnum = strnum.substr(2); // Blank the r1 to prevent matches on the 1
                    _num = strtol(matchRegex(strnum, re_anynum).c_str(), &p, 0);
                    _cmd["cycle_r2"] = CtiParseValue( _num );
                }
                if(!(strnum = matchRegex(token, "r3[ =]*[0-9]+[ =]*")).empty())
                {
                    strnum = strnum.substr(2); // Blank the r1 to prevent matches on the 1
                    _num = strtol(matchRegex(strnum, re_anynum).c_str(), &p, 0);
                    _cmd["cycle_r3"] = CtiParseValue( _num );
                }
            }
        }

        if(containsString(CmdStr, " scram"))
        {
            if(!(token = matchRegex(CmdStr, "scram[ a-z]*" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)?" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)?")).empty())
            {
                if(!(strnum = matchRegex(token, "r1[ =]*[0-9]+[ =]*[hms]?")).empty())
                {
                    strnum = strnum.substr(2); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(matchRegex(strnum, re_anynum).c_str(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["scram_r1"] = CtiParseValue( _num );
                }
                if(!(strnum = matchRegex(token, "r2[ =]*[0-9]+[ =]*[hms]?")).empty())
                {
                    strnum = strnum.substr(2); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(matchRegex(strnum, re_anynum).c_str(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["scram_r2"] = CtiParseValue( _num );
                }
                if(!(strnum = matchRegex(token, "r3[ =]*[0-9]+[ =]*[hms]?")).empty())
                {
                    strnum = strnum.substr(2); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(matchRegex(strnum, re_anynum).c_str(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["scram_r3"] = CtiParseValue( _num );
                }
            }
        }

        if(containsString(CmdStr, " raw"))
        {
            if(!(token = matchRegex(CmdStr, "raw" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?" \
                                      "( *(([ =]+)?(0x)?)?[0-9a-f]+)?")).empty())
            {
                int   i;
                char *ptr = NULL;

                std::string      rawStr;     // This is to contain the raw bytes

                CtiTokenizer   raw_tok(token);

                raw_tok();   // Get us past the "raw" moniker..
                token = raw_tok(); // Returns each whitespace seperated token successivly.
                for(i = 0; i < 20 && !token.empty(); i++)
                {
                    //  strtol returns 0 if token is empty or not convertable, which is what we want
                    rawStr.append(1, (CHAR)strtol(token.c_str(), &ptr, 16));

                    token = raw_tok(); // Returns each whitespace seperated token successivly.
                }

                _cmd["raw"] = CtiParseValue(rawStr);
            }
        }

        if(containsString(CmdStr, " lcrmode"))
        {
            if(!(token = matchRegex(CmdStr, "lcrmode (e|v)")).empty())
            {
                CtiTokenizer lcrmode_tok( token );

                lcrmode_tok();   // Get us past "lcrmode"

                _cmd["lcrmode"] = CtiParseValue(lcrmode_tok());
            }
        }

        if(containsString(CmdStr, " eclp"))
        {
            //  emetcon cold load pickup
            if(!(token = matchRegex(CmdStr, "eclp (en(able)?|dis(able)?)")).empty())
            {
                CtiTokenizer eclp_tok( token );

                eclp_tok();   // Get us past "eclp" moniker..

                _cmd["eclp"] = CtiParseValue(eclp_tok());
            }
        }

        if(containsString(CmdStr, " gold"))
        {
            if(!(token = matchRegex(CmdStr, "gold [0-9]")).empty())
            {
                CtiTokenizer gold_tok( token );

                gold_tok();   // Get us past "gold"

                _cmd["gold"] = CtiParseValue(atoi(gold_tok().c_str()));
            }
        }

        if(containsString(CmdStr, " silver"))
        {
            if(!(token = matchRegex(CmdStr, "silver [0-9]*")).empty())
            {
                CtiTokenizer silver_tok( token );

                silver_tok();   // Get us past "silver"

                _cmd["silver"] = CtiParseValue(atoi(silver_tok().c_str()));
            }
        }

        // cbc-8000 local control enable and disable
        if ( ! (token = matchRegex(CmdStr, "(var|temp|time) (en|dis)able")).empty() )
        {
            CtiTokenizer localTok( token );

            _cmd[ "local_control_type" ]  = localTok();      // (var|temp|time)
            _cmd[ "local_control_state" ] = localTok();      // (en|dis)able

            _actionItems.push_back( boost::algorithm::to_upper_copy( token ) );
        }
        // if i knew for sure that the cbc-8000 code was the only device to use the following block
        //  i could fold it into the preceding one.  As it is, i will add the keys here so the device can
        //  be quasi-agnostic as to the type of local control
        if(!(token = matchRegex(CmdStr, "ovuv[ =]+((ena(ble)?)|(dis(able)?))")).empty())
        {
            int   op = 0;
            CHAR  op_name[20];

            _cmd[ "local_control_type" ]  = CtiParseValue( "ovuv" );

            if(containsString(token, "ena"))
            {
                op = 1;
                _cmd[ "local_control_state" ] = CtiParseValue( "enable" );
                _snprintf(op_name, sizeof(op_name), "ENABLE");
            }
            else if(containsString(token, "dis"))
            {
                op = 0;
                _cmd[ "local_control_state" ] = CtiParseValue( "disable" );
                _snprintf(op_name, sizeof(op_name), "DISABLE");
            }

            _cmd["ovuv"] = CtiParseValue( op );

            _snprintf(tbuf, sizeof(tbuf), "OVUV %s", op_name);
            _actionItems.push_back(tbuf);
        }

        if(!(token = matchRegex(CmdStr, " sync|filler")).empty())
        {
            _cmd["vcfiller"] = TRUE;
        }

        if(!(token = matchRegex(CmdStr, " channel 2 (netmetering|ui1203 water meter|ui1204 water meter|none)")).empty())
        {
            CtiTokenizer fillerTok( token );

            fillerTok();      // == "channel"
            fillerTok();      // == "2"

            _cmd["channel_2_configuration"] = true;
            _cmd["channel_2_configuration_setting"] = CtiParseValue( fillerTok() );
        }
    }
    else
    {
        // Something went WAY wrong....
        CTILOG_ERROR(dout, "This better not ever be seen by mortals...");
    }

    if( containsString(CmdStr, "test_mode_flag") )
    {
        setFlags(CMD_FLAG_TESTMODE);
    }
}

void  CtiCommandParser::doParsePutStatusEmetcon(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    std::string   temp2;
    std::string   token;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "putstatus")
    {
        if(containsString(CmdStr, " rovr"))
        {
            setFlags(getFlags() | CMD_FLAG_PS_RESETOVERRIDE);
        }
        if(containsString(CmdStr, " peak"))
        {
            if(containsString(CmdStr, " peak on"))
            {
                _cmd["peak"] = CtiParseValue(TRUE);
            }
            else if(containsString(CmdStr, " peak off"))
            {
                _cmd["peak"] = CtiParseValue(FALSE);
            }
        }
        if(containsString(CmdStr, " freeze"))
        {
            if(containsString(CmdStr, " voltage"))
            {
                _cmd["voltage"] = CtiParseValue(TRUE);
            }
        }
        if(containsString(CmdStr, " tou holiday rate"))
        {
            if(containsString(CmdStr, "set tou holiday rate"))
            {
                _cmd["set_tou_holiday_rate"] = CtiParseValue( TRUE );
    }

            if(containsString(CmdStr, "clear tou holiday rate"))
            {
                _cmd["clear_tou_holiday_rate"] = CtiParseValue( TRUE );
            }
        }
    }
    else
    {
        // Something went WAY wrong....
        CTILOG_ERROR(dout, "This better not ever be seen by mortals...");
    }
}

void  CtiCommandParser::doParsePutStatusVersacom(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);

    char        tbuf[60];

    std::string   token;
    std::string   temp2;
    std::string   strnum;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "putstatus")
    {
        if(!(token = matchRegex(CmdStr, "prop[a-z]*[ =]+((disp[a-z]*)|(inc[a-z]*)|(term[a-z]*))")).empty())
        {
            int   op = 0;
            CHAR  op_name[20];

            if( containsRegex(token, "disp(lay)?") )                   // Turn on light & bump counter.
            {
                op = 0x04;
                _snprintf(op_name, sizeof(op_name), "DISPLAY");
            }
            else if( containsRegex(token, "inc(rement)?") )            // bump counter.
            {
                op = 0x02;
                _snprintf(op_name, sizeof(op_name), "INCREMENT");
            }
            else if( containsRegex(token, "term(inate)?") )            // Turn off light.
            {
                op = 0x01;
                _snprintf(op_name, sizeof(op_name), "TERMINATE");
            }

            if(op)
            {
                _cmd["proptest"] = CtiParseValue( op );

                _snprintf(tbuf, sizeof(tbuf), "PROP TEST: %01x = %s", op, op_name);
                _actionItems.push_back(tbuf);
            }
        }
        else if(!(token = matchRegex(CmdStr, "ovuv[ =]+((ena(ble)?)|(dis(able)?))")).empty())
        {
            int   op = 0;
            CHAR  op_name[20];

            if(containsString(token, "ena"))
            {
                op = 1;
                _snprintf(op_name, sizeof(op_name), "ENABLE");
            }
            else if(containsString(token, "dis"))
            {
                op = 0;
                _snprintf(op_name, sizeof(op_name), "DISABLE");
            }

            _cmd["ovuv"] = CtiParseValue( op );

            _snprintf(tbuf, sizeof(tbuf), "OVUV %s", op_name);
            _actionItems.push_back(tbuf);
        }
    }
    else
    {
        // Something went WAY wrong....
        CTILOG_ERROR(dout, "Unexpected command \""<< token <<"\", expected \"putstatus\"");
    }
}

void  CtiCommandParser::doParsePutStatusFisherP(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    char        tbuf[60];

    std::string   token;
    std::string   temp2;
    std::string   strnum;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "putstatus")
    {

        if(!(token = matchRegex(CmdStr, "ovuv[ =]+((ena(ble)?)|(dis(able)?))")).empty())
        {
            int   op = 0;
            CHAR  op_name[20];

            if(containsString(token, "ena"))
            {
                op = 1;
                _snprintf(op_name, sizeof(op_name), "ENABLE");
            }
            else if(containsString(token, "dis"))
            {
                op = 0;
                _snprintf(op_name, sizeof(op_name), "DISABLE");
            }

            _cmd["ovuv"] = CtiParseValue( op );

            _snprintf(tbuf, sizeof(tbuf), "OVUV %s", op_name);
            _actionItems.push_back(tbuf);
        }
    }
    else
    {
        // Something went WAY wrong....
        CTILOG_ERROR(dout, "Unexpected command \""<< token <<"\", expected \"putstatus\"");
    }
}

const std::list< string >& CtiCommandParser::getActionItems() const
{
    return _actionItems;
}

void CtiCommandParser::resolveProtocolType(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    int loopcnt;
    std::string         token;

    /*
     *  Type is assigned i.f.f. there is a serial number specified.  The default type is versacom.
     */
    if(!isKeyValid("type"))
    {
        if( isKeyValid("serial") )
        {
            if(containsString(CmdStr, "golay"))       // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "golay", ProtocolGolayType );
            }
            else if(containsString(CmdStr, "sadig"))       // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "sadig", ProtocolSADigitalType );
            }
            else if(containsString(CmdStr, "sa105"))       // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "sa105", ProtocolSA105Type );
            }
            else if(containsString(CmdStr, "sa205"))       // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "sa205", ProtocolSA205Type );
            }
            else if(containsString(CmdStr, "sa305"))       // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "sa305", ProtocolSA305Type );
            }
            else if(containsString(CmdStr, "xcom") || containsString(CmdStr, "expresscom"))
            {
                _cmd["type"] = CtiParseValue( "expresscom", ProtocolExpresscomType );
            }
            else if(containsString(CmdStr, "vcom") || containsString(CmdStr, "versacom") ||
                    containsString(CmdStr, "emetcon"))
            {
                //  NOTE:  all serial number requests with the "emetcon" specifier shall be parsed as Versacom instead.
                //           this is because DLC LCRs are "emetcon" devices in many peoples' minds, despite being addressed
                //           as Versacom for all serial-number (non Gold/Silver) addresses
                //  _cmd["type"] = CtiParseValue( "emetcon",  ProtocolEmetconType );

                _cmd["type"] = CtiParseValue( "versacom", ProtocolVersacomType );
            }
            else if(containsString(CmdStr, "fp"))            // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "fp",  ProtocolFisherPierceType );
            }
            else
            {
                int serialnumber = getiValue("serial", 0);

                std::string xcrangestr = gConfigParms.getValueAsString("LCR_EXPRESSCOM_RANGES").data();
                std::string vcrangestr = gConfigParms.getValueAsString("LCR_VERSACOM_RANGES").data();

                if(!vcrangestr.empty() || !xcrangestr.empty())
                {
                    loopcnt = 0;
                    while(!vcrangestr.empty())
                    {
                        std::string str = matchRegex(vcrangestr, "[0-9]*-[0-9]*,?");

                        if(!str.empty())
                        {
                            char *chptr;
                            std::string startstr = matchRegex(str, "[0-9]*");
                            std::string stopstr = matchRegex(str, " *- *[0-9]* *,? *");
                            boost::trim_if      (stopstr, is_char{' '});
                            boost::trim_left_if (stopstr, is_char{'-'});
                            boost::trim_right_if(stopstr, is_char{','});
                            boost::trim_if      (stopstr, is_char{' '});

                            UINT startaddr = strtoul( startstr.c_str(), &chptr, 10 );
                            UINT stopaddr = strtoul( stopstr.c_str(), &chptr, 10 );

                            if(startaddr <= serialnumber && serialnumber <= stopaddr)
                            {
                                // This is a versacom switch and we can continue!
                                CTILOG_INFO(dout, "Range " << startaddr << " to " << stopaddr << " found and recorded for VersaCom");

                                _cmd["type"] = CtiParseValue( "versacom", ProtocolVersacomType );
                                break;
                            }
                        }

                        removeRegexFirstMatch(vcrangestr, "[0-9]*-[0-9]*,?");

                        if(loopcnt++ > 256)
                        {
                            CTILOG_ERROR(dout, "Problem found with configuration item LCR_VERSACOM_RANGES : \"" << gConfigParms.getValueAsString("LCR_VERSACOM_RANGES") << "\"");
                            break;
                        }
                    }

                    loopcnt = 0;
                    while(!isKeyValid("type") && !xcrangestr.empty())
                    {
                        std::string str = matchRegex(xcrangestr, "[0-9]*-[0-9]*,?");

                        if(!str.empty())
                        {
                            char *chptr;
                            std::string startstr = matchRegex(str, "[0-9]*");
                            std::string stopstr = matchRegex(str, " *- *[0-9]* *,? *");
                            boost::trim_if      (stopstr, is_char{' '});
                            boost::trim_left_if (stopstr, is_char{'-'});
                            boost::trim_right_if(stopstr, is_char{','});
                            boost::trim_if      (stopstr, is_char{' '});

                            UINT startaddr = strtoul( startstr.c_str(), &chptr, 10 );
                            UINT stopaddr = strtoul( stopstr.c_str(), &chptr, 10 );

                            if(startaddr <= serialnumber && serialnumber <= stopaddr)
                            {
                                // This is a versacom switch and we can continue!
                                CTILOG_INFO(dout, "Range " << startaddr << " to " << stopaddr << " found and recorded for ExpressCom");

                                _cmd["type"] = CtiParseValue( "expresscom", ProtocolExpresscomType );
                                break;
                            }
                        }

                        removeRegexFirstMatch(xcrangestr, "[0-9]*-[0-9]*,?");

                        if(loopcnt++ > 256)
                        {
                            CTILOG_ERROR(dout, "Problem found with configuration item LCR_EXPRESSCOM_RANGES : \"" << gConfigParms.getValueAsString("LCR_EXPRESSCOM_RANGES") << "\"");
                            break;
                        }
                    }
                }

                if(!isKeyValid("type"))
                {
                    _cmd["type"] = CtiParseValue( "versacom", ProtocolVersacomType );
                }
            }

            if(getiValue("type") == ProtocolExpresscomType)
            {
                int serialnumber = getiValue("serial", 0);
                std::string xcprefixrange = gConfigParms.getValueAsString("LCR_EXPRESSCOM_SERIAL_PREFIX_RANGES").data();
                loopcnt = 0;
                if(!xcprefixrange.empty())
                {
                    while(!xcprefixrange.empty())
                    {
                        std::string str = matchRegex(xcprefixrange, "[0-9]*-[0-9]*,?");

                        if(!str.empty())
                        {
                            char *chptr;
                            std::string startstr = matchRegex(str, "[0-9]*");
                            std::string stopstr = matchRegex(str, " *- *[0-9]* *,? *");
                            boost::trim_if      (stopstr, is_char{' '});
                            boost::trim_left_if (stopstr, is_char{'-'});
                            boost::trim_right_if(stopstr, is_char{','});
                            boost::trim_if      (stopstr, is_char{' '});

                            UINT startaddr = strtoul( startstr.c_str(), &chptr, 10 );
                            UINT stopaddr = strtoul( stopstr.c_str(), &chptr, 10 );

                            if(startaddr <= serialnumber && serialnumber <= stopaddr)
                            {
                                // This is a prefix switch switch and we can continue!
                                _cmd["xcprefix"] = CtiParseValue( TRUE );
                                _cmd["xcprefixstr"] = CtiParseValue( gConfigParms.getValueAsString("LCR_EXPRESSCOM_SERIAL_PREFIX_MESSAGE").data() );
                                break;
                            }
                        }

                        removeRegexFirstMatch(xcprefixrange, "[0-9]*-[0-9]*,?");

                        if(loopcnt++ > 256)
                        {
                            CTILOG_ERROR(dout, "Problem found with configuration item LCR_EXPRESSCOM_SERIAL_PREFIX_RANGES : \"" << gConfigParms.getValueAsString("LCR_EXPRESSCOM_SERIAL_PREFIX_RANGES") << "\"");
                            break;
                        }
                    }
                }
            }
        }
        else
        {
            if( containsString(CmdStr, "xcom") || containsString(CmdStr, "expresscom") )
            {
                _cmd["type"] = CtiParseValue("expresscom", ProtocolExpresscomType);
                doParseExpresscomAddressing(CmdStr);
            }
            else if( containsString(CmdStr, "emetcon") )   _cmd["type"] = CtiParseValue("emetcon", ProtocolEmetconType);
            else if( containsString(CmdStr, "golay") )     _cmd["type"] = CtiParseValue("golay",   ProtocolGolayType);
            else if( containsString(CmdStr, "sadig") )     _cmd["type"] = CtiParseValue("sadig",   ProtocolSADigitalType);
            else if( containsString(CmdStr, "sa105") )     _cmd["type"] = CtiParseValue("sa105",   ProtocolSA105Type);
            else if( containsString(CmdStr, "sa205") )     _cmd["type"] = CtiParseValue("sa205",   ProtocolSA205Type);
            else if( containsString(CmdStr, "sa305") )     _cmd["type"] = CtiParseValue("sa305",   ProtocolSA305Type);
            else
            {  //  default to Versacom if nothing found
                _cmd["type"] = CtiParseValue("versacom", ProtocolVersacomType);
            }
        }
    }
}

void CtiCommandParser::doParseExpresscomAddressing(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);

    static const boost::regex re_serial  ("serial "   + str_num);
    static const boost::regex re_spid    ("spid "     + str_num);
    static const boost::regex re_geo     ("geo "      + str_num);
    static const boost::regex re_sub     ("sub "      + str_num);
    static const boost::regex re_feeder  ("feeder "   + str_num);
    static const boost::regex re_zip     ("zip "      + str_num);
    static const boost::regex re_uda     ("uda "      + str_num);
    static const boost::regex re_program ("program "  + str_num);
    static const boost::regex re_splinter("splinter " + str_num);

    // putconfig xcom target .. assign .. command should not set these!
    if(matchRegex(CmdStr, re_target).empty())
    {
        std::string temp;

        if( !(temp = matchRegex(CmdStr, re_serial)).empty() )   _cmd["xc_serial"]   = static_cast<int>(strtoul(matchRegex(temp, str_num).c_str(), NULL, 0));   // serial is 32 bit unsigned
        if( !(temp = matchRegex(CmdStr, re_spid)).empty() )     _cmd["xc_spid"]     = atoi(matchRegex(temp, str_num).c_str());
        if( !(temp = matchRegex(CmdStr, re_geo)).empty() )      _cmd["xc_geo"]      = atoi(matchRegex(temp, str_num).c_str());
        if( !(temp = matchRegex(CmdStr, re_sub)).empty() )      _cmd["xc_sub"]      = atoi(matchRegex(temp, str_num).c_str());
        if( !(temp = matchRegex(CmdStr, re_feeder)).empty() )   _cmd["xc_feeder"]   = atoi(matchRegex(temp, str_num).c_str());
        if( !(temp = matchRegex(CmdStr, re_zip)).empty() )      _cmd["xc_zip"]      = atoi(matchRegex(temp, str_num).c_str());
        if( !(temp = matchRegex(CmdStr, re_uda)).empty() )      _cmd["xc_uda"]      = atoi(matchRegex(temp, str_num).c_str());
        if( !(temp = matchRegex(CmdStr, re_program)).empty() )  _cmd["xc_program"]  = atoi(matchRegex(temp, str_num).c_str());
        if( !(temp = matchRegex(CmdStr, re_splinter)).empty() ) _cmd["xc_splinter"] = atoi(matchRegex(temp, str_num).c_str());
    }
}


const string& CtiCommandParser::getCommandStr() const
{
    return(_cmdString);
}

bool CtiCommandParser::isEqual(const string &cmdStr) const
{
    if(!_wasExternallyModified && cmdStr == _cmdString)
    {
        return true;
    }

    return false;
}

INT CtiCommandParser::convertTimeInputToSeconds(const string& _inStr) const
{
    std::string inStr(_inStr);
    INT val  = -1;
    INT mult = 60; //assume minutes for fun ok.

    static const boost::regex   re_scale("[hms]");
    std::string   temp2, valStr;


    if(!(valStr = matchRegex(inStr, re_num)).empty())
    {
        val = atoi(valStr.c_str());

        if(!(temp2 = matchRegex(inStr, re_scale)).empty())
        {
            if(temp2[0] == 'h')
            {
                mult = 3600.0;
            }
            else if(temp2[0] == 's')
            {
                mult = 1.0;
            }
        }

        val = val * mult;
    }

    return val;
}

string CtiCommandParser::asString()
{
    string rstr;

    rstr += "command=" + CtiNumStr(getCommand()) + ":";
    rstr += "flags="   + CtiNumStr(getFlags())   + ":";

    for( map_itr_type itr = _cmd.begin(); itr != _cmd.end(); ++itr )
    {
        rstr += ":";
        rstr += itr->first;
        rstr += "=";
        rstr += (itr->second.getString().empty())?"(none)":itr->second.getString();
        rstr += ",";
        rstr += CtiNumStr((*itr).second.getInt());
        rstr += ",";
        rstr += CtiNumStr((*itr).second.getReal());
    }

    return rstr;
}

int CtiCommandParser::getControlled() const
{
    int ctrld = UNCONTROLLED;

    if(getCommand() == ControlRequest)
    {
        if(getFlags() & CMD_FLAG_CTL_RESTORE)
        {
            ctrld = UNCONTROLLED;
        }
        else if(getFlags() & CMD_FLAG_CTL_TERMINATE)
        {
            ctrld = UNCONTROLLED;
        }
        else if(getFlags() & CMD_FLAG_CTL_CONNECT)
        {
            ctrld = UNCONTROLLED;
        }
        else if(getFlags() & CMD_FLAG_CTL_CLOSE)
        {
            ctrld = UNCONTROLLED;
        }
        else if(getFlags() & CMD_FLAG_CTL_SHED && (isKeyValid("shed") && getiValue("shed") <= 0) )
        {
            ctrld = UNCONTROLLED;
        }
        else if(isKeyValid("xcsetpoint") && getiValue("control_interval") <= 0 )
        {
            ctrld = UNCONTROLLED;
        }
        else
        {
            ctrld = CONTROLLED;                             // Juice is NOT flowing electrically speaking...!
        }
    }

    return ctrld;
}

bool CtiCommandParser::isControlled() const
{
    return( CONTROLLED == getControlled() );
}

bool CtiCommandParser::isTwoWay() const
{
    bool bret = false;

    if(getCommand() == GetValueRequest  ||
       getCommand() == GetConfigRequest ||
       getCommand() == GetStatusRequest ||
       getCommand() == ScanRequest      ||
       getCommand() == LoopbackRequest)
    {
        bret = true;
    }

    return bret;
}

bool CtiCommandParser::isDisconnect() const
{
    bool bret = false;

    if( getCommand() == ControlRequest &&
        getFlags() & (CMD_FLAG_CTL_CONNECT | CMD_FLAG_CTL_DISCONNECT) )
    {
        bret = true;
    }

    return bret;
}

CtiCommandParser& CtiCommandParser::setValue(const string &key, INT val)
{
    _wasExternallyModified = true;
    _cmd[key.c_str()] = CtiParseValue(val);
    return *this;
}
CtiCommandParser& CtiCommandParser::setValue(const string &key, DOUBLE val)
{
    _wasExternallyModified = true;
    _cmd[key.c_str()] = CtiParseValue(val);
    return *this;
}
CtiCommandParser& CtiCommandParser::setValue(const string &key, string val)
{
    _wasExternallyModified = true;
    _cmd[key.c_str()] = CtiParseValue(val.c_str());
    return *this;
}

void CtiCommandParser::doParseGetValueExpresscom(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);

    std::string   temp;

    static const boost::regex   re_tamper_info("xcom tamper info");
    static const boost::regex   re_dr_summary ("xcom dr summary");

    if(!(temp = matchRegex(CmdStr, re_tamper_info)).empty())
    {
        _cmd["xctamper"] = true;
    }
    if(!(temp = matchRegex(CmdStr, re_dr_summary)).empty())
    {
        _cmd["xcdrsummary"] = true;
    }
}

void  CtiCommandParser::doParseControlExpresscom(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    INT         iValue = 0;
    DOUBLE      dValue = 0.0;

    CHAR        tbuf[80];

    std::string   str;
    std::string   temp;
    std::string   valStr;
    std::string   token;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....


    if(!(temp = matchRegex(CmdStr, " mode (heat)|(cool)|(both)")).empty())
    {
        if(containsString(temp, "cool"))
        {
            iValue = 1;
        }
        else if(containsString(temp, "heat"))
        {
            iValue = 2;
        }
        else if(containsString(temp, "both"))
        {
            iValue = 3;
        }

        _cmd["xcmode"] = CtiParseValue( iValue );
    }

    if(containsString(CmdStr, " btp"))
    {
        _cmd["btp"] = CtiParseValue(true);

        const boost::regex btpCmd("btp \\w* " + str_floatnum + " ?[hms]?( |$)");

        if(!(temp = matchRegex(CmdStr,  btpCmd ) ).empty() )
        {
            removeRegexAllMatches(temp, "btp ");

            std::string alertLevel;
            if(! (alertLevel = matchRegex(temp, "\\w*")).empty() )
            {
                _cmd["btp_alert_level"] = alertLevel;
            }

            if(!(token = matchRegex(CmdStr, "btp .*" + str_floatnum + " *[hms]?( |$)")).empty())      // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["xctiertimeout"] = CtiParseValue( getDurationInSeconds(token) );
            }
        }
    }

    if(containsString(CmdStr, " rand"))
    {
        if(!(temp = matchRegex(CmdStr, " rand(om)? ?start [0-9]+")).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcrandstart"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = matchRegex(CmdStr, " rand(om)? ?stop [0-9]+")).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcrandstop"] = CtiParseValue( iValue );
            }
        }
    }

    if( icontainsString(CmdStr, " flip") )
    {
        _cmd["xcflip"] = CtiParseValue( TRUE );
    }
    if(!(token = matchRegex(CmdStr, " tcycle [0-9]+")).empty())
    {
        _cmd["xctcycle"] = CtiParseValue( TRUE );

        if(!(temp = matchRegex(token, re_num)).empty())
        {
            iValue = atoi(temp.c_str());
        }
        else
        {
            // Something went kinda wrong....
            CTILOG_WARN(dout, "Command Parameter Assumed.  Cycle control at 50 percent cycle. ");
            iValue = 50;
        }

        _cmd["cycle"] = CtiParseValue( (iValue) );
        _snprintf(tbuf, sizeof(tbuf), "CYCLE %d%%", iValue);

        if(!(token = matchRegex(CmdStr, " period [0-9]+")).empty())
        {
            if(!(temp = matchRegex(token, re_num)).empty())
            {
                INT _num = atoi(temp.c_str());
                _cmd["cycle_period"] = CtiParseValue( _num );
            }
        }

        if(!(token = matchRegex(CmdStr, " count [0-9]+")).empty())
        {
            if(!(temp = matchRegex(token, re_num)).empty())
            {
                INT _num = atoi(temp.c_str());
                _cmd["cycle_count"] = CtiParseValue( _num );
            }
        }

        if(!(temp = matchRegex(CmdStr, "ctrl temp " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcctrltemp"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = matchRegex(CmdStr, " limit " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xclimittemp"] = CtiParseValue( iValue );
            }

            // We need to look for the fallback %
            if(!(temp = matchRegex(CmdStr, " afallback " + str_num)).empty())
            {
                if(!(valStr = matchRegex(temp, re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["xclimitfbp"] = CtiParseValue( iValue );
                }
            }
        }

        if(!(temp = matchRegex(CmdStr, " maxrate " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcmaxdperh"] = CtiParseValue( iValue );
            }

            // We need to look for the fallback %
            if(!(temp = matchRegex(CmdStr, " bfallback " + str_num)).empty())
            {
                if(!(valStr = matchRegex(temp, re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["xcmaxdperhfbp"] = CtiParseValue( iValue );
                }
            }
        }
    }
    else if(containsString(CmdStr, " setpoint"))
    {
        _cmd["xcsetpoint"] = CtiParseValue( TRUE );

        if(containsString(CmdStr, " hold"))
        {
            _cmd["xcholdtemp"] = CtiParseValue( TRUE );
        }
        if(containsString(CmdStr, " bump"))
        {
            _cmd["xcbump"] = CtiParseValue( TRUE );
        }


        if(!(temp = matchRegex(CmdStr, " min " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcmintemp"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = matchRegex(CmdStr, " max " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcmaxtemp"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = matchRegex(CmdStr, " tr " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xctr"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = matchRegex(CmdStr, " ta " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcta"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = matchRegex(CmdStr, " tb " + str_num)).empty() && containsString(CmdStr, " dsb"))
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xctb"] = CtiParseValue( iValue );
            }

            if(!(temp = matchRegex(CmdStr, " dsb -?[0-9]+")).empty())
            {
                if(!(valStr = matchRegex(temp, "-?[0-9]+")).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["xcdsb"] = CtiParseValue( iValue );
                }
            }
        }

        if(!(temp = matchRegex(CmdStr, " tc " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xctc"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = matchRegex(CmdStr, " td " + str_num)).empty() && containsString(CmdStr, " dsd"))
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xctd"] = CtiParseValue( iValue );
            }

            if(!(temp = matchRegex(CmdStr, " dsd -?[0-9]+")).empty())
            {
                if(!(valStr = matchRegex(temp, "-?[0-9]+")).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["xcdsd"] = CtiParseValue( iValue );
                }
            }
        }

        if(!(temp = matchRegex(CmdStr, " te " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcte"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = matchRegex(CmdStr, " tf " + str_num)).empty() && containsString(CmdStr, " dsf"))
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xctf"] = CtiParseValue( iValue );
            }

            if(!(temp = matchRegex(CmdStr, " dsf -?[0-9]+")).empty())
            {
                if(!(valStr = matchRegex(temp, "-?[0-9]+")).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["xcdsf"] = CtiParseValue( iValue );
                }
            }
        }
        if(!(temp = matchRegex(CmdStr, " stage " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcstage"] = CtiParseValue( iValue );
            }
        }

        _actionItems.push_back("SETPOINT");
    }

    if(!(token = matchRegex(CmdStr, " targetcycle " + str_floatnum)).empty())
    {
        _cmd["xctargetcycle"] = CtiParseValue( TRUE );

        if(!(temp = matchRegex(token, str_floatnum)).empty())
        {
            dValue = atof(temp.c_str());
        }
        else
        {
            // Something went really wrong....
            CTILOG_ERROR(dout, "Command Parameter Assumed.  Cycle Control 0kw load. ");
            dValue = 0;
        }

        _cmd["target_kwh"] = CtiParseValue( (dValue) );
        string tempStr = " adjustments";
        tempStr += "( ";
        tempStr += str_anynum;
        tempStr += ")+";

        if(!(token = matchRegex(CmdStr, tempStr)).empty())
        {
            CtiTokenizer cmdtok(token);
            cmdtok(); //go past adjustment

            int bytes = 0;
            while( !(temp = cmdtok()).empty() )
            {
                bytes++;
                iValue = atoi(temp.c_str());
                _cmd["param_" + CtiNumStr(bytes)] = CtiParseValue( iValue );
            }
            _cmd["bytes_to_follow"] = CtiParseValue(bytes);
        }
        else
        {
            _cmd["bytes_to_follow"] = CtiParseValue(0);
        }
    }
    if(!(token = matchRegex(CmdStr, " backlight ")).empty())
    {
        _cmd["xcbacklight"] = CtiParseValue(TRUE);
        if(!(temp = matchRegex(CmdStr, " cycles " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcbacklightcycle"] = CtiParseValue( iValue );
            }
        }
        if(!(temp = matchRegex(CmdStr, " duty " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                if (iValue == 100)
                    iValue = 0;
                _cmd["xcbacklightduty"] = CtiParseValue( iValue );
            }
        }
        if(!(temp = matchRegex(CmdStr, " bperiod " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcbacklightperiod"] = CtiParseValue( iValue );
            }
        }
    }
    if(!(token = matchRegex(CmdStr, " priority " + str_num)).empty())
    {
        if(!(temp = matchRegex(token, re_num)).empty())
        {
            _cmd["xcpriority"] = CtiParseValue( atoi(temp.c_str()) );            // Expresscom only supports a 0 - 3 priority 0 highest.
        }
    }

    if(!(token = matchRegex(CmdStr, " cpp ")).empty()) //critical peak pricing
    {
        _cmd["xccpp"] = CtiParseValue(TRUE);
        doParseControlExpresscomCriticalPeakPricing(CmdStr);
    }
}


void  CtiCommandParser::doParsePutConfigExpresscom(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    CHAR *p;
    INT         _num;
    UINT        iValue = 0;
    CHAR        tbuf[80];

    std::string   str;
    std::string   strnum;
    std::string   temp;
    std::string   valStr;
    std::string   token;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(containsString(CmdStr, " sync"))
    {
        _cmd["xcsync"] = CtiParseValue( TRUE );
    }

    if(containsString(CmdStr, " setstate"))
    {
        _cmd["xcsetstate"] = CtiParseValue( TRUE );
        if( !isKeyValid("relaymask") )
        {
            _cmd["relaymask"] = CtiParseValue( 0x01 );
        }

        if(containsString(CmdStr, "run"))
        {
            _cmd["xcrunprog"] = CtiParseValue( TRUE );
        }

        if(containsString(CmdStr, " hold"))
        {
            _cmd["xcholdprog"] = CtiParseValue( TRUE );
        }

        if(!(temp = matchRegex(CmdStr, " timeout " + str_num)).empty())         // assume minutes input.
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xctimeout"] = CtiParseValue( iValue );            // In minutes
            }
        }

        if(!(temp = matchRegex(CmdStr, " (cooltemp|heattemp)")).empty())
        {
            _cmd["xctwosetpoints"] = CtiParseValue( TRUE );

            if(!(temp = matchRegex(CmdStr, " fan (on|circulate|auto)")).empty())
            {
                if(containsString(temp, "on"))
                {
                    _cmd["xcfanstate"] = CtiParseValue( 0x03 );
                }
                else if(containsString(temp, "auto"))
                {
                    _cmd["xcfanstate"] = CtiParseValue( 0x02 );
                }
                else if(containsString(temp, "circulate"))
                {
                    _cmd["xcfanstate"] = CtiParseValue( 0x01 );
                }
            }

            if(!(temp = matchRegex(CmdStr, " system (auto|off|heat|cool|emheat)")).empty())
            {
                if(containsString(temp, " off"))
                {
                    _cmd["xcsysstate"] = CtiParseValue( 0x04 );
                }
                else if(containsString(temp, " heat"))
                {
                    _cmd["xcsysstate"] = CtiParseValue( 0x08 );
                }
                else if(containsString(temp, " cool"))
                {
                    _cmd["xcsysstate"] = CtiParseValue( 0x0c );
                }
                else if(containsString(temp, " emheat"))
                {
                    _cmd["xcsysstate"] = CtiParseValue( 0x10 );
                }
                else if(containsString(temp, " auto"))
                {
                    _cmd["xcsysstate"] = CtiParseValue( 0x1c );
                }
            }

            if(!(temp = matchRegex(CmdStr, " cooltemp " + str_num)).empty())
            {
                if(!(valStr = matchRegex(temp, re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["xcsetcooltemp"] = CtiParseValue( iValue );
                }
            }
            if(!(temp = matchRegex(CmdStr, " heattemp " + str_num)).empty())
            {
                if(!(valStr = matchRegex(temp, re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["xcsetheattemp"] = CtiParseValue( iValue );
                }
            }
        }
        else
        {
            if(!(temp = matchRegex(CmdStr, " fan (on|off|auto|circulate)")).empty())
            {
                if(containsString(temp, "on"))
                {
                    _cmd["xcfanstate"] = CtiParseValue( 0x03 );
                }
                else if(containsString(temp, "auto"))
                {
                    _cmd["xcfanstate"] = CtiParseValue( 0x02 );
                }
                else if(containsString(temp, "off") || containsString(temp, "circulate"))
                {
                    // new devices have repurposed this to be circulate instead of off
                    _cmd["xcfanstate"] = CtiParseValue( 0x01 );
                }
            }

            if(!(temp = matchRegex(CmdStr, " system (off|heat|cool|emheat)")).empty())
            {
                if(containsString(temp, " off"))
                {
                    _cmd["xcsysstate"] = CtiParseValue( 0x04 );
                }
                else if(containsString(temp, " heat"))
                {
                    _cmd["xcsysstate"] = CtiParseValue( 0x08 );
                }
                else if(containsString(temp, " cool"))
                {
                    _cmd["xcsysstate"] = CtiParseValue( 0x0c );
                }
                else if(containsString(temp, " emheat"))
                {
                    _cmd["xcsysstate"] = CtiParseValue( 0x10 );
                }
            }

            if(!(temp = matchRegex(CmdStr, " temp " + str_num)).empty())
            {
                if(!(valStr = matchRegex(temp, re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["xcsettemp"] = CtiParseValue( iValue );
                }
            }
        }
    }

    if(containsString(CmdStr, " timesync"))
    {
        _cmd["xctimesync"] = CtiParseValue( TRUE );

        if(containsString(CmdStr, " date"))
        {
            _cmd["xcdatesync"] = CtiParseValue( TRUE );
        }


        if(!(temp = matchRegex(CmdStr, " [0-9]?[0-9]:")).empty())
        {
            int hh = atoi(temp.c_str());
            _cmd["xctimesync_hour"] = CtiParseValue( hh );

            if(!(temp = matchRegex(CmdStr, ":[0-9][0-9]")).empty())
            {
                int mm = atoi(temp.c_str() + 1);
                _cmd["xctimesync_minute"] = CtiParseValue( mm );
            }
        }

    }

    if(!(token = matchRegex(CmdStr, " raw( (0x)?[0-9a-f]+)+")).empty())
    {
        if(!(str = matchRegex(token, "( (0x)?[0-9a-f]+)+")).empty())
        {
            _cmd["xcrawconfig"] = CtiParseValue( str );
        }
    }

    if(!(token = matchRegex(CmdStr, " data( (0x)?[0-9a-f]+)+")).empty())
    {

        removeRegexAllMatches(token, " data");
        if(!(str = matchRegex(token, "( (0x)?[0-9a-f][0-9a-f])+")).empty())
        {
            _cmd["xcdata"] = CtiParseValue( str );
        }

        if(!(temp = matchRegex(CmdStr, "configbyte " + str_anynum)).empty())
        {
            if(!(valStr = matchRegex(temp, str_anynum)).empty())
            {
                iValue = strtoul(matchRegex(valStr, re_anynum).c_str(), &p, 0);
                _cmd["xcdatacfgbyte"] = CtiParseValue( iValue );
            }
        }
    }
    if(!(token = matchRegex(CmdStr, "data " + str_quoted_token)).empty())
    {
        _cmd["xcascii"] = CtiParseValue(TRUE);
        if(!(str = matchRegex(token, str_quoted_token)).empty() )
        {
            _cmd["xcdata"] = CtiParseValue(str.substr(1, str.length() - 2));
        }
        if(!(temp = matchRegex(CmdStr, "port " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcdataport"] = CtiParseValue( iValue );
            }
        }
        if(!(temp = matchRegex(CmdStr, " msgpriority " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcdatapriority"] = CtiParseValue( iValue );
            }
        }
        if(!(temp = matchRegex(CmdStr, " deletable")).empty())
        {
            _cmd["xcdeletable"] = CtiParseValue( TRUE );
        }

        if(!(temp = matchRegex(CmdStr, "timeout " + str_num)).empty() )
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcdatatimeout"] = CtiParseValue( iValue );
            }
        }
        if(!(temp = matchRegex(CmdStr, "hour")).empty())
        {
            _cmd["xchour"] = CtiParseValue( TRUE );
        }
        if(!(temp = matchRegex(CmdStr, "clear")).empty())
        {
            _cmd["xcclear"] = CtiParseValue( TRUE );
        }
    }

    if(!(token = matchRegex(CmdStr, "main(tenance)?( (0x)?[0-9a-f]+)+")).empty())
    {
        // Translates to a maintenance function
        if(!(str = matchRegex(token, "( (0x)?[0-9a-f]+)+")).empty())
        {
            _cmd["xcrawmaint"] = CtiParseValue( str );
        }
    }

    // The parser removes our "serial XYZ", so we have to go back to the original line.
    std::string originalCmdStr = _cmdString;
    if(!(token = matchRegex(originalCmdStr, re_target)).empty())
    {
        _cmd["xcgenericaddress"] = TRUE;

        if(!(valStr = matchRegex(token, "serial *" + str_anynum)).empty())
        {
            _num = strtoul(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_serial_target"] = CtiParseValue( _num );
        }
        if(!(valStr = matchRegex(token, "spid *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_spid_target"] = CtiParseValue( _num );
        }
        if(!(valStr = matchRegex(token, "geo *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_geo_target"] = CtiParseValue( _num );
        }
        if(!(valStr = matchRegex(token, "sub(station)? *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_sub_target"] = CtiParseValue( _num );
        }
        if(!(valStr = matchRegex(token, "feeder *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_feeder_target"] = CtiParseValue( _num );
        }
        if(!(valStr = matchRegex(token, "zip *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_zip_target"] = CtiParseValue( _num );
        }
        if(!(valStr = matchRegex(token, "uda *" + str_anynum)).empty() ||
           !(valStr = matchRegex(token, "user *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_uda_target"] = CtiParseValue( _num );
        }
        if(!(valStr = matchRegex(token, "program *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_program_target"] = CtiParseValue( _num );
        }
        if(!(valStr = matchRegex(token, "splinter *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_splinter_target"] = CtiParseValue( _num );
        }

        token = matchRegex(originalCmdStr, re_assign);

        if(!(valStr = matchRegex(token, "spid *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_spid"] = CtiParseValue( _num );
        }
        if(!(valStr = matchRegex(token, "geo *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_geo"] = CtiParseValue( _num );
        }
        if(!(valStr = matchRegex(token, "sub(station)? *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_sub"] = CtiParseValue( _num );
        }
        if(!(valStr = matchRegex(token, "feeder *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_feeder"] = CtiParseValue( _num );
        }
        if(!(valStr = matchRegex(token, "zip *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_zip"] = CtiParseValue( _num );
        }
        if(!(valStr = matchRegex(token, "uda *" + str_anynum)).empty() ||
           !(valStr = matchRegex(token, "user *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_uda"] = CtiParseValue( _num );
        }
        if(!(valStr = matchRegex(token, "relay *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_load"] = CtiParseValue( _num );
        }
        if(!(valStr = matchRegex(token, "program *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_program"] = CtiParseValue( _num );
        }
        if(!(valStr = matchRegex(token, "splinter *" + str_anynum)).empty())
        {
            _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

            _cmd["xca_splinter"] = CtiParseValue( _num );
        }

    } //Note that "assign" appears in re_target. This necessitates the "else" here
    else if(!(token = matchRegex(CmdStr, "((assign)|(address))")).empty())
    {
        {
            _cmd["xcaddress"] = TRUE;

            if(!(valStr = matchRegex(CmdStr, " s[ =]*(0x)?[0-9a-f]+")).empty())
            {
                _num = strtol(matchRegex(valStr, re_anynum).c_str(), &p, 0);

                _cmd["xca_spid"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG SPID = %d", _num);
                _actionItems.push_back(tbuf);
            }
            if(!(valStr = matchRegex(CmdStr, " g[ =]*[0-9]+")).empty())
            {
                _num = strtol(matchRegex(valStr, re_num).c_str(), &p, 10);
                _cmd["xca_geo"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG GEO = %d", _num);
                _actionItems.push_back(tbuf);
            }
            if(!(valStr = matchRegex(CmdStr, " b[ =]*[0-9]+")).empty())
            {
                _num = strtol(matchRegex(valStr, re_num).c_str(), &p, 10);
                _cmd["xca_sub"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG SUBSTATION = %d", _num);
                _actionItems.push_back(tbuf);
            }
            if(!(valStr = matchRegex(CmdStr, " f[ =]*[0-9]+")).empty())
            {
                _num = strtol(matchRegex(valStr, re_num).c_str(), &p, 10);
                _cmd["xca_feeder"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG FEEDER = %d", _num);
                _actionItems.push_back(tbuf);
            }
            if(!(valStr = matchRegex(CmdStr, " z[ =]*[0-9]+")).empty())
            {
                _num = strtol(matchRegex(valStr, re_num).c_str(), &p, 10);
                _cmd["xca_zip"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG ZIP = %d", _num);
                _actionItems.push_back(tbuf);
            }
            if(!(valStr = matchRegex(CmdStr, " u[ =]*[0-9]+")).empty())
            {
                _num = strtol(matchRegex(valStr, re_num).c_str(), &p, 10);
                _cmd["xca_uda"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG UDA = %d", _num);
                _actionItems.push_back(tbuf);
            }

            std::string programtemp;
            std::string splintertemp;

            if(!(valStr = matchRegex(CmdStr, " p[ =]*[0-9]+( *, *[0-9]+)*")).empty())
            {
                removeRegexAllMatches(valStr, " p[ =]*");
                _cmd["xca_program"] = CtiParseValue( valStr );
                programtemp = valStr;
            }
            if(!(valStr = matchRegex(CmdStr, " r[ =]*[0-9]+( *, *[0-9]+)*")).empty())
            {
                removeRegexAllMatches(valStr, " r[ =]*");
                _cmd["xca_splinter"] = CtiParseValue( valStr );

                splintertemp = valStr;
            }

            if(!(token = matchRegex(CmdStr, "(relay|load) [0-9]+( *, *[0-9]+)*")).empty())
            {
                INT i;
                INT mask = 0;
                CtiTokenizer ptok(programtemp);
                CtiTokenizer rtok(splintertemp);
                std::string tempstr;
                std::string ptemp;
                std::string rtemp;

                for(i = 0; i < 15; i++)
                {
                    std::string numstr = CtiNumStr(i+1).toString().c_str();
                    if(!(temp = matchRegex(token, numstr.c_str())).empty())
                    {
                        mask |= (0x01 << i);

                        {
                            tempstr = ptok(",");
                            if(!tempstr.empty()) ptemp = tempstr;

                            tempstr = rtok(",");
                            if(!tempstr.empty()) rtemp = tempstr;

                            if(!ptemp.empty())
                            {
                                _snprintf(tbuf, sizeof(tbuf), "CONFIG LOAD %d to PROGRAM = %s", i+1, ptemp.c_str());
                                _actionItems.push_back(tbuf);
                            }

                            if(!rtemp.empty())
                            {
                                _snprintf(tbuf, sizeof(tbuf), "CONFIG LOAD %d to SPLINTER = %s", i+1, rtemp.c_str());
                                _actionItems.push_back(tbuf);
                            }
                        }
                    }
                }

                if(mask)
                {
                    _cmd["xca_loadmask"] = CtiParseValue( mask );
                }
            }

        }
    }

    if(containsString(CmdStr, " serv"))
    {
        if(containsString(CmdStr, "temp") && !(token = matchRegex(CmdStr, "serv(ice)? (in|out|enable|disable)")).empty())
        {
            CHAR buf[80];
            INT offtime = 0;
            INT cancel = 0;  // default to cancelling a previous t-service message.
            INT bitP = 0;    // default to using cold load
            INT bitL = 0;    // default to using the LEDs

            if(containsString(token, " out") || containsString(token, " disable"))
            {
                cancel = 1;
                _snprintf(buf, sizeof(buf), "SERVICE DISABLE TEMPORARY");
            }
            else
            {
                _snprintf(buf, sizeof(buf), "SERVICE ENABLE TEMPORARY");
            }

            if(containsString(CmdStr, " noclp"))
            {
                bitP = 1;
            }
            if(containsString(CmdStr, " noled"))
            {
                bitL = 1;
            }

            if(!(token = matchRegex(CmdStr, " offhours " + str_num)).empty())
            {
                str = matchRegex(token, re_num);
                offtime = atoi(str.c_str());
            }

            _cmd["xctservicebitp"] = CtiParseValue( bitP );
            _cmd["xctservicebitl"] = CtiParseValue( bitL );
            _cmd["xctservicetime"] = CtiParseValue( offtime );
            _cmd["xctservicecancel"] = CtiParseValue( cancel );
            _actionItems.push_back(buf);
        }
        else if(!(token = matchRegex(CmdStr, "serv(ice)? (in|out|enable|disable)( (relay|load) [0-9]+)?")).empty())
        {
            CHAR buf[80];
            INT service_flag = 0;

            if(containsString(token, " in") || containsString(token, " enable"))
            {
                service_flag |= 0x80;
                _snprintf(buf, sizeof(buf), "SERVICE ENABLE");
            }
            else if(containsString(token, " out") || containsString(token, " disable"))
            {
                service_flag |= 0x00;
                _snprintf(buf, sizeof(buf), "SERVICE DISABLE");
            }

            if(!(str = matchRegex(token, "(relay|load) " + str_num)).empty() )
            {
                CtiTokenizer   tok2(str);
                tok2();  // hop over relay | load.
                str = tok2();
                INT load = atoi(str.c_str());
                service_flag |= (load & 0x0f);
            }

            _cmd["xcpservice"] = CtiParseValue( service_flag );
            _actionItems.push_back(buf);
        }
    }
    else if(!(token = matchRegex(CmdStr, " ovuv[ =]+((ena(ble)?)|(dis(able)?))")).empty())
    {
        int   op = 0;
        CHAR  op_name[20];

        if(containsString(token, "ena"))
        {
            op = 1;
            _snprintf(op_name, sizeof(op_name), "ENABLE");
        }
        else if(containsString(token, "dis"))
        {
            op = 0;
            _snprintf(op_name, sizeof(op_name), "DISABLE");
        }

        _cmd["ovuv"] = CtiParseValue( op );

        _snprintf(tbuf, sizeof(tbuf), "OVUV %s", op_name);
        _actionItems.push_back(tbuf);
    }
    else if(containsString(CmdStr, "schedule"))
    {
        _cmd["xcschedule"] = TRUE;
        doParsePutConfigThermostatSchedule(CmdStr);
    }
    else if(containsString(CmdStr, " cbc"))
    {
        doParsePutConfigCBC(CmdStr);
    }
    else if(containsString(CmdStr, "utility usage"))
    {
        _cmd["xcutilusage"] = TRUE;
        doParsePutConfigUtilityUsage(CmdStr);
    }
    else if(containsString(CmdStr, "price tier"))
    {
        if(!(token = matchRegex(CmdStr, "price tier " + str_num)).empty())
        {
            str = matchRegex(token, re_num);
            UINT tier = atoi(str.c_str());
            if(tier < 5) //valid range = 0 to 4
            {
                _cmd["xcpricetier"] = CtiParseValue( tier );
            }
        }
    }
    else if(containsString(CmdStr, "command initiator"))
    {
        if(!(token = matchRegex(CmdStr, "command initiator " + str_num)).empty())
        {
            str = matchRegex(token, re_num);
            UINT cmdId = atoi(str.c_str()) & 0xFF;
            _cmd["xccmdinitiator"] = CtiParseValue( cmdId );
        }
    }
    else if(containsString(CmdStr, "compare rssi"))
    {
        _cmd["xccomparerssi"] = CtiParseValue(TRUE);
    }
    else if (containsString(CmdStr, "thermo config"))
    {
        _cmd["xcconfig"] = TRUE;
        if(!(token = matchRegex(CmdStr, "thermo config " + str_num)).empty())
        {
            str = matchRegex(token, re_num);
            int config = atoi(str.c_str()) & 0xFF;

            _cmd["xcthermoconfig"] = CtiParseValue( config );
        }
    }
    else if (containsString(CmdStr, "extended tier"))
    {
        _cmd["xcextier"] = TRUE;
        if(!(token = matchRegex(CmdStr, " tier " + str_num)).empty())
        {
            str = matchRegex(token, re_num);
            int tier = atoi(str.c_str());
            _cmd["xcextierlevel"] = CtiParseValue(tier);

            if(!(token = matchRegex(CmdStr, " rate " + str_num)).empty())
            {
                str = matchRegex(token, re_num);
                int rate = atoi(str.c_str());
                _cmd["xcextierrate"] = CtiParseValue(rate);
            }

            if(!(token = matchRegex(CmdStr, " (cmd|command) " + str_num)).empty())
            {
                str = matchRegex(token, re_num);
                int command = atoi(str.c_str());
                _cmd["xcextiercmd"] = CtiParseValue(command);
            }
            if(!(token = matchRegex(CmdStr, " display " + str_num)).empty())
            {
                str = matchRegex(token, re_num);
                int display = atoi(str.c_str());
                _cmd["xcextierdisp"] = CtiParseValue(display);
            }
            if(!(temp = matchRegex(CmdStr, " timeout " + str_num)).empty())         // assume minutes input.
            {
                if(!(valStr = matchRegex(temp, re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["xctiertimeout"] = CtiParseValue( iValue );            // In minutes
                }
            }
            if(!(temp = matchRegex(CmdStr, " delay " + str_num)).empty())         // assume minutes input.
            {
                if(!(valStr = matchRegex(temp, re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["xctierdelay"] = CtiParseValue( iValue );            // In minutes
                }
            }

        }

    }
    else if (containsString(CmdStr, "display"))
    {
        _cmd["xcdisplay"] = TRUE;
        if(!(token = matchRegex(CmdStr, " setup (lcd)|(seg)")).empty())
        {
            if (containsString(token, "lcd"))
                _cmd["xclcddisplay"] = CtiParseValue(TRUE);
            else
                _cmd["xclcddisplay"] = CtiParseValue(FALSE);
        }
        else if(!(token = matchRegex(CmdStr, " display " + str_num + " " + str_quoted_token)).empty())
        {
            str = matchRegex(token, re_num);
            int msgid = atoi(str.c_str());
            _cmd["xcdisplaymessageid"]  = CtiParseValue(msgid );

            if(!(str = matchRegex(token, str_quoted_token)).empty())
            {
                _cmd["xcdisplaymessage"] = CtiParseValue(str.substr(1, str.length() - 2));
            }
        }
    }
    else if(containsString(CmdStr, "utility info"))
    {
        _cmd["xcutilinfo"] = TRUE;

        if(!(token = matchRegex(CmdStr, "chan " + str_num)).empty())
        {
            str = matchRegex(token, re_num);
            int chan = atoi(str.c_str()) - 1;

            _cmd["xcutilchan"] = CtiParseValue( chan );
        }
        _cmd["xcdisplaycost"] = CtiParseValue(FALSE);
        _cmd["xcdisplayusage"] = CtiParseValue(FALSE);
        _cmd["xcchargecents"] = CtiParseValue(FALSE);
        if(!(token = matchRegex(CmdStr, "cost")).empty())
        {
            _cmd["xcdisplaycost"] = CtiParseValue(TRUE);
        }
        if(!(token = matchRegex(CmdStr, "usage")).empty())
        {
            _cmd["xcdisplayusage"] = CtiParseValue(TRUE);
        }
        if(!(token = matchRegex(CmdStr, "cents")).empty())
        {
            _cmd["xcchargecents"] = CtiParseValue(TRUE);
        }
        if(!(token = matchRegex(CmdStr, str_quoted_token)).empty())
        {
            if(!(str = matchRegex(token, str_quoted_token)).empty())
            {
                _cmd["xcoptionalstring"] = CtiParseValue(str.substr(1, str.length() - 2));
            }
        }
    }
    else if(containsString(CmdStr, " cold"))
    {
        if(!(token = matchRegex(CmdStr, "cold[a-z_]*" \
                                  "(( *r[=][0-9]+[hms]?)|( *r[0-9]+[=][0-9]+[hms]?)*)")).empty())
        {
            //This code is this way to match what verscom does.
            CtiTokenizer   cold_tok(token);

            _cmd["xccold"] = CtiParseValue(TRUE);

            if(!(strnum = matchRegex(token, "r[=][0-9]+[hms]?")).empty())
            {
                strnum = strnum.substr(2);
                _num = convertTimeInputToSeconds(strnum);
                _cmd["xccoldload_r"] = CtiParseValue( _num );
            }
            else
            {
                cold_tok();//go past cold?
                token = cold_tok();
                while(!(strnum = matchRegex(token, "r[0-9]+[=][0-9]+[hms]?")).empty())
                {
                    token = cold_tok();
                    std::string relay = matchRegex(strnum, "r[0-9]+");
                    std::string cold_time = matchRegex(strnum, "[=][0-9]+[hms]?");

                    boost::trim_left_if(relay,     is_char{'r'});
                    boost::trim_left_if(cold_time, is_char{'='});

                    _num = convertTimeInputToSeconds(cold_time);
                    string identifier = "xccoldload_r";
                    identifier += relay;
                    _cmd[identifier] = CtiParseValue(_num);

                }
            }
        }
    }
    else if(containsString(CmdStr, " lcrmode"))
    {
        if(!(token = matchRegex(CmdStr, "lcrmode *[a-zA-Z]*")).empty())
        {
            CtiTokenizer lcrmode_tok( token );

            lcrmode_tok();   // Get us past "lcrmode"

            temp = lcrmode_tok();
            CtiToLower(temp);

            _cmd["lcrmode"] = CtiParseValue(true);
            if(containsString(temp, "ex"))
            {
                _cmd["modexcom"] = CtiParseValue(true);
            }
            if(containsString(temp, "em"))
            {
                _cmd["modeemetcon"] = CtiParseValue(true);
            }
            if(containsString(temp, "v"))
            {
                _cmd["modevcom"] = CtiParseValue(true);
            }
            if(containsString(temp, "g"))
            {
                _cmd["modegolay"] = CtiParseValue(true);
            }
        }
    }
    else if(containsString(CmdStr, " gold"))
    {
        if(!(token = matchRegex(CmdStr, "gold [0-9]")).empty())
        {
            CtiTokenizer gold_tok( token );

            gold_tok();   // Get us past "gold"

            _cmd["gold"] = CtiParseValue(atoi(gold_tok().c_str()));
        }
    }
    else if(containsString(CmdStr, " silver"))
    {
        if(!(token = matchRegex(CmdStr, "silver [0-9]*")).empty())
        {
            CtiTokenizer silver_tok( token );

            silver_tok();   // Get us past "silver"

            _cmd["silver"] = CtiParseValue(atoi(silver_tok().c_str()));
        }
    }
    else if(containsString(CmdStr, " targetloadamps"))
    {
        if(!(token = matchRegex(CmdStr, "targetloadamps " + str_floatnum)).empty())
        {
            CtiTokenizer tla_tok( token );

            tla_tok();   // Get us past "targetloadamps"

            _cmd["xctargetloadamps"] = CtiParseValue(atof(tla_tok().c_str()));
        }
    }
    else if(containsString(CmdStr, " preferredchannels"))
    {
        if(!(token = matchRegex(CmdStr, "preferredchannels " + str_floatnum_list)).empty())
        {
            _cmd["preferredchannellist"] = CtiParseValue(matchRegex(CmdStr, str_floatnum_list));
        }
    }
}

void  CtiCommandParser::doParsePutStatusExpresscom(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    CHAR        tbuf[80];

    std::string   str;
    std::string   temp;
    std::string   valStr;
    std::string   token;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!(token = matchRegex(CmdStr, " prop[a-z]*[ =]+((disp[a-z]*)|(inc[a-z]*)|(term[a-z]*)|(rssi)|(ping)|(test))")).empty())
    {
        int   op = -1;
        CHAR  op_name[20];

        if(containsRegex(token, "disp(lay)?"))                   // Turn on light & bump counter.
        {
            op = 0x002;
            _snprintf(op_name, sizeof(op_name), "DISPLAY");
        }
        else if(containsRegex(token, "inc(rement)?"))            // bump counter.
        {
            op = 0x001;
            _snprintf(op_name, sizeof(op_name), "INCREMENT");
        }
        else if(containsRegex(token, "term(inate)?"))            // Turn off light.
        {
            op = 0x000;
            _snprintf(op_name, sizeof(op_name), "TERMINATE");
        }
        else if(containsString(token, "rssi"))
        {
            op = 0x003;
            _snprintf(op_name, sizeof(op_name), "RSSI");
        }
        else if(containsString(token, "test"))
        {
            op = 0x004;
            _snprintf(op_name, sizeof(op_name), "TEST");
        }
        else if(containsString(token, "ping"))
        {
            op = 0x080;
            _snprintf(op_name, sizeof(op_name), "PING");
        }

        if(op != -1)
        {
            CHAR  buf[80];
            _cmd["xcproptest"] = CtiParseValue( op );

            _snprintf(buf, sizeof(buf), "PROP TEST: %01x = %s", op, op_name);
            _actionItems.push_back(buf);
        }
    }
    else if(!(token = matchRegex(CmdStr, " ovuv[ =]+((ena(ble)?)|(dis(able)?))")).empty())
    {
        int   op = 0;
        CHAR  op_name[20];

        if(containsString(token, "ena"))
        {
            op = 1;
            _snprintf(op_name, sizeof(op_name), "ENABLE");
        }
        else if(containsString(token, "dis"))
        {
            op = 0;
            _snprintf(op_name, sizeof(op_name), "DISABLE");
        }

        _cmd["ovuv"] = CtiParseValue( op );

        _snprintf(tbuf, sizeof(tbuf), "OVUV %s", op_name);
        _actionItems.push_back(tbuf);
    }
}

void CtiCommandParser::doParsePutConfigThermostatSchedule(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    std::string   str;
    std::string   temp;
    std::string   valStr;
    std::string   token;

    CtiTokenizer   tok(CmdStr);

    {
        INT key;

        token = tok(" ,");              // Prime it up.. Shoud be a putconfig.

        while(!token.empty())
        {
            key = isTokenThermostatScheduleDOW(token);
            while(key >= 0)
            {
                doParsePutConfigThermostatScheduleDOW(tok,key);     // This method should finish the rest of this key!
            }

            token = tok(" ,"); // Move us forward.
        }
    }
}

INT CtiCommandParser::isTokenThermostatScheduleDOW(string &token)
{
    INT dow = -1;

    if(token == "weekday")
    {
        dow = 7;
    }
    else if(token == "weekend")
    {
        dow = 8;
    }
    else if(token == "all")
    {
        dow = 9;
    }
    else if(token == "sun")
    {
        dow = 0;
    }
    else if(token == "mon")
    {
        dow = 1;
    }
    else if(token == "tue")
    {
        dow = 2;
    }
    else if(token == "wed")
    {
        dow = 3;
    }
    else if(token == "thu")
    {
        dow = 4;
    }
    else if(token == "fri")
    {
        dow = 5;
    }
    else if(token == "sat")
    {
        dow = 6;
    }

    return dow;
}

void CtiCommandParser::doParsePutConfigThermostatScheduleDOW(CtiTokenizer &tok, int &key)
{
    std::string token;
    INT currentkey = key;   // The key which got us here.
    int pod = 0;            // Period of the day on which we begin!.
    int component = 0;
    BYTE hh = 0xff;
    BYTE mm = 0xff;
    BYTE heat = 0xff;
    BYTE cool = 0xff;

    while( !(token = tok(" :,")).empty() )
    {
        if((key = isTokenThermostatScheduleDOW(token)) >= 0)
        {
            // The current token is a NEW key!
            break;
        }

        //  note that the below atoi() calls may not work if the token is
        //    something like "sakj_3289", since it'll say that it contains
        //    a number, yet not be parseable by atoi()
        switch(component)
        {
        case 0:
            {
                // This is the hh section of the time
                if(containsRegex(token, re_num))
                {
                    hh = atoi(token.c_str());
                }
                else if(containsString(token, "hh"))
                {
                    hh = 254;                           // This is the Energy Pro period cancel indicator.
                }
                break;
            }
        case 1:
            {
                // This is the mm section of the time
                if(containsRegex(token, "[0-9]+"))
                    mm = atoi(token.c_str());
                break;
            }
        case 2:
            {
                // This is the heat temperature section
                if(containsRegex(token, "[0-9]+"))
                    heat = atoi(token.c_str());
                break;
            }
        case 3:
            {
                // This is the cool temperature section
                if(containsRegex(token, "[0-9]+"))
                    cool = atoi(token.c_str());
                break;
            }
        default:
            {
                break;
            }
        }
        component++;

        if(component > 3)       // We got them all!
        {
            BYTE per = ( ((BYTE)currentkey) << 4 | ((BYTE)pod & 0x0f) );

            std::string hhstr("xctodshh_" + CtiNumStr(per));
            std::string mmstr("xctodsmm_" + CtiNumStr(per));
            std::string heatstr("xctodsheat_" + CtiNumStr(per));
            std::string coolstr("xctodscool_" + CtiNumStr(per));

            _cmd[hhstr]   = hh;
            _cmd[mmstr]   = mm;
            _cmd[heatstr] = heat;
            _cmd[coolstr] = cool;

            component = 0;
            pod++;  // Look for next period of the day!
            hh = 0xff;
            mm = 0xff;
            heat = 0xff;
            cool = 0xff;
        }
    }
}

void CtiCommandParser::doParseControlSA(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    INT         iValue = 0;

    std::string   temp;
    std::string   valStr;

    CtiTokenizer   tok(CmdStr);

    temp = tok(); // Get the first one into the hopper....


    _cmd["sa_f1bit"] = 0;


    // Needed for serial commands.
    if(containsString(CmdStr, " utility"))
    {
        if(!(temp = matchRegex(CmdStr, " utility " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["sa_utility"] = CtiParseValue( iValue );
            }
        }
    }

    if(containsString(CmdStr, " protocol_priority"))
    {
        if(!(temp = matchRegex(CmdStr, " protocol_priority [0-3]")).empty())
        {
            if(!(valStr = matchRegex(temp, "[0-3]")).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["sa_priority"] = CtiParseValue( iValue );
            }
        }
    }

    //  042005 CGP:  NOT TO BE USED FOR SA205.  Used for LED FLASHing on SA305
    if(containsString(CmdStr, " ledrepeats"))
    {
        if(!(temp = matchRegex(CmdStr, " ledrepeats " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["sa_reps"] = CtiParseValue( iValue - 1 );
            }
        }
    }

    if(containsString(CmdStr, " function"))
    {
        if(!(temp = matchRegex(CmdStr, " function [01][01][01][01]")).empty())
        {
            if(!(valStr = matchRegex(temp, "[01][01][01][01]")).empty())
            {
                iValue = binaryStringToInt(valStr.c_str(), valStr.length());
                _cmd["sa_function"] = CtiParseValue(iValue);
            }
        }
        else if(!(temp = matchRegex(CmdStr, " function " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["sa_function"] = CtiParseValue(iValue);
            }
        }
    }

    bool abrupt = false;

    // DEFAULT CHOICES BASED UPON COMMANDS.  NOTE that abrupt is set true if restore!
    if( containsString(CmdStr, " restore") || containsString(CmdStr, " shed"))
    {
        abrupt = true;
        _cmd["sa_dlc_mode"] = TRUE;
        _cmd["sa_f0bit"] = 0;
    }
    else if(containsString(CmdStr, " cycle") || containsString(CmdStr, " terminate"))
    {
        _cmd["sa_dlc_mode"] = FALSE;
        _cmd["sa_f0bit"] = 1;
    }

    // DEFAULT CHOICES MAY BE MODIFIED BY OPTIONAL MODIFIERS.
    if(containsString(CmdStr, " dlc"))
    {
        _cmd["sa_dlc_mode"] = TRUE;
        _cmd["sa_f0bit"] = 0;
    }
    else if(containsString(CmdStr, " di"))
    {
        _cmd["sa_dlc_mode"] = FALSE;
        _cmd["sa_f0bit"] = 1;
    }

    if(containsString(CmdStr, " abrupt"))
    {
        abrupt = true;
    }
    else if(containsString(CmdStr, " graceful"))
    {
        abrupt = false;
    }

    if(containsString(CmdStr, " restore") || containsString(CmdStr, " terminate") )    // This parse must be done following the check for dlc.
    {
        _cmd["sa_restore"] = TRUE;
        _cmd["sa_reps"] = abrupt ? 0 : 1;
        _cmd["sa_strategy"] = CtiParseValue(61);        // This is the defined strategy.
    }
    else if(!(temp = matchRegex(CmdStr, " strategy [01][01][01][01][01][01]")).empty())
    {
        // This is a binary...
        if(!(valStr = matchRegex(temp, "[01][01][01][01][01][01]")).empty())
        {
            iValue = binaryStringToInt(valStr.c_str(), valStr.length());
            _cmd["sa_strategy"] = CtiParseValue(iValue);    // If not explicitly called out, this is inferred from the period and cycle percent.
        }
    }
}

void CtiCommandParser::doParsePutConfigSA(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    INT         _num;
    INT         iValue = 0;

    CHAR tbuf[80];
    std::string   valStr;
    std::string   temp, token, strnum;

    CtiTokenizer   tok(CmdStr);

    temp = tok(); // Get the first one into the hopper....


    if(containsString(CmdStr, " utility"))
    {
        if(!(temp = matchRegex(CmdStr, " utility " + str_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["sa_utility"] = CtiParseValue( iValue );
            }
        }
    }

    if(containsString(CmdStr, " protocol_priority"))
    {
        if(!(temp = matchRegex(CmdStr, " protocol_priority [0-3]+")).empty())
        {
            if(!(valStr = matchRegex(temp, "[0-3]+")).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["sa_priority"] = CtiParseValue( iValue );
            }
        }
    }

    if(containsString(CmdStr, " tamper"))
    {
        std::string to_be_matched = "tamper[ a-z_]*"
                                    "( *f[12][ =]*" + str_num + ")"
                                    "( *f[12][ =]*" + str_num + ")?";

        if(!(token = matchRegex(CmdStr, to_be_matched)).empty())
        {
            _cmd["sa_tamper"] = TRUE;

            if(!(strnum = matchRegex(token, "f1[ =]*" + str_num)).empty())
            {
                strnum = strnum.substr(2); // Blank the r1 to prevent matches on the 1
                _num = atoi(matchRegex(strnum, "[0-9]+").c_str());
                _cmd["tamperdetect_f1"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG TAMPER COUNT R1 = %d", _num);
                _actionItems.push_back(tbuf);
            }
            if(!(strnum = matchRegex(token, "f2[ =]*" + str_num)).empty())
            {
                strnum = strnum.substr(2); // Blank the r1 to prevent matches on the 1
                _num = atoi(matchRegex(strnum, "[0-9]+").c_str());
                _cmd["tamperdetect_f2"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG TAMPER COUNT R2 = %d", _num);
                _actionItems.push_back(tbuf);
            }
        }
    }

    temp = matchRegex(CmdStr, " override " + str_num);

    if(!temp.empty())
    {
        _cmd["sa_f1bit"] = 0;
        _cmd["sa_f0bit"] = 0;

        if(!(valStr = matchRegex(temp, re_num)).empty())
        {
            iValue = atoi(valStr.c_str());
            _cmd["sa_override"] = CtiParseValue(iValue);    // Hours switch is off.
            _cmd["sa_strategy"] = CtiParseValue(62);        // This is the defined strategy.
        }
    }
    else if(!(temp = matchRegex(CmdStr, " setled " + str_num)).empty())
    {
        _cmd["sa_f1bit"] = 0;
        _cmd["sa_f0bit"] = 1;

        if(!(valStr = matchRegex(temp, re_num)).empty())
        {
            iValue = atoi(valStr.c_str());
            _cmd["sa_setled"] = CtiParseValue(iValue);
            _cmd["sa_strategy"] = CtiParseValue(63);
        }
    }
    else if(!(temp = matchRegex(CmdStr, " flashled " + str_num)).empty())
    {
        _cmd["sa_f1bit"] = 0;
        _cmd["sa_f0bit"] = 1;

        if(!(valStr = matchRegex(temp, re_num)).empty())
        {
            iValue = atoi(valStr.c_str());
            _cmd["sa_flashled"] = CtiParseValue(iValue);
            _cmd["sa_strategy"] = CtiParseValue(63);
        }
    }
    else if(containsString(CmdStr, " flashrate"))
    {
        _cmd["sa_f1bit"] = 0;
        _cmd["sa_f0bit"] = 0;

        _cmd["sa_flashrate"] = CtiParseValue(TRUE);
        _cmd["sa_strategy"] = CtiParseValue(63);
    }
    else if(!(temp = matchRegex(CmdStr, " setpriority [0-3]")).empty())
    {
        _cmd["sa_f1bit"] = 0;
        _cmd["sa_f0bit"] = 0;

        if(!(valStr = matchRegex(temp, "[0-3]")).empty())
        {
            iValue = atoi(valStr.c_str());
            _cmd["sa_setpriority"] = CtiParseValue(iValue);
            _cmd["sa_strategy"] = CtiParseValue(63);
        }
    }
    else
    {
        _cmd["sa_f1bit"] = 1;

        if(containsString(CmdStr, " assign"))
        {
            char *p;
            CHAR        sabuf[80];

            _cmd["sa_f0bit"] = 1;
            _cmd["sa_assign"] = TRUE;

            if(!isKeyValid("sa_utility") &&  !(valStr = matchRegex(CmdStr, " u[ =]*" + str_num)).empty())  // Must be included (either way is ok)
            {
                _num = strtol(matchRegex(valStr, re_num).c_str(), &p, 0);
                _cmd["sa_utility"] = CtiParseValue( _num );
            }
            if(!(valStr = matchRegex(CmdStr, " g[ =]*" + str_num)).empty())
            {
                _num = strtol(matchRegex(valStr, re_num).c_str(), &p, 0);
                _cmd["sa_group"] = CtiParseValue( _num );

                _snprintf(sabuf, sizeof(sabuf), "CONFIG GROUP = %d", _num);
                _actionItems.push_back(sabuf);
            }
            if(!(valStr = matchRegex(CmdStr, " d[ =]*" + str_num)).empty())
            {
                _num = strtol(matchRegex(valStr, re_num).c_str(), &p, 0);
                _cmd["sa_division"] = CtiParseValue( _num );

                _snprintf(sabuf, sizeof(sabuf), "CONFIG DIVISION = %d", _num);
                _actionItems.push_back(sabuf);
            }
            if(!(valStr = matchRegex(CmdStr, " s[ =]*" + str_num)).empty())
            {
                _num = strtol(matchRegex(valStr, re_num).c_str(), &p, 0);
                _cmd["sa_substation"] = CtiParseValue( _num );

                _snprintf(sabuf, sizeof(sabuf), "CONFIG SUBSTATION = %d", _num);
                _actionItems.push_back(sabuf);
            }
            if(!(valStr = matchRegex(CmdStr, " [pr][ =]*" + str_num)).empty())
            {
                _num = strtol(matchRegex(valStr, re_num).c_str(), &p, 0);
                _cmd["sa_package"] = CtiParseValue( _num );

                _snprintf(sabuf, sizeof(sabuf), "CONFIG RATE PACKAGE = %d", _num);
                _actionItems.push_back(sabuf);
            }
            else
            {
                if(!(valStr = matchRegex(CmdStr, " f[ =]*" + str_num)).empty())
                {
                    _num = strtol(matchRegex(valStr, re_num).c_str(), &p, 0);
                    _cmd["sa_family"] = CtiParseValue( _num );

                    _snprintf(sabuf, sizeof(sabuf), "CONFIG FAMILY = %d", _num);
                    _actionItems.push_back(sabuf);
                }
                if(!(valStr = matchRegex(CmdStr, " m[ =]*" + str_num)).empty())
                {
                    _num = strtol(matchRegex(valStr, re_num).c_str(), &p, 0);
                    _cmd["sa_member"] = CtiParseValue( _num );

                    _snprintf(sabuf, sizeof(sabuf), "CONFIG MEMBER = %d", _num);
                    _actionItems.push_back(sabuf);
                }
            }
        }
        else
        {
            _cmd["sa_f0bit"] = 0; // This is a short stuff config.
            if(containsString(CmdStr, " coldload"))
            {
                _cmd["sa_coldload"] = TRUE;
                // Assume seconds is the input here!
                if(!(temp = matchRegex(CmdStr, " f1[ =]+" + str_num)).empty())
                {
                    removeRegexAllMatches(temp, " f1[ =]+");
                    if(!(valStr = matchRegex(temp, re_num)).empty())
                    {
                        iValue = atoi(valStr.c_str());
                        _cmd["sa_clpf1"] = CtiParseValue(iValue);
                    }
                }
                if(!(temp = matchRegex(CmdStr, " f2[ =]+" + str_num)).empty())
                {
                    removeRegexAllMatches(temp, " f2[ =]+");
                    if(!(valStr = matchRegex(temp, re_num)).empty())
                    {
                        iValue = atoi(valStr.c_str());
                        _cmd["sa_clpf2"] = CtiParseValue(iValue);
                    }
                }
                if(!(temp = matchRegex(CmdStr, " f3[ =]+" + str_num)).empty())
                {
                    removeRegexAllMatches(temp, " f3[ =]+");
                    if(!(valStr = matchRegex(temp, re_num)).empty())
                    {
                        iValue = atoi(valStr.c_str());
                        _cmd["sa_clpf3"] = CtiParseValue(iValue);
                    }
                }
                if(!(temp = matchRegex(CmdStr, " f4[ =]+" + str_num)).empty())
                {
                    removeRegexAllMatches(temp, " f4[ =]+");
                    if(!(valStr = matchRegex(temp, re_num)).empty())
                    {
                        iValue = atoi(valStr.c_str());
                        _cmd["sa_clpf4"] = CtiParseValue(iValue);
                    }
                }
                if(!(temp = matchRegex(CmdStr, " all[ =]+" + str_num)).empty())
                {
                    if(!(valStr = matchRegex(temp, re_num)).empty())
                    {
                        iValue = atoi(valStr.c_str());
                        _cmd["sa_clpall"] = CtiParseValue(iValue);
                    }
                }
            }
            else if(!(temp = matchRegex(CmdStr, " lorm0[ =]+" + str_num)).empty())
            {
                removeRegexAllMatches(temp, " lorm0[ =]+");
                if(!(valStr = matchRegex(temp, re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["sa_lorm0"] = CtiParseValue(iValue);
                }
            }
            else if(!(temp = matchRegex(CmdStr, " horm0[ =]+" + str_num)).empty())
            {
                removeRegexAllMatches(temp, " horm0[ =]+");
                if(!(valStr = matchRegex(temp, re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["sa_horm0"] = CtiParseValue(iValue);
                }
            }
            else if(!(temp = matchRegex(CmdStr, " lorm1[ =]+" + str_num)).empty())
            {
                removeRegexAllMatches(temp, " lorm1[ =]+");
                if(!(valStr = matchRegex(temp, re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["sa_lorm1"] = CtiParseValue(iValue);
                }
            }
            else if(!(temp = matchRegex(CmdStr, " horm1[ =]+" + str_num)).empty())
            {
                removeRegexAllMatches(temp, " horm1[ =]+");
                if(!(valStr = matchRegex(temp, re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["sa_horm1"] = CtiParseValue(iValue);
                }
            }
            else if(!(temp = matchRegex(CmdStr, " use relay *map 0")).empty())
            {
                _cmd["sa_userelaymap0"] = TRUE;
            }
            else if(!(temp = matchRegex(CmdStr, " use relay *map 1")).empty())
            {
                _cmd["sa_userelaymap1"] = TRUE;
            }
            else if(containsString(CmdStr, " clear lc"))
            {
                _cmd["sa_clearlc"] = TRUE;
            }
            else if(containsString(CmdStr, " clear hc"))
            {
                _cmd["sa_clearhc"] = TRUE;
            }
            else if(containsString(CmdStr, " clear pcd"))
            {
                _cmd["sa_clearpcd"] = TRUE;
            }
            else if(containsString(CmdStr, " freeze pcd"))
            {
                _cmd["sa_freezepcd"] = TRUE;
            }
            else if(containsString(CmdStr, " primary freq"))
            {
                _cmd["sa_frequency"] = 0;
            }
            else if(containsString(CmdStr, " secondary freq"))
            {
                _cmd["sa_frequency"] = 1;
            }
            else if(!(temp = matchRegex(CmdStr, " rawdata " + str_num + " " + str_num)).empty())
            {
                CtiTokenizer   tok2(temp);
                temp = tok2(); // Get the rawdata into the hopper....
                               //
                temp = tok2(); // Pull in the first number
                _cmd["sa_datatype"] = atoi(temp.c_str());

                temp = tok2(); // Pull in the next number
                _cmd["sa_dataval"] = atoi(temp.c_str());
            }
        }
    }
}


void CtiCommandParser::doParseControlExpresscomCriticalPeakPricing(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    std::string   str;
    std::string   temp;
    std::string   valStr;
    std::string   token;

    INT setpoint = 0;

    if(!(temp = matchRegex(CmdStr, " minheat[ =]+" + str_num)).empty())
    {
        if(!(valStr = matchRegex(temp, re_num)).empty())
        {
            setpoint = atoi(valStr.c_str()) & 0xFFFF;
            _cmd["xcminheat"] = CtiParseValue(setpoint);
        }
    }
    else if(!(temp = matchRegex(CmdStr, " maxcool[ =]+" + str_num)).empty())
    {
        if(!(valStr = matchRegex(temp, re_num)).empty())
        {
            setpoint = atoi(valStr.c_str()) & 0xFFFF;
            _cmd["xcmaxcool"] = CtiParseValue(setpoint);
        }
    }
    if(!(temp = matchRegex(CmdStr, str_num + " min(s|ute|utes)?")).empty())//minutes of control
    {
        if(!(valStr = matchRegex(temp, re_num)).empty())
        {
            setpoint = atoi(valStr.c_str()) & 0xFFFF;
            _cmd["xccontroltime"] = CtiParseValue(setpoint);
        }

    }
    if(!(temp = matchRegex(CmdStr, "(wake|leave|return|sleep)[ =]+" + str_signed_num)).empty())
    {

        if(!(temp = matchRegex(CmdStr, "wake[ =]+" + str_signed_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_signed_num)).empty())
            {
                setpoint = atoi(valStr.c_str()) & 0xFF;
                _cmd["xcwake"] = CtiParseValue(setpoint);
            }
        }
        if(!(temp = matchRegex(CmdStr, "leave[ =]+" + str_signed_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_signed_num)).empty())
            {
                setpoint = atoi(valStr.c_str()) & 0xFF;
                _cmd["xcleave"] = CtiParseValue(setpoint);
            }
        }
        if(!(temp = matchRegex(CmdStr, "return[ =]+" + str_signed_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_signed_num)).empty())
            {
                setpoint = atoi(valStr.c_str()) & 0xFF;
                _cmd["xcreturn"] = CtiParseValue(setpoint);
            }
        }
        if(!(temp = matchRegex(CmdStr, "sleep[ =]+" + str_signed_num)).empty())
        {
            if(!(valStr = matchRegex(temp, re_signed_num)).empty())
            {
                setpoint = atoi(valStr.c_str()) & 0xFF;
                _cmd["xcsleep"] = CtiParseValue(setpoint);
            }
        }
    }
}

void CtiCommandParser::doParsePutConfigUtilityUsage(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    std::string   str;
    std::string   temp;
    std::string   valStr;
    std::string   token;
    INT ch;
    INT bucket;
    float val;
    int chanIndex = 0;


    CtiTokenizer   tok(CmdStr);
    {
        //putconfig utility usage chanNum:past usage:Val, chanNum:present usage:Val, chanNum:past cost:Val,
        // chanNum:present cost:Val,

        token = tok(",");
        while(!token.empty())
        {
            ch = 0x0;
            bucket = 0x0;
            val = 0x0;

            CtiTokenizer   tok1(token);   //chanNum:chanVal, chanNum:chanVal
            temp = tok1(":");
            if(!(str = matchRegex(temp, re_num)).empty())
            {
                ch = atoi(str.c_str()) - 1;
            }
            temp = tok1(":");
            if(containsString(temp, "past usage"))
            {
                bucket = 0x00;
            }
            else if(containsString(temp, "present usage"))
            {
                bucket = 0x01;
            }
            else if(containsString(temp, "past cost"))
            {
                bucket = 0x02;
            }
            else if(containsString(temp, "present cost"))
            {
                bucket = 0x03;
            }

            temp = tok1(":");
            if(!(valStr = matchRegex(temp, boost::regex("\\-?[0-9]+\\.?[0-9]+?"))).empty() ||
               !(valStr = matchRegex(temp, re_num)).empty())
            {
                val = atof(valStr.c_str());
            }

            std::string chan("xcchan_" + CtiNumStr(chanIndex));
            std::string chanBucket("xcchanbucket_" + CtiNumStr(chanIndex));
            std::string chanValue("xcchanvalue_" + CtiNumStr(chanIndex));

            _cmd[chan]        = CtiParseValue(ch);
            _cmd[chanBucket]      = CtiParseValue(bucket);
            _cmd[chanValue]   = CtiParseValue(val);

            chanIndex++;
            token = tok(",");

        }
        _cmd["xcnumutilvalues"] = CtiParseValue(chanIndex);
    }


}


void  CtiCommandParser::doParsePutConfigCBC(const string &_CmdStr)
{
    std::string CmdStr(_CmdStr);
    UINT        iValue = 0;

    std::string   str;
    std::string   temp;
    std::string valStr;
    std::string   token;
    std::string   xcraw("0x60 ");

    if(containsString(CmdStr, " emergency"))
    {
        int ov = 0, uv = 0, timer = 0;

        if(!(token = matchRegex(CmdStr, " uv[ =]+" + str_num)).empty())
        {
            str = matchRegex(token, re_num);
            uv = atoi(str.c_str());
            uv = limitValue(uv, 105, 122);
            _cmd["cbc_emergency_uv_close_voltage"] = CtiParseValue( uv );
            valStr = "Emergency UV Close Voltage " + CtiNumStr(uv);
            _actionItems.push_back(valStr);
        }

        if(!(token = matchRegex(CmdStr, " ov[ =]+" + str_num)).empty())
        {
            str = matchRegex(token, re_num);
            ov = atoi(str.c_str());
            ov = limitValue(ov, 118, 135);
            _cmd["cbc_emergency_ov_trip_voltage"] = CtiParseValue( ov );
            valStr = "Emergency OV Trip Voltage " + CtiNumStr(ov);
            _actionItems.push_back(valStr);
        }

        if(!(token = matchRegex(CmdStr, " timer[ =]+" + str_num)).empty())
        {
            str = matchRegex(token, re_num);
            timer = atoi(str.c_str());
            timer = limitValue(timer, 1, 255);
            _cmd["cbc_emergency_ov_trip_voltage"] = CtiParseValue( timer );
            valStr = "Emergency OV Trip Voltage " + CtiNumStr(timer);
            _actionItems.push_back(valStr);
        }

        if(ov != 0 && uv != 0 && timer != 0)
        {
            xcraw += "0x24 0x" + CtiNumStr(ov).hex() + " 0x" + CtiNumStr(uv).hex() + " 0x" + CtiNumStr(timer).hex();
            _cmd["xcrawconfig"] = xcraw;
        }
        else
        {
            CTILOG_ERROR(dout, "One or more of ov, uv, or timer not specified."
                               " ov = " << ov <<
                               " uv = " << uv <<
                               " timer = " << timer);
        }
    }
    else if(!(token = matchRegex(CmdStr, " ovuv control trigger time[ =]+" + str_num)).empty())
    {
        int random = 0;
        str = matchRegex(token, re_num);
        iValue = atoi(str.c_str());
        iValue = limitValue(iValue, 0, 0xffff);
        _cmd["cbc_ovuv_control_trigger_time"] = CtiParseValue( iValue );

        if(!(token = matchRegex(CmdStr, " random[ =]+" + str_num)).empty())
        {
            str = matchRegex(token, re_num);
            random = atoi(str.c_str());
            random = limitValue(random, 0, 0xff);
        }

        valStr = "OVUV Control Trigger Time " + CtiNumStr(iValue) + " random " + CtiNumStr(random);
        _actionItems.push_back(valStr);

        xcraw += "0x0b 0x" + CtiNumStr(HIBYTE(iValue)).hex() + " 0x" + CtiNumStr(LOBYTE(iValue)).hex() + " 0x" + CtiNumStr(random).hex();
        _cmd["xcrawconfig"] = xcraw;
    }
    else if(!(token = matchRegex(CmdStr, " daily auto control limit[ =]+" + str_num)).empty())
    {
        str = matchRegex(token, re_num);
        iValue = atoi(str.c_str());
        iValue = limitValue(iValue, 1, 30);
        _cmd["cbc_daily_control_limit"] = CtiParseValue( iValue );
        valStr = "OV Trip Voltage " + CtiNumStr(iValue);
        _actionItems.push_back(valStr);

        xcraw += "0x10 0x" + CtiNumStr(iValue).hex();
        _cmd["xcrawconfig"] = xcraw;
    }
    else if(containsString(CmdStr, " comms lost"))
    {
        BYTE action = 0;
        USHORT commlosstime = 0;
        BYTE uvclval = 0;
        BYTE ovclval = 0;

        if( !containsString(CmdStr, " disable") ) // This disables all comms lost behavior!  action = 0!
        {
            if(containsString(CmdStr, "timed"))
            {
                action |= 0x01;
            }
            if(containsString(CmdStr, "ovuv"))
            {
                action |= 0x02;
            }
            if(containsString(CmdStr, "temperature"))
            {
                action |= 0x04;
            }
            if(containsString(CmdStr, "analogin1"))
            {
                action |= 0x08;
            }
            if(containsString(CmdStr, "analogin2"))
            {
                action |= 0x10;
            }
            if(containsString(CmdStr, "analogin3"))
            {
                action |= 0x20;
            }
            if(containsString(CmdStr, "digitalin1"))
            {
                action |= 0x40;
            }
            if(containsString(CmdStr, "digitalin2"))
            {
                action |= 0x80;
            }
        }

        xcraw += "0x23 0x" + CtiNumStr(action).hex();

        if(!(token = matchRegex(CmdStr, " time[ =]+" + str_num)).empty())    // Ignored if the action == 0.
        {
            str = matchRegex(token, re_num);
            commlosstime = atoi(str.c_str());
            commlosstime = limitValue(commlosstime, 0, 0xffff);
            _cmd["cbc_comms_lost_time"] = CtiParseValue( commlosstime );
        }
        xcraw += " 0x" + CtiNumStr(HIBYTE(commlosstime)).hex() + " 0x" + CtiNumStr(LOBYTE(commlosstime)).hex();

        if(!(token = matchRegex(CmdStr, " (ov|uv)[ =]+" + str_num + ".*(ov|uv)[ =]+" + str_num)).empty())
        {
            if(!(token = matchRegex(CmdStr, " uv[ =]+" + str_num)).empty())    // Ignored if the action == 0.
            {
                str = matchRegex(token, re_num);
                uvclval = atoi(str.c_str());
                uvclval = limitValue(uvclval, 105, 122);
                _cmd["cbc_comms_lost_uvpt"] = CtiParseValue( uvclval );

                xcraw += " 0x" + CtiNumStr(uvclval).hex();
            }

            if(!(token = matchRegex(CmdStr, " ov[ =]+" + str_num)).empty())    // Ignored if the action == 0.
            {
                str = matchRegex(token, re_num);
                ovclval = atoi(str.c_str());
                ovclval = limitValue(ovclval, 118, 135);
                _cmd["cbc_comms_lost_ovpt"] = CtiParseValue( ovclval );

                xcraw += " 0x" + CtiNumStr(ovclval).hex();
            }
        }

        _cmd["xcrawconfig"] = xcraw;
    }
    else if(containsString(CmdStr, " temperature"))
    {
        if(containsString(CmdStr, " control enable"))
        {
            iValue = TRUE;
        }
        else if(containsString(CmdStr, " control disable"))
        {
            iValue = FALSE;
        }
        _cmd["cbc_tempcontrol_enable"] = CtiParseValue( iValue );
        valStr = "Temperature control ";
        valStr += iValue ? "enable" : "disable";
        _actionItems.push_back(valStr);

        _cmd["xcrawconfig"] = xcraw;
    }
    else if(containsString(CmdStr, " time control"))
    {
        if(containsString(CmdStr, " control enable"))
        {
            iValue = TRUE;
        }
        else if(containsString(CmdStr, " control disable"))
        {
            iValue = FALSE;
        }
        _cmd["cbc_timecontrol_enable"] = iValue;
        valStr = "Time control ";
        valStr += iValue ? "enable" : "disable";
        _actionItems.push_back(valStr);

        // FIXME Can we remove this?
        CTILOG_WARN(dout, "Incomplete");

        xcraw += "0x22 0x" + CtiNumStr(iValue).hex();
        _cmd["xcrawconfig"] = xcraw;
    }
    else
    {
        if(!(token = matchRegex(CmdStr, " uv[ =]+" + str_num)).empty())
        {
            str = matchRegex(token, re_num);
            iValue = atoi(str.c_str());
            iValue = limitValue(iValue, 105, 122);
            _cmd["cbc_uv_close_voltage"] = CtiParseValue( iValue );
            valStr = "UV Close Voltage " + CtiNumStr(iValue);
            _actionItems.push_back(valStr);

            xcraw += "0x09 0x" + CtiNumStr(iValue).hex();
            _cmd["xcrawconfig"] = xcraw;
        }
        else if(!(token = matchRegex(CmdStr, " ov[ =]+" + str_num)).empty())
        {
            str = matchRegex(token, re_num);
            iValue = atoi(str.c_str());
            iValue = limitValue(iValue, 118, 135);
            _cmd["cbc_ov_trip_voltage"] = CtiParseValue( iValue );
            valStr = "OV Trip Voltage " + CtiNumStr(iValue);
            _actionItems.push_back(valStr);

            xcraw += "0x0a 0x" + CtiNumStr(iValue).hex();
            _cmd["xcrawconfig"] = xcraw;
        }
    }

    CTILOG_INFO(dout, "XCRAW: " << xcraw);
    return;
}


std::vector<float> CtiCommandParser::parseListOfFloats(const std::string &floatList)
{
    std::string token;
    std::string ctistr_list = floatList;

    std::vector<float> retVal;

    if(!(token = matchRegex(ctistr_list, str_floatnum_list)).empty())
    {
        CtiTokenizer tok( token );

        std::string value;
        while(!(value = tok()).empty())
        {
            retVal.push_back(atof(value.c_str()));
        }
    }

    return retVal;
}

/*
  matches a float followed by possibly h/m/s (hours/minutes/seconds)
  no units specified is assumed to be minutes
  returns time in seconds
  returns -1 on in an invalidly formatted string
 */
double CtiCommandParser::getDurationInSeconds( std::string token_ )
{
    std::string token(token_);

    if((token = matchRegex(token, str_floatnum + " *[hms]?( |$)")).empty() )
    {
        CTILOG_ERROR(dout, "Invalid time format: " << token_);
        return -1.0;
    }

    std::string timeStr = matchRegex(token, re_floatnum);
    double timeSec = atof(timeStr.c_str());

    /*
      Determine units, with default as minutes.
      Multiply to get time in seconds.
     */

    if(containsString(token, "h"))
    {
        timeSec *= 3600.0;
    }
    else if(containsString(token, "s"))
    {
        timeSec *= 1.0;
    }
    else
    {
        timeSec *= 60.0;
    }
    return timeSec;
}

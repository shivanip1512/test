#include "yukon.h"


#include <windows.h>
#include <stdlib.h>
#include <iostream>

#include <rw\re.h>
#undef mask_                // Stupid RogueWave re.h

#include <limits.h>

#include "cmdparse.h"
#include "cparms.h"
#include "devicetypes.h"
#include "logger.h"
#include "numstr.h"
#include "pointdefs.h"
#include "utility.h"
#include "ctistring.h"

using namespace std;

static const CtiString str_quoted_token("((\"[^\"]+\")|('[^']'))");

static const CtiString str_num     ("([0-9]+)");
static const CtiString str_floatnum("([0-9]+(\\.[0-9]*)?)");
static const CtiString str_hexnum  ("(0x[0-9a-f]+)");
static const CtiString str_anynum  ("(" + str_num + "|" + str_hexnum + ")");

static const CtiString str_date("([0-9]+[/-][0-9]+[/-][0-9]+)");
static const CtiString str_time("([0-9]+:[0-9]+(:[0-9]+)?)");

static boost::regex   re_num     (str_num);
static boost::regex   re_floatnum(str_floatnum);
static boost::regex   re_hexnum  (str_hexnum);
static boost::regex   re_anynum  (str_anynum);

CtiCommandParser::CtiCommandParser(const string str) :
_cmdString(str)
{
    _actionItems.clear();
    _cmd.clear();
    doParse(_cmdString);
}

CtiCommandParser::CtiCommandParser(const CtiCommandParser& aRef)
{
    _cmd.clear();
    _actionItems.clear();
    *this = aRef;
}

CtiCommandParser::~CtiCommandParser()
{
    _actionItems.clear();
}

CtiCommandParser& CtiCommandParser::operator=(const CtiCommandParser& aRef)
{
    if(this != &aRef)
    {
        _cmdString = aRef._cmdString;
        _actionItems = aRef._actionItems;
        _cmd = aRef.getMap();
    }
    return *this;
}


void  CtiCommandParser::doParse(const string &_Cmd)
{
    // This may look like replication of the assignment, but allows re-use of the object for
    // other parsing....
    CtiString Cmd = _Cmd;
    Cmd.toLower();
    Cmd.replace("\t", " ", CtiString::all);  //  replace all tabs with spaces
    Cmd.replace(" +", " ", CtiString::all);  //  truncate any duplicate spaces

    _cmdString = Cmd.c_str();       // OK already, now we are in business.
    parse();

    return;
}


void  CtiCommandParser::parse()
{
    CHAR            *p;
    CtiString       CmdStr = _cmdString;
    CtiString       token;
    CtiString       cmdstr;
    CtiString       strnum;
    INT             _num = 0;

    _actionItems.clear();   // 20050125 CGP.  Getting duplicate actionItems when I reparse in the groups.  This should be benign.

    if(!CmdStr.match("^ *pil ").empty() || !CmdStr.match("^ *command ").empty())
    {
        CmdStr = CmdStr.replace("^ *pil ", "");
        CmdStr = CmdStr.replace("^ *command ", "");
    }

    CtiTokenizer    tok(CmdStr);

    if(CmdStr.contains(" serial"))
    {
        boost::regex regexp("serial[= ]+" + str_anynum);

        if(!(token = CmdStr.match(regexp)).empty())
        {
            if(!(strnum = token.match(re_hexnum)).empty())
            {
                // dout << __LINE__ << " " << strnum << endl;
                _num = strtol(strnum.c_str(), &p, 16);
            }
            else if(!(strnum = token.match(re_num)).empty())
            {
                // dout << __LINE__ << " " << strnum << endl;
                _num = strtol(strnum.c_str(), &p, 10);
            }
            _cmd["serial"] = CtiParseValue( _num );
            CmdStr.replace(regexp, "");
        }
    }

    if(CmdStr.contains(" select"))
    {
        boost::regex re_name   ("select name "        + str_quoted_token);
        boost::regex re_id     ("select (device)?id " + str_num);
        boost::regex re_grp    ("select group "       + str_quoted_token);
        boost::regex re_altg   ("select altgroup "    + str_quoted_token);
        boost::regex re_billg  ("select billgroup "   + str_quoted_token);
        boost::regex re_rtename("select route *name " + str_quoted_token);
        boost::regex re_rteid  ("select route *id "   + str_num);
        boost::regex re_ptname ("select point *name " + str_quoted_token);
        boost::regex re_ptid   ("select point *id "   + str_num);

        if(!(token = CmdStr.match(re_name)).empty())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("name ", &nstop);

            nstop += nstart;

            if(!(token = token.match(str_quoted_token, nstop)).empty())   // get the value
            {
                token.erase(0,1);token.erase(token.length()-1,token.length()-1);
                _cmd["device"] = CtiParseValue( token, -1 );
            }

            CmdStr.replace(re_name, "");
        }
        else if(!(token = CmdStr.match(re_id)).empty())
        {
            CtiTokenizer ntok(token);
            ntok();  // pull the select keyword
            ntok();  // pull the id keyword
            if(!(token = ntok()).empty())   // get the value
            {
                _cmd["device"] = CtiParseValue( atoi(token.c_str()) );
            }
            CmdStr.replace(re_id, "");
        }
        else if(!(token = CmdStr.match(re_grp)).empty())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("group ", &nstop);

            nstop += nstart;

            if(!(token = token.match(str_quoted_token, nstop)).empty())   // get the value
            {
                //Removes the ' " ' character from "something"
                token.erase(0,1);token.erase(token.length()-1,token.length()-1);
                _cmd["group"] = CtiParseValue( token, -1 );
            }
            CmdStr.replace(re_grp, "");
        }
        else if(!(token = CmdStr.match(re_altg)).empty())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("altgroup ", &nstop);

            nstop += nstart;

            if(!(token = token.match(str_quoted_token, nstop)).empty())   // get the value
            {
                //Removes the ' " ' character from "something"
                token.erase(0,1);token.erase(token.length()-1,token.length()-1);
                _cmd["altgroup"] = CtiParseValue( token, -1 );
            }
            CmdStr.replace(re_altg, "");
        }
        else if(!(token = CmdStr.match(re_billg)).empty())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("billgroup ", &nstop);

            nstop += nstart;

            if(!(token = token.match(str_quoted_token, nstop)).empty())   // get the value
            {
                //Removes the ' " ' character from "something"
                token.erase(0,1);token.erase(token.length()-1,token.length()-1);
                _cmd["billgroup"] = CtiParseValue( token, -1 );
            }
            CmdStr.replace(re_altg, "");
        }
        else if(!(token = CmdStr.match(re_rtename)).empty())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("name ", &nstop);

            nstop += nstart;

            if(!(token = token.match(str_quoted_token, nstop)).empty())   // get the value
            {
                //Removes the ' " ' character from "something"
                token.erase(0,1);token.erase(token.length()-1,token.length()-1);
                _cmd["route"] = CtiParseValue( token, -1 );
            }
            CmdStr.replace(re_rtename, "");
        }
        else if(!(token = CmdStr.match(re_rteid)).empty())
        {
            token.replace( boost::regex("select route *id "), "");

            if(!token.empty())   // get the value
            {
                _cmd["route"] = CtiParseValue( atoi(token.c_str()) );
            }
            CmdStr.replace(re_rteid, "");
        }
        else if(!(token = CmdStr.match(re_ptname)).empty())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("name ", &nstop);

            nstop += nstart;

            if(!(token = token.match(str_quoted_token, nstop)).empty())   // get the value
            {
                //Removes the ' " ' character from "something"
                token.erase(0,1);token.erase(token.length()-1,token.length()-1);
                _cmd["point"] = CtiParseValue( token, -1 );
            }
            CmdStr.replace(re_ptname, "");
        }
        else if( !(token = CmdStr.match(re_ptid)).empty())
        {
            token.replace(boost::regex("select point *id "),"");

            if(!token.empty())   // get the value
            {
                _cmd["point"] = CtiParseValue( atoi(token.c_str()) );
            }
            CmdStr.replace(re_ptid, "");
        }
        else
        {
            _cmd["device"] = CtiParseValue( -1 );
        }

#if 0
        {
            CtiParseValue& pv = CtiParseValue(); // = _cmd["command"];
            _cmd.findValue("device", pv);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Device specified " << pv.getString() << " id = " << pv.getInt() << endl;
            }
        }
#endif
    }

    resolveProtocolType(CmdStr);


    if(CmdStr.contains(" noqueue"))
    {
        _cmd["noqueue"] = CtiParseValue("true");
    }
    if(!(token = CmdStr.match(" protocol_priority " + str_num)).empty())
    {
        if(!(strnum = token.match(re_num)).empty())
        {
            _cmd["xcpriority"] = CtiParseValue( atoi(strnum.c_str()) );            // Expresscom only supports a 0 - 3 priority 0 highest.
        }
    }


    if(!(cmdstr = tok()).empty())
    {
        cmdstr.toLower();

        if(cmdstr == "getvalue")
        {
            _cmd["command"] = CtiParseValue( cmdstr, GetValueRequest );
            doParseGetValue(CmdStr);
        }
        else if(cmdstr == "putvalue")
        {
            _cmd["command"] = CtiParseValue( cmdstr, PutValueRequest );
            doParsePutValue(CmdStr);
        }
        else if(cmdstr == "getstatus")
        {
            _cmd["command"] = CtiParseValue( cmdstr, GetStatusRequest );
            doParseGetStatus(CmdStr);
        }
        else if(cmdstr == "putstatus")
        {
            _cmd["command"] = CtiParseValue( cmdstr, PutStatusRequest );
            doParsePutStatus(CmdStr);
        }
        else if(cmdstr == "getconfig")
        {
            _cmd["command"] = CtiParseValue( cmdstr, GetConfigRequest );
            doParseGetConfig(CmdStr);
        }
        else if(cmdstr == "putconfig")
        {
            _cmd["command"] = CtiParseValue( cmdstr, PutConfigRequest );
            doParsePutConfig(CmdStr);

        }
        else if(cmdstr == "loop" || cmdstr == "ping")  //  so "ping" is just an alias
        {
            _cmd["command"] = CtiParseValue( cmdstr, LoopbackRequest );
            _cmd["count"] = CtiParseValue( 1 );
        }
        else if(cmdstr == "control")
        {
            _cmd["command"] = CtiParseValue( cmdstr, ControlRequest );
            doParseControl(CmdStr);
        }
        else if(cmdstr == "scan")
        {
            _cmd["command"] = CtiParseValue( cmdstr, ScanRequest );
            doParseScan(CmdStr);
        }
        else
        {
            _cmd["command"] = CtiParseValue( cmdstr, InvalidRequest );
        }
    }
    else
    {
        _cmd["command"] = CtiParseValue( cmdstr, InvalidRequest );
    }

    return;
}

void  CtiCommandParser::doParseGetValue(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    UINT        flag = 0;
    UINT        offset = 0;

    CtiString   temp2;
    CtiString   token;

    boost::regex   re_kxx  ("(kwh|kvah|kvarh)[abcdt]?");  //  Match on kwh, kwha,b,c,d,t
    boost::regex   re_hrate("h[abcdt]?");                 //  Match on h,ha,hb,hc,hd,ht
    boost::regex   re_rate ("rate *[abcdt]");
//not in head
    boost::regex   re_kwh  ("kwh");
    boost::regex   re_kvah ("kvah");
    boost::regex   re_kvarh("kvarh");


    boost::regex   re_demand(" dema|( kw| kvar| kva)( |$)");  //  match "dema"nd, but also match "kw", "kvar", or "kva"

    //  getvalue lp channel 1 12/14/04 12:00
    //  getvalue lp channel 1 8-13-2005 14:45
    boost::regex  re_lp("lp channel " + str_num + " " + str_date + " " + str_time);
    //  getvalue lp peak daily channel 2 9/30/04 30
    //  getvalue lp peak hour channel 3 10-15-2003 15
    boost::regex  re_lp_peak("lp peak (day|hour|interval) channel " + str_num + " " + str_date + " " + str_num);
    boost::regex  re_outage("outage " + str_num);

    boost::regex  re_offset("off(set)? *" + str_num);


    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "getvalue")
    {
        if(!(token = CmdStr.match(re_kxx)).empty())      // Sourcing from CmdStr, which is the entire command string.
        {
            // I have a match on the kxxh regular expression....
            if(token.contains("kwh"))
            {
                flag |= CMD_FLAG_GV_KWH;
            }
            if(token.contains("kvah"))
            {
                flag |= CMD_FLAG_GV_KVAH;
            }
            if(token.contains("kvarh"))
            {
                flag |= CMD_FLAG_GV_KVARH;
            }

            if(!token.empty())
            {
                if(!(temp2 = token.match(re_hrate)).empty())
                {
                    if(temp2 == "ha")
                    {
                        flag &= ~CMD_FLAG_GV_RATEMASK;
                        flag |= CMD_FLAG_GV_RATEA;
                    }
                    else if(temp2 == "hb")
                    {
                        flag &= ~CMD_FLAG_GV_RATEMASK;
                        flag |= CMD_FLAG_GV_RATEB;
                    }
                    else if(temp2 == "hc")
                    {
                        flag &= ~CMD_FLAG_GV_RATEMASK;
                        flag |= CMD_FLAG_GV_RATEC;
                    }
                    else if(temp2 == "hd")
                    {
                        flag &= ~CMD_FLAG_GV_RATEMASK;
                        flag |= CMD_FLAG_GV_RATED;
                    }
                    else if(temp2 == "ht")
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
        else if(CmdStr.contains(" lp "))
        {
            if(!(token = CmdStr.match(re_lp)).empty())
            {
                //  getvalue lp channel 1 12/14/04 12:00
                //  getvalue lp channel 1 8-13-2005 14:45
                CtiTokenizer cmdtok(token);

                cmdtok();  //  move past lp
                cmdtok();  //  move past channel

                _cmd["lp_command"] = CtiParseValue("lp");
                _cmd["lp_channel"] = atoi(CtiString(cmdtok()).c_str());
                _cmd["lp_date"]    = cmdtok();
                _cmd["lp_time"]    = cmdtok();
            }
            else if(!(token = CmdStr.match(re_lp_peak)).empty())
            {
                //  getvalue lp peak daily channel 2 9/30/04 30
                //  getvalue lp peak hourly channel 3 10-15-2003 15
                CtiTokenizer cmdtok(token);

                cmdtok();  //  move past lp
                cmdtok();  //  move past peak

                _cmd["lp_peaktype"] = cmdtok();

                cmdtok();  //  move past channel

                _cmd["lp_command"] = CtiParseValue("peak");
                _cmd["lp_channel"] = atoi(CtiString(cmdtok()).c_str());
                _cmd["lp_date"]    = cmdtok();
                _cmd["lp_range"]   = atoi(CtiString(cmdtok()).c_str());
            }
        }
        else if(!(token = CmdStr.match(re_demand)).empty())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_GV_DEMAND;
        }
        else if(CmdStr.contains(" peak"))
        {
            flag |= CMD_FLAG_GV_PEAK;
        }
        else if(CmdStr.contains(" minmax"))
        {
            flag |= CMD_FLAG_GV_MINMAX;
        }
        else if(CmdStr.contains(" voltage"))
        {
            flag |= CMD_FLAG_GV_VOLTAGE;
        }
        else if(!(token = CmdStr.match(re_offset)).empty())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_OFFSET;

            // What offset is needed now...
            if(!(temp2 = token.match(re_num)).empty())
            {
                offset = atoi(temp2.c_str());
            }
            else
            {
                offset = 0;
            }
        }
        else if(CmdStr.contains(" outage "))
        {
            if(!(token = CmdStr.match(re_outage)).empty())
            {
                //  getvalue outage 1..6
                CtiTokenizer cmdtok(token);

                cmdtok();  //  move past "outage"

                _cmd["outage"] = atoi(CtiString(cmdtok()).c_str());
            }
        }
        else if(CmdStr.contains(" codes"))
        {
            _cmd["codes"] = CtiParseValue(TRUE);
        }
        else if(CmdStr.contains(" freezecount"))
        {
            _cmd["freeze_counter"] = CtiParseValue(TRUE);
        }
        else
        {
            // Default Get Value request has been specified....
        }



        if(!(temp2 = CmdStr.match(re_rate)).empty())
        {
            flag &= ~CMD_FLAG_GV_RATEMASK;   // This one overrides...

            if(temp2[temp2.length() - 1] == 'a')       flag |= CMD_FLAG_GV_RATEA;
            else if(temp2[temp2.length() - 1] == 'b')  flag |= CMD_FLAG_GV_RATEB;
            else if(temp2[temp2.length() - 1] == 'c')  flag |= CMD_FLAG_GV_RATEC;
            else if(temp2[temp2.length() - 1] == 'd')  flag |= CMD_FLAG_GV_RATED;
            else if(temp2[temp2.length() - 1] == 't')  flag |= CMD_FLAG_GV_RATET;
        }

        if(CmdStr.contains(" ied"))      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_GV_IED;                     // Read data from the ied port, not internal counters!
        }
        if(CmdStr.contains(" frozen"))      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_FROZEN;
        }

        if(CmdStr.contains(" power"))
        {
            flag |= CMD_FLAG_GV_PFCOUNT;
        }

        if(CmdStr.contains(" update"))      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_UPDATE;
        }
    }
    else
    {
        // Something went WAY wrong....

        dout << "This better not ever be seen by mortals... " << endl;
    }

    _cmd["flag"]      = CtiParseValue( flag   );
    _cmd["offset"]    = CtiParseValue( offset );
}


void  CtiCommandParser::doParseGetStatus(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    UINT        flag = 0;
    UINT        offset = 0;

    CtiString   temp2;
    CtiString   token;

    boost::regex   re_lp("lp( channel " + str_num + ")?");
    boost::regex   re_eventlog("eventlog(s)?");

    boost::regex   re_offset("off(set)? *" + str_num);

    boost::regex   re_sele("select");

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "getstatus")
    {
        if(!(token = CmdStr.match(re_lp)).empty())
        {
            flag |= CMD_FLAG_GS_LOADPROFILE;

            //  was an offset specified?
            if(!(temp2 = token.match(re_num)).empty())
            {
                _cmd["loadprofile_offset"] = atoi(temp2.c_str());
            }
        }
        if(!(token = CmdStr.match(re_offset)).empty())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_OFFSET;

            // What offset is needed now...
            if(!(temp2 = token.match(re_num)).empty())
            {
                offset = atoi(temp2.c_str());
            }
        }
        if(!CmdStr.match(re_eventlog).empty())
        {
            _cmd["eventlog"] = CtiParseValue(true);
        }
        if(CmdStr.contains(" disc"))
        {
            flag |= CMD_FLAG_GS_DISCONNECT;
        }
        if(CmdStr.contains(" err"))
        {
            flag |= CMD_FLAG_GS_ERRORS;
        }
        if(CmdStr.contains(" inter"))
        {
            flag |= CMD_FLAG_GS_INTERNAL;
        }
        if(CmdStr.contains(" extern"))
        {
            flag |= CMD_FLAG_GS_EXTERNAL;
        }
        if(CmdStr.contains(" ied"))
        {
            flag |= CMD_FLAG_GS_IED;

            if(CmdStr.contains(" ied link"))
            {
                flag |= CMD_FLAG_GS_LINK;
            }
        }
        else
        {
            // Default GetStatus request has been specified....
        }


        if(CmdStr.contains(" froz"))        // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_FROZEN;
        }
        if(CmdStr.contains(" upd"))         // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_UPDATE;
        }
    }
    else
    {
        // Something went WAY wrong....

        dout << "This better not ever be seen by mortals... " << endl;
    }

    _cmd["flag"]   = CtiParseValue(flag);
    _cmd["offset"] = CtiParseValue(offset);
}

void  CtiCommandParser::doParseControl(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    INT         _num;
    UINT        flag   = 0;
    UINT        offset = 0;
    UINT        iValue = 0;
    DOUBLE      dValue = 0.0;

    CHAR        tbuf[80];
    CHAR        tbuf2[80];

    CtiString   temp2;
    CtiString   token;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "control")
    {
        if(CmdStr.contains(" delay"))
        {
            CtiString   valStr;

            if(!(temp2 = CmdStr.match(" delay ?time " + str_num)).empty())
            {
                if(!(valStr = temp2.match(re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["delaytime_sec"] = CtiParseValue( iValue * 60 );
                }
            }

            if(!(temp2 = CmdStr.match(" delay ?until [0-9]?[0-9]:[0-9][0-9]")).empty())
            {
                INT hh = 0;
                INT mm = 0;
                INT ofm = 0;      // Offset from Midnight in seconds.

                if(!(valStr = temp2.match("[0-9]?[0-9]:")).empty())
                {
                    hh = atoi(valStr.c_str());
                }

                if(!(valStr = temp2.match(":[0-9][0-9]")).empty())
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
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        if(CmdStr.contains(" open"))            // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_OPEN;

            _snprintf(tbuf, sizeof(tbuf), "OPEN");
        }
        else if(CmdStr.contains(" close"))      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_CLOSE;
            _snprintf(tbuf, sizeof(tbuf), "CLOSE");
        }
        else if(CmdStr.contains(" disc"))       // Sourcing from CmdStr, which is the entire command string.
        {
            /* MUST LOOK FOR THIS FIRST! */
            flag |= CMD_FLAG_CTL_DISCONNECT;
            _snprintf(tbuf, sizeof(tbuf), "DISCONNECT");
        }
        else if(CmdStr.contains(" conn"))       // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_CONNECT;
            _snprintf(tbuf, sizeof(tbuf), "CONNECT");
        }
        else if(CmdStr.contains(" restore"))    // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_RESTORE;
            _snprintf(tbuf, sizeof(tbuf), "RESTORE");
        }
        else if(CmdStr.contains(" term"))       // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_TERMINATE;
            _snprintf(tbuf, sizeof(tbuf), "TERMINATE");
        }
        else if(CmdStr.contains(" shed"))       // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_SHED;

            if(!(token = CmdStr.match("shed *" + str_floatnum + " *[hms]?( |$)")).empty())      // Sourcing from CmdStr, which is the entire command string.
            {
                DOUBLE mult = 60.0;

                // What shed time is needed now...
                if(!(temp2 = token.match(re_floatnum)).empty())
                {
                    dValue = atof(temp2.c_str());
                }
                else
                {
                    // Something went  wrong sort of ....
                    dValue = 60.0;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "Command Parameter Assumed.  Shed for 1 hour. " << endl;
                    }
                }

                if(dValue == 7.5)
                {
                    _snprintf(tbuf2, sizeof(tbuf2),"SHED %.1f", dValue);
                }
                else
                {
                    _snprintf(tbuf2, sizeof(tbuf2),"SHED %d", (INT)dValue);
                }

                if(!(temp2 = token.match(str_floatnum + " *[hs]")).empty())
                {
                    /*
                     *  Minutes is the assumed entry format, but we return the number in seconds... so
                     */
                    if(temp2.contains("h"))
                    {
                        mult = 3600.0;
                        _snprintf(tbuf, sizeof(tbuf), "%sH", tbuf2);
                    }
                    else if(temp2.contains("s"))
                    {
                        mult = 1.0;
                        _snprintf(tbuf, sizeof(tbuf), "%sS", tbuf2);
                    }
                }
                else
                {
                    _snprintf(tbuf, sizeof(tbuf), "%sM", tbuf2);
                }

                // dout << "Shedding for " << (mult * MacsReq.dValue) << " seconds" << endl;
                _cmd["shed"] = CtiParseValue( (mult * dValue) );
            }
            else
            {
                _snprintf(tbuf, sizeof(tbuf), "SHED");
            }

            if(!(token = CmdStr.match(" rand(om)? *" + str_num)).empty())
            {
                if(!(temp2 = token.match(re_num)).empty())
                {
                    INT _num = atoi(temp2.c_str());
                    _cmd["shed_rand"] = CtiParseValue( _num );
                }
            }
        }
        else if(!(token = CmdStr.match(" cycle " + str_num)).empty())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_CYCLE;

            if(!(temp2 = token.match(re_num)).empty())
            {
                iValue = atoi(temp2.c_str());
            }
            else
            {
                // Something went kinda wrong....
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Command Parameter Assumed.  Cycle control at 50 percent cycle. " << endl;
                }
                iValue = 50;
            }

            if(!(token = CmdStr.match(" period " + str_num)).empty())
            {
                if(!(temp2 = token.match(re_num)).empty())
                {
                    INT _num = atoi(temp2.c_str());
                    _cmd["cycle_period"] = CtiParseValue( _num );
                }
            }

            if(!(token = CmdStr.match(" count " + str_num)).empty())
            {
                if(!(temp2 = token.match(re_num)).empty())
                {
                    INT _num = atoi(temp2.c_str());
                    _cmd["cycle_count"] = CtiParseValue( _num );
                }
            }

            _cmd["cycle"] = CtiParseValue( (iValue) );
            _snprintf(tbuf, sizeof(tbuf), "CYCLE %d%%", iValue);
        }
        else if( CmdStr.contains(" latch") )
        {
            if(!(token = CmdStr.match(" latch relays? ([ab]+|none)")).empty())
            {
                string latch_relays;

                token.replace(" latch relays? ", "");

                if( token.contains("a") )
                {
                    //  note that it's not just a naked "a"
                    latch_relays.append("(a)");
                }
                if( token.contains("b") )
                {
                    //  note that it's not just a naked "b"
                    latch_relays.append("(b)");
                }
                if( token.contains("none") )
                {
                    latch_relays.assign("none");
                }

                _cmd["latch_relays"] = CtiParseValue(latch_relays.data());
            }
        }

        if(CmdStr.contains(" sbo_selectonly"))          // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["sbo_selectonly"] = CtiParseValue(TRUE);
        }
        if(CmdStr.contains(" sbo_operate"))             // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["sbo_operate"] = CtiParseValue(TRUE);
        }


        if(flag) _actionItems.push_back(tbuf);                      // If anything was set, make sure someone can be informed

        if(!(token = CmdStr.match("off(set)? *" + str_num)).empty())            // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_OFFSET;

            // What offset is needed now...
            if(!(temp2 = token.match(re_num)).empty())
            {
                offset = atoi(temp2.c_str());
            }
            else
            {
                offset = 0;
            }
        }

        /*
         *  Try to find out if a relay has been specified for the control operation!
         */
        if(CmdStr.contains(" relay") || CmdStr.contains(" load"))
        {
            if(!(token = CmdStr.match("(( relay)|( load)) [0-9]+( *, *[0-9]+)*")).empty())
            {
                INT i;
                INT mask = 0;

                for(i = 0; i < 10; i++)
                {
                    _snprintf(tbuf, sizeof(tbuf), "%d", i+1);
                    if(!(temp2 = token.match(tbuf)).empty())
                    {
                        mask |= (0x01 << i);
                    }
                }

                if(mask)
                {
                    _cmd["relaymask"] = CtiParseValue( mask );
                }
            }
            if(!(token = CmdStr.match("relay next")).empty())
            {
                _cmd["relaynext"] = CtiParseValue( TRUE );
            }
        }

        if(CmdStr.contains(" truecycle"))
        {
            _cmd["xctruecycle"] = CtiParseValue( TRUE );
        }

        if(CmdStr.contains(" delta"))
        {
            _cmd["xcdelta"] = CtiParseValue( TRUE );    // Temperatures are delta offsets
        }

        if(CmdStr.contains(" noramp"))
        {
            _cmd["xcnoramp"] = CtiParseValue( TRUE );
        }

        if(CmdStr.contains(" celsius"))
        {
            _cmd["xccelsius"] = CtiParseValue( TRUE );  // Temperatures are celsius
        }
        if(CmdStr.contains("froz"))         // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_FROZEN;
        }
        if(CmdStr.contains("upd"))          // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_UPDATE;
        }
    }
    else
    {
        // Something went WAY wrong....

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "This better not ever be seen by mortals... " << endl;
        }
    }

    _cmd["flag"]      = CtiParseValue( flag   );
    _cmd["offset"]    = CtiParseValue( offset );

#if 1

    doParseControlExpresscom(CmdStr);
    doParseControlSA(CmdStr);

#else
    if(isKeyValid("type"))
    {
        switch( getiValue("type") )
        {
        case ProtocolEnergyProType:
            {
                // This will change over time.
                doParseControlExpresscom(CmdStr);
                break;
            }
        case ProtocolExpresscomType:
            {
                doParseControlExpresscom(CmdStr);
                break;
            }
        case ProtocolSADigitalType:
        case ProtocolGolayType:
        case ProtocolSA105Type:
        case ProtocolSA205Type:
        case ProtocolSA305Type:
            {
                // This will change over time.
                doParseControlSA(CmdStr);
                break;
            }
        case ProtocolVersacomType:
        case ProtocolFisherPierceType:
        case ProtocolEmetconType:
        default:
            {
                break;
            }
        }
    }
#endif
}


void  CtiCommandParser::doParsePutValue(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    UINT        flag = 0;
    UINT        offset = 0;

    CtiString   temp2;
    CtiString   token;

    char *p;

    boost::regex   re_reading("reading " + str_floatnum);
    boost::regex   re_kyzoffset("kyz *" + str_num);   //  if there's a kyz offset specified
    boost::regex   re_analog("analog " + str_num + " -?" + str_num);


    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "putvalue")
    {
        if(CmdStr.contains(" kyz"))
        {
            flag |= CMD_FLAG_PV_DIAL;

            //  if a point offset has been specified
            if(!(token = CmdStr.match(re_kyzoffset)).empty())
            {
                offset = atoi(token.match(re_num).c_str());

                _cmd["kyz_offset"] = CtiParseValue(offset);
            }

            if(!(token = CmdStr.match(re_reading)).empty())
            {
                _cmd["dial"]   = CtiParseValue(atof(token.match(re_floatnum).c_str()));
            }
        }
        if(CmdStr.contains(" analog"))
        {
            if(!(token = CmdStr.match(re_analog)).empty())
            {
                flag |= CMD_FLAG_PV_ANALOG;

                CtiTokenizer cmdtok(token);

                cmdtok();

                _cmd["analogoffset"] = CtiParseValue( atoi(cmdtok().c_str()) );
                _cmd["analogvalue"]  = CtiParseValue( atoi(cmdtok().c_str()) );
            }
        }
        if(CmdStr.contains(" reset"))
        {
            flag |= CMD_FLAG_PV_RESET;
        }
        if(CmdStr.contains(" ied"))
        {
            flag |= CMD_FLAG_PV_IED;
        }
        if(CmdStr.contains(" power"))
        {
            flag |= CMD_FLAG_PV_PWR;
        }

        _cmd["flag"] = CtiParseValue( flag );
    }
    else
    {
        // Something went WAY wrong....

        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "This better not ever be seen by mortals... " << endl;
    }
}


void  CtiCommandParser::doParsePutStatus(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    CtiString   temp2;
    CtiString   token;
    unsigned int flag = 0;

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
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Putstatus not supported for this device type " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Putstatus to unknown device type " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        }

        if(CmdStr.contains(" reset"))
        {
            if( _cmd.find("flag") != _cmd.end() )
            {
                flag = _cmd["flag"].getInt();
            }

            flag |= CMD_FLAG_PS_RESET;

            _cmd["flag"] = CtiParseValue(flag);
        }
    }
    else
    {
        // Something went WAY wrong....
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void  CtiCommandParser::doParseGetConfig(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    CtiString   temp2;
    CtiString   token;
    boost::regex    re_rolenum("role *" + str_num);
    boost::regex    re_rawcmd("raw (func(tion)? )?start *= *" + str_hexnum + "( " + str_num + ")?");
    boost::regex    re_interval("interval(s| lp| li)");  //  match "intervals", "interval lp", and "interval li"
    boost::regex    re_time("time( |$)");  //  only match "time" as a single word
    boost::regex    re_multiplier("mult(iplier)?( kyz *" + str_num + ")?");
    boost::regex    re_address("address (group|uniq)");
    boost::regex    re_lp_channel(" lp channel " + str_num);
    boost::regex    re_centron("centron (ratio|parameters)");
    boost::regex    re_tou("tou schedule [0-9]");

    char *p;

    int roleNum, channel;
    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "getconfig")
    {
        if(CmdStr.contains(" model"))
        {
            _cmd["model"] = CtiParseValue( "TRUE" );
        }
        if(CmdStr.contains(" options"))
        {
            _cmd["options"] = CtiParseValue( "TRUE" );
        }
        if(CmdStr.contains(" tou"))
        {
            _cmd["tou"] = CtiParseValue( "TRUE" );

            if(!(token = CmdStr.match(re_tou)).empty())
            {
                _cmd["tou_schedule"] = CtiParseValue(atoi(token.data() + 13));
            }
        }
        if(CmdStr.contains(" address"))
        {
            if(!(token = CmdStr.match(re_address)).empty())
            {
                if(token.contains("group"))
                {
                    _cmd["address_group"] = CtiParseValue(TRUE);
                }
/*                if(token.contains("uniq"))
                {
                    _cmd["address_unique"] = CtiParseValue(TRUE);
                }*/
            }
        }
        if(CmdStr.contains(" channels"))
        {
            _cmd["channels"] = CtiParseValue(TRUE);
        }
        if(CmdStr.contains(" codes"))
        {
            _cmd["codes"] = CtiParseValue(TRUE);
        }
        if(CmdStr.contains(" role"))
        {
            if(!(token = CmdStr.match(re_rolenum)).empty())
            {
                _cmd["rolenum"] = CtiParseValue( atoi( (token.match( re_num )).c_str() ) );
            }
        }
        if(CmdStr.contains(" mult"))
        {
            _cmd["multiplier"] = CtiParseValue(TRUE);

            if(!(token = CmdStr.match(re_multiplier)).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  multiplier
                cmdtok();  //  kyz
                _cmd["multchannel"] = CtiParseValue(atoi(cmdtok().c_str()));
            }
        }
        if(CmdStr.contains(" raw"))
        {
            if(!(token = CmdStr.match(re_rawcmd)).empty())
            {
                CtiTokenizer cmdtok(token);
                CtiString rawData;
                char *p;

                //  go past "raw"
                cmdtok();

                temp2 = cmdtok();

                if(temp2.contains("func"))
                {
                    _cmd["rawfunc"] = CtiParseValue("TRUE");
                    temp2 = cmdtok();
                }

                //  get the start address
                _cmd["rawloc"] = CtiParseValue( strtol((temp2.match(re_hexnum)).c_str(), &p, 16) );

                //  get the length
                temp2 = cmdtok();
                //  if there's length specified
                if( !temp2.empty() )
                {
                    _cmd["rawlen"] = CtiParseValue( atoi( temp2.c_str() ) );
                }
            }
        }
        if(CmdStr.contains(" lp"))
        {
            if(!(token = CmdStr.match(re_lp_channel)).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  lp
                cmdtok();  //  channel
                _cmd["lp_channel"] = CtiParseValue(atoi(cmdtok().c_str()));
            }
        }
        if(CmdStr.contains(" time"))
        {
            if(!(token = CmdStr.match(re_time)).empty())
            {
                _cmd["time"] = CtiParseValue("TRUE");

                if(CmdStr.contains("sync"))
                {
                    _cmd["sync"] = CtiParseValue("TRUE");
                }
            }
        }
        if(CmdStr.contains(" ied"))
        {
            _cmd["ied"] = CtiParseValue("TRUE");
        }
        if(CmdStr.contains(" scan"))
        {
            _cmd["scan"] = CtiParseValue("TRUE");
        }
        if(CmdStr.contains(" interval"))
        {
            if(!(token = CmdStr.match(re_interval)).empty())
            {
                CtiTokenizer cmdtok(token);
                //  get "interval(s?)"
                temp2 = cmdtok();

                //  if it's just "interval," get the next token
                if( temp2.compareTo("interval") == 0 )
                {
                    //  "li" or "lp"
                    temp2 = cmdtok();
                }

                //  so this is either "intervals," "li," or "lp"
                _cmd["interval"] = temp2;
            }
        }
        if(CmdStr.contains(" disc"))
        {
            _cmd["disconnect"] = CtiParseValue("TRUE");
        }
        if(CmdStr.contains(" centron"))
        {
            if(!(token = CmdStr.match(re_centron)).empty())
            {
                CtiTokenizer cmdtok(token);

                cmdtok();          //  move past "centron"

                temp2 = cmdtok();  //  grab the token we care about

                //  if it's just "interval," get the next token
                if( !temp2.compareTo("ratio") )
                {
                    _cmd["centron_ratio"] = CtiParseValue(true);
                }
                if( !temp2.compareTo("parameters") )
                {
                    _cmd["centron_parameters"] = CtiParseValue(true);
                }
            }
        }
    }
    else
    {
        // Something went WAY wrong....
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void  CtiCommandParser::doParsePutConfig(const string &_CmdStr)
{

    boost::regex  re_tou("tou [0-9]+( schedule [0-9]+( [a-z]/[0-9]+:[0-9]+)*)* default [a-z]");
    CtiString CmdStr(_CmdStr);
    CtiString   temp2;
    CtiString   token;

    CtiTokenizer   tok(CmdStr);
    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "putconfig")
    {
        INT type = getiValue("type");

        if(CmdStr.contains(" timesync"))
        {
            _cmd["timesync"] = CtiParseValue("TRUE");
        }

        // Template should be global.
        if(!(token = CmdStr.match("template " + str_quoted_token)).empty())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("template ", &nstop);

            nstop += nstart;

            if(!(token = token.match(str_quoted_token, nstop)).empty())   // get the template name...
            {
                token = token.substr((size_t)1, (size_t)(token.length() - 2));
                _cmd["template"] = CtiParseValue( token );
            }

            CtiString sistr;

            if(CmdStr.contains(" service in"))
            {
                sistr = "service in";
            }
            _cmd["templateinservice"] = CtiParseValue( sistr );
        }

        if(CmdStr.contains(" install"))
        {
            _cmd["install"] = CtiParseValue(TRUE);

            if(!(token = CmdStr.match("install +[a-z0-9]* *[a-z0-9]*")).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "install"

                _cmd["installvalue"] = CtiParseValue(cmdtok());

                token = cmdtok();
                if(!(token.match("force")).empty())
                {
                    _cmd["force"] = CtiParseValue(true);
                }


            }
        }

        if(CmdStr.contains(" tou"))
        {
            string tou_schedule;

            //  indicate that we at least tried to parse the TOU stuff
            _cmd["tou"] = CtiParseValue(true);

            if(!(tou_schedule = CmdStr.match(re_tou)).empty())
            {
                //  tou [0-9]+ (schedule [0-9]+( [a-z]/[0-9]+:[0-9]+)*)* default [a-z]
                CtiTokenizer tok(tou_schedule);

                token = tok();  //  tou

                _cmd["tou_days"] = CtiParseValue(tok());

                token = tok();

                int schedulenum = 0;
                while( !token.compareTo("schedule") )
                {
                    string schedule_name;
                    schedule_name.assign("tou_schedule_");
                    schedule_name.append(CtiNumStr(schedulenum).zpad(2));

                    //  even if there are no rates supplied, we did successfully parse it
                    _cmd[schedule_name.data()] = CtiParseValue(atoi(tok().data()));

                    token = tok();

                    int changenum = 0;
                    while(!token.match("[a-z]/[0-9]+:[0-9]+").empty())
                    {
                        string change_name;
                        change_name.assign(schedule_name);
                        change_name.append("_");
                        change_name.append(CtiNumStr(changenum).zpad(2));

                        _cmd[change_name.data()] = CtiParseValue(token);

                        token = tok();

                        changenum++;
                    }

                    schedulenum++;
                }

                if( !token.compareTo("default") )
                {
                    _cmd["tou_default"] = CtiParseValue(tok());
                }
            }
        }

        switch( type )
        {
        case ProtocolVersacomType:              // For putconfig, we may not know who we are talking to.  Decode for both.
            {
                doParsePutConfigVersacom(CmdStr);
                break;
            }
        case ProtocolEnergyProType:
            {
                // This will change over time
                doParsePutConfigExpresscom(CmdStr);

                if(CmdStr.contains(" reset"))
                {
                    if(CmdStr.contains(" filter"))
                    {
                        setValue("epresetfilter", TRUE);
                    }

                    if(CmdStr.contains(" runtime"))
                    {
                        setValue("epresetruntimes", TRUE);
                    }
                }

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
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Putconfig not supported for this device type " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        case ProtocolEmetconType:
            {
                doParsePutConfigEmetcon(CmdStr);
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Putconfig to unknown device type " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        }
    }

    else
    {
        // Something went WAY wrong....
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void  CtiCommandParser::doParseScan(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    CtiString   token;
    boost::regex    re_loadprofile("loadprofile( +channel +[1-4])?( +block +[0-9]+)?");

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "scan")
    {
        //  this should probably just be a bunch of if statements, no else-if...
        //    the tokenizer should just tokenize, not prioritize

        if(CmdStr.contains(" general"))             // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["scantype"] = CtiParseValue( ScanRateGeneral );
        }
        else if(CmdStr.contains(" integrity"))      // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["scantype"] = CtiParseValue( ScanRateIntegrity );
        }
        else if(CmdStr.contains(" accumulator"))    // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["scantype"] = CtiParseValue( ScanRateAccum );
        }
        else if(CmdStr.contains(" status"))         // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["scantype"] = CtiParseValue( ScanRateStatus );
        }
        else if(!(token = CmdStr.match(re_loadprofile)).empty())      // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["scantype"] = CtiParseValue( ScanRateLoadProfile );

            CtiTokenizer lp_tok(token);

            lp_tok();  //  pull "loadprofile"

            token = lp_tok();

            if( !(token.compareTo("channel")) )
            {
                token = lp_tok();

                _cmd["scan_loadprofile_channel"] = CtiParseValue(atoi(token.c_str()));

                token = lp_tok();
            }

            if( !(token.compareTo("block")) )
            {
                token = lp_tok();

                _cmd["scan_loadprofile_block"] = CtiParseValue(atoi(token.c_str()));
            }
        }
    }

    if(CmdStr.contains(" update"))
    {
        _cmd["flag"] = CtiParseValue( CMD_FLAG_UPDATE );
    }

}

UINT     CtiCommandParser::getCommand() const
{
    CtiParseValue& pv = CtiParseValue(); // = _cmd["command"];
    map_itr_type itr;

    itr = _cmd.find("command");
    if(itr != _cmd.end())
    {
        pv = (*itr).second;
    }
    //_cmd.findValue("command", pv);

    return pv.getInt();
}

UINT     CtiCommandParser::getFlags() const
{
    return getiValue("flag",0);
}

UINT     CtiCommandParser::getOffset() const
{
    return getiValue("offset",-1);
}

bool  CtiCommandParser::isKeyValid(const string key) const
{
    return( _cmd.find(key.c_str()) != _cmd.end() );
}

UINT     CtiCommandParser::getOffset(const string key) const
{
    CtiParseValue& pv = CtiParseValue(); // = _cmd["command"];
    map_itr_type itr;

    itr = _cmd.find(key.c_str());
    if(itr != _cmd.end())
    {
        pv = (*itr).second;
    }
    return pv.getInt();
}
INT      CtiCommandParser::getiValue(const string key, INT valifnotfound) const
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

DOUBLE   CtiCommandParser::getdValue(const string key, DOUBLE valifnotfound) const
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

string CtiCommandParser::getsValue(const string key) const
{
    CtiParseValue& pv = CtiParseValue();
    map_itr_type itr;
    itr = _cmd.find(key.c_str() );
    if(itr != _cmd.end())
    {
        pv = (*itr).second;
    }
    return pv.getString().c_str();
}

void  CtiCommandParser::doParsePutConfigEmetcon(const string &_CmdStr)
{
    //convert to CtiString for parsing.
    CtiString CmdStr(_CmdStr);
    CtiString temp2;
    CtiString token;
    boost::regex  re_rawcmd("raw (func(tion)? )?start=0x[0-9a-f]+( 0x[0-9a-f]+)*");
    boost::regex  re_rolecmd("role [0-9]+" \
                             " [0-9]+" \
                             " [0-9]+" \
                             " [0-9]+" \
                             " [0-9]+");
    boost::regex  re_interval("interval(s| lp| li)");  //  match "intervals", "interval lp" and "interval li"
    boost::regex  re_multiplier("mult(iplier)? kyz *[123] [0-9]+(\\.[0-9]+)?");  //  match "mult kyz # #(.###)
    boost::regex  re_ied_class("ied class [0-9]+ [0-9]+");
    boost::regex  re_ied_scan ("ied scan [0-9]+ [0-9]+");
    boost::regex  re_group_address("group (enable|disable)");
    boost::regex  re_address("address ((uniq(ue)? [0-9]+)|(gold [0-9]+ silver [0-9]+)|(bronze [0-9]+)|(lead meter [0-9]+ load [0-9]+))");
    boost::regex  re_centron("centron ((ratio [0-9]+)|(reading [0-9]+( [0-9]+)?))");
    boost::regex  re_loadlimit("load limit " + str_num + " "
                               + str_num);
    boost::regex  re_holiday("holiday " + str_num + "( " + str_date + ")+");

    char *p;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "putconfig")
    {
        if(CmdStr.contains(" ied"))
        {
            _cmd["ied"] = CtiParseValue(TRUE);

            if(!(token = CmdStr.match(re_ied_class)).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "ied"
                cmdtok();  //  go past "class"

                _cmd["class"]       = CtiParseValue(atoi(cmdtok().c_str()));
                _cmd["classoffset"] = CtiParseValue(atoi(cmdtok().c_str()));
            }
            if(!(token = CmdStr.match(re_ied_scan)).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "ied"
                cmdtok();  //  go past "scan"

                _cmd["scan"]      = CtiParseValue(atoi(cmdtok().c_str()));
                _cmd["scandelay"] = CtiParseValue(atoi(cmdtok().c_str()));
            }
        }
        if(CmdStr.contains(" onoffpeak"))
        {
            _cmd["onoffpeak"] = CtiParseValue(TRUE);
        }
        if(CmdStr.contains(" minmax"))
        {
            _cmd["minmax"] = CtiParseValue(TRUE);
        }
        if(CmdStr.contains(" disconnect"))
        {
            _cmd["disconnect"] = CtiParseValue(TRUE);

            if(!(token = CmdStr.match(re_loadlimit)).empty())
            {
                CtiTokenizer cmdtok(token);
                cmdtok();  //  go past "load"
                cmdtok();  //  go past "limit"

                _cmd["disconnect demand threshold"]        = CtiParseValue(atoi(cmdtok().c_str()));
                _cmd["disconnect load limit connect delay"] = CtiParseValue(atoi(cmdtok().c_str()));
            }
        }
        if(CmdStr.contains(" group"))
        {
            if(!(token = CmdStr.match(re_group_address)).empty())
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
        if(CmdStr.contains(" address"))
        {
            if(!(token = CmdStr.match(re_address)).empty())
            {
                _cmd["address"] = CtiParseValue(TRUE);

                CtiTokenizer cmdtok(token);

                cmdtok();  //  go past "address"

                token = cmdtok();

                if( token.contains("uniq") )
                {
                    _cmd["uniqueaddress"] = CtiParseValue(atoi(cmdtok().c_str()));
                }
                if( token.contains("gold") )
                {
                    _cmd["groupaddress_gold"] = CtiParseValue(atoi(cmdtok().c_str()));

                    cmdtok();  //  go past "silver"

                    _cmd["groupaddress_silver"] = CtiParseValue(atoi(cmdtok().c_str()));
                }
                else if( token.contains("bronze") )
                {
                    _cmd["groupaddress_bronze"] = CtiParseValue(atoi(cmdtok().c_str()));
                }
                else if( token.contains("lead") )
                {
                    cmdtok();  //  go past "meter"

                    _cmd["groupaddress_lead_meter"] = CtiParseValue(atoi(cmdtok().c_str()));

                    cmdtok();  //  go past "load"

                    _cmd["groupaddress_lead_load"] = CtiParseValue(atoi(cmdtok().c_str()));
                }
            }
        }
        if(CmdStr.contains(" mult"))
        {
            if(!(token = CmdStr.match(re_multiplier)).empty())
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
        if(CmdStr.contains(" interval"))
        {
            if(!(token = CmdStr.match(re_interval)).empty())
            {
                CtiTokenizer cmdtok(token);
                //  go past "interval"
                CtiString temp = cmdtok();

                if( temp.contains("intervals") )
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
        if(CmdStr.contains(" centron"))
        {
            if(!(token = CmdStr.match(re_centron)).empty())
            {
                CtiTokenizer cmdtok(token);

                CtiString temp;

                temp = cmdtok();
                temp = cmdtok();  //  move past "centron"

                if( !temp.compareTo("ratio") )
                {
                    _cmd["centron_ratio"] = CtiParseValue(atoi(CtiString(cmdtok()).c_str() ));
                }
                if( !temp.compareTo("reading") )
                {
                    _cmd["centron_reading_forward"] = CtiParseValue(CtiString(cmdtok()));
                    _cmd["centron_reading_reverse"] = CtiParseValue(CtiString(cmdtok()));
                }
            }
        }
        if(CmdStr.contains(" armc"))
        {
            _cmd["armc"] = CtiParseValue("TRUE");
        }
        if(CmdStr.contains(" arml"))
        {
            _cmd["arml"] = CtiParseValue("TRUE");
        }
        if(CmdStr.contains(" arms"))
        {
            _cmd["arms"] = CtiParseValue("TRUE");
        }
        if(CmdStr.contains(" raw"))
        {
            if(!(token = CmdStr.match(re_rawcmd)).empty())
            {
                CtiTokenizer cmdtok(token);
                CtiString rawData;
                int rawloc;

                //  go past "raw"
                cmdtok();

                temp2 = cmdtok();

                if(temp2.contains("func"))
                {
                    _cmd["rawfunc"] = CtiParseValue("TRUE");
                    temp2 = cmdtok();
                }

                _cmd["rawloc"] = CtiParseValue( strtol((temp2.match(re_hexnum)).c_str(), &p, 16) );

                while( !(temp2 = cmdtok()).empty() )
                {
                    rawData.appendLong( strtol(temp2.c_str(), &p, 16) );
                }

                _cmd["rawdata"] = CtiParseValue( rawData );
            }
        }
        if(CmdStr.contains(" role"))
        {
            if(!(token = CmdStr.match(re_rolecmd)).empty())
            {
                CtiTokenizer cmdtok(token);
                CtiString rawData;
                int rawloc;

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
        if(CmdStr.contains(" mrole"))
        {
            if(!(token = CmdStr.match("mrole( [0-9]+)+")).empty())
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

                CtiString strFixed;
                CtiString strVarOut;
                CtiString strVarIn;
                CtiString strStages;

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

#if 0
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Roles found " << rolecount << endl;

                    dout << " First role " << getiValue("multi_rolenum") << endl;
                    dout << " Fix   bits " << strFixed << endl;
                    dout << " OD    bits " << strVarOut << endl;
                    dout << " ID    bits " << strVarIn << endl;
                    dout << " STF   bits " << strStages << endl;
                }
#endif

                _cmd["multi_rolefixed"] = CtiParseValue(strFixed.strip());
                _cmd["multi_roleout"]   = CtiParseValue(strVarOut.strip());
                _cmd["multi_rolein"]    = CtiParseValue(strVarIn.strip());
                _cmd["multi_rolerpt"]   = CtiParseValue(strStages.strip());
                _cmd["multi_rolecount"] = CtiParseValue(rolecount);
            }
        }
        if(CmdStr.contains(" holiday"))
        {
            if(!(token = CmdStr.match(re_holiday)).empty())
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
        }
    }
    else
    {
        // Something went WAY wrong....
        dout << "This better not ever be seen by mortals... " << endl;
    }
}
void  CtiCommandParser::doParsePutConfigVersacom(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    char *p;

    char        tbuf[60];

    CtiString   token;
    CtiString   temp, temp2;
    CtiString   strnum;
    CtiString   str;

    INT         _num = 0;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "putconfig")
    {
        if(!(token = CmdStr.match("vdata( (0x)?[0-9a-f])+")).empty())
        {
            CtiTokenizer cmdtok(token);
            CtiString rawData;

            //  go past "vdata"
            cmdtok();

            while( !(temp2 = cmdtok()).empty() )
            {
                rawData.append( 1,(char)strtol(temp2.c_str(), &p, 16) );
            }

            _cmd["vdata"] = CtiParseValue( rawData );
        }

        if(!(token = CmdStr.match(" util(ity)?[ =]*([ =]+0x)?[0-9a-f]+")).empty())
        {
            if(!(temp = token.match(re_hexnum)).empty())
            {
                _num = strtol(temp.c_str(), &p, 16);
            }
            else
            {
                _num = strtol(token.match(re_num).c_str(), &p, 10);
            }

            _cmd["utility"] = CtiParseValue( _num );

            _snprintf(tbuf, sizeof(tbuf), "CONFIG UID = %d", _num);
            _actionItems.push_back(tbuf);
        }

        if(!(token = CmdStr.match(" aux*[ =]*([ =]+0x)?[0-9a-f]+")).empty())
        {
            if(!(temp = token.match(re_hexnum)).empty())
            {
                _num = strtol(temp.c_str(), &p, 16);
            }
            else
            {
                _num = strtol(token.match(re_num).c_str(), &p, 10);
            }

            _cmd["aux"] = CtiParseValue( _num );

            _snprintf(tbuf, sizeof(tbuf), "CONFIG AUX = %d", _num);
            _actionItems.push_back(tbuf);
        }

        if(!(token = CmdStr.match(" sect(ion)?[ =]*[0-9]+")).empty())
        {
            {
                _num = strtol(token.match(re_num).c_str(), &p, 10);
            }

            _cmd["section"] = CtiParseValue( _num );

            _snprintf(tbuf, sizeof(tbuf), "CONFIG SECTION = %d", _num);
            _actionItems.push_back(tbuf);
        }

        if(CmdStr.contains(" clas"))
        {
            if(!(token = CmdStr.match(" class[ =]*([ =]+)?0x[0-9a-f]+")).empty())
            {
                if(!(temp = token.match(re_hexnum)).empty())
                {
                    _num = strtol(temp.c_str(), &p, 16);
                }
                else
                {
                    _num = strtol(token.match(re_num).c_str(), &p, 10);
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
            else if(!(token = CmdStr.match(" class [0-9]+" \
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
                CtiTokenizer   tok( token );
                _num = 0;
                int  mask = 0x0001;
                int  val;

                tok();   // Get over the class entry

                // dout << __LINE__ << " " << token << endl;

                for(int i = 0; i < 16 && !(temp = tok(", \n\0")).empty() ; i++)
                {
                    val = atoi( temp.c_str() );

                    // dout << "Masking in " << temp << " " << val << endl;
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

        if(CmdStr.contains(" divi"))
        {
            if(!(token = CmdStr.match(" divi(sion)?[ =]*([ =]+)?0x[0-9a-f]+")).empty())
            {
                if(!(temp = token.match(re_hexnum)).empty())
                {
                    _num = strtol(temp.c_str(), &p, 16);
                }
                else
                {
                    _num = strtol(token.match(re_num).c_str(), &p, 10);
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
            else if(!(token = CmdStr.match(" divi(sion)? [0-9]+" \
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
                CtiTokenizer   tok( token );
                _num = 0;
                int  mask = 0x0001;
                int  val;

                tok();   // Get over the "division" entry

                // dout << __LINE__ << " " << token << endl;

                for(int i = 0; i < 16 && !(temp = tok(", \n\0")).empty() ; i++)
                {
                    val = atoi( temp.c_str() );

                    // dout << "Masking in " << temp << " " << val << endl;
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


        if(CmdStr.contains("from"))
        {
            if(!(token = CmdStr.match("fromutil(ity)?[ =]*([ =]+0x)?[0-9a-f]+")).empty())
            {
                if(!(temp = token.match(re_hexnum)).empty())
                {
                    _num = strtol(temp.c_str(), &p, 16);
                }
                else
                {
                    _num = strtol(token.match(re_num).c_str(), &p, 10);
                }

                _cmd["fromutility"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG FROM UID = %d", _num);
                _actionItems.push_back(tbuf);
            }

            if(!(token = CmdStr.match("fromsect(ion)?[ =]*([ =]+(0x))?[0-9a-f]+")).empty())
            {
                if(!(temp = token.match(re_hexnum)).empty())
                {
                    _num = strtol(temp.c_str(), &p, 16);
                }
                else
                {
                    _num = strtol(token.match(re_num).c_str(), &p, 10);
                }

                _cmd["fromsection"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG FROM SECTION = %d", _num);
                _actionItems.push_back(tbuf);
            }

            if(!(token = CmdStr.match("fromclass[ =]*([ =]+)?0x[0-9a-f]+")).empty())
            {
                if(!(temp = token.match(re_hexnum)).empty())
                {
                    _num = strtol(temp.c_str(), &p, 16);
                }
                else
                {
                    _num = strtol(token.match(re_num).c_str(), &p, 10);
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

            if(!(token = CmdStr.match("fromdivi(sion)?[ =]*([ =]+)?0x[0-9a-f]+")).empty())
            {
                if(!(temp = token.match(re_hexnum)).empty())
                {
                    _num = strtol(temp.c_str(), &p, 16);
                }
                else
                {
                    _num = strtol(token.match(re_num).c_str(), &p, 10);
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

        if(CmdStr.contains("assign"))
        {
            if(!(token = CmdStr.match("assign"\
                                      "(( [uascd][ =]*(0x)?[0-9a-f]+)*)+")).empty())
            {
                // dout << token << endl;
                _cmd["vcassign"] = CtiParseValue( TRUE );

                if(!(strnum = token.match(" u[ =]*(0x)?[0-9a-f]+")).empty())
                {
                    _num = strtol(strnum.match(re_anynum).c_str(), &p, 0);

                    _cmd["utility"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG UTILITY = %d", _num);
                    _actionItems.push_back(tbuf);
                }
                if(!(strnum = token.match(" a[ =]*(0x)?[0-9a-f]+")).empty())
                {
                    if(!(temp = strnum.match(re_hexnum)).empty())
                    {
                        _num = strtol(temp.c_str(), &p, 16);
                    }
                    else
                    {
                        _num = strtol(strnum.match(re_num).c_str(), &p, 10);
                    }
                    _cmd["aux"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG AUX = %d", _num);
                    _actionItems.push_back(tbuf);
                }
                if(!(strnum = token.match(" s[ =]*[0-9]+")).empty())
                {
                    _num = strtol(strnum.match(re_num).c_str(), &p, 10);
                    _cmd["section"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG SECTION = %d", _num);
                    _actionItems.push_back(tbuf);
                }
                if(!(strnum = token.match(" c[ =]*(0x)?[0-9a-f]+")).empty())
                {
                    if(!(temp = strnum.match(re_hexnum)).empty())
                    {
                        _num = strtol(temp.c_str(), &p, 16);
                    }
                    else
                    {
                        _num = strtol(strnum.match(re_num).c_str(), &p, 10);
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
                if(!(strnum = token.match(" d[ =]*(0x)?[0-9a-f]+")).empty())
                {
                    if(!(temp = strnum.match(re_hexnum)).empty())
                    {
                        _num = strtol(temp.c_str(), &p, 16);
                    }
                    else
                    {
                        _num = strtol(strnum.match(re_num).c_str(), &p, 10);
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

                if(CmdStr.contains(" service in"))
                {
                    serviceflag |= (0x08 | 0x02); // (VC_SERVICE_C_IN | VC_SERVICE_T_IN);
                    _actionItems.push_back("SERVICE ENABLE");
                }
                else if( CmdStr.contains(" service out"))
                {
                    serviceflag |= 0x04;
                    _actionItems.push_back("SERVICE DISABLE");
                }

                if(CmdStr.contains(" service tin"))
                {
                    serviceflag |= 0x02;
                    _actionItems.push_back("SERVICE ENABLE TEMPORARY");
                }
                else if(CmdStr.contains(" service tout"))
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

        if(CmdStr.contains("serv"))
        {
            if(!(token = CmdStr.match(" serv(ice)? (in|out|enable|disable)( temp)?")).empty())
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
                if(token.contains("in") ||
                   token.contains("enable"))
                {
                    flag |= 0x0a;

                    _snprintf(tbuf, sizeof(tbuf), "SERVICE ENABLE");
                }
                else if(token.contains("out") ||
                        token.contains("disable"))
                {
                    flag |= 0x04;

                    _snprintf(tbuf, sizeof(tbuf), "SERVICE DISABLE");
                }

                if(token.contains(" temp"))
                {
                    char t2[80];
                    ::strcpy(t2, tbuf);

                    flag >>= 2;       // Make the flag match the protocol

                    ::_snprintf(tbuf, sizeof(tbuf), "%s TEMPORARY", t2);

                    if(!(token = CmdStr.match("offhours [0-9]+")).empty())
                    {
                        bool offhourssupported = false;
                        int serialnumber = getiValue("serial", 0);

                        if( serialnumber != 0 )
                        {
                            CtiString vcrangestr = gConfigParms.getValueAsString("LCR_VERSACOM_EXTENDED_TSERVICE_RANGES").data();

                            if(!vcrangestr.empty())
                            {
                                int loopcnt = 0;
                                while(!vcrangestr.empty())
                                {
                                    CtiString rstr = vcrangestr.match("[0-9]*-[0-9]*,?");

                                    if(!rstr.empty())
                                    {
                                        char *chptr;
                                        CtiString startstr = rstr.match("[0-9]*");
                                        CtiString stopstr = rstr.match(" *- *[0-9]* *,? *");
                                        stopstr = stopstr.strip(CtiString::both, ' ');
                                        stopstr = stopstr.strip(CtiString::leading, '-');
                                        stopstr = stopstr.strip(CtiString::trailing, ',');
                                        stopstr = stopstr.strip(CtiString::both, ' ');

                                        UINT startaddr = strtoul( startstr.c_str(), &chptr, 10 );
                                        UINT stopaddr = strtoul( stopstr.c_str(), &chptr, 10 );

                                        if(startaddr <= serialnumber && serialnumber <= stopaddr)
                                        {
                                            {
                                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                dout << " Range " << startaddr << " to " << stopaddr << " found and recorded for VersaCom Extended Addressing" << endl;
                                            }

                                            // This is a supported versacom switch and we can continue!
                                            offhourssupported = true;
                                            break;
                                        }
                                    }

                                    vcrangestr.replace("[0-9]*-[0-9]*,?", "");

                                    if(loopcnt++ > 256)
                                    {
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << " **** ERROR **** Problem found with configuration item LCR_VERSACOM_EXTENDED_TSERVICE_RANGES : \"" << gConfigParms.getValueAsString("LCR_VERSACOM_EXTENDED_TSERVICE_RANGES") << "\"" << endl;
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        if(offhourssupported)
                        {
                            str = token.match(re_num);
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

        if(!(token = CmdStr.match("led (y|n) *(y|n) *(y|n)")).empty())
        {
            INT   flag = 0;
            int   i;
            int   mask;

            token.toLower();

            if(!(strnum = token.match("(y|n) *(y|n) *(y|n)")).empty())
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

        if(CmdStr.contains("reset"))
        {
            if(!(token = CmdStr.match("reset[_a-z]*( (rtc|lf|r1|r2|r3|r4|cl|pt))+")).empty())
            {
                INT   flag = 0;

                if(token.contains("rtc"))
                {
                    flag |= 0x40;
                }
                if(token.contains("lf"))
                {
                    flag |= 0x20;
                }
                if(token.contains("r1"))
                {
                    flag |= 0x10;
                }
                if(token.contains("r2"))
                {
                    flag |= 0x08;
                }
                if(token.contains("r3"))
                {
                    flag |= 0x04;
                }
                if(token.contains("r4"))
                {
                    flag |= 0x80;
                }
                if(token.contains("cl"))
                {
                    flag |= 0x02;
                }
                if(token.contains("pt"))
                {
                    flag |= 0x01;
                }

                _cmd["reset"] = CtiParseValue( flag );

                _snprintf(tbuf, sizeof(tbuf), "CNTR RESET = 0x%02X", flag);
                _actionItems.push_back(tbuf);
            }
        }

        if(CmdStr.contains("prop"))
        {
            if(!(token = CmdStr.match("prop[ a-z_]*[ =]*([ =]+)?[0-9]+")).empty())
            {
                // What offset is needed now...
                if(!(strnum = token.match(re_num)).empty())
                {
                    _num = strtol(strnum.c_str(), &p, 10);
                    _cmd["proptime"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG PROPTIME = %d", _num);
                    _actionItems.push_back(tbuf);
                }
            }
        }

        if(CmdStr.contains(" cold"))
        {
            if(!(token = CmdStr.match("cold[ a-z_]*" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)?" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)?")).empty())
            {
                // dout << token << endl;

                if(!(strnum = token.match("r1[ =]*[0-9]+[ =]*[hms]?")).empty())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    // _num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["coldload_r1"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG COLDLOAD R1 = %d", _num);
                    _actionItems.push_back(tbuf);
                }
                if(!(strnum = token.match("r2[ =]*[0-9]+[ =]*[hms]?")).empty())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(strnum.match(re_anynum).c_str(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["coldload_r2"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG COLDLOAD R2 = %d", _num);
                    _actionItems.push_back(tbuf);
                }
                if(!(strnum = token.match("r3[ =]*[0-9]+[ =]*[hms]?")).empty())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(strnum.match(re_anynum).c_str(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["coldload_r3"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG COLDLOAD R3 = %d", _num);
                    _actionItems.push_back(tbuf);
                }
            }
        }

        if(CmdStr.contains(" cycle"))
        {
            if(!(token = CmdStr.match("cycle[ a-z]*" \
                                      "( *r[123][ =]*[0-9]+[ =]*)" \
                                      "( *r[123][ =]*[0-9]+[ =]*)?" \
                                      "( *r[123][ =]*[0-9]+[ =]*)?")).empty())
            {
                if(!(strnum = token.match("r1[ =]*[0-9]+")).empty())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    _num = strtol(strnum.match(re_anynum).c_str(), &p, 0);
                    _cmd["cycle_r1"] = CtiParseValue( _num );
                }
                if(!(strnum = token.match("r2[ =]*[0-9]+[ =]*")).empty())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    _num = strtol(strnum.match(re_anynum).c_str(), &p, 0);
                    _cmd["cycle_r2"] = CtiParseValue( _num );
                }
                if(!(strnum = token.match("r3[ =]*[0-9]+[ =]*")).empty())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    _num = strtol(strnum.match(re_anynum).c_str(), &p, 0);
                    _cmd["cycle_r3"] = CtiParseValue( _num );
                }
            }
        }

        if(CmdStr.contains(" scram"))
        {
            if(!(token = CmdStr.match("scram[ a-z]*" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)?" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)?")).empty())
            {
                if(!(strnum = token.match("r1[ =]*[0-9]+[ =]*[hms]?")).empty())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(strnum.match(re_anynum).c_str(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["scram_r1"] = CtiParseValue( _num );
                }
                if(!(strnum = token.match("r2[ =]*[0-9]+[ =]*[hms]?")).empty())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(strnum.match(re_anynum).c_str(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["scram_r2"] = CtiParseValue( _num );
                }
                if(!(strnum = token.match("r3[ =]*[0-9]+[ =]*[hms]?")).empty())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(strnum.match(re_anynum).c_str(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["scram_r3"] = CtiParseValue( _num );
                }
            }
        }

        if(CmdStr.contains(" raw"))
        {
            if(!(token = CmdStr.match("raw" \
                                      "( *([ =]+0x)?[0-9a-f]+)" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?" \
                                      "( *([ =]+0x)?[0-9a-f]+)?")).empty())
            {
                int   i;
                int   val;
                char str[10];
                char *ptr = NULL;

                // dout << __LINE__ << " " << token << endl;

                CtiParseValue  rawStr;     // This is to contain the raw bytes



                CtiTokenizer   tok( token );

                tok();   // Get us past the "raw" moniker..

                for(i = 0; i < 20 && !token.empty(); i++)
                {
                    _snprintf(str, sizeof(str), "raw_byte_%d", i);
                    token = tok(); // Returns each whitespace seperated token successivly.

                    if(!token.empty())
                    {
                        val = strtol( token.c_str(), &ptr, 16 );
                    }
                    else
                    {
                        val = 0;
                    }

                    rawStr.getString().append((CHAR)val, 1);
                }

                _cmd[ "raw" ] = rawStr;
            }
        }

        if(CmdStr.contains(" lcrmode"))
        {
            if(!(token = CmdStr.match("lcrmode (e|v)")).empty())
            {
                CtiTokenizer tok( token );

                tok();   // Get us past "lcrmode"

                _cmd["lcrmode"] = CtiParseValue(tok());
            }
        }

        if(CmdStr.contains(" eclp"))
        {
            //  emetcon cold load pickup
            if(!(token = CmdStr.match("eclp (en(able)?|dis(able)?)")).empty())
            {
                CtiTokenizer tok( token );

                tok();   // Get us past "eclp" moniker..

                _cmd["eclp"] = CtiParseValue(tok());
            }
        }

        if(CmdStr.contains(" gold"))
        {
            if(!(token = CmdStr.match("gold [0-9]")).empty())
            {
                CtiTokenizer tok( token );

                tok();   // Get us past "gold"

                _cmd["gold"] = CtiParseValue(atoi(tok().c_str()));
            }
        }

        if(CmdStr.contains(" silver"))
        {
            if(!(token = CmdStr.match("silver [0-9][0-9]")).empty())
            {
                CtiTokenizer tok( token );

                tok();   // Get us past "silver"

                _cmd["silver"] = CtiParseValue(atoi(tok().c_str()));
            }
        }

        if(!(token = CmdStr.match("ovuv[ =]+((ena(ble)?)|(dis(able)?))")).empty())
        {
            int   op = 0;
            CHAR  op_name[20];

            if(token.contains("ena"))
            {
                op = 1;
                _snprintf(op_name, sizeof(op_name), "ENABLE");
            }
            else if(token.contains("dis"))
            {
                op = 0;
                _snprintf(op_name, sizeof(op_name), "DISABLE");
            }

            _cmd["ovuv"] = CtiParseValue( op );

            _snprintf(tbuf, sizeof(tbuf), "OVUV %s", op_name);
            _actionItems.push_back(tbuf);
        }

        if(!(token = CmdStr.match(" sync|filler")).empty())
        {
            _cmd["vcfiller"] = TRUE;
        }
    }
    else
    {
        // Something went WAY wrong....
        dout << "This better not ever be seen by mortals... " << endl;
    }

    if(!(CmdStr.match("test_mode_flag")).empty())
    {
        _cmd["flag"] = CtiParseValue( CMD_FLAG_TESTMODE );
    }
}

void  CtiCommandParser::doParsePutStatusEmetcon(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    CtiString   temp2;
    CtiString   token;
    unsigned int flag = 0;
    char *p;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "putstatus")
    {
        if(CmdStr.contains(" rovr"))
        {
            unsigned int flag = getiValue("flag", 0);

            flag |= CMD_FLAG_PS_RESETOVERRIDE;

            _cmd["flag"] = CtiParseValue(flag);
        }
        if(CmdStr.contains(" peak"))
        {
            if(CmdStr.contains(" peak on"))
            {
                _cmd["peak"] = CtiParseValue(TRUE);
            }
            else if(CmdStr.contains(" peak off"))
            {
                _cmd["peak"] = CtiParseValue(FALSE);
            }
        }
        if(CmdStr.contains(" freeze"))
        {
            if(CmdStr.contains(" one"))
            {
                _cmd["freeze"] = CtiParseValue(1);
            }
            else if(CmdStr.contains(" two"))
            {
                _cmd["freeze"] = CtiParseValue(2);
            }
            else
            {
                _cmd["freeze"] = CtiParseValue(0);
            }

            if(CmdStr.contains(" voltage"))
            {
                _cmd["voltage"] = CtiParseValue(TRUE);
            }
        }
    }
    else
    {
        // Something went WAY wrong....
        dout << "This better not ever be seen by mortals... " << endl;
    }
}

void  CtiCommandParser::doParsePutStatusVersacom(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    char *p;

    char        tbuf[60];

    CtiString   token;
    CtiString   temp2;
    CtiString   strnum;

    INT         _num = 0;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "putstatus")
    {
        if(!(token = CmdStr.match("prop[a-z]*[ =]+((disp[a-z]*)|(inc[a-z]*)|(term[a-z]*))")).empty())
        {
            int   op = 0;
            CHAR  op_name[20];

            if(!(token.match("disp(lay)?")).empty())                   // Turn on light & bump counter.
            {
                op = 0x04;
                _snprintf(op_name, sizeof(op_name), "DISPLAY");
            }
            else if(!(token.match("inc(rement)?")).empty())            // bump counter.
            {
                op = 0x02;
                _snprintf(op_name, sizeof(op_name), "INCREMENT");
            }
            else if(!(token.match("term(inate)?")).empty())            // Turn off light.
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
        else if(!(token = CmdStr.match("ovuv[ =]+((ena(ble)?)|(dis(able)?))")).empty())
        {
            int   op = 0;
            CHAR  op_name[20];

            if(token.contains("ena"))
            {
                op = 1;
                _snprintf(op_name, sizeof(op_name), "ENABLE");
            }
            else if(token.contains("dis"))
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
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** ERROR **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void  CtiCommandParser::doParsePutStatusFisherP(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    char *p;
    char        tbuf[60];

    CtiString   token;
    CtiString   temp2;
    CtiString   strnum;

    INT         _num = 0;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.empty() && token == "putstatus")
    {

        if(!(token = CmdStr.match("ovuv[ =]+((ena(ble)?)|(dis(able)?))")).empty())
        {
            int   op = 0;
            CHAR  op_name[20];

            if(token.contains("ena"))
            {
                op = 1;
                _snprintf(op_name, sizeof(op_name), "ENABLE");
            }
            else if(token.contains("dis"))
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
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** ERROR **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

std::list< string >& CtiCommandParser::getActionItems()
{
    return _actionItems;
}

void CtiCommandParser::resolveProtocolType(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    int loopcnt;
    CtiString         token;

    /*
     *  Type is assigned i.f.f. there is a serial number specified.  The default type is versacom.
     */
    if(!isKeyValid("type"))
    {
        if( isKeyValid("serial") )
        {
            if(CmdStr.contains("fp"))            // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "fp",  ProtocolFisherPierceType );
            }
            else if(CmdStr.contains("golay"))       // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "golay", ProtocolGolayType );
            }
            else if(CmdStr.contains("sadig"))       // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "sadig", ProtocolSADigitalType );
            }
            else if(CmdStr.contains("sa105"))       // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "sa105", ProtocolSA105Type );
            }
            else if(CmdStr.contains("sa205"))       // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "sa205", ProtocolSA205Type );
            }
            else if(CmdStr.contains("sa305"))       // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "sa305", ProtocolSA305Type );
            }
            else if(CmdStr.contains("epro"))
            {
                if(CmdStr.contains(" nooverride"))
                {
                    _cmd["overridedisable"] = CtiParseValue( TRUE );
                }
                _cmd["type"] = CtiParseValue( "energypro", ProtocolEnergyProType );
            }
            else if(CmdStr.contains("xcom") || CmdStr.contains("expresscom"))
            {
                _cmd["type"] = CtiParseValue( "expresscom", ProtocolExpresscomType );
            }
            else if(CmdStr.contains("vcom") || CmdStr.contains("versacom") ||
                    CmdStr.contains("emetcon"))
            {
                //  NOTE:  all serial number requests with the "emetcon" specifier shall be parsed as Versacom instead.
                //           this is because DLC LCRs are "emetcon" devices in many peoples' minds, despite being addressed
                //           as Versacom for all serial-number (non Gold/Silver) addresses
                //  _cmd["type"] = CtiParseValue( "emetcon",  ProtocolEmetconType );

                _cmd["type"] = CtiParseValue( "versacom", ProtocolVersacomType );
            }
            else
            {
                int serialnumber = getiValue("serial", 0);

                CtiString xcrangestr = gConfigParms.getValueAsString("LCR_EXPRESSCOM_RANGES").data();
                CtiString vcrangestr = gConfigParms.getValueAsString("LCR_VERSACOM_RANGES").data();

                if(!vcrangestr.empty() || !xcrangestr.empty())
                {
                    loopcnt = 0;
                    while(!vcrangestr.empty())
                    {
                        CtiString str = vcrangestr.match("[0-9]*-[0-9]*,?");

                        if(!str.empty())
                        {
                            char *chptr;
                            CtiString startstr = str.match("[0-9]*");
                            CtiString stopstr = str.match(" *- *[0-9]* *,? *");
                            stopstr = stopstr.strip(CtiString::both, ' ');
                            stopstr = stopstr.strip(CtiString::leading, '-');
                            stopstr = stopstr.strip(CtiString::trailing, ',');
                            stopstr = stopstr.strip(CtiString::both, ' ');

                            UINT startaddr = strtoul( startstr.c_str(), &chptr, 10 );
                            UINT stopaddr = strtoul( stopstr.c_str(), &chptr, 10 );

                            if(startaddr <= serialnumber && serialnumber <= stopaddr)
                            {
                                // This is a versacom switch and we can continue!
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << " Range " << startaddr << " to " << stopaddr << " found and recorded for VersaCom" << endl;
                                }

                                _cmd["type"] = CtiParseValue( "versacom", ProtocolVersacomType );
                                break;
                            }
                        }

                        vcrangestr.replace("[0-9]*-[0-9]*,?", "");

                        if(loopcnt++ > 256)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** ERROR **** Problem found with configuration item LCR_VERSACOM_RANGES : \"" << gConfigParms.getValueAsString("LCR_VERSACOM_RANGES") << "\"" << endl;
                                break;
                            }
                        }
                    }

                    loopcnt = 0;
                    while(!isKeyValid("type") && !xcrangestr.empty())
                    {
                        CtiString str = xcrangestr.match("[0-9]*-[0-9]*,?");

                        if(!str.empty())
                        {
                            char *chptr;
                            CtiString startstr = str.match("[0-9]*");
                            CtiString stopstr = str.match(" *- *[0-9]* *,? *");
                            stopstr = stopstr.strip(CtiString::both, ' ');
                            stopstr = stopstr.strip(CtiString::leading, '-');
                            stopstr = stopstr.strip(CtiString::trailing, ',');
                            stopstr = stopstr.strip(CtiString::both, ' ');

                            UINT startaddr = strtoul( startstr.c_str(), &chptr, 10 );
                            UINT stopaddr = strtoul( stopstr.c_str(), &chptr, 10 );

                            if(startaddr <= serialnumber && serialnumber <= stopaddr)
                            {
                                // This is a versacom switch and we can continue!

                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << " Range " << startaddr << " to " << stopaddr << " found and recorded for ExpressCom" << endl;
                                }
                                _cmd["type"] = CtiParseValue( "expresscom", ProtocolExpresscomType );
                                break;
                            }
                        }

                        xcrangestr.replace("[0-9]*-[0-9]*,?", "");

                        if(loopcnt++ > 256)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** ERROR **** Problem found with configuration item LCR_EXPRESSCOM_RANGES : \"" << gConfigParms.getValueAsString("LCR_EXPRESSCOM_RANGES") << "\"" << endl;
                                break;
                            }
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
                CtiString xcprefixrange = gConfigParms.getValueAsString("LCR_EXPRESSCOM_SERIAL_PREFIX_RANGES").data();
                loopcnt = 0;
                if(!xcprefixrange.empty())
                {
                    while(!xcprefixrange.empty())
                    {
                        CtiString str = xcprefixrange.match("[0-9]*-[0-9]*,?");

                        if(!str.empty())
                        {
                            char *chptr;
                            CtiString startstr = str.match("[0-9]*");
                            CtiString stopstr = str.match(" *- *[0-9]* *,? *");
                            stopstr = stopstr.strip(CtiString::both, ' ');
                            stopstr = stopstr.strip(CtiString::leading, '-');
                            stopstr = stopstr.strip(CtiString::trailing, ',');
                            stopstr = stopstr.strip(CtiString::both, ' ');

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

                        xcprefixrange.replace("[0-9]*-[0-9]*,?", "");

                        if(loopcnt++ > 256)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** ERROR **** Problem found with configuration item LCR_EXPRESSCOM_SERIAL_PREFIX_RANGES : \"" << gConfigParms.getValueAsString("LCR_EXPRESSCOM_SERIAL_PREFIX_RANGES") << "\"" << endl;
                                break;
                            }
                        }
                    }
                }
            }
        }
        else
        {
            //  check for "emetcon" protocol
            if(CmdStr.contains("emetcon"))
            {
                _cmd["type"] = CtiParseValue( "emetcon",  ProtocolEmetconType );
            }
            else if(CmdStr.contains("xcom") || CmdStr.contains("expresscom"))
            {
                _cmd["type"] = CtiParseValue( "expresscom", ProtocolExpresscomType );
            }
            else if(CmdStr.contains("golay"))       // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "golay", ProtocolGolayType );
            }
            else if(CmdStr.contains("sadig"))       // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "sadig", ProtocolSADigitalType );
            }
            else if(CmdStr.contains("sa105"))       // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "sa105", ProtocolSA105Type );
            }
            else if(CmdStr.contains("sa205"))       // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "sa205", ProtocolSA205Type );
            }
            else if(CmdStr.contains("sa305"))       // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "sa305", ProtocolSA305Type );
            }
            else
            {  //  default to Versacom if nothing found
                _cmd["type"] = CtiParseValue( "versacom", ProtocolVersacomType );
            }
        }
    }
}

const string& CtiCommandParser::getCommandStr() const
{
    return(_cmdString);
}

INT CtiCommandParser::convertTimeInputToSeconds(const string& _inStr) const
{
    CtiString inStr(_inStr);
    INT val  = -1;
    INT mult = 60; //assume minutes for fun ok.

    boost::regex   re_scale("[hms]");
    CtiString   temp2;


    if(!inStr.match(re_num).empty())
    {
        val = atoi(inStr.match(re_num).data());

        if(!(temp2 = inStr.match(re_scale)).empty())
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
    CtiString rstr;

    map_itr_type itr = _cmd.begin();

    for(; itr != _cmd.end(); ++itr )
    {
        if(rstr.length() > 0)
        {
            rstr += CtiString(":");
        }
        rstr += CtiString((*itr).first) + CtiString("=") + ( (*itr).second.getString().length() ? (*itr).second.getString(): CtiString("(none)")) + CtiString(",") + CtiNumStr((*itr).second.getInt()) + CtiString(",") + CtiNumStr((*itr).second.getReal());
    }
    return rstr;
}

void CtiCommandParser::Dump()
{
    CHAR  oldFill = dout.fill();

    dout.fill('0');

    map_itr_type itr = _cmd.begin();

    for(; itr != _cmd.end() ; ++itr )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Key " << (*itr).first << " Value (str) " << (*itr).second.getString() <<
            " (int) " << (int)((*itr).second.getInt()) <<
            " (dbl) " << (*itr).second.getReal() <<
            " (bytes) ";
        }

        for(int i = 0; i < (*itr).second.getString().length(); i++ )
        {
            dout << hex << setw(2) << (INT)((*itr).second.getString()[i]) << " ";
        }

        dout << endl;
    }

    dout << dec << endl;
    dout.fill(oldFill);
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

CtiCommandParser& CtiCommandParser::setValue(const string key, INT val)
{
    _cmd[key.c_str()] = CtiParseValue(val);
    return *this;
}
CtiCommandParser& CtiCommandParser::setValue(const string key, DOUBLE val)
{
    _cmd[key.c_str()] = CtiParseValue(val);
    return *this;
}
CtiCommandParser& CtiCommandParser::setValue(const string key, string val)
{
    _cmd[key.c_str()] = CtiParseValue(val.c_str());
    return *this;
}

void  CtiCommandParser::doParseControlExpresscom(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    INT         _num;
    UINT        flag   = 0;
    UINT        offset = 0;
    INT         iValue = 0;
    DOUBLE      dValue = 0.0;

    CHAR        tbuf[80];
    CHAR        tbuf2[80];

    CtiString   str;
    CtiString   temp;
    CtiString   valStr;
    CtiString   token;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....


    if(!(temp = CmdStr.match(" mode (heat)|(cool)|(both)")).empty())
    {
        if(temp.contains("cool"))
        {
            iValue = 1;
        }
        else if(temp.contains("heat"))
        {
            iValue = 2;
        }
        else if(temp.contains("both"))
        {
            iValue = 3;
        }

        _cmd["xcmode"] = CtiParseValue( iValue );
    }


    if(CmdStr.contains(" rand"))
    {
        if(!(temp = CmdStr.match(" rand(om)? ?start [0-9]+")).empty())
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcrandstart"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" rand(om)? ?stop [0-9]+")).empty())
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcrandstop"] = CtiParseValue( iValue );
            }
        }
    }

    if( CmdStr.contains(" flip", CtiString::ignoreCase) )
    {
        _cmd["xcflip"] = CtiParseValue( TRUE );
    }
    if(!(token = CmdStr.match(" tcycle [0-9]+")).empty())
    {
        _cmd["xctcycle"] = CtiParseValue( TRUE );

        if(!(temp = token.match(re_num)).empty())
        {
            iValue = atoi(temp.c_str());
        }
        else
        {
            // Something went kinda wrong....
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Command Parameter Assumed.  Cycle control at 50 percent cycle. " << endl;
            }
            iValue = 50;
        }

        _cmd["cycle"] = CtiParseValue( (iValue) );
        _snprintf(tbuf, sizeof(tbuf), "CYCLE %d%%", iValue);

        if(!(token = CmdStr.match(" period [0-9]+")).empty())
        {
            if(!(temp = token.match(re_num)).empty())
            {
                INT _num = atoi(temp.c_str());
                _cmd["cycle_period"] = CtiParseValue( _num );
            }
        }

        if(!(token = CmdStr.match(" count [0-9]+")).empty())
        {
            if(!(temp = token.match(re_num)).empty())
            {
                INT _num = atoi(temp.c_str());
                _cmd["cycle_count"] = CtiParseValue( _num );
            }
        }

        if(!(temp = CmdStr.match("ctrl temp " + str_num)).empty())
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcctrltemp"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" limit " + str_num)).empty())
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xclimittemp"] = CtiParseValue( iValue );
            }

            // We need to look for the fallback %
            if(!(temp = CmdStr.match(" afallback " + str_num)).empty())
            {
                if(!(valStr = temp.match(re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["xclimitfbp"] = CtiParseValue( iValue );
                }
            }
        }

        if(!(temp = CmdStr.match(" maxrate " + str_num)).empty())
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcmaxdperh"] = CtiParseValue( iValue );
            }

            // We need to look for the fallback %
            if(!(temp = CmdStr.match(" bfallback " + str_num)).empty())
            {
                if(!(valStr = temp.match(re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["xcmaxdperhfbp"] = CtiParseValue( iValue );
                }
            }
        }
    }
    else if(CmdStr.contains(" setpoint"))
    {
        _cmd["xcsetpoint"] = CtiParseValue( TRUE );

        if(CmdStr.contains(" hold"))
        {
            _cmd["xcholdtemp"] = CtiParseValue( TRUE );
        }

        if(!(temp = CmdStr.match(" min " + str_num)).empty())
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcmintemp"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" max " + str_num)).empty())
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcmaxtemp"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" tr " + str_num)).empty())
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xctr"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" ta " + str_num)).empty())
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcta"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" tb " + str_num)).empty() && CmdStr.contains(" dsb"))
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xctb"] = CtiParseValue( iValue );
            }

            if(!(temp = CmdStr.match(" dsb -?[0-9]+")).empty())
            {
                if(!(valStr = temp.match("-?[0-9]+")).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["xcdsb"] = CtiParseValue( iValue );
                }
            }
        }

        if(!(temp = CmdStr.match(" tc " + str_num)).empty())
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xctc"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" td " + str_num)).empty() && CmdStr.contains(" dsd"))
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xctd"] = CtiParseValue( iValue );
            }

            if(!(temp = CmdStr.match(" dsd -?[0-9]+")).empty())
            {
                if(!(valStr = temp.match("-?[0-9]+")).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["xcdsd"] = CtiParseValue( iValue );
                }
            }
        }

        if(!(temp = CmdStr.match(" te " + str_num)).empty())
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcte"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" tf " + str_num)).empty() && CmdStr.contains(" dsf"))
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xctf"] = CtiParseValue( iValue );
            }

            if(!(temp = CmdStr.match(" dsf -?[0-9]+")).empty())
            {
                if(!(valStr = temp.match("-?[0-9]+")).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["xcdsf"] = CtiParseValue( iValue );
                }
            }
        }

        _actionItems.push_back("SETPOINT");
    }
}


void  CtiCommandParser::doParsePutConfigExpresscom(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    CHAR *p;
    INT         _num;
    UINT        flag   = 0;
    UINT        offset = 0;
    UINT        iValue = 0;
    DOUBLE      dValue = 0.0;
    CHAR        tbuf[80];

    CtiString   str;
    CtiString   temp;
    CtiString   valStr;
    CtiString   token;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(CmdStr.contains(" sync"))
    {
        _cmd["xcsync"] = CtiParseValue( TRUE );
    }

    if(CmdStr.contains(" setstate"))
    {
        _cmd["xcsetstate"] = CtiParseValue( TRUE );

        if( !isKeyValid("relaymask") )
        {
            _cmd["relaymask"] = CtiParseValue( 0x01 );
        }

        if(CmdStr.contains("run"))
        {
            _cmd["xcrunprog"] = CtiParseValue( TRUE );
        }

        if(CmdStr.contains(" hold"))
        {
            _cmd["xcholdprog"] = CtiParseValue( TRUE );
        }

        if(!(temp = CmdStr.match(" fan (on|off|auto)")).empty())
        {
            if(temp.contains("on"))
            {
                _cmd["xcfanstate"] = CtiParseValue( 0x03 );
            }
            else if(temp.contains("auto"))
            {
                _cmd["xcfanstate"] = CtiParseValue( 0x02 );
            }
            else if(temp.contains("off"))
            {
                _cmd["xcfanstate"] = CtiParseValue( 0x01 );
            }
        }

        if(!(temp = CmdStr.match(" system (auto|off|heat|cool|emheat)")).empty())
        {
            if(temp.contains(" off"))
            {
                _cmd["xcsysstate"] = CtiParseValue( 0x04 );
            }
            else if(temp.contains(" heat"))
            {
                _cmd["xcsysstate"] = CtiParseValue( 0x08 );
            }
            else if(temp.contains(" cool"))
            {
                _cmd["xcsysstate"] = CtiParseValue( 0x0c );
            }
            else if(temp.contains(" emheat"))
            {
                _cmd["xcsysstate"] = CtiParseValue( 0x10 );
            }
            else if(temp.contains(" auto"))
            {
                _cmd["xcsysstate"] = CtiParseValue( 0x80 );     // Only valid for EPRO stats!
            }
        }

        if(!(temp = CmdStr.match(" temp " + str_num)).empty())
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xcsettemp"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" timeout " + str_num)).empty())         // assume minutes input.
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["xctimeout"] = CtiParseValue( iValue );            // In minutes
            }
        }
    }

    if(CmdStr.contains(" timesync"))
    {
        _cmd["xctimesync"] = CtiParseValue( TRUE );

        if(CmdStr.contains(" date"))
        {
            _cmd["xcdatesync"] = CtiParseValue( TRUE );
        }


        if(!(temp = CmdStr.match(" [0-9]?[0-9]:")).empty())
        {
            int hh = atoi(temp.c_str());
            _cmd["xctimesync_hour"] = CtiParseValue( hh );

            if(!(temp = CmdStr.match(":[0-9][0-9]")).empty())
            {
                int mm = atoi(temp.c_str() + 1);
                _cmd["xctimesync_minute"] = CtiParseValue( mm );
            }
        }

    }

    if(!(token = CmdStr.match(" raw( (0x)?[0-9a-f]+)+")).empty())
    {
        if(!(str = token.match("( (0x)?[0-9a-f]+)+")).empty())
        {
            _cmd["xcrawconfig"] = CtiParseValue( str );
        }
    }

    if(!(token = CmdStr.match(" data( (0x)?[0-9a-f]+)+")).empty())
    {
        token.replace(" data", "");
        if(!(str = token.match("( (0x)?[0-9a-f][0-9a-f])+")).empty())
        {
            _cmd["xcdata"] = CtiParseValue( str );
        }
    }

    if(!(token = CmdStr.match("main(tenance)?( (0x)?[0-9a-f]+)+")).empty())
    {
        // Translates to a maintenance function
        if(!(str = token.match("( (0x)?[0-9a-f]+)+")).empty())
        {
            _cmd["xcrawmaint"] = CtiParseValue( str );
        }
    }

    if(!(token = CmdStr.match("((assign)|(address))")).empty())
    {
        {
            _cmd["xcaddress"] = TRUE;

            if(!(valStr = CmdStr.match(" s[ =]*(0x)?[0-9a-f]+")).empty())
            {
                _num = strtol(valStr.match(re_anynum).c_str(), &p, 0);

                _cmd["xca_spid"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG SPID = %d", _num);
                _actionItems.push_back(tbuf);
            }
            if(!(valStr = CmdStr.match(" g[ =]*[0-9]+")).empty())
            {
                _num = strtol(valStr.match(re_num).c_str(), &p, 10);
                _cmd["xca_geo"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG GEO = %d", _num);
                _actionItems.push_back(tbuf);
            }
            if(!(valStr = CmdStr.match(" b[ =]*[0-9]+")).empty())
            {
                _num = strtol(valStr.match(re_num).c_str(), &p, 10);
                _cmd["xca_sub"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG SUBSTATION = %d", _num);
                _actionItems.push_back(tbuf);
            }
            if(!(valStr = CmdStr.match(" f[ =]*[0-9]+")).empty())
            {
                _num = strtol(valStr.match(re_num).c_str(), &p, 10);
                _cmd["xca_feeder"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG FEEDER = %d", _num);
                _actionItems.push_back(tbuf);
            }
            if(!(valStr = CmdStr.match(" z[ =]*[0-9]+")).empty())
            {
                _num = strtol(valStr.match(re_num).c_str(), &p, 10);
                _cmd["xca_zip"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG ZIP = %d", _num);
                _actionItems.push_back(tbuf);
            }
            if(!(valStr = CmdStr.match(" u[ =]*[0-9]+")).empty())
            {
                _num = strtol(valStr.match(re_num).c_str(), &p, 10);
                _cmd["xca_uda"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG UDA = %d", _num);
                _actionItems.push_back(tbuf);
            }

            CtiString programtemp;
            CtiString splintertemp;

            if(!(valStr = CmdStr.match(" p[ =]*[0-9]+( *, *[0-9]+)*")).empty())
            {
                valStr.replace(boost::regex(" p[ =]*"),"");
                _cmd["xca_program"] = CtiParseValue( valStr );
                programtemp = valStr;
            }
            if(!(valStr = CmdStr.match(" r[ =]*[0-9]+( *, *[0-9]+)*")).empty())
            {
                valStr.replace(boost::regex(" r[ =]*"),"");
                _cmd["xca_splinter"] = CtiParseValue( valStr );

                splintertemp = valStr;
            }

            if(!(token = CmdStr.match("(relay|load) [0-9]+( *, *[0-9]+)*")).empty())
            {
                INT i;
                INT mask = 0;
                CtiTokenizer ptok(programtemp);
                CtiTokenizer rtok(splintertemp);
                CtiString tempstr;
                CtiString ptemp;
                CtiString rtemp;

                for(i = 0; i < 15; i++)
                {
                    CtiString numstr = CtiNumStr(i+1).toString().c_str();
                    if(!(temp = token.match(numstr.c_str())).empty())
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

    if(CmdStr.contains(" serv"))
    {
        if(CmdStr.contains("temp") && !(token = CmdStr.match("serv(ice)? (in|out|enable|disable)")).empty())
        {
            CHAR tbuf[80];
            INT offtime = 0;
            INT cancel = 0;  // default to cancelling a previous t-service message.
            INT bitP = 0;    // default to using cold load
            INT bitL = 0;    // default to using the LEDs

            if(token.contains(" out") || token.contains(" disable"))
            {
                cancel = 1;
                _snprintf(tbuf, sizeof(tbuf), "SERVICE DISABLE TEMPORARY");
            }
            else
            {
                _snprintf(tbuf, sizeof(tbuf), "SERVICE ENABLE TEMPORARY");
            }

            if(CmdStr.contains(" noclp"))
            {
                bitP = 1;
            }
            if(CmdStr.contains(" noled"))
            {
                bitL = 1;
            }

            if(!(token = CmdStr.match(" offhours " + str_num)).empty())
            {
                str = token.match(re_num);
                offtime = atoi(str.c_str());
            }

            _cmd["xctservicebitp"] = CtiParseValue( bitP );
            _cmd["xctservicebitl"] = CtiParseValue( bitL );
            _cmd["xctservicetime"] = CtiParseValue( offtime );
            _cmd["xctservicecancel"] = CtiParseValue( cancel );
            _actionItems.push_back(tbuf);
        }
        else if(!(token = CmdStr.match("serv(ice)? (in|out|enable|disable)( (relay|load) [0-9]+)?")).empty())
        {
            CHAR tbuf[80];
            INT flag = 0;

            if(token.contains(" in") || token.contains(" enable"))
            {
                flag |= 0x80;
                _snprintf(tbuf, sizeof(tbuf), "SERVICE ENABLE");
            }
            else if(token.contains(" out") || token.contains(" disable"))
            {
                flag |= 0x00;
                _snprintf(tbuf, sizeof(tbuf), "SERVICE DISABLE");
            }

            if(!(str = token.match("(relay|load) " + str_num)).empty())
            {
                CtiTokenizer   tok2(str);
                tok2();  // hop over relay | load.
                str = tok2();
                INT load = atoi(str.c_str());
                flag |= (load & 0x0f);
            }

            _cmd["xcpservice"] = CtiParseValue( flag );
            _actionItems.push_back(tbuf);
        }
    }
    else if(!(token = CmdStr.match(" ovuv[ =]+((ena(ble)?)|(dis(able)?))")).empty())
    {
        int   op = 0;
        CHAR  op_name[20];

        if(token.contains("ena"))
        {
            op = 1;
            _snprintf(op_name, sizeof(op_name), "ENABLE");
        }
        else if(token.contains("dis"))
        {
            op = 0;
            _snprintf(op_name, sizeof(op_name), "DISABLE");
        }

        _cmd["ovuv"] = CtiParseValue( op );

        _snprintf(tbuf, sizeof(tbuf), "OVUV %s", op_name);
        _actionItems.push_back(tbuf);
    }
    else if(CmdStr.contains("schedule"))
    {
        _cmd["xcschedule"] = TRUE;
        doParsePutConfigThermostatSchedule(CmdStr);
    }
    else if(CmdStr.contains(" cbc"))
    {
        doParsePutConfigCBC(CmdStr);
    }
}

void  CtiCommandParser::doParsePutStatusExpresscom(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    INT         _num;
    CHAR        tbuf[80];
    UINT        flag   = 0;
    UINT        offset = 0;
    UINT        iValue = 0;
    DOUBLE      dValue = 0.0;

    CtiString   str;
    CtiString   temp;
    CtiString   valStr;
    CtiString   token;

    CtiTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!(token = CmdStr.match(" prop[a-z]*[ =]+((disp[a-z]*)|(inc[a-z]*)|(term[a-z]*)|(rssi)|(ping))")).empty())
    {
        int   op = -1;
        CHAR  op_name[20];

        if(!(token.match("disp(lay)?")).empty())                   // Turn on light & bump counter.
        {
            op = 0x002;
            _snprintf(op_name, sizeof(op_name), "DISPLAY");
        }
        else if(!(token.match("inc(rement)?")).empty())            // bump counter.
        {
            op = 0x001;
            _snprintf(op_name, sizeof(op_name), "INCREMENT");
        }
        else if(!(token.match("term(inate)?")).empty())            // Turn off light.
        {
            op = 0x000;
            _snprintf(op_name, sizeof(op_name), "TERMINATE");
        }
        else if(token.contains("rssi"))
        {
            op = 0x003;
            _snprintf(op_name, sizeof(op_name), "RSSI");
        }
        else if(token.contains("ping"))
        {
            op = 0x080;
            _snprintf(op_name, sizeof(op_name), "PING");
        }

        if(op != -1)
        {
            CHAR  tbuf[80];
            _cmd["xcproptest"] = CtiParseValue( op );

            _snprintf(tbuf, sizeof(tbuf), "PROP TEST: %01x = %s", op, op_name);
            _actionItems.push_back(tbuf);
        }
    }
    else if(!(token = CmdStr.match(" ovuv[ =]+((ena(ble)?)|(dis(able)?))")).empty())
    {
        int   op = 0;
        CHAR  op_name[20];

        if(token.contains("ena"))
        {
            op = 1;
            _snprintf(op_name, sizeof(op_name), "ENABLE");
        }
        else if(token.contains("dis"))
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
    CtiString CmdStr(_CmdStr);
    CtiString   str;
    CtiString   temp;
    CtiString   valStr;
    CtiString   token;

    CtiTokenizer   tok(CmdStr);

    {
        INT key;
        bool inc = true;

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

void CtiCommandParser::doParsePutConfigThermostatScheduleDOW(CtiTokenizer &tok, INT &key)
{
    CtiString token;
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
                if(!token.match(re_num).empty())
                {
                    hh = atoi(token.c_str());
                }
                else if(token.contains("hh"))
                {
                    hh = 254;                           // This is the Energy Pro period cancel indicator.
                }
                break;
            }
        case 1:
            {
                // This is the mm section of the time
                if(!token.match("[0-9]+").empty())
                    mm = atoi(token.c_str());
                break;
            }
        case 2:
            {
                // This is the heat temperature section
                if(!token.match("[0-9]+").empty())
                    heat = atoi(token.c_str());
                break;
            }
        case 3:
            {
                // This is the cool temperature section
                if(!token.match("[0-9]+").empty())
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

            CtiString hhstr("xctodshh_" + CtiNumStr(per));
            CtiString mmstr("xctodsmm_" + CtiNumStr(per));
            CtiString heatstr("xctodsheat_" + CtiNumStr(per));
            CtiString coolstr("xctodscool_" + CtiNumStr(per));

            _cmd[hhstr]   = hh;
            _cmd[mmstr]   = mm;
            _cmd[heatstr] = heat;
            _cmd[coolstr] = cool;

#if 0
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << hhstr << " " << (int)hh << endl;
                dout << mmstr << " " << (int)mm << endl;
                dout << heatstr << " " << (int)heat << endl;
                dout << coolstr << " " << (int)cool << endl;
            }
#endif

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
    CtiString CmdStr(_CmdStr);
    INT         _num;
    UINT        flag   = 0;
    UINT        offset = 0;
    INT         iValue = 0;
    DOUBLE      dValue = 0.0;

    CHAR        *p;
    CHAR        tbuf[80];
    CHAR        tbuf2[80];

    CtiString   temp;
    CtiString   valStr;

    CtiTokenizer   tok(CmdStr);

    temp = tok(); // Get the first one into the hopper....


    _cmd["sa_f1bit"] = 0;


    // Needed for serial commands.
    if(CmdStr.contains(" utility"))
    {
        if(!(temp = CmdStr.match(" utility " + str_num)).empty())
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["sa_utility"] = CtiParseValue( iValue );
            }
        }
    }

    if(CmdStr.contains(" protocol_priority"))
    {
        if(!(temp = CmdStr.match(" protocol_priority [0-3]")).empty())
        {
            if(!(valStr = temp.match("[0-3]")).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["sa_priority"] = CtiParseValue( iValue );
            }
        }
    }

    //  042005 CGP:  NOT TO BE USED FOR SA205.  Used for LED FLASHing on SA305
    if(CmdStr.contains(" ledrepeats"))
    {
        if(!(temp = CmdStr.match(" ledrepeats " + str_num)).empty())
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["sa_reps"] = CtiParseValue( iValue - 1 );
            }
        }
    }

    if(CmdStr.contains(" function"))
    {
        if(!(temp = CmdStr.match(" function [01][01][01][01]")).empty())
        {
            if(!(valStr = temp.match("[01][01][01][01]")).empty())
            {
                iValue = binaryStringToInt(valStr.c_str(), valStr.length());
                _cmd["sa_function"] = CtiParseValue(iValue);
            }
        }
        else if(!(temp = CmdStr.match(" function " + str_num)).empty())
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["sa_function"] = CtiParseValue(iValue);
            }
        }
    }

    bool abrupt = false;

    // DEFAULT CHOICES BASED UPON COMMANDS.  NOTE that abrupt is set true if restore!
    if( CmdStr.contains(" restore") || CmdStr.contains(" shed"))
    {
        abrupt = true;
        _cmd["sa_dlc_mode"] = TRUE;
        _cmd["sa_f0bit"] = 0;
    }
    else if(CmdStr.contains(" cycle") || CmdStr.contains(" terminate"))
    {
        _cmd["sa_dlc_mode"] = FALSE;
        _cmd["sa_f0bit"] = 1;
    }

    // DEFAULT CHOICES MAY BE MODIFIED BY OPTIONAL MODIFIERS.
    if(CmdStr.contains(" dlc"))
    {
        _cmd["sa_dlc_mode"] = TRUE;
        _cmd["sa_f0bit"] = 0;
    }
    else if(CmdStr.contains(" di"))
    {
        _cmd["sa_dlc_mode"] = FALSE;
        _cmd["sa_f0bit"] = 1;
    }

    if(CmdStr.contains(" abrupt"))
    {
        abrupt = true;
    }
    else if(CmdStr.contains(" graceful"))
    {
        abrupt = false;
    }

    if(CmdStr.contains(" restore") || CmdStr.contains(" terminate") )    // This parse must be done following the check for dlc.
    {
        _cmd["sa_restore"] = TRUE;
        _cmd["sa_reps"] = abrupt ? 0 : 1;
        _cmd["sa_strategy"] = CtiParseValue(61);        // This is the defined strategy.
    }
    else if(!(temp = CmdStr.match(" strategy [01][01][01][01][01][01]")).empty())
    {
        // This is a binary...
        if(!(valStr = temp.match("[01][01][01][01][01][01]")).empty())
        {
            iValue = binaryStringToInt(valStr.c_str(), valStr.length());
            _cmd["sa_strategy"] = CtiParseValue(iValue);    // If not explicitly called out, this is inferred from the period and cycle percent.
        }
    }
}

void CtiCommandParser::doParsePutConfigSA(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    INT         _num;
    INT         iValue = 0;

    CHAR tbuf[80];
    CtiString   valStr;
    CtiString   temp, token, strnum;

    CtiTokenizer   tok(CmdStr);

    temp = tok(); // Get the first one into the hopper....


    if(CmdStr.contains(" utility"))
    {
        if(!(temp = CmdStr.match(" utility " + str_num)).empty())
        {
            if(!(valStr = temp.match(re_num)).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["sa_utility"] = CtiParseValue( iValue );
            }
        }
    }

    if(CmdStr.contains(" protocol_priority"))
    {
        if(!(temp = CmdStr.match(" protocol_priority [0-3]+")).empty())
        {
            if(!(valStr = temp.match("[0-3]+")).empty())
            {
                iValue = atoi(valStr.c_str());
                _cmd["sa_priority"] = CtiParseValue( iValue );
            }
        }
    }

    if(CmdStr.contains(" tamper"))
    {
        if(!(token = CmdStr.match("tamper[ a-z_]*" \
                                  "( *f[12][ =]*" + str_num + ")" \
                                  "( *f[12][ =]*" + str_num + ")?")).empty())
        {
            // dout << token << endl;
            _cmd["sa_tamper"] = TRUE;

            if(!(strnum = token.match("f1[ =]*" + str_num)).empty())
            {
                strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                _num = atoi(strnum.match("[0-9]+").c_str());
                _cmd["tamperdetect_f1"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG TAMPER COUNT R1 = %d", _num);
                _actionItems.push_back(tbuf);
            }
            if(!(strnum = token.match("f2[ =]*" + str_num)).empty())
            {
                strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                _num = atoi(strnum.match("[0-9]+").c_str());
                _cmd["tamperdetect_f2"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG TAMPER COUNT R2 = %d", _num);
                _actionItems.push_back(tbuf);
            }
        }
    }

    if(!(temp = CmdStr.match(" override " + str_num)).empty())
    {
        _cmd["sa_f1bit"] = 0;
        _cmd["sa_f0bit"] = 0;

        if(!(valStr = temp.match(re_num)).empty())
        {
            iValue = atoi(valStr.c_str());
            _cmd["sa_override"] = CtiParseValue(iValue);    // Hours switch is off.
            _cmd["sa_strategy"] = CtiParseValue(62);        // This is the defined strategy.
        }
    }
    else if(!(temp = CmdStr.match(" setled " + str_num)).empty())
    {
        _cmd["sa_f1bit"] = 0;
        _cmd["sa_f0bit"] = 1;

        if(!(valStr = temp.match(re_num)).empty())
        {
            iValue = atoi(valStr.c_str());
            _cmd["sa_setled"] = CtiParseValue(iValue);
            _cmd["sa_strategy"] = CtiParseValue(63);
        }
    }
    else if(!(temp = CmdStr.match(" flashled " + str_num)).empty())
    {
        _cmd["sa_f1bit"] = 0;
        _cmd["sa_f0bit"] = 1;

        if(!(valStr = temp.match(re_num)).empty())
        {
            iValue = atoi(valStr.c_str());
            _cmd["sa_flashled"] = CtiParseValue(iValue);
            _cmd["sa_strategy"] = CtiParseValue(63);
        }
    }
    else if(CmdStr.contains(" flashrate"))
    {
        _cmd["sa_f1bit"] = 0;
        _cmd["sa_f0bit"] = 0;

        _cmd["sa_flashrate"] = CtiParseValue(TRUE);
        _cmd["sa_strategy"] = CtiParseValue(63);
    }
    else if(!(temp = CmdStr.match(" setpriority [0-3]")).empty())
    {
        _cmd["sa_f1bit"] = 0;
        _cmd["sa_f0bit"] = 0;

        if(!(valStr = temp.match("[0-3]")).empty())
        {
            iValue = atoi(valStr.c_str());
            _cmd["sa_setpriority"] = CtiParseValue(iValue);
            _cmd["sa_strategy"] = CtiParseValue(63);
        }
    }
    else
    {
        _cmd["sa_f1bit"] = 1;

        if(CmdStr.contains(" assign"))
        {
            char *p;
            CHAR        tbuf[80];

            _cmd["sa_f0bit"] = 1;
            _cmd["sa_assign"] = TRUE;

            if(!isKeyValid("sa_utility") &&  !(valStr = CmdStr.match(" u[ =]*" + str_num)).empty())  // Must be included (either way is ok)
            {
                _num = strtol(valStr.match(re_num).c_str(), &p, 0);
                _cmd["sa_utility"] = CtiParseValue( _num );
            }
            if(!(valStr = CmdStr.match(" g[ =]*" + str_num)).empty())
            {
                _num = strtol(valStr.match(re_num).c_str(), &p, 0);
                _cmd["sa_group"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG GROUP = %d", _num);
                _actionItems.push_back(tbuf);
            }
            if(!(valStr = CmdStr.match(" d[ =]*" + str_num)).empty())
            {
                _num = strtol(valStr.match(re_num).c_str(), &p, 0);
                _cmd["sa_division"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG DIVISION = %d", _num);
                _actionItems.push_back(tbuf);
            }
            if(!(valStr = CmdStr.match(" s[ =]*" + str_num)).empty())
            {
                _num = strtol(valStr.match(re_num).c_str(), &p, 0);
                _cmd["sa_substation"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG SUBSTATION = %d", _num);
                _actionItems.push_back(tbuf);
            }
            if(!(valStr = CmdStr.match(" [pr][ =]*" + str_num)).empty())
            {
                _num = strtol(valStr.match(re_num).c_str(), &p, 0);
                _cmd["sa_package"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG RATE PACKAGE = %d", _num);
                _actionItems.push_back(tbuf);
            }
            else
            {
                if(!(valStr = CmdStr.match(" f[ =]*" + str_num)).empty())
                {
                    _num = strtol(valStr.match(re_num).c_str(), &p, 0);
                    _cmd["sa_family"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG FAMILY = %d", _num);
                    _actionItems.push_back(tbuf);
                }
                if(!(valStr = CmdStr.match(" m[ =]*" + str_num)).empty())
                {
                    _num = strtol(valStr.match(re_num).c_str(), &p, 0);
                    _cmd["sa_member"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG MEMBER = %d", _num);
                    _actionItems.push_back(tbuf);
                }
            }
        }
        else
        {
            _cmd["sa_f0bit"] = 0; // This is a short stuff config.
            if(CmdStr.contains(" coldload"))
            {
                _cmd["sa_coldload"] = TRUE;
                // Assume seconds is the input here!
                if(!(temp = CmdStr.match(" f1[ =]+" + str_num)).empty())
                {
                    temp.replace(" f1[ =]+", "");
                    if(!(valStr = temp.match(re_num)).empty())
                    {
                        iValue = atoi(valStr.c_str());
                        _cmd["sa_clpf1"] = CtiParseValue(iValue);
                    }
                }
                if(!(temp = CmdStr.match(" f2[ =]+" + str_num)).empty())
                {
                    temp.replace(" f2[ =]+", "");
                    if(!(valStr = temp.match(re_num)).empty())
                    {
                        iValue = atoi(valStr.c_str());
                        _cmd["sa_clpf2"] = CtiParseValue(iValue);
                    }
                }
                if(!(temp = CmdStr.match(" f3[ =]+" + str_num)).empty())
                {
                    temp.replace(" f3[ =]+", "");
                    if(!(valStr = temp.match(re_num)).empty())
                    {
                        iValue = atoi(valStr.c_str());
                        _cmd["sa_clpf3"] = CtiParseValue(iValue);
                    }
                }
                if(!(temp = CmdStr.match(" f4[ =]+" + str_num)).empty())
                {
                    temp.replace(" f4[ =]+", "");
                    if(!(valStr = temp.match(re_num)).empty())
                    {
                        iValue = atoi(valStr.c_str());
                        _cmd["sa_clpf4"] = CtiParseValue(iValue);
                    }
                }
                if(!(temp = CmdStr.match(" all[ =]+" + str_num)).empty())
                {
                    if(!(valStr = temp.match(re_num)).empty())
                    {
                        iValue = atoi(valStr.c_str());
                        _cmd["sa_clpall"] = CtiParseValue(iValue);
                    }
                }
            }
            else if(!(temp = CmdStr.match(" lorm0[ =]+" + str_num)).empty())
            {
                temp.replace(" lorm0[ =]+", "");
                if(!(valStr = temp.match(re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["sa_lorm0"] = CtiParseValue(iValue);
                }
            }
            else if(!(temp = CmdStr.match(" horm0[ =]+" + str_num)).empty())
            {
                temp.replace(" horm0[ =]+", "");
                if(!(valStr = temp.match(re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["sa_horm0"] = CtiParseValue(iValue);
                }
            }
            else if(!(temp = CmdStr.match(" lorm1[ =]+" + str_num)).empty())
            {
                temp.replace(" lorm1[ =]+", "");
                if(!(valStr = temp.match(re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["sa_lorm1"] = CtiParseValue(iValue);
                }
            }
            else if(!(temp = CmdStr.match(" horm1[ =]+" + str_num)).empty())
            {
                temp.replace(" horm1[ =]+", "");
                if(!(valStr = temp.match(re_num)).empty())
                {
                    iValue = atoi(valStr.c_str());
                    _cmd["sa_horm1"] = CtiParseValue(iValue);
                }
            }
            else if(!(temp = CmdStr.match(" use relay *map 0")).empty())
            {
                _cmd["sa_userelaymap0"] = TRUE;
            }
            else if(!(temp = CmdStr.match(" use relay *map 1")).empty())
            {
                _cmd["sa_userelaymap1"] = TRUE;
            }
            else if(CmdStr.contains(" clear lc"))
            {
                _cmd["sa_clearlc"] = TRUE;
            }
            else if(CmdStr.contains(" clear hc"))
            {
                _cmd["sa_clearhc"] = TRUE;
            }
            else if(CmdStr.contains(" clear pcd"))
            {
                _cmd["sa_clearpcd"] = TRUE;
            }
            else if(CmdStr.contains(" freeze pcd"))
            {
                _cmd["sa_freezepcd"] = TRUE;
            }
            else if(CmdStr.contains(" primary freq"))
            {
                _cmd["sa_frequency"] = 0;
            }
            else if(CmdStr.contains(" secondary freq"))
            {
                _cmd["sa_frequency"] = 1;
            }
            else if(!(temp = CmdStr.match(" rawdata " + str_num + " " + str_num)).empty())
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

CtiCommandParser& CtiCommandParser::parseAsString(const string str)
{
#if 0
    _cmdString = CtiString("built from string");
    _actionItems.clear();
    _cmd.clear();

    CtiString key;
    CtiString asStr;
    INT iValue;
    DOUBLE dValue;

    CtiTokenizer   tok(str.c_str());

    while( !(key = tok("= \t\n\0")).empty() )
    {
        asStr = tok(", \t\n\0");
        iValue = atoi( CtiString(tok(", \t\n\0")).c_str() );
        dValue = atof( CtiString(tok(": \t\n\0")).c_str());


        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << key << " " << asStr << " " << iValue << " " << dValue << endl;
        }
    }

#endif


    return *this;
}


void  CtiCommandParser::doParsePutConfigCBC(const string &_CmdStr)
{
    CtiString CmdStr(_CmdStr);
    CHAR *p;
    INT         _num;
    UINT        flag   = 0;
    UINT        offset = 0;
    UINT        iValue = 0;
    DOUBLE      dValue = 0.0;
    CHAR        tbuf[80];

    CtiString   str;
    CtiString   temp;
    CtiString   valStr;
    CtiString   token;
    CtiString   xcraw("0x60 ");

    if(CmdStr.contains(" emergency"))
    {
        int ov = 0, uv = 0, timer = 0;

        if(!(token = CmdStr.match(" uv[ =]+" + str_num)).empty())
        {
            str = token.match(re_num);
            uv = atoi(str.c_str());
            uv = limitValue(uv, 105, 122);
            _cmd["cbc_emergency_uv_close_voltage"] = CtiParseValue( uv );
            valStr = CtiString("Emergency UV Close Voltage ") + CtiNumStr(uv);
            _actionItems.push_back(valStr);
        }

        if(!(token = CmdStr.match(" ov[ =]+" + str_num)).empty())
        {
            str = token.match(re_num);
            ov = atoi(str.c_str());
            ov = limitValue(ov, 118, 135);
            _cmd["cbc_emergency_ov_trip_voltage"] = CtiParseValue( ov );
            valStr = CtiString("Emergency OV Trip Voltage ") + CtiNumStr(ov);
            _actionItems.push_back(valStr);
        }

        if(!(token = CmdStr.match(" timer[ =]+" + str_num)).empty())
        {
            str = token.match(re_num);
            timer = atoi(str.c_str());
            timer = limitValue(timer, 1, 255);
            _cmd["cbc_emergency_ov_trip_voltage"] = CtiParseValue( timer );
            valStr = CtiString("Emergency OV Trip Voltage ") + CtiNumStr(timer);
            _actionItems.push_back(valStr);
        }

        if(ov != 0 && uv != 0 && timer != 0)
        {
            xcraw += "0x24 0x" + CtiNumStr(ov).hex() + " 0x" + CtiNumStr(uv).hex() + " 0x" + CtiNumStr(timer).hex();
            _cmd["xcrawconfig"] = xcraw;
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    else if(!(token = CmdStr.match(" ovuv control trigger time[ =]+" + str_num)).empty())
    {
        int random = 0;
        str = token.match(re_num);
        iValue = atoi(str.c_str());
        iValue = limitValue(iValue, 0, 0xffff);
        _cmd["cbc_ovuv_control_trigger_time"] = CtiParseValue( iValue );

        if(!(token = CmdStr.match(" random[ =]+" + str_num)).empty())
        {
            str = token.match(re_num);
            random = atoi(str.c_str());
            random = limitValue(random, 0, 0xff);
        }

        valStr = CtiString("OVUV Control Trigger Time ") + CtiNumStr(iValue) + " random " + CtiNumStr(random);
        _actionItems.push_back(valStr);

        xcraw += "0x0b 0x" + CtiNumStr(HIBYTE(iValue)).hex() + " 0x" + CtiNumStr(LOBYTE(iValue)).hex() + " 0x" + CtiNumStr(random).hex();
        _cmd["xcrawconfig"] = xcraw;
    }
    else if(!(token = CmdStr.match(" daily auto control limit[ =]+" + str_num)).empty())
    {
        str = token.match(re_num);
        iValue = atoi(str.c_str());
        iValue = limitValue(iValue, 1, 30);
        _cmd["cbc_daily_control_limit"] = CtiParseValue( iValue );
        valStr = CtiString("OV Trip Voltage ") + CtiNumStr(iValue);
        _actionItems.push_back(valStr);

        xcraw += "0x10 0x" + CtiNumStr(iValue).hex();
        _cmd["xcrawconfig"] = xcraw;
    }
    else if(CmdStr.contains(" comms lost"))
    {
        BYTE action = 0;
        USHORT commlosstime = 0;
        BYTE uvclval = 0;
        BYTE ovclval = 0;

        if( !CmdStr.contains(" disable") ) // This disables all comms lost behavior!  action = 0!
        {
            if(CmdStr.contains("timed"))
            {
                action |= 0x01;
            }
            if(CmdStr.contains("ovuv"))
            {
                action |= 0x02;
            }
            if(CmdStr.contains("temperature"))
            {
                action |= 0x04;
            }
            if(CmdStr.contains("analogin1"))
            {
                action |= 0x08;
            }
            if(CmdStr.contains("analogin2"))
            {
                action |= 0x10;
            }
            if(CmdStr.contains("analogin3"))
            {
                action |= 0x20;
            }
            if(CmdStr.contains("digitalin1"))
            {
                action |= 0x40;
            }
            if(CmdStr.contains("digitalin2"))
            {
                action |= 0x80;
            }
        }

        xcraw += "0x23 0x" + CtiNumStr(action).hex();

        if(!(token = CmdStr.match(" time[ =]+" + str_num)).empty())    // Ignored if the action == 0.
        {
            str = token.match(re_num);
            commlosstime = atoi(str.c_str());
            commlosstime = limitValue(commlosstime, 0, 0xffff);
            _cmd["cbc_comms_lost_time"] = CtiParseValue( commlosstime );
        }
        xcraw += " 0x" + CtiNumStr(HIBYTE(commlosstime)).hex() + " 0x" + CtiNumStr(LOBYTE(commlosstime)).hex();

        if(!(token = CmdStr.match(" (ov|uv)[ =]+" + str_num + ".*(ov|uv)[ =]+" + str_num)).empty())
        {
            if(!(token = CmdStr.match(" uv[ =]+" + str_num)).empty())    // Ignored if the action == 0.
            {
                str = token.match(re_num);
                uvclval = atoi(str.c_str());
                uvclval = limitValue(uvclval, 105, 122);
                _cmd["cbc_comms_lost_uvpt"] = CtiParseValue( uvclval );

                xcraw += " 0x" + CtiNumStr(uvclval).hex();
            }

            if(!(token = CmdStr.match(" ov[ =]+" + str_num)).empty())    // Ignored if the action == 0.
            {
                str = token.match(re_num);
                ovclval = atoi(str.c_str());
                ovclval = limitValue(ovclval, 118, 135);
                _cmd["cbc_comms_lost_ovpt"] = CtiParseValue( ovclval );

                xcraw += " 0x" + CtiNumStr(ovclval).hex();
            }
        }

        _cmd["xcrawconfig"] = xcraw;
    }
    else if(CmdStr.contains(" temperature"))
    {
        if(CmdStr.contains(" control enable"))
        {
            iValue = TRUE;
        }
        else if(CmdStr.contains(" control disable"))
        {
            iValue = FALSE;
        }
        _cmd["cbc_tempcontrol_enable"] = CtiParseValue( iValue );
        valStr = CtiString("Temperature control ") + (iValue ? CtiString("enable") : CtiString("disable"));
        _actionItems.push_back(valStr);

        _cmd["xcrawconfig"] = xcraw;
    }
    else if(CmdStr.contains(" time control"))
    {
        if(CmdStr.contains(" control enable"))
        {
            iValue = TRUE;
        }
        else if(CmdStr.contains(" control disable"))
        {
            iValue = FALSE;
        }
        _cmd["cbc_timecontrol_enable"] = iValue;
        valStr = CtiString("Time control ") + (iValue ? CtiString("enable") : CtiString("disable"));
        _actionItems.push_back(valStr);


        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** ACH Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        xcraw += "0x22 0x" + CtiNumStr(iValue).hex();
        _cmd["xcrawconfig"] = xcraw;
    }
    else
    {
        if(!(token = CmdStr.match(" uv[ =]+" + str_num)).empty())
        {
            str = token.match(re_num);
            iValue = atoi(str.c_str());
            iValue = limitValue(iValue, 105, 122);
            _cmd["cbc_uv_close_voltage"] = CtiParseValue( iValue );
            valStr = CtiString("UV Close Voltage ") + CtiNumStr(iValue);
            _actionItems.push_back(valStr);

            xcraw += "0x09 0x" + CtiNumStr(iValue).hex();
            _cmd["xcrawconfig"] = xcraw;
        }
        else if(!(token = CmdStr.match(" ov[ =]+" + str_num)).empty())
        {
            str = token.match(re_num);
            iValue = atoi(str.c_str());
            iValue = limitValue(iValue, 118, 135);
            _cmd["cbc_ov_trip_voltage"] = CtiParseValue( iValue );
            valStr = CtiString("OV Trip Voltage ") + CtiNumStr(iValue);
            _actionItems.push_back(valStr);

            xcraw += "0x0a 0x" + CtiNumStr(iValue).hex();
            _cmd["xcrawconfig"] = xcraw;
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << endl << " XCRAW: " << xcraw << endl << endl;
    }
    return;
}





#pragma warning( disable : 4786)

#include <windows.h>
#include <stdlib.h>
#include <iostream>
using namespace std;

#include <rw\rwtime.h>
#include <rw\cstring.h>
#include <rw\re.h>

#include <limits.h>
#include "yukon.h"
#include "cmdparse.h"
#include "cparms.h"
#include "devicetypes.h"
#include "logger.h"
#include "numstr.h"
#include "pointdefs.h"
#include "utility.h"


static RWCRExpr   re_num("[0-9]+");
static RWCRExpr   re_hexnum("0x[0-9a-f]+");
static RWCRExpr   re_anynum("0x[0-9a-f]+|[0-9]+");




CtiCommandParser::CtiCommandParser(const RWCString str) :
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


void  CtiCommandParser::doParse(RWCString Cmd)
{
    // This may look like replication of the assignment, but allows re-use of the object for
    // other parsing....
    Cmd.toLower();
    Cmd.replace("\t", " ", RWCString::all);

    _cmdString = Cmd;       // OK already, now we are in business.

    CHAR            *p;
    RWCString       CmdStr = Cmd;
    RWCString       token;
    RWCString       cmdstr;
    RWCString       strnum;
    INT             _num = 0;

    if(!CmdStr.match("^pil ").isNull() || !CmdStr.match("^command ").isNull())
    {
        CmdStr = CmdStr.replace("^pil ", "");
        CmdStr = CmdStr.replace("^command ", "");
    }

    RWCTokenizer    tok(CmdStr);


    if(CmdStr.contains(" serial"))
    {
        RWCRExpr regexp("serial[= ]+(([0-9]+)|(0x[0-9a-f]+))");

        if(!(token = CmdStr.match(regexp)).isNull())
        {
            if(!(strnum = token.match(re_hexnum)).isNull())
            {
                // dout << __LINE__ << " " << strnum << endl;
                _num = strtol(strnum.data(), &p, 16);
            }
            else if(!(strnum = token.match(re_num)).isNull())
            {
                // dout << __LINE__ << " " << strnum << endl;
                _num = strtol(strnum.data(), &p, 10);
            }
            _cmd["serial"] = CtiParseValue( _num );
            CmdStr.replace(regexp, "");
        }
    }

    if(CmdStr.contains(" select"))
    {
        RWCRExpr re_name("select[ ]+name[ ]+((\"|')[^\"']+(\"|'))");
        RWCRExpr re_id("select +(device)?id +[0-9]+");
        RWCRExpr re_grp("select +group +((\"|')[^\"']+(\"|'))");
        RWCRExpr re_altg("select +altgroup +((\"|')[^\"']+(\"|'))");
        RWCRExpr re_rtename("select +route *name +((\"|')[^\"']+(\"|'))");
        RWCRExpr re_rteid("select +route *id +[0-9]+");
        RWCRExpr re_ptname("select +point *name +((\"|')[^\"']+(\"|'))");
        RWCRExpr re_ptid("select +point *id +[0-9]+");

        if(!(token = CmdStr.match(re_name)).isNull())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("name +", &nstop);

            nstop += nstart;

            if(!(token = token.match("(\"|')[^\"']+(\"|')", nstop)).isNull())   // get the value
            {
                token = token((size_t)1, (size_t)token.length() - 2);
                _cmd["device"] = CtiParseValue( token, -1 );
            }

            CmdStr.replace(re_name, "");
        }
        else if(!(token = CmdStr.match(re_id)).isNull())
        {
            RWCTokenizer ntok(token);
            ntok();  // pull the select keyword
            ntok();  // pull the id keyword
            if(!(token = ntok()).isNull())   // get the value
            {
                _cmd["device"] = CtiParseValue( atoi(token.data()) );
            }
            CmdStr.replace(re_id, "");
        }
        else if(!(token = CmdStr.match(re_grp)).isNull())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("group +", &nstop);

            nstop += nstart;

            if(!(token = token.match("(\"|')[^\"']+(\"|')", nstop)).isNull())   // get the value
            {
                token = token((size_t)1, (size_t)token.length() - 2);
                _cmd["group"] = CtiParseValue( token, -1 );
            }
            CmdStr.replace(re_grp, "");
        }
        else if(!(token = CmdStr.match(re_altg)).isNull())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("altgroup +", &nstop);

            nstop += nstart;

            if(!(token = token.match("(\"|')[^\"']+(\"|')", nstop)).isNull())   // get the value
            {
                token = token((size_t)1, (size_t)token.length() - 2);
                _cmd["altgroup"] = CtiParseValue( token, -1 );
            }
            CmdStr.replace(re_altg, "");
        }
        else if(!(token = CmdStr.match(re_rtename)).isNull())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("name +", &nstop);

            nstop += nstart;

            if(!(token = token.match("(\"|')[^\"']+(\"|')", nstop)).isNull())   // get the value
            {
                token = token((size_t)1, (size_t)token.length() - 2);
                _cmd["route"] = CtiParseValue( token, -1 );
            }
            CmdStr.replace(re_rtename, "");
        }
        else if(!(token = CmdStr.match(re_rteid)).isNull())
        {
            token.replace( RWCRExpr("select +route *id +"), "");

            if(!token.isNull())   // get the value
            {
                _cmd["route"] = CtiParseValue( atoi(token.data()) );
            }
            CmdStr.replace(re_rteid, "");
        }
        else if(!(token = CmdStr.match(re_ptname)).isNull())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("name +", &nstop);

            nstop += nstart;

            if(!(token = token.match("(\"|')[^\"']+(\"|')", nstop)).isNull())   // get the value
            {
                token = token((size_t)1, (size_t)token.length() - 2);
                _cmd["point"] = CtiParseValue( token, -1 );
            }
            CmdStr.replace(re_ptname, "");
        }
        else if( !(token = CmdStr.match(re_ptid)).isNull())
        {
            token.replace(RWCRExpr("select +point *id +"),"");

            if(!token.isNull())   // get the value
            {
                _cmd["point"] = CtiParseValue( atoi(token.data()) );
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
    if(!(token = CmdStr.match(" priority +[0-9]+")).isNull())
    {
        if(!(strnum = token.match("[0-9]+")).isNull())
        {
            _cmd["xcpriority"] = CtiParseValue( atoi(strnum.data()) );            // Expresscom only supports a 0 - 3 priority 0 highest.
        }
    }

    if(!(cmdstr = tok()).isNull())
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

void  CtiCommandParser::doParseGetValue(const RWCString &CmdStr)
{
    UINT        flag = 0;
    UINT        offset = 0;

    RWCString   temp2;
    RWCString   token;

    RWCRExpr   re_kxx  ("(kwh|kvah|kvarh)[abcdt]?");  //  Match on kwh, kwha,b,c,d,t
    RWCRExpr   re_hrate("h[abcdt]?");                 //  Match on h,ha,hb,hc,hd,ht
    RWCRExpr   re_rate ("rate *[abcdt]");
    RWCRExpr   re_kwh  ("kwh");
    RWCRExpr   re_kvah ("kvah");
    RWCRExpr   re_kvarh("kvarh");

    RWCRExpr   re_demand("dema|kw( |$)|kvar( |$)|kva( |$)");  //  match "dema"nd, but also match "kw", "kvar", or "kva"
    RWCRExpr   re_frozen("froz");                             //     at the end of the string or with whitespace following
    RWCRExpr   re_peak  ("peak");
    RWCRExpr   re_minmax("minmax");

    RWCRExpr   re_powerfail("power");

    RWCRExpr   re_update("upd");

    RWCRExpr   re_offset("off(set)? *[0-9]+");


    RWCTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "getvalue")
    {
        if(!(token = CmdStr.match(re_kxx)).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            // I have a match on the kxxh regular expression....
            if(!(temp2 = token.match(re_kwh)).isNull())
            {
                flag |= CMD_FLAG_GV_KWH;
            }
            else if(!(temp2 = token.match(re_kvah)).isNull())
            {
                flag |= CMD_FLAG_GV_KVAH;
            }
            else if(!(temp2 = token.match(re_kvarh)).isNull())
            {
                flag |= CMD_FLAG_GV_KVARH;
            }

            if(!token.isNull())
            {
                if(!(temp2 = token.match(re_hrate)).isNull())
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
        else if(!(token = CmdStr.match(re_demand)).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_GV_DEMAND;
        }
        else if(!(token = CmdStr.match(re_peak)).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_GV_PEAK;
        }
        else if(!(token = CmdStr.match(re_minmax)).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_GV_MINMAX;
        }
        else if(!(token = CmdStr.match(re_offset)).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_OFFSET;

            // What offset is needed now...
            if(!(temp2 = token.match(re_num)).isNull())
            {
                offset = atoi(temp2.data());
            }
            else
            {
                offset = 0;
            }
        }
        else
        {
            // Default Get Value request has been specified....
        }



        if(!(temp2 = CmdStr.match(re_rate)).isNull())
        {
            flag &= ~CMD_FLAG_GV_RATEMASK;   // This one overrides...

            if(temp2[temp2.length() - 1] == 'a')       flag |= CMD_FLAG_GV_RATEA;
            else if(temp2[temp2.length() - 1] == 'b')  flag |= CMD_FLAG_GV_RATEB;
            else if(temp2[temp2.length() - 1] == 'c')  flag |= CMD_FLAG_GV_RATEC;
            else if(temp2[temp2.length() - 1] == 'd')  flag |= CMD_FLAG_GV_RATED;
            else if(temp2[temp2.length() - 1] == 't')  flag |= CMD_FLAG_GV_RATET;
        }

        if(!(token = CmdStr.match("ied")).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_GV_IED;                     // Read data from the ied port, not internal counters!
        }
        if(!(token = CmdStr.match(re_frozen)).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_FROZEN;
        }

        if(!(token = CmdStr.match(re_powerfail)).isNull())
        {
            flag |= CMD_FLAG_GV_PFCOUNT;
        }

        if(!(token = CmdStr.match(re_update)).isNull())      // Sourcing from CmdStr, which is the entire command string.
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


void  CtiCommandParser::doParseGetStatus(const RWCString &CmdStr)
{
    UINT        flag = 0;
    UINT        offset = 0;

    RWCString   temp2;
    RWCString   token;

    RWCRExpr   re_lp("lp");
    RWCRExpr   re_disc("disc");
    RWCRExpr   re_erro("err");
    RWCRExpr   re_pfco("powerf");
    RWCRExpr   re_intern("inter");
    RWCRExpr   re_extern("extern");
    RWCRExpr   re_eventlog("eventlog(s)?");

    RWCRExpr   re_offset("off(set)? *[0-9]+");

    RWCRExpr   re_frozen("froz");
    RWCRExpr   re_update("upd");
    RWCRExpr   re_iedlink("ied +link");

    RWCRExpr   re_sele("select");

    RWCTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "getstatus")
    {
        if(!(token = CmdStr.match(re_lp)).isNull())
        {
            flag |= CMD_FLAG_GS_LOADSURVEY;
        }
        else if(!(token = CmdStr.match(re_disc)).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_GS_DISCONNECT;
        }
        else if(!(token = CmdStr.match(re_erro)).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_GS_ERRORS;
        }
        else if(!(token = CmdStr.match(re_offset)).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_OFFSET;

            // What offset is needed now...
            if(!(temp2 = token.match(re_num)).isNull())
            {
                offset = atoi(temp2.data());
            }
        }
        else if(!(token = CmdStr.match(re_intern)).isNull())
        {
            flag |= CMD_FLAG_GS_INTERNAL;
        }
        else if(!(token = CmdStr.match(re_extern)).isNull())
        {
            flag |= CMD_FLAG_GS_EXTERNAL;
        }
        else if(!(token = CmdStr.match("ied")).isNull())
        {
            flag |= CMD_FLAG_GS_IED;

            if(!(token = CmdStr.match(re_iedlink)).isNull())
            {
                flag |= CMD_FLAG_GS_LINK;
            }
        }
        else
        {
            // Default GetStatus request has been specified....
        }


        if(!(token = CmdStr.match(re_frozen)).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_FROZEN;
        }
        if(!(token = CmdStr.match(re_update)).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_UPDATE;
        }

        if(!(token = CmdStr.match(re_eventlog)).isNull())
        {
            _cmd["eventlog"] = CtiParseValue(true);
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

void  CtiCommandParser::doParseControl(const RWCString &CmdStr)
{
    INT         _num;
    UINT        flag   = 0;
    UINT        offset = 0;
    UINT        iValue = 0;
    DOUBLE      dValue = 0.0;

    CHAR        tbuf[80];
    CHAR        tbuf2[80];

    RWCString   temp2;
    RWCString   token;

    RWCTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "control")
    {
        if(CmdStr.contains(" delay"))
        {
            RWCString   valStr;

            if(!(temp2 = CmdStr.match(" delay ?time +[0-9]+")).isNull())
            {
                if(!(valStr = temp2.match("[0-9]+")).isNull())
                {
                    iValue = atoi(valStr.data());
                    _cmd["delaytime_sec"] = CtiParseValue( iValue * 60 );
                }
            }

            if(!(temp2 = CmdStr.match(" delay ?until +[0-9]?[0-9]:[0-9][0-9]")).isNull())
            {
                INT hh = 0;
                INT mm = 0;
                INT ofm = 0;      // Offset from Midnight in seconds.

                if(!(valStr = temp2.match("[0-9]?[0-9]:")).isNull())
                {
                    hh = atoi(valStr.data());
                }

                if(!(valStr = temp2.match(":[0-9][0-9]")).isNull())
                {
                    mm = atoi(valStr.data() + 1);
                }


                iValue = (RWTime(hh, mm).seconds() - RWTime().seconds());

                if(iValue > 0)
                {
                    _cmd["delaytime_sec"] = CtiParseValue( iValue );
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        if(!(token = CmdStr.match(" open")).isNull())            // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_OPEN;

            _snprintf(tbuf, sizeof(tbuf), "OPEN");
        }
        else if(!(token = CmdStr.match(" close")).isNull())       // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_CLOSE;
            _snprintf(tbuf, sizeof(tbuf), "CLOSE");
        }
        else if(!(token = CmdStr.match(" disc(onnect)?")).isNull())       // Sourcing from CmdStr, which is the entire command string.
        {
            /* MUST LOOK FOR THIS FIRST! */
            flag |= CMD_FLAG_CTL_DISCONNECT;
            _snprintf(tbuf, sizeof(tbuf), "DISCONNECT");
        }
        else if(!(token = CmdStr.match(" conn(ect)?")).isNull())       // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_CONNECT;
            _snprintf(tbuf, sizeof(tbuf), "CONNECT");
        }
        else if(!(token = CmdStr.match(" restore")).isNull())       // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_RESTORE;
            _snprintf(tbuf, sizeof(tbuf), "RESTORE");
        }
        else if(!(token = CmdStr.match(" term(inate)?")).isNull())       // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_TERMINATE;
            _snprintf(tbuf, sizeof(tbuf), "TERMINATE");
        }
        else if(!(CmdStr.match(" shed")).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_SHED;

            if(!(token = CmdStr.match("shed *[0-9]+(\\.([0-9]+)?)? *[hms]?(( )+|$)")).isNull())      // Sourcing from CmdStr, which is the entire command string.
            {
                DOUBLE mult = 60.0;

                // What shed time is needed now...
                if(!(temp2 = token.match("[0-9]+(\\.([0-9]+)?)?")).isNull())
                {
                    dValue = atof(temp2.data());
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

                if(!(temp2 = token.match("[0-9]+(\\.([0-9]+)?)? *[hs]")).isNull())
                {
                    /*
                     *  Minutes is the assumed entry format, but we return the number in seconds... so
                     */
                    if(!temp2.match("h").isNull())
                    {
                        mult = 3600.0;
                        _snprintf(tbuf, sizeof(tbuf), "%sH", tbuf2);
                    }
                    else if(!temp2.match("s").isNull())
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

            if(!(token = CmdStr.match(" rand(om)? *[0-9]+")).isNull())
            {
                if(!(temp2 = token.match(re_num)).isNull())
                {
                    INT _num = atoi(temp2.data());
                    _cmd["shed_rand"] = CtiParseValue( _num );
                }
            }
        }
        else if(!(token = CmdStr.match(" cycle +[0-9]+")).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_CYCLE;

            if(!(temp2 = token.match(re_num)).isNull())
            {
                iValue = atoi(temp2.data());
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

            if(!(token = CmdStr.match(" period +[0-9]+")).isNull())
            {
                if(!(temp2 = token.match(re_num)).isNull())
                {
                    INT _num = atoi(temp2.data());
                    _cmd["cycle_period"] = CtiParseValue( _num );
                }
            }

            if(!(token = CmdStr.match(" count +[0-9]+")).isNull())
            {
                if(!(temp2 = token.match(re_num)).isNull())
                {
                    INT _num = atoi(temp2.data());
                    _cmd["cycle_count"] = CtiParseValue( _num );
                }
            }

            _cmd["cycle"] = CtiParseValue( (iValue) );
            _snprintf(tbuf, sizeof(tbuf), "CYCLE %d%%", iValue);
        }

        if(!(token = CmdStr.match(" sbo_selectonly")).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["sbo_selectonly"] = CtiParseValue(TRUE);
        }
        if(!(token = CmdStr.match(" sbo_operate")).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["sbo_operate"] = CtiParseValue(TRUE);
        }


        if(flag) _actionItems.insert(tbuf);                      // If anything was set, make sure someone can be informed

        if(!(token = CmdStr.match("off(set)? *[0-9]+")).isNull())            // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_OFFSET;

            // What offset is needed now...
            if(!(temp2 = token.match(re_num)).isNull())
            {
                offset = atoi(temp2.data());
            }
            else
            {
                offset = 0;
            }
        }

        /*
         *  Try to find out if a relay has been specified for the control operation!
         */
        if(!(token = CmdStr.match("(relay)|(load)")).isNull())
        {
            if(!(token = CmdStr.match("((relay)|(load)) +[0-9]+( *, *[0-9]+)*")).isNull())
            {
                INT i;
                INT mask = 0;

                for(i = 0; i < 10; i++)
                {
                    _snprintf(tbuf, sizeof(tbuf), "%d", i+1);
                    if(!(temp2 = token.match(tbuf)).isNull())
                    {
                        mask |= (0x01 << i);
                    }
                }

                if(mask)
                {
                    _cmd["relaymask"] = CtiParseValue( mask );
                }
            }
            if(!(token = CmdStr.match("relay +next")).isNull())
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
        if(!(token = CmdStr.match("froz(en)?")).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_FROZEN;
        }
        if(!(token = CmdStr.match("upd(ate)?")).isNull())      // Sourcing from CmdStr, which is the entire command string.
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
        case ProtocolVersacomType:
        case ProtocolFisherPierceType:
        case ProtocolSA105Type:
        case ProtocolSA205Type:
        case ProtocolSA305Type:
        case ProtocolEmetconType:
        default:
            {
                break;
            }
        }
    }

}


void  CtiCommandParser::doParsePutValue(const RWCString &CmdStr)
{
    UINT        flag = 0;
    UINT        offset = 0;
    double      dial = 0;

    RWCString   temp2;
    RWCString   token;

    char *p;

    RWCRExpr   re_reading("reading *[0-9]+(\\.[0-9]*)?");
    RWCRExpr   re_numfloat("[0-9]+(\\.[0-9]*)?");
    RWCRExpr   re_kyzoffset("kyz *[123]");   //  if there's a kyz offset specified
    RWCRExpr   re_analog("analog +[0-9]+ +-?[0-9]+");


    RWCTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "putvalue")
    {
        if(!(token = CmdStr.match("kyz")).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_PV_DIAL;

            //  default to the first KYZ point
            offset = 1;

            //  if a point offset has been specified
            if(!(token = CmdStr.match(re_kyzoffset)).isNull())
            {
                offset = atoi(token.match("[123]").data());
            }

            //  default to a reset
            dial = 0;

            if(!(token = CmdStr.match(re_reading)).isNull())
            {
                dial = (double)atof(token.match(re_numfloat).data());
            }

            _cmd["dial"]      = CtiParseValue( dial   );
            _cmd["offset"]    = CtiParseValue( offset );
        }
        if(!CmdStr.match("analog").isNull())
        {
            if(!(token = CmdStr.match(re_analog)).isNull())
            {
                flag |= CMD_FLAG_PV_ANALOG;

                RWCTokenizer cmdtok(token);

                cmdtok();

                _cmd["analogoffset"] = CtiParseValue( atoi(cmdtok().data()) );
                _cmd["analogvalue"]  = CtiParseValue( atoi(cmdtok().data()) );
            }
        }
        if(!(CmdStr.match("reset")).isNull())
        {
            flag |= CMD_FLAG_PV_RESET;
        }
        if(!(CmdStr.match("ied")).isNull())    // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_PV_IED;
        }
        if(!(CmdStr.match("power")).isNull())  // Sourcing from CmdStr, which is the entire command string.
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


void  CtiCommandParser::doParsePutStatus(const RWCString &CmdStr)
{
    RWCString   temp2;
    RWCString   token;
    unsigned int flag = 0;

    RWCTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "putstatus")
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

        if(!(token = CmdStr.match(" reset")).isNull())
        {
            if( _cmd.contains("flag") )
                flag = _cmd["flag"].getInt();

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

void  CtiCommandParser::doParseGetConfig(const RWCString &CmdStr)
{
    RWCString   temp2;
    RWCString   token;
    RWCRExpr    rolenum("role *[0-9][0-9]?");
    RWCRExpr    rawcmd("raw +(func(tion)? +)?start=0x[0-9a-f]+( +[0-9]+)?");
    RWCRExpr    interval("interval +(lp|li)");  //  match "interval lp" and "interval li"
    RWCRExpr    multiplier("mult.* *(kyz *[123])?");
    RWCRExpr    address("address (group|uniq)");

    char *p;

    int roleNum, channel;
    RWCTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "getconfig")
    {
        if(!(CmdStr.match("model")).isNull())
        {
            _cmd["model"] = CtiParseValue( "TRUE" );
        }

        if(!(CmdStr.match("options")).isNull())
        {
            _cmd["options"] = CtiParseValue( "TRUE" );
        }

        if(!(CmdStr.match("address")).isNull())
        {
            if(!(token = CmdStr.match(address)).isNull())
            {
                if( !(token.match("group").isNull()) )
                {
                    _cmd["address_group"] = CtiParseValue(TRUE);
                }
/*                else if( !(token.match("uniq").isNull()) )
                {
                    _cmd["address_unique"] = CtiParseValue(TRUE);
                }*/
            }
        }

        if(!(CmdStr.match("role")).isNull())
        {
            if(!(token = CmdStr.match(rolenum)).isNull())
            {
                _cmd["rolenum"] = CtiParseValue( atoi( (token.match( re_num )).data() ) );
            }
        }

        if(!(CmdStr.match("mult")).isNull())
        {
            _cmd["multiplier"] = CtiParseValue(TRUE);

            if(!(token = CmdStr.match(multiplier)).isNull())
            {
                RWCTokenizer cmdtok(token);
                cmdtok();  //  multiplier
                cmdtok();  //  kyz
                _cmd["multchannel"] = CtiParseValue(atoi(cmdtok().data()));
            }
        }

        if(!(CmdStr.match("raw")).isNull())
        {
            if(!(token = CmdStr.match(rawcmd)).isNull())
            {
                RWCTokenizer cmdtok(token);
                RWCString rawData;
                char *p;

                //  go past "raw"
                cmdtok();

                temp2 = cmdtok();

                if(!(temp2.match("func")).isNull())
                {
                    _cmd["rawfunc"] = CtiParseValue("TRUE");
                    temp2 = cmdtok();
                }

                //  get the start address
                _cmd["rawloc"] = CtiParseValue( strtol((temp2.match(re_hexnum)).data(), &p, 16) );

                //  get the length
                temp2 = cmdtok();
                //  if there's length specified
                if( !temp2.isNull() )
                {
                    _cmd["rawlen"] = CtiParseValue( atoi( temp2.data() ) );
                }
            }
        }

        if(!(CmdStr.match("time")).isNull())
        {
            _cmd["time"] = CtiParseValue("TRUE");

            if(!(CmdStr.match("sync")).isNull())
                _cmd["sync"] = CtiParseValue("TRUE");
        }

        if(!(CmdStr.match("ied")).isNull())
        {
            _cmd["ied"] = CtiParseValue("TRUE");
        }

        if(!(CmdStr.match("scan")).isNull())
        {
            _cmd["scan"] = CtiParseValue("TRUE");
        }

        if(!(CmdStr.match("interval")).isNull())
        {
            if(!(token = CmdStr.match(interval)).isNull())
            {
                RWCTokenizer cmdtok(token);
                //  go past "interval"
                cmdtok();

                temp2 = cmdtok();
                //  "li" or "lp"
                _cmd["interval"] = temp2;
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

void  CtiCommandParser::doParsePutConfig(const RWCString &CmdStr)
{
    RWCString   temp2;
    RWCString   token;

    RWCTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "putconfig")
    {
        INT type = getiValue("type");

        if(CmdStr.contains(" timesync"))
        {
            _cmd["timesync"] = CtiParseValue("TRUE");
        }

        // Template should be global.
        if(!(token = CmdStr.match("template +((\"|')[^\"']+(\"|'))")).isNull())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("template +", &nstop);

            nstop += nstart;

            if(!(token = token.match("(\"|')[^\"']+(\"|')", nstop)).isNull())   // get the template name...
            {
                token = token((size_t)1, (size_t)token.length() - 2);
                _cmd["template"] = CtiParseValue( token );
            }

            RWCString sistr;

            if(CmdStr.contains(" service in"))
            {
                sistr = "service in";
            }
            _cmd["templateinservice"] = CtiParseValue( sistr );
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

                if(!(CmdStr.match(" reset")).isNull())
                {
                    if(!(token = CmdStr.match(" filter")).isNull())
                    {
                        setValue("epresetfilter", TRUE);
                    }

                    if(!(token = CmdStr.match(" runtime")).isNull())
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
        case ProtocolFisherPierceType:
        case ProtocolSA105Type:
        case ProtocolSA205Type:
        case ProtocolSA305Type:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Putconfig not supported for this device type " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

void  CtiCommandParser::doParseScan(const RWCString &CmdStr)
{
    RWCString   token;
    RWCRExpr    re_loadprofile("loadprofile( +channel +[1-4])?( +block +[1-8])?");

    RWCTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "scan")
    {
        if(!(token = CmdStr.match("general")).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["scantype"] = CtiParseValue( ScanRateGeneral );
        }
        else if(!(token = CmdStr.match("integrity")).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["scantype"] = CtiParseValue( ScanRateIntegrity );
        }
        else if(!(token = CmdStr.match("accumulator")).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["scantype"] = CtiParseValue( ScanRateAccum );
        }
        else if(!(token = CmdStr.match("status")).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["scantype"] = CtiParseValue( ScanRateStatus );
        }
        else if(!(token = CmdStr.match(re_loadprofile)).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["scantype"] = CtiParseValue( ScanRateLoadProfile );

            RWCTokenizer lp_tok(token);

            lp_tok();  //  pull "loadprofile"

            token = lp_tok();

            if( !(token.compareTo("channel")) )
            {
                token = lp_tok();

                _cmd["scan_loadprofile_channel"] = CtiParseValue(atoi(token.data()));

                token = lp_tok();
            }

            if( !(token.compareTo("block")) )
            {
                token = lp_tok();

                _cmd["scan_loadprofile_block"] = CtiParseValue(atoi(token.data()));
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

    _cmd.findValue("command", pv);

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

bool  CtiCommandParser::isKeyValid(const RWCString key) const
{
    return _cmd.contains(key);
}

UINT     CtiCommandParser::getOffset(const RWCString key) const
{
    CtiParseValue& pv = CtiParseValue(); // = _cmd["command"];
    _cmd.findValue(key, pv);

    return pv.getInt();
}
INT      CtiCommandParser::getiValue(const RWCString key, INT valifnotfound) const
{
    INT val = valifnotfound;

    if(isKeyValid(key))
    {
        CtiParseValue& pv = CtiParseValue(); // = _cmd["command"];
        _cmd.findValue(key, pv);
        val = pv.getInt();
    }

    return val;
}

DOUBLE   CtiCommandParser::getdValue(const RWCString key, DOUBLE valifnotfound) const
{
    DOUBLE val = valifnotfound;

    if(isKeyValid(key))
    {
        CtiParseValue& pv = CtiParseValue(); // = _cmd["command"];
        _cmd.findValue(key, pv);

        val = pv.getReal();
    }

    return val;
}

RWCString CtiCommandParser::getsValue(const RWCString key) const
{
    CtiParseValue& pv = CtiParseValue();
    _cmd.findValue(key, pv);

    return pv.getString();
}

void  CtiCommandParser::doParsePutConfigEmetcon(const RWCString &CmdStr)
{
    RWCString   temp2;
    RWCString   token;
    RWCRExpr    rawcmd("raw +(func(tion)? +)?start=0x[0-9a-f]+( +0x[0-9a-f]+)*");
    RWCRExpr    rolecmd("role +[0-9]+" \
                            " +[0-9]+" \
                            " +[0-9]+" \
                            " +[0-9]+" \
                            " +[0-9]+");
    RWCRExpr    interval("interval +l[pi]");  //  match "interval lp" and "interval li"
    RWCRExpr    multiplier("mult(iplier)? +kyz *[123] +[0-9]+(\\.[0-9]+)?");  //  match "mult kyz # #(.###)
    RWCRExpr    iedClass("ied +class +[0-9]+ +[0-9]+");
    RWCRExpr    iedScan("ied +scan +[0-9]+ +[0-9]+");
    RWCRExpr    groupAddr("group +(enable)|(disable)");
    RWCRExpr    address("address +((gold +[0-9]+ +silver +[0-9]+)|(bronze [0-9]+)|(lead +meter +[0-9]+ +load +[0-9]+))");

    char *p;

    RWCTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "putconfig")
    {
        if(!(CmdStr.match("install")).isNull())
        {
            _cmd["install"] = CtiParseValue(TRUE);
        }
        if(!(CmdStr.match("ied")).isNull())
        {
            _cmd["ied"] = CtiParseValue(TRUE);

            if(!(token = CmdStr.match(iedClass)).isNull())
            {
                RWCTokenizer cmdtok(token);
                cmdtok();  //  go past "ied"
                cmdtok();  //  go past "class"

                _cmd["class"] = CtiParseValue( atoi( cmdtok().data() ) );
                _cmd["classoffset"] = CtiParseValue( atoi( cmdtok().data() ) );
            }
            if(!(token = CmdStr.match(iedScan)).isNull())
            {
                RWCTokenizer cmdtok(token);
                cmdtok();  //  go past "ied"
                cmdtok();  //  go past "scan"

                _cmd["scan"] = CtiParseValue( atoi( cmdtok().data() ) );
                _cmd["scandelay"] = CtiParseValue( atoi( cmdtok().data() ) );
            }
        }
        if(!(CmdStr.match("onoffpeak")).isNull())
        {
            _cmd["onoffpeak"] = CtiParseValue(TRUE);
        }
        if(!(CmdStr.match("minmax")).isNull())
        {
            _cmd["minmax"] = CtiParseValue(TRUE);
        }
        if(!(CmdStr.match("group")).isNull())
        {
            if(!(token = CmdStr.match(groupAddr)).isNull())
            {
                RWCTokenizer cmdtok(token);
                //  go past "group"
                cmdtok();

                if( strcmp( cmdtok().data(), "enable") == 0 )
                {
                    _cmd["groupaddress_enable"] = CtiParseValue( 1 );
                }
                else
                {
                    _cmd["groupaddress_enable"] = CtiParseValue( 0 );
                }
            }
        }
        if(!(CmdStr.match("address")).isNull())
        {
            if(!(token = CmdStr.match(address)).isNull())
            {
                _cmd["groupaddress_set"] = CtiParseValue(TRUE);

                RWCTokenizer cmdtok(token);

                cmdtok();  //  go past "address"

                token = cmdtok();

                if( !(token.match("gold").isNull()) )
                {
                    _cmd["groupaddress_gold"] = CtiParseValue(atoi(cmdtok().data()));

                    cmdtok();  //  go past "silver"

                    _cmd["groupaddress_silver"] = CtiParseValue(atoi(cmdtok().data()));
                }
                else if( !(token.match("bronze").isNull()) )
                {
                    _cmd["groupaddress_bronze"] = CtiParseValue(atoi(cmdtok().data()));
                }
                else if( !(token.match("lead").isNull()) )
                {
                    cmdtok();  //  go past "meter"

                    _cmd["groupaddress_lead_meter"] = CtiParseValue(atoi(cmdtok().data()));

                    cmdtok();  //  go past "load"

                    _cmd["groupaddress_lead_load"] = CtiParseValue(atoi(cmdtok().data()));
                }
            }
        }
        if(!(CmdStr.match("mult")).isNull())
        {
            if(!(token = CmdStr.match(multiplier)).isNull())
            {
                RWCTokenizer cmdtok(token);
                //  go past "multiplier"
                cmdtok();
                //  go past "kyz"
                cmdtok();

                //  kyz offset (1, 2, or 3)
                _cmd["multoffset"] = CtiParseValue( atoi( cmdtok().data() ) );

                //  multiplier
                _cmd["multiplier"] = CtiParseValue( atof( cmdtok().data() ) );
            }
        }
        if(!(CmdStr.match("interval")).isNull())
        {
            if(!(token = CmdStr.match(interval)).isNull())
            {
                RWCTokenizer cmdtok(token);
                //  go past "interval"
                cmdtok();

                //  "li" or "lp"
                _cmd["interval"] = CtiParseValue(cmdtok());
            }
        }
        if(!(CmdStr.match("armc")).isNull())
        {
            _cmd["armc"] = CtiParseValue("TRUE");
        }
        if(!(CmdStr.match("raw")).isNull())
        {
            if(!(token = CmdStr.match(rawcmd)).isNull())
            {
                RWCTokenizer cmdtok(token);
                RWCString rawData;
                int rawloc;

                //  go past "raw"
                cmdtok();

                temp2 = cmdtok();

                if(!(temp2.match("func")).isNull())
                {
                    _cmd["rawfunc"] = CtiParseValue("TRUE");
                    temp2 = cmdtok();
                }

                _cmd["rawloc"] = CtiParseValue( strtol((temp2.match(re_hexnum)).data(), &p, 16) );
//            CtiLockGuard<CtiLogger> logger_guard(dout);
//            dout << "rawloc = " << temp2.data() << endl;

                while( !(temp2 = cmdtok()).isNull() )
                {
                    rawData.append( (char)strtol(temp2.data(), &p, 16) );
//                CtiLockGuard<CtiLogger> logger_guard(dout);
//                dout << "rawdata = " << (char)strtol(temp2.data(), &p, 16) << endl;
                }

                _cmd["rawdata"] = CtiParseValue( rawData );
            }
        }
        if(!(CmdStr.match(" role")).isNull())
        {
            if(!(token = CmdStr.match(rolecmd)).isNull())
            {
                RWCTokenizer cmdtok(token);
                RWCString rawData;
                int rawloc;

                //  go past "role"
                cmdtok();

                temp2 = cmdtok();
                _cmd["rolenum"] = CtiParseValue( atoi(temp2.data()) );
                temp2 = cmdtok();
                _cmd["rolefixed"] = CtiParseValue( atoi(temp2.data()) );
                temp2 = cmdtok();
                _cmd["roleout"] = CtiParseValue( atoi(temp2.data()) );
                temp2 = cmdtok();
                _cmd["rolein"] = CtiParseValue( atoi(temp2.data()) );
                temp2 = cmdtok();
                _cmd["rolerpt"] = CtiParseValue( atoi(temp2.data()) );
            }
        }
        if(!(CmdStr.match(" mrole")).isNull())
        {
            if(!(token = CmdStr.match("mrole( +[0-9]+)+")).isNull())
            {
                RWCTokenizer cmdtok(token);

                //  hop over "multi_role"
                cmdtok();

                // Command looks like this:
                // putconfig emetcon multi_role 1 f1 vo1 vi1 stf1 f2 vo2 vi2 stf2 ... fn von vin stfn
                // The roles are written in 6 role minimum blocks and are filled with default role (31 7 7 0)
                // if fewer than 6 roles are defined in the block.

                temp2 = cmdtok();
                _cmd["multi_rolenum"] = CtiParseValue( atoi(temp2.data()) );    // This is the first role written.  They must be consecutive!

                // Now we need to pull them off one at a time until done..
                int fixbits;
                int varbits_out;
                int varbits_in;
                int stagestf;
                int rolecount = 0;

                RWCString strFixed;
                RWCString strVarOut;
                RWCString strVarIn;
                RWCString strStages;

                while(!(temp2 = cmdtok()).isNull())
                {
                    fixbits = !temp2.isNull() ? atoi(temp2.data()) : 31;
                    temp2 = cmdtok();
                    varbits_out = !temp2.isNull() ? atoi(temp2.data()) : 7;
                    temp2 = cmdtok();
                    varbits_in = !temp2.isNull() ? atoi(temp2.data()) : 7;
                    temp2 = cmdtok();
                    stagestf = !temp2.isNull() ? atoi(temp2.data()) : 15;

                    strFixed    += CtiNumStr( fixbits ) + " " ;
                    strVarOut   += CtiNumStr( varbits_out ) + " " ;
                    strVarIn    += CtiNumStr( varbits_in ) + " " ;
                    strStages   += CtiNumStr( stagestf ) + " " ;

                    rolecount++;
                }

#if 0
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Roles found " << rolecount << endl;

                    dout << " First role " << getiValue("multi_rolenum") << endl;
                    dout << " Fix   bits " << strFixed << endl;
                    dout << " OD    bits " << strVarOut << endl;
                    dout << " ID    bits " << strVarIn << endl;
                    dout << " STF   bits " << strStages << endl;
                }
#endif

                _cmd["multi_rolefixed"] = CtiParseValue( strFixed.strip() );
                _cmd["multi_roleout"] = CtiParseValue( strVarOut.strip() );
                _cmd["multi_rolein"] = CtiParseValue( strVarIn.strip() );
                _cmd["multi_rolerpt"] = CtiParseValue( strStages.strip() );
                _cmd["multi_rolecount"] = CtiParseValue( rolecount );
            }
        }
    }
    else
    {
        // Something went WAY wrong....
        dout << "This better not ever be seen by mortals... " << endl;
    }
}

void  CtiCommandParser::doParsePutConfigVersacom(const RWCString &CmdStr)
{
    char *p;

    char        tbuf[60];

    RWCString   token;
    RWCString   temp, temp2;
    RWCString   strnum;
    RWCString   str;

    INT         _num = 0;

    RWCTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "putconfig")
    {
        if(!(token = CmdStr.match("vdata( +(0x)?[0-9a-f])+")).isNull())
        {
            RWCTokenizer cmdtok(token);
            RWCString rawData;

            //  go past "vdata"
            cmdtok();

            while( !(temp2 = cmdtok()).isNull() )
            {
                rawData.append( (char)strtol(temp2.data(), &p, 16) );
            }

            _cmd["vdata"] = CtiParseValue( rawData );
        }

        if(!(token = CmdStr.match(" util(ity)?[ =]*([ =]+0x)?[0-9a-f]+")).isNull())
        {
            if(!(temp = token.match(re_hexnum)).isNull())
            {
                _num = strtol(temp.data(), &p, 16);
            }
            else
            {
                _num = strtol(token.match(re_num).data(), &p, 10);
            }

            _cmd["utility"] = CtiParseValue( _num );

            _snprintf(tbuf, sizeof(tbuf), "CONFIG UID = %d", _num);
            _actionItems.insert(tbuf);
        }

        if(!(token = CmdStr.match(" aux*[ =]*([ =]+0x)?[0-9a-f]+")).isNull())
        {
            if(!(temp = token.match(re_hexnum)).isNull())
            {
                _num = strtol(temp.data(), &p, 16);
            }
            else
            {
                _num = strtol(token.match(re_num).data(), &p, 10);
            }

            _cmd["aux"] = CtiParseValue( _num );

            _snprintf(tbuf, sizeof(tbuf), "CONFIG AUX = %d", _num);
            _actionItems.insert(tbuf);
        }

        if(!(token = CmdStr.match(" sect(ion)?[ =]*[0-9]+")).isNull())
        {
            {
                _num = strtol(token.match(re_num).data(), &p, 10);
            }

            _cmd["section"] = CtiParseValue( _num );

            _snprintf(tbuf, sizeof(tbuf), "CONFIG SECTION = %d", _num);
            _actionItems.insert(tbuf);
        }

        if(!(token = CmdStr.match(" clas")).isNull())
        {
            if(!(token = CmdStr.match(" class[ =]*([ =]+)?0x[0-9a-f]+")).isNull())
            {
                if(!(temp = token.match(re_hexnum)).isNull())
                {
                    _num = strtol(temp.data(), &p, 16);
                }
                else
                {
                    _num = strtol(token.match(re_num).data(), &p, 10);
                    _num = (0x0001 << (16 - _num));
                }

                int itemp = 0;
                for(int q = 0; q < 16; q++)
                {
                    itemp |= (((_num >> q) & 0x0001) << (15 - q));
                }
                _num = itemp;

                _cmd["class"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG CLASS = %s", convertVersacomAddressToHumanForm(_num).data());
                _actionItems.insert(tbuf);

            }
            else if(!(token = CmdStr.match(" class +[0-9]+" \
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
                                          )).isNull())
            {
                RWCTokenizer   tok( token );
                _num = 0;
                int  mask = 0x0001;
                int  val;

                tok();   // Get over the class entry

                // dout << __LINE__ << " " << token << endl;

                for(int i = 0; i < 16 && !(temp = tok(", \n\0")).isNull() ; i++)
                {
                    val = atoi( temp.data() );

                    // dout << "Masking in " << temp << " " << val << endl;
                    if(val > 0)
                    {
                        _num |= (mask << (val - 1));
                    }
                }

                _cmd["class"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG CLASS = %s", convertVersacomAddressToHumanForm(_num).data());
                _actionItems.insert(tbuf);
            }
        }

        if(!(token = CmdStr.match(" divi")).isNull())
        {
            if(!(token = CmdStr.match(" divi(sion)?[ =]*([ =]+)?0x[0-9a-f]+")).isNull())
            {
                if(!(temp = token.match(re_hexnum)).isNull())
                {
                    _num = strtol(temp.data(), &p, 16);
                }
                else
                {
                    _num = strtol(token.match(re_num).data(), &p, 10);
                    _num = (0x0001 << (16 - _num));
                }

                int itemp = 0;

                for(int q = 0; q < 16; q++)
                {
                    itemp |= (((_num >> q) & 0x0001) << (15 - q));
                }

                _num = itemp;

                _cmd["division"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG DIVISION = %s", convertVersacomAddressToHumanForm(_num).data());
                _actionItems.insert(tbuf);

            }
            else if(!(token = CmdStr.match(" divi(sion)? +[0-9]+" \
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
                                          )).isNull())
            {
                RWCTokenizer   tok( token );
                _num = 0;
                int  mask = 0x0001;
                int  val;

                tok();   // Get over the "division" entry

                // dout << __LINE__ << " " << token << endl;

                for(int i = 0; i < 16 && !(temp = tok(", \n\0")).isNull() ; i++)
                {
                    val = atoi( temp.data() );

                    // dout << "Masking in " << temp << " " << val << endl;
                    if(val > 0)
                    {
                        _num |= (mask << (val - 1));
                    }
                }

                _cmd["division"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG DIVISION = %s", convertVersacomAddressToHumanForm(_num).data());
                _actionItems.insert(tbuf);
            }
        }


        if(!(token = CmdStr.match("from")).isNull())
        {
            if(!(token = CmdStr.match("fromutil(ity)?[ =]*([ =]+0x)?[0-9a-f]+")).isNull())
            {
                if(!(temp = token.match(re_hexnum)).isNull())
                {
                    _num = strtol(temp.data(), &p, 16);
                }
                else
                {
                    _num = strtol(token.match(re_num).data(), &p, 10);
                }

                _cmd["fromutility"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG FROM UID = %d", _num);
                _actionItems.insert(tbuf);
            }

            if(!(token = CmdStr.match("fromsect(ion)?[ =]*([ =]+(0x))?[0-9a-f]+")).isNull())
            {
                if(!(temp = token.match(re_hexnum)).isNull())
                {
                    _num = strtol(temp.data(), &p, 16);
                }
                else
                {
                    _num = strtol(token.match(re_num).data(), &p, 10);
                }

                _cmd["fromsection"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG FROM SECTION = %d", _num);
                _actionItems.insert(tbuf);
            }

            if(!(token = CmdStr.match("fromclass[ =]*([ =]+)?0x[0-9a-f]+")).isNull())
            {
                if(!(temp = token.match(re_hexnum)).isNull())
                {
                    _num = strtol(temp.data(), &p, 16);
                }
                else
                {
                    _num = strtol(token.match(re_num).data(), &p, 10);
                    _num = (0x0001 << (16 - _num));
                }

                int itemp = 0;
                for(int q = 0; q < 16; q++)
                {
                    itemp |= (((_num >> q) & 0x0001) << (15 - q));
                }
                _num = itemp;

                _cmd["fromclass"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG FROM CLASS = %s", convertVersacomAddressToHumanForm(_num).data());
                _actionItems.insert(tbuf);

            }

            if(!(token = CmdStr.match("fromdivi(sion)?[ =]*([ =]+)?0x[0-9a-f]+")).isNull())
            {
                if(!(temp = token.match(re_hexnum)).isNull())
                {
                    _num = strtol(temp.data(), &p, 16);
                }
                else
                {
                    _num = strtol(token.match(re_num).data(), &p, 10);
                    _num = (0x0001 << (16 - _num));
                }

                int itemp = 0;

                for(int q = 0; q < 16; q++)
                {
                    itemp |= (((_num >> q) & 0x0001) << (15 - q));
                }

                _num = itemp;

                _cmd["fromdivision"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG FROM DIVISION = %s", convertVersacomAddressToHumanForm(_num).data());
                _actionItems.insert(tbuf);

            }
        }

        if(!(token = CmdStr.match("assign")).isNull())
        {
            if(!(token = CmdStr.match("assign"\
                                      "(( +[uascd][ =]*(0x)?[0-9a-f]+)*)+")).isNull())
            {
                // dout << token << endl;
                _cmd["vcassign"] = CtiParseValue( TRUE );

                if(!(strnum = token.match(" u[ =]*(0x)?[0-9a-f]+")).isNull())
                {
                    _num = strtol(strnum.match(re_anynum).data(), &p, 0);

                    _cmd["utility"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG UTILITY = %d", _num);
                    _actionItems.insert(tbuf);
                }
                if(!(strnum = token.match(" +a[ =]*(0x)?[0-9a-f]+")).isNull())
                {
                    if(!(temp = strnum.match(re_hexnum)).isNull())
                    {
                        _num = strtol(temp.data(), &p, 16);
                    }
                    else
                    {
                        _num = strtol(strnum.match(re_num).data(), &p, 10);
                    }
                    _cmd["aux"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG AUX = %d", _num);
                    _actionItems.insert(tbuf);
                }
                if(!(strnum = token.match(" +s[ =]*[0-9]+")).isNull())
                {
                    _num = strtol(strnum.match(re_num).data(), &p, 10);
                    _cmd["section"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG SECTION = %d", _num);
                    _actionItems.insert(tbuf);
                }
                if(!(strnum = token.match(" +c[ =]*(0x)?[0-9a-f]+")).isNull())
                {
                    if(!(temp = strnum.match(re_hexnum)).isNull())
                    {
                        _num = strtol(temp.data(), &p, 16);
                    }
                    else
                    {
                        _num = strtol(strnum.match(re_num).data(), &p, 10);
                        _num = (0x0001 << (16 - _num));
                    }

                    int itemp = 0;

                    for(int q = 0; q < 16; q++)
                    {
                        itemp |= (((_num >> q) & 0x0001) << (15 - q));
                    }

                    _num = itemp;

                    _cmd["class"] = CtiParseValue( _num );
                    _snprintf(tbuf, sizeof(tbuf), "CONFIG CLASS = %s", convertVersacomAddressToHumanForm(_num).data());
                    _actionItems.insert(tbuf);

                }
                if(!(strnum = token.match(" +d[ =]*(0x)?[0-9a-f]+")).isNull())
                {
                    if(!(temp = strnum.match(re_hexnum)).isNull())
                    {
                        _num = strtol(temp.data(), &p, 16);
                    }
                    else
                    {
                        _num = strtol(strnum.match(re_num).data(), &p, 10);
                        _num = (0x0001 << (16 - _num));
                    }

                    int itemp = 0;

                    for(int q = 0; q < 16; q++)
                    {
                        itemp |= (((_num >> q) & 0x0001) << (15 - q));
                    }

                    _num = itemp;

                    _cmd["division"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG DIVISION = %s", convertVersacomAddressToHumanForm(_num).data());
                    _actionItems.insert(tbuf);

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
                    _actionItems.insert("SERVICE ENABLE");
                }
                else if( CmdStr.contains(" service out"))
                {
                    serviceflag |= 0x04;
                    _actionItems.insert("SERVICE DISABLE");
                }

                if(CmdStr.contains(" service tin"))
                {
                    serviceflag |= 0x02;
                    _actionItems.insert("SERVICE ENABLE TEMPORARY");
                }
                else if(CmdStr.contains(" service tout"))
                {
                    serviceflag |= 0x01;
                    _actionItems.insert("SERVICE DISABLE TEMPORARY");
                }

                if(serviceflag)
                {
                    _cmd["assignedservice"] = CtiParseValue( serviceflag );
                }
            }
        }

        if(!(token = CmdStr.match("serv")).isNull())
        {
            if(!(token = CmdStr.match(" serv(ice)? +((in)|(out)|(enable)|(disable))( +temp)?")).isNull())
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
                if(!(strnum = token.match("in")).isNull() ||
                   !(strnum = token.match("enable")).isNull())
                {
                    flag |= 0x0a;

                    _snprintf(tbuf, sizeof(tbuf), "SERVICE ENABLE");
                }
                else if(!(strnum = token.match("out")).isNull() ||
                        !(strnum = token.match("disable")).isNull())
                {
                    flag |= 0x04;

                    _snprintf(tbuf, sizeof(tbuf), "SERVICE DISABLE");
                }

                if(token.contains(" temp"))
                {
                    char t2[80];
                    strcpy(t2, tbuf);

                    flag >>= 2;       // Make the flag match the protocol

                    _snprintf(tbuf, sizeof(tbuf), "%s TEMPORARY", t2);

                    if(!(token = CmdStr.match("offhours +[0-9]+")).isNull())
                    {
                        bool offhourssupported = false;
                        int serialnumber = getiValue("serial", 0);

                        if( serialnumber != 0 )
                        {
                            RWCString vcrangestr = gConfigParms.getValueAsString("LCR_VERSACOM_EXTENDED_TSERVICE_RANGES");

                            if(!vcrangestr.isNull())
                            {
                                while(!vcrangestr.isNull())
                                {
                                    RWCString rstr = vcrangestr.match("[0-9]*-[0-9]*,?");

                                    if(!rstr.isNull())
                                    {
                                        char *chptr;
                                        RWCString startstr = rstr.match("[0-9]*");
                                        RWCString stopstr = rstr.match(" *- *[0-9]* *,? *");
                                        stopstr = stopstr.strip(RWCString::both, ' ');
                                        stopstr = stopstr.strip(RWCString::leading, '-');
                                        stopstr = stopstr.strip(RWCString::trailing, ',');
                                        stopstr = stopstr.strip(RWCString::both, ' ');

                                        UINT startaddr = strtoul( startstr.data(), &chptr, 10 );
                                        UINT stopaddr = strtoul( stopstr.data(), &chptr, 10 );

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
                                }
                            }
                        }

                        if(offhourssupported)
                        {
                            str = token.match("[0-9]+");
                            int offtimeinhours = atoi(str.data());

                            _cmd["vctexservice"] = CtiParseValue( TRUE );
                            _cmd["vctservicetime"] = CtiParseValue( offtimeinhours > 65535 ? 65535 : offtimeinhours );  // Must be passed as half seconds for VCOM
                        }
                    }
                }

                _cmd["service"] = CtiParseValue( flag );

                _actionItems.insert(tbuf);
            }
        }

        if(!(token = CmdStr.match("led +(y|n) *(y|n) *(y|n)")).isNull())
        {
            INT   flag = 0;
            int   i;
            int   mask;

            token.toLower();

            if(!(strnum = token.match("(y|n) *(y|n) *(y|n)")).isNull())
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
            _actionItems.insert(tbuf);
        }

        if(!(CmdStr.match("reset")).isNull())
        {
            if(!(token = CmdStr.match("reset[_a-z]*( +((rtc)|(lf)|(r1)|(r2)|(r3)|(r4)|(cl)|(pt)))+")).isNull())
            {
                INT   flag = 0;

                if(!(strnum = token.match("rtc")).isNull())
                {
                    flag |= 0x40;
                }
                if(!(strnum = token.match("lf")).isNull())
                {
                    flag |= 0x20;
                }
                if(!(strnum = token.match("r1")).isNull())
                {
                    flag |= 0x10;
                }
                if(!(strnum = token.match("r2")).isNull())
                {
                    flag |= 0x08;
                }
                if(!(strnum = token.match("r3")).isNull())
                {
                    flag |= 0x04;
                }
                if(!(strnum = token.match("r4")).isNull())
                {
                    flag |= 0x80;
                }
                if(!(strnum = token.match("cl")).isNull())
                {
                    flag |= 0x02;
                }
                if(!(strnum = token.match("pt")).isNull())
                {
                    flag |= 0x01;
                }

                _cmd["reset"] = CtiParseValue( flag );

                _snprintf(tbuf, sizeof(tbuf), "CNTR RESET = 0x%02X", flag);
                _actionItems.insert(tbuf);
            }
        }

        if(!(CmdStr.match("prop")).isNull())
        {
            if(!(token = CmdStr.match("prop[ a-z_]*[ =]*([ =]+)?[0-9]+")).isNull())
            {
                // What offset is needed now...
                if(!(strnum = token.match("[0-9]+")).isNull())
                {
                    _num = strtol(strnum.data(), &p, 10);
                    _cmd["proptime"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG PROPTIME = %d", _num);
                    _actionItems.insert(tbuf);
                }
            }
        }

        if(!(CmdStr.match(" cold")).isNull())
        {
            if(!(token = CmdStr.match("cold[ a-z_]*" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)?" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)?")).isNull())
            {
                // dout << token << endl;

                if(!(strnum = token.match("r1[ =]*[0-9]+[ =]*[hms]?")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    // _num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["coldload_r1"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG COLDLOAD R1 = %d", _num);
                    _actionItems.insert(tbuf);
                }
                if(!(strnum = token.match("r2[ =]*[0-9]+[ =]*[hms]?")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["coldload_r2"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG COLDLOAD R2 = %d", _num);
                    _actionItems.insert(tbuf);
                }
                if(!(strnum = token.match("r3[ =]*[0-9]+[ =]*[hms]?")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["coldload_r3"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG COLDLOAD R3 = %d", _num);
                    _actionItems.insert(tbuf);
                }
            }
        }

        if(!(CmdStr.match(" cycle")).isNull())
        {
            if(!(token = CmdStr.match("cycle[ a-z]*" \
                                      "( *r[123][ =]*[0-9]+[ =]*)" \
                                      "( *r[123][ =]*[0-9]+[ =]*)?" \
                                      "( *r[123][ =]*[0-9]+[ =]*)?")).isNull())
            {
                if(!(strnum = token.match("r1[ =]*[0-9]+")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    _num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _cmd["cycle_r1"] = CtiParseValue( _num );
                }
                if(!(strnum = token.match("r2[ =]*[0-9]+[ =]*")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    _num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _cmd["cycle_r2"] = CtiParseValue( _num );
                }
                if(!(strnum = token.match("r3[ =]*[0-9]+[ =]*")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    _num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _cmd["cycle_r3"] = CtiParseValue( _num );
                }
            }
        }

        if(!(CmdStr.match("scram")).isNull())
        {
            if(!(token = CmdStr.match("scram[ a-z]*" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)?" \
                                      "( *r[123][ =]*[0-9]+[ =]*[hms]?)?")).isNull())
            {
                if(!(strnum = token.match("r1[ =]*[0-9]+[ =]*[hms]?")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["scram_r1"] = CtiParseValue( _num );
                }
                if(!(strnum = token.match("r2[ =]*[0-9]+[ =]*[hms]?")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["scram_r2"] = CtiParseValue( _num );
                }
                if(!(strnum = token.match("r3[ =]*[0-9]+[ =]*[hms]?")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["scram_r3"] = CtiParseValue( _num );
                }
            }
        }

        if(!(CmdStr.match("raw")).isNull())
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
                                      "( *([ =]+0x)?[0-9a-f]+)?")).isNull())
            {
                int   i;
                int   val;
                char str[10];
                char *ptr = NULL;

                // dout << __LINE__ << " " << token << endl;

                CtiParseValue  rawStr;     // This is to contain the raw bytes



                RWCTokenizer   tok( token );

                tok();   // Get us past the "raw" moniker..

                for(i = 0; i < 20 && !token.isNull(); i++)
                {
                    _snprintf(str, sizeof(str), "raw_byte_%d", i);
                    token = tok(); // Returns each whitespace seperated token successivly.

                    if(!token.isNull())
                    {
                        val = strtol( token, &ptr, 16 );
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
    }
    else
    {
        // Something went WAY wrong....
        dout << "This better not ever be seen by mortals... " << endl;
    }

    if(!(CmdStr.match("test_mode_flag")).isNull())
    {
        _cmd["flag"] = CtiParseValue( CMD_FLAG_TESTMODE );
    }
}

void  CtiCommandParser::doParsePutStatusEmetcon(const RWCString &CmdStr)
{
    RWCString   temp2;
    RWCString   token;
    unsigned int flag = 0;
    char *p;

    RWCTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "putstatus")
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
        }
    }
    else
    {
        // Something went WAY wrong....
        dout << "This better not ever be seen by mortals... " << endl;
    }
}

void  CtiCommandParser::doParsePutStatusVersacom(const RWCString &CmdStr)
{
    char *p;

    char        tbuf[60];

    RWCString   token;
    RWCString   temp2;
    RWCString   strnum;

    INT         _num = 0;

    RWCTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "putstatus")
    {
        if(!(token = CmdStr.match("prop[a-z]*[ =]+((disp[a-z]*)|(inc[a-z]*)|(term[a-z]*))")).isNull())
        {
            int   op = 0;
            CHAR  op_name[20];

            if(!(token.match("disp(lay)?")).isNull())                   // Turn on light & bump counter.
            {
                op = 0x04;
                _snprintf(op_name, sizeof(op_name), "DISPLAY");
            }
            else if(!(token.match("inc(rement)?")).isNull())            // bump counter.
            {
                op = 0x02;
                _snprintf(op_name, sizeof(op_name), "INCREMENT");
            }
            else if(!(token.match("term(inate)?")).isNull())            // Turn off light.
            {
                op = 0x01;
                _snprintf(op_name, sizeof(op_name), "TERMINATE");
            }

            if(op)
            {
                _cmd["proptest"] = CtiParseValue( op );

                _snprintf(tbuf, sizeof(tbuf), "PROP TEST: %01x = %s", op, op_name);
                _actionItems.insert(tbuf);
            }
        }
        else if(!(token = CmdStr.match("ovuv[ =]+((ena(ble)?)|(dis(able)?))")).isNull())
        {
            int   op = 0;
            CHAR  op_name[20];

            if(!(token.match("ena")).isNull())
            {
                op = 1;
                _snprintf(op_name, sizeof(op_name), "ENABLE");
            }
            else if(!(token.match("dis")).isNull())
            {
                op = 0;
                _snprintf(op_name, sizeof(op_name), "DISABLE");
            }

            _cmd["ovuv"] = CtiParseValue( op );

            _snprintf(tbuf, sizeof(tbuf), "OVUV %s", op_name);
            _actionItems.insert(tbuf);
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

void  CtiCommandParser::doParsePutStatusFisherP(const RWCString &CmdStr)
{
    char *p;
    char        tbuf[60];

    RWCString   token;
    RWCString   temp2;
    RWCString   strnum;

    INT         _num = 0;

    RWCTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "putstatus")
    {

        if(!(token = CmdStr.match("ovuv[ =]+((ena(ble)?)|(dis(able)?))")).isNull())
        {
            int   op = 0;
            CHAR  op_name[20];

            if(!(token.match("ena")).isNull())
            {
                op = 1;
                _snprintf(op_name, sizeof(op_name), "ENABLE");
            }
            else if(!(token.match("dis")).isNull())
            {
                op = 0;
                _snprintf(op_name, sizeof(op_name), "DISABLE");
            }

            _cmd["ovuv"] = CtiParseValue( op );

            _snprintf(tbuf, sizeof(tbuf), "OVUV %s", op_name);
            _actionItems.insert(tbuf);
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

RWTValSlist< RWCString >& CtiCommandParser::getActionItems()
{
    return _actionItems;
}

void CtiCommandParser::resolveProtocolType(const RWCString &CmdStr)
{
    RWCString         token;

    /*
     *  Type is assigned i.f.f. there is a serial number specified.  The default type is versacom.
     */
    if(!isKeyValid("type"))
    {
        if( isKeyValid("serial") )
        {
            if(CmdStr.contains("emetcon"))
            {
                _cmd["type"] = CtiParseValue( "emetcon",  ProtocolEmetconType );
            }
            else if(CmdStr.contains("fp"))            // Sourcing from CmdStr, which is the entire command string.
            {
                _cmd["type"] = CtiParseValue( "fp",  ProtocolFisherPierceType );
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
            else if(CmdStr.contains("vcom") || CmdStr.contains("versacom"))
            {
                _cmd["type"] = CtiParseValue( "versacom", ProtocolVersacomType );
            }
            else
            {
                int serialnumber = getiValue("serial", 0);

                RWCString xcrangestr = gConfigParms.getValueAsString("LCR_EXPRESSCOM_RANGES");
                RWCString vcrangestr = gConfigParms.getValueAsString("LCR_VERSACOM_RANGES");

                if(!vcrangestr.isNull() || !xcrangestr.isNull())
                {
                    while(!vcrangestr.isNull())
                    {
                        RWCString str = vcrangestr.match("[0-9]*-[0-9]*,?");

                        if(!str.isNull())
                        {
                            char *chptr;
                            RWCString startstr = str.match("[0-9]*");
                            RWCString stopstr = str.match(" *- *[0-9]* *,? *");
                            stopstr = stopstr.strip(RWCString::both, ' ');
                            stopstr = stopstr.strip(RWCString::leading, '-');
                            stopstr = stopstr.strip(RWCString::trailing, ',');
                            stopstr = stopstr.strip(RWCString::both, ' ');

                            UINT startaddr = strtoul( startstr.data(), &chptr, 10 );
                            UINT stopaddr = strtoul( stopstr.data(), &chptr, 10 );

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
                    }

                    while(!isKeyValid("type") && !xcrangestr.isNull())
                    {
                        RWCString str = xcrangestr.match("[0-9]*-[0-9]*,?");

                        if(!str.isNull())
                        {
                            char *chptr;
                            RWCString startstr = str.match("[0-9]*");
                            RWCString stopstr = str.match(" *- *[0-9]* *,? *");
                            stopstr = stopstr.strip(RWCString::both, ' ');
                            stopstr = stopstr.strip(RWCString::leading, '-');
                            stopstr = stopstr.strip(RWCString::trailing, ',');
                            stopstr = stopstr.strip(RWCString::both, ' ');

                            UINT startaddr = strtoul( startstr.data(), &chptr, 10 );
                            UINT stopaddr = strtoul( stopstr.data(), &chptr, 10 );

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
                RWCString xcprefixrange = gConfigParms.getValueAsString("LCR_EXPRESSCOM_SERIAL_PREFIX_RANGES");
                if(!xcprefixrange.isNull())
                {
                    while(!xcprefixrange.isNull())
                    {
                        RWCString str = xcprefixrange.match("[0-9]*-[0-9]*,?");

                        if(!str.isNull())
                        {
                            char *chptr;
                            RWCString startstr = str.match("[0-9]*");
                            RWCString stopstr = str.match(" *- *[0-9]* *,? *");
                            stopstr = stopstr.strip(RWCString::both, ' ');
                            stopstr = stopstr.strip(RWCString::leading, '-');
                            stopstr = stopstr.strip(RWCString::trailing, ',');
                            stopstr = stopstr.strip(RWCString::both, ' ');

                            UINT startaddr = strtoul( startstr.data(), &chptr, 10 );
                            UINT stopaddr = strtoul( stopstr.data(), &chptr, 10 );

                            if(startaddr <= serialnumber && serialnumber <= stopaddr)
                            {
                                // This is a prefix switch switch and we can continue!
                                _cmd["xcprefix"] = CtiParseValue( TRUE );
                                _cmd["xcprefixstr"] = CtiParseValue( gConfigParms.getValueAsString("LCR_EXPRESSCOM_SERIAL_PREFIX_MESSAGE") );
                                break;
                            }
                        }

                        xcprefixrange.replace("[0-9]*-[0-9]*,?", "");
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
            else
            {  //  default to Versacom if nothing found
                _cmd["type"] = CtiParseValue( "versacom", ProtocolVersacomType );
            }
        }
    }
}

const RWCString& CtiCommandParser::getCommandStr() const
{
    return _cmdString;
}

INT CtiCommandParser::convertTimeInputToSeconds(const RWCString& inStr) const
{
    INT val  = -1;
    INT mult = 60; //assume minutes for fun ok.

    RWCRExpr   re_scale("[hms]");
    RWCString   temp2;


    if(!inStr.match("[0-9]+").isNull())
    {
        val = atoi(inStr.match("[0-9]+").data());

        if(!(temp2 = inStr.match(re_scale)).isNull())
        {
            if(temp2.data()[0] == 'h')
            {
                mult = 3600.0;
            }
            else if(temp2.data()[0] == 's')
            {
                mult = 1.0;
            }
        }

        val = val * mult;
    }

    return val;
}

void CtiCommandParser::Dump()
{
    CHAR  oldFill = dout.fill();

    dout.fill('0');

    map_itr_type itr(_cmd);

    for(; itr(); )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Key " << itr.key() << " Value (str) " << itr.value().getString() <<
            " (int) " << itr.value().getInt() <<
            " (dbl) " << itr.value().getReal() <<
            " (bytes) ";
        }

        for(int i = 0; i < itr.value().getString().length(); i++ )
        {
            dout << hex << setw(2) << (INT)(itr.value().getString()(i)) << " ";
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
        else
        {
            ctrld = CONTROLLED;                             // Juice is NOT flowing electrically speaking...!
        }
    }

    return ctrld;
}

bool CtiCommandParser::isControlled() const
{
    return ( CONTROLLED == getControlled() );
}

bool CtiCommandParser::isTwoWay() const
{
    bool bret = false;

    if(getCommand() == GetValueRequest  ||
       getCommand() == GetConfigRequest ||
       getCommand() == ScanRequest      ||
       getCommand() == LoopbackRequest)
    {
        bret = true;
    }

    return bret;
}

CtiCommandParser& CtiCommandParser::setValue(const RWCString key, INT val)
{
    _cmd[key] = CtiParseValue(val);
    return *this;
}
CtiCommandParser& CtiCommandParser::setValue(const RWCString key, DOUBLE val)
{
    _cmd[key] = CtiParseValue(val);
    return *this;
}
CtiCommandParser& CtiCommandParser::setValue(const RWCString key, RWCString val)
{
    _cmd[key] = CtiParseValue(val);
    return *this;
}

void  CtiCommandParser::doParseControlExpresscom(const RWCString &CmdStr)
{
    INT         _num;
    UINT        flag   = 0;
    UINT        offset = 0;
    INT         iValue = 0;
    DOUBLE      dValue = 0.0;

    CHAR        tbuf[80];
    CHAR        tbuf2[80];

    RWCString   str;
    RWCString   temp;
    RWCString   valStr;
    RWCString   token;

    RWCTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....


    if(!(temp = CmdStr.match(" mode (heat)|(cool)|(both)")).isNull())
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
        if(!(temp = CmdStr.match(" rand(om)?start +[0-9]+")).isNull())
        {
            if(!(valStr = temp.match("[0-9]+")).isNull())
            {
                iValue = atoi(valStr.data());
                _cmd["xcrandstart"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" rand(om)? ?stop +[0-9]+")).isNull())
        {
            if(!(valStr = temp.match("[0-9]+")).isNull())
            {
                iValue = atoi(valStr.data());
                _cmd["xcrandstop"] = CtiParseValue( iValue );
            }
        }
    }

    if(!(token = CmdStr.match(" tcycle +[0-9]+")).isNull())
    {
        _cmd["xctcycle"] = CtiParseValue( TRUE );

        if(!(temp = token.match(re_num)).isNull())
        {
            iValue = atoi(temp.data());
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

        if(!(token = CmdStr.match(" period +[0-9]+")).isNull())
        {
            if(!(temp = token.match(re_num)).isNull())
            {
                INT _num = atoi(temp.data());
                _cmd["cycle_period"] = CtiParseValue( _num );
            }
        }

        if(!(token = CmdStr.match(" count +[0-9]+")).isNull())
        {
            if(!(temp = token.match(re_num)).isNull())
            {
                INT _num = atoi(temp.data());
                _cmd["cycle_count"] = CtiParseValue( _num );
            }
        }

        if(!(temp = CmdStr.match("ctrl +temp +[0-9]+")).isNull())
        {
            if(!(valStr = temp.match("[0-9]+")).isNull())
            {
                iValue = atoi(valStr.data());
                _cmd["xcctrltemp"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" limit +[0-9]+")).isNull())
        {
            if(!(valStr = temp.match("[0-9]+")).isNull())
            {
                iValue = atoi(valStr.data());
                _cmd["xclimittemp"] = CtiParseValue( iValue );
            }

            // We need to look for the fallback %
            if(!(temp = CmdStr.match(" afallback +[0-9]+")).isNull())
            {
                if(!(valStr = temp.match("[0-9]+")).isNull())
                {
                    iValue = atoi(valStr.data());
                    _cmd["xclimitfbp"] = CtiParseValue( iValue );
                }
            }
        }

        if(!(temp = CmdStr.match(" maxrate +[0-9]+")).isNull())
        {
            if(!(valStr = temp.match("[0-9]+")).isNull())
            {
                iValue = atoi(valStr.data());
                _cmd["xcmaxdperh"] = CtiParseValue( iValue );
            }

            // We need to look for the fallback %
            if(!(temp = CmdStr.match(" bfallback +[0-9]+")).isNull())
            {
                if(!(valStr = temp.match("[0-9]+")).isNull())
                {
                    iValue = atoi(valStr.data());
                    _cmd["xcmaxdperhfbp"] = CtiParseValue( iValue );
                }
            }
        }
    }
    else if(CmdStr.contains(" setpoint"))
    {
        _cmd["xcsetpoint"] = CtiParseValue( TRUE );

        if(!(CmdStr.match(" hold")).isNull())
        {
            _cmd["xcholdtemp"] = CtiParseValue( TRUE );
        }

        if(!(temp = CmdStr.match(" min +[0-9]+")).isNull())
        {
            if(!(valStr = temp.match("[0-9]+")).isNull())
            {
                iValue = atoi(valStr.data());
                _cmd["xcmintemp"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" max +[0-9]+")).isNull())
        {
            if(!(valStr = temp.match("[0-9]+")).isNull())
            {
                iValue = atoi(valStr.data());
                _cmd["xcmaxtemp"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" tr +[0-9]+")).isNull())
        {
            if(!(valStr = temp.match("[0-9]+")).isNull())
            {
                iValue = atoi(valStr.data());
                _cmd["xctr"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" ta +[0-9]+")).isNull())
        {
            if(!(valStr = temp.match("[0-9]+")).isNull())
            {
                iValue = atoi(valStr.data());
                _cmd["xcta"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" tb +[0-9]+")).isNull() && CmdStr.contains(" dsb"))
        {
            if(!(valStr = temp.match("[0-9]+")).isNull())
            {
                iValue = atoi(valStr.data());
                _cmd["xctb"] = CtiParseValue( iValue );
            }

            if(!(temp = CmdStr.match(" dsb +-?[0-9]+")).isNull())
            {
                if(!(valStr = temp.match("-?[0-9]+")).isNull())
                {
                    iValue = atoi(valStr.data());
                    _cmd["xcdsb"] = CtiParseValue( iValue );
                }
            }
        }

        if(!(temp = CmdStr.match(" tc +[0-9]+")).isNull())
        {
            if(!(valStr = temp.match("[0-9]+")).isNull())
            {
                iValue = atoi(valStr.data());
                _cmd["xctc"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" td +[0-9]+")).isNull() && CmdStr.contains(" dsd"))
        {
            if(!(valStr = temp.match("[0-9]+")).isNull())
            {
                iValue = atoi(valStr.data());
                _cmd["xctd"] = CtiParseValue( iValue );
            }

            if(!(temp = CmdStr.match(" dsd +-?[0-9]+")).isNull())
            {
                if(!(valStr = temp.match("-?[0-9]+")).isNull())
                {
                    iValue = atoi(valStr.data());
                    _cmd["xcdsd"] = CtiParseValue( iValue );
                }
            }
        }

        if(!(temp = CmdStr.match(" te +[0-9]+")).isNull())
        {
            if(!(valStr = temp.match("[0-9]+")).isNull())
            {
                iValue = atoi(valStr.data());
                _cmd["xcte"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" tf +[0-9]+")).isNull() && CmdStr.contains(" dsf"))
        {
            if(!(valStr = temp.match("[0-9]+")).isNull())
            {
                iValue = atoi(valStr.data());
                _cmd["xctf"] = CtiParseValue( iValue );
            }

            if(!(temp = CmdStr.match(" dsf +-?[0-9]+")).isNull())
            {
                if(!(valStr = temp.match("-?[0-9]+")).isNull())
                {
                    iValue = atoi(valStr.data());
                    _cmd["xcdsf"] = CtiParseValue( iValue );
                }
            }
        }
    }
}


void  CtiCommandParser::doParsePutConfigExpresscom(const RWCString &CmdStr)
{
    CHAR *p;
    INT         _num;
    UINT        flag   = 0;
    UINT        offset = 0;
    UINT        iValue = 0;
    DOUBLE      dValue = 0.0;
    CHAR        tbuf[80];

    RWCString   str;
    RWCString   temp;
    RWCString   valStr;
    RWCString   token;

    RWCTokenizer   tok(CmdStr);

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

        if(!(temp = CmdStr.match(" fan +((on)|(off)|(auto))")).isNull())
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

        if(!(temp = CmdStr.match(" system +((auto)|(off)|(heat)|(cool)|(emheat))")).isNull())
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

        if(!(temp = CmdStr.match(" temp +[0-9]+")).isNull())
        {
            if(!(valStr = temp.match("[0-9]+")).isNull())
            {
                iValue = atoi(valStr.data());
                _cmd["xcsettemp"] = CtiParseValue( iValue );
            }
        }

        if(!(temp = CmdStr.match(" timeout +[0-9]+")).isNull())         // assume minutes input.
        {
            if(!(valStr = temp.match("[0-9]+")).isNull())
            {
                iValue = atoi(valStr.data());
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


        if(!(temp = CmdStr.match(" [0-9]?[0-9]:")).isNull())
        {
            int hh = atoi(temp.data());
            _cmd["xctimesync_hour"] = CtiParseValue( hh );

            if(!(temp = CmdStr.match(":[0-9][0-9]")).isNull())
            {
                int mm = atoi(temp.data() + 1);
                _cmd["xctimesync_minute"] = CtiParseValue( mm );
            }
        }

    }

    if(!(token = CmdStr.match(" raw( +(0x)?[0-9a-f]+)+")).isNull())
    {
        if(!(str = token.match("( +(0x)?[0-9a-f]+)+")).isNull())
        {
            _cmd["xcrawconfig"] = CtiParseValue( str );
        }
    }

    if(!(token = CmdStr.match(" data( +(0x)?[0-9a-f]+)+")).isNull())
    {
        token.replace(" data", "");
        if(!(str = token.match("( +(0x)?[0-9a-f][0-9a-f])+")).isNull())
        {
            _cmd["xcdata"] = CtiParseValue( str );
        }
    }

    if(!(token = CmdStr.match("main(tenance)?( +(0x)?[0-9a-f]+)+")).isNull())
    {
        // Translates to a maintenance function
        if(!(str = token.match("( +(0x)?[0-9a-f]+)+")).isNull())
        {
            _cmd["xcrawmaint"] = CtiParseValue( str );
        }
    }

    if(!(token = CmdStr.match("((assign)|(address))")).isNull())
    {
        {
            _cmd["xcaddress"] = TRUE;

            if(!(valStr = CmdStr.match(" s[ =]*(0x)?[0-9a-f]+")).isNull())
            {
                _num = strtol(valStr.match(re_anynum).data(), &p, 0);

                _cmd["xca_spid"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG SPID = %d", _num);
                _actionItems.insert(tbuf);
            }
            if(!(valStr = CmdStr.match(" +g[ =]*[0-9]+")).isNull())
            {
                _num = strtol(valStr.match(re_num).data(), &p, 10);
                _cmd["xca_geo"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG GEO = %d", _num);
                _actionItems.insert(tbuf);
            }
            if(!(valStr = CmdStr.match(" +b[ =]*[0-9]+")).isNull())
            {
                _num = strtol(valStr.match(re_num).data(), &p, 10);
                _cmd["xca_sub"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG SUBSTATION = %d", _num);
                _actionItems.insert(tbuf);
            }
            if(!(valStr = CmdStr.match(" +f[ =]*[0-9]+")).isNull())
            {
                _num = strtol(valStr.match(re_num).data(), &p, 10);
                _cmd["xca_feeder"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG FEEDER = %d", _num);
                _actionItems.insert(tbuf);
            }
            if(!(valStr = CmdStr.match(" +z[ =]*[0-9]+")).isNull())
            {
                _num = strtol(valStr.match(re_num).data(), &p, 10);
                _cmd["xca_zip"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG ZIP = %d", _num);
                _actionItems.insert(tbuf);
            }
            if(!(valStr = CmdStr.match(" +u[ =]*[0-9]+")).isNull())
            {
                _num = strtol(valStr.match(re_num).data(), &p, 10);
                _cmd["xca_uda"] = CtiParseValue( _num );

                _snprintf(tbuf, sizeof(tbuf), "CONFIG UDA = %d", _num);
                _actionItems.insert(tbuf);
            }

            RWCString programtemp;
            RWCString splintertemp;

            if(!(valStr = CmdStr.match(" +p[ =]*[0-9]+( *, *[0-9]+)*")).isNull())
            {
                valStr.replace(RWCRExpr(" +p[ =]*"),"");
                _cmd["xca_program"] = CtiParseValue( valStr );
                programtemp = valStr;
            }
            if(!(valStr = CmdStr.match(" +r[ =]*[0-9]+( *, *[0-9]+)*")).isNull())
            {
                valStr.replace(RWCRExpr(" +r[ =]*"),"");
                _cmd["xca_splinter"] = CtiParseValue( valStr );

                splintertemp = valStr;
            }

            if(!(token = CmdStr.match("((relay)|(load)) +[0-9]+( *, *[0-9]+)*")).isNull())
            {
                INT i;
                INT mask = 0;
                RWTokenizer ptok(programtemp);
                RWTokenizer rtok(splintertemp);
                RWCString tempstr;
                RWCString ptemp;
                RWCString rtemp;

                for(i = 0; i < 15; i++)
                {
                    RWCString numstr = CtiNumStr(i+1);
                    if(!(temp = token.match(numstr)).isNull())
                    {
                        mask |= (0x01 << i);

                        {
                            tempstr = ptok(",");
                            if(!tempstr.isNull()) ptemp = tempstr;

                            tempstr = rtok(",");
                            if(!tempstr.isNull()) rtemp = tempstr;

                            if(!ptemp.isNull())
                            {
                                _snprintf(tbuf, sizeof(tbuf), "CONFIG LOAD %d to PROGRAM = %s", i+1, ptemp);
                                _actionItems.insert(tbuf);
                            }

                            if(!rtemp.isNull())
                            {
                                _snprintf(tbuf, sizeof(tbuf), "CONFIG LOAD %d to SPLINTER = %s", i+1, rtemp);
                                _actionItems.insert(tbuf);
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
        if(CmdStr.contains("temp") && !(token = CmdStr.match("serv(ice)? +((in)|(out)|(enable)|(disable))")).isNull())
        {
            CHAR tbuf[80];
            INT offtime = 0;
            INT cancel = 0;  // default to cancelling a previous t-service message.
            INT bitP = 0;    // default to using cold load
            INT bitL = 0;    // default to using the LEDs

            if(!(str = token.match(" out")).isNull() || !(str = token.match(" disable")).isNull())
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

            if(!(token = CmdStr.match(" offhours +[0-9]+")).isNull())
            {
                str = token.match("[0-9]+");
                offtime = atoi(str.data());
            }

            _cmd["xctservicebitp"] = CtiParseValue( bitP );
            _cmd["xctservicebitl"] = CtiParseValue( bitL );
            _cmd["xctservicetime"] = CtiParseValue( offtime );
            _cmd["xctservicecancel"] = CtiParseValue( cancel );
            _actionItems.insert(tbuf);
        }
        else if(!(token = CmdStr.match("serv(ice)? +((in)|(out)|(enable)|(disable))( +((relay)|(load))? +[0-9]+)?")).isNull())
        {
            CHAR tbuf[80];
            INT flag = 0;

            if(!(str = token.match(" in")).isNull() || !(str = token.match(" enable")).isNull())
            {
                flag |= 0x80;
                _snprintf(tbuf, sizeof(tbuf), "SERVICE ENABLE");
            }
            else if(!(str = token.match(" out")).isNull() || !(str = token.match(" disable")).isNull())
            {
                flag |= 0x00;
                _snprintf(tbuf, sizeof(tbuf), "SERVICE DISABLE");
            }

            if(!(str = token.match("((relay)|(load)) +[0-9]+)?")).isNull())
            {
                RWCTokenizer   tok2(str);
                tok2();  // hop over relay | load.
                str = tok2();
                INT load = atoi(str.data());
                flag |= (load & 0x0f);
            }

            _cmd["xcpservice"] = CtiParseValue( flag );
            _actionItems.insert(tbuf);
        }
    }
    else if(CmdStr.contains("schedule"))
    {
        _cmd["xcschedule"] = TRUE;
        doParsePutConfigThermostatSchedule(CmdStr);
    }
}

void  CtiCommandParser::doParsePutStatusExpresscom(const RWCString &CmdStr)
{
    INT         _num;
    CHAR        tbuf[80];
    UINT        flag   = 0;
    UINT        offset = 0;
    UINT        iValue = 0;
    DOUBLE      dValue = 0.0;

    RWCString   str;
    RWCString   temp;
    RWCString   valStr;
    RWCString   token;

    RWCTokenizer   tok(CmdStr);

    token = tok(); // Get the first one into the hopper....

    if(!(token = CmdStr.match(" prop[a-z]*[ =]+((disp[a-z]*)|(inc[a-z]*)|(term[a-z]*)|(rssi)|(ping))")).isNull())
    {
        int   op = -1;
        CHAR  op_name[20];

        if(!(token.match("disp(lay)?")).isNull())                   // Turn on light & bump counter.
        {
            op = 0x002;
            _snprintf(op_name, sizeof(op_name), "DISPLAY");
        }
        else if(!(token.match("inc(rement)?")).isNull())            // bump counter.
        {
            op = 0x001;
            _snprintf(op_name, sizeof(op_name), "INCREMENT");
        }
        else if(!(token.match("term(inate)?")).isNull())            // Turn off light.
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
            _actionItems.insert(tbuf);
        }
    }
    else if(!(token = CmdStr.match(" ovuv[ =]+((ena(ble)?)|(dis(able)?))")).isNull())
    {
        int   op = 0;
        CHAR  op_name[20];

        if(!(token.match("ena")).isNull())
        {
            op = 1;
            _snprintf(op_name, sizeof(op_name), "ENABLE");
        }
        else if(!(token.match("dis")).isNull())
        {
            op = 0;
            _snprintf(op_name, sizeof(op_name), "DISABLE");
        }

        _cmd["ovuv"] = CtiParseValue( op );

        _snprintf(tbuf, sizeof(tbuf), "OVUV %s", op_name);
        _actionItems.insert(tbuf);
    }
}

void CtiCommandParser::doParsePutConfigThermostatSchedule(const RWCString &CmdStr)
{
    RWCString   str;
    RWCString   temp;
    RWCString   valStr;
    RWCString   token;

    RWCTokenizer   tok(CmdStr);

    {
        INT key;
        bool inc = true;

        token = tok(" ,");              // Prime it up.. Shoud be a putconfig.

        while(!token.isNull())
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

INT CtiCommandParser::isTokenThermostatScheduleDOW(RWCString &token)
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

void CtiCommandParser::doParsePutConfigThermostatScheduleDOW(RWTokenizer &tok, INT &key)
{
    RWCString token;
    INT currentkey = key;   // The key which got us here.
    int pod = 0;            // Period of the day on which we begin!.
    int component = 0;
    BYTE hh = 0xff;
    BYTE mm = 0xff;
    BYTE heat = 0xff;
    BYTE cool = 0xff;

    while( !(token = tok(" :,")).isNull()  )
    {
        if((key = isTokenThermostatScheduleDOW(token)) >= 0)
        {
            // The current token is a NEW key!
            break;
        }

        switch(component)
        {
        case 0:
            {
                // This is the hh section of the time
                if(!token.match("[0-9]+").isNull())
                    hh = atoi(token.data());
                else if(!token.match("hh").isNull())
                    hh = 254;                           // This is the Energy Pro period cancel indicator.
                break;
            }
        case 1:
            {
                // This is the mm section of the time
                if(!token.match("[0-9]+").isNull())
                    mm = atoi(token.data());
                break;
            }
        case 2:
            {
                // This is the heat temperature section
                if(!token.match("[0-9]+").isNull())
                    heat = atoi(token.data());
                break;
            }
        case 3:
            {
                // This is the cool temperature section
                if(!token.match("[0-9]+").isNull())
                    cool = atoi(token.data());
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

            RWCString hhstr("xctodshh_" + CtiNumStr(per));
            RWCString mmstr("xctodsmm_" + CtiNumStr(per));
            RWCString heatstr("xctodsheat_" + CtiNumStr(per));
            RWCString coolstr("xctodscool_" + CtiNumStr(per));

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

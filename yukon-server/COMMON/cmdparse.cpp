
#pragma warning( disable : 4786)

#include <windows.h>
#include <stdlib.h>
#include <iostream>
using namespace std;

#include <rw\rwtime.h>
#include <rw\cstring.h>
#include <rw\ctoken.h>
#include <rw\re.h>

#include <limits.h>
#include "yukon.h"
#include "cmdparse.h"
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
  doParse(_cmdString);
}

CtiCommandParser::CtiCommandParser(const CtiCommandParser& aRef)
{
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

    _cmdString = Cmd;       // OK already, now we are in business.

    CHAR              *p;
    const RWCString   CmdStr = Cmd;
    RWCString         temp = Cmd;
    RWCString         token;
    RWCString         cmdstr;
    RWCString         strnum;

    INT               _num = 0;


    RWCTokenizer      tok(temp);

    if(!(token = CmdStr.match("seri")).isNull())
    {
        if(!(token = CmdStr.match("seri[a-z]*[= \t]+(([0-9]+)|(0x[0-9a-f]+))")).isNull())
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
        }
    }
    else if(!(token = CmdStr.match("addr")).isNull())
    {
        if(!(token = CmdStr.match("addr[a-z]*[= \t]+[0-9a-f]+")).isNull())
        {
            if(!(strnum = token.match(re_hexnum)).isNull())
            {
                _num = strtol(strnum.data(), &p, 16);
            }
            else if(!(strnum = token.match(re_num)).isNull())
            {
                // dout << __LINE__ << " " << strnum << endl;
                _num = strtol(strnum.data(), &p, 10);
            }
            _cmd["serial"] = CtiParseValue( _num );
        }
    }


    if(!(token = CmdStr.match("select")).isNull())
    {
        if(!(token = CmdStr.match("select[ \t]+name[ \t]+((\"|')[^\"']+(\"|'))")).isNull())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("name[ \t]+", &nstop);

            nstop += nstart;

            if(!(token = token.match("(\"|')[^\"']+(\"|')", nstop)).isNull())   // get the value
            {
                token = token((size_t)1, (size_t)token.length() - 2);
                _cmd["device"] = CtiParseValue( token, -1 );
            }
        }
        else if(!(token = CmdStr.match("select[ \t]+id[ \t][0-9]+")).isNull())
        {
            RWCTokenizer ntok(token);
            ntok();  // pull the select keyword
            ntok();  // pull the id keyword
            if(!(token = ntok()).isNull())   // get the value
            {
                _cmd["device"] = CtiParseValue( atoi(token.data()) );
            }
        }
        else if(!(token = CmdStr.match("select[ \t]+group[ \t]+((\"|')[^\"']+(\"|'))")).isNull())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("group[ \t]+", &nstop);

            nstop += nstart;

            if(!(token = token.match("(\"|')[^\"']+(\"|')", nstop)).isNull())   // get the value
            {
                token = token((size_t)1, (size_t)token.length() - 2);
                _cmd["group"] = CtiParseValue( token, -1 );
            }
        }
        else if(!(token = CmdStr.match("select[ \t]+altgroup[ \t]+((\"|')[^\"']+(\"|'))")).isNull())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("altgroup[ \t]+", &nstop);

            nstop += nstart;

            if(!(token = token.match("(\"|')[^\"']+(\"|')", nstop)).isNull())   // get the value
            {
                token = token((size_t)1, (size_t)token.length() - 2);
                _cmd["altgroup"] = CtiParseValue( token, -1 );
            }
        }
        else if(!(token = CmdStr.match("select[ \t]+route[ \t]+name[ \t]+((\"|')[^\"']+(\"|'))")).isNull())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("name[ \t]+", &nstop);

            nstop += nstart;

            if(!(token = token.match("(\"|')[^\"']+(\"|')", nstop)).isNull())   // get the value
            {
                token = token((size_t)1, (size_t)token.length() - 2);
                _cmd["route"] = CtiParseValue( token, -1 );
            }
        }
        else if(!(token = CmdStr.match("select[ \t]+route[ \t]+id[ \t][0-9]+")).isNull())
        {
            RWCTokenizer ntok(token);
            ntok();  //  pull the select keyword
            ntok();  //  pull the route keyword
            ntok();  //  pull the id keyword
            if(!(token = ntok()).isNull())   // get the value
            {
                _cmd["route"] = CtiParseValue( atoi(token.data()) );
            }
        }
        else if(!(token = CmdStr.match("select[ \t]+point[ \t]+name[ \t]+((\"|')[^\"']+(\"|'))")).isNull())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("name[ \t]+", &nstop);

            nstop += nstart;

            if(!(token = token.match("(\"|')[^\"']+(\"|')", nstop)).isNull())   // get the value
            {
                token = token((size_t)1, (size_t)token.length() - 2);
                _cmd["point"] = CtiParseValue( token, -1 );
            }
        }
        else if( !(token = CmdStr.match("select[ \t]+point[ \t]*id[ \t]+[0-9]+")).isNull())
        {
            RWCTokenizer ntok(token);
            ntok();  // pull the select keyword
            RWCString str = ntok();  // pull the pointid keyword
            if(str != RWCString("pointid"))  ntok();     // Grab one more then

            if(!(token = ntok()).isNull())   // get the value
            {
                _cmd["point"] = CtiParseValue( atoi(token.data()) );
            }
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

    resolveProtocolType();


    if(!(CmdStr.match("noqueue")).isNull())
    {
        _cmd["noqueue"] = CtiParseValue("true");
    }

    if(!(cmdstr = tok()).isNull())
    {
        cmdstr.toLower();

        if(cmdstr == "getvalue")
        {
            _cmd["command"] = CtiParseValue( cmdstr, GetValueRequest );
            doParseGetValue();
        }
        else if(cmdstr == "putvalue")
        {
            _cmd["command"] = CtiParseValue( cmdstr, PutValueRequest );
            doParsePutValue();
        }
        else if(cmdstr == "getstatus")
        {
            _cmd["command"] = CtiParseValue( cmdstr, GetStatusRequest );
            doParseGetStatus();
        }
        else if(cmdstr == "putstatus")
        {
            _cmd["command"] = CtiParseValue( cmdstr, PutStatusRequest );
            doParsePutStatus();
        }
        else if(cmdstr == "getconfig")
        {
            _cmd["command"] = CtiParseValue( cmdstr, GetConfigRequest );
            doParseGetConfig();
        }
        else if(cmdstr == "putconfig")
        {
            _cmd["command"] = CtiParseValue( cmdstr, PutConfigRequest );
            doParsePutConfig();
        }
        else if(cmdstr == "loop")
        {
            _cmd["command"] = CtiParseValue( cmdstr, LoopbackRequest );
            _cmd["count"] = CtiParseValue( 1 );

            if(!(cmdstr = tok()).isNull())
            {
                if( !(cmdstr.match(re_num).isNull()) )
                {
                    _cmd["count"] = CtiParseValue( atoi(cmdstr.data()) );
                }
            }
        }
        else if(cmdstr == "control")
        {
            _cmd["command"] = CtiParseValue( cmdstr, ControlRequest );
            doParseControl();
        }
        else if(cmdstr == "scan")
        {
            _cmd["command"] = CtiParseValue( cmdstr, ScanRequest );
            doParseScan();
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

void  CtiCommandParser::doParseGetValue(void)
{
    UINT        flag = 0;
    UINT        offset = 0;

    RWCString   chck = _cmdString;
    RWCString   temp2;
    RWCString   token;

    chck.toLower();

    RWCString   CmdStr = chck;

    RWCRExpr   re_kxx  ("(kwh|kvah|kvarh)[abcdt]?");  //  Match on kwh, kwha,b,c,d,t
    RWCRExpr   re_hrate("h[abcdt]?");                 //  Match on h,ha,hb,hc,hd,ht
    RWCRExpr   re_rate ("rate[ \t]*[abcdt]");
    RWCRExpr   re_kwh  ("kwh");
    RWCRExpr   re_kvah ("kvah");
    RWCRExpr   re_kvarh("kvarh");

    RWCRExpr   re_demand("dema|kw([\t ]|$)|kvar([\t ]|$)|kva([\t ]|$)");  //  match "dema"nd, but also match "kw", "kvar", or "kva"
    RWCRExpr   re_frozen("froz");                                         //     at the end of the string or with whitespace following
    RWCRExpr   re_peak  ("peak");

    RWCRExpr   re_powerfail("power");

    RWCRExpr   re_update("upd");

    RWCRExpr   re_offset("off[a-z]*[ \t]*[0-9]+");


    RWCTokenizer   tok(chck);

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


void  CtiCommandParser::doParseGetStatus(void)
{
    UINT        flag = 0;
    UINT        offset = 0;

    RWCString   chck = _cmdString;
    RWCString   temp2;
    RWCString   token;

    chck.toLower();

    const RWCString   CmdStr = chck;

    RWCRExpr   re_lp("lp");
    RWCRExpr   re_disc("disc");
    RWCRExpr   re_erro("err");
    RWCRExpr   re_pfco("powerf");
    RWCRExpr   re_intern("inter");
    RWCRExpr   re_extern("extern");

    RWCRExpr   re_offset("off[a-z]*[ \t]*[0-9]+");

    RWCRExpr   re_frozen("froz");
    RWCRExpr   re_update("upd");
    RWCRExpr   re_iedlink("ied[ \t]+link");

    RWCRExpr   re_sele("select");

    RWCTokenizer   tok(chck);

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

    }
    else
    {
        // Something went WAY wrong....

        dout << "This better not ever be seen by mortals... " << endl;
    }

    _cmd["flag"]      = CtiParseValue( flag   );
    _cmd["offset"]    = CtiParseValue( offset );
}

void  CtiCommandParser::doParseControl(void)
{
    INT         _num;
    UINT        flag   = 0;
    UINT        offset = 0;
    UINT        iValue = 0;
    DOUBLE      dValue = 0.0;

    CHAR        tbuf[80];
    CHAR        tbuf2[80];

    RWCString   chck = _cmdString;
    RWCString   temp2;
    RWCString   token;

    chck.toLower();

    const RWCString   CmdStr = chck;

    RWCRExpr   re_open("open");
    RWCRExpr   re_clos("clos");
    RWCRExpr   re_conn("conn");
    RWCRExpr   re_disc("disc");
    RWCRExpr   re_rest("rest");
    RWCRExpr   re_term("term");

    RWCRExpr   re_scale("[hs]");
    RWCRExpr   re_offs("off[a-z]*[ \t]*[0-9]+");

    RWCRExpr   re_frozen("froz");
    RWCRExpr   re_update("upd");

    RWCTokenizer   tok(chck);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "control")
    {

        if(!(token = CmdStr.match(re_open)).isNull())            // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_OPEN;

            _snprintf(tbuf, sizeof(tbuf), "OPEN");
        }
        else if(!(token = CmdStr.match(re_clos)).isNull())       // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_CLOSE;
            _snprintf(tbuf, sizeof(tbuf), "CLOSE");
        }
        else if(!(token = CmdStr.match(re_disc)).isNull())       // Sourcing from CmdStr, which is the entire command string.
        {
            /* MUST LOOK FOR THIS FIRST! */
            flag |= CMD_FLAG_CTL_DISCONNECT;
            _snprintf(tbuf, sizeof(tbuf), "DISCONNECT");
        }
        else if(!(token = CmdStr.match(re_conn)).isNull())       // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_CONNECT;
            _snprintf(tbuf, sizeof(tbuf), "CONNECT");
        }
        else if(!(token = CmdStr.match(re_rest)).isNull())       // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_RESTORE;
            _snprintf(tbuf, sizeof(tbuf), "RESTORE");
        }
        else if(!(token = CmdStr.match(re_term)).isNull())       // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_TERMINATE;
            _snprintf(tbuf, sizeof(tbuf), "TERMINATE");
        }
        else if(!(CmdStr.match("shed")).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_CTL_SHED;

            if(!(token = CmdStr.match("ed[ \t]*[0-9]+(\\.[0-9]+)?[ \t]*[hms]?")).isNull())      // Sourcing from CmdStr, which is the entire command string.
            {
                DOUBLE mult = 60.0;

                // What shed time is needed now...
                if(!(temp2 = token.match("[0-9]+(\\.[0-9]+)?")).isNull())
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

                if(!(temp2 = token.match(re_scale)).isNull())
                {
                    /*
                     *  Minutes is the assumed entry format, but we return the number in seconds... so
                     */
                    if(temp2.data()[0] == 'h')
                    {
                        mult = 3600.0;
                        _snprintf(tbuf, sizeof(tbuf), "%sH", tbuf2);
                    }
                    else if(temp2.data()[0] == 's')
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

            if(!(token = CmdStr.match("rand[a-z]*[ \t]*[0-9]+")).isNull())
            {
                if(!(temp2 = token.match(re_num)).isNull())
                {
                    INT _num = atoi(temp2.data());
                    _cmd["shed_rand"] = CtiParseValue( _num );
                }
            }

            if(!(token = CmdStr.match("delay[ \t]*[0-9][0-9]:[0-9][0-9]")).isNull())
            {
                INT hh = 0;
                INT mm = 0;
                INT ofm = 0;      // Offset from Midnight in seconds.

                if(!(temp2 = token.match("[0-9][0-9]:")).isNull())
                {
                    hh = atoi(temp2.data());
                }

                if(!(temp2 = token.match(":[0-9][0-9]")).isNull())
                {
                    mm = atoi(temp2.data() + 1);
                }


                _num = RWTime(hh, mm).seconds() - RWTime().seconds();

                if(_num > 0)
                {
                    _cmd["shed_delay"] = CtiParseValue( _num );
                }
            }

        }
        else if(!(token = CmdStr.match("cycle[ \t]*[0-9]+")).isNull())      // Sourcing from CmdStr, which is the entire command string.
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

            if(!(token = CmdStr.match("period[ \t]*[0-9]+")).isNull())
            {
                if(!(temp2 = token.match(re_num)).isNull())
                {
                    INT _num = atoi(temp2.data());
                    _cmd["cycle_period"] = CtiParseValue( _num );
                }
            }

            if(!(token = CmdStr.match("count[ \t]*[0-9]+")).isNull())
            {
                if(!(temp2 = token.match(re_num)).isNull())
                {
                    INT _num = atoi(temp2.data());
                    _cmd["cycle_count"] = CtiParseValue( _num );
                }
            }

            if(!(token = CmdStr.match("delay[ \t]*[0-9][0-9]:[0-9][0-9]")).isNull())
            {
                INT hh = 0;
                INT mm = 0;
                INT ofm = 0;      // Offset from Midnight in seconds.

                if(!(temp2 = token.match("[0-9][0-9]:")).isNull())
                {
                    hh = atoi(temp2.data());
                }

                if(!(temp2 = token.match(":[0-9][0-9]")).isNull())
                {
                    mm = atoi(temp2.data() + 1);
                }


                _num = RWTime(hh, mm).seconds() - RWTime().seconds();

                if(_num > 0)
                {
                    _cmd["cycle_delay"] = CtiParseValue( _num );
                }
            }


            _cmd["cycle"] = CtiParseValue( (iValue) );
            _snprintf(tbuf, sizeof(tbuf), "CYCLE %d%%", iValue);
        }

        if(flag) _actionItems.insert(tbuf);                      // If anything was set, make sure someone can be informed

        if(!(token = CmdStr.match(re_offs)).isNull())            // Sourcing from CmdStr, which is the entire command string.
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
        if(!(token = CmdStr.match("relay")).isNull())
        {
            //if(!(token = CmdStr.match("relay[ \t,0-9]+")).isNull())
            if(!(token = CmdStr.match("relay[ \t]+[0-9]+([ \t]*,[ \t]*[0-9]+)*")).isNull())
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
            if(!(token = CmdStr.match("relay[ \t]+next")).isNull())
            {
                _cmd["relaynext"] = CtiParseValue( TRUE );
            }
        }

        if(!(token = CmdStr.match(re_frozen)).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            flag |= CMD_FLAG_FROZEN;
        }
        if(!(token = CmdStr.match(re_update)).isNull())      // Sourcing from CmdStr, which is the entire command string.
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
}


void  CtiCommandParser::doParsePutValue(void)
{
    UINT        flag = 0;
    UINT        offset = 0;
    double      dial = 0;

    RWCString   chck = _cmdString;
    RWCString   temp2;
    RWCString   token;

    char *p;

    chck.toLower();

    RWCString  CmdStr = chck;

    RWCRExpr   re_reset("reset");
    RWCRExpr   re_reading("reading[ \t]*[0-9]+(\\.[0-9]*)?");
    RWCRExpr   re_numfloat("[0-9]+(\\.[0-9]*)?");
    RWCRExpr   re_kyzoffset("kyz[ \t]*[123]");  //  if there's an offset specified

    RWCTokenizer   tok(chck);

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


void  CtiCommandParser::doParsePutStatus(void)
{
    RWCString   chck = _cmdString;
    RWCString   temp2;
    RWCString   token;
    unsigned int flag;

    chck.toLower();

    const RWCString   CmdStr = chck;    // A lowercase version of the input commnad string...

    RWCTokenizer   tok(chck);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "putstatus")
    {
        INT type = getiValue("type");

        switch( type )
        {
            case ProtocolVersacomType:
            {
                doParsePutStatusVersacom();
                break;
            }
            case ProtocolFisherPierceType:
            {
                doParsePutStatusFisherP();
                break;
            }
            case ProtocolEmetconType:
            {
                doParsePutStatusEmetcon();
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

        if( _cmd.contains("flag") )
            flag = _cmd["flag"].getInt();

        if(!(token = CmdStr.match("reset")).isNull())
        {
            flag |= CMD_FLAG_PS_RESET;
        }
        _cmd["flag"] = CtiParseValue(flag);
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

void  CtiCommandParser::doParseGetConfig(void)
{
    RWCString   chck = _cmdString;
    RWCString   temp2;
    RWCString   token;
    RWCRExpr    rolenum("role[ \t]*[0-9][0-9]?");
    RWCRExpr    rawcmd("raw[ \t]+(func[a-z]*[ \t]+)?start=0x[0-9a-f]+([ \t]+[0-9]+)?");
    RWCRExpr    interval("interval[ \t]+(lp|li)");  //  match "interval lp" and "interval li"
    RWCRExpr    multiplier("mult.*[ \t]*(kyz[ \t]*[123])?");

    char *p;

    int roleNum, channel;

    chck.toLower();

    const RWCString   CmdStr = chck;    // A lowercase version of the input commnad string...

    RWCTokenizer   tok(chck);

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

void  CtiCommandParser::doParsePutConfig(void)
{
    RWCString   chck = _cmdString;
    RWCString   temp2;
    RWCString   token;

    chck.toLower();

    const RWCString   CmdStr = chck;    // A lowercase version of the input commnad string...

    RWCTokenizer   tok(chck);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "putconfig")
    {
        INT type = getiValue("type");

        switch( type )
        {
        case ProtocolVersacomType:
            {
                doParsePutConfigVersacom();
                break;
            }
        case ProtocolFisherPierceType:
        case ProtocolSA105Type:
        case ProtocolSA205Type:
        case ProtocolSA305Type:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Putconfig not supported for this device type " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        case ProtocolEmetconType:
            {
                doParsePutConfigEmetcon();
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

void  CtiCommandParser::doParseScan(void)
{

    RWCString   chck = _cmdString;
    RWCString   token;

    chck.toLower();

    RWCString   CmdStr = chck;
    RWCTokenizer   tok(chck);

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
        else if(!(token = CmdStr.match("loadprofile")).isNull())      // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["scantype"] = CtiParseValue( ScanRateLoadProfile );
        }
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
    CtiParseValue& pv = CtiParseValue(); // = _cmd["command"];

    _cmd.findValue("flag", pv);

    return pv.getInt();
}

UINT     CtiCommandParser::getOffset() const
{
    CtiParseValue& pv = CtiParseValue(); // = _cmd["command"];

    _cmd.findValue("offset", pv);

    return pv.getInt();
}

UINT     CtiCommandParser::isKeyValid(const RWCString key) const
{
    return _cmd.contains(key);
}

UINT     CtiCommandParser::getOffset(const RWCString key) const
{
    CtiParseValue& pv = CtiParseValue(); // = _cmd["command"];
    _cmd.findValue(key, pv);

    return pv.getInt();
}
INT      CtiCommandParser::getiValue(const RWCString key) const
{
    CtiParseValue& pv = CtiParseValue(); // = _cmd["command"];
    _cmd.findValue(key, pv);

    return pv.getInt();
}

DOUBLE   CtiCommandParser::getdValue(const RWCString key) const
{
    CtiParseValue& pv = CtiParseValue(); // = _cmd["command"];
    _cmd.findValue(key, pv);

    return pv.getReal();
}

RWCString CtiCommandParser::getsValue(const RWCString key) const
{
    CtiParseValue& pv = CtiParseValue();
    _cmd.findValue(key, pv);

    return pv.getString();
}

void  CtiCommandParser::doParsePutConfigEmetcon(void)
{
    RWCString   chck = _cmdString;
    RWCString   temp2;
    RWCString   token;
    RWCRExpr    rawcmd("raw[ \t]+(func[a-z]*[ \t]+)?start=0x[0-9a-f]+([ \t]+0x[0-9a-f]+)*");
    RWCRExpr    rolecmd("role[ \t]+[0-9]+" \
                            "[ \t]+[0-9]+" \
                            "[ \t]+[0-9]+" \
                            "[ \t]+[0-9]+" \
                            "[ \t]+[0-9]+");
    RWCRExpr    interval("interval[ \t]+l[pi]");  //  match "interval lp" and "interval li"
    RWCRExpr    multiplier("mult[a-z]*[ \t]+kyz[ \t]*[123][ \t][0-9]+(\\.[0-9]+)?");  //  match "mult kyz # #(.###)
    RWCRExpr    iedClass("ied[ \t]+class[ \t][0-9]+[ \t]+[0-9]+");
    RWCRExpr    iedScan("ied[ \t]+scan[ \t][0-9]+[ \t]+[0-9]+");

    char *p;

    chck.toLower();

    const RWCString   CmdStr = chck;    // A lowercase version of the input command string...

    RWCTokenizer   tok(chck);

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
        if(!(CmdStr.match("time")).isNull())
        {
            _cmd["timesync"] = CtiParseValue("TRUE");
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
            if(!(token = CmdStr.match("mrole([ \t]+[0-9]+)+")).isNull())
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

void  CtiCommandParser::doParsePutConfigVersacom(void)
{
    char *p;

    char        tbuf[60];

    RWCString   token;
    RWCString   temp = _cmdString;
    RWCString   temp2;
    RWCString   strnum;

    INT         _num = 0;

    temp.toLower();

    const RWCString   CmdStr = temp;    // A lowercase version of the input commnad string...

    RWCTokenizer   tok(temp);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "putconfig")
    {
        if(!(token = CmdStr.match("vdata([ \t]+(0x)?[0-9a-f])+")).isNull())
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

        if(!(token = CmdStr.match("template[ \t]+((\"|')[^\"']+(\"|'))")).isNull())
        {
            size_t nstart;
            size_t nstop;
            nstart = token.index("template[ \t]+", &nstop);

            nstop += nstart;

            if(!(token = token.match("(\"|')[^\"']+(\"|')", nstop)).isNull())   // get the template name...
            {
                token = token((size_t)1, (size_t)token.length() - 2);
                _cmd["template"] = CtiParseValue( token );
            }
        }

        if(!(token = CmdStr.match(" util[a-z]*[ \t=]*([ \t=]+0x)?[0-9a-f]+")).isNull())
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

        if(!(token = CmdStr.match(" aux*[ \t=]*([ \t=]+0x)?[0-9a-f]+")).isNull())
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

        if(!(token = CmdStr.match(" sect[a-z]*[ \t=]*[0-9]+")).isNull())
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
            if(!(token = CmdStr.match(" clas[a-z]*[ \t=]*([ \t=]+)?0x[0-9a-f]+")).isNull())
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
            else if(!(token = CmdStr.match(" clas[a-z]*[ \t]+[0-9]+" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                          )).isNull())
            {
                RWCTokenizer   tok( token );
                _num = 0;
                int  mask = 0x0001;
                int  val;

                tok();   // Get over the class entry

                // dout << __LINE__ << " " << token << endl;

                for(int i = 0; i < 16 && !(temp = tok(", \t\n\0")).isNull() ; i++)
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
            if(!(token = CmdStr.match(" divi[a-z]*[ \t=]*([ \t=]+)?0x[0-9a-f]+")).isNull())
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
            else if(!(token = CmdStr.match(" divi[a-z]*[ \t]+[0-9]+" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                           "([ \t]*,[ \t]*[0-9]+)?" \
                                          )).isNull())
            {
                RWCTokenizer   tok( token );
                _num = 0;
                int  mask = 0x0001;
                int  val;

                tok();   // Get over the "division" entry

                // dout << __LINE__ << " " << token << endl;

                for(int i = 0; i < 16 && !(temp = tok(", \t\n\0")).isNull() ; i++)
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
            if(!(token = CmdStr.match("fromutil[a-z]*[ \t=]*([ \t=]+0x)?[0-9a-f]+")).isNull())
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

            if(!(token = CmdStr.match("fromsect[a-z]*[ \t=]*([ \t=]+(0x))?[0-9a-f]+")).isNull())
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

            if(!(token = CmdStr.match("fromclas[a-z]*[ \t=]*([ \t=]+)?0x[0-9a-f]+")).isNull())
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

            if(!(token = CmdStr.match("fromdivi[a-z]*[ \t=]*([ \t=]+)?0x[0-9a-f]+")).isNull())
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
                                      "([ \t]+[uascd][ \t=]*(0x)?[0-9a-f]+)" \
                                      "([ \t]+[uascd][ \t=]*(0x)?[0-9a-f]+)?" \
                                      "([ \t]+[uascd][ \t=]*(0x)?[0-9a-f]+)?" \
                                      "([ \t]+[uascd][ \t=]*(0x)?[0-9a-f]+)?" \
                                      "([ \t]+[uascd][ \t=]*(0x)?[0-9a-f]+)?")).isNull())
            {
                // dout << token << endl;

                if(!(strnum = token.match("[ \t]u[ \t=]*(0x)?[0-9a-f]+")).isNull())
                {
                    _num = strtol(strnum.match(re_anynum).data(), &p, 0);

                    _cmd["utility"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG UTILITY = %d", _num);
                    _actionItems.insert(tbuf);
                }
                if(!(strnum = token.match("[ \t]+a[ \t=]*(0x)?[0-9a-f]+")).isNull())
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
                if(!(strnum = token.match("[ \t]+s[ \t=]*[0-9]+")).isNull())
                {
                    _num = strtol(strnum.match(re_num).data(), &p, 10);
                    _cmd["section"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG SECTION = %d", _num);
                    _actionItems.insert(tbuf);
                }
                if(!(strnum = token.match("[ \t]+c[ \t=]*(0x)?[0-9a-f]+")).isNull())
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
                if(!(strnum = token.match("[ \t]+d[ \t=]*(0x)?[0-9a-f]+")).isNull())
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
            }
        }

        if(!(token = CmdStr.match("serv")).isNull())
        {
            if(!(token = CmdStr.match("serv[a-z]*[ \t]+((in)|(out)|(enable)|(disable))[ \t]*(t(emp)?)?")).isNull())
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

                if(token[token.length() - 1] == 't' && token[token.length() - 2] != 'u') // Verify the 't' is not following 'u' as in "out"
                {
                    char t2[80];
                    strcpy(t2, tbuf);

                    flag >>= 2;       // Make the flag match the protocol

                    _snprintf(tbuf, sizeof(tbuf), "%s TEMPORARY", t2);
                }

                _cmd["service"] = CtiParseValue( flag );

                _actionItems.insert(tbuf);
            }
        }

        if(!(token = CmdStr.match("led[ \t]+(y|n)[ \t]*(y|n)[ \t]*(y|n)")).isNull())
        {
            INT   flag = 0;
            int   i;
            int   mask;

            token.toLower();

            if(!(strnum = token.match("(y|n)[ \t]*(y|n)[ \t]*(y|n)")).isNull())
            {
                for(i = 0, mask = 0x20 ; i < strnum.length(); i++)
                {
                    if(strnum[(size_t)i] == 'y')
                    {
                        flag |= mask;
                    }

                    if(strnum[(size_t)i] != ' ' && strnum[(size_t)i] != '\t')
                    {
                        mask <<= 1;
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

        if(!(token = CmdStr.match("reset")).isNull())
        {
            if(!(token = CmdStr.match("reset[_a-z]*([ \t]+((rtc)|(lf)|(r1)|(r2)|(r3)|(r4)|(cl)|(pt)))+")).isNull())
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

        if(!(token = CmdStr.match("prop")).isNull())
        {
            if(!(token = CmdStr.match("prop[ a-z_]*[ \t=]*([ \t=]+)?[0-9]+")).isNull())
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

        if(!(token = CmdStr.match("cold")).isNull())
        {
            if(!(token = CmdStr.match("cold[ a-z_]*" \
                                      "([ \t]*r[123][ \t=]*[0-9]+[ \t=]*[hms]?)" \
                                      "([ \t]*r[123][ \t=]*[0-9]+[ \t=]*[hms]?)?" \
                                      "([ \t]*r[123][ \t=]*[0-9]+[ \t=]*[hms]?)?")).isNull())
            {
                // dout << token << endl;

                if(!(strnum = token.match("r1[ \t=]*[0-9]+[ \t=]*[hms]?")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    // _num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["coldload_r1"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG COLDLOAD R1 = %d", _num);
                    _actionItems.insert(tbuf);
                }
                if(!(strnum = token.match("r2[ \t=]*[0-9]+[ \t=]*[hms]?")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["coldload_r2"] = CtiParseValue( _num );

                    _snprintf(tbuf, sizeof(tbuf), "CONFIG COLDLOAD R2 = %d", _num);
                    _actionItems.insert(tbuf);
                }
                if(!(strnum = token.match("r3[ \t=]*[0-9]+[ \t=]*[hms]?")).isNull())
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

        if(!(token = CmdStr.match("cycle")).isNull())
        {
            if(!(token = CmdStr.match("cycle[ a-z]*" \
                                      "([ \t]*r[123][ \t=]*[0-9]+[ \t=]*)" \
                                      "([ \t]*r[123][ \t=]*[0-9]+[ \t=]*)?" \
                                      "([ \t]*r[123][ \t=]*[0-9]+[ \t=]*)?")).isNull())
            {
                if(!(strnum = token.match("r1[ \t=]*[0-9]+")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    _num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _cmd["cycle_r1"] = CtiParseValue( _num );
                }
                if(!(strnum = token.match("r2[ \t=]*[0-9]+[ \t=]*")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    _num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _cmd["cycle_r2"] = CtiParseValue( _num );
                }
                if(!(strnum = token.match("r3[ \t=]*[0-9]+[ \t=]*")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    _num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _cmd["cycle_r3"] = CtiParseValue( _num );
                }
            }
        }

        if(!(token = CmdStr.match("scram")).isNull())
        {
            if(!(token = CmdStr.match("scram[ a-z]*" \
                                      "([ \t]*r[123][ \t=]*[0-9]+[ \t=]*[hms]?)" \
                                      "([ \t]*r[123][ \t=]*[0-9]+[ \t=]*[hms]?)?" \
                                      "([ \t]*r[123][ \t=]*[0-9]+[ \t=]*[hms]?)?")).isNull())
            {
                if(!(strnum = token.match("r1[ \t=]*[0-9]+[ \t=]*[hms]?")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["scram_r1"] = CtiParseValue( _num );
                }
                if(!(strnum = token.match("r2[ \t=]*[0-9]+[ \t=]*[hms]?")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["scram_r2"] = CtiParseValue( _num );
                }
                if(!(strnum = token.match("r3[ \t=]*[0-9]+[ \t=]*[hms]?")).isNull())
                {
                    strnum.replace(0, 2, " "); // Blank the r1 to prevent matches on the 1
                    //_num = strtol(strnum.match(re_anynum).data(), &p, 0);
                    _num = convertTimeInputToSeconds(strnum);
                    _cmd["scram_r3"] = CtiParseValue( _num );
                }
            }
        }

        if(!(token = CmdStr.match("raw")).isNull())
        {
            if(!(token = CmdStr.match("raw" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?" \
                                      "([ \t]*([ \t=]+0x)?[0-9a-f]+)?")).isNull())
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

    if(!(token = CmdStr.match("test_mode_flag")).isNull())
    {
        _cmd["flag"] = CtiParseValue( CMD_FLAG_TESTMODE );
    }
}

void  CtiCommandParser::doParsePutStatusEmetcon(void)
{
    RWCString   chck = _cmdString;
    RWCString   temp2;
    RWCString   token;
    RWCRExpr    re_reset("reset");
    unsigned int flag = 0;
    char *p;

    chck.toLower();

    const RWCString   CmdStr = chck;    // A lowercase version of the input command string...

    RWCTokenizer   tok(chck);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "putstatus")
    {
        //  nothing yet
    }
    else
    {
        // Something went WAY wrong....
        dout << "This better not ever be seen by mortals... " << endl;
    }
}

void  CtiCommandParser::doParsePutStatusVersacom(void)
{
    char *p;

    char        tbuf[60];

    RWCString   token;
    RWCString   temp = _cmdString;
    RWCString   temp2;
    RWCString   strnum;

    INT         _num = 0;

    temp.toLower();

    const RWCString   CmdStr = temp;    // A lowercase version of the input commnad string...

    RWCTokenizer   tok(temp);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "putstatus")
    {
        if(!(token = CmdStr.match("prop[a-z]*[ \t=]+((disp[a-z]*)|(inc[a-z]*)|(term[a-z]*))")).isNull())
        {
            int   op = 0;
            CHAR  op_name[20];

            if(!(token.match("disp[a-z]*")).isNull())
            {
                op = 0x04;
                _snprintf(op_name, sizeof(op_name), "DISPLAY");
            }
            else if(!(token.match("inc[a-z]*")).isNull())
            {
                op = 0x02;
                _snprintf(op_name, sizeof(op_name), "INCREMENT");
            }
            else if(!(token.match("term[a-z]*")).isNull())
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
        else if(!(token = CmdStr.match("ovuv[ \t=]+((ena[a-z]*)|(dis[a-z]*))")).isNull())
        {
            int   op = 0;
            CHAR  op_name[20];

            if(!(token.match("ena[a-z]*")).isNull())
            {
                op = 1;
                _snprintf(op_name, sizeof(op_name), "ENABLE");
            }
            else if(!(token.match("dis[a-z]*")).isNull())
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

void  CtiCommandParser::doParsePutStatusFisherP(void)
{
    char *p;

    char        tbuf[60];

    RWCString   token;
    RWCString   temp = _cmdString;
    RWCString   temp2;
    RWCString   strnum;

    INT         _num = 0;

    temp.toLower();

    const RWCString   CmdStr = temp;    // A lowercase version of the input commnad string...

    RWCTokenizer   tok(temp);

    token = tok(); // Get the first one into the hopper....

    if(!token.isNull() && token == "putstatus")
    {

        if(!(token = CmdStr.match("ovuv[ \t=]+((ena[a-z]*)|(dis[a-z]*))")).isNull())
        {
            int   op = 0;
            CHAR  op_name[20];

            if(!(token.match("ena[a-z]*")).isNull())
            {
                op = 1;
                _snprintf(op_name, sizeof(op_name), "ENABLE");
            }
            else if(!(token.match("dis[a-z]*")).isNull())
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

void CtiCommandParser::resolveProtocolType(void)
{
    const RWCString   CmdStr = _cmdString;

    RWCString         token;

    /*
     *  Type is assigned i.f.f. there is a serial number specified.  The default type is versacom.
     */
    if( isKeyValid("serial") )
    {
        if(!(token = CmdStr.match("emetcon")).isNull())
        {
            _cmd["type"] = CtiParseValue( "emetcon",  ProtocolEmetconType );
        }
        else if(!(token = CmdStr.match("fp")).isNull())            // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["type"] = CtiParseValue( "fp",  ProtocolFisherPierceType );
        }
        else if(!(token = CmdStr.match("sa105")).isNull())       // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["type"] = CtiParseValue( "sa105", ProtocolSA105Type );
        }
        else if(!(token = CmdStr.match("sa205")).isNull())       // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["type"] = CtiParseValue( "sa205", ProtocolSA205Type );
        }
        else if(!(token = CmdStr.match("sa305")).isNull())       // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["type"] = CtiParseValue( "sa305", ProtocolSA305Type );
        }
        else
        {
            _cmd["type"] = CtiParseValue( "versacom", ProtocolVersacomType );
        }
    }
    else
    {
#if 0
        if(!(token = CmdStr.match("ovuv")).isNull() ||
           !(token = CmdStr.match("prop[a-z]*")).isNull())            // Sourcing from CmdStr, which is the entire command string.
        {
            _cmd["type"] = CtiParseValue( "versacom", ProtocolVersacomType );
        }
#else
        //  check for "emetcon" protocol
        if(!(token = CmdStr.match("emetcon")).isNull())
        {
            _cmd["type"] = CtiParseValue( "emetcon",  ProtocolEmetconType );
        }
        else
        {  //  default to Versacom if nothing found
            _cmd["type"] = CtiParseValue( "versacom", ProtocolVersacomType );
        }
#endif
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

/*-----------------------------------------------------------------------------*
*
* File:   cmdparse
*
* Class:  CtiCommandParser
* Date:   2/22/2000
*
* Author:
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __CMDPARSE_H__
#define __CMDPARSE_H__
#pragma warning( disable : 4786)


#include <windows.h>
#include <iomanip>
#include <functional>
#include <string>
#include <list>
#include <map>

#include <rw/tvhdict.h>
#include <rw/rwdate.h>
#include <rw/tvslist.h>
#include <rw\ctoken.h>

#include "ctitokenizer.h"
#include "dlldefs.h"
#include "parsevalue.h"


using std::string;
using std::map;

struct simple_hash
{
   unsigned long operator()(const string& x) const { return x.length() * (long)x[0]; }
};

class IM_EX_CTIBASE CtiCommandParser
{
protected:

   string                  _cmdString;
   std::list< string >   _actionItems;

   map< string, CtiParseValue > _cmd;

   UINT     _flags,
            _command;

private:

    void    doParse(const string &Cmd);
    void    doParseGetValue (const string &CmdStr);
    void    doParsePutValue (const string &CmdStr);
    void    doParseGetStatus(const string &CmdStr);
    void    doParsePutStatus(const string &CmdStr);
    void    doParseControl  (const string &CmdStr);
    void    doParseGetConfig(const string &CmdStr);
    void    doParsePutConfig(const string &CmdStr);
    void    doParseScan     (const string &CmdStr);

    void    doParsePutConfigVersacom(const string &CmdStr);
    void    doParsePutConfigEmetcon (const string &CmdStr);
    void    doParsePutStatusVersacom(const string &CmdStr);
    void    doParsePutStatusFisherP (const string &CmdStr);
    void    doParsePutStatusEmetcon (const string &CmdStr);
    void    resolveProtocolType(const string &CmdStr);
    void    doParseExpresscomAddressing(const string &CmdStr);

    void    doParseControlExpresscom  (const string &CmdStr);
    void    doParsePutConfigExpresscom(const string &CmdStr);
    void    doParsePutStatusExpresscom(const string &CmdStr);

    void    doParseControlSA  (const string &CmdStr);
    void    doParsePutConfigSA(const string &CmdStr);
    void    doParsePutConfigUtilityUsage(const string &_CmdStr);
    void    doParsePutConfigThermostatSchedule(const string &CmdStr);

    INT     convertTimeInputToSeconds(const string& inStr) const;
    INT     isTokenThermostatScheduleDOW(string &token);
    void    doParsePutConfigThermostatScheduleDOW(CtiTokenizer &tok, INT &key);

    void    doParsePutConfigCBC(const string &CmdStr);

    void    setFlags(UINT flags);
    void    setCommand(UINT command);

public:

   typedef std::map< string, CtiParseValue > map_type;
   typedef std::map< string, CtiParseValue >::iterator  map_itr_type;

   CtiCommandParser(const string str);

   CtiCommandParser(const CtiCommandParser& aRef);

   virtual ~CtiCommandParser();

   CtiCommandParser& operator=(const CtiCommandParser& aRef);

   map_type    getMap() const    { return _cmd; }
   map_type&   Map()             { return _cmd; }


   void Dump();

   const string& getCommandStr() const;

   int      getControlled() const;
   bool     isControlled()  const;
   bool     isDisconnect()  const;
   bool     isTwoWay()      const;


   UINT   getCommand() const;

   UINT   getFlags()   const;
   UINT   getOffset()  const;
   bool   isKeyValid(const string &key) const;
   INT    getiValue (const string &key, INT valifnotfound = INT_MIN) const;
   DOUBLE getdValue (const string &key, DOUBLE valifnotfound = 0.0) const;
   string getsValue (const string &key) const;
   CtiCommandParser& setValue(const string &key, INT val);
   CtiCommandParser& setValue(const string &key, DOUBLE val);
   CtiCommandParser& setValue(const string &key, string val);


   std::list< string >& getActionItems();

   void parse();

   string asString();
};



// Flag settings which are command relative.

/* GetValue flags
 *    GetValue Indicator
 *    Flags integer
 *    Offset Number (optional)
 *
 */
#define CMD_FLAG_GV_RATEA        0x00000001
#define CMD_FLAG_GV_RATEB        0x00000002
#define CMD_FLAG_GV_RATEC        0x00000004
#define CMD_FLAG_GV_RATED        0x00000008
#define CMD_FLAG_GV_RATET        0x00000010
#define CMD_FLAG_GV_RATEMASK     0x0000001F

#define CMD_FLAG_GV_KWH          0x00000020
#define CMD_FLAG_GV_KVARH        0x00000040
#define CMD_FLAG_GV_KVAH         0x00000080
#define CMD_FLAG_GV_DEMAND       0x00000100
#define CMD_FLAG_GV_PEAK         0x00000200
#define CMD_FLAG_GV_MINMAX       0x00000400
#define CMD_FLAG_GV_VOLTAGE      0x00000800

#define CMD_FLAG_GV_IED          0x00001000     // The reading is to be from a connected ied!

#define CMD_FLAG_GV_PFCOUNT      0x00002000
#define CMD_FLAG_GV_USAGE        0x00004000     // This is for all channels on a residential meter
#define CMD_FLAG_GV_TOU          0x00008000
/* Defined below.... be careful
#define CMD_FLAG_OFFSET          0x10000000     // A specific offset is defined in the next cmd field
*/
#define CMD_FLAG_GV_ALIASMASK    0x0000FFF0

/* GetStatus flags
 *    GetStatus Indicator
 *    Flags Int
 *    Offset Number       (optional)
 */
#define CMD_FLAG_GS_DISCONNECT   0x00000010
#define CMD_FLAG_GS_ERRORS       0x00000020
#define CMD_FLAG_GS_LOADPROFILE  0x00000080
#define CMD_FLAG_GS_INTERNAL     0x00000100
#define CMD_FLAG_GS_EXTERNAL     0x00000200
#define CMD_FLAG_GS_IED          0x00000400
#define CMD_FLAG_GS_LINK         0x00000800
/* Defined below.... be careful
#define CMD_FLAG_OFFSET          0x10000000     // A specific offset is defined in the next cmd field
*/
#define CMD_FLAG_GS_ALIASMASK    0x00000FF0

/* PutStatus flags
 *    PutStatus Indicator
 *    Flags Int
 *    Offset Number       (optional)
 */
#define CMD_FLAG_PS_RESET         0x00000010
#define CMD_FLAG_PS_FREEZEZERO    0x00000020
#define CMD_FLAG_PS_FREEZEONE     0x00000040
#define CMD_FLAG_PS_RESETOVERRIDE 0x00000080
/* Defined below.... be careful
#define CMD_FLAG_OFFSET          0x10000000     // A specific offset is defined in the next cmd field
*/
#define CMD_FLAG_PS_ALIASMASK    0x000000F0

/* Control flags
 *    Control Indicator
 *    Flags integer
 *    Offset Number       (optional)
 *    Shed time or cycle percentage (optional)
 */
#define CMD_FLAG_CTL_OPEN        0x00000010
#define CMD_FLAG_CTL_CLOSE       0x00000020
#define CMD_FLAG_CTL_CONNECT     0x00000040
#define CMD_FLAG_CTL_DISCONNECT  0x00000080
#define CMD_FLAG_CTL_RESTORE     0x00000100
#define CMD_FLAG_CTL_TERMINATE   0x00000200
#define CMD_FLAG_CTL_SHED        0x00000400
#define CMD_FLAG_CTL_CYCLE       0x00000800     // Data value in slot following offset slot
/* Defined below.... be careful
#define CMD_FLAG_OFFSET          0x10000000     // A specific offset is defined in the next cmd field
*/
#define CMD_FLAG_CTL_ALIASMASK   0x000FFFF0




// Globally inviolable flag settings.
#define CMD_FLAG_OFFSET          0x10000000     // A specific offset is defined in the next cmd field
#define CMD_FLAG_TESTMODE        0x20000000     // An indicator to be used on a whim to cause a non-normal flow change
#define CMD_FLAG_FROZEN          0x40000000
#define CMD_FLAG_UPDATE          0x80000000


enum CtiClientRequest_t
{
   InvalidRequest = 0,
   GetValueRequest,
   PutValueRequest,
   GetStatusRequest,
   PutStatusRequest,
   GetConfigRequest,
   PutConfigRequest,
   LoopbackRequest,
   ControlRequest,
   ScanRequest,

   MaxRequest
};

#endif // #ifndef __CMDPARSE_H__

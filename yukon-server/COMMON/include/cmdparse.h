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
using namespace std;

#include <rw/tvhdict.h>
#include <rw/cstring.h>
#include <rw/rwdate.h>
#include <rw/tvslist.h>
#include <rw\ctoken.h>

#include "dlldefs.h"
#include "parsevalue.h"

struct simple_hash
{
   unsigned long operator()(const RWCString& x) const { return x.length() * (long)x(0); }
};

class IM_EX_CTIBASE CtiCommandParser
{
protected:

   RWCString                  _cmdString;
   RWTValSlist< RWCString >   _actionItems;

   RWTValHashMap< RWCString, CtiParseValue, simple_hash, equal_to<RWCString> > _cmd;


private:

    void    doParse(RWCString Cmd);
    void    doParseGetValue(const RWCString &CmdStr);
    void    doParsePutValue(const RWCString &CmdStr);
    void    doParseGetStatus(const RWCString &CmdStr);
    void    doParsePutStatus(const RWCString &CmdStr);
    void    doParseControl(const RWCString &CmdStr);
    void    doParseGetConfig(const RWCString &CmdStr);
    void    doParsePutConfig(const RWCString &CmdStr);
    void    doParseScan(const RWCString &CmdStr);

    void    doParsePutConfigVersacom(const RWCString &CmdStr);
    void    doParsePutConfigEmetcon(const RWCString &CmdStr);
    void    doParsePutStatusVersacom(const RWCString &CmdStr);
    void    doParsePutStatusFisherP(const RWCString &CmdStr);
    void    doParsePutStatusEmetcon(const RWCString &CmdStr);
    void    resolveProtocolType(const RWCString &CmdStr);

    void    doParseControlExpresscom(const RWCString &CmdStr);
    void    doParsePutConfigExpresscom(const RWCString &CmdStr);
    void    doParsePutStatusExpresscom(const RWCString &CmdStr);

    void    doParseControlSA(const RWCString &CmdStr);
    void    doParsePutConfigSA(const RWCString &CmdStr);

    void    doParsePutConfigThermostatSchedule(const RWCString &CmdStr);

    INT     convertTimeInputToSeconds(const RWCString& inStr) const;
    INT     isTokenThermostatScheduleDOW(RWCString &token);
    void    doParsePutConfigThermostatScheduleDOW(RWTokenizer &tok, INT &key);


public:

   typedef RWTValHashMap< RWCString, CtiParseValue, simple_hash, equal_to<RWCString> > map_type;
   typedef RWTValHashMapIterator< RWCString, CtiParseValue, simple_hash, equal_to<RWCString> > map_itr_type;

   CtiCommandParser(const RWCString str);

   CtiCommandParser(const CtiCommandParser& aRef);

   virtual ~CtiCommandParser();

   CtiCommandParser& operator=(const CtiCommandParser& aRef);

   map_type    getMap() const    { return _cmd; }
   map_type&   Map()             { return _cmd; }


   void Dump();

   const RWCString& getCommandStr() const;

   int      getControlled() const;
   bool     isControlled() const;
   bool     isTwoWay() const;


   UINT     getCommand() const;
   UINT     getFlags() const;
   UINT     getOffset() const;
   bool     isKeyValid(const RWCString key) const;
   UINT     getOffset(const RWCString key) const;
   INT      getiValue(const RWCString key, INT valifnotfound = INT_MIN) const;
   DOUBLE   getdValue(const RWCString key, DOUBLE valifnotfound = 0.0) const;
   RWCString getsValue(const RWCString key) const;
   CtiCommandParser& setValue(const RWCString key, INT val);
   CtiCommandParser& setValue(const RWCString key, DOUBLE val);
   CtiCommandParser& setValue(const RWCString key, RWCString val);


   RWTValSlist< RWCString >& getActionItems();

   void parse();

   RWCString asString();
   CtiCommandParser& parseAsString(const RWCString str);

};



// Flag settings which are command relative.

/* GetValue flags
 *    GetValue Indicator
 *    Flags integer
 *    Offset Number (optional)
 *
 */
#define CMD_FLAG_GV_RATEA        0x00000000
#define CMD_FLAG_GV_RATEB        0x00000001
#define CMD_FLAG_GV_RATEC        0x00000002
#define CMD_FLAG_GV_RATED        0x00000004
#define CMD_FLAG_GV_RATET        0x00000008
#define CMD_FLAG_GV_RATEMASK     0x0000000F

#define CMD_FLAG_GV_KWH          0x00000010
#define CMD_FLAG_GV_KVARH        0x00000020
#define CMD_FLAG_GV_KVAH         0x00000040
#define CMD_FLAG_GV_DEMAND       0x00000080
#define CMD_FLAG_GV_PEAK         0x00000100
#define CMD_FLAG_GV_MINMAX       0x00000200
#define CMD_FLAG_GV_VOLTAGE      0x00000400
#define CMD_FLAG_GV_CHMASK       0x000007F0

#define CMD_FLAG_GV_IED          0x00001000     // The reading is to be from a connected ied!

#define CMD_FLAG_GV_PFCOUNT      0x00002000
/* Defined below.... be careful
#define CMD_FLAG_OFFSET          0x10000000     // A specific offset is defined in the next cmd field
*/
#define CMD_FLAG_GV_ALIASMASK    0x0000FFF0

/* PutValue flags
 *    PutValue Indicator
 *    Flags Int
 *    Offset Number       (optional)
 *    Dial reading number (optional) in DOUBLE field.
 */
#define CMD_FLAG_PV_RESET        0x00000010
#define CMD_FLAG_PV_FREEZE       0x00000020
#define CMD_FLAG_PV_DIAL         0x00000040     // integer value for the PI reg.
#define CMD_FLAG_PV_ANALOG       0x00000080     // integer value for the PI reg.

#define CMD_FLAG_PV_IED          0x00000100     // The write is to be from a connected ied!
#define CMD_FLAG_PV_PWR          0x00000200     // The looking for a powerfail read/write
/* Defined below.... be careful
#define CMD_FLAG_OFFSET          0x10000000     // A specific offset is defined in the next cmd field
*/
#define CMD_FLAG_PV_ALIASMASK    0x00000FF0

#define CMD_FLAG_PV_OFF          0x00010000     // A turn it off, not on.

/* GetStatus flags
 *    GetStatus Indicator
 *    Flags Int
 *    Offset Number       (optional)
 */
#define CMD_FLAG_GS_DISCONNECT   0x00000010
#define CMD_FLAG_GS_ERRORS       0x00000020
#define CMD_FLAG_GS_LOADSURVEY   0x00000080
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


typedef enum {
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

} CtiClientRequest_t;

/*
typedef enum {
   NumericOffset = 0,
   KwhOffset,
   KvarhOffset,
   DemandOffset,
   PeakOffset,

   InvalidOffset

} CtiValueAlias_t;
*/

#endif // #ifndef __CMDPARSE_H__


#pragma warning( disable : 4786)
#ifndef __CMDPARSE_H__
#define __CMDPARSE_H__

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
#include <windows.h>
#include <iomanip>
#include <functional>
using namespace std;

#include <rw/tvhdict.h>
#include <rw/cstring.h>
#include <rw/rwdate.h>
#include <rw/tvslist.h>

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

public:

   typedef RWTValHashMap< RWCString, CtiParseValue, simple_hash, equal_to<RWCString> > map_type;
   typedef RWTValHashMapIterator< RWCString, CtiParseValue, simple_hash, equal_to<RWCString> > map_itr_type;

   CtiCommandParser(const RWCString str) :
      _cmdString(str)
   {
      _actionItems.clear();
      doParse(_cmdString);
   }

   CtiCommandParser(const CtiCommandParser& aRef)
   {
      _actionItems.clear();
      *this = aRef;
   }

   virtual ~CtiCommandParser()
   {
      _actionItems.clear();
   }

   CtiCommandParser& operator=(const CtiCommandParser& aRef)
   {
      if(this != &aRef)
      {
         _cmd = aRef.getMap();
      }
      return *this;
   }

   map_type    getMap() const    { return _cmd; }
   map_type&   Map()             { return _cmd; }


   void Dump();

   const RWCString& getCommandStr() const;

   void     doParse(RWCString Cmd);

   void     doParseGetValue();
   void     doParsePutValue(void);
   void     doParseGetStatus(void);
   void     doParsePutStatus(void);
   void     doParseControl(void);
   void     doParseGetConfig(void);
   void     doParsePutConfig(void);
   void     doParseScan(void);

   void     doParsePutConfigVersacom(void);
   void     doParsePutConfigEmetcon(void);
   void     doParsePutStatusVersacom(void);
   void     doParsePutStatusFisherP(void);
   void     doParsePutStatusEmetcon(void);
   void     resolveProtocolType();

   INT      convertTimeInputToSeconds(const RWCString& inStr) const;
   int      getControlled() const;
   bool     isControlled() const;
   bool     isTwoWay() const;


   UINT     getCommand() const;
   UINT     getFlags() const;
   UINT     getOffset() const;
   UINT     isKeyValid(const RWCString key) const;
   UINT     getOffset(const RWCString key) const;
   INT      getiValue(const RWCString key) const;
   DOUBLE   getdValue(const RWCString key) const;
   RWCString getsValue(const RWCString key) const;


   RWTValSlist< RWCString >& getActionItems();

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
#define CMD_FLAG_GV_CHMASK       0x000001F0

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
#define CMD_FLAG_PV_PEAK         0x00000030
#define CMD_FLAG_PV_DIAL         0x00000040     // integer value for the PI reg.

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
/* Defined below.... be careful
#define CMD_FLAG_OFFSET          0x10000000     // A specific offset is defined in the next cmd field
*/
#define CMD_FLAG_GS_ALIASMASK    0x00000FF0

/* PutStatus flags
 *    PutStatus Indicator
 *    Flags Int
 *    Offset Number       (optional)
 */
#define CMD_FLAG_PS_RESET        0x00000010
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

typedef enum {
   NumericOffset = 0,
   KwhOffset,
   KvarhOffset,
   DemandOffset,
   PeakOffset,

   InvalidOffset

} CtiValueAlias_t;


#endif // #ifndef __CMDPARSE_H__

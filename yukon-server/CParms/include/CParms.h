#ifndef __CPARMS_H__
#define __CPARMS_H__

#include <rw/collect.h>
#include <rw/cstring.h>
#include <rw/hashdict.h>
#include <rw/rwtime.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h>

#include "dlldefs.h"

#define MAX_CONFIG_BUFFER  256
#define MAX_CONFIG_KEY     80
#define MAX_CONFIG_VALUE   ((MAX_CONFIG_BUFFER) - (MAX_CONFIG_KEY))

// Forward decls.
class CtiConfigParameters;

IM_EX_CPARM extern RWCString DefaultMasterConfigFileName;
IM_EX_CPARM extern RWCString ConfKeyRefreshRate;
IM_EX_CPARM extern CtiConfigParameters gConfigParms;

class IM_EX_CPARM CtiConfigParameters
{
private:

   int               RefreshRate;
   RWTime            LastRefresh;
   RWCString         FileName;
   RWCString         BaseDir;
   RWRecursiveLock<RWMutexLock> mutex;
   RWHashDictionary  mHash;

public:

   CtiConfigParameters(RWCString strName = DefaultMasterConfigFileName);

   virtual ~CtiConfigParameters();

   CtiConfigParameters& setConfigFile(RWCString strName = DefaultMasterConfigFileName);

   RWCString getYukonBaseDir() const;

   BOOL              isOpt(RWCString key);
   bool isOpt(RWCString key, RWCString isEqualThisValue);

   /*
    * returns bool true i.f.f. the cparms were refreshed on this call.  Uses the built in timer to
    * track the time since the last refresh on this object.  All CtiConfigParameters use the CPARM
    * CONFIG_REFRESHRATE to fix the time (defaults to 900 = 15 minutes)
    */
   bool              checkForRefresh();
   int               RefreshConfigParameters();
   void              Dump();
   void              HeadAndTail(char *source, char *dest, size_t len);

   RWCString         getValueAsString(RWCString Key, RWCString defaultval = RWCString());
   int               getValueAsInt(RWCString key, int defaultval = 0);
   double            getValueAsDouble(RWCString key, double defaultval = 0.0);
   ULONG             getValueAsULong(RWCString key, ULONG defaultval = 0L, int base = 10);


};




#endif // #ifndef __CPARMS_H__


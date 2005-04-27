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

template<class T>
class CtiParmLockGuard
{
public:
    CtiParmLockGuard(T& resource) :  _res(resource)
    {
        _res.acquire();
        _acquired = true;
    }

    ~CtiParmLockGuard()
    {
        if(_acquired)
            _res.release();
    }

    bool isAcquired() const { return _acquired;}

private:

    bool _acquired;
    T& _res;
};

class CtiParmCriticalSection
{
public:
    CtiParmCriticalSection()
    {
    #ifdef _WINDOWS
        InitializeCriticalSection(&_critical_section);
    #ifdef _DEBUG
        _threadID = 0;
    #endif
    #endif
    }

    virtual ~CtiParmCriticalSection()
    {
    #ifdef _WINDOWS
        DeleteCriticalSection(&_critical_section);
    #endif
    }


    bool acquire()
    {
    #ifdef _WINDOWS
        EnterCriticalSection(&_critical_section);
    #ifdef _DEBUG
        _threadID = (int) _critical_section.OwningThread;
    #endif
        return true;
    #endif
    }

    void release()
    {
    #ifdef _WINDOWS
        LeaveCriticalSection(&_critical_section);

    #ifdef _DEBUG
        _threadID = 0;
    #endif
    #endif
    }

#ifdef _DEBUG
    DWORD lastAcquiredByTID() const
    {
        return _threadID;
    }
#endif

private:

#ifdef _WINDOWS
    CRITICAL_SECTION _critical_section;
#ifdef _DEBUG
    DWORD  _threadID;
#endif
#endif
};


class IM_EX_CPARM CtiConfigParameters
{
private:

   int               RefreshRate;
   RWTime            LastRefresh;
   RWCString         FileName;
   RWCString         BaseDir;
   #ifdef USE_RECURSIVE_MUX
   RWRecursiveLock<RWMutexLock> mutex;
   #else
   CtiParmCriticalSection  crit_sctn;
   #endif
   RWHashDictionary  mHash;

public:

   CtiConfigParameters(RWCString strName = DefaultMasterConfigFileName);
   virtual ~CtiConfigParameters();

   CtiConfigParameters& setConfigFile(RWCString strName = DefaultMasterConfigFileName);

   RWCString getYukonBaseDir() const;

   BOOL isOpt(RWCString key);
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


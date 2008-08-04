#ifndef __CPARMS_H__
#define __CPARMS_H__

#include <time.h>
#include <map>
#include "rwutil.h"
#include "dlldefs.h"
#include "configkey.h"
#include "configval.h"


#define MAX_CONFIG_BUFFER  1024
#define MAX_CONFIG_KEY     256
#define MAX_CONFIG_VALUE   ((MAX_CONFIG_BUFFER) - (MAX_CONFIG_KEY))

using std::string;
using std::map; 

typedef std::map<string,CtiConfigValue*>::iterator mHash_itr2;
typedef std::pair<std::map<string,CtiConfigValue*>::iterator,bool> mHash_pair2;

// Forward decls.
class CtiConfigParameters;

IM_EX_CPARM extern string DefaultMasterConfigFileName;
IM_EX_CPARM extern string ConfKeyRefreshRate;
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
   time_t            LastRefresh;
   string         FileName;
   string         BaseDir;
   #ifdef USE_RECURSIVE_MUX
   RWRecursiveLock<RWMutexLock> mutex;
   #else
   CtiParmCriticalSection  crit_sctn;
   #endif
   map<string,CtiConfigValue*> mHash;

public:

   CtiConfigParameters(const string& strName = DefaultMasterConfigFileName);
   virtual ~CtiConfigParameters();

   CtiConfigParameters& setConfigFile(const string& strName = DefaultMasterConfigFileName);

   string getYukonBaseDir() const;

   BOOL isOpt(const string& key);
   bool isOpt(const string& key, const string& isEqualThisValue);
   bool isTrue(const string& key, bool defaultval = false);

   /*
    * returns bool true i.f.f. the cparms were refreshed on this call.  Uses the built in timer to
    * track the time since the last refresh on this object.  All CtiConfigParameters use the CPARM
    * CONFIG_REFRESHRATE to fix the time (defaults to 900 = 15 minutes)
    */
   bool              checkForRefresh();
   int               RefreshConfigParameters();
   void              Dump();
   void              HeadAndTail(char *source, char *dest, size_t len);

   string            getValueAsString (const string& Key, const string& defaultval = "");
   int               getValueAsInt    (const string& key, int defaultval = 0);
   double            getValueAsDouble (const string& key, double defaultval = 0.0) ;
   ULONG             getValueAsULong  (const string& key, ULONG defaultval = 0L, int base = 10);
};




#endif // #ifndef __CPARMS_H__


#pragma once

#include "dlldefs.h"

#include <windows.h>

#include <map>

#include <chrono>

// Forward decls.
class CtiConfigParameters;

IM_EX_CTIBASE extern CtiConfigParameters gConfigParms;

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
        InitializeCriticalSection(&_critical_section);

        _threadID = 0;
    }

    virtual ~CtiParmCriticalSection()
    {
        DeleteCriticalSection(&_critical_section);
    }


    bool acquire()
    {
        EnterCriticalSection(&_critical_section);

        _threadID = (unsigned long) _critical_section.OwningThread;

        return true;
    }

    void release()
    {
        LeaveCriticalSection(&_critical_section);

        _threadID = 0;
    }

    unsigned long lastAcquiredByTID() const
    {
        return _threadID;
    }

private:

    CRITICAL_SECTION _critical_section;
    unsigned long  _threadID;
};


class IM_EX_CTIBASE CtiConfigParameters
{
private:

    typedef std::map<std::string,std::string> ConfigValueMap;

    typedef ConfigValueMap::iterator mHash_itr2;
    typedef std::pair<mHash_itr2,bool> mHash_pair2;

   int            RefreshRate;
   time_t         LastRefresh;
   std::string    FileName;
   std::string    YukonBase;

   CtiParmCriticalSection  crit_sctn;

   ConfigValueMap mHash;

   std::pair< bool, std::string > preprocessValue( char * chValue ) const;

public:

   CtiConfigParameters();

   void setYukonBase(const std::string& strName);

   std::string getYukonBase() const;

   BOOL isOpt(const std::string& key);
   bool isOpt(const std::string& key, const std::string& isEqualThisValue);
   bool isTrue(const std::string& key, bool defaultval = false);

   /*
    * returns bool true i.f.f. the cparms were refreshed on this call.  Uses the built in timer to
    * track the time since the last refresh on this object.  All CtiConfigParameters use the CPARM
    * CONFIG_REFRESHRATE to fix the time (defaults to 900 = 15 minutes)
    */
   bool              checkForRefresh();
   int               RefreshConfigParameters();
   void              Dump();
   void              HeadAndTail(char *source, char *dest, size_t len);

   using Duration = std::chrono::duration<double>;

   std::string       getValueAsString  (const std::string& Key, const std::string& defaultval = "");
   std::string       getValueAsPath    (const std::string& Key, const std::string& defaultval = "");
   int               getValueAsInt     (const std::string& key, int defaultval = 0);
   double            getValueAsDouble  (const std::string& key, double defaultval = 0.0) ;
   ULONG             getValueAsULong   (const std::string& key, ULONG defaultval = 0L, int base = 10);
   Duration          getValueAsDuration(const std::string& key, Duration defaultval = Duration { 0.0 });
};



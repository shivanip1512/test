#pragma once

#include "dlldefs.h"

#include "tbl_dyn_paoinfo.h"

#include "readers_writer_lock.h"

#include <boost/shared_ptr.hpp>
#include <boost/weak_ptr.hpp>

#include <map>

namespace Cti {

enum Applications
{
    Application_Dispatch = 100,  //  just so if we ever run into a case where it's initialized to 0,
    Application_Porter,          //    there'll be no confusion - it'll be recognized as invalid
    Application_Scanner,
    Application_CapControl,
    Application_LoadManagement,
    Application_CalcLogic,

    Application_Invalid  =  -1
};

class IM_EX_DYNPAOINFO DynamicPaoInfoManager
{
public:
    typedef CtiTableDynamicPaoInfo::PaoInfoKeys PaoInfoKeys;

    static void setOwner(const Applications owner);

    static void purgeInfo(const long paoId);
    static void purgeInfo(const long paoId, PaoInfoKeys k);

    static bool hasInfo(const long paoId, PaoInfoKeys k);

    static void setInfo(const long paoId, PaoInfoKeys k, const std::string   value);
    static void setInfo(const long paoId, PaoInfoKeys k, const int           value);
    static void setInfo(const long paoId, PaoInfoKeys k, const unsigned int  value);
    static void setInfo(const long paoId, PaoInfoKeys k, const long          value);
    static void setInfo(const long paoId, PaoInfoKeys k, const unsigned long value);
    static void setInfo(const long paoId, PaoInfoKeys k, const double        value);
    static void setInfo(const long paoId, PaoInfoKeys k, const CtiTime       value);

    static bool getInfo(const long paoId, PaoInfoKeys k, std::string   &destination);
    static bool getInfo(const long paoId, PaoInfoKeys k, int           &destination);
    static bool getInfo(const long paoId, PaoInfoKeys k, unsigned int  &destination);
    static bool getInfo(const long paoId, PaoInfoKeys k, long          &destination);
    static bool getInfo(const long paoId, PaoInfoKeys k, unsigned long &destination);
    static bool getInfo(const long paoId, PaoInfoKeys k, double        &destination);
    static bool getInfo(const long paoId, PaoInfoKeys k, CtiTime       &destination);
    //  note - this returns the value as a long for convenience - the name may need to be changed to prevent confusion if it arises
    static long getInfo(const long paoId, PaoInfoKeys k);

    static void writeInfo();

protected:

    DynamicPaoInfoManager();

    void loadInfo(const long paoId);
    virtual void loadInfo(const Database::id_set &paoIds);

    Database::id_set loadedPaos;

private:

    typedef boost::shared_ptr<CtiTableDynamicPaoInfo> DynInfoSPtr;
    typedef boost::weak_ptr<CtiTableDynamicPaoInfo>   DynInfoWPtr;

    typedef std::map<PaoInfoKeys, DynInfoSPtr> PaoInfoMap;
    typedef std::map<long, PaoInfoMap> PaoIdToPaoInfoMap;

    void loadInfoIfNecessary(const long paoId);

    void setInfo(DynInfoSPtr &newInfo);

    template<typename T>
    bool getInfoForId(const long paoId, PaoInfoKeys k, T &destination);

    PaoIdToPaoInfoMap paoInfoPerId;

    typedef std::set<DynInfoWPtr> DynInfoRefSet;

    DynInfoRefSet dirtyInfo;

    Applications owner;

    readers_writer_lock_t mux;
};

extern IM_EX_DYNPAOINFO std::auto_ptr<DynamicPaoInfoManager> gDynamicPaoInfoManager;  //  auto_ptr so it can be overridden for unit tests

}

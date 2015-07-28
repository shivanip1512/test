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
    typedef CtiTableDynamicPaoInfo::PaoInfoKeys               PaoInfoKeys;
    typedef CtiTableDynamicPaoInfoIndexed::PaoInfoKeysIndexed PaoInfoKeysIndexed;
    typedef std::set<long> PaoIds;

    virtual ~DynamicPaoInfoManager() = default;

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

    static PaoIds getPaoIdsHavingInfo(PaoInfoKeys k);

    static Database::id_set writeInfo();

    // schedule pao Ids to reload
    static void schedulePaoIdsToReload(const Database::id_set& paoIds);

    // set a values using indexed dynamic info
    template <typename T>
    static void setInfo(const long paoId, PaoInfoKeysIndexed k, const std::vector<T> &values);

    // retrieve a vector of values from indexed dynamic info
    template <typename T>
    static boost::optional<std::vector<T>> getInfo(const long paoId, PaoInfoKeysIndexed k);

protected:

    typedef boost::shared_ptr<CtiTableDynamicPaoInfo> DynInfoSPtr;
    typedef boost::weak_ptr  <CtiTableDynamicPaoInfo> DynInfoWPtr;

    DynamicPaoInfoManager();

    virtual void loadInfo(const long paoId);
    virtual void setDirty(const DynInfoSPtr &dirty);

    Database::id_set loadedPaos;

private:

    typedef std::map<PaoInfoKeys, DynInfoSPtr> PaoInfoMap;
    typedef std::map<long, PaoInfoMap> PaoIdToPaoInfoMap;

    void loadInfoIfNecessary(const long paoId);

    void setInfo(DynInfoSPtr &newInfo);

    template<typename T>
    bool getInfoForId(const long paoId, PaoInfoKeys k, T &destination);

    PaoIdToPaoInfoMap paoInfoPerId;

    typedef std::set<DynInfoWPtr> DynInfoRefSet;

    DynInfoRefSet dirtyInfo;

    typedef boost::shared_ptr<CtiTableDynamicPaoInfoIndexed> DynInfoIndexSPtr;
    typedef boost::weak_ptr<CtiTableDynamicPaoInfoIndexed>   DynInfoIndexWPtr;

    // holds dynamic info related to indexed values
    struct PaoInfoIndexed
    {
        DynInfoIndexSPtr              sizeInfo;
        std::vector<DynInfoIndexSPtr> valuesInfo;
    };

    typedef std::map<PaoInfoKeysIndexed, PaoInfoIndexed> PaoInfoIndexedMap;
    typedef std::map<long, PaoInfoIndexedMap>            PaoIdToPaoInfoIndexedMap;

    // cached indexed values
    PaoIdToPaoInfoIndexedMap paoInfoPerIdIndexed;

    typedef std::set<DynInfoIndexWPtr> DynInfoIndexRefSet;
    typedef std::set<std::pair<long, PaoInfoKeysIndexed>> PaoIdAndIndexedKeySet;

    // contains weak_ptr to insert into the DB
    DynInfoIndexRefSet dirtyInfoIndexed;

    // keeps track of indexed values to delete from the DB before re-inserting
    PaoIdAndIndexedKeySet dirtyInfoIndexedToDelete;

    Applications owner;

    readers_writer_lock_t mux;

    Database::id_set paosToReload;
};

extern IM_EX_DYNPAOINFO std::unique_ptr<DynamicPaoInfoManager> gDynamicPaoInfoManager;  //  unique_ptr so it can be overridden for unit tests

// explicit instantiation
template IM_EX_DYNPAOINFO void DynamicPaoInfoManager::setInfo<unsigned long> (const long paoId, PaoInfoKeysIndexed k, const std::vector<unsigned long> &values);
template IM_EX_DYNPAOINFO boost::optional<std::vector<unsigned long>> DynamicPaoInfoManager::getInfo<unsigned long> (const long paoId, PaoInfoKeysIndexed k);

}

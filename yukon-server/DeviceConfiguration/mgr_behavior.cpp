#include "precompiled.h"

#include "mgr_behavior.h"
#include "dbaccess.h"
#include "database_connection.h"
#include "database_reader.h"
#include "debug_timer.h"
#include "logger.h"
#include "behavior_rfnDataStreaming.h"

#include "std_helper.h"

namespace Cti {

using Behaviors::RfnDataStreamingBehavior;
    
namespace {

    static const std::string behaviorItemSql =
        "SELECT"
            " BV.name, BV.value"
        " FROM"
            " DeviceBehaviorMap DBM"
            " JOIN Behavior B on B.BehaviorId=DBM.BehaviorId"
            " JOIN BehaviorValue BV on BI.BehaviorId=B.BehaviorId"
        " WHERE"
            " DBM.DeviceId=?"
            " AND B.BehaviorType=?";

    static const std::string behaviorReportSql =
        "SELECT"
            " BRV.name, BRV.value"
        " FROM"
            " BehaviorReportValue BRV"
            " JOIN BehaviorReport BR on BR.BehaviorReportId=BRV.BehaviorReportId"
        " WHERE"
            " BR.DeviceId=?"
            " AND BR.BehaviorType=?";

    template<typename T>
    struct DatabaseTypeNameFor
    {
        static constexpr std::string value();
    };

    template<>
    constexpr std::string DatabaseTypeNameFor<RfnDataStreamingBehavior>::value()
    {
        return "DATA_STREAMING";
    }
}


template <typename BehaviorType>
BehaviorType BehaviorManager::getBehaviorForPao(const long paoId)
{
    return gBehaviorManager->loadBehaviorForPao<BehaviorType>(paoId, behaviorItemSql);
}

template <typename BehaviorType>
boost::optional<BehaviorType> BehaviorManager::getBehaviorReportForPao(const long paoId)
{
    return gBehaviorManager->loadRfnDataStreamingBehaviorForPao(paoId, behaviorReportSql);
}

template <typename BehaviorType>
BehaviorType BehaviorManager::loadBehaviorForPao(const long paoId, const std::string sql)
{
    Database::DatabaseConnection conn;
    Database::DatabaseReader rdr(conn, behaviorItemSql);

    rdr << paoId << DatabaseTypeNameFor<BehaviorType>::value();

    rdr.execute();

    std::map<std::string, std::string> dbValues;

    while( rdr() )
    {
        dbValues.emplace(rdr["name"].as<std::string>(), rdr["value"].as<std::string>());
    }

    return BehaviorType{std::move(dbValues)};
}

template IM_EX_CONFIG RfnDataStreamingBehavior BehaviorManager::getBehaviorForPao<RfnDataStreamingBehavior>(const long paoId);

IM_EX_CONFIG std::unique_ptr<BehaviorManager> gBehaviorManager(new BehaviorManager);

}

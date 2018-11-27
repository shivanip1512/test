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

    static const std::string behaviorSql =
        "SELECT"
            " BV.name, BV.value"
        " FROM"
            " DeviceBehaviorMap DBM"
            " JOIN Behavior B on B.BehaviorId=DBM.BehaviorId"
            " JOIN BehaviorValue BV on BV.BehaviorId=B.BehaviorId"
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
        static const std::string value();
    };

    template<>
    const std::string DatabaseTypeNameFor<RfnDataStreamingBehavior>::value()
    {
        return "DATA_STREAMING";
    }
}


template <typename BehaviorType>
boost::optional<BehaviorType> BehaviorManager::getBehaviorForPao(const long paoId)
{
    const auto dbValues = 
            gBehaviorManager->loadBehavior(
                    paoId,
                    DatabaseTypeNameFor<BehaviorType>::value());

    if( dbValues.empty() )
    {
        return boost::none;
    }

    return BehaviorType{ paoId, std::move(dbValues) };
}

template <typename BehaviorType>
boost::optional<BehaviorType> BehaviorManager::getDeviceStateForPao(const long paoId)
{
    const auto dbValues =
            gBehaviorManager->loadBehaviorReport(
                    paoId,
                    DatabaseTypeNameFor<BehaviorType>::value());

    if( dbValues.empty() )
    {
        return boost::none;
    }

    return BehaviorType{ paoId, std::move(dbValues), Behaviors::DeviceBehavior::behavior_report_tag{} };
}


auto BehaviorManager::loadBehavior(const long paoId, const std::string& behaviorType) -> BehaviorValues
{
    return queryDatabaseForBehaviorValues(paoId, behaviorType, behaviorSql);
}

auto BehaviorManager::loadBehaviorReport(const long paoId, const std::string& behaviorType) -> BehaviorValues
{
    return queryDatabaseForBehaviorValues(paoId, behaviorType, behaviorReportSql);
}


auto BehaviorManager::queryDatabaseForBehaviorValues(const long paoId, const std::string& behaviorType, const std::string& sql) -> BehaviorValues
{
    Database::DatabaseConnection conn;
    Database::DatabaseReader rdr{ conn, sql };

    rdr << paoId << behaviorType;

    rdr.execute();

    BehaviorValues dbValues;

    while( rdr() )
    {
        const auto name  = rdr["name" ].as<std::string>();
        const auto value = rdr["value"].as<std::string>();
        
        dbValues.emplace(name, value);
    }

    return dbValues;
}

template IM_EX_CONFIG auto BehaviorManager::getBehaviorForPao   <RfnDataStreamingBehavior>(const long paoId) -> boost::optional<RfnDataStreamingBehavior>;
template IM_EX_CONFIG auto BehaviorManager::getDeviceStateForPao<RfnDataStreamingBehavior>(const long paoId) -> boost::optional<RfnDataStreamingBehavior>;

IM_EX_CONFIG std::unique_ptr<BehaviorManager> gBehaviorManager(new BehaviorManager);

}

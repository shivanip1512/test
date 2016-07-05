#pragma once

#include "dlldefs.h"

#include <boost/optional.hpp>

#include <string>
#include <memory>

namespace Cti {

class IM_EX_CONFIG BehaviorManager
{
public:
    virtual ~BehaviorManager() = default;  //  for unit test override

    template<typename BehaviorType>
    static auto getBehaviorForPao(const long paoId) -> BehaviorType;

    template<typename BehaviorType>
    static auto getDeviceStateForPao(const long paoId) -> boost::optional<BehaviorType>;

protected:
    using BehaviorValues = std::map<std::string, std::string>;

    virtual BehaviorValues loadBehavior      (const long paoId, const std::string& type);
    virtual BehaviorValues loadBehaviorReport(const long paoId, const std::string& type);

private:
    BehaviorValues queryDatabaseForBehaviorValues(const long paoId, const std::string& type, const std::string& sql);
};

struct BehaviorNotFoundException : std::exception
{
    BehaviorNotFoundException(long paoId, std::string type)
    {}
};

extern IM_EX_CONFIG std::unique_ptr<BehaviorManager> gBehaviorManager;

}

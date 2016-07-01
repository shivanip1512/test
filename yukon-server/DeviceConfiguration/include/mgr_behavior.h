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
    static BehaviorType getBehaviorForPao(const long paoId);

    template<typename BehaviorType>
    static boost::optional<BehaviorType> getDeviceStateForPao(const long paoId);

protected:
    template<typename BehaviorType>
    static BehaviorType loadBehaviorForPao(const long paoId, const std::string sql);
private:
    template<typename BehaviorType>
    static std::string getDatabaseNameFor();
};

struct BehaviorNotFoundException : std::exception
{
    BehaviorNotFoundException(long paoId, std::string type)
    {}
};

extern IM_EX_CONFIG std::unique_ptr<BehaviorManager> gBehaviorManager;

}

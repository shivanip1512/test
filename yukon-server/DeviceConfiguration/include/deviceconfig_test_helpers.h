#pragma once

#include "mgr_behavior.h"

namespace Cti {
namespace Test {

struct test_BehaviorManager : public BehaviorManager
{
    BehaviorValues behaviorValues;
    BehaviorValues behaviorReport;

    BehaviorValues loadBehavior(const long paoId, const std::string& behaviorType) override
    {
        return behaviorValues;
    }

    BehaviorValues loadBehaviorReport(const long paoId, const std::string& behaviorType) override
    {
        return behaviorReport;
    }
};

class Override_BehaviorManager
{
    std::unique_ptr<Cti::BehaviorManager> _oldBehaviorManager;

public:

    test_BehaviorManager *behaviorManagerHandle;

    Override_BehaviorManager()
    {
        auto b = std::make_unique<test_BehaviorManager>();

        behaviorManagerHandle = b.get();

        _oldBehaviorManager = std::move(b);
        _oldBehaviorManager.swap(Cti::gBehaviorManager);
    }

    ~Override_BehaviorManager()
    {
        _oldBehaviorManager.swap(Cti::gBehaviorManager);
    }
};

}
}

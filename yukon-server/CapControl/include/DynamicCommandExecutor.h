#pragma once

#include "DynamicCommand.h"
#include "ccexecutor.h"

#include <boost/shared_ptr.hpp>

namespace Cti {
namespace CapControl {

class DynamicCommandExecutor : public CtiCCExecutor
{
    public:
        DynamicCommandExecutor(DynamicCommand* dynamicCommand);

        virtual void execute();

    private:

        bool validatePointResponseDeltaUpdate();
        bool executePointResponseDeltaUpdate();

        boost::shared_ptr<DynamicCommand> _dynamicCommand;
};

}
}

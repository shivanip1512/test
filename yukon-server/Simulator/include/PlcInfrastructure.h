#pragma once

#include "Mct410.h"

#include "EmetconWords.h"
#include "ScopedLogger.h"

#include "Types.h"

#include "smartmap.h"

#include "PlcBehavior.h"
#include "BehaviorCollection.h"

namespace Cti {
namespace Simulator {

class PlcInfrastructure
{
private:

    typedef boost::shared_ptr<Mct410Sim> mct_ptr_t;

    //  using smartmap because it's threadsafe, complete with readers/writer mux
    typedef CtiSmartMap<Mct410Sim> mct_map_t;

    mct_map_t _mcts;

    BehaviorCollection<PlcBehavior> _behaviorCollection;

    mct_map_t::ptr_type getMct(const words_t &request_words);

    std::string  _plcTagIn;
    std::string _plcTagOut;

public:

    PlcInfrastructure();

    void setBehavior(std::unique_ptr<PlcBehavior> &&behavior);

    void processMessage(bytes &buf, Logger &logger);

    bool oneWayCommand(const bytes &request, Logger &logger);
    bool twoWayCommand(const bytes &request, bytes &reply_buf, Logger &logger);
};

}
}


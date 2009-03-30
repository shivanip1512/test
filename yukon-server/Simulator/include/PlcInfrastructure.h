#pragma once

#include "Mct410.h"

#include "EmetconWords.h"

#include "Types.h"

#include "smartmap.h"

namespace Cti {
namespace Simulator {

class PlcInfrastructure
{
private:

    typedef boost::shared_ptr<Mct410Sim> mct_ptr_t;

    //  using smartmap because it's threadsafe, complete with readers/writer mux
    typedef CtiSmartMap<Mct410Sim> mct_map_t;

    mct_map_t _mcts;

    bool getMct(const words_t &request_words, mct_map_t::ptr_type &mct);

public:

    PlcInfrastructure();

    bool oneWayCommand(const bytes &request);
    bool twoWayCommand(const bytes &request, bytes &reply_buf);
};

}
}


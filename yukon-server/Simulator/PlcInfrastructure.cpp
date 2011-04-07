#include "yukon.h"

#include "plcinfrastructure.h"

#include "boostutil.h"

#include <boost/bind.hpp>

using namespace std;

namespace Cti {
namespace Simulator {

PlcInfrastructure::PlcInfrastructure()
{
}


bool PlcInfrastructure::oneWayCommand(const bytes &request)
{
//  TODO-P4: True broadcast/listen capability.
//             Eventually, I'd like to see:
//               *  the PlcInfrastructure class transmit bytes (bits?) to Feeder objects
//               *  Feeder objects transmit bytes (bits?) to MCTs
//               *  MCTs decode bytes (bits?) to Emetcon words
//             This goes far beyond the scope of the current changes, so for now, the PlcInfrastructure decodes
//               B words and sends them to the appropriate end device.
//             Also, to be completely accurate, this should know about the CPSK preambles, but passing mangled byte (bit?)
//               buffers should be sufficient to indicate crosstalk.

    words_t request_words;

    EmetconWord::restoreWords(request, request_words);

    mct_map_t::ptr_type mct;

    if( !getMct(request_words, mct) || !mct )
    {
        return false;
    }

    mct->write(request_words);

    return true;
}


bool PlcInfrastructure::twoWayCommand(const bytes &request, bytes &reply)
{
    words_t request_words;

    EmetconWord::restoreWords(request, request_words);

    mct_map_t::ptr_type mct;

    if( !getMct(request_words, mct) || !mct )
    {
        return false;
    }

    words_t result_words;

//  TODO-P4: See PlcInfrastructure::oneWayCommand()
    mct->read(request_words, result_words);

    copy(result_words.begin(),
         result_words.end(),
         EmetconWord::serializer(byte_appender(reply)));

    // Apply behaviors!
    processMessage(reply);

    return true;
}

void PlcInfrastructure::setBehavior(std::auto_ptr<PlcBehavior> behavior)
{
    _behaviorCollection.push_back(behavior);
}

bool PlcInfrastructure::processMessage(bytes &buf)
{
    return _behaviorCollection.processMessage(buf);
}

bool PlcInfrastructure::getMct(const words_t &request_words, mct_map_t::ptr_type &mct)
{
    if( request_words.empty() )
    {
        return false;
    }

    //  will need to add support for G words here
    if( !request_words[0] || request_words[0]->type != EmetconWord::WordType_B )
    {
        return false;
    }

    unsigned dlc_address = (static_cast<const EmetconWordB *>(request_words[0].get()))->dlc_address;

    //  if we can't find it, insert a new one
    if( !(mct = _mcts.find(dlc_address)) )
    {
        _mcts.insert(dlc_address, new Mct410Sim(dlc_address));

        mct = _mcts.find(dlc_address);
    }

    return mct;  //  conversion to bool
}


}
}


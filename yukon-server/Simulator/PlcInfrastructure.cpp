#include "precompiled.h"

#include "plcinfrastructure.h"

#include "boostutil.h"

using namespace std;

namespace Cti {
namespace Simulator {

PlcInfrastructure::PlcInfrastructure() :
    _plcTagIn("PLC-IN"),
    _plcTagOut("PLC-OUT")
{
}


bool PlcInfrastructure::oneWayCommand(const bytes &request, Logger &logger)
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

    ScopedLogger scope = logger.getNewScope(_plcTagOut);

    words_t request_words;

    EmetconWord::restoreWords(request, request_words);

    mct_map_t::ptr_type mct = getMct(request_words);

    if( ! mct )
    {
        return false;
    }

    mct->write(request_words);

    return true;
}


bool PlcInfrastructure::twoWayCommand(const bytes &request, bytes &reply, Logger &logger)
{
    ScopedLogger scope = logger.getNewScope(_plcTagOut);

    words_t request_words;

    EmetconWord::restoreWords(request, request_words);

    mct_map_t::ptr_type mct = getMct(request_words);

    if( ! mct )
    {
        return false;
    }

    words_t result_words;

//  TODO-P4: See PlcInfrastructure::oneWayCommand()
    mct->read(request_words, result_words, scope);

    auto reply_appender = byte_appender { reply };

    for( const auto& word : result_words )
    {
        word->serialize(reply_appender);
    }

    {
        ScopedLogger plcInScope = scope.getNewScope(_plcTagIn);
        // Apply behaviors!
        processMessage(reply, plcInScope);
    }

    return true;
}

void PlcInfrastructure::setBehavior(std::unique_ptr<PlcBehavior> &&behavior)
{
    _behaviorCollection.push_back(std::move(behavior));
}

void PlcInfrastructure::processMessage(bytes &buf, Logger &logger)
{
    _behaviorCollection.processMessage(buf, logger);
}

PlcInfrastructure::mct_map_t::ptr_type PlcInfrastructure::getMct(const words_t &request_words)
{
    if( request_words.empty() )
    {
        return nullptr;
    }

    //  will need to add support for G words here
    if( !request_words[0] || request_words[0]->type != EmetconWord::WordType_B )
    {
        return nullptr;
    }

    unsigned dlc_address = (static_cast<const EmetconWordB *>(request_words[0].get()))->dlc_address;

    mct_map_t::ptr_type mct = _mcts.find(dlc_address);

    //  if we can't find it, insert a new one
    if( ! mct )
    {
        _mcts.insert(dlc_address, new Mct410Sim(dlc_address));

        mct = _mcts.find(dlc_address);
    }

    return mct;
}


}
}


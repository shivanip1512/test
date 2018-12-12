#include "precompiled.h"

#include "DispatchPointRegistry.h"

#include <boost/range/adaptor/map.hpp>
#include <boost/range/algorithm/copy.hpp>

#include <experimental/map>
#include <iterator>



void DispatchPointRegistry::updateRegistry( DispatchConnectionPtr dispatchConnection )
{
    using std::experimental::fundamentals_v2::erase_if;

    CTILOCKGUARD( CtiMutex, guard, _mutex );

    if ( dispatchConnection && dispatchConnection->valid() )     // We have a good dispatch connection
    {
        // Remove the pending point IDs with zero counts, that just means that their registration
        //  state isn't changing.

        erase_if( _pending,
                      []( auto element )
                      {
                          return element.second == 0;
                      } );

        if ( ! _pending.empty() )       // Something to do!
        {            
            std::set<long>  addList, removeList;

            const bool reregisterAll = _registered.empty();

            for ( auto [ pointID, count ] : _pending )
            {
                _registered[ pointID ] += count;

                if ( _registered[ pointID ] <= 0 )
                {
                    removeList.insert( pointID );
                }
                else if ( _registered[ pointID ] == count )
                {
                    addList.insert( pointID );
                }
            }

            _pending.clear();

            erase_if( _registered,
                          []( auto element )
                          {
                              return element.second <= 0;
                          } );

            if ( reregisterAll )
            {
                removeList.clear();

                boost::copy( _registered
                                | boost::adaptors::map_keys,
                             std::inserter( addList, addList.end() ) );
            }

            if ( ! addList.empty() )
            {
                dispatchConnection->registerForPoints(   nullptr   , addList );
            }

            if ( ! removeList.empty() )
            {
                dispatchConnection->unRegisterForPoints(   nullptr   , removeList );
            }

            dispatchConnection->refreshPointRegistration();
        }
    }
    else
    {
        // Our dispatch connection is bad for whatever reason, move all the currently registered
        //  points into the pending bucket so we register for them all again when dispatch reconnects.

        if ( ! _registered.empty() )
        {
            for ( auto [ pointID, count ] : _registered )
            {
                _pending[ pointID ] += count;
            }

            _registered.clear();
        }
    }
}


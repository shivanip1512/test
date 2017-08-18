#include "precompiled.h"

#include "PointDataHandler.h"
#include "PointDataListener.h"
#include "msg_pdata.h"


PointDataHandler::PointDataHandler()
{
}

void PointDataHandler::clear()
{
    _pointPao.clear();
}

auto PointDataHandler::getEntry( const long pointID, const long paoID ) -> PointIDtoPaoIDMap::const_iterator
{
    for ( auto range = _pointPao.equal_range( pointID );
          range.first != range.second;
          ++range.first )
    {
        if ( range.first->second == paoID )
        {
            return range.first;
        }
    }

    return std::end( _pointPao );
}

void PointDataHandler::addPointOnPao( const long pointID, const long paoID )
{
    auto entry = getEntry( pointID, paoID );

    if ( entry == std::end( _pointPao ) )
    {
        _pointPao.emplace( std::make_pair( pointID, paoID ) );
    }

    registerForPoint( pointID );
}

void PointDataHandler::removePointOnPao( const long pointID, const long paoID )
{
    auto entry = getEntry( pointID, paoID );

    if ( entry != std::end( _pointPao ) )
    {
        _pointPao.erase( entry );
    }

    unRegisterForPoint( pointID );
}

/**
 * Calls into the paoHandler to give it the Point Data message.
 *
 * @param message
 *
 */
void PointDataHandler::processIncomingPointData( const CtiPointDataMsg & message )
{
    for ( auto range = _pointPao.equal_range( message.getId() );
          range.first != range.second;
          ++range.first )
    {
        _pointDataListener->handlePointDataByPaoId( range.first->second, message );
    }
}

/**
 *
 *
 * @param message
 */
void PointDataHandler::processNewMessage(CtiMessage* message)
{

}

/**
 * Returns a list of all point id's being tracked.
 *
 * @return std::list<int>
 */
void PointDataHandler::getAllPointIds(std::set<long>& pointIds)
{
    for ( const auto & entry : _pointPao )
    {
        pointIds.insert( entry.first );
    }
}

/**
 * Sets the Handler, not responsible for deleting the old one.
 *
 * @param pointDataListener
 */
void PointDataHandler::setPointDataListener(PointDataListener* pointDataListener)
{
    _pointDataListener = pointDataListener;
}


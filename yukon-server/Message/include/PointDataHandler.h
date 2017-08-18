#pragma once

#include "yukon.h"
#include "MessageListener.h"

class PointDataListener;
class CtiPointDataMsg;
class CtiMessage;


class IM_EX_MSG PointDataHandler : public MessageListener
{
public:
    PointDataHandler();

    void clear();

    void addPointOnPao( const long pointID, const long paoID );
    void removePointOnPao( const long pointID, const long paoID );

    //Change this one to use the new one (processNewMessage) and not be called from the store.
    void processIncomingPointData( const CtiPointDataMsg & message );
    void processNewMessage(CtiMessage* message) override;

    void getAllPointIds(std::set<long>& pointIds);

    void setPointDataListener(PointDataListener* pointDataListener);

private:

    virtual void registerForPoint( const long pointId ) = 0;
    virtual void unRegisterForPoint( const long pointId ) = 0;

    using PointIDtoPaoIDMap = std::multimap<long, long>;

    PointIDtoPaoIDMap   _pointPao;

    auto getEntry( const long pointID, const long paoID ) -> PointIDtoPaoIDMap::const_iterator;

    PointDataListener * _pointDataListener;
};


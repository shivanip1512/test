#include "precompiled.h"
#include "LMScheduledMessageHolder.h"

using std::unique_ptr;

// Returns the oldest message, if the current time is >= the time for that message.
unique_ptr<CtiRequestMsg> LMScheduledMessageHolder::getAvailableMessage()
{
    auto &index = _messageHolder.get<by_timestamp>();
    ScheduledMessageContainerType::iterator front = index.begin();

    if ( front != index.end() && front->time <= CtiTime::now() )
    {
        unique_ptr<CtiRequestMsg> retVal = std::move(front->message);
        index.erase(front);
        return retVal;
    }
    return nullptr;
}

// Adds a message associated with a time and groupId.
void LMScheduledMessageHolder::addMessage(CtiTime time, long groupId, unique_ptr<CtiRequestMsg> message)
{
    _messageHolder.insert( MessageHolderObject{ time, groupId, std::move(message) });
}

// Returns TRUE if there is a message with this groupId, false otherwise
bool LMScheduledMessageHolder::containsMessageForGroup(long groupId)
{
    auto &index = _messageHolder.get<by_group>();

    auto result = index.find( groupId );

    if ( result != index.end() )
    {
        return true;
    }

    return false;
}

// Removes all messages that match the requested groupId
int LMScheduledMessageHolder::clearMessagesForGroup(long groupId)
{
    auto &index = _messageHolder.get<by_group>();

    return index.erase( groupId );
}

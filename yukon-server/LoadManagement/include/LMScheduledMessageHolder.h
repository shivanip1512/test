#pragma once
#include "ctitime.h"
#include "msg_pcrequest.h"

#include <boost/multi_index_container.hpp>
#include <boost/multi_index/ordered_index.hpp>
#include <boost/multi_index/hashed_index.hpp>
#include <boost/multi_index/member.hpp>
#include <boost/multi_index/composite_key.hpp>

using namespace boost::multi_index;


class LMScheduledMessageHolder
{
public:
    std::unique_ptr<CtiRequestMsg> getAvailableMessage(const CtiTime &currentTime);
    void addMessage(const CtiTime &time, long groupId, std::unique_ptr<CtiRequestMsg> message);
    bool containsMessageForGroup(long groupId);
    int clearMessagesForGroup(long groupId);
private:
    // Boost Multi Index Begin ***************************

    // index tags to provide a nicer interface to get<N>()

    struct by_timestamp    { };     // get<0>
    struct by_group   { };     // get<1>

    struct MessageHolderObject
    {
        CtiTime time;
        long groupId;
        mutable std::unique_ptr<CtiRequestMsg> message; // NOTE the mutable here is what makes unique_ptr work
    };

    using ScheduledMessageContainerType =
        multi_index_container<
            MessageHolderObject,
            indexed_by<
                ordered_non_unique<tag<by_timestamp>,member<MessageHolderObject, CtiTime, &MessageHolderObject::time> >, 
                hashed_non_unique<tag<by_group>,member<MessageHolderObject, long, &MessageHolderObject::groupId> >
            >
        >;

    ScheduledMessageContainerType  _messageHolder;
    // Boost Multi Index End ***************************
};

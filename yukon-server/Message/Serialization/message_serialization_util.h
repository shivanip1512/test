#pragma once

#include "ctitime.h"
#include "message.h"
#include "Thrift/Message_types.h"
#include "message_factory.h"
#include <boost/type_traits.hpp>

namespace Cti {
namespace Messaging {
namespace Serialization {

template<class T>
struct IteratorValue
{
    // remove pointer from the iterator value_type.
    typedef typename boost::remove_pointer<typename std::iterator_traits<typename T::iterator>::value_type>::type type;
};

/*-----------------------------------------------------------------------------
    Transform a container to a new container type,

    Apply op() on each item, where op() takes a value in argument
-----------------------------------------------------------------------------*/
template <class ocont_t, class icont_t>
inline ocont_t transformContainer( const icont_t& icont, typename ocont_t::value_type (*op)( const typename IteratorValue<icont_t>::type ))
{
    ocont_t ocont;

    ocont.reserve( icont.size() );

    for each( const IteratorValue<icont_t>::type item in icont )
    {
        ocont.push_back( op( item ));
    }

    return ocont;
}

/*-----------------------------------------------------------------------------
    Transform a container to a new container type,

    Apply op() on each item, where op() takes a reference in argument
-----------------------------------------------------------------------------*/
template <class ocont_t, class icont_t>
inline ocont_t transformContainer( const icont_t& icont, typename ocont_t::value_type (*op)( const typename IteratorValue<icont_t>::type& ))
{
    ocont_t ocont;

    ocont.reserve( icont.size() );

    for each( const IteratorValue<icont_t>::type& item in icont )
    {
        ocont.push_back( op( item ));
    }

    return ocont;
}

/*-----------------------------------------------------------------------------
    Transform a container of pointers to a new container type,

    Apply op() on each item, where op() takes a pointer in argument
-----------------------------------------------------------------------------*/
template <class ocont_t, class icont_t>
inline ocont_t transformContainer( const icont_t& icont, typename ocont_t::value_type (*op)( const typename IteratorValue<icont_t>::type* ))
{
    ocont_t ocont;

    ocont.reserve( icont.size() );

    for each( const IteratorValue<icont_t>::type* item in icont )
    {
        ocont.push_back( op( item ));
    }

    return ocont;
}

/*-----------------------------------------------------------------------------
    Transform a container to a new container type,

    Apply op() on each item, where op() takes a reference in argument and
    returns a MessagePtr type
-----------------------------------------------------------------------------*/
template <class ocont_t, class icont_t>
inline ocont_t transformContainer( const icont_t& icont, typename MessagePtr<typename ocont_t::value_type>::type (*op)( const typename IteratorValue<icont_t>::type& ))
{
    ocont_t ocont;

    ocont.reserve( icont.size() );

    for each( const IteratorValue<icont_t>::type& item in icont )
    {
        ocont.push_back( *op( item ));
    }

    return ocont;
}

/*-----------------------------------------------------------------------------
   Deserialize an item and return a pointer
-----------------------------------------------------------------------------*/
template <typename out_t, typename in_t>
inline out_t* deserializeToPtr( const in_t& item )
{
    return deserialize( item ).release();
}

/*-----------------------------------------------------------------------------
   Serialize a pointer to an item
-----------------------------------------------------------------------------*/
template <typename out_t, typename in_t>
inline out_t serializeFromPtr( const in_t* item )
{
    return out_t( *serialize( *item ));
}

/*-----------------------------------------------------------------------------
    convert ctitime to milliseconds
-----------------------------------------------------------------------------*/
inline int64_t CtiTimeToMilliseconds( const CtiTime& t )
{
    return ((int64_t)t.seconds())*1000;
}

/*-----------------------------------------------------------------------------
    convert milliseconds to ctitime
-----------------------------------------------------------------------------*/
inline CtiTime MillisecondsToCtiTime( const int64_t t )
{
    return CtiTime((unsigned long)(t/1000));
}

/*-----------------------------------------------------------------------------
    serialize template message to Thrift generic message
-----------------------------------------------------------------------------*/
template<typename MessageBase_t>
inline Thrift::GenericMessage serializeGeneric( const MessageBase_t& imsg, const MessageFactory<MessageBase_t>& factory )
{
    Thrift::GenericMessage omsg;

    std::string messageType;
    std::vector<unsigned char> obytes;

    messageType = factory.serialize( imsg, obytes );

    // if an error occur, messageType field and payload will remain empty
    omsg.__set__messageType ( messageType );
    omsg.__set__payload     ( std::string( obytes.begin(), obytes.end() ));

    return omsg;
}

/*-----------------------------------------------------------------------------
    deserialize Thrift generic message to CtiMessage
-----------------------------------------------------------------------------*/
template<typename MessageBase_t>
inline typename MessagePtr<MessageBase_t>::type deserializeGeneric( const Thrift::GenericMessage& imsg, const MessageFactory<MessageBase_t>& factory )
{
    const std::vector<unsigned char> obytes( imsg._payload.begin(), imsg._payload.end() );

    return factory.deserialize( imsg._messageType, obytes );
}

/*-----------------------------------------------------------------------------
    serialize CtiMessage to Thrift generic message
-----------------------------------------------------------------------------*/
inline Thrift::GenericMessage serializeGenericMessage( const ::CtiMessage* imsg )
{
    if( imsg )
    {
        return serializeGeneric( *imsg, g_messageFactory );
    }

    Thrift::GenericMessage emptyMsg;

    emptyMsg.__set__messageType ( std::string() );
    emptyMsg.__set__payload     ( std::string() );

    return emptyMsg;
}

/*-----------------------------------------------------------------------------
    deserialize Thrift generic message to CtiMessage
-----------------------------------------------------------------------------*/
inline MessagePtr<::CtiMessage>::type deserializeGenericMessage( const Thrift::GenericMessage& imsg )
{
    return deserializeGeneric( imsg, g_messageFactory );
}


}
}
}

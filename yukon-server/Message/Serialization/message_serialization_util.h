#pragma once

#include "ctitime.h"
#include "message.h"
#include "Thrift/Message_types.h"
#include "message_factory.h"
#include <boost/type_traits.hpp>

namespace Cti {
namespace Messaging {
namespace Serialization {

/*-----------------------------------------------------------------------------
    structs for template type definitions
-----------------------------------------------------------------------------*/
template<class T>
struct IteratorValue
{
    // finds a container iterator true type
    typedef typename std::iterator_traits<typename T::iterator>::value_type type;

    // remove pointer from the iterator value_type.
    typedef typename boost::remove_pointer<type>::type no_pointer_type;
};

template< typename out_t, typename in_t >
struct SerializationFunc
{
    // serialization function prototype
    typedef typename MessagePtr<out_t>::type ( *type ) ( const in_t& );
};

namespace TransformImpl {

/*-----------------------------------------------------------------------------
    Explicit specialized template structures to apply operations to
    transformContainer operation function
-----------------------------------------------------------------------------*/
template< typename out_t, typename in_t >
struct Adapator
{
    // Serialization of an element and return a copy
    static out_t op( const in_t& input, typename SerializationFunc<out_t, in_t>::type op )
    {
        return *op( input );
    }
};

template< typename out_t, typename in_t >
struct Adapator< out_t, in_t* >
{
    // Serialization of a pointer and return a copy
    static out_t op( const in_t* input, typename SerializationFunc<out_t, in_t>::type op )
    {
        return *op( *input );
    }
};

template< typename out_t, typename in_t >
struct Adapator< out_t*, in_t >
{
    // Serialization of a element and return a pointer that the caller owns
    static out_t* op( const in_t& input, typename SerializationFunc<out_t, in_t>::type op )
    {
        return op( input ).release();
    }
};

} // namespace TransformImpl

/*-----------------------------------------------------------------------------
    Transform a container to a new container type using implicit casting
-----------------------------------------------------------------------------*/
template< class ocont_t, class icont_t >
inline ocont_t transformContainer( const icont_t& icont )
{
    return ocont_t( icont.begin(), icont.end() );
}

/*-----------------------------------------------------------------------------
    Transform a container to a new container type, and applies op() on each element

    Apply op() on each item, where op() takes a value in argument
-----------------------------------------------------------------------------*/
template< class ocont_t, class icont_t, typename func_t >
inline ocont_t transformContainer( const icont_t& icont, func_t op )
{
    ocont_t ocont;

    ocont.reserve( icont.size() );

    std::transform( icont.begin(), icont.end(), std::back_inserter(ocont), op );

    return ocont;
}

/*-----------------------------------------------------------------------------
    Transform a container to a new container using serialization functions
-----------------------------------------------------------------------------*/
template< class ocont_t, class icont_t >
inline ocont_t transformContainer( const icont_t& icont, typename SerializationFunc< typename IteratorValue<ocont_t>::no_pointer_type, typename IteratorValue<icont_t>::no_pointer_type >::type op )
{
    ocont_t ocont;

    ocont.reserve( icont.size() );

    for each( const IteratorValue<icont_t>::type& item in icont )
    {
        ocont.push_back( TransformImpl::Adapator< IteratorValue<ocont_t>::type, IteratorValue<icont_t>::type >::op( item, op ));
    }

    return ocont;
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

    std::vector<unsigned char> obytes;

    // if an error occur, messageType field and payload will remain empty
    omsg._messageType = factory.serialize( imsg, obytes );

    auto obytes_ptr = reinterpret_cast<const char*>(obytes.data());

    omsg._payload.assign( obytes_ptr, obytes.size() );

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
    if( !imsg )
    {
        Thrift::GenericMessage emptyMsg;

        emptyMsg.__set__messageType ( std::string() );
        emptyMsg.__set__payload     ( std::string() );

        return emptyMsg;
    }

    return serializeGeneric( *imsg, g_messageFactory );
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

#pragma once

#include "message.h"
#include "amq_constants.h"

#include <thrift/thrift.h>
#include <thrift/transport/TBufferTransports.h>
#include <thrift/protocol/tBinaryProtocol.h>

#include <boost/shared_ptr.hpp>
#include <boost/optional.hpp>

#include <string>
#include <map>

namespace Cti {
namespace Messaging {
namespace Serialization {

// template alias workaround
template <typename Message_t>
struct MessagePtr
{
    typedef std::auto_ptr<Message_t> type;
};

/*-----------------------------------------------------------------------------
    Message serializer base Class
-----------------------------------------------------------------------------*/
template <typename MessageBase_t>
class SerializerBase
{
public:
    virtual const std::string serialize( const MessageBase_t& imsg, std::vector<unsigned char>& obytes ) const = 0;
};

/*-----------------------------------------------------------------------------
    Message deserializer base class
-----------------------------------------------------------------------------*/
template <typename MessageBase_t>
class DeserializerBase
{
public:
    virtual typename MessagePtr<MessageBase_t>::type deserialize( const std::vector<unsigned char>& ibytes ) const = 0;
};

/*-----------------------------------------------------------------------------
    Message factory
-----------------------------------------------------------------------------*/
template <typename MessageBase_t>
class MessageFactory
{
    // could be boost::ptr_map to automatically handle the memory
    std::map<std::string, SerializerBase<MessageBase_t> *>   _serializers;
    std::map<std::string, DeserializerBase<MessageBase_t> *> _deserializers;

    std::string _prefix;

public:
    MessageFactory(const std::string &prefix) :
        _prefix(prefix)
    {
    }

    // registration method
    template <typename Msg_t, typename ThriftMsg_t>
    void registerSerializer( typename MessagePtr<ThriftMsg_t>::type (*thriftPopulator) (const Msg_t&),
                             typename MessagePtr<Msg_t>::type (*messagePopulator) (const ThriftMsg_t&),
                             const std::string &msgType )
    {
        const std::string qualifiedMsgType = _prefix + msgType;

        if( thriftPopulator != NULL )
        {
            _serializers[typeid(Msg_t).name()] = new Serializer<MessageBase_t, Msg_t, ThriftMsg_t>( thriftPopulator, qualifiedMsgType );
        }

        if( messagePopulator != NULL )
        {
            _deserializers[qualifiedMsgType] = new Deserializer<MessageBase_t, Msg_t, ThriftMsg_t>( messagePopulator );
        }
    }

    const std::string serialize( const MessageBase_t& imsg, std::vector<unsigned char>& obytes ) const
    {
        std::map<std::string, SerializerBase<MessageBase_t> *>::const_iterator itr;

        itr = _serializers.find( typeid(imsg).name() );
        if( itr != _serializers.end() )
        {
            try
            {
                // return the message type if all went well
                return itr->second->serialize( imsg, obytes );
            }
            catch( apache::thrift::TException &e )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Thrift EXCEPTION **** while serializing object type \"" << typeid(imsg).name() << "\" " << __FILE__ << " (" << __LINE__ << ")" << e.what() << std::endl;
                }
            }
        }

        // return empty string if we could not find a serializer
        return std::string();
    }

    typename MessagePtr<MessageBase_t>::type deserialize( const std::string& msgType, const std::vector<unsigned char>& ibytes ) const
    {
        std::map<std::string, DeserializerBase<MessageBase_t> *>::const_iterator itr;

        itr = _deserializers.find( msgType );
        if( itr != _deserializers.end() )
        {
            try
            {
                // return a new instance ot the MessageBase_t
                return itr->second->deserialize( ibytes );
            }
            catch( apache::thrift::TException &e )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Thrift EXCEPTION **** while deserializing message type \"" << msgType << "\" " << __FILE__ << " (" << __LINE__ << ")" << e.what() << std::endl;
                }
            }
        }

        // return NULL content if we could not find a deserializer
        return typename MessagePtr<MessageBase_t>::type();
    }
};

/*-----------------------------------------------------------------------------
    Message serializer derived class
-----------------------------------------------------------------------------*/
template <typename MessageBase_t, typename Msg_t, typename ThriftMsg_t>
class Serializer : public SerializerBase<MessageBase_t>
{
    typedef typename MessagePtr<ThriftMsg_t>::type (*ThriftMsgPopulator) (const Msg_t&);

    const ThriftMsgPopulator _thriftPopulateFn;
    const std::string        _msgType;

public:
    Serializer( ThriftMsgPopulator fn, const std::string& msgType ) :
        _thriftPopulateFn(fn),
        _msgType(msgType)
    {
    }

    virtual const std::string serialize( const MessageBase_t& imsg, std::vector<unsigned char>& obytes ) const
    {
        if( const Msg_t *p_imsg = dynamic_cast<const Msg_t *>(&imsg) )
        {
            // create memory buffer and binary protocol
            boost::shared_ptr<apache::thrift::transport::TMemoryBuffer> transport( new apache::thrift::transport::TMemoryBuffer() );
            apache::thrift::protocol::TBinaryProtocol protocol( transport );

            // call function pointer to translate from MessageBase_t to thrift message
            MessagePtr<ThriftMsg_t>::type omsg = _thriftPopulateFn( *p_imsg );

            if( !omsg.get() )
            {
                // function returned null pointer... return empty string
                return std::string();
            }

            // write message to buffer with the protocol
            omsg->write( &protocol );

            unsigned char* bufPtr  = 0;
            unsigned int   bufSize = 0;

            transport->getBuffer(&bufPtr, &bufSize);

            if( !bufPtr || !bufSize )
            {
                // serialization failed... return empty string
                return std::string();
            }

            obytes = std::vector<unsigned char>(bufPtr, bufPtr+bufSize);

            // return the message type if all goes well
            return _msgType;
        }

        // return empty string if casting fails
        return std::string();
   }
};


//  Throws apache::thrift::TException
template<class ThriftMsg_t>
ThriftMsg_t DeserializeThriftBytes( const std::vector<unsigned char>& ibytes )
{
    ThriftMsg_t imsg;

    // create memory buffer and binary protocol
    boost::shared_ptr<apache::thrift::transport::TMemoryBuffer> transport( new apache::thrift::transport::TMemoryBuffer( const_cast<uint8_t*>(&(ibytes.front())), ibytes.size() ));
    apache::thrift::protocol::TBinaryProtocol protocol( transport );

    // read protocol and populate thrift message
    imsg.read( &protocol );

    return imsg;
}


/*-----------------------------------------------------------------------------
    Message deserializer derived class
-----------------------------------------------------------------------------*/
template <typename MessageBase_t, typename Msg_t, typename ThriftMsg_t>
class Deserializer : public DeserializerBase<MessageBase_t>
{
    typedef typename MessagePtr<Msg_t>::type (*MessagePopulator) (const ThriftMsg_t&);
    const MessagePopulator _messagePopulateFn;

public:
    Deserializer(MessagePopulator fn) :
        _messagePopulateFn(fn)
    {
    }

    //  Throws apache::thrift::TException
    virtual typename MessagePtr<MessageBase_t>::type deserialize( const std::vector<unsigned char>& ibytes ) const
    {
        // deserialize bytes into Thrift message - may throw apache::thrift::TException
        ThriftMsg_t imsg = DeserializeThriftBytes<ThriftMsg_t>( ibytes );

        // call function pointer to translate from thrift message to MessageBase_t
        return typename MessagePtr<MessageBase_t>::type( _messagePopulateFn( imsg ).release() );
    }
};


template<typename Msg>
struct IM_EX_MSG MessageSerializer
{
    static std::vector<unsigned char> &serialize( const Msg &m );
    static boost::optional<Msg> deserialize( const std::vector<unsigned char> &buf );
};


IM_EX_MSG extern MessageFactory<::CtiMessage> g_messageFactory;

}
}
}

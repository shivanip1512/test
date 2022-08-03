#include "precompiled.h"

#include "rf_der_processor.h"

#include "amq_connection.h"
#include "amq_queues.h"
#include "dev_rfn.h"
#include "mgr_device.h"
#include "RfnEdgeDrMessaging.h"
#include "message_factory.h"

using Cti::Messaging::Rfn::E2eMessenger;
using Cti::Logging::Vector::Hex::operator<<;

namespace Cti::Pil
{

RfDerProcessor::RfDerProcessor( CtiDeviceManager & deviceManager )
    :   _deviceManager{ deviceManager }
{
    // empty
}

void RfDerProcessor::start()
{
    E2eMessenger::registerDataStreamingHandler(
        [ this ]( const E2eMessenger::Indication & msg )
        {
            CTILOG_INFO( dout, "DER packet received for " << msg.rfnIdentifier << "\n" << msg.payload );

            LockGuard guard( _packetMux );

            _packets.push_back( msg );
        } );
}

void RfDerProcessor::tick()
{
    using namespace Messaging::Rfn;
    using Messaging::Serialization::MessageSerializer;

    PacketQueue newPackets;

    {
        LockGuard g{ _packetMux };

        newPackets.swap( _packets );
    }

    for ( auto & packet : newPackets )
    {
        Messaging::Rfn::EdgeDrDataNotification  notification
        {
            0,                  // look this up below...
            packet.payload,
            0,                  // no groupID/usrMessageID since unsolicited...?
            std::nullopt
        };

        if ( auto device = _deviceManager.getDeviceByRfnIdentifier( packet.rfnIdentifier ) )
        {
            notification.paoId = device->getID();
        }
        else
        {
            CTILOG_WARN( dout, "Device not found for RfnIdentifier: " << packet.rfnIdentifier );

            notification.error = { ClientErrors::IdNotFound, "Device not found for RfnIdentifier: " + packet.rfnIdentifier.toString() };
        }

        if ( auto serializedNotification = Messaging::Serialization::serialize( notification ); ! serializedNotification.empty() )
        {
            Messaging::ActiveMQConnectionManager::enqueueMessage(
                Messaging::ActiveMQ::Queues::OutboundQueue::RfnEdgeDrDataNotification,
                serializedNotification );
        }
        else
        {
            CTILOG_WARN( dout, "Could not serialize DR Data Notification message" << FormattedList::of(
                "RFN Device", packet.rfnIdentifier
                ) );
        }
    }
}

}
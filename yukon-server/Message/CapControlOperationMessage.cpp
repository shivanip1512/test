#include "precompiled.h"
#include "CtiTime.h"
#include "CapControlOperationMessage.h"

#include <cms/StreamMessage.h>


namespace Cti           {
namespace Messaging     {
namespace CapControl    {


CapControlOperationMessage::CapControlOperationMessage( const int       deviceId,
                                                        const int       operationId,
                                                        const CtiTime & timestamp )
    : _deviceId( deviceId ),
      _operationId( operationId ),
      _timestamp( timestamp.seconds() )
{
    // empty...!
}


CapControlOperationMessage * CapControlOperationMessage::createOpenBankMessage( const int       deviceId,
                                                                                const CtiTime & timestamp)
{
    CapControlOperationMessage * message = new CapControlOperationMessage( deviceId, Operation_OpenBank, timestamp );

    return message;
}

CapControlOperationMessage * CapControlOperationMessage::createCloseBankMessage( const int       deviceId,
                                                                                 const CtiTime & timestamp)
{
    CapControlOperationMessage * message = new CapControlOperationMessage( deviceId, Operation_CloseBank, timestamp );

    return message;
}

CapControlOperationMessage * CapControlOperationMessage::createLowerTapMessage( const int       deviceId,
                                                                                const CtiTime & timestamp)
{
    CapControlOperationMessage * message = new CapControlOperationMessage( deviceId, Operation_LowerTap, timestamp );

    return message;
}

CapControlOperationMessage * CapControlOperationMessage::createRaiseTapMessage( const int       deviceId,
                                                                                const CtiTime & timestamp)
{
    CapControlOperationMessage * message = new CapControlOperationMessage( deviceId, Operation_RaiseTap, timestamp );

    return message;
}

CapControlOperationMessage * CapControlOperationMessage::createScanDeviceMessage( const int       deviceId,
                                                                                    const CtiTime & timestamp)
{
    CapControlOperationMessage * message = new CapControlOperationMessage( deviceId, Operation_ScanDevice, timestamp );

    return message;
}

CapControlOperationMessage * CapControlOperationMessage::createRefreshSystemMessage( const int       deviceId,
                                                                                    const CtiTime & timestamp)
{
    CapControlOperationMessage * message = new CapControlOperationMessage( deviceId, Operation_RefreshSystem, timestamp );

    return message;
}

void CapControlOperationMessage::streamInto( cms::StreamMessage & message ) const
{
    message.writeInt( _deviceId );
    message.writeInt( _operationId );
    message.writeLong( _timestamp );

}


}
}
}

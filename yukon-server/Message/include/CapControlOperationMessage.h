#pragma once

#include "dlldefs.h"
#include "StreamableMessage.h"

#include <vector>

class CtiTime;

namespace Cti           {
namespace Messaging     {
namespace CapControl    {

class IM_EX_MSG CapControlOperationMessage : public StreamableMessage
{
public:

    enum Operations
    {
        Operation_OpenBank = 1,
        Operation_CloseBank,
        Operation_RaiseTap,
        Operation_LowerTap,
        Operation_ScanDevice,
        Operation_RefreshSystem
    };

    static CapControlOperationMessage * createOpenBankMessage( const int       deviceId,
                                                               const CtiTime & timestamp);

    static CapControlOperationMessage * createCloseBankMessage( const int       deviceId,
                                                                const CtiTime & timestamp);

    static CapControlOperationMessage * createLowerTapMessage( const int       deviceId,
                                                               const CtiTime & timestamp);

    static CapControlOperationMessage * createRaiseTapMessage( const int       deviceId,
                                                               const CtiTime & timestamp);

    static CapControlOperationMessage * createScanDeviceMessage( const int       deviceId,
                                                                 const CtiTime & timestamp);

    static CapControlOperationMessage * createRefreshSystemMessage( const int       deviceId,
                                                                    const CtiTime & timestamp);

    virtual void streamInto( cms::StreamMessage & message ) const;

protected:

    int                 _deviceId;
    int                 _operationId;
    long                _timestamp;

    CapControlOperationMessage( const int       deviceId,
                                const int       opearationId,
                                const CtiTime & timestamp );
};
}}}

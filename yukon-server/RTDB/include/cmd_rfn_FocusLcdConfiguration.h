#pragma once

#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB RfnFocusLcdConfigurationCommand : public RfnCommand
{
public:

    struct DisplayItem
    {
        std::string metric;
        std::string alphamericId;
    };

    typedef std::vector<DisplayItem> DisplayItemVector;

    RfnFocusLcdConfigurationCommand();  //  read
    RfnFocusLcdConfigurationCommand( const DisplayItemVector & displayItems, unsigned char displayItemDuration );  //  write

    virtual RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response);
    virtual RfnCommandResult error(const CtiTime now, const YukonError_t error_code);

    boost::optional<DisplayItemVector> getDisplayItemsReceived() const;
    boost::optional<unsigned char>     getDisplayItemDurationReceived() const;

protected:

    virtual unsigned char getCommandCode() const;
    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

private:

    const boost::optional<Bytes>        _commandDataToSend;

    boost::optional<DisplayItemVector>  _displayItemsReceived;
    boost::optional<unsigned char>      _displayItemDurationReceived;

    Bytes createCommandData( const DisplayItemVector & displayItems, unsigned char displayItemDuration );
};

}
}
}

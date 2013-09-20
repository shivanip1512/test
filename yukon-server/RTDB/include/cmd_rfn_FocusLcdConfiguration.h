#pragma once

#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB RfnFocusLcdConfigurationCommand : public RfnCommand
{
public:

    struct ResultHandler
    {
        virtual void handleResult( const RfnFocusLcdConfigurationCommand &cmd ) = 0;
    };

    struct DisplayItem
    {
        std::string metric;
        std::string alpha;
    };

    typedef std::vector<DisplayItem> DisplayItemVector;

    RfnFocusLcdConfigurationCommand();  //  read
    RfnFocusLcdConfigurationCommand( const DisplayItemVector & displayItems, unsigned char displayItemDuration );  //  write

    virtual RfnResult decodeCommand(const CtiTime now, const RfnResponse &response);
    virtual RfnResult error(const CtiTime now, const YukonError_t error_code);

    boost::optional<DisplayItemVector> getDisplayItemReceived() const;
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

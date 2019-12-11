#include "precompiled.h"

#include "dev_rfn420centron.h"
#include "cmd_rfn_ConfigNotification.h"
#include "cmd_rfn_Wifi.h"
#include "config_data_rfn.h"
#include "config_helpers.h"
#include "std_helper.h"

namespace Cti {
namespace Devices {

namespace {

const std::map<bool, Commands::RfnCentronLcdConfigurationCommand::DisconnectDisplayState> disconnectDisplayDisabledLookup {
    { false, Commands::RfnCentronLcdConfigurationCommand::DisconnectDisplayEnabled },
    { true,  Commands::RfnCentronLcdConfigurationCommand::DisconnectDisplayDisabled } };

}


auto Rfn420CentronDevice::getDisplayConfigItems(const Config::DeviceConfigSPtr &deviceConfig) -> c2sx_display
{
    //  grab the c1sx items first
    c2sx_display config = Rfn410CentronDevice::getDisplayConfigItems(deviceConfig);

    config.disconnect_display = getConfigDataEnum(deviceConfig, Config::RfnStrings::DisconnectDisplayDisabled, disconnectDisplayDisabledLookup);

    return config;
}


bool Rfn420CentronDevice::isDisplayConfigCurrent(const c2sx_display &config)
{
    if( Rfn410CentronDevice::isDisplayConfigCurrent(config) )
    {
        if( const auto paoDisconnectDisplayDisabled = findDynamicInfo<bool>(CtiTableDynamicPaoInfo::Key_RFN_LcdDisconnectDisplayDisabled) )
        {
            if( const auto paoDisconnectDisplay = mapFind(disconnectDisplayDisabledLookup, *paoDisconnectDisplayDisabled) )
            {
                return *paoDisconnectDisplay == config.disconnect_display;
            }
        }
    }

    return false;
}


YukonError_t Rfn420CentronDevice::executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests)
{
    try
    {
        auto deviceConfig = getDeviceConfig();

        if( !deviceConfig )
        {
            return reportConfigErrorDetails( ClientErrors::NoConfigData, "Device \"" + getName() + "\"", pReq, returnMsgs );
        }

        const auto configItems = getDisplayConfigItems(deviceConfig);

        if( isDisplayConfigCurrent(configItems) )
        {
            if( ! parse.isKeyValid("force") )
            {
                return ClientErrors::ConfigCurrent;
            }
        }
        else
        {
            if( parse.isKeyValid("verify") )
            {
                return ClientErrors::ConfigNotCurrent;
            }
        }

        rfnRequests.push_back(
                std::make_unique<Commands::RfnCentronSetLcdConfigurationCommand>(
                        configItems.display_metrics,
                        configItems.disconnect_display,
                        configItems.display_digits,
                        configItems.cycle_delay));

        return ClientErrors::None;
    }
    catch( const InvalidConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \"" << getName() << "\"");

        return reportConfigErrorDetails( e, pReq, returnMsgs );
    }
    catch( const MissingConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");
 
        return reportConfigErrorDetails( e, pReq, returnMsgs );
    }
}


YukonError_t Rfn420CentronDevice::executeGetStatusWifi(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests)
{
    //  This code will probably need to move up to RfnMeter when this moves to more device types
    switch( getDeviceType() )
    {
        //  ... and this will need to change to an isRfnWifiDevice() call
        case TYPE_RFN420CDW:
        case TYPE_RFN420CLW:
        {
            rfnRequests.emplace_back(std::make_unique<Commands::RfnWifiGetCommunicationStatusUpdateCommand>());

            return ClientErrors::None;
        }
    }

    return ClientErrors::NoMethod;
}



void Rfn420CentronDevice::handleCommandResult(const Commands::RfnConfigNotificationCommand &cmd)
{
    Rfn410CentronDevice::handleCommandResult(cmd);

    if( cmd.c2sxDisplay )
    {
        storeLcdConfig(*cmd.c2sxDisplay);
    }
}


void Rfn420CentronDevice::handleCommandResult(const Commands::RfnCentronSetLcdConfigurationCommand &cmd)
{
    Rfn410CentronDevice::handleCommandResult(cmd);

    if( cmd.disconnect_display )
    {
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdDisconnectDisplayDisabled, cmd.disconnect_display == Commands::RfnCentronGetLcdConfigurationCommand::DisconnectDisplayDisabled);
    }
}


void Rfn420CentronDevice::handleCommandResult(const Commands::RfnCentronGetLcdConfigurationCommand &cmd)
{
    Rfn410CentronDevice::handleCommandResult(cmd);

    storeLcdConfig(cmd);
}

void Rfn420CentronDevice::storeLcdConfig(const Commands::RfnCentronLcdConfigurationCommand::Read &cmd)
{
    if( const boost::optional<bool> disconnectDisabled = cmd.getDisconnectDisplayDisabled() )
    {
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdDisconnectDisplayDisabled, *disconnectDisabled);
    }
}


}
}

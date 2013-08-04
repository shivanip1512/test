#pragma once

#include "cmd_rfn.h"



namespace Cti        {
namespace Devices    {
namespace Commands   {



class IM_EX_DEVDB RfnLoadProfileCommand : public RfnCommand
{
protected:

    enum CommandCode
    {
        CommandCode_Request                     = 0x68,
        CommandCode_Response                    = 0x69
    };

    enum Operation
    {
        Operation_SetConfiguration              = 0x00,
        Operation_GetConfiguration              = 0x01,
        Operation_DisableLoadProfileRecording   = 0x02,
        Operation_EnableLoadProfileRecording    = 0x03,
        Operation_GetLoadProfileRecordingState  = 0x04,
        Operation_GetLoadProfilePoints          = 0x05
    };

    enum TlvType
    {
        TlvType_VoltageProfileConfiguration     = 0x01,
        TlvType_LoadProfileState                = 0x02,
        TlvType_GetProfilePoints                = 0x03
    };

    RfnLoadProfileCommand( const Operation operation );

    virtual unsigned char getCommandCode() const;

    virtual unsigned char getOperation() const;

    virtual Bytes getData();

    virtual void populateTlvs( std::vector< TypeLengthValue > & tlvs );

    const Operation _operation;

    RfnResult decodeResponseHeader( const CtiTime now,
                                    const RfnResponse & response );

public:

    virtual RfnResult decode( const CtiTime now,
                              const RfnResponse & response ) = 0;

    virtual RfnResult error( const CtiTime now,
                             const YukonError_t error_code );
};


////


class IM_EX_DEVDB RfnVoltageProfileConfigurationCommand : public RfnLoadProfileCommand
{
    unsigned char _demandInterval,
                  _loadProfileInterval;

protected:

    virtual void populateTlvs( std::vector< TypeLengthValue > & tlvs );

public:

    RfnVoltageProfileConfigurationCommand();

    RfnVoltageProfileConfigurationCommand( const unsigned demand_interval_seconds,
                                           const unsigned load_profile_interval_minutes );

    virtual RfnResult decode( const CtiTime now,
                              const RfnResponse & response );

};


////


class IM_EX_DEVDB RfnLoadProfileRecordingCommand : public RfnLoadProfileCommand
{

public:

    RfnLoadProfileRecordingCommand();

    RfnLoadProfileRecordingCommand( const bool enable );

    virtual RfnResult decode( const CtiTime now,
                              const RfnResponse & response );
};


}
}
}


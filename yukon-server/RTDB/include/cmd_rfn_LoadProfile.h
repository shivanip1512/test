#pragma once

#include "cmd_rfn.h"
#include "ctidate.h"


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
        TlvType_GetProfilePointsResponse        = 0x03,
        TlvType_GetProfilePointsRequest         = 0x04
    };

    RfnLoadProfileCommand( const Operation operation );

    virtual unsigned char getCommandCode() const;

    virtual unsigned char getOperation() const;

    virtual Bytes getCommandData();

    virtual void populateTlvs( std::vector< TypeLengthValue > & tlvs );

    const Operation _operation;

    RfnResult decodeResponseHeader( const CtiTime now,
                                    const RfnResponse & response );

public:

    virtual RfnResult decodeCommand( const CtiTime now,
                                     const RfnResponse & response ) = 0;

    virtual RfnResult error( const CtiTime now,
                             const YukonError_t error_code );
};


////


class IM_EX_DEVDB RfnVoltageProfileConfigurationCommand : public RfnLoadProfileCommand
{
protected:

    virtual void populateTlvs( std::vector< TypeLengthValue > & tlvs );

    enum
    {
        SecondsPerInterval = 15
    };

public:

    struct ResultHandler
    {
        virtual void handleResult( const RfnVoltageProfileConfigurationCommand & cmd ) = 0;
    };

    RfnVoltageProfileConfigurationCommand( ResultHandler & rh );

    RfnVoltageProfileConfigurationCommand( ResultHandler & rh,
                                           const unsigned demand_interval_seconds,
                                           const unsigned load_profile_interval_minutes );

    virtual RfnResult decodeCommand( const CtiTime now,
                                     const RfnResponse & response );

    unsigned getDemandIntervalSeconds() const;
    unsigned getLoadProfileIntervalMinutes() const;

private:

    ResultHandler & _rh;

    unsigned char _demandInterval,
                  _loadProfileInterval;
};


////


class IM_EX_DEVDB RfnLoadProfileRecordingCommand : public RfnLoadProfileCommand
{

public:

    enum RecordingOption
    {
        DisableRecording,
        EnableRecording
    };

    struct ResultHandler
    {
        virtual void handleResult( const RfnLoadProfileRecordingCommand & cmd ) = 0;
    };

    RfnLoadProfileRecordingCommand( ResultHandler & rh );

    RfnLoadProfileRecordingCommand( ResultHandler & rh, const RecordingOption option );

    virtual RfnResult decodeCommand( const CtiTime now,
                                     const RfnResponse & response );

    RecordingOption getRecordingOption() const;

private:

    ResultHandler & _rh;

    RecordingOption _option;
};


////


class IM_EX_DEVDB RfnLoadProfileReadPointsCommand : public RfnLoadProfileCommand
{
    const CtiDate _begin;
    const CtiDate _end;

protected:

    virtual void populateTlvs( std::vector< TypeLengthValue > & tlvs );

public:

    RfnLoadProfileReadPointsCommand( const CtiTime &now,
                                     const CtiDate begin,
                                     const CtiDate end );

    virtual RfnResult decodeCommand( const CtiTime now,
                                     const RfnResponse & response );
};


}
}
}


#pragma warning( disable : 4786)
#ifndef __PROT_ION_H__
#define __PROT_ION_H__

/*-----------------------------------------------------------------------------*
*
* File:   prot_ion
*
* Class:  CtiProtocolDNP
* Date:   5/6/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2002/12/30 16:24:45 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dlldefs.h"
#include "pointtypes.h"

#include "prot_base.h"

#include "ion_rootclasses.h"
#include "ion_value_datastream.h"
#include "ion_net_application.h"


class IM_EX_PROT CtiProtocolION : public  CtiProtocolBase
{
    enum   IONCommand;
    struct ion_output_point;

private:

    CtiIONApplicationLayer _appLayer;

    CtiIONDataStream       _dsOut, _dsIn;

    unsigned short _srcID, _dstID;

    struct ion_protocol_command_struct
    {
        IONCommand command;
    } _currentCommand;

    CtiIONUnsignedIntArray *_setup_handles;
    unsigned int            _currentManagerHandle;

    unsigned long _handleManagerPowerMeter,
                  _handleManagerDataRecorder,
                  _handleManagerDigitalIn;

    vector< unsigned long >           _dataRecorderModules;
    vector< vector< unsigned long > > _dataRecorderSources;

    vector< unsigned long > _digitalInModules;

    unsigned long _powerMeterModule;

    bool _configRead;

    bool hasConfigBeenRead( void );
    void setConfigRead( bool read );

    void generateConfigRead( void );
    void decodeConfigRead( void );

protected:

    int commOut( OUTMESS *&OutMessage );
    int commIn ( INMESS   *InMessage  );

    enum
    {
        IONFeatureManagerHandle = 2
    };

    enum IONStates
    {
        State_Uninitialized,

        //  initialization / config read states
        State_Init,
        State_RequestFeatureManagerInfo,
        State_ReceiveFeatureManagerInfo,
        State_RequestManagerInfo,
        State_ReceiveManagerInfo,
        State_RequestPowerMeterModuleHandles,
        State_ReceivePowerMeterModuleHandles,
        State_RequestDigitalInputModuleHandles,
        State_ReceiveDigitalInputModuleHandles,
        State_RequestDataRecorderModuleHandles,
        State_ReceiveDataRecorderModuleHandles,
        State_RequestDataRecorderInputModuleHandles,
        State_ReceiveDataRecorderInputModuleHandles,

        //  reading statuses
        State_RequestDigitalInputData,
        State_ReceiveDigitalInputData,

        //  reading power meter info
        State_RequestPowerMeterData,
        State_ReceivePowerMeterData,

        //  reading load profile
        State_RequestDataRecorderLogs,
        State_ReceiveDataRecorderLogs,

        State_Complete,
        State_Abort
    } _ionState;

    enum IONClasses
    {
        Class_Alert             = 558,
        Class_AnalogIn          = 502,
        Class_AnalogOut         = 503,
        Class_AND_OR            = 521,
        Class_Arithmetic        = 541,
        Class_Clock             = 526,
        Class_Comm              = 524,
        Class_Convert           = 559,
        Class_Counter           = 520,
        Class_DataAcqn          = 523,
        Class_DataRec           = 537,
        Class_Diagnostics       = 525,
        Class_DigitalIn         = 504,
        Class_DigitalOut        = 505,
        Class_Display           = 569,
        Class_DisplayOptions    = 570,
        Class_DNPSlaveExport    = 561,
        Class_DNPSlaveImport    = 562,
        Class_DNPSlaveOptions   = 563,
        Class_EventLogCtl       = 522,
        Class_ExtBool           = 545,
        Class_ExtNum            = 546,
        Class_ExtPulse          = 544,
        Class_Factory           = 527,
        Class_Feedback          = 567,
        Class_FFT               = 514,
        Class_Harmonics         = 515,
        Class_Integrator        = 510,
        Class_LONExport         = 551,
        Class_LONImport         = 550,
        Class_Maximum           = 512,
        Class_Minimum           = 511,
        Class_ModbusSlave       = 553,
        Class_OneShotTmr        = 519,
        Class_PeriodicTmr       = 518,
        Class_PowerHarmonics    = 566,
        Class_PowerMeter        = 501,
        Class_Profibus          = 568,
        Class_PulseMerge        = 542,
        Class_Pulser            = 506,
        Class_RelativeSetpoint  = 564,
        Class_SagSwell          = 540,
        Class_Scheduler         = 539,
        Class_Setpoint          = 513,
        Class_SWinDemand        = 508,
        Class_SymmComp          = 531,
        Class_ThrmDemand        = 509,
        Class_Transient         = 565,
        Class_WformRec          = 536
    };

public:

    CtiProtocolION();
    CtiProtocolION(const CtiProtocolION &aRef);

    virtual ~CtiProtocolION();

    CtiProtocolION &operator=(const CtiProtocolION &aRef);

    void setAddresses( unsigned short masterAddress, unsigned short slaveAddress );

    void setCommand( IONCommand command, ion_output_point *points = NULL, int numPoints = 0 );

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    bool inputIsValid( CtiIONApplicationLayer &al, CtiIONDataStream &ds );

    bool isTransactionComplete( void );

    int sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    int recvCommResult ( INMESS   *InMessage,  RWTPtrSlist< OUTMESS > &outList );

    int recvCommRequest( OUTMESS *OutMessage );
    int sendCommResult ( INMESS  *InMessage  );

    bool hasInboundPoints( void );
    void getInboundPoints( RWTPtrSlist< CtiPointDataMsg > &pointList );

    /*
    enum IONOutputPointType
    {
        AnalogOutput,
        DigitalOutput
    };

    struct ion_output_point
    {
        union
        {
            struct ion_ao_point
            {
                double value;
            } aout;

            struct ion_do_point
            {
                CtiIONBinaryOutputControl::ControlCode control;
                CtiIONBinaryOutputControl::TripClose trip_close;
                unsigned long on_time;
                unsigned long off_time;
                bool queue;
                bool clear;
                unsigned char count;
            } dout;
        };

        unsigned long offset;

        IONOutputPointType type;
    };
    */

    enum IONCommand
    {
        Command_Invalid = 0,
        Command_ExceptionScan,
        Command_IntegrityScan,
        Command_ScanLoadProfile,
        Command_SetAnalogOut,
        Command_SetDigitalOut
    };

    enum
    {
        DefaultYukonIONMasterAddress =    5,
        DefaultSlaveAddress          =    1,
        MaxAppLayerSize              = 2048
    };
};

#endif // #ifndef __PROT_ION_H__

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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2003/01/07 21:17:28 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dlldefs.h"
#include "pointtypes.h"

#include <map>
using namespace std;

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

    unsigned long _handleManagerPowerMeter,
                  _handleManagerDataRecorder,
                  _handleManagerDigitalIn;

    vector< unsigned long >           _dataRecorderModules;
    vector< vector< unsigned long > > _dataRecorderSources;

    typedef map< unsigned long, unsigned long > IONValueRegisterMap;

    vector< unsigned long > _digitalInModules;
    IONValueRegisterMap     _digitalInValueRegisters;

    vector< unsigned long > _powerMeterModules;

    struct ion_pointdata_struct
    {
        long offset;
        long type;
        unsigned long time;
        double value;
        char name[20];
    };


//  ACH, yo
/*
    struct ionEventLog
    {

    };
*/

    vector< ion_pointdata_struct > _pointData;

    typedef vector< ion_pointdata_struct >::iterator PointDataIterator;

    struct ion_result_descriptor_struct
    {
        unsigned long numPoints;
        unsigned long numEvents;
    };

    bool _configRead;

    bool hasConfigBeenRead( void );
    void setConfigRead( bool read );

    void generateConfigRead( void );
    void decodeConfigRead( void );

    void generateExceptionScan( void );
    void decodeExceptionScan( void );

    void generateIntegrityScan( void );
    void decodeIntegrityScan( void );

    unsigned long resultSize( void );
    void putResult( unsigned char *buf );

protected:

    int commOut( OUTMESS *&OutMessage );
    int commIn ( INMESS   *InMessage  );

    enum IONModuleHandles
    {
        IONFeatureManager = 2
    };

    enum IONRegisterHandles
    {
        Register_PowerMeter1S_Va          = 0x5800,
        Register_PowerMeter1S_Vb          = 0x5801,
        Register_PowerMeter1S_Vc          = 0x5802,
        Register_PowerMeter1S_VInave      = 0x5803,
        Register_PowerMeter1S_Vab         = 0x5804,
        Register_PowerMeter1S_Vbc         = 0x5805,
        Register_PowerMeter1S_Vca         = 0x5806,
        Register_PowerMeter1S_VIIave      = 0x5807,
        Register_PowerMeter1S_Ia          = 0x5808,
        Register_PowerMeter1S_Ib          = 0x5809,
        Register_PowerMeter1S_Ic          = 0x580a,
        Register_PowerMeter1S_Iave        = 0x580b,
        Register_PowerMeter1S_KWa         = 0x580c,
        Register_PowerMeter1S_KWb         = 0x580d,
        Register_PowerMeter1S_KWc         = 0x580e,
        Register_PowerMeter1S_KWtotal     = 0x580f,
        Register_PowerMeter1S_KVARa       = 0x5810,
        Register_PowerMeter1S_KVARb       = 0x5811,
        Register_PowerMeter1S_KVARc       = 0x5812,
        Register_PowerMeter1S_KVARtotal   = 0x5813,
        Register_PowerMeter1S_KVAa        = 0x5814,
        Register_PowerMeter1S_KVAb        = 0x5815,
        Register_PowerMeter1S_KVAc        = 0x5816,
        Register_PowerMeter1S_KVAtotal    = 0x5817,
        Register_PowerMeter1S_Quadrant1   = 0x61fb,
        Register_PowerMeter1S_Quadrant2   = 0x61fc,
        Register_PowerMeter1S_Quadrant3   = 0x61fd,
        Register_PowerMeter1S_Quadrant4   = 0x61fe,
        Register_PowerMeter1S_PFSIGNa     = 0x5818,
        Register_PowerMeter1S_PFSIGNb     = 0x5819,
        Register_PowerMeter1S_PFSIGNc     = 0x581a,
        Register_PowerMeter1S_PFSIGNtotal = 0x581b,
        Register_PowerMeter1S_PFLEADa     = 0x581c,
        Register_PowerMeter1S_PFLEADb     = 0x581d,
        Register_PowerMeter1S_PFLEADc     = 0x581e,
        Register_PowerMeter1S_PFLEADtotal = 0x581f,
        Register_PowerMeter1S_PFLAGa      = 0x5820,
        Register_PowerMeter1S_PFLAGb      = 0x5821,
        Register_PowerMeter1S_PFLAGc      = 0x5822,
        Register_PowerMeter1S_PFLAGtotal  = 0x5823,
        Register_PowerMeter1S_Vunbal      = 0x5824,
        Register_PowerMeter1S_Iunbal      = 0x5825,
        Register_PowerMeter1S_I4          = 0x5826,
        Register_PowerMeter1S_V4          = 0x5e4e,
        Register_PowerMeter1S_I5          = 0x5e4f,
        Register_PowerMeter1S_PhaseRev    = 0x6000,
        Register_PowerMeter1S_LineFreq    = 0x5827,
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
        State_RequestDigitalInputValueRegisters,
        State_ReceiveDigitalInputValueRegisters,
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

    enum IONModules
    {
        Module_PowerModule            = 0x100,
        Module_PowerModule_MeterUnits = 0x101,
        Module_PowerModule_HighSpeed  = 0x102
    };

public:

    CtiProtocolION();
    CtiProtocolION(const CtiProtocolION &aRef);

    virtual ~CtiProtocolION();

    CtiProtocolION &operator=(const CtiProtocolION &aRef);

    void initializeSets( void );

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

    enum IONConstants
    {
        DefaultYukonIONMasterAddress =    5,
        DefaultSlaveAddress          =    1,
        MaxAppLayerSize              = 2048
    };
};

#endif // #ifndef __PROT_ION_H__

/*-----------------------------------------------------------------------------*
*
* File:   prot_dnp
*
* Class:  CtiProtocolDNP
* Date:   5/6/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.14 $
* DATE         :  $Date: 2003/10/27 22:13:50 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_DNP_H__
#define __PROT_DNP_H__
#pragma warning( disable : 4786)


#include "dlldefs.h"
#include "pointtypes.h"

#include "prot_base.h"

#include "dnp_application.h"
#include "dnp_objects.h"
#include "dnp_object_binaryoutput.h"

class IM_EX_PROT CtiProtocolDNP : public CtiProtocolBase
{
    enum   DNPCommand;
    struct dnp_output_point;

private:

    CtiDNPApplication _appLayer;
    unsigned short    _masterAddress, _slaveAddress;
    DNPCommand        _currentCommand;
    int               _options;

    enum Retries
    {
        Retries_Default = 2
    };

    void initLayers( void );

protected:

    static const char const *ControlResultStr_RequestAccepted;
    static const char const *ControlResultStr_ArmTimeout;
    static const char const *ControlResultStr_NoSelect;
    static const char const *ControlResultStr_FormattingError;
    static const char const *ControlResultStr_PointNotControllable;
    static const char const *ControlResultStr_QueueFullPointActive;
    static const char const *ControlResultStr_HardwareError;
    static const char const *ControlResultStr_InvalidStatus;

public:

    CtiProtocolDNP();
    CtiProtocolDNP(const CtiProtocolDNP &aRef);

    virtual ~CtiProtocolDNP();

    CtiProtocolDNP &operator=(const CtiProtocolDNP &aRef);

    void setAddresses( unsigned short slaveAddress, unsigned short masterAddress );
    void setOptions( int options );

    void setCommand( DNPCommand command, dnp_output_point *points = NULL, int numPoints = 0 );
    DNPCommand getCommand( void );

    bool commandRequiresRequeueOnFail( void );
    int  commandRetries( void );

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    bool isTransactionComplete( void );

    int sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    int recvCommResult ( INMESS   *InMessage,  RWTPtrSlist< OUTMESS > &outList );

    int recvCommRequest( OUTMESS  *OutMessage );
    int sendCommResult ( INMESS   *InMessage  );

    bool hasInboundPoints( void );
    void getInboundPoints( RWTPtrSlist< CtiPointDataMsg > &pointList );

    bool hasControlResult( void )              const;
    bool getControlResultSuccess( void )       const;
    long getControlResultOffset( void )        const;
    const char *getControlResultString( void ) const;

    bool          hasTimeResult( void ) const;
    unsigned long getTimeResult( void ) const;


    enum DNPOutputPointType
    {
        AnalogOutput,
        DigitalOutput
    };

    struct dnp_output_point
    {
        union
        {
            struct dnp_ao_point
            {
                double value;
            } aout;

            struct dnp_do_point
            {
                CtiDNPBinaryOutputControl::ControlCode control;
                CtiDNPBinaryOutputControl::TripClose trip_close;
                unsigned long on_time;
                unsigned long off_time;
                bool queue;
                bool clear;
                unsigned char count;
            } dout;
        };

        unsigned long offset;

        DNPOutputPointType type;
    };

    enum DNPCommand
    {
        DNP_Invalid = 0,
        DNP_WriteTime,
        DNP_ReadTime,
        DNP_Class0Read,
        DNP_Class1Read,
        DNP_Class2Read,
        DNP_Class3Read,
        DNP_Class123Read,
        DNP_Class1230Read,
        DNP_SetAnalogOut,
        DNP_SetDigitalOut_Direct,
        DNP_SetDigitalOut_SBO_Select,
        DNP_SetDigitalOut_SBO_Operate,
        DNP_SetDigitalOut_SBO_SelectOnly/*,
        DNP_SBO_Select,
        DNP_SBO_Operate*/
    };

    enum DNPOptions
    {
        //  to be logically OR'd together - keep bit patterns unique
        None            = 0x00,
        DatalinkConfirm = 0x01
    };

    enum
    {
        DefaultYukonDNPMasterAddress =    5,
        DefaultSlaveAddress          =    1,
        MaxAppLayerSize              = 2048
    };
};

#endif // #ifndef __PROT_DNP_H__

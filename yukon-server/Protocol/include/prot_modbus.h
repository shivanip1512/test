/*-----------------------------------------------------------------------------*
*
* File:   prot_modbus
*
* Class:  Modbus
* Date:   7/14/2005
*
* Author: Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/08/03 18:35:33 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_MODBUS_H__
#define __PROT_MODBUS_H__
#pragma warning( disable : 4786)


#include <list>
using namespace std;

#include "dlldefs.h"
#include "pointtypes.h"

#include "prot_base.h"

#include "dnp_application.h"
#include "dnp_objects.h"
#include "dnp_object_binaryoutput.h"
namespace Cti {
namespace Protocol {

class IM_EX_PROT Modbus : public Interface
{
    enum   Functions;
    enum   Command;
    enum   Status;
    enum   PointType;
    struct output_point;

private:

    unsigned char   _slaveAddress;
    bool            _asciiOutput;
    Command         _command;
    Status          _status;
    char            _retries;//a counter.

//    stringlist_t _string_results;
//    pointlist_t  _point_results;

    enum Retries
    {
        Retries_Default = 1
    };

    enum IOState
    {
        IOState_Uninitialized = 0,
        IOState_Initialized,
        IOState_Complete,
        IOState_Error
    } _io_state;

    struct point_data
    {
        Functions pointType;
        int pointOffset;

        bool operator < (const point_data param) const
        {
            return(pointType<param.pointType || (pointType == param.pointType && pointOffset<param.pointOffset));
        }
    };

    set<point_data> _points;

    //two iterators necessary to make retries easier.
    set<point_data>::iterator _points_start;
    set<point_data>::iterator _points_finish;

    unsigned short CRC16(unsigned char *puchMsg, unsigned short usDataLen);
    unsigned char calcModbusLRC(char *dataString);

    bool prepareNextOutMessage(int& function,int& address,int& lengthOrdata);

    static const unsigned char auchCRCHi[];
    static const unsigned char auchCRCLo[];
    static const char *char_CRLF;
    static const char *char_start;

    void assemblePointData(CtiXfer &xfer);

protected:
    BYTE                          _outBuffer[100];
    BYTE                          _inBuffer[505];
    ULONG                         _inCountActual;

    Protocol::Interface::stringlist_t _string_results;
    Protocol::Interface::pointlist_t  _point_results;

public:

    Modbus();
    Modbus(const Modbus &aRef);

    virtual ~Modbus();

    Modbus &operator=(const Modbus &aRef);

    void setAddresses( unsigned short slaveAddress );
    void setCommand(Command newCommand);

   // int  commandRetries( void );

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    bool isTransactionComplete( void );

    void addStatusPoint(int point);
    void addAnalogPoint(int point);
    void clearPoints();

    virtual void getInboundPoints ( pointlist_t  &point_list );
    virtual void getInboundStrings( stringlist_t &string_list );

    enum OutputPointType
    {
        AddressWithData,
        DataOnly
    };

    struct output_point
    {
        PointType pointType;
        unsigned int pointOffset;
    };

    enum Functions
    {
        Command_Invalid =               0,             //These are protocol defined numbers i am using.. They must not be changed!
        Command_ReadCoilStatus =        1,             //Matt will hunt, stalk, and kill you if you touch them.
        Command_ReadInputStatus =       2,             //It will be a slow and painful death.
        Command_ReadHoldingRegisters =  3,             //He'll talk you to death with coding theory.
        Command_ReadInputRegisters =    4,
        Command_ForceSingleCoil =       5,
        Command_SetSingleRegister =     6,
        Command_ReadExceptionStatus =   7,
        Command_FetchCommEventCounter = 11,
        Command_FetchCommEventLog =     12,
        Command_ForceMultipleCoils =    15,
        Command_PresetMultipleRegs =    16,
        Command_ReportSlaveID =         17,
        Command_ReadGeneralReference =  20,
        Command_WriteGeneralReference = 21,
        Command_MaskWrite4XRegister =   22,
        Command_ReadWrite4XRegister =   23,
        Command_ReadFIFOQueue =         24,       //End of protocol defined numbers.
        Command_DecomposeReadHoldingRegisters,
        Command_DecomposeReadInputRegisters
    };

    enum Command
    {
        Command_Error,
        Command_ScanALL //Integrity Scan
    };

    enum Status
    {
        Continue = 0,
        End
    };

    enum PointValues
    {
        FirstStartPoint =                   1,
        SecondStartPoint =                  100001,
        SizeLimit =                         65535,
        HoldingDecomposeRegStart =          1000001,
        InputDecomposeRegStart =            2000001,
        DecomposeSizeLimit =                999999,
        NumBitsPerRegister =                16
    };

    enum LocalProtocolValues
    {
        ReadStatusGapBitLimit = 1,
        ReadStatusMaxBits = 300,
        ReadAnalogGapLimit = 1,
        ReadAnalogDataPointLimit = 25//note this means 16 bit analogs
    };

    enum PointType
    {
        Point_Type_Status=0,
        Point_Type_Register
    };

    enum
    {
        DefaultSlaveAddress  =    1
    };
};

}
}

#endif // #ifndef __PROT_MODBUS_H__


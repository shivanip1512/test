
/*-----------------------------------------------------------------------------*
*
* File:   prot_modbus.cpp
*
* Date:   7/14/2005
*
* Author: Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/08/05 20:01:42 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "logger.h"
#include "utility.h"
#include "numstr.h"
#include "prot_modbus.h"

namespace Cti             {
namespace Protocol        {

Modbus::Modbus() :
    _io_state(IOState_Uninitialized)
{
    setAddresses(DefaultSlaveAddress);
}

Modbus::Modbus(const Modbus &aRef)
{
    *this = aRef;
}

Modbus::~Modbus()   {}

Modbus &Modbus::operator=(const Modbus &aRef)
{
    if( this != &aRef )
    {
        _slaveAddress  = aRef._slaveAddress;
    }

    return *this;
}

void Modbus::setCommand(Command newCommand)
{
    _command = newCommand;
}

void Modbus::setAddresses( unsigned short slaveAddress )
{
    _slaveAddress  = slaveAddress;
}

/*int Modbus::commandRetries( void )
{
    int retVal;

    switch( _command )
    {
        default:
        {
            retVal = Retries_Default;

            break;
        }
    }

    return retVal;
}*/


//Should we use non-blocking reads? It could cause problems with serial over IP and other applications...
int Modbus::generate( CtiXfer &xfer )
{
    _asciiOutput = false;
    int i = 0;
    output_point point;
    int address,lengthOrData,function;

    xfer.setOutBuffer(_outBuffer);
    xfer.setInBuffer(_inBuffer);
    xfer.setInCountActual(&_inCountActual);

    xfer.getOutBuffer()[i++] = _slaveAddress; //assign slave address
    xfer.setMessageStart(true);//how handy! This clears out my inbuffer so I dont have to worry about garbage in it!


    prepareNextOutMessage(function,address,lengthOrData);

    switch( _command )
        {
            case Command_ScanALL:
                {                    
                    xfer.getOutBuffer()[i++] = function; //Protocol defined number.
                    xfer.getOutBuffer()[i++] = address>>8;
                    xfer.getOutBuffer()[i++] = address;
                    xfer.getOutBuffer()[i++] = lengthOrData>>8;
                    xfer.getOutBuffer()[i++] = lengthOrData;

                    if(function == Command_ReadCoilStatus || function == Command_ReadInputStatus)
                    {
                        xfer.setInCountExpected(5 + lengthOrData/8 + ((lengthOrData%8)?1:0));//5 base bytes (incl crc), one for each 8 coils or inputs plus 1 for any leftover bits
                    }
                    else
                    {
                        xfer.setInCountExpected(5 + lengthOrData*2);//5 base bytes (incl crc), 2 bytes for each data point
                    }
                    break;
                }
            case Command_Error:
            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - unused command " << _command << " in Modbus::generate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _command = Command_Error;
            }
    }

    if(i>1)//Something has been added to out buffer
    {
        if(_asciiOutput)
        {
            char tempBuffer[100];
            for(int x=0;x<i;x++)
            {
                tempBuffer[x] = xfer.getOutBuffer()[x];
            }

            strncpy((char *)xfer.getOutBuffer(),char_start,1);

            for(x=0;x<i;x++)
            {
                //Genius!!
                strncat((char *)xfer.getOutBuffer(),CtiNumStr(tempBuffer[x]).zpad(2).hex(),2);//create ascii from bitwise data
            }

            //add LRC
            strncat((char *)xfer.getOutBuffer(),CtiNumStr(calcModbusLRC((char *)xfer.getOutBuffer())).zpad(2).hex(),2);
            strncat((char *)xfer.getOutBuffer(),char_CRLF,2);
            xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));

            //This is twice the number of bytes when in ascii format, and one additional for the colon. This includes the CRLF at the end.
            xfer.setInCountExpected(xfer.getInCountExpected()*2+1);
        }
        else
        {
            //add CRC
            unsigned short crc = CRC16(xfer.getOutBuffer(),i);
            xfer.getOutBuffer()[i++] = crc>>8;
            xfer.getOutBuffer()[i++] = crc;
            xfer.setOutCount(i);
        }
    }


    return NORMAL;//FIX_ME JESS
}


int Modbus::decode( CtiXfer &xfer, int status )
{
    int  retVal = NoError;
    bool final = true;
        //  this block is for commands that return anything besides non-pointdata
        switch( _command )
        {
            case Command_ScanALL:
                {
                    if(xfer.getInCountActual()>=5)//absolute minimum for a message and crc!!!
                    {
                        if(!_asciiOutput)
                        {
                            int crc = CRC16(xfer.getInBuffer(),xfer.getInCountActual()-2);//calc on all but crc
                            if((crc>>8) != xfer.getInBuffer()[xfer.getInCountActual()-2] || (crc&0x00FF) != xfer.getInBuffer()[xfer.getInCountActual()-1])
                            {//CRC ERROR!!!!
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - crc error in received data " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                retVal = UnknownError;//some error code should be set!
                                
                                if(++_retries>Retries_Default)//retry and if that doesnt work, quit!
                                {
                                    clearPoints();
                                    _status = End;
                                }
                                else
                                {
                                    _status = Continue;
                                }
                                break;
                            }

                            if(xfer.getInBuffer()[0] == xfer.getOutBuffer()[0] && xfer.getInBuffer()[1] == xfer.getOutBuffer()[1])
                            {
                                //function code and slave address in response are correct, message should be correct..
                                assemblePointData(xfer);
                                _retries = 0;//reset retry count
                                _status = Continue;
                                _points_start = _points_finish;//move to next section and repeat!!!
                                if(_points_start == _points.end())
                                {
                                    _status = End;
                                    retVal = NoError;
                                    clearPoints();
                                }
                                break;
                                
                            }
                            else if(xfer.getInBuffer()[0] == xfer.getOutBuffer()[0] && xfer.getInBuffer()[1] == (xfer.getOutBuffer()[1] | 0x80))
                            {
                                //this means error code was set, but response was right. Report the error code
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                _string_results.push_back(CTIDBG_new string("There is a read with function " + CtiNumStr((*_points_start).pointType) + " starting at point " + CtiNumStr((*_points_start).pointOffset) +
                                                                            " that cannot be read."));
                                retVal = UnknownError;//some error code should be set!

                                _status = Continue;
                                _points_start = _points_finish;//ok, that didnt work, and it wont work if we try it again, so dont.
                                if(_points_start == _points.end())
                                {
                                    _status = End;
                                    retVal = NoError;//I dont want to repeat!
                                    clearPoints();
                                }
                                break;
                            }
                            else
                            {
                                //unknown data??
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - unknown data received " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                retVal = UnknownError;//some error code should be set, we can retry!

                                if(++_retries>Retries_Default)//retry and if that doesnt work, quit!
                                {
                                    clearPoints();
                                    _status = End;
                                }
                                else
                                {
                                    _status = Continue;
                                }
                                break;
                            }

                        }
                        else 
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - ascii decode unimplemented " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            retVal = UnknownError;//some error code should be set!
                            _status = End;
                            clearPoints();
                            break;                            
                        }
                    
                        break;

                    }
                    else
                    {
                        clearPoints();
                        _status = End;
                        break;
                    }
                }
            case Command_Error:
            default:
            {
                retVal = UnknownError;
                _status = End;
                break;
            }
        }


    return retVal;
}


bool Modbus::prepareNextOutMessage(int &function,int &address,int &lengthOrData)
{
    //be careful here!!! This assumes always that the iterator and command are pointing to the same function!!!!!!!
    int base, current;
    bool retVal = false;

    _points_finish = _points_start;
    switch((*_points_start).pointType)
    {
        case Command_ReadCoilStatus:
        case Command_ReadInputStatus:
            {
                if(_points_finish!=_points.end())
                {
                    base = (*_points_finish).pointOffset;
                    _points_finish++;
                    function = (*_points_start).pointType;
                    current = base;

                    while((function == (*_points_finish).pointType) && ((*_points_finish).pointOffset - base < ReadStatusMaxBits)
                           && (((*_points_finish).pointOffset - current)<=ReadStatusGapBitLimit) && (_points_finish != _points.end()))
                    {
                        current = (*_points_finish).pointOffset;
                        _points_finish++;
                    }
                    
                    address = base;

                    if(current == base)
                        lengthOrData = 1;
                    else
                        lengthOrData = current - base+1;//+1 so we read base and current

                    retVal = true;
                }
                else
                {
                    lengthOrData = 0;
                    retVal =  false;
                }
                break;
            }
        case Command_ReadHoldingRegisters:
        case Command_ReadInputRegisters:
            {
                if(_points_finish!=_points.end())
                {
                    base = (*_points_finish).pointOffset;
                    _points_finish++;
                    function = (*_points_start).pointType;
                    current = base;
                         
                    while((function == (*_points_finish).pointType) && ((*_points_finish).pointOffset - base < ReadAnalogDataPointLimit)
                           && (((*_points_finish).pointOffset - current)<=ReadAnalogGapLimit) && (_points_finish != _points.end()))
                    {
                        current = (*_points_finish).pointOffset;
                        _points_finish++;
                    }

                    if(base == current)
                        lengthOrData = 1;
                    else
                        lengthOrData = current - base+1;//+1 so we read base and current
                    address = base;
                    retVal = true;

                }
                else
                {
                    lengthOrData = 0;
                    retVal =  false;
                }
                break;
            }
        case Command_DecomposeReadHoldingRegisters:
        case Command_DecomposeReadInputRegisters:
            {
                if(_points_finish!=_points.end())
                {
                    base = (*_points_finish).pointOffset;
                    _points_finish++;
                    function = (*_points_start).pointType==Command_DecomposeReadHoldingRegisters ? Command_ReadHoldingRegisters : Command_ReadInputRegisters;
                    current = base;
    
                    while(((*_points_start).pointType == (*_points_finish).pointType) && ((*_points_finish).pointOffset - base < ReadStatusMaxBits)
                           && (((*_points_finish).pointOffset - current)<=ReadStatusGapBitLimit) && (_points_finish != _points.end()))
                    {
                        current = (*_points_finish).pointOffset;
                        _points_finish++;
                    }
    
                    if(base == current)
                        lengthOrData = 1;
                    else
                        lengthOrData = (current - base)/NumBitsPerRegister + (current%NumBitsPerRegister + base%NumBitsPerRegister)>NumBitsPerRegister?1:0 + 1;//+1 so we read base and current
                    address = base/NumBitsPerRegister;
                    retVal = true;
                }
                break;
            }
        default:    
            retVal =  false;
            break;
    }
    return retVal;
}


/******************************************************************************
This currently assumes the decomposed points are packed in the same way
the normal statuses are packed. This means, 8 bits at a time. For example,
Decompose Holding read byte 0 for 1 byte returns these bits:
7 6 5 4 3 2 1 0    15 14 13 12 11 10 9 8   Bit#'s in 16 bit word.
******************************************************************************/
void Modbus::assemblePointData(CtiXfer &xfer)
{
    point_data dataPoint;
    CtiPointDataMsg  pointMessage;
    CtiPointDataMsg* pointPointer;//What a clever name!!

    // _point_results
    switch((*_points_start).pointType)
    {
        case Command_ReadCoilStatus:
        case Command_ReadInputStatus:
        case Command_DecomposeReadHoldingRegisters:
        case Command_DecomposeReadInputRegisters:
            {
                dataPoint.pointType = (*_points_start).pointType;
                if(!_asciiOutput)
                {
                    int maxCount = 8*xfer.getInBuffer()[2];//bits!
                    if((*_points_start).pointType == Command_DecomposeReadHoldingRegisters || (*_points_start).pointType == Command_DecomposeReadInputRegisters)
                    {//Basically this makes sure the decomposed reads dont try to read bits beyond what they contain.
                        maxCount -= ((*_points_start).pointOffset%8);
                    }

                    for(int i=0;i<(maxCount);i++)//bit count
                    {
                        dataPoint.pointOffset = i+(*_points_start).pointOffset;//first point
                        if(_points.count(dataPoint))
                        {
                            //yay, I asked for and want this point.
                            if(dataPoint.pointType == Command_ReadCoilStatus)
                            {
                                pointMessage.setValue(((xfer.getInBuffer()[3+i/8])>>(i%8))&0x01);//fun!!!!
                                pointMessage.setId(dataPoint.pointOffset+1);
                            }
                            else if(dataPoint.pointType == Command_ReadInputStatus)
                            {
                                pointMessage.setValue(((xfer.getInBuffer()[3+i/8])>>(i%8))&0x01);//fun!!!!
                                pointMessage.setId(dataPoint.pointOffset + SecondStartPoint);
                            }
                            else if(dataPoint.pointType == Command_DecomposeReadHoldingRegisters)
                            {
                                pointMessage.setValue(((xfer.getInBuffer()[3+(i+(*_points_start).pointOffset%16)/8])>>((i+(*_points_start).pointOffset)%8))&0x01);//super fun!!!!
                                pointMessage.setId(dataPoint.pointOffset + HoldingDecomposeRegStart);
                            }
                            else if(dataPoint.pointType == Command_DecomposeReadHoldingRegisters)
                            {
                                pointMessage.setValue(((xfer.getInBuffer()[3+(i+(*_points_start).pointOffset%16)/8])>>((i+(*_points_start).pointOffset)%8))&0x01);//super fun!!!!
                                pointMessage.setId(dataPoint.pointOffset + InputDecomposeRegStart);
                            }

                            pointMessage.setType(StatusPointType);
                            pointPointer = CTIDBG_new CtiPointDataMsg(pointMessage);
                            _point_results.push_back(pointPointer);
                        }
                    }
                }
                else
                {
                    //add code here someday!!!
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - ascii decode unimplemented " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        case Command_ReadHoldingRegisters:
        case Command_ReadInputRegisters:
            {
                dataPoint.pointType = (*_points_start).pointType;
                if(!_asciiOutput)
                {
                    for(int i=0;i<(xfer.getInBuffer()[2]/2);i++)//byte count
                    {
                        dataPoint.pointOffset = i+(*_points_start).pointOffset;//first point
                        if(_points.count(dataPoint))
                        {
                            pointMessage.setValue((((int)xfer.getInBuffer()[3+2*i])<<8) + ((int)xfer.getInBuffer()[4+2*i]));

                            if(dataPoint.pointType == Command_ReadHoldingRegisters)
                            {
                                pointMessage.setId(dataPoint.pointOffset+1);
                            }
                            else
                            {
                                pointMessage.setId(dataPoint.pointOffset + SecondStartPoint);
                            }

                            pointMessage.setType(AnalogPointType);
                            pointPointer = CTIDBG_new CtiPointDataMsg(pointMessage);
                            _point_results.push_back(pointPointer);
                        }
                    }
                }
                else
                {
                    //add code here someday!!!
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - ascii decode unimplemented " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }

        default:
            break;
    }
}

void Modbus::addStatusPoint(int point)
{
    point_data tempData;
    _status = Continue;

    if(point>=FirstStartPoint && point<=SizeLimit)//Size limit is due to having only 2 bytes (16 bits) of information
    {
        tempData.pointOffset = point-1;
        tempData.pointType = Command_ReadCoilStatus;
        _points.insert(tempData);     
    }
    else if(point>=SecondStartPoint && point<=(SecondStartPoint+SizeLimit))
    {
        tempData.pointOffset = point-SecondStartPoint;
        tempData.pointType = Command_ReadInputStatus;
        _points.insert(tempData);  
    }
    else if(point>=HoldingDecomposeRegStart && point<=(HoldingDecomposeRegStart + DecomposeSizeLimit))
    {
        tempData.pointOffset = point-HoldingDecomposeRegStart;
        tempData.pointType = Command_DecomposeReadHoldingRegisters;
        _points.insert(tempData);
    }
    else if(point>=InputDecomposeRegStart && point<=(InputDecomposeRegStart + DecomposeSizeLimit))
    {
        tempData.pointOffset = point-InputDecomposeRegStart;
        tempData.pointType = Command_DecomposeReadInputRegisters;
        _points.insert(tempData);
    }
    _points_start = _points.begin();
}
        
void Modbus::addAnalogPoint(int point)
{
    point_data tempData;
    _status = Continue;

    if(point>=FirstStartPoint && point<=SizeLimit)//Size limit is due to having only 2 bytes (16 bits) of information
    {
        tempData.pointOffset = point-1;
        tempData.pointType = Command_ReadHoldingRegisters;
        _points.insert(tempData);     
    }
    else if(point>=SecondStartPoint && point<=(SecondStartPoint+SizeLimit))
    {
        tempData.pointOffset = point-SecondStartPoint;
        tempData.pointType = Command_ReadInputRegisters;
        _points.insert(tempData);  
    }

    //This must be done because any addition causes iterators to become invalid, and we always want to start at begin!!!
    _points_start = _points.begin();
} 

void Modbus::clearPoints()//ok, this does more than clear points. Sue me.
{
    _retries = 0;//reset retry count
    _points.clear();
    _points_start = _points.begin();
    _command = Command_Error;
}

void Modbus::getInboundPoints( pointlist_t &points )
{
    points.insert(points.end(), _point_results.begin(), _point_results.end());

    _point_results.erase(_point_results.begin(), _point_results.end());
}


void Modbus::getInboundStrings( stringlist_t &strings )
{
    strings.insert(strings.end(), _string_results.begin(), _string_results.end());

    _string_results.erase(_string_results.begin(), _string_results.end());
}


bool Modbus::isTransactionComplete( void )
{
    return _status == End;
}

unsigned char Modbus::calcModbusLRC(char *dataString)
{
    char *stringPtr = dataString+1;//start after colon
    unsigned char lrcResult = 0;
    int len = strlen(dataString)-1;//Dont include colon!

    while(len--)
    {
        lrcResult += *stringPtr++;
    }

    //Warning, taken from documentation
    return ((unsigned char)(-1*((char)lrcResult))); // return twos complement
}

unsigned short Modbus::CRC16(unsigned char *puchMsg, unsigned short usDataLen)
{
    unsigned char uchCRCHi = 0xFF ; /* high byte of CRC initialized */
    unsigned char uchCRCLo = 0xFF ; /* low byte of CRC initialized */
    unsigned uIndex ; /* will index into CRC lookup table */
    while(usDataLen--) /* pass through message buffer */
    {
        uIndex = uchCRCHi ^ *puchMsg++ ; /* calculate the CRC */
        uchCRCHi = uchCRCLo ^ auchCRCHi[uIndex] ;
        uchCRCLo = auchCRCLo[uIndex] ;
    }
    return(uchCRCHi << 8 | uchCRCLo) ;
}

/* Table of CRC values for high–order byte */
unsigned const char Modbus::auchCRCHi[] = {
    0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81,
    0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
    0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01,
    0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41,
    0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81,
    0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
    0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01,
    0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
    0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81,
    0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
    0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01,
    0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
    0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81,
    0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
    0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01,
    0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
    0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81,
    0x40
} ;

/* Table of CRC values for low–order byte */
unsigned const char Modbus::auchCRCLo[] = {
    0x00, 0xC0, 0xC1, 0x01, 0xC3, 0x03, 0x02, 0xC2, 0xC6, 0x06, 0x07, 0xC7, 0x05, 0xC5, 0xC4,
    0x04, 0xCC, 0x0C, 0x0D, 0xCD, 0x0F, 0xCF, 0xCE, 0x0E, 0x0A, 0xCA, 0xCB, 0x0B, 0xC9, 0x09,
    0x08, 0xC8, 0xD8, 0x18, 0x19, 0xD9, 0x1B, 0xDB, 0xDA, 0x1A, 0x1E, 0xDE, 0xDF, 0x1F, 0xDD,
    0x1D, 0x1C, 0xDC, 0x14, 0xD4, 0xD5, 0x15, 0xD7, 0x17, 0x16, 0xD6, 0xD2, 0x12, 0x13, 0xD3,
    0x11, 0xD1, 0xD0, 0x10, 0xF0, 0x30, 0x31, 0xF1, 0x33, 0xF3, 0xF2, 0x32, 0x36, 0xF6, 0xF7,
    0x37, 0xF5, 0x35, 0x34, 0xF4, 0x3C, 0xFC, 0xFD, 0x3D, 0xFF, 0x3F, 0x3E, 0xFE, 0xFA, 0x3A,
    0x3B, 0xFB, 0x39, 0xF9, 0xF8, 0x38, 0x28, 0xE8, 0xE9, 0x29, 0xEB, 0x2B, 0x2A, 0xEA, 0xEE,
    0x2E, 0x2F, 0xEF, 0x2D, 0xED, 0xEC, 0x2C, 0xE4, 0x24, 0x25, 0xE5, 0x27, 0xE7, 0xE6, 0x26,
    0x22, 0xE2, 0xE3, 0x23, 0xE1, 0x21, 0x20, 0xE0, 0xA0, 0x60, 0x61, 0xA1, 0x63, 0xA3, 0xA2,
    0x62, 0x66, 0xA6, 0xA7, 0x67, 0xA5, 0x65, 0x64, 0xA4, 0x6C, 0xAC, 0xAD, 0x6D, 0xAF, 0x6F,
    0x6E, 0xAE, 0xAA, 0x6A, 0x6B, 0xAB, 0x69, 0xA9, 0xA8, 0x68, 0x78, 0xB8, 0xB9, 0x79, 0xBB,
    0x7B, 0x7A, 0xBA, 0xBE, 0x7E, 0x7F, 0xBF, 0x7D, 0xBD, 0xBC, 0x7C, 0xB4, 0x74, 0x75, 0xB5,
    0x77, 0xB7, 0xB6, 0x76, 0x72, 0xB2, 0xB3, 0x73, 0xB1, 0x71, 0x70, 0xB0, 0x50, 0x90, 0x91,
    0x51, 0x93, 0x53, 0x52, 0x92, 0x96, 0x56, 0x57, 0x97, 0x55, 0x95, 0x94, 0x54, 0x9C, 0x5C,
    0x5D, 0x9D, 0x5F, 0x9F, 0x9E, 0x5E, 0x5A, 0x9A, 0x9B, 0x5B, 0x99, 0x59, 0x58, 0x98, 0x88,
    0x48, 0x49, 0x89, 0x4B, 0x8B, 0x8A, 0x4A, 0x4E, 0x8E, 0x8F, 0x4F, 0x8D, 0x4D, 0x4C, 0x8C,
    0x44, 0x84, 0x85, 0x45, 0x87, 0x47, 0x46, 0x86, 0x82, 0x42, 0x43, 0x83, 0x41, 0x81, 0x80,
    0x40
} ;

const char *Modbus::char_CRLF = "\r\n";
const char *Modbus::char_start = ":";

}
}


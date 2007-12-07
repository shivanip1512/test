/*****************************************************************************
*
*    FILE NAME: mctStruct.h
*
*    DATE: 11/22/2007
*
*    AUTHOR: Eric Rooney
*
*    PURPOSE: CCU Simulator
*
*    DESCRIPTION: This struct is passed from serverNexus to the CCU711 to give it
*     the current mct database values
*
*    Copyright (C) 2007 Cannon Technologies, Inc.  All rights reserved.
*****************************************************************************/
#ifndef  __MCTSTRUCT_H__
#define  __MCTSTRUCT_H__

//struct to be sent to CCU711 containing: mct address, timestamp, and kwhvalue
struct mctStruct
{
    public:
    int getmctAddress()
    {
        return _mctAddress;
    }
    void setmctAddress(long int address)
    {
        _mctAddress=address;
    }
    void setTime(RWDBDateTime timestamp)
    {
        _timestamp=timestamp;
    }
    RWDBDateTime getTimestamp()
    {
        return _timestamp;
    }
    double getKwhValue()
    {
        return _kwhvalue;
    }
    void setKwhValue(double KwhValue)
    {
        _kwhvalue=KwhValue;
    }

    private:
    RWDBDateTime _timestamp;
    int _mctAddress;
    double _kwhvalue;
};

#endif


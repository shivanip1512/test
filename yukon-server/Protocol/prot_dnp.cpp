#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   prot_dnp
*
* Date:   5/6/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/05/30 15:11:35 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "prot_dnp.h"

CtiProtocolDNP::CtiProtocolDNP()    {}

CtiProtocolDNP::CtiProtocolDNP(int address)
{
    _address = address;
}

CtiProtocolDNP::CtiProtocolDNP(const CtiProtocolDNP &aRef)
{
    *this = aRef;
}

CtiProtocolDNP::~CtiProtocolDNP()   {}

CtiProtocolDNP &CtiProtocolDNP::operator=(const CtiProtocolDNP &aRef)
{
    if( this != &aRef )
    {

    }

    return *this;
}


void CtiProtocolDNP::setCommand( DNPCommand command, XferPoint *points, int numPoints )
{
    switch( command )
    {
        case DNP_Class0Read:
            {
                dnp_point_descriptor cls0rd;

                cls0rd.group     = 60;
                cls0rd.variation =  1;
                cls0rd.qual_code =  6;
                cls0rd.qual_idx  =  0;
                cls0rd.qual_x    =  0;  //  unused bit

                _appLayer.setCommand(CtiDNPApplication::RequestRead);
                _appLayer.addPoint(&cls0rd);

                _appLayer.initForOutput();

                break;
            }
        case DNP_SetAnalogOut:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        case DNP_SetDigitalOut:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        default:
            command = DNP_Invalid;
    }

    _currentCommand = command;
}


void CtiProtocolDNP::initForInput( void )
{
    _appLayer.initForInput();
}


int CtiProtocolDNP::commOut( OUTMESS *OutMessage, RWTPtrSlist< OUTMESS > &outList )
{
    return _appLayer.commOut(OutMessage, outList);
}


int CtiProtocolDNP::commIn ( INMESS *InMessage, RWTPtrSlist< OUTMESS > &outList )
{
    return _appLayer.commIn(InMessage, outList);
}


bool CtiProtocolDNP::hasPoints( void )
{
    return _appLayer.hasPoints();
}


void CtiProtocolDNP::sendPoints( RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList )
{
    _appLayer.sendPoints(vgList, retList);
}


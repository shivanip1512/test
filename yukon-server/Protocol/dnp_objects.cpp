#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dnp_objects
*
* Date:   5/21/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/06/24 18:13:40 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dnp_objects.h"


CtiDNPObjectBlock::CtiDNPObjectBlock()
{

}

CtiDNPObjectBlock::CtiDNPObjectBlock( enum QualifierType type )
{

}

CtiDNPObjectBlock::~CtiDNPObjectBlock()
{

}

void CtiDNPObjectBlock::setStart( unsigned short )
{

}

void CtiDNPObjectBlock::setStop( unsigned short )
{

}

/*    void CtiDNPObjectBlock::addObject( CtiDNPObject *object )
    void CtiDNPObjectBlock::addObject( CtiDNPObject *object, unsigned short index )

    CtiDNPObject *CtiDNPObjectBlock::getObject( int index );*/

int  CtiDNPObjectBlock::getLength( void )
{
    return 0;
}

void CtiDNPObjectBlock::serialize( char *buf )
{

}

int CtiDNPObjectBlock::restore( unsigned char *buf, int len )
{
    return len;
}

bool CtiDNPObjectBlock::hasPoints( void )
{
    return false;
}



#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   verification_report
*
* Date:   4/9/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/04/12 17:14:35 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "verification_report.h"

//============================================================================================================
//============================================================================================================

CtiVerificationReport::CtiVerificationReport()
{
}

//============================================================================================================
//============================================================================================================

CtiVerificationReport::CtiVerificationReport( const CtiVerificationReport& aRef )
{
   *this = aRef;
}

//============================================================================================================
//============================================================================================================

CtiVerificationReport::~CtiVerificationReport()
{

}

//============================================================================================================
//============================================================================================================

RWCString CtiVerificationReport::getData( void )
{
   return _receivedData;
}

//============================================================================================================
//============================================================================================================

void CtiVerificationReport::setData( RWCString input )
{
   _receivedData = input;
}

//============================================================================================================
//============================================================================================================

RWTime CtiVerificationReport::getTransmitTime( void )
{
   return _txTime;
}

//============================================================================================================
//============================================================================================================

void CtiVerificationReport::setTransmitTime( RWTime aTime )
{
   _txTime = aTime;
}


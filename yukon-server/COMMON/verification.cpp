
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   verification
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

#include "verification.h"

//============================================================================================================
//============================================================================================================

CtiVerification::CtiVerification()
{
}

//============================================================================================================
//============================================================================================================

CtiVerification::CtiVerification( const CtiVerification& aRef )
{
   *this = aRef;
}

//============================================================================================================
//============================================================================================================

CtiVerification::~CtiVerification()
{
}

//============================================================================================================
//============================================================================================================

int CtiVerification::getType( void )
{
   return( _type );
}

//============================================================================================================
//============================================================================================================

void CtiVerification::setType( int aType )
{
   _type = aType;
}

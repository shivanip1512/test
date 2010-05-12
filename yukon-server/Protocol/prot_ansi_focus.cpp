
#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   prot_ansi_focus
*
* Date:   8/27/2004
*
* Author: Julie Richter
*


*-----------------------------------------------------------------------------*/


#include "guard.h"
#include "logger.h"
#include "prot_ansi_focus.h"
#include "ctidate.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiProtocolANSI_focus::CtiProtocolANSI_focus( void )
: CtiProtocolANSI()
{
        
}

CtiProtocolANSI_focus::~CtiProtocolANSI_focus( void )
{
}

void CtiProtocolANSI_focus::setAnsiDeviceType()
{
    // 3 = focus
    getApplicationLayer().setAnsiDeviceType(3);
    return;
}



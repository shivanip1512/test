
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_mark_v
*
* Date:   7/16/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/08/06 19:49:36 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "porter.h"
#include "logger.h"
#include "dev_mark_v.h"
#include "utility.h"


/*
SchlumbergerCommandBlk_t   vectronScanCommands[] =
{
    {0x1B1F,             0x1B36,        (0x1B1F-0x1B1F),         24,      "Voltage/Amps  Instantaneous"},
    {0x1D18,             0x1D9D,        (0x1D18-0x1D18),         11,      "Unit Type/ID"},
    {0x1D18,             0x1D9D,        (0x1D28-0x1D18),          4,      "Register Multiplier"},
    {0x1D18,             0x1D9D,        (0x1D98-0x1D18),          6,      "Register Mapping"},
    {0x2118,             0x21DF,        (0x2118-0x2118),          8,      "Reg 2 Rate E Demand or Energy"},
    {0x2118,             0x21DF,        (0x2120-0x2118),         12,      "Reg 1 Rate E Max Demand, Cum"},
    {0x2118,             0x21DF,        (0x2130-0x2118),          4,      "Demand Reset Count/Outage Count"},
    {0x2118,             0x21DF,        (0x2134-0x2118),          4,      "Reg 3 Rate E Cum"},
    {0x2118,             0x21DF,        (0x2138-0x2118),          8,      "Reg 1 Rate A Max Demand"},
    {0x2118,             0x21DF,        (0x2140-0x2118),          4,      "Reg 4 Rate E Cum"},
    {0x2118,             0x21DF,        (0x2144-0x2118),          8,      "Reg 1 Rate B Max Demand"},
    {0x2118,             0x21DF,        (0x2154-0x2118),          8,      "Reg 1 Rate C Max Demand"},
    {0x2118,             0x21DF,        (0x2164-0x2118),          8,      "Reg 1 Rate D Max Demand"},
    {0x2118,             0x21DF,        (0x2172-0x2118),         28,      "Reg 2 Rate A,B Demand or A-D Energy"},
    {0x2118,             0x21DF,        (0x21CB-0x2118),          1,      "Register Flags"},
    {0x2118,             0x21DF,        (0x21D0-0x2118),          8,      "Reg 3 Rate E Demand or Energy"},
    {0x2118,             0x21DF,        (0x21D8-0x2118),          8,      "Reg 4 Rate E Demand or Energy"},
    {0x2201,             0x222A,        (0x2201-0x2201),          4,      "Software/Firmware Revision"},
    {0x2201,             0x222A,        (0x2220-0x2201),         11,      "ProgramID/MeterID"},
    {NULL,               NULL,          NULL,            0,      ""}     // last command packet
};
*/

//=====================================================================================================================
//=====================================================================================================================

CtiDeviceMarkV::CtiDeviceMarkV()
{

}

//=====================================================================================================================
//=====================================================================================================================

CtiDeviceMarkV::~CtiDeviceMarkV()
{

}

//=====================================================================================================================
//=====================================================================================================================

INT CtiDeviceMarkV::GeneralScan( CtiRequestMsg              *pReq,
                                 CtiCommandParser           &parse,
                                 OUTMESS                    *&OutMessage,
                                 RWTPtrSlist< CtiMessage >  &vgList,
                                 RWTPtrSlist< CtiMessage >  &retList,
                                 RWTPtrSlist< OUTMESS >     &outList,
                                 INT                        ScanPriority)
{
   if( OutMessage != NULL )
   {
      setCurrentCommand( CmdScanData );
      EstablishOutMessagePriority( OutMessage, ScanPriority );

      // Load all the other stuff that is needed
      OutMessage->DeviceID  = getID();
      OutMessage->TargetID  = getID();
      OutMessage->Port      = getPortID();
      OutMessage->Remote    = getAddress();
      OutMessage->TimeOut   = 2;
      OutMessage->EventCode = RESULT | ENCODED;
      OutMessage->Sequence  = 0;
      OutMessage->Retry     = 3;

      outList.insert( OutMessage );
   }
   else
   {
      return MEMORY;
   }
   return NoError;
}

//=====================================================================================================================
//=====================================================================================================================

INT CtiDeviceMarkV::ResultDecode( INMESS                    *InMessage,
                                  RWTime                    &TimeNow,
                                  RWTPtrSlist< CtiMessage > &vgList,
                                  RWTPtrSlist< CtiMessage > &retList,
                                  RWTPtrSlist< OUTMESS >    &outList)
{
   return( 1 );
}

//=====================================================================================================================
//=====================================================================================================================

INT CtiDeviceMarkV::ErrorDecode( INMESS                     *InMessage,
                                 RWTime                     &TimeNow,
                                 RWTPtrSlist< CtiMessage >  &vgList,
                                 RWTPtrSlist< CtiMessage >  &retList,
                                 RWTPtrSlist< OUTMESS >     &outList)
{
   return( 1 );
}

//=====================================================================================================================
//=====================================================================================================================

CtiProtocolTransdata & CtiDeviceMarkV::getProtocol( void )
{
   return _transdataProtocol;
}

//=====================================================================================================================
//=====================================================================================================================
/*
void CtiDeviceMarkV::DecodeDatabaseReader( RWDBReader &rdr )
{
   Inherited::DecodeDatabaseReader( rdr );       // get the base class handled

   if( getDebugLevel() & 0x0800 )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   getProtocol().injectData( getIED().getPassword() );
}
*/

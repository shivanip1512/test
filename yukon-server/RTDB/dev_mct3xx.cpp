#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_mct3XX
*
* Date:   8/21/2001
*
* Author: Matthew Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :
* REVISION     :
* DATE         :
*
* Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include "device.h"
#include "devicetypes.h"
#include "dev_mct3XX.h"
#include "logger.h"
#include "mgr_point.h"
#include "porter.h"
#include "prot_emetcon.h"
#include "utility.h"

set< CtiDLCCommandStore > CtiDeviceMCT3XX::_commandStore;


CtiDeviceMCT3XX::CtiDeviceMCT3XX( )  { }

CtiDeviceMCT3XX::CtiDeviceMCT3XX( const CtiDeviceMCT3XX &aRef )
{
    *this = aRef;
}

CtiDeviceMCT3XX::~CtiDeviceMCT3XX( )  { }

CtiDeviceMCT3XX &CtiDeviceMCT3XX::operator=( const CtiDeviceMCT3XX &aRef )
{
    if( this != &aRef )
    {
        Inherited::operator=( aRef );
    }

    return *this;
}


bool CtiDeviceMCT3XX::initCommandStore( )
{
    bool failed = false;

    CtiDLCCommandStore cs;

    cs._cmd     = CtiProtocolEmetcon::PutValue_KYZ;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair(MCT3XX_PUTMREAD0_ADDRESS, MCT3XX_PUTMREAD_LENGTH);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutValue_KYZ2;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair(MCT3XX_PUTMREAD1_ADDRESS, MCT3XX_PUTMREAD_LENGTH);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutValue_KYZ3;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair(MCT3XX_PUTMREAD2_ADDRESS, MCT3XX_PUTMREAD_LENGTH);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetValue_Powerfail;
    cs._io      = IO_READ;
    cs._funcLen = make_pair(MCT3XX_PFCOUNT_ADDRESS, MCT3XX_PFCOUNT_LENGTH);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutValue_ResetPFCount;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair(MCT3XX_PFCOUNT_ADDRESS, MCT3XX_PFCOUNT_LENGTH);
    _commandStore.insert( cs );

    return failed;
}


bool CtiDeviceMCT3XX::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    if( _commandStore.empty( ) )  //  Must initialize!
    {
        CtiDeviceMCT3XX::initCommandStore();
    }

    CTICMDSET::iterator itr = _commandStore.find(CtiDLCCommandStore(cmd));

    if( itr != _commandStore.end( ) )
    {
        CtiDLCCommandStore &cs = *itr;
        function = cs._funcLen.first;   //  Copy over the found function!
        length   = cs._funcLen.second;  //  Copy over the found length!
        io       = cs._io;              //  Copy over the found io indicator!

        found = true;
    }
    else                                //  Look in the parent if not found in the child!
    {
        found = Inherited::getOperation( cmd, function, length, io );
    }

    return found;
}


//  ResultDecode MUST decode all CtiDLCCommand_t which are defined in the initCommandStore object.  The only exception to this
//    would be a child whose decode was identical to the parent, but whose request was done differently..
//    This MAY be the case for example in an IED scan.

INT CtiDeviceMCT3XX::ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT status = NORMAL;

    switch( InMessage->Sequence )
    {
        default:
        {
            status = Inherited::ResultDecode( InMessage, TimeNow, vgList, retList, outList );

            if( status != NORMAL )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime( ) << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " IM->Sequence = " << InMessage->Sequence << " " << getName( ) << endl;
            }

            break;
        }
    }

    return status;
}


#pragma warning( disable : 4786 )

#ifndef __DEV_ION_H__
#define __DEV_ION_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   dev_ion.h
 *
 * Class:  CtiDeviceION
 * Date:   06/05/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include <windows.h>
#include "ctitypes.h"
#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_meter.h"
#include "mgr_point.h"

#include "ion_rootclasses.h"
#include "ion_valuebasictypes.h"
#include "ion_classtypes.h"
#include "ion_netlayers.h"

class IM_EX_DEVDB CtiDeviceION : public CtiDeviceMeter
{
public:

    CtiDeviceION( ) { setIONState(IONStateUninitialized); };
    ~CtiDeviceION( ) { freeDataBins( ); };


    virtual INT ExecuteRequest( CtiRequestMsg              *pReq,
                                CtiCommandParser           &parse,
                                OUTMESS                   *&OutMessage,
                                RWTPtrSlist< CtiMessage >  &vgList,
                                RWTPtrSlist< CtiMessage >  &retList,
                                RWTPtrSlist< OUTMESS >     &outList);

    INT executeScan( CtiRequestMsg              *pReq,
                     CtiCommandParser           &parse,
                     OUTMESS                   *&OutMessage,
                     RWTPtrSlist< CtiMessage >  &vgList,
                     RWTPtrSlist< CtiMessage >  &retList,
                     RWTPtrSlist< OUTMESS >     &outList );

    int GeneralScan( CtiRequestMsg              *pReq,
                     CtiCommandParser           &parse,
                     OUTMESS                   *&OutMessage,
                     RWTPtrSlist< CtiMessage >  &vgList,
                     RWTPtrSlist< CtiMessage >  &retList,
                     RWTPtrSlist< OUTMESS >     &outList,
                     int ScanPriority = MAXPRIORITY - 4 );

   // interrogation routines
    int generateCommand           ( CtiXfer &Transfer );
//    virtual INT generateCommandHandshake  ( CtiXfer &Transfer );
//    virtual INT generateCommandScan       ( CtiXfer &Transfer );
//    virtual INT generateCommandLoadProfile( CtiXfer &Transfer );
//    virtual INT generateCommandSelectMeter( CtiXfer &Transfer );

    int decodeResponse           ( CtiXfer &Transfer, int commReturnValue );
//    virtual INT decodeResponseHandshake  ( CtiXfer &Transfer, INT commReturnValue );
//    virtual INT decodeResponseScan       ( CtiXfer &Transfer, INT commReturnValue );
//    virtual INT decodeResponseSelectMeter( CtiXfer &Transfer, INT commReturnValue );
//    virtual INT decodeResponseLoadProfile( CtiXfer &Transfer, INT commReturnValue );

    int copyLoadProfileData( unsigned char *aInMessBuffer, unsigned long &aTotalBytes );

    int allocateDataBins( OUTMESS *outMess );
    int freeDataBins( );


    int ResultDecode( INMESS *InMessage,
                      RWTime &TimeNow,
                      RWTPtrSlist< CtiMessage > &vgList,
                      RWTPtrSlist< CtiMessage > &retList,
                      RWTPtrSlist< OUTMESS > &outList );

    int decodeResultScan( INMESS *InMessage,
                          RWTime &TimeNow,
                          RWTPtrSlist< CtiMessage > &vgList,
                          RWTPtrSlist< CtiMessage > &retList,
                          RWTPtrSlist< OUTMESS >    &outList );

    int decodeResultLoadProfile( INMESS *InMessage,
                                 RWTime &TimeNow,
                                 RWTPtrSlist< CtiMessage > &vgList,
                                 RWTPtrSlist< CtiMessage > &retList,
                                 RWTPtrSlist< OUTMESS >    &outList );

    enum
    {
        IONFeatureManagerHandle = 2
    };

private:

    void resolveNextState( void );
    void resolveNextStateSelectMeter( void );
    void resolveNextStateScanData( void );
    void resolveNextStateLoadProfile( void );

    enum IONStates;

    IONStates getIONState( void ) { return _IONState; };
    void      setIONState( IONStates newState ) { _IONState = newState; };

    enum IONStates
    {
        IONStateUninitialized,
        IONStateInit,
        IONStateRequestFeatureManagerInfo,
        IONStateReceiveFeatureManagerInfo,
        IONStateRequestManagerInfo,
        IONStateReceiveManagerInfo,
        IONStateRequestModuleInfo,
        IONStateReceiveModuleInfo,
        IONStateRequestRegisterInfo,
        IONStateReceiveRegisterInfo,
        IONStateRequestData,
        IONStateReceiveData,
        IONStateComplete,
        IONStateAbort
    } _IONState;

    CtiIONApplicationLayer *_appLayer;
    CtiIONNetworkLayer     *_netLayer;
    CtiIONDataLinkLayer    *_dllLayer;
    CtiIONDataStream       *_dsBuf;

    unsigned long _lastLPTime;
};

#endif //  #ifndef __DEV_ION_H__


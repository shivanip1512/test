/*-----------------------------------------------------------------------------*
*
* File:   dev_mct470
*
* Date:   2005-jan-03
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_mct310.cpp-arc  $
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2005/08/12 14:47:37 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include "device.h"
#include "devicetypes.h"
#include "tbl_ptdispatch.h"
#include "dev_mct470.h"
#include "logger.h"
#include "mgr_point.h"
#include "numstr.h"
#include "porter.h"
#include "dllyukon.h"
#include "utility.h"
#include "numstr.h"
#include <string>

using namespace std;
using Cti::Protocol::Emetcon;


const CtiDeviceMCT470::DLCCommandSet CtiDeviceMCT470::_commandStore   = CtiDeviceMCT470::initCommandStore();

CtiDeviceMCT470::CtiDeviceMCT470( ) :
    _lastConfigRequest(0)
{
}

CtiDeviceMCT470::CtiDeviceMCT470( const CtiDeviceMCT470 &aRef )
{
    *this = aRef;
}

CtiDeviceMCT470::~CtiDeviceMCT470( )
{
}

CtiDeviceMCT470 &CtiDeviceMCT470::operator=( const CtiDeviceMCT470 &aRef )
{
    if( this != &aRef )
    {
        Inherited::operator=( aRef );
    }

    return *this;
}


CtiDeviceMCT470::DLCCommandSet CtiDeviceMCT470::initCommandStore( )
{
    CtiDLCCommandStore cs;
    DLCCommandSet s;

    cs._cmd     = Emetcon::Command_Loop;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair( (int)MCT_ModelPos, 1 );
    s.insert( cs );

    cs._cmd     = Emetcon::Scan_Accum;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_MReadPos,
                            (int)MCT470_FuncRead_MReadLen );
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_Default;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_MReadPos,
                            (int)MCT470_FuncRead_MReadLen );
    s.insert(cs);

    cs._cmd     = Emetcon::Scan_Integrity;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_DemandPos,
                            (int)MCT470_FuncRead_DemandLen );
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_Demand;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_DemandPos,
                            (int)MCT470_FuncRead_DemandLen );
    s.insert(cs);

    cs._cmd     = Emetcon::Scan_LoadProfile;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair(0, 0);
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_Demand;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_DemandPos,
                            (int)MCT470_FuncRead_DemandLen );
    s.insert(cs);

    cs._cmd      = Emetcon::GetValue_PeakDemand;
    cs._io       = Emetcon::IO_Function_Read;
    cs._funcLen  = make_pair((int)MCT470_FuncRead_PeakDemandBasePos,
                             (int)MCT470_FuncRead_PeakDemandLen );
    s.insert(cs);

    cs._cmd     = Emetcon::PutConfig_Raw;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair( 0, 0 );  //  filled in later
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_Raw;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair( 0, 0 );  //  filled in later
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_Model;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)MCT470_Memory_ModelPos,
                            (int)MCT470_Memory_ModelLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_Multiplier;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)MCT470_Memory_ChannelMultiplierPos,
                            (int)MCT470_Memory_ChannelMultiplierLen);
    s.insert(cs);

    cs._cmd     = Emetcon::PutConfig_Multiplier;
    cs._io      = Emetcon::IO_Function_Write;
    cs._funcLen = make_pair((int)MCT470_Memory_ChannelMultiplierPos,
                            (int)MCT470_Memory_ChannelMultiplierLen);
    s.insert(cs);

    cs._cmd     = Emetcon::PutConfig_TSync;
    cs._io      = Emetcon::IO_Function_Write;
    cs._funcLen = make_pair((int)CtiDeviceMCT410::FuncWrite_TSyncPos,
                            (int)CtiDeviceMCT410::FuncWrite_TSyncLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_Time;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)MCT470_Memory_RTCPos,
                            (int)MCT470_Memory_RTCLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_TSync;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)MCT470_Memory_LastTSyncPos,
                            (int)MCT470_Memory_LastTSyncLen);
    s.insert(cs);


    cs._cmd     = Emetcon::PutConfig_Intervals;
    cs._io      = Emetcon::IO_Function_Write;
    cs._funcLen = make_pair((int)MCT470_FuncWrite_IntervalsPos,
                            (int)MCT470_FuncWrite_IntervalsLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_Intervals;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)MCT470_Memory_IntervalsPos,
                            (int)MCT470_Memory_IntervalsLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_ChannelSetup;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_ChannelSetupPos,
                            (int)MCT470_FuncRead_ChannelSetupLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_LoadProfile;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair(0, 0);
    s.insert(cs);

    cs._cmd     = Emetcon::GetStatus_LoadProfile;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_LPStatusCh1Ch2Pos,
                            (int)MCT470_FuncRead_LPStatusLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetStatus_Internal;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)MCT470_Memory_StatusPos,
                            (int)MCT470_Memory_StatusLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_IED;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair(0, 13);  //  filled in by "getvalue ied" code
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_IEDDemand;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_IED_RealTime, 9);  //  magic number
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_IEDTime;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_IED_TOU_MeterStatus, 13);  //  magic number
    s.insert(cs);

    cs._cmd     = Emetcon::PutValue_IEDReset;
    cs._io      = Emetcon::IO_Function_Write;
    cs._funcLen = make_pair(0, 0);
    s.insert(cs);

    cs._cmd     = Emetcon::PutStatus_FreezeOne;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair((int)MCT_Command_FreezeOne, 0);
    s.insert(cs);

    cs._cmd     = Emetcon::PutStatus_FreezeTwo;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair((int)MCT_Command_FreezeTwo, 0);
    s.insert(cs);

    return s;
}


bool CtiDeviceMCT470::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    DLCCommandSet::const_iterator itr = _commandStore.find( CtiDLCCommandStore( cmd ) );

    if( itr != _commandStore.end( ) )
    {
        const CtiDLCCommandStore &cs = *itr;

        function = cs._funcLen.first;   //  Copy the relevant bits from the commandStore
        length   = cs._funcLen.second;  //
        io       = cs._io;              //

        found = true;
    }
    //  add this back in later - for now, we want to limit the interaction between the 470 and the 410
/*    else                                //  Look in the parent if not found in the child!
    {
        found = Inherited::getOperation( cmd, function, length, io );
    }
*/
    return found;
}


//  this function checks to see if we have all of the parameters we need to correctly request and interpret
//    load profile date from the MCT
bool CtiDeviceMCT470::isLPDynamicInfoCurrent( void )
{
    bool retval = true;
    long sspec = 0,
         sspec_rev = 0;

    //  grab these two because we'll use them later...
    //    also, the return value is identical to hasDynamicInfo, so we'll just use it the same
    retval &= getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec,         sspec);
    retval &= getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, sspec_rev);

    //  note that we're verifying this against the interval that's in the database - more things will be used this way in the future
    retval &= (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval) == getLoadProfile().getLoadProfileDemandRate());

    //  we don't use the second load profile rate yet
    //retval |= (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval2) == getLoadProfile().getVoltageProfileDemandRate());

    if( retval && sspec == MCT470_Sspec && sspec_rev >= MCT470_SspecRevMin && sspec_rev <= MCT470_SspecRevMax )
    {
        //  we only care about these if we're the correct rev...  otherwise, we ignore everything
        //    we would've done with it.  everything pre-rev E is development only, and needs to be treated with kid gloves

        //  we will need to verify this eventually, and if it doesn't match the 470 config, we'll reconfig the 470 (and complain)
        retval &= hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig);
        retval &= hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval);
    }

    return retval;
}


void CtiDeviceMCT470::requestDynamicInfo(CtiTableDynamicPaoInfo::Keys key, OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList )
{
    bool valid = true;

    OutMessage->DeviceID  = getID();
    OutMessage->TargetID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    OutMessage->Priority  = ScanPriority_LoadProfile;
    OutMessage->TimeOut   = 2;
    OutMessage->Retry     = 2;

    // Tell the porter side to complete the assembly of the message.
    OutMessage->Request.BuildIt = TRUE;

    if( getMCTDebugLevel(MCTDebug_DynamicInfo) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint - device \"" << getName() << "\" requesting key (" << key << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if( key == CtiTableDynamicPaoInfo::Key_MCT_SSpec )
    {
        strncpy(OutMessage->Request.CommandStr, "getconfig model", COMMAND_STR_SIZE );
        OutMessage->Sequence  = Emetcon::GetConfig_Model;     // Helps us figure it out later!
    }
    else
    {
        //  the ideal case - the correct, non-development sspec
        if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec)         == MCT470_Sspec       &&
            getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) >= MCT470_SspecRevMin &&
            getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) <= MCT470_SspecRevMax )
        {
            switch( key )
            {
                //  all of these three are retrieved by this read
                case CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval:
                case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval:
                case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig:
                {
                    strncpy(OutMessage->Request.CommandStr, "getconfig channels", COMMAND_STR_SIZE );
                    OutMessage->Sequence = Emetcon::GetConfig_ChannelSetup;     // Helps us figure it out later!

                    break;
                }
                default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - unhandled key (" << key << ") in CtiDeviceMCT470::requestDynamicInfo **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    delete OutMessage;
                    OutMessage = 0;
                }
            }
        }
        else
        {
            switch( key )
            {
                case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval:
                {
                    strncpy(OutMessage->Request.CommandStr, "getconfig intervals", COMMAND_STR_SIZE );
                    OutMessage->Sequence = Emetcon::GetConfig_Intervals;     // Helps us figure it out later!

                    break;
                }
                default:
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - unhandled key (" << key << ") in CtiDeviceMCT470::requestDynamicInfo **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                //  we don't care about these keys, since this sspec should only be used for pulse inputs...
                //    they only matter for IED reads
                case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig:
                case CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval:
                {
                    delete OutMessage;
                    OutMessage = 0;
                }
            }
        }
    }

    if( OutMessage )
    {
        outList.insert(OutMessage);
        OutMessage = NULL;
    }
}


ULONG CtiDeviceMCT470::calcNextLPScanTime( void )
{
    RWTime        Now;
    unsigned long next_time    = YUKONEOT,
                  planned_time = YUKONEOT;

    if( !isLPDynamicInfoCurrent() )
    {
        //  i'm purposely using the DB rate instead of the fancy getLoadProfileInterval() call here, so it can be ignorant
        //    of the dynamic LP config info - this could probably be improved to look at the minimum of the available
        //    LP intervals (interval 1, interval 2, electronic)
        unsigned int overdue_rate = getLPRetryRate(getLoadProfile().getLoadProfileDemandRate());

        //  only try for the dynamic info at the retry rate, no faster
        next_time  = (Now.seconds() - LPBlockEvacuationTime) + overdue_rate;
        next_time -= (Now.seconds() - LPBlockEvacuationTime) % overdue_rate;

        //  only for consistency...  we don't /really/ have to add on the evacuation time for config reads, but it keeps
        //    outbound requests predictable, which is helpful
        next_time += LPBlockEvacuationTime;
    }
    else
    {
        int demand_rate, block_size;

        for( int i = 0; i < MCT4XX_LPChannels; i++ )
        {
            demand_rate = getLoadProfileInterval(i);
            block_size  = demand_rate * 6;

            if( demand_rate <= 0 )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - device \"" << getName() << "\" has invalid LP rate (" << demand_rate << ") for channel (" << i << ") - setting nextLPtime out 30 minutes **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                next_time = Now.seconds() + (30 * 60);
            }
            else
            {
                CtiPointBase *pPoint = getDevicePointOffsetTypeEqual((i+1) + MCT_PointOffset_LoadProfileOffset, DemandAccumulatorPointType);

                //  if we're not collecting load profile, or there's no point defined, don't scan
                if( !getLoadProfile().isChannelValid(i) || pPoint == NULL )
                {
                    _lp_info[i].current_schedule = YUKONEOT;

                    continue;
                }

                //  uninitialized - get the readings from the DB
                if( !_lp_info[i].archived_reading )
                {
                    //  so we haven't talked to it yet
                    _lp_info[i].archived_reading = 86400;
                    _lp_info[i].current_request  = 86400;

                    CtiTablePointDispatch pd(pPoint->getPointID());

                    if(pd.Restore().errorCode() == RWDBStatus::ok)
                    {
                        _lp_info[i].archived_reading = pd.getTimeStamp().rwtime().seconds();
                    }
                }

                //  basically, we plan to request again after a whole block has been recorded...
                //    then we add on a little bit to make sure the MCT is out of the memory
                planned_time  = _lp_info[i].archived_reading + block_size;
                planned_time -= planned_time % block_size;  //  make double sure we're block-aligned
                planned_time += LPBlockEvacuationTime;      //  add on the safeguard time

                /*
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Checkpoint - lp calctime check... **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "planned_time = " << planned_time << endl;
                    dout << "_lp_info[" << i << "].archived_reading = " << _lp_info[i].archived_reading << endl;
                    dout << "_lp_info[" << i << "].current_schedule = " << _lp_info[i].current_schedule << endl;
                }
                */

                _lp_info[i].current_schedule = planned_time;

                //  if we've already made the request for a block, or we're overdue...  almost the same thing
                if( (_lp_info[i].current_request >= _lp_info[i].archived_reading) ||
                    (planned_time <= Now.seconds()) )
                {
                    unsigned int overdue_rate = getLPRetryRate(demand_rate);

                    _lp_info[i].current_schedule  = (Now.seconds() - LPBlockEvacuationTime) + overdue_rate;
                    _lp_info[i].current_schedule -= (Now.seconds() - LPBlockEvacuationTime) % overdue_rate;

                    _lp_info[i].current_schedule += LPBlockEvacuationTime;
                }

                if( next_time > _lp_info[i].current_schedule )
                {
                    next_time = _lp_info[i].current_schedule;
                }

                /*
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Checkpoint - lp calctime check... **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "planned_time = " << planned_time << endl;
                    dout << "_lp_info[" << i << "].archived_reading = " << _lp_info[i].archived_reading << endl;
                    dout << "_lp_info[" << i << "].current_schedule = " << _lp_info[i].current_schedule << endl;
                    dout << "next_time = " << next_time << endl;
                }
                */
            }
        }
    }

    if( getMCTDebugLevel(MCTDebug_LoadProfile) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << getName() << "'s next Load Profile request at " << RWTime(next_time) << endl;
    }

    return (_nextLPScanTime = next_time);
}


void CtiDeviceMCT470::sendIntervals( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList )
{
    // Load all the other stuff that is needed
    OutMessage->DeviceID  = getID();
    OutMessage->TargetID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    // 082002 CGP // OutMessage->RouteID   = getRouteID();
    OutMessage->Priority  = ScanPriority_LoadProfile;
    OutMessage->TimeOut   = 2;
    OutMessage->Sequence  = Emetcon::GetStatus_LoadProfile;     // Helps us figure it out later!
    OutMessage->Retry     = 2;

    // Tell the porter side to complete the assembly of the message.
    OutMessage->Request.BuildIt = TRUE;
    strncpy(OutMessage->Request.CommandStr, "putconfig emetcon intervals", COMMAND_STR_SIZE );

    outList.insert(OutMessage);
    OutMessage = NULL;
}


//  sero-based channel offset
long CtiDeviceMCT470::getLoadProfileInterval( unsigned channel )
{
    long retval = -1;
    string config;

    if( channel < MCT4XX_LPChannels )
    {
        if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, config) )
        {
            //  the at() function call can throw, so this isn't ideal yet - if we ever go to exception based error handling, we
            //    can be more graceful about this
            try
            {
                if( config.at(channel * 3) == '1' )
                {
                    //  leaves it untouched (i.e., -1) if it doesn't have it
                    getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval, retval);
                }
                //  uncomment when we care about LP interval #2
                /*else if( config.at(channel * 3 + 2) == '1' )
                {
                    getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval2, retval);
                }*/
                else
                {
                    getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval, retval);
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - device \"" << getName() << "\" - OOB exception when accessing Key_MCT_LoadProfileConfig **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - device \"" << getName() << "\" - dynamic LP interval not stored for channel " << channel << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - device \"" << getName() << "\" - channel " << channel << " not in range **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    if( retval < 0 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - device \"" << getName() << "\" - load profile interval requested, but value not retrieved...  sending DB value **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retval = getLoadProfile().getLoadProfileDemandRate();
    }

    return retval;
}


INT CtiDeviceMCT470::calcAndInsertLPRequests(OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList)
{
    int nRet = NoError;

    RWTime         Now;
    unsigned int   demand_rate, block_size;
    int            channel, block;
    string         descriptor;
    OUTMESS       *tmpOutMess;

    if( !isLPDynamicInfoCurrent() )
    {
        if( _lastConfigRequest >= (Now.seconds() - rwEpoch) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - config request too soon for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        else
        {
            if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec) )
            {
                requestDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, OutMessage, outList);
            }
            else
            {
                //  check if we're the IED sspec
                if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec)         == MCT470_Sspec       &&
                    getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) >= MCT470_SspecRevMin &&
                    getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) <= MCT470_SspecRevMax )
                {
                    if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval) != getLoadProfile().getLoadProfileDemandRate() )
                    {
                        OUTMESS *om = CTIDBG_new OUTMESS(*OutMessage);

                        //  send the intervals....
                        sendIntervals(om, outList);
                        //  then verify them - the ordering here does matter
                        requestDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval, OutMessage, outList);
                    }

                    if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval) )
                    {
                        //  as i understand it, we can only read this, not write it, so we'll never do a write-then-read confirmation
                        requestDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval, OutMessage, outList);
                    }

                    if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig) )
                    {
                        //  this will need to be changed to check for a match like LoadProfileInterval above -
                        //    if it doesn't match, then re-send and re-read
                        //    (which will happen when the 470 config is added)
                        requestDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, OutMessage, outList);
                    }
                }
                else
                {
                    if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval) != getLoadProfile().getLoadProfileDemandRate() )
                    {
                        if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval) )
                        {
                            OUTMESS *om = new CtiOutMessage(*OutMessage);

                            sendIntervals(om, outList);
                        }

                        requestDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval, OutMessage, outList);
                    }
                }
            }

            _lastConfigRequest = Now.seconds() - rwEpoch;
        }
    }
    else
    {
        for( int i = 0; i < MCT4XX_LPChannels; i++ )
        {
            demand_rate = getLoadProfileInterval(i);
            block_size  = demand_rate * 6;

            if( useScanFlags() )
            {
                if( _lp_info[i].current_schedule > Now )
                {
                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - LP scan too early for device \"" << getName() << "\", aborted **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else
                {
                    tmpOutMess = CTIDBG_new OUTMESS(*OutMessage);

                    _lp_info[i].current_request  = _lp_info[i].archived_reading;

                    //  make sure we only ask for what the function reads can access
                    if( (Now.seconds() - _lp_info[i].current_request) >= (unsigned long)(MCT4XX_LPRecentBlocks * block_size) )
                    {
                        //  go back as far as we can
                        _lp_info[i].current_request  = Now.seconds();
                        _lp_info[i].current_request -= MCT4XX_LPRecentBlocks * block_size;
                    }

                    if( getMCTDebugLevel(MCTDebug_LoadProfile) )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - LP variable check for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Now.seconds() = " << Now.seconds() << endl;
                        dout << "_lp_info[" << i << "].archived_reading = " << _lp_info[i].archived_reading << endl;
                        dout << "MCT4XX_LPRecentBlocks * block_size = " << MCT4XX_LPRecentBlocks * block_size << endl;
                        dout << "_lp_info[" << i << "].current_request = " << _lp_info[i].current_request << endl;
                    }

                    //  make sure we're aligned (note - rwEpoch is an even multiple of 86400, so no worries)
                    _lp_info[i].current_request -= _lp_info[i].current_request % block_size;

                    //  which block to grab?
                    channel = i + 1;
                    block   = (Now.seconds() - _lp_info[i].current_request) / block_size;

                    descriptor = " channel " + CtiNumStr(channel) + " block " + CtiNumStr(block);

                    strncat( tmpOutMess->Request.CommandStr,
                             descriptor.data(),
                             sizeof(tmpOutMess->Request.CommandStr) - strlen(tmpOutMess->Request.CommandStr));

                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS  );
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - command string check for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "\"" << tmpOutMess->Request.CommandStr << "\"" << endl;
                    }

                    outList.insert(tmpOutMess);
                }
            }
        }
    }

    if( OutMessage != NULL )
    {
        delete OutMessage;
        OutMessage = NULL;
    }

    return nRet;
}


bool CtiDeviceMCT470::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
{
    bool retVal = false;
    int  address, block, channel;

    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint - LP parse value check **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "parse.getiValue(\"scan_loadprofile_block\",   0) = " << parse.getiValue("scan_loadprofile_block", 0) << endl;
        dout << "parse.getiValue(\"scan_loadprofile_channel\", 0) = " << parse.getiValue("scan_loadprofile_channel", 0) << endl;
    }

    block   = parse.getiValue("scan_loadprofile_block",   0);
    channel = parse.getiValue("scan_loadprofile_channel", 0);

    if( block && channel && block <= MCT4XX_LPRecentBlocks && channel <= MCT4XX_LPChannels )
    {
        channel--;
        block--;    //  adjust to be a zero-based offset

        address  = 0x50 + (0x10 * channel);
        address += block;

        OutMessage->Buffer.BSt.Function = address;
        OutMessage->Buffer.BSt.Length   = 13;  //  2 bytes per interval, and the table pointer
        OutMessage->Buffer.BSt.IO       = Emetcon::IO_Function_Read;

        retVal = true;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - Improperly formed LP request discarded for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;;
        }

        retVal = false;
    }

    return retVal;
}


/*
 *  ResultDecode MUST decode all CtiDLCCommand_t which are defined in the initCommandStore object.  The only exception to this
 *  would be a child whose decode was identical to the parent, but whose request was done differently..
 *  This MAY be the case for example in an IED scan.
 */
INT CtiDeviceMCT470::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    switch(InMessage->Sequence)
    {
        case Emetcon::Scan_Accum:
        case Emetcon::GetValue_Default:
        {
            status = decodeGetValueKWH(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::Scan_Integrity:
        case Emetcon::GetValue_Demand:
        {
            status = decodeGetValueDemand(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetValue_PeakDemand:
        {
            status = decodeGetValuePeakDemand(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetValue_Outage:
        {
            status = decodeGetValueOutage(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetValue_IED:
        case Emetcon::GetValue_IEDDemand:
        {
            status = decodeGetValueIED(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetConfig_IEDTime:
        case Emetcon::GetConfig_IEDScan:
        {
            status = decodeGetConfigIED(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetValue_LoadProfile:
        {
            status = decodeGetValueLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::Scan_LoadProfile:
        {
            status = decodeScanLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetStatus_Internal:
        {
            status = decodeGetStatusInternal(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetStatus_LoadProfile:
        {
            status = decodeGetStatusLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetConfig_Disconnect:
        {
            status = decodeGetConfigDisconnect(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetConfig_Intervals:
        {
            status = decodeGetConfigIntervals(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetConfig_ChannelSetup:
        {
            status = decodeGetConfigChannelSetup(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::GetConfig_Model):
        {
            status = decodeGetConfigModel(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::GetConfig_Time):
        case (Emetcon::GetConfig_TSync):
        {
            //  ooo, but it works
            status = CtiDeviceMCT410::decodeGetConfigTime(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::GetValue_PFCount):
        default:
        {
            status = Inherited::ResultDecode(InMessage, TimeNow, vgList, retList, outList);

            if(status != NORMAL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " IM->Sequence = " << InMessage->Sequence << " " << getName() << endl;
            }
            break;
        }
    }


    return status;
}


INT CtiDeviceMCT470::executeGetValue( CtiRequestMsg              *pReq,
                                      CtiCommandParser           &parse,
                                      OUTMESS                   *&OutMessage,
                                      RWTPtrSlist< CtiMessage >  &vgList,
                                      RWTPtrSlist< CtiMessage >  &retList,
                                      RWTPtrSlist< OUTMESS >     &outList )
{
    INT nRet = NoMethod;

    bool found = false;
    int function;

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                   RWCString(OutMessage->Request.CommandStr),
                                                   RWCString(),
                                                   nRet,
                                                   OutMessage->Request.RouteID,
                                                   OutMessage->Request.MacroOffset,
                                                   OutMessage->Request.Attempt,
                                                   OutMessage->Request.TrxID,
                                                   OutMessage->Request.UserID,
                                                   OutMessage->Request.SOE,
                                                   RWOrdered( ));

    if( parse.getFlags() & CMD_FLAG_GV_IED )  //  This parse has the token "IED" in it!
    {
        if( parse.getFlags() & CMD_FLAG_GV_DEMAND )
        {
            //  this will be for the real-time table
            function = Emetcon::GetValue_IEDDemand;
            found = getOperation( function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO );
        }
        // else if()  //  this is where the IED status would be handled
        else
        {
            function = Emetcon::GetValue_IED;

            found = getOperation( function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO );

            if( parse.getFlags() & CMD_FLAG_GV_RATET )
            {
                OutMessage->Buffer.BSt.Function = MCT470_FuncRead_IED_TOU_CurrentTotals;
            }
            else
            {
                if( parse.getFlags() & CMD_FLAG_GV_KVARH || parse.getFlags() & CMD_FLAG_GV_KVAH  )
                {
                    OutMessage->Buffer.BSt.Function = MCT470_FuncRead_IED_TOU_CurrentKMBase;
                }
                else
                {
                    OutMessage->Buffer.BSt.Function = MCT470_FuncRead_IED_TOU_CurrentKWBase;
                }

                if(      parse.getFlags() & CMD_FLAG_GV_RATEA )  OutMessage->Buffer.BSt.Function += 0;
                else if( parse.getFlags() & CMD_FLAG_GV_RATEB )  OutMessage->Buffer.BSt.Function += 1;
                else if( parse.getFlags() & CMD_FLAG_GV_RATEC )  OutMessage->Buffer.BSt.Function += 2;
                else if( parse.getFlags() & CMD_FLAG_GV_RATED )  OutMessage->Buffer.BSt.Function += 3;
            }

            if( parse.getFlags() & CMD_FLAG_FROZEN )
            {
                OutMessage->Buffer.BSt.Function += MCT470_FuncRead_IED_TOU_PreviousOffset;
            }
        }
    }
    else if( parse.getFlags() & CMD_FLAG_GV_PEAK )
    {
        function = Emetcon::GetValue_PeakDemand;
        found = getOperation( function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO );

        if( parse.getFlags() & CMD_FLAG_OFFSET )
        {
            switch( parse.getOffset() )
            {
                //  both 0 and 1 resolve to the first register
                case 0:     break;
                case 1:     break;
                //  please note the magic numbers
                case 2:     OutMessage->Buffer.BSt.Function += 2;   break;
                case 3:     OutMessage->Buffer.BSt.Function += 6;   break;
                case 4:     OutMessage->Buffer.BSt.Function += 8;   break;

                //  anything outside the range from 0-4 is invalid
                default:    found = false;  break;
            }
        }

        if( parse.getFlags() & CMD_FLAG_FROZEN )
        {
            //  magic number
            OutMessage->Buffer.BSt.Function += 1;
        }
    }
    else if( parse.isKeyValid("lp_command") )  //  kinda clunky...  the 400 series needs a base object
    {
        nRet = CtiDeviceMCT410::executeGetValue(pReq, parse, OutMessage, vgList, retList, outList);
    }
    else
    {
        nRet = CtiDeviceMCT::executeGetValue(pReq, parse, OutMessage, vgList, retList, outList);
    }

    if( found )
    {
        // Load all the other stuff that is needed
        //  FIXME:  most of this is taken care of in propagateRequest - we could probably trim a lot of this out
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString(), COMMAND_STR_SIZE);

        nRet = NoError;
    }

    return nRet;
}


INT CtiDeviceMCT470::executeGetConfig( CtiRequestMsg              *pReq,
                                       CtiCommandParser           &parse,
                                       OUTMESS                   *&OutMessage,
                                       RWTPtrSlist< CtiMessage >  &vgList,
                                       RWTPtrSlist< CtiMessage >  &retList,
                                       RWTPtrSlist< OUTMESS >     &outList )
{
    INT nRet = NoMethod;

    bool found = false;
    int function;

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                   RWCString(OutMessage->Request.CommandStr),
                                                   RWCString(),
                                                   nRet,
                                                   OutMessage->Request.RouteID,
                                                   OutMessage->Request.MacroOffset,
                                                   OutMessage->Request.Attempt,
                                                   OutMessage->Request.TrxID,
                                                   OutMessage->Request.UserID,
                                                   OutMessage->Request.SOE,
                                                   RWOrdered( ));

    if( parse.isKeyValid("channels") )
    {

    }
    if( parse.isKeyValid("ied") )
    {
        //  ACH:  add a dynamic info check to ensure that we're reading from Precanned Table 1
        if(parse.isKeyValid("time"))
        {
            function = Emetcon::GetConfig_IEDTime;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
        else if( parse.isKeyValid("scan"))
        {
            function = Emetcon::GetConfig_IEDScan;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
    }
    else
    {
        //  note - bypassing MCT410 - we want to keep them seperate for now, though there will be a 400-series base class eventually
        nRet = CtiDeviceMCT::executeGetConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

    if( found )
    {
        // Load all the other stuff that is needed
        //  FIXME:  most of this is taken care of in propagateRequest - we could probably trim a lot of this out
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString(), COMMAND_STR_SIZE);

        nRet = NoError;
    }

    return nRet;
}


INT CtiDeviceMCT470::decodeGetValueKWH(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;
    ULONG i,x;
    INT pid;
    RWCString resultString;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    unsigned long pulses;
    point_info_t  pi;

    CtiPointBase         *pPoint = NULL;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Accumulator Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    //  ACH:  are these necessary?  /mskf
    resetScanFreezePending();
    resetScanFreezeFailed();


    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        for( i = 0; i < MCT470_ChannelCount; i++ )
        {
            pPoint = getDevicePointOffsetTypeEqual( 1 + i, PulseAccumulatorPointType );

            point_info_t pi = getData(DSt->Message + (i * 3), 3, ValueType_Accumulator);

            // handle accumulator data here
            if( pPoint != NULL)
            {
                // 24 bit pulse value
                pi.value = ((CtiPointNumeric *)pPoint)->computeValueForUOM(pi.value);

                resultString = "";

                if( pi.quality != InvalidQuality )
                {
                    resultString += getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                    if( pData = makePointDataMsg(pPoint, pi, resultString) )
                    {
                        pData->setTime(RWTime::now() - (RWTime::now().seconds() % 300) );

                        ReturnMsg->PointData().insert(pData);
                        pData = NULL;  // We just put it on the list...
                    }
                }
                else
                {
                    resultString += ReturnMsg->ResultString();
                    resultString += getName() + " / " + pPoint->getName() + " = (invalid data)";

                    ReturnMsg->setResultString(resultString);
                }
            }
            else if( i == 0 )
            {
                if( pi.quality != InvalidQuality )
                {
                    resultString += getName() + " / KYZ 1 = " + CtiNumStr(pi.value) + "  --  POINT UNDEFINED IN DB";
                }
                else
                {
                    resultString += getName() + " / KYZ 1 = (invalid data) --  POINT UNDEFINED IN DB";
                }

                ReturnMsg->setResultString(resultString);
            }
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetValueDemand(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int          status = NORMAL, i;
    point_info_t pi;
    RWCString    resultString, pointString, stateName;

    INT ErrReturn = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    CtiPointBase    *pPoint = NULL;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    resetScanPending();  //  needed for dev_single...  sets this in initiateIntegrityScan
    setMCTScanPending(ScanRateIntegrity, false);

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        for( i = 0; i < 8; i++ )
        {
            if( pPoint = getDevicePointOffsetTypeEqual(i + 1, StatusPointType) )
            {
                pi.value   = (DSt->Message[0] >> i) & 0x01;
                pi.quality = NormalQuality;

                stateName = ResolveStateName(pPoint->getStateGroupID(), pi.value);

                if( !stateName.isNull() )
                {
                    resultString += getName() + " / " + pPoint->getName() + ":" + stateName;
                }
                else
                {
                    resultString += getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value);
                }

                if(pData = makePointDataMsg(pPoint, pi, resultString))
                {
                    ReturnMsg->PointData().insert(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
        }

        for(int i = 0; i < 5; i++)
        {
            if( i == 4 )
            {
                pi = getData(DSt->Message + (i * 2) + 1, 2, ValueType_Raw);

                pPoint = getDevicePointOffsetTypeEqual(MCT_PointOffset_Accumulator_Powerfail, PulseAccumulatorPointType);
            }
            else
            {
                pi = getData(DSt->Message + (i * 2) + 1, 2, ValueType_KW);

                pPoint = getDevicePointOffsetTypeEqual(i + 1, DemandAccumulatorPointType);

                //  turn raw pulses into a demand reading
                pi.value *= double(3600 / getDemandInterval());
            }

            if( pPoint )
            {
                RWTime pointTime;

                pi.value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(pi.value);

                pointString  = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
                //  if the point exists, we don't need the "POINT UNDEFINED" guys hanging around
                resultString = "";

                if( pData = makePointDataMsg(pPoint, pi, pointString) )
                {
                    if( i != 4 )
                    {
                        pointTime -= pointTime.seconds() % getDemandInterval();
                        pData->setTime( pointTime );
                    }

                    ReturnMsg->PointData().insert(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else if( i == 4 )
            {
                resultString += getName() + " / Blink Counter = " + CtiNumStr(pi.value) + "\n";
            }
            else if( i == 0 )
            {
                resultString += getName() + " / Channel 1 Demand = " + CtiNumStr(pi.value) + "  --  POINT UNDEFINED IN DB\n";
            }
        }

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetValuePeakDemand(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int          status = NORMAL, base_offset, point_offset;
    point_info_t pi, pi_time;
    RWCString    resultString, pointString, stateName;
    RWTime       pointTime;

    INT ErrReturn = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    CtiPointBase    *pPoint = NULL;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Peak Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        base_offset = parse.getOffset();

        if( base_offset < 1 || base_offset > 4 )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - offset = " << base_offset << " - resetting to 1 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            base_offset = 1;
        }

        point_offset = base_offset + MCT4XX_PointOffset_PeakOffset;

        if( parse.getFlags() & CMD_FLAG_FROZEN )
        {
            point_offset += MCT4XX_PointOffset_FrozenOffset;
        }

        pi      = getData(DSt->Message + 0, 2, ValueType_KW);
        pi_time = getData(DSt->Message + 2, 4, ValueType_Raw);

        //  turn raw pulses into a demand reading
        pi.value *= double(3600 / getDemandInterval());

        pointTime = RWTime(pi_time.value + rwEpoch);

        if( pPoint = getDevicePointOffsetTypeEqual(point_offset, DemandAccumulatorPointType) )
        {
            pi.value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(pi.value);

            pointString  = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
            if( pi_time.value > MCT4XX_DawnOfTime )
            {
                pointString += " @ " + pointTime.asString();
            }
            else
            {
                pointString += " @ (invalid time)";
            }

            if( pData = makePointDataMsg(pPoint, pi, pointString) )
            {
                pointTime -= pointTime.seconds() % getDemandInterval();
                pData->setTime(pointTime);

                ReturnMsg->PointData().insert(pData);
                pData = 0;  // We just put it on the list...
            }
        }
        else
        {
            resultString += getName() + " / Channel " + CtiNumStr(base_offset) + " Max Demand = " + CtiNumStr(pi.value) + " @ " + pointTime.asString() + "  --  POINT UNDEFINED IN DB\n";
        }

        pi      = getData(DSt->Message + 6, 2, ValueType_KW);
        pi_time = getData(DSt->Message + 8, 4, ValueType_Raw);

        //  turn raw pulses into a demand reading
        pi.value *= double(3600 / getDemandInterval());

        pointTime = RWTime(pi_time.value + rwEpoch);

        //  use the max point for the computation, if we've got it
        if( pPoint )
        {
            pi.value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(pi.value);

            pointString  = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
            if( pi_time.value > MCT4XX_DawnOfTime )
            {
                pointString += " @ " + pointTime.asString();
            }
            else
            {
                pointString += " @ (invalid time)";
            }

            //  we don't actually send a pointdata message for the min point, so we tack the results onto the result string instead
            resultString += pointString;
        }
        else
        {
            resultString += getName() + " / Channel " + CtiNumStr(base_offset) + " Min Demand = " + CtiNumStr(pi.value) + " @ " + pointTime.asString() + "  --  POINT UNDEFINED IN DB\n";
        }

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetValueIED(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int             status = NORMAL,
                    frozen = 0,
                    offset = 0,
                    rate   = 0;
    point_info_t    pi;
    RWCString       point_string, resultString;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** IED Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    resetScanPending();  //  needed for dev_single...  sets this in initiateIntegrityScan
    setMCTScanPending(ScanRateIntegrity, false);

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        //  should we archive non-frozen points?

        if( parse.getFlags() & CMD_FLAG_GV_DEMAND )
        {
            CtiPointBase *kw, *km;

            pi = getData(DSt->Message, 3, ValueType_Raw);

            if(kw = getDevicePointOffsetTypeEqual(MCT470_PointOffset_TotalKW, AnalogPointType))
            {
                pi.value = ((CtiPointNumeric*)kw)->computeValueForUOM(pi.value);

                point_string = getName() + " / " + kw->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)kw)->getPointUnits().getDecimalPlaces());

                ReturnMsg->PointData().insert(makePointDataMsg(kw, pi, point_string));
            }
            else
            {
                resultString += getName() + " / current KW = " + CtiNumStr(pi.value) + "\n";
            }

            pi = getData(DSt->Message + 3, 3, ValueType_Raw);

            if(km = getDevicePointOffsetTypeEqual(MCT470_PointOffset_TotalKM, AnalogPointType))
            {
                pi.value = ((CtiPointNumeric*)km)->computeValueForUOM(pi.value);

                point_string = getName() + " / " + km->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)km)->getPointUnits().getDecimalPlaces());

                ReturnMsg->PointData().insert(makePointDataMsg(km, pi, point_string));
            }
            else
            {
                resultString += getName() + " / current KM = " + CtiNumStr(pi.value) + "\n";
            }

            pi = getData(DSt->Message + 6, 2, ValueType_Raw);

            resultString += getName() + " / outage count: " + CtiNumStr(pi.value) + "\n";
        }
        else if( parse.getFlags() & CMD_FLAG_GV_RATET )
        {
            CtiPointBase *kwh, *kmh;

            pi = getData(DSt->Message, 5, ValueType_Raw);

            if(kwh = getDevicePointOffsetTypeEqual(MCT470_PointOffset_TotalKWH, AnalogPointType))
            {
                pi.value = ((CtiPointNumeric*)kwh)->computeValueForUOM(pi.value);

                point_string = getName() + " / " + kwh->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)kwh)->getPointUnits().getDecimalPlaces());

                ReturnMsg->PointData().insert(makePointDataMsg(kwh, pi, point_string));
            }
            else
            {
                resultString += getName() + " / KWH total = " + CtiNumStr(pi.value) + "\n";
            }

            pi = getData(DSt->Message + 5, 5, ValueType_Raw);

            if(kmh = getDevicePointOffsetTypeEqual(MCT470_PointOffset_TotalKMH, AnalogPointType))
            {
                pi.value = ((CtiPointNumeric*)kmh)->computeValueForUOM(pi.value);

                point_string = getName() + " / " + kmh->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)kmh)->getPointUnits().getDecimalPlaces());

                ReturnMsg->PointData().insert(makePointDataMsg(kmh, pi, point_string));
            }
            else
            {
                resultString += getName() + " / KMH total = " + CtiNumStr(pi.value) + "\n";
            }

            pi = getData(DSt->Message + 10, 2, ValueType_Raw);

            resultString += getName() + " / Average power factor since last freeze = " + CtiNumStr(pi.value) + "\n";
        }
        else
        {
            CtiPointBase *kwh, *kw;
            point_info_t time_info;
            RWTime peak_time;

            if( parse.getFlags() & CMD_FLAG_GV_KVARH || parse.getFlags() & CMD_FLAG_GV_KVAH  )
            {
                offset = MCT470_PointOffset_TOU_KMBase;
            }
            else
            {
                offset = MCT470_PointOffset_TOU_KWBase;
            }

            if(      parse.getFlags() & CMD_FLAG_GV_RATEA )  rate = 0;
            else if( parse.getFlags() & CMD_FLAG_GV_RATEB )  rate = 1;
            else if( parse.getFlags() & CMD_FLAG_GV_RATEC )  rate = 2;
            else if( parse.getFlags() & CMD_FLAG_GV_RATED )  rate = 3;

            pi = getData(DSt->Message, 5, ValueType_Raw);

            if(kwh = getDevicePointOffsetTypeEqual(offset + rate * 2 + 1, AnalogPointType))
            {
                pi.value = ((CtiPointNumeric*)kwh)->computeValueForUOM(pi.value);

                point_string = getName() + " / " + kwh->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)kwh)->getPointUnits().getDecimalPlaces());

                ReturnMsg->PointData().insert(makePointDataMsg(kwh, pi, point_string));
            }
            else
            {
                resultString += getName() + " / KWH rate " + RWCString((char)('A' + rate)) + " total = " + CtiNumStr(pi.value) + "\n";
            }

            pi        = getData(DSt->Message + 5, 3, ValueType_Raw);
            time_info = getData(DSt->Message + 8, 4, ValueType_Raw);
            peak_time = RWTime((unsigned long)time_info.value + rwEpoch);

            if(kw = getDevicePointOffsetTypeEqual(offset + rate * 2, AnalogPointType))
            {
                CtiPointDataMsg *peak_msg;

                pi.value = ((CtiPointNumeric*)kw)->computeValueForUOM(pi.value);

                point_string  = getName() + " / " + kw->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)kw)->getPointUnits().getDecimalPlaces());
                point_string += " @ " + peak_time.asString() + "\n";

                peak_msg = makePointDataMsg(kw, pi, point_string);
                peak_msg->setTime(peak_time);

                ReturnMsg->PointData().insert(peak_msg);
            }
            else
            {
                resultString += getName() + " / KW rate " + RWCString((char)('A' + rate)) + " peak = " + CtiNumStr(pi.value);

                resultString += " @ " + peak_time.asString() + "\n";
            }
        }

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetConfigIED(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int             status = NORMAL,
                    frozen = 0,
                    offset = 0,
                    rate   = 0;
    point_info_t    pi;
    PointQuality_t  quality;
    RWCString       point_string, resultString;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    resetScanPending();  //  needed for dev_single...  sets this in initiateIntegrityScan
    setMCTScanPending(ScanRateIntegrity, false);

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        //  should we archive non-frozen points?

        switch( InMessage->Sequence )
        {
            case Emetcon::GetConfig_IEDTime:
            {
                point_info_t pi_time  = getData(DSt->Message, 4, ValueType_Raw);
                RWTime       ied_time = RWTime((unsigned long)pi_time.value + rwEpoch);

                resultString += getName() + " / current time: " + ied_time.asString() + "\n";

                /*
                resultString = getName() + " / phase A: " + (DSt->Message[] & 0x02)?"present":"not present" + "\n";
                resultString = getName() + " / phase B: " + (DSt->Message[] & 0x04)?"present":"not present" + "\n";
                resultString = getName() + " / phase C: " + (DSt->Message[] & 0x08)?"present":"not present" + "\n";
                */

                pi = getData(DSt->Message + 5, 2, ValueType_Raw);

                resultString += getName() + " / demand reset count: " + CtiNumStr((int)pi.value) + "\n";

                pi_time  = getData(DSt->Message + 7, 4, ValueType_Raw);
                ied_time = RWTime((unsigned long)pi_time.value + rwEpoch);

                resultString += getName() + " / time of last reset: " + ied_time.asString() + "\n";

                pi = getData(DSt->Message + 11, 2, ValueType_Raw);

                resultString += getName() + " / outage count: " + CtiNumStr((int)pi.value) + "\n";

                break;
            }

            case Emetcon::GetConfig_IEDScan:
            {
                resultString += getName() + " / function not implemented yet\n";

                break;
            }
        }

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetValueLoadProfile(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    RWCString valReport, resultString;
    int interval_len, block_len, function, channel, badData;

    point_info_t  pi;
    unsigned long timeStamp, decode_time;


    CtiPointNumeric *pPoint    = NULL;
    CtiReturnMsg    *ReturnMsg = NULL;  // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData     = NULL;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        channel      = _llpInterest.channel;
        decode_time  = _llpInterest.time + _llpInterest.offset;

        interval_len = getLoadProfileInterval(channel - 1);  //  zero-based
        block_len    = interval_len * 6;

        pPoint = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( channel + MCT_PointOffset_LoadProfileOffset, DemandAccumulatorPointType );

        for( int i = 0; i < 6; i++ )
        {
            //  this is where the block started...
            timeStamp  = decode_time + (interval_len * i);
            //  but we want interval *ending* times, so add on one more interval
            timeStamp += interval_len;

            pi = getData(DSt->Message + (i * 2) + 1, 2, ValueType_KW);

            pi.value *= 3600 / interval_len;

            if( pPoint != NULL )
            {
                if( pi.quality != InvalidQuality )
                {
                    pi.value = pPoint->computeValueForUOM(pi.value);

                    valReport = getName() + " / " + pPoint->getName() + " @ " + RWTime(timeStamp).asString() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                    if( pData = makePointDataMsg(pPoint, pi, valReport) )
                    {
                        pData->setTime(timeStamp);

                        ReturnMsg->insert(pData);
                    }
                }
                else
                {
                    resultString += getName() + " / " + pPoint->getName() + " @ " + RWTime(timeStamp).asString() + " = (invalid data)\n";
                }
            }
            else
            {
                if( pi.quality == NormalQuality )
                {
                    resultString += getName() + " / LP channel " + CtiNumStr(channel) + " @ " + RWTime(timeStamp).asString() + " = " + CtiNumStr(pi.value, 0) + "\n";
                }
                else
                {
                    resultString += getName() + " / LP channel " + CtiNumStr(channel) + " @ " + RWTime(timeStamp).asString() + " = (invalid data)\n";
                }
            }
        }

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeScanLoadProfile(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    string         val_report;
    int            channel, block, interval_len;
    unsigned long  timestamp, pulses;
    point_info_t   pi;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    CtiPointNumeric *point   = 0;
    CtiReturnMsg    *ret_msg = 0;  // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pdata   = 0;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Load Profile Scan Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    //  ACH:  are these necessary?  /mskf
    resetScanFreezePending( );
    resetScanFreezeFailed( );

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ret_msg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ret_msg->setUserMessageId(InMessage->Return.UserID);

        if( (channel = parse.getiValue("scan_loadprofile_channel", 0)) &&
            (block   = parse.getiValue("scan_loadprofile_block",   0)) )
        {
            interval_len = getLoadProfileInterval(channel - 1);

            point = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( channel + MCT_PointOffset_LoadProfileOffset, DemandAccumulatorPointType );

            if( point )
            {
                //  this is where the block started...
                timestamp  = TimeNow.seconds();
                timestamp -= interval_len * 6 * block;
                timestamp -= timestamp % (interval_len * 6);

                if( timestamp == _lp_info[channel - 1].current_request )
                {
                    for( int offset = 5; offset >= 0; offset-- )
                    {
                        pi = getData(DSt->Message + offset*2 + 1, 2, ValueType_KW);

                        //  adjust for the demand interval
                        pi.value *= 3600 / interval_len;

                        //  compute for the UOM
                        pi.value = point->computeValueForUOM(pi.value);

                        if( pdata = makePointDataMsg(point, pi, "") )
                        {
                            pdata->setTags(TAG_POINT_LOAD_PROFILE_DATA);

                            //  the data goes from latest to earliest...  it's kind of backwards
                            pdata->setTime(timestamp + interval_len * (6 - offset));

                            ret_msg->insert(pdata);
                        }
                    }

                    //  unnecessary?
                    setLastLPTime (timestamp + interval_len * 6);

                    _lp_info[channel - 1].archived_reading = timestamp + interval_len * 6;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - possible LP logic error for device \"" << getName() << "\";  calculated timestamp=" << RWTime(timestamp) << "; current_request=" << RWTime(_lp_info[channel - 1].current_request) << endl;
                        dout << "commandstr = " << InMessage->Return.CommandStr << endl;
                    }
                }
            }
            else
            {
                ret_msg->setResultString("No load profile point defined for '" + getName() + "'");
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - missing scan_loadprofile token in decodeScanLoadProfile for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            ret_msg->setResultString("Malformed LP command string for '" + getName() + "'");
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ret_msg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetStatusInternal( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    unsigned char *geneBuf = InMessage->Buffer.DSt.Message;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        RWCString resultString;

        CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg      *pData = NULL;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        resultString += getName() + " / Internal Status:\n";

        resultString += (InMessage->Buffer.DSt.Message[0] & 0x01)?"Group addressing disabled\n":"Group addressing enabled\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x04)?"DST active\n":"DST inactive\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x08)?"Holiday active\n":"";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x10)?"TOU disabled\n":"TOU enabled\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x20)?"Time sync needed\n":"In time sync\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x40)?"Critical peak active\n":"Critical peak inactive\n";

        resultString += (InMessage->Buffer.DSt.Message[1] & 0x01)?"Power fail occurred\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x02)?"Electronic meter communication error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x08)?"Power fail carryover\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x10)?"RTC adjusted\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x20)?"Holiday Event occurred\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x40)?"DST Change occurred\n":"";

        resultString += (InMessage->Buffer.DSt.Message[2] & 0x01)?"Zero usage on channel 1 for 24 hours\n":"";
        resultString += (InMessage->Buffer.DSt.Message[2] & 0x02)?"Zero usage on channel 2 for 24 hours\n":"";
        resultString += (InMessage->Buffer.DSt.Message[2] & 0x04)?"Zero usage on channel 3 for 24 hours\n":"";
        resultString += (InMessage->Buffer.DSt.Message[2] & 0x08)?"Zero usage on channel 4 for 24 hours\n":"";

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetStatusLoadProfile( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT status = NORMAL, lp_channel;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    unsigned char *geneBuf = InMessage->Buffer.DSt.Message;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        lp_channel = (parse.getiValue("loadprofile_offset") < 3)?1:3;

        RWCString resultString;
        unsigned long tmpTime;
        RWTime lpTime;

        CtiReturnMsg     *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg  *pData = NULL;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        resultString += getName() + " / Load Profile Channel " + CtiNumStr(lp_channel) + " Status:\n";

        tmpTime = DSt->Message[0] << 24 |
                  DSt->Message[1] << 16 |
                  DSt->Message[2] <<  8 |
                  DSt->Message[3];

        lpTime = RWTime(tmpTime + rwEpoch);

        resultString += "Current Interval Time: " + lpTime.asString() + "\n";
        resultString += "Current Interval Pointer: " + CtiNumStr(DSt->Message[4]) + "\n";

        resultString += "\n";

        resultString += getName() + " / Load Profile Channel " + CtiNumStr(lp_channel + 1) + " Status:\n";

        tmpTime = DSt->Message[5] << 24 |
                  DSt->Message[6] << 16 |
                  DSt->Message[7] <<  8 |
                  DSt->Message[8];

        lpTime = RWTime(tmpTime + rwEpoch);

        resultString += "Current Interval Time: " + lpTime.asString() + "\n";
        resultString += "Current Interval Pointer: " + CtiNumStr(DSt->Message[9]) + "\n";

        resultString += "\n";

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetConfigIntervals(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        RWCString resultString;

        resultString += getName() + " / Demand Interval: ";

        if( DSt->Message[0] )
        {
            resultString += CtiNumStr(DSt->Message[0]) + " minutes\n";
        }
        else
        {
            resultString += "disabled\n";
        }

        resultString += getName() + " / Load Profile Interval 1: " + CtiNumStr(DSt->Message[1]) + " minutes\n";
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval, (long)DSt->Message[1] * 60L);

        resultString += getName() + " / Load Profile Interval 2: " + CtiNumStr(DSt->Message[2]) + " minutes\n";
        // setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval, (long)DSt->Message[2] * 60L);  //  eventually?

        resultString += getName() + " / Table Read Interval: " + CtiNumStr(DSt->Message[3] * 15) + " seconds\n";

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetConfigChannelSetup(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        string result_string, dynamic_info;

        result_string += getName() + " / Load Profile Interval 1: " + CtiNumStr(DSt->Message[4]) + " minutes\n";
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval, (long)DSt->Message[4] * 60L);

        result_string += getName() + " / Load Profile Interval 2: " + CtiNumStr(DSt->Message[5]) + " minutes\n";
        //  setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval, DSt->Message[5] * 60);  //  someday?

        result_string += getName() + " / Electronic Meter Load Profile Interval: " + CtiNumStr(DSt->Message[6]) + " seconds\n";
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval, (long)DSt->Message[6]);

        for( int i = 0; i < MCT470_ChannelCount; i++ )
        {
            result_string += getName() + " / LP Channel " + CtiNumStr(i+1) + " config: ";

            /*
            Bits 0-1 - Type:    00=Channel Not Used
                                01 = Electronic Meter
                                10 = 2-wire KYZ (form A)
                                11 = 3-wire KYZ (form C)
            Bits 2-5 - Physical Channel / Attached Meter's Channel: 0000=1      0100=5      1000=9      1100=13
                                                                    0001=2      0101=6      1001=10     1101=14
                                                                    0010=3      0110=7      1010=11     1110=15
                                                                    0011=4      0111=8      1011=12     1111=16
            Bit 6 - Load Profile Interval #0 or #1 (0, 1)
            */

            if( DSt->Message[i] & 0x03 )
            {
                if     ( (DSt->Message[i] & 0x03) == 1 ) { result_string += "Electronic meter ";      dynamic_info += "1"; }
                else if( (DSt->Message[i] & 0x03) == 2 ) { result_string += "2-wire KYZ (form A) ";   dynamic_info += "2"; }
                else if( (DSt->Message[i] & 0x03) == 3 ) { result_string += "3-wire KYZ (form C) ";   dynamic_info += "3"; }

                result_string += "channel " + CtiNumStr((DSt->Message[i] >> 2) & 0x0f);
                dynamic_info  += CtiNumStr((DSt->Message[i] >> 2) & 0x0f).hex();

                result_string += " load profile interval #";
                result_string += (DSt->Message[i] & 0x40)?"1":"0";
                dynamic_info  += (DSt->Message[i] & 0x40)?"1":"0";

                result_string += "\n";
            }
            else
            {
                result_string += "Channel not used\n";
                dynamic_info  += "000";
            }
        }

        if( getMCTDebugLevel(MCTDebug_DynamicInfo) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - device \"" << getName() << "\" LP config decode - \"" << dynamic_info << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, dynamic_info);

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString(result_string.c_str());

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetConfigModel(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        RWCString sspec;
        RWCString options;
        int  ssp;
        char rev;
        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi


        ssp  = InMessage->Buffer.DSt.Message[0];
        ssp |= InMessage->Buffer.DSt.Message[4] << 8;

        rev  = InMessage->Buffer.DSt.Message[1];  //  1 = A, 2 = B, etc...  > 26 is undefined

        //  set the dynamic info for use later
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec,         (long)ssp);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, (long)rev);

        if( rev && rev <= 26 )
        {
            rev += 'A' - 1;

            sspec = "\nSoftware Specification " + CtiNumStr(ssp) + "  Rom Revision " + RWCString(rev) + "\n";
        }
        else
        {
            sspec = "\nSoftware Specification " + CtiNumStr(ssp) + "  Rom Revision " + CtiNumStr(rev) + " (development)\n";
        }

        options += "Connected Meter: ";
        switch( InMessage->Buffer.DSt.Message[3] & 0xf0 )
        {
            case 0x00:  options += "None\n";                break;
            case 0x10:  options += "Landis and Gyr S4\n";   break;
            case 0x20:  options += "Alpha A1\n";            break;
            case 0x30:  options += "Alpha Power Plus\n";    break;
            case 0x40:  options += "GE kV\n";               break;
            case 0x50:  options += "GE kV2\n";              break;
            default:    options += "Unknown (" + CtiNumStr((int)InMessage->Buffer.DSt.Message[3]).xhex().zpad(2) + ")\n";   break;
        }

        if( InMessage->Buffer.DSt.Message[3] & 0x04 )
        {
            options += "Multiple electronic meters attached\n";
        }

        options += "DST ";
        options += (InMessage->Buffer.DSt.Message[3] & 0x01)?"enabled\n":"disabled\n";

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString( sspec + options );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


void CtiDeviceMCT470::DecodeDatabaseReader(RWDBReader &rdr)
{
    INT iTemp;
    RWDBNullIndicator isNull;

    Inherited::DecodeDatabaseReader(rdr);

    _iedPort.DecodeDatabaseReader( rdr );

    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint - in CtiDeviceMCT31X::DecodeDatabaseReader for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Default data class: " << _iedPort.getDefaultDataClass() << endl;
        dout << "Default data offset: " << _iedPort.getDefaultDataOffset() << endl;
        dout << "Device ID: " << _iedPort.getDeviceID() << endl;
        dout << "IED Scan Rate: " << _iedPort.getIEDScanRate() << endl;
        dout << "IED Type: " << _iedPort.getIEDType() << endl;
        dout << "Password: " << _iedPort.getPassword() << endl;
        dout << "Real Time Scan Flag: " << _iedPort.getRealTimeScanFlag() << endl;
    }
}


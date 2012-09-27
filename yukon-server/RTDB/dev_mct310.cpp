#include "precompiled.h"

#include "devicetypes.h"
#include "dev_mct310.h"
#include "logger.h"
#include "pt_status.h"
#include "numstr.h"
#include "porter.h"
#include "utility.h"
#include "dllyukon.h"

using Cti::Protocols::EmetconProtocol;
using std::string;
using std::endl;
using std::list;

namespace Cti {
namespace Devices {

const Mct310Device::CommandSet Mct310Device::_commandStore = Mct310Device::initCommandStore();


#define STATUS1_BIT_MCT3XX    0x80
#define STATUS2_BIT_MCT3XX    0x40
#define STATUS3_BIT_MCT3XX    0x20
#define STATUS4_BIT_MCT3XX    0x10
#define STATUS5_BIT_MCT3XX    0x01
#define STATUS6_BIT_MCT3XX    0x02
#define STATUS7_BIT_MCT3XX    0x08
#define STATUS8_BIT_MCT3XX    0x04
#define STATUS1_BIT           0x40
#define STATUS2_BIT           0x80
#define STATUS3_BIT           0x02
#define STATUS4_BIT           0x04
#define OVERFLOW_BIT          0x01
#define L_PWRFAIL_BIT         0x02
#define S_PWRFAIL_BIT         0x04
#define OVERFLOW310_BIT       0x04
#define L_PWRFAIL310_BIT      0x08
#define S_PWRFAIL310_BIT      0x10
#define TAMPER_BIT            0x08





Mct310Device::Mct310Device( )
{
}

Mct310Device::Mct310Device( const Mct310Device &aRef )
{
    *this = aRef;
}

Mct310Device::~Mct310Device( )
{
}

Mct310Device& Mct310Device::operator=( const Mct310Device &aRef )
{
    if( this != &aRef )
    {
        Inherited::operator=( aRef );
    }

    return *this;
}


Mct310Device::CommandSet Mct310Device::initCommandStore( )
{
    CommandSet cs;

    cs.insert(CommandStore(EmetconProtocol::PutConfig_Raw, EmetconProtocol::IO_Write, 0, 0));  //  this will be filled in by executePutConfig

    //  300 series common commands
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Time,                 EmetconProtocol::IO_Read,           MCT3XX_TimePos,                 MCT3XX_TimeLen));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_DemandInterval,       EmetconProtocol::IO_Read,           MCT3XX_DemandIntervalPos,       MCT3XX_DemandIntervalLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_LoadProfileInterval,  EmetconProtocol::IO_Read,           MCT3XX_LPIntervalPos,           MCT3XX_LPIntervalLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Multiplier,           EmetconProtocol::IO_Read,           MCT3XX_Mult1Pos,                MCT3XX_MultLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Options,              EmetconProtocol::IO_Read,           MCT3XX_OptionPos,               MCT3XX_OptionLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_GroupAddress,         EmetconProtocol::IO_Read,           MCT3XX_GroupAddressPos,         MCT3XX_GroupAddressLen));

    cs.insert(CommandStore(EmetconProtocol::PutConfig_UniqueAddress,        EmetconProtocol::IO_Write,          MCT3XX_UniqueAddressPos,          MCT3XX_UniqueAddressLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_GroupAddress_GoldSilver, EmetconProtocol::IO_Write,       MCT3XX_GroupAddressGoldSilverPos, MCT3XX_GroupAddressGoldSilverLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_GroupAddress_Bronze,  EmetconProtocol::IO_Write,          MCT3XX_GroupAddressBronzePos,     MCT3XX_GroupAddressBronzeLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_GroupAddress_Lead,    EmetconProtocol::IO_Write,          MCT3XX_GroupAddressLeadPos,       MCT3XX_GroupAddressLeadLen));

    cs.insert(CommandStore(EmetconProtocol::PutConfig_DemandInterval,       EmetconProtocol::IO_Write,          MCT3XX_DemandIntervalPos,       MCT3XX_DemandIntervalLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_LoadProfileInterval,  EmetconProtocol::IO_Write,          Command_LPInt,                  0));

    cs.insert(CommandStore(EmetconProtocol::PutConfig_Multiplier,           EmetconProtocol::IO_Write,          MCT3XX_Mult1Pos,                MCT3XX_MultLen));

    cs.insert(CommandStore(EmetconProtocol::PutConfig_TSync,                EmetconProtocol::IO_Write,          Memory_TSyncPos,                Memory_TSyncLen));

    cs.insert(CommandStore(EmetconProtocol::PutConfig_OnOffPeak,            EmetconProtocol::IO_Write,          MCT3XX_MinMaxPeakConfigPos,     1));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_MinMax,               EmetconProtocol::IO_Write,          MCT3XX_MinMaxPeakConfigPos,     1));

    cs.insert(CommandStore(EmetconProtocol::Scan_Accum,                     EmetconProtocol::IO_Function_Read,  FuncRead_MReadPos,        FuncRead_MReadLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_KWH,                   EmetconProtocol::IO_Function_Read,  FuncRead_MReadPos,        FuncRead_MReadLen));

//  ACH add frozen support for 310
//  cs.insert(CommandStore(Emetcon::GetValue_FrozenKWH,             Emetcon::IO_Function_Read,  MCT3XX_FuncReadFrozenPos, MCT3XX_FuncReadFrozenLen);

    cs.insert(CommandStore(EmetconProtocol::GetStatus_Internal,             EmetconProtocol::IO_Read,           MCT3XX_GenStatPos,        MCT3XX_GenStatLen));

    cs.insert(CommandStore(EmetconProtocol::PutStatus_Reset,                EmetconProtocol::IO_Write,          MCT3XX_ResetPos,          MCT3XX_ResetLen));

    cs.insert(CommandStore(EmetconProtocol::GetValue_PFCount,               EmetconProtocol::IO_Read,           MCT3XX_PFCountPos,        MCT3XX_PFCountLen));
    cs.insert(CommandStore(EmetconProtocol::PutValue_ResetPFCount,          EmetconProtocol::IO_Write,          MCT3XX_PFCountPos,        MCT3XX_PFCountLen));

    cs.insert(CommandStore(EmetconProtocol::PutStatus_PeakOn,               EmetconProtocol::IO_Write,          Command_PeakOn,           0));
    cs.insert(CommandStore(EmetconProtocol::PutStatus_PeakOff,              EmetconProtocol::IO_Write,          Command_PeakOff,          0));

    cs.insert(CommandStore(EmetconProtocol::PutStatus_FreezeOne,            EmetconProtocol::IO_Write | Q_ARMS, Command_FreezeOne,        0));
    cs.insert(CommandStore(EmetconProtocol::PutStatus_FreezeTwo,            EmetconProtocol::IO_Write | Q_ARMS, Command_FreezeTwo,        0));

    //  only valid for sspec 1007 (and above?)
    cs.insert(CommandStore(EmetconProtocol::Scan_Integrity,                 EmetconProtocol::IO_Read,           Memory_DemandPos,         Memory_DemandLen));

    //  310 specific commands
    //  310 cannot do a FR0x92 (MCT31X_FuncReadDemand) and can only collect 1 demand reading!
    cs.insert(CommandStore(EmetconProtocol::Scan_Integrity,                 EmetconProtocol::IO_Read,           Memory_DemandPos,         Memory_DemandLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_Demand,                EmetconProtocol::IO_Read,           Memory_DemandPos,         Memory_DemandLen));

    cs.insert(CommandStore(EmetconProtocol::PutValue_KYZ,                   EmetconProtocol::IO_Write,          MCT3XX_PutMRead1Pos,      MCT3XX_PutMReadLen));

    //  only valid for 310IL, this case handled in getOperation
    cs.insert(CommandStore(EmetconProtocol::Scan_LoadProfile,               EmetconProtocol::IO_Function_Read,  0,  0));

    cs.insert(CommandStore(EmetconProtocol::GetStatus_Disconnect,           EmetconProtocol::IO_Read,           Memory_StatusPos,         Memory_StatusLen));
    cs.insert(CommandStore(EmetconProtocol::GetStatus_LoadProfile,          EmetconProtocol::IO_Read,           MCT3XX_LPStatusPos,       MCT3XX_LPStatusLen));

    cs.insert(CommandStore(EmetconProtocol::Control_Latch,                  EmetconProtocol::IO_Write | Q_ARML, Command_Latch,            0));

    return cs;
}


bool Mct310Device::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
    //  the 310IL/IDL is the only 310 that supports load profile, and i didn't want to add a seperate class for the one action
    if( cmd == EmetconProtocol::Scan_LoadProfile &&
        getType( ) != TYPEMCT310IL  &&
        getType( ) != TYPEMCT310IDL )
    {
        return false;
    }

    if( getOperationFromStore(_commandStore, cmd, bst) )
    {
        return true;
    }

    return Inherited::getOperation(cmd, bst);
}


ULONG Mct310Device::calcNextLPScanTime( void )
{
    CtiTime Now, blockStart, nextTime;
    unsigned long midnightOffset;
    int lpBlockSize, lpDemandRate, lpMaxBlocks;

    CtiPointSPtr pPoint = getDevicePointOffsetTypeEqual(1 + PointOffset_LoadProfileOffset, DemandAccumulatorPointType);

    //  make sure to completely recalculate this every time
    _nextLPScanTime = YUKONEOT;

    if( !_lpIntervalSent )
    {
        //  send load profile interval on the next 5 minute boundary
        _nextLPScanTime = Now.seconds() + 300;

        _nextLPScanTime -= _nextLPScanTime % 300;
    }
    else if( pPoint && getLoadProfile()->isChannelValid(0) )
    {
        //  we read 6 intervals at a time
        lpDemandRate = getLoadProfile()->getLoadProfileDemandRate();
        lpBlockSize  = lpDemandRate * 6;

        //  if we collect hour data, we only keep a day;  anything less, we keep 48 intervals
        if( lpDemandRate == 3600 )
        {
            lpMaxBlocks = 4;
        }
        else
        {
            lpMaxBlocks = 8;
        }

        blockStart   = getLastLPTime();

        if( blockStart < (Now - ((lpMaxBlocks - 1) * lpBlockSize)) )
        {
            blockStart = Now - ((lpMaxBlocks - 1) * lpBlockSize);
        }

        //  figure out seconds from midnight
        midnightOffset  = blockStart.hour() * 3600;
        midnightOffset += blockStart.minute() * 60;
        midnightOffset += blockStart.second();

        //  make sure we're actually at the beginning of a block
        blockStart -= midnightOffset % lpBlockSize;

        //  we can only request in blocks, so we plan to request LP data
        //    after one block (6 intervals) has passed
        nextTime  = blockStart + lpBlockSize;
        //  also add on time for it to move out of the memory we're requesting
        nextTime += LPBlockEvacuationTime;

        //  if we're overdue
        while( (nextTime <= (Now - LoadProfileCollectionWindow)) ||
               (nextTime <= _lastLPRequest) )
        {
            nextTime += getLPRetryRate(lpDemandRate);
        }

        _nextLPScanTime = nextTime.seconds();
    }

    return _nextLPScanTime;
}


INT Mct310Device::calcAndInsertLPRequests(OUTMESS *&OutMessage, list< OUTMESS* > &outList)
{
    int nRet = NoError;

    int            lpDemandRate;
    unsigned int   lpBlockAddress;
    unsigned long  lpBlocksToCollect,
                   lpMaxBlocks,
                   lpBlockSize,
                   lpMidnightOffset;
    CtiTime         lpBlockStartTime;
    CtiTime         Now;
    OUTMESS       *tmpOutMess;
    string      lpDescriptorString;
    int            lpChannel;

    lpDemandRate     = getLoadProfile()->getLoadProfileDemandRate();

    if( !_lpIntervalSent )
    {
        sendLPInterval( OutMessage, outList );
    }
    else
    {
        //  we can read 6 intervals at a time
        lpBlockSize = lpDemandRate * 6;

        //  if we collect hour data, we only keep a day;  anything less, we keep 48 intervals
        if( lpDemandRate == 3600 )
            lpMaxBlocks = 4;
        else
            lpMaxBlocks = 8;

        if( useScanFlags() )
        {
            if( _nextLPScanTime <= Now )
            {
                tmpOutMess = CTIDBG_new OUTMESS(*OutMessage);

                lpBlockStartTime = getLastLPTime();

                lpBlocksToCollect = (Now.seconds() - lpBlockStartTime.seconds()) / lpBlockSize;

                //  make sure we only ask for what remains in memory
                if( lpBlocksToCollect >= lpMaxBlocks )
                {
                    //  start with everything but the current block
                    lpBlockStartTime  = Now.seconds();
                    lpBlockStartTime -= (lpMaxBlocks - 1) * lpBlockSize;
                }

                //  figure out seconds from midnight
                lpMidnightOffset  = lpBlockStartTime.hour() * 3600;
                lpMidnightOffset += lpBlockStartTime.minute() * 60;
                lpMidnightOffset += lpBlockStartTime.second();

                //  make sure our reported "start time" is at the beginning of a block
                lpMidnightOffset -= lpMidnightOffset % lpBlockSize;

                //  which block to grab?
                lpBlockAddress  = lpMidnightOffset / lpBlockSize;
                //  adjust for wraparound
                lpBlockAddress %= lpMaxBlocks;

                lpDescriptorString = string(" block ") + CtiNumStr(lpBlockAddress+1);

                strncat( tmpOutMess->Request.CommandStr,
                         lpDescriptorString.c_str(),
                         sizeof(tmpOutMess->Request.CommandStr) - strlen(tmpOutMess->Request.CommandStr));

                if( isDebugLudicrous() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - command string check for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "\"" << tmpOutMess->Request.CommandStr << "\"" << endl;
                }

                outList.push_back(tmpOutMess);
                _lastLPRequest = Now;
            }
            else
            {
                if( isDebugLudicrous() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - LP scan too early for device \"" << getName() << "\", aborted **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


bool Mct310Device::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
{
    bool retVal = false;
    int lpBlockAddress;

    if( getMCTDebugLevel(DebugLevel_LoadProfile) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "parse.getiValue(\"scan_loadprofile_block\",   0) = " << parse.getiValue("scan_loadprofile_block", 0) << endl;
    }

    if( lpBlockAddress = parse.getiValue("scan_loadprofile_block", 0) )
    {
        lpBlockAddress--;  //  adjust to be a zero-based offset

        lpBlockAddress += 0x50;

        OutMessage->Buffer.BSt.Function = lpBlockAddress;
        OutMessage->Buffer.BSt.Length   = 13;  //  2 bytes per interval, and a status byte to boot
        OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;

        retVal = true;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Improperly formed LP request discarded for \"" << getName() << "\"." << endl;
        }

        retVal = false;
    }

    return retVal;
}


DOUBLE Mct310Device::translateStatusValue (INT PointOffset, INT PointType, INT DeviceType, PBYTE DataValueArray)
{
    /* key off the point offset */
    switch(PointOffset)
    {
        case 1:
        {
            if(DataValueArray[0] & STATUS1_BIT_MCT3XX)
            {
               return((DOUBLE) CLOSED);
            }
            else
            {
               return((DOUBLE) OPENED);
            }

            break;
        }

        case 2:
        {
            if(DataValueArray[0] & STATUS2_BIT_MCT3XX)
            {
                return((DOUBLE) CLOSED);
            }
            else
            {
                return((DOUBLE) OPENED);
            }

            break;
        }

        case 3:
        {
            if(DataValueArray[0] & STATUS3_BIT_MCT3XX)
            {
                return((DOUBLE) CLOSED);
            }
            else
            {
                return((DOUBLE) OPENED);
            }

            break;
        }

        case 4:
        {
            if(DataValueArray[0] & STATUS4_BIT_MCT3XX)
            {
                return((DOUBLE) CLOSED);
            }
            else
            {
                return((DOUBLE) OPENED);
            }

            break;
        }

        case 5:
        {
            if(DataValueArray[0] & STATUS5_BIT_MCT3XX)
            {
                return((DOUBLE) CLOSED);
            }
            else
            {
                return((DOUBLE) OPENED);
            }

            break;
        }

        case 6:
        {
            if(DataValueArray[0] & STATUS6_BIT_MCT3XX)
            {
                return((DOUBLE) CLOSED);
            }
            else
            {
                return((DOUBLE) OPENED);
            }

            break;
        }

        case 7:
        {
            if(DataValueArray[0] & STATUS7_BIT_MCT3XX)
            {
                return((DOUBLE) CLOSED);
            }
            else
            {
                return((DOUBLE) OPENED);
            }

            break;
        }

        case 8:
        {
            if(DataValueArray[0] & STATUS8_BIT_MCT3XX)
            {
                return((DOUBLE) CLOSED);
            }
            else
            {
                return((DOUBLE) OPENED);
            }

            break;
        }

        case 11:
        {
            /* Short Power Fail Flag */
            if(DeviceType == TYPEMCT310)
            {
                /* special bit for mct310 */
                if(DataValueArray[4] & S_PWRFAIL310_BIT)
                {
                   return((DOUBLE) CLOSED);
                }

                return((DOUBLE) OPENED);
            }
            else
            {
                if(DataValueArray[4] & S_PWRFAIL_BIT)
                {
                    return((DOUBLE) CLOSED);
                }

                return((DOUBLE) OPENED);
            }

            break;
        }

        case 12:
        {
            /* reading overflow Flag */
            if(DeviceType == TYPEMCT310)
            {
                /* special bit for mct310 */
                if(DataValueArray[4] & OVERFLOW310_BIT)
                {
                    return((DOUBLE) CLOSED);
                }

                return((DOUBLE) OPENED);
            }
            else
            {
                if(DataValueArray[4] & OVERFLOW_BIT)
                {
                    return((DOUBLE) CLOSED);
                }

                return((DOUBLE) OPENED);
            }

            break;
        }

        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        return((DOUBLE) INVALID);
    }

    //  We shouldn't even ever get here...
    return(NORMAL);
}


/*
 *  ModelDecode MUST decode all CtiDLCCommand_t which are defined in the initCommandStore object.  The only exception to this
 *  would be a child whose decode was identical to the parent, but whose request was done differently..
 *  This MAY be the case for example in an IED scan.
 */
INT Mct310Device::ModelDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    switch(InMessage->Sequence)
    {
        case (EmetconProtocol::Scan_Accum):
        case (EmetconProtocol::GetValue_KWH):
        {
            status = decodeGetValueKWH(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::Scan_Integrity):
        case (EmetconProtocol::GetValue_Demand):
        {
            status = decodeGetValueDemand(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::Scan_LoadProfile):
        {
            status = decodeScanLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::GetValue_PeakDemand):
        case (EmetconProtocol::GetValue_FrozenPeakDemand):
        {
            status = decodeGetValuePeak(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::PutConfig_OnOffPeak):
        case (EmetconProtocol::PutConfig_MinMax):
        {
            status = decodePutConfigPeakMode(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::PutStatus_FreezeOne):
        case (EmetconProtocol::PutStatus_FreezeTwo):
        {
            status = decodePutStatus(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::GetStatus_Disconnect):
        {
            status = decodeGetStatusDisconnect(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::GetStatus_Internal):
        {
            status = decodeGetStatusInternal(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::GetStatus_LoadProfile):
        {
            status = decodeGetStatusLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::GetConfig_Model):
        {
            status = decodeGetConfigModel(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::GetConfig_Options):
        {
            status = decodeGetConfigOptions(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        default:
        {
            status = Inherited::ModelDecode(InMessage, TimeNow, vgList, retList, outList);

            if(status != NORMAL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " IM->Sequence = " << InMessage->Sequence << " " << getName() << endl;
            }
            break;
        }
    }


    return status;
}


INT Mct310Device::decodePutConfigPeakMode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;
    ULONG i,x;
    string resultString;

    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    /* this means we are getting NON-demand accumulator points */
    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

    if( InMessage->Sequence == EmetconProtocol::PutConfig_MinMax )
    {
        resultString = getName() + " / peak mode set to \"minmax\"";
    }
    else
    {
        resultString = getName() + " / peak mode set to \"on/off peak\"";
    }

    resultString += " - also, wire config and cold load options reset to default;  use DB MCT Config and \"putconfig emetcon install\" instead to avoid this";

    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


INT Mct310Device::decodeGetValueKWH(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;
    ULONG i,x;
    INT pid;
    string resultString;

    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    DOUBLE Value;
    CtiPointSPtr         pPoint;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Accumulator Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    ULONG RecentValue = 0;
    USHORT TempDevType;

    if( InMessage->Sequence == EmetconProtocol::Scan_Accum )
    {
        setScanFlag(ScanRateAccum, false);
    }

    /* this means we are getting NON-demand accumulator points */
    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

    pPoint = getDevicePointOffsetTypeEqual( 1, PulseAccumulatorPointType );

    RecentValue = MAKEULONG(MAKEUSHORT (DSt->Message[2], DSt->Message[1]), (USHORT)(DSt->Message[0]));

    // handle accumulator data here
    if( pPoint)
    {
        // 24 bit pulse value
        Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( RecentValue );
        CtiTime pointTime;

        if( getType() == TYPEMCT310ID ||
            getType() == TYPEMCT310IDL ||
            getType() == TYPEMCT310IL )
        {
            while( Value > MCT310_Rollover )
            {
                Value -= MCT310_Rollover;
            }
        }

        resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                 boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());
        pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, PulseAccumulatorPointType, resultString, TAG_POINT_MUST_ARCHIVE);

        if(pData != NULL)
        {
            pointTime -= pointTime.seconds() % 300;
            pData->setTime( pointTime );
            ReturnMsg->PointData().push_back(pData);
            pData = NULL;  // We just put it on the list...
        }
    }
    else
    {
        if( getType() == TYPEMCT310ID ||
            getType() == TYPEMCT310IDL ||
            getType() == TYPEMCT310IL )
        {
            while( RecentValue > MCT310_Rollover )
            {
                RecentValue -= MCT310_Rollover;
            }
        }

        resultString = getName() + " / Meter Reading = " + CtiNumStr(RecentValue) + "  --  POINT UNDEFINED IN DB";
        ReturnMsg->setResultString(resultString);
    }

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );


    return status;
}


INT Mct310Device::decodeGetValueDemand(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int       status = NORMAL, demand_interval;
    double    Value;
    string resultString;
    unsigned long pulses;
    PointQuality_t quality;
    bool bad_data;

    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiPointSPtr         pPoint;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    setScanFlag(ScanRateIntegrity, false);
    setScanFlag(ScanRateGeneral, false);

    /* this means we are getting NON-demand accumulator points */
    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

    pulses = MAKEUSHORT(DSt->Message[1], DSt->Message[0]);

    demand_interval = getDemandInterval();

    // look for first defined DEMAND accumulator
    pPoint = getDevicePointOffsetTypeEqual( 1, DemandAccumulatorPointType );

    if( checkDemandQuality( pulses, quality, bad_data ) )
    {
        Value = 0.0;
    }
    else
    {
        //  if no fatal problems with the quality,
        //    adjust for the demand interval
        Value = pulses * (3600 / demand_interval);

        if( pPoint )
        {
            //    and the UOM
            Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(Value);
        }
    }

    if( pPoint)
    {
        CtiTime pointTime;

        resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                 boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

        pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, DemandAccumulatorPointType, resultString);
        if(pData != NULL)
        {
            pointTime -= pointTime.seconds() % getDemandInterval();
            pData->setTime( pointTime );
            ReturnMsg->PointData().push_back(pData);
            pData = NULL;  // We just put it on the list...
        }
    }
    else
    {
        resultString = getName() + " / Demand = " + CtiNumStr(Value) + "  --  POINT UNDEFINED IN DB";
        ReturnMsg->setResultString(resultString);
    }

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


INT Mct310Device::decodeGetValuePeak(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int       status = NORMAL;
    double    Value;
    string resultString;

    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiPointSPtr         pPoint;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    if( getMCTDebugLevel(DebugLevel_Info) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Min/Max On/Off-Peak Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    setScanFlag(ScanRateGeneral, false);

    /* this means we are getting NON-demand accumulator points */
    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

    for( int i = 0; i < 2; i++ )
    {
        //  ACH:  check error bits
        Value = MAKEUSHORT(DSt->Message[i*2+1], (DSt->Message[i*2+0] & 0x3f) );

        //  turn raw pulses into a demand reading
        Value *= DOUBLE(3600 / getDemandInterval());

        // look for the appropriate point
        pPoint = getDevicePointOffsetTypeEqual( 10 + i, DemandAccumulatorPointType );

        if( pPoint )
        {
            CtiTime pointTime;

            Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(Value);

            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                     boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

            if( InMessage->Sequence == EmetconProtocol::GetValue_FrozenPeakDemand )
            {
                pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, DemandAccumulatorPointType, resultString);
                if(pData != NULL)
                {
                    pointTime -= pointTime.seconds() % getDemandInterval();
                    pData->setTime( pointTime );
                    ReturnMsg->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else
            {
                resultString = ReturnMsg->ResultString() + resultString + "\n";
                ReturnMsg->setResultString(resultString);
            }
        }
        else
        {
            resultString += getName() + " / demand accumulator offset " + CtiNumStr(10+i) + " = " + CtiNumStr(Value) + "  --  point undefined in DB\n";
            ReturnMsg->setResultString(resultString);
        }
    }

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


INT Mct310Device::decodeScanLoadProfile(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int status = NORMAL;

    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    string val_report, result_string;

    int     demand_rate, block_size, max_blocks;
    int     current_block_num, retrieved_block_num, midnight_offset;
    bool    bad_data = false;
    double  value;
    unsigned long pulses, timestamp, current_block_start, retrieved_block_start;
    PointQuality_t quality;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    CtiPointNumericSPtr point;
    CtiReturnMsg    *return_msg = 0;  // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *point_data = 0;

    if( getMCTDebugLevel(DebugLevel_Scanrates | DebugLevel_LoadProfile) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Load Profile Scan Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if((return_msg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    return_msg->setUserMessageId(InMessage->Return.UserID);

    if( (retrieved_block_num = parse.getiValue("scan_loadprofile_block",   0)) )
    {
        if( isDebugLudicrous() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - retrieved_block_num " << retrieved_block_num << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retrieved_block_num--;

        demand_rate = getLoadProfile()->getLoadProfileDemandRate();
        block_size  = demand_rate * 6;

        //  if we collect hour data, we only keep a day;  anything less, we keep 48 intervals
        if( demand_rate == 3600 )
        {
            max_blocks = 4;
        }
        else
        {
            max_blocks = 8;
        }

        point = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual( 1 + PointOffset_LoadProfileOffset, DemandAccumulatorPointType ));

        if( point )
        {
            //  figure out current seconds from midnight
            midnight_offset  = TimeNow.hour() * 3600;
            midnight_offset += TimeNow.minute() * 60;
            midnight_offset += TimeNow.second();

            //  make sure the alignment is correct
            current_block_start  = TimeNow.seconds();
            current_block_start -= midnight_offset % block_size;
            midnight_offset     -= midnight_offset % block_size;

            current_block_num    = midnight_offset / block_size;
            current_block_num   %= max_blocks;

            //  work backwards to find the retrieved block
            retrieved_block_start  = current_block_start;
            retrieved_block_start -= ((current_block_num + max_blocks - retrieved_block_num) % max_blocks) * block_size;

            if( current_block_num == retrieved_block_num )
            {
                if( getMCTDebugLevel(DebugLevel_LoadProfile) )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - attempt to decode current load profile block for \"" << getName() << "\" - aborting decode **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << InMessage->Return.CommandStr << endl;
                }

                result_string = "Attempt to decode current load profile block for \"" + getName() + "\" - aborting decode ";
            }
            else if( retrieved_block_start < getLastLPTime() )
            {
                if( getMCTDebugLevel(DebugLevel_LoadProfile) )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - load profile debug for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << InMessage->Return.CommandStr << endl;
                    dout << "retrieved_block_num = " << retrieved_block_num << endl;
                    dout << "retrieved_block_start = " << retrieved_block_start << endl;
                    dout << "lastLPTime = " << getLastLPTime()  << endl;
                }

                result_string  = "Block < lastLPTime for device \"" + getName() + "\" - aborting decode";
            }
            else
            {
                for( int interval_offset = 0; interval_offset < 6; interval_offset++ )
                {
                    //  error code in the top 5 bits - parsed by checkLoadProfileQuality
                    pulses   = DSt->Message[interval_offset*2 + 1];
                    pulses <<= 8;
                    pulses  |= DSt->Message[interval_offset*2 + 2];

                    if( bad_data )  //  load survey was halted - the rest of the data is bad
                    {
                        quality = DeviceFillerQuality;
                        value = 0.0;
                    }
                    else if( checkDemandQuality( pulses, quality, bad_data ) )
                    {
                        value = 0.0;
                    }
                    else
                    {
                        //  if no fatal problems with the quality,
                        //    adjust for the demand interval
                        value = pulses * (3600 / demand_rate);
                        //    and the UOM
                        value = point->computeValueForUOM(value);
                    }

                    point_data = CTIDBG_new CtiPointDataMsg(point->getPointID(),
                                                            value,
                                                            quality,
                                                            DemandAccumulatorPointType,
                                                            "",
                                                            TAG_POINT_LOAD_PROFILE_DATA );

                    //  this is where the block started...
                    timestamp  = retrieved_block_start + (demand_rate * interval_offset);

                    //  but we want interval *ending* times, so add on one more interval
                    timestamp += demand_rate;

                    if( isDebugLudicrous() )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - load profile debug for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "value = " << value << endl;
                        dout << "interval_offset = " << interval_offset << endl;
                        dout << "timestamp = " << CtiTime(timestamp) << endl;
                    }

                    point_data->setTime(timestamp);

                    return_msg->insert(point_data);
                }

                setLastLPTime(timestamp);
            }
        }
        else
        {
            result_string = "No load profile point defined for '" + getName() + "'";
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - scan_loadprofile tokens not found in command string \"" << InMessage->Return.CommandStr << "\" - cannot proceed with decode, aborting **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        result_string  = "scan_loadprofile tokens not found in command string \"";
        result_string += InMessage->Return.CommandStr;
        result_string += "\" - cannot proceed with decode, aborting";
    }

    return_msg->setResultString(result_string);

    retMsgHandler(InMessage->Return.CommandStr, status, return_msg, vgList, retList);

    return status;
}


void Mct310Device::decodeAccumulators(ULONG result[], INT accum_cnt, BYTE *Data)
{
    int i, j, maxj;

    for(i = 0; i < accum_cnt && i < 3; i++)
    {
        result[i] = 0;
        maxj = (i * 3) + 3;

        for(j = i * 3; j < maxj; j++)
        {
            result[i] = (result[i] << 8) + Data[j];
        }

        if(result[i] > MCT310_MaxPulseCount)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - result[i] > MCT_MaxPulseCount **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
}


INT Mct310Device::decodeGetStatusInternal( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NORMAL;

    unsigned char *geneBuf = InMessage->Buffer.DSt.Message;

    string resultString;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;
    CtiPointStatusSPtr   point;

    int powerfailStatus;

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

    resultString  = getName() + " / Internal Status:\n";

    if( geneBuf[5] )        resultString += "  Active\n";
    else                    resultString += "  Halted\n";

    if( geneBuf[0] & 0x04 ) resultString += "  Metering Overflow\n";
    if( geneBuf[0] & 0x08 ) resultString += "  Powerfail flag set\n";
    if( geneBuf[0] & 0x10 ) resultString += "  Reset Flag set\n";
    if( geneBuf[0] & 0x20 ) resultString += "  Self Test Failed\n";

    if( geneBuf[1] & 0x80 ) resultString += "  UADD fail - NovRam\n";
    if( geneBuf[1] & 0x40 ) resultString += "  UADD fail - Ram\n";
    if( geneBuf[1] & 0x20 ) resultString += "  SPI interrupt flag\n";
    if( geneBuf[1] & 0x10 ) resultString += "  SCI interrupt flag\n";
    if( geneBuf[1] & 0x08 ) resultString += "  SWI interrupt flag\n";
    if( geneBuf[1] & 0x02 ) resultString += "  Watchdog Reset flag\n";

    if( !(geneBuf[0] | geneBuf[1] | geneBuf[5]) )
        resultString += "  Normal Operating Mode\n";

    if( geneBuf[0] & 0x08 )
    {
        powerfailStatus = 1;
    }
    else
    {
        powerfailStatus = 0;
    }

    if( point = boost::static_pointer_cast<CtiPointStatus>(getDevicePointOffsetTypeEqual( PointOffset_Status_Powerfail, StatusPointType ) ))
    {
        string pointResult;

        pointResult = getName() + " / " + point->getName() + ": " + ResolveStateName(point->getStateGroupID(), powerfailStatus);

        pData = CTIDBG_new CtiPointDataMsg(point->getPointID(), powerfailStatus, NormalQuality, StatusPointType, pointResult);

        ReturnMsg->PointData().push_back(pData);
    }


    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


INT Mct310Device::decodeGetStatusLoadProfile( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NORMAL;

    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    INT   j;
    ULONG mread = 0;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

    string resultString;
    double Value;

    resultString  = getName() + " / Load Survey Control Parameters:\n";
    resultString += "Demand Interval   : " + CtiNumStr((int)(DSt->Message[6] * 5)) + string("\n");
    resultString += "Current Interval  : " + CtiNumStr((int)((DSt->Message[8] / 2) + 1)) + string("\n");
    resultString += "PI 1 Current Value: " + CtiNumStr((int)((DSt->Message[0] << 8) + DSt->Message[1])) + string("\n");
    resultString += "PI 2 Current Value: " + CtiNumStr((int)((DSt->Message[2] << 8) + DSt->Message[3])) + string("\n");
    resultString += "PI 3 Current Value: " + CtiNumStr((int)((DSt->Message[4] << 8) + DSt->Message[5])) + string("\n");

    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


INT Mct310Device::decodeGetConfigModel(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    INT ssp;
    char rev;
    char temp[80];

    string sspec;
    string options("Options:\n");

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi


    ssp = InMessage->Buffer.DSt.Message[4] * 256 + InMessage->Buffer.DSt.Message[0];
    rev = 64 + InMessage->Buffer.DSt.Message[1];

    sspec = "\nSoftware Specification " + CtiNumStr(ssp) + string("  Rom Revision ") + rev + "\n";

    if(InMessage->Buffer.DSt.Message[2] & 0x01)
    {
        options += "  Metering channel #1 available\n";
    }
    if(InMessage->Buffer.DSt.Message[2] & 0x02)
    {
        options += "  Metering channel #2 available\n";
    }
    if(InMessage->Buffer.DSt.Message[2] & 0x04)
    {
        options += "  Metering channel #3 available\n";
    }
    if(InMessage->Buffer.DSt.Message[2] & 0x08)
    {
        options += "  Feedback load control available\n";
    }
    if(InMessage->Buffer.DSt.Message[2] & 0x10)
    {
        options += "  4-state latch relays available\n";
    }
    if(InMessage->Buffer.DSt.Message[2] & 0x20)
    {
        options += "  Capacitor control available\n";
    }
    if(InMessage->Buffer.DSt.Message[2] & 0x40)
    {
        options += "  Service disconnect available\n";
    }
    if(InMessage->Buffer.DSt.Message[2] & 0x80)
    {
        options += "  Timed relays available\n";
    }

    if( InMessage->Buffer.DSt.Message[3] & 0x08 )
    {
        options += "  Pulse input 1: 3-wire\n";
    }
    else
    {
        options += "  Pulse input 1: 2-wire\n";
    }

    if( getType() == TYPEMCT318 || getType() == TYPEMCT318L || getType() == TYPEMCT360 || getType() == TYPEMCT370 )
    {
        if( InMessage->Buffer.DSt.Message[3] & 0x10 )
        {
            options += "  Pulse input 2: 3-wire\n";
        }
        else
        {
            options += "  Pulse input 2: 2-wire\n";
        }
        if( InMessage->Buffer.DSt.Message[3] & 0x20 )
        {
            options += "  Pulse input 3: 3-wire\n";
        }
        else
        {
            options += "  Pulse input 3: 2-wire\n";
        }

        if( InMessage->Buffer.DSt.Message[3] & 0x04 )
        {
            options += "  Peak mode:  Min/Max\n";
        }
        else
        {
            options += "  Peak mode:  On/Off Peak\n";
        }
    }

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);
    ReturnMsg->setResultString( sspec + options );

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


INT Mct310Device::decodeGetConfigOptions(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn = InMessage->EventCode & 0x3fff;
    unsigned char *optBuf  = InMessage->Buffer.DSt.Message;

    string options;

    options  = "Device: \"" + getName() + "\" Configuration:\n";

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    //  310s don't use this bit
    if( getType() != TYPEMCT310 && getType() != TYPEMCT310ID && getType() != TYPEMCT310IL && getType() != TYPEMCT310IDL )
    {
        if( optBuf[1] & 0x04 )
            options += "  In on/off peak mode\n";
        else
            options += "  In min/max mode\n";
    }

    if( optBuf[3] & 0x01 && optBuf[3] & 0x02 )  //  00
        options += "  Address restrict flag invalid\n";
    else if( optBuf[3] & 0x01 )                 //  01
        options += "  No Group Addressing\n";
    else if( optBuf[3] & 0x02 )                 //  10
        options += "  FCT Addressing Only\n";
    else                                        //  11
        options += "  Normal Addressing\n";

    if( optBuf[1] & 0x40 )
        options += "  Latch relay requires ARM\n";

    if( optBuf[1] & 0x80 )
        options += "  Latch relay by Unique Only\n";

    if( optBuf[0] & 0x80 )
    {
        if( !(optBuf[1] & 0x01) )
            options += "  No\n";
        options += "  Cold Load Pick-up Load A\n";

        if( !(optBuf[1] & 0x02) )
            options += "  No\n";
        options += "  Cold Load Pick-up Load B\n";
    }

    if( optBuf[0] & 0x01 )
    {
        options += "  Metering input #1:";
        if( optBuf[1] & 0x08 )
            options += " 3-wire\n";
        else
            options += " 2-wire\n";
    }

    if( optBuf[0] & 0x02 )
    {
        options += "  Metering input #2:";
        if( optBuf[1] & 0x10 )
            options += " 3-wire\n";
        else
            options += " 2-wire\n";
    }

    if( optBuf[0] & 0x04 )
    {
        options += "  Metering input #3:";
        if( optBuf[1] & 0x20 )
            options += " 3-wire\n";
        else
            options += " 2-wire\n";
    }

    ReturnMsg->setUserMessageId( InMessage->Return.UserID );
    ReturnMsg->setResultString( options );

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );


    return status;
}

}
}


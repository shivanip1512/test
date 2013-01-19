#include "precompiled.h"

#include "devicetypes.h"
#include "dev_mct22X.h"
#include "logger.h"
#include "porter.h"
#include "pt_numeric.h"
#include "numstr.h"

using std::string;
using std::endl;
using std::list;

using namespace Cti::Protocols;

namespace Cti {
namespace Devices {

const Mct22xDevice::CommandSet Mct22xDevice::_commandStore = Mct22xDevice::initCommandStore();

Mct22xDevice::Mct22xDevice() {}

Mct22xDevice::Mct22xDevice(const Mct22xDevice &aRef)
{
   *this = aRef;
}

Mct22xDevice::~Mct22xDevice() {}

Mct22xDevice& Mct22xDevice::operator=(const Mct22xDevice &aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
   }
   return *this;
}


Mct22xDevice::CommandSet Mct22xDevice::initCommandStore()
{
   CommandSet cs;

   cs.insert(CommandStore(EmetconProtocol::GetConfig_GroupAddress,            EmetconProtocol::IO_Read,  MCT2XX_GroupAddressPos,            MCT2XX_GroupAddressLen));
   cs.insert(CommandStore(EmetconProtocol::PutConfig_GroupAddress_GoldSilver, EmetconProtocol::IO_Write, MCT2XX_GroupAddressGoldSilverPos,  MCT2XX_GroupAddressGoldSilverLen));
   cs.insert(CommandStore(EmetconProtocol::PutConfig_GroupAddress_Bronze,     EmetconProtocol::IO_Write, MCT2XX_GroupAddressBronzePos,      MCT2XX_GroupAddressBronzeLen));
   cs.insert(CommandStore(EmetconProtocol::PutConfig_GroupAddress_Lead,       EmetconProtocol::IO_Write, MCT2XX_GroupAddressLeadPos,        MCT2XX_GroupAddressLeadLen));

   cs.insert(CommandStore(EmetconProtocol::GetValue_KWH,        EmetconProtocol::IO_Read,   MCT22X_MReadPos,    MCT22X_MReadLen));
   cs.insert(CommandStore(EmetconProtocol::Scan_Accum,          EmetconProtocol::IO_Read,   MCT22X_MReadPos,    MCT22X_MReadLen));

   //  this meter requires you to subtract the current and previous meter readings to get a 5-minute demand value
   cs.insert(CommandStore(EmetconProtocol::GetValue_Demand,     EmetconProtocol::IO_Read,   MCT22X_DemandPos,   MCT22X_DemandLen));
   cs.insert(CommandStore(EmetconProtocol::Scan_Integrity,      EmetconProtocol::IO_Read,   MCT22X_DemandPos,   MCT22X_DemandLen));

   return cs;
}


bool Mct22xDevice::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
    if( getOperationFromStore(_commandStore, cmd, bst) )
    {
        if( bst.IO == EmetconProtocol::IO_Write )
        {
            bst.IO |= Q_ARMC;
        }

        return true;
    }

    return Inherited::getOperation(cmd, bst);
}

/*
 *  ModelDecode MUST decode all CtiDLCCommand_t which are defined in the initCommandStore object.  The only exception to this
 *  would be a child whose decode was identical to the parent, but whose request was done differently..
 *  This MAY be the case for example in an IED scan.
 */
INT Mct22xDevice::ModelDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    switch(InMessage->Sequence)
    {
        case (EmetconProtocol::GetValue_Demand):
        case (EmetconProtocol::Scan_Integrity):
        {
            status = decodeGetValueDemand(InMessage, TimeNow, vgList, retList, outList);
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



INT Mct22xDevice::decodeGetValueDemand(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    setScanFlag(ScanRateIntegrity, false);    //    resetScanFlag(ScanPending);

    INT    j;
    ULONG  curead, prevrd;
    double Value;
    string resultString;

    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData     = NULL;
    CtiPointSPtr    pPoint;

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

    curead = (DSt->Message[3] << 16) |
             (DSt->Message[4] <<  8) |
              DSt->Message[5];
    prevrd = (DSt->Message[0] << 16) |
             (DSt->Message[1] <<  8) |
              DSt->Message[2];

    if( curead < prevrd )
    {
        //  account for rollover
        curead += 10000000;
    }

    //  figure out the difference between current and previous readings
    Value  = curead - prevrd;
    //  turn raw pulses into a demand reading
    Value *= 12;

    //  look for first defined DEMAND accumulator
    pPoint = getDevicePointOffsetTypeEqual( 1, DemandAccumulatorPointType );

    if(pPoint)
    {
        CtiTime pointTime;

        Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(Value);

        resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                 boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

        pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, DemandAccumulatorPointType, resultString);
        //  correct to beginning of interval

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
        resultString = getName() + " / Demand = " + CtiNumStr((int)Value) + "  --  POINT UNDEFINED IN DB";
        ReturnMsg->setResultString(resultString);
    }

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

   return status;
}

}
}

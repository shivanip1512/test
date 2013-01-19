#include "precompiled.h"

#include "devicetypes.h"
#include "dev_repeater800.h"
#include "logger.h"
#include "pt_numeric.h"
#include "porter.h"
#include "utility.h"
#include "numstr.h"

using namespace Cti::Protocols;
using std::string;
using std::endl;
using std::list;

namespace Cti {
namespace Devices {

const Repeater800Device::CommandSet Repeater800Device::_commandStore = Repeater800Device::initCommandStore();


Repeater800Device::Repeater800Device() {}

Repeater800Device::Repeater800Device(const Repeater800Device& aRef)
{
    *this = aRef;
}

Repeater800Device::~Repeater800Device() {}

Repeater800Device& Repeater800Device::operator=(const Repeater800Device& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
    }

    return *this;
}

Repeater800Device::CommandSet Repeater800Device::initCommandStore()
{
    CommandSet cs;

    cs.insert(CommandStore(EmetconProtocol::GetValue_PFCount,    EmetconProtocol::IO_Read,   Rpt800_PFCountPos,  Rpt800_PFCountLen));

    return cs;
}


bool Repeater800Device::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    CommandSet::const_iterator itr = _commandStore.find(CommandStore(cmd));

    if( itr != _commandStore.end() )
    {
        function = itr->function;
        length   = itr->length;
        io       = itr->io;

        found = true;
    }
    else    // Look in the parent if not found in the child!
    {
        //  CtiDevRepeater900 is the base...
        found = Inherited::getOperation(cmd, function, length, io);
    }

    return found;
}


INT Repeater800Device::ResultDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;


    switch(InMessage->Sequence)
    {
        case (EmetconProtocol::GetValue_PFCount):
        {
            status = decodeGetValuePFCount(InMessage, TimeNow, vgList, retList, outList);
            break;
        }
        default:
        {
            status = Inherited::ResultDecode(InMessage, TimeNow, vgList, retList, outList);

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


INT Repeater800Device::decodeGetValuePFCount(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    INT   j;
    ULONG pfCount = 0;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

    for(j = 0; j < 2; j++)
    {
        pfCount = (pfCount << 8) + InMessage->Buffer.DSt.Message[j];
    }

    {
        string resultString, pointString;
        double value;

        CtiLockGuard<CtiMutex> guard(_classMutex);               // Lock the MCT device!
        CtiPointSPtr pPoint;

        if( pPoint = getDevicePointOffsetTypeEqual(20, PulseAccumulatorPointType) )
        {
            value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(pfCount);

            pointString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(value, 0);  //  boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

            if( pData = CTIDBG_new CtiPointDataMsg(pPoint->getID(), value, NormalQuality, PulseAccumulatorPointType, pointString) )
            {
                ReturnMsg->PointData().push_back(pData);
                pData = NULL;  // We just put it on the list...
            }
        }
        else
        {
            resultString += getName() + " / Blink Counter = " + CtiNumStr(pfCount) + "\n";

            ReturnMsg->setResultString(resultString);
        }
    }

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

}
}

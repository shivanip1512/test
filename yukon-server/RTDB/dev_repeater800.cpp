/*-----------------------------------------------------------------------------*
*
* File:   dev_repeater800
*
* Date:   8/24/2001
*
* Author: Matthew Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   $
* REVISION     :  $Revision: 1.21 $
* DATE         :  $Date: 2007/11/12 17:04:59 $
*
* Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <windows.h>
#include "device.h"
#include "devicetypes.h"
#include "dev_repeater800.h"
#include "logger.h"
#include "pt_numeric.h"
#include "porter.h"
#include "utility.h"
#include "numstr.h"

using Cti::Protocol::Emetcon;
using std::string;


const CtiDeviceRepeater800::CommandSet CtiDeviceRepeater800::_commandStore = CtiDeviceRepeater800::initCommandStore();


CtiDeviceRepeater800::CtiDeviceRepeater800() {}

CtiDeviceRepeater800::CtiDeviceRepeater800(const CtiDeviceRepeater800& aRef)
{
    *this = aRef;
}

CtiDeviceRepeater800::~CtiDeviceRepeater800() {}

CtiDeviceRepeater800& CtiDeviceRepeater800::operator=(const CtiDeviceRepeater800& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
    }

    return *this;
}

CtiDeviceRepeater800::CommandSet CtiDeviceRepeater800::initCommandStore()
{
    CommandSet cs;

    cs.insert(CommandStore(Emetcon::GetValue_PFCount,    Emetcon::IO_Read,   Rpt800_PFCountPos,  Rpt800_PFCountLen));

    return cs;
}


bool CtiDeviceRepeater800::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    CommandSet::iterator itr = _commandStore.find(CommandStore(cmd));

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


INT CtiDeviceRepeater800::ResultDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;


    switch(InMessage->Sequence)
    {
        case (Emetcon::GetValue_PFCount):
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


INT CtiDeviceRepeater800::decodeGetValuePFCount(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    DSTRUCT *DSt   = &InMessage->Buffer.DSt;


    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

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

            LockGuard guard(monitor());               // Lock the MCT device!
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
    }

    return status;
}




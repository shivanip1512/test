
#include "yukon.h"

#include "devicetypes.h"
#include "dev_repeater850.h"
#include "logger.h"
#include "pt_numeric.h"
#include "porter.h"
#include "utility.h"
#include "numstr.h"

using Cti::Protocols::EmetconProtocol;
using std::string;
using std::endl;
using std::list;

namespace Cti {
namespace Devices {

const Repeater850Device::CommandSet Repeater850Device::_commandStore = Repeater850Device::initCommandStore();

Repeater850Device::CommandSet Repeater850Device::initCommandStore()
{
    CommandSet cs;

    cs.insert(CommandStore(EmetconProtocol::GetConfig_Model,    EmetconProtocol::IO_Function_Read,   Rpt850_SSPecPos,  Rpt850_SSPecLen));

    return cs;
}


bool Repeater850Device::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
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
        //  CtiDevRepeater800 is the base...
        found = Inherited::getOperation(cmd, function, length, io);
    }

    return found;
}


INT Repeater850Device::ResultDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;


    switch( InMessage->Sequence )
    {
        case (EmetconProtocol::GetConfig_Model):
        {
            status = decodeGetConfigModel(InMessage, TimeNow, vgList, retList, outList);
            break;
        }
        default:
        {
            status = Inherited::ResultDecode(InMessage, TimeNow, vgList, retList, outList);

            if( status != NORMAL )
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


INT Repeater850Device::decodeGetConfigModel(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiCommandParser parse(InMessage->Return.CommandStr);


    if( !(status = decodeCheckErrorReturn(InMessage, retList, outList)) )
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg      *pData = NULL;

        if( (ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        int  sspec, rev;
        string modelStr;

        sspec  = DSt->Message[1] << 8;
        sspec |= DSt->Message[2];
        rev    = DSt->Message[3];

        modelStr = getName() + " / sspec: " + CtiNumStr(sspec);

        //  convert 10 to 1.0, 24 to 2.4
        modelStr += " rev " + CtiNumStr(((double)rev) / 10.0, 1);

        //  valid/released versions are 1.0 - 24.9
        if( rev <= SspecRev_BetaLo ||
            rev >= SspecRev_BetaHi )
        {
            modelStr += " [possible development revision]";
        }

        setDynamicInfo(CtiTableDynamicPaoInfo::Key_RPT_SSpec,         (long)sspec);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_RPT_SSpecRevision, (long)rev);

        ReturnMsg->setResultString( modelStr );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}

}
}

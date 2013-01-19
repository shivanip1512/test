#include "precompiled.h"

#include "devicetypes.h"
#include "dev_mct210.h"
#include "logger.h"
#include "numstr.h"
#include "porter.h"
#include "pt_numeric.h"
#include "utility.h"

using std::string;
using std::endl;
using std::list;

using namespace Cti::Protocols;

namespace Cti {
namespace Devices {

const Mct210Device::CommandSet Mct210Device::_commandStore = Mct210Device::initCommandStore();


Mct210Device::Mct210Device()
{
}

Mct210Device::Mct210Device(const Mct210Device &aRef)
{
    *this = aRef;
}

Mct210Device::~Mct210Device()
{
}

Mct210Device& Mct210Device::operator=(const Mct210Device &aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
    }
    return *this;
}


Mct210Device::CommandSet Mct210Device::initCommandStore()
{
    CommandSet cs;

    //  MCT 210 commands
    cs.insert(CommandStore(EmetconProtocol::GetValue_KWH,           EmetconProtocol::IO_Read,  MCT210_MReadPos,    MCT210_MReadLen));
    cs.insert(CommandStore(EmetconProtocol::Scan_Accum,             EmetconProtocol::IO_Read,  MCT210_MReadPos,    MCT210_MReadLen));

    cs.insert(CommandStore(EmetconProtocol::PutValue_KYZ,           EmetconProtocol::IO_Write, MCT210_PutMReadPos, MCT210_PutMReadLen));

    cs.insert(CommandStore(EmetconProtocol::GetValue_Demand,        EmetconProtocol::IO_Read,  MCT210_DemandPos,   MCT210_DemandLen));
    cs.insert(CommandStore(EmetconProtocol::Scan_Integrity,         EmetconProtocol::IO_Read,  MCT210_DemandPos,   MCT210_DemandLen));
    cs.insert(CommandStore(EmetconProtocol::GetStatus_Disconnect,   EmetconProtocol::IO_Read,  MCT210_StatusPos,   MCT210_StatusLen));
    cs.insert(CommandStore(EmetconProtocol::GetStatus_Internal,     EmetconProtocol::IO_Read,  MCT210_GenStatPos,  MCT210_GenStatLen));

    cs.insert(CommandStore(EmetconProtocol::PutStatus_Reset,        EmetconProtocol::IO_Write, MCT210_ResetPos,    MCT210_ResetLen));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_Multiplier,   EmetconProtocol::IO_Read,  MCT210_MultPos,     MCT210_MultLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Multiplier,   EmetconProtocol::IO_Write, MCT210_MultPos,     MCT210_MultLen));

    return cs;
}


bool Mct210Device::getOperation( const UINT &cmd, BSTRUCT &bst ) const
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
INT Mct210Device::ModelDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    switch(InMessage->Sequence)
    {
        case (EmetconProtocol::GetStatus_Disconnect):
        {
            status = decodeGetStatusDisconnect(InMessage, TimeNow, vgList, retList, outList);
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

}
}


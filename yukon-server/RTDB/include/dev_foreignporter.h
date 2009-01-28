/*-----------------------------------------------------------------------------*
*
* File:   dev_foreignporter
*
* Class:  Cti::Device::ForeignPorter
* Date:   6/21/2001
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_710.h-arc  $
* REVISION     :  $Revision: 1.2.20.1 $
* DATE         :  $Date: 2008/11/13 17:23:39 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_FOREIGNPORTER_H__
#define __DEV_FOREIGNPORTER_H__
#pragma warning( disable : 4786)



#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "dev_remote.h"

namespace Cti    {
namespace Device {

class IM_EX_DEVDB ForeignPorter : public CtiDeviceRemote
{
private:

    typedef CtiDeviceRemote Inherited;

    bool           _complete;

    unsigned char *_out_buf;
    unsigned long  _out_count;

    unsigned long  _in_count;
    unsigned char *_in_buf;
    int _errors;

    enum
    {
        MaxInbound = 10000,
        MaxErrors  = 3,
    };

    //  shouldn't be any need for this to be called by anyone
    ForeignPorter(const ForeignPorter &aRef);

protected:

public:

    ForeignPorter();

    virtual ~ForeignPorter();

    int generate(CtiXfer &xfer);
    int decode(CtiXfer &xfer, int status);

    bool isTransactionComplete();

    int recvCommRequest(OUTMESS *OutMessage);
//    int sendCommResult(INMESS *InMessage);
};


}
}

#endif // #ifndef __DEV_FOREIGNPORTER_H__

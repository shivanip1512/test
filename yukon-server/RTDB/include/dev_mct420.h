#pragma once

#include "dev_mct410.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Mct420Device : public Mct410Device
{
    static const ConfigPartsList  _config_parts;
    static       ConfigPartsList  initConfigParts();

    static const read_key_store_t _readKeyStore;
    static       read_key_store_t initReadKeyStore();

    typedef Mct410Device Inherited;

protected:

    virtual ConfigPartsList getPartsList();

    virtual bool isProfileTablePointerCurrent(const unsigned char table_pointer, const CtiTime TimeNow, const unsigned interval_len) const;

    virtual int executePutConfig       (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list<CtiMessage *> &vgList, std::list<CtiMessage *> &retList, std::list<OUTMESS *> &outList);

    virtual int executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list<CtiMessage *> &vgList, std::list<CtiMessage *> &retList, std::list<OUTMESS *> &outList, bool readsOnly);

    virtual string decodeDisconnectStatus(const DSTRUCT &DSt);

    virtual int decodeGetConfigMeterParameters( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    //  overriding the MCT-410's definitions
    virtual bool isSupported(const Mct410Device::Features feature) const;
};

}
}

#pragma once

#include "dev_mct410.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Mct420Device : public Mct410Device
{
    static const CommandSet       _commandStore;
    static       CommandSet       initCommandStore();

    static const ConfigPartsList  _config_parts;
    static       ConfigPartsList  initConfigParts();

    static const read_key_store_t _readKeyStore;
    static       read_key_store_t initReadKeyStore();

    virtual bool getOperation( const UINT &cmd, BSTRUCT &bst ) const;

    const read_key_store_t &getReadKeyStore() const;

    typedef Mct410Device Inherited;

    virtual ConfigPartsList getPartsList();

protected:

    virtual bool isProfileTablePointerCurrent(const unsigned char table_pointer, const CtiTime TimeNow, const unsigned interval_len) const;

    virtual int executePutConfig       (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list<CtiMessage *> &vgList, std::list<CtiMessage *> &retList, std::list<OUTMESS *> &outList);
    virtual int executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list<CtiMessage *> &vgList, std::list<CtiMessage *> &retList, std::list<OUTMESS *> &outList, bool readsOnly);
    virtual int executeGetConfig       (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list<CtiMessage *> &vgList, std::list<CtiMessage *> &retList, std::list<OUTMESS *> &outList);

    virtual DlcCommandSPtr makeHourlyReadCommand(const CtiDate date_begin, const CtiDate date_end, const unsigned channel) const;

    virtual std::string decodeDisconnectStatus(const DSTRUCT &DSt);

    virtual int decodeGetConfigMeterParameters( INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );
    virtual int decodeGetConfigModel          ( INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );

    //  overriding the MCT-410's definitions
    virtual bool isSupported(const Mct410Device::Features feature) const;

    virtual point_info getAccumulatorData(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const;
    virtual point_info getDemandData     (const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const;

public:

    static point_info getMct420AccumulatorData(const unsigned char *buf, const unsigned len);
};

}
}

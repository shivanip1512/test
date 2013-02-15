#pragma once

#include "dev_mct410.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Mct420Device : public Mct410Device
{
    static const CommandSet       _commandStore;
    static const ConfigPartsList  _config_parts;

    static const FunctionReadValueMappings _readValueMaps;

    typedef std::pair<unsigned, std::string> FlagMask;
    typedef std::set<FlagMask> FlagSet;

    static const FlagSet _eventFlags;
    static const FlagSet _meterAlarmFlags;

    virtual bool getOperation( const UINT &cmd, BSTRUCT &bst ) const;

    typedef Mct410Device Inherited;

    virtual ConfigPartsList getPartsList();

    virtual unsigned getUsageReportDelay(const unsigned interval_length, const unsigned days) const;

protected:

    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    virtual const FunctionReadValueMappings *getReadValueMaps() const;
    virtual const ReadDescriptor getDescriptorForRead(const unsigned char io, const unsigned function, const unsigned readLength) const;

    virtual bool isProfileTablePointerCurrent(const unsigned char table_pointer, const CtiTime TimeNow, const unsigned interval_len) const;

    virtual int decodePutConfig( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    virtual int executePutConfig       (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual int executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly);
    virtual int executeGetConfig       (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual DlcCommandSPtr makeHourlyReadCommand(const CtiDate date_begin, const CtiDate date_end, const unsigned channel) const;

    virtual std::string decodeDisconnectStatus(const DSTRUCT &DSt);

    virtual int decodeGetConfigMeterParameters  ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual int decodeGetConfigModel            ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual int decodeGetConfigDailyReadInterest( const INMESS &InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    int decodePutConfigChannel2NetMetering( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    int decodeGetConfigOptions( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    enum Features
    {
        Feature_LcdDisplayDigitConfiguration,
    };

    enum
    {
        Sspec = 10290,
        SspecRev_LcdDisplayDigitConfiguration = 44
    };

    virtual bool sspecValid(const unsigned sspec, const unsigned rev) const;

    virtual bool isSupported(const Mct410Device::Features feature) const;
    virtual bool isSupported(const Mct420Device::Features feature) const;

    virtual point_info getAccumulatorData(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const;
    virtual point_info getDemandData     (const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const;

public:

    static point_info decodePulseAccumulator(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter);
};

}
}

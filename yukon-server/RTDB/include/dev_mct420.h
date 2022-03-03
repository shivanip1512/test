#pragma once

#include "dev_mct410.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Mct420Device : public Mct410Device
{
    static const CommandSet       _commandStore;

    static const FunctionReadValueMappings _readValueMaps;

    typedef std::pair<unsigned, std::string> FlagMask;
    typedef std::set<FlagMask> FlagSet;

    static const FlagSet _eventFlags;
    static const FlagSet _meterAlarmFlags;

    typedef Mct410Device Inherited;

    virtual unsigned getUsageReportDelay(const unsigned interval_length, const unsigned days) const;

protected:

    virtual ConfigPartsList getPartsList();

    virtual bool getOperation( const UINT &cmd, BSTRUCT &bst ) const;

    YukonError_t ModelDecode( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;

    virtual const FunctionReadValueMappings *getReadValueMaps() const;
    virtual const ReadDescriptor getDescriptorForRead(const unsigned char io, const unsigned function, const unsigned readLength) const;

    virtual bool isProfileTablePointerCurrent(const unsigned char table_pointer, const CtiTime TimeNow, const unsigned interval_len) const;

    YukonError_t decodePutConfig( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;

    YukonError_t executePutConfig       (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
    YukonError_t executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly) override;
    YukonError_t executeGetConfig       (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
    YukonError_t executePutConfigMeterParameters(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly) override;

    DlcCommandPtr makeHourlyReadCommand(const CtiDate date_begin, const CtiDate date_end, const unsigned channel) const override;

    std::string decodeDisconnectStatus(const DSTRUCT &DSt) const override;

    virtual YukonError_t decodeGetConfigMeterParameters  ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual YukonError_t decodeGetConfigModel            ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual YukonError_t decodeGetConfigDailyReadInterest( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    YukonError_t decodePutConfigChannel2NetMetering( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    YukonError_t decodeGetConfigOptions( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    enum Features
    {
        Feature_LcdDisplayDigitConfiguration,
        Feature_Display,
    };

    enum
    {
        Sspec = 10290,
        SspecRev_LcdDisplayDigitConfiguration = 44
    };

    virtual bool sspecValid(const unsigned sspec, const unsigned rev) const;

    virtual bool isSupported(const Mct410Device::Features feature) const;
    virtual bool isSupported(const Mct420Device::Features feature) const;

    virtual frozen_point_info getAccumulatorData(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const;
    virtual frozen_point_info getDemandData     (const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const;

public:
    Mct420Device() {};

    static frozen_point_info decodePulseAccumulator(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter);
};

}
}

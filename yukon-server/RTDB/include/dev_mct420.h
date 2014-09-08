#pragma once

#include "dev_mct410.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Mct420Device : public Mct410Device
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    Mct420Device(const Mct420Device&);
    Mct420Device& operator=(const Mct420Device&);

    static const CommandSet       _commandStore;
    static const ConfigPartsList  _config_parts;

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

    INT ModelDecode( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;

    virtual const FunctionReadValueMappings *getReadValueMaps() const;
    virtual const ReadDescriptor getDescriptorForRead(const unsigned char io, const unsigned function, const unsigned readLength) const;

    virtual bool isProfileTablePointerCurrent(const unsigned char table_pointer, const CtiTime TimeNow, const unsigned interval_len) const;

    int decodePutConfig( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;

    int executePutConfig       (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
    int executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly) override;
    int executeGetConfig       (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
    int executePutConfigMeterParameters(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly) override;

    DlcCommandAutoPtr makeHourlyReadCommand(const CtiDate date_begin, const CtiDate date_end, const unsigned channel) const override;

    std::string decodeDisconnectStatus(const DSTRUCT &DSt) const override;

    virtual int decodeGetConfigMeterParameters  ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual int decodeGetConfigModel            ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual int decodeGetConfigDailyReadInterest( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    int decodePutConfigChannel2NetMetering( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    int decodeGetConfigOptions( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

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
    Mct420Device() {};

    static point_info decodePulseAccumulator(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter);
};

}
}

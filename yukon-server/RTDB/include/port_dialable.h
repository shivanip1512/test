#pragma once

#include "port_base.h"
#include "port_modem.h"
#include "tbl_port_dialup.h"


class CtiPortDialable : private boost::noncopyable
{
    BOOL                _shouldDisconnect;
    std::string         _dialedUpNumber;

    CtiTablePortDialup  _tblPortDialup;

protected:

    CtiPort             *_superPort;           // This is how we know who owns us...
    CtiHayesModem       _modem;

public:

    CtiPortDialable()  :
        _shouldDisconnect(FALSE)
    {}
    virtual ~CtiPortDialable() {}

    CtiHayesModem& getModem();
    CtiPortDialable& setSuperPort(CtiPort *port);

    std::string getDialedUpNumber() const;
    std::string& getDialedUpNumber();
    CtiPortDialable& setDialedUpNumber(const std::string &str);

    virtual BOOL shouldDisconnect() const;
    virtual void setShouldDisconnect(BOOL b = TRUE);

    virtual YukonError_t connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace) = 0;

    virtual YukonError_t reset(INT trace) = 0;
    virtual INT setup(INT trace) = 0;
    virtual INT close(INT trace) = 0;

    virtual YukonError_t waitForResponse(PULONG ResponseSize, PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse = NULL);
    virtual INT disconnect(CtiDeviceSPtr Device, INT trace);

    const CtiTablePortDialup& getTablePortDialup() const;

    static std::string getSQLCoreStatement();

    void DecodeDatabaseReader(Cti::RowReader &rdr);

};

inline CtiPortDialable& CtiPortDialable::setSuperPort(CtiPort *port)
{
    _superPort = port;
    return *this;
}

inline BOOL CtiPortDialable::shouldDisconnect() const
{
    return _shouldDisconnect;
}

inline void CtiPortDialable::setShouldDisconnect(BOOL b)
{
    _shouldDisconnect = b;
}

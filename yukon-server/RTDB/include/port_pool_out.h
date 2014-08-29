#pragma once

#include "port_base.h"


#define PORTPOOL_DEBUGLEVL_CHILDAVAILABILITY            0x00000010
#define PORTPOOL_DEBUGLEVL_CHILDSELECTION               0x00000020
#define PORTPOOL_DEBUGLEVL_CHILDALLOCATION              0x00000040
#define PORTPOOL_DEBUGLEVL_CHILDADDITION                0x00000080

#define PORTPOOL_DEBUGLEVL_POOLQUEUE                    0x00010000  // Used on the porter side...
#define PORTPOOL_DEBUGLEVL_QUEUEDUMPS                   0x00020000
#define PORTPOOL_DEBUGLEVL_POSTSTOPARENT                0x00040000  // Used on the porter side...

class IM_EX_PRTDB CtiPortPoolDialout : public CtiPort
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiPortPoolDialout(const CtiPortPoolDialout&);
    CtiPortPoolDialout& operator=(const CtiPortPoolDialout&);

protected:

    typedef std::vector< CtiPortSPtr > CtiPortPoolVector;

    std::vector< long >          _portids;
    CtiPortPoolVector       _ports;

private:

    static int _poolDebugLevel;

public:

    enum
    {
        PPSC_ParentQueueEmpty,
        PPSC_AllChildrenBusy,
        PPSC_ChildReady
    }
    CtiPortPoolDialoutStatusCode;

    typedef CtiPort Inherited;

    CtiPortPoolDialout() {}

    static std::string getSQLCoreStatement();
    static std::string getSQLPooledPortsStatement();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual YukonError_t openPort(INT rate = 0, INT bits = 8, INT parity = NOPARITY, INT stopbits = ONESTOPBIT);
    virtual YukonError_t inMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, std::list< CtiMessage* > &traceList);
    virtual YukonError_t outMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, std::list< CtiMessage* > &traceList);

    virtual size_t addPort(CtiPortSPtr port);

    void DecodePooledPortsDatabaseReader(Cti::RowReader &rdr);

    CtiPortSPtr getAvailableChildPort(CtiDeviceSPtr  Device);
    INT allocateQueueEntsToChildPort();

};

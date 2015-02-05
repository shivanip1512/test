#pragma once

#include <windows.h>

#include "yukon.h"
#include "vcomdefs.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableExpresscomLoadGroup : public CtiMemDBObject, private boost::noncopyable
{
    LONG _lmGroupId;
    LONG _routeId;

    UINT _serial;                   // 1 - 4294967295
    USHORT _serviceProvider;        // 1 - 65534
    USHORT _geo;                    // 1 - 65534
    USHORT _substation;             // 1 - 65534
    USHORT _feeder;                 // 1 - 65534            // Bit-wise or'd against switch config in FW.
    UINT _zip;                      // 1 - 16777214
    USHORT _uda;                    // 1 - 65534            // User Defined Address
    UCHAR _program;                 // 1 - 65534
    UCHAR _splinter;
    UCHAR _priority;

    USHORT _addressUsage;             // bit indicators.  LSB is SPID.  No bits set indicates serial.
    USHORT _loads;             // 0 indicates all loads.  Otherwise, one load per message!

public:

    typedef CtiMemDBObject Inherited;

    CtiTableExpresscomLoadGroup();
    virtual ~CtiTableExpresscomLoadGroup();

    LONG getId() const;
    CtiTableExpresscomLoadGroup& setId(LONG id);
    LONG getRouteId() const;
    CtiTableExpresscomLoadGroup& setRouteId(LONG id);
    UINT getSerial() const;
    CtiTableExpresscomLoadGroup&  setSerial(UINT sid);
    USHORT getServiceProvider() const;
    CtiTableExpresscomLoadGroup& setServiceProvider(USHORT spid);
    USHORT getGeo() const;
    CtiTableExpresscomLoadGroup& setGeo(USHORT geo);
    USHORT getSubstation() const;
    CtiTableExpresscomLoadGroup& setSubstation(USHORT sub);
    USHORT getFeeder() const;
    CtiTableExpresscomLoadGroup& setFeeder(USHORT feed);
    UINT getZip() const;
    CtiTableExpresscomLoadGroup& setZip(UINT zip);
    USHORT getUda() const;
    CtiTableExpresscomLoadGroup& setUda(USHORT user);
    UCHAR getPriority() const;
    CtiTableExpresscomLoadGroup& setPriority(UCHAR prog);
    UCHAR getProgram() const;
    CtiTableExpresscomLoadGroup& setProgram(UCHAR prog);
    UCHAR getSplinter() const;
    CtiTableExpresscomLoadGroup& setSplinter(UCHAR splinter);

    USHORT getAddressUsage() const;
    CtiTableExpresscomLoadGroup& setAddressUsage(USHORT addrussage);
    BYTE getLoadMask() const;
    CtiTableExpresscomLoadGroup& setLoadMask(BYTE load);
    BOOL useRelay(const INT r) const;

    static std::string getTableName();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual bool Insert();
    virtual bool Update();
    virtual bool Delete();

};

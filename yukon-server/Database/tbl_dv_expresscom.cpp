/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_expresscom
*
* Date:   10/4/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2008/04/30 21:15:39 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "resolvers.h"
#include "tbl_dv_expresscom.h"

using std::string;
using std::endl;

CtiTableExpresscomLoadGroup::CtiTableExpresscomLoadGroup() :
_lmGroupId(-1),
_routeId(-1),
_serial(0),              // 1 - 4294967295
_serviceProvider(0),     // 1 - 65534
_geo(0),                 // 1 - 65534
_substation(0),          // 1 - 65534
_feeder(0),              // 1 - 65534            // Bit-wise or'd against switch config in FW.
_zip(0),                 // 1 - 16777214
_uda(0),                 // 1 - 65534            // User Defined Address
_program(0),             // 1 - 65534
_splinter(0),
_addressUsage(0),        // bit indicators.  LSB is SPID.  No bits set indicates serial.
_loads(0),           // 0 indicates all loads.  Otherwise, one load per message!
_priority(0)
{
}

CtiTableExpresscomLoadGroup::CtiTableExpresscomLoadGroup(const CtiTableExpresscomLoadGroup& aRef)
{
    *this = aRef;
    return;
}

CtiTableExpresscomLoadGroup::~CtiTableExpresscomLoadGroup()
{
}

CtiTableExpresscomLoadGroup& CtiTableExpresscomLoadGroup::operator=(const CtiTableExpresscomLoadGroup& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _lmGroupId = aRef.getId();
        _routeId = aRef.getRouteId();
        _serial = aRef.getSerial();
        _serviceProvider = aRef.getServiceProvider();
        _geo = aRef.getGeo();
        _substation = aRef.getSubstation();
        _feeder = aRef.getFeeder();
        _zip = aRef.getZip();
        _uda = aRef.getUda();
        _program = aRef.getProgram();
        _splinter = aRef.getSplinter();
        _addressUsage = aRef.getAddressUsage();
        _loads = aRef.getLoadMask();
        _priority = aRef.getPriority();
    }

    return *this;
}

LONG CtiTableExpresscomLoadGroup::getId() const
{
    return _lmGroupId;
}
CtiTableExpresscomLoadGroup& CtiTableExpresscomLoadGroup::setId(LONG id)
{
    _lmGroupId = id;
    return *this;
}
LONG CtiTableExpresscomLoadGroup::getRouteId() const
{
    return _routeId;
}
CtiTableExpresscomLoadGroup& CtiTableExpresscomLoadGroup::setRouteId(LONG id)
{
    _routeId = id;
    return *this;
}
UINT CtiTableExpresscomLoadGroup::getSerial() const
{
    return _serial;
}
CtiTableExpresscomLoadGroup&  CtiTableExpresscomLoadGroup::setSerial(UINT sid)
{
    _serial = sid;
    return *this;
}
USHORT CtiTableExpresscomLoadGroup::getServiceProvider() const
{
    return _serviceProvider;
}
CtiTableExpresscomLoadGroup& CtiTableExpresscomLoadGroup::setServiceProvider(USHORT spid)
{
    _serviceProvider = spid;
    return *this;
}
USHORT CtiTableExpresscomLoadGroup::getGeo() const
{
    return _geo;
}
CtiTableExpresscomLoadGroup& CtiTableExpresscomLoadGroup::setGeo(USHORT geo)
{
    _geo = geo;
    return *this;
}
USHORT CtiTableExpresscomLoadGroup::getSubstation() const
{
    return _substation;
}
CtiTableExpresscomLoadGroup& CtiTableExpresscomLoadGroup::setSubstation(USHORT sub)
{
    _substation = sub;
    return *this;
}
USHORT CtiTableExpresscomLoadGroup::getFeeder() const
{
    return _feeder;
}
CtiTableExpresscomLoadGroup& CtiTableExpresscomLoadGroup::setFeeder(USHORT feed)
{
    _feeder = feed;
    return *this;
}
UINT CtiTableExpresscomLoadGroup::getZip() const
{
    return _zip;
}
CtiTableExpresscomLoadGroup& CtiTableExpresscomLoadGroup::setZip(UINT zip)
{
    _zip = zip;
    return *this;
}
USHORT CtiTableExpresscomLoadGroup::getUda() const
{
    return _uda;
}
CtiTableExpresscomLoadGroup& CtiTableExpresscomLoadGroup::setUda(USHORT user)
{
    _uda = user;
    return *this;
}
UCHAR CtiTableExpresscomLoadGroup::getProgram() const
{
    return _program;
}
CtiTableExpresscomLoadGroup& CtiTableExpresscomLoadGroup::setProgram(UCHAR prog)
{
    _program = prog;
    return *this;
}
UCHAR CtiTableExpresscomLoadGroup::getPriority() const
{
    return _priority;
}
CtiTableExpresscomLoadGroup& CtiTableExpresscomLoadGroup::setPriority(UCHAR priority)
{
    _priority = priority;
    return *this;
}
UCHAR CtiTableExpresscomLoadGroup::getSplinter() const
{
    return _splinter;
}
CtiTableExpresscomLoadGroup& CtiTableExpresscomLoadGroup::setSplinter(UCHAR splinter)
{
    _splinter = splinter;
    return *this;
}

USHORT CtiTableExpresscomLoadGroup::getAddressUsage() const
{
    return _addressUsage;
}
CtiTableExpresscomLoadGroup& CtiTableExpresscomLoadGroup::setAddressUsage(USHORT addrussage)
{
    _addressUsage = addrussage;
    return *this;
}
BYTE CtiTableExpresscomLoadGroup::getLoadMask() const
{
    return _loads;
}
CtiTableExpresscomLoadGroup& CtiTableExpresscomLoadGroup::setLoadMask(BYTE load)
{
    _loads = load;
    return *this;
}

string CtiTableExpresscomLoadGroup::getTableName()
{
    return string("ExpressComAddress_View");
}

void CtiTableExpresscomLoadGroup::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    /*
    USHORT _serviceProvider;        // 1 - 65534
    USHORT _geo;                    // 1 - 65534
    USHORT _substation;             // 1 - 65534
    USHORT _feeder;                 // 1 - 65534            // Bit-wise or'd against switch config in FW.
    UINT _zip;                      // 1 - 16777214
    USHORT _uda;                    // 1 - 65534            // User Defined Address
    UCHAR _program;                 // 1 - 65534
    UCHAR _splinter;

    BYTE _addressUsage;             // bit indicators.  LSB is SPID.  No bits set indicates serial.
    USHORT _loads;             // 0 indicates all loads.  Otherwise, one load per message!

     */


    rdr["paobjectid"        ] >> _lmGroupId;
    rdr["routeid"           ] >> _routeId;
    rdr["serialnumber"      ] >> rwsTemp;

    _serial = atoi(rwsTemp.c_str());

    rdr["serviceaddress"    ] >> _serviceProvider;
    rdr["geoaddress"        ] >> _geo;
    rdr["substationaddress" ] >> _substation;
    rdr["feederaddress"     ] >> _feeder;
    rdr["zipcodeaddress"    ] >> _zip;
    rdr["udaddress"         ] >> _uda;
    rdr["programaddress"    ] >> _program;
    rdr["splinteraddress"   ] >> _splinter;

    rdr["addressusage"      ] >> rwsTemp;
    _addressUsage = resolveAddressUsage( rwsTemp.c_str(), Cti::AddressUsage_Expresscom );

    rdr["relayusage"        ] >> rwsTemp;
    rdr["protocolpriority"  ] >> _priority;

    _loads = resolveRelayUsage(rwsTemp.c_str());
}

bool CtiTableExpresscomLoadGroup::Insert()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return false;
}

bool CtiTableExpresscomLoadGroup::Update()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return false;
}

bool CtiTableExpresscomLoadGroup::Delete()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return false;
}

BOOL CtiTableExpresscomLoadGroup::useRelay(const INT r) const
{
    return((_loads & (0x00000001 << (r - 1))) ? TRUE : FALSE);
}





#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_base
*
* Date:   9/13/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_port_base.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_PORT_BASE_H__
#define __TBL_PORT_BASE_H__

#include "dbmemobject.h"
#include "dbaccess.h"
#include "porttypes.h"
#include "resolvers.h"
#include "yukon.h"

class IM_EX_CTIYUKONDB CtiTablePortBase : public CtiMemDBObject
{

protected:

    // Original CommPort Table
    //LONG          PortID;
    //RWCString     Description;                   // This is no longer used as the name in porter anywhere!!!
    //INT           Type;

    UINT _protocol;              // 32 protocols.
    bool _alarmInhibit;
    bool _performanceAlarm;

    INT _performanceThreshold;

    RWCString _sharedPortType;
    INT _sharedSocketNumber;

private:

    public:

    typedef CtiMemDBObject Inherited;

    CtiTablePortBase();
    CtiTablePortBase(const CtiTablePortBase& aRef);

    virtual ~CtiTablePortBase();

    CtiTablePortBase& operator=(const CtiTablePortBase& aRef);

    INT   getProtocol() const;
    void  setProtocol(int t);


    CtiTablePortBase& setAlarmInhibit(bool b);
    bool  getAlarmInhibit() const;

    bool isPerformanceAlarm() const;
    void setPerformanceAlarm(bool b = true);

    void setSharedPortType(RWCString str);
    RWCString getSharedPortType() const;
    INT getSharedSocketNumber() const;
    void setSharedSocketNumber(INT sockNum);

    bool isTCPIPPort() const;

    INT  getPerformanceThreshold() const;
    INT& getPerformanceThreshold();
    CtiTablePortBase& setPerformanceThreshold( const INT aPerformanceThreshold );

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    static RWCString getTableName();
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual void DumpData();
};
#endif // #ifndef __TBL_PORT_BASE_H__

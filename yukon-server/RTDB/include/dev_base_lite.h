
#pragma warning( disable : 4786)
#ifndef __DEV_BASE_LITE_H__
#define __DEV_BASE_LITE_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_base_lite
*
* Class:  CtiDeviceBaseLite
* Date:   1/2/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_base_lite.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2002/10/02 19:20:03 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
//using namespace std;

#include <rw\cstring.h>

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>

#include "dbaccess.h"
#include "dbmemobject.h"
#include "dlldefs.h"

class IM_EX_PNTDB CtiDeviceBaseLite : public CtiMemDBObject, public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
protected:

    LONG        _deviceID;
    RWCString   _name;
    RWCString   _description;
    RWCString   _objectType;
    RWCString   _disableFlag;
    RWCString   _controlInhibitFlag;

    private:

    public:

    CtiDeviceBaseLite(LONG id = -1);
    CtiDeviceBaseLite(const CtiDeviceBaseLite& aRef);

    virtual ~CtiDeviceBaseLite();

    CtiDeviceBaseLite& operator=(const CtiDeviceBaseLite& aRef);
    LONG getID() const;
    RWCString getName() const;
    RWCString getDescription() const;

    CtiDeviceBaseLite& setID( LONG id );
    CtiDeviceBaseLite& setName( const RWCString &str );
    CtiDeviceBaseLite& setDescription( const RWCString &str );
    CtiDeviceBaseLite& setDisableFlag( const RWCString &str );
    CtiDeviceBaseLite& setControlInhibitFlag( const RWCString &str );

    RWCString getObjectType() const;
    RWCString getDisableFlag() const;
    bool isDisabled() const;
    RWCString getControlInhibitFlag() const;
    bool isControlInhibited() const;

    bool operator<( const CtiDeviceBaseLite &rhs ) const;
    bool operator==( const CtiDeviceBaseLite &rhs ) const;
    bool operator()(const CtiDeviceBaseLite& aRef) const;


    static RWCString getTableName();
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual RWDBStatus Restore();
};
#endif // #ifndef __DEV_BASE_LITE_H__

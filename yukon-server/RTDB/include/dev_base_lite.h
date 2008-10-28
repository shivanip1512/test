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
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2008/10/28 19:21:43 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_BASE_LITE_H__
#define __DEV_BASE_LITE_H__
#pragma warning( disable : 4786)


#include <windows.h>



#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>

#include "dbaccess.h"
#include "dbmemobject.h"
#include "dlldefs.h"

class IM_EX_PNTDB CtiDeviceBaseLite : public CtiMemDBObject
{
protected:

    LONG        _deviceID;
    LONG        _portID;
    string   _class;
    string   _name;
    string   _description;
    string   _objectType;
    string   _disableFlag;
    string   _controlInhibitFlag;

    private:

    public:

    CtiDeviceBaseLite(LONG id = -1);
    CtiDeviceBaseLite(const CtiDeviceBaseLite& aRef);

    virtual ~CtiDeviceBaseLite();

    CtiDeviceBaseLite& operator=(const CtiDeviceBaseLite& aRef);
    LONG getID() const;
    LONG getPortID() const;
    string getClass() const;
    string getName() const;
    string getDescription() const;

    CtiDeviceBaseLite& setID( LONG id );
    CtiDeviceBaseLite& setPortID( LONG id );
    CtiDeviceBaseLite& setClass( const string &str );
    CtiDeviceBaseLite& setName( const string &str );
    CtiDeviceBaseLite& setDescription( const string &str );
    CtiDeviceBaseLite& setDisableFlag( const string &str );
    CtiDeviceBaseLite& setControlInhibitFlag( const string &str );

    string getObjectType() const;
    string getDisableFlag() const;
    bool isDisabled() const;
    string getControlInhibitFlag() const;
    bool isControlInhibited() const;

    bool operator<( const CtiDeviceBaseLite &rhs ) const;
    bool operator==( const CtiDeviceBaseLite &rhs ) const;
    bool operator()(const CtiDeviceBaseLite& aRef) const;


    static string getTableName();
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual RWDBStatus Restore();
};
#endif // #ifndef __DEV_BASE_LITE_H__

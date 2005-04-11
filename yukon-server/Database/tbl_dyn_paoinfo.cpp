/*-----------------------------------------------------------------------------*
*
* File:   tbl_dyn_paoinfo
*
* Date:   2005-jan-17
*
* Author: Matt Fisher
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/04/11 16:15:47 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_dyn_paoinfo.h"

using namespace std;


const string                                     CtiTableDynamicPaoInfo::_empty_string = "(empty)";
const map<int, CtiTableDynamicPaoInfo::EntityID> CtiTableDynamicPaoInfo::_entity_map   = CtiTableDynamicPaoInfo::initEntityMap();

map<int, CtiTableDynamicPaoInfo::EntityID> CtiTableDynamicPaoInfo::initEntityMap()
{
    map<int, EntityID> e_m;

    e_m.insert(make_pair(Entity_Dispatch,       Entity_Dispatch));
    e_m.insert(make_pair(Entity_Porter,         Entity_Porter));
    e_m.insert(make_pair(Entity_Scanner,        Entity_Scanner));
    e_m.insert(make_pair(Entity_CapControl,     Entity_CapControl));
    e_m.insert(make_pair(Entity_LoadManagement, Entity_LoadManagement));
    e_m.insert(make_pair(Entity_Calc,           Entity_Calc));

    return e_m;
}


//  remove for 3.1 and head
RWDBInserter& operator<<(RWDBInserter& ins, const string &s)
{
    return ins << RWCString(s.data());
}

RWDBReader& operator>>(RWDBReader& rdr, string& s)
{
    RWCString rw_str;
    rdr >> rw_str;
    s = (const char*) rw_str.data();
    return rdr ;
}

CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo() :
    _pao_id(-1),
    _entity_id(Entity_Invalid),
    _string_parameter(""),
    _long_parameter(0)
{
}


CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo(const CtiTableDynamicPaoInfo& aRef) :
    _pao_id(-1),
    _entity_id(Entity_Invalid),
    _string_parameter(""),
    _long_parameter(0)
{
    *this = aRef;
}


CtiTableDynamicPaoInfo::~CtiTableDynamicPaoInfo()
{
}


CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::operator=(const CtiTableDynamicPaoInfo& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        setPaoID(aRef.getPaoID());
        setEntityID(aRef.getEntityID());
        setStringParameter(aRef.getStringParameter());
        setLongParameter(aRef.getLongParameter());
    }

    return *this;
}


RWCString CtiTableDynamicPaoInfo::getTableName()
{
    return RWCString("DynamicPaoInfo");
}


RWDBStatus CtiTableDynamicPaoInfo::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    return Insert(conn);
}


RWDBStatus CtiTableDynamicPaoInfo::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    return Update(conn);
}

RWDBStatus CtiTableDynamicPaoInfo::Insert(RWDBConnection &conn)
{
    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();
    RWDBStatus retval(RWDBStatus::ok);

    if(getStringParameter().empty())
    {
        setStringParameter(_empty_string);
    }

    if(getPaoID())
    {
        inserter << getPaoID()
                 << getEntityID()
                 << getStringParameter()
                 << getLongParameter();

        if(DebugLevel & DEBUGLEVEL_LUDICROUS)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << endl << RWTime() << " **** INSERT Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << inserter.asString() << endl << endl;
        }

        inserter.execute( conn );

        if(inserter.status().errorCode() != RWDBStatus::ok)    // error occured!
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** SQL FAILED Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << inserter.asString() << endl;
            }
        }
        else
        {
            resetDirty(FALSE);
        }

        retval = inserter.status();
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - attempt to insert into " << getTableName << " with paoid == 0 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return retval;
}

RWDBStatus CtiTableDynamicPaoInfo::Update(RWDBConnection &conn)
{
    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();
    RWDBStatus  dbstatus(RWDBStatus::ok);

    updater.where( table["paoid"] == getPaoID() && table["entityid"] == getEntityID() );

    if(getStringParameter().empty())
    {
        setStringParameter(_empty_string);
    }

    if(getPaoID())
    {
        updater << table["paoid"].assign(getPaoID())
                << table["entityid"].assign(getEntityID())
                << table["string_parameter"].assign(getStringParameter().data())
                << table["long_parameter"].assign(getLongParameter());

        RWDBResult myResult = updater.execute( conn );

        dbstatus = myResult.status();

        long rowsAffected = myResult.rowCount();

        if(DebugLevel & DEBUGLEVEL_LUDICROUS)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << endl << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << updater.asString() << endl << endl;
        }

        if( dbstatus.errorCode() == RWDBStatus::ok && rowsAffected > 0)
        {
            setDirty(false);
        }
        else
        {
            dbstatus = Insert(conn);        // Try a vanilla insert if the update failed!
        }
    }

    return dbstatus;
}

RWDBStatus CtiTableDynamicPaoInfo::Restore()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector << table["paoid"]
             << table["string_parameter"]
             << table["long_parameter"];

    selector.where( table["paoid"] == getPaoID() );

    RWDBReader reader = selector.reader( conn );

    /*
     *  If we are in the database, we reload and ARE NOT dirty... otherwise, we are dirty and need to be
     *  written into the database
     */
    if( reader() )
    {
        DecodeDatabaseReader( reader );
    }
    else
    {
        setDirty( TRUE );
    }

    return reader.status();
}

RWDBStatus CtiTableDynamicPaoInfo::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["paoid"] == getPaoID() && table["entityid"] == getEntityID() );

    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << deleter.asString() << endl << endl;
    }

    return deleter.execute( conn ).status();
}

void CtiTableDynamicPaoInfo::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(CtiTableDynamicPaoInfo::getTableName());

    selector << keyTable["paoid"]
             << keyTable["entityid"]
             << keyTable["string_parameter"]
             << keyTable["long_parameter"];

    selector.from(keyTable);
}
void CtiTableDynamicPaoInfo::DecodeDatabaseReader(RWDBReader& rdr)
{
    RWCString tmp_string;
    long tmp_paoid, tmp_long;
    int  tmp_entityid;
    map<int, EntityID>::const_iterator itr;
    EntityID e_id = Entity_Invalid;

    rdr["paoid"]            >> tmp_paoid;
    rdr["entityid"]         >> tmp_entityid;
    rdr["string_parameter"] >> tmp_string;
    rdr["long_parameter"]   >> tmp_long;

    itr = _entity_map.find(tmp_entityid);
    if( itr != _entity_map.end() )
    {
        e_id = itr->second;
    }

    setPaoID(tmp_paoid);
    setEntityID(e_id);
    setStringParameter(tmp_string.data());
    setLongParameter(tmp_long);

    resetDirty(FALSE);
}


long CtiTableDynamicPaoInfo::getPaoID() const
{
    return _pao_id;
}
CtiTableDynamicPaoInfo::EntityID CtiTableDynamicPaoInfo::getEntityID() const
{
    return _entity_id;
}
const string &CtiTableDynamicPaoInfo::getStringParameter() const
{
    return _string_parameter;
}
long CtiTableDynamicPaoInfo::getLongParameter() const
{
    return _long_parameter;
}


CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setPaoID(long pao_id)
{
    _pao_id = pao_id;

    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setEntityID(EntityID entity_id)
{
    _entity_id = entity_id;

    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setStringParameter(const string &s)
{
    _string_parameter = s;

    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setLongParameter(long l)
{
    _long_parameter = l;

    return *this;
}


void CtiTableDynamicPaoInfo::dump()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << "getPaoID()           " << getPaoID()           << endl;
        dout << "getEntityID()        " << getEntityID()        << endl;
        dout << "getStringParameter() " << getStringParameter() << endl;
        dout << "getLongParameter()   " << getLongParameter()   << endl;
    }
}


/*-----------------------------------------------------------------------------*
*
* File:   tbl_dyn_paoinfo
*
* Date:   2005-jan-17
*
* Author: Matt Fisher
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/05/12 19:46:13 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "rwutil.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_dyn_paoinfo.h"

using namespace std;


const string CtiTableDynamicPaoInfo::_empty_string = "(empty)";

const string CtiTableDynamicPaoInfo::_owner_dispatch       = "dispatch";
const string CtiTableDynamicPaoInfo::_owner_porter         = "porter";
const string CtiTableDynamicPaoInfo::_owner_scanner        = "scanner";
const string CtiTableDynamicPaoInfo::_owner_capcontrol     = "capacitor control";
const string CtiTableDynamicPaoInfo::_owner_loadmanagement = "load management";
const string CtiTableDynamicPaoInfo::_owner_calc           = "calc and logic";

const string CtiTableDynamicPaoInfo::_key_mct_sspec = "mct sspec";

const CtiTableDynamicPaoInfo::owner_map_t CtiTableDynamicPaoInfo::_owner_map = CtiTableDynamicPaoInfo::init_owner_map();
const CtiTableDynamicPaoInfo::key_map_t   CtiTableDynamicPaoInfo::_key_map   = CtiTableDynamicPaoInfo::init_key_map();


CtiTableDynamicPaoInfo::owner_map_t CtiTableDynamicPaoInfo::init_owner_map()
{
    owner_map_t retval;

    retval.insert(make_pair(Owner_Dispatch, &_owner_dispatch));

    return retval;
}


CtiTableDynamicPaoInfo::key_map_t CtiTableDynamicPaoInfo::init_key_map()
{
    key_map_t retval;

    retval.insert(make_pair(Key_MCTSSpec, &_key_mct_sspec));

    return retval;
}


CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo() :
    _pao_id(-1),
    _owner_id(Owner_Invalid),
    _key(Key_Invalid),
    _value("")
{
}


CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo(const CtiTableDynamicPaoInfo& aRef) :
    _pao_id(-1),
    _owner_id(Owner_Invalid),
    _key(Key_Invalid),
    _value("")
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
        setOwner(aRef.getOwner());
        setKey(aRef.getKey());
        setValue(aRef.getStringValue());
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

    if(getStringValue().empty())
    {
        setValue(_empty_string);
    }

    if(getPaoID())
    {
        /*
        //  this needs to become smart about turning its enums into strings.
        inserter << getPaoID()
                 << getOwner()
                 << getKey()
                 << getValue();
        */

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

    //  this needs to become smart about turning its enums into strings.
    /*updater.where( table["paoid"] == getPaoID() && table["ownerid"] == getOwnerID() );*/

    if(getStringValue().empty())
    {
        setValue(_empty_string);
    }

    if(getPaoID())
    {
    //  this needs to become smart about turning its enums into strings.
        /*
        updater << table["paoid"].assign(getPaoID())
                << table["ownerid"].assign(getOwnerID())
                << table["string_parameter"].assign(getStringParameter().data())
                << table["long_parameter"].assign(getLongParameter());
        */

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

    //  this needs to become smart about turning its enums into strings
    /*deleter.where( table["paoid"] == getPaoID() && table["ownerid"] == getOwnerID() );*/

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

    selector << keyTable["entryid"]
             << keyTable["paoid"]
             << keyTable["owner"]
             << keyTable["key"]
             << keyTable["value"];

    selector.from(keyTable);
}
void CtiTableDynamicPaoInfo::DecodeDatabaseReader(RWDBReader& rdr)
{
    string tmp_owner, tmp_key, tmp_value;
    long tmp_entryid, tmp_paoid;

    key_map_t::const_iterator k_itr;
    owner_map_t::const_iterator o_itr;

    rdr["entryid"] >> tmp_entryid;
    rdr["paoid"]   >> tmp_paoid;
    rdr["owner"]   >> tmp_owner;
    rdr["key"]     >> tmp_key;
    rdr["value"]   >> tmp_value;

    setEntryID(tmp_entryid);
    setPaoID(tmp_paoid);

    o_itr = _owner_map.begin();
    while( o_itr != _owner_map.end() )
    {
        if( !tmp_owner.compare(*(o_itr->second)) )
        {
            setOwner(o_itr->first);
            o_itr = _owner_map.end();
        }
        else
        {
            o_itr++;
        }
    }

    k_itr = _key_map.begin();
    while( k_itr != _key_map.end() )
    {
        if( !tmp_key.compare(*(k_itr->second)) )
        {
            setKey(k_itr->first);
            k_itr = _key_map.end();
        }
        else
        {
            k_itr++;
        }
    }

    setValue(tmp_value);

    resetDirty(FALSE);
}


long CtiTableDynamicPaoInfo::getEntryID() const
{
    return _entry_id;
}
long CtiTableDynamicPaoInfo::getPaoID() const
{
    return _pao_id;
}
CtiTableDynamicPaoInfo::Owners CtiTableDynamicPaoInfo::getOwner() const
{
    return _owner_id;
}
CtiTableDynamicPaoInfo::Keys CtiTableDynamicPaoInfo::getKey() const
{
    return _key;
}
const string &CtiTableDynamicPaoInfo::getStringValue() const
{
    return _value;
}
long CtiTableDynamicPaoInfo::getLongValue()
{
    _value_as_long = atol(_value.data());

    return _value_as_long;
}
double CtiTableDynamicPaoInfo::getDoubleValue()
{
    _value_as_double = atof(_value.data());

    return _value_as_double;
}


CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setEntryID(long entry_id)
{
    _entry_id = entry_id;

    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setPaoID(long pao_id)
{
    _pao_id = pao_id;

    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setOwner(Owners owner_id)
{
    _owner_id = owner_id;

    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setKey(Keys k)
{
    _key = k;

    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setValue(const string &s)
{
    _value = s;

    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setValue(double d)
{
    _value_as_long = d;
    _value = CtiNumStr(d);

    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setValue(long l)
{
    _value_as_long = l;
    _value = CtiNumStr(l);

    return *this;
}


void CtiTableDynamicPaoInfo::dump()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << "getPaoID()       " << getPaoID() << endl;
        dout << "getOwner()       " << getOwner() << endl;
        dout << "getStringValue() " << getStringValue() << endl;
        dout << "getDoubleValue() " << getDoubleValue() << endl;
        dout << "getLongValue()   " << getLongValue() << endl;
    }
}


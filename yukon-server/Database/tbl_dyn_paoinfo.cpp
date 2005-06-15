/*-----------------------------------------------------------------------------*
*
* File:   tbl_dyn_paoinfo
*
* Date:   2005-jan-17
*
* Author: Matt Fisher
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/06/15 19:20:30 $
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

const string CtiTableDynamicPaoInfo::_key_mct_sspec                = "mct sspec";
const string CtiTableDynamicPaoInfo::_key_mct_ied_loadprofile_rate = "mct ied load profile rate";
const string CtiTableDynamicPaoInfo::_key_mct_loadprofile_config   = "mct load profile config";

const CtiTableDynamicPaoInfo::owner_map_t CtiTableDynamicPaoInfo::_owner_map = CtiTableDynamicPaoInfo::init_owner_map();
const CtiTableDynamicPaoInfo::key_map_t   CtiTableDynamicPaoInfo::_key_map   = CtiTableDynamicPaoInfo::init_key_map();


CtiTableDynamicPaoInfo::owner_map_t CtiTableDynamicPaoInfo::init_owner_map()
{
    owner_map_t retval;

    retval.insert(make_pair(Application_Dispatch, &_owner_dispatch));

    return retval;
}


CtiTableDynamicPaoInfo::key_map_t CtiTableDynamicPaoInfo::init_key_map()
{
    key_map_t retval;

    retval.insert(make_pair(Key_MCTSSpec,              &_key_mct_sspec));
    retval.insert(make_pair(Key_MCTIEDLoadProfileRate, &_key_mct_ied_loadprofile_rate));
    retval.insert(make_pair(Key_MCTLoadProfileConfig,  &_key_mct_loadprofile_config));

    return retval;
}


CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo() :
    _entry_id(-1),
    _pao_id(-1),
    _owner_id(Application_Invalid),
    _key(Key_Invalid),
    _value("")
{
}


CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo(const CtiTableDynamicPaoInfo& aRef) :
    _entry_id(-1),
    _pao_id(-1),
    _owner_id(Application_Invalid),
    _key(Key_Invalid),
    _value("")
{
    *this = aRef;
}


CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo(long paoid, Keys k) :
    _entry_id(-1),
    _pao_id(paoid),
    _owner_id(Application_Invalid),
    _key(k),
    _value("")
{
}


CtiTableDynamicPaoInfo::~CtiTableDynamicPaoInfo()
{
}


CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::operator=(const CtiTableDynamicPaoInfo& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        setEntryID(aRef.getEntryID());
        setPaoID(aRef.getPaoID());
        setOwner(aRef.getOwner());
        setKey(aRef.getKey());
        setValue(aRef.getValue());
    }

    return *this;
}


bool CtiTableDynamicPaoInfo::operator<(const CtiTableDynamicPaoInfo &rhs) const
{
    //  there should not be more than one of these in any device's collection of table entries, so this is safe for a total ordering
    //    it makes set-based lookups possible, as well - i didn't want to use a map in the device
    return getKey() < rhs.getKey();
}


RWCString CtiTableDynamicPaoInfo::getTableName()
{
    return RWCString("DynamicPaoInfo");
}


bool CtiTableDynamicPaoInfo::hasRow() const
{
    return (_entry_id > 0);
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

    const string *tmp_owner = 0, *tmp_key = 0;
    string tmp_value;

    if( _owner_map.find(getOwner()) != _owner_map.end() )
    {
        tmp_owner = (_owner_map.find(getOwner()))->second;
    }

    if( _key_map.find(getKey()) != _key_map.end() )
    {
        tmp_key = (_key_map.find(getKey()))->second;
    }

    getValue(tmp_value);
    if( tmp_value.empty() )
    {
        tmp_value = _empty_string;
    }

    if( getPaoID() && tmp_owner && tmp_key )
    {
        inserter <<  getEntryID()  //  MUST be set before we try to insert
                 <<  getPaoID()
                 << *tmp_owner
                 << *tmp_key
                 <<  tmp_value;

        if(DebugLevel & DEBUGLEVEL_LUDICROUS)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << endl << RWTime() << " **** INSERT Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << inserter.asString() << endl << endl;
        }

        inserter.execute(conn);

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

    updater.where( table["entryid"] == getEntryID() );

    const string *tmp_owner = 0, *tmp_key = 0;
    string tmp_value;

    if( _owner_map.find(getOwner()) != _owner_map.end() )
    {
        tmp_owner = (_owner_map.find(getOwner()))->second;
    }

    if( _key_map.find(getKey()) != _key_map.end() )
    {
        tmp_key = (_key_map.find(getKey()))->second;
    }

    getValue(tmp_value);
    if( tmp_value.empty() )
    {
        tmp_value = _empty_string;
    }

    if( getEntryID() && tmp_owner && tmp_key )
    {
        updater << table["paoid"].assign(getPaoID())
                << table["owner"].assign(tmp_owner->data())
                << table["infokey"].assign(tmp_key->data())
                << table["value"].assign(tmp_value.data());

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

    selector << table["entryid"]
             << table["paoid"]
             << table["owner"]
             << table["infokey"]
             << table["value"];

    selector.where( table["entryid"] == getEntryID() );

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

    deleter.where(table["entryid"] == getEntryID());

    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << deleter.asString() << endl << endl;
    }

    return deleter.execute(conn).status();
}

void CtiTableDynamicPaoInfo::getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector, CtiApplication_t app_id)
{
    keyTable = db.table(CtiTableDynamicPaoInfo::getTableName());
    owner_map_t::const_iterator o_itr;

    selector << keyTable["entryid"]
             << keyTable["paoid"]
             << keyTable["owner"]
             << keyTable["infokey"]
             << keyTable["value"];

    selector.from(keyTable);

    o_itr = _owner_map.find(app_id);
    if( o_itr != _owner_map.end() && o_itr->second )
    {
        selector.where(keyTable["owner"] == o_itr->second->data());
    }
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
    rdr["infokey"]     >> tmp_key;
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

    //  should we turn _empty_string into ""?
    setValue(tmp_value);

    //  make sure this happens at the end, so we reset the dirty bit AFTER all of those above calls set it dirty
    resetDirty();
}


long CtiTableDynamicPaoInfo::getEntryID() const
{
    return _entry_id;
}
long CtiTableDynamicPaoInfo::getPaoID() const
{
    return _pao_id;
}
CtiApplication_t CtiTableDynamicPaoInfo::getOwner() const
{
    return _owner_id;
}
CtiTableDynamicPaoInfo::Keys CtiTableDynamicPaoInfo::getKey() const
{
    return _key;
}


string CtiTableDynamicPaoInfo::getValue() const
{
    return _value;
}

//  these may need to become individually named get functions, if the assignment idiom doesn't work out
void CtiTableDynamicPaoInfo::getValue(string &destination) const
{
    destination = _value;
}
void CtiTableDynamicPaoInfo::getValue(long &destination) const
{
    destination = atol(_value.data());
}
void CtiTableDynamicPaoInfo::getValue(double &destination) const
{
    destination = atof(_value.data());
}


CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setEntryID(long entry_id)
{
    _entry_id = entry_id;

    setDirty();
    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setPaoID(long pao_id)
{
    _pao_id = pao_id;

    setDirty();
    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setOwner(CtiApplication_t owner_id)
{
    _owner_id = owner_id;

    setDirty();
    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setKey(Keys k)
{
    _key = k;

    setDirty();
    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setValue(const string &s)
{
    //  maybe put in a null check, and assign "(empty)"
    _value = s;

    setDirty();
    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setValue(double d)
{
    _value = CtiNumStr(d);

    setDirty();
    return *this;
}
CtiTableDynamicPaoInfo& CtiTableDynamicPaoInfo::setValue(long l)
{
    _value = CtiNumStr(l);

    setDirty();
    return *this;
}


void CtiTableDynamicPaoInfo::dump()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << "getEntryID() " << getEntryID() << endl;
        dout << "getPaoID()   " << getPaoID() << endl;
        dout << "getOwner()   " << getOwner() << endl;
        dout << "getKey()     " << getKey() << endl;
        dout << "getValue()   " << getValue() << endl;
    }
}


/*-----------------------------------------------------------------------------*
*
* File:   verifier
*
* Date:   4/12/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/07/27 15:43:23 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include <set>
using namespace std;

#include "boost_time.h"

#include "mgr_port.h"
#include "dbaccess.h"
#include "rwutil.h"

#include "portverify.h"

extern CtiPortManager PortManager;

const string CtiPorterVerification::_table_name = "DynamicVerification";


CtiPorterVerification::CtiPorterVerification() :
    _running(false),
    _sequence(0)
{
}

CtiPorterVerification::CtiPorterVerification(const CtiPorterVerification& aRef)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint - copy constructor called for CtiPorterVerification **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

CtiPorterVerification::~CtiPorterVerification()
{
}


void CtiPorterVerification::run( void )
{
    _running = true;  //  race condition is not possible, since Porter hasn't started the port threads yet

    verificationThread();
}



const string &CtiPorterVerification::getTableName()
{
    return _table_name;
}


void CtiPorterVerification::verificationThread( void )
{
    CtiVerificationBase   *base;
    CtiVerificationWork   *work;
    CtiVerificationReport *report;

    int sleep;

    set(RELOAD);

    try
    {
        while( !isSet(SHUTDOWN) )
        {
            if( isSet(RELOAD) )
            {
                loadAssociations();
            }

            //  3 seconds, in order to allow reasonable shutdown behavior
            if( base = _input.getQueue(3000) )
            {
                switch( base->getType() )
                {
                    case CtiVerificationBase::Type_Work:
                    {
                        work = static_cast<CtiVerificationWork *>(base);

                        //  find the range of receivers associated with this transmitter
                        pair<association_itr, association_itr> range = _associations.equal_range(work->getTransmitterID());

                        //  if there are any entries
                        if( range.first != range.second )
                        {
                            //  walk through the list
                            for( association_itr itr = range.first; itr != range.second; itr++ )
                            {
                                association &a = (*itr).second;
                                receiver_itr r_itr = _receiver_work.find(a.receiver_id);

                                //  insert it if it doesn't exist (this should only happen once)
                                if( r_itr == _receiver_work.end() )
                                {
                                    r_itr = (_receiver_work.insert(make_pair(a.receiver_id, vector< CtiVerificationWork * >()))).first;
                                }

                                (r_itr->second).push_back(work);
                                work->addExpectation(a.receiver_id, a.retransmit);
                            }

                            _work_queue.push(work);
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - no associations found for for transmitter id \"" << work->getTransmitterID() << "\" - discarding work object **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            delete work;
                        }

                        break;
                    }

                    case CtiVerificationBase::Type_Report:
                    {
                        report = static_cast<CtiVerificationReport *>(base);

                        //  grab the work vector for this receiver
                        receiver_itr r_itr = _receiver_work.find(report->getReceiverID());

                        if( r_itr != _receiver_work.end() )
                        {
                            pending_vector &p_v = r_itr->second;

                            //  iterate through all entries, looking for a matching code
                            for( pending_itr itr = p_v.begin(); itr != p_v.end(); itr++ )
                            {
                                //  if it's accepted
                                if( (*itr)->checkReceipt(*report) )
                                {
                                    delete report;
                                    report = 0;

                                    p_v.erase(itr);

                                    break;
                                }
                            }

                            if( report )
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint - record not found for code \"" << report->getCode() << "\"  **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }

                                writeUnknown(*report);

                                delete report;
                                report = 0;
                            }
                        }

                        break;
                    }

                    default:
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - unknown type \"" << base->getType() << "\" in verificationThread;  deleting message **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        //  base base base
                        delete base;
                    }
                }
            }

            while( !_work_queue.empty() && _work_queue.top()->getExpiration() < second_clock::universal_time() )
            {
                CtiVerificationBase::CodeStatus status;
                CtiVerificationWork *work = _work_queue.top();

                _work_queue.pop();

                status = work->processResult(/* pass in any previous entry with a matching om->VerificationSequence */);

                writeWorkRecord(*work);

                if( status == CtiVerificationBase::CodeStatus_Retry )
                {
                    CtiOutMessage *om = work->getRetryOM();

                    PortManager.writeQueue(om->Port, om->EventCode, sizeof(*om), static_cast<void *>(om), om->Priority);

                    //  possible addition to enhance retry logic  ----
                    //_retry_queue.push_back(work);  //  this is where we keep track of the receivers on which we've already heard this code
                }
                //else
                //{
                delete work;
                //}
            }
            //  possible optimization sometime
            /*
            if( !_work_queue.empty() )
            {
                ptime::time_duration_type td = (*_work_queue.begin())->getExpiration() - second_clock::universal_time();

                if( td < seconds::seconds(15) )
                {
                    wait = td.total_milliseconds();
                }
            }
            */
        }
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Exception in CtiPorterVerification, thread exiting" << endl;
    }
}


void CtiPorterVerification::push(CtiVerificationBase *entry)
{
    if( _running )
    {
        if( entry )
        {
            _input.putQueue(entry);
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - CtiPorterVerification::push cannot enqueue null pointer **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - CtiPorterVerification::push will not enqueue message when thread is not running, deleting entry **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        delete entry;
    }
}


void CtiPorterVerification::push(queue< CtiVerificationBase * > &entries)
{
    if( _running )
    {
        while( !entries.empty() )
        {
            if( entries.front() )
            {
                _input.putQueue(entries.front());
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - CtiPorterVerification::push cannot enqueue null pointer **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            entries.pop();
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - CtiPorterVerification::push will not enqueue message when thread is not running, deleting " << entries.size() << " entries **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        while( !entries.empty() )
        {
            delete entries.front();

            entries.pop();
        }
    }
}


void CtiPorterVerification::loadAssociations(void)
{
    RWDBConnection conn     = getConnection();
    RWDBDatabase   db       = getDatabase();
    RWDBSelector   selector = db.selector();

    RWDBTable      tblVerification = db.table("DeviceVerification");

    RWDBReader rdr;
    long tmp;

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Verification thread loading transmitter associations" << endl;
    }

    {
        selector << tblVerification["ReceiverID"]
                 << tblVerification["TransmitterID"]
                 << tblVerification["ResendOnFail"];

        rdr = selector.reader(conn);

        if( rdr.status().errorCode() == RWDBStatus::ok )
        {
            while( rdr() )
            {
                association tmp_association;
                unsigned long tmp_receiver,
                              tmp_transmitter;
                RWCString     tmp_resend;

                rdr["ReceiverID"]    >> tmp_receiver;
                rdr["TransmitterID"] >> tmp_transmitter;
                rdr["ResendOnFail"]  >> tmp_resend;
                tmp_resend.toLower();

                //tmp_association.transmitter_id = tmp_transmitter;
                tmp_association.receiver_id    = tmp_receiver;
                tmp_association.retransmit     = (tmp_resend.compareTo("y") == 0)?true:false;

                _associations.insert(make_pair(tmp_transmitter, tmp_association));
            }
        }
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Verification thread done loading transmitter associations" << endl;
    }

    set(RELOAD, false);
}


void CtiPorterVerification::writeWorkRecord(const CtiVerificationWork &work)
{
    RWDBTable table = getDatabase().table(getTableName().data());
    RWDBInserter inserter = table.inserter();
    RWDBStatus::ErrorCode err;

    ptime now(second_clock::universal_time());

    vector< long > expectations;
    vector< long >::iterator e_itr;

    vector< pair< long, ptime > > receipts;
    vector< pair< long, ptime > >::iterator r_itr;

    expectations = work.getExpectations();
    receipts     = work.getReceipts();

    string command      = work.getCommand();
    string code_status  = CtiVerificationBase::getCodeStatusName(work.getCodeStatus());
    long transmitter_id = work.getTransmitterID();
    long sequence       = work.getSequence();

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        {
            RWDBStatus dbstat = conn.beginTransaction("DynamicVerification");

            for( r_itr = receipts.begin(); (r_itr != receipts.end()) && dbstat.isValid() && conn.isValid(); r_itr++ )
            {
                inserter << logIDGen()
                         << r_itr->second
                         << r_itr->first
                         << transmitter_id
                         << command
                         << "Y"
                         << sequence
                         << code_status;

                if( err = inserter.execute(conn).status().errorCode() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - error \"" << err << "\" while inserting in CtiPorterVerification::recordUnknown **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    //  dout << "Data: ";
                }
            }

            for( e_itr = expectations.begin(); e_itr != expectations.end(); ++e_itr )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << expectations.size() << endl;
                    dout << "*e_itr = " << e_itr << endl;
                }
            }

            for( e_itr = expectations.begin(); (e_itr != expectations.end()) && dbstat.isValid() && conn.isValid(); e_itr++ )
            {
                inserter << logIDGen()
                         << now
                         << *e_itr
                         << transmitter_id
                         << command
                         << "N"
                         << sequence
                         << code_status;

                if( err = inserter.execute(conn).status().errorCode() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - error \"" << err << "\" while inserting in CtiPorterVerification::recordUnknown **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    //  dout << "Data: ";
                }
            }

            conn.commitTransaction("DynamicVerification");
        }
    }
}


void CtiPorterVerification::writeUnknown(const CtiVerificationReport &report)
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table(getTableName().data());
    RWDBInserter inserter = table.inserter();
    RWDBStatus::ErrorCode e;
    const string &cs = CtiVerificationBase::getCodeStatusName(CtiVerificationBase::CodeStatus_Unexpected);

    inserter << logIDGen()
             << report.getReceiptTime()
             << report.getReceiverID()
             << -1  //  unknown transmitter ID
             << report.getCode()
             << "Y"
             << -1
             << cs;

    if( e = inserter.execute(conn).status().errorCode() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint - error \"" << e << "\" while inserting in CtiPorterVerification::recordUnknown **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        //  dout << "Data: ";
    }
}


void CtiPorterVerification::pruneEntries(const ptime::time_duration_type &age)
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table(getTableName().data());
    RWDBDeleter deleter = table.deleter();
    RWDBStatus::ErrorCode e;

    unsigned long earliest = ::std::time(0) - age.total_seconds();

    deleter.where(table["datetime"] < RWDBDateTime(RWTime(earliest + rwEpoch)));

    if( e = deleter.execute(conn).status().errorCode() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint - error \"" << e << "\" while pruning entries in CtiPorterVerification::pruneEntries **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        //  dout << "Data: ";
    }
}


long CtiPorterVerification::logIDGen(bool force)
{
    static RWMutexLock mux;
    RWMutexLock::LockGuard guard(mux);

    INT tempid = 0;
    static BOOL init_id = FALSE;
    static INT id = 0;
    static const CHAR sql[] = "SELECT MAX(LOGID) FROM DYNAMICVERIFICATION";

    if(!init_id || force)
    {   // Make sure all objects that that store results
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        // are out of scope when the release is called
        RWDBReader  rdr = ExecuteQuery( conn, sql );

        if(rdr() && rdr.isValid())
        {
            rdr >> tempid;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - invalid reader in CtiPorterVerification::logIDGen **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if(tempid >= id)
        {
            id = tempid;
        }

        init_id = TRUE;
    }   // Temporary results are destroyed to free the connection

    return(++id);
}


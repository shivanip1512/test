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
* REVISION     :  $Revision: 1.17 $
* DATE         :  $Date: 2005/02/10 23:23:55 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <set>
using namespace std;

#include "boost_time.h"

#include "mgr_port.h"
#include "dbaccess.h"
#include "rwutil.h"
#include "cparms.h"

#include "portverify.h"

extern CtiPortManager PortManager;

const string CtiPorterVerification::_table_name = "DynamicVerification";


CtiPorterVerification::CtiPorterVerification() :
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
    if( !_work_queue.empty() )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - " << _work_queue.size() << " items left in CtiPorterVerification work queue, purging **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    while( !_work_queue.empty() )
    {
        delete _work_queue.top();

        _work_queue.pop();
    }
}


void CtiPorterVerification::run( void )
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " PortVerificationThread TID: " << CurrentTID () << endl;
    }

    verificationThread();

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " PortVerificationThread TID: " << CurrentTID() << " shutting down" << endl;
    }

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

    ptime::time_duration_type prune_interval      = hours(24) * gConfigParms.getValueAsInt("DYNAMIC_VERIFICATION_PRUNE_DAYS", 30);
    ptime::time_duration_type queue_read_interval = seconds(10);
    ptime last_prune = second_clock::universal_time() - hours(48);  //  two days ago will force it to go right now

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

            if( (last_prune + hours(24)) <= second_clock::universal_time() )
            {
                pruneEntries(prune_interval);

                last_prune = second_clock::universal_time();
            }

            ptime next_db_check = second_clock::universal_time() + queue_read_interval;

            while( second_clock::universal_time() < next_db_check )
            {
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
                                        r_itr = (_receiver_work.insert(make_pair(a.receiver_id, deque< CtiVerificationWork * >()))).first;
                                    }

                                    (r_itr->second).push_back(work);
                                    work->addExpectation(a.receiver_id, a.retransmit);
                                }
                            }
                            else
                            {
                                if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint - no associations found for for transmitter id \"" << work->getTransmitterID() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                            }

                            _work_queue.push(work);

                            break;
                        }

                        case CtiVerificationBase::Type_Report:
                        {
                            report = static_cast<CtiVerificationReport *>(base);

                            //  grab the work vector for this receiver
                            receiver_itr r_itr = _receiver_work.find(report->getReceiverID());

                            if( r_itr != _receiver_work.end() )
                            {
                                pending_queue &p_q = r_itr->second;

                                //  iterate through all entries, looking for a matching code
                                for( pending_itr itr = p_q.begin(); itr != p_q.end(); itr++ )
                                {
                                    //  if it's accepted
                                    if( (*itr)->checkReceipt(*report) )
                                    {
                                        p_q.erase(itr);

                                        delete report;
                                        report = 0;

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
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint - entry received for unknown receiver \"" << report->getReceiverID() << "\"  **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }

                                writeUnknown(*report);

                                delete report;
                                report = 0;
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
            }

            processWorkQueue();
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Verification thread received shutdown - writing all pending codes to DB" << endl;
        }

        processWorkQueue(true);
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Exception in CtiPorterVerification, thread exiting" << endl;
    }
}


void CtiPorterVerification::processWorkQueue(bool purge)
{
    if( !_work_queue.empty() && (purge || (_work_queue.top()->getExpiration() < second_clock::universal_time())) )
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBStatus dbstat = conn.beginTransaction("DynamicVerification");

        while( !_work_queue.empty() && (purge || (_work_queue.top()->getExpiration() < second_clock::universal_time())) )
        {
            CtiVerificationBase::CodeStatus status;
            CtiVerificationWork *work = _work_queue.top();

            _work_queue.pop();

            status = work->processResult(/* pass in any previous entry with a matching om->VerificationSequence */);

            if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - writing code sequence \"" << work->getSequence() << "\" **** Expires at " << to_simple_string(work->getExpiration()) << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            writeWorkRecord(*work, conn, dbstat);

            if( status == CtiVerificationBase::CodeStatus_Retry )
            {
                CtiOutMessage *om = work->getRetryOM();

                PortManager.writeQueue(om->Port, om->EventCode, sizeof(*om), static_cast<void *>(om), om->Priority);

                //  possible addition to enhance retry logic  ----
                //_retry_queue.push_back(work);  //  this is where we keep track of the receivers on which we've already heard this code
                                                 //    we would need to make sure not to delete the work object in this case
            }

            //  hmm...  i could hash on code instead of receiver...  that would be suave

            //  remove the expectations that weren't received  (maybe change this to a global list based on expiration)
            deque< long > expectations = work->getExpectations();
            deque< long >::iterator e_itr;

            for( e_itr = expectations.begin(); e_itr != expectations.end(); e_itr++ )
            {
                receiver_itr r_itr = _receiver_work.find(*e_itr);

                if( r_itr != _receiver_work.end() )
                {
                    pending_queue &p_q = r_itr->second;

                    //  iterate through all entries, looking for a pointer match
                    for( pending_itr itr = p_q.begin(); itr != p_q.end(); itr++ )
                    {
                        if( *itr == work )
                        {
                            p_q.erase(itr);

                            break;
                        }
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }

            //else
            //{
            delete work;
            //}
        }

        conn.commitTransaction("DynamicVerification");
    }
}


void CtiPorterVerification::push(CtiVerificationBase *entry)
{
    static bool whine = false;

    if( isRunning() )
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
        if(!whine)
        {
            whine = true;
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - CtiPorterVerification::push will not enqueue message when thread is not running, deleting entry **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        delete entry;
    }
}


void CtiPorterVerification::push(queue< CtiVerificationBase * > &entries)
{
    static bool whine = false;

    if( isRunning() )
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
        if(!whine)
        {
            whine = true;
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
                 << tblVerification["ResendOnFail"]
                 << tblVerification["Disable"];

        rdr = selector.reader(conn);

        if( rdr.status().errorCode() == RWDBStatus::ok )
        {
            while( rdr() )
            {
                association tmp_association;
                unsigned long tmp_receiver,
                              tmp_transmitter;
                RWCString     tmp_resend,
                              tmp_disable;

                rdr["ReceiverID"]    >> tmp_receiver;
                rdr["TransmitterID"] >> tmp_transmitter;
                rdr["ResendOnFail"]  >> tmp_resend;
                rdr["Disable"]       >> tmp_disable;
                tmp_resend.toLower();

                //tmp_association.transmitter_id = tmp_transmitter;
                tmp_association.receiver_id    = tmp_receiver;
                tmp_association.retransmit     = (tmp_resend.compareTo("y") == 0)?true:false;

                //  only add this association if we're to verify using it...  otherwise, ignore it and move on
                //    generally, all associations on a transmitter will be either all on or all off, but
                //    this allows for some flexibility
                if( tmp_disable.compareTo("y", RWCString::ignoreCase) != 0 )
                {
                    _associations.insert(make_pair(tmp_transmitter, tmp_association));
                }
            }
        }
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Verification thread done loading transmitter associations" << endl;
    }

    set(RELOAD, false);
}


void CtiPorterVerification::writeWorkRecord(const CtiVerificationWork &work, RWDBConnection &conn, RWDBStatus &dbstat)
{
    //  note that this should be called from within a trasnaction

    RWDBTable table = getDatabase().table(getTableName().data());
    RWDBInserter inserter = table.inserter();
    RWDBStatus::ErrorCode err;

    ptime now(second_clock::universal_time());

    deque< long > expectations;
    deque< long >::iterator e_itr;

    deque< pair< long, ptime > > receipts;
    deque< pair< long, ptime > >::iterator r_itr;

    expectations = work.getExpectations();
    receipts     = work.getReceipts();

    string command      = work.getCommand();
    string code         = work.getCode();
    string code_status  = CtiVerificationBase::getCodeStatusName(work.getCodeStatus());
    long transmitter_id = work.getTransmitterID();
    long sequence       = work.getSequence();

    if( code.empty() )
    {
        code = "(empty)";
    }

    inserter << logIDGen()
             << work.getSubmissionTime()
             << 0
             << transmitter_id
             << command
             << code
             << sequence
             << "-"
             << CtiVerificationBase::getCodeStatusName(CtiVerificationBase::CodeStatus_Sent);

    if( err = inserter.execute(conn).status().errorCode() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint - error \"" << err << "\" while inserting in CtiPorterVerification::recordUnknown **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        //  dout << "Data: ";
    }

    for( r_itr = receipts.begin(); (r_itr != receipts.end()) && dbstat.isValid() && conn.isValid(); r_itr++ )
    {
        inserter << logIDGen()
                 << r_itr->second
                 << r_itr->first
                 << transmitter_id
                 << command
                 << code
                 << sequence
                 << "Y"
                 << code_status;

        if( err = inserter.execute(conn).status().errorCode() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - error \"" << err << "\" while inserting in CtiPorterVerification::recordUnknown **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            //  dout << "Data: ";
        }
    }

    for( e_itr = expectations.begin(); (e_itr != expectations.end()) && dbstat.isValid() && conn.isValid(); e_itr++ )
    {
        inserter << logIDGen()
                 << now
                 << *e_itr
                 << transmitter_id
                 << command
                 << code
                 << sequence
                 << "N"
                 << code_status;

        if( err = inserter.execute(conn).status().errorCode() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - error \"" << err << "\" while inserting in CtiPorterVerification::recordUnknown **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            //  dout << "Data: ";
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
    string code = report.getCode();

    if( code.empty() )
    {
        code = "(empty)";
    }

    inserter << logIDGen()
             << report.getReceiptTime()
             << report.getReceiverID()
             << 0  //  unknown transmitter ID
             << "-"
             << code
             << -1
             << "Y"
             << cs;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << inserter.asString() << endl;
    }


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

    unsigned long earliest = ::time(0) - age.total_seconds();

    deleter.where(table["timearrival"] < RWDBDateTime(RWTime(earliest + rwEpoch)));

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Beginning to prune DynamicVerification. " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << deleter.asString() << endl;
    }
    if( e = deleter.execute(conn).status().errorCode() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint - error \"" << e << "\" while pruning entries in CtiPorterVerification::pruneEntries **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        //  dout << "Data: ";
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " DynamicVerification pruning successful (" << RWTime(earliest + rwEpoch) << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
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


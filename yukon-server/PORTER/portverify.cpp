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
* REVISION     :  $Revision: 1.33 $
* DATE         :  $Date: 2008/08/14 15:57:41 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <set>
#include "boost_time.h"

#include "mgr_port.h"
#include "dbaccess.h"
#include "cparms.h"

#include "portverify.h"
#include "ctidate.h"
#include "ctitime.h"
#include "database_reader.h"
#include "database_writer.h"

using namespace std;
using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseReader;

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
        dout << CtiTime() << " **** Checkpoint - copy constructor called for CtiPorterVerification **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

CtiPorterVerification::~CtiPorterVerification()
{
    if( !_work_queue.empty() )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - " << _work_queue.size() << " items left in CtiPorterVerification work queue, purging **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        dout << CtiTime() << " PortVerificationThread TID: " << CurrentTID () << endl;
    }

    SetThreadName(-1, "PrtVerify");
	
	verificationThread();

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " PortVerificationThread TID: " << CurrentTID() << " shutting down" << endl;
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
    CtiVerificationReport *vReport;

    ptime::time_duration_type report_interval     = minutes(5);
    ptime::time_duration_type queue_read_interval = seconds(10);
    ptime::time_duration_type prune_interval      = hours(24);
    ptime::time_duration_type prune_depth         = hours(24) * gConfigParms.getValueAsInt("DYNAMIC_VERIFICATION_PRUNE_DAYS", 30);

    ptime last_prune  = second_clock::universal_time() - hours(48);  //  two days ago will force it to go right now
    ptime last_report = second_clock::universal_time();  //  we don't need to print right away

    set(RELOAD);

    try
    {
        //  main loop
        while( !isSet(SHUTDOWN) )
        {
            //  check if we need to reload...
            if( isSet(RELOAD) )
            {
                loadAssociations();
            }

            //  ... or prune anything from the DB...
            if( (last_prune + prune_interval) <= second_clock::universal_time() )
            {
                pruneEntries(prune_depth);

                last_prune = second_clock::universal_time();
            }

            //  ... or let the world know we're doing OK
            if( (last_report + report_interval) <= second_clock::universal_time() )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " PortVerificationThread TID: " << CurrentTID () << " is running "
                         << "(INQ: " << _input.size() << ", WQ: " << _work_queue.size();

                    if( !_work_queue.empty() && _work_queue.top() )
                    {
                        dout << ", WQ top: " << to_simple_string(_work_queue.top()->getExpiration()) << ")" << endl;
                    }
                    else
                    {
                        dout << ", WQ top: [empty])" << endl;
                    }
                }

                last_report = second_clock::universal_time();
            }

            //  set the next DB check time...
            ptime next_db_check = second_clock::universal_time() + queue_read_interval;

            //  ... we'll try to read from the queue until then
            while( !isSet(SHUTDOWN) && second_clock::universal_time() < next_db_check )
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
                                        r_itr = (_receiver_work.insert(std::make_pair(a.receiver_id, deque< CtiVerificationWork * >()))).first;
                                    }

                                    (r_itr->second).push_back(work);
                                    work->addExpectation(a.receiver_id, a.retransmit);
                                }
                            }
                            else
                            {
                                if( isDebugLudicrous() )
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Checkpoint - no associations found for for transmitter id \"" << work->getTransmitterID() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                            }

                            _work_queue.push(work);

                            break;
                        }

                        case CtiVerificationBase::Type_Report:
                        {
                            vReport = static_cast<CtiVerificationReport *>(base);

                            //  grab the work vector for this receiver
                            receiver_itr r_itr = _receiver_work.find(vReport->getReceiverID());

                            if( r_itr != _receiver_work.end() )
                            {
                                pending_queue &p_q = r_itr->second;

                                //  iterate through all entries, looking for a matching code
                                for( pending_itr itr = p_q.begin(); itr != p_q.end(); itr++ )
                                {
                                    //  if it's accepted
                                    if( (*itr)->checkReceipt(*vReport) )
                                    {
                                        p_q.erase(itr);

                                        delete vReport;
                                        vReport = 0;

                                        /*
                                        if( (*itr)->complete() )
                                        {
                                            somehow reorder this one to the top so it gets written in a reasonable amount of time... ?
                                        }
                                        */

                                        break;
                                    }
                                }

                                if( vReport )
                                {
                                    if( isDebugLudicrous() )
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " **** Checkpoint - record not found for code \"" << vReport->getCode() << "\"  **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }

                                    writeUnknown(*vReport);

                                    delete vReport;
                                    vReport = 0;
                                }
                            }
                            else
                            {
                                if( isDebugLudicrous() )
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Checkpoint - entry received for unknown receiver \"" << vReport->getReceiverID() << "\"  **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }

                                writeUnknown(*vReport);

                                delete vReport;
                                vReport = 0;
                            }

                            break;
                        }

                        default:
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - unknown type \"" << base->getType() << "\" in verificationThread;  deleting message **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            //  base base base
                            delete base;
                        }
                    }
                }
            }

            processWorkQueue();
        }

        if( _work_queue.empty() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Verification thread received shutdown - no pending codes" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Verification thread received shutdown - writing " << _work_queue.size() << " pending codes to DB" << endl;
        }

        processWorkQueue(true);
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Exception in CtiPorterVerification, thread exiting" << endl;
    }
}


void CtiPorterVerification::processWorkQueue(bool purge)
{
    if( !_work_queue.empty() && (purge || (_work_queue.top()->getExpiration() < second_clock::universal_time())) )
    {
        Cti::Database::DatabaseConnection   conn;

        conn.beginTransaction();

        while( !_work_queue.empty() && (purge || (_work_queue.top()->getExpiration() < second_clock::universal_time())) )
        {
            CtiVerificationBase::CodeStatus status;
            CtiVerificationWork *work = _work_queue.top();

            _work_queue.pop();

            status = work->processResult(/* pass in any previous entry with a matching om->VerificationSequence */);

            if( isDebugLudicrous() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - writing code sequence \"" << work->getSequence() << "\" **** Expires at " << to_simple_string(work->getExpiration()) << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            writeWorkRecord(*work, conn);

            if( status == CtiVerificationBase::CodeStatus_Retry )
            {
                CtiOutMessage *om = work->getRetryOM();

                PortManager.writeQueue(om->Port, om->Request.GrpMsgID, sizeof(*om), static_cast<void *>(om), om->Priority);

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
                        dout << CtiTime() << " **** Checkpoint - work/expectation mismatch **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }

            //else
            //{
            delete work;
            //}
        }

        conn.commitTransaction();
    }
}


void CtiPorterVerification::push(CtiVerificationBase *entry)
{
    static bool warning_printed = false;

    if( isRunning() )
    {
        if( entry )
        {
            int maxsize = gConfigParms.getValueAsInt("PORTER_VERIFICATION_QUEUE_LIMIT", 10000);

            if( _input.entries() < maxsize )
            {
                _input.putQueue(entry);
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - CtiPorterVerification::push has exceeded maximum queue size (" << maxsize << "), purging **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _input.clearAndDestroy();
                delete entry;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - CtiPorterVerification::push cannot enqueue null pointer **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else
    {
        if( !warning_printed )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - CtiPorterVerification::push will not enqueue message when thread is not running, deleting entry **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            warning_printed = true;
        }

        delete entry;
    }
}


void CtiPorterVerification::push(queue< CtiVerificationBase * > &entries)
{
    static bool warning_printed = false;

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
                dout << CtiTime() << " **** Checkpoint - CtiPorterVerification::push cannot enqueue null pointer **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            entries.pop();
        }
    }
    else
    {
        if( !warning_printed )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - CtiPorterVerification::push will not enqueue message when thread is not running, deleting " << entries.size() << " entries **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            warning_printed = true;
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
    long tmp;

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Verification thread loading transmitter associations" << endl;
    }

    {
        static const string sql = "SELECT DVR.ReceiverID, DVR.TransmitterID, DVR.ResendOnFail, DVR.Disable "
                                  "FROM DeviceVerification DVR";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);
        rdr.execute();

        if( rdr.isValid() )
        {
            while( rdr() )
            {
                association tmp_association;
                unsigned long tmp_receiver,
                              tmp_transmitter;
                string     tmp_resend,
                              tmp_disable;

                rdr["ReceiverID"]    >> tmp_receiver;
                rdr["TransmitterID"] >> tmp_transmitter;
                rdr["ResendOnFail"]  >> tmp_resend;
                rdr["Disable"]       >> tmp_disable;
                std::transform(tmp_resend.begin(), tmp_resend.end(), tmp_resend.begin(), ::tolower);

                //tmp_association.transmitter_id = tmp_transmitter;
                tmp_association.receiver_id    = tmp_receiver;
                tmp_association.retransmit     = (tmp_resend.compare("y") == 0)?true:false;

                //  only add this association if we're to verify using it...  otherwise, ignore it and move on
                //    generally, all associations on a transmitter will be either all on or all off, but
                //    this allows for some flexibility
                if(!ciStringEqual(tmp_disable,"y"))
                {
                    _associations.insert(std::make_pair(tmp_transmitter, tmp_association));
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


void CtiPorterVerification::writeWorkRecord(const CtiVerificationWork &work, Cti::Database::DatabaseConnection &conn)
{
    //  note that this should be called from within a transaction

    static const std::string sql = "insert into " + getTableName() +
                                   " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

    if( command.empty() )
    {
        command = "(empty)";
    }
    if( code.empty() )
    {
        code = "(empty)";
    }
    if( code_status.empty() )
    {
        code_status = "(empty)";
    }

    Cti::Database::DatabaseWriter   inserter(conn, sql);

    inserter
        << logIDGen()
        << work.getSubmissionTime()
        << 0
        << transmitter_id
        << command
        << code
        << sequence
        << string("-")
        << CtiVerificationBase::getCodeStatusName(CtiVerificationBase::CodeStatus_Sent);

    if( ! inserter.execute()  )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - error while inserting in CtiPorterVerification::writeWorkRecord **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    for( r_itr = receipts.begin(); r_itr != receipts.end(); r_itr++ )
    {
        inserter
            << logIDGen()
            << r_itr->second
            << r_itr->first
            << transmitter_id
            << command
            << code
            << sequence
            << string("Y")
            << code_status;

        if( ! inserter.execute() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - error while inserting in CtiPorterVerification::writeWorkRecord **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    for( e_itr = expectations.begin(); e_itr != expectations.end(); e_itr++ )
    {
        inserter
            << logIDGen()
            << now
            << *e_itr
            << transmitter_id
            << command
            << code
            << sequence
            << string("N")
            << code_status;

        if( ! inserter.execute() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - error while inserting in CtiPorterVerification::writeWorkRecord **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}


void CtiPorterVerification::writeUnknown(const CtiVerificationReport &vReport)
{
    static const std::string sql = "insert into " + getTableName() +
                                   " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    const string &cs = CtiVerificationBase::getCodeStatusName(CtiVerificationBase::CodeStatus_Unexpected);
    string code = vReport.getCode();

    if( code.empty() )
    {
        code = "(empty)";
    }

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter
        << logIDGen()
        << vReport.getReceiptTime()
        << vReport.getReceiverID()
        << 0  //  unknown transmitter ID
        << string("-")
        << code
        << -1
        << string("Y")
        << cs;

    if( isDebugLudicrous() )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << inserter.asString() << endl;
    }

    if( ! inserter.execute() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - error while inserting in CtiPorterVerification::writeUnknown **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


void CtiPorterVerification::pruneEntries(const ptime::time_duration_type &age)
{
    static const string sql = "delete from " + getTableName() + " where timearrival < ?";

    unsigned long earliest = ::time(0) - age.total_seconds();

    CtiDate prune_time = CtiDate( CtiTime(earliest) );
    CtiDate prune_date = CtiDate( prune_time.day(), prune_time.year() );

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << CtiTime(prune_date);

    if(isDebugLudicrous())
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Beginning DynamicVerification prune. " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << CtiTime() << deleter.asString() << endl;
    }

    if( deleter.execute() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " DynamicVerification pruning successful (" << CtiTime(earliest) << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - error while pruning entries in CtiPorterVerification::pruneEntries **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    {
        DatabaseConnection conn;
        DatabaseReader rdr(conn, sql);
        rdr.execute();

        if(rdr() && rdr.isValid())
        {
            rdr >> tempid;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - invalid reader in CtiPorterVerification::logIDGen **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if(tempid >= id)
        {
            id = tempid;
        }

        init_id = TRUE;
    }   // Temporary results are destroyed to free the connection

    return(++id);
}

long CtiPorterVerification::report() const
{
    long cnt = _work_queue.size() + _input.entries();
    return cnt;
}


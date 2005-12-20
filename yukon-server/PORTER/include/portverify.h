/*---------------------------------------------------------------------------------*
*
* File:   portverify
*
* Class:  CtiPorterVerification
* Date:   4/14/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/12/20 17:19:25 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __PORTVERIFY_H__
#define __PORTVERIFY_H__

#include <map>
#include <queue>
using std::map;
using std::queue;
using std::multimap;
using std::less;
using std::priority_queue;


#include "queue.h"
#include "thread.h"
#include "verification_objects.h"

class CtiPorterVerification : public CtiThread
{
private:

    //  the input queue
    CtiQueue< CtiVerificationBase, less< CtiVerificationBase > > _input;

    //  the structures associating verification receivers with transmitters
    struct association
    {
        long receiver_id;
        bool retransmit;
    };

    typedef std::multimap< long, association >  association_map;
    typedef association_map::iterator      association_itr;

    association_map _associations;

    unsigned long _sequence;

    typedef std::deque< CtiVerificationWork * > pending_queue;  //  this could also be made to a map if iterating is too slow
    typedef std::map< long, pending_queue >     receiver_map;    //  maps receivers to their work queues

    typedef pending_queue::iterator        pending_itr;
    typedef receiver_map::iterator         receiver_itr;

    receiver_map _receiver_work;

    priority_queue< CtiVerificationWork *, pending_queue, CtiVerificationWork::later > _work_queue;

    static const string _table_name;

    void verificationThread(void);
    void loadAssociations(void);
    void processWorkQueue(bool purge=false);
    void writeWorkRecord(const CtiVerificationWork &work, RWDBConnection &conn, RWDBStatus &dbstat);
    void writeUnknown(const CtiVerificationReport &report);
    void pruneEntries(const ptime::time_duration_type &earliest);

    long logIDGen(bool force=false);

    //  yeah
    CtiPorterVerification(const CtiPorterVerification& aRef);

protected:

public:

    CtiPorterVerification();
    virtual ~CtiPorterVerification();

    enum
    {
        RELOAD = CtiThread::LAST
    };

    static const string &getTableName();

    void push(CtiVerificationBase *e);                    //  the objects are consumed when submitted to the thread
    void push(queue< CtiVerificationBase * > &entries);   //

    void run();

    void report();
};

#endif // #ifndef __PORTVERIFY_H__

#ifndef __MGR_FDRPOINT_H__
#define __MGR_FDRPOINT_H__

/*****************************************************************************
*
*    FILE NAME: mgr_fdrpoint.h
*
*    DATE: 09/18/2000
*
*    AUTHOR: Ben Wallace
*
*    PURPOSE: class head for CtiFDRManager
*
*    DESCRIPTION: Manages a collection of FDRPoints which are points
*                 that are sent or received from other systems.  Translation
*                 information is also included in our for id or renaming.
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/


/** include files **/
#include <rw/db/connect.h>

#include "dlldefs.h"
#include "smartmap.h"
#include "fdrpoint.h"
#include "logger.h"

#include <boost/shared_ptr.hpp>

using boost::shared_ptr;

class IM_EX_FDRBASE CtiFDRManager
{
    
    public:
        // constructors, destructor
        CtiFDRManager(string & InterfaceName);
        CtiFDRManager(string & InterfaceName, string & aWhereSelectStr);
    
        virtual ~CtiFDRManager();

        typedef CtiSmartMap<CtiFDRPoint> FdrPointMap;
        typedef CtiSmartMap<CtiFDRPoint>::coll_type coll_type;
        typedef CtiSmartMap<CtiFDRPoint>::ptr_type ptr_type;
        typedef CtiSmartMap<CtiFDRPoint>::spiterator spiterator;
        typedef CtiSmartMap<CtiFDRPoint>::reader_lock_guard_t readerLock;
        typedef CtiSmartMap<CtiFDRPoint>::writer_lock_guard_t writerLock;
        typedef CtiSmartMap<CtiFDRPoint>::lock_t lock;

        coll_type & getMap();
        lock & getLock();
        ptr_type find(bool (*testFun)(ptr_type&, void*),void* d);

        string       getInterfaceName();
        CtiFDRManager & setInterfaceName(string &);

        string       getWhereSelectStr();
        CtiFDRManager & setWhereSelectStr(string &);

        RWDBStatus loadPointList(void);
        RWDBStatus loadPoint(long pointId);
        RWDBStatus refreshPointList(void);
    
        CtiFDRPointSPtr findFDRPointID(LONG myPointId);
        CtiFDRPointSPtr removeFDRPointID(long myPointId);
        bool addFDRPointId(long myPointId);

        void printIds(CtiLogger& dout);

        //Encapsulated functions:
        size_t entries();
    protected:
        static const CHAR * TBLNAME_FDRTRANSLATION;
        static const CHAR * COLNAME_FDRTRANSLATION;
        static const CHAR * COLNAME_FDRDESTINATION;
        static const CHAR * COLNAME_FDR_POINTID;
        static const CHAR * COLNAME_FDRINTERFACETYPE;
        static const CHAR * COLNAME_FDRDIRECTIONTYPE;

        static const CHAR * TBLNAME_PTANALOG;
        static const CHAR * COLNAME_PTANALOG_MULT;
        static const CHAR * COLNAME_PTANALOG_OFFSET;
        static const CHAR * COLNAME_PTANALOG_POINTID;

        static const CHAR * TBLNAME_PTBASE;
        static const CHAR * COLNAME_PTBASE_POINTTYPE;
        static const CHAR * COLNAME_PTBASE_POINTID;


    private:
        RWDBStatus getPointsFromDB(RWDBSelector& selector, std::map<long,CtiFDRPointSPtr >& fdrPtrMap);
        void buildFDRPointSelector(RWDBDatabase& db, RWDBSelector& selector, RWDBTable& fdrTranslation, RWDBTable& pointBaseTable, RWDBTable& pointAnalogTable);

        // private data
        string   iInterfaceName;
        string   iWhereSelectStr;
        CtiSmartMap<CtiFDRPoint> pointMap;
    
};

#endif                  // #ifndef __MGR_FDRPOINT_H__

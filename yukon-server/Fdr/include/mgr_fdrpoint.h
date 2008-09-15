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
#include "rtdbSmartPointer.h"
#include "fdrpoint.h"
#include "logger.h"

#include <boost/shared_ptr.hpp>

using boost::shared_ptr;

class IM_EX_FDRBASE CtiFDRManager : public CtiRTDBSmartPointer< shared_ptr<CtiFDRPoint> >
{
    
    public:
        // constructors, destructor
        CtiFDRManager(string & InterfaceName);
        CtiFDRManager(string & InterfaceName, string & aWhereSelectStr);
    
        virtual ~CtiFDRManager();
    
        // change to friendly name for Iterator
        typedef MapIterator CTIFdrPointIterator;


        string       getInterfaceName();
        CtiFDRManager & setInterfaceName(string &);

        string       getWhereSelectStr();
        CtiFDRManager & setWhereSelectStr(string &);
       
        void DeleteList(void)   
        {
            Map.clear();
        }

        RWDBStatus loadPointList(void);
        RWDBStatus loadPoint(long pointId);
        RWDBStatus refreshPointList(void);
    
        shared_ptr<CtiFDRPoint> findFDRPointID(LONG myPointId);
        shared_ptr<CtiFDRPoint> removeFDRPointID(long myPointId);
        bool addFDRPointId(long myPointId);

        void printIds(CtiLogger& dout);

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
        RWDBStatus getPointsFromDB(RWDBConnection& conn, RWDBSelector& selector, 
                                                  std::map<long,boost::shared_ptr<CtiFDRPoint> >& fdrPtrMap);

        // private data
        string   iInterfaceName;
        string   iWhereSelectStr;

    
};

#endif                  // #ifndef __MGR_FDRPOINT_H__

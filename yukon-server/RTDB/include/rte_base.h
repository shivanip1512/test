/*-----------------------------------------------------------------------------*
*
* File:   rte_base
*
* Class:  CtiRouteBase
* Date:   9/30/1999
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/rte_base.h-arc  $
* REVISION     :  $Revision: 1.16.2.1 $
* DATE         :  $Date: 2008/11/18 20:11:29 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __RTE_BASE_H__
#define __RTE_BASE_H__

#include <boost/shared_ptr.hpp>
#include "boostutil.h"
using boost::shared_ptr;

#include <rw/db/dbase.h>
#include <rw/db/table.h>

#include "dsm2.h"
#include "dbmemobject.h"
#include "cmdparse.h"
#include "ctibase.h"
#include "message.h"
#include "tbl_pao_lite.h"
#include "tbl_rtcomm.h"
#include "msg_signal.h"
#include "yukon.h"
#include <list>

using std::list;


class CtiRequestMsg;    // Use forward declaration #include "msg_pcrequest.h"
class CtiReturnMsg;

class IM_EX_DEVDB CtiRouteBase : public CtiMemDBObject
{
protected:

    CtiTblPAOLite _tblPAO;
    CtiTableCommRoute _tblComm;

private:
    mutable CtiMutex _classMutex;

public:
    typedef CtiMemDBObject Inherited;

    CtiRouteBase() {}

    CtiRouteBase(const CtiRouteBase& aRef)
    {
        *this = aRef;
    }

    virtual ~CtiRouteBase() {}

    CtiRouteBase& operator=(const CtiRouteBase& aRef)
    {
        if(this != &aRef)
        {
            Inherited::operator=(aRef);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " FIX FIX FIX  **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

        }
        return *this;
    }

    /*
     *  Some nice little virtual functinos to make me like life...
     */
    virtual void DumpData()
    {
        _tblPAO.DumpData();
        _tblComm.DumpData();

    }

    virtual LONG getTrxDeviceID() const                { return -1L;}
    virtual INT  getBus() const                        { return 0;}
    virtual INT  getCCUFixBits() const                 { return 0;}
    virtual INT  getCCUVarBits() const                 { return 0;}
    virtual INT  getStages() const                     { return 0;}

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const
    {
        _tblPAO.getSQL(db, keyTable, selector);
        CtiTableCommRoute::getSQL(db, keyTable, selector);
    }

    virtual void DecodeDatabaseReader(RWDBReader &rdr)
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);
        _tblPAO.DecodeDatabaseReader(rdr);
        _tblComm.DecodeDatabaseReader(rdr);
    }

    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
    {
        return NoExecuteRequestMethod;
    }


    CtiTableCommRoute& getCommRoute()
    {
        return _tblComm;
    }

    virtual bool isDefaultRoute() const
    {
        return _tblComm.getDefaultRoute();
    }


    LONG getRouteID() const;
    string getName() const;
    INT getType() const;

    virtual bool processAdditionalRoutes( INMESS *InMessage ) const;

};

inline LONG CtiRouteBase::getRouteID() const { return _tblPAO.getID(); }
inline string CtiRouteBase::getName() const { return _tblPAO.getName(); }
inline INT CtiRouteBase::getType() const { return _tblPAO.getType(); }
inline bool CtiRouteBase::processAdditionalRoutes( INMESS *InMessage ) const { return false; }


typedef CtiRouteBase CtiRoute;


typedef shared_ptr< CtiRouteBase > CtiRouteSPtr;


#endif // #ifndef __RTE_BASE_H__

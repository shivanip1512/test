
#ifndef __RTE_BASE_H__
#define __RTE_BASE_H__

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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:57 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <rw/db/dbase.h>
#include <rw/db/table.h>

#include <rw/tpslist.h>

#include "dsm2.h"
#include "dbmemobject.h"
#include "cmdparse.h"
#include "ctibase.h"
#include "message.h"
#include "tbl_pao.h"
#include "tbl_rtcomm.h"
#include "yukon.h"

class CtiRequestMsg;    // Use forward declaration #include "msg_pcrequest.h"
class CtiReturnMsg;
class CtiSignalMsg;

class IM_EX_DEVDB CtiRouteBase : public CtiMemDBObject, public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
protected:

    CtiTblPAO _tblPAO;
    CtiTableCommRoute _tblComm;

private:

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
                dout << RWTime() << " FIX FIX FIX  **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
    {
        CtiTblPAO::getSQL(db, keyTable, selector);
        CtiTableCommRoute::getSQL(db, keyTable, selector);
    }

    virtual void DecodeDatabaseReader(RWDBReader &rdr)
    {
        LockGuard gd(monitor());
        _tblPAO.DecodeDatabaseReader(rdr);
        _tblComm.DecodeDatabaseReader(rdr);
    }

    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
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
    RWCString getName() const;
    INT getType() const;

    virtual bool processAdditionalRoutes( INMESS *InMessage ) const;

};

inline LONG CtiRouteBase::getRouteID() const { return _tblPAO.getID(); }
inline RWCString CtiRouteBase::getName() const { return _tblPAO.getName(); }
inline INT CtiRouteBase::getType() const { return _tblPAO.getType(); }
inline bool CtiRouteBase::processAdditionalRoutes( INMESS *InMessage ) const { return false; }


typedef CtiRouteBase CtiRoute;

#endif // #ifndef __RTE_BASE_H__

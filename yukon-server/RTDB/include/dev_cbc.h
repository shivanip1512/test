/*-----------------------------------------------------------------------------*
*
* File:   dev_cbc
*
* Class:  CtiDeviceCBC
* Date:   8/24/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_cbc.h-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2008/10/28 19:21:43 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_CBC_H__
#define __DEV_CBC_H__
#pragma warning( disable : 4786)


#include "dev_base.h"
#include "tbl_dv_cbc.h"       // TYPEVERSACOMCBC, TYPEFISHERPCBC

class IM_EX_DEVDB CtiDeviceCBC : public CtiDeviceBase
{
private:

    typedef CtiDeviceBase Inherited;

protected:

    CtiTableDeviceCBC _cbc;
    static int        _cbcTries;

public:

    CtiDeviceCBC();
    CtiDeviceCBC(const CtiDeviceCBC& aRef);
    virtual ~CtiDeviceCBC();

    CtiDeviceCBC& operator=(const CtiDeviceCBC& aRef);
    CtiTableDeviceCBC   getCBC() const;
    CtiTableDeviceCBC&  getCBC();
    CtiDeviceCBC&     setCBC(const CtiTableDeviceCBC& aRef);

    int getCBCRetries(void);

    virtual LONG getRouteID();
    virtual string getDescription(const CtiCommandParser & parse) const;
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    virtual INT ExecuteRequest(CtiRequestMsg                  *pReq,
                               CtiCommandParser               &parse,
                               OUTMESS                        *&OutMessage,
                               list< CtiMessage* >      &vgList,
                               list< CtiMessage* >      &retList,
                               list< OUTMESS* >         &outList);


    INT executeFisherPierceCBC(CtiRequestMsg                  *pReq,
                               CtiCommandParser               &parse,
                               OUTMESS                        *&OutMessage,
                               list< CtiMessage* >      &vgList,
                               list< CtiMessage* >      &retList,
                               list< OUTMESS* >         &outList);

    INT executeVersacomCBC(CtiRequestMsg                  *pReq,
                           CtiCommandParser               &parse,
                           OUTMESS                        *&OutMessage,
                           list< CtiMessage* >      &vgList,
                           list< CtiMessage* >      &retList,
                           list< OUTMESS* >         &outList);

    INT executeExpresscomCBC(CtiRequestMsg                  *pReq,
                             CtiCommandParser               &parse,
                             OUTMESS                        *&OutMessage,
                             list< CtiMessage* >      &vgList,
                             list< CtiMessage* >      &retList,
                             list< OUTMESS* >         &outList);


};


#endif // #ifndef __DEV_CBC_H__

#pragma once

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

    virtual std::string getSQLCoreStatement() const;

    virtual LONG getRouteID();
    virtual std::string getDescription(const CtiCommandParser & parse) const;
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual INT ExecuteRequest(CtiRequestMsg                  *pReq,
                               CtiCommandParser               &parse,
                               OUTMESS                        *&OutMessage,
                               std::list< CtiMessage* >      &vgList,
                               std::list< CtiMessage* >      &retList,
                               std::list< OUTMESS* >         &outList);


    INT executeFisherPierceCBC(CtiRequestMsg                  *pReq,
                               CtiCommandParser               &parse,
                               OUTMESS                        *&OutMessage,
                               std::list< CtiMessage* >      &vgList,
                               std::list< CtiMessage* >      &retList,
                               std::list< OUTMESS* >         &outList);

    INT executeVersacomCBC(CtiRequestMsg                  *pReq,
                           CtiCommandParser               &parse,
                           OUTMESS                        *&OutMessage,
                           std::list< CtiMessage* >      &vgList,
                           std::list< CtiMessage* >      &retList,
                           std::list< OUTMESS* >         &outList);

    INT executeExpresscomCBC(CtiRequestMsg                  *pReq,
                             CtiCommandParser               &parse,
                             OUTMESS                        *&OutMessage,
                             std::list< CtiMessage* >      &vgList,
                             std::list< CtiMessage* >      &retList,
                             std::list< OUTMESS* >         &outList);


};

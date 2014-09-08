#pragma once

#include "dev_base.h"
#include "tbl_dv_cbc.h"       // TYPEVERSACOMCBC, TYPEFISHERPCBC

class IM_EX_DEVDB CtiDeviceCBC : public CtiDeviceBase
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceCBC(const CtiDeviceCBC&);
    CtiDeviceCBC& operator=(const CtiDeviceCBC&);

    typedef CtiDeviceBase Inherited;

protected:

    CtiTableDeviceCBC _cbc;
    static int        _cbcTries;

public:

    CtiDeviceCBC();

    const CtiTableDeviceCBC& getCBC() const;

    int getCBCRetries(void);

    virtual std::string getSQLCoreStatement() const;

    virtual LONG getRouteID();
    virtual std::string getDescription(const CtiCommandParser & parse) const;
    void DecodeDatabaseReader(Cti::RowReader &rdr) override;

    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    INT executeFisherPierceCBC(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    INT executeVersacomCBC    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    INT executeExpresscomCBC  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);


};

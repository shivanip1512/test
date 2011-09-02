#pragma once

#include "dev_grp.h"
#include "tbl_dv_lmgmct.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"

class IM_EX_DEVDB CtiDeviceGroupMCT : public CtiDeviceGroupBase
{

private:

    typedef CtiDeviceGroupBase Inherited;

    CtiTableLMGroupMCT _lmGroupMCT;

protected:

    enum Commands
    {
        Command_Restore  = 0x00,

        Command_Shed_07m = 0x00,
        Command_Shed_15m = 0x10,
        Command_Shed_30m = 0x20,
        Command_Shed_60m = 0x30,
    };

    enum BaseAddresses
    {
        BaseAddress_LeadLoad  = 4182016,  //  3FD000
        BaseAddress_LeadMeter = 4186112,  //  3FE000
        BaseAddress_Bronze    = 4190208,  //  3FF000
    };

public:

    CtiDeviceGroupMCT();
    CtiDeviceGroupMCT( const CtiDeviceGroupMCT &aRef );
    virtual ~CtiDeviceGroupMCT();

    CtiDeviceGroupMCT& operator=( const CtiDeviceGroupMCT &aRef );

    virtual std::string getSQLCoreStatement() const;

    virtual LONG getRouteID();
    virtual LONG getAddress() const;
    virtual std::string getDescription( const CtiCommandParser &parse ) const;
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual INT ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );
    virtual INT executeControl( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );

};

#pragma once

#include "dev_remote.h"
#include "prot_modbus.h"
#include "tbl_dv_address.h"

#include <map>
#include <string>

namespace Cti {
namespace Devices {

class IM_EX_DEVDB ModbusDevice : public CtiDeviceRemote
{
    typedef CtiDeviceRemote Inherited;

    struct pseudo_info
    {
        bool is_pseudo;
        int  pointid;
        int  state;
    };

    struct outmess_header
    {
        Protocols::ModbusProtocol::Command      command;
        Protocols::ModbusProtocol::output_point parameter;
        pseudo_info                          pseudo_info;
    };

    struct info_struct
    {
        Protocols::ModbusProtocol::Command      protocol_command;
        Protocols::ModbusProtocol::output_point protocol_parameter;
        pseudo_info pseudo_info;
        std::string      user;
    };

    info_struct _porter_info;
    info_struct _pil_info;

protected:

    Protocols::ModbusProtocol _modbus;
    CtiTableDeviceAddress  _modbus_address;

    std::vector<std::string> _pointdata_strings;
    Protocols::Interface::pointlist_t  _point_results;

    Protocols::Interface *getProtocol() override;

    void processPoints( Protocols::Interface::pointlist_t &points );

public:

    ModbusDevice();

    virtual std::string getSQLCoreStatement() const;

    virtual std::string getDescription(const CtiCommandParser & parse) const;
    void DecodeDatabaseReader(Cti::RowReader &rdr) override;

    YukonError_t sendCommRequest( OUTMESS *&OutMessage, OutMessageList &outList );  //  not override or virtual
    YukonError_t recvCommRequest( OUTMESS *OutMessage ) override;

    virtual YukonError_t generate(CtiXfer &xfer);
    virtual YukonError_t decode  (CtiXfer &xfer, YukonError_t status);
    void sendDispatchResults(CtiConnection &vg_connection);
    YukonError_t sendCommResult(INMESS &InMessage) override;

    //  virtual in case devices need to form up different Modbus requests for the same command ("control open", for example)
    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    YukonError_t IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
    YukonError_t GeneralScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;

    YukonError_t ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    YukonError_t ErrorDecode (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList);
};

}
}

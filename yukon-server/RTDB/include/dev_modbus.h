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
private:

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

    Protocol::Interface::stringlist_t _string_results;
    Protocol::Interface::pointlist_t  _point_results;

    virtual Protocol::Interface *getProtocol();

    virtual void processPoints( Protocol::Interface::pointlist_t &points );

public:

    ModbusDevice(void);
    ModbusDevice(const ModbusDevice& aRef);
    virtual ~ModbusDevice();

    ModbusDevice& operator=(const ModbusDevice& aRef);

    virtual std::string getDescription(const CtiCommandParser & parse) const;
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    int sendCommRequest( OUTMESS *&OutMessage, std::list< OUTMESS* > &outList );
    int recvCommRequest( OUTMESS *OutMessage );

    virtual int generate(CtiXfer &xfer);
    virtual int decode(CtiXfer &xfer, int status);
    void sendDispatchResults(CtiConnection &vg_connection);
    int  sendCommResult(INMESS *InMessage);

    //  virtual in case devices need to form up different Modbus requests for the same command ("control open", for example)
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    virtual INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    INT ErrorDecode (const INMESS &InMessage, const CtiTime TimeNow, std::list< CtiMessage* > &retList);
};

}
}

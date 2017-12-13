#pragma once

#include "dev_remote.h"
#include "prot_dnp.h"
#include "tbl_dv_address.h"

#include <map>
#include <string>
#include <memory>

class CtiPointAccumulator;
using CtiPointAccumulatorSPtr = boost::shared_ptr<CtiPointAccumulator>;

namespace Cti {
namespace Devices {

class IM_EX_DEVDB DnpDevice : public CtiDeviceRemote
{
    typedef CtiDeviceRemote Inherited;

    struct dnp_accumulator_pointdata
    {
        double point_value;
        ctitime_t point_time;
    };

    struct pseudo_info
    {
        bool is_pseudo;
        int  pointid;
        int  state;
    };

    struct outmess_header
    {
        Protocols::DnpProtocol::Command      command;
        Protocols::DnpProtocol::output_point parameter;
        pseudo_info                          pseudo_info;
        //  we really only use one outbound point at the moment...  otherwise we'd need a parameter count
        //    for passing multiple parameters around, etc
    };

    struct info_struct
    {
        Protocols::DnpProtocol::Command      protocol_command;
        Protocols::DnpProtocol::output_point protocol_parameter;
        pseudo_info pseudo_info;
        std::string      user;
    };

    info_struct _porter_info;
    info_struct _pil_info;  //  only used for the call to sendCommRequest(), unreliable after - DO NOT USE FOR DECODES

    using dnp_accumulator_pointdata_map = std::map<long, dnp_accumulator_pointdata>;

    dnp_accumulator_pointdata_map _lastIntervalAccumulatorData;

    std::unique_ptr<CtiPointDataMsg> calculateDemandAccumulator(const CtiPointAccumulatorSPtr& pt, dnp_accumulator_pointdata current);

protected:

    Protocols::DnpProtocol _dnp;
    CtiTableDeviceAddress  _dnp_address;

    void resetDNPScansPending( void );

    Protocols::Interface *getProtocol() override;

    using PointsByType = std::map<CtiPointType_t, std::map<int, CtiPointSPtr>>;

    virtual PointsByType getDevicePointsByType() const;
    virtual void processPoints( Protocols::Interface::pointlist_t &points );

    void loadConfigData();

    bool isConfigurationValueTrue(const std::string &configKey) const;

public:

    DnpDevice();

    virtual std::string getSQLCoreStatement() const;

    virtual std::string getDescription(const CtiCommandParser & parse) const;
    void DecodeDatabaseReader(Cti::RowReader &rdr) override;

    LONG getAddress() const;
    LONG getMasterAddress() const;

    YukonError_t sendCommRequest( OUTMESS *&OutMessage, OutMessageList &outList );
    YukonError_t recvCommRequest( OUTMESS *OutMessage ) override;

    void initUnsolicited();

    void sendDispatchResults(CtiConnection &vg_connection);
    YukonError_t sendCommResult(INMESS &InMessage) override;

    //  virtual in case devices need to form up different DNP requests for the same command ("control open", for example)
    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    bool clearedForScan( const CtiScanRate_t scantype ) override;

    YukonError_t IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
    YukonError_t GeneralScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;

    YukonError_t ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    YukonError_t ErrorDecode (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList);
};

}
}


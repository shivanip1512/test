#pragma once

#include "dev_remote.h"
#include "prot_dnp.h"
#include "tbl_dv_address.h"

#include <map>
#include <string>

namespace Cti {
namespace Devices {

class IM_EX_DEVDB DnpDevice : public CtiDeviceRemote
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    DnpDevice(const DnpDevice&);
    DnpDevice& operator=(const DnpDevice&);

    typedef CtiDeviceRemote Inherited;

    struct dnp_accumulator_pointdata
    {
        unsigned long point_value;
        unsigned long point_time;
    };

    struct pseudo_info
    {
        bool is_pseudo;
        int  pointid;
        int  state;
    };

    struct outmess_header
    {
        Protocol::DNPInterface::Command      command;
        Protocol::DNPInterface::output_point parameter;
        pseudo_info                          pseudo_info;
        //  we really only use one outbound point at the moment...  otherwise we'd need a parameter count
        //    for passing multiple parameters around, etc
    };

    struct info_struct
    {
        Protocol::DNPInterface::Command      protocol_command;
        Protocol::DNPInterface::output_point protocol_parameter;
        pseudo_info pseudo_info;
        std::string      user;
    };

    info_struct _porter_info;
    info_struct _pil_info;  //  only used for the call to sendCommRequest(), unreliable after - DO NOT USE FOR DECODES

    typedef std::map< long, dnp_accumulator_pointdata > dnp_accumulator_pointdata_map;

    dnp_accumulator_pointdata_map _lastIntervalAccumulatorData;

protected:

    Protocol::DNPInterface _dnp;
    CtiTableDeviceAddress  _dnp_address;

    void resetDNPScansPending( void );

    virtual Protocol::Interface *getProtocol();

    virtual void processPoints( Protocol::Interface::pointlist_t &points );

    void loadConfigData();

    bool isConfigurationValueTrue(const std::string &configKey) const;

public:

    DnpDevice();
    virtual ~DnpDevice();

    virtual std::string getSQLCoreStatement() const;

    virtual std::string getDescription(const CtiCommandParser & parse) const;
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    LONG getAddress() const;
    LONG getMasterAddress() const;

    int sendCommRequest( OUTMESS *&OutMessage, std::list< OUTMESS* > &outList );
    int recvCommRequest( OUTMESS *OutMessage );

    void initUnsolicited();

    void sendDispatchResults(CtiConnection &vg_connection);
    int  sendCommResult(INMESS *InMessage);

    //  virtual in case devices need to form up different DNP requests for the same command ("control open", for example)
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    virtual bool clearedForScan( int scantype );

    virtual INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(const INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    INT ErrorDecode (const INMESS &InMessage, const CtiTime TimeNow, std::list< CtiMessage* > &retList);
};

}
}


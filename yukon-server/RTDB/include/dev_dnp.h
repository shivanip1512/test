/*-----------------------------------------------------------------------------*
*
* File:   dev_dnp
*
* Class:  CtiDeviceDNP
* Date:   8/05/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_cbc.h-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2005/03/10 20:57:22 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_DNP_H__
#define __DEV_DNP_H__
#pragma warning( disable : 4786)


#include "dev_remote.h"
#include "prot_dnp.h"
#include "tbl_dv_address.h"

#include <map>
#include <string>

namespace Cti       {
namespace Device    {

class IM_EX_DEVDB DNP : public CtiDeviceRemote
{
private:

    struct dnp_accumulator_pointdata
    {
        unsigned long point_value;
        unsigned long point_time;
    };

    struct inmess_header
    {
        unsigned return_string_length;
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
        //  we really only use one outbount point at the moment...  otherwise we'd need a parameter count
        //    for passing multiple parameters around, etc
    };

    struct info_struct
    {
        Protocol::DNPInterface::Command      protocol_command;
        Protocol::DNPInterface::output_point protocol_parameter;
        pseudo_info pseudo_info;
        string      user;
    };

    info_struct _porter_info;
    info_struct _pil_info;

    typedef map< long, dnp_accumulator_pointdata > dnp_accumulator_pointdata_map;

    dnp_accumulator_pointdata_map _lastIntervalAccumulatorData;

    bool _scanGeneralPending, _scanIntegrityPending, _scanAccumulatorPending;

protected:

    Protocol::DNPInterface _dnp;
    CtiTableDeviceAddress  _dnp_address;

    queue< string * > _results;

    void setDNPScanPending( int scantype, bool pending );
    void resetDNPScansPending( void );

    virtual Protocol::Interface *getProtocol();

public:

    typedef CtiDeviceRemote Inherited;

    DNP();
    DNP(const DNP& aRef);
    virtual ~DNP();

    DNP& operator=(const DNP& aRef);

    virtual RWCString getDescription(const CtiCommandParser & parse) const;
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    int sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    int recvCommRequest( OUTMESS *OutMessage );

    virtual int generate(CtiXfer &xfer);
    virtual int decode(CtiXfer &xfer, int status);
    void sendDispatchResults(CtiConnection &vg_connection);
    int  sendCommResult(INMESS *InMessage);

    //  virtual in case devices need to form up different DNP requests for the same command ("control open", for example)
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

    virtual bool clearedForScan( int scantype );
    virtual void resetForScan  ( int scantype );

    virtual INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage> &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    INT ErrorDecode (INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage> &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    //virtual void processInboundPoints(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, RWTPtrSlist<CtiPointDataMsg> &dnpPoints );
};

}
}

#endif // #ifndef __DEV_CBC_H__

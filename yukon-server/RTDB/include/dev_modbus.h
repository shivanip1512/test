/*-----------------------------------------------------------------------------*
*
* File:   dev_dnp
*
* Class:  CtiDeviceDNP
* Date:   7/18/2005
*
* Author: Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_cbc.h-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MODBUS_H__
#define __DEV_MODBUS_H__
#pragma warning( disable : 4786)


#include "dev_remote.h"
#include "prot_modbus.h"
#include "tbl_dv_address.h"

#include <map>
#include <string>

namespace Cti       {
namespace Device    {

class IM_EX_DEVDB Modbus : public CtiDeviceRemote
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
        Protocol::Modbus::Command      command;
        Protocol::Modbus::output_point parameter;
        pseudo_info                          pseudo_info;
    };

    struct info_struct
    {
        Protocol::Modbus::Command      protocol_command;
        Protocol::Modbus::output_point protocol_parameter;
        pseudo_info pseudo_info;
        string      user;
    };

    info_struct _porter_info;
    info_struct _pil_info;

protected:

    Protocol::Modbus _modbus;
    CtiTableDeviceAddress  _modbus_address;

    Protocol::Interface::stringlist_t _string_results;
    Protocol::Interface::pointlist_t  _point_results;

    virtual Protocol::Interface *getProtocol();

    virtual void processPoints( Protocol::Interface::pointlist_t &points );

public:

    Modbus(void);
    Modbus(const Modbus& aRef);
    virtual ~Modbus();

    Modbus& operator=(const Modbus& aRef);

    virtual string getDescription(const CtiCommandParser & parse) const;
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    int sendCommRequest( OUTMESS *&OutMessage, list< OUTMESS* > &outList );
    int recvCommRequest( OUTMESS *OutMessage );

    virtual int generate(CtiXfer &xfer);
    virtual int decode(CtiXfer &xfer, int status);
    void sendDispatchResults(CtiConnection &vg_connection);
    int  sendCommResult(INMESS *InMessage);

    //  virtual in case devices need to form up different Modbus requests for the same command ("control open", for example)
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    virtual INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    INT ErrorDecode (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, bool &overrideExpectMore);
    //virtual void processInboundPoints(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, RWTPtrSlist<CtiPointDataMsg> &dnpPoints );
};

}
}

#endif // #ifndef __DEV_MODBUS_H__

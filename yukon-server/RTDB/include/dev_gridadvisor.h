/*-----------------------------------------------------------------------------*
 *
 * File:   dev_gridadvisor.h
 *
 * Class:  CtiDeviceGridAdvisor
 * Date:   08/07/2007
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#ifndef __DEV_GRIDADVISOR_H__
#define __DEV_GRIDADVISOR_H__
#pragma warning( disable : 4786 )

#include <windows.h>
#include "dev_single.h"
#include "tbl_dv_address.h"
#include "tbl_direct.h"

class IM_EX_DEVDB CtiDeviceGridAdvisor : public CtiDeviceSingle
{
private:

    typedef CtiDeviceSingle Inherited;

    CtiTableDeviceAddress    _address;
    CtiTableDeviceDirectComm _commport;

protected:

public:

    enum FCIPoints
    {
        FCI_Status_Fault = 1,
        FCI_Status_CurrentSurveyEnabled = 101,
        FCI_Status_UDPAck               = 102,

        FCI_Analog_NominalAmps    =   1,
        FCI_Analog_PeakAmps       =   2,
        FCI_Analog_Phase          =   3,
        FCI_Analog_Temperature    =   4,
        FCI_Analog_BatteryVoltage =   5,
        FCI_Analog_Latitude       =  11,
        FCI_Analog_Longitude      =  12,
        FCI_Analog_FaultHistory   =  21,
        FCI_Analog_CurrentSurvey  =  31,
        FCI_Analog_UDPRepeats     = 101,
    };

    CtiDeviceGridAdvisor();
    CtiDeviceGridAdvisor(const CtiDeviceGridAdvisor& aRef);

    virtual ~CtiDeviceGridAdvisor();

    CtiDeviceGridAdvisor& operator=(const CtiDeviceGridAdvisor& aRef);

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    //  virtual in case different GridAdvisor devices need to form up alternate requests for the same command
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    virtual INT AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
};

#endif //  #ifndef __DEV_GRIDADVISOR_H__


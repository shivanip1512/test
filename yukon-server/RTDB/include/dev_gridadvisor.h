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


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include "dev_dnp.h"
#include "tbl_dv_address.h"
#include "tbl_direct.h"

// THIS DEVICE SHOULD INHERIT FROM CTIDEVICEREMOTE
class IM_EX_DEVDB CtiDeviceGridAdvisor : public Cti::Device::DNP
{
private:

    typedef CtiDeviceSingle Inherited;

    CtiTableDeviceAddress    _address;
    CtiTableDeviceDirectComm _commport;

protected:

public:

    enum FCIPoints
    {
        FCI_Status_Fault      = 1,
        FCI_Status_NoPower    = 2,
        FCI_Status_BatteryLow = 3,      // per YUK-4101
        FCI_Status_Momentary  = 4,      //
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

        FCI_Accum_MomentaryCount =   1,
    };

    enum CBNMPoints
    {
        //CBNM_Status_Fault      = 1,
        CBNM_Status_NoPower    = 2,
        CBNM_Status_BatteryLow = 3,      // per YUK-4101
        CBNM_Status_Momentary  = 4,      //
        CBNM_Status_CurrentSurveyEnabled = 101,
        CBNM_Status_UDPAck               = 102,

        CBNM_Analog_NominalAmps    =   1,
        CBNM_Analog_PeakAmps       =   2,
        CBNM_Analog_Phase          =   3,
        CBNM_Analog_Temperature    =   4,
        CBNM_Analog_BatteryVoltage =   5,
        CBNM_Analog_Latitude       =  11,
        CBNM_Analog_Longitude      =  12,
        CBNM_Analog_CurrentSurvey  =  31,
        CBNM_Analog_UDPRepeats     = 101,

        CBNM_Accum_MomentaryCount =   1,
    };

    CtiDeviceGridAdvisor();
    CtiDeviceGridAdvisor(const CtiDeviceGridAdvisor& aRef);

    virtual ~CtiDeviceGridAdvisor();

    CtiDeviceGridAdvisor& operator=(const CtiDeviceGridAdvisor& aRef);

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    //  virtual in case different GridAdvisor devices need to form up alternate requests for the same command
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    virtual INT AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    virtual LONG getPortID() const;
    virtual LONG getAddress() const;
    virtual LONG getMasterAddress() const;
};

#endif //  #ifndef __DEV_GRIDADVISOR_H__


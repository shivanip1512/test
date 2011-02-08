/*-----------------------------------------------------------------------------*
*
* File:   dev_lmi
*
* Class:  CtiDeviceLMI
* Date:   2004-feb-02
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:     $
* REVISION     :  $Revision: 1.22 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_LMI_H__
#define __DEV_LMI_H__
#pragma warning( disable : 4786 )

#include "dev_remote.h"
#include "tbl_dv_address.h"
#include "tbl_dv_seriesv.h"
#include "prot_lmi.h"

class IM_EX_DEVDB CtiDeviceLMI : public CtiDeviceRemote
{
private:

    CtiTableDeviceAddress _address;
    CtiTableDeviceSeriesV _seriesv;

    typedef CtiDeviceRemote Inherited;

    CtiDeviceExclusion _lmi_exclusion;
    CtiTime _lastPreload;

protected:

    CtiProtocolLMI _lmi;
    Cti::Protocol::Interface *getProtocol();

public:

    CtiDeviceLMI();
    virtual ~CtiDeviceLMI();

    virtual string getSQLCoreStatement() const;

    void DecodeDatabaseReader(Cti::RowReader &rdr);

    int decode(CtiXfer &xfer, int status);
    void sendDispatchResults(CtiConnection &vg_connection);
    void getVerificationObjects(queue< CtiVerificationBase * > &vq);

    INT ExecuteRequest (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    INT AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 3);
    INT IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(INMESS *InMessage, CtiTime &Now, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    void processInboundData(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, list<CtiPointDataMsg*> &points, string &info );

    bool hasExclusions() const;
    void addExclusion(CtiTablePaoExclusion &paox);
    void clearExclusions();
    CtiDeviceExclusion& getExclusion();
    CtiDeviceExclusion exclusion() const; // New copy.
    exclusions getExclusions() const;
    CtiTime selectCompletionTime() const;
    bool   isDeviceExcluded(long id) const;
    bool   isExecuting() const;
    void   setExecuting(bool set = true, CtiTime when = CtiTime(YUKONEOT));
    bool   isExecutionProhibited(const CtiTime &now = CtiTime(), LONG did = 0);
    size_t setExecutionProhibited(unsigned long id, CtiTime& releaseTime = CtiTime(YUKONEOT));
    bool   removeInfiniteProhibit(unsigned long id);

    unsigned queuedWorkCount() const;
    bool    hasQueuedWork() const;
    bool    hasPreloadWork() const;
    CtiTime getPreloadEndTime() const;
    LONG    getPreloadBytes() const;
    LONG    getCycleTime() const;
    LONG    getCycleOffset() const;

    INT  queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt);
    bool getOutMessage(CtiOutMessage *&OutMessage);
    LONG deviceQueueCommunicationTime() const;
    LONG deviceMaxCommunicationTime() const;
};


#endif // #ifndef __DEV_LMI_H__

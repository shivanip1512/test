/*-----------------------------------------------------------------------------*
*
* File:   dev_meter
*
* Class:  CtiDeviceMeter
* Date:   8/18/1999
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_meter.h-arc  $
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_METER_H__
#define __DEV_METER_H__


#include <rw\thr\mutex.h>

#include "tbl_metergrp.h"
#include "dev_ied.h"
#include "dlldefs.h"
#include "xfer.h"

class IM_EX_DEVDB CtiDeviceMeter : public CtiDeviceIED
{
private:

    typedef CtiDeviceIED Inherited;

protected:

   CtiTableDeviceMeterGroup  MeterGroup;

   // data class members for this device

   int _dstFlag, _dstFlagValid;

public:

   CtiDeviceMeter( );
   CtiDeviceMeter(const CtiDeviceMeter& aRef);

   virtual ~CtiDeviceMeter( )  { };

   CtiDeviceMeter& operator=(const CtiDeviceMeter& aRef);

   int readDSTFile( string &id );

   CtiTableDeviceMeterGroup  getMeterGroup() const;
   CtiTableDeviceMeterGroup& getMeterGroup();
   CtiDeviceMeter& setMeterGroup( const CtiTableDeviceMeterGroup & aMeterGroup );

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   bool shouldRetrieveLoadProfile(ULONG &aLPTime, int aIntervalLength);

   virtual INT ExecuteRequest(CtiRequestMsg              *pReq,
                              CtiCommandParser           &parse,
                              OUTMESS                   *&OutMessage,
                              list< CtiMessage* >  &vgList,
                              list< CtiMessage* >  &retList,
                              list< OUTMESS* >     &outList);

   /*
    *  A paired set which implements a state machine (before/do port work/after) in conjunction with
    *  the port's function out/inMess pair.
    */
   virtual INT   generateCommandScan       ( CtiXfer &Transfer, list< CtiMessage* > &traceList)                     { return NoGenerateCmdMethod; };
   virtual INT   generateCommandLoadProfile( CtiXfer &Transfer, list< CtiMessage* > &traceList )                     { return NoGenerateCmdMethod; };
   virtual INT   generateCommandSelectMeter( CtiXfer &Transfer, list< CtiMessage* > &traceList )                     { return NoGenerateCmdMethod; };

   virtual INT   decodeResponseScan       ( CtiXfer &Transfer, INT commReturnValue, list< CtiMessage* > &traceList ) { return NoDecodeResponseMethod; };
   virtual INT   decodeResponseLoadProfile( CtiXfer &Transfer, INT commReturnValue, list< CtiMessage* > &traceList ) { return NoDecodeResponseMethod; };
   virtual INT   decodeResponseSelectMeter( CtiXfer &Transfer, INT commReturnValue, list< CtiMessage* > &traceList ) { return NoDecodeResponseMethod; };

   virtual INT   decodeResultScan( INMESS                    *InMessage,
                                   CtiTime                    &TimeNow,
                                   list< CtiMessage* > &vgList,
                                   list< CtiMessage* > &retList,
                                   list< OUTMESS* >    &outList )         { return NoResultDecodeMethod; };
   virtual INT   decodeResultLoadProfile( INMESS                    *InMessage,
                                          CtiTime                    &TimeNow,
                                          list< CtiMessage* > &vgList,
                                          list< CtiMessage* > &retList,
                                          list< OUTMESS* >    &outList )  { return NoResultDecodeMethod; };

   virtual BOOL verifyAndAddPointToReturnMsg( LONG   aPointId,
                                              DOUBLE aValue,
                                              USHORT aQuality,
                                              CtiTime aTime,
                                              CtiReturnMsg *aReturnMsg,
                                              USHORT aIntervalType=0,
                                              string aValReport=string()) { return YukonBaseError; };

   virtual BOOL insertPointIntoReturnMsg( CtiMessage   *aDataPoint,
                                          CtiReturnMsg *aReturnMsg )            { return YukonBaseError; };

   virtual bool isMeter() const;

};


#endif // #ifndef __DEV_METER_H__

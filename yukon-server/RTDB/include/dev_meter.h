#pragma once

#include "tbl_metergrp.h"
#include "dev_ied.h"
#include "dlldefs.h"
#include "xfer.h"

class IM_EX_DEVDB CtiDeviceMeter : public CtiDeviceIED
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceMeter(const CtiDeviceMeter&);
    CtiDeviceMeter& operator=(const CtiDeviceMeter&);

    typedef CtiDeviceIED Inherited;

protected:

   CtiTableDeviceMeterGroup  MeterGroup;

   // data class members for this device

   int _dstFlag, _dstFlagValid;

public:

   CtiDeviceMeter( );

   int readDSTFile( std::string &id );

   virtual std::string getSQLCoreStatement() const;

   void DecodeDatabaseReader(Cti::RowReader &rdr) override;

   bool shouldRetrieveLoadProfile(ULONG &aLPTime, int aIntervalLength);

   virtual YukonError_t ExecuteRequest(CtiRequestMsg              *pReq,
                                       CtiCommandParser           &parse,
                                       OUTMESS                   *&OutMessage,
                                       CtiMessageList  &vgList,
                                       CtiMessageList  &retList,
                                       OutMessageList     &outList);

   /*
    *  A paired set which implements a state machine (before/do port work/after) in conjunction with
    *  the port's function out/inMess pair.
    */
   virtual YukonError_t generateCommandScan       ( CtiXfer &Transfer, CtiMessageList &traceList )  { return ClientErrors::NoMethodForGenerateCmd; }
   virtual YukonError_t generateCommandLoadProfile( CtiXfer &Transfer, CtiMessageList &traceList )  { return ClientErrors::NoMethodForGenerateCmd; }
   virtual YukonError_t generateCommandSelectMeter( CtiXfer &Transfer, CtiMessageList &traceList )  { return ClientErrors::NoMethodForGenerateCmd; }

   virtual YukonError_t decodeResponseScan       ( CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList )  { return ClientErrors::NoMethodForDecodeResponse; }
   virtual YukonError_t decodeResponseLoadProfile( CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList )  { return ClientErrors::NoMethodForDecodeResponse; }
   virtual YukonError_t decodeResponseSelectMeter( CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList )  { return ClientErrors::NoMethodForDecodeResponse; }

   virtual INT decodeResultScan       ( const INMESS &InMessage,const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )  { return ClientErrors::NoMethodForResultDecode; }
   virtual INT decodeResultLoadProfile( const INMESS &InMessage,const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )  { return ClientErrors::NoMethodForResultDecode; }

   virtual BOOL verifyAndAddPointToReturnMsg( LONG   aPointId,
                                              DOUBLE aValue,
                                              USHORT aQuality,
                                              CtiTime aTime,
                                              CtiReturnMsg *aReturnMsg,
                                              USHORT aIntervalType=0,
                                              std::string aValReport=std::string()) { return ClientErrors::NoMethod; };

   virtual BOOL insertPointIntoReturnMsg( CtiMessage   *aDataPoint,
                                          CtiReturnMsg *aReturnMsg )            { return ClientErrors::NoMethod; };

   virtual bool isMeter() const;

};

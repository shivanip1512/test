
#pragma warning( disable : 4786)
#ifndef __DEV_MARK_V_H__
#define __DEV_MARK_V_H__

/*---------------------------------------------------------------------------------*
*
* File:   dev_mark_v
*
* Class:  CtiDeviceMarkV
* Date:   7/16/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2004/01/20 19:06:01 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <rw/rwtime.h>
#include <rw/rwdate.h>

#include "prot_transdata.h"  
#include "dev_meter.h"
#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"
#include "mgr_point.h"
#include "device.h"
#include "pt_numeric.h"
#include "connection.h" 
#include "msg_cmd.h"

class IM_EX_DEVDB CtiDeviceMarkV : public CtiDeviceMeter
{                                        
private:

  enum
  {
     Type_II         = 50
  };

  enum
  {
     TOTAL_USAGE      = 1,
     CURRENT_DEMAND,
     PEAK_DEMAND,
     PEAK_TIME,
     PEAK_DATE,
     RATEA_USAGE,
     RATEB_USAGE,
     RATEC_USAGE,
     RATED_USAGE,
     RATEA_PEAK_DEMAND,
     RATEB_PEAK_DEMAND,
     RATEC_PEAK_DEMAND,
     RATED_PEAK_DEMAND,
     RATEA_PEAK_TIME,
     RATEB_PEAK_TIME,
     RATEC_PEAK_TIME,
     RATED_PEAK_TIME,
     RATEA_PEAK_DATE,
     RATEB_PEAK_DATE,
     RATEC_PEAK_DATE,
     RATED_PEAK_DATE,
     PREVIOUS_DEMAND,
     LOAD_PROFILE,
     CH1_OFFSET       = 200,
     CH2_OFFSET       = 250,
     CH3_OFFSET       = 300,
     CH4_OFFSET       = 350
  };


   CtiProtocolTransdata       _transdataProtocol;
   CtiProtocolTransdata::llp  _llp;
   
protected:

public:

   typedef CtiDeviceMeter Inherited;

   CtiDeviceMarkV();

   CtiDeviceMarkV( const CtiDeviceMarkV& aRef );

   virtual ~CtiDeviceMarkV();

   virtual INT GeneralScan(CtiRequestMsg              *pReq,
                           CtiCommandParser           &parse,
                           OUTMESS                    *&OutMessage,
                           RWTPtrSlist< CtiMessage >  &vgList,
                           RWTPtrSlist< CtiMessage >  &retList,
                           RWTPtrSlist< OUTMESS >     &outList,
                           INT                        ScanPriority=MAXPRIORITY-4);

   virtual INT LoadProfileScan(CtiRequestMsg              *pReq,
                              CtiCommandParser           &parse,
                              OUTMESS                    *&OutMessage,
                              RWTPtrSlist< CtiMessage >  &vgList,
                              RWTPtrSlist< CtiMessage >  &retList,
                              RWTPtrSlist< OUTMESS >     &outList,
                              INT                        ScanPriority=MAXPRIORITY-4);
   
   virtual INT ExecuteRequest(CtiRequestMsg              *pReq,
                              CtiCommandParser           &parse,
                              OUTMESS                    *&OutMessage,
                              RWTPtrSlist< CtiMessage >  &vgList,
                              RWTPtrSlist< CtiMessage >  &retList,
                              RWTPtrSlist< OUTMESS >     &outList,
                              INT                        ScanPriority=MAXPRIORITY-4);
   
   virtual INT ResultDecode(INMESS                    *InMessage,
                            RWTime                    &TimeNow,
                            RWTPtrSlist< CtiMessage > &vgList,
                            RWTPtrSlist< CtiMessage > &retList,
                            RWTPtrSlist< OUTMESS >    &outList);

   virtual INT ErrorDecode(INMESS                     *InMessage,
                           RWTime                     &TimeNow,
                           RWTPtrSlist< CtiMessage >  &vgList,
                           RWTPtrSlist< CtiMessage >  &retList,
                           RWTPtrSlist< OUTMESS >     &outList);

   int decodeResultScan( INMESS                    *InMessage,
                          RWTime                    &TimeNow,
                          RWTPtrSlist< CtiMessage > &vgList,
                          RWTPtrSlist< CtiMessage > &retList,
                          vector<CtiTransdataData *> transVector );

   void processDispatchReturnMessage( CtiReturnMsg *msgPtr );
   int sendCommResult( INMESS *InMessage );
   int checkQuality( int yyMap, int lpValue );
   void CtiDeviceMarkV::correctValue( CtiTransdataTracker::lpRecord rec, int yyMap, int &value, int &quality );
   int getChannelOffset( int index );

   CtiProtocolTransdata & getProtocol( void );
   RWTime getMsgTime( int timeID, int dateID, vector<CtiTransdataData *> transVector );
   
   CtiPointDataMsg* fillPDMsg( vector<CtiTransdataData *> transVector,
                               CtiPointBase *point, 
                               int index,
                               int timeID,
                               int dateID );

   void DecodeDatabaseReader( RWDBReader &rdr );

};

#endif // #ifndef __DEV_H__

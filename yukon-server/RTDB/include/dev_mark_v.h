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
* REVISION     :  $Revision: 1.23.2.1 $
* DATE         :  $Date: 2008/11/17 23:06:32 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __DEV_MARK_V_H__
#define __DEV_MARK_V_H__
#pragma warning( disable : 4786)


#include "prot_transdata.h"
#include "dev_meter.h"
#include "dlldefs.h"
#include "dsm2.h"

#include "types.h"
#include "pt_numeric.h"
#include "connection.h"
#include "msg_cmd.h"
#include "dllbase.h"

class IM_EX_DEVDB CtiDeviceMarkV : public CtiDeviceMeter
{
private:

    typedef CtiDeviceMeter Inherited;

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

   CtiDeviceMarkV();

   CtiDeviceMarkV( const CtiDeviceMarkV& aRef );

   virtual ~CtiDeviceMarkV();

   virtual INT GeneralScan(CtiRequestMsg              *pReq,
                           CtiCommandParser           &parse,
                           OUTMESS                    *&OutMessage,
                           std::list< CtiMessage* >  &vgList,
                           std::list< CtiMessage* >  &retList,
                           std::list< OUTMESS* >     &outList,
                           INT                        ScanPriority=MAXPRIORITY-4);

   virtual INT LoadProfileScan(CtiRequestMsg              *pReq,
                              CtiCommandParser           &parse,
                              OUTMESS                    *&OutMessage,
                              std::list< CtiMessage* >  &vgList,
                              std::list< CtiMessage* >  &retList,
                              std::list< OUTMESS* >     &outList,
                              INT                        ScanPriority=MAXPRIORITY-4);

   virtual INT ExecuteRequest(CtiRequestMsg              *pReq,
                              CtiCommandParser           &parse,
                              OUTMESS                    *&OutMessage,
                              std::list< CtiMessage* >  &vgList,
                              std::list< CtiMessage* >  &retList,
                              std::list< OUTMESS* >     &outList,
                              INT                        ScanPriority=MAXPRIORITY-4);

   virtual INT ResultDecode(INMESS                    *InMessage,
                            CtiTime                    &TimeNow,
                            std::list< CtiMessage* > &vgList,
                            std::list< CtiMessage* > &retList,
                            std::list< OUTMESS* >    &outList);

   virtual INT ErrorDecode(const INMESS      &InMessage,
                           const CtiTime      TimeNow,
                           std::list<CtiMessage*> &retList);

   int decodeResultScan( INMESS                    *InMessage,
                          CtiTime                    &TimeNow,
                          std::list< CtiMessage* > &vgList,
                          std::list< CtiMessage* > &retList,
                          std::vector<CtiTransdataData *> transVector );

   void processDispatchReturnMessage( CtiReturnMsg *msgPtr );
   int sendCommResult( INMESS *InMessage );
   int checkQuality( int yyMap, int lpValue );
   void CtiDeviceMarkV::correctValue( CtiTransdataTracker::lpRecord rec, int yyMap, int &value, int &quality );
   int getChannelOffset( int index );

   CtiProtocolTransdata & getTransdataProtocol( void );
   CtiTime getMsgTime( int timeID, int dateID, std::vector<CtiTransdataData *> transVector );

   CtiPointDataMsg* fillPDMsg( std::vector<CtiTransdataData *> transVector,
                               CtiPointSPtr point,
                               int index,
                               int timeID,
                               int dateID );

   void DecodeDatabaseReader(Cti::RowReader &rdr);

};

#endif // #ifndef __DEV_H__

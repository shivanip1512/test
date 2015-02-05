#pragma once

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

public:
   CtiDeviceMarkV();

   YukonError_t GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                                    INT ScanPriority=MAXPRIORITY-4) override;

   YukonError_t LoadProfileScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                                        INT ScanPriority=MAXPRIORITY-4) override;

   virtual YukonError_t ExecuteRequest (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                                                INT ScanPriority=MAXPRIORITY-4);

   YukonError_t ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   YukonError_t ErrorDecode (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList) override;

   YukonError_t decodeResultScan( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, std::vector<CtiTransdataData *> transVector );

   void processDispatchReturnMessage( CtiReturnMsg *msgPtr );
   YukonError_t sendCommResult( INMESS &InMessage ) override;
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

   void DecodeDatabaseReader(Cti::RowReader &rdr) override;

};

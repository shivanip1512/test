#pragma once

#include "dev_base.h"
#include "rte_base.h"
#include "msg_pcrequest.h"
#include "smartmap.h"


class IM_EX_DEVDB CtiRouteXCU : public CtiRouteBase
{
protected:

   CtiDeviceSPtr _transmitterDevice;    // This object is NOT responsible for this memory..

private:

    void enablePrefix(bool enable);

public:

   typedef CtiRouteBase Inherited;

   CtiRouteXCU();

   virtual std::string toString() const override;

   void setDevicePointer(CtiDeviceSPtr p);

   long getTrxDeviceID() const override;

   void DecodeDatabaseReader(Cti::RowReader &rdr) override;
   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList) override;

   YukonError_t assembleVersacomRequest    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   YukonError_t assembleFisherPierceRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   YukonError_t assembleRippleRequest      (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   YukonError_t assembleExpresscomRequest  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   YukonError_t assembleSA305Request       (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   YukonError_t assembleSA105205Request    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   YukonError_t assembleSASimpleRequest    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
};

typedef boost::shared_ptr<CtiRouteXCU> CtiRouteXCUSPtr;

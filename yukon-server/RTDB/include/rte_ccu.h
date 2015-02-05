#pragma once

#include "rte_xcu.h"
#include "tbl_rtcarrier.h"
#include "tbl_rtrepeater.h"

class IM_EX_DEVDB CtiRouteCCU : public CtiRouteXCU
{
public:
    typedef std::set< CtiTableRepeaterRoute > RepeaterSet;

protected:

   CtiTableCarrierRoute    Carrier;

   // This is a vector of repeaters 0 to 7 in length... currently we rely on DBEditor to assure this...
   RepeaterSet _repeaters;

   static void adjustOutboundStagesToFollow(unsigned short &stagesToFollow, unsigned &messageFlags, const int type);

private:

    typedef CtiRouteXCU Inherited;

public:

   CtiRouteCCU();

   virtual std::string toString() const override;

   const RepeaterSet &getRepeaters() const;

   INT getStages() const override;

   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList) override;

   YukonError_t assembleVersacomRequest  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   YukonError_t assembleExpresscomRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

   YukonError_t assembleDLCRequest (CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

   INT  getBus() const override;
   INT  getCCUFixBits() const override;
   INT  getCCUVarBits() const override;

   static std::string getSQLCoreStatement();

   void DecodeDatabaseReader(Cti::RowReader &rdr) override;
   void addRepeater(const CtiTableRepeaterRoute &Rpt);
   void clearRepeaters();
};

typedef boost::shared_ptr<CtiRouteCCU> CtiRouteCCUSPtr;


#pragma once

#include <rw/tvordvec.h>
#include "rte_xcu.h"
#include "tbl_rtcarrier.h"
#include "tbl_rtrepeater.h"

class IM_EX_DEVDB CtiRouteCCU : public CtiRouteXCU
{
protected:

   CtiTableCarrierRoute    Carrier;

   // This is a vector of repeaters 0 to 7 in length... currently we rely on DBEditor to assure this...
   RWTValOrderedVector< CtiTableRepeaterRoute >  RepeaterList;

   static void adjustOutboundStagesToFollow(unsigned short &stagesToFollow, unsigned &messageFlags, const int type);

private:

    enum
    {
        MaxStagesToFollow = 7
    };

public:

   typedef CtiRouteXCU Inherited;
   typedef RWTValOrderedVector< CtiTableRepeaterRoute > CtiRepeaterList_t;

   CtiRouteCCU();
   CtiRouteCCU(const CtiRouteCCU& aRef);

   ~CtiRouteCCU();

   CtiRouteCCU &operator=(const CtiRouteCCU& aRef);

   virtual void DumpData();

   CtiRepeaterList_t &getRepeaterList();

   virtual INT        getStages() const;

   virtual INT ExecuteRequest(CtiRequestMsg        *pReq,
                              CtiCommandParser     &parse,
                              OUTMESS             *&OutMessage,
                              std::list< CtiMessage* >  &vgList,
                              std::list< CtiMessage* >  &retList,
                              std::list< OUTMESS* >     &outList);

   INT         assembleVersacomRequest  (CtiRequestMsg        *pReq,
                                         CtiCommandParser     &parse,
                                         OUTMESS              *OutMessage,
                                         std::list< CtiMessage* >  &vgList,
                                         std::list< CtiMessage* >  &retList,
                                         std::list< OUTMESS* >     &outList);

   INT         assembleExpresscomRequest(CtiRequestMsg        *pReq,
                                         CtiCommandParser     &parse,
                                         OUTMESS              *OutMessage,
                                         std::list< CtiMessage* >  &vgList,
                                         std::list< CtiMessage* >  &retList,
                                         std::list< OUTMESS* >     &outList);

   INT         assembleDLCRequest       (CtiCommandParser     &parse,
                                         OUTMESS             *&OutMessage,
                                         std::list< CtiMessage* >  &vgList,
                                         std::list< CtiMessage* >  &retList,
                                         std::list< OUTMESS* >     &outList);

   virtual INT  getBus() const;
   virtual INT  getCCUFixBits() const;
   virtual INT  getCCUVarBits() const;

   static std::string getSQLCoreStatement();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual void addRepeater(const CtiTableRepeaterRoute &Rpt);
};

typedef boost::shared_ptr<CtiRouteCCU> CtiRouteCCUSPtr;


#pragma once

#include <rw/tvordvec.h>
#include "rte_xcu.h"
#include "tbl_rtcarrier.h"
#include "tbl_rtrepeater.h"

class IM_EX_DEVDB CtiRouteCCU : public CtiRouteXCU
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiRouteCCU(const CtiRouteCCU&);
    CtiRouteCCU& operator=(const CtiRouteCCU&);

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

   virtual std::string toString() const override;

   CtiRepeaterList_t &getRepeaterList();

   virtual INT        getStages() const;

   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList) override;

   YukonError_t assembleVersacomRequest  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   YukonError_t assembleExpresscomRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

   YukonError_t assembleDLCRequest (CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

   virtual INT  getBus() const;
   virtual INT  getCCUFixBits() const;
   virtual INT  getCCUVarBits() const;

   static std::string getSQLCoreStatement();

   void DecodeDatabaseReader(Cti::RowReader &rdr) override;
   virtual void addRepeater(const CtiTableRepeaterRoute &Rpt);
};

typedef boost::shared_ptr<CtiRouteCCU> CtiRouteCCUSPtr;


#pragma once

#include "dev_base.h"
#include "rte_base.h"
#include "msg_pcrequest.h"
#include "smartmap.h"


class IM_EX_DEVDB CtiRouteXCU : public CtiRouteBase
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiRouteXCU(const CtiRouteXCU&);
    CtiRouteXCU& operator=(const CtiRouteXCU&);

protected:

   CtiDeviceSPtr _transmitterDevice;    // This object is NOT responsible for this memory..

private:

    void enablePrefix(bool enable);

public:

   typedef CtiRouteBase Inherited;

   CtiRouteXCU();

   virtual void DumpData();

   void resetDevicePointer();
   CtiRouteXCU&  setDevicePointer(CtiDeviceSPtr p);

   virtual LONG getTrxDeviceID() const;

   void DecodeDatabaseReader(Cti::RowReader &rdr) override;
   INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList) override;

   INT assembleVersacomRequest    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   INT assembleFisherPierceRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   INT assembleRippleRequest      (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   INT assembleExpresscomRequest  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   INT assembleSA305Request       (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   INT assembleSA105205Request    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   INT assembleSASimpleRequest    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
};

typedef boost::shared_ptr<CtiRouteXCU> CtiRouteXCUSPtr;

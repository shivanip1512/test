#pragma once
#pragma warning( disable : 4786)

#include "port_serial.h"

namespace Cti   {
namespace Ports {

class IM_EX_PRTDB TcpPort : public CtiPortSerial
{
private:

    TcpPort(const TcpPort& aRef);
    TcpPort& operator=(const TcpPort& aRef);

public:

   typedef CtiPortSerial Inherited;

   TcpPort() {};
   virtual ~TcpPort() {};

   static string getSQLCoreStatement()
   {
       static const string sql =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                      "CP.alarminhibit, CP.commonprotocol, CP.performancealarm, CP.performthreshold, "
                                      "CP.sharedporttype, CP.sharedsocketnumber, PST.baudrate, PST.cdwait, PST.linesettings, "
                                      "TMG.pretxwait, TMG.rtstotxwait, TMG.posttxwait, TMG.receivedatawait, TMG.extratimeout "
                                  "FROM YukonPAObject YP, CommPort CP, PortSettings PST, PortTiming TMG "
                                  "WHERE YP.paobjectid = CP.portid AND YP.paobjectid = PST.portid AND "
                                      "YP.paobjectid = TMG.portid";

       return sql;
   }

   virtual INT inMess(CtiXfer &Xfer, CtiDeviceSPtr Dev, list<CtiMessage *> &traceList)
   {
       return 0;
   }
   virtual INT openPort(INT rate, INT bits, INT parity, INT stopbits)
   {
       return 0;
   }
   virtual INT outMess(CtiXfer &Xfer, CtiDeviceSPtr Dev, list<CtiMessage *> &traceList)
   {
       return 0;
   }
   virtual bool isViable()
   {
       return true;  //  port manages individual connections, so the port itself is always viable
   }
};

typedef boost::shared_ptr< TcpPort > TcpPortSPtr;

}
}


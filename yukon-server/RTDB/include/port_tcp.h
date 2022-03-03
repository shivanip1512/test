#pragma once

#include "port_serial.h"

namespace Cti   {
namespace Ports {

class IM_EX_PRTDB TcpPort : public CtiPortSerial
{
public:

   typedef CtiPortSerial Inherited;

   TcpPort() {};

   static std::string getSQLCoreStatement()
   {
       static const std::string sql =
           "SELECT"
               " YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag,"
               " CP.alarminhibit, CP.commonprotocol, CP.performancealarm, CP.performthreshold, CP.sharedporttype, CP.sharedsocketnumber,"
               " PST.baudrate, PST.cdwait, PST.linesettings,"
               " TMG.pretxwait, TMG.rtstotxwait, TMG.posttxwait, TMG.receivedatawait, TMG.extratimeout, TMG.PostCommWait"
           " FROM"
               " YukonPAObject YP"
               " JOIN CommPort CP on YP.paobjectid = CP.portid"
               " JOIN PortSettings PST on YP.paobjectid = PST.portid"
               " JOIN PortTiming TMG on YP.paobjectid = TMG.portid"
           " WHERE"
               " YP.type = 'TCP'";

       return sql;
   }

   virtual YukonError_t inMess(CtiXfer &Xfer, CtiDeviceSPtr Dev, std::list<CtiMessage *> &traceList)
   {
       return ClientErrors::None;
   }
   virtual YukonError_t openPort(INT rate, INT bits, INT parity, INT stopbits)
   {
       return ClientErrors::None;
   }
   virtual YukonError_t outMess(CtiXfer &Xfer, CtiDeviceSPtr Dev, std::list<CtiMessage *> &traceList)
   {
       return ClientErrors::None;
   }
   virtual bool isViable()
   {
       return true;  //  port manages individual connections, so the port itself is always viable
   }
};

typedef boost::shared_ptr< TcpPort > TcpPortSPtr;

}
}


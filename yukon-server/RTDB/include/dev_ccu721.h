#pragma once

#include "dev_remote.h"
#include "tbl_dv_address.h"
#include "dev_ccu721_queue_interface.h"
#include "prot_klondike.h"

namespace Cti       {
namespace Devices   {

class IM_EX_DEVDB Ccu721Device : public CtiDeviceRemote
{
private:

    typedef CtiDeviceRemote Inherited;

    CtiTableDeviceAddress _address;

    bool _initialized;  //  this may need to be muxed, since a couple of different
                        //    threads call buildCommand() for LoadRoutes

    std::vector<OUTMESS> _statistics;

    OUTMESS *_current_om;

    enum
    {
        InMessage_StringOffset = 96  //  hopefully enough to stay out of the way of the DSTRUCT
    };

    static int translateKlondikeError(Protocol::Klondike::Errors error);

    CCU721DeviceQueueInterface _queueInterface;

protected:

    typedef std::vector<unsigned char> byte_buffer_t;

    static void writeDLCMessage (byte_buffer_t &buf, const OUTMESS *om);
    static void writeDLCTimesync(byte_buffer_t &buf);
    static void writeAWord      (byte_buffer_t &buf, const ASTRUCT &ASt);
    static void writeBWord      (byte_buffer_t &buf, const BSTRUCT &BSt);

    int decodeDWords(const unsigned char *input, const unsigned input_length, const unsigned Remote, DSTRUCT *DSt) const;
    static int decodeEWord(const unsigned char *input, const unsigned input_length);

    Protocol::Klondike _klondike;
    Protocol::IDLC     _idlc_wrap;

    Protocol::Interface *getProtocol();

public:

    Ccu721Device();
    virtual ~Ccu721Device();

    enum Commands
    {
        Command_Loopback,
        Command_LoadQueue,
        Command_ReadQueue,
        Command_LoadRoutes,
        Command_Timesync
    };

    virtual string getSQLCoreStatement() const;

    void DecodeDatabaseReader(Cti::RowReader &rdr);

    INT ExecuteRequest (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList);

    INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(INMESS *InMessage, CtiTime &Now, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList);

    bool needsReset() const;

    //  these commands just indicate that messages are queued into the Yukon-side CCU device;
    //    it does not indicate whether the messages are currently loaded into the field device
    INT  queuedWorkCount() const;
    bool hasQueuedWork()   const;
    bool hasWaitingWork()  const;
    bool hasRemoteWork()   const;
    INT  queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt);

    string queueReport() const;

    virtual DeviceQueueInterface *getDeviceQueueHandler();

    virtual LONG getAddress() const;

    bool buildCommand(CtiOutMessage *&OutMessage, Commands command);

    virtual int recvCommRequest(OUTMESS *OutMessage);
    virtual int sendCommResult (INMESS  *InMessage);

    void getTargetDeviceStatistics(vector< OUTMESS > &om_statistics);

    int processInbound(const OUTMESS *om, INMESS *im);
};

typedef boost::shared_ptr<Ccu721Device> Ccu721SPtr;

}
}


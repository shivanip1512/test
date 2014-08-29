#pragma once

#include "dev_remote.h"
#include "tbl_dv_address.h"
#include "device_queue_interface.h"
#include "prot_klondike.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Ccu721Device : public CtiDeviceRemote, public DeviceQueueInterface
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    Ccu721Device(const Ccu721Device&);
    Ccu721Device& operator=(const Ccu721Device&);

    typedef CtiDeviceRemote Inherited;

    CtiTableDeviceAddress _address;

    bool _routes_loaded;  //  this may need to be muxed, since a couple of different
                          //    threads call buildCommand() for LoadRoutes

    std::vector<queued_result_t> _results;

    typedef std::set<OUTMESS *> request_handles;

    request_handles _queued_outmessages;

    OUTMESS *_current_om;

    enum
    {
        InMessage_StringOffset = 96  //  hopefully enough to stay out of the way of the DSTRUCT
    };

    static YukonError_t translateKlondikeError(Protocols::KlondikeProtocol::Errors error);

protected:

    typedef std::vector<unsigned char> byte_buffer_t;

    static void writeDLCMessage (byte_buffer_t &buf, const OUTMESS *om);
    static void writeDLCTimesync(byte_buffer_t &buf);
    static void writeAWord      (byte_buffer_t &buf, const ASTRUCT &ASt);
    static void writeBWord      (byte_buffer_t &buf, const BSTRUCT &BSt);

    YukonError_t decodeDWords(const unsigned char *input, const unsigned input_length, const unsigned Remote, DSTRUCT *DSt, ESTRUCT *ESt) const;
    static YukonError_t decodeEWord(const unsigned char *input, const unsigned input_length, ESTRUCT *ESt);

    Protocols::KlondikeProtocol _klondike;

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

    virtual std::string getSQLCoreStatement() const;

    void DecodeDatabaseReader(Cti::RowReader &rdr);

    INT ExecuteRequest (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list<CtiMessage *> &vgList, std::list<CtiMessage *> &retList, std::list<OUTMESS *> &outList);

    INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list<CtiMessage *> &vgList, std::list<CtiMessage *> &retList, std::list<OUTMESS *> &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(const INMESS *InMessage, CtiTime &Now, std::list<CtiMessage *> &vgList, std::list<CtiMessage *> &retList, std::list<OUTMESS *> &outList);

    bool needsReset() const;

    //  these commands just indicate that messages are queued into the Yukon-side CCU device;
    //    it does not indicate whether the messages are currently loaded into the field device
    unsigned queuedWorkCount() const;
    bool hasQueuedWork()   const;
    bool hasWaitingWork()  const;
    bool hasRemoteWork()   const;
    YukonError_t queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt);

    std::string queueReport() const;

    virtual DeviceQueueInterface *getDeviceQueueHandler();

    virtual unsigned long getRequestCount(unsigned long requestID) const;
    virtual void retrieveQueueEntries(bool( *myFindFunc)(void *, void *), void *findParameter, std::list<void *> &entries);

    virtual LONG getAddress() const;

    bool buildCommand(CtiOutMessage *&OutMessage, Commands command);

    virtual YukonError_t recvCommRequest(OUTMESS *OutMessage);
    virtual YukonError_t sendCommResult (INMESS  *InMessage);

    void getQueuedResults(std::vector<queued_result_t> &results);

    YukonError_t processInbound(const OUTMESS *om, INMESS *im);
};

typedef boost::shared_ptr<Ccu721Device> Ccu721SPtr;

}
}


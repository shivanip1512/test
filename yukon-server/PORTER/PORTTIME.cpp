#include "precompiled.h"

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "porter.h"
#include "portdecl.h"
#include "elogger.h"

#include "portglob.h"

#include "c_port_interface.h"
#include "port_base.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "dev_base.h"
#include "dev_ccu.h"
#include "dev_ccu721.h"
#include "dev_dnp.h"
#include "dev_ilex.h"
#include "dev_mct4xx.h"
#include "mgr_route.h"

#include "logger.h"
#include "guard.h"
#include "trx_info.h"
#include "trx_711.h"
#include "thread_monitor.h"
#include "ThreadStatusKeeper.h"

#include "config_data_dnp.h"

#include "prot_welco.h"
#include "prot_lmi.h"

#include <sys\timeb.h>

using namespace std;
using namespace Cti::Config;
using Cti::ThreadStatusKeeper;

extern ULONG TimeSyncRate;

extern HCTIQUEUE*       QueueHandle(LONG pid);

extern CtiRouteManager RouteManager;

IM_EX_CTIBASE extern CtiCriticalSection coutMux;


ULONG getNextTimeSync()
{
    ULONG next = nextScheduledTimeAlignedOnRate(CtiTime::now(), TimeSyncRate).seconds();

    if( TimeSyncRate >= 300 )
    {
        //  for any time sync rates slower than 5 minutes, offset them by 2.5 minutes
        //    e.g. a rate of 1 hour (3600 seconds) will go at 2.5 minutes after the hour
        next += 150;
    }

    return next;
}

struct timeSyncCCU711
{
    long port_id;

    timeSyncCCU711(long port_id_) : port_id(port_id_) {  };

    void operator()(CtiDeviceSPtr &RemoteRecord)
    {
        if( (RemoteRecord->getPortID() != port_id) || (RemoteRecord->getAddress() == 0xffff) || (RemoteRecord->isInhibited()) )
        {
            return;
        }

        /* Allocate some memory */
        OUTMESS *OutMessage = new OUTMESS;

        /* send a time sync to this guy */
        OutMessage->DeviceID = RemoteRecord->getID();
        OutMessage->Port     = RemoteRecord->getPortID();
        OutMessage->Remote   = RemoteRecord->getAddress();
        OutMessage->TimeOut  = TIMEOUT;
        OutMessage->Retry        = 0;
        OutMessage->OutLength    = 10;
        OutMessage->InLength     = 0;
        OutMessage->Source       = 0;
        OutMessage->Destination  = DEST_TSYNC;
        OutMessage->Command      = CMND_XTIME;
        OutMessage->Sequence     = 0;
        OutMessage->Priority     = MAXPRIORITY;
        OutMessage->EventCode    = NOWAIT | NORESULT | ENCODED | TSYNC | RCONT;
        OutMessage->ReturnNexus  = NULL;
        OutMessage->SaveNexus    = NULL;
        OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
        OutMessage->ExpirationTime = getNextTimeSync();

        if(PortManager.writeQueue(OutMessage))
        {
            CTILOG_ERROR(dout, "Could not write to queue for Port "<< OutMessage->Port);

            delete OutMessage;
        }
        else
        {
            CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)RemoteRecord->getTrxInfo();
            /* Increment the number of entries for this guys queue */
            p711Info->PortQueueEnts++;
            p711Info->PortQueueConts++;
        }
    }
};

struct timeSyncCCU710
{
    long port_id;

    timeSyncCCU710(long port_id_) : port_id(port_id_) {  };

    void operator()(CtiDeviceSPtr &RemoteRecord)
    {
        if((RemoteRecord->getPortID() != port_id) || (RemoteRecord->getAddress() == 0xffff) || (RemoteRecord->isInhibited()) )
        {
            return;
        }

        try
        {
            CtiRouteSPtr    RouteRecord;

            CtiRouteManager::coll_type::reader_lock_guard_t guard(RouteManager.getLock());
            CtiRouteManager::spiterator rte_itr;

            //  Walk down the routes for this RemoteRecord and pick out the time sync ("default") routes
            for(rte_itr = RouteManager.begin(); rte_itr != RouteManager.end(); CtiRouteManager::nextPos(rte_itr))
            {
                RouteRecord = rte_itr->second;

                if(RouteRecord->getTrxDeviceID() == RemoteRecord->getID() && RouteRecord->isDefaultRoute())
                {
                    BSTRUCT message;
                    OUTMESS *OutMessage = new OUTMESS;

                    using Cti::Devices::DlcBaseDevice;
                    using Cti::Devices::MctDevice;
                    using namespace Cti::Protocols;

                    //  load up all of the port/route specific items
                    OutMessage->DeviceID  = RemoteRecord->getID();
                    OutMessage->Port      = RemoteRecord->getPortID();
                    OutMessage->Remote    = RemoteRecord->getAddress();
                    OutMessage->TimeOut   = TIMEOUT;
                    OutMessage->Retry     = 0;
                    OutMessage->Sequence  = 0;
                    OutMessage->Priority  = MAXPRIORITY;
                    OutMessage->EventCode = NOWAIT | NORESULT | DTRAN | BWORD | TSYNC;
                    OutMessage->Command   = CMND_DTRAN;
                    OutMessage->InLength  = 0;
                    OutMessage->ReturnNexus = NULL;
                    OutMessage->SaveNexus   = NULL;
                    OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
                    OutMessage->ExpirationTime = getNextTimeSync();

                    OutMessage->Buffer.BSt.Port     = RemoteRecord->getPortID();
                    OutMessage->Buffer.BSt.Remote   = RemoteRecord->getAddress();
                    OutMessage->Buffer.BSt.Address  = DlcBaseDevice::BroadcastAddress;
                    OutMessage->Buffer.BSt.Function = MctDevice::Memory_TSyncPos;
                    OutMessage->Buffer.BSt.Length   = MctDevice::Memory_TSyncLen;
                    OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Write;
                    //  we don't fill in the data because it's filled in by RefreshMCTTimeSync() later on

                    //  this should all be filled in by the route's ExecuteRequest
                    OutMessage->Buffer.BSt.DlcRoute.Amp        = ((CtiDeviceIDLC *)(RemoteRecord.get()))->getIDLC().getAmp();
                    OutMessage->Buffer.BSt.DlcRoute.Bus        = RouteRecord->getBus();
                    OutMessage->Buffer.BSt.DlcRoute.RepVar     = RouteRecord->getCCUVarBits();
                    OutMessage->Buffer.BSt.DlcRoute.RepFixed   = RouteRecord->getCCUFixBits();
                    OutMessage->Buffer.BSt.DlcRoute.Stages     = 0;  //  must set the stages to 0 on a CCU 710

                    //  Ideally, use something like this instead of the above code...
                    //RouteRecord->ExecuteRequest();
                    //  ... but because we're not executing on the route, we have to do this manually
                    EmetconProtocol::buildBWordMessage(OutMessage);

                    if(PortManager.writeQueue(OutMessage))
                    {
                        CTILOG_ERROR(dout, "Could not write to port queue for DeviceID "<< OutMessage->DeviceID <<" / Port "<< OutMessage->Port);

                        delete OutMessage;
                    }
                }
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }
};


struct timeSyncTDMarkV
{
    long port_id;

    timeSyncTDMarkV(long port_id_) : port_id(port_id_) {  };

    void operator()(CtiDeviceSPtr &RemoteRecord)
    {
        if(RemoteRecord->getPortID() != port_id || RemoteRecord->isInhibited() || RemoteRecord->getAddress() != 0xffff)
        {
            return;
        }

        /* Generate a time sync for this guy */

        /* Allocate some memory */
        OUTMESS *OutMessage = new OUTMESS;

        int Index = PREIDLEN;

        /* Make sure the meter is ready for the password */
        OutMessage->Buffer.OutMessage[Index++] = '\r';
        OutMessage->Buffer.OutMessage[Index++] = '\0';

        /* Load the password into the buffer */
        cerr << __FILE__ << " (" << __LINE__ << ") This may break.. CGP 072599" << endl;

        for(int i = 0; i < STANDNAMLEN; i++)
        {
            if(RemoteRecord->getPassword()[i] == ' ')
            {
                break;
            }
            OutMessage->Buffer.OutMessage[Index++] = RemoteRecord->getPassword()[i];
        }

        OutMessage->Buffer.OutMessage[Index++] = '\r';
        OutMessage->Buffer.OutMessage[Index++] = '\0';

        /* Load the ID command */
        if(RemoteRecord->getAddress() != 0)
        {
            OutMessage->Buffer.OutMessage[Index++] = 'I';
            OutMessage->Buffer.OutMessage[Index++] = 'D';
            OutMessage->Buffer.OutMessage[Index++] = '\r';
            OutMessage->Buffer.OutMessage[Index++] = '\0';
        }

        /* Load the TI command */
        OutMessage->Buffer.OutMessage[Index++] = 'T';
        OutMessage->Buffer.OutMessage[Index++] = 'I';
        OutMessage->Buffer.OutMessage[Index++] = '\r';
        OutMessage->Buffer.OutMessage[Index++] = '\0';

        /* Load the LO command */
        OutMessage->Buffer.OutMessage[Index++] = 'L';
        OutMessage->Buffer.OutMessage[Index++] = 'O';
        OutMessage->Buffer.OutMessage[Index++] = '\r';
        OutMessage->Buffer.OutMessage[Index++] = '\0';

        /* Load all the other stuff that is needed */
        OutMessage->DeviceID = RemoteRecord->getID();
        OutMessage->Port     = RemoteRecord->getPortID();
        OutMessage->Remote   = RemoteRecord->getAddress();
        // memcpy (OutMessage->DeviceName, RemoteRecord->getDeviceName(), STANDNAMLEN);
        OutMessage->TimeOut     = 2;
        OutMessage->OutLength   = Index - PREIDLEN;
        OutMessage->InLength    = -1;
        OutMessage->Sequence    = 0;
        OutMessage->Priority    = MAXPRIORITY - 2;
        OutMessage->EventCode   = NOWAIT | NORESULT | ENCODED | TSYNC;
        OutMessage->ReturnNexus = NULL;
        OutMessage->SaveNexus   = NULL;
        OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
        OutMessage->ExpirationTime = getNextTimeSync();

        if(PortManager.writeQueue(OutMessage))
        {
            CTILOG_ERROR(dout, "Could not write to port queue for DeviceID "<< OutMessage->DeviceID <<" / Port "<< OutMessage->Port);
            delete OutMessage;
        }
    }
};


struct timeSyncILEX
{
    long port_id;

    timeSyncILEX(long port_id_) : port_id(port_id_) {  };

    void operator()(CtiDeviceSPtr &RemoteRecord)
    {
        if(RemoteRecord->getPortID() != port_id || RemoteRecord->isInhibited())
        {
            return;
        }

        OUTMESS *OutMessage = new OUTMESS;

        ILEXHeader (OutMessage->Buffer.OutMessage + PREIDLEN, RemoteRecord->getAddress(), ILEXTIMESYNC, TIMESYNC1, TIMESYNC2);

        OutMessage->Buffer.OutMessage[PREIDLEN + 2] = ILEXSETTIME;

        /* send a time sync to this guy */
        OutMessage->DeviceID = RemoteRecord->getID();
        OutMessage->Port     = RemoteRecord->getPortID();
        OutMessage->Remote   = RemoteRecord->getAddress();
        OutMessage->TimeOut     = 2;
        OutMessage->Retry       = 0;
        OutMessage->OutLength   = ILEXTIMELENGTH;
        OutMessage->InLength    = 0;
        OutMessage->Source      = 0;
        OutMessage->Destination = 0;
        OutMessage->Sequence    = 0;
        OutMessage->Priority    = MAXPRIORITY - 2;
        OutMessage->EventCode   = NOWAIT | NORESULT | ENCODED | TSYNC;
        OutMessage->ReturnNexus = NULL;
        OutMessage->SaveNexus   = NULL;
        OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
        OutMessage->ExpirationTime = getNextTimeSync();

        if(PortManager.writeQueue(OutMessage))
        {
            CTILOG_ERROR(dout, "Could not write to port queue for DeviceID "<< OutMessage->DeviceID <<" / Port "<< OutMessage->Port);
            delete OutMessage;
        }
    }
};


struct timeSyncWelco_VTU
{
    long port_id;

    timeSyncWelco_VTU(long port_id_) : port_id(port_id_) {  };

    void operator()(CtiDeviceSPtr &RemoteRecord)
    {
        // 20050919 CGP: Timesyncs not supported on the RTU (ODEC RTU implementation) to the global address
        if(RemoteRecord->getPortID() != port_id || RemoteRecord->isInhibited() || RemoteRecord->getAddress() == RTUGLOBAL)
        {
            return;
        }

        /* Allocate some memory */
        OUTMESS *OutMessage = new OUTMESS;

        OutMessage->Buffer.OutMessage[5] = IDLC_TIMESYNC | 0x80;
        OutMessage->Buffer.OutMessage[6] = 7;

        /* send a time sync to this guy */
        OutMessage->DeviceID = RemoteRecord->getID();
        OutMessage->Port     = RemoteRecord->getPortID();
        OutMessage->Remote   = RemoteRecord->getAddress();
        OutMessage->TimeOut     = 2;
        OutMessage->Retry       = 0;
        OutMessage->OutLength   = 7;
        OutMessage->InLength    = 0;
        OutMessage->Source      = 0;
        OutMessage->Destination = 0;
        OutMessage->Sequence    = 0;
        OutMessage->Priority    = MAXPRIORITY - 2;
        OutMessage->EventCode   = NOWAIT | NORESULT | ENCODED | TSYNC;
        OutMessage->ReturnNexus = NULL;
        OutMessage->SaveNexus   = NULL;
        OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
        OutMessage->ExpirationTime = getNextTimeSync();

        if(PortManager.writeQueue(OutMessage))
        {
            CTILOG_ERROR(dout, "Could not write to port queue for DeviceID "<< OutMessage->DeviceID <<" / Port "<< OutMessage->Port);
            delete OutMessage;
        }
    }
};


struct timeSyncSeriesVLMIRTU
{
    long port_id;

    timeSyncSeriesVLMIRTU(long port_id_) : port_id(port_id_) {  };

    void operator()(CtiDeviceSPtr &RemoteRecord)
    {
        if(RemoteRecord->getPortID() != port_id || RemoteRecord->isInhibited())
        {
            return;
        }

        /* Allocate some memory */
        OUTMESS *OutMessage = new OUTMESS;

        //  send a time sync to this guy
        OutMessage->DeviceID    = RemoteRecord->getID();
        OutMessage->Port        = RemoteRecord->getPortID();
        OutMessage->Remote      = RemoteRecord->getAddress();
        OutMessage->TimeOut     = 2;
        OutMessage->Retry       = 0;
        OutMessage->Sequence    = CtiProtocolLMI::Sequence_TimeSync;  //  a relatively unique value, just for safety's sake
        OutMessage->Priority    = MAXPRIORITY - 2;
        OutMessage->EventCode   = NOWAIT | NORESULT | ENCODED | TSYNC;
        OutMessage->ReturnNexus = NULL;
        OutMessage->SaveNexus   = NULL;
        OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
        OutMessage->ExpirationTime = getNextTimeSync();

        if(PortManager.writeQueue(OutMessage))
        {
            CTILOG_ERROR(dout, "Could not write to port queue for DeviceID "<< OutMessage->DeviceID <<" / Port "<< OutMessage->Port);
            delete OutMessage;
        }
    }
};


struct timeSyncCCU721
{
    long port_id;

    timeSyncCCU721(long port_id_) : port_id(port_id_) {  };

    void operator()(CtiDeviceSPtr &RemoteRecord)
    {
        if(RemoteRecord->getPortID() != port_id || RemoteRecord->isInhibited())
        {
            return;
        }

        /* Allocate some memory */
        OUTMESS *OutMessage = new OUTMESS;

        using Cti::Devices::Ccu721Device;
        boost::shared_ptr<Ccu721Device> ccu = boost::static_pointer_cast<Ccu721Device>(RemoteRecord);

        if( ccu->buildCommand(OutMessage, Ccu721Device::Command_Timesync) )
        {
            OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
            OutMessage->ExpirationTime = getNextTimeSync();

            if(PortManager.writeQueue(OutMessage))
            {
                CTILOG_ERROR(dout, "Could not write to port queue for DeviceID "<< OutMessage->DeviceID <<" / Port "<< OutMessage->Port);
                delete OutMessage;
            }
        }
    }
};


struct timeSyncDNPDevices
{
    long port_id;

    timeSyncDNPDevices(long port_id_) : port_id(port_id_) {  };

    void operator()(CtiDeviceSPtr &RemoteRecord)
    {
        if( RemoteRecord->getPortID() != port_id || RemoteRecord->isInhibited())
        {
            return;
        }

        Cti::Config::DeviceConfigSPtr config = RemoteRecord->getDeviceConfig();
        if( !config )
        {
            CTILOG_ERROR(dout, "Device "<< RemoteRecord->getName() <<" is not assigned a DNP configuration. Unable to process DNP timesync.");

            return;
        }

        const bool dnpTimesyncEnabled = ciStringEqual(config->getValueFromKey(DNPStrings::enableDnpTimesyncs), "true");

        if( dnpTimesyncEnabled )
        {
            /* Allocate some memory */
            OUTMESS *OutMessage = new OUTMESS;

            using Cti::Devices::DnpDevice;
            boost::shared_ptr<DnpDevice> dnp_device = boost::static_pointer_cast<DnpDevice>(RemoteRecord);

            CtiRequestMsg request(RemoteRecord->getID(), "putconfig timesync");

            list<CtiMessage *> stub;
            list<OUTMESS *> outlist;

            dnp_device->ExecuteRequest(&request, CtiCommandParser(request.CommandString()), OutMessage, stub, stub, outlist);

            while( !stub.empty() )
            {
                delete stub.back();
                stub.pop_back();
            }

            while( !outlist.empty() )
            {
                OutMessage = outlist.back();

                OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
                OutMessage->ExpirationTime = getNextTimeSync();

                if(PortManager.writeQueue(OutMessage))
                {
                    CTILOG_ERROR(dout, "Could not write to port queue for DeviceID "<< OutMessage->DeviceID <<" / Port "<< OutMessage->Port);
                    delete OutMessage;
                }

                outlist.pop_back();
            }
        }
    }
};


//  send out the 400-series time sync on all default routes on this port, since we can't use the normal CCU broadcast format
static void applyMCT400TimeSync(const long key, CtiRouteSPtr pRoute, void* d)
{
    long portid = (long)d;
    CtiDeviceSPtr RemoteRecord;

    try
    {
        RemoteRecord = DeviceManager.getDeviceByID(pRoute->getTrxDeviceID());

        //  make sure the route's transmitting device is on this port...  and make sure this is a default route
        if( RemoteRecord &&
            (RemoteRecord->getType() == TYPE_CCU700 ||
             RemoteRecord->getType() == TYPE_CCU710 ||
             RemoteRecord->getType() == TYPE_CCU711) &&  //  note that we're not including the 721 here - it has timesync broadcast abaility of its own
            (RemoteRecord->getPortID() == portid) && pRoute->isDefaultRoute() )
        {
            using Cti::Devices::Mct4xxDevice;
            using namespace Cti::Protocols;

            BSTRUCT message;
            OUTMESS *OutMessage = new OUTMESS;

            bool stages_supported = !(RemoteRecord->getType() == TYPE_CCU700 || RemoteRecord->getType() == TYPE_CCU710);

            //  load up all of the port/route specific items
            OutMessage->DeviceID  = pRoute->getTrxDeviceID();
            OutMessage->Port      = portid;
            OutMessage->Remote    = RemoteRecord->getAddress();
            OutMessage->TimeOut   = TIMEOUT;
            OutMessage->Retry     = 0;
            OutMessage->Sequence  = 0;
            OutMessage->Priority  = MAXPRIORITY;
            OutMessage->EventCode = NOWAIT | NORESULT | DTRAN | BWORD | TSYNC;
            OutMessage->Command   = CMND_DTRAN;
            OutMessage->InLength  = 0;
            OutMessage->ReturnNexus = NULL;
            OutMessage->SaveNexus   = NULL;
            OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
            OutMessage->ExpirationTime = getNextTimeSync();

            OutMessage->Buffer.BSt.Port    = RemoteRecord->getPortID();
            OutMessage->Buffer.BSt.Remote  = RemoteRecord->getAddress();
            //  this is key - this, and the TSYNC flag, are what get the 400-series time loaded into the message
            OutMessage->Buffer.BSt.Address  = Mct4xxDevice::UniversalAddress;
            OutMessage->Buffer.BSt.Function = Mct4xxDevice::FuncWrite_TSyncPos;
            OutMessage->Buffer.BSt.Length   = Mct4xxDevice::FuncWrite_TSyncLen;
            OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Write;
            //  we don't fill in the data because it's filled in by RefreshMCTTimeSync() later on

            OutMessage->Buffer.BSt.DlcRoute.Amp        = ((CtiDeviceIDLC *)(RemoteRecord.get()))->getIDLC().getAmp();
            OutMessage->Buffer.BSt.DlcRoute.Bus        = pRoute->getBus();
            OutMessage->Buffer.BSt.DlcRoute.RepVar     = pRoute->getCCUVarBits();
            OutMessage->Buffer.BSt.DlcRoute.RepFixed   = pRoute->getCCUFixBits();
            OutMessage->Buffer.BSt.DlcRoute.Stages     = (stages_supported)?(pRoute->getStages()):(0);  //  must set stages to 0 or the timesync will fail on the 700/710

            //  because we're not executing on the route, we have to do this manually
            EmetconProtocol::buildBWordMessage(OutMessage);

            if(PortManager.writeQueue(OutMessage))
            {
                CTILOG_ERROR(dout, "Could not write to port queue for DeviceID "<< OutMessage->DeviceID <<" / Port "<< OutMessage->Port);
                delete OutMessage;
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Failed to send MCT 400 series timesyncs");
    }

}


/* Routine to generate needed time sync messages */
static void applyPortSendTime(const long unusedid, CtiPortSPtr PortRecord, void *unusedPtr)
{
    OUTMESS     *OutMessage;
    CtiDeviceSPtr RemoteRecord;

    {
        /* check for a remote port */
        if(PortRecord->getName()[0] == '@') return;
        if(PortRecord->isInhibited()) return;

        if(isForeignCcuPort(PortRecord->getPortID())) return;

        if(PortRecord->getProtocolWrap() == ProtocolWrapIDLC)
        {
            /* make sure that the broadcast ccu is defined */
            if(RemoteRecord = DeviceManager.RemoteGetPortRemoteEqual (PortRecord->getPortID(), CCUGLOBAL))
            {
                if(RemoteRecord->isInhibited())
                {
                    return;
                }

                /* Allocate some memory */
                if((OutMessage = CTIDBG_new OUTMESS) == NULL)
                {
                    return;
                }

                /* send a time sync to this guy */
                OutMessage->DeviceID = RemoteRecord->getID();
                OutMessage->Port = PortRecord->getPortID();
                OutMessage->Remote = CCUGLOBAL;
                OutMessage->TimeOut = TIMEOUT;
                OutMessage->Retry = 0;
                OutMessage->OutLength = 10;
                OutMessage->InLength = 0;
                OutMessage->Source = 0;
                OutMessage->Destination = DEST_TSYNC;
                OutMessage->Command = CMND_XTIME;
                OutMessage->Sequence = 0;
                OutMessage->Priority = MAXPRIORITY;
                OutMessage->EventCode = NOWAIT | NORESULT | ENCODED | TSYNC;
                OutMessage->ReturnNexus = NULL;
                OutMessage->SaveNexus = NULL;
                OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
                OutMessage->ExpirationTime = getNextTimeSync();

                if(PortRecord->writeQueue(OutMessage, PortThread))
                {
                    CTILOG_ERROR(dout, "Could not write to port queue for DeviceID "<< OutMessage->DeviceID <<" / Port "<< OutMessage->Port);
                    delete (OutMessage);
                }
            }
            else
            {
                /* Broadcast ccu does not exist on this port */
                CtiPortManager::coll_type::reader_lock_guard_t guard(PortManager.getLock());  // this applyFunc Writes to the PortManager queues!

                vector<CtiDeviceSPtr> ccu_devices;

                DeviceManager.getDevicesByType(TYPE_CCU711, ccu_devices);

                for_each(ccu_devices.begin(), ccu_devices.end(), timeSyncCCU711(PortRecord->getPortID()));
            }
        }
        else
        {
            /* we need to walk through and generate to 710's on this port */
            CtiPortManager::coll_type::reader_lock_guard_t guard(PortManager.getLock());  // this applyFunc Writes to the PortManager queues!

            vector<CtiDeviceSPtr> ccu_devices;

            DeviceManager.getDevicesByType(TYPE_CCU700, ccu_devices);
            DeviceManager.getDevicesByType(TYPE_CCU710, ccu_devices);

            for_each(ccu_devices.begin(), ccu_devices.end(), timeSyncCCU710(PortRecord->getPortID()));
        }

        {
            /* Now check for Anything not covered by above */
            CtiPortManager::coll_type::reader_lock_guard_t guard(PortManager.getLock());  // these timeSync...() functions write to the PortManager queues!

            vector<CtiDeviceSPtr> devices;

            DeviceManager.getDevicesByType(TYPE_TDMARKV,  devices);
            for_each(devices.begin(), devices.end(), timeSyncTDMarkV(PortRecord->getPortID()));
            devices.clear();

            DeviceManager.getDevicesByType(TYPE_ILEXRTU,  devices);
            for_each(devices.begin(), devices.end(), timeSyncILEX(PortRecord->getPortID()));
            devices.clear();

            DeviceManager.getDevicesByType(TYPE_WELCORTU, devices);
            for_each(devices.begin(), devices.end(), timeSyncWelco_VTU(PortRecord->getPortID()));
            devices.clear();

            DeviceManager.getDevicesByType(TYPE_SERIESVLMIRTU, devices);
            for_each(devices.begin(), devices.end(), timeSyncSeriesVLMIRTU(PortRecord->getPortID()));
            devices.clear();

            DeviceManager.getDevicesByType(TYPE_CCU721,   devices);
            for_each(devices.begin(), devices.end(), timeSyncCCU721(PortRecord->getPortID()));
            devices.clear();

            DeviceManager.getDevicesByType(TYPE_DNPRTU,   devices);
            DeviceManager.getDevicesByType(TYPE_DARTRTU,  devices);
            DeviceManager.getDevicesByType(TYPECBCDNP,    devices);
            DeviceManager.getDevicesByType(TYPECBC7020,   devices);
            DeviceManager.getDevicesByType(TYPECBC8020,   devices);
            for_each(devices.begin(), devices.end(), timeSyncDNPDevices(PortRecord->getPortID()));
            devices.clear();

            CtiDeviceManager::coll_type::reader_lock_guard_t dvguard(DeviceManager.getLock());  // Deadlock avoidance!
            RouteManager.apply(applyMCT400TimeSync, (void*)PortRecord->getPortID());
        }
    }

    return;
}

/* Routine to generate the basic time sync messages... time filled in at port */
void TimeSyncThread()
{
    /* See if we should even be running */
    if(TimeSyncRate <= 0)
    {
        /* We are history */
        return;
    }

    ThreadStatusKeeper threadStatus("Time Sync Thread");

    CTILOG_INFO(dout, "TimeSyncThread started (Sync every "<< TimeSyncRate <<" seconds)");

    /* Let the port routines get started */
    if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[ P_QUIT_EVENT ], 60000L) )
    {
        return;     // If we get this we never really got to run...
    }

    /* send out time syncs for the first time... */
    PortManager.apply(applyPortSendTime, NULL);

    HANDLE hTimeSyncArray[] = {
        hPorterEvents[P_QUIT_EVENT],
        hPorterEvents[P_TIMESYNC_EVENT]
    };

    /* loop doing time sync at 150 seconds after the hour */
    while( PorterQuit != TRUE )
    {
        threadStatus.monitorCheck(TimeSyncRate + CtiThreadMonitor::StandardMonitorTime, CtiThreadRegData::None);

        ResetEvent(hPorterEvents[P_TIMESYNC_EVENT]);

        const ULONG EventWait = 1000L * (getNextTimeSync() - CtiTime().seconds());

        const DWORD dwWait = WaitForMultipleObjects(2, hTimeSyncArray, FALSE, EventWait);

        switch( dwWait )
        {
            case WAIT_OBJECT_0: // P_QUIT_EVENT:
            {
                PorterQuit = TRUE;

                return;
            }
            case WAIT_TIMEOUT:          // the normal case - waiting undisturbed until the next time sync
            case WAIT_OBJECT_0 + 1:     // P_TIMESYNC_EVENT:
            {
                PortManager.apply(applyPortSendTime, NULL);

                continue;
            }
            default:
            {
                CTILOG_ERROR(dout, "WaitForMultipleObjects returned "<< dwWait);

                Sleep(1000);

                continue;
            }
        }
    }
}

/* Routine called to load an XTIME message */
INT LoadXTimeMessage (BYTE *Message)
{
    ULONG Mod8Time;
    struct timeb TimeB;
    struct tm *TimeSt;

    /* get the time from the system */
    UCTFTime (&TimeB);

    TimeSt = UCTLocalTime (TimeB.time, TimeB.dstflag);

    /* Build up the time sync message */
    Message[6] = HIBYTE (TimeSt->tm_year + 1900);
    Message[7] = LOBYTE (TimeSt->tm_year + 1900);

    Message[8] = HIBYTE (TimeSt->tm_yday + 1);
    Message[9] = LOBYTE (TimeSt->tm_yday + 1);

    Message[10] = TimeSt->tm_wday + 1;

    Message[11] = TimeSt->tm_hour / 8;

    Mod8Time = (ULONG)(TimeSt->tm_hour % 8) * 3600L + (ULONG)TimeSt->tm_min * 60L + (ULONG)TimeSt->tm_sec;

    Message[12] = HIBYTE (Mod8Time);
    Message[13] = LOBYTE (Mod8Time);

    return ClientErrors::None;
}


INT RefreshMCTTimeSync(OUTMESS *OutMessage)
{
    struct timeb TimeB;
    struct tm TimeSt;
    USHORT EmetDay;
    USHORT Hour;
    USHORT EmetHTime;
    USHORT EmetFTime;

    int address   = 0,
        wordcount = 0,
        function  = 0,
        io        = 0;

    int length    = 0;


    //  this is where the processed B word starts...
    //    note that we should NEVER get an unprocessed B word through here;  only DTRAN messages, NEVER queued
    unsigned char *b_word = OutMessage->Buffer.OutMessage + PREIDLEN + PREAMLEN;
    unsigned char timesync_message[13];

    address  |= (b_word[1] & 0x0f) << 18;
    address  |=  b_word[2]         << 10;
    address  |=  b_word[3]         <<  2;
    address  |= (b_word[4] & 0xc0) >>  6;

    wordcount = (b_word[4] & 0x30) >> 4;

    function |= (b_word[4] & 0x0f) << 4;
    function |= (b_word[5] & 0xf0) >> 4;

    io        = (b_word[5] & 0x0c) >> 2;

    using Cti::Devices::DlcBaseDevice;
    using Cti::Devices::MctDevice;
    using Cti::Devices::Mct4xxDevice;
    using namespace Cti::Protocols;

    if( address == Mct4xxDevice::UniversalAddress
          || (io == EmetconProtocol::IO_Function_Write
                && function  == Mct4xxDevice::FuncWrite_TSyncPos
                && wordcount == 2) )  //  the 4xx has 6 bytes to write - two C words
    {
        length = Mct4xxDevice::loadTimeSync(timesync_message);
    }
    else if( address == DlcBaseDevice::BroadcastAddress
               || (io == EmetconProtocol::IO_Write
                     && function  == MctDevice::Memory_TSyncPos
                     && wordcount == 1) )  //  non-400-series MCT timesyncs have 5 bytes to write - one C word
    {
        //  this is the normal MCT timesync
        UCTFTime (&TimeB);

        UCTLocoTime (TimeB.time, TimeB.dstflag, &TimeSt);

        //  figure out how many 15 second intervals left in this 5 minutes
        EmetFTime = 20 - ((TimeSt.tm_min % 5) * 60 + TimeSt.tm_sec) / 15;

        //  figure out how many AM/PMs left in the week
        Hour = TimeSt.tm_hour;
        EmetDay = (7 - TimeSt.tm_wday) * 2;
        if(Hour > 11)
        {
            EmetDay--;
            Hour -= 12;
        }

        //  figure out how many 5 minute periods left in this 12 hours
        EmetHTime = 144 - ((Hour * 12) + (TimeSt.tm_min / 5));

        timesync_message[0] = EmetFTime;
        timesync_message[1] = EmetHTime;
        timesync_message[2] = EmetDay;
        timesync_message[3] = DLCFreq1;  //  these are globals based on the system frequencies
        timesync_message[4] = DLCFreq2;  //    (12.5, 9.6, etc)

        length = 5;

        //OutMessage->Destination = 0;
    }
    else
    {
        CTILOG_ERROR(dout, "unknown timesync (address = "<< address <<", io = "<< io <<", function = "<< function <<", wordcount = "<< wordcount <<")");
    }

    //  lay it over the original message
    C_Words( (OutMessage->Buffer.OutMessage + PREIDLEN + PREAMLEN + BWORDLEN),
              timesync_message,
              length );

    return ClientErrors::None;
}


/* Routine to stuff the two byte header */
INT ILEXHeader (PBYTE Header,          /* Pointer to message */
            USHORT Remote,          /* RTU Remote */
            USHORT Function,        /* Function code */
            USHORT SubFunction1,    /* High order sub function code */
            USHORT SubFunction2)    /* Low order sub function code */

{
    Header[0] = (Function & 0x0007);
    Header[0] |= LOBYTE ((Remote << 5) & 0xe0);
    if (SubFunction1)
        Header[0] |= 0x10;
    if (SubFunction2)
        Header[0] |= 0x08;
    Header[1] = LOBYTE (Remote >> 3);
    return ClientErrors::None;

}

/* Routine to load up time for an ilex rtu */
INT LoadILEXTimeMessage (BYTE *Message, USHORT MilliSecsSkew)
{
    ULONG aMilliTime;
    struct timeb TimeB;
    struct tm TimeSt;

    /* get the time from the system */
    UCTFTime (&TimeB);

    TimeB.millitm += MilliSecsSkew;

    /* Add in the extra seconds */
    TimeB.time += (TimeB.millitm / 1000);

    /* Readjust milliseconds */
    TimeB.millitm %= 1000;

    UCTLocoTime (TimeB.time,
                 TimeB.dstflag,
                 &TimeSt);

    /* Calculate the milliseconds */
    aMilliTime = ((TimeSt.tm_hour * 60L + TimeSt.tm_min) * 60L + TimeSt.tm_sec) * 1000L + TimeB.millitm;

    /* Move it into the message */
    Message[2] |= (HIBYTE (HIUSHORT(aMilliTime)) << 5) & 0xE0;
    Message[3] = LOBYTE (HIUSHORT (aMilliTime));
    Message[4] = HIBYTE (LOUSHORT (aMilliTime));
    Message[5] = LOBYTE (LOUSHORT (aMilliTime));

    /* Load up the Day of week and day of month */
    Message[6] = LOBYTE ((TimeSt.tm_wday + 1) << 5 | TimeSt.tm_mday);

    /* Load the day of the month */
    Message[7] = LOBYTE (TimeSt.tm_mon + 1);

    /* Load Up the year */
    Message[8] = HIBYTE (TimeSt.tm_year + 1900);
    Message[9] = LOBYTE (TimeSt.tm_year + 1900);

    return ClientErrors::None;
}


/* Routine to load up time for a Welco rtu */
INT LoadWelcoTimeMessage (BYTE *Message,
                      USHORT MilliSecsSkew)
{
    struct timeb TimeB;
    struct tm TimeSt;

    /* get the time from the system */
    UCTFTime (&TimeB);

    TimeB.millitm += MilliSecsSkew;

    /* Add in the extra seconds */
    TimeB.time += (TimeB.millitm / 1000);

    /* Readjust milliseconds */
    TimeB.millitm %= 1000;

    UCTLocoTime (TimeB.time,
                 TimeB.dstflag,
                 &TimeSt);

    /* Move it into the message */
    Message[0] = TimeSt.tm_mon + 1;
    Message[1] = TimeSt.tm_mday;
    Message[2] = TimeSt.tm_hour;
    Message[3] = TimeSt.tm_min;
    Message[4] = TimeSt.tm_sec;

    /* Load the milliseconds */
    Message[5] = LOBYTE (TimeB.millitm);
    Message[6] = HIBYTE (TimeB.millitm);

    return ClientErrors::None;
}


/* Routine to load up time for a Welco rtu */
INT LoadSES92TimeMessage (BYTE *Message,
                      USHORT MilliSecsSkew)
{
    struct timeb TimeB;
    struct tm TimeSt;

    /* get the time from the system */
    UCTFTime (&TimeB);

    TimeB.millitm += MilliSecsSkew;

    /* Add in the extra seconds */
    TimeB.time += (TimeB.millitm / 1000);

    /* Readjust milliseconds */
    TimeB.millitm %= 1000;

    UCTLocoTime (TimeB.time,
                 TimeB.dstflag,
                 &TimeSt);

    /* Move it into the message */
    Message[2] = HIBYTE (TimeSt.tm_yday + 1);
    Message[3] = LOBYTE (TimeSt.tm_yday + 1);
    Message[4] = TimeSt.tm_hour;
    Message[5] = TimeSt.tm_min;
    Message[6] = TimeSt.tm_sec;

    /* Load the milliseconds */
    Message[7] = HIBYTE (TimeB.millitm);
    Message[8] = LOBYTE (TimeB.millitm);

    return ClientErrors::None;
}


#pragma once

#include <windows.h>

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_remote.h"
#include "logger.h"
#include "tbl_dv_idlcremote.h"
#include "trx_info.h"
#include "trx_711.h"

class CtiDeviceIDLC : public CtiDeviceRemote
{
    typedef CtiDeviceRemote Inherited;

protected:

   CtiTableDeviceIDLC   _idlc;
   mutable CtiTransmitterInfo   *_trxInfo;

public:

    CtiDeviceIDLC() :
        _trxInfo(NULL)
    {
    }

    virtual ~CtiDeviceIDLC()
    {
        if(_trxInfo != NULL)
        {
            delete _trxInfo;
            _trxInfo = NULL;
        }
    }

    CtiTableDeviceIDLC& getIDLC()
    {
        return _idlc;
    }

    const CtiTableDeviceIDLC& getIDLC() const
    {
        return _idlc;
    }

    virtual LONG getAddress() const
    {
        return _idlc.getAddress();
    }
    virtual INT  getPostDelay() const
    {
        return _idlc.getPostDelay();
    }

    virtual std::string getSQLCoreStatement() const
    {
        static const std::string sqlCore =
            "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
              "YP.disableflag, DV.deviceid, DV.alarminhibit, DV.controlinhibit, CS.portid, "
              "DUS.phonenumber, DUS.minconnecttime, DUS.maxconnecttime, DUS.linesettings, "
              "DUS.baudrate, IDLC.address, IDLC.postcommwait, IDLC.ccuampusetype "
            "FROM Device DV, DeviceIDLCRemote IDLC, DeviceDirectCommSettings CS, YukonPAObject YP "
              "LEFT OUTER JOIN DeviceDialupSettings DUS ON YP.paobjectid = DUS.deviceid "
            "WHERE YP.paobjectid = IDLC.deviceid AND YP.paobjectid = DV.deviceid AND "
              "YP.paobjectid = CS.deviceid";

        return sqlCore;
    }

    void DecodeDatabaseReader(Cti::RowReader &rdr ) override
    {
        Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

        if(getDebugLevel() & DEBUGLEVEL_DATABASE)
        {
            CTILOG_DEBUG(dout, "Decoding DB reader");
        }

        _idlc.DecodeDatabaseReader(rdr);
    }

    bool ownsTrxInfo() const // Porter side info to retrieve transmitter device bookkeeping!
    {
        return(_trxInfo != 0);
    }

    virtual CtiTransmitterInfo* getTrxInfo() // Porter side info to retrieve transmitter device bookkeeping!
    {
        if(!_trxInfo)
        {
            initTrxInfo();
            _trxInfo->setStatus( NEEDSRESET );
        }
        return _trxInfo;
    }
    virtual bool hasTrxInfo() const    // This device type does indeed have a TrxInfo!
    {
        return true;
    }
    virtual CtiTransmitterInfo* initTrxInfo()  // Porter side info to setup transmitter device bookkeeping!
    {
        // set up and return the _trxInfo

        if(_trxInfo != NULL)
        {
            CTILOG_ERROR(dout, "UNEXPECTED _trxInfo is not NULL");

            delete _trxInfo;
        }

        switch( getType() )
        {
            case TYPE_CCU711:
            {
                CtiTransmitter711Info *p711 = CTIDBG_new CtiTransmitter711Info(getType());

                /* create the queue */
                if(CreateQueue (&p711->QueueHandle))
                {
                    printf ("Error Creating CCU Queue\n");
                }

                /* create the queue */
                if(CreateQueue (&p711->ActinQueueHandle))
                {
                    printf ("Error Creating Actin Queue\n");
                }

                _trxInfo = p711;

                break;

            }
            default:
            {
                _trxInfo = CTIDBG_new CtiTransmitterInfo(getType());

                break;
            }
        }

        if(getAddress() > 0)
        {
            CTILOG_INFO(dout, "Enabling P: "<< getPortID() <<" D: "<< getID() <<" / "<< getName() <<". DLC ID: "<< getAddress());
        }
        else
        {
            CTILOG_INFO(dout, "Enabling P: "<< getPortID() <<" D: "<< getID() <<" / "<< getName());
        }

        return _trxInfo;
    }

    virtual INT getProtocolWrap() const
    {
        return ProtocolWrapIDLC;
    }

};

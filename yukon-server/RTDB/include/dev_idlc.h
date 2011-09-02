#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

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
private:

    typedef CtiDeviceRemote Inherited;

protected:

   CtiTableDeviceIDLC   _idlc;
   mutable CtiTransmitterInfo   *_trxInfo;

public:

    CtiDeviceIDLC() :
        _trxInfo(NULL)
    {
    }

    CtiDeviceIDLC(const CtiDeviceIDLC& aRef) :
        _trxInfo(NULL)
    {
        *this = aRef;
    }

    virtual ~CtiDeviceIDLC()
    {
        if(_trxInfo != NULL)
        {
            delete _trxInfo;
            _trxInfo = NULL;
        }
    }

    CtiDeviceIDLC& operator=(const CtiDeviceIDLC& aRef)
    {
        Inherited::operator=(aRef);

        if(this != &aRef)
        {
            _idlc = aRef.getIDLC();

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << FO(__FILE__) << " (" << __LINE__ << ")" << std::endl;
            }

            if(_trxInfo != NULL)
            {
                delete _trxInfo;
                _trxInfo = NULL;
            }

            if( aRef.ownsTrxInfo() != NULL )
            {
                initTrxInfo();
                *_trxInfo = *(aRef._trxInfo); // Do an init + assignment
            }
        }
        return *this;
    }

    CtiTableDeviceIDLC  getIDLC() const
    {
        return _idlc;
    }

    CtiTableDeviceIDLC& getIDLC()
    {
        return _idlc;
    }

    CtiDeviceIDLC& setIDLC( const CtiTableDeviceIDLC &aRef )
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);
        _idlc = aRef;
        return *this;
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

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr)
    {
        Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

        if(getDebugLevel() & DEBUGLEVEL_DATABASE)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Decoding " << FO(__FILE__) << " (" << __LINE__ << ")" << std::endl;
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
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** UNEXPECTED Checkpoint **** " << FO(__FILE__) << " (" << __LINE__ << ")" << std::endl;
            }

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
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Enabling P: " << getPortID() << " D: " << getID() << " / " << getName() << ". DLC ID: " << getAddress() << std::endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Enabling P: " << getPortID() << " D: " << getID() << " / " << getName() << std::endl;
        }

        return _trxInfo;
    }

    virtual INT getProtocolWrap() const
    {
        return ProtocolWrapIDLC;
    }

};

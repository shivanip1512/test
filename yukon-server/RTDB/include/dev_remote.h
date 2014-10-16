#pragma once

#include "dev_single.h"
#include "tbl_dialup.h"
#include "tbl_direct.h"
#include "porter.h"  //  for RESULT and ENCODED

#include "string_utility.h"

class CtiDeviceRemote : public CtiDeviceSingle
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceRemote(const CtiDeviceRemote&);
    CtiDeviceRemote& operator=(const CtiDeviceRemote&);

    typedef CtiDeviceSingle   Inherited;

protected:

    CtiTableDeviceDirectComm   Direct;
    CtiTableDeviceDialup       *pDialup;

    void populateRemoteOutMessage(OUTMESS &OutMessage)
    {
        populateOutMessage(OutMessage);

        OutMessage.EventCode = RESULT | ENCODED;
        OutMessage.Sequence  = 0;
        OutMessage.Retry     = 2;
    }

public:

    CtiDeviceRemote() :
    pDialup(NULL)
    {}

    virtual ~CtiDeviceRemote()
    {
        if(pDialup != NULL)
        {
            delete pDialup;
        }
    }

    virtual bool isDialup() const
    {
        return (pDialup != NULL);
    }

    virtual INT getBaudRate() const
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);

        INT baud = 0;

        if(isDialup())
        {
            baud = pDialup->getBaudRate();
        }

        return baud;
    }

    virtual INT getBits() const
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);

        INT val = 8;

        if(isDialup())
        {
            val = pDialup->getBits();
        }

        return val;
    }

    virtual INT getStopBits() const
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);

        INT val = ONESTOPBIT;

        if(isDialup())
        {
            val = pDialup->getStopBits();
        }

        return val;
    }
    virtual INT getParity() const
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);

        INT val = NOPARITY;

        if(isDialup())
        {
            val = pDialup->getParity();
        }

        return val;
    }

    virtual std::string getSQLCoreStatement() const
    {
        static const std::string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                         "YP.disableflag, DV.deviceid, DV.alarminhibit, DV.controlinhibit, CS.portid, "
                                         "DUS.phonenumber, DUS.minconnecttime, DUS.maxconnecttime, DUS.linesettings, "
                                         "DUS.baudrate "
                                       "FROM Device DV, DeviceDirectCommSettings CS, YukonPAObject YP LEFT OUTER JOIN "
                                         "DeviceDialupSettings DUS ON YP.paobjectid = DUS.deviceid "
                                       "WHERE YP.paobjectid = DV.deviceid AND YP.paobjectid = CS.deviceid";

        return sqlCore;
    }

    void DecodeDatabaseReader(Cti::RowReader &rdr) override
    {
        INT iTemp;

        Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

        if(getDebugLevel() & DEBUGLEVEL_DATABASE)
        {
            CTILOG_DEBUG(dout, "Decoding DB reader");
        }

        Direct.DecodeDatabaseReader(rdr);

        /*
         * Check if we are a dialup
         * new some memory if we are and call the decoder on it!
         */

        if(!rdr["phonenumber"].isNull())
        {
            std::string tempstr;
            rdr["phonenumber"] >> tempstr;

            if(tempstr.length() > 1)
            {
                CtiLockGuard<CtiMutex> guard(_classMutex);

                // There was a dialup
                // allocate only if we haven't been here before
                if(pDialup == NULL)
                {
                    pDialup = new CtiTableDeviceDialup;
                }
                pDialup->DecodeDatabaseReader(rdr);
            }
            else
            {
                if(isDebugLudicrous())
                {
                    //FIXME: is this debug or error log?
                    CTILOG_ERROR(dout, "Invalid DIALUPDEVICESETTINGS row for device (phonenumber is too short) "<< getID() <<" = "<< getName());
                }
            }
        }
    }

    virtual LONG getPortID() const
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);

        return Direct.getPortID();
    }

    virtual ULONG getUniqueIdentifier() const
    {
        ULONG CSum = 0;

        if(pDialup)
        {
            std::string num;
            CHAR ch[2] = {'\0', '\0'};

            for(int i = 0; i < pDialup->getPhoneNumber().length(); i++ )
            {
                ch[0] = pDialup->getPhoneNumber()[(size_t)i];

                if( ::isdigit(ch[0]) )
                    num += std::string(ch);
            }

            // Now get a standard CRC
            CSum = (ULONG)CCITT16CRC( 0, (BYTE*)num.c_str(), num.length(), FALSE);
        }
        else
        {
            CSum = Inherited::getUniqueIdentifier();
        }

        return CSum;
    }

    virtual std::string getPhoneNumber() const
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);

        std::string   rStr;
        if(pDialup)
        {
            rStr = pDialup->getPhoneNumber();
        }
        return rStr;
    }

    virtual LONG getMinConnectTime() const
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);

        LONG mct = 0;
        if(pDialup)
        {
            mct = pDialup->getMinConnectTime();
        }
        else
        {
            mct = Inherited::getMinConnectTime();
        }
        return mct;
    }

    virtual LONG getMaxConnectTime() const
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);

        LONG mct = 0;
        if(pDialup)
        {
            mct = pDialup->getMaxConnectTime();
        }
        else
        {
            mct = Inherited::getMaxConnectTime();
        }
        return mct;
    }

};


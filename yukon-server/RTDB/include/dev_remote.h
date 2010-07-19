/*-----------------------------------------------------------------------------*
*
* File:   dev_remote
*
* Class:  CtiDeviceRemote
* Date:   8/18/1999
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_remote.h-arc  $
* REVISION     :  $Revision: 1.21.2.1 $
* DATE         :  $Date: 2008/11/18 20:11:29 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_REMOTE_H__
#define __DEV_REMOTE_H__


#include <ctype.h>

#include <rw\thr\mutex.h>
#include <rw\db\nullind.h>


#include "dev_single.h"
#include "tbl_dialup.h"
#include "tbl_direct.h"


class IM_EX_DEVDB CtiDeviceRemote : public CtiDeviceSingle
{
private:

    typedef CtiDeviceSingle   Inherited;

protected:

    CtiTableDeviceDirectComm   Direct;
    CtiTableDeviceDialup       *pDialup;

public:

    CtiDeviceRemote() :
    pDialup(NULL)
    {}

    CtiDeviceRemote(const CtiDeviceRemote& aRef) :
    pDialup(NULL)
    {
        *this = aRef;
    }

    virtual ~CtiDeviceRemote()
    {
        if(pDialup != NULL)
        {
            delete pDialup;
        }
    }

    CtiDeviceRemote& operator=(const CtiDeviceRemote& aRef)
    {
        Inherited::operator=(aRef);

        if(this != &aRef)
        {
            CtiLockGuard<CtiMutex> guard(_classMutex);

            Direct = aRef.getDirect();

            if(isDialup())             // If I was a dialup free up the old stuff
            {
                delete pDialup;
                pDialup = NULL;
            }

            if(aRef.isDialup())
            {
                // Copy the dialup stuff
                pDialup = CTIDBG_new CtiTableDeviceDialup;
                *pDialup = aRef.getDialup();
            }
        }
        return *this;
    }

    const CtiTableDeviceDirectComm &getDirect() const
    {
        return Direct;
    }
    CtiTableDeviceDirectComm& getDirect()
    {
        return Direct;
    }
    CtiDeviceRemote& setDirect( const CtiTableDeviceDirectComm & aDirect )
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);
        Direct = aDirect;
        return *this;
    }

    CtiTableDeviceDialup*  getDialup()
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);
        return pDialup;
    }

    const CtiTableDeviceDialup  getDialup() const
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);
        return *pDialup;
    }

    CtiDeviceRemote& setDialup( CtiTableDeviceDialup *aDialup )
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);
        if(pDialup != NULL)
        {
            delete pDialup;
            pDialup = NULL;
        }

        pDialup = aDialup;
        return *this;
    }

    virtual bool isDialup() const
    {
        return (pDialup != NULL);
    }

    virtual INT getBaudRate() const
    {
        INT baud = 0;

        if(isDialup())
        {
            baud = pDialup->getBaudRate();
        }

        return baud;
    }

    virtual INT getBits() const
    {
        INT val = 8;

        if(isDialup())
        {
            val = pDialup->getBits();
        }

        return val;
    }

    virtual INT getStopBits() const
    {
        INT val = ONESTOPBIT;

        if(isDialup())
        {
            val = pDialup->getStopBits();
        }

        return val;
    }
    virtual INT getParity() const
    {
        INT val = NOPARITY;

        if(isDialup())
        {
            val = pDialup->getParity();
        }

        return val;
    }

    virtual string getSQLCoreStatement() const
    {
        static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                         "YP.disableflag, DV.deviceid, DV.alarminhibit, DV.controlinhibit, CS.portid, "
                                         "DUS.phonenumber, DUS.minconnecttime, DUS.maxconnecttime, DUS.linesettings, "
                                         "DUS.baudrate "
                                       "FROM Device DV, DeviceDirectCommSettings CS, YukonPAObject YP LEFT OUTER JOIN "
                                         "DeviceDialupSettings DUS ON YP.paobjectid = DUS.deviceid "
                                       "WHERE YP.paobjectid = DV.deviceid AND YP.paobjectid = CS.deviceid";
    
        return sqlCore;
    }

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr)
    {
        INT iTemp;

        Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

        if(getDebugLevel() & DEBUGLEVEL_DATABASE)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        Direct.DecodeDatabaseReader(rdr);

        /*
         * Check if we are a dialup
         * new some memory if we are and call the decoder on it!
         */

        if(!rdr["phonenumber"].isNull())
        {
            string tempstr;
            rdr["phonenumber"] >> tempstr;

            if(tempstr.length() > 1)
            {
                // There was a dialup
                // allocate only if we haven't been here before
                if(pDialup == NULL)
                {
                    pDialup = CTIDBG_new CtiTableDeviceDialup;
                }
                pDialup->DecodeDatabaseReader(rdr);
            }
            else
            {
                if(isDebugLudicrous())
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** ERROR ****  Invalid DIALUPDEVICESETTINGS row for device (phonenumber is too short) " << getID() << " = " << getName() << endl;
                }
            }
        }
    }

    virtual LONG getPortID() const
    {
        return getDirect().getPortID();
    }

    virtual ULONG getUniqueIdentifier() const
    {
        ULONG CSum = 0;

        if(pDialup)
        {
            string num;
            CHAR ch[2] = {'\0', '\0'};

            for(int i = 0; i < pDialup->getPhoneNumber().length(); i++ )
            {
                ch[0] = pDialup->getPhoneNumber()[(size_t)i];

                if( ::isdigit(ch[0]) )
                    num += string(ch);
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

    virtual string getPhoneNumber() const
    {
        string   rStr;
        if(pDialup)
        {
            rStr = pDialup->getPhoneNumber();
        }
        return rStr;
    }

    virtual LONG getMinConnectTime() const
    {
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
#endif // #ifndef __DEV_REMOTE_H__

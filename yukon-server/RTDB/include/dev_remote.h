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
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2004/06/28 20:16:11 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_REMOTE_H__
#define __DEV_REMOTE_H__


#include <ctype.h>

#include <rw\cstring.h>
#include <rw\thr\mutex.h>
#include <rw\db\nullind.h>


#include "dev_single.h"
#include "tbl_dialup.h"
#include "tbl_direct.h"


class IM_EX_DEVDB CtiDeviceRemote : public CtiDeviceSingle
{
protected:

    CtiTableDeviceDirectComm   Direct;
    CtiTableDeviceDialup       *pDialup;

public:

    typedef CtiDeviceSingle   Inherited;

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
            LockGuard guard(monitor());

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

    CtiTableDeviceDirectComm  getDirect() const
    {
        return Direct;
    }
    CtiTableDeviceDirectComm& getDirect()
    {
        return Direct;
    }
    CtiDeviceRemote& setDirect( const CtiTableDeviceDirectComm & aDirect )
    {
        LockGuard guard(monitor());
        Direct = aDirect;
        return *this;
    }

    CtiTableDeviceDialup*  getDialup()
    {
        LockGuard guard(monitor());
        return pDialup;
    }

    const CtiTableDeviceDialup  getDialup() const
    {
        LockGuard guard(monitor());
        return *pDialup;
    }

    CtiDeviceRemote& setDialup( CtiTableDeviceDialup *aDialup )
    {
        LockGuard guard(monitor());
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


    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
    {
        Inherited::getSQL(db, keyTable, selector);
        CtiTableDeviceDirectComm::getSQL(db, keyTable, selector);
        CtiTableDeviceDialup::getSQL(db, keyTable, selector);
    }

    virtual void DecodeDatabaseReader(RWDBReader &rdr)
    {
        INT iTemp;
        RWDBNullIndicator isNull;

        Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

        if(getDebugLevel() & 0x0800)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        Direct.DecodeDatabaseReader(rdr);

        /*
         * Check if we are a dialup
         * new some memory if we are and call the decoder on it!
         */
        rdr["phonenumber"] >> isNull;

        if(!isNull)
        {
            RWCString tempstr;
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
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** ERROR ****  Invalid DIALUPDEVICESETTINGS row for device (phonenumber is too short) " << getID() << " = " << getName() << endl;
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
            RWCString num;

            for(int i = 0; i < pDialup->getPhoneNumber().length(); i++ )
            {
                CHAR ch = pDialup->getPhoneNumber().data()[(size_t)i];

                if( isdigit(ch) )
                {
                    num.append(ch);
                }
            }

            // Now get a standard CRC
            CSum = (ULONG)CCITT16CRC( 0, (BYTE*)num.data(), num.length(), FALSE);
        }
        else
        {
            CSum = Inherited::getUniqueIdentifier();
        }

        return CSum;
    }

    virtual RWCString getPhoneNumber() const
    {
        RWCString   rStr;
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

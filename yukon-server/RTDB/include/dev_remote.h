
#ifndef __DEV_REMOTE_H__
#define __DEV_REMOTE_H__

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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:53 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <ctype.h>

#include <rw\cstring.h>
#include <rw\thr\mutex.h>
#include <rw\db\nullind.h>

#include "portsup.h"

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
            pDialup = new CtiTableDeviceDialup;
            *pDialup = aRef.getDialup();
         }
      }
      return *this;
   }

   BOOL  isDialup() const
   {
      return((pDialup == NULL ? FALSE : TRUE ));
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


   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
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

      if(getDebugLevel() & 0x0800) cout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
      Direct.DecodeDatabaseReader(rdr);

      /*
       * Check if we are a dialup
       * new some memory if we are and call the decoder on it!
       */
      rdr["phonenumber"] >> isNull;

      if(!isNull)
      {
         // There was a dialup
        // allocate only if we haven't been here before
        if (pDialup == NULL)
        {
           pDialup = new CtiTableDeviceDialup;
        }
         pDialup->DecodeDatabaseReader(rdr);
      }
   }

   virtual LONG getPortID() const
   {
      return getDirect().getPortID();
   }

   virtual ULONG getPhoneNumberCRC() const
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
         CSum = Inherited::getPhoneNumberCRC();
      }

      return CSum;
   }

   virtual RWCString getPhoneNumber() const
   {
      RWCString   rStr;
      if(pDialup)    // Throw an exception for this one!
      {
         rStr = pDialup->getPhoneNumber();
      }
      return rStr;
   }

   virtual LONG getMinConnectTime() const
   {
      if(!pDialup)    // Throw an exception for this one!
      {
         RWxmsg   err("CtiDeviceRemote has no dialup memory");
         err.raise();
      }
      return pDialup->getMinConnectTime();
   }

   virtual LONG getMaxConnectTime() const
   {
      if(!pDialup)    // Throw an exception for this one!
      {
         RWxmsg   err("CtiDeviceRemote has no dialup memory");
         err.raise();
      }
      return pDialup->getMaxConnectTime();
   }      // From CtiTableDeviceDialup


};
#endif // #ifndef __DEV_REMOTE_H__

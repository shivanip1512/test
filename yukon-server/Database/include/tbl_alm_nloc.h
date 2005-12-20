
#pragma warning( disable : 4786)
#ifndef __TBL_ALM_NLOC_H__
#define __TBL_ALM_NLOC_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_alm_nloc
*
* Class:  CtiTableGroupRecipient
* Date:   11/29/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_alm_nloc.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>


class IM_EX_CTIYUKONDB CtiTableGroupRecipient : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
protected:

   LONG           _recipientID;
   string      _recipientName;
   string      _emailAddress;
   INT            _emailSendType;            // 1 is a To: address, 2 is a CC: address
   string      _pagerNumber;
   BOOL           _disabled;                 // This destination is currently disabled
   string      _recipientType;
   BOOL           _isDirty;


private:

public:

   CtiTableGroupRecipient(LONG id = 0);
   CtiTableGroupRecipient(const CtiTableGroupRecipient& aRef);
   virtual ~CtiTableGroupRecipient();

   CtiTableGroupRecipient& operator=(const CtiTableGroupRecipient& aRef);

   bool operator<( const CtiTableGroupRecipient &rhs ) const;
   bool operator==( const CtiTableGroupRecipient &rhs ) const;
   bool operator()(const CtiTableGroupRecipient& aRef) const;

   LONG getRecipientID() const;
   CtiTableGroupRecipient& setRecipientID(LONG id);

   const string& getRecipientName() const;
   CtiTableGroupRecipient& setRecipientName(const string &str);

   const string& getEmailAddress() const;
   CtiTableGroupRecipient& setEmailAddress(const string &str);

   INT getEmailSendType() const;
   CtiTableGroupRecipient& setEmailSendType(INT type);

   string getRecipientType() const;
   CtiTableGroupRecipient& setRecipientType(string type);

   const string& getPagerNumber() const;
   CtiTableGroupRecipient& setPagerNumber(const string &str);

   bool isDisabled() const;
   CtiTableGroupRecipient& setDisabled(bool b = true);

   CtiTableGroupRecipient& setDirty( bool dirt );
   bool isDirty() const;


   void dump() const;

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   static string getTableName();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Restore();
   virtual RWDBStatus Delete();

   virtual void DecodeDatabaseReader(RWDBReader& rdr);

};
#endif // #ifndef __TBL_ALM_NLOC_H__


#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_rtversacom
*
* Date:   9/30/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_rtversacom.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_RTVERSACOM_H__
#define __TBL_RTVERSACOM_H__

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw\cstring.h>

#include "dbmemobject.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "resolvers.h"
#include "yukon.h"


class IM_EX_CTIYUKONDB CtiTableVersacomRoute : public CtiMemDBObject
{

protected:

   LONG     RouteID;
   INT      UtilityID;           // 1-256
   INT      Section;             // 1-256
   INT      Class;               // 1-16
   INT      Division;            // 1-16
   INT      Bus;                 // 1-8   // Zero based in code so 0-7
   INT      Amp;                 // 1-2   // Zero based in code so 0-1

private:

public:

   CtiTableVersacomRoute();

   CtiTableVersacomRoute(const CtiTableVersacomRoute& aRef);

   ~CtiTableVersacomRoute();

   CtiTableVersacomRoute& operator=(const CtiTableVersacomRoute& aRef);

   void DumpData();

   INT  getUtilityID() const;
   CtiTableVersacomRoute& setUtilityID( const INT aUtilityID );

   INT  getSection() const;
   CtiTableVersacomRoute& setSection( const INT aSection );

   INT  getClass() const;
   CtiTableVersacomRoute& setClass( const INT aClass );

   INT  getDivision() const;
   CtiTableVersacomRoute& setDivision( const INT aDivision );

   INT  getBus() const;
   CtiTableVersacomRoute& setBus( const INT aBus );

   INT  getAmp() const;
   CtiTableVersacomRoute& setAmp( const INT aAmp );

   LONG getRouteID() const;
   CtiTableVersacomRoute& setRouteID( const LONG rid );

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   static RWCString getTableName();

   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();
   virtual RWDBStatus Restore();
};
#endif // #ifndef __TBL_RTVERSACOM_H__

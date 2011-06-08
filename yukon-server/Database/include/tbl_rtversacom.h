
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_rtversacom
*
* Date:   9/30/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_rtversacom.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2006/07/19 19:02:01 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_RTVERSACOM_H__
#define __TBL_RTVERSACOM_H__


#include "dbmemobject.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"


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

   UINT     Individual;          // 0-4,000,000,000+ (for expresscom)

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

   static std::string getSQLCoreStatement();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   static std::string getTableName();

   INT  getSPID() const;
   INT  getGeo() const;
   INT  getSubstation() const;
   UINT getIndividual() const;
};
#endif // #ifndef __TBL_RTVERSACOM_H__

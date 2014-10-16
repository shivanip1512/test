#pragma once

#include "dbmemobject.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTableVersacomRoute : public CtiMemDBObject, private boost::noncopyable, public Cti::Loggable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableVersacomRoute(const CtiTableVersacomRoute&);
    CtiTableVersacomRoute& operator=(const CtiTableVersacomRoute&);

protected:

   LONG     RouteID;
   INT      UtilityID;           // 1-256
   INT      Section;             // 1-256
   INT      Class;               // 1-16
   INT      Division;            // 1-16
   INT      Bus;                 // 1-8   // Zero based in code so 0-7
   INT      Amp;                 // 1-2   // Zero based in code so 0-1

   UINT     Individual;          // 0-4,000,000,000+ (for expresscom)

public:

   CtiTableVersacomRoute();
   virtual ~CtiTableVersacomRoute();

   virtual std::string toString() const override;

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

#pragma once

#include "row_reader.h"

#include "dbmemobject.h"
#include "yukon.h"

#include "database_connection.h"

class IM_EX_CTIYUKONDB CtiTableDeviceScanData : public CtiMemDBObject
{
protected:

   LONG                 _deviceID;

   CtiTime         lastFreezeTime;
   CtiTime         prevFreezeTime;
   CtiTime         lastLPTime;

   LONG                 lastFreezeNumber;
   LONG                 prevFreezeNumber;

   CtiTime         _nextScan[ScanRateInvalid];
   CtiTime               _lastCommunicationTime[ScanRateInvalid];

private:

   public:
   CtiTableDeviceScanData(LONG did = 0);
   CtiTableDeviceScanData(const CtiTableDeviceScanData& aRef);
   virtual ~CtiTableDeviceScanData();

   CtiTableDeviceScanData& operator=(const CtiTableDeviceScanData& aRef);
   LONG getDeviceID() const;
   CtiTableDeviceScanData& setDeviceID(LONG id);

   LONG  getLastFreezeNumber() const;
   LONG& getLastFreezeNumber();
   CtiTime getNextScan(INT a) const;
   CtiTableDeviceScanData& setNextScan(INT a, const CtiTime &b);
   CtiTime nextNearestTime(int maxrate = ScanRateInvalid) const;
   CtiTableDeviceScanData& setLastFreezeNumber( const LONG aLastFreezeNumber );
   LONG  getPrevFreezeNumber() const;
   LONG& getPrevFreezeNumber();
   CtiTableDeviceScanData& setPrevFreezeNumber( const LONG aPrevFreezeNumber );
   CtiTime  getLastFreezeTime() const;
   CtiTableDeviceScanData& setLastFreezeTime( const CtiTime& aLastFreezeTime );
   CtiTime  getPrevFreezeTime() const;
   CtiTableDeviceScanData& setPrevFreezeTime( const CtiTime& aPrevFreezeTime );
   CtiTime  getLastLPTime() const;
   CtiTableDeviceScanData& setLastLPTime( const CtiTime& aLastFreezeTime );
   CtiTime getLastCommunicationTime(int i) const;
   CtiTableDeviceScanData& setLastCommunicationTime( int i, const CtiTime& tme );


   void DecodeDatabaseReader( Cti::RowReader& rdr );
   virtual bool Insert();
   virtual bool Restore();
   virtual bool Update();
   virtual std::string getTableName() const;

   bool Update(Cti::Database::DatabaseConnection &conn);

};

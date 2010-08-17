#pragma once

#include "dbmemobject.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "row_reader.h"
#include "ctistring.h"

class IM_EX_CTIYUKONDB CtiTableRDSTransmitter : public CtiMemDBObject
{

protected:

   unsigned char  _siteAddress;
   unsigned char  _encoderAddress;
   CtiString      _groupType;
   float          _groupRate;

public:

   CtiTableRDSTransmitter();
   virtual ~CtiTableRDSTransmitter();
   CtiTableRDSTransmitter(const CtiTableRDSTransmitter &aRef);
   CtiTableRDSTransmitter& operator=(const CtiTableRDSTransmitter &aRef);

   unsigned char getSiteAddress() const;
   unsigned char getEncoderAddress() const;
   unsigned char getGroupType() const;
   float         getGroupRate() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};

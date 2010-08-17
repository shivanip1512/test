#include "yukon.h"
#include "tbl_rds_transmitter.h"

CtiTableRDSTransmitter::CtiTableRDSTransmitter() :
_groupRate(0),
_siteAddress(0),
_encoderAddress(0)
{
}

CtiTableRDSTransmitter::~CtiTableRDSTransmitter() {}

CtiTableRDSTransmitter::CtiTableRDSTransmitter(const CtiTableRDSTransmitter &aRef)
{
    *this = aRef;
}

CtiTableRDSTransmitter& CtiTableRDSTransmitter::operator=(const CtiTableRDSTransmitter &aRef)
{
    _groupRate = aRef._groupRate;
    _siteAddress = aRef._siteAddress;
    _encoderAddress = aRef._encoderAddress;
    _groupType = aRef._groupType;

    return *this;
}

unsigned char CtiTableRDSTransmitter::getSiteAddress() const
{
    return _siteAddress;
}

unsigned char CtiTableRDSTransmitter::getEncoderAddress() const
{
    return _encoderAddress;
}

// Convert string (11A) to integer ((Number(11) << 1) | A = 0, B =  1)
// If not specified or there is garbage, assumes  type A.
unsigned char CtiTableRDSTransmitter::getGroupType() const
{
    unsigned char result = (atoi(_groupType.c_str())) << 1;

    if (_groupType.contains("b",CtiString::ignoreCase))
    {
        result |= 1;
    }
    
    return result;
}

float CtiTableRDSTransmitter::getGroupRate() const
{
    return _groupRate;
}

void CtiTableRDSTransmitter::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    rdr["siteaddress"] >>    _siteAddress;
    rdr["encoderaddress"] >> _encoderAddress;
    rdr["grouptype"] >>      _groupType;
    rdr["transmitspeed"] >>  _groupRate;
}

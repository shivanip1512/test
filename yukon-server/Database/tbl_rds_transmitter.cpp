#include "yukon.h"
#include "tbl_rds_transmitter.h"

CtiTableRDSTransmitter::CtiTableRDSTransmitter() :
_groupRate(0),
_siteAddress(0),
_encoderAddress(0),
_groupID(0)
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
    _groupID = aRef._groupID;

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

unsigned char CtiTableRDSTransmitter::getGroupID() const
{
    return _groupID;
}

float CtiTableRDSTransmitter::getGroupRate() const
{
    return _groupRate;
}

void CtiTableRDSTransmitter::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    rdr["siteaddress"] >>    _siteAddress;
    rdr["encoderaddress"] >> _encoderAddress;
    rdr["groupnumber"] >>    _groupID;
    rdr["transmitspeed"] >>  _groupRate;
}

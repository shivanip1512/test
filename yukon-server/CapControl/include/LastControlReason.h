#pragma once

#include <string>

class CtiTime;
class CtiCCTwoWayPoints;



class LastControlReason
{
public:

    virtual std::string getText( const CtiCCTwoWayPoints & points ) = 0;

    virtual long serialize( const CtiCCTwoWayPoints & points ) = 0;
    virtual void deserialize( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp ) = 0;
};


// ------------------------------


class LastControlReasonCbcDnp : public LastControlReason
{
public:

    std::string getText( const CtiCCTwoWayPoints & points ) override;

    long serialize( const CtiCCTwoWayPoints & points ) override;
    void deserialize( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp ) override;
};


// ------------------------------


class LastControlReasonCbc702x : public LastControlReason
{
public:

    std::string getText( const CtiCCTwoWayPoints & points ) override;

    long serialize( const CtiCCTwoWayPoints & points ) override;
    void deserialize( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp ) override;

protected:

    enum Reasons    // a bitmask
    {
        Uninitialized   =   0u,
        Local           = ( 1u << 0 ),
        Remote          = ( 1u << 1 ),
        OvUv            = ( 1u << 2 ),
        NeutralFault    = ( 1u << 3 ),
        Scheduled       = ( 1u << 4 ),
        Digital         = ( 1u << 5 ),
        Analog          = ( 1u << 6 ),
        Temperature     = ( 1u << 7 )
    };
};


// ------------------------------


class LastControlReasonCbc802x : public LastControlReason
{
public:

    std::string getText( const CtiCCTwoWayPoints & points ) override;

    long serialize( const CtiCCTwoWayPoints & points ) override;
    void deserialize( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp ) override;

protected:

    enum
    {
        DefaultStateGroup = -17
    };

    enum
    {
        UninitializedRawSate = -1
    };

    virtual std::string lookupStateName( const long reason, const long stateGroup ) const;
};


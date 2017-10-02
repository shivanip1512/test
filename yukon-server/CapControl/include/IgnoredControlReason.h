#pragma once

#include <string>

class CtiTime;
class CtiCCTwoWayPoints;



class IgnoredControlReason
{
public:

    virtual std::string getText( const CtiCCTwoWayPoints & points ) = 0;

    virtual bool controlRejectedByVoltageLimits( const CtiCCTwoWayPoints & points ) = 0;
    virtual bool checkDeltaVoltageRejection( const CtiCCTwoWayPoints & points ) = 0;
    virtual bool checkControlAccepted( const CtiCCTwoWayPoints & points ) = 0;

    virtual bool serializeIndicator( const CtiCCTwoWayPoints & points ) = 0;
    virtual long serializeReason( const CtiCCTwoWayPoints & points ) = 0;

    virtual void deserializeIndicator( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp ) = 0;
    virtual void deserializeReason( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp ) = 0;

    virtual std::unique_ptr<IgnoredControlReason>  clone() const = 0;
};


// ------------------------------


class IgnoredControlReasonCbcDnp : public IgnoredControlReason
{
public:

    std::string getText( const CtiCCTwoWayPoints & points ) override;

    bool controlRejectedByVoltageLimits( const CtiCCTwoWayPoints & points ) override;
    bool checkDeltaVoltageRejection( const CtiCCTwoWayPoints & points ) override;
    bool checkControlAccepted( const CtiCCTwoWayPoints & points ) override;

    bool serializeIndicator( const CtiCCTwoWayPoints & points ) override;
    long serializeReason( const CtiCCTwoWayPoints & points ) override;

    void deserializeIndicator( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp ) override;
    void deserializeReason( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp ) override;

    std::unique_ptr<IgnoredControlReason>  clone() const override;
};


// ------------------------------


class IgnoredControlReasonCbc702x : public IgnoredControlReason
{
public:

    std::string getText( const CtiCCTwoWayPoints & points ) override;

    bool controlRejectedByVoltageLimits( const CtiCCTwoWayPoints & points ) override;
    bool checkDeltaVoltageRejection( const CtiCCTwoWayPoints & points ) override;
    bool checkControlAccepted( const CtiCCTwoWayPoints & points ) override;

    bool serializeIndicator( const CtiCCTwoWayPoints & points ) override;
    long serializeReason( const CtiCCTwoWayPoints & points ) override;

    void deserializeIndicator( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp ) override;
    void deserializeReason( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp ) override;

    std::unique_ptr<IgnoredControlReason>  clone() const override;

protected:

    enum Reasons
    {
        Local,
        FaultCurrent,
        EmVolt,
        Time,
        Voltage,
        Digital1,
        Analog1,
        Digital2,
        Analog2,
        Digital3,
        Analog3,
        Digital4,
        Temp,
        Remote,
        NtrlLockOut,
        BrownOut,
        BadActRelay
    };

    enum DefaultValues
    {
        UninitializedIndicator  = 0,
        UninitializedReason     = 0
    };
};


// ------------------------------


class IgnoredControlReasonCbc802x : public IgnoredControlReason
{
public:

    std::string getText( const CtiCCTwoWayPoints & points ) override;

    bool controlRejectedByVoltageLimits( const CtiCCTwoWayPoints & points ) override;
    bool checkDeltaVoltageRejection( const CtiCCTwoWayPoints & points ) override;
    bool checkControlAccepted( const CtiCCTwoWayPoints & points ) override;

    bool serializeIndicator( const CtiCCTwoWayPoints & points ) override;
    long serializeReason( const CtiCCTwoWayPoints & points ) override;

    void deserializeIndicator( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp ) override;
    void deserializeReason( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp ) override;

    std::unique_ptr<IgnoredControlReason>  clone() const override;

protected:

    virtual std::string lookupStateName( const long reason, const long stateGroup ) const;

    enum Reasons
    {
        Manual,
        ScadaOverride,
        FaultCurrent,
        EmergencyVoltage,
        TimeOnOff,
        OvUvControl,
        VAR,
        Va,
        Vb,
        Vc,
        Ia,
        Ib,
        Ic,
        Temp,
        Unused_14,
        Unused_15,
        Unused_16,
        BadActiveRelay,
        NCLockout,
        ControlAccepted,
        AutoMode,
        RecloseBlock
    };

    enum DefaultValues
    {
        UninitializedReason = -1,
        DefaultStateGroup   = -20
    };
};


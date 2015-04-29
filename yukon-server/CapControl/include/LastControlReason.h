#pragma once

#include <string>



class LastControlReason
{
public:

    virtual std::string getText( const long reason, const long stateGroup ) = 0;
};


// ------------------------------


class LastControlReasonCbcDnp : public LastControlReason
{
public:

    std::string getText( const long reason, const long stateGroup ) override;
};


// ------------------------------


class LastControlReasonCbc702x : public LastControlReason
{
public:

    std::string getText( const long reason, const long stateGroup ) override;
};


// ------------------------------


class LastControlReasonCbc802x : public LastControlReason
{
public:

    std::string getText( const long reason, const long stateGroup ) override;
};


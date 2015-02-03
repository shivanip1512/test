#pragma once

#include <windows.h>
#include <iostream>
#include <deque>

#include "dlldefs.h"


class IM_EX_CTIBASE CtiRegression
{
public:
    typedef std::pair< double, double > val_type;
    typedef std::deque< val_type > regress_col;

private:

    regress_col _regData;
    int _regDepth;
    int _minDepth; // Only used with linear regression

public:
    CtiRegression(int depth = 10);
    virtual ~CtiRegression();

    void clear();
    void append( const val_type &vt );
    void appendWithoutFill( const val_type &vt );
    void resize( size_t n );
    void setDepth( size_t n );
    void setMinDepth( size_t n );
    int  getCurDepth() const;
    int  getRegDepth() const;
    bool depthMet() const;
    /*
     *  Compute the regession using the stored values with the X value passed into this function
     *
     *  returns: The y coordinate of y = mx + b.
     */
    double regression( double xprojection ) const;
    bool linearConstantIntervalRegression( double &slope, double &intercept );
};

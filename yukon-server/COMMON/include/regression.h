
/*-----------------------------------------------------------------------------*
*
* File:   regression
*
* Class:  CtiRegression
* Date:   11/18/2005
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/02/12 04:15:29 $
* HISTORY      :
* $Log: regression.h,v $
* Revision 1.5  2008/02/12 04:15:29  jrichter
* YUK-4375
* report: VAR/PF confirmation percentage for last 7 days & 30 days
*
* Revision 1.4  2007/08/07 21:04:52  mfisher
* removed "using namespace std;" from header files
*
* Revision 1.3  2007/06/13 15:28:29  jotteson
* YUK-3797
* Added more control over the number of points used in a regression, also added regression functions that return more data.
*
* Revision 1.2  2005/11/21 16:05:26  cplender
* Added object
*
* Revision 1.1.2.1  2005/11/21 16:01:02  cplender
* Added regression object
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __REGRESSION_H__
#define __REGRESSION_H__

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
#endif // #ifndef __REGRESSION_H__




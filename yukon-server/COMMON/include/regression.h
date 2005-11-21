
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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/11/21 16:05:26 $
* HISTORY      :
* $Log: regression.h,v $
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

using namespace std;


class IM_EX_CTIBASE CtiRegression
{
public:
    typedef pair< double, double > val_type;
    typedef deque< val_type > regress_col;

private:

    regress_col _regData;
    int _regDepth;

public:
    CtiRegression(int depth = 10);
    virtual ~CtiRegression();

    void clear();
    void append( const val_type &vt );
    void resize( size_t n );

    /*
     *  Compute the regession using the stored values with the X value passed into this function
     *
     *  returns: The y coordinate of y = mx + b.
     */
    double regression( double xprojection );
};
#endif // #ifndef __REGRESSION_H__




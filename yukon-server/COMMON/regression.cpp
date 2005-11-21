
/*-----------------------------------------------------------------------------*
*
* File:   regression
*
* Date:   11/18/2005
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/11/21 16:04:54 $
*
* HISTORY      :
* $Log: regression.cpp,v $
* Revision 1.2  2005/11/21 16:04:54  cplender
* Added object
*
* Revision 1.1.2.1  2005/11/21 16:01:53  cplender
* Added regression object
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include "yukon.h"

#include "cparms.h"
#include "logger.h"
#include "regression.h"

CtiRegression::CtiRegression(int depth) :
    _regDepth(depth)
{ }

CtiRegression::~CtiRegression() {}

void CtiRegression::clear()
{
    _regData.clear();
}

void CtiRegression::append( const val_type &vt )
{
    if(_regData.size() != _regDepth)
    {
        _regData.resize( _regDepth, vt );   // Populate the regression with the first x,y pair.
    }
    else
    {
        _regData.push_back( vt );           // Atttach new elements on the back.
        _regData.pop_front();               // Remove the "oldest element" to keep the collection at _regDepth.
    }
}

void CtiRegression::resize( size_t n )
{
    _regDepth = n;
    if(_regData.size() < _regDepth)
    {
        // It is Smaller.  COPY the oldest element to pad out the regression.
        val_type front_element = _regData.front();
        while(_regData.size() < _regDepth)
        {
            _regData.push_front(front_element); // COPY the "oldest element" to make the collection _regDepth.
        }
    }
    else
    {
        // It is bigger.  Pop the oldest elements.
        while(_regData.size() > _regDepth)
        {
            _regData.pop_front();               // Remove the "oldest element" to keep the collection at _regDepth.
        }
    }
}

/*
 *  Compute the regession using the stored values with the X value passed into this function
 *
 *  returns: The y coordinate of y = mx + b.
 */
double CtiRegression::regression( double xprojection )
{
    double y = 0.0;
    double xorigin;             // Regress computes relative to the front element's x coord.
    double xoffset;             // xprojection's difference from xorigin.
    double xtemp;

    double sum_x = 0.0;
    double sum_y = 0.0;
    double sum_xy = 0.0;
    double sum_xx = 0.0;
    double delta;
    double m;                   // This is B in the book.
    double b;                   // This is A in the book

    val_type vt;

    if(!_regData.empty() && _regDepth == _regData.size())       // The second question should not be possible.
    {
        xorigin = _regData[0].first;                            // x's will be computed relative to xorigin to reduce the liklihood of overflow.
        xoffset = xprojection - xorigin;

        for(int i = 0; i < _regDepth; i++ )
        {
            vt = _regData[i];
            xtemp = vt.first - xorigin;                         // This is the value of x offset by data element #1.

            if(gConfigParms.getValueAsULong("DEBUGLEVEL_REGRESSION",0,16) & 0x00000001)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << " " << xtemp << ", " << vt.second << endl;
            }

            sum_x += xtemp;
            sum_y += vt.second;
            sum_xy += xtemp * vt.second;
            sum_xx += xtemp * xtemp;
        }

        delta = (_regDepth * sum_xx) - (sum_x * sum_x);
        if(delta != 0.0)
        {
            m = ((_regDepth * sum_xy) - (sum_x*sum_y)) / delta;
            b = ((sum_xx * sum_y) - (sum_x * sum_xy)) / delta;

            y = m * xoffset + b;

            if(gConfigParms.getValueAsULong("DEBUGLEVEL_REGRESSION",0,16) & 0x00000001)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << endl << "Forecast: for " << RWTime((ULONG)xprojection) << " = offset " << xoffset << ", " << y << endl;
                dout << endl;
            }
        }
    }

    return y;
}




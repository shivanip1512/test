
/*-----------------------------------------------------------------------------*
*
* File:   regression
*
* Date:   11/18/2005
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2008/02/12 04:15:29 $
*
* HISTORY      :
* $Log: regression.cpp,v $
* Revision 1.6  2008/02/12 04:15:29  jrichter
* YUK-4375
* report: VAR/PF confirmation percentage for last 7 days & 30 days
*
* Revision 1.5  2007/06/13 15:28:28  jotteson
* YUK-3797
* Added more control over the number of points used in a regression, also added regression functions that return more data.
*
* Revision 1.4  2006/01/16 20:29:10  mfisher
* Changed RWTime to CtiTime
*
* Revision 1.3  2005/12/22 14:33:47  cplender
* Made a change to prevent pop before data was in the deque
*
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

#include "precompiled.h"

#include "cparms.h"
#include "logger.h"
#include "regression.h"

using std::endl;

CtiRegression::CtiRegression(int depth) :
    _regDepth(depth), _minDepth(depth)
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

void CtiRegression::appendWithoutFill( const val_type &vt )
{
    if( !_regData.empty() && _regData.back() == vt )
    {
        //Do nothing, this is the same point we already have.
    }
    else
    {
        _regData.push_back( vt );           // Atttach new elements on the back.
    }

    if( _regData.size() > _regDepth )
    {
        _regData.pop_front();               // Remove the "oldest element" to keep the collection at _regDepth.
    }
}

void CtiRegression::setDepth( size_t n )
{
    _regDepth = n;
}

void CtiRegression::setMinDepth( size_t n )
{
    _minDepth = n;
}

int CtiRegression::getCurDepth( ) const
{
    return _regData.size();
}

void CtiRegression::resize( size_t n )
{
    _regDepth = n;
    if(!_regData.empty())
    {
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
}

int CtiRegression::getRegDepth() const
{
    return _regDepth;
}

bool CtiRegression::depthMet() const
{
    return ( _regDepth == _regData.size() );
}
/*
 *  Compute the regession using the stored values with the X value passed into this function
 *
 *  returns: The y coordinate of y = mx + b.
 */
double CtiRegression::regression( double xprojection ) const
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
                dout << endl << "Forecast: for " << CtiTime((ULONG)xprojection) << " = offset " << xoffset << ", " << y << endl;
                dout << endl;
            }
        }
    }

    return y;
}


/*  This does a regression and forms a line of the form y = (slope)x + (intercept)
 *
 *  The function assumes all points are 1 interval apart, so time is really not used.
 *  Returns false if there were not enough entries to do the regression.
 */
bool CtiRegression::linearConstantIntervalRegression( double &slope, double &intercept )
{
    bool retVal = false;
    double y = 0.0;
    double xtemp;

    double sum_x = 0.0;
    double sum_y = 0.0;
    double sum_xy = 0.0;
    double sum_xx = 0.0;
    double delta;
    double m;                   // This is B in the book.
    double b;                   // This is A in the book

    val_type vt;

    if(!_regData.empty() && _regData.size() >= _minDepth)       // The second question should not be possible.
    {
        for(int i = 0; i < _regData.size(); i++ )
        {
            vt = _regData[i];
            xtemp = i;                         // Since we are assuming an interval of 1, this works.

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

        delta = (_regData.size() * sum_xx) - (sum_x * sum_x);
        if(delta != 0.0)
        {
            m = slope = ((_regData.size() * sum_xy) - (sum_x*sum_y)) / delta;
            b = intercept = ((sum_xx * sum_y) - (sum_x * sum_xy)) / delta;

            if(gConfigParms.getValueAsULong("DEBUGLEVEL_REGRESSION",0,16) & 0x00000001)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << endl << "Line Formula: Y =  " << m << " * X + " << b << endl;
                dout << endl;
            }
            retVal = true;
        }

    }

    return retVal;
}

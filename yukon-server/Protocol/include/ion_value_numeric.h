#pragma warning( disable : 4786 )

#ifndef __ION_VALUE_NUMERIC_H__
#define __ION_VALUE_NUMERIC_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_numeric.h
 *
 * Class:  CtiIONNumeric
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ctitypes.h"

class CtiIONNumeric
{
private:

protected:

public:

    CtiIONNumeric( )  { };
    ~CtiIONNumeric( ) { };

    virtual double getNumericValue( void ) const;
};


#endif  //  #ifndef __ION_VALUE_NUMERIC_H__


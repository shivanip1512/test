#pragma warning( disable : 4786 )

#ifndef __ION_VALUEBASIC_NUMERIC_H__
#define __ION_VALUEBASIC_NUMERIC_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuebasic_numeric.h
 *
 * Class:  CtiIONNumeric
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 *         Basic value type classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ion_rootclasses.h"
#include "ctitypes.h"

class IM_EX_PROT CtiIONNumeric : public CtiIONValue
{
public:
    CtiIONNumeric( CtiIONValue::IONValueTypes valueType ) :
        CtiIONValue(valueType)
        { };
    ~CtiIONNumeric( ) { };
};


#endif  //  #ifndef __ION_VALUEBASIC_NUMERIC_H__


#pragma warning( disable : 4786 )

#ifndef __ION_VALUE_VARIABLE_FIXEDARRAY_ELEMENT_H__
#define __ION_VALUE_VARIABLE_FIXEDARRAY_ELEMENT_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_variable_fixedarray_element.h
 *
 * Class:  CtiIONFixedArrayElement
 * Date:   2003-feb-06
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/


//  this definition is here so the elements can make proper friends
//    with their respective array instantiations
template < class T > class CtiIONFixedArrayTemplate;

class CtiIONFixedArrayElement
{
public:

    //  i wish i could make these protected, but i can't find a way to make friends with CtiIONFixedArrayTemplate.
    virtual unsigned int getElementLength( void ) const = 0;
    virtual void putElement( unsigned char *buf ) const = 0;
};


#endif  //  #ifndef __ION_VALUE_VARIABLE_FIXEDARRAY_ELEMENT_H__

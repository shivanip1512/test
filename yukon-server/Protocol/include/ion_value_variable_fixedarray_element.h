#pragma once

//  this definition is here so the elements can make proper friends
//    with their respective array instantiations
template < class T > class CtiIONFixedArrayTemplate;

class CtiIONFixedArrayElement
{
public:

    virtual ~CtiIONFixedArrayElement()
    {
    }

    //  i wish i could make these protected, but i can't find a way to make friends with CtiIONFixedArrayTemplate.
    virtual unsigned int getElementLength( void ) const = 0;
    virtual void putElement( unsigned char *buf ) const = 0;
};

#pragma warning( disable : 4786 )

#ifndef __ION_VALUEBASIC_PROGRAM_H__
#define __ION_VALUEBASIC_PROGRAM_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuebasic_program.h
 *
 * Class:  CtiIONProgram
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ion_rootclasses.h"
#include "ion_value_statement.h"

class IM_EX_PROT CtiIONProgram : public CtiIONValue
{
public:

    CtiIONProgram( CtiIONStatement *initial );
    CtiIONProgram( unsigned char *byteStream, unsigned long streamLength );

    ~CtiIONProgram( );

    CtiIONProgram &addStatement( CtiIONStatement *toAdd );

    unsigned int getSerializedValueLength( void ) const;
    void putSerializedValue( unsigned char *buf ) const;

private:

    vector< CtiIONStatement * > _statements;
};


#endif  //  #ifndef __ION_VALUEBASICTYPES_H__



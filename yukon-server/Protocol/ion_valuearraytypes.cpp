/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuearraytypes.cpp
 *
 * Class:  CtiION*Array
 * Date:   09/12/2001
 *
 * Author: Matthew Fisher
 *
 *         Array value classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ctidbgmem.h" // defines CTIDBG_new
#include "ion_valuearraytypes.h"
#include "ion_valuestructtypes.h"


CtiIONCharArray::CtiIONCharArray( unsigned long itemCount, unsigned long itemLength, unsigned char *byteStream ) :
    CtiIONArray(IONCharArray)
{
    CtiIONChar *tmpChar;
    unsigned long offset;

    setValid( TRUE );

    for( unsigned long i = 0; i < itemCount && isValid( ); i++ )
    {
        offset = (i * itemCount);
        tmpChar = CTIDBG_new CtiIONChar( byteStream + offset, itemLength );
        if( tmpChar != NULL && tmpChar->isValid( ) )
        {
            appendArrayElement( tmpChar );
        }
        else
        {
            setValid( FALSE );
        }
    }
}


CtiIONChar *CtiIONCharArray::operator[]( unsigned long index )
{
    return (CtiIONChar *)getArrayElement( index );
}


CtiIONBooleanArray::CtiIONBooleanArray( unsigned long itemCount, unsigned long itemLength, unsigned char *byteStream ) :
    CtiIONArray(IONBooleanArray)
{
    CtiIONBoolean *tmpElement;
    unsigned long offset;

    setValid( TRUE );

    for( unsigned long i = 0; i < itemCount && isValid( ); i++ )
    {
        offset = (i * itemCount);
        tmpElement = CTIDBG_new CtiIONBoolean( byteStream + offset, itemLength );
        if( tmpElement != NULL && tmpElement->isValid( ) )
        {
            appendArrayElement( tmpElement );
        }
        else
        {
            setValid( FALSE );
        }
    }
}


CtiIONBoolean *CtiIONBooleanArray::operator[]( unsigned long index )
{
    return (CtiIONBoolean *)getArrayElement( index );
}


CtiIONFloatArray::CtiIONFloatArray( unsigned long itemCount, unsigned long itemLength, unsigned char *byteStream ) :
    CtiIONArray(IONFloatArray)
{
    CtiIONFloat *tmpElement;
    unsigned long offset;

    setValid( TRUE );

    for( unsigned long i = 0; i < itemCount && isValid( ); i++ )
    {
        offset = (i * itemCount);
        tmpElement = CTIDBG_new CtiIONFloat( byteStream + offset, itemLength );
        if( tmpElement != NULL && tmpElement->isValid( ) )
        {
            appendArrayElement( tmpElement );
        }
        else
        {
            setValid( FALSE );
        }
    }
}


CtiIONFloat *CtiIONFloatArray::operator[]( unsigned long index )
{
    return (CtiIONFloat *)getArrayElement( index );
}


CtiIONSignedIntArray::CtiIONSignedIntArray( unsigned long itemCount, unsigned long itemLength, unsigned char *byteStream ) :
    CtiIONArray(IONBooleanArray)
{
    CtiIONSignedInt *tmpElement;
    unsigned long offset;

    setValid( TRUE );

    for( unsigned long i = 0; i < itemCount && isValid( ); i++ )
    {
        offset = (i * itemCount);
        tmpElement = CTIDBG_new CtiIONSignedInt( byteStream + offset, itemLength );
        if( tmpElement != NULL && tmpElement->isValid( ) )
        {
            appendArrayElement( tmpElement );
        }
        else
        {
            setValid( FALSE );
        }
    }
}


CtiIONSignedInt *CtiIONSignedIntArray::operator[]( unsigned long index )
{
    return (CtiIONSignedInt *)getArrayElement( index );
}


CtiIONUnsignedIntArray::CtiIONUnsignedIntArray( unsigned long itemCount, unsigned long itemLength, unsigned char *byteStream ) :
    CtiIONArray(IONUnsignedIntArray)
{
    CtiIONUnsignedInt *tmpElement;
    unsigned long offset;

    setValid( TRUE );

    for( unsigned long i = 0; i < itemCount && isValid( ); i++ )
    {
        offset = (i * itemCount);
        tmpElement = CTIDBG_new CtiIONUnsignedInt( byteStream + offset, itemLength );
        if( tmpElement != NULL && tmpElement->isValid( ) )
        {
            appendArrayElement( tmpElement );
        }
        else
        {
            setValid( FALSE );
        }
    }
}


CtiIONUnsignedInt *CtiIONUnsignedIntArray::operator[]( unsigned long index )
{
    return (CtiIONUnsignedInt *)getArrayElement( index );
}



void CtiIONStruct::init( vector< CtiIONValue * > &structValues )
{
    if( _numElements > 0 )
    {
        if( structValues.size( ) != _numElements )
        {
            setValid( FALSE );
        }
    }

    for( int i = 0; i < structValues.size( ) && isValid( ); i++ )
    {
        if( structValues[i]->isValid( ) )
        {
            appendArrayElement( structValues[i] );
        }
        else
        {
            setValid( FALSE );
        }
    }
}


unsigned char CtiIONStruct::getStructKey( void )
{
    unsigned char key;

    key  = 0x80;
    key |= getStructType( );

    return key;
}


CtiIONValue *CtiIONStruct::operator[]( Elements index )
{
    CtiIONValue *retVal;

    retVal = getArrayElement( index );

    return retVal;
}


CtiIONArray *CtiIONStruct::restoreObject( unsigned char classDescriptor, unsigned char *byteStream, unsigned long streamLength )
{
    int error = FALSE;
    unsigned long streamPos;

    CtiIONValue *tmpValue;
    CtiIONArray *CTIDBG_newVal;

    vector< CtiIONValue * > structValues;

    streamPos = 0;

    while( byteStream[streamPos] != 0xF3 &&  //  end of struct
           !error )
    {
        tmpValue = CtiIONValue::restoreObject( byteStream, streamLength );
        if( tmpValue == NULL || !tmpValue->isValid( ) )
        {
            error = TRUE;
        }
        else
        {
            structValues.push_back( tmpValue );
            streamPos += tmpValue->getSerializedLength( );
        }
    }

    switch( classDescriptor )
    {
        case IONLogRecord:
            CTIDBG_newVal = CTIDBG_new CtiIONLogRecord( structValues );
            break;

        case IONAlarm:
            CTIDBG_newVal = CTIDBG_new CtiIONAlarm( structValues );
            break;

        case IONEvent:
            CTIDBG_newVal = CTIDBG_new CtiIONEvent( structValues );
            break;

        case IONRange:
            CTIDBG_newVal = CTIDBG_new CtiIONRange( structValues );
            break;

        case IONList:
            CTIDBG_newVal = CTIDBG_new CtiIONList( structValues );
            break;
/*
        case IONException:
            CTIDBG_newVal = CTIDBG_new CtiIONException( structValues );
            break;

        case IONWaveform:
            CTIDBG_newVal = CTIDBG_new CtiIONWaveform( structValues );
            break;

        case IONDate:
            CTIDBG_newVal = CTIDBG_new CtiIONDate( structValues );
            break;

        case IONCalendar:
            CTIDBG_newVal = CTIDBG_new CtiIONCalendar( structValues );
            break;

        case IONProfile:
            CTIDBG_newVal = CTIDBG_new CtiIONProfile( structValues );
            break;

        case IONStringArray:
            CTIDBG_newVal = CTIDBG_new CtiIONStringArray( structValues );
  */
        default:
            CTIDBG_newVal = NULL;
    }

    return CTIDBG_newVal;
}



unsigned char CtiIONStructArray::getStructArrayKey( void )
{
    unsigned char key;

    key  = 0x90;
    key |= getStructArrayType( );

    return key;
}


CtiIONArray *CtiIONStructArray::restoreObject( unsigned char classDescriptor, unsigned char *byteStream, unsigned long streamLength )
{
    int error = FALSE;
    unsigned long streamPos;

    CtiIONValue *tmpValue;
    CtiIONArray *CTIDBG_newVal;
    vector< CtiIONValue * > structValues;

    while( byteStream[streamPos] != 0xF9 &&  //  end of struct array
           !error )
    {
        tmpValue = CtiIONValue::restoreObject( byteStream, streamLength );
        if( tmpValue == NULL || !tmpValue->isValid( ) || ((CtiIONStruct *)tmpValue)->getStructType( ) != classDescriptor )
        {
            error = TRUE;
        }
        else
        {
            structValues.push_back( tmpValue );
            streamPos += tmpValue->getSerializedLength( );
        }
    }

    switch( classDescriptor )
    {
/*        case IONLogArray:
            CTIDBG_newVal = CTIDBG_new CtiIONLogArray( structValues );
            break;

        case IONAlarmArray:
            CTIDBG_newVal = CTIDBG_new CtiIONAlarmArray( structValues );
            break;

        case IONStringArrayArray:
            CTIDBG_newVal = CTIDBG_new CtiIONStringArrayArray( structValues );
            break;

        case IONMultiArray:
            CTIDBG_newVal = CTIDBG_new CtiIONMultiArray( structValues );
            break;
*/
        default:
            CTIDBG_newVal = NULL;
            break;
    }

    return CTIDBG_newVal;
}





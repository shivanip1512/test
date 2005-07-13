/*-----------------------------------------------------------------------------*
*
* File:   dnp_objects
*
* Namespace: CtiDNP
* Class:     Object, ObjectBlock
* Date:   5/21/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2005/07/13 18:56:37 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_OBJECTS_H__
#define __DNP_OBJECTS_H__
#pragma warning( disable : 4786)


#include <vector>
#include <queue>

#include <rw/tpslist.h>

#include "dllbase.h"

#include "msg_pdata.h"
#include "pointtypes.h"

#include "prot_base.h"

using namespace std;

namespace Cti       {
namespace Protocol  {
namespace DNP       {

class TimeCTO;  //  forward declaration

class Object
{
protected:
    unsigned char _group, _variation;

    bool _valid;

    Object( int group, int variation );

public:

    virtual ~Object();

    int getGroup( void )     const;
    int getVariation( void ) const;

    virtual int restore( const unsigned char *buf, int len );
    virtual int restoreBits( const unsigned char *buf, int bitpos, int len );

    bool isValid( void );

    virtual int serialize( unsigned char *buf ) const;
    virtual int getSerializedLen( void ) const;

    virtual CtiPointDataMsg *getPoint( const TimeCTO *cto ) const;
};


class ObjectBlock
{
    enum QualifierType;

private:
    bool _restoring,
         _valid;

    int  _group,
         _variation,
         _qualifier;

    unsigned short _start;

    typedef vector< const Object * > object_vector;
    typedef vector< int >            index_vector;
    object_vector _objectList;
    index_vector  _objectIndices;

    void init( QualifierType type, int group, int variation );

    int restoreObject( const unsigned char *buf, int len, Object *&obj );
    int restoreBitObject( const unsigned char *buf, int bitpos, int len, Object *&obj );

    void erase( void );

public:

    ObjectBlock();  //  for restoring a serialized object
    ObjectBlock( QualifierType type );
    ObjectBlock( QualifierType type, int group, int variation );
    ~ObjectBlock();

    enum QualifierType
    {
        NoIndex_ByteStartStop  = 0x00,
        NoIndex_ShortStartStop = 0x01,
        NoIndex_NoRange        = 0x06,
        NoIndex_ByteQty        = 0x07,
        NoIndex_ShortQty       = 0x08,
        ByteIndex_ByteQty      = 0x17,
        ByteIndex_ShortQty     = 0x18,
        ShortIndex_ShortQty    = 0x28
    };

    enum
    {
        ObjectBlockMinSize = 3
    };

    struct object_descriptor
    {
        const Object *object;
        int index;
    };

    int getGroup( void )     const;
    int getVariation( void ) const;

    bool addObject( const Object *object );
    bool addObjectIndex( const Object *object, int index );
//    void addRange( Object *objArray, int start, int stop );

    int  size( void )  const;
    bool empty( void ) const;
    object_descriptor at( unsigned index ) const;
    object_descriptor operator[]( unsigned index ) const;

    int  getSerializedLen( void ) const;
    int  serialize( unsigned char *buf ) const;

    int  restore( const unsigned char *buf, int len );

    void getPoints( Interface::pointlist_t &points, const TimeCTO *cto ) const;
};

}
}
}

#endif // #ifndef __DNP_OBJECTS_H__

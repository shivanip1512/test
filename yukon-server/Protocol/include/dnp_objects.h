#pragma once

#include <vector>
#include <queue>

#include "dllbase.h"

#include "msg_pdata.h"
#include "pointtypes.h"

#include "prot_base.h"

namespace Cti       {
namespace Protocol  {
namespace DNP       {

class TimeCTO;  //  forward declaration
class Time;

class IM_EX_PROT Object
{
protected:
    unsigned char _group, _variation;

    bool _valid;

    Object( int group, int variation );

    template<typename T>
    static T restoreValue(const void *buf)
    {
        return *reinterpret_cast<const T *>(buf);
    }

    template<typename T>
    static void serializeValue(void *buf, T value)
    {
        *reinterpret_cast<T *>(buf) = value;
    }

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
    bool _unsolicited;

    int  _group,
         _variation,
         _qualifier;

    unsigned short _start;

    typedef std::vector< const Object * > object_vector;
    typedef std::vector< int >            index_vector;
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

    void setUnsolicited();

    bool addObject( const Object *object );
    bool addObjectIndex( const Object *object, int index );
    bool addObjectRange( Object *objectArray, const unsigned start, const unsigned stop );

    unsigned size( void )  const;
    bool     empty( void ) const;
    object_descriptor at( unsigned index ) const;
    object_descriptor operator[]( unsigned index ) const;

    unsigned getSerializedLen( void ) const;
    unsigned serialize( unsigned char *buf ) const;

    int  restore( const unsigned char *buf, int len );

    void getPoints( Interface::pointlist_t &points, const TimeCTO *cto, const Time *arrival ) const;
};

}
}
}


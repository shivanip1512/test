#pragma once

#include "dllbase.h"

#include "msg_pdata.h"
#include "pointtypes.h"

#include "prot_base.h"

#include <boost/ptr_container/ptr_map.hpp>

#include <vector>
#include <queue>

namespace Cti {
namespace Protocols {
namespace DNP {

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

    ObjectBlock( QualifierType type, int group, int variation );

    enum QualifierType
    {
        //  makeRanged
        NoIndex_ByteStartStop  = 0x00,
        NoIndex_ShortStartStop = 0x01,

        //  Normal constructor
        NoIndex_NoRange        = 0x06,

        //  makeQuantity
        NoIndex_ByteQty        = 0x07,
        NoIndex_ShortQty       = 0x08,

        //  makeIndexed
        ByteIndex_ByteQty      = 0x17,
        ByteIndex_ShortQty     = 0x18,
        ShortIndex_ShortQty    = 0x28
    };

public:

    ObjectBlock();  //  for restoring a serialized object
    ~ObjectBlock();

    static std::auto_ptr<ObjectBlock> makeRangedBlock     (std::auto_ptr<Object> obj, const unsigned rangeStart);  //  can add boost::ptr_vector overload when necessary

    static std::auto_ptr<ObjectBlock> makeNoIndexNoRange  (int group, int variation);

    static std::auto_ptr<ObjectBlock> makeIndexedBlock    (std::auto_ptr<Object> obj, unsigned index);
    static std::auto_ptr<ObjectBlock> makeLongIndexedBlock(std::auto_ptr<Object> obj, unsigned index);
    template<class T>
    static std::auto_ptr<ObjectBlock> makeLongIndexedBlock(boost::ptr_map<unsigned, T> &objects);

    static std::auto_ptr<ObjectBlock> makeQuantityBlock   (std::auto_ptr<Object> obj);

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


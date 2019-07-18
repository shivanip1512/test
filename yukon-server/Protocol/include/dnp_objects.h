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


class ObjectBlock;

using ObjectPtr      = std::unique_ptr<const Object>;
using ObjectBlockPtr = std::unique_ptr<const ObjectBlock>;

class IM_EX_PROT ObjectBlock
{
    bool _unsolicited;

    int  _group,
         _variation,
         _qualifier;

    unsigned short _start;

    using object_vector = std::vector<ObjectPtr>;
    using index_vector  = std::vector<int>;
    object_vector _objectList;
    index_vector  _objectIndices;

    using RestoredObject = std::pair<std::unique_ptr<Object>, unsigned>;
    RestoredObject restoreObject   ( const unsigned char *buf, int len );
    RestoredObject restoreBitObject( const unsigned char *buf, int len, int bitpos );

    void erase( void );

    enum QualifierType
    {
        //  makeRanged
        NoIndex_ByteStartStop  = 0x00,
        NoIndex_ShortStartStop = 0x01,

        //  makeNoIndexNoRange
        NoIndex_NoRange        = 0x06,

        //  makeQuantity
        NoIndex_ByteQty        = 0x07,
        NoIndex_ShortQty       = 0x08,

        //  makeIndexed
        ByteIndex_ByteQty      = 0x17,
        ByteIndex_ShortQty     = 0x18,
        ShortIndex_ShortQty    = 0x28
    };

    static std::unique_ptr<ObjectBlock> makeObjectBlock( QualifierType type, int group, int variation );

    ObjectBlock( QualifierType type, int group, int variation );

public:

    ObjectBlock();  //  for restoring a serialized object
    ObjectBlock(const ObjectBlock &) = delete;
    ObjectBlock &operator=(const ObjectBlock &) = delete;

    static ObjectBlockPtr makeRangedBlock(ObjectPtr obj, const unsigned rangeStart);
    template<class T>
    static ObjectBlockPtr makeRangedBlock(std::map<unsigned, std::unique_ptr<const T>> objects);
    template<class T>
    static std::vector<ObjectBlockPtr> makeRangedBlocks(std::map<unsigned, std::unique_ptr<const T>> objects);

    static ObjectBlockPtr makeNoIndexNoRange  (int group, int variation);

    static ObjectBlockPtr makeIndexedBlock    (ObjectPtr obj, unsigned index);
    static ObjectBlockPtr makeLongIndexedBlock(ObjectPtr obj, unsigned index);

    static ObjectBlockPtr makeQuantityBlock   (ObjectPtr obj);

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

    size_t getIndexLength() const;
    size_t getQuantityLength() const;
    size_t getStartStopLength() const;

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


enum class ControlStatus : unsigned char
{
    Success           = 0,
    Timeout           = 1,
    NoSelect          = 2,
    FormatError       = 3,
    NotSupported      = 4,
    AlreadyActive     = 5,
    HardwareError     = 6,
    Local             = 7,
    TooManyObjs       = 8,
    NotAuthorized     = 9,
    AutomationInhibit = 10,
    ProcessingLimited = 11,
    OutOfRange        = 12,

    ReservedMin       = 13,
    ReservedMax       = 125,

    NonParticipating  = 126,

    Undefined         = 127,
};


}
}
}


/*-----------------------------------------------------------------------------*
*
* File:   dnp_objects
*
* Class:
* Date:   5/21/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2003/10/17 18:41:46 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_OBJECTS_H__
#define __DNP_OBJECTS_H__
#pragma warning( disable : 4786)


#include <vector>

#include <rw/tpslist.h>

#include "msg_pdata.h"
#include "pointtypes.h"

using namespace std;

class CtiDNPObject
{
protected:
    unsigned char _group, _variation;

    bool _valid;

    CtiDNPObject( int group, int variation );

public:

    virtual ~CtiDNPObject();

    int getGroup( void );
    int getVariation( void );

    virtual int restore( unsigned char *buf, int len );
    virtual int restoreBits( unsigned char *buf, int bitpos, int len );

    bool isValid( void );

    virtual int serialize( unsigned char *buf );
    virtual int getSerializedLen( void );

    virtual CtiPointDataMsg *getPoint( void );
};


class CtiDNPObjectBlock
{
private:
    bool _restoring,
         _valid;

    int  _group,
         _variation,
         _qualifier;

    unsigned short _qty, _start;

    vector< CtiDNPObject * > _objectList;
    vector< int > _objectIndices;

    int restoreObject( unsigned char *buf, int len, CtiDNPObject *&obj );
    int restoreBitObject( unsigned char *buf, int bitpos, int len, CtiDNPObject *&obj );

    void eraseObjectList( void );

public:
    enum QualifierType;

    CtiDNPObjectBlock();  //  for restoring a serialized object
    CtiDNPObjectBlock( enum QualifierType type );
    ~CtiDNPObjectBlock();

    enum QualifierType
    {
        NoIndex_ByteStartStop  = 0x00,
        NoIndex_ShortStartStop = 0x01,
        NoIndex_NoRange        = 0x06,
        NoIndex_ByteQty        = 0x07,
        NoIndex_ShortQty       = 0x08,
        ByteIndex_ByteQty      = 0x17,
        ShortIndex_ShortQty    = 0x28
    };

    enum
    {
        ObjectBlockMinSize = 3
    };

    void addObject( CtiDNPObject *object );
    void addObjectIndex( CtiDNPObject *object, int index );
//    void addRange( CtiDNPObject *objArray, int start, int stop );

//    CtiDNPObject *getObject( int index );

    int  getSerializedLen( void ) const;
    int  serialize( unsigned char *buf ) const;

    int  restore( unsigned char *buf, int len );
    bool hasPoints( void );
    void getPoints( RWTPtrSlist< CtiPointDataMsg > &pointList );

    bool isBinaryOutputControl( void )        const;
    int  getBinaryOutputControlStatus( void ) const;
    long getBinaryOutputControlOffset( void ) const;
};

#endif // #ifndef __DNP_OBJECTS_H__

#ifndef __RWDB_ORARDR_H__
#define __RWDB_ORARDR_H__

/**************************************************************************
 *
 * $Id$
 *
 ***************************************************************************
 *
 * Copyright (c) 1994-1999 Rogue Wave Software, Inc.  All Rights Reserved.
 *
 * This computer software is owned by Rogue Wave Software, Inc. and is
 * protected by U.S. copyright laws and other laws and by international
 * treaties.  This computer software is furnished by Rogue Wave Software,
 * Inc. pursuant to a written license agreement and may be used, copied,
 * transmitted, and stored only in accordance with the terms of such
 * license and with the inclusion of the above copyright notice.  This
 * computer software or any other copies thereof may not be provided or
 * otherwise made available to any other person.
 *
 * U.S. Government Restricted Rights.  This computer software is provided
 * with Restricted Rights.  Use, duplication, or disclosure by the
 * Government is subject to restrictions as set forth in subparagraph (c)
 * (1) (ii) of The Rights in Technical Data and Computer Software clause
 * at DFARS 252.227-7013 or subparagraphs (c) (1) and (2) of the
 * Commercial Computer Software – Restricted Rights at 48 CFR 52.227-19,
 * as applicable.  Manufacturer is Rogue Wave Software, Inc., 5500
 * Flatiron Parkway, Boulder, Colorado 80301 USA.
 *
 **************************************************************************
 *
 * Oracle OCI Access Library definition of RWDBReaderImp
 *
 **************************************************************************/

#include <rw/db/dbsrc/stmt.h>
#include <rw/db/dbsrc/readeri.h>

#include <rw/db/orasrc/oraval.h>
#include <rw/db/orasrc/orafutur.h>
#include <rw/db/orasrc/oracda.h>

//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
class RWDBOracleColumnData : public RWCollectable {
public:
    RWDBOracleColumnData( int rows, const RWDBColumn& column );
    ~RWDBOracleColumnData();
 
    osword                  datalength()
      { return datalength_; }
    int                     nativeType()
      { return nativetype_; }
    oub1*                   buffers()
      { return buffers_; }
    osb2*                   indicators()
      { return indicators_; }
    oub2*                   returnlengths()
      { return returnlengths_; }
    RWDBValue::ValueType    rwType() const
      { return rwtype_; }

    RWDBOracleValue         asValue( int row );
    RWBoolean               isNull( int row );
 
private:
    osword                  datalength_;
    RWDBValue::ValueType    rwtype_;
//    RWBoolean               nativetype_;
    int                     nativetype_;
    oub1*                   buffers_;
    osb2*                   indicators_;
    oub2*                   returnlengths_;
};

////////////////////////////////////////////////////////////////////////////
//
// Most of the functions of RWDBOracleReader are implemented by this class.
//
////////////////////////////////////////////////////////////////////////////
class RWDBAccessExport RWDBOracleReaderBodyImp : public RWDBReference {

friend class RWDBAccessExport RWDBOracleReaderImp;
friend class RWDBAccessExport RWDBOracleReaderFutureImp;
private:
    RWDBOracleReaderBodyImp(
        const RWDBOracleCDA&  aCda, RWDBStatement&    stmt );

    ~RWDBOracleReaderBodyImp();

    void             constructReader( const RWDBTable& table,
                                            RWDBStatus *rdrStatus);
    RWBoolean        fetchRow( RWDBShiftableRow *row,
                               RWDBStatus       *rdrStatus );

    int              shiftOutPosition();
    void             setShiftOutPosition( size_t pos );
    RWBoolean        isValidPosition( RWDBStatus* status );

    void     getValue(RWDBNullIndicator& aNullInd, RWDBStatus* rdrStatus);
    void     getValue(RWDBRow& aRow, RWDBStatus* rdrStatus);
    void     getValue(char& value, RWDBStatus* rdrStatus);
    void     getValue(unsigned char& value, RWDBStatus* rdrStatus);
    void     getValue(short& value, RWDBStatus* rdrStatus);
    void     getValue(unsigned short& value, RWDBStatus* rdrStatus);
    void     getValue(int& value, RWDBStatus* rdrStatus);
    void     getValue(unsigned int& value, RWDBStatus* rdrStatus);
    void     getValue(long& value, RWDBStatus* rdrStatus);
    void     getValue(unsigned long& value, RWDBStatus* rdrStatus);
    void     getValue(float& value, RWDBStatus* rdrStatus);
    void     getValue(double& value, RWDBStatus* rdrStatus);
    void     getValue(RWDecimalPortable& value, RWDBStatus* rdrStatus);
    void     getValue(RWDate& value, RWDBStatus* rdrStatus);
    void     getValue(RWTime& value, RWDBStatus* rdrStatus);
    void     getValue(RWDBDateTime& value, RWDBStatus* rdrStatus);
    void     getValue(RWDBDuration& value, RWDBStatus* rdrStatus);
    void     getValue(RWCString& value, RWDBStatus* rdrStatus);
    void     getValue(RWDBMBString& value, RWDBStatus* rdrStatus);
#ifndef RW_NO_WSTR
    void     getValue(RWWString& value, RWDBStatus* rdrStatus);
#endif
    void     getValue(RWDBBlob& value, RWDBStatus* rdrStatus);
    void     getValue(RWDBValue& value, RWDBStatus* rdrStatus);

    void                         fetchLongValue( RWDBValue& value,
                                                 osword     position );

    RWDBConnection   connection_;  // Should not need this data member.
    RWDBStatement    stmt_;
        RWDBOracleCDA    aCda_;        // Can get connection from aCda_.connection()
    RWOrdered        columnData_;
    int              boundRows_;
    int              fetchedRows_;
    int              lastRow_;
    int              column_;   
    RWBoolean        endOfFetch_;   

    // not implemented
    RWDBOracleReaderBodyImp  ( const RWDBOracleReaderBodyImp& );
    RWDBOracleReaderBodyImp& operator=( const RWDBOracleReaderBodyImp& );
};

//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
class RWDBAccessExport RWDBOracleReaderImp : public RWDBReaderImp {
public:
    RWDBOracleReaderImp        ( const RWDBTable&      table,
                                 const RWDBOracleCDA&  aCda,
                                 const RWDBStatus&     status,
                                     RWDBStatement& stmt);
    ~RWDBOracleReaderImp       ();

    RWBoolean                      fetchRow( RWDBShiftableRow* row );

    virtual RWDBReaderImp&         operator>>(RWDBNullIndicator& aNullInd);
    virtual RWDBReaderImp&         operator>>(RWDBRow& aRow);
    virtual RWDBReaderImp&         operator>>(char& value);
    virtual RWDBReaderImp&         operator>>(unsigned char& value);
    virtual RWDBReaderImp&         operator>>(short& value);
    virtual RWDBReaderImp&         operator>>(unsigned short& value);
    virtual RWDBReaderImp&         operator>>(int& value);
    virtual RWDBReaderImp&         operator>>(unsigned int& value);
    virtual RWDBReaderImp&         operator>>(long& value);
    virtual RWDBReaderImp&         operator>>(unsigned long& value);
    virtual RWDBReaderImp&         operator>>(float& value);
    virtual RWDBReaderImp&         operator>>(double& value);
    virtual RWDBReaderImp&         operator>>(RWDecimalPortable& value);
    virtual RWDBReaderImp&         operator>>(RWDate& value);
    virtual RWDBReaderImp&         operator>>(RWTime& value);
    virtual RWDBReaderImp&         operator>>(RWDBDateTime& value);
    virtual RWDBReaderImp&         operator>>(RWDBDuration& value);
    virtual RWDBReaderImp&         operator>>(RWCString& value);
    virtual RWDBReaderImp&         operator>>(RWDBMBString& value);
#ifndef RW_NO_WSTR
    virtual RWDBReaderImp&         operator>>(RWWString& value);
#endif
    virtual RWDBReaderImp&         operator>>(RWDBBlob& value);
    virtual RWDBReaderImp&         operator>>(RWDBValue& value);

    int                        shiftOutPosition();
    void                       setShiftOutPosition( size_t pos );
    void                       setShiftOutPosition( const RWCString&  value );
    void                       setShiftOutPosition( const RWDBColumn& value );


private:
    RWDBOracleReaderBodyImp *rdrBodyImp_;

    // not implemented
    RWDBOracleReaderImp        ( const RWDBOracleReaderImp& );
    RWDBOracleReaderImp&       operator=( const RWDBOracleReaderImp& );
};

////////////////////////////////////////////////////////////////////////////
//
// RWDBOracleReader specific Oracle Future Imp
//
////////////////////////////////////////////////////////////////////////////
class RWDBAccessExport RWDBOracleReaderFutureImp : public RWDBOracleFutureImp{

public :
    RWDBOracleReaderFutureImp( const RWDBConnection&     conn,
                               const RWDBStatus&         status,
                               RWDBOraOciCalls           oci,
                               short                     stages,   
                               RWDBOracleReaderBodyImp   *rdrBodyImp
                             );
    ~RWDBOracleReaderFutureImp();

protected :
    RWDBOraRetCode          doNext();
private :
    RWDBOracleReaderBodyImp *rdrBodyImp_;

    // not implemented
    RWDBOracleReaderFutureImp ( const RWDBOracleReaderFutureImp& );
    RWDBOracleReaderFutureImp& operator=( const RWDBOracleReaderFutureImp& );
};
#endif


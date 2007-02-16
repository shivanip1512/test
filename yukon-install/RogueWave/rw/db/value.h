#ifndef __RWDB_VALUE_H__
#define __RWDB_VALUE_H__

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
 * Definition of class RWDBValue
 *
 * This class provides a central interface to all database values, i.e. 
 * the C++ primitives int, float, etc, as well as RWDBDateTime, RWCString,
 * etc. The value can be set either thru the constructors or the assignment
 * operators. IS NULL/ IS NOT NULL semantics are added for those types
 * (i.e. the primitives) which do not support them natively.
 *
 * Explicit type conversion routines (the asXXX() methods) are provided,
 * automatic type conversion is not provided. 
 *
 * RWDBValue is derived from RWCollectable, and the compareTo() and isEqual()
 * methods required for RWCollectables are redefined. They should be used
 * with care, however, since no type conversion is implicit in them. RWDBValue
 * instances representing different types will never be equal. For example:
 * short y; int x = y;
 * if (RWDBValue(x).compareTo(RWDBValue(y)) == 0)
 *     //  never happens.
 *
 **************************************************************************/


#include <rw/db/defs.h>
#include <rw/db/collect.h>

#ifndef RW_NO_WSTR
#include <rw/wstring.h>
#endif
#include <rw/db/mbstring.h>
#include <rw/rwdate.h>
#if( RWTOOLS < 0x700 )
#include <rw/rwfile.h>
#include <rw/vstream.h>
#endif


// To manipulate state_:
#define RWDBHIBIT               0x80
#define RWDBNOTHIBIT            0x7f
#define RWDBSTATE(isNull, type) ((isNull) ? ((type) | RWDBHIBIT) : (type))
#define RWDBISNULLSTATE(state)  ((state) & RWDBHIBIT)
#define RWDBTYPE(state)         ((ValueType)((state) & RWDBNOTHIBIT))


// define the RWDBValueManip for the assigment operator with rwdbNull
//
// NOTE: Null values, regardless of type, return a default value;
//       zero(0) or default constructor.  isNull() should always be
//       used to determine null values, not the default null value.

class RWDBExport RWDBValue;
typedef void (*RWDBValueManip)(RWDBValue&);
void rwdbexport rwdbNull(RWDBValue&);


// define the RWDBValue class and members

class RWDBExport RWDBValue : public RWCollectable
{
    RWDBDECLARE_COLLECTABLE(RWDBValue);

    friend void rwdbexport rwdbNull(RWDBValue&);
public:
    enum ValueType {
        NoType = 0,
        Char,     UnsignedChar,
        Tiny,     UnsignedTiny,
        Short,    UnsignedShort,
        Int,      UnsignedInt,
        Long,     UnsignedLong,
        Float,    Double,
        Decimal,
        Date, DateTime, Duration,
        String,   Blob, 
#ifndef RW_NO_WSTR
        WString,
#endif
        MBString
    };

    // constructors
    RWDBValue();
    RWDBValue(const RWDBValue& value);
    RWDBValue(RWDBValueManip value);
    RWDBValue(char value);
    RWDBValue(unsigned char value);
    RWDBValue(short value);
    RWDBValue(unsigned short value);
    RWDBValue(int value);
    RWDBValue(unsigned int value);
    RWDBValue(long value);
    RWDBValue(unsigned long value);
    RWDBValue(float value);
    RWDBValue(double value);
    RWDBValue(const char *value);
#ifndef RW_NO_WSTR
    RWDBValue(const wchar_t *value);
#endif
    RWDBValue(const RWDecimalPortable& value);
    RWDBValue(const RWDate& value);
    RWDBValue(const RWDBDateTime& value);
    RWDBValue(const RWDBDuration& value);
    RWDBValue(const RWCString& value);
    RWDBValue(const RWDBBlob& value);
#ifndef RW_NO_WSTR
    RWDBValue(const RWWString& value);
#endif
    RWDBValue(const RWDBMBString& value);
    
    // destructor
    virtual ~RWDBValue();

    // RWCollectable compliant member functions
    virtual RWspace     binaryStoreSize() const;
    virtual int         compareTo(const RWCollectable* value) const;
    virtual RWBoolean   isEqual(const RWCollectable* value) const;
    virtual unsigned    hash() const;
    virtual void        saveGuts(RWFile&     file) const;
    virtual void        saveGuts(RWvostream& stream) const;
    virtual void        restoreGuts(RWFile&     file);
    virtual void        restoreGuts(RWvistream& stream);

    // public member functions
    ValueType           type() const;
    RWBoolean           isNull() const;
    RWBoolean           canConvert(ValueType type) const;

    char                asChar() const;
    unsigned char       asUnsignedChar() const;
    short               asShort() const;
    unsigned short      asUnsignedShort() const;
    int                 asInt() const;
    unsigned int        asUnsignedInt() const;
    long                asLong() const;
    unsigned long       asUnsignedLong() const;
    float               asFloat() const;
    double              asDouble() const;
    RWDecimalPortable   asDecimal() const;
    RWDate              asDate() const;
    RWDBDateTime        asDateTime() const;
    RWDBDuration        asDuration() const;
    RWCString           asString() const;
    RWCString           asString(const RWDBPhraseBook& phrasebook) const;
    void                asString(const RWDBPhraseBook& phrasebook,
                                       RWCString& result) const;
    RWDBBlob            asBlob() const;
#ifndef RW_NO_WSTR
    RWWString           asWString() const;
#endif
    RWDBMBString        asMBString() const;

    // public member operators
    RWDBValue&  operator=(const RWDBValue& value);
    RWDBValue&  operator=(RWDBValueManip value);
    RWDBValue&  operator=(char value);
    RWDBValue&  operator=(unsigned char value);
    RWDBValue&  operator=(short value);
    RWDBValue&  operator=(unsigned short value);
    RWDBValue&  operator=(int value);
    RWDBValue&  operator=(unsigned int value);
    RWDBValue&  operator=(long value);
    RWDBValue&  operator=(unsigned long value);
    RWDBValue&  operator=(float value);
    RWDBValue&  operator=(double value);
    RWDBValue&  operator=(const RWDecimalPortable& value);
    RWDBValue&  operator=(const RWDate& value);
    RWDBValue&  operator=(const RWDBDateTime& value);
    RWDBValue&  operator=(const RWDBDuration& value);
    RWDBValue&  operator=(const RWCString& value);
    RWDBValue&  operator=(const RWDBBlob& value);
#ifndef RW_NO_WSTR
    RWDBValue&  operator=(const RWWString& value);
#endif
    RWDBValue&  operator=(const RWDBMBString& value);

      // static asString methods.  These do not require creating
      // an RWDBValue object
    static void asString(char v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);
    static void asString(unsigned char v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);
    static void asString(short v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);
    static void asString(unsigned short v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);
    static void asString(int v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);
    static void asString(unsigned int v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);
    static void asString(long v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);
    static void asString(unsigned long v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);
    static void asString(float v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);
    static void asString(double v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);
    static void asString(const RWDecimalPortable& v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);
    static void asString(const RWDate& v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);
    static void asString(const RWDBDateTime& v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);

    static void asString(const RWDBDuration& v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);

    static void asString(const RWCString& v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);
    static void asString(const RWDBBlob& v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);
#ifndef RW_NO_WSTR
    static void asString(const RWWString& v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);
#endif
    static void asString(const RWDBMBString& v,
                         const RWDBPhraseBook& pb,
                               RWCString& theString);

protected:
    RWDBValue(ValueType type);         // construct typed NULL

protected:
    
    union{
        long                long_;    // stores char, tiny, short, int and long
        unsigned long       ulong_;      // stores unsigned versions of above
        double              double_;     // stores float and double
        RWDecimalPortable   *decimal_;
        RWDBDateTime        *datetime_;  // stores date and datetime
        RWDBDuration        *duration_;
        RWCString           *string_;
        RWDBBlob            *blob_;
#ifndef RW_NO_WSTR
        RWWString           *wstring_;
#endif
    };
    
private:
    void                    cleanup();
    static void             enclose(RWCString& string, const char *openquote,
                                  const char *closequote, const char *escape,
                                  const char *quotePrefix = 0);
    static void             duplicateCharacter(RWCString& string, const char *escape);

    unsigned char state_;                // codes type and nullness
    
    static const char  RWDBConversions[];  // table of allowable conversions
};

#endif


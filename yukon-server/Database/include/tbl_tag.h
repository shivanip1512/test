#pragma once

#include "dllbase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableTag : public CtiMemDBObject
{
    int             _tagId;
    std::string       _tagName;
    int             _tagLevel;
    bool            _inhibit;
    int             _colorId;
    int             _imageId;

public:

    typedef CtiMemDBObject Inherited;


    CtiTableTag();
    CtiTableTag(const CtiTableTag& aRef);
    virtual ~CtiTableTag();

    virtual int operator==(const CtiTableTag& aRef) const;

    static std::string getTableName();

    static std::string getSQLCoreStatement();

    void DecodeDatabaseReader(Cti::RowReader& rdr);

    int getTagId() const;
    bool getInhibit() const;
    std::string getTagName() const;
};

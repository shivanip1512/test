#pragma once

class CtiMemDBObject
{
protected:
    /*
     *  Add any dynamic data here....
     */
    union
    {
        UINT     Flag;
        struct
        {
            UINT _valid   : 1;
            UINT _updated : 1;       // Bit indicates if the last re-load touched this objects.  FALSE if not.
            UINT _dirty   : 1;       // Someone has sent me an update on this guy
        };
    };


private:

public:
    CtiMemDBObject() :
    Flag(0)
    {}

    CtiMemDBObject(const CtiMemDBObject& aRef)
    {
        *this = aRef;
    }

    virtual ~CtiMemDBObject() {}

    CtiMemDBObject& operator=(const CtiMemDBObject& aRef)
    {
        if(this != &aRef)
        {
            _valid   = aRef.isValid();
            _updated = aRef.getUpdatedFlag();
            _dirty   = aRef.isDirty();
        }
        return *this;
    }

    BOOL getUpdatedFlag() const            { return _updated;}
    void resetUpdatedFlag(BOOL b = FALSE)  { _updated = b;}
    void setUpdatedFlag(BOOL b = TRUE)     { _updated = b;}

    BOOL isValid() const                   { return _valid;}
    void setValid(BOOL i = TRUE)           { _valid = i;}
    void resetValid(BOOL i = FALSE)        { _valid = i;}

    BOOL isDirty() const                   { return _dirty;}
    virtual void setDirty(BOOL b = TRUE)   { _dirty = b;}
    void resetDirty(BOOL b = FALSE)        { _dirty = b;}
};

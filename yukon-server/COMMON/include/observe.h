#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "types.h"
#include "dlldefs.h"
#include <list>
#include "utility.h"

class CtiObservable;

class IM_EX_CTIBASE CtiObserver
{
public:
    virtual ~CtiObserver() { };

    virtual void update(CtiObservable& observable) = 0;

    virtual int operator==(const CtiObserver& obs) const;

protected:
    CtiObserver() { };

};

class IM_EX_CTIBASE CtiObservable
{
public:
    virtual ~CtiObservable();

    virtual void addObserver(CtiObserver& observer);
    virtual void deleteObserver(CtiObserver& observer);
    virtual void deleteObservers();

    virtual void notifyObservers();

    virtual void setChanged();
    virtual void clearChanged();
    virtual BOOL hasChanged() const;

    virtual UINT countObservers() const;

    virtual void setNotifyEnabled(BOOL value);
    virtual BOOL isNotifyEnabled() const;

protected:
    CtiObservable();

    std::list< CtiObserver* > _observers;
    BOOL _haschanged;

    BOOL _notifyenabled;

};

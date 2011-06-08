/*-----------------------------------------------------------------------------
    Filename:  observe.h

    Programmer:  Aaron Lauinger

    Description:    Header file for CtiObserver and CtiObservable
                    which are implementations of the Observer pattern.
                    Many CtiObservers can be regiseterd with a CtiObservable
                    in order to receive notifications of state changes in the
                    CtiObservable.  Each CtiObserver can be registered with
                    many CtiObservables also.
    Initial Date:  4/7/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/

#ifndef CTIOBSERVE_H
#define CTIOBSERVE_H


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

#endif

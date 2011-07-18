/*-----------------------------------------------------------------------------
    Filename:  observe.cpp

    Programmer:  Aaron Lauinger

    Description:    Source file for CtiObserver and CtiObservable

    Initial Date:  4/7/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#include "precompiled.h"
#include "observe.h"

/*---------------------------------------------------------------------------
    Equality operator for CtiObserver
----------------------------------------------------------------------------*/
int CtiObserver::operator==(const CtiObserver& obs) const
{
    return ( this == &obs );
}


/*===========================================================================
    CtiObserverable
============================================================================*/

/*---------------------------------------------------------------------------
    Constructor

    Private to keep CtiObservables from being created by anyone other than
    a subclass
---------------------------------------------------------------------------*/
CtiObservable::CtiObservable() : _haschanged(FALSE), _notifyenabled(TRUE)
{
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiObservable::~CtiObservable()
{
}


/*---------------------------------------------------------------------------
    addObserver

    addObserver adds a CtiObserver to the list of observers to be notified
---------------------------------------------------------------------------*/
void CtiObservable::addObserver(CtiObserver& observer)
{
    _observers.push_back(&observer);
}

/*---------------------------------------------------------------------------
    deleteObserver

    deleteObserver deletes a CtiObserver from the list of observers to be
    notified
-----------------------------------------------------------------------------*/
void CtiObservable::deleteObserver(CtiObserver& observer)
{
   for (std::list<CtiObserver*>::iterator itr = _observers.begin(); itr != _observers.end(); itr++) {
        if (**itr == observer) {
            _observers.erase(itr);
            break;
        }
   }


}

/*---------------------------------------------------------------------------
    deleteObservers

    deleteObservers deletes all CtiObservers from the list of observers to be
    notified
---------------------------------------------------------------------------*/
void CtiObservable::deleteObservers()
{
    delete_container(_observers);
    _observers.clear();
}

/*---------------------------------------------------------------------------
    notifyObservers

    notifyObservers calls the update member function in all of the
    CtiObservers in the list of observers to be notified if self has been
    changed.
---------------------------------------------------------------------------*/
void CtiObservable::notifyObservers()
{
    if( hasChanged() && isNotifyEnabled() )
    {
        std::list< CtiObserver* >::iterator itr = _observers.begin();
        while( itr != _observers.end() )
        {
            CtiObserver* ptr = *itr++;
             ptr->update(*this);
        }

        clearChanged();
    }
}

/*---------------------------------------------------------------------------
    setChanged

    setChanged sets the state of the observable to that of changed or
    modified
---------------------------------------------------------------------------*/
void CtiObservable::setChanged()
{
    _haschanged = TRUE;
}

/*---------------------------------------------------------------------------
    clearChanged

    clearChanged sets the state of the observable to that of unchanged or
    unmodified
---------------------------------------------------------------------------*/
void CtiObservable::clearChanged()
{
    _haschanged = FALSE;
}

/*---------------------------------------------------------------------------
    hasChanged

    hasChanged returns TRUE if the observable has been changed or modified
    and FALSE otherwise
---------------------------------------------------------------------------*/
BOOL CtiObservable::hasChanged() const
{
    return _haschanged;
}

/*---------------------------------------------------------------------------
    countObservers

    countObservers returns the number of CtiObservers in the list of
    observers to be notified
---------------------------------------------------------------------------*/
UINT CtiObservable::countObservers() const
{
    return _observers.size();
}

/*---------------------------------------------------------------------------
    setNotifyEnabled

    Sets whether or not notifyObservers(xxx) will actually notify observers.
    Useful for making changed to an Observable that you don't want propagated
    to the Observers.
---------------------------------------------------------------------------*/
void CtiObservable::setNotifyEnabled(BOOL value)
{
    _notifyenabled = TRUE;
    return;
}

/*---------------------------------------------------------------------------
    isNotifyEnabled

    Returns TRUE if observers will be notified from notifyObservers(xxx),
    FALSE otherwise.
---------------------------------------------------------------------------*/
BOOL CtiObservable::isNotifyEnabled() const
{
    return _notifyenabled;
}


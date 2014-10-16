#include "precompiled.h"
#include "pointstore.h"

using std::endl;

RWDEFINE_NAMED_COLLECTABLE(CtiPointStoreElement,"CtiPointStoreElement")

CtiPointStoreElement *CtiPointStore::insertPointElement( long pointNum, long dependentId, enum PointUpdateType updateType )
{
    CtiPointStoreElement *newElement;
    CtiHashKey *newHashKey;

    try
    {
        if( pointNum == 0 )
            return NULL;

        newElement = CTIDBG_new CtiPointStoreElement( pointNum );
        newHashKey = CTIDBG_new CtiHashKey( pointNum );

        //  if the insertion wasn't successful, that means this point is already in the pointstore
        if( !(this->insert( newHashKey, newElement )) )
        {
            delete newElement;
            //  may as well use the indexing hashkey before deleting it...
            newElement = (CtiPointStoreElement *)((*this).findValue(newHashKey));
            delete newHashKey;
        }

        //  in either case, newElement now points to the CtiPointStoreElement of pointID pointNum...

        //  we append the pointID of the calc point that is dependent on it...
        if( newElement != rwnil
            && ( updateType == allUpdate || updateType == anyUpdate || updateType == periodicPlusUpdate || dependentId == 0)
            && dependentId >= 0 )// this be for the calc points cause they ain't got no dependents
        {
            newElement->appendDependent( dependentId, updateType );
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return newElement;
}

void CtiPointStore::removePointElement( long pointNum )
{
    CtiPointStoreElement *element;
    CtiHashKey *hashKey;

    try
    {
        if( pointNum !=0 )
        {
            hashKey = CTIDBG_new CtiHashKey( pointNum );

            element = (CtiPointStoreElement *)((*this).findValue(hashKey));
            this->removeAll( hashKey );

            if( element != rwnil )
            {
                delete element;
            }

            delete hashKey;
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/* Pointer to the singleton instance of CtiPointStore
   Instantiate lazily by Instance */
CtiPointStore* CtiPointStore::_instance = NULL;

/*---------------------------------------------------------------------------
    getInstance

    Returns a pointer to the singleton instance of CtiPointStore
---------------------------------------------------------------------------*/
CtiPointStore *CtiPointStore::getInstance()
{
    if ( _instance == NULL )
    {
        _instance = CTIDBG_new CtiPointStore();
    }

    return _instance;
}

void CtiPointStore::removeInstance()
{
    if ( _instance != NULL )
    {
        delete _instance;
        _instance = NULL;
    }
}


#pragma once

template <class T>
T *create_object(long objectid, string name)
{
    T *object = new T();

    object->setPAOId(objectid);
    object->setPAOName(name);
    return object;
}

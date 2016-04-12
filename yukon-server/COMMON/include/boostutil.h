#pragma once

#include <iostream>
#include "utility.h"  //  defines autopsy()

//  for use with shared_ptr<>
struct null_deleter
{
    void operator()(void const *) const  {}
};


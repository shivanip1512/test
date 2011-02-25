#pragma once

#include "dlldefs.h"

#define FO(x) (::Cti::filename_only((x)))

namespace Cti {

IM_EX_CTIBASE const char *filename_only(const char *filename_and_path);

}

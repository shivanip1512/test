#pragma once

#include "dlldefs.h"

#include <vector>
#include <array>

namespace Cti {

using Md5Digest = std::array<unsigned char, 16>;

IM_EX_CTIBASE Md5Digest calculateMd5Digest(const std::vector<unsigned char>& data);

}
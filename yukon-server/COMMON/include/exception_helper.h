#pragma once

#include <string>
#include "dlldefs.h"

class SAException;

namespace Cti {
namespace Logging {

/**
 * Retrieve the exception cause for logging
 */
std::string IM_EX_CTIBASE getExceptionCause(const std::exception& e);
std::string IM_EX_CTIBASE getExceptionCause(const SAException& e);

std::string IM_EX_CTIBASE getUnknownExceptionCause();

}
} // namespace Cti::Log


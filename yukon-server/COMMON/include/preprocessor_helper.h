#pragma once

#define IN_PARENS(x) (##x##)

#define DO_R_PREFIX(x) R##x
#define R_PREFIX(x) DO_R_PREFIX(x)

#define DO_STRINGIZE(x) #x
#define STRINGIZE(x) DO_STRINGIZE(x)

#define RAW_STRINGIZE(x) R_PREFIX(STRINGIZE(IN_PARENS(x)))
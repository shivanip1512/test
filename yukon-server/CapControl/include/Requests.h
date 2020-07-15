#pragma once

#include <string>
#include <memory>

class CtiCCCapBank;
class CtiRequestMsg;

namespace Cti           {
namespace CapControl    {

enum BankOperationType
{
    BankOperation_Open,
    BankOperation_Close,
    BankOperation_Flip,
    BankOperation_Unknown
};

BankOperationType resolveOperationTypeForPointId(const std::string &commandString, CtiCCCapBank & bank);

std::unique_ptr<CtiRequestMsg> createBankOpenRequest (const CtiCCCapBank &capBank);
std::unique_ptr<CtiRequestMsg> createBankCloseRequest(const CtiCCCapBank &capBank);
std::unique_ptr<CtiRequestMsg> createBankFlipRequest (const CtiCCCapBank &capBank);

CtiRequestMsg* createPorterRequestMsg(long controllerId,const std::string& commandString);
CtiRequestMsg* createPorterRequestMsg(long controllerId,const std::string& commandString, const std::string& user);

}}

package com.cannontech.services.password;

public interface EncryptPlainTextPasswordsService {
    void encryptPassword(int userId);
    void encryptPasswordHistory(int passwordHistoryId);
}

package com.cannontech.services.password.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authentication.dao.PasswordHistoryDao;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.model.PasswordHistory;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.PasswordEncrypter;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.services.password.EncryptPlainTextPasswordsService;

public class EncryptPlainTextPasswordsServiceImpl implements EncryptPlainTextPasswordsService {
    private final static Logger log = YukonLogManager.getLogger(EncryptPlainTextPasswordsServiceImpl.class);

    @Autowired private PasswordEncrypter encrypter;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private AuthenticationService authenticationService;
    @Autowired private PasswordHistoryDao passwordHistoryDao;

    @Override
    @Transactional(isolation=Isolation.SERIALIZABLE)
    public void encryptPassword(int userId) {
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        // We need to check again before we encrypt it in case it got encrypted by the user logging in.
        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserID());
        if (userAuthenticationInfo.getAuthType() == AuthType.PLAIN) {
            authenticationService.encryptPlainTextPassword(user);
        }
    }

    @Override
    @Transactional
    public void encryptPasswordHistory(int passwordHistoryId) {
        PasswordHistory passwordHistory = passwordHistoryDao.getPasswordHistory(passwordHistoryId);
        AuthType authType = AuthenticationCategory.ENCRYPTED.getSupportingAuthType();
        if (passwordHistory.getAuthType() == AuthType.PLAIN) {
            String digest = encrypter.encryptPassword(passwordHistory.getPassword());
            passwordHistory.setAuthType(authType);
            passwordHistory.setPassword(digest);
            passwordHistoryDao.updateWithEncryptedPassword(passwordHistory);
        }
    }

    private void encryptPasswords() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT userId FROM yukonUser WHERE authType").eq_k(AuthType.PLAIN);
        List<Integer> userIds = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        if (userIds.size() == 0) {
            return;
        }
        log.info("begining to encrypt " + userIds.size() + " passwords");
        int count = 0;
        for (Integer userId : userIds) {
            encryptPassword(userId);
            if (++count % 1000 == 0) {
                log.info("finished with " + count + " passwords so far");
            }
        }
        log.info("finished encrypting " + userIds.size() + " passwords");
    }

    private void encryptPasswordHistory() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT passwordHistoryId, password FROM passwordHistory WHERE authType").eq_k(AuthType.PLAIN);
        List<Integer> passwordHistoryIds = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        if (passwordHistoryIds.size() == 0) {
            return;
        }
        log.info("begining to encrypt " + passwordHistoryIds.size() + " historical passwords");
        int count = 0;
        for (Integer passwordHistoryId : passwordHistoryIds) {
            encryptPasswordHistory(passwordHistoryId);
            if (++count % 1000 == 0) {
                log.info("finished with " + count + " historical passwords so far");
            }
        }
        log.info("finished encrypting " + passwordHistoryIds.size() + " historical passwords");
    }

    @PostConstruct
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                encryptPasswords();
                encryptPasswordHistory();
            }
        }).start();
    }
}

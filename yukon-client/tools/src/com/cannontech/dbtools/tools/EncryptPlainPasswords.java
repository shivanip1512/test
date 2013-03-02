package com.cannontech.dbtools.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.service.impl.LocalHashV2AuthenticationService;
import com.cannontech.database.PoolManager;

public class EncryptPlainPasswords {
    LocalHashV2AuthenticationService authenticationService = new LocalHashV2AuthenticationService();

    private void encryptPasswords(Connection conn, Statement stmt, String tableName, String idColumnName)
            throws SQLException {
        String querySql = "SELECT " + idColumnName + ", password FROM " + tableName + " WHERE authType = 'PLAIN'";
        String updateSql = "UPDATE " + tableName + " SET authType = ?, password = ? WHERE " + idColumnName + " = ?";
        try (ResultSet rs = stmt.executeQuery(querySql); PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            while (rs.next()) {
                int id = rs.getInt(idColumnName);
                String password = rs.getString("password");
                String newDigest = authenticationService.encryptPassword(password);

                pstmt.clearParameters();
                pstmt.setString(1, AuthType.HASH_SHA_V2.name());
                pstmt.setString(2, newDigest);
                pstmt.setInt(3, id);
                pstmt.execute();
            }
        }
    }

    public EncryptPlainPasswords() {
        try (Connection conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias())) {
            try (Statement stmt = conn.createStatement()) {
                encryptPasswords(conn, stmt, "yukonUser", "userId");
                encryptPasswords(conn, stmt, "passwordHistory", "passwordHistoryId");
            }
        } catch (SQLException sqle) {
            System.out.println("database error:");
            sqle.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EncryptPlainPasswords();
    }
}

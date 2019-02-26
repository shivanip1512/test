package com.cannontech.support.service.impl;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.tools.zip.ZipWriter;
import com.opencsv.CSVWriter;

public abstract class SupportBundleSqlWriter extends AbstractSupportBundleWriter {
    private static final Logger log = YukonLogManager.getLogger(SupportBundleSqlWriter.class);
    private final static String databaseZipDir = "Database";

    private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public void addToZip(final ZipWriter zipWriter, ReadableInstant start, ReadableInstant stop) {
        SqlFragmentSource sqlStatement = getSqlFragmentSource();
        ResultSetExtractor<?> resultSetExtractor = new ResultSetExtractor<Object>() {
            @Override
            public Object extractData(ResultSet resultSet) throws SQLException,
                        DataAccessException {
                Writer pw = zipWriter.getBufferedWriter(databaseZipDir, getZipFilename());

                CSVWriter csvWriter = new CSVWriter(pw);

                try {
                    csvWriter.writeAll(resultSet, true);
                    pw.flush();
                } catch (IOException e) {
                    log.error("Error while trying to write a resultSet to the zip file", e);
                    throw new RuntimeException(e);
                }

                return null;
            }
        };

        yukonJdbcTemplate.query(sqlStatement, resultSetExtractor);
    }

    protected abstract SqlFragmentSource getSqlFragmentSource();

    protected abstract String getZipFilename();

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}

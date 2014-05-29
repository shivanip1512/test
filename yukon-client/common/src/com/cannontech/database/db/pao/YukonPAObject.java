package com.cannontech.database.db.pao;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.spring.YukonSpringHook;

public class YukonPAObject extends com.cannontech.database.db.DBPersistent implements YukonPao {
    private Integer paObjectID = null;
    private String paoName = null;
    private PaoType paoType = null;
    private String description = com.cannontech.common.util.CtiUtilities.STRING_NONE;
    private Character disableFlag = new Character('N');
    private String paoStatistics = "-----";

    public static final String SETTER_COLUMNS[] = { "Category", "PAOClass",
            "PAOName", "Type", "Description", "DisableFlag", "PAOStatistics" };

    public static final String CONSTRAINT_COLUMNS[] = { "PAObjectID" };

    public static final String TABLE_NAME = "YukonPAObject";

    public YukonPAObject() {
        super();
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return new PaoIdentifier(paObjectID, paoType);
    }
    
    @Override
    public void add() throws java.sql.SQLException {
        Object addValues[] = {
                paObjectID,
                paoType.getPaoCategory().getDbString(),
                paoType.getPaoClass().getDbString(),
                StringUtils.left(paoName, 60),
                paoType.getDbString(),
                description,
                disableFlag,
                paoStatistics
                };

        add(TABLE_NAME, addValues);
    }

    @Override
    public void delete() throws java.sql.SQLException {
        Object values[] = { paObjectID };

        delete(TABLE_NAME, CONSTRAINT_COLUMNS, values);

        UserPageDao userPageDao = YukonSpringHook.getBean(UserPageDao.class);
        userPageDao.deletePagesForPao(getPaoIdentifier());
    }

    public java.lang.String getDescription() {
        return description;
    }

    public java.lang.Character getDisableFlag() {
        return disableFlag;
    }

    public java.lang.Integer getPaObjectID() {
        return paObjectID;
    }

    public java.lang.String getPaoName() {
        return paoName;
    }

    public java.lang.String getPaoStatistics() {
        return paoStatistics;
    }

    public Object getPrimaryKey() {
        return getPaObjectID();
    }

    public PaoType getPaoType() {
        return paoType;
    }
    
    @Override
    public void retrieve() throws java.sql.SQLException {
        Object constraintValues[] = { paObjectID };
        Object results[] = retrieve(SETTER_COLUMNS,
                                    TABLE_NAME,
                                    CONSTRAINT_COLUMNS,
                                    constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
//            setCategory((String) results[0]);
//            setPaoClass((String) results[1]);
            setPaoName((String) results[2]);
            PaoType.getForDbString((String) results[3]);
            setDescription((String) results[4]);
            setDisableFlag(new Character(((String) results[5]).charAt(0)));
            setPaoStatistics((String) results[6]);
        } else
            throw new Error(getClass() + " - Incorrect Number of results retrieved");

    }

    public void setDescription(java.lang.String newDescription) {
        description = newDescription;
    }

    public void setDisableFlag(java.lang.Character newDisableFlag) {
        disableFlag = newDisableFlag;
    }

    public void setPaObjectID(java.lang.Integer newPaObjectID) {
        paObjectID = newPaObjectID;
    }

    public void setPaoName(java.lang.String newPaoName) {
        paoName = newPaoName;
    }

    public void setPaoStatistics(java.lang.String newPaoStatistics) {
        paoStatistics = newPaoStatistics;
    }

    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }
    
    @Override
    public void update() throws java.sql.SQLException {

        Object setValues[] = { 
                paoType.getPaoCategory().getDbString(), 
                paoType.getPaoClass().getDbString(), 
                StringUtils.left(paoName, 60),
                paoType.getDbString(),
                description,
                disableFlag,
                paoStatistics
                };

        Object constraintValues[] = { paObjectID };

        update(TABLE_NAME,
               SETTER_COLUMNS,
               setValues,
               CONSTRAINT_COLUMNS,
               constraintValues);

        UserPageDao userPageDao = YukonSpringHook.getBean(UserPageDao.class);
        userPageDao.updatePagesForPao(getPaoIdentifier(), paoName);
    }
}
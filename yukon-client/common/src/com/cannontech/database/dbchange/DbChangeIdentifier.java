package com.cannontech.database.dbchange;

import com.cannontech.message.dispatch.message.DBChangeMsg;

public class DbChangeIdentifier {
    private int database;
    private int id;
    public DbChangeIdentifier(int database, int id) {
        super();
        this.database = database;
        this.id = id;
    }
    public static DbChangeIdentifier createIdentifier(DBChangeMsg dbChangeMessage) {
        return new DbChangeIdentifier(dbChangeMessage.getDatabase(), dbChangeMessage.getId());
    }
    public int getDatabase() {
        return database;
    }
    public int getId() {
        return id;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + database;
        result = prime * result + id;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DbChangeIdentifier other = (DbChangeIdentifier) obj;
        if (database != other.database)
            return false;
        if (id != other.id)
            return false;
        return true;
    };
    
    @Override
    public String toString() {
        return "DbChangeIdentifier [id=" + id + ",database=" + database + "]";
    }
}

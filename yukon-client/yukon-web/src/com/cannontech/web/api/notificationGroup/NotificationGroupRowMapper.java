import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.web.notificationGroup.NotificationGroup;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class NotificationGroupRowMapper implements RowMapperWithBaseQuery<NotificationGroup> {

    @Override
    public SqlFragmentSource getBaseQuery() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("select NotificationGroupID, GroupName, DisableFlag from NotificationGroup ");
        return retVal;
    }

    @Override
    public boolean needsWhere() {
        return true;
    }

    @Override
    public NotificationGroup mapRow(YukonResultSet rs) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }



    @Override
    public SqlFragmentSource getOrderBy() {
        // TODO Auto-generated method stub
        return null;
    }


}
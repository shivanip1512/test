#include <boost/test/unit_test.hpp>

#include "sql_util.h"

BOOST_AUTO_TEST_SUITE( test_sql_util )

BOOST_AUTO_TEST_CASE( test_getRfnAddressInDeviceGroupSql)
{
    BOOST_CHECK_EQUAL(
        Cti::Database::getRfnAddressInDeviceGroupSql(0),
        "SELECT"
            " COUNT(*)"
        " FROM"
            " DeviceGroupMember dgm"
                " JOIN DeviceGroup DG0"
                    " ON DG0.devicegroupid = dgm.devicegroupid"
                " JOIN RfnAddress r"
                    " ON r.DeviceId=dgm.YukonPaoId"
        " WHERE"
            " DG0.parentdevicegroupid IS NULL"
                " AND DG0.groupname = ' '"
                " AND r.Manufacturer = ?"
                " AND r.Model = ?"
                " AND r.SerialNumber = ?");

    BOOST_CHECK_EQUAL(
        Cti::Database::getRfnAddressInDeviceGroupSql(1),
        "SELECT"
            " COUNT(*)"
        " FROM"
            " DeviceGroupMember dgm"
                " JOIN DeviceGroup DG1"
                    " ON DG1.devicegroupid = dgm.devicegroupid"
                " JOIN DeviceGroup DG0"
                    " ON DG0.devicegroupid = DG1.parentdevicegroupid"
                " JOIN RfnAddress r"
                    " ON r.DeviceId=dgm.YukonPaoId"
        " WHERE"
            " DG0.parentdevicegroupid IS NULL"
                " AND DG0.groupname = ' '"
                " AND DG1.groupname = ?"
                " AND r.Manufacturer = ?"
                " AND r.Model = ?"
                " AND r.SerialNumber = ?");

    BOOST_CHECK_EQUAL(
        Cti::Database::getRfnAddressInDeviceGroupSql(4),
        "SELECT"
            " COUNT(*)"
        " FROM"
            " DeviceGroupMember dgm"
                " JOIN DeviceGroup DG4"
                    " ON DG4.devicegroupid = dgm.devicegroupid"
                " JOIN DeviceGroup DG3"
                    " ON DG3.devicegroupid = DG4.parentdevicegroupid"
                " JOIN DeviceGroup DG2"
                    " ON DG2.devicegroupid = DG3.parentdevicegroupid"
                " JOIN DeviceGroup DG1"
                    " ON DG1.devicegroupid = DG2.parentdevicegroupid"
                " JOIN DeviceGroup DG0"
                    " ON DG0.devicegroupid = DG1.parentdevicegroupid"
                " JOIN RfnAddress r"
                    " ON r.DeviceId=dgm.YukonPaoId"
        " WHERE"
            " DG0.parentdevicegroupid IS NULL"
                " AND DG0.groupname = ' '"
                " AND DG1.groupname = ?"
                " AND DG2.groupname = ?"
                " AND DG3.groupname = ?"
                " AND DG4.groupname = ?"
                " AND r.Manufacturer = ?"
                " AND r.Model = ?"
                " AND r.SerialNumber = ?");
}

BOOST_AUTO_TEST_SUITE_END()

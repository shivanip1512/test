package com.cannontech.analysis.tablemodel;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import org.junit.Test;

import com.cannontech.common.util.SqlStatementBuilder;
import com.google.common.collect.Sets;

public class CapControlStateComparisonModelTest {

    @Test
    public void test_buildSQLStatement() throws Exception {
        
        CapControlStateComparisonModel model = new CapControlStateComparisonModel();
       
        SqlStatementBuilder builder;
        
        builder = model.buildSQLStatement();
        
        assertEquals("Query:  select ca.paoname region, yp3.paoName subName, yp2.paoName feederName, yp1.paoName capBankName, yp.paoName cbcName, s.text capBankStatus, elf.capbankstateinfo capBankState, s1.text cbcStatus, dcb.laststatuschangetime capBankChangeTime, dcb.twowaycbcstatetime cbcChangeTime from (select * from yukonpaobject where type in ( 'CBC 7020' , 'CBC 7022' , 'CBC 7023' , 'CBC 7024' , 'CBC 8020' , 'CBC 8024' , 'CBC DNP' , 'CBC Logical' ) ) yp left join capbank cb on cb.controldeviceid = yp.paobjectid and cb.controldeviceid > 0 join POINT p on p.PAObjectID = cb.deviceid and p.POINTOFFSET = 1 and p.POINTTYPE = 'Status' left join (select pointid, capbankstateinfo from CCEventLog el, (select MAX(logid) as el2Logid, pointid as el2PointId from CCEventLog where text like 'Var:%' group by pointid) el2 where el.logID = el2.el2Logid ) elf on ELF.pointid = p.pointid join (select * from yukonpaobject where type = 'CAP BANK' ) yp1 on yp1.paobjectid = cb.deviceid left outer join ccfeederbanklist fb on fb.deviceid = cb.deviceid left outer join (select * from yukonpaobject where type = 'CCFEEDER' ) yp2 on yp2.paobjectid = fb.feederid left outer join ccfeedersubassignment sf on fb.feederid = sf.feederid left outer join (select * from yukonpaobject where type = 'CCSUBBUS' ) yp3 on yp3.paobjectid = sf.substationbusid left outer join ccsubstationsubbuslist ss on sf.substationbusid = ss.substationbusid left outer join (select * from yukonpaobject where type = 'CCSUBSTATION' ) yp4 on yp4.paobjectid = ss.substationid join dynamiccccapbank dcb on dcb.capbankid = cb.deviceid join state s on s.stategroupid = 3 and dcb.controlstatus = s.rawstate left outer join state s1 on s1.stategroupid = 3 and dcb.twowaycbcstate = s1.rawstate left outer join ccsubstationsubbuslist ssb on ssb.substationbusid = sf.substationbusid left outer join ccsubareaassignment saa on saa.substationbusid = ssb.substationid left outer join (select paobjectid, paoname from yukonpaobject where type = 'CCAREA' ) ca on ca.paobjectid = saa.areaid  Arguments: []", 
            builder.getDebugSql());
        
        model.setShowMisMatch(true);

        builder = model.buildSQLStatement();
        
        assertEquals("Query:  select ca.paoname region, yp3.paoName subName, yp2.paoName feederName, yp1.paoName capBankName, yp.paoName cbcName, s.text capBankStatus, elf.capbankstateinfo capBankState, s1.text cbcStatus, dcb.laststatuschangetime capBankChangeTime, dcb.twowaycbcstatetime cbcChangeTime from (select * from yukonpaobject where type in ( 'CBC 7020' , 'CBC 7022' , 'CBC 7023' , 'CBC 7024' , 'CBC 8020' , 'CBC 8024' , 'CBC DNP' , 'CBC Logical' ) ) yp left join capbank cb on cb.controldeviceid = yp.paobjectid and cb.controldeviceid > 0 join POINT p on p.PAObjectID = cb.deviceid and p.POINTOFFSET = 1 and p.POINTTYPE = 'Status' left join (select pointid, capbankstateinfo from CCEventLog el, (select MAX(logid) as el2Logid, pointid as el2PointId from CCEventLog where text like 'Var:%' group by pointid) el2 where el.logID = el2.el2Logid ) elf on ELF.pointid = p.pointid join (select * from yukonpaobject where type = 'CAP BANK' ) yp1 on yp1.paobjectid = cb.deviceid left outer join ccfeederbanklist fb on fb.deviceid = cb.deviceid left outer join (select * from yukonpaobject where type = 'CCFEEDER' ) yp2 on yp2.paobjectid = fb.feederid left outer join ccfeedersubassignment sf on fb.feederid = sf.feederid left outer join (select * from yukonpaobject where type = 'CCSUBBUS' ) yp3 on yp3.paobjectid = sf.substationbusid left outer join ccsubstationsubbuslist ss on sf.substationbusid = ss.substationbusid left outer join (select * from yukonpaobject where type = 'CCSUBSTATION' ) yp4 on yp4.paobjectid = ss.substationid join dynamiccccapbank dcb on dcb.capbankid = cb.deviceid join state s on s.stategroupid = 3 and dcb.controlstatus = s.rawstate join state s1 on s1.stategroupid = 3 and dcb.twowaycbcstate = s1.rawstate and s1.rawstate != s.rawstate left outer join ccsubstationsubbuslist ssb on ssb.substationbusid = sf.substationbusid left outer join ccsubareaassignment saa on saa.substationbusid = ssb.substationid left outer join (select paobjectid, paoname from yukonpaobject where type = 'CCAREA' ) ca on ca.paobjectid = saa.areaid  Arguments: []", 
                builder.getDebugSql());
    }

    TreeSet<Integer> getThree(int start) {
        return Sets.newTreeSet(IntStream.range(start, start + 3).boxed()::iterator);   
    }

    String getSqlForFilter(int start, BiConsumer<CapControlStateComparisonModel, Set<Integer>> filter) {
        CapControlStateComparisonModel model = new CapControlStateComparisonModel();
        
        filter.accept(model, getThree(start));

        SqlStatementBuilder builder = model.buildSQLStatement();
        
        return builder.getDebugSql();
    }
    
    @Test
    public void test_buildSQLStatement_filters() throws Exception {

        final String prefix = "Query:  select ca.paoname region, yp3.paoName subName, yp2.paoName feederName, yp1.paoName capBankName, yp.paoName cbcName, s.text capBankStatus, elf.capbankstateinfo capBankState, s1.text cbcStatus, dcb.laststatuschangetime capBankChangeTime, dcb.twowaycbcstatetime cbcChangeTime from (select * from yukonpaobject where type in ( 'CBC 7020' , 'CBC 7022' , 'CBC 7023' , 'CBC 7024' , 'CBC 8020' , 'CBC 8024' , 'CBC DNP' , 'CBC Logical' ) ) yp left join capbank cb on cb.controldeviceid = yp.paobjectid and cb.controldeviceid > 0 join POINT p on p.PAObjectID = cb.deviceid and p.POINTOFFSET = 1 and p.POINTTYPE = 'Status' left join (select pointid, capbankstateinfo from CCEventLog el, (select MAX(logid) as el2Logid, pointid as el2PointId from CCEventLog where text like 'Var:%' group by pointid) el2 where el.logID = el2.el2Logid ) elf on ELF.pointid = p.pointid join (select * from yukonpaobject where type = 'CAP BANK' ) yp1 on yp1.paobjectid = cb.deviceid left outer join ccfeederbanklist fb on fb.deviceid = cb.deviceid left outer join (select * from yukonpaobject where type = 'CCFEEDER' ) yp2 on yp2.paobjectid = fb.feederid left outer join ccfeedersubassignment sf on fb.feederid = sf.feederid left outer join (select * from yukonpaobject where type = 'CCSUBBUS' ) yp3 on yp3.paobjectid = sf.substationbusid left outer join ccsubstationsubbuslist ss on sf.substationbusid = ss.substationbusid left outer join (select * from yukonpaobject where type = 'CCSUBSTATION' ) yp4 on yp4.paobjectid = ss.substationid join dynamiccccapbank dcb on dcb.capbankid = cb.deviceid join state s on s.stategroupid = 3 and dcb.controlstatus = s.rawstate left outer join state s1 on s1.stategroupid = 3 and dcb.twowaycbcstate = s1.rawstate left outer join ccsubstationsubbuslist ssb on ssb.substationbusid = sf.substationbusid left outer join ccsubareaassignment saa on saa.substationbusid = ssb.substationid left outer join (select paobjectid, paoname from yukonpaobject where type = 'CCAREA' ) ca on ca.paobjectid = saa.areaid";
        
        assertEquals(prefix + " where ca.paobjectid in ( 17,18,19 )  Arguments: []", 
                getSqlForFilter(17, CapControlStateComparisonModel::setAreaIdsFilter));

        assertEquals(prefix + " where yp1.paobjectid in ( 23,24,25 )  Arguments: []", 
                getSqlForFilter(23, CapControlStateComparisonModel::setCapBankIdsFilter));
        
        assertEquals(prefix + " where yp2.paobjectid in ( 29,30,31 )  Arguments: []", 
                getSqlForFilter(29, CapControlStateComparisonModel::setFeederIdsFilter));
        
        assertEquals(prefix + "  Arguments: []", 
                getSqlForFilter(35, CapControlStateComparisonModel::setStrategyIdsFilter));

        assertEquals(prefix + " where yp3.paobjectid in ( 41,42,43 )  Arguments: []", 
                getSqlForFilter(41, CapControlStateComparisonModel::setSubbusIdsFilter));

        assertEquals(prefix + " where yp4.paobjectid in ( 47,48,49 )  Arguments: []", 
                getSqlForFilter(47, CapControlStateComparisonModel::setSubstationIdsFilter));
    }
}

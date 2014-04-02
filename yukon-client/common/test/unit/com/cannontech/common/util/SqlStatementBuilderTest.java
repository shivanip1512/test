package com.cannontech.common.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.cannontech.database.SqlParameterSink;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;


public class SqlStatementBuilderTest {
    
    private static enum SomeEnum {
        FOO,
        BAR
    }
    
    private static enum SomeDrsEnum implements DatabaseRepresentationSource {
        MR("man"),
        MS("woman"),
        ;
        private final String text;

        private SomeDrsEnum(String text) {
            this.text = text;
        }
        
        @Override
        public Object getDatabaseRepresentation() {
            return text;
        }
    }
    
    @Test
    public void testAll() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("basic append");
        sql.append("multiple", "arguments", 345);
        sql.appendList(ImmutableList.<Integer>of(10, 20));
        sql.appendList(ImmutableList.<String>of("one", "two"));
        sql.appendArgument("arg 1");
        sql.appendArgument(350);
        sql.appendArgumentList(ImmutableList.<Integer>of(30, 40));
        sql.appendArgumentList(ImmutableList.<String>of("three", "four"));
        sql.append("and so on");
        sql.contains("contains arg");
        sql.startsWith("stars with arg");
        sql.endsWith("ends with arg");
        sql.append("a").eq_k(732);
        sql.append("b").eq("B");
        sql.append("c").lt("C");
        sql.append("d").lte("D");
        sql.append("e").gt("E");
        sql.append("f").gte("F");
        sql.append("g").neq("G");
        sql.append("h").neq_k(744);
        sql.append("i").in(ImmutableList.<Integer>of(60,61,62));
        sql.append("j").in(ImmutableList.<Integer>of());
        
        SqlStatementBuilder sql2 = new SqlStatementBuilder();
        sql2.append("select new code here").appendArgument("arg in frag");
        sql.append("k").appendFragment(sql2);

        SqlStatementBuilder sql3 = new SqlStatementBuilder();
        sql3.append("select id from table where foo").eq(943);
        sql.append("l").in(sql3);
        
        sql.append("m").eq_k(SomeEnum.FOO);
        sql.append("n").eq_k(SomeEnum.BAR);
        sql.append("o").eq_k(SomeDrsEnum.MR);
        sql.append("p").eq_k(SomeDrsEnum.MS);
        
        sql.append("q").neq_k(SomeEnum.FOO);
        sql.append("r").neq_k(SomeEnum.BAR);
        sql.append("s").neq_k(SomeDrsEnum.MR);
        sql.append("t").neq_k(SomeDrsEnum.MS);
        
        sql.append("u").eq(SomeEnum.FOO);
        sql.append("v").neq(SomeDrsEnum.MS);
        
        sql.append("done");
        
        String sqlAsString = sql.getSql();
        String expected = 
            " basic append multiple arguments 345 10,20 one," +
            "two ? ? ?, ? ?, ? and so on like ? like ? like " +
            "? a = 732 b = ? c < ? d <= ? e > ? f >= ? g != " +
            "? h != 744 i in (?, ?, ? ) j in (null) k select" +
            " new code here ? l in (select id from table whe" +
            "re foo = ? ) m = 'FOO' n = 'BAR' o = 'man' p = " +
            "'woman' q != 'FOO' r != 'BAR' s != 'man' t != " +
            "'woman' u = ? v != ? done ";
        Assert.assertEquals(expected, sqlAsString);
        List<Object> argumentList = sql.getArgumentList();
        
        // this must match exactly, always
        Assert.assertEquals(StringUtils.countMatches(sqlAsString, "?"), argumentList.size());
        
        // note that some of the operators change the argument
        Builder<Object> builder = ImmutableList.builder();
        builder.add("arg 1");
        builder.add(350);
        builder.add(30);
        builder.add(40);
        builder.add("three");
        builder.add("four");
        builder.add("%contains arg%");
        builder.add("stars with arg%");
        builder.add("%ends with arg");
        builder.add("B");
        builder.add("C");
        builder.add("D");
        builder.add("E");
        builder.add("F");
        builder.add("G");
        builder.add(60);
        builder.add(61);
        builder.add(62);
        builder.add("arg in frag");
        builder.add(943);
        builder.add("FOO");
        builder.add("woman");
        Assert.assertEquals(builder.build(), argumentList);
    }
    
    @Test
    public void testInsert() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("start");
        SqlParameterSink p = sql.insertInto("TABLE1");
        p.addValue("a", "a");
        p.addValue("b", 2);
        sql.append("end");
        
        String expectedSql = " start INSERT INTO TABLE1 (a,b) VALUES (?,?) end ";
        Assert.assertEquals(expectedSql, sql.getSql());
        
        List<Object> expectedArguments = ImmutableList.<Object>of("a", 2);
        Assert.assertEquals(expectedArguments, sql.getArgumentList());
    }
    
    @Test
    public void testUpdate() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("start");
        SqlParameterSink p = sql.update("TABLE1");
        p.addValue("a", "a");
        p.addValue("b", 2);
        sql.append("end");
        
        String expectedSql = " start UPDATE TABLE1 SET a = ? , b = ? end ";
        Assert.assertEquals(expectedSql, sql.getSql());
        
        List<Object> expectedArguments = ImmutableList.<Object>of("a", 2);
        Assert.assertEquals(expectedArguments, sql.getArgumentList());
    }
}

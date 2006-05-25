<%@ page import="java.lang.management.MemoryMXBean" %>
<%@ page import="java.lang.management.GarbageCollectorMXBean" %>
<%@ page import="java.lang.management.MemoryPoolMXBean" %>
<%@ page import="java.lang.management.ManagementFactory" %>
<html>
<body>
JAVA RUNTIME MEMORY DUMP<BR>
<U>Total Memory: </U><%=Runtime.getRuntime().totalMemory()%> bytes (<%=Runtime.getRuntime().totalMemory()/1024%> KB)<BR>
<U>Free Memory: </U><%=Runtime.getRuntime().freeMemory()%> bytes (<%=Runtime.getRuntime().freeMemory()/1024%> KB)<BR>
<U>Max Memory: </U><%=Runtime.getRuntime().maxMemory()%> bytes (<%=Runtime.getRuntime().maxMemory()/1024%> KB)<BR>
<HR>
<%
    try
    {
      // Read MemoryMXBean
      MemoryMXBean memorymbean = ManagementFactory.getMemoryMXBean();
%>    <U>MEMORY MXBEAN FROM (NEW JDK 1.5)</U><BR>
      Heap Memory Usage: <%=memorymbean.getHeapMemoryUsage()%><BR>
      Non-Heap Memory Usage: <%= memorymbean.getNonHeapMemoryUsage()%><BR>
      <HR>

<%      // Read Garbage Collection information
      java.util.List<GarbageCollectorMXBean> gcmbeans = ManagementFactory.getGarbageCollectorMXBeans();
      for( GarbageCollectorMXBean gcmbean : gcmbeans )
      {%><BR>
        <U>Name: <%=gcmbean.getName()%></U><BR>
        &nbsp;&nbsp;Collection count: <%=gcmbean.getCollectionCount()%><BR>
        &nbsp;&nbsp;Collection time: <%= gcmbean.getCollectionTime()%><BR><BR>
        &nbsp;&nbsp;MEMORY POOLS:<BR>
<%        String[] memoryPoolNames = gcmbean.getMemoryPoolNames();
        for( int i=0; i<memoryPoolNames.length; i++ )
        {%>
          &nbsp;&nbsp;&nbsp;&nbsp;<%=memoryPoolNames[ i ]%>,
<%        }
      }

      // Read Memory Pool Information
%>
      <HR><BR><U>MEMORY POOLS INFO</U>
<%      java.util.List<MemoryPoolMXBean> mempoolsmbeans = ManagementFactory.getMemoryPoolMXBeans();
      for( MemoryPoolMXBean mempoolmbean : mempoolsmbeans )
      {%><BR><BR>
        <U>Name: <%=mempoolmbean.getName()%></U><BR>
        &nbsp;&nbsp;Usage: <%=mempoolmbean.getUsage()%><BR>
        &nbsp;&nbsp;Collection Usage: <%=mempoolmbean.getCollectionUsage()%><BR>
        &nbsp;&nbsp;Peak Usage: <%=mempoolmbean.getPeakUsage()%><BR>
        &nbsp;&nbsp;Type: <%=mempoolmbean.getType()%><BR><BR>
        &nbsp;&nbsp;Memory Manager Names: <BR>
<%        String[] memManagerNames = mempoolmbean.getMemoryManagerNames();
        for( int i=0; i<memManagerNames.length; i++ )
        {%>
          &nbsp;&nbsp;&nbsp;&nbsp;<%= memManagerNames[ i ]%>,
<%        }
      }
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
%>
</body>
</html>
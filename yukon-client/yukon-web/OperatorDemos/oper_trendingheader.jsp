<%                	     
// Grab their graphdefinitions
   Class[] types = { Integer.class,String.class };
   Object[][] gData = com.cannontech.util.ServletUtil.executeSQL( dbAlias, "select graphdefinition.graphdefinitionid,graphdefinition.name from graphdefinition,OperatorLoginGraphList where graphdefinition.graphdefinitionid=OperatorLoginGraphList.graphdefinitionid and OperatorLoginGraphList.OperatorLoginID=" + operator.getOperatorLogin().getOperatorLoginID() + " order by graphdefinition.graphdefinitionid", types );
%>
<jsp:setProperty name="graphBean" property="startStr" param="start"/>
<jsp:setProperty name="graphBean" property="tab" param="tab"/>
<jsp:setProperty name="graphBean" property="period" param="period"/>
<jsp:setProperty name="graphBean" property="gdefid" param="gdefid"/>
<jsp:setProperty name="graphBean" property="viewType" param="view"/>
<jsp:setProperty name="graphBean" property="option" param="option"/>

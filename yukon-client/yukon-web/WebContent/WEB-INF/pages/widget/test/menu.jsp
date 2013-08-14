<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<cti:standardPage module="blank" title="Widget Test">
<cti:standardMenu/>

<h2>Please select a widget</h2>

<c:forEach items="${widgets}" var="widget">
<form action="create" method="get">
<div style="margin-bottom: 8px">
<ct:boxContainer title="${widget.title}">
<div style="margin-left: 15px; margin-bottom: 15px">

  <table>
  <c:forEach items="${widget.inputs}" var="input">
  
      <tr>
        <td>${input.description}</td>
        <td><input type="text" name="${input.name}"></td>
      </tr>
  </c:forEach>
  </table>
  <input type="hidden" name="type" value="${widget.shortName}">
  <input type="submit" name="create" value="Show Widget">
  </div>
</ct:boxContainer>
</div>
</form>
</c:forEach>

</cti:standardPage>
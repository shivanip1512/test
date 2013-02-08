<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<c:url var="DirUpImg" value="/WebConfig/yukon/Icons/arrow_turn_left.png"/>

<cti:standardPage module="support" page="logMenu">

<tags:layoutHeadingSuffixPart>
	<c:if test="${isNotLogRoot}">
		<a href="?file=${rootlessParentDir}&sortType=${oldStateSort}"><img src="${DirUpImg}" /></a>
	</c:if>
</tags:layoutHeadingSuffixPart>
	
<h4><i:inline key=".sortBy"/> <a href="?file=${file}&sortType=alphabetic" id="alphabetic" name="alphabetic"><i:inline key=".sortBy.alphabetic"/></a> <i:inline key=".sortBy.or"/> <a href="?file=${file}&sortType=date" id="date" name="alphabetic"><i:inline key=".sortBy.date"/></a></h4>

<!-- Display and link to the local log files -->
<h3 class="indentedElementHeading"><i:inline key=".directories"/></h3>
<div>
<c:forEach var="dirName" items="${dirList}">
	<a href="?file=${file}${dirName}/&sortType=${oldStateSort}">${dirName}/</a><br/>
</c:forEach>
</div>

<!-- Display and link to the local log files -->
<c:forEach var="logSection" items="${localLogList}">
	<h3 class="indentedElementHeading">${logSection.key}</h3>
    <div>
	<c:forEach var="fileName" items="${logSection.value}">
		<a href="tail?file=${file}${fileName}" >${fileName}</a><br/>
	</c:forEach>
	</div>
</c:forEach>

</cti:standardPage>
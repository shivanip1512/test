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

<div class="stacked clearfix">
    <div class="fl">
        <h3><i:inline key=".directories"/></h3>
        <ul>
            <c:forEach var="dirName" items="${dirList}">
                <li style="margin-left: 20px;"><a style="float: none;" class="labeled_icon icon_folder" href="?file=${file}${dirName}/&sortType=${oldStateSort}">${dirName}/</a></li>
            </c:forEach>
        </ul>
    </div>
    <h3 class="fl" style="margin-left: 40px;">
        <i:inline key=".sortBy"/>&nbsp;
        <a href="?file=${file}&sortType=alphabetic" id="alphabetic" name="alphabetic"><i:inline key=".alphabetic"/></a>
        &nbsp;<i:inline key="yukon.web.defaults.or"/>&nbsp;
        <a href="?file=${file}&sortType=date" id="date" name="alphabetic"><i:inline key="yukon.web.defaults.date"/></a>
    </h3>
</div>

<!-- Display and link to the local log files -->
<cti:dataGrid cols="4" tableClasses="stacked clear">
    <c:forEach var="logSection" items="${localLogList}">
        <cti:dataGridCell>
            <tags:sectionContainer title="${logSection.key}">
                <ul>
                    <c:forEach var="fileName" items="${logSection.value}">
                        <cti:url value="tail" var="url"><cti:param name="file" value="${file}${fileName}"></cti:param></cti:url>
                        <li><a href="${url}">${fileName}</a></li>
                    </c:forEach>
                </ul>
            </tags:sectionContainer>
        </cti:dataGridCell>
    </c:forEach>
    
</cti:dataGrid>
</cti:standardPage>
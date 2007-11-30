<%@ attribute name="title" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%--Note this code is duplicated in TitledContainerTag.java --%>

<div class="titledContainer roundedContainer ${styleClass}" <c:if test="${!empty id}" >id="${id}"</c:if>>
    <span class="top">
    <span class="t1"></span>
    <span class="t2"></span>
    <span class="t3"></span>
    <span class="t4"></span>
    <span class="t5"></span>
    <span class="t6"></span>
    </span>
    <div class="titleBar roundedContainer_titleBar">
    <div class="title roundedContainer_title">${title}</div>
    </div>
    <div class="content roundedContainer_content">

    <jsp:doBody/>

    <br class="makesNoSense">
    </div>
    
    <div class="lbottom">
    <span class="l5"></span>
    <span class="l4"></span>
    <span class="l3"></span>
    <span class="l2"></span>
    <span class="l1"></span>
    </div>
    <div class="rbottom">
    <span class="r5"></span>
    <span class="r4"></span>
    <span class="r3"></span>
    <span class="r2"></span>
    <span class="r1"></span>
    </div>
    <div class="bottomBar"></div>
    </div>
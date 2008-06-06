<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="onClose" required="true" type="java.lang.String"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="${id}" class="popUpDiv simplePopup ${styleClass}" style="display:none;">
<!--  fix for IE6 bug (see itemPicker.css for more info) -->
<!--[if lte IE 6.5]><iframe></iframe><![endif]-->
<div class="titledContainer boxContainer ${styleClass}">

    <div class="titleBar boxContainer_titleBar">
              <div class="controls" onclick="${onClose}">
                <img class="minMax" alt="close" src="/WebConfig/yukon/Icons/close_x.gif">
              </div>
        <div class="title boxContainer_title">
            ${title}
        </div>
    </div>
    
    <div class="content boxContainer_content">
        <jsp:doBody/>
    </div>    
                
</div>
</div>

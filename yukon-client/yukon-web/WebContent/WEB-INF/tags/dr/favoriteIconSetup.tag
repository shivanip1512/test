<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/drFavoriteFuncs.js"/>

<script type="text/javascript">
var addFavoriteUrl = '<cti:url value="/dr/addFavorite"/>';
var removeFavoriteUrl = '<cti:url value="/dr/removeFavorite"/>';
</script>

<a id="removeFavoriteTemplate" href="javascript:void(0)" class="simpleLink" style="display: none">
    <cti:logo key="yukon.web.modules.dr.actions.removeFavoriteIcon"/>
</a>

<a id="addFavoriteTemplate" href="javascript:void(0)" class="simpleLink" style="display: none">
    <cti:logo key="yukon.web.modules.dr.actions.addFavoriteIcon"/>
</a>

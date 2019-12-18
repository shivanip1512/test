<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@ attribute name="containerCss" required="true" type="java.lang.String" %>
<%@ attribute name="isDisabled" required="true" type="java.lang.Boolean" %>

<span class="js-value" style="margin-left: 18px;">9</span>
<span class="js-value" style="margin-left: 13px;">10</span>
<span class="js-value" style="margin-left: 13px;">11</span>
<span class="js-value" style="margin-left: 12px;">12</span>
<span class="js-value" style="margin-left: 15px;">13</span>
<span class="js-value" style="margin-left: 11px;">14</span>
<span class="js-value" style="margin-left: 14px;">15</span>
<span class="js-value" style="margin-left: 12px;">16</span>
<span class="js-value" style="margin-left: 13px;">17</span>
<span class="js-value" style="margin-left: 12px;">18</span>
<span class="js-value" style="margin-left: 12px;">19</span>
<span class="js-value" style="margin-left: 14px;">20</span>
<span class="js-value" style="margin-left: 13px;">21</span>
<span class="js-value" style="margin-left: 12px;">22</span>
<span class="js-value" style="margin-left: 13px;">23</span>
<span class="js-value" style="margin-left: 12px;">24</span>
<span class="js-value" style="margin-left: 11px;">25</span>
<br>
<div class="js-control-value ${containerCss}">
    <div class="button-group">
        <c:forEach begin="0" end="32" step="2" varStatus="status">
            <tags:check buttonStyle="width: 32px;" value="0" disabled="${isDisabled}" forceDisplayCheckbox="true" id="${containerCss}-chkbx_${status.index}" />
        </c:forEach>
    </div>
    <br>
    <div class="button-group MT5">
        <c:forEach begin="1" end="33" step="2" varStatus="status">
            <tags:check buttonStyle="width: 32px;" value="0" disabled="${isDisabled}" forceDisplayCheckbox="true" id="${containerCss}-chkbx_${status.index}"/>
        </c:forEach>
    </div>
</div>

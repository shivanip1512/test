<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="support" page="webServices.eimTest">
<cti:url var="sampleXmlUrl" value="sampleXml"/>

<script type="text/javascript">
function formatXml(unformattedXml) {
    var formatted = '';
    var reg = /(>)(<)(\/*)/g;
    unformattedXml = unformattedXml.replace(reg, '$1\n$2$3');
    var pad = 0;
    jQuery.each(unformattedXml.split('\n'), function(index, node) {
        var indent = 0;
        if (node.match( /.+<\/\w[^>]*>$/ )) {
            indent = 0;
        } else if (node.match( /^<\/\w/ )) {
            if (pad != 0) {
                pad -= 1;
            }
        } else if (node.match( /^<\w[^>]*[^\/]>.*$/ )) {
            indent = 1;
        } else {
            indent = 0;
        }

        var padding = '';
        for (var i = 0; i < pad; i++) {
            padding += '  ';
        }

        formatted += padding + node + '\n';
        pad += indent;
    });
    return formatted;
}

jQuery(function() {
    jQuery('#resetUsername').click(function() {
        jQuery('input[name=username]').val('${username}');
    });

    var uriSelect = jQuery('#uriSelect');
    var uriInput = jQuery('#uriInput');
    uriSelect.change(function() {
        if (uriSelect.val() == 'other') {
            uriInput.show();
            uriInput.focus();
            uriInput.select();
        } else {
            uriInput.hide();
        }
    });

    var sampleFileSelect = jQuery('#sampleFileSelect');
    sampleFileSelect.change(function() {
        jQuery.ajax({
            'url' : '${sampleXmlUrl}',
            'dataType' : 'json',
            'data' : {'sampleId' : sampleFileSelect.val()},
            'success' : function(sampleXml) {
                jQuery('#xmlRequest').val(sampleXml)
            }
        });
    });

    jQuery('#requestForm').ajaxForm({
        'dataType' : 'json',
        'method' : 'POST',
        'beforeSerialize' : function() {
            jQuery('#uriHidden').val((uriSelect.val() == 'other' ? uriInput : uriSelect).val());
        },
        'success' : function(responseXml) {
            Yukon.ui.unblockPage();
            var dest = jQuery('#eimResponseDiv').empty();
            if (jQuery('#formatResponse')[0].checked) {
                dest.append(jQuery('<pre/>', {'text' : formatXml(responseXml)}));
            } else {
                dest.text(responseXml)
            }
        }
    });

    jQuery('#selectAll').click(function() {
        jQuery('#eimResponseDiv').selectText();
    });
});
</script>

<tags:sectionContainer2 nameKey="request">
    <cti:url var="executeRequestUrl" value="executeRequest"/>
    <form id="requestForm" action="${executeRequestUrl}">
        <div id="requestArea">
            <select id="sampleFileSelect">
                <option><cti:msg2 key=".sampleRequests"/></option>
                <c:forEach var="category" items="${categories}">
                    <optgroup label="${category}">
                        <c:forEach var="sampleFile" items="${samplesByCategory[category]}">
                            <option value="${sampleFile.id}">${sampleFile.niceName}</option>
                        </c:forEach>
                    </optgroup>
                </c:forEach>
            </select>
            <textarea id="xmlRequest" name="xmlRequest"></textarea>
        </div>

        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".uri">
                <select id="uriSelect">
                    <c:forEach var="uri" items="${uris}">
                        <option value="${uri}">${uri}</option>
                    </c:forEach>
                    <option value="other"><cti:msg2 key=".otherUri"/></option>
                </select>
                <input style="display: none" type="text" id="uriInput" value="${uris[0]}">
                <input type="hidden" id="uriHidden" name="uri">
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".username">
                <input type="text" name="username" value="${username}">
                <button id="resetUsername"><cti:msg2 key=".resetUsername"/></button>
            </tags:nameValue2>
        </tags:nameValueContainer2>

        <div class="pageActionArea">
            <button class="f_blocker" type="submit"><cti:msg2 key=".submitRequest"/></button>
            <label>
                <input id="formatResponse" type="checkbox" checked="checked">
                <i:inline key=".formatResponse"/>
            </label>
        </div>
    </form>
</tags:sectionContainer2>

<tags:sectionContainer2 nameKey="response">
    <div class="code pr">
        <div id="selectAllDiv"><a href="javascript:void(0)" id="selectAll">Select All</a></div>
        <div id="eimResponseDiv"><i:inline key=".noRequestMade"/></div>
    </div>
</tags:sectionContainer2>

</cti:standardPage>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="rfnTest">

    <tags:sectionContainer title="Point Mapping ICD">
        <h2>
            <a data-show-hide="#models">Toggle</a> Models
        </h2>
        <table id="models" class="compact-results-table">
                <tr>
                    <th>Meter class</th>
                    <th>ICD Model string</th>
                    <th>RfnManufacturerModel</th>
                </tr>
            <c:forEach var="models" items="${meterModels}">
                <c:forEach var="meterClass" items="${models.value}">
                    <c:forEach var="model" items="${meterClass.value}">
                        <tr>
                            <td>${models.key.displayName} ${meterClass.key.displayName}</td>
                            <td>${model.original}</td>
                            <td>${model.manufacturerModel}</td>
                        </tr>
                    </c:forEach>
                </c:forEach>
            </c:forEach>
        </table>

        <h2>
            <a data-show-hide="#units">Toggle</a> Units
        </h2>
        <table id="units" class="compact-results-table">
            <thead>
                <tr>
                    <th>Common name</th>
                    <th>Description</th>
                    <th>Yukon UOM</th>
                    <th class="tar">Multiplier</th>
                </tr>
            </thead>
            <c:forEach var="unit" items="${units}">
                <tr>
                    <td>${unit.key.commonName}</td>
                    <td>${unit.value}</td>
                    <td>${unit.key.yukonUom}</td>
                    <td class="tar">${unit.key.multiplier}</td>
                </tr>
            </c:forEach>
        </table>

        <h2>
            <a data-show-hide="#metrics">Toggle</a> Metrics
        </h2>
        <table id="metrics" class="compact-results-table">
            <c:forEach var="metric" items="${metrics}" varStatus="rows">
                <c:if test="${(rows.count - 1) % 20 == 0}"><tr>
                    <th class="tar">ID</th>
                    <th>Description</th>
                    <th>Unit</th>
                    <th>Modifiers</th>
                </tr></c:if>
                <tr>
                    <td class="tar">${metric.key}</td>
                    <td>${metric.value.name}</td>
                    <td>${metric.value.unit.commonName}</td>
                    <td>
                        <c:forEach var="modifier" items="${metric.value.modifiers}" varStatus="loop">
                            ${modifier.commonName}${loop.last ? "" : ","}
                        </c:forEach>
                    </td>
                </tr>
            </c:forEach>
        </table>

        <h2>
            <a data-show-hide="#centronxPoints">Toggle</a> Centronx
        </h2>
        <table id="centronxPoints" class="compact-results-table">
            <c:forEach var="point" items="${centronxPoints}" varStatus="rows">
                <c:if test="${(rows.count - 1) % 20 == 0}"><tr>
                    <th>Name</th>
                    <th class="tar">Metric</th>
                    <th>Unit</th>
                    <th>Modifiers</th>
                    <th>Models</th>
                </tr></c:if>
                <tr>
                    <td>${point.name}</td>
                    <td class="tar">${point.metric}</td>
                    <td/>
                    <td/>
                    <td><c:forEach var="model" items="${point.models}" varStatus="loop">
                        ${model}${loop.last ? "" : ","}
                    </c:forEach></td>
                </tr>
            </c:forEach>
        </table>

        <c:forEach var="point" items="${points}">
            <h2>
                <a data-show-hide="#${point.id}">Toggle</a>
                ${point.title}
            </h2>
            <pre id="${point.id}">${point.content}</pre>
        </c:forEach>

        <h2><a data-show-hide="#rfnPointMapping">Toggle</a> rfnPointMapping</h2>
       <pre id="rfnPointMapping" class="dn">${rfnPointMapping}</pre> 
       
       <h2><a data-show-hide="#icdjson">Toggle</a> ICD JSON</h2>
       <pre id="icdjson" class="dn">${icd}</pre>

       <h2><a data-show-hide="#pointTable">Toggle</a> Point table</h2>
       <table id="pointTable" class="results-table" style="width:900px">
           <thead>
               <tr>
                <th>Point</th>
                <th>RfnPointMapping</th>
                <th>YukonPointMappingIcd</th>
               </tr>
           </thead>
           <tfoot></tfoot>
           <tbody>
               <c:forEach var="pointRow" items="${pointTable}">
               <tr>
                   <td>${pointRow.point}</td>
                   <td>${pointRow.rfnPointMapping}</td>
                   <td>${pointRow.yukonPointMappingIcd}</td>
               </tr>
               </c:forEach>
           </tbody>
       </table>
    </tags:sectionContainer>

</cti:standardPage>
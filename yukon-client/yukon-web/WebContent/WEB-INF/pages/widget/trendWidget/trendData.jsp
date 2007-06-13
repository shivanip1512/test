<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script type="text/javascript" src="/JavaScript/prototype.js" ></script>

<chart>
	<xaxis>
		<c:forEach var="xValue" items="${xAxisValues}" >
			<value xid="${xValue.id}">${xValue.formattedValue}</value>
		</c:forEach>		
	</xaxis>
	<graphs>
	
		<c:forEach var="graph" items="${graphList}" >
	
			<graph 
				title="${graph.seriesTitle}" 					
				axis="${graph.YAxis}"								
	      		unit=""									
	      		unit_position="left"					
	      		color=""							
	      		color_hover=""							
	      		line_alpha=""							
	      		line_width=""							
	      		fill_alpha=""							
	      		balloon_color=""						
	      		balloon_text_color=""					
	      		bullet="round"							
	      		bullet_size="5"							
	      		bullet_color=""							
	      		hidden=""								
	      		selected=""								
			>
			
				<c:forEach var="dataValue" items="${graph.chartData}" >
					<value xid="${dataValue.id}" description="${dataValue.description}">${dataValue.formattedValue}</value>
				</c:forEach>		
				                                                            
			</graph>
			
		</c:forEach>
	</graphs>
</chart>

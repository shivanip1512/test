<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<chart>
	<graphs>
		<c:forEach var="graph" items="${graph.lines}">
			<graph gid="${graph.id}">
				<c:forEach var="point" items="${graph.points}">
					<point x="${point.x}" y="${point.y}"/>
				</c:forEach>
			</graph>
		</c:forEach>
		<graph gid="-10">
			<point x="0" y="140"/>
		</graph>
		<graph gid="-11">
			<point x="0" y="100"/>
		</graph>
	</graphs>
</chart>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<script src="<c:url value='/resource/js/deleteCommunity.js'/>"></script>

<div style="text-align: center;">
	<h4>
		<spring:message code="label.community.pagename" />
	</h4>
</div>
<p>
	<a href="<c:url value='/administrator/users/addCommunity'/>"
		class="btn btn-success" role="button"><spring:message
			code="label.community.add"/></a>
</p>
<table class="table table-striped table-bordered table-hover">

	<thead>
		<tr>
			<th style="width: 80%;"><spring:message code="label.community.name" /></th>
			<th style="text-align: center;"><spring:message code="label.restype.actions" /></th>
		</tr>
	</thead>

	<tbody>
		<c:if test="${not empty listOfTerritorialCommunity}">
			<c:forEach items="${listOfTerritorialCommunity}" var="commun">
				<tr>
					<td>${commun.name}</td>
					<td>
						 <div class="wrapper" style="text-align: center;">
							<a href="deleteCommunity/${commun.territorialCommunityId}" class="btn btn-danger"
								id="deleterecommunity" role="button"><spring:message code="label.community.delete" /></a>
						</div>
					</td>
				</tr>
			</c:forEach>
		</c:if>
	</tbody>
</table>
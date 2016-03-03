<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<h2>
	<spring:message code="label.community.add" />
</h2>

<form:form method="POST" action="addCommunity"
	modelAttribute="newCommunity" class="form-horizontal">

	<form:errors path="name" cssClass="error" style="color:red" />
	<div class="form-group">
		<label class="control-label col-sm-2"><spring:message
				code="label.community.title" />:</label>
		<div class="col-sm-2">
			<input class="form-control" name="name" value="${name}"
				placeholder=<spring:message
                    code="label.community.enterName" />
				required>
		</div>
	</div>
	<br />
	<div class="button">
		<input type="submit" value=<spring:message code="label.save"/>
			class="btn btn-success" />
		<button type="reset" class="btn btn-default">
			<spring:message code="label.clearall" />
		</button>
	</div>

</form:form>
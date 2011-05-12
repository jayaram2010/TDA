<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<tr>
	<td>&nbsp;</td>
	<td>
		<form:label for="places" path="itineraryForm.places[${placeNumber }].city" cssErrorClass="error">Ciudad</form:label>
	</td>
	<td>
		<form:input path="itineraryForm.places[${placeNumber }].city" size="30"/>
	</td>
	<td>&nbsp;</td>
</tr>
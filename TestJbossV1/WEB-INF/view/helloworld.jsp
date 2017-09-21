<%--
Copyright 2004 The Apache Software Foundation
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="jp.sf.pal.helloworld.resources.HelloWorldResources" />
<portlet:defineObjects/>

<form action="<portlet:actionURL />" method="POST">
<table border="0">
	<tr>
		<td align="center"><fmt:message key="helloworld.lable.Hello"/></td>
		<td align="center"><%= request.getAttribute("yourName") %></td>
	</tr>
	<tr>
		<td align="right"><fmt:message key="helloworld.lable.YourName"/></td>
		<td align="left"><input type="text" name="yourName"/></td>
	</tr>
	<tr>
		<td align="center" colspan="2"><input type="submit" value="<fmt:message key="helloworld.lable.Submit"/>"/></td>
	</tr>
</table>
</form>

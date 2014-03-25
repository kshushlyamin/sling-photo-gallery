<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page session="false"%>

<%@page import="org.apache.sling.api.resource.ResourceUtil,
                  org.apache.sling.api.resource.ValueMap,
                  org.apache.sling.api.request.ResponseUtil"%><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
    final ValueMap attributes = ResourceUtil.getValueMap(resource);
    // final String photoName = ResponseUtil.escapeXml(attributes.get("jcr:title", resource.getName()));
    // final String albumName = ResponseUtil.escapeXml(ResourceUtil.getValueMap(resource.getParent()).get("jcr:title", resource.getParent().getName()));
%>

<h1>Description: <%=attributes.get("description") %></h1>
<p/>

<img src="<%=resource.getPath() %>/thumbnails/picture_100" width="100px" />

<br/>


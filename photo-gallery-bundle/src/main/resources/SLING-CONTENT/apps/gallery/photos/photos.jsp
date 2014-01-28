<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page session="false" %>
<%
%>
<%@page import="org.apache.sling.api.resource.Resource,
                java.util.Iterator" %>
<%
%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%
%><sling:defineObjects/>
<div>
    <%
        int i = 0;
        final Iterator<Resource> fi = resource.listChildren();
        while (fi.hasNext()) {
            final Resource current = fi.next();
            if (current.isResourceType("gallery/photo")) {
    %>
    <sling:include resource="<%= current %>" resourceType="gallery/photo" replaceSelectors="scale"/> <!-- replaceSelectors="main" -->
    <%
            }
        }
    %>
</div>
<br/>



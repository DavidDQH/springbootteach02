<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:set var="ctx" value="${pageContext.request.contextPath}" ></c:set>
<script type="text/javascript">

</script>
<title>Insert title here</title>
</head>
<body>

    id: ${record.id}  <br/>
    orderNo: ${record.orderNo} <br/>
    orderType: ${record.orderType}

    <div class="row margin-top-20">
        <table class="table">
            <thead>
                <tr>
                    <th class="seq">附件名</th>
                    <th>大小</th>
                    <th>位置</th>
                </tr>
            </thead>
            <tbody>
            </tbody>
            <c:forEach var="appendix" items="${appendixList}" >
                <tr>
                    <td>${appendix.name}</td>
                    <td>${appendix.size}</td>
                    <td><a href="${appendix.realLocation}" target="_new window">${appendix.realLocation}</a></td>
                </tr>
            </c:forEach>
        </table>
    </div>

</body>
</html>
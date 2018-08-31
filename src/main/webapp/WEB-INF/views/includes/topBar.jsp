<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="page" value="${param.page}"/>

<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container">

        <a class="navbar-brand" style="background: url('${pageContext.request.contextPath}/img/logo.png') no-repeat;" href="#"></a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav ml-auto">
                <li class="nav-item <c:if test="${page=='ginnungagap'}">active</c:if>">
                    <a class="nav-link" href="${pageContext.request.contextPath}/ginnungagap">Ginnungagap</a>
                </li>
                <li class="nav-item <c:if test="${page=='preservation'}">active</c:if>">
                    <a class="nav-link" href="${pageContext.request.contextPath}/preservation">Preservation</a>
                </li>
                <li class="nav-item <c:if test="${page=='update'}">active</c:if>">
                    <a class="nav-link" href="${pageContext.request.contextPath}/update">Update</a>
                </li>
                <li class="nav-item <c:if test="${page=='validation'}">active</c:if>">
                    <a class="nav-link" href="${pageContext.request.contextPath}/validation">Validation</a>
                </li>
                <li class="nav-item <c:if test="${page=='import'}">active</c:if>">
                    <a class="nav-link" href="${pageContext.request.contextPath}/import">Import</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

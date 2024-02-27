<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<body>
								</div>
                                <%-- 
                                <div class="cont-tab cont-tab2">내 리포트</div>
                                <div class="cont-tab cont-tab3">비정형 분석</div>
                                <div class="cont-tab cont-tab4">새 대시보드</div>
                                --%>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- contents -->
		<div id="portal-loading"  class="portal-loading" style="display: none;">
			<img id="loading-image" class="loading-image" src="${pageContext.request.contextPath}/_custom/image/main/loading.gif" alt="Loading..." />
		</div>
		<iframe id="downloadTarget" name="downloadTarget" style="display:none;"></iframe>
	</div>
</body>
</html>
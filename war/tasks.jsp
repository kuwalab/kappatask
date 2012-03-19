<%@page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html>
 <head>
  <meta charset="utf-8">
  <link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/redmond/jquery-ui.css">
  <jsp:include page="style.jsp" flush="true" />
  <title>タスクの一覧</title>
 </head>
 <body>
選択したリストのタスクの一覧です<br>
  <c:if test="${not empty tasks}">
   <dl>
   <c:forEach items="${tasks.items}" var="task">
    <dt style="margin-left: ${(task.level - 1) * 16}px">${f:h(task.title)}</dt>
    <dd style="margin-left: ${(task.level - 1) * 16 + 8}px"><span class="due"><c:if test="${not empty task.dispDue}">${f:h(task.dispDue)}</c:if></span><input type="text" size="10" class="dueInput" value=""> <span class="edit">編集</span><span class="update"><input type="hidden" class="task" value="${f:h(task.id)}">更新</span></dd>
   </c:forEach>
   </dl>
  </c:if>
  <jsp:include page="script.jsp" flush="true" />
  <script type="text/javascript">
(function() {
	var due = $('.due'),
		dueInput = $('.dueInput'),
		edit = $('.edit'),
		update = $('.update');
	
	dueInput.datepicker({ dateFormat: 'yy/mm/dd' });

	edit.each(function(index) {
		$(this).click(function() {
			var currentDueInput = $(dueInput[index]),
				currentDue = $(due[index]),
				currentEdit = $(edit[index]),
				currentUpdate = $(update[index]);
			due.show();
			edit.show();
			dueInput.hide();
			update.hide();
			update.unbind('click');
			currentDue.hide();
			currentEdit.hide();
			currentDueInput.val(currentDue.text());
			currentDueInput.show();
			currentUpdate.show();
			currentUpdate.click(function() {
				$.ajax({
					type: 'GET',
					url: 'update',
					data: {
						access_token: '${f:h(access_token)}',
						tasklist: '${f:h(tasklist)}',
						task: $($('.task')[index]).val(),
						due: currentDueInput.val()
					},
					dataType: 'json',
					cache: false,
					success: function(data) {
						if (data.result == 'ok') {
							currentDue.text(currentDueInput.val());
							currentDueInput.hide();
							currentUpdate.unbind('click');
							currentUpdate.hide();
							currentDue.show();
							currentEdit.show();
						} else {
							alert('日付が正しくありません');
						}
					}
				});
			});
		});
	});
}())
  </script>
 </body>
</html>
  
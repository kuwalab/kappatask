<%@page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html>
 <head>
  <meta charset="utf-8">
  <link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/redmond/jquery-ui.css">
  <jsp:include page="style.jsp" flush="true" />
  <title>タスクの一覧です</title>
 </head>
 <body>
  <div id="main">
   <div id="tasklists">
    <c:if test="${not empty taskLists}">
     <ul>
      <c:forEach items="${taskLists.items}" var="taskList">
       <li>${f:h(taskList.title)}<input type="hidden" name="tasklist" value="${f:h(taskList.id)}"></li>
      </c:forEach>
     </ul>
    </c:if>
    <c:if test="${not empty taskLists}">
     <ul>
      <c:forEach items="${taskLists.items}" var="taskList">
       <li><a href="tasks?access_token=${f:h(access_token)}&id=${f:h(taskList.id)}">${f:h(taskList.title)}</a></li>
      </c:forEach>
     </ul>
    </c:if>
   </div>
   <div id="tasks">
    ←タスクリストを選択して下さい。
   </div>
  </div>
  <jsp:include page="script.jsp" flush="true" />
  <script type="text/javascript">
(function() {
	var tasklists = $('#tasklists ul li');
	tasklists.click(function() {
		var tasklist = $('[name="tasklist"]', $(this)).val(),
			tasks = $('#tasks');
		$.ajax({
			type: 'GET',
			url: 'tasks',
			data: {
				access_token: '${f:h(access_token)}',
				tasklist: tasklist
			},
			dataType: 'json',
			cache: false,
			success: function(data) {
				var edit = $('.edit');
				if (edit) {
					edit.unbind('click');
				}
				tasks.html(data.result);
				$('.option').append('<img src="image/edit.png" class="edit">');
				edit = $('.edit');
				edit.bind('click', function() {
					var o = $(this),
						parent = o.parent(),
						due = parent.find('.due');
					o.hide();
					due.hide();
					parent.append('<input type="text" class="dueDate" size="10" value="' + due.text() +'">' +
						'<input type="button" class="save" value="保存">' +
						'<input type="button" class="cancel" value="キャンセル">');
					parent.find('.dueDate').datepicker({
						dateFormat: 'yy/mm/dd',
						showOn: 'button',
						buttonImage: 'image/calendar.png',
						buttonImageOnly: true
					});
					parent.find('.cancel').bind('click', function() {
						var elem = parent.find('input');
						elem.unbind('click');
						parent.find('.dueDate').remove();
						parent.find('.save').remove();
						parent.find('.cancel').remove();
						parent.find('.ui-datepicker-trigger').remove();
						o.show();
						due.show();
					});
					parent.find('.save').bind('click', function() {
						var elem = parent.find('input');
						var dueDate = parent.find('.dueDate').val();
						elem.unbind('click');
						parent.find('.dueDate').remove();
						parent.find('.save').remove();
						parent.find('.cancel').remove();
						parent.find('.ui-datepicker-trigger').remove();
						o.show();
						due.show();
						$.ajax({
							type: 'GET',
							url: 'update',
							data: {
								access_token: '${f:h(access_token)}',
								tasklist: tasklist,
								task: parent.find('.task').val(),
								due: dueDate
							},
							dataType: 'json',
							cache: false,
							success: function(data) {
								if (data.result == 'ok') {
									console.log(dueDate);
									due.text(dueDate);
								} else {
									alert('日付が正しくありません');
								}
							}
						});
					});
				});
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				console.log(textStatus);
			}
		});
	});
}());
  </script>
 </body>
</html>
  
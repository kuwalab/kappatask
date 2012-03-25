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
    <div id="tasklistsHeader">タスクリスト</div>
    <div id="tasklistsMain">
     <c:if test="${not empty taskLists}">
      <ul>
       <c:forEach items="${taskLists.items}" var="taskList">
        <li>${f:h(taskList.title)}<input type="hidden" name="tasklist" value="${f:h(taskList.id)}"></li>
       </c:forEach>
      </ul>
     </c:if>
    </div>
   </div>
   <div id="tasks">
    <div id="tasksHeader">タスク</div>
    <div id="tasksMain"></div>
   </div>
  </div>
  <jsp:include page="script.jsp" flush="true" />
  <script type="text/javascript" charset="utf-8">
(function() {
	var height = $(window).height();
	$('#tasklistsMain').height((height - 50) + 'px');
	$('#tasksMain').height((height - 60) + 'px');
	
	TasklistsModel = (function() {
		var Constr = function(tasklists) {
			$(tasklists).bind('click', {tasklists: $(tasklists)}, this.onClickTasklists);
		};
	
		Constr.prototype = {
			onClickTasklists: function(event) {
				var tasklist = $(this).find('[name="tasklist"]').val();
				$('#tasksMain').html('<img src="image/wait.gif">')
				event.data.tasklists.removeAttr('class');
				$(this).attr('class', 'selected');
				
				$.ajax({
					type: 'GET',
					url: 'tasks',
					data: {
						access_token: '${f:h(access_token)}',
						tasklist: $(this).find('[name="tasklist"]').val()
					},
					dataType: 'json',
					cache: false,
					success: function(data) {
						// 編集領域を設定し、編集ボタンを付ける
						var tasksModel = new TasksModel('#tasksMain', data.result, tasklist);
					}
				});
			}
		};
		
		return Constr;
	}());

	TasksModel = (function() {
		var Constr = function(tasksSelector, html, tasklist) {
			var me = this,
				tasks = $(tasksSelector),
				edit;
			
			tasks.html(html);
			this.tasklist = tasklist;

			tasks.find('.option').append('<img src="image/edit.png" class="edit">');
			edit = tasks.find('.edit');

			if (edit) {
				edit.unbind('click');
			}
			edit.bind('click', {me: this}, this.onClickEdit);
		};
	
		Constr.prototype = {
			tasklist: null,
			onClickEdit: function(event) {
				var save, cancel,
					editButton = $(this),
					optionArea = editButton.parent(),
					due = optionArea.find('.due'),
					me = event.data.me;
				// 表示済みであれば、再度処理しない
				if (due.css('display') == 'none') {su
					return;
				}
				due.hide();
				optionArea.append('<input type="text" class="dueDate" size="10" value="' + due.text() +'">' +
					'<input type="button" class="save" value="保存">' +
					'<input type="button" class="cancel" value="キャンセル">');
				optionArea.find('.dueDate').datepicker({
					dateFormat: 'yy/mm/dd',
					showOn: 'button',
					buttonImage: 'image/calendar.png',
					buttonImageOnly: true	
				});
				
				save = optionArea.find('.save');
				cancel = optionArea.find('.cancel');
				save.bind('click', {me: me}, me.onClickSave);
				cancel.bind('click', me.onClickCancel);
			},
			onClickSave: function(event) {
				var saveButton = $(this);
				var optionArea = saveButton.parent();
				var elem = optionArea.find('input');
				var dueDate = optionArea.find('.dueDate').val();
				var due = optionArea.find('.due');
				elem.unbind('click');
				optionArea.find('.dueDate').remove();
				optionArea.find('.save').remove();
				optionArea.find('.cancel').remove();
				optionArea.find('.ui-datepicker-trigger').remove();
				optionArea.show();
				due.show();
				$.ajax({
					type: 'PUT',
					url: 'update',
					data: {
						access_token: '${f:h(access_token)}',
						tasklist: event.data.me.tasklist,
						task: optionArea.find('.task').val(),
						due: dueDate
					},
					dataType: 'json',
					cache: false,
					success: function(data) {
						if (data.result == 'ok') {
							due.text(dueDate);
						} else {
							alert('日付が正しくありません');
						}
					}
				});
			},
			onClickCancel: function() {
				var editButton = $(this),
					optionArea = editButton.parent(),
					due = optionArea.find('.due');
				optionArea.find('input').unbind('click');
				optionArea.find('.dueDate').remove();
				optionArea.find('.save').remove();
				optionArea.find('.cancel').remove();
				optionArea.find('.ui-datepicker-trigger').remove();
				optionArea.show();
				due.show();
			}
		};
		
		return Constr;
	}());

	var tasklistsModesl = new TasklistsModel('#tasklists ul li');
}());
  </script>
 </body>
</html>
  
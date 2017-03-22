$(document).ready(function(){
	$(".register").click(function(){
		var json = new Object();
		json.username = $('.username').val();
		json.email = $('.email').val();
		json.password = $('.password').val();
		var jsonStr = JSON.stringify(json);

		$.post("http://localhost:9000/user/register",
			jsonStr,
			function(data,status){
	        console.log("数据: \n" + JSON.stringify(data) + "\n状态: " + status);
			if(data.statuscode == 0){
				alert('注册成功！');
				// window.location.href = 'http://demo.cssmoban.com/cssthemes3/mstp_115_enonadmin/dashboard-1.html';
				
			}
			else{
				alert('注册失败！');				
			}
				
	    });
	});
	$(".login").click(function(){
		var json = new Object();
		json.username = $('.username').val();
		// loginJson.email = $('.email').val();
		json.password = $('.password').val();
		var jsonStr = JSON.stringify(json);

		$.post("http://localhost:9000/user/login",
	    jsonStr,
	    function(data,status){
			var secretKey=data.message;
			
	        console.log("数据: \n" + JSON.stringify(data) + "\n状态: " + status);
			if(data.statuscode == 0){
				sessionStorage.setItem('secretKey',secretKey);
				var str = $('.username').val();
				sessionStorage.setItem('username',str);
				alert('登陆成功！')
				window.location.href = "http://localhost:9000/home";
				
			}
				
			else	
				alert('登录失败！')
	    });
	});	
})


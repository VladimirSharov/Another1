window.onload = customize;

function customize(){
	window.document.getElementById('div3').style.display = 'none';
}

function convertNumber()
{
    window.document.getElementById('div3').style.display = 'none';
	var q_str = 'type=number&value='+document.getElementById('t1').value;
	doAjax('NC_convertor_servlet',q_str,'convertNumber_back','post',0);
}
function convertNumber_back(result)
{
	if (result.substring(0,5)=='error'){
	   window.document.getElementById('div3').style.display = 'block';
	   window.document.getElementById('div3').innerHTML="<p style='color:red;'><b>"+result.substring(6)+"</b></p>";
   }else{
	   window.document.getElementById('t2').value=""+result;
   }
}

function convertPrice()
{
    window.document.getElementById('div3').style.display = 'none';
	var q_str = 'type=price&value='+document.getElementById('t3').value;
	doAjax('NC_convertor_servlet',q_str,'convertPrice_back','post',0);
}
function convertPrice_back(result)
{
	if (result.substring(0,5)=='error'){
	   window.document.getElementById('div3').style.display = 'block';
	   window.document.getElementById('div3').innerHTML="<p style='color:red;'><b>"+result.substring(6)+"</b></p>";
   }else{
	   window.document.getElementById('t4').value=""+result;
   }
}

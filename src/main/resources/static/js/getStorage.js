	//새로고침이나 페이지이동을 했을 시 세션스토리지에 있는 값을 기준으로
	//현재 페이지에 있는 체크박스 체크하기
	let storage = sessionStorage.getItem("checked");
	let storageValueList = JSON.parse(storage);
	let element = document.getElementsByName("tree");
	if (storageValueList){
		for(let i=0; i<element.length; i++){
		    for(let j=0; j<storageValueList.length; j++){
		        if(element[i].id == storageValueList[j]){
		        	//체크박스의 value값과 id값을 같게해서 처리한다.
		            document.getElementById(element[i].id).checked = true;
		        }
		    }
		}
		
		//id가 count인 태그에 배열길이 넣기
		document.getElementById("count").innerHTML = storageValueList.length + "개 선택"
		
		//id가 checkList인 태그에 배열값들을 출력하기
		let checkList = "";
		for(let i=0; i<storageValueList.length; i++){
		    checkList += storageValueList[i] + "<br>";
		}
		document.getElementById("result").innerHTML = checkList
		
		//hidden에 value를 현재 세션스토리지 배열로 설정
	    let hiddenValueList = [];
	    for(let i=0; i<storageValueList.length; i++){
	    	let fastaCode = storageValueList[i].split("  ")[0];
	    	hiddenValueList.push(fastaCode);
	    }
	    document.getElementById("hidden").value = hiddenValueList
	}
	
function initCheckbox(){
	sessionStorage.clear();

    //전체 체크박스 해제
    const element = document.getElementsByName("tree")
    element.forEach((checkbox) => {checkbox.checked = false;})

    //html에 출력한 것들 전부 초기화
    document.getElementById("count").innerHTML = ""
    document.getElementById("result").innerHTML = ""
    document.getElementById("hidden").value = ""
}

function inputCheckboxValue(){
    //체크된 값들 배열에 넣기
    let element = document.getElementsByName("tree")
    let checkedItems = [];
    let uncheckedItems = [];
    for (let i=0; i<element.length; i++){
        if(element[i].checked){
        	//체크박스에서 체크된 값들
        	checkedItems.push(element[i].value);
        }
        else{
        	//체크박스에서 체크되지 않은 값들
        	uncheckedItems.push(element[i].value);
        }
    }

    //체크값 배열을 세션스토리지에 넣기
    //key값을 checked로 value를 checkedItem배열로 설정
    //세션스토리지값을 가져온다.
    let storage = sessionStorage.getItem("checked");
    
    if (storage == null){
    	//세션스토리지 값이 비어있다면 (헌마디로 체크박스에 아무것도 체크되지 않은 상태라면)
    	sessionStorage.setItem("checked",JSON.stringify(checkedItems));
    } else {
    	//체크박스에 체크된 것들이 있는 상태라면
    	let storageValueList = JSON.parse(storage);
    		
    	//storageValueList에서 uncheckedItems원소가 있다면  삭제한다.
    	//uncheckedItems이 있을때만 한다.
    	if (uncheckedItems.length != 0){
	    	for (let i=0; i<storageValueList.length; i++){
	    		for (let j=0; j<uncheckedItems.length; j++){
	    			if(storageValueList[i] == uncheckedItems[j]){
		    			//해당 원소 삭제
		    			storageValueList.splice(i,1);
		    			break;
		    		}
	    		}    		
	    	}
    	}
    	
    	//원래 스토리지 배열과 checkedItems를 합친후에 set를 이용하여 중복을 제거해준다.
    	//이후 중복이 제거된 배열로 새로운 세션스토리지를 만들어준다.
    	checkedItems = checkedItems.concat(storageValueList);
    	checkedItems = [...new Set(checkedItems)];
    	
    	//체크박스 개수제한
    	if(checkedItems.length == 16){
    		alert("15개까지만 체크가 가능합니다.");
    		location.reload();
    		return;
    	}
    	
    	sessionStorage.setItem("checked",JSON.stringify(checkedItems));
    }
    writeHTML();
}

function writeHTML(){
	//세션스토리지에 넣은 배열을 가져오기
    let storage = sessionStorage.getItem("checked");
    let storageValueList = JSON.parse(storage);

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
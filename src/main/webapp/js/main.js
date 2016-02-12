/*
 * COPYRIGHT LICENSE: This information contains sample code provided in source
 * code form. You may copy, modify, and distribute these sample programs in any 
 * form without payment to IBM for the purposes of developing, using, marketing 
 * or distributing application programs conforming to the application programming 
 * interface for the operating platform for which the sample code is written. 
 * 
 * Notwithstanding anything to the contrary, IBM PROVIDES THE SAMPLE SOURCE CODE 
 * ON AN "AS IS" BASIS AND IBM DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING, 
 * BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, 
 * SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY OR 
 * CONDITION OF NON-INFRINGEMENT. IBM SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR
 * OPERATION OF THE SAMPLE SOURCE CODE. IBM HAS NO OBLIGATION TO PROVIDE
 * MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE
 * SOURCE CODE.
 * 
 * (C) Copyright IBM Corp. 2015.
 * 
 * All Rights Reserved. Licensed Materials - Property of IBM.  
 */

function submitOwner() {
	//var formData = new FormData(document.querySelector("#trandata"));
	
	var args = [];
	
	args.push(createArgPair("ownername"));
	args.push(createArgPair("tranannowner", true));
	args.push(createArgPair("excannowner", true));
	args.push(createArgPair("rbrteowner", false, true));
	args.push(createArgPair("petname"));
	args.push(createArgPair("tranannpet", true));
	args.push(createArgPair("excannpet", true));
	args.push(createArgPair("rbrtepet", false, true));
	
	var request = new XMLHttpRequest();
	request.onreadystatechange = function() {
		if (request.readyState == 4 && request.status == 200) {
			getData();
		}
	}
	request.open("POST", "OwnerServlet");
	
	var encoded = args.join('&').replace(/%20/g, '+');
	request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	request.setRequestHeader('Content-Length', encoded.length);
	request.send(encoded);
}

function createArgPair(key, isSelect, isCheck) {
	var value = document.getElementById(key)
	if(isSelect) {
		value = value.options[value.selectedIndex].value;
	} else if (isCheck) {
		value = value.checked;
	} else {
		value = value.value;
	}
	return encodeURIComponent(key) + '=' + encodeURIComponent(value);
} 

function clearData() {
	var request = new XMLHttpRequest();
	request.onreadystatechange = function() {
		if (request.readyState == 4 && request.status == 200) {
			var request2 = new XMLHttpRequest();
			request2.onreadystatechange = function() {
				if (request2.readyState == 4 && request2.status == 200) {
					getData();
				}
			}
			request2.open("DELETE", "ActivityLogServlet");
			request2.send();
		}
	}
	request.open("DELETE", "OwnerServlet");
	request.send();
}

// Refresh owners and pets/activity logging data
function getData() {
	clearEverything();

	var request = new XMLHttpRequest();
	request.onreadystatechange = function() {
		if (request.readyState == 4 && request.status == 200) {
			buildOwnersTable(request.responseText);
		} else if (request.readyState == 4 && request.status != 200) {
			//TODO
			console.log("Error getting owner data");
		}
	}
	request.open("GET", "OwnerServlet?rand="+new Date().getTime());
	request.send();

	var request2 = new XMLHttpRequest();
	request2.onreadystatechange = function() {
		if (request2.readyState == 4 && request2.status == 200) {
			buildActivityLog(request2.responseText);
		} else if (request2.readyState == 4 && request2.status != 200) {
			//TODO
			console.log("Error getting people data");
		}
	}
	
	request2.open("GET", "ActivityLogServlet?rand="+new Date().getTime());
	request2.send();
}

function buildOwnersTable(data) {
	var tableData = JSON.parse(data);
	var tableBody = document.getElementById("tabledatabody");
	for (var i = 0; i < tableData.length; i++) {
		var tr = document.createElement("tr");
		var tdOwner = document.createElement("td");
		tdOwner.textContent = tableData[i][0];
		var tdPet = document.createElement("td");
		tdPet.textContent = tableData[i][1];
		tr.appendChild(tdOwner);
		tr.appendChild(tdPet);
		tableBody.appendChild(tr);
	}
}

function buildActivityLog(data) {
	var actData = JSON.parse(data);
	var actEnt = document.getElementById("activitylogentries");
	for (var i = 0; i < actData.length; i++) {
		var p = document.createElement("p");
		p.innerHTML = "[TX: <b>" + actData[i].txid + "</b>, BEAN: <b>" + actData[i].beanName + "</b>] " + actData[i].text;
		actEnt.appendChild(p);
	}
}

function clearEverything() {
	document.getElementById("activitylogentries").innerHTML = "";
	
	//can't use innerHTML here because of IE compatibility
	var tbody = document.getElementById("tabledatabody");
	while (tbody.firstChild) {
		tbody.removeChild(tbody.firstChild);
	}
}
      var xmlHttp;
      function createXMLHttpRequest(){
        if(window.ActiveXObject){
          xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        else if(window.XMLHttpRequest){
          xmlHttp = new XMLHttpRequest();
        }
      }
      function refreshModelList(){
        var paraVer = document.getElementById("paraVer").value;
        var paraVerDate = document.getElementById("para_ver_date").value;
        if(paraVer == "" || paraVerDate == ""){
          clearModelsList();
          return;
        }
        var url = "paraVer?"+createQueryString(paraVer,paraVerDate)+"&ts="+new Date().getTime();
        createXMLHttpRequest();
        xmlHttp.onreadystatechange = handleStateChange;
        xmlHttp.open("GET",url,true);
        xmlHttp.send(null);
      }
      function createQueryString(paraVer, paraVerDate){
        var queryString = "paraVer="+paraVer+"&paraVerDate="+paraVerDate;
        return queryString;
      }
      function handleStateChange(){
        if(xmlHttp.readyState == 4){
          if(xmlHttp.status == 200){
            updateModelsList();
          }
        }
      }
      function updateModelsList(){
        clearModelsList();
        var models = document.getElementById("para_ver_sel");
        var results = xmlHttp.responseXML.getElementsByTagName("model");
        var option = null;
        for(var i=0; i<results.length; i++){
          option = new Option(results[i].firstChild.nodeValue,results[i].firstChild.nodeValue);  
          models.options.add(option);
        }
        document.getElementById("para_ver_sel").onchange();
      }
      function clearModelsList(){
         var models = document.getElementById("para_ver_sel");
         while(models.childNodes.length>0){
           models.removeChild(models.childNodes[0]);
         }
      }
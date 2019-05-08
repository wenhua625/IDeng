(function($){
	$.fn.Huploadify = function(opts){
		var itemTemp = '<div id="${fileID}" class="uploadify-queue-item"><div class="uploadify-progress"><div class="uploadify-progress-bar"></div></div><span class="up_filename">${fileName}</span><a href="javascript:void(0);" class="uploadbtn">�ϴ�</a><a href="javascript:void(0);" class="delfilebtn">ɾ��</a></div>';
		var defaults = {
			fileTypeExts:'*.*',//�����ϴ����ļ����ͣ���ʽ'*.jpg;*.doc'
			uploader:'',//�ļ��ύ�ĵ�ַ
			auto:false,//�Ƿ����Զ��ϴ�
			method:'post',//��������ķ�ʽ��get��post
			multi:true,//�Ƿ�����ѡ�����ļ�
			formData:null,//���͸�����˵Ĳ�������ʽ��{key1:value1,key2:value2}
			fileObjName:'file',//�ں�˽����ļ��Ĳ������ƣ���PHP�е�$_FILES['file']
			fileSizeLimit:2048,//�����ϴ����ļ���С����λKB
			showUploadedPercent:true,//�Ƿ�ʵʱ��ʾ�ϴ��İٷֱȣ���20%
			showUploadedSize:false,//�Ƿ�ʵʱ��ʾ���ϴ����ļ���С����1M/2M
			buttonText:'',//�ϴ���ť�ϵ�����
			removeTimeout: 1000,//�ϴ���ɺ����������ʧʱ�䣬��λ����
			itemTemplate:itemTemp,//�ϴ�������ʾ��ģ��
			onUploadStart:null,//�ϴ���ʼʱ�Ķ���
			onUploadSuccess:null,//�ϴ��ɹ��Ķ���
			onUploadComplete:null,//�ϴ���ɵĶ���
			onUploadError:null, //�ϴ�ʧ�ܵĶ���
			onInit:null,//��ʼ��ʱ�Ķ���
			onCancel:null,//ɾ����ĳ���ļ���Ļص��������ɴ������file
			onClearQueue:null,//����ϴ����к�Ļص��������ڵ���cancel���������*ʱ����
			onDestroy:null,//�ڵ���destroy����ʱ����
			onSelect:null,//ѡ���ļ���Ļص��������ɴ������file
			onQueueComplete:null//�����е������ļ��ϴ���ɺ󴥷�
		}
			
		var option = $.extend(defaults,opts);

		//����һ��ͨ�ú�������
		var F = {
			//���ļ��ĵ�λ��bytesת��ΪKB��MB�����ڶ�������ָ��Ϊtrue������Զת��ΪKB
			formatFileSize : function(size,withKB){
				if (size > 1024 * 1024 && !withKB){
					size = (Math.round(size * 100 / (1024 * 1024)) / 100).toString() + 'MB';
				}
				else{
					size = (Math.round(size * 100 / 1024) / 100).toString() + 'KB';
				}
				return size;
			},
			//��������ļ������ַ���ת��Ϊ����,ԭ��ʽΪ*.jpg;*.png
			getFileTypes : function(str){
				var result = [];
				var arr1 = str.split(";");
				for(var i=0, len=arr1.length; i<len; i++){
					result.push(arr1[i].split(".").pop());
				}
				return result;
			},
			////�����ļ���Ż�ȡ�ļ�
			getFile : function(index,files){
				for(var i=0;i<files.length;i++){	   
					if(files[i].index == index){
						return files[i];
					}
				}
				return null;
			}
		};

		var returnObj = null;

		this.each(function(index, element){
			var _this = $(element);
			var instanceNumber = $('.uploadify').length+1;
			var uploadManager = {
				container : _this,
				filteredFiles : [],//���˺���ļ�����
				init : function(){
					var inputStr = '<input id="select_btn_'+instanceNumber+'" class="selectbtn" style="display:none;" type="file" name="fileselect[]"';
					inputStr += option.multi ? ' multiple' : '';
					inputStr += ' accept="';
					inputStr += F.getFileTypes(option.fileTypeExts).join(",");
					inputStr += '"/>';
					inputStr += '<a id="file_upload_'+instanceNumber+'-button" href="javascript:void(0)" class="uploadify-button">';
					inputStr += option.buttonText;
					inputStr += '</a>';
					var uploadFileListStr = '<div id="file_upload_'+instanceNumber+'-queue" class="uploadify-queue"></div>';
					_this.append(inputStr+uploadFileListStr);

					//��ʼ�����ص�ʵ��
					returnObj =  {
						instanceNumber : instanceNumber,
						upload : function(fileIndex){
							if(fileIndex === '*'){
								for(var i=0,len=uploadManager.filteredFiles.length;i<len;i++){
									uploadManager._uploadFile(uploadManager.filteredFiles[i]);
								}
							}
							else{
								var file = F.getFile(fileIndex,uploadManager.filteredFiles);
								file && uploadManager._uploadFile(file);	
							}
						},
						cancel : function(fileIndex){
							if(fileIndex === '*'){
								var len=uploadManager.filteredFiles.length;
								for(var i=len-1;i>=0;i--){
									uploadManager._deleteFile(uploadManager.filteredFiles[i]);
								}
								option.onClearQueue && option.onClearQueue(len);
							}
							else{
								var file = F.getFile(fileIndex,uploadManager.filteredFiles);
								file && uploadManager._deleteFile(file);
							}
						},
						disable : function(instanceID){
							var parent = instanceID ? $('file_upload_'+instanceID+'-button') : $('body');
							parent.find('.uploadify-button').css('background-color','#888').off('click');
						},
						ennable : function(instanceID){
							//����ϴ���ťʱ����file��click�¼�
							var parent = instanceID ? $('file_upload_'+instanceID+'-button') : $('body');
						  	parent.find('.uploadify-button').css('background-color','#707070').on('click',function(){
								parent.find('.selectbtn').trigger('click');
							});
						},
						destroy : function(){
							uploadManager.container.html('');
							uploadManager = null;
							option.onDestroy && option.onDestroy();
						},
						settings : function(name,value){
							if(arguments.length==1){
								return option[name];
							}
							else{
								if(name=='formData'){
									option.formData = $.extend(option.formData, value);
								}
								else{
									option[name] = value;
								}
							}
						},
						Huploadify : function(){
							var method = arguments[0];
							if(method in this){
								Array.prototype.splice.call(arguments, 0, 1);
								this[method].apply(this[method], arguments);
							}
						}
					};

					//�ļ�ѡ��ؼ�ѡ��
					var fileInput = this._getInputBtn();
				  	if (fileInput.length>0) {
						fileInput.change(function(e) { 
							uploadManager._getFiles(e); 
					 	});	
				 	}
				  
					//���ѡ���ļ���ťʱ����file��click�¼�
					_this.find('.uploadify-button').on('click',function(){
						_this.find('.selectbtn').trigger('click');
					});
				  
					option.onInit && option.onInit(returnObj);
				},
				_filter: function(files) {		//ѡ���ļ���Ĺ��˷���
					var arr = [];
					var typeArray = F.getFileTypes(option.fileTypeExts);
					if(typeArray.length>0){
						for(var i=0,len=files.length;i<len;i++){
							var f = files[i];
							if(parseInt(F.formatFileSize(f.size,true))<=option.fileSizeLimit){
								if($.inArray('*',typeArray)>=0 || $.inArray(f.name.split('.').pop(),typeArray)>=0){
									arr.push(f);
								}
								else{
									alert('�ļ� "'+f.name+'" ���Ͳ�����');
								}
							}
							else{
								alert('�ļ� "'+f.name+'" ��С�������ƣ�');
								continue;
							}
						}
					}
					return arr;
				},
				_getInputBtn : function(){
					return _this.find('.selectbtn');
				},
				_getFileList : function(){
					return _this.find('.uploadify-queue');
				},
				//����ѡ����ļ�����ȾDOM�ڵ�
				_renderFile : function(file){
					var $html = $(option.itemTemplate.replace(/\${fileID}/g,'fileupload_'+instanceNumber+'_'+file.index).replace(/\${fileName}/g,file.name).replace(/\${fileSize}/g,F.formatFileSize(file.size)).replace(/\${instanceID}/g,_this.attr('id')));
					//����Ƿ��Զ��ϴ�����ʾ�ϴ���ť
					if(!option.auto){
						$html.find('.uploadbtn').css('display','inline-block');
					}
					uploadManager._getFileList().append($html);

					//�ж��Ƿ���ʾ���ϴ��ļ���С
					if(option.showUploadedSize){
						var num = '<span class="progressnum"><span class="uploadedsize">0KB</span>/<span class="totalsize">${fileSize}</span></span>'.replace(/\${fileSize}/g,F.formatFileSize(file.size));
						$html.find('.uploadify-progress').after(num);
					}
					
					//�ж��Ƿ���ʾ�ϴ��ٷֱ�	
					if(option.showUploadedPercent){
						var percentText = '<span class="up_percent">0%</span>';
						$html.find('.uploadify-progress').after(percentText);
					}

					//����select����
					option.onSelect && option.onSelect(file);

					//�ж��Ƿ����Զ��ϴ�
					if(option.auto){
						uploadManager._uploadFile(file);
					}
					else{
						//������÷��Զ��ϴ������ϴ��¼�
					 	$html.find('.uploadbtn').on('click',function(){
					 		if(!$(this).hasClass('.disabledbtn')){
					 			$(this).addClass('.disabledbtn');
					 			uploadManager._uploadFile(file);
					 		}
				 		});
					}

					//Ϊɾ���ļ���ť��ɾ���ļ��¼�
			 		$html.find('.delfilebtn').on('click',function(){
			 			if(!$(this).hasClass('.disabledbtn')){
					 		$(this).addClass('.disabledbtn');
			 				uploadManager._deleteFile(file);
			 			}
			 		});
				},
				//��ȡѡ�����ļ�
				_getFiles : function(e){
			  		var files = e.target.files;
			  		files = uploadManager._filter(files);
			  		var fileCount = _this.find('.uploadify-queue .uploadify-queue-item').length;//�������Ѿ��е��ļ�����
		  			for(var i=0,len=files.length;i<len;i++){
		  				files[i].index = ++fileCount;
		  				files[i].status = 0;//���Ϊδ��ʼ�ϴ�
		  				uploadManager.filteredFiles.push(files[i]);
		  				var l = uploadManager.filteredFiles.length;
		  				uploadManager._renderFile(uploadManager.filteredFiles[l-1]);
		  			}
				},
				//ɾ���ļ�
				_deleteFile : function(file){
					for (var i = 0,len=uploadManager.filteredFiles.length; i<len; i++) {
						var f = uploadManager.filteredFiles[i];
						if (f.index == file.index) {
							uploadManager.filteredFiles.splice(i,1);
							_this.find('#fileupload_'+instanceNumber+'_'+file.index).fadeOut();
							option.onCancel && option.onCancel(file);	
							break;
						}
			  		}
				},
				//У���ϴ���ɺ�Ľ��������
				_regulateView : function(file){
					var thisfile = _this.find('#fileupload_'+instanceNumber+'_'+file.index);
					thisfile.find('.uploadify-progress-bar').css('width','100%');
					option.showUploadedSize && thisfile.find('.uploadedsize').text(thisfile.find('.totalsize').text());
					option.showUploadedPercent && thisfile.find('.up_percent').text('100%');	
				},
				onProgress : function(file, loaded, total) {
					var eleProgress = _this.find('#fileupload_'+instanceNumber+'_'+file.index+' .uploadify-progress');
					var percent = (loaded / total * 100).toFixed(2) +'%';
					if(option.showUploadedSize){
						eleProgress.nextAll('.progressnum .uploadedsize').text(F.formatFileSize(loaded));
						eleProgress.nextAll('.progressnum .totalsize').text(F.formatFileSize(total));
					}
					if(option.showUploadedPercent){
						eleProgress.nextAll('.up_percent').text(percent);	
					}
					eleProgress.children('.uploadify-progress-bar').css('width',percent);
			  	},
			  	_allFilesUploaded : function(){
		  			var queueData = {
						uploadsSuccessful : 0,
						uploadsErrored : 0
					};
			  		for(var i=0,len=uploadManager.filteredFiles.length; i<len; i++){
			  			var s = uploadManager.filteredFiles[i].status;
			  			if(s===0 || s===1){
			  				queueData = false;
			  				break;
			  			}
			  			else if(s===2){
			  				queueData.uploadsSuccessful++;
			  			}
			  			else if(s===3){
			  				queueData.uploadsErrored++;
			  			}
			  		}
			  		return queueData;
			  	},
				//�ϴ��ļ�
				_uploadFile : function(file){
					var xhr = null;
					try{
						xhr=new XMLHttpRequest();
					}catch(e){	  
						xhr=ActiveXobject("Msxml12.XMLHTTP");
				  	}
				  	if(xhr.upload){
				  		// �ϴ���
					  	xhr.upload.onprogress = function(e) {
							uploadManager.onProgress(file, e.loaded, e.total);
						};

						xhr.onreadystatechange = function(e) {
							if(xhr.readyState == 4){
								if(xhr.status == 200){
									uploadManager._regulateView(file);
									file.status = 2;//���Ϊ�ϴ��ɹ�
									option.onUploadSuccess && option.onUploadSuccess(file, xhr.responseText);
									//��ָ���ļ��ʱ���ɾ��������
									setTimeout(function(){
										_this.find('#fileupload_'+instanceNumber+'_'+file.index).fadeOut();
									},option.removeTimeout);
								}
								else {
									file.status = 3;//���Ϊ�ϴ�ʧ��
									option.onUploadError && option.onUploadError(file, xhr.responseText);		
								}
								option.onUploadComplete && option.onUploadComplete(file,xhr.responseText);
								
								//�������е��ļ��Ƿ�ȫ���ϴ���ɣ�ִ��onQueueComplete
								if(option.onQueueComplete){
									var queueData = uploadManager._allFilesUploaded();
									queueData && option.onQueueComplete(queueData);	
								}

								//����ļ�ѡ����е�����ֵ
								uploadManager._getInputBtn().val('');
							}
						}

						if(file.status===0){
							file.status = 1;//���Ϊ�����ϴ�
							option.onUploadStart && option.onUploadStart(file);
							// ��ʼ�ϴ�
							xhr.open(option.method, option.uploader, true);
							xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
							var fd = new FormData();
							fd.append(option.fileObjName,file);
							if(option.formData){
							 	for(key in option.formData){
							  		fd.append(key,option.formData[key]);
							  	}
							}
							xhr.send(fd);
						}
						
				  	}
				}
			};

			uploadManager.init();
		});
		
		return returnObj;

	}
})(jQuery)
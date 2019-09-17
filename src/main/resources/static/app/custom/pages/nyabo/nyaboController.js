angular
    .module('altairApp')
    	.controller("nyaboCtrl",['$scope','$timeout','user_data','mainService','sweet','$state','Upload','fileUpload',
	        function ($scope,$timeout,user_data,mainService,sweet,$state,Upload,fileUpload) {       	
    		
    		
    		$('.dropify').dropify();
    	
			
		    $scope.formUpload={};
		    $scope.formFile={};
		    $scope.submitUploadNotlohZuil = function() {
		       $scope.sendBtn=false;
		       if ($scope.formUpload.uploadfile.$valid && $scope.uploadfile) {
		    	    bar.css("width", "0%").text("0%");
                  progressbar.removeClass("uk-hidden");
                  $scope.uploadNotlohZuil($scope.uploadfile,2);
		       }
	        };
	        
	     
	        $scope.submitUploadDifference = function() {
		       $scope.sendBtn=false;
		       if ($scope.formFile.afl.$valid && $scope.afl) {
		    	   bar.css("width", "0%").text("0%");
                   progressbar.removeClass("uk-hidden");
		           $scope.uploadNotlohZuil($scope.afl,1);
			    }
	        };
	        
	        
	        $scope.download = function(id){
	        	mainService.withdomain('get', '/api/file/download/'+id).
    			then(function(data){
    				
    			});  
  	        }
  	      
	        
	   	 	$scope.pmenuGrid = {
	                dataSource: {	                   
	                    transport: {
	                    	read:  {
	                            url: "/core/list/FileConverted",
	                            contentType:"application/json; charset=UTF-8",     
	                            data: {"custom":"where userid="+user_data.id,"sort":[{field: 'id', dir: 'desc'}]},
	                            type:"POST"
	                        },
	                        update: {
	                            url: "/core/update/"+$scope.domain+"",
	                            contentType:"application/json; charset=UTF-8",                                    
	                            type:"POST",
	                            complete: function(e) {
	                            	$(".k-grid").data("kendoGrid").dataSource.read(); 
	                    		}
	                        },
	                        destroy: {
	                            url: "/core/delete/"+$scope.domain+"",
	                            contentType:"application/json; charset=UTF-8",                                    
	                            type:"POST"
	                        },
	                        create: {
	                        	url: "/core/create/"+$scope.domain+"",
	                            contentType:"application/json; charset=UTF-8",                                    
	                            type:"POST",
	                            complete: function(e) {
	                            	$(".k-grid").data("kendoGrid").dataSource.read(); 
	                    		}
	                        },
	                        parameterMap: function(options) {
	                       	 return JSON.stringify(options);
	                       }
	                    },
	                    schema: {
	                     	data:"data",
	                     	total:"total",
	                     	 model: {                                	
	                             id: "id",
	                             fields: {   
	                             	id: { editable: false,nullable: true},
	                             	menuname: { type: "string", validation: { required: true } },
	                             	stateurl: { type: "string", defaultValue:'#'},
	                                uicon: { type: "string"},
	                                parentid: { type: "number"},
	                                orderid: { type: "number" }
	                             }
	                         }
	                     },
	                    pageSize: 8,
	                    serverFiltering: true,
	                    serverPaging: true,
	                    serverSorting: true
	                },
	                filterable:{
		                	 mode: "row"
		                },
	                sortable: true,
	                resizable: true,
	                pageable: {
	                    refresh: true,
	                    pageSizes: true,
	                    buttonCount: 5
	                },
	                columns: [
	                	 	  {title: "#",template: "<span class='row-number'></span>", width:"60px"},
	                          { field:"name", title: "Нэр /Mn/" },
	                          /*{ field: "flurl", title:"URL" },*/
	                          { field: "fsize", title:"IKON"},
	                          { field: "fdate",  title:"Эцэг цэс"},
	                          {template: kendo.template($("#download").html()), width: "90px"}
                 ],
                 dataBound: function () {
		                var rows = this.items();
		                  $(rows).each(function () {
		                      var index = $(this).index() + 1 
		                      + ($(".k-grid").data("kendoGrid").dataSource.pageSize() * ($(".k-grid").data("kendoGrid").dataSource.page() - 1));;
		                      var rowLabel = $(this).find(".row-number");
		                      $(rowLabel).html(index);
		                  });
		  	           },
	                      
	            };  
	        
	        $scope.uploadNotlohZuil = function (file,y) {
		    	  var xurl="";
		    	  if(y==1){
		    		  xurl='/api/checker';
		    	  }		
		    	  else{
		    		  xurl='/api/nyabo';
		    	  }
		          Upload.upload({
		              url: xurl,
		              data: {file: file, 'description': $scope.description}
		          }).then(function (resp) {
		        	  console.log(resp.data);
		        	  if(!resp.data.excel){
		        		  $scope.formula=resp.data.formula;
		        		  sweet.show('Анхаар!', 'Тайлангийн дансаа шалгана уу!!!', 'error');
		        	  }
		        	  else{
		        		  sweet.show('Анхаар!', 'Тайлан амжилттай хөрвүүлэгдлээ!!!', 'success');
		        	  }
		        	  progressbar.removeClass("uk-hidden");
		              console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
		              $scope.ars=resp.data.error;
		              $scope.uploadfile=null;
		              $scope.uploadfileDif=null;		         
		              $(".nnn .k-grid").data("kendoGrid").dataSource.read(); 
		              progressbar.addClass("uk-hidden");
		          }, function (resp) {
		              console.log('Error status: ' + resp.status);
		          }, function (evt) {
		              var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
		             
		              percent = progressPercentage;
                     bar.css("width", percent+"%").text(percent+"%");                    
		              console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
		          });
		          
		        
		       };
		
    		
    		
    		  var progressbar = $("#file_upload-progressbar"),
              bar         = progressbar.find('.uk-progress-bar'),
              settings    = {

                  action: '/api/nyabo', // upload url

                  allow : '*.(xlsx|xls)', // allow only images

                  loadstart: function() {
                      bar.css("width", "0%").text("0%");
                      progressbar.removeClass("uk-hidden");
                  },

                  progress: function(percent) {
                      percent = Math.ceil(percent);
                      bar.css("width", percent+"%").text(percent+"%");
                  },

                  allcomplete: function(response) {

                      bar.css("width", "100%").text("100%");

                      setTimeout(function(){
                          progressbar.addClass("uk-hidden");
                      }, 250);

                      console.log(response);
                      alert("Upload Completed")
                  }
              };

    		  var select = UIkit.uploadSelect($("#file_upload-select"), settings),
              drop   = UIkit.uploadDrop($("#file_upload-drop"), settings);
    		
    		  
    		
    		    $("#spreadSheetZagwarView").kendoSpreadsheet();
		        $scope.viewExcel =function(dataItem){
		        	 $rootScope.content_preloader_show();
		        	 $scope.workTitle=dataItem.data2;
		          	 mainService.withdomain('get','/api/excel/verify/nbb/'+dataItem.appid+'/'+dataItem.formid).then(function(response){
		           		 if(response!=false){
		           			$scope.xlsx=true;
			         	    $scope.purl='/api/excel/export/nbb/'+dataItem.appid+'/'+dataItem.formid;
			         	    var xhr = new XMLHttpRequest();
				           	xhr.open('GET',  $scope.purl, true);
				           	xhr.responseType = 'blob';
				           	 
				           	xhr.onload = function(e) {
				           	  if (this.status == 200) {
				           	    // get binary data as a response
				           		  
				           	    var blob = this.response;
				           	    
				           	    console.log(blob);
				           	    var spreadsheet = $("#spreadSheetZagwarView").data("kendoSpreadsheet");
				 		            spreadsheet.fromFile(blob);				 		          
							   		UIkit.modal("#modal_excel_file", {center: false}).show();
				           	  }
				           	  else{
				           		  sweet.show('Анхаар!', 'Файл устгагдсан байна.', 'error');
				           	  }
				           	};
				            setTimeout(function(){
       	 					 $rootScope.content_preloader_hide();
	                         }, 3000);
				           	xhr.send();    	 					
		           		 }
		           		 else{
		           			 sweet.show('Анхаар!', 'Excel тайлан оруулаагүй байна !!!', 'error');
		           			 $rootScope.content_preloader_hide();
		           		 }
		           		
		           	 });
	            }        
	        
	        }
    ]);

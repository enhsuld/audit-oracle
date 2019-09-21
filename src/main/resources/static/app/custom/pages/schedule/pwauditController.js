angular
    .module('altairApp')
    .controller("wauditCtrl", ['$scope', 'user_data', 'mainService', 'sweet', '$state', 'p_cat', 'reason',
        function ($scope, user_data, mainService, sweet, $state, p_cat, reason) {

            var modal = UIkit.modal("#modal_update");
            $scope.domain = "com.netgloo.models.MainAuditRegistration.";
            $scope.read = function (item) {
                $state.go('restricted.work.mainwork', {issueId: item.id});
            };

            var aj = [{"text": "Хувиарлаагүй", "value": "1"}, {
                "text": "Хувиарласан",
                "value": "2"
            }, {"text": "Баталсан", "value": "3"}, {"text": "Буцаасан", "value": "4"}];

            var alevel = [{"text": "Төлөвлөх үе шат", "value": "3"}, {
                "text": "Гүйцэтгэх үе шат",
                "value": "4"
            }, {"text": "Тайлагналын үе шат", "value": "5"}, {"text": "Тайлагналын дараах", "value": "6"}];

            var orgtype = [{"text": "ААН", "value": "2"}, {"text": "ТШЗ", "value": "1"}, {
                "text": "Бусад",
                "value": "3"
            }];
            var autype = [{"text": "Төрийн", "value": 1}, {"text": "Хувийн", "value": 2}];

            function init() {
                mainService.withdomain('get', '/core/rjson/' + user_data.id + '/' + $state.current.name + '.')
                    .then(function (data) {
                        if (data.rread === 1) {
                            $scope.puserGrid = {
                                dataSource: {
                                    autoSync: true,
                                    transport: {
                                        read: {
                                            url: "/fin/list/MainAuditRegistrationAu",
                                            contentType: "application/json; charset=UTF-8",
                                            data: {"userid": user_data.id, "depid": user_data.depid},
                                            type: "POST"
                                        },
                                        update: {
                                            url: "/core/update/" + $scope.domain + "",
                                            contentType: "application/json; charset=UTF-8",
                                            type: "POST"
                                        },
                                        destroy: {
                                            //  url: "/core/delete/"+$scope.domain+"",
                                            contentType: "application/json; charset=UTF-8",
                                            type: "POST",
                                            complete: function (e) {
                                                $("#notificationDestroy").trigger('click');
                                            }
                                        },
                                        create: {
                                            url: "/core/create/" + $scope.domain + "",
                                            contentType: "application/json; charset=UTF-8",
                                            type: "POST",
                                            complete: function (e) {
                                                $("#notificationSuccess").trigger('click');
                                                $(".k-grid").data("kendoGrid").dataSource.read();
                                            }
                                        },
                                        parameterMap: function (options) {
                                            return JSON.stringify(options);
                                        }
                                    },
                                    schema: {
                                        data: "data",
                                        total: "total",
                                        model: {
                                            id: "id"
                                        }
                                    },
                                    pageSize: 8,
                                    serverPaging: true,
                                    serverSorting: true,
                                    serverFiltering: true
                                },

                                filterable: {
                                    mode: "row"
                                },
                                excel: {
                                    fileName: "Organization Export.xlsx",
                                    proxyURL: "//demos.telerik.com/kendo-ui/service/export",
                                    filterable: true,
                                    allPages: true
                                },
                                sortable: true,
                                resizable: true,
								height: function () {
									return $(window).height() - 220;
								},
                                pageable: {
                                    refresh: true,
                                    pageSizes: true,
                                    buttonCount: 5
                                },
                                columns: [
                                    {title: "#", template: "<span class='row-number'></span>", width: 60},
                                    {field: "gencode", title: "Код", width: 200},
                                    {field: "orgname", title: "Байгууллагын нэр"},
                                    {field: "regnum", title: "Байгууллагын регистр", width: 200,},
                                    {field: "autype", values: autype, width: 150, title: "Аудитын төрөл"},
                                    {field: "orgtype", values: orgtype, width: 150, title: "Байгууллагын төрөл"},
                                    {field: "auditname", title: "Аудитын нэр"},
                                    {
                                        title: "Тайлан",
                                        columns: [{
                                            field: "excelurlplan",
                                            title: "Төлөвлөлт",
                                            template: "#if (excelurlplan!=null) {# <span>Татсан</span> #} else {# <span>Татаагүй</span> #}#",
                                            filterable: false,
                                            width: 100
                                        }, {
                                            field: "excelurlprocess",
                                            title: "Гүйцэтгэл",
                                            template: "#if (excelurlprocess!=null) {# <span>Татсан</span> #} else {# <span>Татаагүй</span> #}#",
                                            filterable: false,
                                            width: 100
                                        }]
                                    },
                                    {template: kendo.template($("#read").html()), width: 80}
                                ],
                                dataBound: function () {
                                    var rows = this.items();
                                    $(rows).each(function () {
                                        var index = $(this).index() + 1
                                            + ($(".k-grid").data("kendoGrid").dataSource.pageSize() * ($(".k-grid").data("kendoGrid").dataSource.page() - 1));
                                        var rowLabel = $(this).find(".row-number");
                                        $(rowLabel).html(index);
                                    });
                                    var grid = this;
                                    grid.tbody.find("tr").dblclick(function (e) {
                                        var dataItem = grid.dataItem(this);
                                        $scope.read(dataItem);
                                    });
                                },
                                editable: "popup"
                            };
                        } else {
                            $state.go('error.404');
                        }
                    });
            }
            init();
        }
    ]);

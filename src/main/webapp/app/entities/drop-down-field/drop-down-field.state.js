(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('drop-down-field', {
            parent: 'entity',
            url: '/drop-down-field?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.dropDownField.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/drop-down-field/drop-down-fields.html',
                    controller: 'DropDownFieldController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('dropDownField');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('drop-down-field-detail', {
            parent: 'entity',
            url: '/drop-down-field/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.dropDownField.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/drop-down-field/drop-down-field-detail.html',
                    controller: 'DropDownFieldDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('dropDownField');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'DropDownField', function($stateParams, DropDownField) {
                    return DropDownField.get({id : $stateParams.id});
                }]
            }
        })
        .state('drop-down-field.new', {
            parent: 'drop-down-field',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/drop-down-field/drop-down-field-dialog.html',
                    controller: 'DropDownFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                weaponName: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('drop-down-field', null, { reload: true });
                }, function() {
                    $state.go('drop-down-field');
                });
            }]
        })
        .state('drop-down-field.edit', {
            parent: 'drop-down-field',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/drop-down-field/drop-down-field-dialog.html',
                    controller: 'DropDownFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['DropDownField', function(DropDownField) {
                            return DropDownField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('drop-down-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('drop-down-field.delete', {
            parent: 'drop-down-field',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/drop-down-field/drop-down-field-delete-dialog.html',
                    controller: 'DropDownFieldDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['DropDownField', function(DropDownField) {
                            return DropDownField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('drop-down-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

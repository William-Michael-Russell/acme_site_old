(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('text-inputs', {
            parent: 'entity',
            url: '/text-inputs?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.text_inputs.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/text-inputs/text-inputs.html',
                    controller: 'Text_inputsController',
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
                    $translatePartialLoader.addPart('text_inputs');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('text-inputs-detail', {
            parent: 'entity',
            url: '/text-inputs/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.text_inputs.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/text-inputs/text-inputs-detail.html',
                    controller: 'Text_inputsDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('text_inputs');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Text_inputs', function($stateParams, Text_inputs) {
                    return Text_inputs.get({id : $stateParams.id});
                }]
            }
        })
        .state('text-inputs.new', {
            parent: 'text-inputs',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/text-inputs/text-inputs-dialog.html',
                    controller: 'Text_inputsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('text-inputs', null, { reload: true });
                }, function() {
                    $state.go('text-inputs');
                });
            }]
        })
        .state('text-inputs.edit', {
            parent: 'text-inputs',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/text-inputs/text-inputs-dialog.html',
                    controller: 'Text_inputsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Text_inputs', function(Text_inputs) {
                            return Text_inputs.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('text-inputs', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('text-inputs.delete', {
            parent: 'text-inputs',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/text-inputs/text-inputs-delete-dialog.html',
                    controller: 'Text_inputsDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Text_inputs', function(Text_inputs) {
                            return Text_inputs.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('text-inputs', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

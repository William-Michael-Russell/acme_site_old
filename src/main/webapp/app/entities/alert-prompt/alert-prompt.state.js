(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('alert-prompt', {
            parent: 'entity',
            url: '/alert-prompt?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.alertPrompt.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/alert-prompt/alert-prompts.html',
                    controller: 'AlertPromptController',
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
                    $translatePartialLoader.addPart('alertPrompt');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('alert-prompt-detail', {
            parent: 'entity',
            url: '/alert-prompt/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.alertPrompt.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/alert-prompt/alert-prompt-detail.html',
                    controller: 'AlertPromptDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('alertPrompt');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'AlertPrompt', function($stateParams, AlertPrompt) {
                    return AlertPrompt.get({id : $stateParams.id});
                }]
            }
        })
        .state('alert-prompt.new', {
            parent: 'alert-prompt',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alert-prompt/alert-prompt-dialog.html',
                    controller: 'AlertPromptDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                alertName: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('alert-prompt', null, { reload: true });
                }, function() {
                    $state.go('alert-prompt');
                });
            }]
        })
        .state('alert-prompt.edit', {
            parent: 'alert-prompt',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alert-prompt/alert-prompt-dialog.html',
                    controller: 'AlertPromptDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AlertPrompt', function(AlertPrompt) {
                            return AlertPrompt.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('alert-prompt', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('alert-prompt.delete', {
            parent: 'alert-prompt',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alert-prompt/alert-prompt-delete-dialog.html',
                    controller: 'AlertPromptDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['AlertPrompt', function(AlertPrompt) {
                            return AlertPrompt.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('alert-prompt', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

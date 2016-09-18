(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('alpha-numeric-input-field', {
            parent: 'entity',
            url: '/alpha-numeric-input-field?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.alphaNumericInputField.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/alpha-numeric-input-field/alpha-numeric-input-fields.html',
                    controller: 'AlphaNumericInputFieldController',
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
                    $translatePartialLoader.addPart('alphaNumericInputField');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('alpha-numeric-input-field-detail', {
            parent: 'entity',
            url: '/alpha-numeric-input-field/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.alphaNumericInputField.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/alpha-numeric-input-field/alpha-numeric-input-field-detail.html',
                    controller: 'AlphaNumericInputFieldDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('alphaNumericInputField');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'AlphaNumericInputField', function($stateParams, AlphaNumericInputField) {
                    return AlphaNumericInputField.get({id : $stateParams.id});
                }]
            }
        })
        .state('alpha-numeric-input-field.new', {
            parent: 'alpha-numeric-input-field',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alpha-numeric-input-field/alpha-numeric-input-field-dialog.html',
                    controller: 'AlphaNumericInputFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                alphaNumericField: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('alpha-numeric-input-field', null, { reload: true });
                }, function() {
                    $state.go('alpha-numeric-input-field');
                });
            }]
        })
        .state('alpha-numeric-input-field.edit', {
            parent: 'alpha-numeric-input-field',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alpha-numeric-input-field/alpha-numeric-input-field-dialog.html',
                    controller: 'AlphaNumericInputFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AlphaNumericInputField', function(AlphaNumericInputField) {
                            return AlphaNumericInputField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('alpha-numeric-input-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('alpha-numeric-input-field.delete', {
            parent: 'alpha-numeric-input-field',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alpha-numeric-input-field/alpha-numeric-input-field-delete-dialog.html',
                    controller: 'AlphaNumericInputFieldDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['AlphaNumericInputField', function(AlphaNumericInputField) {
                            return AlphaNumericInputField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('alpha-numeric-input-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

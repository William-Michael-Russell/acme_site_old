(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('numeric-input-field', {
            parent: 'entity',
            url: '/numeric-input-field?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.numericInputField.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/numeric-input-field/numeric-input-fields.html',
                    controller: 'NumericInputFieldController',
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
                    $translatePartialLoader.addPart('numericInputField');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('numeric-input-field-detail', {
            parent: 'entity',
            url: '/numeric-input-field/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.numericInputField.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/numeric-input-field/numeric-input-field-detail.html',
                    controller: 'NumericInputFieldDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('numericInputField');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'NumericInputField', function($stateParams, NumericInputField) {
                    return NumericInputField.get({id : $stateParams.id});
                }]
            }
        })
        .state('numeric-input-field.new', {
            parent: 'numeric-input-field',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/numeric-input-field/numeric-input-field-dialog.html',
                    controller: 'NumericInputFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                numericField: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('numeric-input-field', null, { reload: true });
                }, function() {
                    $state.go('numeric-input-field');
                });
            }]
        })
        .state('numeric-input-field.edit', {
            parent: 'numeric-input-field',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/numeric-input-field/numeric-input-field-dialog.html',
                    controller: 'NumericInputFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['NumericInputField', function(NumericInputField) {
                            return NumericInputField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('numeric-input-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('numeric-input-field.delete', {
            parent: 'numeric-input-field',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/numeric-input-field/numeric-input-field-delete-dialog.html',
                    controller: 'NumericInputFieldDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['NumericInputField', function(NumericInputField) {
                            return NumericInputField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('numeric-input-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

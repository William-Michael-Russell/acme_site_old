(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('alpa-numeric-input-field', {
            parent: 'entity',
            url: '/alpa-numeric-input-field?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.alpaNumericInputField.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/alpa-numeric-input-field/alpa-numeric-input-fields.html',
                    controller: 'AlpaNumericInputFieldController',
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
                    $translatePartialLoader.addPart('alpaNumericInputField');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('alpa-numeric-input-field-detail', {
            parent: 'entity',
            url: '/alpa-numeric-input-field/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.alpaNumericInputField.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/alpa-numeric-input-field/alpa-numeric-input-field-detail.html',
                    controller: 'AlpaNumericInputFieldDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('alpaNumericInputField');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'AlpaNumericInputField', function($stateParams, AlpaNumericInputField) {
                    return AlpaNumericInputField.get({id : $stateParams.id});
                }]
            }
        })
        .state('alpa-numeric-input-field.new', {
            parent: 'alpa-numeric-input-field',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alpa-numeric-input-field/alpa-numeric-input-field-dialog.html',
                    controller: 'AlpaNumericInputFieldDialogController',
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
                    $state.go('alpa-numeric-input-field', null, { reload: true });
                }, function() {
                    $state.go('alpa-numeric-input-field');
                });
            }]
        })
        .state('alpa-numeric-input-field.edit', {
            parent: 'alpa-numeric-input-field',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alpa-numeric-input-field/alpa-numeric-input-field-dialog.html',
                    controller: 'AlpaNumericInputFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AlpaNumericInputField', function(AlpaNumericInputField) {
                            return AlpaNumericInputField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('alpa-numeric-input-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('alpa-numeric-input-field.delete', {
            parent: 'alpa-numeric-input-field',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alpa-numeric-input-field/alpa-numeric-input-field-delete-dialog.html',
                    controller: 'AlpaNumericInputFieldDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['AlpaNumericInputField', function(AlpaNumericInputField) {
                            return AlpaNumericInputField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('alpa-numeric-input-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('password-input-field', {
            parent: 'entity',
            url: '/password-input-field?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.passwordInputField.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/password-input-field/password-input-fields.html',
                    controller: 'PasswordInputFieldController',
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
                    $translatePartialLoader.addPart('passwordInputField');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('password-input-field-detail', {
            parent: 'entity',
            url: '/password-input-field/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.passwordInputField.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/password-input-field/password-input-field-detail.html',
                    controller: 'PasswordInputFieldDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('passwordInputField');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'PasswordInputField', function($stateParams, PasswordInputField) {
                    return PasswordInputField.get({id : $stateParams.id});
                }]
            }
        })
        .state('password-input-field.new', {
            parent: 'password-input-field',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/password-input-field/password-input-field-dialog.html',
                    controller: 'PasswordInputFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                passwordField: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('password-input-field', null, { reload: true });
                }, function() {
                    $state.go('password-input-field');
                });
            }]
        })
        .state('password-input-field.edit', {
            parent: 'password-input-field',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/password-input-field/password-input-field-dialog.html',
                    controller: 'PasswordInputFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['PasswordInputField', function(PasswordInputField) {
                            return PasswordInputField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('password-input-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('password-input-field.delete', {
            parent: 'password-input-field',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/password-input-field/password-input-field-delete-dialog.html',
                    controller: 'PasswordInputFieldDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['PasswordInputField', function(PasswordInputField) {
                            return PasswordInputField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('password-input-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

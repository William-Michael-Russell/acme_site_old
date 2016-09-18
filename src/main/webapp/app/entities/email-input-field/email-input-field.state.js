(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('email-input-field', {
            parent: 'entity',
            url: '/email-input-field?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.emailInputField.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/email-input-field/email-input-fields.html',
                    controller: 'EmailInputFieldController',
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
                    $translatePartialLoader.addPart('emailInputField');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('email-input-field-detail', {
            parent: 'entity',
            url: '/email-input-field/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.emailInputField.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/email-input-field/email-input-field-detail.html',
                    controller: 'EmailInputFieldDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('emailInputField');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'EmailInputField', function($stateParams, EmailInputField) {
                    return EmailInputField.get({id : $stateParams.id});
                }]
            }
        })
        .state('email-input-field.new', {
            parent: 'email-input-field',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/email-input-field/email-input-field-dialog.html',
                    controller: 'EmailInputFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                emailField: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('email-input-field', null, { reload: true });
                }, function() {
                    $state.go('email-input-field');
                });
            }]
        })
        .state('email-input-field.edit', {
            parent: 'email-input-field',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/email-input-field/email-input-field-dialog.html',
                    controller: 'EmailInputFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['EmailInputField', function(EmailInputField) {
                            return EmailInputField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('email-input-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('email-input-field.delete', {
            parent: 'email-input-field',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/email-input-field/email-input-field-delete-dialog.html',
                    controller: 'EmailInputFieldDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['EmailInputField', function(EmailInputField) {
                            return EmailInputField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('email-input-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

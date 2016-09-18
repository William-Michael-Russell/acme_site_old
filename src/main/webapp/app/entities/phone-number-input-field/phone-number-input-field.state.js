(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('phone-number-input-field', {
            parent: 'entity',
            url: '/phone-number-input-field?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.phoneNumberInputField.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/phone-number-input-field/phone-number-input-fields.html',
                    controller: 'PhoneNumberInputFieldController',
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
                    $translatePartialLoader.addPart('phoneNumberInputField');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('phone-number-input-field-detail', {
            parent: 'entity',
            url: '/phone-number-input-field/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.phoneNumberInputField.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/phone-number-input-field/phone-number-input-field-detail.html',
                    controller: 'PhoneNumberInputFieldDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('phoneNumberInputField');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'PhoneNumberInputField', function($stateParams, PhoneNumberInputField) {
                    return PhoneNumberInputField.get({id : $stateParams.id});
                }]
            }
        })
        .state('phone-number-input-field.new', {
            parent: 'phone-number-input-field',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/phone-number-input-field/phone-number-input-field-dialog.html',
                    controller: 'PhoneNumberInputFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                phoneNumberField: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('phone-number-input-field', null, { reload: true });
                }, function() {
                    $state.go('phone-number-input-field');
                });
            }]
        })
        .state('phone-number-input-field.edit', {
            parent: 'phone-number-input-field',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/phone-number-input-field/phone-number-input-field-dialog.html',
                    controller: 'PhoneNumberInputFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['PhoneNumberInputField', function(PhoneNumberInputField) {
                            return PhoneNumberInputField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('phone-number-input-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('phone-number-input-field.delete', {
            parent: 'phone-number-input-field',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/phone-number-input-field/phone-number-input-field-delete-dialog.html',
                    controller: 'PhoneNumberInputFieldDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['PhoneNumberInputField', function(PhoneNumberInputField) {
                            return PhoneNumberInputField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('phone-number-input-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

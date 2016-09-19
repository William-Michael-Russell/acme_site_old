(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('radio-checkbox-field', {
            parent: 'entity',
            url: '/radio-checkbox-field?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.radioCheckboxField.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/radio-checkbox-field/radio-checkbox-fields.html',
                    controller: 'RadioCheckboxFieldController',
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
                    $translatePartialLoader.addPart('radioCheckboxField');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('radio-checkbox-field-detail', {
            parent: 'entity',
            url: '/radio-checkbox-field/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.radioCheckboxField.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/radio-checkbox-field/radio-checkbox-field-detail.html',
                    controller: 'RadioCheckboxFieldDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('radioCheckboxField');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'RadioCheckboxField', function($stateParams, RadioCheckboxField) {
                    return RadioCheckboxField.get({id : $stateParams.id});
                }]
            }
        })
        .state('radio-checkbox-field.new', {
            parent: 'radio-checkbox-field',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/radio-checkbox-field/radio-checkbox-field-dialog.html',
                    controller: 'RadioCheckboxFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                radioCheckbox: false,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('radio-checkbox-field', null, { reload: true });
                }, function() {
                    $state.go('radio-checkbox-field');
                });
            }]
        })
        .state('radio-checkbox-field.edit', {
            parent: 'radio-checkbox-field',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/radio-checkbox-field/radio-checkbox-field-dialog.html',
                    controller: 'RadioCheckboxFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['RadioCheckboxField', function(RadioCheckboxField) {
                            return RadioCheckboxField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('radio-checkbox-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('radio-checkbox-field.delete', {
            parent: 'radio-checkbox-field',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/radio-checkbox-field/radio-checkbox-field-delete-dialog.html',
                    controller: 'RadioCheckboxFieldDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['RadioCheckboxField', function(RadioCheckboxField) {
                            return RadioCheckboxField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('radio-checkbox-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

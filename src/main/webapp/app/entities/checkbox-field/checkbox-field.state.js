(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('checkbox-field', {
            parent: 'entity',
            url: '/checkbox-field?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.checkboxField.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/checkbox-field/checkbox-fields.html',
                    controller: 'CheckboxFieldController',
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
                    $translatePartialLoader.addPart('checkboxField');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('checkbox-field-detail', {
            parent: 'entity',
            url: '/checkbox-field/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.checkboxField.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/checkbox-field/checkbox-field-detail.html',
                    controller: 'CheckboxFieldDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('checkboxField');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'CheckboxField', function($stateParams, CheckboxField) {
                    return CheckboxField.get({id : $stateParams.id});
                }]
            }
        })
        .state('checkbox-field.new', {
            parent: 'checkbox-field',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/checkbox-field/checkbox-field-dialog.html',
                    controller: 'CheckboxFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                checkboxOption: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('checkbox-field', null, { reload: true });
                }, function() {
                    $state.go('checkbox-field');
                });
            }]
        })
        .state('checkbox-field.edit', {
            parent: 'checkbox-field',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/checkbox-field/checkbox-field-dialog.html',
                    controller: 'CheckboxFieldDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['CheckboxField', function(CheckboxField) {
                            return CheckboxField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('checkbox-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('checkbox-field.delete', {
            parent: 'checkbox-field',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/checkbox-field/checkbox-field-delete-dialog.html',
                    controller: 'CheckboxFieldDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['CheckboxField', function(CheckboxField) {
                            return CheckboxField.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('checkbox-field', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

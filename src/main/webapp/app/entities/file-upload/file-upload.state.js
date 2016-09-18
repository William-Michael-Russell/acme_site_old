(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('file-upload', {
            parent: 'entity',
            url: '/file-upload?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.fileUpload.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/file-upload/file-uploads.html',
                    controller: 'FileUploadController',
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
                    $translatePartialLoader.addPart('fileUpload');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('file-upload-detail', {
            parent: 'entity',
            url: '/file-upload/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.fileUpload.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/file-upload/file-upload-detail.html',
                    controller: 'FileUploadDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('fileUpload');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'FileUpload', function($stateParams, FileUpload) {
                    return FileUpload.get({id : $stateParams.id});
                }]
            }
        })
        .state('file-upload.new', {
            parent: 'file-upload',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/file-upload/file-upload-dialog.html',
                    controller: 'FileUploadDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                fieldField: null,
                                fieldFieldContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('file-upload', null, { reload: true });
                }, function() {
                    $state.go('file-upload');
                });
            }]
        })
        .state('file-upload.edit', {
            parent: 'file-upload',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/file-upload/file-upload-dialog.html',
                    controller: 'FileUploadDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['FileUpload', function(FileUpload) {
                            return FileUpload.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('file-upload', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('file-upload.delete', {
            parent: 'file-upload',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/file-upload/file-upload-delete-dialog.html',
                    controller: 'FileUploadDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['FileUpload', function(FileUpload) {
                            return FileUpload.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('file-upload', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

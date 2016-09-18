(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('image-upload', {
            parent: 'entity',
            url: '/image-upload?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.imageUpload.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/image-upload/image-uploads.html',
                    controller: 'ImageUploadController',
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
                    $translatePartialLoader.addPart('imageUpload');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('image-upload-detail', {
            parent: 'entity',
            url: '/image-upload/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.imageUpload.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/image-upload/image-upload-detail.html',
                    controller: 'ImageUploadDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('imageUpload');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ImageUpload', function($stateParams, ImageUpload) {
                    return ImageUpload.get({id : $stateParams.id});
                }]
            }
        })
        .state('image-upload.new', {
            parent: 'image-upload',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image-upload/image-upload-dialog.html',
                    controller: 'ImageUploadDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                imageField: null,
                                imageFieldContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('image-upload', null, { reload: true });
                }, function() {
                    $state.go('image-upload');
                });
            }]
        })
        .state('image-upload.edit', {
            parent: 'image-upload',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image-upload/image-upload-dialog.html',
                    controller: 'ImageUploadDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ImageUpload', function(ImageUpload) {
                            return ImageUpload.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('image-upload', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('image-upload.delete', {
            parent: 'image-upload',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image-upload/image-upload-delete-dialog.html',
                    controller: 'ImageUploadDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ImageUpload', function(ImageUpload) {
                            return ImageUpload.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('image-upload', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

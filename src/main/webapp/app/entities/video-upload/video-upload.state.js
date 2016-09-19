(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('video-upload', {
            parent: 'entity',
            url: '/video-upload?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.videoUpload.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/video-upload/video-uploads.html',
                    controller: 'VideoUploadController',
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
                    $translatePartialLoader.addPart('videoUpload');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('video-upload-detail', {
            parent: 'entity',
            url: '/video-upload/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.videoUpload.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/video-upload/video-upload-detail.html',
                    controller: 'VideoUploadDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('videoUpload');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'VideoUpload', function($stateParams, VideoUpload) {
                    return VideoUpload.get({id : $stateParams.id});
                }]
            }
        })
        .state('video-upload.new', {
            parent: 'video-upload',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/video-upload/video-upload-dialog.html',
                    controller: 'VideoUploadDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                videoField: null,
                                videoFieldContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('video-upload', null, { reload: true });
                }, function() {
                    $state.go('video-upload');
                });
            }]
        })
        .state('video-upload.edit', {
            parent: 'video-upload',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/video-upload/video-upload-dialog.html',
                    controller: 'VideoUploadDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['VideoUpload', function(VideoUpload) {
                            return VideoUpload.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('video-upload', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('video-upload.delete', {
            parent: 'video-upload',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/video-upload/video-upload-delete-dialog.html',
                    controller: 'VideoUploadDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['VideoUpload', function(VideoUpload) {
                            return VideoUpload.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('video-upload', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

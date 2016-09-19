(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('audio-upload', {
            parent: 'entity',
            url: '/audio-upload?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.audioUpload.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/audio-upload/audio-uploads.html',
                    controller: 'AudioUploadController',
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
                    $translatePartialLoader.addPart('audioUpload');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('audio-upload-detail', {
            parent: 'entity',
            url: '/audio-upload/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'acmeSiteApp.audioUpload.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/audio-upload/audio-upload-detail.html',
                    controller: 'AudioUploadDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('audioUpload');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'AudioUpload', function($stateParams, AudioUpload) {
                    return AudioUpload.get({id : $stateParams.id});
                }]
            }
        })
        .state('audio-upload.new', {
            parent: 'audio-upload',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/audio-upload/audio-upload-dialog.html',
                    controller: 'AudioUploadDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                audioField: null,
                                audioFieldContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('audio-upload', null, { reload: true });
                }, function() {
                    $state.go('audio-upload');
                });
            }]
        })
        .state('audio-upload.edit', {
            parent: 'audio-upload',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/audio-upload/audio-upload-dialog.html',
                    controller: 'AudioUploadDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AudioUpload', function(AudioUpload) {
                            return AudioUpload.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('audio-upload', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('audio-upload.delete', {
            parent: 'audio-upload',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/audio-upload/audio-upload-delete-dialog.html',
                    controller: 'AudioUploadDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['AudioUpload', function(AudioUpload) {
                            return AudioUpload.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('audio-upload', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

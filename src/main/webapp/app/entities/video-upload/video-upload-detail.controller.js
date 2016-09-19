(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('VideoUploadDetailController', VideoUploadDetailController);

    VideoUploadDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'VideoUpload', 'User'];

    function VideoUploadDetailController($scope, $rootScope, $stateParams, DataUtils, entity, VideoUpload, User) {
        var vm = this;
        vm.videoUpload = entity;
        
        var unsubscribe = $rootScope.$on('acmeSiteApp:videoUploadUpdate', function(event, result) {
            vm.videoUpload = result;
        });
        $scope.$on('$destroy', unsubscribe);

        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
    }
})();

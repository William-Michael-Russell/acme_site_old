(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('AudioUploadDetailController', AudioUploadDetailController);

    AudioUploadDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'AudioUpload', 'User'];

    function AudioUploadDetailController($scope, $rootScope, $stateParams, DataUtils, entity, AudioUpload, User) {
        var vm = this;
        vm.audioUpload = entity;
        
        var unsubscribe = $rootScope.$on('acmeSiteApp:audioUploadUpdate', function(event, result) {
            vm.audioUpload = result;
        });
        $scope.$on('$destroy', unsubscribe);

        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
    }
})();

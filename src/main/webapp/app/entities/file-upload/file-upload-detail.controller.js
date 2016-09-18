(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('FileUploadDetailController', FileUploadDetailController);

    FileUploadDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'FileUpload', 'User'];

    function FileUploadDetailController($scope, $rootScope, $stateParams, DataUtils, entity, FileUpload, User) {
        var vm = this;
        vm.fileUpload = entity;
        
        var unsubscribe = $rootScope.$on('acmeSiteApp:fileUploadUpdate', function(event, result) {
            vm.fileUpload = result;
        });
        $scope.$on('$destroy', unsubscribe);

        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
    }
})();

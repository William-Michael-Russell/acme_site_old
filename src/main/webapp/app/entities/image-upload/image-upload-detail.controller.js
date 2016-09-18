(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('ImageUploadDetailController', ImageUploadDetailController);

    ImageUploadDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'ImageUpload', 'User'];

    function ImageUploadDetailController($scope, $rootScope, $stateParams, DataUtils, entity, ImageUpload, User) {
        var vm = this;
        vm.imageUpload = entity;
        
        var unsubscribe = $rootScope.$on('acmeSiteApp:imageUploadUpdate', function(event, result) {
            vm.imageUpload = result;
        });
        $scope.$on('$destroy', unsubscribe);

        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
    }
})();

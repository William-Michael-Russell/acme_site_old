(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('VideoUploadDeleteController',VideoUploadDeleteController);

    VideoUploadDeleteController.$inject = ['$uibModalInstance', 'entity', 'VideoUpload'];

    function VideoUploadDeleteController($uibModalInstance, entity, VideoUpload) {
        var vm = this;
        vm.videoUpload = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            VideoUpload.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

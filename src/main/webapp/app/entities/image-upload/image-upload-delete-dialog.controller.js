(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('ImageUploadDeleteController',ImageUploadDeleteController);

    ImageUploadDeleteController.$inject = ['$uibModalInstance', 'entity', 'ImageUpload'];

    function ImageUploadDeleteController($uibModalInstance, entity, ImageUpload) {
        var vm = this;
        vm.imageUpload = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            ImageUpload.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

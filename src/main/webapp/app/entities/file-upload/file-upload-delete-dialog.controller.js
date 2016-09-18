(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('FileUploadDeleteController',FileUploadDeleteController);

    FileUploadDeleteController.$inject = ['$uibModalInstance', 'entity', 'FileUpload'];

    function FileUploadDeleteController($uibModalInstance, entity, FileUpload) {
        var vm = this;
        vm.fileUpload = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            FileUpload.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

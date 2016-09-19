(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('AudioUploadDeleteController',AudioUploadDeleteController);

    AudioUploadDeleteController.$inject = ['$uibModalInstance', 'entity', 'AudioUpload'];

    function AudioUploadDeleteController($uibModalInstance, entity, AudioUpload) {
        var vm = this;
        vm.audioUpload = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            AudioUpload.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

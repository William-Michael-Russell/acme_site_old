(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('AlpaNumericInputFieldDeleteController',AlpaNumericInputFieldDeleteController);

    AlpaNumericInputFieldDeleteController.$inject = ['$uibModalInstance', 'entity', 'AlpaNumericInputField'];

    function AlpaNumericInputFieldDeleteController($uibModalInstance, entity, AlpaNumericInputField) {
        var vm = this;
        vm.alpaNumericInputField = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            AlpaNumericInputField.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

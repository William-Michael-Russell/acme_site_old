(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('NumericInputFieldDeleteController',NumericInputFieldDeleteController);

    NumericInputFieldDeleteController.$inject = ['$uibModalInstance', 'entity', 'NumericInputField'];

    function NumericInputFieldDeleteController($uibModalInstance, entity, NumericInputField) {
        var vm = this;
        vm.numericInputField = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            NumericInputField.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

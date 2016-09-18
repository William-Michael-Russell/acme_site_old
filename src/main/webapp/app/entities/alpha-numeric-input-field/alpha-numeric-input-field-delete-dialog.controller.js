(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('AlphaNumericInputFieldDeleteController',AlphaNumericInputFieldDeleteController);

    AlphaNumericInputFieldDeleteController.$inject = ['$uibModalInstance', 'entity', 'AlphaNumericInputField'];

    function AlphaNumericInputFieldDeleteController($uibModalInstance, entity, AlphaNumericInputField) {
        var vm = this;
        vm.alphaNumericInputField = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            AlphaNumericInputField.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

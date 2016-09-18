(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('PhoneNumberInputFieldDeleteController',PhoneNumberInputFieldDeleteController);

    PhoneNumberInputFieldDeleteController.$inject = ['$uibModalInstance', 'entity', 'PhoneNumberInputField'];

    function PhoneNumberInputFieldDeleteController($uibModalInstance, entity, PhoneNumberInputField) {
        var vm = this;
        vm.phoneNumberInputField = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            PhoneNumberInputField.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

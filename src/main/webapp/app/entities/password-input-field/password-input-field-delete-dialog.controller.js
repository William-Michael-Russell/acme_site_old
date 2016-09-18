(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('PasswordInputFieldDeleteController',PasswordInputFieldDeleteController);

    PasswordInputFieldDeleteController.$inject = ['$uibModalInstance', 'entity', 'PasswordInputField'];

    function PasswordInputFieldDeleteController($uibModalInstance, entity, PasswordInputField) {
        var vm = this;
        vm.passwordInputField = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            PasswordInputField.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

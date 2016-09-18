(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('EmailInputFieldDeleteController',EmailInputFieldDeleteController);

    EmailInputFieldDeleteController.$inject = ['$uibModalInstance', 'entity', 'EmailInputField'];

    function EmailInputFieldDeleteController($uibModalInstance, entity, EmailInputField) {
        var vm = this;
        vm.emailInputField = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            EmailInputField.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

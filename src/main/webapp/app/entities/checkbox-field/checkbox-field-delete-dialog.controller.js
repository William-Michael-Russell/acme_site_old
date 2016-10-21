(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('CheckboxFieldDeleteController',CheckboxFieldDeleteController);

    CheckboxFieldDeleteController.$inject = ['$uibModalInstance', 'entity', 'CheckboxField'];

    function CheckboxFieldDeleteController($uibModalInstance, entity, CheckboxField) {
        var vm = this;
        vm.checkboxField = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            CheckboxField.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

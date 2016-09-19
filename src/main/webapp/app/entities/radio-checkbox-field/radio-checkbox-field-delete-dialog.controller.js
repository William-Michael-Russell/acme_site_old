(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('RadioCheckboxFieldDeleteController',RadioCheckboxFieldDeleteController);

    RadioCheckboxFieldDeleteController.$inject = ['$uibModalInstance', 'entity', 'RadioCheckboxField'];

    function RadioCheckboxFieldDeleteController($uibModalInstance, entity, RadioCheckboxField) {
        var vm = this;
        vm.radioCheckboxField = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            RadioCheckboxField.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

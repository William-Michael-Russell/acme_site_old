(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('DropDownFieldDeleteController',DropDownFieldDeleteController);

    DropDownFieldDeleteController.$inject = ['$uibModalInstance', 'entity', 'DropDownField'];

    function DropDownFieldDeleteController($uibModalInstance, entity, DropDownField) {
        var vm = this;
        vm.dropDownField = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            DropDownField.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

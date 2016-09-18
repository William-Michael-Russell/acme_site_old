(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('Text_inputsDeleteController',Text_inputsDeleteController);

    Text_inputsDeleteController.$inject = ['$uibModalInstance', 'entity', 'Text_inputs'];

    function Text_inputsDeleteController($uibModalInstance, entity, Text_inputs) {
        var vm = this;
        vm.text_inputs = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Text_inputs.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

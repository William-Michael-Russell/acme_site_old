(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('Text_inputsDialogController', Text_inputsDialogController);

    Text_inputsDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Text_inputs'];

    function Text_inputsDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Text_inputs) {
        var vm = this;
        vm.text_inputs = entity;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('acmeSiteApp:text_inputsUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.text_inputs.id !== null) {
                Text_inputs.update(vm.text_inputs, onSaveSuccess, onSaveError);
            } else {
                Text_inputs.save(vm.text_inputs, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();

(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('RadioCheckboxFieldDialogController', RadioCheckboxFieldDialogController);

    RadioCheckboxFieldDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'RadioCheckboxField', 'User'];

    function RadioCheckboxFieldDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, RadioCheckboxField, User) {
        var vm = this;
        vm.radioCheckboxField = entity;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('acmeSiteApp:radioCheckboxFieldUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.radioCheckboxField.id !== null) {
                RadioCheckboxField.update(vm.radioCheckboxField, onSaveSuccess, onSaveError);
            } else {
                RadioCheckboxField.save(vm.radioCheckboxField, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();




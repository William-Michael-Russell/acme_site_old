(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('AlpaNumericInputFieldDialogController', AlpaNumericInputFieldDialogController);

    AlpaNumericInputFieldDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'AlpaNumericInputField', 'User'];

    function AlpaNumericInputFieldDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, AlpaNumericInputField, User) {
        var vm = this;
        vm.alpaNumericInputField = entity;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('acmeSiteApp:alpaNumericInputFieldUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.alpaNumericInputField.id !== null) {
                AlpaNumericInputField.update(vm.alpaNumericInputField, onSaveSuccess, onSaveError);
            } else {
                AlpaNumericInputField.save(vm.alpaNumericInputField, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();

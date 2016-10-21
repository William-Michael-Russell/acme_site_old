(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('DropDownFieldDialogController', DropDownFieldDialogController);

    DropDownFieldDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'DropDownField', 'User'];

    function DropDownFieldDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, DropDownField, User) {
        var vm = this;
        vm.dropDownField = entity;
        vm.users = User.query();

        $scope.weapons = [
            {model : "Feather Anvil", price : "$2,400"},
            {model : "Fiat Rocket X500", price : "$Fiat Rocket X500"},
            {model : "Diamond Rope", price : "$Orange"},
            {model : "Plastic Shotgun", price : "$25"},
            {model : "Paper Sword %20 OFF!", price : "$45"}
        ];


        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('acmeSiteApp:dropDownFieldUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.dropDownField.id !== null) {
                DropDownField.update(vm.dropDownField, onSaveSuccess, onSaveError);
            } else {
                DropDownField.save(vm.dropDownField, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();

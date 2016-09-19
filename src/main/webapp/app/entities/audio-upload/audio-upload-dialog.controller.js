(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('AudioUploadDialogController', AudioUploadDialogController);

    AudioUploadDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'AudioUpload', 'User'];

    function AudioUploadDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, AudioUpload, User) {
        var vm = this;
        vm.audioUpload = entity;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('acmeSiteApp:audioUploadUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.audioUpload.id !== null) {
                AudioUpload.update(vm.audioUpload, onSaveSuccess, onSaveError);
            } else {
                AudioUpload.save(vm.audioUpload, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.setAudioField = function ($file, audioUpload) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        audioUpload.audioField = base64Data;
                        audioUpload.audioFieldContentType = $file.type;
                    });
                });
            }
        };

        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
    }
})();

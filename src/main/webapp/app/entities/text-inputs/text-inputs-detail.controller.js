(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .controller('Text_inputsDetailController', Text_inputsDetailController);

    Text_inputsDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Text_inputs'];

    function Text_inputsDetailController($scope, $rootScope, $stateParams, entity, Text_inputs) {
        var vm = this;
        vm.text_inputs = entity;
        
        var unsubscribe = $rootScope.$on('acmeSiteApp:text_inputsUpdate', function(event, result) {
            vm.text_inputs = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();

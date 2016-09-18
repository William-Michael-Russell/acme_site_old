'use strict';

describe('Controller Tests', function() {

    describe('Text_inputs Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockText_inputs;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockText_inputs = jasmine.createSpy('MockText_inputs');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Text_inputs': MockText_inputs
            };
            createController = function() {
                $injector.get('$controller')("Text_inputsDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'acmeSiteApp:text_inputsUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

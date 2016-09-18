'use strict';

describe('Controller Tests', function() {

    describe('NumericInputField Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockNumericInputField, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockNumericInputField = jasmine.createSpy('MockNumericInputField');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'NumericInputField': MockNumericInputField,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("NumericInputFieldDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'acmeSiteApp:numericInputFieldUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

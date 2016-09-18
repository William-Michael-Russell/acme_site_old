'use strict';

describe('Controller Tests', function() {

    describe('AlphaNumericInputField Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockAlphaNumericInputField, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockAlphaNumericInputField = jasmine.createSpy('MockAlphaNumericInputField');
            MockUser = jasmine.createSpy('MockUser');


            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'AlphaNumericInputField': MockAlphaNumericInputField,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("AlphaNumericInputFieldDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'acmeSiteApp:alphaNumericInputFieldUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

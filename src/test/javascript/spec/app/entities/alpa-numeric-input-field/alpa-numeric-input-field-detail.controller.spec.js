'use strict';

describe('Controller Tests', function() {

    describe('AlpaNumericInputField Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockAlpaNumericInputField, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockAlpaNumericInputField = jasmine.createSpy('MockAlpaNumericInputField');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'AlpaNumericInputField': MockAlpaNumericInputField,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("AlpaNumericInputFieldDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'acmeSiteApp:alpaNumericInputFieldUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

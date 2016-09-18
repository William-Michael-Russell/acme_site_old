'use strict';

describe('Controller Tests', function() {

    describe('PhoneNumberInputField Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPhoneNumberInputField, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPhoneNumberInputField = jasmine.createSpy('MockPhoneNumberInputField');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'PhoneNumberInputField': MockPhoneNumberInputField,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("PhoneNumberInputFieldDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'acmeSiteApp:phoneNumberInputFieldUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

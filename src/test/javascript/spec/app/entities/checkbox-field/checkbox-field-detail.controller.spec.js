'use strict';

describe('Controller Tests', function() {

    describe('CheckboxField Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockCheckboxField, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockCheckboxField = jasmine.createSpy('MockCheckboxField');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'CheckboxField': MockCheckboxField,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("CheckboxFieldDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'acmeSiteApp:checkboxFieldUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

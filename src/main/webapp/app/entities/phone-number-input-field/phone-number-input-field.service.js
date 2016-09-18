(function() {
    'use strict';
    angular
        .module('acmeSiteApp')
        .factory('PhoneNumberInputField', PhoneNumberInputField);

    PhoneNumberInputField.$inject = ['$resource'];

    function PhoneNumberInputField ($resource) {
        var resourceUrl =  'api/phone-number-input-fields/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();

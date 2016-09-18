(function() {
    'use strict';
    angular
        .module('acmeSiteApp')
        .factory('AlphaNumericInputField', AlphaNumericInputField);

    AlphaNumericInputField.$inject = ['$resource'];

    function AlphaNumericInputField ($resource) {
        var resourceUrl =  'api/alpha-numeric-input-fields/:id';

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

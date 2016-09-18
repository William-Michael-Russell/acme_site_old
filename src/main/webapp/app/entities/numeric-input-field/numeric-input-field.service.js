(function() {
    'use strict';
    angular
        .module('acmeSiteApp')
        .factory('NumericInputField', NumericInputField);

    NumericInputField.$inject = ['$resource'];

    function NumericInputField ($resource) {
        var resourceUrl =  'api/numeric-input-fields/:id';

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

(function() {
    'use strict';
    angular
        .module('acmeSiteApp')
        .factory('AlpaNumericInputField', AlpaNumericInputField);

    AlpaNumericInputField.$inject = ['$resource'];

    function AlpaNumericInputField ($resource) {
        var resourceUrl =  'api/alpa-numeric-input-fields/:id';

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

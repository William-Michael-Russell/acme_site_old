(function() {
    'use strict';
    angular
        .module('acmeSiteApp')
        .factory('RadioCheckboxField', RadioCheckboxField);

    RadioCheckboxField.$inject = ['$resource'];

    function RadioCheckboxField ($resource) {
        var resourceUrl =  'api/radio-checkbox-fields/:id';

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

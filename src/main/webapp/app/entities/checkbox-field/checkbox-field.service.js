(function() {
    'use strict';
    angular
        .module('acmeSiteApp')
        .factory('CheckboxField', CheckboxField);

    CheckboxField.$inject = ['$resource'];

    function CheckboxField ($resource) {
        var resourceUrl =  'api/checkbox-fields/:id';

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

(function() {
    'use strict';
    angular
        .module('acmeSiteApp')
        .factory('EmailInputField', EmailInputField);

    EmailInputField.$inject = ['$resource'];

    function EmailInputField ($resource) {
        var resourceUrl =  'api/email-input-fields/:id';

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

(function() {
    'use strict';
    angular
        .module('acmeSiteApp')
        .factory('PasswordInputField', PasswordInputField);

    PasswordInputField.$inject = ['$resource'];

    function PasswordInputField ($resource) {
        var resourceUrl =  'api/password-input-fields/:id';

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

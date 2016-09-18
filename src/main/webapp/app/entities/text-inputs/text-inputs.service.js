(function() {
    'use strict';
    angular
        .module('acmeSiteApp')
        .factory('Text_inputs', Text_inputs);

    Text_inputs.$inject = ['$resource'];

    function Text_inputs ($resource) {
        var resourceUrl =  'api/text-inputs/:id';

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

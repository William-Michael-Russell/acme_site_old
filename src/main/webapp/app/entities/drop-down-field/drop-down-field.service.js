(function() {
    'use strict';
    angular
        .module('acmeSiteApp')
        .factory('DropDownField', DropDownField);

    DropDownField.$inject = ['$resource'];

    function DropDownField ($resource) {
        var resourceUrl =  'api/drop-down-fields/:id';

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

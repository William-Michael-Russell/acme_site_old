(function() {
    'use strict';
    angular
        .module('acmeSiteApp')
        .factory('FileUpload', FileUpload);

    FileUpload.$inject = ['$resource'];

    function FileUpload ($resource) {
        var resourceUrl =  'api/file-uploads/:id';

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

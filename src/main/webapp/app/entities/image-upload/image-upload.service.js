(function() {
    'use strict';
    angular
        .module('acmeSiteApp')
        .factory('ImageUpload', ImageUpload);

    ImageUpload.$inject = ['$resource'];

    function ImageUpload ($resource) {
        var resourceUrl =  'api/image-uploads/:id';

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

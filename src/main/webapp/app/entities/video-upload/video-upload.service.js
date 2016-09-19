(function() {
    'use strict';
    angular
        .module('acmeSiteApp')
        .factory('VideoUpload', VideoUpload);

    VideoUpload.$inject = ['$resource'];

    function VideoUpload ($resource) {
        var resourceUrl =  'api/video-uploads/:id';

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

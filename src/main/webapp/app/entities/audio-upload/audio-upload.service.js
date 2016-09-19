(function() {
    'use strict';
    angular
        .module('acmeSiteApp')
        .factory('AudioUpload', AudioUpload);

    AudioUpload.$inject = ['$resource'];

    function AudioUpload ($resource) {
        var resourceUrl =  'api/audio-uploads/:id';

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

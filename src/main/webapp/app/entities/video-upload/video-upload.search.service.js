(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .factory('VideoUploadSearch', VideoUploadSearch);

    VideoUploadSearch.$inject = ['$resource'];

    function VideoUploadSearch($resource) {
        var resourceUrl =  'api/_search/video-uploads/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

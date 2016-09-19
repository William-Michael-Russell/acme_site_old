(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .factory('AudioUploadSearch', AudioUploadSearch);

    AudioUploadSearch.$inject = ['$resource'];

    function AudioUploadSearch($resource) {
        var resourceUrl =  'api/_search/audio-uploads/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

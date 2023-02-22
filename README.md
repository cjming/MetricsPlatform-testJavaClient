# Wikimedia Metrics Platform Client - Java

This is an end-to-end testing implementation of the Metrics Platform Java Client library using local EventLogging.

See https://wikitech.wikimedia.org/wiki/Metrics_Platform for high-level detail about the Metrics Platform project.

## Getting Started

These instructions make the following assumptions:
- A working [MediaWiki](https://www.mediawiki.org/wiki/MediaWiki) installation is set up for local testing.
  - Please follow these instructions for setting up a [local MediaWiki development environment](https://gerrit.wikimedia.org/g/mediawiki/core/+/HEAD/DEVELOPERS.md) using Docker. 
- Relevant [EventLogging extensions](https://www.mediawiki.org/wiki/Extension:EventLogging) are installed.
  - Please follow these instructions for setting up [local Event Logging](https://www.mediawiki.org/wiki/MediaWiki-Docker/Configuration_recipes/EventLogging).
- The [Metrics Platform Java Client library](https://github.com/wikimedia/mediawiki-libs-metrics-platform/tree/master/java)
is packaged as a dependency for this repository.

This repository uses [Maven](https://maven.apache.org/index.html) as a build tool.

This repository contains all the necessary files to run end-to-end testing in a local development environment.

### Configuration

To test the Java client using local event logging, the Metrics Platform Java Client jar file must be added as a
dependency to this project. A packaged version is included inside the `dependencies` directory.

Note that this packaged version of the [MP Java Client](https://github.com/wikimedia/mediawiki-libs-metrics-platform/tree/master/java)
does not reflect the latest main branch. It has been optimized to use [localhost as the DestinationEventService](https://github.com/wikimedia/mediawiki-libs-metrics-platform/blob/master/java/src/main/java/org/wikimedia/metrics_platform/config/DestinationEventService.java#L26)
for submitting events.

In the case of using an IDE like [IntelliJ](https://www.jetbrains.com/idea/),
dependencies can be added by navigating to File > Project Structure > Modules and adding the jar file.

### Testing

Local EventLogging can be initiated by running in terminal:

```
docker compose up --force-recreate eventlogging
```

Once the MP Java Client jar file has been added as a dependency and the project has been built, run `TestJava::main`.

The following messages should be displayed in the testing console:
```
Testing the Java client:
{"$schema":"/analytics/mediawiki/client/metrics_event/1.1.0","name":"test_java_event","dt":"2023-02-22T01:23:38Z","custom_data":{"action":{"data_type":"string","value":"scroll"}},"meta":{"stream":"test.stream","domain":"en.wikipedia.org"},"agent":{"app_install_id":"ffffffff-ffff-ffff-ffff-ffffffffffff","client_platform":"mediawiki_js","client_platform_family":"desktop_browser"},"page":{"id":30715282,"title":"Dark_Souls","namespace":0,"namespace_name":"","revision_id":1084793259,"content_language":"en","is_redirect":false,"user_groups_allowed_to_move":[],"user_groups_allowed_to_edit":[]},"mediawiki":{"skin":"vector","version":"1.39.0-wmf.8","is_production":true,"is_debug_mode":false,"database":"enwiki","site_content_language":"en"},"performer":{"is_logged_in":false,"id":0,"session_id":"eeeeeeeeeeeeeeeeeeee","pageview_id":"eeeeeeeeeeeeeeeeeeee","groups":["*"],"is_bot":false,"language":"en","can_probably_edit_page":true}}
Posted data to local event logging!

Event submitted using the Java client :)
```

And the terminal running the local EventLogging stream should validate the test event:
```
mediawiki-eventlogging-1  | [2023-02-22T01:23:39.113Z] TRACE: service-runner/27 on c53e3f496c66: Set meta.dt in event e0676c88-fca4-43eb-89bf-04ce53d4862b of schema at /analytics/mediawiki/client/metrics_event/1.1.0 destined to stream test.stream
mediawiki-eventlogging-1  | [2023-02-22T01:23:39.113Z] TRACE: service-runner/27 on c53e3f496c66: Set meta.dt in event e0676c88-fca4-43eb-89bf-04ce53d4862b of schema at /analytics/mediawiki/client/metrics_event/1.1.0 destined to stream test.stream
mediawiki-eventlogging-1  | [2023-02-22T01:23:39.114Z] TRACE: service-runner/27 on c53e3f496c66: Set meta.request_id in event e0676c88-fca4-43eb-89bf-04ce53d4862b of schema at /analytics/mediawiki/client/metrics_event/1.1.0 destined to stream test.stream
mediawiki-eventlogging-1  | [2023-02-22T01:23:39.114Z] TRACE: service-runner/27 on c53e3f496c66: Set http.request_headers.user-agent in event e0676c88-fca4-43eb-89bf-04ce53d4862b of schema at /analytics/mediawiki/client/metrics_event/1.1.0 destined to stream test.stream
mediawiki-eventlogging-1  | [2023-02-22T01:23:39.116Z] TRACE: service-runner/27 on c53e3f496c66: event passed schema validation, producing...
mediawiki-eventlogging-1  |     event: {
mediawiki-eventlogging-1  |       "$schema": "/analytics/mediawiki/client/metrics_event/1.1.0",
mediawiki-eventlogging-1  |       "name": "test_java_event",
mediawiki-eventlogging-1  |       "dt": "2023-02-22T01:23:38Z",
mediawiki-eventlogging-1  |       "custom_data": {
mediawiki-eventlogging-1  |         "action": {
mediawiki-eventlogging-1  |           "data_type": "string",
mediawiki-eventlogging-1  |           "value": "scroll"
mediawiki-eventlogging-1  |         }
mediawiki-eventlogging-1  |       },
mediawiki-eventlogging-1  |       "meta": {
mediawiki-eventlogging-1  |         "stream": "test.stream",
mediawiki-eventlogging-1  |         "domain": "en.wikipedia.org",
mediawiki-eventlogging-1  |         "id": "e0676c88-fca4-43eb-89bf-04ce53d4862b",
mediawiki-eventlogging-1  |         "dt": "2023-02-22T01:23:39.113Z",
mediawiki-eventlogging-1  |         "request_id": "89202860-b24f-11ed-91a1-013038519d35"
mediawiki-eventlogging-1  |       },
mediawiki-eventlogging-1  |       "agent": {
mediawiki-eventlogging-1  |         "app_install_id": "ffffffff-ffff-ffff-ffff-ffffffffffff",
mediawiki-eventlogging-1  |         "client_platform": "mediawiki_js",
mediawiki-eventlogging-1  |         "client_platform_family": "desktop_browser"
mediawiki-eventlogging-1  |       },
mediawiki-eventlogging-1  |       "page": {
mediawiki-eventlogging-1  |         "id": 30715282,
mediawiki-eventlogging-1  |         "title": "Dark_Souls",
mediawiki-eventlogging-1  |         "namespace": 0,
mediawiki-eventlogging-1  |         "namespace_name": "",
mediawiki-eventlogging-1  |         "revision_id": 1084793259,
mediawiki-eventlogging-1  |         "content_language": "en",
mediawiki-eventlogging-1  |         "is_redirect": false,
mediawiki-eventlogging-1  |         "user_groups_allowed_to_move": [],
mediawiki-eventlogging-1  |         "user_groups_allowed_to_edit": []
mediawiki-eventlogging-1  |       },
mediawiki-eventlogging-1  |       "mediawiki": {
mediawiki-eventlogging-1  |         "skin": "vector",
mediawiki-eventlogging-1  |         "version": "1.39.0-wmf.8",
mediawiki-eventlogging-1  |         "is_production": true,
mediawiki-eventlogging-1  |         "is_debug_mode": false,
mediawiki-eventlogging-1  |         "database": "enwiki",
mediawiki-eventlogging-1  |         "site_content_language": "en"
mediawiki-eventlogging-1  |       },
mediawiki-eventlogging-1  |       "performer": {
mediawiki-eventlogging-1  |         "is_logged_in": false,
mediawiki-eventlogging-1  |         "id": 0,
mediawiki-eventlogging-1  |         "session_id": "eeeeeeeeeeeeeeeeeeee",
mediawiki-eventlogging-1  |         "pageview_id": "eeeeeeeeeeeeeeeeeeee",
mediawiki-eventlogging-1  |         "groups": [
mediawiki-eventlogging-1  |           "*"
mediawiki-eventlogging-1  |         ],
mediawiki-eventlogging-1  |         "is_bot": false,
mediawiki-eventlogging-1  |         "language": "en",
mediawiki-eventlogging-1  |         "can_probably_edit_page": true
mediawiki-eventlogging-1  |       },
mediawiki-eventlogging-1  |       "http": {
mediawiki-eventlogging-1  |         "request_headers": {
mediawiki-eventlogging-1  |           "user-agent": "Java/1.8.0_345"
mediawiki-eventlogging-1  |         }
mediawiki-eventlogging-1  |       }
mediawiki-eventlogging-1  |     }
mediawiki-eventlogging-1  | [2023-02-22T01:23:39.117Z] DEBUG: eventgate-devserver/27 on c53e3f496c66: All 1 out of 1 events were accepted. (levelPath=debug/events, request_id=89202860-b24f-11ed-91a1-013038519d35)
mediawiki-eventlogging-1  |     request: {
mediawiki-eventlogging-1  |       "url": "/v1/events",
mediawiki-eventlogging-1  |       "headers": {
mediawiki-eventlogging-1  |         "content-type": "application/json",
mediawiki-eventlogging-1  |         "user-agent": "Java/1.8.0_345",
mediawiki-eventlogging-1  |         "content-length": "932",
mediawiki-eventlogging-1  |         "x-request-id": "89202860-b24f-11ed-91a1-013038519d35"
mediawiki-eventlogging-1  |       },
mediawiki-eventlogging-1  |       "method": "POST",
mediawiki-eventlogging-1  |       "params": {
mediawiki-eventlogging-1  |         "0": "/v1/events"
mediawiki-eventlogging-1  |       },
mediawiki-eventlogging-1  |       "query": {},
mediawiki-eventlogging-1  |       "remoteAddress": "172.18.0.1",
mediawiki-eventlogging-1  |       "remotePort": 54112
mediawiki-eventlogging-1  |     }
```

# Netflix Music Service API

## Overview

Core is built on Spring boot, the basic job of this microservice is to provide rest apis to get artist details using their musicbrainz id.

# Spring boot application #

The project is based on a small web service which uses the following technologies:

* Java
* Spring Boot (latest)
* Maven

* The architecture of the web service is built with the following components:
    * **Controller** - Implements the processing logic of the web service, parsing of parameters and redirecting the request to relevant service.
    * **Service** - Implements the business logic and build the response.
    * **Repository** - Implements the logic to handle external API calls, digest the response and pass it to the business layer. 
    * **Modals** - Functional objects for handling internal and external data.

* Key components used -
  * **RetryTemplate** - Retry template is configured to re-attempt external calls in case of HTTPServerErrorException, maximum upto 3 times.
  * **RedirectionStrategy** - Redirection strategy is implemented to handle covert art API redirection.
  * **Caching** - Caching is implemented to improve response time for repeated calls.
  * **Async Calls** - Cover art api calls are made async to improve latency.
  * **Response Exception Handling** - Using _@Controller_ advice global exception handling is implemented to return proper response for exceptions.

## How to build the app

Please execute below commands in order to build the application.
```shell
$mvn clean install
```

## How to start the app

You should be able to start the example application by executing main class, which starts a webserver on port
[8080](http://localhost:8080).
You can also execute below command to start the application.

````shell
$mvn spring-boot:run
````

## How to test the API ##

Below are few MBID's to test the API's -

* f27ec8db-af05-4f36-916e-3d57f91ecf5e 
* 65f4f0c5-ef9e-490c-aee3-909e7ae6b2ab

## Prerequisite
* Maven
* JAVA16
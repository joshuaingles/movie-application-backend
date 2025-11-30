# Overview

This is a SpringBoot-based collection of REST endpoints for a Movie CRUD API.
This API currently supports the following functions:

 -- Create a Movie resource
    - Path: POST /api/v1/movies
 -- Create Movie resources
    - Path: POST /api/v1/movies/bulk
 -- Get a Movie
    - Path: GET /api/v1/movies/{id}
 -- Get a list of Movies, optional filter criteria of Release Year and/or Genre
    - Path: GET /api/v1/movies
 -- Update a Movie
    - Path: PATCH /api/v1/movies/{id}
 -- Delete a Movie
    - Path: DELETE /api/v1/movies/{id}

# Swagger UI

Navigate to http://localhost:8080/swagger-ui.html to access Swagger documentation.

# Postman

Use this button to fork the most up to date Postman collection for this API:

[<img src="https://run.pstmn.io/button.svg" alt="Run In Postman" style="width: 128px; height: 32px;">](https://god.gw.postman.com/run-collection/49295948-224511c1-18c9-4f1f-9867-6b40d575f324?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D49295948-224511c1-18c9-4f1f-9867-6b40d575f324%26entityType%3Dcollection%26workspaceId%3D88e023de-dae4-4824-8bae-c2b384129d63)

or 

see Postman collection in resources.

## Prerequisites

Please ensure you have the following installed:

- Java 17+
- Maven

## Steps for Setup

1. Clone this repository.
2. Open the project in your preferred IDE.
3. Build the project using Maven.
4. Run the tests to ensure everything is functioning properly.


## To Do
1. ~~Create CRUD endpoints~~
2. ~~Create Postman collection~~
3. ~~Create Documentation~~
4. Create tests
5. Expand error handling and reporting

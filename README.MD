# Declarative API Example

This example application for a Declarative API has been created in context of the talk about [Declarative APIs](declarative.pdf).

The heart of this application is an endpoint that takes the desired state and a request to it looks like this:

```bash
curl --location --request PUT 'http://localhost:8080/objects?objectCollection=my-test' \
--header 'Content-Type: application/json' \
--data-raw '[
    {
        "kind": "user",
        "apiVersion": "v1",
        "metadata": {
            "userId": "test-user"
        },
        "spec": {
            "firstName": "Test",
            "lastName": "User",
            "email": "test@user.de"
        }
    },
    {
        "kind": "user",
        "apiVersion": "v1",
        "metadata": {
            "userId": "test-user2"
        },
        "spec": {
            "firstName": "Test",
            "lastName": "User2",
            "email": "test2@user.de"
        }
    },
    {
        "kind": "group",
        "apiVersion": "v1",
        "metadata": {
            "name": "my-group"
        },
        "spec": {
            "members": [
                "test-user2",
                "not-existing"
            ]
        }
    }
]'
```
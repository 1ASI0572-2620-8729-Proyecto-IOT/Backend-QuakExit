@F01
Feature: User registration and login
  As a resident
  I want to register an account and log in
  So that I can manage my QuakExit system

  Scenario: Successful registration
    Given a resident with an email that is not registered
    When the resident sends a registration request with valid data
    Then the system responds with status 201
    And the account is created

  Scenario: Duplicate email
    Given a resident with an email that is already registered
    When the resident sends a registration request
    Then the system responds with status 409

  Scenario: Successful login
    Given a registered resident
    When the resident sends a login request with correct credentials
    Then the system responds with status 200 and an access token

  Scenario: Invalid credentials
    Given a registered resident
    When the resident sends a login request with a wrong password
    Then the system responds with status 401

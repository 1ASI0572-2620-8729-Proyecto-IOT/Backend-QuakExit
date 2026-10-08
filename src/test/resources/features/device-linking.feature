@F02
Feature: Device linking
  As a resident
  I want to link my QuakExit Hub to my account
  So that I can control it from the application

  Scenario: Successful linking
    Given an authenticated resident and an unlinked device code
    When the resident sends a link request with the device code
    Then the system responds with status 201
    And the device is associated with the resident

  Scenario: Device already linked
    Given an authenticated resident and a device code that is already linked
    When the resident sends a link request with the device code
    Then the system responds with status 409

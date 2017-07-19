Feature: Testing Insert and Search features

  Background:
    Given all existing users are removed from the system
    Given the following users are present in the system
      | firstName | lastName | email                | password |
      | John      | Doe      | john.doe@sytac.io    | test     |
      | John      | Wayne    | john.wayne@sytac.io  | test     |
      | Roger     | Moore    | roger.moore@sytac.io | test     |
      | Jane      | Fonda    | jane.fonda@sytac.io  | test     |

  Scenario: Inserting a new User then searching for this user by email should return the same record
    When the following users are registered
      | firstName | lastName | email                  | password |
      | Mickey    | Rourke   | mickey.rourke@sytac.io | test     |

    Then there should be 5 users present in the system

    Then searching using findByEmail REST endpoint for parameter email with value mickey.rourke@sytac.io should return the following records
      | firstName | lastName | email                  | password |
      | Mickey    | Rourke   | mickey.rourke@sytac.io | test     |

    # Implementing the following features:
    # - Search by firstName (this should return more than one results)
    # - Rejecting registration if email already exists
    # - Rejecting registration if data is not sane



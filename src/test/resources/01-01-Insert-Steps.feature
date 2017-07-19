Feature: Insert

  Background:
    Given all existing users are removed from the system
    Given the following users are present in the system
      | firstName | lastName | email                | password |
      | John      | Doe      | john.doe@sytac.io    | test     |
      | John      | Wayne    | john.wayne@sytac.io  | test     |
      | Roger     | Moore    | roger.moore@sytac.io | test     |
      | Jane      | Fonda    | jane.fonda@sytac.io  | test     |

  Scenario: Inserting a new user
    When the following users are registered
      | firstName | lastName | email                  | password |
      | Mickey    | Rourke   | mickey.rourke@sytac.io | test     |
    Then there should be 5 users present in the system
    Then searching for email mickey.rourke@sytac.io should return the following record
      | firstName | lastName | email                  | password |
      | Mickey    | Rourke   | mickey.rourke@sytac.io | test     |




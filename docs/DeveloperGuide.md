---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# SpyGlass Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

---

## Acknowledgements

This project is a modified version of **AddressBook-Level 3 (AB3)**, created by the [SE-EDU initiative](https://se-education.org).

* **Original Source:** [AddressBook-Level 3](https://github.com/se-edu/addressbook-level3)
* **Third-party libraries used:** [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit5](https://junit.org/junit5/)

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

---

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The **_Architecture Diagram_** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S2-CS2103T-T15-2/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2526S2-CS2103T-T15-2/tp/blob/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.

- At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
- At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

- [**`UI`**](#ui-component): The UI of the App.
- [**`Security`**](#security-component): Validates password presence and handles initial configuration.
- [**`Logic`**](#logic-component): The command executor and handler for App Mode.
- [**`Model`**](#model-component): Holds the data of the App in memory.
- [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The _Sequence Diagram_ below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

- defines its _API_ in an `interface` with the same name as the Component.
- implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `CommandHistory`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

- executes user commands using the `Logic` component.
- listens for changes to `Model` data so that the UI can be updated with the modified data.
- keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
- depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Security component

**API** : [`Security.java`](https://github.com/AY2526S2-CS2103T-T15-2/tp/blob/master/src/main/java/seedu/address/security/Security.java)

The `Security` component is responsible for the application's integrity check upon startup.

- **Integrity Check:** Verifies if the `password` field in the storage file is present and contains valid characters (not just empty or whitespace).
- **Startup Logic:** Returns a status to `Main` indicating whether the application should proceed to the Password Setup screen or the standard Locked Mode.
- **Note:** It does *not* handle runtime password verification for restricted commands; that is delegated to the `Logic` and `Model` components.

The sequence diagram below illustrates the interactions during the startup phase, showing how the `Security` component determines the initial UI state.

<puml src="diagrams/SecurityStartupSequenceDiagram.puml" alt="Interactions during the startup integrity check" />

How the startup check works:
1. `MainApp` calls `Security#getStartupStatus()`.
2. `Security` queries `Storage` for the current password configuration.
3. Based on the result (valid password vs. empty/missing), `Security` returns a status code.
4. `MainApp` then initializes either the `MainWindow` or the `PasswordSetupWindow` based on that status.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

#### Unlock Command Example

The sequence diagram below illustrates the interactions within the `Logic` component for a state-changing command, taking `execute("unlock myPassword123")` API call as an example.

<puml src="diagrams/UnlockSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `unlock` Command" />

This diagram shows how the `UnlockCommand` validates a password against the stored credentials in the `Model` and transitions the application to the `UNLOCKED` state upon successful authentication.

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command communicates with the **`Model`** when it is executed (e.g., to delete a person).
    * **App Mode Management:** `Logic` is also responsible for managing state transitions; it passes the current `AppMode` (Locked or Unlocked) down to the `Model` for execution.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:

- When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
- All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component

**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />

The `Model` component,

- stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
- stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
- stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
- does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>

### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,

- can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
- inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
- depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

---

## **Documentation, logging, testing, configuration, dev-ops**

- [Documentation guide](Documentation.md)
- [Testing guide](Testing.md)
- [Logging guide](Logging.md)
- [Configuration guide](Configuration.md)
- [DevOps guide](DevOps.md)

---

## **Appendix: Requirements**

### Product scope

**Target user profile**:

- is a social individual in a high-scrutiny domestic environment
- has their digital privacy frequently compromised by an overbearing or possessive partner
- needs to discreetly manage sensitive social connections
- requires a fast interface for the near-instant concealment of private data during unexpected screen checks
- can type incredibly fast and prefers typing to mouse interactions

**Value proposition**: Spyglass provides a secure interface for managing sensitive contacts hidden from observers. It allows users to categorise private contacts, enabling the concealment of private data through commands that hides sensitive entries. This ensures the application maintains the appearance of a standard address book, providing a layer of plausible deniability.

## User Stories

**Priorities:** High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​ | I want to …​ | So that I can…​                                                  |
| :--- | :--- | :--- |:-----------------------------------------------------------------|
| `* * *` | Contact Manager | add a contact with essential details | store new social connections efficiently.                        |
| `* * *` | Contact Manager | view a list of public contacts | see my everyday connections at a glance.                         |
| `* * *` | Discreet Contact Manager | delete sensitive contacts while in Unlocked mode | remove specific records permanently to avoid detection.          |
| `* * *` | Discreet Contact Manager | switch to Locked mode instantly | hide private data and display a harmless interface to onlookers. |
| `* * *` | Privacy-Conscious User | set a secure password upon initial launch | ensure only I can access the locked mode of the app.             |
| `* * *` | Privacy-Conscious User | unlock the app using a secret password | transition from the public view to my private contact list.      |
| `* *` | Contact Manager | edit contact information | keep my records accurate and up to date.                         |
| `* *` | Discreet Contact Manager | search through hidden contacts by keyword | quickly retrieve sensitive information without manual scrolling. |

_{More to be added}_

### Use cases

(For all use cases below, the **System** is `SpyGlass` and the **Actor** is the `user`, unless specified otherwise)

**Use case: UC1 - Initial password setup**

**Preconditions:** The application is launched for the first time, or the password field in the data file is empty/invalid.

**MSS**

1. User launches SpyGlass.
2. SpyGlass detects no existing password and displays the **Password Setup** screen.
3. User enters a new password.
4. SpyGlass saves the password to the data file.
5. SpyGlass transitions to the main interface in **Locked mode**.

   Use case ends.

**Extensions**

* 3a. The user enters a password consisting only of spaces or leaves it empty.
    * 3a1. SpyGlass shows an error message.
    * 3a2. SpyGlass prompts the user to enter a valid password again.
      Use case resumes at step 3.

* 4a. SpyGlass fails to write to the data file.
    * 4a1. SpyGlass shows an error message indicating a storage failure.
      Use case ends.

**Use case: UC2 - Lock application**

**Preconditions:** User is in Unlocked mode.

**MSS**

1. User decides to hide their private contacts.
2. User enters the command `lock`.
3. SpyGlass switches the UI from Unlocked mode to **Locked mode**.
4. SpyGlass continues operating as a normal-looking addressbook application.

   Use case ends.

**Extensions**


* 3a. The user performs operations while the system is locked.
    * 3a1. SpyGlass accepts the operation.
    * 3a2. SpyGlass stores the data in **Locked mode storage** instead of Unlocked mode storage.
      Use case ends.

---

**Use case: UC3 - Unlock application**

**MSS**

1. User is currently interacting with the **Locked mode**.
2. User enters the secret password previously set.
3. SpyGlass switches the UI from **Locked mode** to Unlocked mode.
4. SpyGlass loads the Unlocked mode contact list and shows a success message.

   Use case ends.

**Extensions**

* 3a. The entered password is incorrect.
    * 3a1. SpyGlass remains in **Locked mode**.
    * 3a2. SpyGlass displays an "unknown command" message to mask the app's capabilities.
      Use case ends.

---

**Use case: UC4 - Add a contact**

**MSS**

1. User requests to add a contact with required details.
2. User adds the new contact with relevant details.
3. SpyGlass saves the new contact to the current mode's storage and updates the display.
4. SpyGlass updates the command history to show the user added a new contact.

   Use case ends.

**Extensions**

* 3a. A required parameter is missing.
    * 3a1. SpyGlass shows an error message indicating the missing parameter.
      Use case ends.

* 3b. A parameter fails validation.
    * 3b1. SpyGlass shows the corresponding validation error message.
      Use case ends.

* 4a. The contact already exists in the current mode.
    * 4a1. SpyGlass shows an error message indicating the contact already exists.
      Use case ends.

---

**Use case: UC5 - Delete a contact**

**MSS**

1. User requests to delete a specific contact in the list by index.
2. SpyGlass deletes the contact from the current mode's storage.
3. SpyGlass updates the contact list display.
4. SpyGlass updates the command history to reflect the user deleted a contact.

   Use case ends.

**Extensions**

* 1a. The given index is invalid.
    * 1a1. SpyGlass shows an error message.
      Use case resumes at step 2.

* 2a. SpyGlass fails to save the deletion to storage.
    * 2a1. SpyGlass shows an error message indicating storage failed.

    Use case ends.

_{More to be added}_

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4. While in locked mode, the application name must not contain the words "Spy" or "Secret" to avoid detection.
5. While in locked mode, restricted commands must not provide any visual feedback that hints at the existence of a hidden mode.

_{More to be added}_

### Glossary

- **Mainstream OS**: Windows, Linux, Unix, MacOS.
- **Private contact detail**: Information that is meant to be hidden from unauthorized users.
- **Locked Mode**: The default, public state of the app. It displays as a standard "AddressBook" to hide its true purpose.
- **Unlocked Mode**: The secure state revealed after entering a password, showing private contacts.
- **Locked Mode Storage**: A public database that saves contacts added while the app is locked.
- **Unlocked Mode Storage**: A hidden database where sensitive contacts are kept.
- **Restricted Command**: A command that only works in one specific mode (e.g., `lock` only works when the app is Unlocked).
- **Unrestricted Command**: A command that functions in both Locked and Unlocked modes.
- **Index**: A number representing a contact's position in the current list on the screen.

---

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on; testers are expected to do more *exploratory* testing.

</box>

### Launch and Password Setup

1. **Initial launch and setup**
    1. Download the jar file and copy it into an empty folder.
    2. Open a terminal and run `java -jar addressbook.jar`.
    3. **Expected:** Instead of the main contact list, a **Password Setup** screen appears.
    4. Enter a password (e.g., `myPassword123`) and confirm it.
    5. **Expected:** The app transitions to the main GUI in **Locked mode** (window title shows "AddressBook"). Sample contacts are visible.

2. **Invalid Password Setup**
    1. Delete the `data/addressbook.json` file to reset the app.
    2. Launch the app again.
    3. Try entering a password consisting only of spaces.
    4. **Expected:** An error message is shown. The app does not proceed to the main interface.

### Authentication (Lock/Unlock)

1. **Unlocking the app**
    1. Prerequisites: App is in **Locked mode**.
    2. Test case: `unlock myPassword123` (using the password set during setup).
    3. **Expected:** App switches to **Unlocked mode**. The secret contact list is displayed.
    4. Test case: `unlock wrongPassword`.
    5. **Expected:** App remains in Locked mode. An `Unknown command` message is shown to mask the authentication attempt.

2. **Locking the app**
    1. Prerequisites: App is in **Unlocked mode**.
    2. Test case: `lock`.
    3. **Expected:** App immediately switches back to **Locked mode**. Secret contacts are hidden, and the public contact list is shown.

### Deleting a Person

1. **Deleting a person**
    1. Prerequisites: Ensure there are multiple contacts in the current list.
    2. Test case: `delete 1`.
    3. **Expected:** The first contact in the secret list is deleted. Success message shown in the status box.

### Saving Data

1. **Dealing with missing/corrupted data files**
    1. **Missing File:** Delete `data/addressbook.json`. Launch the app.
        * **Expected:** App treats this as a fresh install and prompts for password setup.
    2. **Corrupted JSON:** Open `data/addressbook.json` and remove a bracket or quote to make the JSON invalid.
        * **Expected:** SpyGlass clears the corrupted data and starts with an empty file/password setup prompt.
    3. **Empty Password Field:** Manually edit the JSON file to set the `password` field to `""`.
        * **Expected:** On next launch, the app prompts the user to set a new password.

### Window Preferences

1. **Saving window state**
    1. Resize the window and move it to a new corner of your screen. Close the app.
    2. Re-launch the app.
    3. **Expected:** The app opens with the previous size and position.

1. _{ more test cases …​ }_

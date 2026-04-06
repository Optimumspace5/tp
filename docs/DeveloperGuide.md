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

**API** : [`Logic.java`](https://github.com/AY2526S2-CS2103T-T15-2/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

The `Logic` component is responsible for:

- receiving raw command text from the UI
- parsing that text into concrete `Command` objects
- enforcing mode-based command availability via `CommandRegistry`
- executing commands against the current `Model` using a `CommandContext`
- applying requested mode transitions through `AppModeManager`
- saving the address book through `Storage` after each command that completes without throwing
- returning a `CommandResult` to the UI so that the UI can handle follow-up actions such as
  selecting a person, showing setup, or exiting the application

Here is a partial class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="650"/>

The sequence diagram below illustrates the interactions within the `Logic` component for
`execute("delete 1")`, assuming the provided index is valid.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions inside the Logic component for the delete command" />

#### Unlock command example

The next sequence diagram shows how a mode-changing command flows through the same pipeline, using
`execute("unlock myPassword123")` as the example and assuming the application is currently in
locked mode.

<puml src="diagrams/UnlockSequenceDiagram.puml" alt="Interactions inside the Logic component for the unlock command" />

This diagram highlights an important design decision: `UnlockCommand` does not directly mutate the
application mode. Instead, it validates the password and returns a `CommandResult` that requests
`AppMode.UNLOCKED`. `LogicManager` is responsible for applying the transition through
`AppModeManager`, refreshing the filtered list for the new mode, and persisting the address book.

How the `Logic` component works:

1. `LogicManager#execute(...)` receives the raw command text from the UI and logs it.
1. `LogicManager` forwards the raw text to `AddressBookParser`.
1. `AddressBookParser` trims the input, splits it into `commandWord` and `arguments` using
   `BASIC_COMMAND_FORMAT`, then queries the current `AppMode` through the `Supplier<AppMode>`
   provided by `LogicManager`.
1. `AddressBookParser` delegates mode-aware command routing to `CommandRegistry`.
1. `CommandRegistry` checks whether the command is registered and allowed in the current mode.
   If either check fails, it throws a generic unknown-command `ParseException`.
1. If the command requires structured argument parsing, `CommandRegistry` delegates to a concrete
   parser such as `AddCommandParser`, `DeleteCommandParser`, `EditCommandParser`,
   `FindCommandParser`, `HelpCommandParser`, `ToggleCommandParser`, `UnlockCommandParser`,
   or `ViewCommandParser`.
1. Commands that do not require their own parser, such as `clear`, `list`, `lock`, `exit`,
   and `setup`, are instantiated directly by `CommandRegistry`.
1. After parsing, `LogicManager` creates a `CommandContext`, which packages the current `Model`
   and `AppMode` at execution time.
1. The parsed command executes using that `CommandContext`.
   This avoids stale-state issues where a command could be parsed under one mode but executed
   after the application has already transitioned to another.
1. The command returns a `CommandResult`.
   In addition to user-facing feedback, `CommandResult` can request follow-up UI actions such as
   selecting a person by index, opening the setup flow, exiting the application, or transitioning
   to a different `AppMode`.
1. If `CommandResult` requests a mode change, `LogicManager` updates `AppModeManager`
   and refreshes the filtered list in `Model` for the requested mode.
1. `LogicManager` saves the current address book through `Storage`.
   If saving fails, `AccessDeniedException` and `IOException` are wrapped and surfaced as
   `CommandException`.

Concrete command classes encapsulate feature-specific behaviour after parsing. For example,
`AddCommand`, `DeleteCommand`, `ToggleCommand`, and `ViewCommand` obtain the current `Model`
and `AppMode` from `CommandContext` before mutating or querying the filtered list.
Commands then use `CommandResult` to communicate follow-up actions:
`AddCommand`, `EditCommand`, and `ViewCommand` can request UI selection through `selectedIndex`,
`SetupCommand` uses `showSetup`, `ExitCommand` uses `exit`, and `LockCommand` /
`UnlockCommand` request mode changes through `requestedMode`.

The parser-related classes used by the `Logic` component are shown below:

<puml src="diagrams/ParserClasses.puml" width="700"/>

How the parsing support classes work:

- All concrete parsers implement the `Parser<T>` interface.
- `AddressBookParser` is intentionally lightweight: it only performs the initial split of raw user
  input before handing mode-aware routing to `CommandRegistry`.
- `CommandRegistry` centralizes command registration and authorization, which keeps parser selection
  logic and mode rules out of `AddressBookParser`.
- `ArgumentTokenizer`, `ArgumentMultimap`, `ParserUtil`, `CliSyntax`, and `Prefix` are reused by
  parsers that need structured argument extraction and validation.

### Model component

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

## **Implementation**

This section describes some noteworthy details on how selected features are implemented.

### Lock/Unlock mode switching

This subsection describes how SpyGlass switches between **Locked** and **Unlocked** mode.
It focuses on mode transitions and UI behaviour only.
Password setup and password persistence are documented separately.

#### Implementation

The lock/unlock mechanism is facilitated primarily by `AppModeManager`, `CommandResult`,
`LogicManager`, `ModelManager`, and `MainWindow`.

Unlike an implementation that swaps between two separate databases, SpyGlass keeps a single
combined `AddressBook` in memory and exposes different views of that data depending on the
current `AppMode`.

The relevant responsibilities are:

- `LockCommand#execute(CommandContext)` returns a `CommandResult` requesting `AppMode.LOCKED`.
- `UnlockCommand#execute(CommandContext)` validates the password against `Model` and returns a
  `CommandResult` requesting `AppMode.UNLOCKED` when validation succeeds.
- `LogicManager#execute(String)` applies the requested mode change through `AppModeManager`,
  refreshes the filtered list for the new mode, and persists the address book through `Storage`.
- `ModelManager#getFilteredPersonList(AppMode)` and
  `ModelManager#updateFilteredPersonList(Predicate<Person>, AppMode)` expose the correct mode-based
  view of the same underlying address book.
- `MainWindow#updateUi(AppMode)` refreshes the visible UI after a mode change by updating the
  application title, refreshing the person list, clearing the selected person details, and toggling
  restricted fields such as the status label in the detail panel.

The following state diagram summarizes the two application modes and their visible behaviour:

<puml src="diagrams/LockUnlockStateDiagram.puml" width="650" />

A few implementation details are worth noting:

- The current mode is stored centrally in `AppModeManager`, not inside individual commands.
- `LockCommand` and `UnlockCommand` do not directly mutate global application state.
  They only return a `CommandResult` that requests a mode transition.
- `LogicManager` is responsible for applying the mode transition and refreshing the filtered list.
- `ModelManager` maintains two filtered views over the same combined person list:
  one for locked mode and one for unlocked mode.
- In locked mode, only persons with `PersonStatus.LOCKED` are visible.
  In unlocked mode, the filtered list can show the full combined list.
- A successful mode switch is still followed by `Storage#saveAddressBook(...)`,
  because `LogicManager` persists the address book after every command that completes without
  throwing an exception.

#### Lock flow

`lock` is registered only for **Unlocked** mode in `CommandRegistry`.
When the user executes `lock`, the command does not directly update `AppModeManager`.
Instead, it returns a `CommandResult` requesting `AppMode.LOCKED`.

`LogicManager` then:

1. applies the requested mode transition through `AppModeManager`
1. refreshes the model using `Model.PREDICATE_SHOW_ALL_PERSONS` for locked mode
1. saves the address book through `Storage`

After that, `MainWindow` updates the visible interface by:

- changing the window title from `Spyglass` to `AddressBook`
- refreshing the person list so only locked contacts remain visible
- clearing the currently selected person details
- hiding restricted UI fields such as the status label in the detail panel

The sequence diagram below shows the successful `lock` path:

<puml src="diagrams/LockSequenceDiagram.puml" width="900" />

One intentional UI decision is that lock success feedback is not shown in the result pane.
`MainWindow` clears the previous result history during a mode change, and only restores feedback
for unlock transitions.
This makes the locked interface appear cleaner and less suspicious.

#### Unlock flow

`unlock` is registered in both modes, but it behaves differently depending on the current state.

When the app is in locked mode, `UnlockCommand` validates the provided password against the value
stored in `Model`. If the password is correct, it returns a `CommandResult` requesting
`AppMode.UNLOCKED`.

`LogicManager` then:

1. applies the requested mode transition through `AppModeManager`
1. refreshes the filtered list for unlocked mode
1. saves the address book through `Storage`

Finally, `MainWindow` updates the UI by:

- changing the window title from `AddressBook` to `Spyglass`
- refreshing the person list so the unlocked-mode view is shown
- clearing the current person details and updating the detail panel for the new mode
- showing the unlock success message in the result pane

If the provided password is incorrect while the app is locked, `UnlockCommand` throws a generic
unknown-command `CommandException` instead of revealing password failure explicitly.
If `unlock` is executed while the app is already unlocked, it throws a specific
"already unlocked" message instead.

The sequence diagram below shows the successful unlock path and the incorrect-password path:

<puml src="diagrams/UnlockSequenceDiagram.puml" width="900" />

#### Design considerations

**Aspect: Where mode transitions should be applied**

- **Alternative 1 (current choice):** Commands return a `CommandResult` that requests a mode change,
  and `LogicManager` applies the transition.
  - Pros: Keeps commands simple and avoids coupling them directly to global application state.
  - Pros: Ensures all mode-changing commands follow the same post-processing pipeline
    (mode update, filtered-list refresh, and save).
  - Cons: The full behaviour of `lock`/`unlock` is split across command, logic, model, and UI.

- **Alternative 2:** Let `LockCommand` and `UnlockCommand` directly mutate the current mode.
  - Pros: Reduces the amount of follow-up handling outside the command class.
  - Cons: Increases coupling between commands and application state managers.
  - Cons: Makes it easier for commands to bypass consistent refresh and persistence behaviour.

**Aspect: How locked and unlocked contact views should be represented**

- **Alternative 1 (current choice):** Maintain one combined address book and expose different
  filtered views depending on `AppMode`.
  - Pros: Provides a single source of truth for all contacts.
  - Pros: Simplifies features that operate across both modes, such as toggling a contact's status.
  - Pros: Avoids data duplication and synchronization problems between separate lists.
  - Cons: Mode behaviour depends on correct filtering logic in `ModelManager`.

- **Alternative 2:** Maintain separate data stores or separate in-memory lists for locked and
  unlocked contacts.
  - Pros: Makes the distinction between the two modes conceptually explicit.
  - Cons: Introduces extra synchronization complexity when contacts move between modes.
  - Cons: Makes shared operations and persistence logic harder to maintain.

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

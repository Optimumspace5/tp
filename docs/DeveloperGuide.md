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

The bulk of the app's work is done by the following five components:

- [**`UI`**](#ui-component): The UI of the App.
- [**`Security`**](#security-component): Validates password presence and handles initial configuration.
- [**`Logic`**](#logic-component): The command executor and handler for App Mode.
- [**`Model`**](#model-component): Holds the data of the App in memory.
- [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The _Sequence Diagram_ below shows how the components interact with each other for the scenario where the user completes the initial password setup (e.g., submitting `"myPassword123"`).

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="900" />

Each of the five main components (also shown in the diagram above),

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

Here's a class diagram of the `Security` component:

<puml src="diagrams/SecurityClassDiagram.puml" width="300"/>

The `Security` component is responsible for the application's integrity check upon startup and handling password authentication state.

- **Integrity Check:** Verifies if the `password` field in the storage file is present and contains valid characters via `isAuthenticated()`.
- **Startup Logic:** Returns a boolean value indicating whether the application should transition to the initial Setup Panel.
- **Password Setup:** Validates the user's plain-text input using `PasswordUtil` and delegates to the `Logic` component to securely save the password state.

The sequence diagram below illustrates the interactions during the startup phase, showing how the `Security` component determines the initial UI state.

<puml src="diagrams/SecurityStartupSequenceDiagram.puml" alt="Interactions during the startup integrity check" />

How the startup check works:
1. `MainWindow` asks `Security` if the app is configured via `isAuthenticated()` during its initialization phase (`fillInnerParts()`).
2. `Security` queries `Logic` (which in turn queries `Model`) for the stored password state.
3. Based on the result (valid password vs. empty/missing), `Security` returns a boolean back to `MainWindow`.
4. If the application is not authenticated, `MainWindow` then invokes `handleSetup()` to load the new `SetupPanel` for the user.

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
- does not depend on any of the other four components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>

### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S2-CS2103T-T15-2/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,

- can save both address book data and user preference data in JSON format, and read them back into corresponding objects. 
- saves the application's **access password** alongside the contact list within `JsonSerializableAddressBook` to ensure the security state persists across application launches. 
- preserves the privacy status of each `Person` status (Public or Sensitive) during serialisation, enabling the Model to correctly filter the FilteredPersonList based on the active AppMode.
- inherits from both `AddressBookStorage` and `UserPrefStorage`, allowing it to be treated as either interface depending on the required functionality.

The `Storage` component depends on the following classes in the `Model` component:

- **`ReadOnlyAddressBook` & `ReadOnlyUserPrefs`**: Used to retrieve immutable snapshots of the data for the saving process.
- **`Person`**: Required by `JsonAdaptedPerson` to map contact details and privacy levels from JSON format to domain objects.
- **`UserPrefs`**: Used to store and retrieve metadata such as GUI settings and the path to the data file.

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

---

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Setup Password Implementation

The current setup feature allows users to configure a secure password for their vault. Setting up a valid password is mandatory to ensure the privacy of the Unlocked mode. The setup mechanism switches the user interface to a dedicated view via `SetupPanel`.

The setup initialization is facilitated by `MainWindow`, `SecurityManager`, and `LogicManager`. It securely delegates the
exact UI state change back to the UI context.

When an operation dictates that the user is required to set a password, the system invokes the `MainWindow#handleSetup()` mechanism. This occurs in two primary ways:

1.  **Initial Launch** — `MainWindow#fillInnerParts()` checks upon startup via `SecurityManager#isAuthenticated()`. If no valid password exists in the save file (such as on the first launch/data corruption), it prevents normal rendering of `MainWindow`.
2.  **Setup Command** — While in Unlocked mode, the user deliberately enters the `setup` command.

When the `setup` command is executed, `LogicManager` generates an empty `SetupCommand`. It creates and returns a `CommandResult` object with its `showSetup` flag evaluated to `true`. When the `CommandResult` is returned to the `MainWindow`, it checks `isShowSetup() == true`, and switches the active GUI root to the `SetupPanel`.

Once the user is on the `SetupPanel`, they submit a string password, and the UI triggers the `MainWindow#handlePasswordInput(String)` callback. `SecurityManager` intercepts this, validates it utilizing `PasswordUtil`, and communicates with `LogicManager` to store it via `LogicManager#setAddressBookPassword()` and writes it persistently via `LogicManager#saveAddressBook()`.

The following operations actively facilitate password setup and its view transitions:

*   `SecurityManager#isAuthenticated()` — Validates whether a setup screen should appear.
*   `CommandResult#isShowSetup()` — Checks if a command actively dictates a transition to the configuration interface.
*   `MainWindow#handleSetup()` — Triggers the application into rendering its custom `SetupPanel` view.
*   `SecurityManager#savePassword(String)` — Validates the password and store it persistently in `Storage`.

Given below is an example usage scenario and how the system behaves at each step when initiated via command.

**Step 1.** The user launches the application, successfully enters the Unlocked Mode, and types `setup`.

**Step 2.** The `LogicManager` propagates execution. Since the command identifies correctly, the parser creates a `SetupCommand`.

**Step 3.** The `SetupCommand` executes. It creates a `CommandResult` instantiated with a `showSetup` flag set to `true`.

**Step 4.** The `MainWindow` retrieves the boolean via `CommandResult#isShowSetup()`. Recognizing it is `true`, it immediately calls `MainWindow#handleSetup()`, which renders the secondary `SetupPanel` directly onto the primary stage.

<box type="info" seamless>

**Note:** If the user fails to input a valid secure string inside the `SetupPanel`, `SecurityManager` throws an exception that `MainWindow` catches, ensuring the user stays restricted inside the `SetupPanel` until a valid one is stored.

</box>

The following sequence diagram shows how an explicit `setup` execution passes through the UI and Logic component:

<puml src="diagrams/SetupSequenceDiagram.puml" alt="Setup Sequence Diagram" />

### Design considerations

**Aspect: How a password entry executes:**

*   **Alternative 1 (current choice):** Switches to a dedicated `SetupPanel` view.
    *   **Pros:** Prominently guides the user through the password setup and ensures the user is not able to interact
    with the application without going through the critical setup phase.
    *   **Cons:** Architecture becomes slightly more complex, as the state transition requires propagating an abstract representation of UI intent (`showSetup=true` flags inside `CommandResult`) directly back to the active `MainWindow`.

*   **Alternative 2:** Execute it purely as a one-liner console command like `setup mypassword123`.
    *   **Pros:** Requires almost no architectural additions inside `UI`, very simple implementation, avoids an entire UI panel creation sequence.
    *   **Cons:** Harder to implement mode-based command restrictions as the initial setup will need be executed in Locked mode but password changes should only be done in the Unlocked mode. Furthermore, the setup command could be saved in the `CommandHistory`, potentially revealing the existence of hidden functionalities in Locked mode.

---

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

- is a privacy-conscious user under high scrutiny in their domestic environment
- has a need to manage sensitive contacts discreetly and securely
- requires near-instant concealment of private data during unexpected checks
- can type fast and prefers typing to mouse interactions
- is reasonably comfortable using CLI apps

**Value proposition**: Allows privacy-conscious users to manage sensitive social connections faster and more discreetly than a typical mouse/GUI driven app

## User stories

**Priorities**: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​ | I want to …​ | So that I can…​ |
|:---------| :--- | :--- |:--------------------------------------------------------------------------------|
| `* * *`  | new user | see usage instructions | refer to instructions when I forget how to use the application |
| `* * *`  | new user | set a secure password upon initial launch | ensure only I can access the private features of the application from the start |
| `* * *`  | privacy-conscious user | change my access password | update my security credentials to ensure continued privacy |
| `* * *`  | privacy-conscious user | add a new public contact | store non-sensitive social connections in the standard list |
| `* * *`  | privacy-conscious user | add a new sensitive contact | securely store connections that must remain hidden |
| `* * *`  | privacy-conscious user | edit a contact | update details of an existing contact |
| `* * *`  | privacy-conscious user | list all contacts | see all contacts available in my current access level |
| `* * *`  | privacy-conscious user | delete a contact | remove entries that I no longer need to store |
| `* * *`  | user under scrutiny | lock the application instantly | hide sensitive data and show a standard interface to onlookers |
| `* * *`  | user under scrutiny | unlock the application with a hidden command | access my private data through a password |
| `* * *`  | privacy-conscious user | toggle a contact between public and sensitive | change the privacy level of a contact as my situation evolves |
| `* * *`  | user under scrutiny | view specific details of a contact in a separate panel | ensure that sensitive details can be conditionally displayed to the user |
| `* * *`  | user under scrutiny | experience no discovery of restricted commands in locked mode | ensure that sensitive commands are hidden from the help menu and suggestions |
| `* * *`  | privacy-conscious user | navigate previous commands using up and down arrow keys | re-run or edit prior commands rapidly during high-pressure situations |
| `* *`    | privacy-conscious user | navigate and focus the UI using Tab and Shift-Tab | operate the application at high speed using a keyboard |
| `* *`    | privacy-conscious user | find a contact by name | quickly find a specific contact in my list |
| `* *`    | privacy-conscious user | view detailed contact information via a command | access data entirely through the CLI for a faster experience than UI navigation |
| `* *`    | user under scrutiny | clear all data | wipe the database instantly if the device's security is compromised |
| `* *`    | privacy-conscious user | save contact details to a file | backup my sensitive information securely |
| `* *`    | privacy-conscious user | load contact details from a file | restore my sensitive information from a backup |
| `*`      | user under scrutiny | see a history of command results | verify the success of my data commands quickly |

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

- **Mainstream OS**: Windows, Linux, Unix, macOS.
- **Privacy-conscious user**: An individual who maintains a distinction between sensitive and public contacts, requiring distinct access protocols and visibility levels for each to ensure personal privacy.
- **User under scrutiny**: A user in a high-pressure environment where their digital privacy is actively monitored or at risk of compromise.
- **Locked Mode**: The default, public state of the application. It functions as a standard, mundane address book to provide plausible deniability and hide the existence of any sensitive data from onlookers.
  - *Visual Identifier:* The window title displays as **"AddressBook"** to blend in with standard utility software.
- **Unlocked Mode**: The secure state of the application, revealed only after entering a hidden password. This mode allows the user to view, add and manage sensitive contacts that are otherwise hidden.
  - *Visual Identifier:* The window title displays as **"Spyglass"** to confirm the user has access to private data.
- **Sensitive Contact**: A contact entry that is only visible and accessible while the application is in Unlocked Mode.
- **Public Contact**: A contact entry that remains visible in both Locked and Unlocked modes.
- **Restricted Command**: A command that is only operational in a specific mode. For example, the `setup` command only functions when the application is currently Unlocked.
- **Unrestricted Command**: A command that functions consistently across both Locked and Unlocked modes, such as the `exit` or `list` commands.
- **Highlighted Contact**: The specific contact entry currently selected from the list, whose full details are displayed in the UI component located at the bottom left of the interface.
- **MSS (Main Success Scenario)**: The most straightforward interaction for a given use case that assumes nothing goes wrong and all steps are completed successfully.
- **API (Application Programming Interface)**: A set of rules or protocols that govern the application to allow different software components or external applications to communicate and work together.
- **CLI (Command Line Interface)**: A text-based interface where users interact with Spyglass by typing specific commands on a keyboard.
- **GUI (Graphical User Interface)**: The visual component of the application that displays contact lists and command results, allowing users to see information processed via the CLI.
- **JAR**: A Java Archive file format used to distribute the Spyglass application and its required libraries as a single, portable executable file.
- **JSON (JavaScript Object Notation)**: A lightweight, human-readable data format used by Spyglass to store contact information, password and application settings in local storage.

---

## **Appendix: Instructions for manual testing**

Given below are instructions to test Spyglass manually.

<box type="info" seamless>

**Note:** These instructions serve as a baseline. Testers are expected to go beyond these cases and perform exploratory testing to verify the reliability of all privacy and security mechanisms.

</box>

### Launch and Password Setup

1. **Initial launch and setup**
    1. Download the `spyglass.jar` file and copy it into an empty folder.
    2. Open a terminal and run `java -jar spyglass.jar`.
    3. **Expected:** A **Password Setup** screen appears. The main interface is not accessible.
    4. Enter a secure password (e.g., `secure123`) and confirm it.
    5. **Expected:** The app transitions to the main GUI in **Locked mode**. Window title displays `AddressBook`. Sample public contacts are visible.
       <br>Output: `Setup process completed successfully.`

2. **Invalid Password Setup**
    1. Delete the `data/` folder to reset the app.
    2. Launch the app and try entering a password consisting only of spaces or invalid symbols.
    3. **Expected:** Error message is shown. The app prevents proceeding until a valid password is set.

3. **Persistence of Locked State on Re-launch**
    1. Prerequisites: App is in **Unlocked mode**.
    2. Exit the application using the `exit` command.
    3. Re-launch the app using `java -jar spyglass.jar`.
    4. **Expected:** The app starts in **Locked mode** regardless of the exit state. Window title displays `AddressBook`.

4. **Reconfiguring Password via Setup Command**
    1. Prerequisites: App is in **Unlocked mode**.
    2. Test case: `setup`
    3. **Expected:** A **Password Setup** overlay or window appears, allowing the user to redefine their access credentials.
       <br>Output: `Opening Setup Page...` and `Setup process completed successfully.`
    4. Test case (In Locked Mode): `setup`
    5. **Expected:** The app remains unchanged.
       <br>Output: `Unknown command.`

### Authentication (Lock/Unlock)

1. **Unlocking the app (Successful)**
    1. Prerequisites: App is in **Locked mode**.
    2. Test case: `unlock secure123`
    3. **Expected:** The window title changes to `Spyglass`. Sensitive contacts become visible.
       <br>Output: `Switched to Unlocked Interface.`

2. **Unlocking the app (Failed/Stealth check)**
    1. Prerequisites: App is in **Locked mode**.
    2. Test case: `unlock wrongPassword`
    3. **Expected:** The window title remains `AddressBook`. No sensitive data is revealed.
       <br>Output: `Unknown Command`

3. **Locking the app**
    1. Prerequisites: App is in **Unlocked mode**.
    2. Test case: `lock`
    3. **Expected:** The window title immediately reverts to `AddressBook`. Sensitive contacts are instantly hidden.
       <br>Output: None

### Listing Contacts

1. **Listing contacts in different modes**
    1. Prerequisites: Ensure there is at least one sensitive contact and one public contact.
    2. Test case (Locked): `list`
    3. **Expected:** Only public contacts are displayed.
       <br>Output: `Listed all persons`
    4. Test case (Unlocked): `list`
    5. **Expected:** Both public and sensitive contacts are displayed in a unified list.
       <br>Output: `Listed all persons`

### Adding Contacts

1. **Adding a public contact**
    1. Prerequisites: App is in **Locked mode**.
    2. Test case: `add -n John Doe -p 98765432 -e john@example.com -a 123 Main St`
    3. **Expected:** Contact is added. Visible in both Locked and Unlocked modes.
       <br>Output: `New person added: John Doe; Phone: 98765432; Email: john@example.com; Address: 123 Main St; Tags: `
       <br>John Public has been added to the address book!

2. **Adding a sensitive contact**
    1. Prerequisites: App is in **Unlocked mode**.
    2. Test case: `add -n Rebecca Lee -p 12345678 -e rebecca@secret.com -a 234 Main St`
    3. **Expected:** Contact is added to the list.
       <br>Output: `New person added: Rebecca Lee; Phone: 12345678; Email: rebecca@secret.com; Address: 234 Main St; Tags: `
    4. Test case: Switch to **Locked mode** via `lock`.
    5. **Expected:** "Rebecca Lee" is no longer visible in the list.

### Editing Contacts

1. **Editing a person while all contacts are shown**
    1. Prerequisites: Sample public contacts are used.
    2. Test case: `edit 1 -n Jane Doe -p 91234567`
    3. **Expected:** The first contact in the list is updated with the new name and phone number. Detail panel reflects changes immediately.
       <br>Output: `Edited Person: Jane Doe; Phone: 91234567; Email: alexyeoh@example.com; Address: Blk 30 Geylang Street 29, #06-40; Tags: [friends]`

2. **Editing with missing fields**
    1. Test case: `edit 1`
    2. **Expected:** No contact is edited. Error details shown in the status message.
       <br>Output: `At least one field to edit must be provided.`

3. **Editing with an invalid index**
    1. Test case: `edit 0 -n Jane Doe`
    2. **Expected:** Error details shown in the status message.
       <br>Output: `Invalid command format! 
                     edit: Edits the details of the person identified by the index number used in the displayed person list. Existing values will be overwritten by the input values.
                     Parameters: INDEX (must be a positive integer) [-n NAME] [-p PHONE] [-e EMAIL] [-a ADDRESS] [-t TAG]...
                     Example: edit 1 -p 91234567 -e johndoe@example.com`

### Finding Contacts

1. **Finding contacts by name**
    1. Prerequisites: Sample public contacts are used.
    2. Test case: `find Alex`
    3. **Expected:** The contact list filters to show only contacts whose names contain "Alex". Alex Yeoh should be displayed.
       <br>Output: `1 persons listed!`

2. **Finding contacts with multiple keywords**
    1. Prerequisites: Sample public contacts are used.
    2. Test case: `find Alex Bernice Charlotte`
    3. **Expected:** The contact list filters to show all contacts whose names contain at least one of the keywords. Alex Yeoh, Bernice Yu, and Charlotte Oliveiro should be displayed.
       <br>Output: `3 persons listed!`

3. **Finding contacts with no matching results**
    1. Test case: `find NonExistentName`
    2. **Expected:** The contact list becomes empty.
       <br>Output: `0 persons listed!`

4. **Finding contacts while Locked vs Unlocked**
    1. Prerequisites: A sensitive contact "Sensitive Alex" has been added. App is in **Locked mode**.
    2. Test case: `find Alex`
    3. **Expected:** Only "Alex Yeoh" is displayed. "Sensitive Alex" remains hidden.
       <br>Output: `1 persons listed!`
    4. Test case: `unlock [password]` then `find Alex`
    5. **Expected:** Both "Alex Yeoh" and "Sensitive Alex" are displayed in the list.
       <br>Output: `2 persons listed!`
   
### Deleting a Contact

1. **Deleting a person while all contacts are shown**
    1. Prerequisites: Sample public contacts are used. List all contacts using the `list` command. Multiple contacts in the list.
    2. Test case: `delete 1`
    3. **Expected:** First contact is deleted from the list. Success message shown in the status message.
       <br>Output: `Deleted Person: Alex Yeoh; Phone: 87438807; Email: alexyeoh@example.com; Address: Blk 30 Geylang Street 29, #06-40; Tags: [friends]`

### Privacy Management (Toggling)

1. **Toggling a contact from Public to Sensitive**
    1. Prerequisites: Sample public contacts are used. App is in **Unlocked mode**.
    2. Test case: `toggle 1`
    3. **Expected:** The contact's status changes to sensitive.
       <br>Output: `Updated Alex Yeoh to Sensitive contact.`
    4. Switch to **Locked mode** via `lock`.
    5. **Expected:** Alex Yeoh is now hidden from the list.

2. **Toggling a contact from Sensitive to Public**
    1. Prerequisites: Sample public contacts are used. App is in **Unlocked mode**.
    2. Test case: `add -n Rebecca Lee -p 12345678 -e rebecca@secret.com -a 234 Main St` followed by `toggle 7`
    3. **Expected:** The contact's status changes to public.
       <br>Output: `Updated Rebecca Lee to Public contact.`
    4. Switch to **Locked mode** via `lock`.
    5. **Expected:** Rebecca Lee is visible from the list.

### Viewing Details

1. **Viewing details**
    1. Prerequisites: Sample public contacts are used. App is in **Locked mode**.
    2. Test case: `view 1`
    3. **Expected:** The **PersonDetailPanel** updates at the bottom left. No description of "Public Contact" or "Sensitive Contact" are visible in the panel.
       <br>Output: `Viewed Person: Alex Yeoh`

### Keyboard Navigation and History

1. **Focus and Navigation**
    1. Prerequisites: Sample public contacts are used. No contacts are highlighted.
    2. Press `Tab`.
    2. **Expected:** Command Box is focused and Alex Yeoh is highlighted.
    3. Press `Shift + Tab`.
    4. **Expected:** Roy Balakrishnan is highlighted.

2. **Command History**
    1. Type `list`, then `help`, then `clear`.
    2. Press the **Up Arrow**.
    3. **Expected:** Command box populates with `clear`, then `help`, then `list`.
    4. Press the **Down Arrow**.
    5. **Expected:** Command box populates with `help`, then `clear`.

### Stealth Verification

1. **No Discovery Test**
    1. Prerequisites: App is in **Locked mode**.
    2. Test case: `help`
    3. **Expected:** The help window does **not** list `unlock`, `lock`, `setup` or `toggle`.
    4. Test case: Type `unlock` into the command box.
    5. **Expected:** No auto-suggestions for the `unlock` command appear.

### Saving Data

1. **Dealing with missing data files**
    1. Navigate to the `data/` folder and delete `addressbook.json`.
    2. Attempt to run the app.
    3. **Expected:** App treats this as a fresh install and prompts for **Password Setup**.

2. **Dealing with corrupted data files**
    1. Open `data/addressbook.json` and delete a bracket to corrupt the JSON.
    2. Attempt to run the app.
    3. **Expected:** Spyglass detects corruption, clears invalid data for security, and prompts for **Password Setup**.

3. **Dealing with invalid password**
    1. Open `data/addressbook.json` and add a whitespace to the password.
    2. Attempt to run the app.
    3. **Expected:** Spyglass detects unrecoverable password, and prompts for **Password Setup**.

### Window Preferences

1. **Saving window state**
    1. Resize the window and move it to a new corner of your screen. Close the app.
    2. Re-launch the app.
    3. **Expected:** The app opens with the previous size and position.

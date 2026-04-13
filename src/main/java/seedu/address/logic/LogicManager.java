package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandContext;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AppModeManager modeManager;
    private final AddressBookParser addressBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     * The {@code ModeManager} will be initialized to the default locked mode.
     */
    public LogicManager(Model model, Storage storage) {
        this(model, storage, new AppModeManager(AppMode.LOCKED));
    }

    /**
     * Constructs a {@code LogicManager} with explicit app mode manager.
     */
    public LogicManager(Model model, Storage storage, AppModeManager modeManager) {
        this.model = model;
        this.storage = storage;
        this.modeManager = modeManager;
        this.addressBookParser = new AddressBookParser(modeManager::getMode);
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        // Take a snapshot to revert in case of storage failure
        ReadOnlyAddressBook addressBookSnapshot = new AddressBook(model.getAddressBook());
        Command command = addressBookParser.parseCommand(commandText);
        CommandContext context = new CommandContext(model, modeManager.getMode());
        CommandResult commandResult = command.execute(context);

        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (AccessDeniedException e) {
            model.setAddressBook(addressBookSnapshot);
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            model.setAddressBook(addressBookSnapshot);
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        commandResult.getRequestedMode().ifPresent(requestedMode -> {
            modeManager.transitionTo(requestedMode);
            model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS, requestedMode);
        });

        return commandResult;
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return model.getAddressBook();
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList(modeManager.getMode());
    }

    @Override
    public Path getAddressBookFilePath() {
        return model.getAddressBookFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }

    @Override
    public String getAddressBookPassword() {
        return model.getAddressBookPassword();
    }

    @Override
    public void setAddressBookPassword(String password) {
        model.setAddressBookPassword(password);
    }

    @Override
    public void saveAddressBook() throws IOException {
        storage.saveAddressBook(model.getAddressBook());
    }

    @Override
    public AppMode getCurrentMode() {
        return modeManager.getMode();
    }
}

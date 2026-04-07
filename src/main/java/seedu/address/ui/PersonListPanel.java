package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.Region;
import seedu.address.commons.core.index.Index;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 * Implements "Smart Scrolling" and wrap-around "Snapping" to keep navigation natural.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     *
     * @param personList The list of persons to be displayed.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        // Disable focus on the list itself so the CommandBox remains the primary focus owner.
        personListView.setFocusTraversable(false);
    }

    /**
     * Nudges the scrollbar directionally only if the index is not currently visible.
     *
     * @param index The index to ensure visibility for.
     * @param isMovingDown True if navigating forward (bottom-align), false if backward (top-align).
     */
    private void scrollToVisible(int index, boolean isMovingDown) {
        VirtualFlow<?> flow = (VirtualFlow<?>) personListView.lookup(".virtual-flow");

        if (flow == null || flow.getFirstVisibleCell() == null || flow.getLastVisibleCell() == null) {
            personListView.scrollTo(index);
            return;
        }

        int firstIndex = flow.getFirstVisibleCell().getIndex();
        int lastIndex = flow.getLastVisibleCell().getIndex();

        if (isMovingDown) {
            // TAB: If the card is below the current viewport, nudge so it appears at the bottom
            if (index >= lastIndex) {
                int visibleRange = lastIndex - firstIndex;
                int scrollTarget = Math.max(0, index - visibleRange + 1);
                personListView.scrollTo(scrollTarget);
            }
        } else {
            // SHIFT+TAB: If the card is above the current viewport, align it to the top
            if (index <= firstIndex) {
                personListView.scrollTo(index);
            }
        }
    }

    /**
     * Selects the next person in the list.
     * If at the end of the list, wraps around and snaps the scrollbar back to the top.
     */
    public void selectNext() {
        int size = personListView.getItems().size();
        if (size == 0) {
            return;
        }

        int currentIndex = personListView.getSelectionModel().getSelectedIndex();
        int nextIndex = (currentIndex + 1) % size;

        personListView.getSelectionModel().select(nextIndex);

        // Snap-back Logic: If we wrapped from last to first
        if (nextIndex == 0 && currentIndex == size - 1) {
            personListView.scrollTo(0);
        } else {
            scrollToVisible(nextIndex, true);
        }
    }

    /**
     * Selects the previous person in the list.
     * If at the start of the list, wraps around and snaps the scrollbar to the bottom.
     */
    public void selectPrevious() {
        int size = personListView.getItems().size();
        if (size == 0) {
            return;
        }

        int currentIndex = personListView.getSelectionModel().getSelectedIndex();
        int prevIndex = (currentIndex <= 0) ? size - 1 : currentIndex - 1;

        personListView.getSelectionModel().select(prevIndex);

        // If we wrapped from first to last
        if (prevIndex == size - 1 && currentIndex == 0) {
            scrollToVisible(prevIndex, true);
        } else {
            scrollToVisible(prevIndex, false);
        }
    }

    /**
     * Selects the first person in the list and forces a scroll to the top.
     */
    public void selectFirst() {
        if (personListView.getItems().isEmpty()) {
            return;
        }
        personListView.getSelectionModel().selectFirst();
        personListView.scrollTo(0);
    }

    /**
     * Selects the last person in the list and forces a scroll to the bottom.
     */
    public void selectLast() {
        int size = personListView.getItems().size();
        if (size == 0) {
            return;
        }
        personListView.getSelectionModel().selectLast();
        // Bottom align for last item
        scrollToVisible(size - 1, true);
    }

    /**
     * Returns true if no person is currently selected.
     *
     * @return True if selection does not exist.
     */
    public boolean isSelectionEmpty() {
        return personListView.getSelectionModel().getSelectedIndex() < 0;
    }

    public void setOnSelectionChange(java.util.function.Consumer<Person> onSelectionChange) {
        personListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            onSelectionChange.accept(newValue);
        });
    }

    /**
     * Selects the given person in the list and scrolls to make it visible.
     * Uses ListView's built-in selection model and scrollTo for minimal scrolling.
     *
     * @param person the person to select (must be in the current filtered list)
     */
    public void select(Person person) {
        int index = personListView.getItems().indexOf(person);
        if (index >= 0) {
            personListView.scrollTo(index);
            personListView.getSelectionModel().select(index);
        }
    }

    /**
     * Selects the given 1-based index in the list and scrolls to make it visible.
     */
    public void select(Index index) {
        int zeroBasedIndex = index.getZeroBased();
        if (zeroBasedIndex >= 0 && zeroBasedIndex < personListView.getItems().size()) {
            personListView.scrollTo(zeroBasedIndex);
            personListView.getSelectionModel().select(zeroBasedIndex);
        }
    }

    /**
     * Clears any current selection in the person list.
     */
    public void clearSelection() {
        personListView.getSelectionModel().clearSelection();
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1).getRoot());
            }
        }
    }
}

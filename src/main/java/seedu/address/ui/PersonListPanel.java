package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons with "Smart Scrolling" logic.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";

    @FXML
    private ListView<Person> personListView;

    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        personListView.setFocusTraversable(false);
    }

    /**
     * Helper to scroll only if the index is not currently visible.
     */
    private void scrollToVisible(int index) {
        // Find VirtualFlow (internal part of ListView)
        VirtualFlow<?> flow = (VirtualFlow<?>) personListView.lookup(".virtual-flow");
        if (flow == null) {
            personListView.scrollTo(index); // Fallback
            return;
        }

        int firstIndex = flow.getFirstVisibleCell().getIndex();
        int lastIndex = flow.getLastVisibleCell().getIndex();

        if (index <= firstIndex || index >= lastIndex) {
            personListView.scrollTo(index);
        }
        // If index is between first and last, do nothing
    }

    public void selectNext() {
        int size = personListView.getItems().size();
        if (size == 0) return;

        int currentIndex = personListView.getSelectionModel().getSelectedIndex();
        int nextIndex = (currentIndex + 1) % size;

        personListView.getSelectionModel().select(nextIndex);
        scrollToVisible(nextIndex);
    }

    public void selectPrevious() {
        int size = personListView.getItems().size();
        if (size == 0) return;

        int currentIndex = personListView.getSelectionModel().getSelectedIndex();
        int prevIndex = (currentIndex <= 0) ? size - 1 : currentIndex - 1;

        personListView.getSelectionModel().select(prevIndex);
        scrollToVisible(prevIndex);
    }

    public void selectFirst() {
        if (personListView.getItems().isEmpty()) return;
        personListView.getSelectionModel().selectFirst();
        personListView.scrollTo(0); // Safe to jump to top for first item
    }

    public void selectLast() {
        int size = personListView.getItems().size();
        if (size == 0) return;
        personListView.getSelectionModel().selectLast();
        personListView.scrollTo(size - 1); // Safe to jump to bottom for last item
    }

    public boolean isAnySelected() {
        return personListView.getSelectionModel().getSelectedIndex() >= 0;
    }

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
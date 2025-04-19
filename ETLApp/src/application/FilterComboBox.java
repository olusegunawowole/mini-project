package application;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The Class FilterComboBox.
 */
public class FilterComboBox extends ComboBox< String > {
    private String ch;
    private String prevCh = "sos";
    private ListView listView;
    private int index,  firstIndex;
    public FilterComboBox(String[] array) {
	super();
	ObservableList<String> items = FXCollections.observableArrayList();
	for(int i = 0; i < array.length; i++) {
	    items.add(array[i]);
	}
	setItems(items);
	this.setOnKeyReleased(e -> keyReleased(e));
    }

    private void keyReleased(KeyEvent evt) {
	KeyCode code = evt.getCode();
	if(code == KeyCode.DOWN || code == KeyCode.UP) {
	    listView = getListView();
	    index = listView.getSelectionModel().getSelectedIndex();
	    prevCh =  getSelectionModel().getSelectedItem().substring(0, 1).toLowerCase();
	}	
	if(!code.isLetterKey()) 
	    return;
	ch = evt.getText().toLowerCase();
	if(ch.equalsIgnoreCase(prevCh)) {
	    index++;
	    if (index < getItems().size() && getItems().get(index).toString().toLowerCase().startsWith(ch.toString())) {
		listView.getSelectionModel().selectNext();
		scroll();
	    }
	    else {
		index = firstIndex;
		listView.getSelectionModel().clearAndSelect(index);
		scroll();
	    }     
	}
	else {
	    for (int i = 0; i < getItems().size(); i++) {
		if (code.isLetterKey() && getItems().get(i).toString().toLowerCase().startsWith(ch.toString())) {
		    listView = getListView();
		    listView.getSelectionModel().clearAndSelect(i);
		    scroll();
		    index = i;
		    firstIndex = i;
		    break;
		}
	    }
	}
	prevCh = ch;
    }
    private void scroll() {
	listView = getListView();
	int selectedIndex = listView.getSelectionModel().getSelectedIndex();
	listView.scrollTo(selectedIndex == 0 ? selectedIndex : selectedIndex - 9);
    }

    private ListView getListView() {
	return ((ComboBoxListViewSkin) this.getSkin()).getListView();
    }
}

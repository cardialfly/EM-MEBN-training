package unbbayes.gui.mebn.extension.output;


import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import unbbayes.io.mebn.em.ReasoningOutputTableModel;


/**
 * Renders the output of {@link unbbayes.gui.mebn.extension.EMMEBNModule}
 * as a table.
 * @author cardialfly
 */
public class OutputRenderer extends JPanel {
    
	private static final long serialVersionUID = 7722768688663765705L;
	
	private JTable table;
    private JTextField filterText;
    private JLabel statusText;
	private TableRowSorter<ReasoningOutputTableModel> sorter;
	
	/**
	 * Renders the output of {@link unbbayes.gui.mebn.extension.EMMEBNModule}
	 * as a table.
	 * @param reasoningResult : the text file created as a result
	 * of {@link unbbayes.gui.mebn.extension.EMMEBNModule}
	 * ran in reasoning mode
	 */
	public OutputRenderer(InputStream reasoningResult) {
		this(new ReasoningOutputTableModel(reasoningResult));
	}
	
	/**
	 * Renders the output of {@link unbbayes.gui.mebn.extension.EMMEBNModule}
	 * as a table.
	 * @param model : rendered from text file created as a result
	 * of {@link unbbayes.gui.mebn.extension.EMMEBNModule}
	 * ran in reasoning mode
	 */
    public OutputRenderer(ReasoningOutputTableModel model) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //Create a table with default sorter.
        sorter = new TableRowSorter<ReasoningOutputTableModel>(model);
        table = new JTable(model);
        table.setRowSorter(sorter);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // show row numbers
        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        int viewRow = table.getSelectedRow();
                        if (viewRow < 0) {
                            //Selection got filtered away.
                            statusText.setText("");
                        } else {
                            int modelRow = 
                                table.convertRowIndexToModel(viewRow);
                            statusText.setText(
                                String.format("%d (original row = %d)", 
                                    viewRow, modelRow));
                        }
                    }
                }
        );


        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);

        //Create a separate form for filterText and statusText
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel l1 = new JLabel("Filter:", SwingConstants.TRAILING);
        form.add(l1);
        filterText = new JTextField(20);
        //Whenever filterText changes, invoke newFilter.
        filterText.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        updateFilter();
                    }
                    public void insertUpdate(DocumentEvent e) {
                        updateFilter();
                    }
                    public void removeUpdate(DocumentEvent e) {
                        updateFilter();
                    }
                });
        l1.setLabelFor(filterText);
        form.add(filterText);
        JLabel l2 = new JLabel("Selected row:", SwingConstants.TRAILING);
        form.add(l2);
        statusText = new JLabel();
        l2.setLabelFor(statusText);
        form.add(statusText);
        add(form);
    }
    

    /** 
     * Update the row filter regular expression from the expression in
     * the text box.
     */
    private void updateFilter() {
        RowFilter<ReasoningOutputTableModel, Object> rf = null;
        try {
            rf = RowFilter.regexFilter(filterText.getText());
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
    }


    

	
	/**
	 * This is just a sample/test
	 * @param args
	 */
    public static void showRenderedOutput(InputStream stream) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Create and set up the window.
                JFrame frame = new JFrame("Output");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                
                //Create and set up the content pane.
                OutputRenderer newContentPane = new OutputRenderer(stream);
                newContentPane.setOpaque(true); //content panes must be opaque
                frame.setContentPane(newContentPane);

                //Display the window.
                frame.pack();
                frame.setVisible(true);
            
            }
        });
    }
	
}
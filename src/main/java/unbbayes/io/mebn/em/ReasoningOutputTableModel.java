package unbbayes.io.mebn.em;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;


/**
 * Default table model for output of reasoning mode of
   {@link unbbayes.gui.mebn.extension.EMMEBNModule}.
 * Input format is like:
 * <pre>
 * stepstyle__T197 double 
 *  40,86%
 * stepstyle__T197 single 
 *  59,14%
 * stepstyle__T196 double 
 *  40,93%
 * stepstyle__T196 single 
 *  59,07%
 * </pre>
 * Output format (table) is like:
 * <pre>
 * ------------------------------------------
 * | Node            | State  | Probability |
 * ------------------------------------------
 * | stepstyle__T197 | double | 40,86%      |
 * | stepstyle__T197 | single | 40,86%      |
 * | stepstyle__T196 | double | 40,93%      |
 * | stepstyle__T196 | single | 59,07%      |
 * ------------------------------------------
 * </pre>
 */
public class ReasoningOutputTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 9190538923510847205L;
	
	public static final String NODE_COLUMN = "Node";
	public static final String STATE_COLUMN = "State";
	public static final String PROBABILITY_COLUMN = "Probability";
	
	private final String[] columnNames = {NODE_COLUMN,
									STATE_COLUMN,
									PROBABILITY_COLUMN};
	
    private Object[][] data = new Object[0][0];
    
    private NumberFormat percentFormat = NumberFormat.getPercentInstance(); {
    	percentFormat.setMinimumFractionDigits(2);
    	percentFormat.setMaximumFractionDigits(4);
    }

    /**
     * Instantiates a default table model 
     * for output of reasoning mode of 
     * {@link unbbayes.gui.mebn.extension.EMMEBNModule}.
     * @param reasoningResult : text in a format like:
     * <pre>
	 * stepstyle__T197 double 
	 *  40,86%
	 * stepstyle__T197 single 
	 *  59,14%
	 * stepstyle__T196 double 
	 *  40,93%
	 * stepstyle__T196 single 
	 *  59,07%
	 * </pre>
	 * @see #getDataFromStream(InputStream)
     */
    public ReasoningOutputTableModel(InputStream reasoningResult) {
    	if (reasoningResult != null) {
    		try {
				setData(getDataFromStream(reasoningResult));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
    	}
	}

    /**
     * Parses the input stream (assuming it's a text)
     * and render it to a matrix/table.
     * @param reasoningResult : stream to parse,
     * in a format like:
	 * <pre>
	 * stepstyle__T197 double 
	 *  40,86%
	 * stepstyle__T197 single 
	 *  59,14%
	 * stepstyle__T196 double 
	 *  40,93%
	 * stepstyle__T196 single 
	 *  59,07%
	 * </pre>
     * @return data matrix in a format like
     * <pre>
     * ------------------------------------------
	 * | stepstyle__T197 | double | 40,86%      |
	 * | stepstyle__T197 | single | 40,86%      |
	 * | stepstyle__T196 | double | 40,93%      |
	 * | stepstyle__T196 | single | 59,07%      |
	 * ------------------------------------------
     * </pre>
     * @throws IOException 
     */
	public String[][] getDataFromStream(InputStream reasoningResult) throws IOException {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(reasoningResult));
		StreamTokenizer st = new StreamTokenizer(reader);
		st.resetSyntax();
		st.eolIsSignificant(true);	// line separates prob with node/state
		st.whitespaceChars(' ',' ');
		st.whitespaceChars('\t','\t');
		st.wordChars('a', 'z');
		st.wordChars('A', 'Z');
		st.wordChars(128 + 32, 255);
		st.wordChars('0', '9');	// force numbers to be considered as word
		// force some special characters to be considered as part of the word
		st.wordChars('.', '.');
		st.wordChars('_', '_');
		st.wordChars('%', '%');
		st.wordChars(',', ',');	
		
		// will be filled with triples (node, column, probability)
		List<Map<String, String>> records = new ArrayList<Map<String,String>>();
		while (st.nextToken() != StreamTokenizer.TT_EOF) {
			Map<String, String> record = new HashMap<>();	// triple (node, column, probability)
			// read node name
			if ( st.ttype == StreamTokenizer.TT_WORD ) {
				record.put(NODE_COLUMN, st.sval.trim());
			} else {
				throw new IOException("Expected node name at line " + st.lineno());
			}
			
			// read node state
			if ( st.nextToken() == StreamTokenizer.TT_WORD ) {
				record.put(STATE_COLUMN, st.sval.trim());
			} else {
				throw new IOException("Expected state name at line " + st.lineno());
			}
			
			// expect new line
			while (st.ttype != StreamTokenizer.TT_EOF ) {
				st.nextToken();
				if (st.ttype != StreamTokenizer.TT_EOL
						&& st.ttype != '\r') {
					break;
				}
			}
			// read probability
			if ( st.ttype == StreamTokenizer.TT_WORD ) {
				String probLabel = st.sval.trim();
				// replace , with .
				probLabel = probLabel.replace(',', '.');
				// remove %
				boolean isPercent = false;
				if (probLabel.contains("%")) {
					isPercent = true;
					probLabel = probLabel.replace("%", "");
				}
				// extract numeric value
				Float numValue = Float.parseFloat(probLabel);
				if (isPercent) {
					numValue /= 100;
				}
				// convert to percent format of current default locale
				probLabel = getPercentFormat().format(numValue);
				
				record.put(PROBABILITY_COLUMN, probLabel);
			} else {
				throw new IOException("Expected probability at line " + st.lineno());
			}
			
			
			// go to next block (in next line)
			while (st.ttype != StreamTokenizer.TT_EOF ) {
				st.nextToken();
				if (st.ttype != StreamTokenizer.TT_EOL
						&& st.ttype != '\r') {
					st.pushBack();
					break;
				}
			}
			
			records.add(record);
			
		}
		
		reader.close();
		
		// convert records to matrix
		String[][] ret = new String[records.size()][3];
		for (int row = 0; row < records.size(); row++) {
			Map<String, String> record = records.get(row);
			ret[row][0] = record.get(NODE_COLUMN);
			ret[row][1] = record.get(STATE_COLUMN);
			ret[row][2] = record.get(PROBABILITY_COLUMN);
		}
		
		return ret;
	}

	@Override
	public int getColumnCount() {
        return getColumnNames().length;
    }

	@Override
    public int getRowCount() {
        return getData().length;
    }

	@Override
    public String getColumnName(int col) {
        return getColumnNames()[col];
    }

	@Override
    public Object getValueAt(int row, int col) {
        return getData()[row][col];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

	/**
	 * @return the columnNames
	 */
	public String[] getColumnNames() {
		return columnNames;
	}

	/**
	 * @return the data
	 * @see #getDataFromStream(InputStream)
	 */
	public Object[][] getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 * @see #getDataFromStream(InputStream)
	 */
	public void setData(Object[][] data) {
		this.data = data;
	}

	/**
	 * @return the percentFormat
	 */
	public NumberFormat getPercentFormat() {
		return percentFormat;
	}

	/**
	 * @param percentFormat the percentFormat to set
	 */
	public void setPercentFormat(NumberFormat percentFormat) {
		this.percentFormat = percentFormat;
	}

}

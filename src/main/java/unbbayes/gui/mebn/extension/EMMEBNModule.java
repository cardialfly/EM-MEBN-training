package unbbayes.gui.mebn.extension;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import unbbayes.controller.FileHistoryController;
import unbbayes.gui.FileIcon;
import unbbayes.gui.SimpleFileFilter;
import unbbayes.gui.UnBBayesFrame;
import unbbayes.io.BaseIO;
import unbbayes.io.OwnerAwareFileExtensionIODelegator;
import unbbayes.io.mebn.MebnIO;
import unbbayes.io.mebn.UbfIO;
import unbbayes.prs.Graph;
import unbbayes.prs.mebn.em.MEBNReasoning;
import unbbayes.util.Debug;
import unbbayes.util.extension.UnBBayesModule;
import unbbayes.util.extension.UnBBayesModuleBuilder;

/**
 * This class converts the EM-MEBN-training tool to a UnBBayes module plugin.
 * @author Shou Matsumoto
 */
public class EMMEBNModule extends UnBBayesModule implements UnBBayesModuleBuilder {


	private String name = "EM-MEBN-training";
	
	private BaseIO io;
	
	private MEBNReasoning mebnReasoning;

	private String trainedFileNamePrefix = "Newexcl";
	
	/** Load resource file from this package */
	private static ResourceBundle resource = unbbayes.util.ResourceController.newInstance().getBundle(
			unbbayes.gui.mebn.extension.resources.EMMEBNResources.class.getName(), Locale.getDefault(),
			EMMEBNModule.class.getClassLoader());
  	
	
	public EMMEBNModule() {
		super();
		
		// setting up the i/o classes used by UnBBayesFrame in order to load a file from the main pane
		OwnerAwareFileExtensionIODelegator fileIO = new OwnerAwareFileExtensionIODelegator(this);
		fileIO.setDelegators(Collections.singletonList((BaseIO)UbfIO.getInstance()));
		this.io = fileIO;
		
		// let this frame to be invisible
		this.setVisible(false);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	public void setVisible(boolean visible){
		// this is a workarount to assure this is always invisible
		super.setVisible(false);
	};

	/* (non-Javadoc)
	 * @see unbbayes.util.extension.UnBBayesModuleBuilder#buildUnBBayesModule()
	 */
	public UnBBayesModule buildUnBBayesModule() {
		return this;
	}
	

	/**
	 * Shows a file chooser to the user.
	 * 
	 * @param title         : message to be shown to user
	 * @param fileFilter    : filter to be used to limit the files visible in file
	 *                      chooser
	 * @param selectionMode : {@link JFileChooser#FILES_ONLY},
	 *                      {@link JFileChooser#FILES_AND_DIRECTORIES}, or
	 *                      {@link JFileChooser#DIRECTORIES_ONLY}
	 * @param defaultDir    : default directory to look for
	 * @return the selected file. Null if no file was selected.
	 * @see JFileChooser#showOpenDialog(java.awt.Component)
	 */
	public File showFileChooser(String title, FileFilter fileFilter, int selectionMode, File defaultDir) {
		JFileChooser chooser = new JFileChooser();
		if (defaultDir != null) {
			chooser.setCurrentDirectory(defaultDir);
		}
		chooser.setDialogTitle(title);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(selectionMode);

		if (fileFilter != null) {
			chooser.addChoosableFileFilter(fileFilter);
			chooser.setFileFilter(fileFilter);	// make sure this is selected by default
		}


		int option = chooser.showOpenDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			try {
				// update file history
				FileHistoryController.getInstance().setCurrentDirectory(chooser
						.getCurrentDirectory());
			} catch (Exception e) {
				e.printStackTrace();
			}
			chooser.setVisible(false);
			chooser.setEnabled(false);
			return chooser.getSelectedFile();
		}

		// no selection
		chooser.setVisible(false);
		chooser.setEnabled(false);
		return null;
	}
	
	/**
	 * This is a workaround in order to start process only when the UnBBayesFrame is
	 * set (that means this module has been added as its internal frame, since this
	 * method is called on UnBBayesFrame#add(JUnBBayesModule))
	 * 
	 * @see unbbayes.util.extension.UnBBayesModule#setUnbbayesFrame(unbbayes.gui.UnBBayesFrame)
	 */
	public void setUnbbayesFrame(UnBBayesFrame unbbayesFrame) {
		super.setUnbbayesFrame(unbbayesFrame);
		try {
			// Load mebn file (filter non-mebn files)
			SimpleFileFilter fileFilter = new SimpleFileFilter(
					getIO().getSupportedFileExtensions(true),
					getIO().getSupportedFilesDescription(true));
			File mebnFile = this.showFileChooser(resource.getString("openMEBNFile"), fileFilter, JFileChooser.FILES_ONLY, new File("models/MEBNmodel/MEBNMultimodalTsamikoUntrained.ubf"));
			this.openFile(mebnFile);
		} catch (Throwable t) {
			t.printStackTrace();
			JOptionPane.showMessageDialog(null, t.toString());
		}
		
		unbbayesFrame.getDesktop().remove(this);
		this.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see unbbayes.util.extension.UnBBayesModule#openFile(java.io.File)
	 */
	public UnBBayesModule openFile(File mebnFile) throws IOException {
		
		// check input
		if (mebnFile == null
				|| !mebnFile.exists()
				|| !mebnFile.isFile()) {
			JOptionPane.showMessageDialog(null, resource.getObject("InvalidMEBNFile"));
			return null;
		}
		
		// choose PLM folder
		File plmFolder = this.showFileChooser(resource.getString("openPLMFile"), null, JFileChooser.DIRECTORIES_ONLY, new File("models/TsamikoPLMs/"));
		if (plmFolder == null
				|| !plmFolder.exists()
				|| !plmFolder.isDirectory()) {
			JOptionPane.showMessageDialog(null, resource.getObject("InvalidPLMFolder"));
			return null;
		}
		
		// choose output folder;
		File outFolder = this.showFileChooser(resource.getString("openOutputFolder"), null, JFileChooser.DIRECTORIES_ONLY, new File("models/MEBNmodel/"));
		if (outFolder == null) {
			JOptionPane.showMessageDialog(null, resource.getObject("InvalidOutputFolder"));
			return null;
		}
		
		// make sure directories are created recursively (if not present)
		if (!outFolder.exists()) {
			if (!outFolder.mkdirs()) {
				JOptionPane.showMessageDialog(null, resource.getObject("FailedToCreateOutputFolder"));
				return null;
			}
		}
		
		// generalEMIter: max number of EM iterations (int>0)
		int generalEMIter = 5;
		String val = JOptionPane.showInputDialog(null, resource.getObject("generalEMIterMessage"), ""+generalEMIter);
		try {
			generalEMIter = Integer.parseInt(val);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, resource.getObject("EmptyValue"));
			return null;
		}
		if (generalEMIter <= 0) {
			JOptionPane.showMessageDialog(null, resource.getObject("EmptyValue"));
			return null;
		}
		
		// fileexl: the number of the file to be excluded from the training set (>1 or 0 for no exclusion).
		int fileexl = 1;	// default value
		val = JOptionPane.showInputDialog(null, resource.getObject("fileexlMessage"), ""+fileexl);
		try {
			fileexl = Integer.parseInt(val);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, resource.getObject("EmptyValue"));
			return null;
		}
		if (fileexl < 0) {
			JOptionPane.showMessageDialog(null, resource.getObject("EmptyValue"));
			return null;
		}
		
		// queryvariablename: the variable name (resident node) of query
		// which is used subsequently in to retrieve resident nodes of the MEBN we want to "learn" probabilities with the EM algorithm. 
		String queryvariablename = "step";
		queryvariablename = JOptionPane.showInputDialog(null, resource.getObject("queryvariablenameMessage"), queryvariablename);
		if (queryvariablename == null || queryvariablename.isEmpty()) {
			JOptionPane.showMessageDialog(null, resource.getObject("EmptyValue"));
			return null;
		}
		
		// ovinstances: specific logical argument(s) (ordinary instance(s)) of query. 
		String[] ovinstances = {"T1"};
		val = JOptionPane.showInputDialog(null, resource.getObject("ovinstancesMessage"), ovinstances[0]);
		if (val == null || val.isEmpty()) {
			JOptionPane.showMessageDialog(null, resource.getObject("EmptyValue"));
			return null;
		}
		if (val.contains(",")) {
			ovinstances = val.split(",");
		} else {
			ovinstances[0] = val;
		}

		// extract the mode from 2 options: { "reasoning", "training" }
		String[] options = { resource.getObject("reasoningMode").toString(),  resource.getObject("trainingMode").toString()};
		val = (String) JOptionPane.showInputDialog(null, resource.getObject("trainingModeMessage"),
				resource.getObject("trainingModeTitle").toString(), JOptionPane.QUESTION_MESSAGE, null, options,
				options[0]);
		if (val == null || val.isEmpty()) {
			JOptionPane.showMessageDialog(null, resource.getObject("EmptyValue"));
			return null;
		}

		try {
			// where to include or look for output files
			String outPath = outFolder.getPath();
			// make sure it ends with a /, so that path is considered as a folder.
			if (!outPath.endsWith("/")) {
				outPath += "/";	
			}
			if( val.equals(options[0]) ) {
				System.out.println("Proceed to inference");
				outPath += fileexl + ".txt";
				getMEBNReasoning().MEBNRunInference(fileexl, mebnFile.getPath(), plmFolder.getPath(), outPath, queryvariablename, ovinstances);
			} else {
				System.out.println("Proceed to training");
				getMEBNReasoning().MEBNTraining(generalEMIter, fileexl,  mebnFile.getPath(), plmFolder.getPath(), outPath, queryvariablename, ovinstances);
				String mebnfiletrained = new String( outPath  + getTrainedFileNamePrefix() + fileexl + ".ubf" );
				getMEBNReasoning().MEBNCorrection(fileexl, mebnfiletrained, plmFolder.getPath(), outPath, queryvariablename, ovinstances);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// print the stack trace to a string (to byte array)
			ByteArrayOutputStream stackTraceStream = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(stackTraceStream));
			// show error message containing stack trace and error message
			JOptionPane.showMessageDialog(null, stackTraceStream.toString(), e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		// show message indicating completion
		JOptionPane.showMessageDialog(null, resource.getObject("OutputGeneratedAt") + outFolder.getPath());
		
		return null;	// do not add module to window
	}


	/*
	 * (non-Javadoc)
	 * @see unbbayes.util.extension.UnBBayesModuleBuilder#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see unbbayes.util.extension.UnBBayesModuleBuilder#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * @see unbbayes.util.extension.UnBBayesModule#getModuleName()
	 */
	public String getModuleName() {
		return this.getName();
	}


	/*
	 * (non-Javadoc)
	 * @see unbbayes.gui.IPersistenceAwareWindow#getPersistingGraph()
	 */
	public Graph getPersistingGraph() {
		return null;
	}
	
	

	/*
	 * (non-Javadoc)
	 * @see unbbayes.gui.IPersistenceAwareWindow#getIO()
	 */
	public BaseIO getIO() {
		return io;
	}

	/**
	 * @param io the io to set
	 */
	public void setIO(BaseIO io) {
		this.io = io;
	}

	/**
	 * @return the mebnReasoning
	 */
	public MEBNReasoning getMEBNReasoning() {
		if (mebnReasoning == null) {
			mebnReasoning = new MEBNReasoning();
		}
		return mebnReasoning;
	}

	/**
	 * @param mebnReasoning the mebnReasoning to set
	 */
	public void setMEBNReasoning(MEBNReasoning mebnReasoning) {
		this.mebnReasoning = mebnReasoning;
	}

	/**
	 * @return prefix to be used in the file resulting from training process:
	 *         outFolder.getPath() + getTrainedFileNamePrefix() + fileexl + ".ubf"
	 */
	public String getTrainedFileNamePrefix() {
		return trainedFileNamePrefix;
	}

	/**
	 * @param trainedFileNamePrefix : prefix to be used in the file resulting from
	 *                              training process: outFolder.getPath() +
	 *                              getTrainedFileNamePrefix() + fileexl + ".ubf"
	 */
	public void setTrainedFileNamePrefix(String trainedFileNamePrefix) {
		this.trainedFileNamePrefix = trainedFileNamePrefix;
	}

	

}

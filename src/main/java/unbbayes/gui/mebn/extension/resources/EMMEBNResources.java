/*
 *  UnBBayes
 *  Copyright (C) 2002, 2008 Universidade de Brasilia - http://www.unb.br
 *
 *  This file is part of UnBBayes.
 *
 *  UnBBayes is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  UnBBayes is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with UnBBayes.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package unbbayes.gui.mebn.extension.resources;

import java.util.ListResourceBundle;

/**
 * Resources for the monte carlo package.
 * @author Rommel Carvalho (rommel.carvalho@gmail.com)
 */

public class EMMEBNResources extends ListResourceBundle {

    /**
	 *  Override getContents and provide an array, where each item in the array is a pair
	 *	of objects. The first element of each pair is a String key,
	 *	and the second is the value associated with that key.
	 *
	 * @return The resources' contents
	 */
	public Object[][] getContents() {
		return contents;
	}

	/**
	 * The resources
	 */
	static final Object[][] contents =
	{	


		{"openMEBNFile", "Select MEBN file"},
		{"openPLMFile", "Select folder with PLMs"},
		{"openOutputFolder", "Select output folder"},
		{"InvalidMEBNFile", "MEBN file was not present or it was invalid."},
		{"InvalidPLMFolder", "PLM folder was not present or it was invalid."},
		{"InvalidOutputFolder", "Output folder was not present or it was invalid."},
		{"FailedToCreateOutputFolder", "Could not create output folder. Please, check for permissions."},
		
		{"EmptyValue", "Empty or invalid value was provided."},
		
		{"generalEMIterMessage", "Provide the max number of EM iterations (int>0)"},
		{"fileexlMessage", "Provide the number of the file to be excluded from the training set (>=1 or 0 for no exclusion)"},
		{"queryvariablenameMessage", "Provide the variable name (resident node) to be queried. \n"
				+ "This is used subsequently in a query to retrieve all the resident nodes of the MEBN which we want to \"learn\" their probabilities with the EM algorithm. \n"
				+ "Thus, please be sure that you choose the correct one in this context."},
		{"ovinstancesMessage", "Provide a comma-separated list with specific logical argument(s) \n (ordinary instance(s)) of the previously provided query variable."},
		
		{"trainingModeTitle", "Mode."},
		{"reasoningMode", "reasoning"},
		{"trainingMode", "training"},
		{"trainingModeMessage", "Choose training or reasoning mode"},
		
		{"OutputGeneratedAt", "Process completed. Output was generated at: "},
		
	};
}
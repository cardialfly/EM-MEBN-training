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

public class EMMEBNResources_pt extends ListResourceBundle {

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


		{"openMEBNFile", "Selecione o arquivo MEBN"},
		{"openPLMFile", "Selecione o diretorio contendo PLMs"},
		{"openOutputFolder", "Selecione diretorio de saida"},
		{"InvalidMEBNFile", "Arquivo nao encontrado ou invalido."},
		{"InvalidPLMFolder", "Diretorio PLM nao encontrado ou invalido."},
		{"InvalidOutputFolder", "Diretorio de saida nao encontrado ou invalido."},
		{"FailedToCreateOutputFolder", "Nao foi possivel criar o diretorio de saida. Por favor, verifique as permissoes."},
		
		{"EmptyValue", "Valor invalido ou vazio fornecido."},
		
		{"generalEMIterMessage", "Forneca o numero maximo de iteracoes EM (int>0)"},
		{"fileexlMessage", "Forneca o numero de arquivos a ser excluido do conjunto de aprendizagem (>=1 ou 0 para nao excluir)"},
		{"queryvariablenameMessage", "Forneca o nome da variavel (no residente) a consultar. \n"
				+ "Este valor sera usado para retribuir todos os nos a passar pela etapa de \"aprendizagem\" com algoritmo EM. \n"
				+ "Portanto, por favor forneca um nome correto neste contexto."},
		{"ovinstancesMessage", "Forneca uma lista separada de virgulas com argumento(s) \n (instancias de variaveis ordinarias) da variavel de consulta."},
		
		{"trainingModeTitle", "Modo."},
		{"reasoningMode", "raciocinio"},
		{"trainingMode", "treinamento"},
		{"trainingModeMessage", "Selecione treinamento (modo de aprendizagem) ou raciocinio (modo de inferencia)"},
		
		{"OutputGeneratedAt", "Processo concluido. Saida gerada em: "},
		
	};
}
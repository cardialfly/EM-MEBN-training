/**
 * 
 */
package unbbayes.io.mebn.em;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import unbbayes.io.exception.LoadException;
import unbbayes.io.mebn.UbfIO;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;
import unbbayes.prs.mebn.RandomVariableFinding;
import unbbayes.prs.mebn.ResidentNode;
import unbbayes.prs.mebn.em.MebnUtil;
import unbbayes.prs.mebn.entity.Entity;
import unbbayes.prs.mebn.entity.ObjectEntity;
import unbbayes.prs.mebn.entity.ObjectEntityInstance;
import unbbayes.prs.mebn.exception.MEBNException;

/**
 * @author Shou Matsumoto
 *
 */
public class MebnUtilLoadFindingsTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link unbbayes.prs.mebn.em.MebnUtil#loadFindingsFile(java.io.File)}.
	 * @throws IOException 
	 * @throws LoadException 
	 * @throws MEBNException 
	 */
	@Test
	public final void testLoadFindingsFile() throws LoadException, IOException, MEBNException {
		// the files to be used for this test
		File ubfFile = new File("models/MEBNmodel/MEBNMultimodalTsamikoUntrained.ubf");
		File plmFile = new File("models/TsamikoPLMs/Tsamiko5.plm");
		
		assertTrue(ubfFile.exists());
		assertTrue(plmFile.exists());

		// load the mtheory
		UbfIO ubf = UbfIO.getInstance();
		MultiEntityBayesianNetwork mebn = (MultiEntityBayesianNetwork) ubf.load(ubfFile);
		assertNotNull(mebn);
		
		// instantiate the mebn util 
		MebnUtil mebnUtil = new MebnUtil(mebn);
		
		// this is to make sure instances stored directly in ontology/t-box  (e.g. T1 - T211)
		// are not considered, so that only the instances declared in the plm files are considered.
		mebnUtil.removeAllEntityInstances();
		
		// load findings
		mebnUtil.loadFindingsFile(plmFile);
		
		// following is an example of how to access asserted typed instances (e.g., T1 of type TimeStep)
		
		// retrieve the entity (type/class) by name
		ObjectEntity timeEntity = mebn.getObjectEntityContainer().getObjectEntityByName("TimeStep");
		
		// access all the instances of this entities
		Set<ObjectEntityInstance> instances = timeEntity.getInstanceList();
		assertNotNull(instances);
		// T1 - T177
		assertEquals(177, instances.size());
		// instances can also be retrieved by name
		for (int i = 1; i <= 177; i++) {
			ObjectEntityInstance instance = timeEntity.getInstanceByName("T" + i);
			assertNotNull(instance);
		}
		
		// following is an example of how to access assertions about resident nodes 
		// e.g., (ASSERT (=(HASDIRECTION T132) RIGHTDIRECTION))
		
		// the resident node with the assertion
		ResidentNode resident = mebn.getDomainResidentNode("hasDirection");
		
		// extract all the assertions related to this resident node
		List<RandomVariableFinding> findings = resident.getRandomVariableFindingList();
		// (ASSERT (=(HASDIRECTION X)), with X = {T1 - T177}
		assertEquals(177, findings.size());
		// access each entry
		for (RandomVariableFinding finding : findings) {
			// the argument should be T1 - T177
			ObjectEntityInstance[] args = finding.getArguments();
			assertNotNull(args);
			assertEquals(1, args.length);
			assertEquals(timeEntity, args[0].getInstanceOf());
			int timeIndex = Integer.parseInt(args[0].getInstanceName().split("T")[1]);
			assertTrue(timeIndex >= 1);
			assertTrue(timeIndex <= 177);
			
			// the state (possible value asserted for the node) must be rightDirection or leftDirection
			Entity assertedState = finding.getState();
			assertNotNull(assertedState);
			assertTrue(assertedState.getName().equalsIgnoreCase("rightDirection")
					|| assertedState.getName().equalsIgnoreCase("leftDirection"));
		}
		
	}

}

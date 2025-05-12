package ar.com.moodle;

import org.junit.jupiter.api.Test;

public class MoodleProcessTest {

	@Test
	public void testExecuteProcess() throws Exception {

		MoodleProcess process = new MoodleProcess();

//		process.executeProcessTest("users.file.path.test");
//		process.executeProcess("users.file.path.test");
		process.executeProcessJnext();
	}

}

package ar.com.moodle;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MoodleProcessTest {

	@Test
	void testExecuteProcessJnext() {
		try {
			MoodleProcess process = new MoodleProcess();
			process.executeProcessJnext();
		} catch (Exception e) {
			assertNotNull(null);
		}
	}

	@Test
	void testExecuteProcessFromFile() {
		try {
			MoodleProcess process = new MoodleProcess();
			process.executeProcessFromFile("users.file.path.test");
		} catch (Exception e) {
			assertNotNull(null);
		}
	}

}

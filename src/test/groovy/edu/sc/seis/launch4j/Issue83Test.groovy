package edu.sc.seis.launch4j

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

import edu.sc.seis.launch4j.util.FunctionalSpecification

class Issue83Test extends FunctionalSpecification {

    def 'Check that setting jreMinVersion to empty string does not use default value'() {
        given:
        buildFile << """
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'test.exe'
                jreMinVersion = ''
            }
        """

        File sourceFile = new File(testProjectDir.newFolder('src', 'main', 'java'), 'Main.java')
        sourceFile << """
            package com.test.app;

            public class Main {
                public static void main(String[] args) {
                }
            }
        """

        when:
        def result = build('createExe')
        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def outfile = new File(projectDir, 'build/launch4j/test.exe')
        then:
        outfile.exists()

        when:
        def process = [outfile.path, '--l4j-debug'].execute()
        def logfile = new File(projectDir, 'build/tmp/createExe/createExe.xml')
        then:
        process.waitFor() == 0

        logfile.exists()

        def chdir = logfile.readLines().find { String line ->
            line.contains("<jreMinVersion></jreMinVersion>")
        }
        chdir == null
    }

    def 'Check that not setting jreMinVersion uses default value'() {
        given:
        buildFile << """
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'test.exe'
            }
        """

        File sourceFile = new File(testProjectDir.newFolder('src', 'main', 'java'), 'Main.java')
        sourceFile << """
            package com.test.app;

            public class Main {
                public static void main(String[] args) {
                }
            }
        """

        when:
        def result = build('createExe')
        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def outfile = new File(projectDir, 'build/launch4j/test.exe')
        then:
        outfile.exists()

        when:
        def process = [outfile.path, '--l4j-debug'].execute()
        def logfile = new File(projectDir, 'build/tmp/createExe/createExe.xml')
        then:
        process.waitFor() == 0

        logfile.exists()

		// java 1.6.0 is set in build.gradle as targetCompatibility
        def chdir = logfile.readLines().find { String line ->
            line.contains("<jreMinVersion>1.6.0</jreMinVersion>")
        }
        chdir != null
    }

	def 'Check that not setting a jreMinVersion uses that value'() {
		given:
		buildFile << """
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'test.exe'
				jreMinVersion = '11.0.2'
            }
        """

		File sourceFile = new File(testProjectDir.newFolder('src', 'main', 'java'), 'Main.java')
		sourceFile << """
            package com.test.app;

            public class Main {
                public static void main(String[] args) {
                }
            }
        """

		when:
		def result = build('createExe')
		then:
		result.task(':jar').outcome == SUCCESS
		result.task(':createExe').outcome == SUCCESS

		when:
		def outfile = new File(projectDir, 'build/launch4j/test.exe')
		then:
		outfile.exists()

		when:
		def process = [outfile.path, '--l4j-debug'].execute()
		def logfile = new File(projectDir, 'build/tmp/createExe/createExe.xml')
		then:
		process.waitFor() == 0

		logfile.exists()

		def chdir = logfile.readLines().find { String line ->
			line.contains("<jreMinVersion>11.0.2</jreMinVersion>")
		}
		chdir != null
	}


}

package edu.sc.seis.launch4j

import java.io.File;

import org.gradle.api.internal.ConventionTask

import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CreateLaunch4jXMLTask extends DefaultTask {

    static final Logger LOGGER = LoggerFactory.getLogger(CreateLaunch4jXMLTask)



    @OutputFile
    File getXmlOutFile() {
        return project.launch4j.getXmlOutFileForProject(project)
    }

    @TaskAction
    def void writeXmlConfig() {
        Launch4jPluginExtension configuration = project.launch4j
        def classpath = project.configurations.runtime.collect { "lib/${it.name}" }
        def file = getXmlOutFile()
        file.parentFile.mkdirs()
        def writer = new BufferedWriter(new FileWriter(file))
        def xml = new MarkupBuilder(writer)
        xml.launch4jConfig() {
            dontWrapJar(configuration.dontWrapJar)
            headerType(configuration.headerType)
            jar(configuration.jar)
            outfile(configuration.outfile)
            errTitle(configuration.errTitle)
            cmdLine(configuration.cmdLine)
            chdir(configuration.chdir)
            priority(configuration.priority)
            downloadUrl(configuration.downloadUrl)
            supportUrl(configuration.supportUrl)
            customProcName(configuration.customProcName)
            stayAlive(configuration.stayAlive)
            manifest(configuration.manifest)
            icon(configuration.icon)
            classPath() {
                mainClass(configuration.mainClassName)
                classpath.each() { val -> cp(val ) }
            }
            versionInfo() {
                fileVersion(parseDotVersion(configuration.version) )
                txtFileVersion(configuration.version )
                fileDescription(project.name)
                copyright(configuration.copyright)
                productVersion(parseDotVersion(configuration.version) )
                txtProductVersion(configuration.version )
                productName(project.name )
                internalName(project.name )
                originalFilename(configuration.outfile )
            }
            jre() {
                minVersion('1.5.0' )
                if (configuration.opt.length() != 0) opt(configuration.opt)
            }
        }
        writer.close()
    }

    /**
     * launch4j fileVersion and productVersion are required to be x.y.z.w format, no text like beta or 
     * SNAPSHOT. I think this is a windows thing. So we check the version, and if it is only dots and 
     * numbers, we use it. If not we use 0.0.0.1
     * @param version
     * @return
     */
    String parseDotVersion(version) {
        if (version ==~ /\d+(\.\d+){0,3}/) {
            return version
        } else {
            return '0.0.0.1'
        }
    }

}
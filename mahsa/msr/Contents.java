/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opm.mahsa.msr;

/**
 *
 * @author mahsa
 */
public class Contents {
     public static final String CONTENT = "\"/*\n" +
" *  Licensed to the Apache Software Foundation (ASF) under one\n" +
" *  or more contributor license agreements.  See the NOTICE file\n" +
" *  distributed with this work for additional information\n" +
" *  regarding copyright ownership.  The ASF licenses this file\n" +
" *  to you under the Apache License, Version 2.0 (the\n" +
" *  \"\"License\"\"); you may not use this file except in compliance\n" +
" *  with the License.  You may obtain a copy of the License at\n" +
" *\n" +
" *    http://www.apache.org/licenses/LICENSE-2.0\n" +
" *\n" +
" *  Unless required by applicable law or agreed to in writing,\n" +
" *  software distributed under the License is distributed on an\n" +
" *  \"\"AS IS\"\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY\n" +
" *  KIND, either express or implied.  See the License for the\n" +
" *  specific language governing permissions and limitations\n" +
" *  under the License.\n" +
" */\n" +
"\n" +
"buildscript {\n" +
"    repositories {\n" +
"        jcenter()\n" +
"        maven {\n" +
"            name 'Bintray Asciidoctor repo'\n" +
"            url 'http://dl.bintray.com/content/aalmiray/asciidoctor'\n" +
"        }\n" +
"        maven{\n" +
"            name 'Bintray Javadoc Hotfix repo'\n" +
"            url 'http://dl.bintray.com/melix/gradle-javadoc-hotfix-plugin'\n" +
"        }\n" +
"        maven {\n" +
"            url \"\"https://plugins.gradle.org/m2/\"\"\n" +
"        }\n" +
"    }\n" +
"\n" +
"    dependencies {\n" +
"        // using the old \"\"classpath\"\" style of plugins because the new one doesn't play well with multi-modules\n" +
"        classpath 'org.asciidoctor:asciidoctor-gradle-plugin:1.5.2'\n" +
"        classpath \"\"org.jfrog.buildinfo:build-info-extractor-gradle:3.0.3\"\"\n" +
"        classpath 'me.champeau.gradle:gradle-javadoc-hotfix-plugin:0.1'\n" +
"        //classpath 'me.champeau.gradle:japicmp-gradle-plugin:0.1.1'\n" +
"        //classpath 'nl.javadude.gradle.plugins:license-gradle-plugin:0.11.0'\n" +
"        classpath \"\"gradle.plugin.org.nosphere.apache:creadur-rat-gradle:0.1.3\"\"\n" +
"        classpath \"\"gradle.plugin.com.github.jk1:gradle-license-report:0.3.2\"\"\n" +
"    }\n" +
"}\n" +
"\n" +
"plugins {\n" +
"    id 'com.gradle.build-scan' version '1.6'\n" +
"    id 'me.champeau.buildscan-recipes' version '0.1.7'\n" +
"}\n" +
"\n" +
"buildScan {\n" +
"    licenseAgreementUrl = 'https://gradle.com/terms-of-service'\n" +
"    licenseAgree = 'yes'\n" +
"    publishAlways()\n" +
"    recipe 'git-commit', baseUrl: 'https://github.com/apache/groovy/tree'\n" +
"    recipe 'teamcity', baseUrl: 'https://ci.groovy-lang.org', guest: 'true'\n" +
"    recipes 'git-status', 'gc-stats', 'teamcity', 'travis-ci'\n" +
"}\n" +
"\n" +
"apply from: 'gradle/filter.gradle'\n" +
"apply from: 'gradle/indy.gradle'\n" +
"apply from: 'gradle/publish.gradle'\n" +
"apply plugin: 'javadocHotfix'\n" +
"apply plugin: \"\"com.github.jk1.dependency-license-report\"\"\n" +
"\n" +
"File javaHome = new File(System.getProperty('java.home'))\n" +
"logger.lifecycle \"\"Using Java from $javaHome (version ${System.getProperty('java.version')})\"\"\n" +
"indyBanner()\n" +
"\n" +
"// TODO use antlr plugin\n" +
"//apply plugin: 'antlr'\n" +
"\n" +
"allprojects {\n" +
"    apply plugin: 'java'\n" +
"\n" +
"    buildDir = 'target'\n" +
"    sourceCompatibility = 1.7\n" +
"    targetCompatibility = 1.7\n" +
"\n" +
"    group = 'org.codehaus.groovy'\n" +
"    version = groovyVersion\n" +
"    repositories {\n" +
"        jcenter()\n" +
"        maven { url 'http://dl.bintray.com/melix/thirdparty-apache' } // openbeans\n" +
"    }\n" +
"\n" +
"    apply plugin: 'groovy'\n" +
"    apply from: \"\"${rootProject.projectDir}/gradle/indy.gradle\"\"\n" +
"    if (JavaVersion.current().java7Compatible) {\n" +
"        apply from: \"\"${rootProject.projectDir}/gradle/asciidoctor.gradle\"\"\n" +
"    }\n" +
"}\n" +
"\n" +
"// todo: use the conventional \"\"resources\"\" directory for classpath resources\n" +
"task(copyResources, type: Copy) {\n" +
"    destinationDir = file(\"\"$buildDir/classes\"\")\n" +
"    // text files requiring filtering\n" +
"    into('main') {\n" +
"        from('src/main')\n" +
"        include('**/*.txt', '**/*.xml', '**/*.properties', '**/*.html')\n" +
"        filter(rootProject.propertiesFilter, org.apache.tools.ant.filters.ReplaceTokens)\n" +
"    }\n" +
"    // other resources\n" +
"    into('main') {\n" +
"        from 'src/main'\n" +
"        include('**/*.png', '**/*.gif', '**/*.ico', '**/*.css')\n" +
"    }\n" +
"}\n" +
"compileJava.dependsOn(copyResources)\n" +
"task(copyTestResources, type: Copy)\n" +
"        .from('src/test')\n" +
"        .into(\"\"$buildDir/classes/test\"\")\n" +
"        .include('**/*.txt', '**/*.xml', '**/*.properties', '**/*.png', '**/*.html', '**/*.gif', '**/*.ico', '**/*.css')\n" +
"compileTestJava.dependsOn(copyTestResources)\n" +
"\n" +
"task sourceJar(type: Jar) {\n" +
"    classifier = 'sources'\n" +
"    from 'src/main'\n" +
"}\n" +
"\n" +
"subprojects {\n" +
"    task sourceJar(type: Jar) {\n" +
"        classifier = 'sources'\n" +
"        from sourceSets.main.allSource\n" +
"    }\n" +
"}\n" +
"\n" +
"repositories {\n" +
"    // todo Some repos are needed only for some configs. Declare them just for the configuration once Gradle allows this.\n" +
"    maven { url 'http://repository.jboss.org/nexus/content/groups/m2-release-proxy' } // examples, tools\n" +
"    maven { url 'http://repo.bodar.com' } // tools - jarjar\n" +
"}\n" +
"\n" +
"// todo do we need compile and runtime scope for examples?\n" +
"configurations {\n" +
"    compilerCompile\n" +
"    tools\n" +
"    examplesCompile.extendsFrom compile\n" +
"    examplesRuntime.extendsFrom examplesCompile\n" +
"    antlr\n" +
"    spec\n" +
"}\n" +
"\n" +
"ext {\n" +
"    antVersion = '1.9.9'\n" +
"    asmVersion = '6.0_ALPHA'\n" +
"    antlrVersion = '2.7.7'\n" +
"    bridgerVersion = '1.1.Final'\n" +
"    coberturaVersion = '1.9.4.1'\n" +
"    commonsCliVersion = '1.4'\n" +
"    commonsHttpClientVersion = '3.1'\n" +
"    eclipseOsgiVersion = '3.9.1-v20140110-1610'\n" +
"    gparsVersion = '1.2.1'\n" +
"    gradleVersion = '3.3'\n" +
"    ivyVersion = '2.4.0'\n" +
"    jansiVersion = '1.13'\n" +
"    jarjarVersion = '1.4.1'\n" +
"    jlineVersion = '2.14.2'\n" +
"    jmockVersion = '1.2.0'\n" +
"    logbackVersion = '1.1.7'\n" +
"    log4jVersion = '1.2.17'\n" +
"    log4j2Version = '2.8'\n" +
"    luceneVersion = '4.7.2'\n" +
"    openbeansVersion = '1.0'\n" +
"    openejbVersion = '1.0'\n" +
"    qdoxVersion = '1.12.1'\n" +
"    slf4jVersion = '1.7.21'\n" +
"    xmlunitVersion = '1.6'\n" +
"    xstreamVersion = '1.4.9'\n" +
"    spockVersion = '1.1-groovy-2.4-SNAPSHOT' // supports 3.0\n" +
"    isReleaseVersion = !groovyVersion.toLowerCase().endsWith(\"\"snapshot\"\")\n" +
"}\n" +
"\n" +
"dependencies {\n" +
"    compile \"\"antlr:antlr:$antlrVersion\"\"\n" +
"    compile \"\"org.ow2.asm:asm:$asmVersion\"\"\n" +
"    compile \"\"org.ow2.asm:asm-analysis:$asmVersion\"\"\n" +
"    compile \"\"org.ow2.asm:asm-commons:$asmVersion\"\"\n" +
"    compile \"\"org.ow2.asm:asm-tree:$asmVersion\"\"\n" +
"    compile \"\"org.ow2.asm:asm-util:$asmVersion\"\"\n" +
"\n" +
"    compile \"\"commons-cli:commons-cli:$commonsCliVersion\"\"\n" +
"    compile \"\"org.apache.ant:ant:$antVersion\"\"\n" +
"    compile(\"\"com.thoughtworks.xstream:xstream:$xstreamVersion\"\") {\n" +
"        exclude(group: 'xpp3', module: 'xpp3_min')\n" +
"        exclude(group: 'junit', module: 'junit')\n" +
"        exclude(group: 'jmock', module: 'jmock')\n" +
"        exclude(group: 'xmlpull', module: 'xmlpull')\n" +
"    }\n" +
"    compile \"\"com.googlecode:openbeans:$openbeansVersion\"\"\n" +
"    compile \"\"org.fusesource.jansi:jansi:$jansiVersion\"\"\n" +
"    compile(\"\"org.apache.ivy:ivy:$ivyVersion\"\") {\n" +
"        transitive = false\n" +
"    }\n" +
"    compile files(\"\"${buildDir}/generated-classes\"\")\n" +
"\n" +
"    runtime(\"\"org.codehaus.gpars:gpars:$gparsVersion\"\") {\n" +
"        exclude(group: 'org.codehaus.groovy', module: 'groovy-all')\n" +
"    }\n" +
"    testCompile \"\"jmock:jmock:$jmockVersion\"\"\n" +
"    testCompile \"\"jmock:jmock-cglib:$jmockVersion\"\"\n" +
"    testCompile \"\"xmlunit:xmlunit:$xmlunitVersion\"\"\n" +
"    testCompile \"\"ch.qos.logback:logback-classic:$logbackVersion\"\"\n" +
"    testCompile \"\"log4j:log4j:$log4jVersion\"\"\n" +
"    testCompile \"\"org.apache.logging.log4j:log4j-core:$log4j2Version\"\"\n" +
"    testCompile \"\"org.slf4j:jcl-over-slf4j:$slf4jVersion\"\"\n" +
"    testCompile \"\"com.thoughtworks.qdox:qdox:$qdoxVersion\"\"\n" +
"\n" +
"    tools \"\"com.googlecode.jarjar:jarjar:$jarjarVersion\"\"\n" +
"    tools \"\"org.jboss.bridger:bridger:$bridgerVersion\"\"\n" +
"\n" +
"    tools(\"\"net.sourceforge.cobertura:cobertura:$coberturaVersion\"\") {\n" +
"        exclude(module: 'asm')\n" +
"        exclude(module: 'asm')\n" +
"        exclude(module: 'ant')\n" +
"    }\n" +
"    tools \"\"org.ow2.asm:asm-all:$asmVersion\"\"\n" +
"    tools \"\"com.thoughtworks.qdox:qdox:$qdoxVersion\"\"\n" +
"\n" +
"    examplesCompile project(':groovy-test')\n" +
"    examplesCompile project(':groovy-swing')\n" +
"\n" +
"    examplesCompile \"\"org.apache.lucene:lucene-core:$luceneVersion\"\"\n" +
"    examplesCompile \"\"org.apache.lucene:lucene-analyzers-common:$luceneVersion\"\"\n" +
"    examplesCompile \"\"org.apache.lucene:lucene-queryparser:$luceneVersion\"\"\n" +
"    examplesCompile \"\"org.eclipse:osgi:$eclipseOsgiVersion\"\"\n" +
"    examplesRuntime(\"\"commons-httpclient:commons-httpclient:$commonsHttpClientVersion\"\") {\n" +
"        exclude(module: 'junit')\n" +
"        exclude(module: 'commons-logging')\n" +
"        exclude(module: 'commons-codec')\n" +
"    }\n" +
"    examplesRuntime(\"\"openejb:openejb-loader:$openejbVersion\"\") {\n" +
"        exclude(module: 'log4j')\n" +
"        exclude(module: 'openejb-core')\n" +
"        exclude(module: 'geronimo-jta_1.0.1B_spec')\n" +
"        exclude(module: 'geronimo-servlet_2.4_spec')\n" +
"        exclude(module: 'geronimo-ejb_2.1_spec')\n" +
"        exclude(module: 'geronimo-j2ee-connector_1.5_spec')\n" +
"    }\n" +
"\n" +
"// TODO use antlr plugin\n" +
"//    antlr \"\"antlr:antlr:$antlrVersion\"\"\n" +
"    antlr \"\"org.apache.ant:ant-antlr:$antVersion\"\"\n" +
"\n" +
"    testCompile project(':groovy-ant')\n" +
"    testCompile project(':groovy-test')\n" +
"    testCompile project(':groovy-macro')\n" +
"}\n" +
"\n" +
"ext.generatedDirectory = \"\"${buildDir}/generated-sources\"\"\n" +
"\n" +
"sourceSets {\n" +
"    main {\n" +
"        java {\n" +
"            srcDirs = [\n" +
"                    'src/main',\n" +
"                    \"\"$generatedDirectory/src/main\"\"\n" +
"            ]\n" +
"            fileTree('src/main/groovy/ui').matching {\n" +
"                exclude 'GroovyMain.java', 'GroovySocketServer.java'\n" +
"            }.visit { details ->\n" +
"                exclude \"\"groovy/ui/$details.path\"\"\n" +
"            }\n" +
"        }\n" +
"        groovy {\n" +
"            srcDirs = [\n" +
"                    'src/main',\n" +
"                    \"\"$generatedDirectory/src/main\"\"\n" +
"            ]\n" +
"        }\n" +
"        resources {\n" +
"            srcDirs = ['src/main', 'src/tools', 'src/resources']\n" +
"            include 'META-INF/services/*',\n" +
"                    'META-INF/groovy-release-info.properties',\n" +
"                    'groovy/grape/*.xml',\n" +
"                    'groovy/ui/*.properties',\n" +
"                    'groovy/ui/**/*.png',\n" +
"                    'groovy/inspect/swingui/AstBrowserProperties.groovy',\n" +
"                    'org/codehaus/groovy/tools/shell/**/*.properties',\n" +
"                    'org/codehaus/groovy/tools/shell/**/*.xml',\n" +
"                    'org/codehaus/groovy/tools/groovydoc/gstringTemplates/**/*.*',\n" +
"                    'org/codehaus/groovy/tools/groovy.ico'\n" +
"        }\n" +
"    }\n" +
"    test {\n" +
"        groovy {\n" +
"            srcDirs = ['src/test']\n" +
"        }\n" +
"        resources {\n" +
"            srcDirs = ['src/test-resources']\n" +
"        }\n" +
"    }\n" +
"    tools {\n" +
"        compileClasspath = sourceSets.main.runtimeClasspath + configurations.tools\n" +
"        runtimeClasspath = output + compileClasspath\n" +
"    }\n" +
"    examples {\n" +
"        groovy {\n" +
"            srcDirs = ['src/examples']\n" +
"        }\n" +
"        resources {\n" +
"            srcDirs = ['src/examples']\n" +
"        }\n" +
"        compileClasspath = configurations.examplesRuntime + sourceSets.main.output + project(':groovy-xml').sourceSets.main.output\n" +
"    }\n" +
"}\n" +
"\n" +
"// make sure examples can be compiled, even if we don't run them\n" +
"// todo: reorganize examples so that we can run them too\n" +
"check {\n" +
"    dependsOn examplesClasses\n" +
"}\n" +
"\n" +
"// remove this from config once GRADLE-854 is fixed.\n" +
"processResources.doLast {\n" +
"    copy {\n" +
"        from('src/main') {\n" +
"            include 'groovy/inspect/swingui/AstBrowserProperties.groovy',\n" +
"                    'org/codehaus/groovy/tools/groovydoc/gstringTemplates/GroovyDocTemplateInfo.java'\n" +
"        }\n" +
"        into sourceSets.main.output.classesDir\n" +
"    }\n" +
"}\n" +
"\n" +
"tasks.withType(Jar) {\n" +
"    doFirst {\n" +
"        ant.java(classname:'org.jboss.bridger.Bridger', classpath: rootProject.configurations.tools.asPath, outputproperty: 'stdout') {\n" +
"            arg(value: \"\"${sourceSets.main.output.classesDir.canonicalPath}/org/codehaus/groovy/runtime/DefaultGroovyMethods.class\"\")\n" +
"        }\n" +
"        ant.echo('Bridger: ' + ant.properties.stdout)\n" +
"    }\n" +
"}\n" +
"\n" +
"task ensureGrammars {\n" +
"    description = 'Ensure all the Antlr generated files are up to date.'\n" +
"    ext.antlrDirectory = \"\"$projectDir/src/main/org/codehaus/groovy/antlr\"\"\n" +
"    ext.groovyParserDirectory = \"\"$ext.antlrDirectory/parser\"\"\n" +
"    ext.javaParserDirectory = \"\"$ext.antlrDirectory/java\"\"\n" +
"    ext.genPath = \"\"$generatedDirectory/src/main/org/codehaus/groovy/antlr\"\"\n" +
"    ext.groovyOutDir = \"\"$ext.genPath/parser\"\"\n" +
"    ext.javaOutDir = \"\"$ext.genPath/java\"\"\n" +
"    inputs.dir(antlrDirectory)\n" +
"    outputs.dir(groovyOutDir)\n" +
"    outputs.dir(javaOutDir)\n" +
"    doFirst {\n" +
"        new File(groovyOutDir).mkdirs()\n" +
"        new File(javaOutDir).mkdirs()\n" +
"        ant {\n" +
"            taskdef(name: 'antlr',\n" +
"                    classname: 'org.apache.tools.ant.taskdefs.optional.ANTLR',\n" +
"                    classpath: configurations.antlr.asPath)\n" +
"\n" +
"            mkdir dir: ext.groovyParserDirectory\n" +
"            antlr(target: \"\"$ext.antlrDirectory/groovy.g\"\", outputdirectory: ext.groovyOutDir) {\n" +
"                classpath path: configurations.compile.asPath\n" +
"            }\n" +
"            antlr(target: \"\"$ext.javaParserDirectory/java.g\"\", outputdirectory: ext.javaOutDir) {\n" +
"                classpath path: configurations.compile.asPath\n" +
"            }\n" +
"        }\n" +
"    }\n" +
"}\n" +
"\n" +
"apply from: 'gradle/utils.gradle'\n" +
"apply from: 'gradle/wrapper.gradle'\n" +
"\n" +
"ext.modules = {\n" +
"    subprojects\n" +
"}\n" +
"\n" +
"task dgmConverter(dependsOn:compileJava) {\n" +
"    description = 'Generates DGM info file required for faster startup.'\n" +
"    def classesDir = sourceSets.main.output.classesDir\n" +
"    def classpath = files(classesDir, configurations.compile)\n" +
"\n" +
"    //main = 'org.codehaus.groovy.tools.DgmConverter'\n" +
"    //args = ['--info', classesDir.absolutePath]\n" +
"    doFirst {\n" +
"        file(\"\"$classesDir/META-INF\"\").mkdirs()\n" +
"        // we use ant.java because Gradle is a bit \"\"too smart\"\" with JavaExec\n" +
"        // as it will invalidate the task if classpath changes, which will\n" +
"        // happen once Groovy files are compiled\n" +
"        ant.java(classname:'org.codehaus.groovy.tools.DgmConverter', classpath: classpath.asPath) {\n" +
"            arg(value: '--info')\n" +
"            arg(value: classesDir.absolutePath)\n" +
"        }\n" +
"    }\n" +
"    inputs.files fileTree('src').include('**/*GroovyMethods.java')\n" +
"    outputs.file file(\"\"${classesDir}/META-INF/dgminfo\"\")\n" +
"}\n" +
"\n" +
"compileJava {\n" +
"    dependsOn ensureGrammars\n" +
"    options.fork(memoryMaximumSize: javacMain_mx)\n" +
"}\n" +
"\n" +
"    // Gradle classloading magic with Groovy will only work if it finds a *jar*\n" +
"    // on classpath. This \"\"bootstrap jar\"\" contains the minimal compiler, without .groovy compiled files\n" +
"\n" +
"task bootstrapJar {\n" +
"    dependsOn compileJava, dgmConverter\n" +
"\n" +
"    def destinationDir = file(\"\"$buildDir/bootstrap\"\")\n" +
"    def archiveName = \"\"groovy-${version}-bootstrap.jar\"\"\n" +
"    ext.archivePath = file(\"\"$destinationDir/$archiveName\"\")\n" +
"\n" +
"    doLast {\n" +
"        // we use ant.jar because Gradle is a bit \"\"too smart\"\" with JavaExec\n" +
"        // as it will invalidate the task if classpath changes, which will\n" +
"        // happen once Groovy files are compiled\n" +
"        destinationDir.mkdirs()\n" +
"        ant.jar(\n" +
"                destfile: archivePath,\n" +
"                basedir: file(sourceSets.main.output.classesDir)\n" +
"        )\n" +
"    }\n" +
"    inputs.property('indy', useIndy())\n" +
"    inputs.files sourceSets.main.allJava\n" +
"    outputs.file archivePath\n" +
"}\n" +
"\n" +
"compileGroovy.dependsOn bootstrapJar\n" +
"\n" +
"allprojects {\n" +
"\n" +
"    tasks.withType(JavaCompile) {\n" +
"        options.encoding = 'UTF-8'\n" +
"    }\n" +
"\n" +
"    tasks.withType(GroovyCompile) {\n" +
"        groovyOptions.fork(memoryMaximumSize: groovycMain_mx)\n" +
"        groovyOptions.encoding = 'UTF-8'\n" +
"        groovyClasspath = files(\n" +
"                rootProject.compileJava.classpath,\n" +
"                rootProject.bootstrapJar.archivePath\n" +
"        )\n" +
"\n" +
"        classpath = classpath + groovyClasspath\n" +
"    }\n" +
"\n" +
"    if (useIndy()) {\n" +
"        tasks.withType(GroovyCompile) {\n" +
"            logger.info(\"\"Building ${project.name}:${name} with InvokeDynamic support activated\"\")\n" +
"            groovyOptions.optimizationOptions.indy = true\n" +
"            sourceCompatibility = 1.7\n" +
"            targetCompatibility = 1.7\n" +
"        }\n" +
"        tasks.withType(JavaCompile) {\n" +
"            if (project.name=='performance') {\n" +
"                sourceCompatibility = 1.8\n" +
"                targetCompatibility = 1.8\n" +
"            } else {\n" +
"                sourceCompatibility = 1.7\n" +
"                targetCompatibility = 1.7\n" +
"            }\n" +
"        }\n" +
"        jar {\n" +
"            classifier = 'indy'\n" +
"        }\n" +
"    }\n" +
"}\n" +
"\n" +
"compileTestGroovy {\n" +
"    groovyOptions.fork(memoryMaximumSize: groovycTest_mx)\n" +
"}\n" +
"\n" +
"// TODO superfluous to check for JDK7 for Gradle version 3.2+ but leave for future?\n" +
"task checkCompatibility {\n" +
"    doLast {\n" +
"        assert JavaVersion.current().java7Compatible\n" +
"    }\n" +
"}\n" +
"\n" +
"if (!JavaVersion.current().java7Compatible) {\n" +
"    logger.lifecycle '''\n" +
"    **************************************** WARNING ********************************************\n" +
"    ******   You are running the build with an older JDK. NEVER try to release with 1.6.   ******\n" +
"    ******   You must use a JDK 1.7+ in order to compile all features of the language.     ******\n" +
"    *********************************************************************************************\n" +
"'''\n" +
"}\n" +
"\n" +
"apply from: 'gradle/test.gradle'\n" +
"apply from: 'gradle/groovydoc.gradle'\n" +
"apply from: 'gradle/docs.gradle'\n" +
"apply from: 'gradle/assemble.gradle'\n" +
"apply from: 'gradle/upload.gradle'\n" +
"apply from: 'gradle/idea.gradle'\n" +
"apply from: 'gradle/eclipse.gradle'\n" +
"apply from: 'gradle/quality.gradle'\n" +
"apply from: 'gradle/jdk9.gradle'\n" +
"\n" +
"// If a local configuration file for tweaking the build is present, apply it\n" +
"if (file('user.gradle').exists()) {\n" +
"    apply from: 'user.gradle'\n" +
"}\n" +
"\n" +
"apply from: 'gradle/signing.gradle'\n" +
"\n" +
"licenseReport {\n" +
"    excludeGroups = [\n" +
"            'com.googlecode', // openbeans has no pom but is ASLv2\n" +
"            'org.multiverse'  // we never include this optional dependency of an optional dependency\n" +
"    ]\n" +
"}\n" +
"\n" +
"// UNCOMMENT THE FOLLOWING TASKS IF YOU WANT TO RUN LICENSE CHECKING\n" +
"//task licenseFormatCustom(type:nl.javadude.gradle.plugins.license.License) {\n" +
"//    source = fileTree(dir:\"\"src\"\").include (\"\"**/*.java\"\",'**/*.groovy','**/*.html','**/*.css','**/*.xml','**/*.properties','**/*.properties')\n" +
"//}\n" +
"//\n" +
"//task licenseFormatGradle(type:nl.javadude.gradle.plugins.license.License) {\n" +
"//    source = files(fileTree(dir:projectDir).include('**/*.gradle'),fileTree('buildSrc').include('**/*.groovy'))\n" +
"//}\n" +
"//\n" +
"//licenseFormat.dependsOn licenseFormatCustom\n" +
"//licenseFormat.dependsOn licenseFormatGradle\n" +
"//\n" +
", \"";
}

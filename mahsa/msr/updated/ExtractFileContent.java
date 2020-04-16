/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opm.mahsa.msr.updated;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mahsa
 */
public class ExtractFileContent {

    public static List<String> dom(String input) {
        List<String> artifactIdL = new ArrayList<>();
        try {
            File inputFile = new File(input);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("dependencies");
            //System.out.println("----------------------------");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                NodeList nodeList = nList.item(temp).getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node nNode = nList.item(temp);
                    try {
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;
                            String groupId = eElement
                                    .getElementsByTagName("groupId")
                                    .item(0)
                                    .getTextContent();

                            String artifactId = eElement
                                    .getElementsByTagName("artifactId")
                                    .item(0)
                                    .getTextContent();
                            String version = eElement
                                    .getElementsByTagName("version")
                                    .item(0)
                                    .getTextContent();
                            //System.out.println(groupId+"  :  "+artifactId);
                            String contents = groupId + "/" + artifactId+":"+version;

                            if (!artifactIdL.contains(contents)) {
                                artifactIdL.add(contents);
                                //System.out.println("           " + groupId + " , " + artifactId);
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }
            nList = doc.getElementsByTagName("plugins");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                NodeList nodeList = nList.item(temp).getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        try {
                            if (eElement
                                    .getElementsByTagName("groupId") != null && eElement
                                    .getElementsByTagName("artifactId") != null) {
                                String groupId = eElement
                                        .getElementsByTagName("groupId")
                                        .item(0)
                                        .getTextContent();

                                String artifactId = eElement
                                        .getElementsByTagName("artifactId")
                                        .item(0)
                                        .getTextContent();
                                String version = eElement
                                    .getElementsByTagName("version")
                                    .item(0)
                                    .getTextContent();
                                //System.out.println(groupId+"  :  "+artifactId);
                                String contents = groupId + "/" + artifactId+":"+version;

                                if (!artifactIdL.contains(contents)) {
                                    artifactIdL.add(contents);
                                    //System.out.println("           " + groupId + " , " + artifactId);
                                }

                            }
                        } catch (Exception e) {

                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return artifactIdL;
    }

    public static List<String> readPOMOOOO(String input) {
        //System.out.println("  " + input);
        int flag = 0;
        List<String> groupId = new ArrayList<>();
        List<String> artifactId = new ArrayList<>();
        try {
            String[] splits = input.split(System.getProperty("line.separator"));
            boolean startHere = false;
            for (int i = 0; i < splits.length; i++) {
                String line = splits[i];
                if (line.contains("<dependency>")) {
                    startHere = true;
                }
                if (startHere == true && line.contains("<groupId>")) {
                    String cleaned = line.replaceAll("<groupId>", "");
                    cleaned = line.replaceAll("</groupId>", "");
                    groupId.add(cleaned);
                } else if (startHere == true && line.contains("<artifactId>")) {
                    String cleaned = line.replaceAll("<artifactId>", "");
                    cleaned = line.replaceAll("</artifactId>", "");
                    artifactId.add(cleaned);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> list = new ArrayList<>();
        try {
            for (int i = 0; i < groupId.size(); i++) {
                list.add(groupId + "/" + artifactId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String> readGradle(String input) {
        int flag = 0;
        List<String> dependencies = new ArrayList<>();
        try {
            String[] splits = input.split(System.getProperty("line.separator"));
            boolean startHere = false;
            for (int i = 0; i < splits.length; i++) {
                String line = splits[i];
                if (line.contains("dependencies") && line.contains("{")) {
                    startHere = true;
                    
                }
                if (line.contains("classpath ")) {
                    System.out.println(line.substring(line.indexOf("classpath"),line.length()));
                }

                if (line.contains("}")) {
                    startHere = false;
                }
                if (line.contains("plugins") && line.contains("{")) {
                    startHere = true;
                }
                if (line.contains("}")) {
                    startHere = false;
                }
                if (startHere == true && (!line.contains("{"))) {
                    String cleanString = line;
                    //System.out.println(cleanString);
                    if (cleanString.contains(":")) {
                        cleanString = cleanString.substring(0, cleanString.length());//.lastIndexOf(":"));
                    }
                    if (cleanString.contains("version")) {
                        cleanString = cleanString.substring(0, cleanString.lastIndexOf("version"));
                    }
                    dependencies.add(cleanString);
                } else if (line.contains("apply") || line.contains("plugin")) {
                    dependencies.add(line);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dependencies;
    }

}

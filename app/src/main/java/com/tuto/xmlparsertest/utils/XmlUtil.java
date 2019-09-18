package com.tuto.xmlparsertest.utils;

import com.tuto.xmlparsertest.BuildConfig;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XmlUtil {
    public static HashMap<String, Object> parseVersionCode(String path, String xmlFileTag) {
        InputStream is = null;
        InputStreamReader isr = null;

        HashMap<String, Object> result = new HashMap<>();

        try {
            if (!new File(path).exists()) {
                return result;
            }

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            is = new FileInputStream(path);

            isr = new InputStreamReader(is, "UTF-8");
            parser.setInput(isr);

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.PROCESSING_INSTRUCTION:
                        String pi = parser.getText();
                        result = parseProcessingInstruction(pi, xmlFileTag);
                        break;

                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.nextToken();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (Exception e) {
                }
            }
        }

        return result;
    }

    public static HashMap<String, Object> parseProcessingInstruction(String pi, String xmlFileTag) {
        pi = "<" + pi + "/>";
        HashMap<String, Object> result = new HashMap<>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            ByteArrayInputStream bis = new ByteArrayInputStream(pi.getBytes());
            Document doc = db.parse(bis);
            NodeList nodeList = doc.getElementsByTagName(xmlFileTag);

            if (nodeList.getLength() <= 0) {
                return result;
            }

            Node versionNode = nodeList.item(0).getAttributes().getNamedItem("version");

            if(versionNode != null) {
                result.put("version", versionNode.getNodeValue());
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }

        return result;
    }

    public static void resetVersionCode(String path, String xmlFileTag, String version) throws Exception {
        DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();

        Document doc = fac.newDocumentBuilder().parse(new File(path));

        String instruction = "version=\"" + version + "\"";
        ProcessingInstruction pi = doc.createProcessingInstruction(xmlFileTag, instruction);

        Element element = doc.getDocumentElement();

        if(element != null) {
            NodeList nodeList = element.getParentNode().getChildNodes();

            if(nodeList.item(0).getNodeName().equals(xmlFileTag)) {
                element.getParentNode().removeChild(nodeList.item(0));
            }

            element.getParentNode().insertBefore(pi, element);
        } else {
            Node node = doc.getOwnerDocument().importNode(pi, true);
            element.appendChild(node);
        }

        DOMSource source = new DOMSource(doc);

        TransformerFactory transformerFac = TransformerFactory.newInstance();
        Transformer transformer = transformerFac.newTransformer();
        StreamResult result = new StreamResult(new File(path));
        transformer.transform(source, result);
    }
}

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

@SuppressWarnings("restriction")
public class Demo {

	public static void main(String[] args) throws Exception {
		new Demo().split("src/main/resources/test.xml");
	}

	private void split(String xmlResource) throws Exception {
		XMLEventFactory eventFactory = XMLEventFactory.newFactory();
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader reader = inputFactory
				.createXMLEventReader(new FileReader(xmlResource));

		StartElement rootElement = reader.nextTag().asStartElement();
		StartDocument startDocument = eventFactory.createStartDocument();
		EndDocument endDocument = eventFactory.createEndDocument();

		XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(out);
		eventWriter.add(startDocument);
		eventWriter.add(rootElement);
		FileOutputStream os = new FileOutputStream(new File("target/out.xml"));
		
		Stack<String> elementPath = new Stack<String>();
		elementPath.push(getElementName(rootElement.getName()));

		while (reader.hasNext()) {
			XMLEvent xmlEvent = reader.nextEvent();
			if (xmlEvent.isStartElement()) {
				StartElement startElement = xmlEvent.asStartElement();
				elementPath.push(getElementName(startElement.getName()));
			} else if (xmlEvent.isEndElement()) {
				elementPath.pop();
			}
			eventWriter.add(xmlEvent);
		}
		eventWriter.add(endDocument);
		try {
			os.write(out.toByteArray());
		} finally {
			os.close();
		}
	}

	private static String getElementName(QName name) {
		StringBuilder builder = new StringBuilder();
		if (name.getPrefix() != null) {
			builder.append(name.getPrefix());
			builder.append(":");
		}
		builder.append(name.getLocalPart());
		return builder.toString();
	}

}
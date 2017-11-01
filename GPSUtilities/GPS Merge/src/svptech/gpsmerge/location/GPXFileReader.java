package svptech.gpsmerge.location;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Read a GPX file and provide access to the gps location data that is contained
 * in it.
 * 
 * @author Steve Harding
 *
 */
public class GPXFileReader
{
	private boolean loggingEnabled = false;

	private static GPSLocation priorLocation = null;

	/**
	 * Reader for a GPX formatted file.
	 * 
	 * @param gpxFile
	 *            A java.io.File representing the GPX formatted file to read.
	 * @param enableLogging
	 *            Flag that controls whether significant data from the file will be
	 *            logged to stdout when it is read.
	 * @throws FileNotFoundException
	 */
	public GPXFileReader(File gpxFile, boolean enableLogging) throws FileNotFoundException
	{
		super();
		this.loggingEnabled = enableLogging;
		this.gpxFileStream = new FileInputStream(gpxFile);
	}

	private FileInputStream gpxFileStream = null;

	public List<GPSLocation> getGPXFileLocations() throws XMLStreamException
	{
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader reader = null;
		try
		{
			reader = inputFactory.createXMLStreamReader(gpxFileStream);
			return readDocument(reader);
		} finally
		{
			if (reader != null)
			{
				reader.close();
			}
		}
	}

	private List<GPSLocation> readDocument(XMLStreamReader reader) throws XMLStreamException
	{
		while (reader.hasNext())
		{
			int eventType = reader.next();
			switch (eventType)
			{
			case XMLStreamReader.START_ELEMENT:
				String elementName = reader.getLocalName();
				if (elementName.equals("trkseg"))
					return readPoints(reader);
				break;
			case XMLStreamReader.END_ELEMENT:
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}

	private List<GPSLocation> readPoints(XMLStreamReader reader) throws XMLStreamException
	{
		List<GPSLocation> points = new ArrayList<>();

		while (reader.hasNext())
		{
			int eventType = reader.next();
			switch (eventType)
			{
			case XMLStreamReader.START_ELEMENT:
				String elementName = reader.getLocalName();
				if (elementName.equals("trkpt"))
					points.add(readPoint(reader));
				break;
			case XMLStreamReader.END_ELEMENT:
				return points;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}

	private GPSLocation readPoint(XMLStreamReader reader) throws XMLStreamException
	{

		double latitude = new Double(reader.getAttributeValue(null, "lat"));
		double longitude = new Double(reader.getAttributeValue(null, "lon"));
		String locationTime = "";
		GPSLocation point = null;
		boolean finished = false;

		while (reader.hasNext() && !finished)
		{
			int eventType = reader.next();
			switch (eventType)
			{
			case XMLStreamReader.START_ELEMENT:
				String elementName = reader.getLocalName();
				if (elementName.equals("time"))
				{
					locationTime = readCharacters(reader);
				} else if (elementName.equals("ele"))
				{
					readCharacters(reader);
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				point = new GPSLocation(locationTime, latitude, longitude);

				if (priorLocation != null)
				{
					double timeDifference = point.getLocationTime().toEpochMilli()
							- priorLocation.getLocationTime().toEpochMilli();
					if (loggingEnabled)
					{
						System.out.println("Seconds between points = " + timeDifference / 1000.0);
					}
				}
				priorLocation = point;

				finished = true;
				break;
			default:
				if (loggingEnabled)
				{
					logUnhandledEvent(eventType);
				}
				break;
			}
		}
		return point;
	}

	private void logUnhandledEvent(int eventType)
	{
		System.out.println("Unhandled event type in GPXFileReader : " + eventType);
		if (eventType == XMLStreamReader.ATTRIBUTE)
		{
			System.out.println("ATTRIBUTE");
		} else if (eventType == XMLStreamReader.CDATA)
		{
			System.out.println("CDATA");
		} else if (eventType == XMLStreamReader.CHARACTERS)
		{
			System.out.println("CHARACTERS");
		} else if (eventType == XMLStreamReader.COMMENT)
		{
			System.out.println("COMMENT");
		} else if (eventType == XMLStreamReader.DTD)
		{
			System.out.println("DTD");
		} else if (eventType == XMLStreamReader.END_DOCUMENT)
		{
			System.out.println("END_DOCUMENT");
		} else if (eventType == XMLStreamReader.END_ELEMENT)
		{
			System.out.println("END_ELEMENT");
		} else if (eventType == XMLStreamReader.ENTITY_DECLARATION)
		{
			System.out.println("ENTITY_DECLARATION");
		} else if (eventType == XMLStreamReader.ENTITY_REFERENCE)
		{
			System.out.println("ENTITY_REFERENCE");
		} else if (eventType == XMLStreamReader.NAMESPACE)
		{
			System.out.println("NAMESPACE");
		} else if (eventType == XMLStreamReader.NOTATION_DECLARATION)
		{
			System.out.println("NOTATION_DECLARATION");
		} else if (eventType == XMLStreamReader.PROCESSING_INSTRUCTION)
		{
			System.out.println("PROCESSING_INSTRUCTION");
		} else if (eventType == XMLStreamReader.SPACE)
		{
			System.out.println("SPACE");
		} else if (eventType == XMLStreamReader.START_DOCUMENT)
		{
			System.out.println("START_DOCUMENT");
		} else if (eventType == XMLStreamReader.START_ELEMENT)
		{
			System.out.println("START_ELEMENT");
		} else
		{
			System.out.println("Unknown XMLStreamReader event code : " + eventType);
		}
	}

	private String readCharacters(XMLStreamReader reader) throws XMLStreamException
	{
		StringBuilder result = new StringBuilder();
		while (reader.hasNext())
		{
			int eventType = reader.next();
			switch (eventType)
			{
			case XMLStreamReader.CHARACTERS:
			case XMLStreamReader.CDATA:
				result.append(reader.getText());
				break;
			case XMLStreamReader.END_ELEMENT:
				return result.toString();
			}
		}
		throw new XMLStreamException("Premature end of file");
	}

}

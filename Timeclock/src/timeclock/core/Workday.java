package timeclock.core;

import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Workday {

	private SimpleDate date;
	private LinkedList<Timespan> schedule;
	
	public Workday(SimpleDate date) {
		this.date = date;
		schedule = new LinkedList<Timespan>();
	}
	
	public Workday(Element fromElement) {
		schedule = new LinkedList<Timespan>();
		loadFromXML(fromElement);
	}
	
	public void mergeWith(Workday other) {
		if (!hasSameDateAs(other)) return;
		LinkedList<Timespan> shifts = other.getSchedule();
		for (Timespan t : shifts) {
			if (!schedule.contains(t))
				schedule.add(t);
		}
		
	}
	
	public void clearSchedule() {
		schedule.clear();
	}
	
	public void addHours(Timespan toAdd) {
		schedule.add(toAdd);
	}
	
	public double getHours() {
		double hours = 0; 
		for (int i = 0; i < schedule.size(); i++)
			hours += schedule.get(i).getLength();
		return hours;
	}
	
	public String getHoursString() {
		return getHours() + "";
	}
	
	public SimpleDate getDate() {
		return date;
	}
	
	public String getDayOfWeek() {
		return date.getDayOfWeek();
	}
	
	public String getDateString() {
		return date.toString();
	}
	
	public String getStartFormatted() {
		return schedule.getFirst().getStartFormatted();
	}
	
	public String getEndFormatted() {
		return schedule.getFirst().getEndFormatted();
	}
	
	public boolean hasShifts() {
		return schedule.size() > 0;
	}
	
	public boolean hasMultipleShifts() {
		return schedule.size() > 1;
	}
	
	public LinkedList<Timespan> getSchedule() {
		return schedule;
	}
	
	public Element toXML() {
		Document dom;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.newDocument();
			Element root = dom.createElement("workday");
			
			Element dateNode = dom.createElement("date");
			dateNode.appendChild(dom.createTextNode(date.toString()));
			root.appendChild(dateNode);
			
			for (int i = 0; i < schedule.size(); i++) {
				Element hoursRoot = dom.createElement("hours");
				Element e2 = dom.createElement("start");
				e2.appendChild(dom.createTextNode(schedule.get(i).getStart() + ""));
				hoursRoot.appendChild(e2);
				
				e2 = dom.createElement("end");
				e2.appendChild(dom.createTextNode(schedule.get(i).getEnd() + ""));
				hoursRoot.appendChild(e2);
				
				root.appendChild(hoursRoot);
			}
			
			return root;			
			
		} catch (ParserConfigurationException pce) {
	        System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
	    }
		
		return null;
	}
	
	public void loadFromXML(Element e) {
		date = new SimpleDate(e.getElementsByTagName("date").item(0).getTextContent());
		
		NodeList hours = e.getElementsByTagName("hours");
		for (int i = 0; i < hours.getLength(); i++) {
			Element x = (Element) hours.item(i);
			double start = Double.parseDouble(x.getElementsByTagName("start").item(0).getTextContent());
			double end   = Double.parseDouble(x.getElementsByTagName("end").item(0).getTextContent());
			schedule.add(new Timespan(start, end));
		}
	}
	
	public String toString() {
		String ret = "";
		ret += "Workday " + date.toString() + "\n";
		for (int i = 0; i < schedule.size(); i++)
			ret += schedule.get(i).toString() + "\n";
		return ret;
	}
	
	public boolean hasSameDateAs(Workday other) {
		return date.equals(other.getDate());
	}
	
}

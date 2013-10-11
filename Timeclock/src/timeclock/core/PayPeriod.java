package timeclock.core;

import java.util.Calendar;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PayPeriod {
	
	private SimpleDate start;
	private SimpleDate end;
	private LinkedList<Workday> schedule;
	
	private String wk1Markout;
	private String wk2Markout;
	private double wk1Tips;
	private double wk2Tips;
	
	public PayPeriod(SimpleDate start, SimpleDate end) {
		this.start = start;
		this.end = end;
		init();
		populateEmptyWorkdays();
	}
	
	public PayPeriod(String fromStartingFile) {
		init();
		populateEmptyWorkdays();
		loadPayPeriod(fromStartingFile);
	}
	
	public PayPeriod(Element fromElement) {
		init();
		loadPayPeriod(fromElement);
	}
	
	private void init() {
		schedule = new LinkedList<Workday>();
		wk1Markout = "";
		wk2Markout = "";
		wk1Tips = 0;
		wk2Tips = 0;
	}
	
	public void addWorkday(Workday toAdd) {
		schedule.add(toAdd);
	}
	
	public void populateEmptyWorkdays() {
		SimpleDate current = start;
		for (int i = 0; i < 14; i++) {
			schedule.add(new Workday(current));
			current = current.nextCalendarDay();
		}
	}
	
	public void add(Workday toAdd) {
		for (Workday w : schedule) {
			if (w.hasSameDateAs(toAdd)) {
				w.mergeWith(toAdd);
				return;
			} 
		}
	}
	
	public Workday getWorkdayFromDate(SimpleDate date) {
		for (Workday w : schedule) {
			if (w.getDate().equals(date))
				return w;
		}
		return null;
	}
	
	/**
	 * If this PayPeriod contains a Workday of the same date as
	 * the one passed, this method will combine their associated
	 * data into one object.
	 * @param w Workday to update (dates must match)
	 */
	public void updateWorkday(Workday updatedWorkday) {
		for (Workday w : schedule) {
			if (w.hasSameDateAs(updatedWorkday)) {
				w.mergeWith(updatedWorkday);
				return;
			}
		}
	}
	
	public void loadPayPeriod(String source) {
		Document dom;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(source);
			
			Element starting = (Element) dom.getElementsByTagName("starting").item(0);
			start = new SimpleDate(starting.getTextContent());
			
			Element ending = (Element) dom.getElementsByTagName("ending").item(0);
			end = new SimpleDate(ending.getTextContent());
			
			NodeList wk1MarkoutL = dom.getElementsByTagName("wk1Markout");
			if (wk1MarkoutL != null) {
				Element wk1MarkoutE = (Element) wk1MarkoutL.item(0);
				this.wk1Markout = wk1MarkoutE.getTextContent();
			} 
			
			NodeList wk2MarkoutL = dom.getElementsByTagName("wk2Markout");
			if (wk2MarkoutL != null) {
				Element wk2MarkoutE = (Element) wk2MarkoutL.item(0);
				wk1Markout = wk2MarkoutE.getTextContent();
			}
			
			NodeList wk1TipsL = dom.getElementsByTagName("wk1Tips");
			if (wk1TipsL != null) {
				Element wk2TipsE = (Element) wk1TipsL.item(0);
				wk1Tips = Double.parseDouble(wk2TipsE.getTextContent());
			}
			
			NodeList wk2TipsL = dom.getElementsByTagName("wk2Tips");
			if (wk2TipsL != null) {
				Element wk2TipsE = (Element) wk2TipsL.item(0);
				wk2Tips = Double.parseDouble(wk2TipsE.getTextContent());
			}
			
			NodeList nList = dom.getElementsByTagName("workday");
			for (int i = 0; i < nList.getLength(); i++) {
				Node n = nList.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Workday wd = new Workday( (Element) n );
					add(wd);
				}
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadPayPeriod(Element source) {
		Element starting = (Element) source.getElementsByTagName("starting").item(0);
		start = new SimpleDate(starting.getTextContent());
		
		Element ending = (Element) source.getElementsByTagName("ending").item(0);
		end = new SimpleDate(ending.getTextContent());
		
		Node wk1MarkoutN = source.getElementsByTagName("wk1Markout").item(0);
		if (wk1MarkoutN != null) {
			Element wk1MarkoutE = (Element) wk1MarkoutN;
			wk1Markout = wk1MarkoutE.getTextContent();
		} 
		
		Node wk2MarkoutN = source.getElementsByTagName("wk2Markout").item(0);
		if (wk2MarkoutN != null) {
			Element wk2MarkoutE = (Element) wk2MarkoutN; 
			wk2Markout = wk2MarkoutE.getTextContent();
		}
		
		Node wk1TipsN = source.getElementsByTagName("wk1Tips").item(0);
		if (wk1TipsN != null) {
			Element wk2TipsE = (Element) wk1TipsN;
			wk1Tips = Double.parseDouble(wk2TipsE.getTextContent());
		}
		
		Node wk2TipsN = source.getElementsByTagName("wk2Tips").item(0);
		if (wk2TipsN != null) {
			Element wk2TipsE = (Element) wk2TipsN;
			wk2Tips = Double.parseDouble(wk2TipsE.getTextContent());
		}
		
		populateEmptyWorkdays();
		
		NodeList workdays = source.getElementsByTagName("workday");
		for (int i = 0; i < workdays.getLength(); i++) {
			Node n = workdays.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				Workday w = new Workday(e);
				add(w);
			}
		}
	}
//	
//	public void savePayPeriod(String toLocation) {
//		Document dom;
//		
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		try {
//			DocumentBuilder db = dbf.newDocumentBuilder();
//			dom = db.newDocument();
//			Element root = dom.createElement("pay_period");
//			
//			Element x = dom.createElement("starting");
//			x.appendChild(dom.createTextNode(start.toString()));
//			root.appendChild(x);
//			
//			x = dom.createElement("ending");
//			x.appendChild(dom.createTextNode(end.toString()));
//			root.appendChild(x);
//			
//			for (int i = 0; i < schedule.size(); i++) {
//				root.appendChild(dom.importNode(schedule.get(i).toXML(), true));
//			}
//			
//			dom.appendChild(root);
//			
//			try {
//	            Transformer tr = TransformerFactory.newInstance().newTransformer();
//	            tr.setOutputProperty(OutputKeys.INDENT, "yes");
//	            tr.setOutputProperty(OutputKeys.METHOD, "xml");
//	            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//	            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
//
//	            // send DOM to file
//	            String filename = toLocation + "Pay Period from " + start.toString() + " to " + end.toString() + ".xml";
//	            tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(filename)));
//
//	        } catch (TransformerException te) {
//	            System.out.println(te.getMessage());
//	        } catch (IOException ioe) {
//	            System.out.println(ioe.getMessage());
//	        }
//		} catch (ParserConfigurationException pce) {
//	        System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
//	    }
//	}
	
	public Element toXML() {
		Document dom;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.newDocument();
			Element root = dom.createElement("pay_period");
			
			Element x = dom.createElement("starting");
			x.appendChild(dom.createTextNode(start.toString()));
			root.appendChild(x);
			
			x = dom.createElement("ending");
			x.appendChild(dom.createTextNode(end.toString()));
			root.appendChild(x);
			
			if (!wk1Markout.equals("")) {
				x = dom.createElement("wk1Markout");
				x.appendChild(dom.createTextNode(wk1Markout));
				root.appendChild(x);
			}
			
			if (!wk2Markout.equals("")) {
				x = dom.createElement("wk2Markout");
				x.appendChild(dom.createTextNode(wk2Markout));
				root.appendChild(x);
			}
			
			x = dom.createElement("wk1Tips");
			x.appendChild(dom.createTextNode(wk1Tips + ""));
			root.appendChild(x);
			
			x = dom.createElement("wk2Tips");
			x.appendChild(dom.createTextNode(wk2Tips + ""));
			root.appendChild(x);
			
//			for (int i = 0; i < schedule.size(); i++)
//				root.appendChild(dom.importNode(schedule.get(i).toXML(), true));
			
			for (Workday w : schedule)
				if (w.hasShifts()) root.appendChild(dom.importNode(w.toXML(), true));
			
			dom.appendChild(root);
			
			return root;
			
		} catch (ParserConfigurationException pce) {
			System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
		}
		
		return null;
	}
	
//	public void savePayPeriod() {
//		savePayPeriod("");
//	}
	
	public void clearSchedule() {
		schedule.clear();
	}

	public SimpleDate getStart() {
		return start;
	}
	
	public String getStartString() {
		return start.toString();
	}

	public SimpleDate getEnd() {
		return end;
	}
	
	public String getEndString() {
		return end.toString();
	}
	
	public String getWeekOneMarkout() {
		return wk1Markout.equals("") ? null : wk1Markout;
	}
	
	public void setWeekOneMarkout(String markout) {
		wk1Markout = markout;
	}
	
	public String getWeekTwoMarkout() {
		return wk2Markout.equals("") ? null : wk2Markout;
	}
	
	public void setWeekTwoMarkout(String markout) {
		wk2Markout = markout;
	}
	
	public double getWeekOneTips() {
		return wk1Tips;
	}
	
	public void setWeekOneTips(double tips) {
		wk1Tips = tips;
	}
	
	public double getWeekTwoTips() {
		return wk2Tips;
	}
	
	public void setWeekTwoTips(double tips) {
		wk2Tips = tips;
	}
	
	/**
	 * @return the start of the next pay period
	 */
	public SimpleDate getStartOfNext() {
		return end.nextCalendarDay();
	}
	
	public SimpleDate getEndOfPrevious() {
		return start.previousCalendarDay();
	}
	
	public SimpleDate getEndOfNext() {
		Calendar c = end.toCalendar();
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 14);
		return new SimpleDate(c);
	}
	
	public SimpleDate getStartOfPrevious() {
		Calendar c = start.toCalendar();
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - 14);
		return new SimpleDate(c);
	}
	
	public SimpleDate getPayday() {
		Calendar c = end.toCalendar();
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 5);
		return new SimpleDate(c);
	}
	
	public double getHoursWorked() {
		double ret = 0;
		for (int i = 0; i < schedule.size(); i++)
			ret += schedule.get(i).getHours();
		return ret;
	}
	
	public double getHoursWorkedWeekOne() {
		double ret = 0.0;
		for (int i = 0; i < schedule.size() && i < 7; i++)
			ret += schedule.get(i).getHours();
		return ret;
	}
	
	public double getHoursWorkedWeekTwo() {
		double ret = 0.0;
		for (int i = 7; i < schedule.size(); i++)
			ret += schedule.get(i).getHours();
		return ret;
	}
	
	public boolean hasNonemptyWorkdays() {
		for (Workday w : schedule) {
			if (w.hasShifts())
				return true;
		}
		return false;
	}
	
	public LinkedList<Workday> getWorkdays() {
		return (LinkedList<Workday>) schedule.clone();
	}
	
	@Override
	public String toString() {
		String ret = "";
		ret += "Pay Period from " + start.toString() + " to " + end.toString() + "\n";
		for (int i = 0; i < schedule.size(); i++)
			ret += schedule.get(i).toString();
		return ret;
	}


}

package timeclock.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Handles calculating hours worked, compensation, etc for one pay period at a time.
 * Also controls saving and loading this data.
 *
 * @author Justin Toniazzo
 *
 */
public class Timeclock {
	
	public static final String DEFAULT_SAVE_LOCATION = "C:\\\\Users\\Justin\\AppData\\Roaming\\Timeclock\\";
	
	public static final double DEFAULT_PAYRATE = 7.50;
	public static final double OVERTIME_MULTIPLIER = 1.5;
	
	public double payrate;
	public double grossPay;
	
	private LinkedList<PayPeriod> schedule;
	private int index;
	
	public Timeclock() {
		payrate = DEFAULT_PAYRATE;
		grossPay = 0;
		index = 0;
		
		schedule = new LinkedList<PayPeriod>();
		loadSchedule();
	}
	
	public LinkedList<PayPeriod> getSchedule() {
		return schedule;
	}
	
	public void loadSchedule() {
		String source = DEFAULT_SAVE_LOCATION + "tc-out.xml";
		System.out.println("Loading: " + source);
		Document dom;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(source);
			
			NodeList pPeriods = dom.getElementsByTagName("pay_period");
			for (int i = 0; i < pPeriods.getLength(); i++) {
				Node n = pPeriods.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					PayPeriod p = new PayPeriod(e);
					schedule.add(p);
				}
			}
		} catch (Exception e) {
			System.err.println(e);
		}
		
//		File folder = new File(DEFAULT_SAVE_LOCATION);
//		if (!folder.exists()) {
//			try {
//				Files.createDirectory(folder.toPath());
//			} catch (IOException e) {
//				System.err.println("IOException in Timeclock.loadSchedule(): " + e.getMessage());
//			}
//			return;
//		}
//		File[] fileList = folder.listFiles();
//		
//		for (int i = 0; i < fileList.length; i++) {
//			System.out.println("Loading file: " + fileList[i].getAbsolutePath());
//			PayPeriod p = new PayPeriod(fileList[i].getAbsolutePath());
//			schedule.add(p);
//		}
	}
	
	public PayPeriod getCurrentPayPeriod() {
		return schedule.get(index);
	}
	
	public void moveToNextPayPeriod() {
		SimpleDate startNext = schedule.get(index).getStartOfNext();
		SimpleDate endNext   = schedule.get(index).getEndOfNext();
		index++;
		if (index >= schedule.size())
			schedule.add(new PayPeriod(startNext, endNext));
	}
	
	public void moveToPreviousPayPeriod() {
		if (--index < 0) {
			PayPeriod current    = schedule.get(index + 1);
			SimpleDate startPrev = current.getStartOfPrevious();
			SimpleDate endPrev   = current.getEndOfPrevious();
			schedule.addFirst(new PayPeriod(startPrev, endPrev));
			index = 0;
		}
	}
	
	public String getCurrentStartString() {
		return schedule.get(index).getStartString();
	}
	
	public String getCurrentEndString() {
		return schedule.get(index).getEndString();
	}
	
//	public void saveSchedule() {
//		for (int i = 0; i < schedule.size(); i++) {
//			schedule.get(i).savePayPeriod(DEFAULT_SAVE_LOCATION);
//		}
//	}
	
	public LinkedList<Workday> getWorkdays() {
		return schedule.get(index).getWorkdays();
	}

	public double getPayrate() {
		return payrate;
	}

	public void setPayrate(double payrate) {
		this.payrate = payrate;
	}

	public String getGrossPay() {
		grossPay = schedule.get(index).getHoursWorked() * payrate;
		return String.format("%.2f", grossPay);
	}
	
	public double getWeekOneHoursWorked() {
		return schedule.get(index).getHoursWorkedWeekOne();
	}
	
	public double getWeekTwoHoursWorked() {
		return schedule.get(index).getHoursWorkedWeekTwo();
	}
	
	public String getWeekOneMarkout() {
		return schedule.get(index).getWeekOneMarkout();
	}
	
	public void setWeekOneMarkout(String markout) {
		schedule.get(index).setWeekOneMarkout(markout);
	}
	
	public String getWeekTwoMarkout() {
		return schedule.get(index).getWeekTwoMarkout();
	}
	
	public void setWeekTwoMarkout(String markout) {
		schedule.get(index).setWeekTwoMarkout(markout);
	}
	
	public double getWeekOneTips() {
		return schedule.get(index).getWeekOneTips();
	}
	
	public double getWeekTwoTips() {
		return schedule.get(index).getWeekTwoTips();
	}
	
	public void saveToFile() {
		Document dom;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.newDocument();
			Element root = dom.createElement("timeclock");
			
//			for (int i = 0; i < schedule.size(); i++)
//				root.appendChild(dom.importNode(schedule.get(i).toXML(), true));
			
			for (PayPeriod p : schedule) {
				if (p.hasNonemptyWorkdays())
					root.appendChild(dom.importNode(p.toXML(), true));
			}
			
			dom.appendChild(root);
			
			try {
	            Transformer tr = TransformerFactory.newInstance().newTransformer();
	            tr.setOutputProperty(OutputKeys.INDENT, "yes");
	            tr.setOutputProperty(OutputKeys.METHOD, "xml");
	            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	            // send dom to file
	            String filename = DEFAULT_SAVE_LOCATION + "tc-out.xml";
	            tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(filename)));

	        } catch (TransformerException te) {
	            System.out.println(te.getMessage());
	        } catch (IOException ioe) {
	            System.out.println(ioe.getMessage());
	        }
		} catch (ParserConfigurationException pce) {
	        System.err.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
	    }
	}
	
	@Override
	public String toString() {
		String ret = "";
		for (PayPeriod p : schedule)
			ret += p.toString();
		return ret;
	}
}

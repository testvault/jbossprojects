package sfdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.w3c.dom.Document;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import net.sf.json.JSONObject;

public class Util {
	public static JSONObject buildJSONError(JSONObject data, String errorCode, String errorReason) {
		data.accumulate("status", "FAILURE");
		data.accumulate("error_code", errorCode);
		data.accumulate("error_reason", errorReason);
		return data;
	}
	
	public static HashMap<String, String> parseResponseParams(String body) throws Exception {
		HashMap<String, String> results = new HashMap<String, String>();
		for (String keyValuePair : body.split("&")) {
			String[] kvp = keyValuePair.split("=");
			results.put(kvp[0], URLDecoder.decode(kvp[1], "UTF-8"));
		}
		return results;
	}

	public static String readInputStream(InputStream input) {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));

		try {
			String line = "";
			;
			while ((line = reader.readLine()) != null)
				sb.append(line + "\n");
		} catch (IOException e) {
		} finally {
			try {
				input.close();
			} catch (IOException e) {
			}
			input = null;
		}
		return sb.toString().trim();
	}

	public static String xmlStreamToXML(InputStream instream) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(instream);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			StreamResult result = new StreamResult(new StringWriter());
			transformer.transform(new DOMSource(doc), result);

			return result.getWriter().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String stringToXML(String xmlContent) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlContent);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			StreamResult result = new StreamResult(new StringWriter());
			transformer.transform(new DOMSource(doc), result);

			return result.getWriter().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static boolean isEmpty(String value) {
		return Validator.isEmpty(value);
	}
	
	public static boolean isJSONEmpty(String value) {
		return Validator.isEmpty(value) || value.equals("null");
	}

	public static String makeValidString(String value) {
		return (value == null || String.valueOf(value).trim().equals("null")) ? "" : value.trim();
	}

	public static String getProperty(Properties prop, String strKey) {
		return prop.getProperty(strKey);
	}

	public static String formatPhoneNumber(String strPhoneNumber) {
		String phoneNumber = strPhoneNumber;
		if (phoneNumber.toLowerCase().indexOf("ext") != -1) {
			phoneNumber = phoneNumber.substring(0, phoneNumber.toLowerCase().indexOf("ext"));
		}
		if (phoneNumber.toLowerCase().indexOf("x") != -1) {
			phoneNumber = phoneNumber.substring(0, phoneNumber.toLowerCase().indexOf("x"));
		}
		if (phoneNumber.indexOf("+") == -1) {
			phoneNumber = phoneNumber.replaceAll("[-()+. ]", "");
			if (!Util.isEmpty(phoneNumber)) {
				if (phoneNumber.charAt(0) == '1') {
					phoneNumber = phoneNumber.substring(1, phoneNumber.length());
				}
				if (phoneNumber.length() == 10)
					phoneNumber = "(" + phoneNumber.substring(0, 3) + ") " + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, phoneNumber.length());
				else
					phoneNumber = strPhoneNumber;
			} else {
				phoneNumber = strPhoneNumber;
			}
		}
		return phoneNumber;
	}

	public static String getExtension(String phoneNumber) {
		if (Util.isEmpty(phoneNumber))
			return "";

		phoneNumber = phoneNumber.toLowerCase();

		if (phoneNumber.contains("ext"))
			return StringUtils.substringAfter(phoneNumber, "ext").trim();

		if (phoneNumber.contains("x"))
			return StringUtils.substringAfter(phoneNumber, "x").trim();

		return "";
	}

	public static String truncateExtension(String phoneNumber) {
		phoneNumber = phoneNumber.replaceAll("[^0-9a-zA-Z()\\-+\\s]", "");
		if (Util.isEmpty(phoneNumber))
			return phoneNumber;

		for (int k = 0; k < phoneNumber.length(); k++) {
			char letter = phoneNumber.charAt(k);

			if (Character.isLetter(letter))
				return phoneNumber.substring(0, k);
		}
		return phoneNumber;
	}

	public static String getNow(String dateFormat) {
		return getDateTime(Calendar.getInstance(), dateFormat);
	}

	public static String getNow() {
		return getDateTime(Calendar.getInstance());
	}

	public static String getDateTime(Calendar calendar) {
		return DateFormatUtils.format(calendar.getTimeInMillis(), "yyyy/MM/dd hh:mm aa");
	}

	public static String getDateTime(Calendar calendar, String strDateFormat) {
		return DateFormatUtils.format(calendar.getTimeInMillis(), strDateFormat);
	}

	public static String getDateTimeInTimeZone(Calendar calendar, String dateFormat, String timeZone) {
		calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
		return DateFormatUtils.format(calendar.getTimeInMillis(), dateFormat);
	}

	public static String getConvertedGMTFormat(java.util.Date locDate) {
		DateFormat gmtFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a zzz");
		gmtFormat.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
		return gmtFormat.format(new Date());
	}

	public static String makeDate(Date date) {
		return date != null ? DateFormatUtils.format(date, "EEE MMM dd, yyyy hh:mm aa") : "";
	}

	public static String formatDateTime(String strDate, String inputFormat, String outputFormat) {
		try {
			DateTimeFormatter dfInputFormat = DateTimeFormat.forPattern(inputFormat);
			DateTime date = dfInputFormat.parseDateTime(strDate);

			DateTimeFormatter dfOutputFormat = DateTimeFormat.forPattern(outputFormat);
			return dfOutputFormat.print(date);
		} catch (Exception e) {
			return "";
		}
	}

	public static String formatDateTime(String strDate, String strReqdDateFormat) {
		return formatDateTime(strDate, "yyyy-MM-dd", strReqdDateFormat);
	}

	public static String getReminderDateTime(String startDateTime, String reminder) {
		if (StringUtils.isBlank(reminder))
			return "";

		String[] reminderParts = reminder.split(" ");
		if (reminderParts.length != 2)
			return "";

		try {
			DateTimeFormatter df = DateTimeFormat.forPattern("MM/dd/yyyy hh:mm aa");
			DateTime startDt = df.parseDateTime(startDateTime);

			int timeCount = Integer.parseInt(reminderParts[0]);
			String timeType = reminderParts[1];

			if (timeType.equals("minutes")) {
				return df.print(startDt.minusMinutes(timeCount));
			} else if (timeType.startsWith("hour")) {
				return df.print(startDt.minusHours(timeCount));
			}
		} catch (Exception e) {
			return "";
		}

		return "";
	}

	public static String getXsdDate(String dateTimeValue) {
		try {
			DateTimeFormatter df = DateTimeFormat.forPattern("MM/dd/yyyy hh:mm aa");
			DateTime date = df.parseDateTime(dateTimeValue);

			DateTimeFormatter dfReturn = ISODateTimeFormat.dateTime();
			return dfReturn.print(date);
		} catch (Exception e) {
			return "";
		}
	}

	public static String getXsdDateWithFormat(String dateTimeValue, String format) {
		try {
			DateTimeFormatter df = DateTimeFormat.forPattern(format);
			df.withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT")));
			DateTime date = df.parseDateTime(dateTimeValue);

			DateTimeFormatter dfReturn = ISODateTimeFormat.dateTime();
			return dfReturn.print(date);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getXsdDateFromPureDate(String dateValue) {
		try {
			DateTimeFormatter df = DateTimeFormat.forPattern("MM/dd/yyyy");
			DateTime date = df.parseDateTime(dateValue);

			DateTimeFormatter dfReturn = ISODateTimeFormat.date();
			return dfReturn.print(date);
		} catch (Exception e) {
			return "";
		}
	}

	public static String getMessageProperty(String strKey) {
		ResourceBundle rbProperties = ResourceBundle.getBundle("ApplicationResources");
		try {
			return rbProperties.getString(strKey);
		} catch (Exception e) {
			return "";
		}
	}

	public static boolean isValidOrginOrReferrer(String clientOrigin, String accpetedDomain) {
		if (clientOrigin.indexOf(":8080") != -1)
			return true;
		List<String> accpetedDomains = Lists.newArrayList(Splitter.on(',').trimResults().split(accpetedDomain));
		for (String domain : accpetedDomains) {
			if (clientOrigin.indexOf(domain) != -1) {
				return true;
			}
		}
		return false;
	}

	public static boolean isValidHost(String clientOrigin, String accpetedDomain) {
		List<String> accpetedDomains = Lists.newArrayList(Splitter.on(',').trimResults().split(accpetedDomain));
		for (String domain : accpetedDomains) {
			if (domain.indexOf(clientOrigin) != -1) {
				return true;
			}
		}
		return false;
	}

}

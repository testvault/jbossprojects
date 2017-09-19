package sfdata;

import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

public class Validator 
{
    public static final String MSG_EMPTY = "cannot be a blank value";
    public static final String MSG_INVALID_DATE = "is not a valid date";
    public static final String MSG_INVALID_INT = "is not a valid integer";
    public static final String MSG_INVALID_SELECTION = "is not a valid selection";
    public static final String MSG_INVALID_AMOUNT = "is not a valid amount";
    public static final String MSG_GENERIC_INVALID_FORMAT = "is formatted incorrectly";
    public static final String MSG_NON_EXISTENT = "does not exist";
    public static final String MSG_DUPLICATES = "multiple entries found with same name";

    public static String MSG_INVALID_LENGTH(int length) { return "cannot exceed " + length + " characters"; }
    public static String MSG_INVALID_RANGE_INCLUSIVE(int min, int max) { return "should be between " + min + " and " + max + " (inclusive)"; }
    public static String MSG_INVALID_INTEGER(IntegerType type) { return "should be a valid " +  getIntegerType(type); }
    

    public static String getIntegerType(IntegerType type)
    {
    	 switch (type) {
         	case Any: return "integer";
         	case Whole: return "integer greater than or equal to zero";
         	case Natural: return "integer greater than zero";
    	 }
    	 return "";
    }
    
    
    public static boolean isEmpty(String value)
    {
    	return StringUtils.isBlank(value);
    }
    
    public static String formatDateForValidation(String date)
    {
    	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    	try {
    		return sdf.format(sdf.parse(date));
    	}
    	catch (Exception e) {
    		return "Incorrect Date";
    	}
    }
    
    //Because SimpleDateFormat will accept even invalid dates in the defined format and roll them to the next available valid date, (Apr 32 => March 2)
    //You have to write it back to the string value and then compare again. This can be avoided by writing a custom check of the date. Anyway!!!
    public static boolean isValidDate(String date)
    {
    	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    	try {
    		return sdf.format(sdf.parse(date)).equals(date);
    	}
    	catch (Exception e) {
    		return false;
    	}
    }
    public static boolean isValidInteger(String value)
    {
    	return isEmpty(value) == false && NumberUtils.isDigits(value);
    }
    public static boolean isValidNaturalNumber(String value) //1, 2, 3, ...
    {
        return isValidInteger(value) && Integer.parseInt(value) >= 1;
    }
    public static boolean isValidWholeNumber(String value) //0, 1, 2, 3, ...
    {
        return isValidInteger(value) && Integer.parseInt(value) >= 0;
    }
    public static boolean isValidDecimal(String value)
    {
    	try {
    		return isEmpty(value) == false && Double.parseDouble(value) != Double.NaN;
    	}
    	catch (Exception e) {
    		return false;
    	}
    }
    public static boolean doesNotExceedLength(String value, int length)
    {
        return isEmpty(value) || value.length() <= length;
    }
    public static boolean isEmptyOrDoesNotExceedLength(String value, int length)
    {
        return isEmpty(value) || doesNotExceedLength(value, length);
    }
    public static boolean isEmptyOrIsValidDate(String value)
    {
        return isEmpty(value) || isValidDate(value);
    }
    public static boolean isEmptyOrIsValidDecimal(String value)
    {
        return isEmpty(value) || isValidDecimal(value);
    }
    public static boolean isEmptyOrIsValidIntegerDropdown(String value)
    {
        return isEmpty(value) || isValidInteger(value);
    }
    public static boolean isEmptyOrIsValidIntegerDropdown(String value, String emptyValue)
    {
        return value.equals(emptyValue) || isValidInteger(value);
    }
}

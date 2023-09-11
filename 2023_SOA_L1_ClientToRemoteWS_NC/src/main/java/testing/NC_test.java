package testing;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.soap.ws.client.NumberConversion;
import com.soap.ws.client.NumberConversionSoapType;

public class NC_test {

	
	public static String convert2word(String inputStr) {
		BigInteger input_N = new BigInteger(inputStr);
		NumberConversion NC_service = new NumberConversion(); //created service object
		NumberConversionSoapType NC_serviceSOAP = NC_service.getNumberConversionSoap(); //create SOAP object (a port of the service)
        String result = NC_serviceSOAP.numberToWords(input_N);  
        return result;
	}
    
	public static String convert2dollars(String inputStr) {
		BigDecimal input_D = new BigDecimal(inputStr);
		NumberConversion NC_service = new NumberConversion(); //created service object
        NumberConversionSoapType NC_serviceSOAP = NC_service.getNumberConversionSoap(); //create SOAP object (a port of the service)
        String result = NC_serviceSOAP.numberToDollars(input_D);  
        return result;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("for '23' number is: "+ convert2word("23"));
		System.out.println("for '145.75' price is: "+ convert2dollars("145.75"));
	}

}

package clientWS_NC;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.soap.ws.client.NumberConversion;
import com.soap.ws.client.NumberConversionSoapType;

public class NumberConvertor {
	public NumberConvertor() {}
	
	public String convert2word(String inputStr) {
		BigInteger input_N = new BigInteger(inputStr);
		NumberConversion NC_service = new NumberConversion(); //created service object
		NumberConversionSoapType NC_serviceSOAP = NC_service.getNumberConversionSoap(); //create SOAP object (a port of the service)
        String result = NC_serviceSOAP.numberToWords(input_N);  
        return result;
	}
    
	public String convert2dollars(String inputStr) {
		BigDecimal input_D = new BigDecimal(inputStr);
		NumberConversion NC_service = new NumberConversion(); //created service object
        NumberConversionSoapType NC_serviceSOAP = NC_service.getNumberConversionSoap(); //create SOAP object (a port of the service)
        String result = NC_serviceSOAP.numberToDollars(input_D);  
        return result;
	}

}

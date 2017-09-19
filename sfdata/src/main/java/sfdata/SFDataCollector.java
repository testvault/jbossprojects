package sfdata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.collect.ImmutableMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SFDataCollector {
	protected static CLLogger logger = CLLogger.getLogger("");
	
	Properties prop = new Properties();
	private String instanceURL = "";
	private String sessionId = "";
	
	public static void main(String a[]){
		
		System.out.println(System.getProperty("user.home"));
		SFDataCollector sfd = new SFDataCollector();
		sfd.initialize();
		sfd.collectData();
		
	}
	
	public void initialize(){
		logger.debug("SFDataCollector.initialize() Invoked");
		try{
			File path = new File(SFDataCollector.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			String propertiesPath = path.getParentFile().getParentFile().getAbsolutePath(); // Uncomment to run inside eclipse
			//String propertiesPath = path.getParentFile().getAbsolutePath();
			logger.debug("propertiesPath="+propertiesPath);
			prop.load(new FileInputStream(propertiesPath+"/ApplicationResources.properties"));
		}catch(Exception e){
			logger.error("SFDataCollector.initialize() Exception="+ExceptionUtils.getStackTrace(e));
		}
		logger.debug("SFDataCollector.initialize() Initialized");
	}
	
	public void collectData(){
		String clientCount = Util.getProperty(prop, "client_count");
		if(Util.isEmpty(clientCount)) clientCount= "0";
		for(int k=0; k < Integer.parseInt(clientCount); k++){
			List<BasicNameValuePair> formparams = WebUtil.getFormParams(ImmutableMap.<String, String>builder()
					.put("grant_type", "password")
					.put("client_id", Util.getProperty(prop, "client"+(k+1)+"_client_id"))
					.put("client_secret", Util.getProperty(prop, "client"+(k+1)+"_secret"))
					.put("username", Util.getProperty(prop, "client"+(k+1)+"_login_name"))
					.put("password", Util.getProperty(prop, "client"+(k+1)+"_password"))
					.build());
			String response = WebUtil.getResponse(formparams, "https://login.salesforce.com/services/oauth2/token");
			logger.debug(response);
			JSONObject loginResponse = JSONObject.fromObject(response);
			if(loginResponse.containsKey("error") == false){
				sessionId = loginResponse.optString("access_token","");
				instanceURL = loginResponse.optString("instance_url","");
				logger.debug("SFDataCollector.collecData() OrgId="+Util.getProperty(prop, "client"+(k+1)+"_org_id")+", instanceURL="+instanceURL+", sessionId="+sessionId+", Started");
				pullOpportunitiesData(Util.getProperty(prop, "output_file_path").replaceAll("<org_id>", Util.getProperty(prop, "client"+(k+1)+"_org_id")),
						Util.getProperty(prop, "client"+(k+1)+"_org_id"));
			}else{
				logger.error("SFDataCollector.collecData() OrgId="+Util.getProperty(prop, "client"+(k+1)+"_org_id")+", loginResponse="+loginResponse);
			}
		}
	}
	
	public void pullOpportunitiesData(String filePath, String orgId) {
		BufferedWriter bw = null;
		try{
			File file = new File(filePath);
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write("\"Company\",Is Won,Date");
			bw.newLine();
			List<BasicNameValuePair> formparams = WebUtil.getFormParams(ImmutableMap.<String, String>builder()
					.put("sid", sessionId)
					.build());
			String soqlQuery = "Select a.name,o.IsWon, o.CloseDate from Opportunity o, o.Account a";
			String response = WebUtil.getGetResponse(formparams, instanceURL+"/services/data/v20.0/query/?q="+URLEncoder.encode(soqlQuery, "utf-8"));
			JSONObject jResponse = JSONObject.fromObject(response);
			if(jResponse.containsKey("done") == true){
				JSONArray records = jResponse.getJSONArray("records");
				for (Object object : records) {
					JSONObject record = JSONObject.fromObject(object);
					String accountName = "";
					if(record.containsKey("Account") && record.getJSONObject("Account").isNullObject() == false)
						accountName = record.getJSONObject("Account").optString("Name","");
					if(!Util.isEmpty(accountName)){
						bw.write("\""+accountName+"\","+(record.optBoolean("IsWon")?"Y":"N")+","+record.getString("CloseDate"));
						bw.newLine();
					}
				}
			}
			
		}catch(Exception e){
			logger.error("SFDataCollector.pullOpportunitiesData() OrgId="+orgId+", Exception="+ExceptionUtils.getStackTrace(e));
		}finally{
			try{ if(bw != null) bw.close();}catch(Exception e){}
		}
		logger.debug("SFDataCollector.pullOpportunitiesData() OrgId="+orgId+", Completed");
	}
}

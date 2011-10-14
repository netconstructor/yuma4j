package at.ait.dme.yuma4j.server.gui.admin;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.openid4java.association.AssociationException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;

import at.ait.dme.yuma4j.server.config.ServerConfig;
import at.ait.dme.yuma4j.server.gui.WicketApplication;

public class OpenIdAuthenticator {

	private ConsumerManager consumerManager;
	private DiscoveryInformation discoveryInformation;
		
	public void performAuthentication(String userSuppliedId)
		throws Exception 
	{
    	discoveryInformation = performDiscoveryOnUserSuppliedIdentifier(userSuppliedId);
    	AuthRequest authRequest = createOpenIdAuthRequest(discoveryInformation, getServerBaseUrl());
    	redirectToOpenIdProvider(authRequest);    	
	}
	
	public boolean authenticationSuccess(PageParameters pageParameters) 
		throws MessageException, DiscoveryException, AssociationException, ConsumerException 
	{
		ParameterList response = new ParameterList(pageParameters);
		VerificationResult verificationResult = getConsumerManager().verify(
			getServerBaseUrl(), 
			response, 
			discoveryInformation);
		Identifier verifiedIdentifier = verificationResult.getVerifiedId();
		return verifiedIdentifier != null;
	}

	@SuppressWarnings("unchecked")
	private DiscoveryInformation performDiscoveryOnUserSuppliedIdentifier(String userSuppliedId)
		throws DiscoveryException, ConsumerException
	{
		ConsumerManager consumerManager = getConsumerManager();
		List<DiscoveryInformation> discoveries = consumerManager.discover(userSuppliedId);
		return consumerManager.associate(discoveries);
	}
	
	private ConsumerManager getConsumerManager() 
		throws ConsumerException
	{
		if (consumerManager == null) {
			consumerManager = new ConsumerManager();
			consumerManager.setAssociations(new InMemoryConsumerAssociationStore());
			consumerManager.setNonceVerifier(new InMemoryNonceVerifier(10000));
		}
		return consumerManager;
	}
	
	private String getServerBaseUrl() {
    	ServerConfig config = ServerConfig.getInstance(WicketApplication.getConfig());
    	return config.getServerBaseURL() + "admin/login?is_return=true";
	}
	    
    private AuthRequest createOpenIdAuthRequest(DiscoveryInformation discoveryInformation, String returnToUrl) 
    	throws MessageException, ConsumerException 
    {
    	return getConsumerManager().authenticate(discoveryInformation, returnToUrl);
    }
    
    private void redirectToOpenIdProvider(AuthRequest authRequest) {
    	RequestCycle.get().setRedirect(false);
    	RequestCycle.get().getResponse().redirect(authRequest.getDestinationUrl(true));
    }
    
}

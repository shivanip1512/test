package unit.cayenta;

import com.cannontech.web.simplePost.SimpleHttpPostService;
import com.cannontech.web.simplePost.SimpleHttpPostServiceFactory;

public class MockSimpleHttpPostServiceFactory implements SimpleHttpPostServiceFactory {

	@Override
	public SimpleHttpPostService getSimpleHttpPostService(String url, int port, String userName, String password) {
		return new MockSimpleHttpPostService();
	}
}
